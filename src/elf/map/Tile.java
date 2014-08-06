package elf.map;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Tile{
	private String _strDir;
	public NamedLocation[] _szPoints;
	public Line[] _szLines;
	public Line[] _szWaterLines;
	public Area[] _szWaters;
	public Area[] _szAreas;

	public String getDir(){
		return _strDir;
	}
	public boolean Load(String _strRootPath, String strDir){
		_strDir = strDir;
		String strFullPath = _strRootPath + "/" + strDir;
		try{
			LoadPoints(strFullPath);
			LoadLines(strFullPath);
			LoadAreas(strFullPath);
			LoadWaterLines(strFullPath);
			LoadWaters(strFullPath);
		}
		catch(OutOfMemoryError e){
			return false;
		}
		return true;
	}

	private boolean LoadPoints(String strDir){
		long nPoints;
		try{
			FileInputStream fisLLUFile = new FileInputStream(strDir + "/points.llu");

			//			if(!ReadPoint(fisLLUFile, _ptCenter)){
			//				fisLLUFile.close();
			//				return false;
			//			}

			nPoints = ReadLong(fisLLUFile);

			if(nPoints == -1){
				fisLLUFile.close();
				return false;
			}

			if(nPoints == 0){
				fisLLUFile.close();
				return true;
			}

			_szPoints = new NamedLocation[(int)nPoints];

			for(int i = 0; i < nPoints; ++i){
				_szPoints[i] = new NamedLocation();

				if(!ReadPoint(fisLLUFile, _szPoints[i])){
					//					ClearLocations();
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
			//			ClearLocations();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/points.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				//				ClearLocations();
				fisStrFile.close();
				return false;
			}

			if(nPoints != nStrings){
				//				ClearLocations();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				_szPoints[i].strName = ReadString(fisStrFile);
				if(_szPoints[i].strName == null){
					//					ClearLocations();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			//			ClearLocations();
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

			_szLines = new Line[(int)nLines];

			Line line;
			Point point;
			int nPoints;
			int nType;
			for(int i = 0; i < nLines; ++i){
				nPoints = ReadInt(fisLLUFile);
				//				if(nPoints == -1){
				if(nPoints < 0){
					//					ClearLines();
					fisLLUFile.close();
					return false;
				}

				nType = ReadInt(fisLLUFile);
				if(nType == -1){
					//					ClearLines();
					fisLLUFile.close();
					return false;
				}

				line = _szLines[i] = new Line(nPoints);

				for(int p = 0; p < nPoints; ++p){
					point = new Point();
					if(!ReadPoint(fisLLUFile, point)){
						fisLLUFile.close();
						return false;
					}
					line.setPoint(p, point);
				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			//			ClearLines();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/lines.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				//				ClearLines();
				fisStrFile.close();
				return false;
			}

			if(nLines != nStrings){
				//				ClearLines();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				_szLines[i]._strName = ReadString(fisStrFile);
				if(_szLines[i]._strName == null){
					//					ClearLines();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			//			ClearLines();
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

			_szAreas = new Area[(int)nAreas];

			Area area;
			Point point;
			int nPoints;
			int nType;
			for(int i = 0; i < nAreas; ++i){
				nPoints = ReadInt(fisLLUFile);
				if(nPoints == -1){
					//					ClearAreas();
					fisLLUFile.close();
					return false;
				}

				nType = ReadInt(fisLLUFile);
				if(nType == -1){
					//					ClearAreas();
					fisLLUFile.close();
					return false;
				}

				area = _szAreas[i] = new Area(nPoints);

				for(int p = 0; p < nPoints; ++p){
					point = new Point();
					if(!ReadPoint(fisLLUFile, point)){
						fisLLUFile.close();
						return false;
					}
					area.setPoint(p, point);
				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			//			ClearAreas();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/areas.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				//				ClearAreas();
				fisStrFile.close();
				return false;
			}

			if(nAreas != nStrings){
				//				ClearAreas();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				_szAreas[i]._strName = ReadString(fisStrFile);
				if(_szAreas[i]._strName == null){
					//					ClearAreas();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			//			ClearAreas();
			return false;
		}

		return true;
	}


	private boolean LoadWaterLines(String strDir){
		long nLines;
		try{
			FileInputStream fisLLUFile = new FileInputStream(strDir + "/water_lines.llu");

			nLines = ReadLong(fisLLUFile);

			if(nLines == -1){
				fisLLUFile.close();
				return false;
			}

			if(nLines == 0){
				fisLLUFile.close();
				return true;
			}

			_szWaterLines = new Line[(int)nLines];

			Line line;
			Point point;
			int nPoints;
			int nType;
			for(int i = 0; i < nLines; ++i){
				nPoints = ReadInt(fisLLUFile);
				//				if(nPoints == -1){
				if(nPoints < 0){
					//					ClearLines();
					fisLLUFile.close();
					return false;
				}

				nType = ReadInt(fisLLUFile);
				if(nType == -1){
					//					ClearLines();
					fisLLUFile.close();
					return false;
				}

				line = _szWaterLines[i] = new Line(nPoints);

				for(int p = 0; p < nPoints; ++p){
					point = new Point();
					if(!ReadPoint(fisLLUFile, point)){
						fisLLUFile.close();
						return false;
					}
					line.setPoint(p, point);
				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			//			ClearLines();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/water_lines.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				//				ClearLines();
				fisStrFile.close();
				return false;
			}

			if(nLines != nStrings){
				//				ClearLines();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				_szWaterLines[i]._strName = ReadString(fisStrFile);
				if(_szWaterLines[i]._strName == null){
					//					ClearLines();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			//			ClearLines();
			return false;
		}

		return true;
	}

	private boolean LoadWaters(String strDir){
		long nAreas;
		try{
			FileInputStream fisLLUFile = new FileInputStream(strDir + "/waters.llu");

			nAreas = ReadLong(fisLLUFile);

			if(nAreas == -1){
				fisLLUFile.close();
				return false;
			}

			if(nAreas == 0){
				fisLLUFile.close();
				return true;
			}

			_szWaters = new Area[(int)nAreas];

			Area area;
			Point point;
			int nPoints;
			int nType;
			for(int i = 0; i < nAreas; ++i){
				nPoints = ReadInt(fisLLUFile);
				if(nPoints == -1){
					//					ClearAreas();
					fisLLUFile.close();
					return false;
				}

				nType = ReadInt(fisLLUFile);
				if(nType == -1){
					//					ClearAreas();
					fisLLUFile.close();
					return false;
				}

				area = _szWaters[i] = new Area(nPoints);

				for(int p = 0; p < nPoints; ++p){
					point = new Point();
					if(!ReadPoint(fisLLUFile, point)){
						fisLLUFile.close();
						return false;
					}
					area.setPoint(p, point);

				}
			}

			fisLLUFile.close();
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e) {
			//			ClearAreas();
			return false;
		}

		long nStrings;
		try{
			FileInputStream fisStrFile = new FileInputStream(strDir + "/waters.str");

			nStrings = ReadLong(fisStrFile);

			if(nStrings == -1){
				//				ClearAreas();
				fisStrFile.close();
				return false;
			}

			if(nAreas != nStrings){
				//				ClearAreas();
				fisStrFile.close();
				return false;
			}

			for(int i = 0; i < nStrings; ++i){
				_szWaters[i]._strName = ReadString(fisStrFile);
				if(_szWaters[i]._strName == null){
					//					ClearAreas();
					fisStrFile.close();
					return false;
				}
			}
		}
		//		catch(FileNotFoundException e){
		//			return false;
		//		}
		catch (IOException e){
			//			ClearAreas();
			return false;
		}

		return true;
	}

	private byte[] _buffer1 = new byte[1];
	private byte[] _buffer4 = new byte[4];
	private byte[] _buffer8 = new byte[8];
	//	private byte[] _buffer256 = new byte[256];

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

		return ((int)_buffer4[0] & 0xFF)
				| (((int)_buffer4[1] & 0xFF) << 8)
				| (((int)_buffer4[2] & 0xFF) << 16)
				| (((int)_buffer4[3] & 0xFF) << 24);
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

		return ((long)_buffer8[0] & 0xFFL)
				| (((long)_buffer8[1] & 0xFFL) << 8)
				| (((long)_buffer8[2] & 0xFFL) << 16)
				| (((long)_buffer8[3] & 0xFFL) << 24)
				| (((long)_buffer8[4] & 0xFFL)  << 32)
				| (((long)_buffer8[5] & 0xFFL) << 40)
				| (((long)_buffer8[6] & 0xFFL) << 48)
				| (((long)_buffer8[7] & 0xFFL) << 56);
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
}
