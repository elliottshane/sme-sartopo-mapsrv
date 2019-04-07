package hmac2;

public class Base64 {
	private static final byte EQUALS_SIGN = 61;
	private static final byte NEW_LINE = 10;
	private static final byte[] ALPHABET = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
			84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
			113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
	private static final byte[] DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9,
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9,
			-9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29,
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9,
			-9 };
	private static final byte WHITE_SPACE_ENC = -5;
	private static final byte EQUALS_SIGN_ENC = -1;

	private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset,
			byte[] alphabet) {
		int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0)
				| (numSigBytes > 1 ? source[(srcOffset + 1)] << 24 >>> 16 : 0)
				| (numSigBytes > 2 ? source[(srcOffset + 2)] << 24 >>> 24 : 0);
		switch (numSigBytes) {
		case 3:
			destination[destOffset] = alphabet[(inBuff >>> 18)];
			destination[(destOffset + 1)] = alphabet[(inBuff >>> 12 & 0x3F)];
			destination[(destOffset + 2)] = alphabet[(inBuff >>> 6 & 0x3F)];
			destination[(destOffset + 3)] = alphabet[(inBuff & 0x3F)];
			return destination;
		case 2:
			destination[destOffset] = alphabet[(inBuff >>> 18)];
			destination[(destOffset + 1)] = alphabet[(inBuff >>> 12 & 0x3F)];
			destination[(destOffset + 2)] = alphabet[(inBuff >>> 6 & 0x3F)];
			destination[(destOffset + 3)] = 61;
			return destination;
		case 1:
			destination[destOffset] = alphabet[(inBuff >>> 18)];
			destination[(destOffset + 1)] = alphabet[(inBuff >>> 12 & 0x3F)];
			destination[(destOffset + 2)] = 61;
			destination[(destOffset + 3)] = 61;
			return destination;
		}
		return destination;
	}

	public static String encode(byte[] source) {
		return encode(source, 0, source.length, ALPHABET, true);
	}

	private static String encode(byte[] source, int off, int len, byte[] alphabet, boolean doPadding) {
		byte[] outBuff = encode(source, off, len, alphabet, Integer.MAX_VALUE);
		int outLen = outBuff.length;
		while ((!doPadding) && (outLen > 0) && (outBuff[(outLen - 1)] == 61)) {
			outLen--;
		}
		return new String(outBuff, 0, outLen);
	}

	private static byte[] encode(byte[] source, int off, int len, byte[] alphabet, int maxLineLength) {
		int lenDiv3 = (len + 2) / 3;
		int len43 = lenDiv3 * 4;
		byte[] outBuff = new byte[len43 + len43 / maxLineLength];

		int d = 0;
		int e = 0;
		int len2 = len - 2;
		int lineLength = 0;
		for (; d < len2; e += 4) {
			int inBuff = source[(d + off)] << 24 >>> 8 | source[(d + 1 + off)] << 24 >>> 16
					| source[(d + 2 + off)] << 24 >>> 24;

			outBuff[e] = alphabet[(inBuff >>> 18)];
			outBuff[(e + 1)] = alphabet[(inBuff >>> 12 & 0x3F)];
			outBuff[(e + 2)] = alphabet[(inBuff >>> 6 & 0x3F)];
			outBuff[(e + 3)] = alphabet[(inBuff & 0x3F)];

			lineLength += 4;
			if (lineLength == maxLineLength) {
				outBuff[(e + 4)] = 10;
				e++;
				lineLength = 0;
			}
			d += 3;
		}
		if (d < len) {
			encode3to4(source, d + off, len - d, outBuff, e, alphabet);

			lineLength += 4;
			if (lineLength == maxLineLength) {
				outBuff[(e + 4)] = 10;
				e++;
			}
			e += 4;
		}
		assert (e == outBuff.length);
		return outBuff;
	}

	private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, byte[] decodabet) {
		if (source[(srcOffset + 2)] == 61) {
			int outBuff = decodabet[source[srcOffset]] << 24 >>> 6 | decodabet[source[(srcOffset + 1)]] << 24 >>> 12;

			destination[destOffset] = ((byte) (outBuff >>> 16));
			return 1;
		}
		if (source[(srcOffset + 3)] == 61) {
			int outBuff = decodabet[source[srcOffset]] << 24 >>> 6 | decodabet[source[(srcOffset + 1)]] << 24 >>> 12
					| decodabet[source[(srcOffset + 2)]] << 24 >>> 18;

			destination[destOffset] = ((byte) (outBuff >>> 16));
			destination[(destOffset + 1)] = ((byte) (outBuff >>> 8));
			return 2;
		}
		int outBuff = decodabet[source[srcOffset]] << 24 >>> 6 | decodabet[source[(srcOffset + 1)]] << 24 >>> 12
				| decodabet[source[(srcOffset + 2)]] << 24 >>> 18 | decodabet[source[(srcOffset + 3)]] << 24 >>> 24;

		destination[destOffset] = ((byte) (outBuff >> 16));
		destination[(destOffset + 1)] = ((byte) (outBuff >> 8));
		destination[(destOffset + 2)] = ((byte) outBuff);
		return 3;
	}

	public static byte[] decode(String s) throws Exception {
		byte[] bytes = s.getBytes();
		return decode(bytes, 0, bytes.length);
	}

	private static byte[] decode(byte[] source, int off, int len) throws Exception {
		return decode(source, off, len, DECODABET);
	}

	private static byte[] decode(byte[] source, int off, int len, byte[] decodabet) throws Exception {
		int len34 = len * 3 / 4;
		byte[] outBuff = new byte[2 + len34];
		int outBuffPosn = 0;

		byte[] b4 = new byte[4];
		int b4Posn = 0;
		int i = 0;
		byte sbiCrop = 0;
		byte sbiDecode = 0;
		for (i = 0; i < len; i++) {
			sbiCrop = (byte) (source[(i + off)] & 0x7F);
			sbiDecode = decodabet[sbiCrop];
			if (sbiDecode >= -5) {
				if (sbiDecode >= -1) {
					if (sbiCrop == 61) {
						int bytesLeft = len - i;
						byte lastByte = (byte) (source[(len - 1 + off)] & 0x7F);
						if ((b4Posn == 0) || (b4Posn == 1)) {
							throw new Exception("invalid padding byte '=' at byte offset " + i);
						}
						if (((b4Posn == 3) && (bytesLeft > 2)) || ((b4Posn == 4) && (bytesLeft > 1))) {
							throw new Exception(
									"padding byte '=' falsely signals end of encoded value at offset " + i);
						}
						if ((lastByte == 61) || (lastByte == 10)) {
							break;
						}
						throw new Exception("encoded value has invalid trailing byte");
					}
					b4[(b4Posn++)] = sbiCrop;
					if (b4Posn == 4) {
						outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
						b4Posn = 0;
					}
				}
			} else {
				throw new Exception(
						"Bad Base64 input character at " + i + ": " + source[(i + off)] + "(decimal)");
			}
		}
		if (b4Posn != 0) {
			if (b4Posn == 1) {
				throw new Exception("single trailing character at offset " + (len - 1));
			}
			b4[(b4Posn++)] = 61;
			outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
		}
		byte[] out = new byte[outBuffPosn];
		System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
		return out;
	}
}
