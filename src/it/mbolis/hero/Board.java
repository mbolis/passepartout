package it.mbolis.hero;

import it.mbolis.game2d.GraphicSurface;
import it.mbolis.game2d.Input;
import it.mbolis.game2d.SurfaceDrawer;
import it.mbolis.game2d.event.InputEvent;
import it.mbolis.game2d.event.Key;
import it.mbolis.game2d.event.Mouse;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

public class Board extends JFrame {

	private static final long serialVersionUID = 1L;

	private final GraphicSurface surface;
	private final Input input;

	private final Map map;

	private Rectangle offset;
	private Rectangle area;
	private int tileSize;

	public Board(Map map, int tilesWidth, int tilesHeight, int tileSize) {
		this.map = map;// new Map(width, height);

		Dimension size = new Dimension(tilesWidth * tileSize, tilesHeight * tileSize);
		setSize(size);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "Invisible");
		setCursor(invisibleCursor);

		surface = new GraphicSurface(this, size.width, size.height, 100);
		input = new Input(this);

		offset = new Rectangle(tilesWidth, tilesHeight);
		area = new Rectangle(size);
		this.tileSize = tileSize;
	}

	public void start() {
		surface.start(new SurfaceDrawer() {

			private int mousex, mousey;
			private boolean mouseDown;

			private Deque<Rectangle> toRedraw = new LinkedList<>();
			{
				toRedraw.add(area);
			}

			java.util.Map<InputEvent, Callable<Rectangle>> actions = new HashMap<>();
			{
				actions.put(new Key.Down(KeyEvent.VK_UP, 0), () -> {
					offset.setLocation(offset.x, Math.max(offset.y - 1, 0));
					return area;
				});
				actions.put(new Key.Down(KeyEvent.VK_DOWN, 0), () -> {
					offset.setLocation(offset.x, Math.min(offset.y + 1, map.width - offset.width));
					return area;
				});
				actions.put(new Key.Down(KeyEvent.VK_LEFT, 0), () -> {
					offset.setLocation(Math.max(offset.x - 1, 0), offset.y);
					return area;
				});
				actions.put(new Key.Down(KeyEvent.VK_RIGHT, 0), () -> {
					offset.setLocation(Math.min(offset.x + 1, map.height - offset.height), offset.y);
					return area;
				});
				actions.put(new Mouse.Left.Down(KeyEvent.BUTTON1_DOWN_MASK), () -> {
					mouseDown = true;
					return new Rectangle((mousex - 1) * tileSize, (mousey - 1) * tileSize, 3 * tileSize, 3 * tileSize);
				});
				actions.put(new Mouse.Left.Up(0), () -> {
					mouseDown = false;
					return new Rectangle((mousex - 1) * tileSize, (mousey - 1) * tileSize, 3 * tileSize, 3 * tileSize);
				});
				actions.put(new Mouse.Drag(KeyEvent.BUTTON1_DOWN_MASK), () -> {
					return new Rectangle((mousex - 1) * tileSize, (mousey - 1) * tileSize, 3 * tileSize, 3 * tileSize);
				});
			}

			@Override
			public void update() {
				List<InputEvent> events = input.getEvents();
				for (InputEvent e : events) {
					Callable<Rectangle> action = actions.get(e);
					if (action != null) {
						try {
							toRedraw.add(action.call());
						} catch (Exception exc) {
						}
					}
				}

				toRedraw.add(new Rectangle(mousex * tileSize, mousey * tileSize, tileSize, tileSize));

				Point position = input.getMousePointer().position;
				mousex = position.x / tileSize;
				mousey = position.y / tileSize;
			}

			@Override
			public void draw(Graphics2D g) {
				Terrain[][] tiles = map.getTiles();
				for (Iterator<Rectangle> iRedraw = toRedraw.iterator(); iRedraw.hasNext();) {
					Rectangle rect = iRedraw.next();
					iRedraw.remove();
					for (int y = rect.y / tileSize, ymax = y + rect.height / tileSize; y < ymax; y++) {
						for (int x = rect.x / tileSize, xmax = x + rect.width / tileSize; x < xmax; x++) {
							int yo = y + offset.y;
							if (yo < tiles.length) {
								Terrain[] row = tiles[yo];
								int xo = x + offset.x;
								if (xo < row.length) {
									Terrain tile = row[xo];
									if (tile != null) {
										Graphics gt = g.create(x * tileSize, y * tileSize, tileSize, tileSize);
										tile.getSkin().paint(gt);
									}
								}
							}
						}
					}
				}

				// paint mouse position
				g.setColor(Color.red);
				if (mouseDown) {
					g.fillRect((mousex - 1) * tileSize, mousey * tileSize, tileSize, tileSize);
					g.fillRect(mousex * tileSize, (mousey - 1) * tileSize, tileSize, tileSize);
					g.fillRect((mousex + 1) * tileSize, mousey * tileSize, tileSize, tileSize);
					g.fillRect(mousex * tileSize, (mousey + 1) * tileSize, tileSize, tileSize);
				} else {
					g.fillRect(mousex * tileSize, mousey * tileSize, tileSize, tileSize);
				}
			}
		});
	}

	public static void main(String[] args) {
		Map map = new Map(48, 48);
		final Board board = new Board(map, 32, 32, 24);

		Terrain[][] tiles = board.map.getTiles();
		try (BufferedReader mapfile = new BufferedReader(new InputStreamReader(
				ClassLoader.getSystemResourceAsStream("map.hgm")))) {
			String line;
			int y = 0;
			while ((line = mapfile.readLine()) != null) {
				for (int x = 0; x < line.length(); x++) {
					Terrain tile;
					switch (line.charAt(x)) {
					case 'M':
						tile = new Terrain(Skin.createStatic(Color.gray, 24));
						break;
					case 'h':
						tile = new Terrain(Skin.createStatic(Color.orange, 24));
						break;
					case ',':
						tile = new Terrain(Skin.createStatic(Color.green, 24));
						break;
					case 's':
						tile = new Terrain(Skin.createStatic(Color.yellow, 24));
						break;
					case '~':
						tile = new Terrain(Skin.createStatic(Color.blue, 24));
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

		board.setVisible(true);
		board.start();
	}
}
