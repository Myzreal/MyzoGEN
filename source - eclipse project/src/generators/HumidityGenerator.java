package generators;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import other.Gradient;
import other.Point;

import exception.ExceptionInvalidParam;
import main.MyzoGEN;
import module.Perlin;
import parameters.IOFlags;
import util.ImageCafe;
import util.NoiseMap;
import util.NoiseMapBuilderPlane;
import util.RendererImage;
import utils.Utils;

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
public class HumidityGenerator {

	private String name;
	private int chunksX, chunksY, seed, octaves;
	private double frequency;
	private IOFlags ioflags;
	private boolean details;
	
	public HumidityGenerator(String name, int chunksX, int chunksY, int seed, int octaves, double freq, IOFlags flags, boolean details) {
		this.name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.seed = seed;
		this.octaves = octaves;
		this.frequency = freq;
		this.ioflags = flags;
		this.details = details;
	}
	
	public String generate() {
		String error = "No errors.";
		
		if (details)
			System.out.println("Humidity flags: PRODUCE="+ioflags.PRODUCE+", SAVE="+ioflags.SAVE+", LOAD="+ioflags.LOAD);
		
		if (Utils.flagsCheck(ioflags) != 0) {
			System.out.println("ERROR: IOFlags object is corrupted, error: "+Utils.flagsCheck(ioflags));
			return "HumidityGenerator critical error - read above.";
		}
		
		if (ioflags.PRODUCE)
			produceHumidity();
		
		if (ioflags.LOAD)
			error = loadHumidity();
		
		return error;
	}
	
	private String loadHumidity() {
		if (details) System.out.println("Loading humidity.");
		
		File imageFile = new File("output/"+name+"/overviews/humidity_overview.png");
		if (imageFile.exists()) {
			try {
				BufferedImage humImage = ImageIO.read(imageFile);
				if (humImage.getWidth() == chunksX * MyzoGEN.DIMENSION_X && humImage.getHeight() == chunksY * MyzoGEN.DIMENSION_Y) {
					for (int i = 0; i < chunksX * MyzoGEN.DIMENSION_X; i++) {
						for (int j = 0; j < chunksY * MyzoGEN.DIMENSION_Y; j++) {
							Color pixel = new Color(humImage.getRGB(i, j));
							int hum = Utils.pixelToTemperature(pixel);
							MyzoGEN.getOutput().setHumiditySimple(new Point(i, j), hum);
						}
					}
				} else return "HumidityGenerator critical error - the loaded temperature map does not match the specified dimensions. Loaded file is: "+humImage.getWidth()+"x"+humImage.getHeight()+
						"while the specified dimensions are: "+(chunksX * MyzoGEN.DIMENSION_X)+"x"+(chunksY * MyzoGEN.DIMENSION_Y);
			} catch (IOException e) {
				e.printStackTrace();
				return "HumidityGenerator critical error - LOAD option is on but I can't load the file.";
			}
		} else return "HumidityGenerator critical error - LOAD option is on but I can't find the humidity_overview.png file.";
		
		return "No errors.";
	}
	
	private void produceHumidity() {
		Perlin noise = new Perlin();
		try { noise.setOctaveCount(octaves); } catch (Exception e) {e.printStackTrace();}
		noise.setFrequency(frequency);
		noise.setSeed(seed);
		
		if (details) System.out.println("Noise produced.");
		
		int dimx = MyzoGEN.DIMENSION_X * chunksX;
		int dimy = MyzoGEN.DIMENSION_Y * chunksY;
		NoiseMap tempMap = null;
		try {
			tempMap = new NoiseMap(dimx, dimy);
			NoiseMapBuilderPlane builder = new NoiseMapBuilderPlane();
			builder.setSourceModule(noise);
			builder.setDestNoiseMap(tempMap);
            builder.setDestSize(dimx, dimy);
            builder.setBounds(0, chunksX * 4, 0, chunksY*4);
            builder.build();
		} catch (Exception e) {e.printStackTrace();}
		
		for (int ii = 0; ii < dimx; ii++) {
			for (int jj = 0; jj < dimy; jj++) {
				MyzoGEN.getOutput().setHumidity(new Point(ii, jj), Utils.roundToDecimals(tempMap.getValue(ii, jj), 4));
			}
		}
		
		if (ioflags.SAVE) {
			ImageCafe image = null;
			try {
	            RendererImage render = new RendererImage();
	            image = new ImageCafe(dimx, dimy);
	            render.setSourceNoiseMap(tempMap);
	            render.setDestImage(image);
	            render.clearGradient();
	            Gradient gradient = new Gradient("humidity");
	            for (int i = 0; i < gradient.gradientPoints.length; i++) {
	            	render.addGradientPoint(gradient.gradientPoints[i], gradient.gradientColors[i]);
	            }
	            render.render();
	        } catch (ExceptionInvalidParam ex) {
	            ex.printStackTrace();
	        }
			
			BufferedImage im = Utils.imageCafeToBufferedImage(dimx, dimy, image);
			
			Utils.saveImage(im, "output/"+name+"/overviews/humidity_overview.png");
			
			if (details) System.out.println("Humidity overview produced. It can be found in: output/"+name+"/overviews/");
		}
	}
}
