package heap;

import java.util.Arrays;

public class Heap<T extends HeapItem> {
	
	// Note the T is a parameter representing a type that extends the HeapItem interface
	// This a new way to use inheritance!
	protected T[] items; // Array that is used to store heap items. items[0] is the highest priority element.
	protected int maxHeapSize; // The capacity of the heap
	protected int currentItemCount; // How many elements we have currently on the heap

	@SuppressWarnings("unchecked")	
	public Heap(int maxHeapSize) {
		this.maxHeapSize = maxHeapSize;
		items = (T[]) new HeapItem[maxHeapSize];
		currentItemCount = 0; // heap is empty!
	}

	public boolean isEmpty() {
		return currentItemCount == 0;
	}

	public boolean isFull() {
		return currentItemCount == maxHeapSize;
	}

	
	//insert new item to the end of items 
	//and then sort it up to right place
	public void add(T item) throws HeapFullException
	// Adds item T to its correct position on the heap
	{
		if (isFull())
			throw new HeapFullException();
		else {
			item.setHeapIndex(currentItemCount); 
			items[currentItemCount] = item;  // the element is added to the bottom
			sortUp(item); // Move the element up to its legitimate place. Check the diagram on the hand-out!
			currentItemCount++;
		}
	}

	public boolean contains(T item)
	// Returns true if item is on the heap
	// Otherwise returns false
	{
		return items[item.getHeapIndex()].equals(item);
	}

	public int count() {
		return currentItemCount;
	}
	
	public void updateItem(T item) {
		sortUp(item);
	}

	//remove the top item with highest priority;
	//place the last item in the items to the top and then sort it 
	//down to the right place. 
	public T removeFirst() throws HeapEmptyException
	// Removes and returns the element sitting on top of the heap
	{
		if (isEmpty())
			throw new HeapEmptyException();
		else {
			T firstItem = items[0]; // element of top of the heap is stored in firstItem variable
			currentItemCount--;
			items[0] = items[currentItemCount]; //last element moves on top
			items[0].setHeapIndex(0);
			sortDown(items[0]); // move the element to its legitimate position. Please check the diagram on the hand-out.
			return firstItem;
		}
	}
	
	//compare child item and its parent item and swap them if applicable
	private void sortUp(T item) {
		// Implement this method according to the diagram on the hand-out.
		// Also: the indices of children and parent elements satisfy some relationships.
		// The formulas are on the hand-out.		
		int childIndex = item.getHeapIndex(); 	
		int parentIndex = (childIndex - 1) / 2;
		
		while (parentIndex >= 0 && parentIndex != childIndex) {
			T child = this.items[childIndex];
			T parent = this.items[parentIndex];
			
			if (child.compareTo(parent) <= 0) {
				this.items[parentIndex] = child;
				this.items[childIndex] = parent;
				
				childIndex = parentIndex;
				parentIndex = (childIndex - 1) / 2;			
			} else 
				break;			
		}	
	}
	
	//compare parent item and child items and swap them if applicable
	private void sortDown(T item) {
		// Implement this method according to the diagram on the hand-out.
		// Also: the indices of children and parent elements satisfy some relationships.
		// The formulas are on the hand-out.
		int parentIndex = item.getHeapIndex();		
		int leftChildIndex = parentIndex * 2 + 1;		
		int rightChildIndex = parentIndex * 2 + 2;
		
		//parent has both left and right child.
		while (rightChildIndex < this.count()) {
			
				T parent = this.items[parentIndex];
				T leftChild = this.items[leftChildIndex];
				T rightChild = this.items[rightChildIndex];
				
				HeapItem[] sortedNodes = {parent, leftChild, rightChild};
				Arrays.sort(sortedNodes); 
								
				if (sortedNodes[0].equals(parent)) //parent is the smallest					
					break;
				else if (sortedNodes[0].equals(rightChild)) { //rightChild is the smallest.					
					this.items[parentIndex] = rightChild;
					this.items[rightChildIndex] = parent;
					
					parentIndex = rightChildIndex;	
					leftChildIndex = parentIndex * 2 + 1;
					rightChildIndex = parentIndex * 2 + 2;					
				}
				else if (sortedNodes[0].equals(leftChild)){ //leftChild is the smallest.					
					this.items[parentIndex] = leftChild;
					this.items[leftChildIndex] = parent;
					
					parentIndex = leftChildIndex;
					leftChildIndex = parentIndex * 2 + 1;
					rightChildIndex = parentIndex * 2 + 2;
				}
			}
		
		//since the heap is complete binary tree, the codes below are unnecessary 
		//parent has only left child.
		while (leftChildIndex < this.count()) {
			
			T parent = this.items[parentIndex];
			T leftChild = this.items[leftChildIndex];
			
			if (parent.compareTo(leftChild) >= 0) {
				this.items[parentIndex] = leftChild;
				this.items[leftChildIndex] = parent;
				
				parentIndex = leftChildIndex;
				leftChildIndex = parentIndex * 2 + 1;								
			} else 
				break;			
		}			
	}	
	
	// You may implement additional helper methods if desired. Make sure to make them private!
}
