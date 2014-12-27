package it.mbolis.hero;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;

public abstract class Images {

	private static final Map<Path, Image> images = new HashMap<>();

	public static Image get(String path) throws IOException {
		Path p = Paths.get(path).normalize();
		Image image = images.get(p);
		if (image == null) {
			image = ImageIO.read(p.toFile());
			images.put(p, image);
		}
		return image;
	}
}
