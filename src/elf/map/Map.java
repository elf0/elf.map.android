/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;


import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.graphics.RectF;
import android.util.Log;

public class Map {
	private String _strRootPath;

	public Map(String strRootDir){
		_strRootPath = strRootDir;
	}

	public enum ObjectType{
		Location, WaterWay, Water, Way, Area
	}

	private final static float LONGITUDE_F2L = (float)(4294967296.0 / 360.0);
	private final static float LONGITUDE_L2F = (float)(360.0 / 4294967296.0);

	public static native long LongitudeFloatToLong(float fLongitude);
	//	public static long Longitude_FloatToLong(float fLongitude){
	//		return (long)((180.0f + fLongitude) * LONGITUDE_F2L);
	//	}

	public static float Longitude_LongToFloat(long nLongitude){
		return nLongitude * LONGITUDE_L2F - 180.0f;
	}

	private final static float LATITUDE_F2L = (float)(4294967296.0 / 360.0);
	private final static float LATITUDE_L2F = (float)(360.0 / 4294967296.0);

	public static native long LatitudeFloatToLong(float fLatitude);
	//	public static long Latitude_FloatToLong(float fLatitude){
	//		return (long)((90.0f - fLatitude) * LATITUDE_F2L);
	//	}

	public static float Latitude_LongToFloat(long nLatitude){
		return 90.0f - nLatitude * LATITUDE_L2F;
	}

	//	public boolean Open(String strDir){
	//		if(!LoadPoints(strDir))
	//			return false;
	//
	//		LoadLines(strDir);
	//		LoadWaterLines(strDir);
	//		LoadAreas(strDir);
	//		LoadWaters(strDir);
	//		
	////		if(!LoadLines(strDir))
	////			return false;
	////
	////		if(!LoadWaterLines(strDir))
	////			return false;
	////
	////		if(!LoadAreas(strDir))
	////			return false;
	////		
	////		if(!LoadWaters(strDir))
	////			return false;
	//		return true;
	//	}






	//	public void Close(){
	//		Clear();
	//	}

	//	public final Point Center(){
	//		return _ptCenter;
	//	}

	//	public List<NamedLocation> Locations(){
	//		return _locations;
	//	}

	private Rect _rtBoundingRect;


	private List<Integer> _lstTileKeys = new LinkedList<Integer>();
	private java.util.Map<Integer, Tile> _mapTiles = new HashMap<Integer, Tile>();
	//	SparseArray;
	private List<Tile> _lstTiles = new LinkedList<Tile>();

	public void Load(Rect rtBoundingRect){
		_lstTiles.clear();

		short nLeft = (short)Longitude_LongToFloat(rtBoundingRect.Left());
		if(nLeft <= -180)
			nLeft = -179;
		else if(nLeft >= 180)
			nLeft = 179;

		short nRight = (short)Longitude_LongToFloat(rtBoundingRect.Right());
		//		++nRight;
		if(nRight <= -180)
			nRight = -179;
		else if(nRight >= 180)
			nRight = 179;

		short nTop = (short)Latitude_LongToFloat(rtBoundingRect.Top());
		//		++nTop;
		if(nTop <= -90)
			nTop = -89;
		else if(nTop >= 90)
			nTop = 89;

		short nBottom = (short)Latitude_LongToFloat(rtBoundingRect.Bottom());
		if(nBottom <= -90)
			nBottom = -89;
		else if(nBottom >= 90)
			nBottom = 89;

		for(short y = nBottom; y <= nTop; ++y){
			for(short x = nLeft; x <= nRight; ++x){
				int nKey = (x & 0xFFFF) | ((y & 0xFFFF) << 16);
				Tile tile = _mapTiles.get(nKey);
				if(tile == null){
					String strDir;
					strDir = (nLeft < 0? String.format("/W%03d", -x) : String.format("/E%03d", x));
					strDir += (nTop < 0? String.format("/S%02d", -y) : String.format("/N%02d", y));
					while(true){
						tile = new Tile();
						if(tile.Load(_strRootPath, strDir)){
							if(_mapTiles.size() == 8){
								_mapTiles.remove(_lstTileKeys.get(0));
								_lstTileKeys.remove(0);
							}

							_mapTiles.put(nKey, tile);
							_lstTileKeys.add(nKey);
							break;
						}
						else{
							if(_lstTileKeys.size() > 0){
								_mapTiles.remove(_lstTileKeys.get(0));
								_lstTileKeys.remove(0);
							}
							else{
								tile = null;
								break;
							}
						}
					}
				}
				else{
					int i = 0;
					for(int n = _lstTileKeys.size(); i < n; ++i){
						if(_lstTileKeys.get(i) == nKey)
							break;
					}
					_lstTileKeys.remove(i);
					_lstTileKeys.add(nKey);
				}

				if(tile != null)
					_lstTiles.add(tile);
			}
		}

		_rtBoundingRect = rtBoundingRect;
	}

