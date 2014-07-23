/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

public class NamedLocation extends Point{
	public NamedLocation(String strName, long nLongitude, long nLatitude){
		super(nLongitude, nLatitude);
		this.strName = strName;
	}
	public String strName;
}