package it.mbolis.game2d.input;

import java.awt.Point;

public abstract class Gesture {

	public static class Click extends Gesture {

		public final int button;
		public final int count;

		public Click(int button, int count) {
			this.button = button;
			this.count = count;
		}

		@Override
		public String toString() {
			return "Click[" + ((button & Input.Mouse.LEFT) > 0 ? 'x' : ' ')
					+ ((button & Input.Mouse.MIDDLE) > 0 ? 'x' : ' ') + ((button & Input.Mouse.RIGHT) > 0 ? 'x' : ' ')
					+ "](" + count + ")";
		}
	}

	public static class Drag extends Gesture {

		public final Point from, to;

		public Drag(Point from, Point to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String toString() {
			return "Drag(" + from.x + "," + from.y + ")->(" + to.x + "," + to.y + ")";
		}
	}

	public static class Drop extends Gesture {

		public final int button;
		public final Point position;

		public Drop(int button, Point position) {
			this.button = button;
			this.position = position;
		}

	}

	public static class Wheel extends Gesture {

		public final int amount;

		public Wheel(int amount) {
			this.amount = amount;
		}

		@Override
		public String toString() {
			return "Wheel(" + amount + ")";
		}
	}

}