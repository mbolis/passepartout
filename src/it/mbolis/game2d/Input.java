package it.mbolis.game2d;

import static java.awt.MouseInfo.getPointerInfo;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isMiddleMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import it.mbolis.game2d.event.InputEvent;
import it.mbolis.game2d.event.Key;
import it.mbolis.game2d.event.Mouse;
import it.mbolis.game2d.event.Mouse.Custom;
import it.mbolis.game2d.event.Mouse.Left;
import it.mbolis.game2d.event.Mouse.Middle;
import it.mbolis.game2d.event.Mouse.Right;
import it.mbolis.game2d.event.Mouse.Wheel;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Input implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private List<InputEvent> events = new LinkedList<>();
	private Mouse.Pointer mousePointer;

	public Input(Window window) {

		Point mousePosition = getPointerInfo().getLocation();
		Rectangle windowBounds = window.getBounds();

		mousePosition.x = max(min(mousePosition.x - windowBounds.x, windowBounds.width), 0);
		mousePosition.y = max(min(mousePosition.y - windowBounds.y, windowBounds.height), 0);

		mousePointer = new Mouse.Pointer(mousePosition, 0);

		window.addMouseListener(this);
		window.addMouseMotionListener(this);
		window.addMouseWheelListener(this);
		window.addKeyListener(this);

		listenToComponents(window, new InputDispatcher(window));
	}

	private void listenToComponents(Container container, InputDispatcher dispatcher) {
		for (Component c : container.getComponents()) {
			c.addMouseListener(this);
			c.addMouseMotionListener(this);
			c.addMouseWheelListener(this);
			c.addKeyListener(this);

			if (c instanceof Container) {
				listenToComponents((Container) c, dispatcher);
			}
		}
	}

	public List<InputEvent> getEvents() {
		List<InputEvent> currentEvents;
		LinkedList<InputEvent> newEvents = new LinkedList<>();
		synchronized (events) {
			currentEvents = events;
			events = newEvents;
		}
		return currentEvents;
	}

	public synchronized Mouse.Pointer getMousePointer() {
		return mousePointer;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		e.consume();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		e.consume();
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_META:
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_ALT_GRAPH:
			return;
		default:
			synchronized (events) {
				events.add(new Key.Down(keyCode, e.getModifiersEx()));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		e.consume();
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_META:
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_ALT_GRAPH:
			return;
		default:
			synchronized (events) {
				events.add(new Key.Up(keyCode, e.getModifiersEx()));
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		e.consume();

		InputEvent event;
		if (isLeftMouseButton(e)) {
			event = new Left.Click(e.getClickCount(), e.getModifiersEx());
		} else if (isMiddleMouseButton(e)) {
			event = new Middle.Click(e.getClickCount(), e.getModifiersEx());
		} else if (isRightMouseButton(e)) {
			event = new Right.Click(e.getClickCount(), e.getModifiersEx());
		} else {
			event = new Custom.Click(e.getButton(), e.getClickCount(), e.getModifiersEx());
		}

		synchronized (events) {
			events.add(event);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		e.consume();

		InputEvent event;
		if (isLeftMouseButton(e)) {
			event = new Left.Down(e.getModifiersEx());
		} else if (isMiddleMouseButton(e)) {
			event = new Middle.Down(e.getModifiersEx());
		} else if (isRightMouseButton(e)) {
			event = new Right.Down(e.getModifiersEx());
		} else {
			event = new Custom.Down(e.getButton(), e.getModifiersEx());
		}

		synchronized (events) {
			events.add(event);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();

		InputEvent event;
		if (isLeftMouseButton(e)) {
			event = new Left.Up(e.getModifiersEx());
		} else if (isMiddleMouseButton(e)) {
			event = new Middle.Up(e.getModifiersEx());
		} else if (isRightMouseButton(e)) {
			event = new Right.Up(e.getModifiersEx());
		} else {
			event = new Custom.Up(e.getButton(), e.getModifiersEx());
		}

		synchronized (events) {
			events.add(event);
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
		if (rotation < 0) {
			synchronized (events) {
				events.add(new Wheel.Up(-rotation, e.getModifiersEx()));
			}
		} else {
			synchronized (events) {
				events.add(new Wheel.Down(rotation, e.getModifiersEx()));
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		e.consume();
		int modifiers = e.getModifiersEx();
		synchronized (events) {
			events.add(new Mouse.Drag(modifiers));
		}
		mousePointer = new Mouse.Pointer(e.getPoint(), modifiers);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		e.consume();
		mousePointer = new Mouse.Pointer(e.getPoint(), e.getModifiersEx());
	}

	public static void main(String[] args) throws AWTException, InterruptedException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new FlowLayout());
		panel.setSize(200, 200);
		frame.add(panel);

		frame.setSize(200, 200);
		frame.setVisible(true);

		final Input input = new Input(frame);

		Thread.sleep(100);

		Robot robot = new Robot();
		robot.mouseMove(frame.getLocation().x + 100, frame.getLocation().y + 100);
		robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);

		robot.keyPress(KeyEvent.VK_0);
		robot.keyRelease(KeyEvent.VK_0);

		for (int i = 0; i < 10; i++) {
			Thread.sleep(10);
			System.out.println(input.getEvents());
		}
	}
}
