/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package win.hupubao.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestCaptcha {

    public static void main(String[] args) throws IOException {
        BufferedImage image = Captcha.getInstance()
                .captchaLength(4)
                .noiseRate(0.01f)
                .noiseLineColorBounds(new Captcha.ColorBounds("#666633", "#997744"))
                .generateCaptchaImage();

        File file = new File("I:/" + Captcha.getInstance().getCaptchaCode() + ".jpg");
        ImageIO.write(image, "jpg", file);
    }
}
