package com.examples.android2sm2.code;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SM4Utils
{
	private String secretKey = "";
	
	private String iv = "";
	
	private boolean hexString = false;
	
	public SM4Utils()
	{
	}
	
	public String encryptData_ECB(String plainText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_ENCRYPT;
			
			byte[] keyBytes;
			if (hexString)
			{
				keyBytes = Util.hexStringToBytes(secretKey);
			}
			else
			{
				keyBytes = secretKey.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("GBK"));
			String cipherText = Base64Encoder.encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0)
			{
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryptData_ECB(String cipherText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_DECRYPT;
			
			byte[] keyBytes;
			if (hexString)
			{
				keyBytes = Util.hexStringToBytes(secretKey);
			}
			else
			{
				keyBytes = secretKey.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4_crypt_ecb(ctx, Base64Decoder.decode(cipherText));
			return new String(decrypted, "GBK");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String encryptData_CBC(String plainText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_ENCRYPT;
			
			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString)
			{
				keyBytes = Util.hexStringToBytes(secretKey);
				ivBytes = Util.hexStringToBytes(iv);
			}
			else
			{
				keyBytes = secretKey.getBytes();
				ivBytes = iv.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("GBK"));
			String cipherText = Base64Encoder.encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0)
			{
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryptData_CBC(String cipherText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_DECRYPT;
			
			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString)
			{
				keyBytes = Util.hexStringToBytes(secretKey);
				ivBytes = Util.hexStringToBytes(iv);
			}
			else
			{
				keyBytes = secretKey.getBytes();
				ivBytes = iv.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64Decoder.decode(cipherText));
			return new String(decrypted, "GBK");
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
}
