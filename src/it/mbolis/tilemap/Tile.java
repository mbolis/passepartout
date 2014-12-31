package it.mbolis.tilemap;

import java.util.LinkedList;
import java.util.List;

public class Tile {
	
	private Terrain terrain;
	
    private final char icon;
    private final List<Item> content = new LinkedList<Item>();

    public Tile(char icon) {
        this.icon = icon;
    }

    public char getIcon() {
        return icon;
    }
    
    public List<Item> getContent() {
		return content;
	}

    @Override
    public String toString() {
        return Character.toString(icon);
    }
}
