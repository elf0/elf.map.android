/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

public class Location{
	public Location(){}

	public Location(float longitude, float latitude){
		Set(longitude, latitude);
	}

	public Location(Location location){
		Set(location.Longitude(), location.Latitude());
	}

	public float Longitude(){
		return _longitude;
	}
	
	public float Latitude(){
		return _latitude;
	}

	public void SetLongitude(float longitude){
		this._longitude = longitude;
	}
	
	public void SetLatitude(float latitude){
		this._latitude = latitude;
	}

	public void Set(float longitude, float latitude){
		SetLongitude(longitude);
		SetLatitude(latitude);
	}

	public void Set(Location location){
		Set(location.Longitude(), location.Latitude());
	}

	private float _longitude;
	private float _latitude;
}
