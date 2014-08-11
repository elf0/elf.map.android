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
	//	public boolean _bWater;
	public String _strName;
	//	public List<Point> _points = new LinkedList<Point>();
	private Point[] _points;
	public Rect _rtBoundingRect;
}