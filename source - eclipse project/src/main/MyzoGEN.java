package main;

import generators.BaseTerrainGenerator;
import generators.BiomesGenerator;
import generators.FloorsOverviewGenerator;
import generators.HumidityGenerator;
import generators.NoiseGenerator;
import generators.RiversGenerator;
import generators.TemperatureGenerator;
import generators.TileBordersGenerator;

import java.io.File;
import java.util.concurrent.TimeUnit;

import module.Perlin;
import output.Output;
import output.OutputSaver;
import output.SaveMode;
import parameters.FloorSettings;
import parameters.IOFlags;

/**
 * +----------------------------------------------------------------------------+
 * |                     			MyzoGEN										|
 * | 					by: Radoslaw Skupnik, aka Myzreal						|
 * |																			|
 * |							version: 1.1									|
 * +----------------------------------------------------------------------------+
 * 
 * ================================ HOW TO USE ==================================
 * MyzoGEN class is the main and only entrance to the generator.
 * It acts as an interface and takes a great load of parameters in the constructor.
 * These parameters decide how the generator will act.
 * For more details, see the MyzoGEN's constructor head commentary.
 * Calling the constructor will automatically start the generation and output will
 * be printed to the console, more or less detailed depending on your preferences.
 * It is up to you how you use it.
 * 
 * ================================ HOW IT WORKS ================================
 * At the start the generator creates an empty Output object and fills it with
 * default-state tiles (floor 1, grass, temperature and humidity 0, etc.).
 * The tiles are generated in chunks of 32x32 pixels/tiles.
 * The generator is divided into segments and each of the segment will fill the
 * Output object with relevant data. Each segment has an IOFlags object that
 * provides 3 booleans: PRODUCE, SAVE and LOAD. The PRODUCE flag will make the
 * segment produce data. SAVE will save the data as a visual image so you can see
 * how it looks like. Note that the image also contains all the produced data
 * therefore it can be later loaded - that is what LOAD is here for. Note that LOAD
 * and PRODUCE cannot be both ON and SAVE cannot be ON without PRODUCE. If you don't
 * comply to these rules you will get an "IOFlags object is corrupted" error with a number.
 * See Utils.flagCheck() to find out what does that number mean.
 * The segments are as follows:
 * 	* Heightmap is generated first - fills the Output with height information AND
 *    floor information, according to the FloorSettings.
 *  * An optional FloorsOverview segment can be ran which will produce an overview
 *    of the floors. It does NOT fill the Output with any data.
 *  * Temperature is generated next, fills the Output data with information about
 *    temperature. It's generated from a separate noise function, therefore as to
 *    not be identical to the heightmap, using a different seed is advised. Also
 *    using low octaves and low frequency is advised. Temperature range is specified
 *    by a constant MyzoGEN.TEMPERATURE_RANGE. See the comment on that
 *    constant for more details on how it works.
 *  * Humidity is basically identical to Temperature and all the notes on temperature
 *    apply to humidity as well.
 *  * Biomes are created using 3 elements: temperature, humidity and the biomes diagram.
 *    Biomes diagram is the biomes.png file found in the project's main folder.
 *    For more details on how it works see the section "BIOMES DIAGRAM" below.
 *  * Rivers and lakes are produced later. Lakes are very simple - each tile below certain
 *    height is marked as a water tile. Rivers are generated using an A* algorithm, which
 *    can be found in the astar package. They start at higher places (you can specify that)
 *    and seek their way towards the nearest static water, taking height into account.
 *    Rivers are stopped if they join another river or reach the map boundaries.
 *  * The final segment decides on how the map will be saved. As you can decide at each
 *    segment if it will produce a visual image, this final segment takes the Output object
 *    with all its data and saves it in the specified formats.
 *    
 *================================ BIOMES DIAGRAM ================================
 * Biomes diagram is the biomes.png file that can be found in resources
 * /src/resources/ inside jar. It acts as a diagram. It should always be set
 * to the range value of temperature and humidity (default 41). NOTE: CHANGING THIS
 * WILL CURRENTLY BREAK THE GENERATOR. The vertical dimension of the image servers as
 * humidity - ranging from -20 at the lower-left to 20 at the upper-left. Temperature is
 * horizontal - from 20 at lower-left to -20 at lower-right. The most humid and hot spot
 * is in the upper-left corner and the most dry and cold in the lower-right.
 * The biomes generator takes a value of temperature and humidity that range between
 * -20 and 20 (so 40 values) and picks the according pixel from the diagram image.
 * Based on the pixel color (specified in Biomes class) a biome is decided.
 * This allows to easily change the biomes while visualising it at the same time by
 * altering the pixels in the biomes.png file.
 * WARNING: The pixels' colors must be EXACTLY those that are specified in Biomes class.
 * 
 * ============================ CURRENT HARD CAPS ================================
 * Rivers amount cannot be larger than 255.
 * Temperature and humidity ranges should not be changed.
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
 */
