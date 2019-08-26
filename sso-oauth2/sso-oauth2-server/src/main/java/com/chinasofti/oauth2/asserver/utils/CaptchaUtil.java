package com.chinasofti.oauth2.asserver.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 生成验证码
 */
public class CaptchaUtil {
	public String getVerificationCode(HttpServletRequest request,
			HttpServletResponse response) {
		int width = 41;
		int height = 18;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		Random random = new Random();

		g.setColor(getRandomColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setColor(new Color(0, 0, 255));
		g.drawRect(0, 0, width - 1, height - 1);

		g.setColor(getRandomColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		StringBuffer code = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			code.append(rand);
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			Font font = new Font("宋体", Font.BOLD, 12);
			g.setFont(font);
			g.drawString(rand, 9 * i + 6, 14);
		}
		g.dispose();
		try {
			ImageIO.write(image, "JPEG", response.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return code.toString();
	}

	private Color getRandomColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
