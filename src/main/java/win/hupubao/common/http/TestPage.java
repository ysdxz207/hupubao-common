package win.hupubao.common.http;

import com.alibaba.fastjson.JSONObject;

import static org.jsoup.Connection.Method.*;

public class TestPage {
    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("nihao", "你好");

        String params = json.toJSONString();
        Page.Response response = Page.create().request("", params, POST);

        System.out.println(response);
    }
}
