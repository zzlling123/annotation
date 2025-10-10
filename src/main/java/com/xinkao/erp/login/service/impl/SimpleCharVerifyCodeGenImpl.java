package com.xinkao.erp.login.service.impl;

import com.xinkao.erp.common.util.RandomUtils;
import com.xinkao.erp.login.mapper.VerifyCodeDao;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

@Service
public class SimpleCharVerifyCodeGenImpl {

    private static final String[] FONT_TYPES = { "\u5b8b\u4f53", "\u65b0\u5b8b\u4f53", "\u9ed1\u4f53", "\u6977\u4f53", "\u96b6\u4e66" };

    private static final int VALICATE_CODE_LENGTH = 5;

    private static void fillBackground(Graphics graphics, int width, int height) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        int num = 8;
        for (int i = 0; i < num; i++) {
            graphics.setColor(RandomUtils.randomColor(40, 150));
            Random random = new Random();
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            graphics.drawLine(x, y, x1, y1);
        }
    }

    /**
     * @Author ZSX
     * @Description 生成随机字符
     * @Date 2020/12/22 14:30
     * @Param [width, height, os]
     * @return String
     **/
    public String generate(int width, int height, OutputStream os) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        fillBackground(graphics, width, height);
        String randomStr = RandomUtils.randomString(VALICATE_CODE_LENGTH);
        createCharacter(graphics, randomStr);
        graphics.dispose();
        ImageIO.write(image, "JPEG", os);
        return randomStr;
    }

    /**
     * @Author ZSX
     * @Description 生成验证码
     * @Date 2020/12/22 14:30
     * @Param [width, height]
     * @return VerifyCodeDTO
     **/
    public VerifyCodeDao generate(int width, int height) {
        VerifyCodeDao verifyCode = null;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            String code = generate(width, height, baos);
            verifyCode = new VerifyCodeDao();
            verifyCode.setCode(code);
            verifyCode.setImgBytes(baos.toByteArray());
        } catch (IOException e) {
            verifyCode = null;
        }
        return verifyCode;
    }

    /**
     * @Author ZSX
     * @Description 设置字符颜色大小
     * @Date 2020/12/22 14:30
     * @Param [g, randomStr]
     * @return
     **/
    private void createCharacter(Graphics g, String randomStr) {
        char[] charArray = randomStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            g.setColor(new Color(50 + RandomUtils.nextInt(100),
                    50 + RandomUtils.nextInt(100), 50 + RandomUtils.nextInt(100)));
            g.setFont(new Font(FONT_TYPES[RandomUtils.nextInt(FONT_TYPES.length)], Font.BOLD, 26));
            g.drawString(String.valueOf(charArray[i]), 15 * i + 5, 19 + RandomUtils.nextInt(8));
        }
    }
}
