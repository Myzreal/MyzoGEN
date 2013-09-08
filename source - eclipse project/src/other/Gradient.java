package other;

import parameters.FloorSettings;
import util.ColorCafe;

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
public class Gradient {
	
	public double[] gradientPoints;
	public ColorCafe[] gradientColors;
	
	public Gradient(FloorSettings fSet) {
		gradientPoints = new double[16];
		gradientColors = new ColorCafe[16];
		gradientPoints[0] = -1.0000;
		gradientColors[0] = new ColorCafe(0, 0, 0, 255);
		gradientPoints[1] = fSet.floorLevels[0] - 0.0001d;
		gradientColors[1] = new ColorCafe(0, 0, 0, 255);
		gradientPoints[2] = fSet.floorLevels[0];
		gradientColors[2] = new ColorCafe(144, 144, 144, 255);
		gradientPoints[3] = fSet.floorLevels[1] - 0.0001;
		gradientColors[3] = new ColorCafe(144, 144, 144, 255);
		gradientPoints[4] = fSet.floorLevels[1];
		gradientColors[4] = new ColorCafe(160, 160, 160, 255);
		gradientPoints[5] = fSet.floorLevels[2] - 0.0001;
		gradientColors[5] = new ColorCafe(160, 160, 160, 255);
		gradientPoints[6] = fSet.floorLevels[2];
		gradientColors[6] = new ColorCafe(176, 176, 176, 255);
		gradientPoints[7] = fSet.floorLevels[3] - 0.0001;
		gradientColors[7] = new ColorCafe(176, 176, 176, 255);
		gradientPoints[8] = fSet.floorLevels[3];
		gradientColors[8] = new ColorCafe(192, 192, 192, 255);
		gradientPoints[9] = fSet.floorLevels[4] - 0.0001;
		gradientColors[9] = new ColorCafe(192, 192, 192, 255);
		gradientPoints[10] = fSet.floorLevels[4];
		gradientColors[10] = new ColorCafe(208, 208, 208, 255);
		gradientPoints[11] = fSet.floorLevels[5] - 0.0001;
		gradientColors[11] = new ColorCafe(208, 208, 208, 255);
		gradientPoints[12] = fSet.floorLevels[5];
		gradientColors[12] = new ColorCafe(224, 224, 224, 255);
		gradientPoints[13] = fSet.floorLevels[6] - 0.0001;
		gradientColors[13] = new ColorCafe(224, 224, 224, 255);
		gradientPoints[14] = fSet.floorLevels[6];
		gradientColors[14] = new ColorCafe(255, 255, 255, 255);
		gradientPoints[15] = 1.0000;
		gradientColors[15] = new ColorCafe(255, 255, 255, 255);
	}
	
