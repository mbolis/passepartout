package it.mbolis.tilemap;

import static java.util.Arrays.asList;

import java.util.Iterator;

public class TileRow implements Iterable<Tile> {

    private final Tile[] tiles;

    TileRow(Tile[] tiles) {
        this.tiles = tiles;
    }

    @Override
    public Iterator<Tile> iterator() {
        return asList(tiles).iterator();
    }

}
