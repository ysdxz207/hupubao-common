package win.hupubao.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author feihong
 * @date 2017-08-10
 */
public class StringUtils {

    public static boolean isBlank(Object obj) {
        return obj == null || spark.utils.StringUtils.isBlank(obj.toString())
                || obj.toString().equalsIgnoreCase("null");
    }

    public static boolean isNotBlank(Object obj){
         return !isBlank(obj);
    }

    public static Integer parseInteger(String str) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return Integer.valueOf(m.replaceAll("").trim());
    }

    /**
     * 首字母转大写
     * @param name
     * @return
     */
    public static String firstToUpperCase(String name) {
        return StringUtils.isBlank(name) ? "" : name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 首字母转小写
     * @param name
     * @return
     */
    public static String firstToLowerCase(String name) {
        return StringUtils.isBlank(name) ? "" : name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    public static void main(String[] args) {
        String str = "int(12)";
        System.out.println(parseInteger(str));
    }

    public static String join(Object[] strAry, String join){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<strAry.length;i++){
            if(i==(strAry.length-1)){
                sb.append(strAry[i]);
            }else{
                sb.append(strAry[i]).append(join);
            }
        }

        return new String(sb);
    }

    /**
     * @param htmlStr
     * @return
     * 删除Html标签
     */
    public static String delHTMLTag(String htmlStr) {
        String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regExHtml = "<[^>]+>"; // 定义HTML标签的正则表达式
        String regExSpace = "\\s*|\t|\r|\n";//定义空格回车换行符

        Pattern p_script = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签
        Pattern p_style = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签
        Pattern p_html = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签
        Pattern p_space = Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        return htmlStr.trim(); // 返回文本字符串
    }

    /**
     * 删除空格，换行符，制表符
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
