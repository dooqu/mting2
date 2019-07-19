package cn.xylink.mting.speech.data;


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


import okhttp3.Call;
import okhttp3.MediaType;


public class XiaoIceTTSAudioLoader {

    private final static String URL_PATH = "http://xylink.aic.msxiaobing.com/api/platform/Reply";
    private final static String KEY_SUBSCRIPTION = "bc2c2003ad7342d7afd7d3c48f28abad";
    private final static String MSG_ID = "f5ff4f16fb90d07eb9475b5d9b582967ad09e3a7b875a62a26f02ffec1b37c2dff4ab5684fc620ee";
    private final static String TIMESTAMP = "300";


    public interface LoadResult
    {
        void invoke(int errorCode, String message, String audioUrl);
    }

    public static void cancel() {
        OkGo.getInstance().cancelAll();
    }


    public void textToSpeech(String text, LoadResult result) {

        Log.d("xylink", "TTS:" + text);
        String postData = null;

        try {
            postData = createPostString(text);
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

                            OkGo.<File>get(voiceUrl)
                                    .tag(XiaoIceTTSAudioLoader.this)
                                    .execute(new FileCallback() {
                                @Override
                                public void onSuccess(Response<File> response) {
                                    String fileUrl = response.body().getAbsolutePath();
                                    if(result != null) {
                                        result.invoke(0, null, fileUrl);
                                    }
                                }

                                @Override
                                public void onError(Response<File> response) {
                                    super.onError(response);
                                    if(result != null) {
                                        result.invoke(-101, "文件下载错误", null);
                                    }
                                }
                            });
                        }
                        catch (JSONException jsonError) {
                            if(result != null) {
                                result.invoke(-100, jsonError.getMessage(), null);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if(result != null) {
                            result.invoke(-response.code(), response.message(), null);
                        }
                    }
                });
    }


    private String createPostString(String text) throws JSONException
    {
        JSONObject itemObject = new JSONObject();

        itemObject.put("senderId", "11111");
        itemObject.put("senderNickname", "Dilly");
        JSONObject contentObject = new JSONObject();

        contentObject.put("text",text);

        JSONObject medataObject = new JSONObject();
        medataObject.put("ReadContent", "true");
        medataObject.put("SpeechRate", "0");
        contentObject.put("Metadata", medataObject);

        itemObject.put("content", contentObject);
        itemObject.put("msgId", MSG_ID);
        itemObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return itemObject.toString();
    }


    public static String SHA512(final String strText) {
        return SHA(strText, "SHA-512");
    }

    /**
     * 字符串 SHA 加密
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
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }

}
