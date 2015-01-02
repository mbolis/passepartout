package it.mbolis.game2d;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputDispatcher implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private final Component dispatch;

	InputDispatcher(Component dispatch) {
		this.dispatch = dispatch;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		dispatch.dispatchEvent(e);
	}

}
