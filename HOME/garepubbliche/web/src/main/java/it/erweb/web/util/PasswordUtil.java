package it.erweb.web.util;

import java.security.MessageDigest;

public class PasswordUtil
{
	private static final String ENCODING = "UTF-8";
	private static final String HASHING_ALGORITHM = "MD5";
	
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
