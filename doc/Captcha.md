**传统验证码生成工具**

- 使用方法

```java


import win.hupubao.common.utils.Captcha;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestCaptcha {
    public static void main(String[] args) throws IOException {
        Captcha captcha = Captcha.getInstance().isExcludeSimilerCharacter(false)
                .captchaLength(4)
                .width(80)
                .height(34)
                .noiseLineNum(10)
                .noiseRate(0.06f)
                .captchaCharacterColorBounds(new Captcha.ColorBounds("#994444", "#CC5555"))
                .backgroundColorBounds(new Captcha.ColorBounds("#444444", "#999955"));
        Captcha.CaptchaImage captchaImage = captcha.generate();
                //是否排除相似字母
                ImageIO.write(captchaImage.getBufferedImage(), "jpg", new File("D://" + captchaImage.getCaptchaCode() + ".jpg"));

        System.out.println(captchaImage.getCaptchaCode());
        String imageBase64 = captcha.captchaLength(6).generate().getBase64Image();
        System.out.println(imageBase64);

    }
}

```