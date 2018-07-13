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

import sun.misc.BASE64Encoder;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import static java.util.regex.Pattern.compile;

/**
 * @author L.feihong
 * @date 2017年1月13日
 * RSA密钥生成工具
 * 因本工具类使用随机数，在tomcat下使用时可能需加上启动参数：
 * -Djava.security.egd=file:/dev/./urandom
 */
public class RSAKeyUtils {


    private static final String ALGORITHMS = "RSA";

    /**
     * 密钥长度
     */
    private static int KEYSIZE = 1024;

    private static BASE64Encoder encoder = new BASE64Encoder();


    public static RSAKeyUtils getInstance() {
        return RSAKeyUtilsInstance.INSTANCE.singleton;
    }

    private enum RSAKeyUtilsInstance {
        INSTANCE;

        RSAKeyUtilsInstance() {
            singleton = new RSAKeyUtils();
        }

        private RSAKeyUtils singleton;
    }

    public static class RSAKey {
        private String privateKey;
        private String publicKey;

        public RSAKey(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }


    public RSAKeyUtils keySize(int keySize) {
        KEYSIZE = keySize;
        return this;
    }

    public RSAKey generateRSAKey() throws NoSuchAlgorithmException {

        //RSA算法要求有一个可信任的随机数源
        SecureRandom secureRandom = new SecureRandom();

        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHMS);

        //利用上面的随机数据源初始化这个KeyPairGenerator对象
        keyPairGenerator.initialize(KEYSIZE, secureRandom);  

        //生成密匙对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
  
        //得到公钥
        Key publicKey = keyPair.getPublic();  
  
        //得到私钥
        Key privateKey = keyPair.getPrivate();  
  
        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        String publicKeyBase64 = encoder.encode(publicKeyBytes);
        String privateKeyBase64 = encoder.encode(privateKeyBytes);

        //java语言需要pkcs8
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHMS);
            PrivateKey privateKey2 = keyFactory.generatePrivate(keySpec);
            privateKeyBase64 = encoder.encode(privateKey2.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RSAKey(privateKeyBase64, publicKeyBase64);
    }

    /**
     * 测试用
     * @param privateKey
     * @param publicKey
     * @return
     */
    private boolean testSign(String privateKey, String publicKey) {

        Map<String, String> sPara = new HashMap<>();
        sPara.put("name", "你猜啊");
        sPara.put("type", "workbook");
        String sign = SignUtils.sign(privateKey, sPara, SignUtils.SignType.RSA2, "UTF-8");

        return SignUtils.verify(sPara, sign, publicKey, SignUtils.SignType.RSA2, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        RSAKey rsaKey = RSAKeyUtils.getInstance()
                .keySize(2048).generateRSAKey();

        System.out.println(rsaKey.getPrivateKey());
        System.out.println("-----------------------------");
        System.out.println(rsaKey.getPublicKey());

        System.out.println(RSAKeyUtils.getInstance().testSign(rsaKey.getPrivateKey(), rsaKey.getPublicKey()));
    }
}  