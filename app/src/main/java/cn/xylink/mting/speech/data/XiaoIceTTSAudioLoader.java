package cn.xylink.mting.speech.data;


import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cn.xylink.mting.MTing;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.speech.TTSAudioLoader;
import cn.xylink.mting.utils.PackageUtils;
import okhttp3.Call;
import okhttp3.MediaType;

import static cn.xylink.mting.speech.SpeechError.FRAGMENT_LOAD_INNTERNAL_ERROR;


public class XiaoIceTTSAudioLoader implements TTSAudioLoader {

    private final static String URL_PATH = "http://xylink.aic.msxiaobing.com/api/platform/Reply";
    private final static String KEY_SUBSCRIPTION = "bc2c2003ad7342d7afd7d3c48f28abad";
    private final static String MSG_ID = "f5ff4f16fb90d07eb9475b5d9b582967ad09e3a7b875a62a26f02ffec1b37c2dff4ab5684fc620ee";
    private final static String TIMESTAMP = "300";
    private static String TAG = XiaoIceTTSAudioLoader.class.getSimpleName();


    private String getSpeechString(Speechor.SpeechorSpeed speechorSpeed) {
        switch (speechorSpeed) {
            case SPEECH_SPEED_NORMAL:
                return "0";

            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                return "50";

            case SPEECH_SPEED_MULTIPLE_2:
                return "100";

            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                return "200";
        }

        return "0";
    }

    @Override
    public void textToSpeech(String text, Speechor.SpeechorSpeed speechorSpeed, LoadResult result) {
        Log.d(TAG, "TTS:" + text);
        String postData = null;

        try {
            postData = createPostString(text, getSpeechString(speechorSpeed));
        }
        catch (JSONException jsonEx) {
            jsonEx.printStackTrace();
            result.invoke(-3, jsonEx.getMessage(), null);
            return;
        }

        String signature = SHA512(postData + KEY_SUBSCRIPTION + TIMESTAMP);

        OkGo.<String>post(URL_PATH)
                .tag(this)
                .upJson(postData)
                .headers("subscription-key", KEY_SUBSCRIPTION)
                .headers("timestamp", "300")
                .headers("signature", signature)
                .execute(new com.lzy.okgo.callback.StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONArray itemArray = new JSONArray(response.body());
                            JSONObject itemObject = itemArray.getJSONObject(0);
                            JSONObject contentObject = itemObject.getJSONObject("content");
                            String voiceUrl = contentObject.getString("audioUrl");
                            Uri voiceUri = Uri.parse(voiceUrl);
                            String fileStoragePath = MTing.getInstance().AudioCachePath;
                            String filename = voiceUri.getLastPathSegment();

                            Log.d(TAG, "download:" + text);
                            OkGo.<File>get(voiceUrl)
                                    .tag(XiaoIceTTSAudioLoader.this)
                                    .execute(new FileCallback(fileStoragePath, filename) {
                                        @Override
                                        public void onSuccess(Response<File> response) {
                                            String fileUrl = response.body().getAbsolutePath();
                                            if (result != null) {
                                                Log.d(TAG, "complete:" + text);
                                                result.invoke(0, null, fileUrl);
                                            }
                                        }

                                        @Override
                                        public void onError(Response<File> response) {
                                            super.onError(response);
                                            if (result != null) {
                                                result.invoke(FRAGMENT_LOAD_INNTERNAL_ERROR, "音频文件下载错误:" + response.getException().getMessage(), null);
                                            }
                                        }
                                    });
                        }
                        catch (JSONException jsonError) {
                            Log.d("SPEECH", "testToSpeechError:" + jsonError.toString());
                            if (result != null) {
                                result.invoke(FRAGMENT_LOAD_INNTERNAL_ERROR, "音频文件下载错误:" + jsonError.getMessage(), null);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (result != null) {
                            result.invoke(-response.code(), response.message(), null);
                        }
                    }
                });
    }

    @Override
    public void cancelAll() {
        OkGo.getInstance().cancelTag(this);
    }


    private String createPostString(String text, String speedStr) throws JSONException {
        JSONObject itemObject = new JSONObject();

        itemObject.put("senderId", "11111");
        itemObject.put("senderNickname", "Dilly");
        JSONObject contentObject = new JSONObject();

        contentObject.put("text", text);

        JSONObject medataObject = new JSONObject();
        medataObject.put("ReadContent", "true");
        medataObject.put("SpeechRate", speedStr);
        contentObject.put("Metadata", medataObject);

        itemObject.put("content", contentObject);
        itemObject.put("msgId", MSG_ID);
        itemObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return itemObject.toString();
    }


    private static String SHA512(final String strText) {
        return SHA(strText, "SHA-512");
    }

    /**
     * 字符串 SHA 加密
     *
     * @return
     */
    private static String SHA(final String strText, final String strType) {
        String strResult = null;

        if (strText != null && strText.length() > 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                messageDigest.update(strText.getBytes());
                byte byteBuffer[] = messageDigest.digest();
                StringBuffer strHexString = new StringBuffer();
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                strResult = strHexString.toString();
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
