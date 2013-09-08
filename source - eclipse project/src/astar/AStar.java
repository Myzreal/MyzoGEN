package astar;

import java.util.ArrayList;

import main.MyzoGEN;
import other.Point;
import other.Tile;
import other.Tiles;


/**
 * An AStar algorithm designed to work with a tile-based 2d game.
 * Tailored for Outlander.
 * Made according to: http://www.policyalmanac.org/games/aStarTutorial.htm
 * HARDCODED FOR STRAIGHT MOVES ONLY, NO DIAGONAL MOVES.
 * 
 *
 * ===================================== LICENSE =================================
 * Copyright 2013 Radoslaw Skupnik.
 * 
 * This file is part of MyzoGEN.
	
	   MyzoGEN is free software; you can redistribute it and/or modify
	   it under the terms of the GNU Lesser General Public License as published by
	   the Free Software Foundation; either version 2 of the License, or
	   (at your option) any later version.
	
	   MyzoGEN is distributed in the hope that it will be useful,
	   but WITHOUT ANY WARRANTY; without even the implied warranty of
	   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	   GNU Lesser General Public License for more details.
	
	   You should have received a copy of the GNU Lesser General Public License
	   along with MyzoGEN; if not, write to the Free Software
	   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * @author Radoslaw Skupnik, aka "Myzreal"
 **/
public class AStar {
	
	//private static final int STRAIGHT_MOVE_COST = 10;    unused
	private static final int DIAGONAL_MOVE_COST = 14;
	
	private ArrayList<Node> OPENLIST = new ArrayList<Node>();
	private ArrayList<Node> CLOSEDLIST = new ArrayList<Node>();
	private ArrayList<Node> INVALID = new ArrayList<Node>();
	
	private int width, height;
	private ASPoint finish;
	private ASPoint start;
	private boolean details;
	private int riverID;
	
	/**
	 * Constructor which takes necessary information.
	 * @param invalid - array of points that are "unpassable", they should be initialized with the 3rd parameter being false.
	 * @param width - width of the square grid (25 for Outlander)
	 * @param height - height of the square grid (14 for Outlander)
	 * @param start - start point (passable doesn't matter)
	 * @param finish - destination point (passable doesn't matter)
	 */
	public AStar(ArrayList<ASPoint> invalid, int width, int height, ASPoint start, ASPoint finish, boolean details, int riverID) {
		this.width = width;
		this.height = height;
		this.finish = finish;
		this.start = start;
		this.details = details;
		this.riverID = riverID;
		
		//Remember which tiles are unpassable.
		fillInvalidNodes(invalid);
	}
	
	/**
	 * This should be called after initializing AStar.
	 * @return A path in a form of an array of ASPoint starting from destination and heading towards start point. If destination is unreachable then null is returned.
	 */
	public ArrayList<ASPoint> calculate() {
		ArrayList<ASPoint> path = null;
		try {
		//STARTING THE PROCESS
		//1. Begin at starting point A...
		Node sNode = new Node(start.x, start.y);
		sNode.G = 0;
		calculateH(sNode);
		calculateF(sNode);
		addToOpenList(sNode);
		//2. Look at all the reachable or walkable...
		checkAdjacentNodesStart(sNode);
		//3. Drop the starting square A...
		removeFromOpenList(sNode);
		addToClosedList(sNode);
				
		Node fNode = new Node(finish.x, finish.y);
		Node breakNode = null;
		do {
		//CONTINUING THE PROCESS
		//Next, we choose one of the adjacent squares on the open list and more or less repeat the earlier process...
		//To continue the search, we simply choose the lowest F score square...
		Node chosen = getLowestFNode();
		if (chosen == null) {
			return null;
		}
		
		//4. Drop it from the open list and add it to the closed list...
		removeFromOpenList(chosen);
		addToClosedList(chosen);
		checkAdjacentNodes(chosen);
		
		Tile t = MyzoGEN.getOutput().getTile(new Point(chosen.x, chosen.y));
		if (t != null) {
			if (t.tile == Tiles.WATER && t.riverID != riverID || (chosen.x == 0 || chosen.y == 0 || chosen.x == width-1 || chosen.y == height-1)) {
				breakNode = chosen;
				break;
			}
		}
		
		} while (!isInClosedArray(fNode));
		
		if (breakNode != null)
			path = getPath(breakNode.x, breakNode.y);
		else
			path = getPath(fNode.x, fNode.y);
		} catch (Exception e) {e.printStackTrace();}
		return path;
	}
	
	private ArrayList<ASPoint> getPath(int x, int y) {
		ArrayList<ASPoint> out = new ArrayList<ASPoint>();
		Node fNode = getNodeFromClosedList(x, y);
		out.add(new ASPoint(fNode.x, fNode.y, true));
		do {
			Node n = fNode.parent;
			if (n == null)
				break;
			out.add(new ASPoint(n.x, n.y, true));
			fNode = n;
		} while (true);
		return out;
	}
	
	private Node getLowestFNode() {
		Node out = null;
		synchronized (OPENLIST) {
			for (Node n : OPENLIST) {
				if (out == null)
					out = n;
				else {
					if (n.F < out.F)
						out = n;
				}
			}
		}
		if (details && out != null) System.out.println("Lowest F Node: ("+out.x+","+out.y+")");
		return out;
	}
	
