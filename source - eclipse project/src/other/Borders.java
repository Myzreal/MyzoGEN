package other;

import utils.Utils;

public class Borders {

	public static final byte UP = 1;
	public static final byte LEFT = 2;
	public static final byte DOWN = 3;
	public static final byte RIGHT = 4;
	
	public static final byte UP_LEFT = 5;
	public static final byte UP_RIGHT = 6;
	public static final byte DOWN_RIGHT = 7;
	public static final byte DOWN_LEFT = 8;
	
	public static final byte UP_EMPTY = 9;
	public static final byte RIGHT_EMPTY = 10;
	public static final byte DOWN_EMPTY = 11;
	public static final byte LEFT_EMPTY = 12;
	
	public static final byte UP_LEFT_CORNER = 13;
	public static final byte UP_RIGHT_CORNER = 14;
	public static final byte DOWN_RIGHT_CORNER = 15;
	public static final byte DOWN_LEFT_CORNER = 16;
	
	public static final byte DL_DR_CORNER = 17;
	public static final byte UL_DL_CORNER = 18;
	public static final byte UL_UR_CORNER = 19;
	public static final byte UR_DR_CORNER = 20;
	
	public static final byte UL_CORNER_EMPTY = 21;
	public static final byte UR_CORNER_EMPTY = 22;
	public static final byte DR_CORNER_EMPTY = 23;
	public static final byte DL_CORNER_EMPTY = 24;
	
	public static final byte UD_EMPTY = 25;
	public static final byte LR_EMPTY = 26;
	public static final byte UL_DR_CORNER_EMPTY = 27;
	public static final byte UR_DL_CORNER_EMPTY = 28;
	public static final byte SURROUNDED = 29;
	public static final byte SURROUNDED_CORNER = 30;
	
	public static byte calculateBiomeBorder(Tile tile, Point[] surrPoints) {
		byte output = 0;
		Tile[] sTiles = Utils.surroundingPointsToTiles(surrPoints);		// Pattern produced by this method matches the encoding.
		for (int i = 0; i < sTiles.length; i++) {
			Tile sTile = sTiles[i];
			// Higher precedence tiles overlap the lower precedence ones, so we are only interested in higher precedence tiles surrounding the chosen tile.
			if (tile != null && sTile != null && 									// If both exist &...
				Tiles.getPrecedence(sTile.tile) > Tiles.getPrecedence(tile.tile) && // ...higher precedence &...
				sTile.tile != tile.tile) {											// ...different type.
				output = setBit(output, i, 1);		// "i" can be simply used here because the pattern produced by Utils is compatible.
			}
		}
		return output;
	}
	
	public static byte calculateHeightBorder(Tile tile, Point[] surrPoints) {
		byte output = 0;
		Tile[] sTiles = Utils.surroundingPointsToTiles(surrPoints);		// Pattern produced by this method matches the encoding.
		for (int i = 0; i < sTiles.length; i++) {
			Tile sTile = sTiles[i];
			// Only interested in surrounding tiles that are lower.
			if (tile != null && sTile != null && tile.floor > sTile.floor) {
				output = setBit(output, i, 1);		// "i" can be simply used here because the pattern produced by Utils is compatible.
			}
		}
		return output;
	}
	
