package win.hupubao.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import win.hupubao.common.email.Email;

import java.util.Set;

/**
 * @author L.feihong
 * @date 2018-07-10
 */
public class ExceptionEmailSender {

    private Email.SendTo sendTo;
    private static String PACKAGES_EXCEPTION_EXTRACT;

    private static Email email;

    public static ExceptionEmailSender getInstance() {
        return ExceptionEmailSender.ExceptionEmailInstance.INSTANCE.singleton;
    }
    private enum ExceptionEmailInstance {
        INSTANCE;
        ExceptionEmailInstance() {
            singleton = new ExceptionEmailSender();
        }
        private ExceptionEmailSender singleton;
    }


    public ExceptionEmailSender config(Email.Config config,
                                       Email.SendTo sendTo) {
        email = new Email(config);
        this.sendTo = sendTo;
        return this;
    }

    /**
     * styles
     */
    private static final String STYLE_TH_TD = "font-size: 0.95em;text-align: center;padding: 4px;border-collapse: collapse;  border: 1px solid #cff8fe;border-width: 1px 0 1px 0;";
    private static final String STYLE_TH = "background-color: #ACF3FF;color: #000000;";


    public ExceptionEmailSender extractPackages(String extractPackages) {
        PACKAGES_EXCEPTION_EXTRACT = extractPackages;
        return this;
    }

    public void sendException(Throwable ex) {
        try {
            sendTo.setContent(getExceptionTableString(ex));
            email.send(sendTo);
        } catch (Exception e) {
            LoggerUtils.error("Send exception email failed:", e);
            e.printStackTrace();
        }
    }


    private static String getExceptionTableString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbHappened = new StringBuilder();
        StringBuilder sbTable = new StringBuilder();

        String happendClass = "";
        String happendNum = "";
        try {
            JSONObject exJson = JSON.parseObject(JSON.toJSONString(ex));

            String type = ex.toString();
            sb.append("<br><p>");
            sb.append("异常类：");
            sb.append(type);

            JSONArray stackTraceArr = exJson.getJSONArray("stackTrace");

            Set<String> keys = ((JSONObject) stackTraceArr.get(0)).keySet();
            sbTable.append("<table  style=\"width: 70%;margin: 15px 0;border: 0;\">");
            sbTable.append("<tr style=\"border: 1px solid #cff8fe;\">");
            for (String key : keys) {
                sbTable.append("<th style=\"");
                sbTable.append(STYLE_TH_TD);
                sbTable.append(STYLE_TH);
                sbTable.append("\">");
                sbTable.append(key);
                sbTable.append("</th>");
            }
            sbTable.append("</tr>");


            for (int i = 0; i < stackTraceArr.size(); i++) {
                Object stackTrace = stackTraceArr.get(i);

                if (i % 2 == 0) {
                    sbTable.append("<tr style=\"background-color: #e3fbfe;\">");
                } else {
                    sbTable.append("<tr style=\"background-color: #fdfdfd;\">");
                }

                JSONObject jsonStackTrace = (JSONObject) stackTrace;
                String className = jsonStackTrace.getString("className");
                String lineNumber = jsonStackTrace.getString("lineNumber");

                for (String key : keys) {
                    sbTable.append("<td style=\"");
                    sbTable.append(STYLE_TH_TD);
                    sbTable.append("\">");
                    sbTable.append(jsonStackTrace.getString(key));
                    sbTable.append("</td>");
                }

                sbTable.append("</tr>");

                //检测异常发生类
                if (className.contains(PACKAGES_EXCEPTION_EXTRACT)
                        && StringUtils.isEmpty(happendNum)) {
                    happendClass = className;
                    happendNum = lineNumber;
                }
            }

            sbTable.append("</table>");

            sbHappened.append("</p><p>");
            sbHappened.append("发生类：");
            sbHappened.append(happendClass);
            sbHappened.append("</p><p>");
            sbHappened.append("发生行：");
            sbHappened.append(happendNum);
            sbHappened.append("</p>");

        } catch (Exception e) {
            sb = new StringBuilder();
            sb.append(JSON.toJSONString(ex));
        }

        sb.append(sbHappened);
        sb.append(sbTable);
        return sb.toString();
    }
}
