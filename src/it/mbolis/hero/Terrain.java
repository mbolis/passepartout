package it.mbolis.hero;

public class Terrain {

	public static final Terrain DEFAULT = new Terrain(Skin.DEFAULT);
	public static void setDefault(Terrain terrain) {
		DEFAULT.skin = terrain.skin;
	}

	private Skin skin;

	public Terrain(Skin skin) {
		this.skin = skin;
	}

	public Skin getSkin() {
		return skin;
	}
}
