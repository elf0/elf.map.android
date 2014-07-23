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
	private final static float _F2L = (float)(4294967296.0 / 360.0);
	private final static float _L2F = (float)(360.0 / 4294967296.0);

	public static long Longitude_FloatToLong(float fLongitude){
		return (long)((180.0f + fLongitude) * _F2L);
	}

	public static long Latitude_FloatToLong(float fLatitude){
		return (long)((180.0f - fLatitude) * _F2L);
	}

	public static float Longitude_LongToFloat(long nLongitude){
		return nLongitude * _L2F - 180.0f;
	}

	public static float Latitude_LongToFloat(long nLatitude){
		return 180.0f - nLatitude * _L2F;
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
		for(int i = 0, n = buffer.length; i < n; ++i){
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


			String[] items = strLine.split("\"");
			if(items.length < 4){
				Clear();
				return false;
			}

			if("0".equals(items[0])){
				AddLocation(items[1], Long.valueOf(items[2]), Long.valueOf(items[3]));
			}
			else if("1".equals(items[0])){
				Way way = new Way(items[1]);
				for(int iLL = 2, nItems = items.length - 2; iLL < nItems; iLL += 2){
					way.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
				}
				AddWay(way);
			}
			else if("2".equals(items[0])){
				Area area = new Area(items[1]);
				for(int iLL = 2, nItems = items.length - 2; iLL < nItems; iLL += 2){
					area.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
				}
				AddArea(area);
			}

		}
		return true;
	}

	public void Close(){
		Clear();
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

	private void AddWay(Way way){
//		if(_ways.size() < 4000)
			_ways.add(way);
	}

	private void AddArea(Area area){
//		if(_areas.size() < 2000)
			_areas.add(area);
	}
	
	private void Clear(){
		_areas.clear();
		_ways.clear();
		_locations.clear();
	}

	private List<NamedLocation> _locations = new ArrayList<NamedLocation>();
	private List<Way> _ways = new ArrayList<Way>();
	private List<Area> _areas = new ArrayList<Area>();
}
