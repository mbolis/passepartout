package it.mbolis.hero;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Board extends JPanel {

	private static final int TILE = 8;

	private final Map map;

	private final Dimension size;

	public Board(int width, int height) {
		map = new Map(width, height);
		size = new Dimension(width * TILE, height * TILE);
	}

	@Override
	public Dimension getMinimumSize() {
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		return size;
	}

	@Override
	public Dimension getMaximumSize() {
		return size;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Terrain[][] tiles = map.getTiles();
		for (int y = 0; y < tiles.length; y++) {
			Terrain[] row = tiles[y];
			for (int x = 0; x < row.length; x++) {
				Terrain tile = row[x];
				Graphics gt = g.create(x * TILE, y * TILE, TILE, TILE);
				if (tile != null) {
					tile.getSkin().paint(gt);
				}
				//gt.setColor(Color.red);
				//gt.drawRect(0, 0, TILE, TILE);
				gt.dispose();
			}
		}
	}

	public static void main(String[] args) {
		final Board board = new Board(48, 48);
		board.setBackground(Color.black);
		board.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int x = e.getX() / TILE;
					int y = e.getY() / TILE;
					Terrain[][] tiles = board.map.getTiles();
					Terrain tile = tiles[y][x];
					if (tile == null) {
						//tiles[y][x] = new Terrain(Skin.createStatic(Color.green, TILE));
					} else {
						//tiles[y][x] = null;
					}
					board.repaint();
				}
			}
		});

		Terrain[][] tiles = board.map.getTiles();
		try (BufferedReader mapfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("map.hgm")))) {
			String line;
			int y = 0, xmax = 0;
			while ((line = mapfile.readLine()) != null) {
				for (int x = 0; x < line.length(); x++) {
					Terrain tile;
					switch (line.charAt(x)) {
					case 'M':
						tile = new Terrain(Skin.createStatic(Color.gray, TILE));
						break;
					case 'h':
						tile = new Terrain(Skin.createStatic(Color.orange, TILE));
						break;
					case ',':
						tile = new Terrain(Skin.createStatic(Color.green, TILE));
						break;
					case 's':
						tile = new Terrain(Skin.createStatic(Color.yellow, TILE));
						break;
					case '~':
						tile = new Terrain(Skin.createStatic(Color.blue, TILE));
						break;
					default:
						tile = null;
					}
					tiles[y][x] = tile;
				}
				y++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		JFrame frame = new JFrame("Hero");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(board);
		frame.pack();
		frame.setVisible(true);
	}
}
