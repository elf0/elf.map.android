/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

public class Size{
	public Size(){}

	public Size(long width, long height){
		Set(width, height);
	}

	public Size(Size size){
		Set(size.Width(), size.Height());
	}

	public long Width(){
		return _width;
	}
	
	public long Height(){
		return _height;
	}
	
	public void SetWidth(long width){
		this._width = width;
	}

	public void SetHeight(long height){
		this._height = height;
	}

	public void Set(long width, long height){
		SetWidth(width);
		SetHeight(height);
	}

	public void Set(Size size){
		Set(size.Width(), size.Height());
	}

	private long _width;
	private long _height;
}
