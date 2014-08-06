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
		if(_rtBoundingRect != null){
			if(point.X() < _rtBoundingRect.Left())
				_rtBoundingRect.SetBoundingLeft(point.X());
			else if(point.X() >= _rtBoundingRect.Right())
				_rtBoundingRect.SetBoundingRight(point.X() + 1);

			if(point.Y() < _rtBoundingRect.Top())
				_rtBoundingRect.SetBoundingTop(point.Y());
			else if(point.Y() >= _rtBoundingRect.Bottom())
				_rtBoundingRect.SetBoundingBottom(point.Y() + 1);
		}
		else{
			_rtBoundingRect = new Rect(point.X(), point.Y(), 1, 1);
		}
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
	private Rect _rtBoundingRect;
}