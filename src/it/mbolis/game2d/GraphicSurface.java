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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GraphicSurface extends Canvas {

	private static final long serialVersionUID = 1L;

	private static final int FPS = 30;

	private static Toolkit toolkit = Toolkit.getDefaultToolkit();
	private static GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDefaultConfiguration();

	private int fpsPeriod;

	private BufferStrategy strategy;

	private ScheduledExecutorService scheduler;

	/**
	 * Creates a {@code GraphicSurface} with given parent and size.
	 * 
	 * Note the surface will be added to the parent and this will be made visible. If the parent is a {@link Window}, it
	 * will also be packed and a {@link WindowListener} will be added that will stop the rendering loop on <i>window
	 * closing</i>.
	 * 
	 */
	public GraphicSurface(Container parent, int width, int height) {
		this(parent, width, height, FPS);
	}

	/**
	 * Creates a {@code GraphicSurface} with given parent, size and FPS.
	 * 
	 * Note the surface will be added to the parent and this will be made visible. If the parent is a {@link Window}, it
	 * will also be packed and a {@link WindowListener} will be added that will stop the rendering loop on <i>window
	 * closing</i>.
	 * 
	 */
	public GraphicSurface(Container parent, int width, int height, int fps) {
		super(config);

		setIgnoreRepaint(true);
		setSize(width, height);
		parent.add(this);
		if (parent instanceof Window) {
			Window window = (Window) parent;
			window.pack();
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

	public synchronized void start(final SurfaceDrawer sd) {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newScheduledThreadPool(1);

			Runnable command = new Runnable() {

				@Override
				public void run() {
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
				}
			};

			scheduler.scheduleAtFixedRate(command, 0, fpsPeriod, TimeUnit.MILLISECONDS);
		}
	}

	public synchronized void stop() {
		scheduler.shutdownNow();
		scheduler = null;
	}

}