	private void checkAdjacentNodes(Node origin) {
		Node n = null;
		for (int i = origin.x-1; i <= origin.x+1; i++) {
			for (int j = origin.y-1; j <= origin.y+1; j++) {
				n = new Node(i, j);
				int dirr = directionFromAdjacentNode(origin, n);
				if (dirr == 1) {
					if (isValid(n)) {
						n.parent = origin;
						if (!isInOpenArray(n)) {
							calculateG(n);
							calculateH(n);
							calculateF(n);
							addToOpenList(n);
						}
						else {
							n = getNodeFromOpenList(n.x, n.y);
							int dir = directionFromAdjacentNode(origin, n);
							double nG = 0;
							if (dir == 0)
								nG = origin.G + DIAGONAL_MOVE_COST;
							else if (dir == 1)
								nG = origin.G + roundHeight(MyzoGEN.getOutput().getTile(new Point(n.x, n.y)).height);
							if (nG < n.G) {
								n.parent = origin;
								calculateG(n);
								calculateF(n);
							}
						}
					}
				}
			}
		}
	}
	
	private void checkAdjacentNodesStart(Node origin) {
		Node n = null;
		for (int i = origin.x-1; i <= origin.x+1; i++) {
			for (int j = origin.y-1; j <= origin.y+1; j++) {
				if (!(i == origin.x && j == origin.y)) {
					n = new Node(i, j);
					int dirr = directionFromAdjacentNode(origin, n);
					if (dirr == 1) {
						if (isValidStart(n)) {
							n.parent = origin;
							calculateG(n);
							calculateH(n);
							calculateF(n);
							addToOpenList(n);
						}
					}
				}
			}
		}
	}
	
	private boolean isValidStart(Node node) {
		if (node.x < 0 || node.y < 0 || node.x >= width || node.y >= height)
			return false;
		else {
			if (isInInvalidArray(node))
				return false;
			else
				return true;
		}
	}
	
	private boolean isValid(Node node) {
		if (node.x < 0 || node.y < 0 || node.x >= width || node.y >= height)
			return false;
		else {
			if (isInInvalidArray(node))
				return false;
			else {
				if (isInClosedArray(node))
					return false;
				else return true;
			}
		}
	}
	
	private void calculateG(Node node) {
		Tile t = MyzoGEN.getOutput().getTile(new Point(node.x, node.y));
		if (node.parent != null && t != null) {
			int dir = directionFromParent(node);
			if (dir == 1)
				node.G = roundHeight(t.height);
			else if (dir == 0)
				node.G = node.parent.G + DIAGONAL_MOVE_COST;
		}
		if (details) System.out.println("("+node.x+","+node.y+") G = "+node.G);
	}
	
	private void calculateH(Node node) {
		int dx = Math.abs(finish.x - node.x);
		int dy = Math.abs(finish.y - node.y);
		node.H = (dx + dy);
		if (details) System.out.println("("+node.x+","+node.y+") H = "+node.H);
	}
	
	private void calculateF(Node node) {
		node.F = (int) (node.G*1.2) + node.H;
		if (details) System.out.println("("+node.x+","+node.y+") F = "+node.F);
	}
	
	/**
	 * @return -1 if there is no parent, 0 if it is diagonal, 1 if straight.
	 */
	private int directionFromParent(Node node) {
		if (node.parent != null) {
			if (node.x != node.parent.x && node.y != node.parent.y)
				return 0;
			else
				return 1;
		} else return -1;
	}
	
	/**
	 * @return -1 if there is no parent, 0 if it is diagonal, 1 if straight.
	 */
	private int directionFromAdjacentNode(Node node, Node node1) {
		if (node.x != node1.x && node.y != node1.y)
			return 0;
		else
			return 1;
	}
	
	private boolean isInOpenArray(Node node) {
		synchronized (OPENLIST) {
			for (Node n : OPENLIST) {
				if (n.x == node.x && n.y == node.y) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isInClosedArray(Node node) {
		synchronized (CLOSEDLIST) {
			for (Node n : CLOSEDLIST) {
				if (n.x == node.x && n.y == node.y) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isInInvalidArray(Node node) {
		synchronized (INVALID) {
			for (Node n : INVALID) {
				if (n.x == node.x && n.y == node.y) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void fillInvalidNodes(ArrayList<ASPoint> points) {
		if (points != null) {
			synchronized (INVALID) {
				for (ASPoint point : points) {
					if (!point.passable)
						INVALID.add(new Node(point.x, point.y));
				}
			}
		}
	}
	
	private void addToOpenList(Node node) {
		synchronized (OPENLIST) {
			OPENLIST.add(node);
		}
	}
	
	private void removeFromOpenList(Node node) {
		synchronized (OPENLIST) {
			OPENLIST.remove(node);
		}
	}
	
	private void addToClosedList(Node node) {
		synchronized (CLOSEDLIST) {
			CLOSEDLIST.add(node);
		}
	}
	
	private Node getNodeFromOpenList(int x, int y) {
		synchronized (OPENLIST) {
			for (Node n : OPENLIST) {
				if (n.x == x && n.y == y)
					return n;
			}
		}
		return null;
	}
	
	private Node getNodeFromClosedList(int x, int y) {
		synchronized (CLOSEDLIST) {
			for (Node n : CLOSEDLIST) {
				if (n.x == x && n.y == y)
					return n;
			}
		}
		return null;
	}
	
	private int roundHeight(double h) {
		return (int) Math.round(h*1000);
	}
}
