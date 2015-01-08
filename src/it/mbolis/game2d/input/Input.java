package it.mbolis.game2d.input;

import static java.awt.MouseInfo.getPointerInfo;
import static java.awt.event.KeyEvent.getKeyText;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Input implements MouseListener, MouseMotionListener, MouseWheelListener, KeyEventDispatcher {

	public static class Keyboard {

		public static final int SHIFT = 1 << KeyEvent.VK_SHIFT - 1;
		public static final int CTRL = 1 << KeyEvent.VK_CONTROL - 1;
		public static final int ALT = 1 << KeyEvent.VK_ALT - 1;

		public int modifiers;
		public Set<Integer> keys = new HashSet<>();
		public List<Integer> keyPresses = new ArrayList<>();

		private Keyboard() {
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if ((modifiers & SHIFT) > 0) {
				sb.append("Shift");
			}
			if ((modifiers & CTRL) > 0) {
				if (sb.length() > 0) {
					sb.append("+");
				}
				sb.append("Ctrl");
			}
			if ((modifiers & ALT) > 0) {
				if (sb.length() > 0) {
					sb.append("+");
				}
				sb.append("Alt");
			}
			for (int k : keys) {
				if (sb.length() > 0) {
					sb.append("+");
				}
				sb.append(getKeyText(k));
				if (keyPresses.contains(k)) {
					sb.append('*');
				}
			}
			return sb.append('\n').toString();
		}
	}

	public static class Mouse {

		public static final int LEFT = 0x1;
		public static final int MIDDLE = 0x2;
		public static final int RIGHT = 0x4;

		public Point position;
		public int buttons;
		public List<Integer> buttonPresses = new ArrayList<>();
		public List<Gesture> gestures = new ArrayList<>();

		private Mouse() {
		}

		@Override
		public String toString() {
			return String.format("[%s%s%s] (%d,%d) %n%s%n", (buttons & LEFT) > 0 ? 'x' : ' ',
					(buttons & MIDDLE) > 0 ? 'x' : ' ', (buttons & RIGHT) > 0 ? 'x' : ' ', position.x, position.y,
					gestures);
		}
	}

	public static class InputState {

		public final Keyboard keyboard = new Keyboard();
		public final Mouse mouse = new Mouse();

		private InputState() {
		}

		public InputState copy() {
			InputState newState = new InputState();

			Keyboard newKeyboard = newState.keyboard;
			ArrayList<Integer> newKeyPresses = new ArrayList<>();
			synchronized (keyboard) {
				newKeyboard.modifiers = keyboard.modifiers;
				newKeyboard.keys = new HashSet<>(keyboard.keys);
				newKeyboard.keyPresses = keyboard.keyPresses;
				keyboard.keyPresses = newKeyPresses;
			}

			Mouse newMouse = newState.mouse;
			ArrayList<Integer> newButtonPresses = new ArrayList<>();
			ArrayList<Gesture> newGestures = new ArrayList<>();
			synchronized (mouse) {
				newMouse.position = new Point(mouse.position);
				newMouse.buttons = mouse.buttons;
				newMouse.buttonPresses = mouse.buttonPresses;
				mouse.buttonPresses = newButtonPresses;
				newMouse.gestures = mouse.gestures;
				mouse.gestures = newGestures;
			}

			return newState;
		}

		@Override
		public String toString() {
			return keyboard + "\n" + mouse;
		}
	}

	private InputState state = new InputState();

	private final Set<Integer> modifierKeys = new HashSet<>();

	public Input(Component component) {

		Point mousePosition = getPointerInfo().getLocation();
		Point location = component.getLocationOnScreen();
		Rectangle bounds = component.getBounds();
		bounds.translate(location.x, location.y);

		mousePosition.x = max(min(mousePosition.x - bounds.x, bounds.width), 0);
		mousePosition.y = max(min(mousePosition.y - bounds.y, bounds.height), 0);

		state.mouse.position = mousePosition;

		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);

		if (component instanceof Container) {
			listenToComponents((Container) component, new InputDispatcher(component));
		}
	}

	private void listenToComponents(Container container, InputDispatcher dispatcher) {
		for (Component c : container.getComponents()) {
			c.addMouseListener(this);
			c.addMouseMotionListener(this);
			c.addMouseWheelListener(this);

			if (c instanceof Container) {
				listenToComponents((Container) c, dispatcher);
			}
		}
	}

	public InputState getState() {
		return state.copy();
	}

	public void setModifierKeys(int... modifiers) {
		modifierKeys.clear();
		for (Integer m : modifiers) {
			modifierKeys.add(m);
		}

		synchronized (state.keyboard) {
			Set<Integer> newKeys = new HashSet<>();
			for (int key : state.keyboard.keys) {
				if (modifierKeys.contains(key)) {
					state.keyboard.modifiers |= 1 << key - 1;
				} else {
					newKeys.add(key);
				}
			}
			state.keyboard.keys = newKeys;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		e.consume();

		int keyCode;
		switch (e.getID()) {
		case KeyEvent.KEY_PRESSED:
			keyCode = e.getKeyCode();
			if (modifierKeys.contains(keyCode)) {
				int mod = 1 << keyCode - 1;
				synchronized (state.keyboard) {
					state.keyboard.modifiers |= mod;
				}
			} else {
				synchronized (state.keyboard) {
					state.keyboard.keys.add(keyCode);
					state.keyboard.keyPresses.add(keyCode);
				}
			}
			break;
		case KeyEvent.KEY_RELEASED:
			keyCode = e.getKeyCode();
			if (modifierKeys.contains(keyCode)) {
				int mod = 1 << keyCode - 1;
				synchronized (state.keyboard) {
					state.keyboard.modifiers ^= mod;
				}
			} else {
				synchronized (state.keyboard) {
					state.keyboard.keys.remove(keyCode);
				}
			}
			break;
		default:
		}

		return true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		e.consume();
		int button = 1 << e.getButton() - 1;
		int clicks = e.getClickCount();
		Gesture.Click gesture = new Gesture.Click(button, clicks);
		synchronized (state.mouse) {
			state.mouse.gestures.add(gesture);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		e.consume();
		int button = 1 << e.getButton() - 1;
		synchronized (state.mouse) {
			state.mouse.buttonPresses.add(button);
			state.mouse.buttons |= button;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();
		int button = 1 << e.getButton() - 1;
		synchronized (state.mouse) {
			state.mouse.buttons ^= button;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		e.consume();
		int rotation = e.getWheelRotation();
		Gesture.Wheel gesture = new Gesture.Wheel(rotation);
		synchronized (state.mouse) {
			state.mouse.gestures.add(gesture);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		e.consume();
		Point newLocation = e.getPoint();
		Gesture.Drag gesture = new Gesture.Drag(state.mouse.position, newLocation);
		synchronized (state.mouse) {
			state.mouse.gestures.add(gesture);
			state.mouse.position = newLocation;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		e.consume();
		Point position = e.getPoint();
		synchronized (state.mouse) {
			state.mouse.position = position;
		}
	}

	public static void main(String[] args) throws AWTException, InterruptedException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		class InfoPanel extends JPanel {

			private static final long serialVersionUID = 1L;

			InputState state;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(200, 200);
			}

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (state != null) {
					String[] stateLines = state.toString().split("\n");
					for (int i = 0; i < stateLines.length; i++) {
						g.drawString(stateLines[i], 5, 16 + 16 * i);
					}
				}
			}
		}

		InfoPanel panel = new InfoPanel();
		frame.add(panel);

		frame.pack();
		frame.setVisible(true);

		final Input input = new Input(panel);
		input.setModifierKeys(Keyboard.SHIFT, Keyboard.CTRL, Keyboard.ALT);

		new Thread() {

			@Override
			public void run() {
				while (!isInterrupted()) {

					panel.state = input.getState();
					panel.repaint();

					try {
						sleep(500);
					} catch (InterruptedException e) {
						break;
					}
				}
			};

		}.start();
	}

}
