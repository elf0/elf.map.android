/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.PointF;

public class MapPainter {
	private Canvas _canvas;
	private Map _map;
	private MapStyle _style;

	private List<NamedLocation> _locations = new ArrayList<NamedLocation>();
	private List<Way> _ways = new ArrayList<Way>();
	private List<Area> _areas = new ArrayList<Area>();
	private Rect _rtLocationRect = new Rect();

	private int _nLevel = 0;
	private int _nTileSizeShift = 8;
	private float _fSize = 256.0f;
	private float _fLocationToPixel = (float)(256.0 / 4294967296.0);
	private float _fPixelToLocation = (float)(4294967296.0 / 256.0);

	//
	private Paint _paint = new Paint();
	private Paint _textPaint = new Paint();

	public MapPainter(){
		_paint.setAntiAlias(true);

		_textPaint.setAntiAlias(true);
		_textPaint.setTextSize(20.0f);
		_textPaint.setTextAlign(Align.CENTER);
	}

	//	public void setTileSizeShift(int nTileSizeShift){
	//		if(nTileSizeShift > 10)
	//			nTileSizeShift = 10;//1024
	//		else if(nTileSizeShift < 8)
	//			nTileSizeShift = 8;//256
	//
	//		_fSize = 1 << (_nLevel + nTileSizeShift);
	//		_fLocationToPixel = (float)(_fSize / 4294967296.0);
	//		_fPixelToLocation = (float)(4294967296.0 / _fSize);
	//
	//		_nTileSizeShift = nTileSizeShift;
	//	}

	private void SetLevel(int nLevel){
		if(nLevel > 20)
			nLevel = 20;
		else if(nLevel < 0)
			nLevel = 0;

		_fSize = 1 << (nLevel + _nTileSizeShift);
		_fLocationToPixel = (float)(_fSize / 4294967296.0);
		_fPixelToLocation = (float)(4294967296.0 / _fSize);

		_nLevel = nLevel;
	}

//	public void DecreaseLevel(){
//		SetLevel(_nLevel - 1);
//	}
//
//	public void IncreaseLevel(){
//		SetLevel(_nLevel + 1);
//	}

	public void SetStyle(MapStyle style){
		_style = style;
	}

	public void SetMap(Map map){
		_map = map;
	}

	public long LocationToPixel(long nLocation){
		return (int)(nLocation * _fLocationToPixel);
	}

	public long PixelToLocation(long nPixel){
		return (int)(nPixel * _fPixelToLocation);
	}

	//	private long LongitudeToX(float fLongitude){
	//		long nLongitude = Map.Longitude_FloatToLong(fLongitude);
	//		return LocationToPixel(nLongitude);
	//	}
	//	
	//	private long LatitudeToY(float fLatitude){
	//		long nLatitude = Map.Latitude_FloatToLong(fLatitude);
	//		return LocationToPixel(nLatitude);
	//	}


	public void Begin(Canvas canvas, Point ptLocation, int nLevel){
		SetLevel(nLevel);
		
		_canvas = canvas;

		_rtLocationRect.SetSize(PixelToLocation(canvas.getWidth()), PixelToLocation(canvas.getHeight()));
		_rtLocationRect.SetCenter(ptLocation.X(), ptLocation.Y());

		_textPaint.setColor(_style.TextColor());
	}
	
	public void End(){
		_areas.clear();
		_ways.clear();
		_locations.clear();
		_canvas = null;
	}

//	public void DrawMap(){
//		DrawBackground();
//		DrawAreas();
//		DrawWays();
//		DrawLocations();
//		DrawNameOfAreas();
//	}

	public void DrawBackground(){
		_canvas.drawColor(_style.BackgroundColor());
	}

	public void DrawLocations(){
		List<NamedLocation> locations = SelectLocations(_rtLocationRect);

		float fX;
		float fY;
		for(NamedLocation location: locations){
			fX = LocationToPixel(location.X() - _rtLocationRect.Left());
			fY = LocationToPixel(location.Y() - _rtLocationRect.Top());
			//			canvas.drawPoint(fX, fY, _paint);
			_canvas.drawText(location.strName, fX, fY, _textPaint);
		}
	}

	public void DrawWays(){
		List<Way> ways = SelectWays(_rtLocationRect);

		Path path = new Path();
		_paint.setColor(_style.WayColor());
		_paint.setStyle(Paint.Style.STROKE);

		float fX;
		float fY;
		Point location;
		for(Way way: ways){
			location = way._points.get(0);
			fX = LocationToPixel(location.X() - _rtLocationRect.Left());
			fY = LocationToPixel(location.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way._points.size(); i < n; ++i){
				location = way._points.get(i);
				fX = LocationToPixel(location.X() - _rtLocationRect.Left());
				fY = LocationToPixel(location.Y() - _rtLocationRect.Top());
				path.lineTo(fX, fY);
			}
			_canvas.drawPath(path, _paint);
			path.reset();

		}
	}

	public void DrawAreas(){
		List<Area> areas = SelectAreas(_rtLocationRect);

		Path path = new Path();
		_paint.setColor(_style.AreaColor());
		_paint.setStyle(Paint.Style.FILL);

		float fX;
		float fY;
		Point location;
		for(Area area: areas){
			location = area._points.get(0);
			fX = LocationToPixel(location.X() - _rtLocationRect.Left());
			fY = LocationToPixel(location.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = area._points.size(); i < n; ++i){
				location = area._points.get(i);
				fX = LocationToPixel(location.X() - _rtLocationRect.Left());
				fY = LocationToPixel(location.Y() - _rtLocationRect.Top());
				path.lineTo(fX, fY);
			}
			path.close();
			_canvas.drawPath(path, _paint);
			path.reset();
		}
	}

	public void DrawNameOfAreas(){
		float fX;
		float fY;
		Point location;

		for(Area area: _areas){
			location = area._points.get(0);
			fX = LocationToPixel(location.X() - _rtLocationRect.Left());
			fY = LocationToPixel(location.Y() - _rtLocationRect.Top());
			_canvas.drawText(area.strName, fX, fY, _textPaint);
		}
	}

	private RectF _rtfRect = new RectF();
	
	public void DrawOval(Point ptLocation, float fRadiusX, float fRadiusY, Paint paint){
		if(!_rtLocationRect.Contains(ptLocation))
			return;
		
		float fX = LocationToPixel(ptLocation.X() - _rtLocationRect.Left());
		float fY = LocationToPixel(ptLocation.Y() - _rtLocationRect.Top());
		_rtfRect.set(fX - fRadiusX, fY - fRadiusY, fX + fRadiusX, fY + fRadiusY);
		_canvas.drawOval(_rtfRect, paint);
	}

	public void DrawText(Point ptLocation, String strText, Paint paint){
		if(!_rtLocationRect.Contains(ptLocation))
			return;
		
		float fX = LocationToPixel(ptLocation.X() - _rtLocationRect.Left());
		float fY = LocationToPixel(ptLocation.Y() - _rtLocationRect.Top());
		_canvas.drawText(strText, fX, fY, paint);

	}

	private List<NamedLocation> SelectLocations(Rect rtLocationRect){
		_locations.clear();
		_map.SelectLocations(rtLocationRect, _locations);
		return _locations;
	}

	private List<Way> SelectWays(Rect rtLocationRect){
		_ways.clear();
		_map.SelectWays(rtLocationRect, _ways);
		return _ways;
	}

	private List<Area> SelectAreas(Rect rtLocationRect){
		_areas.clear();
		_map.SelectAreas(rtLocationRect, _areas);
		return _areas;
	}
}


