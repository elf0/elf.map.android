/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

public class Map {
	public enum ObjectType{
		Location, WaterWay, Water, Way, Area
	}
	
	private final static float LONGITUDE_F2L = (float)(4294967296.0 / 360.0);
	private final static float LONGITUDE_L2F = (float)(360.0 / 4294967296.0);

	public static long Longitude_FloatToLong(float fLongitude){
		return (long)((180.0f + fLongitude) * LONGITUDE_F2L);
	}

	public static float Longitude_LongToFloat(long nLongitude){
		return nLongitude * LONGITUDE_L2F - 180.0f;
	}

	private final static float LATITUDE_F2L = (float)(4294967296.0 / 360.0);
	private final static float LATITUDE_L2F = (float)(360.0 / 4294967296.0);

	public static long Latitude_FloatToLong(float fLatitude){
		return (long)((90.0f - fLatitude) * LATITUDE_F2L);
	}

	public static float Latitude_LongToFloat(long nLatitude){
		return 90.0f - nLatitude * LATITUDE_L2F;
	}

	public boolean Open(String strFileName){
		byte[] buffer;

		try{
			FileInputStream fisMapFile = new FileInputStream(strFileName);
			buffer = new byte[fisMapFile.available()]; 
			int n = fisMapFile.read(buffer);
			fisMapFile.close();
			if(n <= 0)
				return false;
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			return false;
		}

		String strLine;
		byte[] subBuffer;
		int iLine;
		int nLineSize;
		int i = 0;
		int n = buffer.length;
		iLine = i;
		while(buffer[i] != '\n')
			++i;
		nLineSize = i - iLine;
		subBuffer = new byte[nLineSize];
		System.arraycopy(buffer, iLine, subBuffer, 0, nLineSize);
		try {
			strLine = new String(subBuffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//				e.printStackTrace();
			return false;
		}
		String[] items = strLine.split(",");
		if(items.length != 4)
			return false;
		_ptCenter.Set(Long.valueOf(items[0]), Long.valueOf(items[1]));
		++i;



		for(; i < n; ++i){
			iLine = i;
			while(buffer[i] != '\n')
				++i;

			nLineSize = i - iLine;
			subBuffer = new byte[nLineSize];
			System.arraycopy(buffer, iLine, subBuffer, 0, nLineSize);
			try {
				strLine = new String(subBuffer, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				//				e.printStackTrace();
				return false;
			}


			items = strLine.split("\"");
			if(items.length < 4){
				Clear();
				return false;
			}

			switch(items[0]){
			case "0":
				AddLocation(items[1], Long.valueOf(items[2]), Long.valueOf(items[3]));
				break;
			case "1":{
				WaterWay way = new WaterWay(items[1]);
				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
					way.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
				}
				AddWaterWay(way);
			}break;
			case "2":{
				Water water = new Water(items[1]);
				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
					water.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
				}
				AddWater(water);
			}break;
			case "3":{
				Way way = new Way(items[1]);
				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
					way.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
				}
				AddWay(way);
			}break;
			case "4":{
				Area area = new Area(items[1]);
				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
					area.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
				}
				AddArea(area);
			}break;
			}
		}
		return true;
	}

	public void Close(){
		Clear();
	}

	public final Point Center(){
		return _ptCenter;
	}

	public List<NamedLocation> Locations(){
		return _locations;
	}

	public void SelectLocations(Rect rect, List<NamedLocation> locations){
		for(NamedLocation location: _locations){
			if(rect.Contains(location))
				locations.add(location);
		}
	}

	public List<WaterWay> WaterWays(){
		return _waterWays;
	}

	public void SelectWaterWays(Rect rect, List<WaterWay> ways){
		for(WaterWay way: _waterWays){
			for(Point location: way._points){
				if(rect.Contains(location)){
					ways.add(way);
					break;
				}
			}
		}
	}

	public List<Way> Ways(){
		return _ways;
	}

	public void SelectWays(Rect rect, List<Way> ways){
		for(Way way: _ways){
			for(Point location: way._points){
				if(rect.Contains(location)){
					ways.add(way);
					break;
				}
			}
		}
	}

	public List<Water> Waters(){
		return _waters;
	}

	public void SelectWaters(Rect rect, List<Water> waters){
		for(Water water: _waters){
			for(Point location: water._points){
				if(rect.Contains(location)){
					waters.add(water);
					break;
				}
			}
		}
	}

	public List<Area> Areas(){
		return _areas;
	}

	public void SelectAreas(Rect rect, List<Area> areas){
		for(Area area: _areas){
			for(Point location: area._points){
				if(rect.Contains(location)){
					areas.add(area);
					break;
				}
			}
		}
	}

	private void AddLocation(String strName, long nLongitude, long nLatitude){
		NamedLocation l = new NamedLocation(strName, nLongitude, nLatitude);
		_locations.add(l);
	}

	private void AddWaterWay(WaterWay way){
		if(_waterWays.size() < 1000)
			_waterWays.add(way);
	}

	private void AddWay(Way way){
		if(_ways.size() < 1000)
			_ways.add(way);
	}

	private void AddWater(Water water){
//		if(_ways.size() < 1000)
		_waters.add(water);
	}

	private void AddArea(Area area){
		if(_areas.size() < 1000)
			_areas.add(area);
	}

	private void Clear(){
		_areas.clear();
		_ways.clear();
		_locations.clear();
	}

	private Point _ptCenter = new Point();
	private List<NamedLocation> _locations = new ArrayList<NamedLocation>();
	private List<Way> _ways = new ArrayList<Way>();
	private List<WaterWay> _waterWays = new ArrayList<WaterWay>();
	private List<Water> _waters = new ArrayList<Water>();
	private List<Area> _areas = new ArrayList<Area>();
}
