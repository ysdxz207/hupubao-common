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

        //生成BufferedImage
        Captcha captchaBufferedImage = Captcha.create();
        BufferedImage bufferedImage = captchaBufferedImage
                .noiseLineNum(30)
                .noiseRate(0.36f)
                .captchaCharacterColorBounds(new Captcha.ColorBounds("#994444", "#CC5555"))
                .backgroundColorBounds(new Captcha.ColorBounds("#444444", "#999955")).generateCaptchaImage();
        ImageIO.write(bufferedImage, "jpg", new File("D://" + captchaBufferedImage.getCaptchaCode() + ".jpg"));
        //生成base64
        String imageBase64 = Captcha.create()
                .captchaLength(6).generateCaptchaImageBase64();
        System.out.println(imageBase64);

    }
}
```