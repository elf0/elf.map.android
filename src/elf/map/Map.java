/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

	public boolean Open(String strDir){
		if(!LoadPoints(strDir))
			return false;

		if(!LoadLines(strDir))
			return false;

		if(!LoadAreas(strDir))
			return false;
		//		byte[] buffer;
		//
		//		try{
		//			FileInputStream fisMapFile = new FileInputStream(strDir + "/elf.map");
		//			buffer = new byte[fisMapFile.available()]; 
		//			int n = fisMapFile.read(buffer);
		//			fisMapFile.close();
		//			if(n <= 0)
		//				return false;
		//		}
		//		//		catch(FileNotFoundException e){
		//		//			return false;
		//		//		}
		//		catch (IOException e) {
		//			return false;
		//		}
		//
		//		String strLine;
		//		byte[] subBuffer;
		//		int iLine;
		//		int nLineSize;
		//		int i = 0;
		//		int n = buffer.length;
		//		iLine = i;
		//		while(buffer[i] != '\n')
		//			++i;
		//		nLineSize = i - iLine;
		//		subBuffer = new byte[nLineSize];
		//		System.arraycopy(buffer, iLine, subBuffer, 0, nLineSize);
		//		try {
		//			strLine = new String(subBuffer, "UTF-8");
		//		} catch (UnsupportedEncodingException e) {
		//			// TODO Auto-generated catch block
		//			//				e.printStackTrace();
		//			return false;
		//		}
		//		String[] items = strLine.split(",");
		//		if(items.length != 4)
		//			return false;
		//		_ptCenter.Set(Long.valueOf(items[0]), Long.valueOf(items[1]));
		//		++i;
		//
		//
		//
		//		for(; i < n; ++i){
		//			iLine = i;
		//			while(buffer[i] != '\n')
		//				++i;
		//
		//			nLineSize = i - iLine;
		//			subBuffer = new byte[nLineSize];
		//			System.arraycopy(buffer, iLine, subBuffer, 0, nLineSize);
		//			try {
		//				strLine = new String(subBuffer, "UTF-8");
		//			} catch (UnsupportedEncodingException e) {
		//				// TODO Auto-generated catch block
		//				//				e.printStackTrace();
		//				return false;
		//			}
		//
		//
		//			items = strLine.split("\"");
		//			if(items.length < 4){
		//				Clear();
		//				return false;
		//			}
		//
		//			switch(items[0]){
		//			case "0":
		//				//				AddLocation(items[1], Long.valueOf(items[2]), Long.valueOf(items[3]));
		//				break;
		//			case "1":{
		//				WaterWay way = new WaterWay(items[1]);
		//				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
		//					way.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
		//				}
		//				AddWaterWay(way);
		//			}break;
		//			case "2":{
		//				Water water = new Water(items[1]);
		//				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
		//					water.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
		//				}
		//				AddWater(water);
		//			}break;
		//			case "3":{
		//				Way way = new Way(items[1]);
		//				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
		//					way.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
		//				}
		//				AddWay(way);
		//			}break;
		//			case "4":{
		//				Area area = new Area(items[1]);
		//				for(int iLL = 2, nItems = items.length; iLL < nItems; iLL += 2){
		//					area.Add(Long.valueOf(items[iLL]), Long.valueOf(items[iLL + 1]));
		//				}
		//				AddArea(area);
		//			}break;
		//			}
		//		}
		return true;
	}

	private byte[] _buffer1 = new byte[1];
	private byte[] _buffer4 = new byte[4];
	private byte[] _buffer8 = new byte[8];
	private byte[] _buffer256 = new byte[256];

	private int ReadU8(InputStream stream){
		int n;
		try {
			n = stream.read(_buffer1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			return -1;
		}

		if(n != 1){
			return -1;
		}

		return _buffer1[0] & 0xFF;
	}

	private int ReadInt(InputStream stream){
		int n;
		try {
			n = stream.read(_buffer4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			return -1;
		}

		if(n != 4){
			return -1;
		}

		return (_buffer4[0] & 0xFF)
				| ((_buffer4[1] & 0xFF) << 8)
				| ((_buffer4[2] & 0xFF) << 16)
				| ((_buffer4[3] & 0xFF) << 24);
	}

	private long ReadLong(InputStream stream){
		int n;
		try {
			n = stream.read(_buffer8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			return -1;
		}

		if(n != 8){
			return -1;
		}

		return ((long)_buffer8[0] & 0xFF)
				| (((long)_buffer8[1] & 0xFF) << 8)
				| (((long)_buffer8[2] & 0xFF) << 16)
				| (((long)_buffer8[3] & 0xFF) << 24)
				| (((long)_buffer8[4] & 0xFF)  << 32)
				| (((long)_buffer8[5] & 0xFF) << 40)
				| (((long)_buffer8[6] & 0xFF) << 48)
				| (((long)_buffer8[7] & 0xFF) << 56);
	}

	private boolean ReadPoint(InputStream stream, Point point){
		int n;
		try {
			n = stream.read(_buffer8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			return false;
		}

		if(n != 8){
			return false;
		}

		point.Set(((long)_buffer8[0] & 0xFF) | (((long)_buffer8[1] & 0xFF) << 8) | (((long)_buffer8[2] & 0xFF) << 16) | (((long)_buffer8[3] & 0xFF) << 24)
				, ((long)_buffer8[4] & 0xFF) | (((long)_buffer8[5] & 0xFF) << 8) | (((long)_buffer8[6] & 0xFF) << 16) | (((long)_buffer8[7] & 0xFF) << 24));
		return true;
	}

	//	private boolean ReadString(InputStream stream, String str){
	//		int nSize = ReadU8(stream);
	//		if(nSize == -1)
	//			return false;
	//
	//		byte[] buffer = new byte[nSize];
	//
	//		int n;
	//		try {
	//			n = stream.read(buffer);
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			//			e.printStackTrace();
	//			return false;
	//		}
	//
	//		if(n != nSize)
	//			return false;
	//
	//		try {
	//			str = new String(buffer, "UTF-8");
	//		} catch (UnsupportedEncodingException e) {
	//			// TODO Auto-generated catch block
	//			//				e.printStackTrace();
	//			return false;
	//		}
	//
	//		return true;
	//	}

	private String ReadString(InputStream stream){
		int nSize = ReadU8(stream);
		if(nSize == -1)
			return null;

		byte[] buffer = new byte[nSize];

		int n;
		try {
			n = stream.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			return null;
		}

		if(n != nSize)
			return null;

		String str;
		try {
			str = new String(buffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//				e.printStackTrace();
			return null;
		}

		return str;
	}

	private boolean LoadPoints(String strDir){
		long nPoints;
		try{
			FileInputStream fisLLUFile = new FileInputStream(strDir + "/points.llu");

			if(!ReadPoint(fisLLUFile, _ptCenter)){
				fisLLUFile.close();
				return false;
			}

			nPoints = ReadLong(fisLLUFile);

			if(nPoints == -1){
				fisLLUFile.close();
				return false;
			}

			if(nPoints == 0){
				fisLLUFile.close();
				return true;
			}

			_locations = new NamedLocation[(int)nPoints];

			for(int i = 0; i < nPoints; ++i){
				_locations[i] = new NamedLocation();

				if(!ReadPoint(fisLLUFile, _locations[i])){
					ClearLocations();
					fisLLUFile.close();
					return false;
				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			ClearLocations();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/points.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				ClearLocations();
				fisStrFile.close();
				return false;
			}

			if(nPoints != nStrings){
				ClearLocations();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				//				if(!ReadString(fisStrFile, _locations[i].strName)){
				_locations[i].strName = ReadString(fisStrFile);
				if(_locations[i].strName == null){
					ClearLocations();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			ClearLocations();
			return false;
		}

		return true;
	}

	private boolean LoadLines(String strDir){
		long nLines;
		try{
			FileInputStream fisLLUFile = new FileInputStream(strDir + "/lines.llu");

			nLines = ReadLong(fisLLUFile);

			if(nLines == -1){
				fisLLUFile.close();
				return false;
			}

			if(nLines == 0){
				fisLLUFile.close();
				return true;
			}

			_lines = new Line[(int)nLines];

			Line line;
			Point point;
			int nPoints;
			int nType;
			for(int i = 0; i < nLines; ++i){
				nPoints = ReadInt(fisLLUFile);
				if(nPoints == -1){
					ClearLines();
					fisLLUFile.close();
					return false;
				}

				nType = ReadInt(fisLLUFile);
				if(nType == -1){
					ClearLines();
					fisLLUFile.close();
					return false;
				}

				line = _lines[i] = new Line(nPoints);

				line._bWater = (nType == 0? true : false);

				for(int p = 0; p < nPoints; ++p){
					line._points[p] = point = new Point();
					if(!ReadPoint(fisLLUFile, point)){
						ClearLines();
						fisLLUFile.close();
						return false;
					}
				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			ClearLines();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/lines.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				ClearLines();
				fisStrFile.close();
				return false;
			}

			if(nLines != nStrings){
				ClearLines();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				//				if(!ReadString(fisStrFile, _lines[i]._strName)){
				_lines[i]._strName = ReadString(fisStrFile);
				if(_lines[i]._strName == null){
					ClearLines();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			ClearLines();
			return false;
		}

		return true;
	}

	private boolean LoadAreas(String strDir){
		long nAreas;
		try{
			FileInputStream fisLLUFile = new FileInputStream(strDir + "/areas.llu");

			nAreas = ReadLong(fisLLUFile);

			if(nAreas == -1){
				fisLLUFile.close();
				return false;
			}

			if(nAreas == 0){
				fisLLUFile.close();
				return true;
			}

			_areas = new Area[(int)nAreas];

			Area area;
			Point point;
			int nPoints;
			int nType;
			for(int i = 0; i < nAreas; ++i){
				nPoints = ReadInt(fisLLUFile);
				if(nPoints == -1){
					ClearAreas();
					fisLLUFile.close();
					return false;
				}

				nType = ReadInt(fisLLUFile);
				if(nType == -1){
					ClearAreas();
					fisLLUFile.close();
					return false;
				}

				area = _areas[i] = new Area(nPoints);

				area._bWater = (nType == 0? true : false);

				for(int p = 0; p < nPoints; ++p){
					area._points[p] = point = new Point();
					if(!ReadPoint(fisLLUFile, point)){
						ClearAreas();
						fisLLUFile.close();
						return false;
					}
				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			ClearAreas();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/areas.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				ClearAreas();
				fisStrFile.close();
				return false;
			}

			if(nAreas != nStrings){
				ClearAreas();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				//				if(!ReadString(fisStrFile, _areas[i]._strName)){
				_areas[i]._strName = ReadString(fisStrFile);
				if(_areas[i]._strName == null){
					ClearAreas();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			ClearAreas();
			return false;
		}

		return true;
	}

	public void Close(){
		Clear();
	}

	public final Point Center(){
		return _ptCenter;
	}

	//	public List<NamedLocation> Locations(){
	//		return _locations;
	//	}

	public void SelectLocations(Rect rect, List<NamedLocation> locations){
		if(_locations == null)
			return;

		for(NamedLocation location: _locations){
			if(rect.Contains(location))
				locations.add(location);
		}
	}

	//	public Line[] WaterWays(){
	//		return _waterWays;
	//	}

	public void SelectWaterWays(Rect rect, List<Line> ways){
		if(_lines == null)
			return;

		for(Line line: _lines){
			if(!line._bWater)
				continue;

			for(Point location: line._points){
				if(rect.Contains(location)){
					ways.add(line);
					break;
				}
			}
		}
	}

	//	public Line[] Ways(){
	//		return _ways;
	//	}

	public void SelectWays(Rect rect, List<Line> ways){
		if(_lines == null)
			return;

		for(Line line: _lines){
			if(line._bWater)
				continue;

			for(Point location: line._points){
				if(rect.Contains(location)){
					ways.add(line);
					break;
				}
			}
		}
	}

	//	public Area[] Waters(){
	//		return _waters;
	//	}

	public void SelectWaters(Rect rect, List<Area> waters){
		if(_areas == null)
			return;
		
		for(Area area: _areas){
			if(!area._bWater)
				continue;

			for(Point location: area._points){
				if(rect.Contains(location)){
					waters.add(area);
					break;
				}
			}
		}
	}

	//	public Area[] Areas(){
	//		return _areas;
	//	}

	public void SelectAreas(Rect rect, List<Area> areas){
		if(_areas == null)
			return;

		for(Area area: _areas){
			if(area._bWater)
				continue;

			for(Point location: area._points){
				if(rect.Contains(location)){
					areas.add(area);
					break;
				}
			}
		}
	}

	//	private void AddLocation(String strName, long nLongitude, long nLatitude){
	//		NamedLocation l = new NamedLocation(strName, nLongitude, nLatitude);
	//		_locations.add(l);
	//	}

	//	private void AddWaterWay(Line way){
	//		if(_waterWays.size() < 1000)
	//			_waterWays.add(way);
	//	}
	//
	//	private void AddWay(Line way){
	//		if(_ways.size() < 1000)
	//			_ways.add(way);
	//	}
	//
	//	private void AddWater(Area water){
	//		if(_ways.size() < 1000)
	//			_waters.add(water);
	//	}
	//
	//	private void AddArea(Area area){
	//		if(_areas.size() < 1000)
	//			_areas.add(area);
	//	}

	private void Clear(){
		ClearLocations();
		ClearLines();
		ClearAreas();
	}

	private void ClearLocations(){
		if(_locations != null){
			for(int i = 0, n = _locations.length; i < n; ++i){
				_locations[i] = null;
			}
		}

		_locations = null;
	}

	private void ClearLines(){
		if(_lines != null){
			for(int i = 0, n = _lines.length; i < n; ++i){
				_lines[i] = null;
			}
		}
		_lines = null;
	}

	private void ClearAreas(){
		if(_areas != null){
			for(int i = 0, n = _areas.length; i < n; ++i){
				_areas[i] = null;
			}
		}

		_areas = null;
	}


	//	private void ClearWaterWays(){
	//		ClearLines(_waterWays);
	//		_waterWays = null;
	//	}
	//
	//	private void ClearWaters(){
	//		if(_waters != null){
	//			for(int i = 0, n = _waters.length; i < n; ++i){
	//				_waters[i] = null;
	//			}
	//		}
	//
	//		_waters = null;
	//	}


	private Point _ptCenter = new Point();
	//	private List<NamedLocation> _locations = new ArrayList<NamedLocation>();
	private NamedLocation[] _locations;
	private Line[] _lines;
	//	private Line[] _ways;
	//	private Line[] _waterWays;
	//	private Area[] _waters;
	private Area[] _areas;
}
