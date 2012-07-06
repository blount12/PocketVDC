package sate.pocketvdc;

import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Secure 
{
	private static final String ALGORTM = "AES";
	private static final byte[] keyValue = new byte[] 
			{ 'T', 'h', 'e', 'B',
			'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' 
			};

	/**
	 * This method of encrypt allows the user to input a parameter of type
	 * string and then receive an encrypted version of that string.
	 * 
	 * @param Data
	 *            : the user's input string
	 * @return encryptedValue: This is the encrypted version of the user's
	 *         string.
	 * @throws Exception
	 *             : This throws an exception which may take place.
	 */
	public static String encrypt(String Data) throws Exception 
	{
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORTM);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
		return encryptedValue;
	}

	/**
	 * This method of decrypt allows the user to input a parameter of type an
	 * encrypted string and then receive a decrypted version of that string.
	 * 
	 * @param Data
	 *            : the user's input string
	 * @return decryptedValue: This is the decrypted version of the user's
	 *         string.
	 * @throws Exception
	 *             : This throws an exception which may take place.
	 */
	public static String decrypt(String encryptedData) throws Exception
	{
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORTM);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	/**
	 * This method generates a key which someone can use.
	 * 
	 * @return key
	 * @throws Exception
	 *             : Throws an exception if something occurs.
	 */
	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGORTM);
		return key;
	}

}
