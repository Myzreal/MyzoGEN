  +----------------------------------------------------------------------------+
  |                     	           MyzoGEN								   |
  | 				        by: Radoslaw Skupnik, aka Myzreal		   		   |
  |																               |
  |						             version: 1.0						       |
  +----------------------------------------------------------------------------+
  
  ================================ HOW TO USE ==================================
  MyzoGEN class is the main and only entrance to the generator.
  It acts as an interface and takes a great load of parameters in the constructor.
  These parameters decide how the generator will act.
  For more details, see the MyzoGEN's constructor head commentary.
  Calling the constructor will automatically start the generation and output will
  be printed to the console, more or less detailed depending on your preferences.
  It is up to you how you use it.
  This demo file just calls it from the code, without
  taking any parameters from the user. You can create a console application that
  will take the parameters or a visual UI or any other method of gather input - 
  later you just need to pass it in the constructor of MyzoGEN and
  it will work.
  Output will be saved in the "output/<world_name>/" folder.
  
  ================================ HOW IT WORKS ================================
  At the start the generator creates an empty Output object and fills it with
  default-state tiles (floor 1, grass, temperature and humidity 0, etc.).
  The tiles are generated in chunks of 32x32 pixels/tiles.
  The generator is divided into segments and each of the segment will fill the
  Output object with relevant data. Each segment has an IOFlags object that
  provides 3 booleans: PRODUCE, SAVE and LOAD. The PRODUCE flag will make the
  segment produce data. SAVE will save the data as a visual image so you can see
  how it looks like. Note that the image also contains all the produced data
  therefore it can be later loaded - that is what LOAD is here for. Note that LOAD
  and PRODUCE cannot be both ON and SAVE cannot be ON without PRODUCE. If you don't
  comply to these rules you will get an "IOFlags object is corrupted" error with a number.
  See Utils.flagCheck() to find out what does that number mean.
  The segments are as follows:
  	* Heightmap is generated first - fills the Output with height information AND
     floor information, according to the FloorSettings.
    * An optional FloorsOverview segment can be ran which will produce an overview
     of the floors. It does NOT fill the Output with any data.
    * Temperature is generated next, fills the Output data with information about
     temperature. It's generated from a separate noise function, therefore as to
     not be identical to the heightmap, using a different seed is advised. Also
     using low octaves and low frequency is advised. Temperature range is specified
     by a constant MyzoGEN.TEMPERATURE_RANGE. See the comment on that
     constant for more details on how it works.
    * Humidity is basically identical to Temperature and all the notes on temperature
     apply to humidity as well.
    * Biomes are created using 3 elements: temperature, humidity and the biomes diagram.
     Biomes diagram is the biomes.png file found in the project's main folder.
     For more details on how it works see the section "BIOMES DIAGRAM" below.
    * Rivers and lakes are produced later. Lakes are very simple - each tile below certain
     height is marked as a water tile. Rivers are generated using an A algorithm, which
     can be found in the astar package. They start at higher places (you can specify that)
     and seek their way towards the nearest static water, taking height into account.
     Rivers are stopped if they join another river or reach the map boundaries.
    * The final segment decides on how the map will be saved. As you can decide at each
     segment if it will produce a visual image, this final segment takes the Output object
     with all its data and saves it in the specified formats.
     
 ======================= HOW TO CREATE YOUR OWN SAVING FORMAT======================
  1) Edit the output.SaveMode abstract class and add your own IDENT to the enum there. Like this:
  		public enum IDENT {
			OWF_FORMAT, OW_FORMAT, YOUR_IDENT;
		}
		
  2) Create a new .java file in the output package, name it how you want and make it
     extends the SaveMode class. Override the save() method and the constructor.
     Make sure that your class constructor calls super(IDENT.YOUR_IDENT);
     
  3) All that you need is passed in the save() method parameters. You have an Output object there.
  	  You can access the tiles by calling Output.getTile() or Output.getTilesArray().
     Write your code in the created class that will save the Output in your format.
     When in doubt, checkout my OwMode class that saves the output in .ow format.
     
  4) Add your save mode when calling MyzoGEN(). It's the last parameter. Like this:
		new SaveMode[] {					
					new OwMode(						
						"Map created by Myzreal.",  
						true),
					new YourMode(
						Param1,
						Param2,
						...
						ParamX)
				});
     
 ================================ BIOMES DIAGRAM ================================
  Biomes diagram is the biomes.png file that can be found in resources
  /src/resources/ inside jar. It acts as a diagram. It should always be set
  to the range value of temperature and humidity (default 41). NOTE: CHANGING THIS
  WILL CURRENTLY BREAK THE GENERATOR. The vertical dimension of the image servers as
  humidity - ranging from -20 at the lower-left to 20 at the upper-left. Temperature is
  horizontal - from 20 at lower-left to -20 at lower-right. The most humid and hot spot
  is in the upper-left corner and the most dry and cold in the lower-right.
  The biomes generator takes a value of temperature and humidity that range between
  -20 and 20 (so 40 values) and picks the according pixel from the diagram image.
  Based on the pixel color (specified in Biomes class) a biome is decided.
  This allows to easily change the biomes while visualising it at the same time by
  altering the pixels in the biomes.png file.
  WARNING: The pixels' colors must be EXACTLY those that are specified in Biomes class.
  
  ============================ CURRENT HARD CAPS ================================
  Rivers amount cannot be larger than 255.
  Temperature and humidity ranges should not be changed.
  
  =========================== THIRD PARTY LIBRARIES ==============================
  Apache Commons IO - http://commons.apache.org/proper/commons-io/
  Libnoise - http://libnoise.sourceforge.net/
  
  ===================================== LICENSE =================================
  Copyright 2013 Radoslaw Skupnik.
  
  This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
  
  @author Radoslaw Skupnik, aka "Myzreal"
