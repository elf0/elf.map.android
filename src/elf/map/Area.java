/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

//import java.util.ArrayList;
//import java.util.List;

public class Area{
	public Area(int nPoints){
		_points = new Point[nPoints];
	}

	//	public Area(String strName){
	//		this.strName = strName;
	//	}

	//	public void Add(long nLongitude, long nLatitude){
	//		_points.add(new Point(nLongitude, nLatitude));
	//	}

	public boolean _bWater;
	public String _strName;
	//	public List<Point> _points = new ArrayList<Point>();
	public Point[] _points;
}