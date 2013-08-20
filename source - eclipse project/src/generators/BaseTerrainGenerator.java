package generators;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.MyzoGEN;
import module.Perlin;
import other.Point;
import parameters.IOFlags;
import util.ImageCafe;
import util.NoiseMap;
import util.NoiseMapBuilderPlane;
import util.RendererImage;
import utils.Utils;
import exception.ExceptionInvalidParam;

/**
 * This class handles generating the base terrain heightmap.
 * You need to pass the necessary parameters in the constructor.
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
public class BaseTerrainGenerator {
	
	private String name;
	private int chunksX, chunksY;
	private IOFlags ioflags;
	private boolean details = false;
	private Perlin noise;

	public BaseTerrainGenerator(String name, int chunksX, int chunksY, Perlin noise, IOFlags flags, boolean details) {
		this.name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.ioflags = flags;
		this.details = details;
		this.noise = noise;
	}
	
	public String generate() {
		String error = "No errors.";
		
		if (details)
			System.out.println("Heightmap flags: PRODUCE="+ioflags.PRODUCE+", SAVE="+ioflags.SAVE+", LOAD="+ioflags.LOAD);
		
		if (Utils.flagsCheck(ioflags) != 0) {
			System.out.println("ERROR: IOFlags object is corrupted, error: "+Utils.flagsCheck(ioflags));
			return "BaseTerrainGenerator critical error - read above.";
		}
		
		if (ioflags.PRODUCE)
			produceHeightmap();
		
		if (ioflags.LOAD) {
			error = loadHeightmap();
		}
		
		return error;
	}
	
	private String loadHeightmap() {
		if (details) System.out.println("Loading the heightmap.");
		
		File imageFile = new File("output/"+name+"/overviews/heightmap_overview.png");
		if (imageFile.exists()) {
			try {
				BufferedImage heightmapImage = ImageIO.read(imageFile);
				if (heightmapImage.getWidth() == chunksX * MyzoGEN.DIMENSION_X && heightmapImage.getHeight() == chunksY * MyzoGEN.DIMENSION_Y) {
					for (int i = 0; i < chunksX * MyzoGEN.DIMENSION_X; i++) {
						for (int j = 0; j < chunksY * MyzoGEN.DIMENSION_Y; j++) {
							Color pixel = new Color(heightmapImage.getRGB(i, j));
							double height = Utils.pixelToHeight(pixel);
							MyzoGEN.getOutput().setHeightAndFloor(new Point(i, j), Utils.roundToDecimals(height, 4));
						}
					}
				} else return "BaseTerrainGenerator critical error - the loaded heightmap does not match the specified dimensions. Loaded file is: "+heightmapImage.getWidth()+"x"+heightmapImage.getHeight()+
						"while the specified dimensions are: "+(chunksX * MyzoGEN.DIMENSION_X)+"x"+(chunksY * MyzoGEN.DIMENSION_Y);
			} catch (IOException e) {
				e.printStackTrace();
				return "BaseTerrainGenerator critical error - LOAD option is on but I can't load the file.";
			}
		} else return "BaseTerrainGenerator critical error - LOAD option is on but I can't find the heightmap_overview.png file.";
		
		return "No errors.";
	}
	
	private void produceHeightmap() {
		if (details)
			System.out.println("Producing overview. It can be found in: output/"+name+"/overviews.");
		
		int dimx = MyzoGEN.DIMENSION_X * chunksX;
		int dimy = MyzoGEN.DIMENSION_Y * chunksY;
		NoiseMap overviewnoise = null;
		try {
			overviewnoise = new NoiseMap(dimx, dimy);
			NoiseMapBuilderPlane builder = new NoiseMapBuilderPlane();
			builder.setSourceModule(noise);
			builder.setDestNoiseMap(overviewnoise);
            builder.setDestSize(dimx, dimy);
            builder.setBounds(0, chunksX * 4, 0, chunksY*4);
            builder.build();
		} catch (Exception e) {e.printStackTrace();}
		
		for (int ii = 0; ii < dimx; ii++) {
			for (int jj = 0; jj < dimy; jj++) {
				MyzoGEN.getOutput().setHeightAndFloor(new Point(ii, jj), Utils.roundToDecimals(overviewnoise.getValue(ii, jj), 4));
			}
		}
		
		ImageCafe image = null;
		try {
            RendererImage render = new RendererImage();
            image = new ImageCafe(dimx, dimy);
            render.setSourceNoiseMap(overviewnoise);
            render.setDestImage(image);
            render.render();
        } catch (ExceptionInvalidParam ex) {
            ex.printStackTrace();
        }
		
		BufferedImage im = Utils.imageCafeToBufferedImage(dimx, dimy, image);
		
		Utils.saveImage(im, "output/"+name+"/overviews/heightmap_overview.png");

        if (details)
        	System.out.println("Heightmap overview saved.");
	}
}
