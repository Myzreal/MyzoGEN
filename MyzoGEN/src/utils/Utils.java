package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.MyzoGEN;

import other.Biomes;
import other.Point;
import parameters.FloorSettings;
import parameters.IOFlags;
import util.ColorCafe;
import util.ImageCafe;

/**
 * This class encapsulates all the useful functions that do not fit elsewhere.
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
public class Utils {
	
	/**
	 * Returns the manhattan distance between two points.
	 * @param one
	 * @param two
	 * @return
	 */
	public static int getManhattanDistance(Point one, Point two) {
		return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
	}
	
	/**
	 * Get the width and height of a square created by the two specified points
	 * being opposite corners of the square.
	 * @param start
	 * @param end
	 * @return
	 */
	public static int[] getEnclosedSquareWidthHeight(Point start, Point end) {
		int[] output = new int[2];
		output[0] = Math.abs(start.x - end.x);
		output[1] = Math.abs(start.y - end.y);
		
		return output;
	}
	
	/**
	 * Returns an array of points surrounding this point starting with the one "above" and going clockwise.
	 * Does not include diagonal points.
	 * @param input
	 * @return
	 */
	public static Point[] getSurroundingPointsNoDiagonal(Point input) {
		Point[] output = new Point[4];
		output[0] = new Point(input.x, input.y-1);
		output[1] = new Point(input.x+1, input.y);
		output[2] = new Point(input.x, input.y+1);
		output[3] = new Point(input.x-1, input.y);
		
		return output;
	}
	
	/**
	 * Returns an array of points surrounding this point starting with the one "above" and going clockwise.
	 * Does include diagonal points.
	 * @param input
	 * @return
	 */
	public static Point[] getSurroundingPoints(Point input) {
		Point[] output = new Point[8];
		output[0] = new Point(input.x, input.y-1);
		output[1] = new Point(input.x+1, input.y);
		output[2] = new Point(input.x, input.y+1);
		output[3] = new Point(input.x-1, input.y);
		output[4] = new Point(input.x-1, input.y-1);
		output[5] = new Point(input.x+1, input.y-1);
		output[6] = new Point(input.x+1, input.y+1);
		output[7] = new Point(input.x-1, input.y+1);
		
		return output;
	}

	/**
	 * Checks an IOFlags object for potential errors.
	 * @param flags
	 * @return 0 - all ok; 1 - LOAD flag cannot be ON when PRODUCE is ON; 2 - SAVE cannot be ON when PRODUCE is OFF; 3 - PRODUCE and/or SAVE cannot be ON when LOAD is ON
	 */
	public static int flagsCheck(IOFlags flags) {
		int output = 0;
		
		if (flags.PRODUCE) {
			if (flags.LOAD)
				output = 1;
		}
		
		if (flags.SAVE) {
			if (!flags.PRODUCE)
				output = 2;
		}
		
		if (flags.LOAD) {
			if (flags.PRODUCE || flags.SAVE)
				output = 3;
		}
		
		return output;
	}
	
	/**
	 * Used when loading a heightmap - calculates the pixel range 0-255 to a height range -1 - 1.
	 * @param pixel
	 * @return
	 */
	public static double pixelToHeight(Color pixel) {
		double perc = pixel.getRed() / 256.0;
		double v1 = 2 * perc;
		double v2 = v1 - 1 + 0.01;
		return v2;
	}
	
	/**
	 * Used when loading a temperature map - calculates the pixel range 0-255 to a temperature range specified in a constant in OutlanderGenerator.
	 * @param pixel
	 * @return
	 */
	public static int pixelToTemperature(Color pixel) {
		double perc = pixel.getRed() / 256.0;
		double v1 = (2 * MyzoGEN.TEMPERATURE_RANGE) * perc;
		double v2 = v1 - MyzoGEN.TEMPERATURE_RANGE;
		return (int) Math.round(v2);
	}
	
	/**
	 * Pixel to a byte indicating a biome, simple as that.
	 * @param pixel
	 * @return
	 */
	public static byte pixelToBiome(Color pixel) {
		return Biomes.BIOME_COLORS.get(pixel.getRGB());
	}
	
	public static int calculateFloor(double height, FloorSettings settings) {
		int res = 0;
		for (int i = 0; i < settings.floorLevels.length; i++) {
			if (height < settings.floorLevels[i]) {
				res = i;
				break;
			}
		}
		
		return res;
	}
	
	/**
	 * Transforms an ImageCafe object into a BufferedImage.
	 * @param x - dimensions of the image.
	 * @param y - dimensions of the image.
	 * @param image - ImageCafe object.
	 * @return
	 */
	public static BufferedImage imageCafeToBufferedImage(int x, int y, ImageCafe image) {
		BufferedImage im = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		for (int ii = 0; ii < x; ii++)
	    {
	         for (int jj = 0; jj < y; jj++)
	         {
	        	ColorCafe colorCafe = image.getValue(ii, jj);
	        	int red, blue, green, alpha;
		      	red = colorCafe.getRed();
		      	blue = colorCafe.getBlue();
		      	green = colorCafe.getGreen();
		      	alpha = colorCafe.getAlpha();
		      	Color color = new Color(red, green, blue, alpha);
		        int rgbnumber = color.getRGB();
	            im.setRGB(ii, jj, rgbnumber);
	         }
	    }
		return im;
	}
	
	/**
	 * Saves the specified BufferedImage at the specified path.
	 * @param im
	 * @param filename
	 */
	public static void saveImage(BufferedImage im, String filename) {
		try
        {
           ImageIO.write(im, "png", new File(filename));
        }
        catch (IOException e1)
        {
           System.out.println("Could not write the image file.");
        }
	}
	
	/**
	 * Rounds a double to specified decimal spots.
	 * @param d - double to be rounded
	 * @param c - how many decimals should be preserved
	 * @return
	 */
	public static double roundToDecimals(double d, int c) {
		int temp=(int)((d*Math.pow(10,c)));
		return (((double)temp)/Math.pow(10,c));
	}
}
