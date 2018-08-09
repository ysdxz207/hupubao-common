**访问请求限制工具**

-简介

基于[ExpiringMap](https://github.com/jhalterman/expiringmap)和AOP的访问请求控制工具


- 使用方法

1.需要加入maven插件：aspectj-maven-plugin

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.10</version>
    <configuration>
        <encoding>UTF-8</encoding>
        <complianceLevel>1.8</complianceLevel>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>win.hupubao</groupId>
                <artifactId>hupubao-common</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </execution>
    </executions>
</plugin>

```

2、方法需要有HttpServletRequest类型参数
3、添加注解@RequestLimit
4、配置参数：interval是访问间隔，单位毫秒，
adapter是继承了RequestLimitAdapter的处理拦截请求的方法的类，
updated为true则每次请求都会重新算请求时间，默认false

```java

public class TestRequestLimitController {
    
    @RequestLimit(interval = 5 * 1000, adapter = MyRequestLimitAdapter.class, updated = true)
    public static Object requestLimit(HttpServletRequest request) {
    
        //Your code.
        return null;
    }
}

```

- Adapter示例

```java


import com.alibaba.fastjson.JSON;
import win.hupubao.common.handler.adaper.RequestLimitAdapter;
import win.hupubao.common.utils.LoggerUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

public class MyRequestLimitAdapter extends RequestLimitAdapter {
    @Override
    public Object handle(long interval,
                         boolean updated,
                         long limitTimeLast,
                         Object ... args) {
        Optional<Object> optional = Arrays.stream(args).filter(o -> o instanceof HttpServletResponse).findAny();

        long second = limitTimeLast % 1000 > 0 ? (limitTimeLast / 1000 + 1) : (limitTimeLast / 1000);
        String msg = "操作频繁，请" + second + "秒后重试。";

        ResponseBean responseBean = new ResponseBean();
        if (optional.isPresent()) {

            HttpServletResponse response = (HttpServletResponse) optional.get();

            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = null;
            try (PrintWriter out = response.getWriter()){
                out.print(JSON.toJSONString(responseBean.errorMessage(msg)));
            } catch (IOException e) {
                LoggerUtils.error("[MyRequestLimitAdapter]异常:", e);
            }
            return null;
        } else {
            return responseBean.errorMessage(msg);
        }
    }
}

```