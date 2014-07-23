/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import java.util.ArrayList;
import java.util.List;

public class Way{
	public Way(){}

	public Way(String strName){
		this.strName = strName;
	}

	public void Add(long nLongitude, long nLatitude){
		_points.add(new Point(nLongitude, nLatitude));
	}

	public String strName;
	public List<Point> _points = new ArrayList<Point>();
}