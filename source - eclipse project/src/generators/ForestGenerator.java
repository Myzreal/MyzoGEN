package generators;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

import main.MyzoGEN;
import module.Perlin;
import other.Biomes;
import other.Gradient;
import other.Point;
import other.Tile;
import other.Tiles;
import other.gTree;
import parameters.IOFlags;
import util.ImageCafe;
import util.NoiseMap;
import util.NoiseMapBuilderPlane;
import util.RendererImage;
import utils.Utils;
import exception.ExceptionInvalidParam;

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
public class ForestGenerator {

	private String name;
	private int chunksX, chunksY, seed, octaves;
	private double frequency;
	private int baseChanceForest, baseChanceNonForest;
	private IOFlags ioflags;
	private boolean details;
	
	public ForestGenerator(String name, int chunksX, int chunksY, int seed, int octaves, double freq, int bchf, int bchnf, IOFlags flags, boolean details) {
		this.name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.seed = seed;
		this.octaves = octaves;
		this.frequency = freq;
		this.ioflags = flags;
		this.details = details;
		this.baseChanceForest = bchf;
		this.baseChanceNonForest = bchnf;
	}
	
	public String generate() {
		String error = "No errors.";
		
		if (details)
			System.out.println("Forest flags: PRODUCE="+ioflags.PRODUCE+", SAVE="+ioflags.SAVE+", LOAD="+ioflags.LOAD);
		
		if (Utils.flagsCheck(ioflags) != 0) {
			System.out.println("ERROR: IOFlags object is corrupted, error: "+Utils.flagsCheck(ioflags));
			return "ForestGenerator critical error - read above.";
		}
		
		if (ioflags.PRODUCE) {
			produceForest();
			produceTrees();
		}
		
		if (ioflags.LOAD)
			error = "Not yet available.";
		
		return error;
	}
	
	private void produceForest() {
		Perlin noise = new Perlin();
		try { noise.setOctaveCount(octaves); } catch (Exception e) {e.printStackTrace();}
		noise.setFrequency(frequency);
		noise.setSeed(seed);
		
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
				if (tempMap.getValue(ii, jj) > 0)
					MyzoGEN.getOutput().setForestArea(new Point(ii, jj));
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
	            Gradient gradient = new Gradient("forest");
	            for (int i = 0; i < gradient.gradientPoints.length; i++) {
	            	render.addGradientPoint(gradient.gradientPoints[i], gradient.gradientColors[i]);
	            }
	            render.render();
	        } catch (ExceptionInvalidParam ex) {
	            ex.printStackTrace();
	        }
			
			BufferedImage im = Utils.imageCafeToBufferedImage(dimx, dimy, image);
			
			Utils.saveImage(im, "output/"+name+"/overviews/forests_overview.png");
			
			if (details) System.out.println("Forests overview produced. It can be found in: output/"+name+"/overviews/");
		}
	}
	
	private void produceTrees() {
		BufferedImage treesImage = null;
		if (ioflags.SAVE)
			treesImage = new BufferedImage(chunksX * MyzoGEN.DIMENSION_X, chunksY * MyzoGEN.DIMENSION_Y, BufferedImage.TYPE_INT_ARGB);
		
		Random rnd = new Random();
		int dimx = MyzoGEN.DIMENSION_X * chunksX;
		int dimy = MyzoGEN.DIMENSION_Y * chunksY;
		for (int x = 0; x < dimx; x++) {
			for (int y = 0; y < dimy; y++) {
				Tile tile = MyzoGEN.getOutput().getTile(new Point(x, y));
				if (tile != null && !tile.river && tile.tile != Tiles.WATER) {
					int chance = 0;
					if (tile.biome == Biomes.DESERT) {
						chance = 1;
					} else if (tile.biome == Biomes.SAVANNAH) {
						chance = 4;
					} else {
						chance = tile.isForestArea ? baseChanceForest : baseChanceNonForest;
						if (tile.origin.x == 670 && tile.origin.y == 95)
							System.out.println("Non forest area base chance: "+chance);
						else if (tile.origin.x == 628 && tile.origin.y == 119)
							System.out.println("Forest area base chance: "+chance);
						chance += Biomes.getVegetationModifier(tile.biome);
					}
					
					if (rnd.nextInt(100) < chance) {
						tile.object = new gTree();
						
						if (ioflags.SAVE)
							treesImage.setRGB(x, y, new Color(0, 255, 0, 255).getRGB());
					}
				}
			}
		}
		
		if (ioflags.SAVE && treesImage != null) {
			Utils.saveImage(treesImage, "output/"+name+"/overviews/trees_overview.png");
			if (details) System.out.println("Trees overview produced. It can be found in: output/"+name+"/overviews/");
		}
	}
}
