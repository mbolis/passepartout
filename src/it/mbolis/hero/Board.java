package it.mbolis.hero;

import static java.lang.Integer.bitCount;
import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import it.mbolis.game2d.GraphicSurface;
import it.mbolis.game2d.input.Input;
import it.mbolis.game2d.input.Input.InputState;
import it.mbolis.game2d.SurfaceDrawer;

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
import java.util.SortedMap;
import java.util.TreeMap;
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

	public static class KeyCombination implements Comparable<KeyCombination> {

		public final int keyCode;
		public final int modifiers;

		public KeyCombination(int keyCode, int modifiers) {
			this.keyCode = keyCode;
			this.modifiers = modifiers;
		}

		@Override
		public int compareTo(KeyCombination that) {
			int codeCompare = this.keyCode - that.keyCode;
			return codeCompare != 0 ? codeCompare : bitCount(this.modifiers) - bitCount(that.modifiers);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + keyCode;
			result = prime * result + modifiers;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KeyCombination other = (KeyCombination) obj;
			if (keyCode != other.keyCode)
				return false;
			if (modifiers != other.modifiers)
				return false;
			return true;
		}

	}

	public static class MouseCombination implements Comparable<MouseCombination> {

		public final int buttons;
		public final int modifiers;

		public MouseCombination(int buttons, int modifiers) {
			this.buttons = buttons;
			this.modifiers = modifiers;
		}

		@Override
		public int compareTo(MouseCombination that) {
			int btnCompare = this.buttons - that.buttons;
			return btnCompare != 0 ? btnCompare : bitCount(this.modifiers) - bitCount(that.modifiers);
		}
	}

	public void start() {
		surface.start(new SurfaceDrawer() {

			private int mousex, mousey;
			private boolean mouseDown;

			private Deque<Rectangle> toRedraw = new LinkedList<>();
			{
				toRedraw.add(area);
			}

			java.util.Map<Integer, SortedMap<KeyCombination, Integer>> keyCombinations = new HashMap<>();
			java.util.Map<Integer, SortedMap<MouseCombination, Integer>> mouseCombinations = new HashMap<>();
			java.util.Map<Integer, Callable<Rectangle>> actions = new HashMap<>();
			{
				SortedMap<KeyCombination, Integer> combinationSet = new TreeMap<>();
				combinationSet.put(new KeyCombination(KeyEvent.VK_UP, 0), 0x01);
				keyCombinations.put(KeyEvent.VK_UP, combinationSet);
				actions.put(0x01, () -> {
					offset.setLocation(offset.x, Math.max(offset.y - 1, 0));
					return area;
				});

				combinationSet = new TreeMap<>();
				combinationSet.put(new KeyCombination(KeyEvent.VK_DOWN, 0), 0x02);
				keyCombinations.put(KeyEvent.VK_DOWN, combinationSet);
				actions.put(0x02, () -> {
					offset.setLocation(offset.x, Math.min(offset.y + 1, map.width - offset.width));
					return area;
				});

				combinationSet = new TreeMap<>();
				combinationSet.put(new KeyCombination(KeyEvent.VK_LEFT, 0), 0x03);
				keyCombinations.put(KeyEvent.VK_LEFT, combinationSet);
				actions.put(0x03, () -> {
					offset.setLocation(Math.max(offset.x - 1, 0), offset.y);
					return area;
				});

				combinationSet = new TreeMap<>();
				combinationSet.put(new KeyCombination(KeyEvent.VK_RIGHT, 0), 0x04);
				keyCombinations.put(KeyEvent.VK_RIGHT, combinationSet);
				actions.put(0x04, () -> {
					offset.setLocation(min(offset.x + 1, map.height - offset.height), offset.y);
					return area;
				});

				SortedMap<MouseCombination, Integer> mouseSet = new TreeMap<>();
				mouseSet.put(new MouseCombination(Input.Mouse.LEFT, 0), 0x5);
				mouseCombinations.put(Input.Mouse.LEFT, mouseSet);
				actions.put(0x5, () -> {
					mouseDown = true;
					return new Rectangle((mousex - 1) * tileSize, (mousey - 1) * tileSize, 3 * tileSize, 3 * tileSize);
				});

				// actions.put(new Mouse.Left.Up(0), () -> {
				// mouseDown = false;
				// return new Rectangle((mousex - 1) * tileSize, (mousey - 1) * tileSize, 3 * tileSize, 3 * tileSize);
				// });
				// actions.put(new Mouse.Drag(KeyEvent.BUTTON1_DOWN_MASK), () -> {
				// return new Rectangle((mousex - 1) * tileSize, (mousey - 1) * tileSize, 3 * tileSize, 3 * tileSize);
				// });
			}

			@Override
			public void update() {

				InputState state = input.getState();

				int modifiers = state.keyboard.modifiers;

				for (Integer k : state.keyboard.keyPresses) {
					SortedMap<KeyCombination, Integer> kcs = keyCombinations.get(k);
					for (KeyCombination kc : kcs.keySet()) {
						if ((kc.modifiers & modifiers) == modifiers) {
							Integer actionCode = kcs.get(kc);
							Callable<Rectangle> action = actions.get(actionCode);
							if (action != null) {
								try {
									toRedraw.add(action.call());
								} catch (Exception exc) {
								}
							}
							break;
						}
					}
				}

				for (int b = 0; b < 32 - numberOfLeadingZeros(state.mouse.buttons); b++) {
					int btn = (int) pow(2, b);
					if ((state.mouse.buttons & btn) > 0) {
						SortedMap<MouseCombination, Integer> mcs = mouseCombinations.get(btn);
						for (MouseCombination mc : mcs.keySet()) {
							if ((mc.modifiers & modifiers) == modifiers) {
								Integer actionCode = mcs.get(mc);
								Callable<Rectangle> action = actions.get(actionCode);
								if (action != null) {
									try {
										toRedraw.add(action.call());
									} catch (Exception exc) {
									}
								}
								break;
							}
						}
					}

				}

				toRedraw.add(new Rectangle(mousex * tileSize, mousey * tileSize, tileSize, tileSize));

				Point position = state.mouse.position;
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
