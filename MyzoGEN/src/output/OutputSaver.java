package output;

import java.util.ArrayList;

/**
 * This class accepts an Output object and provides method to save the given Output in specified formats.
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
public class OutputSaver {
	
	enum OutputError {
		NO_OUTPUT_OBJECT("No output object was specified. Please use setOutput() before trying to save the output."),
		NO_MODE_SPECIFIED("No saving mode has been specified. Please use addMode() before trying to save the output.");
		
		private String err;
		
		OutputError(String er) {
			this.err = er;
		}
		
		public String throwError() {
			System.out.println(err);
			return err;
		}
	}

	private String name;
	private int chunksX, chunksY;
	
	private Output output;
	private ArrayList<SaveMode> save_modes = new ArrayList<SaveMode>();
	
	public OutputSaver(String name, int chunksX, int chunksY) {
		this.name = name;
		this.chunksX = chunksX;
		this.chunksY = chunksY;
	}
	
	/**
	 * This is the main method.
	 * Output will be saved in all specified formats.
	 * Be sure to use setOutput() and addMode() before calling save().
	 */
	public void save() {
		if (output == null) {
			throwError(OutputError.NO_OUTPUT_OBJECT);
			return;
		} else if (save_modes.size() <= 0) {
			throwError(OutputError.NO_MODE_SPECIFIED);
			return;
		} else {
			for (SaveMode mode : save_modes) {
				mode.save(output, name, chunksX, chunksY);
			}
		}
	}
	
	public void setOutput(Output out) {
		this.output = out;
	}
	
	public Output getOutput() {
		return output;
	}
	
	public void addMode(SaveMode mode) {
		if (!save_modes.contains(mode))
			save_modes.add(mode);
	}
	
	public void printModes() {
		for (SaveMode sm : save_modes) {
			System.out.println(sm.getIdent());
		}
	}
	
	private void throwError(OutputError error) {
		error.throwError();
	}
}
