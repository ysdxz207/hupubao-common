/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package win.hupubao.common.utils.rsa;

import org.apache.commons.codec.digest.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import win.hupubao.common.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/**
 * RSA签名工具
 * @author L.feihong
 * @date 2017年1月13日
 */
public class SignUtils {

	private static BASE64Encoder encoder = new BASE64Encoder();
	private static BASE64Decoder decoder = new BASE64Decoder();


	public static enum SignType {
		RSA("SHA1WithRSA"),
		RSA2("SHA256WithRSA");

		SignType(String algorithm) {
			this.algorithm = algorithm;
		}

		public String algorithm;
	}

	public static String sign(String privateKey,
							  Map<String, String> params,
							  SignType signType,
							  String charset) {

		privateKey = StringUtils.replaceBlank(privateKey);
		String prestr = createLinkString(params);
		String md = DigestUtils.md5Hex(getContentBytes(prestr, charset));
		String mysign = buildSign(md, privateKey, signType, charset);
		return mysign;
	}

	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if(value==null || "".equals(value.trim())) {
				continue;
			}
			if(!"sign".equals(key)) {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr.substring(0, prestr.length()-1);
	}

	private static String buildSign(String content,
									String privateKey,
									SignType signType,
									String charset) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decoder.decodeBuffer(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(signType.algorithm);

			signature.initSign(priKey);
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();

			return encoder.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	/**
	 *
	 * @param sPara 签名参数
	 * @param sign 签名字符串
	 * @param publicKey 公钥
	 * @return
	 */
	public static boolean verify(Map<String, String> sPara,
								 String sign,
								 String publicKey,
								 SignType signType,
								 String charset) {

		publicKey = StringUtils.replaceBlank(publicKey);
		boolean flag = false;
		String prestr = createLinkString(sPara);
		String md = DigestUtils.md5Hex(getContentBytes(prestr, charset));
		flag = verify(md, sign, publicKey, signType, charset);
		return flag;
	}

	private static boolean verify(String content,
								  String sign,
								  String publicKey,
								  SignType signType,
								  String charset) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = decoder.decodeBuffer(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance(signType.algorithm);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(charset));

			boolean bverify = signature.verify(decoder.decodeBuffer(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
