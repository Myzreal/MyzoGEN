package generators;

import java.awt.image.BufferedImage;

import main.MyzoGEN;
import module.Perlin;
import other.Gradient;
import parameters.FloorSettings;
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
public class FloorsOverviewGenerator {
	
	private String name;
	private int chunksX, chunksY;
	private Perlin noise;
	private FloorSettings floorSet;
	private boolean details;

	public FloorsOverviewGenerator(String name, int chunksX, int chunksY, Perlin noise, FloorSettings fSet, boolean details) {
		this.name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.noise = noise;
		this.floorSet = fSet;
		this.details = details;
	}
	
	public String generate() {
		if (details)
			System.out.println("Producing floors overview. It can be found in: output/"+name+"/overviews");
		
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
		
		ImageCafe image = null;
		try {
            RendererImage render = new RendererImage();
            image = new ImageCafe(dimx, dimy);
            render.setSourceNoiseMap(overviewnoise);
            render.setDestImage(image);
            render.clearGradient();
            Gradient gradient = new Gradient(floorSet);
            for (int i = 0; i < gradient.gradientPoints.length; i++) {
            	render.addGradientPoint(gradient.gradientPoints[i], gradient.gradientColors[i]);
            }
            render.render();
        } catch (ExceptionInvalidParam ex) {
            ex.printStackTrace();
        }
		
		BufferedImage im = Utils.imageCafeToBufferedImage(dimx, dimy, image);
		
		Utils.saveImage(im, "output/"+name+"/overviews/floors_overview.png");

        if (details)
        	System.out.println("Floors overview saved.");
        
        return "No errors.";
	}
}
