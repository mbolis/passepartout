package it.mbolis.hero;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Board extends JPanel {

	private final Dimension size = new Dimension(400, 400);

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

	public static void main(String[] args) {
		JFrame frame = new JFrame("Hero");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Board());
		frame.pack();
		frame.setVisible(true);
	}
}