	public Gradient(String type) {
		if (type.equals("terrain")) {
			gradientPoints = new double[7];
			gradientColors = new ColorCafe[7];
			gradientPoints[0] = -1.0000;
		   	gradientColors[0] = new ColorCafe(0, 0, 120, 255);
		    gradientPoints[1] = -0.9000;
		 	gradientColors[1] = new ColorCafe(0, 0, 255, 255);
		    gradientPoints[2] = -0.8500;
		   	gradientColors[2] = new ColorCafe(0, 128, 255, 255); //shallow water
		   	//gradientPoints[3] = 0.0625;
		   	//gradientColors[3] = new ColorCafe(240, 240, 64, 255);
		   	gradientPoints[3] = -0.7000;//0.0625;		//grass, was 0.1250 before
		   	gradientColors[3] = new ColorCafe(32, 160, 0, 255); //grass
		   	//gradientPoints[5] = 0.37500;                          //dirt
		   	//gradientColors[5] = new ColorCafe(224, 224, 0, 255);
		   	gradientPoints[4] = 0.9700; //rock
		   	gradientColors[4] = new ColorCafe(128, 128, 128, 255);
		   	gradientPoints[5] = 0.9950; //snow
		   	gradientColors[5] = new ColorCafe(136, 136, 136, 255);
		   	gradientPoints[6] = 1.0000;
		   	gradientColors[6] = new ColorCafe(255, 255, 255, 255);
		} else if (type.equals("forest")) {
			gradientPoints = new double[3];
			gradientColors = new ColorCafe[3];
			gradientPoints[0] = -1.0000;
			gradientColors[0] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[1] = -0.0001;
			gradientColors[1] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[2] = 0.0000;
			gradientColors[2] = new ColorCafe(255, 255, 255, 255);
		} else if (type.equals("floors")) {
			gradientPoints = new double[17];
			gradientColors = new ColorCafe[17];
			gradientPoints[0] = -1.0000;
			gradientColors[0] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[1] = -0.7001;
			gradientColors[1] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[2] = -0.7000;
			gradientColors[2] = new ColorCafe(128, 128, 128, 255);
			gradientPoints[3] = -0.4551;
			gradientColors[3] = new ColorCafe(128, 128, 128, 255);
			gradientPoints[4] = -0.4550;
			gradientColors[4] = new ColorCafe(144, 144, 144, 255);
			gradientPoints[5] = -0.2101;
			gradientColors[5] = new ColorCafe(144, 144, 144, 255);
			gradientPoints[6] = -0.2100;
			gradientColors[6] = new ColorCafe(160, 160, 160, 255);
			gradientPoints[7] = 0.0349;
			gradientColors[7] = new ColorCafe(160, 160, 160, 255);
			gradientPoints[8] = 0.0350;
			gradientColors[8] = new ColorCafe(176, 176, 176, 255);
			gradientPoints[9] = 0.2799;
			gradientColors[9] = new ColorCafe(176, 176, 176, 255);
			gradientPoints[10] = 0.2800;
			gradientColors[10] = new ColorCafe(192, 192, 192, 255);
			gradientPoints[11] = 0.5249;
			gradientColors[11] = new ColorCafe(192, 192, 192, 255);
			gradientPoints[12] = 0.5250;
			gradientColors[12] = new ColorCafe(208, 208, 208, 255);
			gradientPoints[13] = 0.7699;
			gradientColors[13] = new ColorCafe(208, 208, 208, 255);
			gradientPoints[14] = 0.7700;
			gradientColors[14] = new ColorCafe(224, 224, 224, 255);
			gradientPoints[14] = 0.9949;
			gradientColors[14] = new ColorCafe(224, 224, 224, 255);
			gradientPoints[15] = 0.9950;
			gradientColors[15] = new ColorCafe(255, 255, 255, 255);
			gradientPoints[16] = 1.0000;
			gradientColors[16] = new ColorCafe(255, 255, 255, 255);
		} else if (type.equals("floors2")) {
			gradientPoints = new double[16];
			gradientColors = new ColorCafe[16];
			gradientPoints[0] = -1.0000;
			gradientColors[0] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[1] = -0.4551;
			gradientColors[1] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[2] = -0.4550;
			gradientColors[2] = new ColorCafe(144, 144, 144, 255);
			gradientPoints[3] = 0.4999;
			gradientColors[3] = new ColorCafe(144, 144, 144, 255);
			gradientPoints[4] = 0.5000;
			gradientColors[4] = new ColorCafe(160, 160, 160, 255);
			gradientPoints[5] = 0.5999;
			gradientColors[5] = new ColorCafe(160, 160, 160, 255);
			gradientPoints[6] = 0.6000;
			gradientColors[6] = new ColorCafe(176, 176, 176, 255);
			gradientPoints[7] = 0.6999;
			gradientColors[7] = new ColorCafe(176, 176, 176, 255);
			gradientPoints[8] = 0.7000;
			gradientColors[8] = new ColorCafe(192, 192, 192, 255);
			gradientPoints[9] = 0.7999;
			gradientColors[9] = new ColorCafe(192, 192, 192, 255);
			gradientPoints[10] = 0.8000;
			gradientColors[10] = new ColorCafe(208, 208, 208, 255);
			gradientPoints[11] = 0.8999;
			gradientColors[11] = new ColorCafe(208, 208, 208, 255);
			gradientPoints[12] = 0.9000;
			gradientColors[12] = new ColorCafe(224, 224, 224, 255);
			gradientPoints[13] = 0.9499;
			gradientColors[13] = new ColorCafe(224, 224, 224, 255);
			gradientPoints[14] = 0.9500;
			gradientColors[14] = new ColorCafe(255, 255, 255, 255);
			gradientPoints[15] = 1.0000;
			gradientColors[15] = new ColorCafe(255, 255, 255, 255);
		} else if (type.equals("temperature")) {
			gradientPoints = new double[2];
			gradientColors = new ColorCafe[2];
			gradientPoints[0] = -1.0000;
			gradientColors[0] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[1] = 1.0000;
			gradientColors[1] = new ColorCafe(255, 255, 255, 255);
		} else if (type.equals("humidity")) {
			gradientPoints = new double[2];
			gradientColors = new ColorCafe[2];
			gradientPoints[0] = -1.0000;
			gradientColors[0] = new ColorCafe(0, 0, 0, 255);
			gradientPoints[1] = 1.0000;
			gradientColors[1] = new ColorCafe(255, 255, 255, 255);
		}
	}

}
