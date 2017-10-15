package redbeard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import heap.HeapEmptyException;
import heap.HeapFullException;
import world.Grid;
import world.Node;

public class TreasureHunt {

	private final int DEFAULT_SONARS = 3; // default number of available sonars
	private final int DEFAULT_RANGE = 200; // default range of a sonar
	protected Grid islands; // the world where the action happens!
	protected int height, width, landPercent;
	protected int sonars, range; // user defined number of sonars and range
	protected String state; // state of the game (STARTED, OVER)
	protected ArrayList<Node> path; // the path to the treasure!

	public TreasureHunt() {
		// The default constructor
		this.islands = new Grid();
		this.sonars = DEFAULT_SONARS;
		this.range = DEFAULT_RANGE;
	}

	public TreasureHunt(int height, int width, int landPercent, int sonars,
			int range) {
		// The constructor that uses parameters
		this.height = height;
		this.width = width;
		this.landPercent = landPercent;
		this.islands = new Grid(height, width, landPercent);
		this.sonars = sonars;
		this.range = range;
	}
	
	private void processCommand(String command) throws HeapFullException,
			HeapEmptyException {
		// The allowed commands are: 
		// SONAR to drop the sonar in hope to detect treasure
		// GO direction to move the boat in some direction
		// For example, GO NW means move the boat one cell up left (if the cell is navigable; 
		// if not simply ignore the command) 
		String[] commands = command.split(" ");		
		if (commands[0].trim().contains("SONAR") && this.sonars > 0) {					
				this.sonars--;
				Node treasure = this.islands.getTreasure(this.range);
				if (treasure != null) {						
						islands.findPath(islands.getBoat(), treasure);
						this.path = islands.retracePath(islands.getBoat(), treasure);				
				}
		}		
		else if (commands[0].trim().contains("GO")) {				
				islands.move(commands[1].trim());			
		}		
	}

	public int pathLength() {
		if (path == null)
			return 0;
		else return path.size();
	}

	public String getMap() {
		return islands.drawMap();
	}

	public void play(String pathName) throws FileNotFoundException,
			HeapFullException, HeapEmptyException {
		// Read a batch of commands from a text file and process them.
		Scanner inStream = null;
		try { 
			inStream =  new Scanner(new File(pathName));
			while (inStream.hasNextLine()) {
				String command = inStream.nextLine();
				if (command.length() > 0) {
					this.state = "STARTED";
					this.processCommand(command.toUpperCase());					
					if (this.pathLength() > 0 || this.sonars == 0) 
						break;
					}
			}
		} catch (FileNotFoundException e) {			
			System.err.println(e.getLocalizedMessage());
		} catch (HeapFullException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (HeapEmptyException e) {
			System.err.println(e.getLocalizedMessage());
		} finally {
			this.state = "OVER";
			if (inStream != null) {inStream.close();}
		}
	}
}
