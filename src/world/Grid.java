package world;

import heap.Heap;
import heap.HeapEmptyException;
import heap.HeapFullException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Grid {

	private final int DEFAULT_WIDTH = 60; // default width of the world map - gridX runs from 0 to 59
	private final int DEFAULT_HEIGHT = 15; // default height of the map - gridY runs from 0 to 14
	private final int DEFAULT_PERCENT = 20; // this is the percentage of the map occupied by islands
	protected int width, height; // user defined width and height, if one is not using defaults
	protected int percent; // user defined percentage of islands on the map
	protected Node treasure; // points to the map node where the Red-beard treasure is sunken
	protected Node boat; // points to the current location of our boat on the map

	protected Node[][] map; // the map

	public Grid() {
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		percent = DEFAULT_PERCENT;
		buildMap();
	}

	public Grid(int width, int height, int percent) {
		this.width = width;
		this.height = height;
		if (percent <= 0 || percent >= 100)
			this.percent = DEFAULT_PERCENT;
		else
			this.percent = percent;
		buildMap();
	}

	private void buildMap() {
		// Your implementation goes here
		// For each map position (i,j) you need to generate a Node with can be navigable or it may belong to an island
		// You may use ideas from Lab3 here.
		// Don't forget to generate the location of the boat and of the treasure; 
		// they must be on navigable waters, not on the land!		
		map = new Node[height][width];
		Random random = new Random();		
		// add walkable squares
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++){
				Node square = new Node(true, j, i);
				map[i][j] = square;				
			}
		}		
		// add in-walkable islands whose percent of the grid is specified
		boolean addIslands = false;
		int counter = 0;
		do {
			int y = random.nextInt(height);
			int x = random.nextInt(width);
			Node island = new Node(false, x, y);
			if (map[y][x].walkable) {
				map[y][x] = island;
				counter++;
			}
			if (counter == width * height * percent/100) {				
				addIslands = true;			
			}			
		} while (! addIslands);		
		// add boat and treasure
		boolean addMore = false;		
		do {
			int x1 = random.nextInt(width), x2 = random.nextInt(width);
			int y1 = random.nextInt(height), y2 = random.nextInt(height);
			this.boat = new Node(true, x1, y1);
			this.treasure = new Node(true, x2, y2);
			if (map[y1][x1].walkable && map[y2][x2].walkable) {
				map[y1][x1] = this.boat;
				map[y2][x2] = this.treasure;
				addMore = true;
			}
		} while (! addMore);		
	}

	public String drawMap() {
		// provided for your convenience
		String result = "";
		String hline = "       ";
		String extraSpace;
		for (int i = 0; i < width / 10; i++)
			hline += "         " + (i + 1);
		result += hline + "\n";
		hline = "       ";
		for (int i = 0; i < width; i++)
			hline += (i % 10);
		result += hline + "\n";
		for (int i = 0; i < height; i++) {
			if (i < 10)
				extraSpace = "      ";
			else
				extraSpace = "     ";
			hline = extraSpace + i;
			for (int j = 0; j < width; j++) {
				if (i == boat.gridY && j == boat.gridX)
					hline += "B";
				else if (i == treasure.gridY && j == treasure.gridX)
					hline += "T";
				else if (map[i][j].inPath)
					hline += "*";
				else if (map[i][j].walkable)
					hline += ".";
				else
					hline += "+";
			}
			result += hline + i + "\n";
		}
		hline = "       ";
		for (int i = 0; i < width; i++)
			hline += (i % 10);
		result += hline + "\n";
		return result;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPercent() {
		return percent;
	}
	
	public Node getBoat() {
		return boat;
	}
	
	private ArrayList<Node> getNeighbours(Node node) {
		// each node has at most 8 neighbors
		// Lab3 may be useful here as well
		ArrayList<Node> neighbours = new ArrayList<Node>();		
		int x = node.gridX;
		int y = node.gridY;	
		
		int[] xArray = {x-1, x, x+1};
		int[] yArray = {y-1, y, y+1};
		
		for (int i : yArray) {			
				for (int j : xArray){
					if (i >= 0 && i < height && j >= 0 && j < width) {
						neighbours.add(map[i][j]);
					}
				}
			}
		neighbours.remove(node);		
		return neighbours;		
	}

	//straight distance between two nodes that ignores the obstacles
	//in the middle
	private int getDistance(Node nodeA, Node nodeB) {
		// helper method. Provided for your convenience.
		int dstX = Math.abs(nodeA.gridX - nodeB.gridX);
		int dstY = Math.abs(nodeA.gridY - nodeB.gridY);
		if (dstX > dstY)
			return 14 * dstY + 10 * (dstX - dstY);
		return 14 * dstX + 10 * (dstY - dstX);
	}

	//implementing A* algorithm to search possible shortest path
	//note the algorithm doesn't actually find the shortest path but labels 
	//the visited node with minimum fCost = gCost + hCost.
	//All visited node are stored in a priority queue that's later used to trace the path
	//from target node back to starting node through parent-child link between two nodes.  
	public void findPath(Node startNode, Node targetNode)
			throws HeapFullException, HeapEmptyException {
		Heap<Node> openSet = new Heap<>(width * height); // this where we make use of our heaps		
		// The rest of your implementation goes here.
		// This method implements A-star path search algorithm.
		// The pseudo code is provided in the appropriate web links.
		// Make sure to use the helper method getNeighbours
		
		Set<Node> closeSet = new HashSet<>();//contain all nodes visited	
		startNode.gCost = 0;//real distance from starting node to current node 
		startNode.hCost = 0;//estimated distance from current node to target node
		openSet.add(startNode);//contain all nodes not visited 
		
		while (! openSet.isEmpty()){
			Node currentNode = openSet.removeFirst(); 
			closeSet.add(currentNode);
			
			if (currentNode.equals(targetNode))//reach the target node
				break;		
			
			ArrayList<Node> neighbourNodes = this.getNeighbours(currentNode);
			for (Node neighbourNode : neighbourNodes) {				
				// skip this node as defined below for early exit.
				if (! neighbourNode.walkable) 
					continue; 
				if (closeSet.contains(neighbourNode)) 
					continue; 							
				
				// add this new node never visited that's assigned gCost and hCost immediately				
				if (! openSet.contains(neighbourNode)) {
					//add parent-child relationship for later path-tracing					
					neighbourNode.parent = currentNode;
					//estimated distance because the final path may not go through the node
					neighbourNode.hCost = this.getDistance(neighbourNode, targetNode);
					//real distance  
					neighbourNode.gCost = currentNode.gCost + this.getDistance(currentNode, neighbourNode);
					//final fCost for the child node is undetermined because it might be updated later by another
					//parent node. So it's placed to openSet rather than closeSet
					openSet.add(neighbourNode);					
					continue;
				} 
				
				// this node is in the openSet because it was visited by another parent node.
				// so must check its gCost but remain hCost.  
				int tempG = currentNode.gCost + this.getDistance(currentNode, neighbourNode);
				if (tempG < neighbourNode.gCost) {							
					neighbourNode.parent = currentNode;
					neighbourNode.gCost = tempG;
					openSet.updateItem(neighbourNode);//update the openSet as this node's G-cost is re-computed. 							
				}	
			}
		}
	}

	//find the shortest path because all nodes are labeled the fCost by A* algorithm
	public ArrayList<Node> retracePath(Node startNode, Node endNode) {
		Node currentNode = endNode;
	    ArrayList<Node> path = new ArrayList<Node>();
		while (currentNode != startNode && currentNode != null) {
			currentNode.inPath = true;
			path.add(currentNode);
			currentNode = currentNode.parent;
		}
		return path;
	}

	public void move(String direction) {
		// Direction may be: N,S,W,E,NE,NW,SE,SW
		// move the boat 1 cell in the required direction
		int x = this.boat.gridX;
		int y = this.boat.gridY;		
		
		if (direction.equals("N") && y-1 >= 0 && this.map[y-1][x].walkable) {
			this.boat.gridY -= 1;			
			this.map[y-1][x] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("S") && y+1 < height && this.map[y+1][x].walkable) {
			this.boat.gridY += 1;
			this.map[y+1][x] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("W") && x-1 >= 0 && this.map[y][x-1].walkable ) {
			this.boat.gridX -= 1;
			this.map[y][x-1] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("E") && x+1 < width && this.map[y][x+1].walkable) {
			this.boat.gridX += 1;
			this.map[y][x+1] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("NE") && y-1 >= 0 && x+1 < width && this.map[y-1][x+1].walkable) {
			this.boat.gridX += 1;
			this.boat.gridY -= 1;
			this.map[y-1][x+1] = this.boat;	
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("NW") && y-1 >=0 && x-1 >= 0 && this.map[y-1][x-1].walkable) {
			this.boat.gridX -= 1;
			this.boat.gridY -= 1;
			this.map[y-1][x-1] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("SE") && y+1 < height && x+1 < width && this.map[y+1][x+1].walkable) {
			this.boat.gridX += 1;
			this.boat.gridY += 1;
			this.map[y+1][x+1] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}
		if (direction.equals("SW") && y+1 < height && x-1 > 0 && this.map[y+1][x-1].walkable) {
			this.boat.gridX -= 1;
			this.boat.gridY += 1;
			this.map[y+1][x-1] = this.boat;
			this.map[y][x] = new Node(true, x, y);
			}		
	}		
	
	public Node getTreasure(int range) {
		// range is the range of the sonar
		// if the distance of the treasure from the boat is less or equal that the sonar range,
		// return the treasure node. Otherwise return null.
		int distance = this.getDistance(this.boat, this.treasure);
		if (distance <= range) {
			return this.treasure;
		} else {
			return null;
		}		
	}
}
