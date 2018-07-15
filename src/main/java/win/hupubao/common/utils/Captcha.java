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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import win.hupubao.common.utils.rsa.RSA;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * 传统验证码生成器
 *
 * @author feihong
 * @date 2018-07-12
 */
public class Captcha {

    private static final Random random = new Random();
    private static final Pattern PATTERN_COLOR = Pattern.compile("^#([0-9a-fA-F]{6})$");
    private static final String CHARACTERS = "23456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * Base64 image prefix.
     */
    private static String BASE64_IMAGE_PREFIX = "data:image/jpg;base64,";
    /**
     * Need base64 image prefix or not.
     */
    private static boolean NEED_PREFIX = true;
    /**
     * Character length of captcha.
     */
    private static int CAPTCHA_LENGTH = 4;
    /**
     * Image width of captcha.
     */
    private static int WIDTH = 64;
    /**
     * Image height of captcha.
     */
    private static int HEIGHT = 30;

    /**
     * Noise rate of captcha background.
     */
    private static float NOISE_RATE = 0.06f;
    /**
     * Bounds of captcha image noise line color.
     */
    private ColorBounds NOISE_LINE_COLOR_BOUNDS = new ColorBounds("#666633", "#997744");
    /**
     * Bounds of captcha image background color.
     */
    private ColorBounds BACKGROUND_COLOR_BOUNDS = new ColorBounds("#BBBBBB", "#CCCCCC");
    /**
     * Bounds of captcha character color.
     */
    private static ColorBounds CAPTCHA_CHARACTER_COLOR_BOUNDS = new ColorBounds("#554444", "#886677");
    /**
     * Noise line numbers.
     */
    private static int NOISE_LINE_NUM = 20;
    /**
     * Exclude similer character or not.
     */
    private static boolean EXCLUDE_SIMILER_CHARACTER = true;

    private CaptchaImage captchaImage;


    public Captcha() {
    }

    public static Captcha getInstance() {
        Captcha instance = Captcha.CaptchaInstance.INSTANCE.singleton;
        return instance;
    }

    private enum CaptchaInstance {
        INSTANCE;

        CaptchaInstance() {
            singleton = new Captcha();
        }

        private Captcha singleton;
    }


    public Captcha width(int width) {
        WIDTH = width;
        return this;
    }

    public Captcha height(int height) {
        HEIGHT = height;
        return this;
    }

    public Captcha needPrefix(boolean needPrefix) {
        NEED_PREFIX = needPrefix;
        return this;
    }

    public Captcha captchaLength(int captchaLength) {
        CAPTCHA_LENGTH = captchaLength;
        return this;
    }

    public Captcha noiseRate(float noiseRate) {
        NOISE_RATE = noiseRate;
        return this;
    }

    public Captcha base64ImagePrefix(String base64ImagePrefix) {
        BASE64_IMAGE_PREFIX = base64ImagePrefix;
        return this;
    }

    public Captcha noiseLineColorBounds(ColorBounds noiseLineColorBounds) {
        NOISE_LINE_COLOR_BOUNDS = noiseLineColorBounds;
        return this;
    }

    public Captcha backgroundColorBounds(ColorBounds backgroundColorBounds) {
        BACKGROUND_COLOR_BOUNDS = backgroundColorBounds;
        return this;
    }

    public Captcha captchaCharacterColorBounds(ColorBounds captchaCharacterColorBounds) {
        CAPTCHA_CHARACTER_COLOR_BOUNDS = captchaCharacterColorBounds;
        return this;
    }

    public Captcha noiseLineNum(int noiseLineNum) {
        NOISE_LINE_NUM = noiseLineNum;
        return this;
    }

    public Captcha isExcludeSimilerCharacter(boolean excludeSimilerCharacter) {
        EXCLUDE_SIMILER_CHARACTER = excludeSimilerCharacter;
        return this;
    }


    public static class CaptchaImage implements Serializable{
        private static final long serialVersionUID = 1L;
        private String captchaCode;
        private String base64Image;
        private BufferedImage bufferedImage;

        public String getCaptchaCode() {
            return captchaCode;
        }

        public void setCaptchaCode(String captchaCode) {
            this.captchaCode = captchaCode;
        }

        public String getBase64Image() {
            return base64Image;
        }

        public void setBase64Image(String base64Image) {
            this.base64Image = base64Image;
        }

        public BufferedImage getBufferedImage() {
            return bufferedImage;
        }

