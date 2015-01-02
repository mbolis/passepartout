package it.mbolis.game2d;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

public class GraphicSurface extends Canvas {

	private static final long serialVersionUID = 1L;

	private static final int FPS = 30;

	private static Toolkit toolkit = Toolkit.getDefaultToolkit();
	private static GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDefaultConfiguration();

	private BufferStrategy strategy;
	private int fpsPeriod;
	private Thread loop;

	/**
	 * Creates a {@code GraphicSurface} with given parent and size.
	 * 
	 * Note the surface will be added to the parent and this will be made visible. If the parent is a {@link Window}, a
	 * {@link WindowListener} will be added that will stop the rendering loop on <i>window closing</i>.
	 * 
	 */
	public GraphicSurface(Container parent, int width, int height) {
		this(parent, width, height, FPS);
	}

	/**
	 * Creates a {@code GraphicSurface} with given parent, size and FPS.
	 * 
	 * Note the surface will be added to the parent and this will be made visible. If the parent is a {@link Window}, a
	 * {@link WindowListener} will be added that will stop the rendering loop on <i>window closing</i>.
	 * 
	 */
	public GraphicSurface(Container parent, int width, int height, int fps) {
		super(config);

		setIgnoreRepaint(true);
		setSize(width, height);
		parent.add(this);
		if (parent instanceof Window) {
			Window window = (Window) parent;
			window.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					stop();
				};
			});
		}
		parent.setVisible(true);

		createBufferStrategy(2);
		do {
			strategy = getBufferStrategy();
		} while (strategy == null);

		this.fpsPeriod = (int) (1.0 / fps * 1000);
	}

	private class RenderLoop extends Thread {

		final SurfaceDrawer sd;

		RenderLoop(SurfaceDrawer sd) {
			this.sd = sd;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				long frameStart = System.currentTimeMillis();

				sd.update();

				do {
					Graphics2D buffer;
					try {
						buffer = (Graphics2D) strategy.getDrawGraphics();
						sd.draw(buffer);
						buffer.dispose();

						strategy.show();
						toolkit.sync();

					} catch (NullPointerException | IllegalStateException e) {
						break;
					}
				} while (strategy.contentsLost());

				long frameEnd = System.currentTimeMillis();

				long frameTime = frameEnd - frameStart;
				long frameSleep = fpsPeriod - frameTime;
				if (frameSleep > 0) {
					try {
						Thread.sleep(frameSleep);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
	}

	public synchronized void start(final SurfaceDrawer sd) {
		if (loop == null || loop.isInterrupted()) {
			loop = new RenderLoop(sd);
			loop.start();
		}
	}

	public synchronized void stop() {
		loop.interrupt();
		loop = null;
	}

}
