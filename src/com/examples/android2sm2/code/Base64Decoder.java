package com.examples.android2sm2.code;


public class Base64Decoder {
	static byte[] ByteTable = new byte[256];

	static {
		for (int i = 0; i < 256; i++) {
			ByteTable[i] = -1;
		}
		for (int i = 65; i <= 90; i++) {
			ByteTable[i] = (byte) (i - 65);
		}
		for (int i = 97; i <= 122; i++) {
			ByteTable[i] = (byte) (26 + i - 97);
		}
		for (int i = 48; i <= 57; i++) {
			ByteTable[i] = (byte) (52 + i - 48);
		}
		ByteTable[43] = 62;
		ByteTable[47] = 63;
		ByteTable[61] = 64;
	}

	private static int getByte(byte in) {
		return ByteTable[in];
	}

	private static byte[] decode(byte A, byte B, byte C, byte D) {
		byte[] output = new byte[3];

		output[0] = (byte) (getByte(A) << 2 | (getByte(B) & 0x30) >> 4);
		output[1] = (byte) ((getByte(B) & 0xF) << 4 | (getByte(C) & 0x3C) >> 2);
		output[2] = (byte) ((getByte(C) & 0x3) << 6 | getByte(D));
		return output;
	}

	private static byte[] decode(byte A, byte B, byte C) {
		byte[] output = new byte[2];

		output[0] = (byte) (getByte(A) << 2 | (getByte(B) & 0x30) >> 4);
		output[1] = (byte) ((getByte(B) & 0xF) << 4 | (getByte(C) & 0x3C) >> 2);
		return output;
	}

	private static byte[] decode(byte A, byte B) {
		byte[] output = new byte[1];

		output[0] = (byte) (getByte(A) << 2 | (getByte(B) & 0x30) >> 4);
		return output;
	}

	private static byte[] decode(byte A) {
		byte[] output = new byte[1];

		output[0] = (byte) (getByte(A) << 2);
		return output;
	}

	public static byte[] decode(String encString) {
		byte[] decByte = new byte[0];
		byte[] buf = new byte[4];
		byte[] ret = new byte[0];

		if ((encString == null) || (encString.length() == 0)) {
			return decByte;
		}
		int len = encString.length();

		int writeCount = 0;
		int j = 0;
		for (int i = 0; i < len; i++) {
			int c = encString.charAt(i);
			if (c == 13 || c == 10)
				continue;

			if (c != 61) {
				buf[writeCount] = (byte) c;
				writeCount++;
			}
			j++;
			if ((j == 4) || (i == len - 1)) {
				switch (writeCount) {
				case 1:
					ret = decode(buf[0]);
					break;
				case 2:
					ret = decode(buf[0], buf[1]);
					break;
				case 3:
					ret = decode(buf[0], buf[1], buf[2]);
					break;
				case 4:
					ret = decode(buf[0], buf[1], buf[2], buf[3]);
				}

				byte[] origin = new byte[decByte.length];

				System.arraycopy(decByte, 0, origin, 0, decByte.length);
				decByte = new byte[origin.length + ret.length];
				System.arraycopy(origin, 0, decByte, 0, origin.length);
				System.arraycopy(ret, 0, decByte, origin.length, ret.length);

				j = 0;
				writeCount = 0;
			}
		}

		return decByte;
	}
}