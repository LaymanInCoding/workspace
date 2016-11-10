package com.witmoon.xmb.api.alipay;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 签名工具类, 主要用于支付宝支付过程参数签名
 */
public class SignUtils {

	private static final String ALGORITHM = "RSA";
	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	private static final String DEFAULT_CHARSET = "UTF-8";

	public static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM, "BC");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 校验数字签名
	 * @param data  加密数据
	 * @param publicKey 公钥
	 * @param sign  数字签名
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data,String publicKey,String sign)throws Exception{
		//解密公钥
		byte[] keyBytes = Base64.decode(publicKey);
		//构造X509EncodedKeySpec对象
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		//指定加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		//取公钥匙对象
		PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

		Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
		signature.initVerify(publicKey2);
		signature.update(data);
		//验证签名是否正常
		return signature.verify(Base64.decode(sign));

	}
}
