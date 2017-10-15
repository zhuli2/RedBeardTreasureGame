package heap;

@SuppressWarnings("serial")

public class HeapEmptyException extends Exception {
	
	public HeapEmptyException() {
		super("Heap is empty!");
	}
	
	public HeapEmptyException(String message) {
		super(message);
	}

}