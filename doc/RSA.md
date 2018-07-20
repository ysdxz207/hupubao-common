**RSA、RSA2工具**

> 1.生成私钥公钥对

> 2.签名，验签

> 3.加密解密

- 使用方法



```java

import win.hupubao.common.utils.rsa.RSA;

import java.util.HashMap;
import java.util.Map;

public class TestRsa {
    public static void main(String[] args) throws Exception {
        RSA.SignType signType = RSA.SignType.RSA2;
        RSA.RSAKey rsaKey = RSA.getInstance()
                .keySize(2048).generateRSAKey();

        Map<String, String> map = new HashMap<>();
        map.put("ccc", "c");
        map.put("aaa", "a");
        map.put("eee", "e");
        map.put("ddd", "d");
        String sign = RSA.getInstance().sign(rsaKey.getPrivateKey(),
                map, signType);

        boolean success = RSA.getInstance().rsaKey(rsaKey)
                .verify(map, sign, signType);

        System.out.println(rsaKey.getPrivateKey());
        System.out.println(rsaKey.getPublicKey());

        System.out.println("-------签名结果-------");
        System.out.println(sign);
        System.out.println("-------验签结果-------");
        System.out.println(success);

        String str = "abcdefg";
        System.out.println("-------私钥加密，公钥解密-------");

        RSA rsa = RSA.getInstance()
                .rsaKey(rsaKey);
        String encrypted = rsa.encryptByPrivateKey(str);
        System.out.println(encrypted);
        String decrypted = rsa.decryptByPublicKey(encrypted);
        System.out.println(decrypted);

        System.out.println("-------公钥加密，私钥解密-------");

        String encrypted2 = rsa.encryptByPublicKey(str);
        System.out.println(encrypted2);
        String decrypted2 = rsa.decryptByPrivateKey(encrypted2);
        System.out.println(decrypted2);
    }
}
```