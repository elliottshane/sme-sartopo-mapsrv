package hmac2;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hash {

	  private static byte[] HmacSHA256(String data, byte[] key)
	    throws Exception
	  {
	    String algorithm = "HmacSHA256";
	    Mac mac = Mac.getInstance(algorithm);
	    mac.init(new SecretKeySpec(key, algorithm));
	    return mac.doFinal(data.getBytes("UTF8"));
	  }

	public static void main(String[] args) {
		
		String method = "POST";
		String uri = "";
		String payload = "";
		String expires = "";
		String key = "";

		
		String point = "{\"id\":null,\"properties\":{\"marker-symbol\":\"point\",\"marker-color\":\"#FF00FF\",\"title\":\"Test of API\",\"description\":\"\",\"folderId\":null,\"marker-rotation\":null},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-106.95389,39.20581]}}";
			
		//point = "test";
		
		payload = point;

		
		System.out.println(point);
		try {
			
			byte[] sdk = java.util.Base64.getDecoder().decode(key);
			String sdke = Base64.encode(sdk);
			byte[] decodedKey =  Base64.decode(key);
			String encodedKey =  Base64.encode(decodedKey);
			
			System.out.println("standard decode: " + sdk);
			System.out.println("standard encoded by Mark: " + sdke);
			System.out.println("decoded key: " + decodedKey);
		  	System.out.println("encoded key: " + encodedKey);
		
			byte[] bytes = HmacSHA256(method + " " + uri + "\n" + expires + "\n" + (payload == null ? "" : payload), Base64.decode(key));
			
			String b64 = Base64.encode(bytes);
			String b642 = java.util.Base64.getEncoder().encodeToString(bytes);
			
			System.out.println(bytes);
			System.out.println(b64);
			System.out.println(b642);
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	
	
}
