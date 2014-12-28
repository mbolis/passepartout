package it.mbolis.hero;

public class Map {

	private final Terrain[][] tiles;

	public Map(int width, int height) {
		tiles = new Terrain[height][width];
	}

	public Terrain[][] getTiles() {
		return tiles;
	}
}
