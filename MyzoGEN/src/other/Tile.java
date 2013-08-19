package other;

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
public class Tile {
	
	public Point origin;
	public double height = 0;
	public int floor = 1;
	public int temperature = 20;
	public int humidity = -20;
	public byte biome = Biomes.MODERATE;
	public byte tile = Tiles.GRASS;
	public boolean river = false;
	public int riverID = -1;
	
	public Tile(Point o) {
		origin = o;
	}
	
	@Override
	public String toString() {
		return "Origin: "+origin+"; Height: "+height+"; Floor: "+floor+"; Temp.: "+temperature+"; Humid.: "+humidity+"; Biome: "+Biomes.biomeToString(biome)+"; Tile: "+Tiles.tileToString(tile)+"; River: "+river+"; RiverID: "+riverID;
	}
}
