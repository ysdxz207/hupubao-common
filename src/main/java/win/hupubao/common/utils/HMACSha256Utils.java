package win.hupubao.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * HMAC_SHA256签名工具
 * @author ysdxz207
 * @date 2018-12-20 10:05:26
 */
public class HMACSha256Utils {
    private static final String INSTANCE_NAME = "HmacSHA256";

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String sign(String key,
                              Map<String, String> params) {
        String signPre = StringUtils.createLinkString(params) + "&key=" + key;
        return hmacSha256(signPre, key);
    }

    public static String hmacSha256(String str, String key) {
        String hash = "";
        try {
            Mac sha256Hmac = Mac.getInstance(INSTANCE_NAME);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), INSTANCE_NAME);
            sha256Hmac.init(secretKey);
            byte[] bytes = sha256Hmac.doFinal(str.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            LoggerUtils.error("HMAC_SHA256 sign error:", e);
        }
        return hash.toUpperCase();
    }

    public static boolean verify(String sign,
                                 Map<String, String> params,
                                 String key) {
        if (StringUtils.isBlank(sign)) {
            return false;
        }
        return sign.equals(sign(key, params));
    }
}
