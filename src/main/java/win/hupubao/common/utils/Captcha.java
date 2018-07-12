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

import javax.imageio.ImageIO;

public class Captcha {

    private final static Random random = new Random();

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
    private ColorBounds NOISE_LINE_COLOR_BOUNDS = new ColorBounds("#999999", "#EEEEEE");
    /**
     * Bounds of captcha image background color.
     */
    private ColorBounds BACKGROUND_COLOR_BOUNDS = new ColorBounds("#AAAAAA", "#FFFFFF");
    /**
     * Bounds of captcha character color.
     */
    private ColorBounds CAPTCHA_CHARACTER_COLOR_BOUNDS = new ColorBounds("#888888", "#AAAAAA");

    public static Captcha getInstance() {
        return Captcha.CaptchaInstance.INSTANCE.singleton;
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


    static class ColorBounds {
        private String start = "#999999";
        private String end = "#FFFFFF";

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

    /**
     * Generate a base64 captcha image.
     * @return
     * @throws IOException
     */
    public String generateCaptchaImageBase64() throws IOException{
        BufferedImage bufferedImage = generateCaptchaImage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        return (NEED_PREFIX ? BASE64_IMAGE_PREFIX : "") + Base64.encodeBase64String(outputStream.toByteArray());
    }


    /**
     * Generate a captcha image.
     *
     * @return
     * @throws IOException
     */
    public BufferedImage generateCaptchaImage() {
        String captchaCode = RandomStringUtils.randomAlphanumeric(CAPTCHA_LENGTH);
        BufferedImage image = new BufferedImage(Captcha.WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[]{Color.WHITE, Color.CYAN,
                Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
                Color.PINK, Color.YELLOW};
        float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[random.nextInt(colorSpaces.length)];
            fractions[i] = random.nextFloat();
        }
        Arrays.sort(fractions);

        graphics2D.setColor(getRandomColor(NOISE_LINE_COLOR_BOUNDS));// 干扰线颜色
        graphics2D.fillRect(0, 0, WIDTH, HEIGHT);

        Color bgColor = getRandomColor(BACKGROUND_COLOR_BOUNDS);
        graphics2D.setColor(bgColor);// 背景色
        graphics2D.fillRect(0, 2, WIDTH, HEIGHT - 4);

        //绘制干扰线
        graphics2D.setColor(getRandomColor(NOISE_LINE_COLOR_BOUNDS));// 干扰线颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(WIDTH - 1);
            int y = random.nextInt(HEIGHT - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            graphics2D.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        int area = (int) (NOISE_RATE * WIDTH * HEIGHT);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        shear(graphics2D, WIDTH, HEIGHT, bgColor);

        graphics2D.setColor(getRandomColor(CAPTCHA_CHARACTER_COLOR_BOUNDS));
        int fontSize = WIDTH / CAPTCHA_LENGTH;
        Font font = new Font("Serif", Font.ITALIC, fontSize);
        graphics2D.setFont(font);

        char[] chars = captchaCode.toCharArray();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), (WIDTH / CAPTCHA_LENGTH) * i + fontSize / 2, HEIGHT / 2);
            graphics2D.setTransform(affine);
            graphics2D.drawChars(chars, i, 1, ((WIDTH - 10) / CAPTCHA_LENGTH) * i + 5, HEIGHT / 2 + fontSize / 2 - 10);
        }

        graphics2D.dispose();

        return image;
    }


    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
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

        String red = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成绿色颜色代码
        String green = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成蓝色颜色代码
        String blue = Integer.toHexString(random.nextInt(256)).toUpperCase();

        int r = start + random.nextInt(end - start);

    }
}