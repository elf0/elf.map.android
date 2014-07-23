/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import android.graphics.Color;

public class MapStyle {
	private int _nBackgroundColor = Color.BLACK;
	private int _nTextColor = Color.WHITE;
	private int _nWayColor = 0xFFA0A0A0;
	private int _nAreaColor = 0xFF00A0FF;
	
	public int BackgroundColor(){
		return _nBackgroundColor;
	}
	
	public void setBackgroundColor(int color){
		_nBackgroundColor = color;
	}

	public int TextColor(){
		return _nTextColor;
	}
	
	public void setTextColor(int color){
		_nTextColor = color;
	}

	public int WayColor(){
		return _nWayColor;
	}
	
	public void setWayColor(int color){
		_nWayColor = color;
	}
	
	public int AreaColor(){
		return _nAreaColor;
	}
	
	public void setAreaColor(int color){
		_nAreaColor = color;
	}
}
