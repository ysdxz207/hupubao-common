**异常发送邮件工具**

- 使用方法

```java

import win.hupubao.common.email.Email;
import win.hupubao.common.utils.ExceptionEmailSender;

public class TestExceptionEmailSender {

    public static void init () {
        Email.Config emailConfig = new Email.Config();
        emailConfig.setUsername("Email address.");
        emailConfig.setAuthcode("Auth code or password");
        emailConfig.setHost("smtp.sina.com");
        emailConfig.setPort(465);

        Email.SendTo sendTo = new Email.SendTo();
        sendTo.setTitle("Test exception email.");
        sendTo.setSenderName("System");
        sendTo.setTo(new String [] {"email receiver"});
        ExceptionEmailSender.getInstance()
                .extractPackages("win.hupubao")
                .config(emailConfig, sendTo);
    }

    public static void main(String[] args) {

        init();

        try {
            String str = null;
            System.out.println(str.equals(""));
        } catch (Exception e) {
            ExceptionEmailSender.getInstance().sendException(e);
        }
    }
}
```