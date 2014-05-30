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
 * OWF format stands for OutlanderWorldFragmented format.
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
 * |   descr.	|   String	| string+2	| additional descr. by creator				|
 * +>>>>-------------------------- CHUNKS --------------------------------------<<<<+
 * | ident.		|	byte	|	 8b		|  an iden. that a new chunk starts: 0xFE	|
 * | pos_x		|	short	|	 16b	|	Point.x position						|
 * | pos_y		|	short	|	 16b	|	Point.y position						|
 * +>>>>>>>>----------------------- TILES ----------------------------------<<<<<<<<+
 * +----------------- this information is always present ---------------------------+
 * | marker		|	byte	|	 8b		|		0xFF								|
 * | pos_x		|	short	|	 16b	|	Point.x position						|
 * | pos_y		|	short	|	 16b	|	Point.y position						|
 * +-this info is only present if FLOOR != 1 && BIOME != MODERATE && TYPE != GRASS--+
 * +----------- if compression is false then this info is always present -----------+
 * | floor		|	byte	|	 8b		|  a byte indicating the floor				|
 * | biome		|	byte	|	 8b		|  a byte indicating the biome				|
 * | type		|	byte	|    8b		|  a byte indicating the tile type			|
 * | borderF1	|	byte	|	 8b		|  a byte indicating border flag one		| < New in V2
 * | borderF2	|	byte	|	 8b		|  a byte indicating border flag two		| < New in V2
 * +--------------------------------------------------------------------------------+
 * 
 * 
 * 
 * 
 * >>>>>>>>>>>>>>> OLD (VERSION 1) <<<<<<<<<<<<<<<<<<<<
 * \ type ...\
 * | edge		|	byte	|	 8b		|  the edge border for biomes				|
 * | corner		|	byte	|	 8b		|  the corner border for biomes				|
 * | b_type		|	byte	|	 8b		|  the border type for biomes				|
 * | edge		|	byte	|	 8b		|  the edge border for heights				|
 * | corner		|	byte	|	 8b		|  the corner border for heights			|
 * | b_type		|	byte	|	 8b		|  the border type for heights				|
 * +--------------------------------------------------------------------------------+
 * >>>>>>>>>>>>>>>> /OLD <<<<<<<<<<<<<<<<<<<<<<<
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
public class OwfMode extends SaveMode {

	private final short VERSION = 2;
	
	private String filepath;
	private ByteBuffer tempBuffer;
	
	private String description;
	
	public OwfMode(String description) {
		super(IDENT.OWF_FORMAT);
		this.description = description;
	}

	@Override
	public void save(Output output, String name, int chunksX, int chunksY) {
		this.filepath = "output/"+name+"/build/"+name+".owf";
		
		System.out.println(":: OutlanderWorldFragmented format (.owf format) ::");
		System.out.println(": The file will be located in: "+filepath);
		
		File f = new File(filepath);
		if (f.exists()) f.delete();
		
		int dimx = chunksX * MyzoGEN.DIMENSION_X;
		int dimy = chunksY * MyzoGEN.DIMENSION_Y;
		tempBuffer = ByteBuffer.allocate(calcSize(name, description, chunksX, chunksY, output));
		
		//Header
		writeShort(VERSION);
		writeString(name);
		writeInt(dimx);
		writeInt(dimy);
		writeString(description);
		
		for (int j = 0; j < chunksY; j++) {
			for (int i = 0; i < chunksX; i++) {
				writeChunk(i, j, output);
			}
		}
		
		push();
	}
	
	/**
	 * Writes a chunk to the file.
	 * @param x
	 * @param y
	 * @param output
	 */
	private void writeChunk(int x, int y, Output output) {
		writeByte((byte) 0xFE);
		writePoint(new Point(x, y));
		for (int j = 0; j < MyzoGEN.DIMENSION_Y; j++) {
			for (int i = 0; i < MyzoGEN.DIMENSION_X; i++) {
				Tile t = output.getTile(new Point((x*MyzoGEN.DIMENSION_X)+i, (y*MyzoGEN.DIMENSION_Y)+j));
				if (t != null) {
					writeTile(t);
				} else {
					System.out.println("OWF FORMAT CRITICAL ERROR: Tile not found at "+(new Point((x*MyzoGEN.DIMENSION_X)+i, (y*MyzoGEN.DIMENSION_Y)+j)));
					return;
				}
			}
		}
	}
	
	/**
	 * Writes a tile to the file.
	 * @param tile
	 */
	private void writeTile(Tile tile) {
		writeByte((byte) 0xFF);
		writePoint(tile.origin);
		writeByte((byte) tile.floor);
		writeByte(tile.biome);
		//if (tile.wall)
			//writeByte(Tiles.STONE_WALL);		//HARDCODED: if the tile is a wall it's type is set to 32.
		//else
		writeByte(tile.tile);
		writeByte(tile.borderFlagOne);
		writeByte(tile.borderFlagTwo);
		/**writeByte(tile.borderBiomesEdges);
		writeByte(tile.borderBiomesCorners);
		writeByte(tile.borderBiomesType);
		writeByte(tile.borderHeightEdges);
		writeByte(tile.borderHeightCorners);
		writeByte(tile.borderHeightType);**/
	}
	
	private void writeBoolean(boolean input) {
		if (input)
			tempBuffer.put((byte) 1);
		else
			tempBuffer.put((byte) 0);
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
	
	/**
	 * Calculates the size of the file.
	 * Necessary for the ByteBuffer to allocate sufficient memory.
	 * @param name
	 * @param desc
	 * @param output
	 * @return
	 */
	private int calcSize(String name, String desc, int chunksX, int chunksY, Output output) {
		int out = 0;
		
		//Header
		out += 2;
		out += name.length();
		out += 2;
		out += 4;
		out += 4;
		out += 1;
		out += desc.length();
		out += 2;
		
		//Chunks and tiles
		out += (chunksX * chunksY) * 5;
		for (Tile tile : output.getTilesArray().values()) {
			out += 5;
			out += 5;
		}
		
		return out;
	}
}
