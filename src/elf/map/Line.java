/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

//import java.util.ArrayList;
//import java.util.List;

public class Line{
	public Line(int nPoints){
		_points = new Point[nPoints];
//		for(int i = 0, n = _points.length; i < n; ++i){
//			_points[i] = new Point();
//		}
	}

	public int Points(){
		return _points.length;
	}

	public void setPoint(int nIndex, Point point){
		_points[nIndex] = point;
    }

	public Point getPoint(int nIndex){
	    return _points[nIndex];
    }
	
	public Rect getBoundingRect(){
		return _rtBoundingRect;
	}
//	public Line(String strName){
//		this.strName = strName;
//	}

//	public void Set(int nIndex, long nLongitude, long nLatitude){
//		_points[nIndex] = new Point(nLongitude, nLatitude);
//	}

//	public final Point[] getPoints(){
//		return _points;
//	}
//	public boolean _bWater;
	public String _strName;
//	public List<Point> _points = new LinkedList<Point>();
	private Point[] _points;
	public Rect _rtBoundingRect;
}