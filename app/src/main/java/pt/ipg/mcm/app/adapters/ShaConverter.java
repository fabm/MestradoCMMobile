package pt.ipg.mcm.app.adapters;

import okio.ByteString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaConverter {
  public static String getSHAInBase64(String pass) throws NoSuchAlgorithmException {


    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(pass.getBytes());

    byte byteData[] = md.digest();

    //convert the byte to hex format method 1
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }

    return ByteString.of(sb.toString().getBytes()).base64();
  }
}
