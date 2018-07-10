package win.hupubao.common.utils;

import win.hupubao.common.email.Email;

public class Hei {
    public void send(String str) {
        try {
            System.out.println(str.equals(""));
        } catch (Exception e) {
            Email.Config config = new Email.Config();
            config.setUsername("");
            config.setAuthcode("");
            config.setHost("smtp.sina.com");
            config.setPort(465);
            Email.SendTo sendTo = new Email.SendTo();
            sendTo.setTo(new String [] {"ysdxz207@qq.com"});
            sendTo.setSenderName("异常报告");
            ExceptionEmailSender.getInstance().config(config, sendTo)
                    .extractPackages("win.hupubao")
                    .sendException("异常", e);
        }
    }
}
