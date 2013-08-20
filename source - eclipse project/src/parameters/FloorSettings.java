package parameters;

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
public class FloorSettings {

	public double[] floorLevels = {-0.4550, 0.5000, 0.6000, 0.7000, 0.8000, 0.9000, 0.9500};
	
	/**
	 * The default constructor.
	 */
	public FloorSettings() {
		
	}
	
	/**
	 * A custom constructor. Keep in mind that you must specify exactly 7 floors.
	 * The parameters must be between -1 and 1
	 * Anything below the first parameter will be water. After that you specify floors.
	 * Everything above the last parameter will be snow.
	 * @param params
	 */
	public FloorSettings(double[] params) {
		if (floorLevels.length != 7) {
			System.out.println("CRITICAL ERROR: FloorSettings must contain exactly 7 parameters.");
			return;
		}
		
		for (double d : floorLevels) {
			if (d < -1 || d > 1) {
				System.out.println("CRITICAL ERROR: FloorSettings parameters must be between -1 and 1.");
				return;
			}
		}
		
		floorLevels = params;
	}
}
