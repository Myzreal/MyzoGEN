package generators;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.MyzoGEN;
import other.Biomes;
import other.Point;
import other.Tile;
import parameters.IOFlags;
import utils.Utils;

/**
 * This generator is based on a small 41x41 image: biomes.png.
 * According to the temperature and humidity values on the scale of -20 - 20 (41 values total in one dimension)
 * it chooses a pixel from the image and judging by its color - sets the biome. 
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
public class BiomesGenerator {

	private String Name;
	private int chunksX, chunksY;
	private IOFlags ioflags;
	private boolean details;
	
	private BufferedImage biomesPNG;
	
	public BiomesGenerator(String name, int chunksX, int chunksY, IOFlags flags, boolean details) {
		this.Name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.ioflags = flags;
		this.details = details;
	}
	
	public String generate() {
		String error = "No errors.";
		
		if (details)
			System.out.println("Biomes flags: PRODUCE="+ioflags.PRODUCE+", SAVE="+ioflags.SAVE+", LOAD="+ioflags.LOAD);
		
		if (Utils.flagsCheck(ioflags) != 0) {
			System.out.println("ERROR: IOFlags object is corrupted, error: "+Utils.flagsCheck(ioflags));
			return "BiomesGenerator critical error - read above.";
		}
		
		if (ioflags.PRODUCE)
			error = produceBiomes();
		
		if (ioflags.LOAD)
			error = loadBiomes();
		
		return error;
	}
	
	private String loadBiomes() {
		if (details) System.out.println("Loading biomes.");
		
		File imageFile = new File("output/"+Name+"/overviews/biomes_overview.png");
		if (imageFile.exists()) {
			try {
				BufferedImage tempImage = ImageIO.read(imageFile);
				if (tempImage.getWidth() == chunksX * MyzoGEN.DIMENSION_X && tempImage.getHeight() == chunksY * MyzoGEN.DIMENSION_Y) {
					for (int i = 0; i < chunksX * MyzoGEN.DIMENSION_X; i++) {
						for (int j = 0; j < chunksY * MyzoGEN.DIMENSION_Y; j++) {
							Color pixel = new Color(tempImage.getRGB(i, j));
							MyzoGEN.getOutput().getTile(new Point(i, j)).setBiomeAndType(Utils.pixelToBiome(pixel));
						}
					}
				} else return "BiomesGenerator critical error - the loaded temperature map does not match the specified dimensions. Loaded file is: "+tempImage.getWidth()+"x"+tempImage.getHeight()+
						"while the specified dimensions are: "+(chunksX * MyzoGEN.DIMENSION_X)+"x"+(chunksY * MyzoGEN.DIMENSION_Y);
			} catch (IOException e) {
				e.printStackTrace();
				return "BiomesGenerator critical error - LOAD option is on but I can't load the file.";
			}
		} else return "BiomesGenerator critical error - LOAD option is on but I can't find the biomes_overview.png file.";
		
		return "No errors.";
	}
	
	private String produceBiomes() {
		BufferedImage biomesImage = null;
		if (ioflags.SAVE)
			biomesImage = new BufferedImage(chunksX * MyzoGEN.DIMENSION_X, chunksY * MyzoGEN.DIMENSION_Y, BufferedImage.TYPE_INT_ARGB);
		
		if (loadBiomesPNG()) {
			for (Tile tile : MyzoGEN.getOutput().getTilesArray().values()) {
				tile.setBiomeAndType(Biomes.calculateBiome(biomesPNG, tile.temperature, tile.humidity));
				
				if (ioflags.SAVE && biomesImage != null) {
					biomesImage.setRGB(tile.origin.x, tile.origin.y, Biomes.biomeToColor(tile.biome).getRGB());
				}
			}
			
			if (ioflags.SAVE && biomesImage != null) {
				Utils.saveImage(biomesImage, "output/"+Name+"/overviews/biomes_overview.png");
				if (details) System.out.println("Biomes overview produced. It can be found in: output/"+Name+"/overviews/");
			}
		} else return "BiomesGenerator critical error - could not load biomes.png file.";
		
		return "No errors.";
	}
	
	private boolean loadBiomesPNG() {
		try {
			biomesPNG = ImageIO.read(Class.class.getResource("/resources/biomes.png"));
		} catch (IOException e) {
			System.out.println("Can't read the biomes.png diagram.");
			e.printStackTrace();
		}
		
		if (biomesPNG.getHeight() == 41 && biomesPNG.getWidth() == 41)
			return true;
		else return false;
	}
}
