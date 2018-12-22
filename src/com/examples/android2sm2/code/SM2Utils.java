package com.examples.android2sm2.code;


import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class SM2Utils {
	/** The LOG. */
	// private static final Logger LOG =
	// LoggerFactory.getLogger(SM2Utils.class);

	// 生成随机秘钥对
	public static Pair generateKeyPair() {
		SM2 sm2 = SM2.Instance();
		AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
		BigInteger privateKey = ecpriv.getD();
		ECPoint publicKey = ecpub.getQ();
		Pair result = new Pair();
		result.setPrivatekey(Util.byteConvert32Bytes(privateKey));
		result.setPublickey(publicKey.getEncoded());

		return result;
	}

	public static byte[] getCompressPubkey(byte[] encoded) {

		SM2 sm2 = SM2.Instance();

		ECPoint ptFlat = sm2.ecc_curve.decodePoint(encoded);
		return ptFlat.getEncoded();

	}

	public static byte[] getUnCompressPubkey(byte[] encoded) {

		SM2 sm2 = SM2.Instance();

		ECPoint ptFlat = sm2.ecc_curve.decodePoint(encoded);
		return ptFlat.getEncoded();

	}

	public static byte[] encrypt(byte[] publicKey, byte[] data) throws IOException {
		if (publicKey == null || publicKey.length == 0) {
			return null;
		}

		if (data == null || data.length == 0) {
			return null;
		}

		byte[] source = new byte[data.length];
		System.arraycopy(data, 0, source, 0, data.length);

		Cipher cipher = new Cipher();
		SM2 sm2 = SM2.Instance();

		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

		ECPoint c1 = cipher.Init_enc(sm2, userKey);
		cipher.Encrypt(source);
		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);

		return Util.hexToByte(Util.byteToHex(c1.getEncoded()) + Util.byteToHex(source) + Util.byteToHex(c3));
	}

	public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException {
		if (privateKey == null || privateKey.length == 0) {
			return null;
		}

		if (encryptedData == null || encryptedData.length == 0) {
			return null;
		}

		String data = Util.byteToHex(encryptedData);
		/***
		 * 分解加密字串 （C1 = C1标志位2位 + C1实体部分128位 = 130） （C3 = C3实体部分64位 = 64） （C2 =
		 * encryptedData.length * 2 - C1长度 - C2长度）
		 */
		byte[] c1Bytes = Util.hexToByte(data.substring(0, 130));
		int c2Len = encryptedData.length - 97;
		byte[] c2 = Util.hexToByte(data.substring(130, 130 + 2 * c2Len));
		byte[] c3 = Util.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(1, privateKey);

		// 通过C1实体字节来生成ECPoint
		ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
		Cipher cipher = new Cipher();
		cipher.Init_dec(userD, c1);
		cipher.Decrypt(c2);
		cipher.Dofinal(c3);

		// 返回解密结果
		return c2;
	}

	public static byte[] signForSptcc(byte[] userId, byte[] privateKey, byte[] sourceData) throws IOException {
		if (privateKey == null || privateKey.length == 0) {
			return null;
		}

		if (sourceData == null || sourceData.length == 0) {
			return null;
		}

		SM2 sm2 = SM2.Instance();
		BigInteger userD = Util.byteConvertInteger(privateKey);
		// LOG.debug("userD:{} " , userD.toString(16));

		ECPoint userKey = sm2.ecc_point_g.multiply(userD);
		// LOG.debug("椭圆曲线点X: {} ", userKey.getX().toBigInteger().toString(16));
		// LOG.debug("椭圆曲线点Y: {}" , userKey.getY().toBigInteger().toString(16));

		SM3Digest sm3 = new SM3Digest();
		byte[] z = sm2.sm2GetZ(userId, userKey);
		// LOG.debug("SM3摘要Z: {}", Util.getHexString(z));

		// LOG.debug("M: {}" , Util.getHexString(sourceData));

		sm3.update(z, 0, z.length);
		sm3.update(sourceData, 0, sourceData.length);
		byte[] md = new byte[32];
		sm3.doFinal(md, 0);

		// LOG.debug("SM3摘要值: {}" , Util.getHexString(md));

		SM2Result sm2Result = new SM2Result();
		sm2.sm2Sign(md, userD, userKey, sm2Result);
		// LOG.debug("r: {}" , sm2Result.r.toString(16));
		// LOG.debug("s: {}" + sm2Result.s.toString(16));

		String singdatas = "15" + Util.getHexString(Util.byteConvert32Bytes(sm2Result.r))
				+ Util.getHexString(Util.byteConvert32Bytes(sm2Result.s));
		byte[] signdata = Util.hexStringToBytes(singdatas);
		return signdata;
	}

	@SuppressWarnings("unchecked")
	public static boolean verifySignForSptcc(byte[] userId, byte[] publicKey, byte[] sourceData, byte[] signData)
			throws IOException {
		if (publicKey == null || publicKey.length == 0) {
			return false;
		}

		if (sourceData == null || sourceData.length == 0) {
			return false;
		}

		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

		SM3Digest sm3 = new SM3Digest();
		byte[] z = sm2.sm2GetZ(userId, userKey);
		sm3.update(z, 0, z.length);
		sm3.update(sourceData, 0, sourceData.length);
		byte[] md = new byte[32];
		sm3.doFinal(md, 0);
		// LOG.debug("SM3摘要值:{} " , Util.getHexString(md));

		String signs = Util.byteToHex(signData);

		byte[] signdata = Util.hexStringToBytes(signs);

		BigInteger r = Util.byteConvertInteger(Util.subByte(signdata, 1, 32));

		BigInteger s = Util.byteConvertInteger(Util.subByte(signdata, 33, 32));
		SM2Result sm2Result = new SM2Result();
		sm2Result.r = r;
		sm2Result.s = s;
		// LOG.debug("r: {}", sm2Result.r.toString(16));
		// LOG.debug("s: {}", sm2Result.s.toString(16));

		sm2.sm2Verify(md, userKey, sm2Result.r, sm2Result.s, sm2Result);
		return sm2Result.r.equals(sm2Result.R);
	}

}