public class MyzoGEN {
	
	/**
	 * STATIC VARIABLES - DO NOT CHANGE.
	 */
	private static MyzoGEN _instance;
	public static final int DIMENSION_X = 32;
	public static final int DIMENSION_Y = 32;
	public static final int TEMPERATURE_RANGE = 20; //If set to 20 the temperature will range from -20 to 20.
	public static final int HUMIDITY_RANGE = 20; //Same as above and yes I know humidity can't get below 0 - it's for simplicity's sake :)
	
	/**
	 * Those are the variables that are passed in the constructor, in the same order.
	 * See OutlanderGenerator constructor for descriptions of what these do.
	 */
	private String  		_Name							= "Default";
	private int     		_WorldSizeChunksX    			= 0;
	private int     		_WorldSizeChunksY     			= 0;
	private int 			_BaseTerrainSeed				= 0;
	private int     		_BaseTerrainOctaves   			= 8;
	private double  		_BaseTerrainFrequency 			= 0.18;
	private IOFlags			_BaseTerrainIOFlags				= new IOFlags(false, false, false);
	private boolean			_BaseTerrainLogDetails			= false;
	private FloorSettings 	_FloorSettings					= new FloorSettings();
	private boolean 		_FloorsOverview 				= false;
	private int 			_TemperatureSeed				= 0;
	private int 			_TemperatureOctaves				= 1;
	private double			_TemperatureFrequency			= 0.18;
	private IOFlags			_TemperatureIOFlags				= new IOFlags(false, false, false);
	private boolean			_TemperatureLogDetails			= false;
	private int 			_HumiditySeed					= 5;
	private int 			_HumidityOctaves				= 1;
	private double			_HumidityFrequency				= 0.18;
	private IOFlags			_HumidityIOFlags				= new IOFlags(false, false, false);
	private boolean			_HumidityLogDetails				= false;
	private IOFlags			_BiomeIOFlags					= new IOFlags(false, false, false);
	private boolean			_BiomeLogDetails				= false;
	private int				_RiversMaxRivers				= 10;
	private int				_RiversMinFloor					= 6;
	private int				_RiversMinDist					= 30;
	private IOFlags			_RiversIOFlags					= new IOFlags(false, false, false);
	private boolean			_RiversLogDetails				= false;
	private IOFlags			_BordersIOFlags					= new IOFlags(false, false, false);
	private	boolean			_BordersLogDetails				= false;
	private SaveMode[]		_SaveModes						= null;
	
	/**
	 * Those variables hold all the generators needed.
	 */
	private NoiseGenerator			_NoiseGenerator				= null;
	private BaseTerrainGenerator	_BaseTerrainGenerator		= null;
	private FloorsOverviewGenerator _FloorsOverviewGenerator	= null;
	private TemperatureGenerator	_TemperatureGenerator		= null;
	private HumidityGenerator		_HumidityGenerator			= null;
	private BiomesGenerator			_BiomesGenerator			= null;
	private RiversGenerator			_RiversGenerator			= null;
	private TileBordersGenerator	_BordersGenerator			= null;
	
	/**
	 * Those variables hold the various data that can be produced.
	 */
	private Output						_Output				= null;
	private Perlin						_Noise				= null;
	private OutputSaver					_OutputSaver		= null;

