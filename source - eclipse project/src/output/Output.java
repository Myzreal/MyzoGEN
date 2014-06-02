package output;

import java.util.HashMap;
import java.util.Random;

import other.Point;
import other.Tile;
import other.gObject;

import utils.Utils;

import main.MyzoGEN;

/**
 * This class contains the output of the generator.
 * Placeholders are created at start and they are filled with data at certain steps.
 * This object can then be passed to a class that will save it in any format.
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
public class Output {
	
	private int width = 10;
	private int height = 10;
	private HashMap<Point, Tile> TILES = new HashMap<Point, Tile>();
	
	public Output(int width, int height, boolean details) {
		this.width = width;
		this.height = height;
		
		if (details)
			System.out.println("Creating output: "+(width*height)+" tiles.");
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				Point pnt = new Point(i, j);
				TILES.put(pnt, new Tile(pnt));
			}
		}
	}
	
	public void debugCheck() {
		Random r = new Random();
		int rw1 = r.nextInt(width);
		int rh1 = r.nextInt(height);
		System.out.println(TILES.get(new Point(rw1, rh1)));
		rw1 = r.nextInt(width);
		rh1 = r.nextInt(height);
		System.out.println(TILES.get(new Point(rw1, rh1)));
		rw1 = r.nextInt(width);
		rh1 = r.nextInt(height);
		System.out.println(TILES.get(new Point(rw1, rh1)));
	}
	
	public HashMap<Point, Tile> getTilesArray() {
		return TILES;
	}
	
	public Tile getTile(Point pnt) {
		return TILES.get(pnt);
	}

	/**
	 * This sets only the height value and does not touch the floor value.
	 * @param pnt
	 * @param height
	 */
	public void setHeight(Point pnt, double height) {
		Tile t = TILES.get(pnt);
		if (t != null)
			t.height = height;
		else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * This sets the height by calling setHeight(pnt, height) and later
	 * calculates the floor from the height using FloorSettings and sets
	 * the floor value.
	 * @param pnt
	 * @param height
	 */
	public void setHeightAndFloor(Point pnt, double height) {
		setHeight(pnt, height);
		Tile t = TILES.get(pnt);
		if (t != null)
			t.floor = Utils.calculateFloor(height, MyzoGEN.getFloorSettings());
		else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * Sets the tile at the specified point to be a forest area.
	 * @param pnt
	 */
	public void setForestArea(Point pnt) {
		Tile t = TILES.get(pnt);
		if (t != null) {
			t.isForestArea = true;
		} else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * Sets an object at this tile.
	 * @param pnt
	 * @param obj
	 */
	public void setObject(Point pnt, gObject obj) {
		Tile t = TILES.get(pnt);
		if (t != null) {
			t.object = obj;
		} else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * This accepts the raw noise input from TemperatureGenerator and produces
	 * an int temperature from it.
	 * @param pnt
	 * @param temp
	 */
	public void setTemperature(Point pnt, double temp) {
		Tile t = TILES.get(pnt);
		if (t != null) {
			float tempF = (float) temp;
			t.temperature = Math.round(tempF*MyzoGEN.TEMPERATURE_RANGE);
		} else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * This accepts a precalculated noise value that has already been turned into an temperature integer.
	 * @param pnt
	 * @param temp
	 */
	public void setTemperatureSimple(Point pnt, int temp) {
		Tile t = TILES.get(pnt);
		if (t != null) {
			t.temperature = temp;
		} else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * This accepts a precalculated noise value that has already been turned into an humidity integer.
	 * @param pnt
	 * @param temp
	 */
	public void setHumiditySimple(Point pnt, int hum) {
		Tile t = TILES.get(pnt);
		if (t != null) {
			t.humidity = hum;
		} else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
	
	/**
	 * This accepts the raw noise input from HumidityGenerator and produces
	 * an int humidity from it.
	 * @param pnt
	 * @param temp
	 */
	public void setHumidity(Point pnt, double hum) {
		Tile t = TILES.get(pnt);
		if (t != null) {
			float humF = (float) hum;
			t.humidity = Math.round(humF*MyzoGEN.HUMIDITY_RANGE);
		} else {
			System.out.println("Tile at "+pnt+" not found.");
			return;
		}
	}
}
