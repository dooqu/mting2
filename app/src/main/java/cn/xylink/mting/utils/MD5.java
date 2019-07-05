package cn.xylink.mting.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组为16进制字串
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * MD5加密用户密码
	 * @param input 输入
	 * @return 密文
	 */
	public static String md5crypt(String input) {
		String resultString = null;
		try {
			resultString = new String(input);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {}
		return resultString;
	}
	
	 /**
     * 获得文件的md5值 
     * */
    public static String getFileMd5(String filePath){
    	String fileMd5 = null;	
    	 FileInputStream fis = null;
    	try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            BigInteger bigInt = new BigInteger(1, md.digest());
            fileMd5 = bigInt.toString(16);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			if(fis!= null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileMd5;
    }
    /**
     * 获得文件的md5值 
     * */
    public static String getFileMd5(byte[] bytes){
    	String fileMd5 = null;	
    	try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            BigInteger bigInt = new BigInteger(1, md.digest());
            fileMd5 = bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			
		}
		return fileMd5;
    }
}