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
public class Tiles {

	public static final byte WATER = 0;
	public static final byte GRASS = 1;
	public static final byte SNOW = 2;
	public static final byte SAVANNAH = 3;
	public static final byte SAND_DESERT = 4;
	
	public static final byte STONE_WALL = 32;
	
	//Border precedence decides which tile will be the border at in case of two different tiles bordering.
	//For example if grass and savannah meet then the border will be at savannah tile because prec is lower and grass will be the bordering type because prec is higher.
	public static final byte WATER_PRECEDENCE = 4;
	public static final byte GRASS_PRECEDENCE = 3;
	public static final byte SNOW_PRECEDENCE = 2;
	public static final byte SAVANNAH_PRECEDENCE = 1;
	public static final byte SAND_DESERT_PRECEDENCE = 0;
	
	public static String tileToString(byte t) {
		switch (t) {
			case WATER:
				return "Water";
			case GRASS:
				return "Grass";
			case SNOW:
				return "Snow";
			case SAVANNAH:
				return "Savannah";
			case SAND_DESERT:
				return "Sand_Desert";
			default:
				return "Grass";
		}
	}
	
	public static byte tileToPrecedence(byte t) {
		switch (t) {
			case WATER:
				return WATER_PRECEDENCE;
			case GRASS:
				return GRASS_PRECEDENCE;
			case SNOW:
				return SNOW_PRECEDENCE;
			case SAVANNAH:
				return SAVANNAH_PRECEDENCE;
			case SAND_DESERT:
				return SAND_DESERT_PRECEDENCE;
			default:
				return (byte) 0;
		}
	}
}
