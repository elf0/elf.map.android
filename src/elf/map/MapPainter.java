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

//	private int _nLevel = 0;
	private float _fLocationToPixel;
	private float _fPixelToLocation;
	//
	private Paint _paint = new Paint();
	private Paint _textPaint = new Paint();

	public MapPainter(){
		_paint.setAntiAlias(true);

		_textPaint.setAntiAlias(true);
		_textPaint.setTextSize(20.0f);
		_textPaint.setTextAlign(Align.CENTER);
	}



	public void SetStyle(MapStyle style){
		_style = style;
	}

	public void SetMap(Map map){
		_map = map;
	}

	public long LocationToPixel(long nLocation){
		return (long)(nLocation * _fLocationToPixel);
	}

	public long PixelToLocation(long nPixel){
		return (long)(nPixel * _fPixelToLocation);
	}
	
	//canvas's width must equal it's height
	public int Begin(Canvas canvas, Point ptLocation, int nLevel){
		if(nLevel > 20)
			nLevel = 20;
		else if(nLevel < 0)
			nLevel = 0;
		
		long nSize = canvas.getWidth();
		long nVirtualSize = (1L << nLevel) * nSize;
		
		_fLocationToPixel = (float)(nVirtualSize / 4294967296.0);
		_fPixelToLocation = (float)(4294967296.0 / nVirtualSize);

//		_nLevel = nLevel;

		_canvas = canvas;

		_rtLocationRect.SetSize(PixelToLocation(nSize), PixelToLocation(nSize));
		_rtLocationRect.SetCenter(ptLocation.X(), ptLocation.Y());

		_textPaint.setColor(_style.TextColor());
		
		return nLevel;
	}

	public void End(){
		_waters.clear();
		_areas.clear();
		_waterWays.clear();
		_ways.clear();
		_locations.clear();
		_canvas = null;
	}

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
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way._points.length; i < n; ++i){
				point = way._points[i];
				fX = LocationToPixel(point.X() - _rtLocationRect.Left());
				fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
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
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way._points.length; i < n; ++i){
				point = way._points[i];
				fX = LocationToPixel(point.X() - _rtLocationRect.Left());
				fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
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
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = water._points.length; i < n; ++i){
				point = water._points[i];
				fX = LocationToPixel(point.X() - _rtLocationRect.Left());
				fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
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
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(way._strName, fX, fY, _textPaint);
		}
	}
	
	public void DrawNameOfWaters(){
		float fX;
		float fY;
		Point point;

		for(Area water: _waters){
			point = water._points[0];
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(water._strName, fX, fY, _textPaint);
		}
	}
	
	public void DrawNameOfWays(){
		float fX;
		float fY;
		Point point;

		for(Line way: _ways){
			point = way._points[0];
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
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
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = area._points.length; i < n; ++i){
				point = area._points[i];
				fX = LocationToPixel(point.X() - _rtLocationRect.Left());
				fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
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
			fX = LocationToPixel(point.X() - _rtLocationRect.Left());
			fY = LocationToPixel(point.Y() - _rtLocationRect.Top());
			_canvas.drawText(area._strName, fX, fY, _textPaint);
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