	/**
	 * Calculates the type of border judging by tiles surrounding the tile.
	 * It considers the tile type.
	 * Only for edges.
	 * @param tile
	 * @param surrPoints
	 * @param biome - decides if the border is between biomes or heights.
	 * @return
	 */
	public static byte calcBorderTypeByTileTypeEdges(Tile tile, Point[] surrPoints, boolean biome) {
		Tile[] sTiles = Utils.surroundingPointsToTiles(surrPoints);
		if (sTiles.length == 8) {
			if (!isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[3], tile, biome)) {
				return Borders.UP_EMPTY;
			} else if (!isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[3], tile, biome)) {
				return Borders.RIGHT_EMPTY;
			} else if (!isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[3], tile, biome)) {
				return Borders.DOWN_EMPTY;
			} else if (!isDifferent(sTiles[3], tile, biome) && isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[0], tile, biome)) {
				return Borders.LEFT_EMPTY;
			} else if (!(isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[2], tile, biome)) && isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[3], tile, biome)) {
				return Borders.UD_EMPTY;
			} else if (!(isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[3], tile, biome)) && isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[2], tile, biome)) {
				return Borders.LR_EMPTY;
			} else if (allDifferent(tile, sTiles, biome)) {
				return Borders.SURROUNDED;
			} else if (isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[3], tile, biome) && !(isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[2], tile, biome))) {
				return Borders.UP_LEFT;
			} else if (isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[1], tile, biome) && !(isDifferent(sTiles[3], tile, biome) && isDifferent(sTiles[2], tile, biome))) {
				return Borders.UP_RIGHT;
			} else if (isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[1], tile, biome) && !(isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[3], tile, biome))) {
				return Borders.DOWN_RIGHT;
			} else if (isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[3], tile, biome) && !(isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[0], tile, biome))) {
				return Borders.DOWN_LEFT;
			} else if (isDifferent(sTiles[2], tile, biome) && !(isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[3], tile, biome))) {
				return Borders.DOWN;
			} else if (isDifferent(sTiles[0], tile, biome) && !(isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[3], tile, biome))) {
				return Borders.UP;
			} else if (isDifferent(sTiles[1], tile, biome) && !(isDifferent(sTiles[0], tile, biome) && isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[3], tile, biome))) {
				return Borders.RIGHT;
			} else if (isDifferent(sTiles[3], tile, biome) && !(isDifferent(sTiles[1], tile, biome) && isDifferent(sTiles[2], tile, biome) && isDifferent(sTiles[0], tile, biome))) {
				return Borders.LEFT;
			} 
		}
		
		return 0;
	}
	
	/**
	 * Calculates the type of border judging by tiles surrounding the tile.
	 * It considers the tile type.
	 * Only for corners.
	 * @param tile
	 * @param surrPoints
	 * @return
	 */
	public static byte calcBorderTypeByTileTypeCorners(Tile tile, Point[] surrPoints, boolean biome) {
		Tile[] sTiles = Utils.surroundingPointsToTiles(surrPoints);
		if (sTiles.length == 8) {
			if (!isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[5], tile, biome) && isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[7], tile, biome)) {
				return Borders.UL_CORNER_EMPTY;
			} else if (!isDifferent(sTiles[5], tile, biome) && isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[7], tile, biome)) {
				return Borders.UR_CORNER_EMPTY;
			} else if (!isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[5], tile, biome) && isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[7], tile, biome)) {
				return Borders.DR_CORNER_EMPTY;
			} else if (!isDifferent(sTiles[7], tile, biome) && isDifferent(sTiles[5], tile, biome) && isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[4], tile, biome)) {
				return Borders.DL_CORNER_EMPTY;
			} else if (!(isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[6], tile, biome)) && isDifferent(sTiles[5], tile, biome) && isDifferent(sTiles[7], tile, biome)) {
				return Borders.UL_DR_CORNER_EMPTY;
			} else if (!(isDifferent(sTiles[5], tile, biome) && isDifferent(sTiles[7], tile, biome)) && isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[6], tile, biome)) {
				return Borders.UR_DL_CORNER_EMPTY;
			} else if (cornersSurrounded(tile, sTiles, biome)) {
				return Borders.SURROUNDED_CORNER;
			} else if (isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[7], tile, biome) && !(isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[5], tile, biome))) {
				return Borders.DL_DR_CORNER;
			} else if (isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[7], tile, biome) && !(isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[5], tile, biome))) {
				return Borders.UL_DL_CORNER;
			} else if (isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[5], tile, biome) && !(isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[7], tile, biome))) {
				return Borders.UL_UR_CORNER;
			} else if (isDifferent(sTiles[6], tile, biome) && isDifferent(sTiles[5], tile, biome) && !(isDifferent(sTiles[4], tile, biome) && isDifferent(sTiles[7], tile, biome))) {
				return Borders.UR_DR_CORNER;
			} else if (isDifferent(sTiles[6], tile, biome) && otherSame(sTiles[6], sTiles, biome)) {
				return Borders.DOWN_RIGHT_CORNER;
			} else if (isDifferent(sTiles[7], tile, biome) && otherSame(sTiles[7], sTiles, biome)) {
				return Borders.DOWN_LEFT_CORNER;
			} else if (isDifferent(sTiles[4], tile, biome) && otherSame(sTiles[4], sTiles, biome)) {
				return Borders.UP_LEFT_CORNER;
			} else if (isDifferent(sTiles[5], tile, biome) && otherSame(sTiles[5], sTiles, biome)) {
				return Borders.UP_RIGHT_CORNER;
			}  
		}
		
		return 0;
	}
	
	/**
	 * Just a helper method for calcBorderType().
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static boolean isDifferent(Tile t1, Tile t2, boolean biome) {
		if (biome) {
			if (t1 == null || t2 == null) {
				return false;
			} else {
				if ((t1.tile == t2.tile && !(Tiles.getPrecedence(t1.tile) > Tiles.getPrecedence(t2.tile)) || t1.floor != t2.floor))
					return false;
				else return true;
			}
		} else {
			if (t1 == null || t2 == null) {
				return false;
			} else {
				if (t1.floor >= t2.floor)
					return false;
				else return true;
			}
		}
	}
	
	/**
	 * Another helper method for calcBorderType().
	 * @param rightTile
	 * @param tileArray
	 * @return
	 */
	private static boolean otherSame(Tile rightTile, Tile[] tileArray, boolean biome) {
		boolean out = true;
		if (biome) {
			for (Tile t : tileArray) {
				if (t != null && !t.origin.equals(rightTile.origin)) {
					if (t.tile == rightTile.tile || t.floor != rightTile.floor) {
						out = false;
						break;
					}
				}
			}
		} else {
			for (Tile t : tileArray) {
				if (t != null && !t.origin.equals(rightTile.origin)) {
					if (t.floor < rightTile.floor) {
						out = false;
						break;
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * And yet another helper method for calcBorderType().
	 * @param tile
	 * @param tileArray
	 * @return
	 */
	private static boolean allDifferent(Tile tile, Tile[] tileArray, boolean biome) {
		boolean out = true;
		if (biome) {
			for (Tile t : tileArray) {
				if (t == null || tile == null) {
					out = false;
					break;
				}
				
				if (tile.tile == t.tile && t.floor == tile.floor) {
					out = false;
					break;
				}
			}
		} else {
			for (Tile t : tileArray) {
				if (t == null || tile == null) {
					out = false;
					break;
				}
				
				if (t.floor >= tile.floor) {
					out = false;
					break;
				}
			}
		}
		return out;
	}
	
	/**
	 * Aaaaaand yet another one.
	 * @param tile
	 * @param tileArray
	 * @return
	 */
	private static boolean cornersSurrounded(Tile tile, Tile[] tileArray, boolean biome) {
		boolean out = true;
		int i = 0;
		if (biome) {
			for (Tile t : tileArray) {
				if (i < 4) {
					if (t != null && (t.tile != tile.tile && Tiles.getPrecedence(t.tile) > Tiles.getPrecedence(tile.tile)) && t.floor == tile.floor) {
						out = false;
						break;
					}
				} else {
					if (t == null) {
						out = false;
						break;
					} else if (t.tile == tile.tile && t.floor == tile.floor) {
						out = false;
						break;
					}
				}
					
				i++;
			}
		} else {
			for (Tile t : tileArray) {
				if (i < 4) {
					if (t != null && t.floor < tile.floor) {
						out = false;
						break;
					}
				} else {
					if (t == null) {
						out = false;
						break;
					} else if (tile.floor >= t.floor) {
						out = false;
						break;
					}
				}
					
				i++;
			}
		}
		return out;
	}
	
	/**
	 * Sets the bit at a position indicated by "index" in byte "flag"
	 * to the value indiciated by "val".
	 */
	private static byte setBit(byte flag, int index, int val) {
		return (byte) (((val > 0 ? 1 : 0) << index) | flag);
	}
}
