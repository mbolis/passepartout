package it.mbolis.hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;

public abstract class Skin {

	public static final Skin DEFAULT = createStatic(Color.black, 64);

	private final Image image;

	public static Skin createStatic(Color color, int size) {
		Image image = Images.get(color, size);
		return new Static(image);
	}
	
	public static Skin createStatic(String imagePath) {
		Image image;
		try {
			image = Images.get(imagePath);
		} catch (IOException e) {
			return null;
		}
		return new Static(image);
	}

	private Skin(Image image) {
		this.image = image;
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	public static final class Static extends Skin {

		private Static(Image image) {
			super(image);
		}
	}
}
