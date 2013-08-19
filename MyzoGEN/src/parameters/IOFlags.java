package parameters;

/**
 * * Distinguish flags: PRODUCE, IMAGE and LOAD:
 * 		* PRODUCE - the output is produced. This excludes the LOAD flag.
 * 		* SAVE - the output is saved as an image. Works only if PRODUCE is true.
 * 		* LOAD - saved images are loaded and an output is created based on them. This excludes the PRODUCE flag and SAVE flag.
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
public class IOFlags {
	
	public boolean PRODUCE = false;
	public boolean SAVE = false;
	public boolean LOAD = false;
	
	public IOFlags(boolean p, boolean s, boolean l) {
		PRODUCE = p;
		SAVE = s;
		LOAD = l;
	}

}