        public void setBufferedImage(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }
    }
    public static class ColorBounds {
        private String start = "#999999";
        private String end = "#EEEEEE";

        public ColorBounds(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public CaptchaImage generate() {
        CaptchaImage captchaImage = new CaptchaImage();
        String captchaCode;
        if (EXCLUDE_SIMILER_CHARACTER) {
            captchaCode = generateChapterCodeWithoutSimilerCharacters();
        } else {
            captchaCode = RandomStringUtils.randomAlphanumeric(CAPTCHA_LENGTH);
        }

        captchaImage.setCaptchaCode(captchaCode);
        captchaImage.setBufferedImage(generateCaptchaImage(captchaCode));
        captchaImage.setBase64Image(generateCaptchaImageBase64(captchaCode));

        return captchaImage;
    }

    /**
     * Generate a base64 captcha image.
     *
     * @return
     * @throws IOException
     */
    private String generateCaptchaImageBase64(String captchaCode) {
        BufferedImage bufferedImage = generateCaptchaImage(captchaCode);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (NEED_PREFIX ? BASE64_IMAGE_PREFIX : "") + Base64.encodeBase64String(outputStream.toByteArray());
    }


    /**
     * Generate a captcha image.
     *
     * @return
     * @throws IOException
     */
    private BufferedImage generateCaptchaImage(String captchaCode) {

        BufferedImage image = new BufferedImage(Captcha.WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Background color.
        Color bgColor = getRandomColor(BACKGROUND_COLOR_BOUNDS);
        graphics2D.setColor(bgColor);
        graphics2D.fillRect(0, 0, WIDTH, HEIGHT);

        //Draw noise line.
        if (NOISE_LINE_NUM > 0) {
            graphics2D.setColor(getRandomColor(NOISE_LINE_COLOR_BOUNDS));
            for (int i = 0; i < NOISE_LINE_NUM; i++) {
                int x = random.nextInt(WIDTH - 1);
                int y = random.nextInt(HEIGHT - 1);
                int xl = random.nextInt(6) + 1;
                int yl = random.nextInt(12) + 1;
                graphics2D.drawLine(x, y, x + xl + 40, y + yl + 20);
            }
        }

        //Noise point.
        int area = (int) (NOISE_RATE * WIDTH * HEIGHT);
        for (int i = 0; i < area; i++) {
            int xxx = random.nextInt(WIDTH);
            int yyy = random.nextInt(HEIGHT);
            int rgb = getRandomColor(NOISE_LINE_COLOR_BOUNDS).getRGB();
            image.setRGB(xxx, yyy, rgb);
        }

        //Shear image.
        shear(graphics2D, WIDTH, HEIGHT, bgColor);

        //Captcha characters.
        int fontSize = (int) (HEIGHT * 0.8);
        int fx = 0;
        int fy;
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        for (int i = 0; i < captchaCode.length(); i++) {
            fy = (int) ((Math.random() * 0.3 + 0.6) * HEIGHT);
            graphics2D.setColor(getRandomColor(CAPTCHA_CHARACTER_COLOR_BOUNDS));
            graphics2D.drawString(captchaCode.charAt(i) + "", fx, fy);
            fx += (WIDTH / captchaCode.length()) * (Math.random() * 0.3 + 0.8);
        }

        graphics2D.dispose();

        return image;
    }


    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }

    private Color getRandomColor(ColorBounds colorBounds) {

        String start = colorBounds.getStart();
        String end = colorBounds.getEnd();

        if (!PATTERN_COLOR.matcher(start).matches()
                || !PATTERN_COLOR.matcher(end).matches()) {
            return Color.WHITE;
        }

        int startRInt = Integer.parseInt(start.substring(1, 3), 16);
        int startGInt = Integer.parseInt(start.substring(3, 5), 16);
        int startBInt = Integer.parseInt(start.substring(5, 7), 16);

        int endRInt = Integer.parseInt(end.substring(1, 3), 16);
        int endGInt = Integer.parseInt(end.substring(3, 5), 16);
        int endBInt = Integer.parseInt(end.substring(5, 7), 16);

        int r = startRInt + random.nextInt(endRInt - startRInt);
        int g = startGInt + random.nextInt(endGInt - startGInt);
        int b = startBInt + random.nextInt(endBInt - startBInt);
        return new Color(r, g, b);
    }

    private static String generateChapterCodeWithoutSimilerCharacters() {
        int codesLen = CHARACTERS.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(rand.nextInt(codesLen - 1)));
        }
        return sb.toString();
    }
}