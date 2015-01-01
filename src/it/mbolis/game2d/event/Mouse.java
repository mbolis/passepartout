package it.mbolis.game2d.event;

import java.awt.Point;

public abstract class Mouse extends InputEvent {

	public static final class Pointer {

		public final Point position;
		public final int modifiers;

		public Pointer(Point position, int modifiers) {
			this.position = position;
			this.modifiers = modifiers;
		}

		@Override
		public String toString() {
			return "Pointer[position=" + position + ", modifiers=" + modifiers + "]";
		}

	}

	public static abstract class Click extends Mouse {

		public final int clicks;

		public Click(int clicks, Point pos, int modifiers) {
			super(pos, modifiers);
			this.clicks = clicks;
		}

	}

	public static abstract class Left {

		public static class Click extends Mouse.Click {

			public Click(int clicks, Point pos, int modifiers) {
				super(clicks, pos, modifiers);
			}

			@Override
			public String toString() {
				return "Left.Click[clicks=" + clicks + ", x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}

		public static class Down extends Mouse {

			public Down(Point pos, int modifiers) {
				super(pos, modifiers);
			}

			@Override
			public String toString() {
				return "Left.Down[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Mouse {

			public Up(Point pos, int modifiers) {
				super(pos, modifiers);
			}

			@Override
			public String toString() {
				return "Left.Up[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}
	}

	public static abstract class Middle {

		public static class Click extends Mouse.Click {

			public Click(int clicks, Point pos, int modifiers) {
				super(clicks, pos, modifiers);
			}

			@Override
			public String toString() {
				return "Middle.Click[clicks=" + clicks + ", x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}
		}

		public static class Down extends Mouse {

			public Down(Point pos, int modifiers) {
				super(pos, modifiers);
			}

			@Override
			public String toString() {
				return "Middle.Down[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Mouse {

			public Up(Point pos, int modifiers) {
				super(pos, modifiers);
			}

			@Override
			public String toString() {
				return "Middle.Up[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}
	}

	public static abstract class Right {

		public static class Click extends Mouse.Click {

			public Click(int clicks, Point pos, int modifiers) {
				super(clicks, pos, modifiers);
			}

			@Override
			public String toString() {
				return "Right.Click[clicks=" + clicks + ", x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}
		}

		public static class Down extends Mouse {

			public Down(Point pos, int modifiers) {
				super(pos, modifiers);
			}

			@Override
			public String toString() {
				return "Right.Down[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Mouse {

			public Up(Point pos, int modifiers) {
				super(pos, modifiers);
			}

			@Override
			public String toString() {
				return "Right.Up[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}
	}

	public abstract static class Custom extends Mouse {

		public static class Click extends Mouse.Click {

			public final int button;

			public Click(int button, int clicks, Point pos, int modifiers) {
				super(clicks, pos, modifiers);
				this.button = button;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = super.hashCode();
				result = prime * result + button;
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!super.equals(obj))
					return false;
				if (getClass() != obj.getClass())
					return false;
				Custom.Click other = (Custom.Click) obj;
				if (button != other.button)
					return false;
				return true;
			}

			@Override
			public String toString() {
				return "Button<" + button + ">.Click[clicks=" + clicks + ", x=" + x + ", y=" + y + ", modifiers="
						+ modifiers + "]";
			}
		}

		public static class Down extends Custom {

			public Down(int button, Point pos, int modifiers) {
				super(button, pos, modifiers);
			}

			@Override
			public String toString() {
				return "Button<" + button + ">.Down[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Custom {

			public Up(int button, Point pos, int modifiers) {
				super(button, pos, modifiers);
			}

			@Override
			public String toString() {
				return "Button<" + button + ">.Up[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
			}

		}

		public final int button;

		public Custom(int button, Point pos, int modifiers) {
			super(pos, modifiers);
			this.button = button;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + button;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Custom other = (Custom) obj;
			if (button != other.button)
				return false;
			return true;
		}

	}

	public static class Drag extends Mouse {

		public Drag(Point pos, int modifiers) {
			super(pos, modifiers);
		}

		@Override
		public String toString() {
			return "Drag[x=" + x + ", y=" + y + ", modifiers=" + modifiers + "]";
		}

	}

	public static abstract class Wheel extends InputEvent {

		public static final class Up extends Wheel {

			public Up(int rotation, int modifiers) {
				super(rotation, modifiers);
			}

			@Override
			public String toString() {
				return "Wheel.Up[rotation=" + rotation + ", modifiers=" + modifiers + "]";
			}

		}

		public static final class Down extends Wheel {

			public Down(int rotation, int modifiers) {
				super(rotation, modifiers);
			}

			@Override
			public String toString() {
				return "Wheel.Down[rotation=" + rotation + ", modifiers=" + modifiers + "]";
			}

		}

		public final int rotation;
		public final int modifiers;

		public Wheel(int rotation, int modifiers) {
			this.rotation = rotation;
			this.modifiers = modifiers;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + modifiers;
			result = prime * result + rotation;
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
			Wheel other = (Wheel) obj;
			if (modifiers != other.modifiers)
				return false;
			if (rotation != other.rotation)
				return false;
			return true;
		}

	}

	public final int x, y;
	public final int modifiers;

	public Mouse(Point pos, int modifiers) {
		this.x = pos.x;
		this.y = pos.y;
		this.modifiers = modifiers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + modifiers;
		result = prime * result + x;
		result = prime * result + y;
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
		Mouse other = (Mouse) obj;
		if (modifiers != other.modifiers)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
