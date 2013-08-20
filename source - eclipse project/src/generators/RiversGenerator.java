package generators;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import main.MyzoGEN;
import other.Point;
import other.Tile;
import parameters.IOFlags;
import utils.Utils;
import astar.ASPoint;
import astar.AStar;

/** 
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
public class RiversGenerator {
	
	private String Name;
	private int chunksX, chunksY, maxrivers, minfloor, mindistance;
	private IOFlags ioflags;
	private boolean details;
	
	Random rnd = new Random();
	private HashMap<Integer, Point> rivers = new HashMap<Integer, Point>();
	private ArrayList<Point> riverEndings = new ArrayList<Point>();

	public RiversGenerator(String name, int chunksX, int chunksY, int maxrivers, int minfloor, int mindistance, IOFlags flags, boolean details) {
		this.Name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.maxrivers = maxrivers;
		this.minfloor = minfloor;
		this.mindistance = mindistance;
		this.ioflags = flags;
		this.details = details;
	}
	
	public String generate() {
		String error = "No errors.";
		
		if (details)
			System.out.println("Rivers flags: PRODUCE="+ioflags.PRODUCE+", SAVE="+ioflags.SAVE+", LOAD="+ioflags.LOAD);
		
		if (Utils.flagsCheck(ioflags) != 0) {
			System.out.println("ERROR: IOFlags object is corrupted, error: "+Utils.flagsCheck(ioflags));
			return "RiversGenerator critical error - read above.";
		}
		
		if (ioflags.PRODUCE)
			error = produceRivers();
		
		if (ioflags.LOAD)
			error = loadRivers();
		
		return error;
	}
	
	private String produceRivers() {
		findRiverOrigins();
		findRiverEndings();
		
		int c = 0;
		for (Point r : rivers.values()) {
			if (details) System.out.println("Developing river: "+c);
			developRiver(r, false, c);
			c++;
		}
		
		if (ioflags.SAVE)
			produceImage();
		
		return "No errors.";
	}
	
	private String loadRivers() {
		if (details) System.out.println("Loading rivers.");
		
		File imageFile = new File("output/"+Name+"/overviews/rivers_overview.png");
		if (imageFile.exists()) {
			try {
				BufferedImage tempImage = ImageIO.read(imageFile);
				if (tempImage.getWidth() == chunksX * MyzoGEN.DIMENSION_X && tempImage.getHeight() == chunksY * MyzoGEN.DIMENSION_Y) {
					for (int i = 0; i < chunksX * MyzoGEN.DIMENSION_X; i++) {
						for (int j = 0; j < chunksY * MyzoGEN.DIMENSION_Y; j++) {
							Color pixel = new Color(tempImage.getRGB(i, j), true);
							if (pixel.getBlue() == 255) {
								MyzoGEN.getOutput().getTile(new Point(i, j)).river = true;
								if (pixel.getAlpha() <= 254)
									MyzoGEN.getOutput().getTile(new Point(i, j)).riverID = 254 - pixel.getAlpha();
							}
						}
					}
				} else return "RiversGenerator critical error - the loaded temperature map does not match the specified dimensions. Loaded file is: "+tempImage.getWidth()+"x"+tempImage.getHeight()+
						"while the specified dimensions are: "+(chunksX * MyzoGEN.DIMENSION_X)+"x"+(chunksY * MyzoGEN.DIMENSION_Y);
			} catch (IOException e) {
				e.printStackTrace();
				return "RiversGenerator critical error - LOAD option is on but I can't load the file.";
			}
		} else return "RiversGenerator critical error - LOAD option is on but I can't find the rivers_overview.png file.";
		
		return "No errors.";
	}
	
	/**
	 * Takes a starting point of a river and develops it further until it is finished.
	 * @param start
	 */
	private String developRiver(Point start, boolean d, int rivnum) {
		try {
			Random rnd = new Random();
			int broadnum = 0;
			int broadam = 0;
			Point end = findClosestEnding(start, rivnum);
			AStar astar = new AStar(null, MyzoGEN.DIMENSION_X * chunksX, MyzoGEN.DIMENSION_Y * chunksY, new ASPoint(start.x, start.y, true), new ASPoint(end.x, end.y, true), d, rivnum);
			ArrayList<ASPoint> path = astar.calculate();
			if (path != null) {
				for (ASPoint asp : path) {
					Tile t = MyzoGEN.getOutput().getTile(new Point(asp.x, asp.y));
					if (t != null) {
						t.river = true;
						t.riverID = rivnum;
						
						//Small chance to make the river broaden.
						if (broadnum <= 0) {
							if (rnd.nextInt(100) < 50) {
								if (rnd.nextInt(100) < 30) {
									broadam = 2;
								} else {
									broadam = 1;
								}
								broadnum = 6;
							}
						}
						
						if (broadnum > 0) {
							broadnum--;
							broadenRiver(t, broadam, rivnum);
							if (broadnum == 0) broadam = 0;
						}
					}
				}
			}
			path = null;
		} catch (Exception e) {e.printStackTrace();}
		
		if (details) System.out.println("Finished river.");
		
		return "No errors.";
	}
	
	/**
	 * Broadens the river at the tile point by additional tiles.
	 * @param tile
	 * @param broad
	 */
	private void broadenRiver(Tile tile, int broad, int rivnum) {
		Point[] surrPoints = Utils.getSurroundingPoints(tile.origin);
		for (Point p : surrPoints) {
			Tile t = MyzoGEN.getOutput().getTile(p);
			if (t != null && t.riverID == -1) {
				t.river = true;
				t.riverID = rivnum;
				
				if (broad == 2) {
					Point[] surrPoints2 = Utils.getSurroundingPoints(p);
					for (Point p2 : surrPoints2) {
						Tile t2 = MyzoGEN.getOutput().getTile(p2);
						if (t2 != null && t2.riverID == -1) {
							t2.river = true;
							t2.riverID = rivnum;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Scans the tilemap and finds river origins.
	 * These can only occur at or above the specified floor.
	 * Once limit is reached, the loop breaks.
	 */
	private void findRiverOrigins() {
		if (details) System.out.println("Finding river origins...");
		Random rnd = new Random();
		int c = 0;
		int rx = 0;
		int ry = 0;
		do {
			rx = rnd.nextInt(MyzoGEN.DIMENSION_X * chunksX);
			ry = rnd.nextInt(MyzoGEN.DIMENSION_Y * chunksY);
			Point p = new Point(rx, ry);
			Tile t = MyzoGEN.getOutput().getTile(p);
			if (t != null && t.floor >= minfloor) {
				boolean okay = true;
				for (Point r : rivers.values()) {
					if (Utils.getManhattanDistance(p, r) < mindistance) {
						okay = false;
						break;
					}
				}
				
				if (okay) {
					rivers.put(c, p);
					t.river = true;
					t.riverID = c;
					c++;
				}
			}
		} while (c < maxrivers);
		
		if (details) System.out.println("Origins found: "+c+", max was: "+maxrivers);
	}
	
	/**
	 * Iterates through all tiles and finds possible river endings.
	 * A river ending is a water tile (lowest floor) that has a grass tile (one floor higher) as a neighbor.
	 */
	private void findRiverEndings() {
		if (details) System.out.println("Looking for possible river endings...");
		for (Tile tile : MyzoGEN.getOutput().getTilesArray().values()) {
			if (tile.height < MyzoGEN.getFloorSettings().floorLevels[0] && !tile.river) {
				tile.river = true;
				riverEndings.add(tile.origin);
			}
		}
		if (details) System.out.println("River endings found: "+riverEndings.size());
	}
	
	/**
	 * Finds a river ending closest to the specified start point.
	 * Measured by manhattan distance.
	 * @param start
	 * @return
	 */
	private Point findClosestEnding(Point start, int rivnum) {
		Point closest = null;
		for (Point p : riverEndings) {
			Tile t = MyzoGEN.getOutput().getTile(p);
			if (t != null && t.riverID != rivnum) {
				if (closest == null) {
					closest = p;
				} else {
					if (Utils.getManhattanDistance(start, p) < Utils.getManhattanDistance(start, closest))
						closest = p;
				}
			}
		}
		
		return closest;
	}
	
	/**
	 * Produces an image of the rivers.
	 */
	private void produceImage() {
		BufferedImage riversImage = new BufferedImage(chunksX * MyzoGEN.DIMENSION_X, chunksY * MyzoGEN.DIMENSION_Y, BufferedImage.TYPE_INT_ARGB);
		
		for (Tile tile : MyzoGEN.getOutput().getTilesArray().values()) {
			if (tile.river) {
				riversImage.setRGB(tile.origin.x, tile.origin.y, new Color(0, 0, 255, 255).getRGB());
				if (tile.riverID != -1)
					riversImage.setRGB(tile.origin.x, tile.origin.y, new Color(0, 0, 255, 254 - tile.riverID).getRGB());
				else
					riversImage.setRGB(tile.origin.x, tile.origin.y, new Color(0, 0, 255, 255).getRGB());
			}
		}
		
		Utils.saveImage(riversImage, "output/"+Name+"/overviews/rivers_overview.png");
	}
}
