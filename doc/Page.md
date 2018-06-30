**Http工具**

- 使用方法

```java

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import win.hupubao.common.http.Page;

public class TestPage {
    public static void main(String[] args) {
        JSONObject paramsJson = new JSONObject();//json参数，当然也可以用Map
        paramsJson.put("q", "太阳");

        String paramsXml = "<xml><id>123456</id><name>大帅比</name></xml>";//raw参数，post方式
        Page.Response responseGet = Page.create()
                .connectionTimeout(3000)
                .retryTimes(3)
                .request("https://www.google.com/search",
                        paramsJson,
                        Connection.Method.GET);

        System.out.println(responseGet);

        Page.Response responsePost = Page.create()
                .connectionTimeout(3000)
                .retryTimes(3)
                .request("https://www.google.com/search",
                        paramsXml,
                        Connection.Method.POST);

        System.out.println(responsePost);
    }
}

```