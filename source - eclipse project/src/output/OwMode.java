package output;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import main.MyzoGEN;

import org.apache.commons.io.FileUtils;

import other.Biomes;
import other.Point;
import other.Tile;
import other.Tiles;

/**
 * OW format stands for OutlanderWorld format.
 * NOTES:
 * 		* String is preceeded by a short value indicating the following string's length.
 * 
 * The file format is as following:
 * +--------------------------------------------------------------------------------+
 * |	LABEL	|	TYPE	|	LENGTH	|					INFO					|
 * +------------+-----------+-----------+-------------------------------------------+
 * +------------------------------ HEADER ------------------------------------------+
 * |   version	|	short	|	 16b	|  version of the file						|
 * | world_name	|	String	| string+2  |  name of the world.						|
 * | world_width|	int		|	 32b	|	width of the world						|
 * |world_height|	int		|	 32b	|	height of the world						|
 * |compressed	|	bool	|	 16b	|	whether compression is applied
 * |   descr.	|   String	| string+2	| additional descr. by creator				|
 * +-------------------------------- TILES -----------------------------------------+
 * +----------------- THIS INFORMATION IS ALWAYS PRESENT ---------------------------+
 * | marker		|	byte	|	 8b		|		0xFF								|
 * | pos_x		|	short	|	 16b	|	Point.x position						|
 * | pos_y		|	short	|	 16b	|	Point.y position						|
 * +-THIS INFO IS ONLY PRESENT IF FLOOR != 1 && BIOME != MODERATE && TYPE != GRASS--+
 * +----------- if compression is false then this info is always present -----------+
 * | floor		|	byte	|	 8b		|  a byte indicating the floor				|
 * | biome		|	byte	|	 8b		|  a byte indicating the biome				|
 * | type		|	byte	|    8b		|  a byte indicating the tile type			|
 * +--------------------------------------------------------------------------------+
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
public class OwMode extends SaveMode {

	private final short VERSION = 1;
	
	private String filepath;
	private ByteBuffer tempBuffer;
	
	private String description;
	private boolean compression;
	
	public OwMode(String description, boolean compression) {
		super(IDENT.OW_FORMAT);
		this.description = description;
		this.compression = compression;
	}

	@Override
	public void save(Output output, String name, int chunksX, int chunksY) {
		this.filepath = "output/"+name+"/build/"+name+".ow";
		
		System.out.println(":: OutlanderWorld format (.ow format) ::");
		System.out.println(": The file will be located in: "+filepath);
		
		File f = new File(filepath);
		if (f.exists()) f.delete();
		
		int dimx = chunksX * MyzoGEN.DIMENSION_X;
		int dimy = chunksY * MyzoGEN.DIMENSION_Y;
		
		tempBuffer = ByteBuffer.allocate(calcSize(name, description, output));
		writeShort(VERSION);
		writeString(name);
		writeInt(dimx);
		writeInt(dimy);
		writeBoolean(compression);
		writeString(description);
		
		for (Tile tile : output.getTilesArray().values()) {
			writeByte((byte) 0xFF);
			writePoint(tile.origin);
			if (compression) {
				if (!(tile.floor == 1 && tile.biome == Biomes.MODERATE && tile.tile == Tiles.GRASS)) {
					writeByte((byte) tile.floor);
					writeByte(tile.biome);
					writeByte(tile.tile);
				}
			} else {
				writeByte((byte) tile.floor);
				writeByte(tile.biome);
				writeByte(tile.tile);
			}
		}
		
		push();
	}
	
	private void writeBoolean(boolean input) {
		if (input)
			tempBuffer.put((byte) 0);
		else
			tempBuffer.put((byte) 1);
	}
	
	private void writeByte(byte input) {
		tempBuffer.put(input);
	}
	
	private void writeShort(short input) {
		tempBuffer.putShort(input);
	}
	
	private void writeInt(int input) {
		tempBuffer.putInt(input);
	}
	
	private void writeString(String string) {
		tempBuffer.putShort((short) string.length());
		tempBuffer.put(string.getBytes());
	}
	
	private void writePoint(Point input) {
		tempBuffer.putShort((short) input.x);
		tempBuffer.putShort((short) input.y);
	}
	
	private void push() {
		byte[] tempBytes = tempBuffer.array();
		try {
			FileUtils.writeByteArrayToFile(new File(filepath), tempBytes, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tempBytes = null;
		tempBuffer = null;
	}
	
	private int calcSize(String name, String desc, Output output) {
		int out = 0;
		out += 2;
		out += name.length();
		out += 2;
		out += 4;
		out += 4;
		out += 1;
		out += desc.length();
		out += 2;
		for (Tile tile : output.getTilesArray().values()) {
			out += 5;
			if (compression) {
				if (!(tile.floor == 1 && tile.biome == Biomes.MODERATE && tile.tile == Tiles.GRASS)) {
					out += 3;
				}
			} else out += 3;
		}
		return out;
	}
}
