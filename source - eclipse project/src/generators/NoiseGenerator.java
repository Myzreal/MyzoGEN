package generators;

import module.Perlin;

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
public class NoiseGenerator {

	private int seed;
	private int octaves;
	private double frequency;
	
	public NoiseGenerator(int seed, int octaves, double frequency) {
		this.seed = seed;
		this.octaves = octaves;
		this.frequency = frequency;
	}
	
	public Perlin generate() {
		Perlin result = new Perlin();
		try { result.setOctaveCount(octaves); } catch (Exception e) {e.printStackTrace();}
		result.setFrequency(frequency);
		result.setSeed(seed);
		return result;
	}
}