	/**
	 * This is the only entrance to the generator.
	 * By calling this constructor with various parameters one can decide the shape of the generated world.
	 * 
	 * @param name - Name - name of the world, decides mainly on the how the output folder will be named.
	 * @oaram wscx - WorldSizeChunksX - defines the size of the world in X axis IN CHUNKS (each chunks is 32x32 pixels/tiles).
	 * @param wscy - WorldSizeChunksY - as above but in Y axis.
	 * @param seed - BaseTerrainSeed - seed of the terrain.
	 * @param bto -  BaseTerrainOctaves - decides the octave count of the base terrain, default 8
	 * @param btf  - BaseTerrainFrequency - the frequency of the base terrain, default 0.18
	 * @param btflags - BaseTerrainIOFlags - an IOFlags object deciding whether BaseTerrain should be produce, save and/or loaded.
	 * @param btld - BaseTerrainLogDetails - whether BaseTerrainGenerator should log details and display them.
	 * @param floorset - FloorSettings - a FloorSettings object that decides at what points specifics floors start and end. You can leave this as null for default set.
	 * @param floorsov - FloorsOverview - whether a FloorsOverview image should be generated. WARNING: will prolong generation.
	 * @param tseed - TemperatureSeed - the seed from which temperature map will be generated.
	 * @param toct - TemperatureOctaves - octaves for temperature generator, the best is 1.
	 * @param tfreq - TemperatureFrequency - it should be the same as BaseTerrainFrequency.
	 * @param tflags - TemperatureIOFlags - an IOFlags object determining whether temperature should be produced, saved and or loaded.
	 * @param tld - TemperatureLogDetails - whether details of temperature generation should be printed in the console.
	 * @param hseed - HumiditySeed - same as for temperature.
	 * @param hoct - HumidityOctaves - as above.
	 * @param hfreq - HumidityFrequency - as above.
	 * @param hflags - HumidityIOFlags - as above.
	 * @param hld - HumidityLogDetails - as above.
	 * @param bflags - BiomeIOFlags - IOFlags object for biomes.
	 * @param bld - BiomeLogDetails - log details of biomes creation?
	 * @param rmax - RiversMaxRivers - max amount of rivers
	 * @param rminf - RiversMinFloor - rivers will start at this floor or above it
	 * @param rmind - RiversMinDist - minimum distance between river origins
	 * @param rlflags - RiversIOFlags
	 * @param rld - RiversLogDetails
	 * @param boflags - BordersIOFlags - whether or not borders should be produced
	 * @param bold - BordersLogDetails
	 * @param modes - SaveModes - saving modes
	 */
	public MyzoGEN(String name, int wscx, int wscy, int seed, int bto, double btf, IOFlags btflags, boolean btld, FloorSettings floorset, boolean floorsov,
							  int tseed, int toct, double tfreq, IOFlags tflags, boolean tld, int hseed, int hoct, double hfreq, IOFlags hflags, boolean hld,
							  IOFlags bflags, boolean bld, int rmax, int rminf, int rmind, IOFlags rflags, boolean rld, IOFlags boflags, boolean bold, SaveMode[] modes) {
		_instance = this;
		
		_Name = name;
		_WorldSizeChunksX = wscx;
		_WorldSizeChunksY = wscy;
		_BaseTerrainSeed = seed;
		_BaseTerrainOctaves = bto;
		_BaseTerrainFrequency = btf;
		_BaseTerrainIOFlags = btflags;
		_BaseTerrainLogDetails = btld;
		if (floorset != null) _FloorSettings = floorset;
		_FloorsOverview = floorsov;
		_TemperatureSeed = tseed;
		_TemperatureOctaves = toct;
		_TemperatureFrequency = tfreq;
		_TemperatureIOFlags = tflags;
		_TemperatureLogDetails = tld;
		_HumiditySeed = hseed;
		_HumidityOctaves = hoct;
		_HumidityFrequency = hfreq;
		_HumidityIOFlags = hflags;
		_HumidityLogDetails = hld;
		_BiomeIOFlags = bflags;
		_BiomeLogDetails = bld;
		_RiversMaxRivers = rmax;
		_RiversMinFloor = rminf;
		_RiversMinDist = rmind;
		_RiversIOFlags = rflags;
		_RiversLogDetails = rld;
		_BordersIOFlags = boflags;
		_BordersLogDetails = bold;
		_SaveModes = modes;
		
		generate();
	}
	
