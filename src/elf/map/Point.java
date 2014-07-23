/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

public class Point{
	public Point(){}

	public Point(long x, long y){
		Set(x, y);
	}

	public Point(Point point){
		Set(point.X(), point.Y());
	}

	public long X(){
		return _x;
	}
	
	public long Y(){
		return _y;
	}

	public void SetX(long x){
		this._x = x;
	}
	
	public void SetY(long y){
		this._y = y;
	}

	public void Set(long x, long y){
		SetX(x);
		SetY(y);
	}

	public void Set(Point point){
		Set(point.X(), point.Y());
	}

	public void Offset(long x, long y){
		_x += x;
		_y += y;
	}

	private long _x;
	private long _y;
}