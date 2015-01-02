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

	private static abstract class Click extends Mouse {

		public final int clicks;

		public Click(int clicks, int modifiers) {
			super(modifiers);
			this.clicks = clicks;
		}

	}

	public static abstract class Left {

		public static class Click extends Mouse.Click {

			public Click(int clicks, int modifiers) {
				super(clicks, modifiers);
			}

			@Override
			public String toString() {
				return "Left.Click[clicks=" + clicks + ", modifiers=" + modifiers + "]";
			}

		}

		public static class Down extends Mouse {

			public Down(int modifiers) {
				super(modifiers);
			}

			@Override
			public String toString() {
				return "Left.Down[modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Mouse {

			public Up(int modifiers) {
				super(modifiers);
			}

			@Override
			public String toString() {
				return "Left.Up[modifiers=" + modifiers + "]";
			}

		}
	}

	public static abstract class Middle {

		public static class Click extends Mouse.Click {

			public Click(int clicks, int modifiers) {
				super(clicks, modifiers);
			}

			@Override
			public String toString() {
				return "Middle.Click[clicks=" + clicks + ", modifiers=" + modifiers + "]";
			}
		}

		public static class Down extends Mouse {

			public Down(int modifiers) {
				super(modifiers);
			}

			@Override
			public String toString() {
				return "Middle.Down[modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Mouse {

			public Up(int modifiers) {
				super(modifiers);
			}

			@Override
			public String toString() {
				return "Middle.Up[modifiers=" + modifiers + "]";
			}

		}
	}

	public static abstract class Right {

		public static class Click extends Mouse.Click {

			public Click(int clicks, int modifiers) {
				super(clicks, modifiers);
			}

			@Override
			public String toString() {
				return "Right.Click[clicks=" + clicks + ", modifiers=" + modifiers + "]";
			}
		}

		public static class Down extends Mouse {

			public Down(int modifiers) {
				super(modifiers);
			}

			@Override
			public String toString() {
				return "Right.Down[modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Mouse {

			public Up(int modifiers) {
				super(modifiers);
			}

			@Override
			public String toString() {
				return "Right.Up[modifiers=" + modifiers + "]";
			}

		}
	}

	public abstract static class Custom extends Mouse {

		public static class Click extends Mouse.Click {

			public final int button;

			public Click(int button, int clicks, int modifiers) {
				super(clicks, modifiers);
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
				return "Button<" + button + ">.Click[clicks=" + clicks + ", modifiers=" + modifiers + "]";
			}
		}

		public static class Down extends Custom {

			public Down(int button, int modifiers) {
				super(button, modifiers);
			}

			@Override
			public String toString() {
				return "Button<" + button + ">.Down[modifiers=" + modifiers + "]";
			}

		}

		public static class Up extends Custom {

			public Up(int button, int modifiers) {
				super(button, modifiers);
			}

			@Override
			public String toString() {
				return "Button<" + button + ">.Up[modifiers=" + modifiers + "]";
			}

		}

		public final int button;

		public Custom(int button, int modifiers) {
			super(modifiers);
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

		public Drag(int modifiers) {
			super(modifiers);
		}

		@Override
		public String toString() {
			return "Drag[modifiers=" + modifiers + "]";
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

	public final int modifiers;

	public Mouse(int modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Mouse other = (Mouse) obj;
		if (modifiers != other.modifiers)
			return false;
		return true;
	}

}