	/**
	 * This is the main function of the generator.
	 * Once the input from the user has been collected by the constructor, this function takes over.
	 */
	private void generate() {
		long time = System.currentTimeMillis();
		
		System.out.println(":: COMMENCING GENERATION OF WORLD: "+_Name+" ::");
		prepareFolder();
		_Output = new Output(_WorldSizeChunksX * DIMENSION_X, _WorldSizeChunksY * DIMENSION_Y, true);
		System.out.println("");
		
		System.out.println(":: Generating noise ::");
		_NoiseGenerator = new NoiseGenerator(_BaseTerrainSeed, _BaseTerrainOctaves, _BaseTerrainFrequency);
		_Noise = _NoiseGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println("");
		
		System.out.println(":: Generating base terrain ::");
		_BaseTerrainGenerator = new BaseTerrainGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _Noise, _BaseTerrainIOFlags, _BaseTerrainLogDetails);
		String bterr = _BaseTerrainGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println(":: BaseTerrainGenerator result: "+bterr);
		if (bterr != "No errors.") return;
		System.out.println("");
		
		if (_FloorsOverview) {
			System.out.println(":: Generating floors overview ::");
			_FloorsOverviewGenerator = new FloorsOverviewGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _Noise, _FloorSettings, true); //HARDCODED floors overview details.
			String foerr = _FloorsOverviewGenerator.generate();
			System.out.println(":: Done! ::");
			System.out.println(":: FloorsOverviewGenerator result: "+foerr);
			if (foerr != "No errors.") return;
			System.out.println("");
		}
		
		System.out.println(":: Generating temperature ::");
		_TemperatureGenerator = new TemperatureGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _TemperatureSeed, _TemperatureOctaves, _TemperatureFrequency, _TemperatureIOFlags, _TemperatureLogDetails);
		String terr = _TemperatureGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println(":: TemperatureGenerator result: "+terr);
		if (terr != "No errors.") return;
		System.out.println("");
		
		System.out.println(":: Generating humidity ::");
		_HumidityGenerator = new HumidityGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _HumiditySeed, _HumidityOctaves, _HumidityFrequency, _HumidityIOFlags, _HumidityLogDetails);
		String herr = _HumidityGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println(":: HumidityGenerator result: "+herr);
		if (herr != "No errors.") return;
		System.out.println("");
		
		System.out.println(":: Generating biomes ::");
		_BiomesGenerator = new BiomesGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _BiomeIOFlags, _BiomeLogDetails);
		String berr = _BiomesGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println(":: BiomesGenerator result: "+berr);
		if (berr != "No errors.") return;
		System.out.println("");
		
		System.out.println(":: Generating rivers ::");
		_RiversGenerator = new RiversGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _RiversMaxRivers, _RiversMinFloor, _RiversMinDist, _RiversIOFlags, _RiversLogDetails);
		String rerr = _RiversGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println(":: RiversGenerator result: "+rerr);
		if (rerr != "No errors.") return;
		System.out.println("");
		
		System.out.println(":: Generating borders ::");
		_BordersGenerator = new TileBordersGenerator(_Name, _WorldSizeChunksX, _WorldSizeChunksY, _BordersIOFlags, _BordersLogDetails);
		String boerr = _BordersGenerator.generate();
		System.out.println(":: Done! ::");
		System.out.println(":: BordersGenerator result: "+boerr);
		if (boerr != "No errors.") return;
		System.out.println("");
		
		System.out.println("======= SAVING OUTPUT =======");
		_OutputSaver = new OutputSaver(_Name, _WorldSizeChunksX, _WorldSizeChunksY);
		for (SaveMode mode : _SaveModes) {
			_OutputSaver.addMode(mode);
		}
		_OutputSaver.setOutput(_Output);
		_OutputSaver.save();
		System.out.println("");
		
		_Output.debugCheck();
		
		long millis = System.currentTimeMillis() - time;
		System.out.println("Generation time: "+String.format("%d min, %d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(millis),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
			));
		
	}
	
	/**
	 * Prepares the output folder.
	 */
	private void prepareFolder() {
		File f = new File("output/"+_Name+"");
		f.mkdirs();
		
		f = new File("output/"+_Name+"/overviews");
		f.mkdirs();
	}
	
	public static MyzoGEN getInstance() {
		return _instance;
	}
	
	public static Output getOutput() {
		return _instance._Output;
	}
	
	public static FloorSettings getFloorSettings() {
		return _instance._FloorSettings;
	}
}
