package it.mbolis.tilemap;

import java.util.Iterator;

public class TileMap implements Iterable<Tile[]> {

    private final Tile[][] tiles;

    public TileMap(int width, int height) {
        this(new Tile[height][width]);
    }

    private TileMap(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public void set(int x, int y, Tile tile) {
        tiles[y][x] = tile;
    }

    public Tile get(int x, int y) {
        return tiles[y][x];
    }

    public TileMap window(int x, int y, int width, int height) {
        Tile[][] window = new Tile[height][width];
        for (int iwy = 0, iy = Math.max(y, 0); iy < y + height; iwy++, iy++) {
            if (iy >= tiles.length) {
                break;
            }

            Tile[] row = tiles[iy];
            for (int iwx = 0, ix = Math.max(x, 0); ix < x + width; iwx++, ix++) {
                if (ix >= row.length) {
                    break;
                }

                window[iwy][iwx] = row[ix];
            }
        }

        return new TileMap(window);
    }

    public static void main(String[] args) {
        TileMap map = new TileMap(10, 8);

        map.set(0, 0, new Tile('o'));
        map.set(9, 7, new Tile('x'));
        map.set(0, 7, new Tile('|'));
        map.set(9, 0, new Tile('-'));

        Tile block = new Tile('#');
        map.set(1, 1, block);
        map.set(1, 6, block);
        map.set(8, 1, block);
        map.set(8, 6, block);

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 10; x++) {
                Tile tile = map.get(x, y);
                if (tile == null) {
                    System.out.print('·');
                } else {
                    System.out.print(tile.getIcon());
                }
            }
            System.out.println();
        }

        System.out.println();

        TileMap window = map.window(1, 1, 8, 6);
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = window[y][x];
                if (tile == null) {
                    System.out.print('·');
                } else {
                    System.out.print(tile.getIcon());
                }
            }
            System.out.println();
        }
    }

    @Override
    public Iterator<Tile[]> iterator() {
        return new Iterator<Tile[]>() {

            private int current = 0;

            @Override
            public boolean hasNext() {
                return tiles.length > current;
            }

            @Override
            public Tile[] next() {
                return tiles[current++];
            }
        };
    }
}
