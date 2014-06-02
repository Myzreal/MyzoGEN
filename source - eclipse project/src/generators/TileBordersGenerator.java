package generators;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.MyzoGEN;
import other.Biomes;
import other.Borders;
import other.Point;
import other.Tile;
import other.Tiles;
import parameters.IOFlags;
import utils.Utils;

/**
 * Creates borders between different tile types that should reside in the FRINGE_LOW layer.
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
public class TileBordersGenerator {

	private String name;
	private int chunksX, chunksY;
	private IOFlags ioflags;
	private boolean details;
	
	public TileBordersGenerator(String name, int chunksX, int chunksY, IOFlags flags, boolean details) {
		this.name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.ioflags = flags;
		this.details = details;
	}
	
	public String generate() {
		String error = "No errors.";
		
		if (details)
			System.out.println("TileBorders flags: PRODUCE="+ioflags.PRODUCE+", SAVE="+ioflags.SAVE+", LOAD="+ioflags.LOAD);
		
		if (Utils.flagsCheck(ioflags) != 0) {
			System.out.println("ERROR: IOFlags object is corrupted, error: "+Utils.flagsCheck(ioflags));
			return "TileBordersGenerator critical error - read above.";
		}
		
		if (ioflags.PRODUCE)
			error = produceBorders();
		
		if (ioflags.LOAD)
			error = loadBorders();
		
		return error;
	}
	
	private String loadBorders() {
		return "No errors.";
	}
	
	private String produceBorders() {
		BufferedImage bordersImage = null;
		if (ioflags.SAVE)
			bordersImage = new BufferedImage(chunksX * MyzoGEN.DIMENSION_X, chunksY * MyzoGEN.DIMENSION_Y, BufferedImage.TYPE_INT_ARGB);
		
		for (Tile tile : MyzoGEN.getOutput().getTilesArray().values()) {
			Point[] surrPoints = Utils.getSurroundingPoints(tile.origin);
			//if (!tile.wall) {
				if (isBorderHeight(tile, surrPoints)) {
					/**byte be = Borders.calcBorderTypeByTileTypeEdges(tile, surrPoints, false);
					byte bc = Borders.calcBorderTypeByTileTypeCorners(tile, surrPoints, false);
					tile.borderHeightEdges = be;
					tile.borderHeightCorners = bc;
					tile.borderHeightType = Tiles.STONE_WALL;**/
					
					Borders.calculateHeightData(tile, surrPoints);
					
					produceWall(tile, surrPoints, bordersImage);
					
					if (ioflags.SAVE) {
						bordersImage.setRGB(tile.origin.x, tile.origin.y, new Color(0, Math.abs(tile.borderFlagTwo), 0, 255).getRGB());
					}
				} 
				
				if (isBorderBiomes(tile, surrPoints)) {
					/**byte be = Borders.calcBorderTypeByTileTypeEdges(tile, surrPoints, true);
					byte bc = Borders.calcBorderTypeByTileTypeCorners(tile, surrPoints, true);
					tile.borderBiomesEdges = be;
					tile.borderBiomesCorners = bc;
					tile.borderBiomesType = getBorderTypeBiomes(tile, surrPoints);**/
					
					Borders.calculateBiomeData(tile, surrPoints);
					
					if (ioflags.SAVE) {
						//System.out.println(tile.borderFlagOne);
						bordersImage.setRGB(tile.origin.x, tile.origin.y, new Color(Math.abs(tile.borderFlagOne), 0, 0, 255).getRGB());
					}
				}
			//}
		}
		
		if (ioflags.SAVE && bordersImage != null) {
			Utils.saveImage(bordersImage, "output/"+name+"/overviews/borders_overview.png");
			if (details) System.out.println("Borders overview produced. It can be found in: output/"+name+"/overviews/");
		}
		
		return "No errors.";
	}
	
	/**
	 * Checks for a biome border.
	 * @param tile
	 * @param surrPoints
	 * @return
	 */
	private boolean isBorderBiomes(Tile tile, Point[] surrPoints) {
		boolean out = false;
		for (Point p : surrPoints) {
			Tile t = MyzoGEN.getOutput().getTile(p);
			if (t != null && t.tile != tile.tile && Tiles.getPrecedence(t.tile) > Tiles.getPrecedence(tile.tile)) {
				out = true;
				break;
			}
		}
		return out;
	}
	
	private byte getBorderTypeBiomes(Tile tile, Point[] surrPoints) {
		for (Point p : surrPoints) {
			Tile t = MyzoGEN.getOutput().getTile(p);
			if (t != null && t.tile != tile.tile && Tiles.getPrecedence(t.tile) > Tiles.getPrecedence(tile.tile)) {
				return t.tile;
			}
		}
		return (byte) 255;
	}
	
	/**
	 * Checks for a height border.
	 * @param tile
	 * @param surrPoints
	 * @return
	 */
	private boolean isBorderHeight(Tile tile, Point[] surrPoints) {
		boolean out = false;
		for (Point p : surrPoints) {
			Tile t = MyzoGEN.getOutput().getTile(p);
			if (t != null && t.floor < tile.floor) {
				out = true;
				break;
			}
		}
		return out;
	}
	
	private void produceWall(Tile tile, Point[] surrPoints, BufferedImage bordersImage) {
		Tile t = MyzoGEN.getOutput().getTile(surrPoints[2]);
		if (t != null && t.floor < tile.floor) {
			//t.wall = true;
			t.tile = Tiles.STONE_WALL;
			/**t.borderBiomesCorners = (byte) 255;
			t.borderBiomesEdges = (byte) 255;
			t.borderBiomesType = Tiles.STONE_WALL;**/
			
			if (ioflags.SAVE) {
				bordersImage.setRGB(t.origin.x, t.origin.y, new Color(0, 0, 224, 255).getRGB());
			}
		}
	}
}
