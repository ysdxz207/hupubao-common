package win.hupubao.common.utils;

import spark.Request;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getIp(Request request) {
        HttpServletRequest req = request.raw();
        String remoteAddr = req.getRemoteAddr();
        String forwarded = req.getHeader("X-Forwarded-For");
        String realIp = req.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded;
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                ip = realIp + "/" + forwarded.replaceAll(", " + realIp, "");
            }
        }
        return ip == null ? "" : ip;
    }
}