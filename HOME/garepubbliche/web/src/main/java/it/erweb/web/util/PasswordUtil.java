package it.erweb.web.util;

import java.security.MessageDigest;

/**
 * Utility class for calculating hash md5 passwords
 */
public class PasswordUtil
{
	private static final String ENCODING = "UTF-8";
	private static final String HASHING_ALGORITHM = "MD5";
	
	/**
	 * 	Computes the md5 algorithm and returns the corresponding md5 hash digest for the input string
	 * 
	 * @param password	the input password to be hash-ed
	 * @return			the calculated digest for input password
	 */
	public static String computeHash(String password)
	{
		String ret = password;

		try
		{
			byte[] bytesOfpass = password.getBytes(ENCODING);
			MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
			byte[] digest = md.digest(bytesOfpass);
			ret = new String(digest, ENCODING);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
}
