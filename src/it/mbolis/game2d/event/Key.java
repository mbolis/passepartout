package it.mbolis.game2d.event;

public abstract class Key extends InputEvent {

	public static class Down extends Key {

		public Down(int keyCode, int modifiers) {
			super(keyCode, modifiers);
		}

		@Override
		public String toString() {
			return "Key.Down[keyCode=" + keyCode + ", modifiers=" + modifiers + "]";
		}
	}

	public static class Up extends Key {

		public Up(int keyCode, int modifiers) {
			super(keyCode, modifiers);
		}

		@Override
		public String toString() {
			return "Key.Up[keyCode=" + keyCode + ", modifiers=" + modifiers + "]";
		}
	}

	public final int keyCode;
	public final int modifiers;

	public Key(int keyCode, int modifiers) {
		this.keyCode = keyCode;
		this.modifiers = modifiers;
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
		Key other = (Key) obj;
		if (keyCode != other.keyCode)
			return false;
		if (modifiers != other.modifiers)
			return false;
		return true;
	}

}
