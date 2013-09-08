package other;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

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
public class Biomes {
	
	public static final byte DESERT = 0;
	public static final byte SAVANNAH = 1;
	public static final byte SWAMP_RAIN_FOREST = 2;
	public static final byte MODERATE = 3;
	public static final byte TUNDRA = 4;
	
	public static final Color DESERT_COLOR = new Color(255, 106, 0, 255);
	public static final Color SAVANNAH_COLOR = new Color(182, 255, 0, 255);
	public static final Color SWAMP_RAIN_FOREST_COLOR = new Color(38, 127, 0, 255);
	public static final Color MODERATE_COLOR = new Color(0, 255, 33, 255);
	public static final Color TUNDRA_COLOR = new Color(0, 255, 255, 255);
	
	public static HashMap<Integer, Byte> BIOME_COLORS = new HashMap<Integer, Byte>();
	public static HashMap<Byte, Byte> BIOME_TILES = new HashMap<Byte, Byte>();
	
	static {
		BIOME_COLORS.put(DESERT_COLOR.getRGB(), DESERT);
		BIOME_COLORS.put(SAVANNAH_COLOR.getRGB(), SAVANNAH);
		BIOME_COLORS.put(SWAMP_RAIN_FOREST_COLOR.getRGB(), SWAMP_RAIN_FOREST);
		BIOME_COLORS.put(MODERATE_COLOR.getRGB(), MODERATE);
		BIOME_COLORS.put(TUNDRA_COLOR.getRGB(), TUNDRA);
		
		BIOME_TILES.put(DESERT, Tiles.SAND_DESERT);
		BIOME_TILES.put(SAVANNAH, Tiles.SAVANNAH);
		BIOME_TILES.put(SWAMP_RAIN_FOREST, Tiles.GRASS);
		BIOME_TILES.put(MODERATE, Tiles.GRASS);
		BIOME_TILES.put(TUNDRA, Tiles.SNOW);
	}

	public static String biomeToString(byte b) {
		switch (b) {
			case DESERT:
				return "Desert";
			case SAVANNAH:
				return "Savannah";
			case SWAMP_RAIN_FOREST:
				return "Swamp/Rain Forest";
			case MODERATE:
				return "Moderate";
			case TUNDRA:
				return "Tundra";
			default:
				return "Moderate";
		}
	}
	
	public static Color biomeToColor(byte b) {
		switch (b) {
			case DESERT:
				return DESERT_COLOR;
			case SAVANNAH:
				return SAVANNAH_COLOR;
			case SWAMP_RAIN_FOREST:
				return SWAMP_RAIN_FOREST_COLOR;
			case MODERATE:
				return MODERATE_COLOR;
			case TUNDRA:
				return TUNDRA_COLOR;
			default:
				return MODERATE_COLOR;
		}
	}
	
	public static byte calculateBiome(BufferedImage biomesPNG, int temp, int hum) {
		int t1 = temp + 20;
		int tpos = 40 - t1;
		int h1 = hum + 20;
		int hpos = 40 - h1;
		int rgb = biomesPNG.getRGB(tpos, hpos);
		return BIOME_COLORS.get(rgb);
	}
}
