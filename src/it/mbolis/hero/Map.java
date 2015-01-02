package it.mbolis.hero;

public class Map {

	public final int width, height;

	private final Terrain[][] tiles;

	public Map(int width, int height) {
		this.width = width;
		this.height = height;

		tiles = new Terrain[height][width];
	}

	public Terrain[][] getTiles() {
		return tiles;
	}
}
