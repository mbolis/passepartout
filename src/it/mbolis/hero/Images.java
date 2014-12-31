package it.mbolis.hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;

public abstract class Images {

	private static final Map<String, Image> images = new HashMap<>();

	public static Image get(Color color, int size) {
		String key = "color://" + color + ":" + size;
		Image image = images.get(key);
		if (image == null) {
			image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.setColor(color);
			g.fillRect(0, 0, size, size);

			images.put(key, image);
		}
		return image;
	}

	public static Image get(String path) throws IOException {
		Path p = Paths.get(path).normalize();
		String key = p.toUri().toString();
		Image image = images.get(key);
		if (image == null) {
			image = ImageIO.read(p.toFile());
			images.put(key, image);
		}
		return image;
	}
}
