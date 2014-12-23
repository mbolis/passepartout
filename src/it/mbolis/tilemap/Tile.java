package it.mbolis.tilemap;

public class Tile {

    private final char icon;

    public Tile(char icon) {
        this.icon = icon;
    }

    public char getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return Character.toString(icon);
    }
}
