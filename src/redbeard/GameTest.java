package redbeard;

import heap.HeapEmptyException;
import heap.HeapFullException;

import java.io.FileNotFoundException;

// This class is not part of your evaluation. You may use it for testing if you want.
public class GameTest {

	/**
	 * @param args
	 * @throws HeapEmptyException 
	 * @throws HeapFullException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, HeapFullException, HeapEmptyException {
		// TODO Auto-generated method stub
		TreasureHunt game = new TreasureHunt();
		System.out.println(game.getMap());		
        game.play("/game.txt");
        System.out.println(game.getMap());
        System.out.println(game.state);
        System.out.println(game.pathLength());      
	}

}
