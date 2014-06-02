package other;

public abstract class gObject {

	public enum ObjectType {
		NO_OBJECT((byte) 0), TREE((byte) 1), ROCK((byte) 2);
		
		private byte b;
		
		ObjectType(byte b) {
			this.b = b;
		}
		
		public byte getByteValue() {
			return b;
		}
	}
	
	public byte getType() {
		return ObjectType.NO_OBJECT.getByteValue();
	}
}
