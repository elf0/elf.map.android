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
	private List<Line> _waterWays = new ArrayList<Line>();
	private List<Line> _ways = new ArrayList<Line>();
	private List<Area> _waters = new ArrayList<Area>();
	private List<Area> _areas = new ArrayList<Area>();
	private Rect _rtLocationRect = new Rect();

	private int _nLevel = 0;
	private int _nTileSizeShift = 8;
	private float _fWidth = 256.0f;
	private float _fHeight = 128.0f;
	private float _fLongitudeToPixel = (float)(256.0 / 4294967296.0);
	private float _fPixelToLongitude = (float)(4294967296.0 / 256.0);
	private float _fLatitudeToPixel = (float)(128.0 / 4294967296.0);
	private float _fPixelToLatitude = (float)(4294967296.0 / 128.0);

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
	//		_fWidth = 1 << (_nLevel + nTileSizeShift);
	//		_fLongitudeToPixel = (float)(_fWidth / 4294967296.0);
	//		_fPixelToLongitude = (float)(4294967296.0 / _fWidth);
	//
	//		_nTileSizeShift = nTileSizeShift;
	//	}

	private void SetLevel(int nLevel){
		if(nLevel > 20)
			nLevel = 20;
		else if(nLevel < 0)
			nLevel = 0;

		int nWidth = 1 << (nLevel + _nTileSizeShift);

		_fWidth = nWidth;
		_fLongitudeToPixel = (float)(_fWidth / 4294967296.0);
		_fPixelToLongitude = (float)(4294967296.0 / _fWidth);

		_fHeight = nWidth >> 1;
//		_fLatitudeToPixel = (float)(_fHeight / 4294967296.0);
//		_fPixelToLatitude = (float)(4294967296.0 / _fHeight);
		_fLatitudeToPixel = (float)(_fWidth / 4294967296.0);
		_fPixelToLatitude = (float)(4294967296.0 / _fWidth);
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

	public long LongitudeToPixel(long nLongitude){
		return (int)(nLongitude * _fLongitudeToPixel);
	}

	public long PixelToLongitude(long nPixel){
		return (int)(nPixel * _fPixelToLongitude);
	}
	
	public long LatitudeToPixel(long nLatitude){
		return (int)(nLatitude * _fLatitudeToPixel);
	}

	public long PixelToLatitude(long nPixel){
		return (int)(nPixel * _fPixelToLatitude);
	}
	//	private long LongitudeToX(float fLongitude){
	//		long nLongitude = Map.Longitude_FloatToLong(fLongitude);
	//		return LongitudeToPixel(nLongitude);
	//	}
	//	
	//	private long LatitudeToY(float fLatitude){
	//		long nLatitude = Map.Latitude_FloatToLong(fLatitude);
	//		return LatitudeToPixel(nLatitude);
	//	}


	public void Begin(Canvas canvas, Point ptLocation, int nLevel){
		SetLevel(nLevel);

		_canvas = canvas;

		_rtLocationRect.SetSize(PixelToLongitude(canvas.getWidth()), PixelToLatitude(canvas.getHeight()));
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
			fX = LongitudeToPixel(location.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(location.Y() - _rtLocationRect.Top());
			//			canvas.drawPoint(fX, fY, _paint);
			_canvas.drawText(location.strName, fX, fY, _textPaint);
		}
	}

	public void DrawWaterWays(){
		List<Line> ways = SelectWaterWays(_rtLocationRect);

		Path path = new Path();
		_paint.setColor(_style.WaterColor());
		_paint.setStyle(Paint.Style.STROKE);

		float fX;
		float fY;
		Point point;
		for(Line way: ways){
			point = way._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way._points.length; i < n; ++i){
				point = way._points[i];
				fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
				fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
				path.lineTo(fX, fY);
			}
			_canvas.drawPath(path, _paint);
			path.reset();

		}
	}
	
	public void DrawWays(){
		List<Line> ways = SelectWays(_rtLocationRect);

		Path path = new Path();
		_paint.setColor(_style.WayColor());
		_paint.setStyle(Paint.Style.STROKE);

		float fX;
		float fY;
		Point point;
		for(Line way: ways){
			point = way._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way._points.length; i < n; ++i){
				point = way._points[i];
				fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
				fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
				path.lineTo(fX, fY);
			}
			_canvas.drawPath(path, _paint);
			path.reset();

		}
	}

	public void DrawWaters(){
		List<Area> waters = SelectWaters(_rtLocationRect);

		Path path = new Path();
		_paint.setColor(_style.WaterColor());
		_paint.setStyle(Paint.Style.FILL);

		float fX;
		float fY;
		Point point;
		for(Area water: waters){
			point = water._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = water._points.length; i < n; ++i){
				point = water._points[i];
				fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
				fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
				path.lineTo(fX, fY);
			}
			path.close();
			_canvas.drawPath(path, _paint);
			path.reset();
		}
	}
	
	public void DrawNameOfWaterWays(){
		float fX;
		float fY;
		Point point;

		for(Line way: _waterWays){
			point = way._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(way._strName, fX, fY, _textPaint);
		}
	}
	
	public void DrawNameOfWaters(){
		float fX;
		float fY;
		Point point;

		for(Area water: _waters){
			point = water._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(water._strName, fX, fY, _textPaint);
		}
	}
	
	public void DrawNameOfWays(){
		float fX;
		float fY;
		Point point;

		for(Line way: _ways){
			point = way._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(way._strName, fX, fY, _textPaint);
		}
	}
	
	public void DrawAreas(){
		List<Area> areas = SelectAreas(_rtLocationRect);

		Path path = new Path();
		_paint.setColor(_style.AreaColor());
		_paint.setStyle(Paint.Style.FILL);

		float fX;
		float fY;
		Point point;
		for(Area area: areas){
			point = area._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = area._points.length; i < n; ++i){
				point = area._points[i];
				fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
				fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
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
		Point point;

		for(Area area: _areas){
			point = area._points[0];
			fX = LongitudeToPixel(point.X() - _rtLocationRect.Left());
			fY = LatitudeToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(area._strName, fX, fY, _textPaint);
		}
	}

	private RectF _rtfRect = new RectF();

	public void DrawOval(Point ptLocation, float fRadiusX, float fRadiusY, Paint paint){
		if(!_rtLocationRect.Contains(ptLocation))
			return;

		float fX = LongitudeToPixel(ptLocation.X() - _rtLocationRect.Left());
		float fY = LatitudeToPixel(ptLocation.Y() - _rtLocationRect.Top());
		_rtfRect.set(fX - fRadiusX, fY - fRadiusY, fX + fRadiusX, fY + fRadiusY);
		_canvas.drawOval(_rtfRect, paint);
	}

	public void DrawText(Point ptLocation, String strText, Paint paint){
		if(!_rtLocationRect.Contains(ptLocation))
			return;

		float fX = LongitudeToPixel(ptLocation.X() - _rtLocationRect.Left());
		float fY = LatitudeToPixel(ptLocation.Y() - _rtLocationRect.Top());
		_canvas.drawText(strText, fX, fY, paint);

	}

	private List<NamedLocation> SelectLocations(Rect rtLocationRect){
		_locations.clear();
		_map.SelectLocations(rtLocationRect, _locations);
		return _locations;
	}
	
	private List<Line> SelectWaterWays(Rect rtLocationRect){
		_waterWays.clear();
		_map.SelectWaterWays(rtLocationRect, _waterWays);
		return _waterWays;
	}

	private List<Line> SelectWays(Rect rtLocationRect){
		_ways.clear();
		_map.SelectWays(rtLocationRect, _ways);
		return _ways;
	}

	private List<Area> SelectWaters(Rect rtLocationRect){
		_waters.clear();
		_map.SelectWaters(rtLocationRect, _waters);
		return _waters;
	}

	private List<Area> SelectAreas(Rect rtLocationRect){
		_areas.clear();
		_map.SelectAreas(rtLocationRect, _areas);
		return _areas;
	}
}