	//	public void EndSelection(){
	//	}

	public void SelectLocations(List<NamedLocation> locations){
		for(Tile tile: _lstTiles){
			if(tile._szPoints != null){
				for(NamedLocation location: tile._szPoints){
					if(_rtBoundingRect.Contains(location))
						locations.add(location);
				}
			}
		}
	}

	//	public Line[] WaterWays(){
	//		return _waterWays;
	//	}

	public void SelectWaterWays(List<Line> ways){
		for(Tile tile: _lstTiles){
			if(tile._szWaterLines != null){
				for(Line line: tile._szWaterLines){
					if(line.getBoundingRect().Intersects(_rtBoundingRect)){
						for(int i = 0, n = line.Points(); i < n; ++i){
							Point point = line.getPoint(i);
							if(_rtBoundingRect.Contains(point)){
								ways.add(line);
								break;
							}
						}
					}
				}
			}
		}
	}

	//	public Line[] Ways(){
	//		return _ways;
	//	}

	public void SelectWays(List<Line> ways){
		for(Tile tile: _lstTiles){
			if(tile._szLines != null){
				for(Line line: tile._szLines){
					if(line.getBoundingRect().Intersects(_rtBoundingRect)){
						for(int i = 0, n = line.Points(); i < n; ++i){
							Point point = line.getPoint(i);
							if(_rtBoundingRect.Contains(point)){
								ways.add(line);
								break;
							}
						}
					}
				}
			}
		}
	}

	//	public Area[] Waters(){
	//		return _waters;
	//	}

	public void SelectWaters(List<Area> waters){
		for(Tile tile: _lstTiles){
			if(tile._szWaters != null){
				for(Area area: tile._szWaters){
					if(area.getBoundingRect().Intersects(_rtBoundingRect)){
						for(int i = 0, n = area.Points(); i < n; ++i){
							Point point = area.getPoint(i);
							if(_rtBoundingRect.Contains(point)){
								waters.add(area);
								break;
							}
						}
					}
				}
			}
		}
	}

	//	public Area[] Areas(){
	//		return _areas;
	//	}

	public void SelectAreas(List<Area> areas){
		for(Tile tile: _lstTiles){
			if(tile._szAreas != null){
				for(Area area: tile._szAreas){
					if(area.getBoundingRect().Intersects(_rtBoundingRect)){
						for(int i = 0, n = area.Points(); i < n; ++i){
							Point point = area.getPoint(i);
							if(_rtBoundingRect.Contains(point)){
								areas.add(area);
								break;
							}
						}
					}
				}
			}
		}
	}

	//	private void Clear(){
	//		ClearLocations();
	//		ClearLines();
	//		ClearWaterLines();
	//		ClearAreas();
	//		ClearWaters();
	//	}

	//	private void ClearLocations(){
	//		//		if(_locations != null){
	//		//			for(int i = 0, n = _locations.length; i < n; ++i){
	//		//				_locations[i] = null;
	//		//			}
	//		//		}
	//		//
	//		//		_locations = null;
	//	}

	//	private void ClearLines(){
	//		if(_lines != null){
	//			for(int i = 0, n = _lines.length; i < n; ++i){
	//				_lines[i] = null;
	//			}
	//		}
	//		_lines = null;
	//	}

	//	private void ClearWaterLines(){
	//		//		if(_szWaterLines != null){
	//		//			for(int i = 0, n = _szWaterLines.length; i < n; ++i){
	//		//				_szWaterLines[i] = null;
	//		//			}
	//		//		}
	//		//		_szWaterLines = null;
	//	}

	//	private void ClearAreas(){
	//		//		if(_areas != null){
	//		//			for(int i = 0, n = _areas.length; i < n; ++i){
	//		//				_areas[i] = null;
	//		//			}
	//		//		}
	//		//
	//		//		_areas = null;
	//	}
	//
	//	private void ClearWaters(){
	//		//		if(_waters != null){
	//		//			for(int i = 0, n = _waters.length; i < n; ++i){
	//		//				_waters[i] = null;
	//		//			}
	//		//		}
	//		//
	//		//		_waters = null;
	//	}

	//	private Point _ptCenter = new Point();
	//	private NamedLocation[] _locations;
	//	private Line[] _lines;
	//	private Line[] _szWaterLines;
	//	private Area[] _waters;
	//	private Area[] _areas;

}
