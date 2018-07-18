package win.hupubao.common.email;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author W.feihong
 * @date 2018-07-10
 */
public class Email implements Serializable {

    private static final long serialVersionUID = -5271472798010115316L;
    private static String ENCODING = "UTF-8";
    private static boolean DEBUG = false;

    private Config config;

    public static class Config {
        private String username;
        private String authcode;
        private String host;
        private int port;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAuthcode() {
            return authcode;
        }

        public void setAuthcode(String authcode) {
            this.authcode = authcode;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class SendTo {
        private String senderName;
        private String[] to;
        private String [] copyTo;
        private String title;
        private String content;
        private List<File> fileList;

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String[] getTo() {
            return to;
        }

        public void setTo(String[] to) {
            this.to = to;
        }

        public String[] getCopyTo() {
            return copyTo;
        }

        public void setCopyTo(String[] copyTo) {
            this.copyTo = copyTo;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<File> getFileList() {
            return fileList;
        }

        public void setFileList(List<File> fileList) {
            this.fileList = fileList;
        }
    }

    public Email(Config config) {
        this.config = config;
    }


    public Email debug() {
        DEBUG = true;
        return this;
    }

    public Email setEncoding(String encoding) {
        ENCODING = encoding;
        return this;
    }



    /**
     * @param sendTo
     * @throws Exception 发送邮件异常
     */
    public void send(SendTo sendTo) throws Exception {

        System.setProperty("mail.mime.splitlongparameters", "false");

        Properties prop = new Properties();
        //协议
        prop.setProperty("mail.transport.protocol", "smtp");
        //服务器
        prop.setProperty("mail.smtp.host", config.getHost());
        //端口
        prop.setProperty("mail.smtp.port", config.getPort() + "");
        //使用smtp身份验证
        prop.setProperty("mail.smtp.auth", "true");
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            e1.printStackTrace();
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(prop, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getAuthcode());
            }

        });

        session.setDebug(DEBUG);
        MimeMessage mimeMessage = new MimeMessage(session);
        //发件人
        mimeMessage.setFrom(new InternetAddress(config.getUsername(), sendTo.getSenderName()));        //可以设置发件人的别名
        //mimeMessage.setFrom(new InternetAddress(account));    //如果不需要就省略
        //收件人
        for (String emailTo : sendTo.getTo()) {
            if (StringUtils.isNotBlank(emailTo)) {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo.trim()));
            }
        }
        //抄送
        if(sendTo.getCopyTo() != null && sendTo.getCopyTo().length != 0){
            InternetAddress[] toCC = new InternetAddress[sendTo.getCopyTo().length];
            // 设置邮件消息的发送者
            for (int i = 0; i < sendTo.getCopyTo().length; i++) {
                toCC[i] = new InternetAddress(sendTo.getCopyTo()[i]);
            }
            mimeMessage.addRecipients(Message.RecipientType.CC, toCC);
        }
        //主题
        mimeMessage.setSubject(sendTo.getTitle());
        //时间
        mimeMessage.setSentDate(new Date());

        //容器类，可以包含多个MimeBodyPart对象
        Multipart mp = new MimeMultipart();

        //MimeBodyPart可以包装文本，图片，附件
        MimeBodyPart body = new MimeBodyPart();
        //HTML正文
        body.setContent(sendTo.getContent(), "text/html; charset=" + ENCODING);
        mp.addBodyPart(body);

        //附件
        if (sendTo.getFileList() != null
                && !sendTo.getFileList().isEmpty()) {
            for (File file :
                    sendTo.getFileList()) {
                BodyPart part = new MimeBodyPart();
                // 根据文件名获取数据源
                DataSource dataSource = new FileDataSource(file);
                DataHandler dataHandler = new DataHandler(dataSource);
                part.setDataHandler(dataHandler);
                part.setFileName(MimeUtility.encodeText(dataSource.getName()));
                mp.addBodyPart(part);
            }
        }

        mimeMessage.setContent(mp);

        //设置邮件内容
        //仅仅发送文本
        //mimeMessage.setText(content);
        mimeMessage.saveChanges();
        Transport.send(mimeMessage);
    }
}
