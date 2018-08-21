package win.hupubao.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtils {


    private static final List<String> LOCAL_ADDRESS_LIST = new ArrayList<String>() {
        {
            add("127.0.0.1");
            add("0:0:0:0:0:0:0:1");
        }
    };

    private static final Pattern PATTERN_LINUX = Pattern
            .compile("[0-9a-f]+:[0-9a-f]+:[0-9a-f]+:[0-9a-f]+:[0-9a-f]+:[0-9a-f]+");

    private static final Pattern PATTERN_WINDOWS = Pattern
            .compile("[0-9a-f]+-[0-9a-f]+-[0-9a-f]+-[0-9a-f]+-[0-9a-f]+-[0-9a-f]+");


    /**
     * <p>
     * Web 服务器反向代理中用于存放客户端原始 IP 地址的 Http header 名字，
     * 若新增其他的需要增加或者修改其中的值。
     * </p>
     */
    private static final String[] PROXY_REMOTE_IP_ADDRESS = { "X-Forwarded-For", "X-Real-IP" };
    /**
     * <p>
     * 获取请求的客户端的 IP 地址。若应用服务器前端配有反向代理的 Web 服务器，
     * 需要在 Web 服务器中将客户端原始请求的 IP 地址加入到 HTTP header 中。
     * 详见 {@link #PROXY_REMOTE_IP_ADDRESS}
     * </p>
     */
    public static String getRemoteIp( HttpServletRequest request ) {
        for ( int i = 0 ; i < PROXY_REMOTE_IP_ADDRESS.length ; i++ ) {
            String ip = request.getHeader( PROXY_REMOTE_IP_ADDRESS[i] );
            if ( ip != null && ip.trim().length() > 0 ) {
                return getRemoteIpFromForward( ip.trim() );
            }
        }
        return request.getRemoteHost();
    }

    /**
     * <p>
     * 从 HTTP Header 中截取客户端连接 IP 地址。如果经过多次反向代理，
     * 在请求头中获得的是以“,&lt;SP&gt;”分隔 IP 地址链，第一段为客户端 IP 地址。
     * </p>
     *
     * @param xforwardIp 从 HTTP 请求头中获取转发过来的 IP 地址链
     * @return 客户端源 IP 地址
     */
    private static String getRemoteIpFromForward( String xforwardIp ) {
        int commaOffset = xforwardIp.indexOf( ',' );
        if ( commaOffset < 0 ) {
            return xforwardIp;
        }
        return xforwardIp.substring( 0 , commaOffset );
    }

    public static String getMac(String ip) {
        Pattern macpt = null;

        // Find OS and set command according to OS
        String OS = System.getProperty("os.name").toLowerCase();

        String[] cmd;
        if (OS.contains("win")) {
            // Windows
            macpt = PATTERN_WINDOWS;
            String[] a = { "arp", "-a", ip };
            cmd = a;
        } else {
            // Mac OS X, Linux
            macpt = PATTERN_LINUX;
            String[] a = { "arp", ip };
            cmd = a;
        }

        try {
            // Run command
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            // read output with BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line = reader.readLine();

            // Loop trough lines
            while (line != null) {
                Matcher m = macpt.matcher(line);

                // when Matcher finds a Line then return it as result
                if (m.find()) {
                    return m.group(0);
                }

                line = reader.readLine();
            }

        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
        }

        // Return empty string if no MAC is found
        return "";
    }

    public static boolean isLocalAddress(String ip) {
        return LOCAL_ADDRESS_LIST.contains(ip);
    }

}