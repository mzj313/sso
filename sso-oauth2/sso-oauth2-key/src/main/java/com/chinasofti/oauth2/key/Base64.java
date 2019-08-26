package com.chinasofti.oauth2.key;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64 {
	// 将 s 进行 BASE64 编码
	public static String encode(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String decode(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] encode(byte[] s){
		if (s == null)
			return null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			(new sun.misc.BASE64Encoder()).encode(s, os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}finally{
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return os.toByteArray();
	}

	public static byte[] decode(byte[] s){
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(new ByteArrayInputStream(s));
			return b;
		} catch (Exception e) {
			return null;
		}
	}
}
