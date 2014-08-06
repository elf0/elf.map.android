/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import java.util.ArrayList;
import java.util.LinkedList;
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

	private List<NamedLocation> _locations = new LinkedList<NamedLocation>();
	private List<Line> _waterWays = new LinkedList<Line>();
	private List<Line> _ways = new LinkedList<Line>();
	private List<Area> _waters = new LinkedList<Area>();
	private List<Area> _areas = new LinkedList<Area>();
	//	private RectF _rtfBoundingRect = new RectF();
	private Rect _rtBoundingRect = new Rect();

	//	private int _nLevel = 0;
	private float _fLocationLongToPixel;
	private float _fPixelToLocationLong;
	//	private float _fPixelToLocation;
	//	private float _fLocationToPixel;
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

	public long LocationLongToPixel(long nLocation){
		return (long)(nLocation * _fLocationLongToPixel);
	}

	public long PixelToLocationLong(long nPixel){
		return (long)(nPixel * _fPixelToLocationLong);
	}

	//	public float PixelToLocation(long nPixel){
	//		return (long)(nPixel * _fPixelToLocation);
	//	}
	//	
	//	public long LocationToPixel(float fLocation){
	//		return (long)(fLocation * _fLocationToPixel);
	//	}

	//canvas's width must equal it's height
	public int Begin(Canvas canvas, Point ptLocation, int nLevel){
		if(nLevel > 20)
			nLevel = 20;
		else if(nLevel < 8)
			nLevel = 8;

		long nSize = canvas.getWidth();
		long nVirtualSize = (1L << nLevel) * nSize;

		_fLocationLongToPixel = (float)(nVirtualSize / 4294967296.0);
		_fPixelToLocationLong = (float)(4294967296.0 / nVirtualSize);
		//		_fPixelToLocation = (float)(360.0 / nVirtualSize);
		//		_fLocationToPixel = (float)(nVirtualSize / 360.0);

		//		float fLocationSize = PixelToLocation(nSize);
		//		float fHalfLocationSize = fLocationSize / 2.0f;
		//		_nLevel = nLevel;

		_textPaint.setColor(_style.TextColor());

		//		_rtfBoundingRect.set(ptfLocation.x - fHalfLocationSize, ptfLocation.y - fHalfLocationSize, ptfLocation.x + fHalfLocationSize, ptfLocation.y + fHalfLocationSize);

		long nLocationLongSize = PixelToLocationLong(nSize);
//		_rtBoundingRect.SetSize(nLocationLongSize, nLocationLongSize > 2147483648L? 2147483648L : nLocationLongSize);
		_rtBoundingRect.SetSize(nLocationLongSize, nLocationLongSize);
		_rtBoundingRect.SetCenter(ptLocation.X(), ptLocation.Y());

		if(_rtBoundingRect.Left() < 0){
			_rtBoundingRect.SetLeft(0);
			_rtBoundingRect.SetWidth(nLocationLongSize);
			Point ptCenter = new Point();
			_rtBoundingRect.GetCenter(ptCenter);
			ptLocation.SetX(ptCenter.X());
		}

		if(_rtBoundingRect.Top() < 0){
			_rtBoundingRect.SetTop(0);
			_rtBoundingRect.SetHeight(nLocationLongSize);
			Point ptCenter = new Point();
			_rtBoundingRect.GetCenter(ptCenter);
			ptLocation.SetY(ptCenter.Y());
		}

		if(_rtBoundingRect.Right() > 4294967296L){
			_rtBoundingRect.SetLeft(4294967296L - nLocationLongSize);
			_rtBoundingRect.SetWidth(nLocationLongSize);
			Point ptCenter = new Point();
			_rtBoundingRect.GetCenter(ptCenter);
			ptLocation.SetX(ptCenter.X());
		}

		if(_rtBoundingRect.Bottom() > 2147483648L){
			_rtBoundingRect.SetTop(2147483648L - nLocationLongSize);
			_rtBoundingRect.SetHeight(nLocationLongSize);
			Point ptCenter = new Point();
			_rtBoundingRect.GetCenter(ptCenter);
			ptLocation.SetY(ptCenter.Y());
		}

		_map.Load(_rtBoundingRect);

		_canvas = canvas;

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
		List<NamedLocation> locations = SelectLocations();

		float fX;
		float fY;
		for(NamedLocation location: locations){
			fX = LocationLongToPixel(location.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(location.Y() - _rtBoundingRect.Top());
			//			canvas.drawPoint(fX, fY, _paint);
			_canvas.drawText(location.strName, fX, fY, _textPaint);
		}
//		locations.clear();
	}

	public void DrawWaterWays(){
		List<Line> ways = SelectWaterWays();

		Path path = new Path();
		_paint.setColor(_style.WaterColor());
		_paint.setStyle(Paint.Style.STROKE);

		float fX;
		float fY;
		Point point;
		for(Line way: ways){
			point = way.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way.Points(); i < n; ++i){
				point = way.getPoint(i);
				fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
				fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
				path.lineTo(fX, fY);
			}
			_canvas.drawPath(path, _paint);
			path.reset();

		}
	}

	public void DrawLines(){
		List<Line> ways = SelectWays();

		Path path = new Path();
		_paint.setColor(_style.WayColor());
		_paint.setStyle(Paint.Style.STROKE);

		float fX;
		float fY;
		Point point;
		for(Line way: ways){
			point = way.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = way.Points(); i < n; ++i){
				point = way.getPoint(i);
				fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
				fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
				path.lineTo(fX, fY);
			}
			_canvas.drawPath(path, _paint);
			path.reset();

		}
	}

	public void DrawWaters(){
		List<Area> waters = SelectWaters();

		Path path = new Path();
		_paint.setColor(_style.WaterColor());
		_paint.setStyle(Paint.Style.FILL);

		float fX;
		float fY;
		Point point;
		for(Area water: waters){
			point = water.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = water.Points(); i < n; ++i){
				point = water.getPoint(i);
				fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
				fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
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
			point = way.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			_canvas.drawText(way._strName, fX, fY, _textPaint);
		}
	}

	public void DrawNameOfWaters(){
		float fX;
		float fY;
		Point point;

		for(Area water: _waters){
			point = water.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			_canvas.drawText(water._strName, fX, fY, _textPaint);
		}
	}

	public void DrawNameOfWays(){
		float fX;
		float fY;
		Point point;

		for(Line way: _ways){
			point = way.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			_canvas.drawText(way._strName, fX, fY, _textPaint);
		}
	}

	public void DrawAreas(){
		List<Area> areas = SelectAreas();

		Path path = new Path();
		_paint.setColor(_style.AreaColor());
		_paint.setStyle(Paint.Style.FILL);

		float fX;
		float fY;
		Point point;
		for(Area area: areas){
			point = area.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			path.moveTo(fX, fY);
			for(int i = 1, n = area.Points(); i < n; ++i){
				point = area.getPoint(i);
				fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
				fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
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
			point = area.getPoint(0);
			fX = LocationLongToPixel(point.X() - _rtBoundingRect.Left());
			fY = LocationLongToPixel(point.Y() - _rtBoundingRect.Top());
			_canvas.drawText(area._strName, fX, fY, _textPaint);
		}
	}

	private RectF _rtfRect = new RectF();

	public void DrawOval(Point ptLocation, float fRadiusX, float fRadiusY, Paint paint){
		if(!_rtBoundingRect.Contains(ptLocation))
			return;

		float fX = LocationLongToPixel(ptLocation.X() - _rtBoundingRect.Left());
		float fY = LocationLongToPixel(ptLocation.Y() - _rtBoundingRect.Top());
		_rtfRect.set(fX - fRadiusX, fY - fRadiusY, fX + fRadiusX, fY + fRadiusY);
		_canvas.drawOval(_rtfRect, paint);
	}

	public void DrawText(Point ptLocation, String strText, Paint paint){
		if(!_rtBoundingRect.Contains(ptLocation.X(), ptLocation.Y()))
			return;

		float fX = LocationLongToPixel(ptLocation.X() - _rtBoundingRect.Left());
		float fY = LocationLongToPixel(ptLocation.Y() - _rtBoundingRect.Top());
		_canvas.drawText(strText, fX, fY, paint);

	}

	private List<NamedLocation> SelectLocations(){
		_locations.clear();
		_map.SelectLocations(_locations);
		return _locations;
	}

	private List<Line> SelectWaterWays(){
		_waterWays.clear();
		_map.SelectWaterWays(_waterWays);
		return _waterWays;
	}

	private List<Line> SelectWays(){
		_ways.clear();
		_map.SelectWays(_ways);
		return _ways;
	}

	private List<Area> SelectWaters(){
		_waters.clear();
		_map.SelectWaters(_waters);
		return _waters;
	}

	private List<Area> SelectAreas(){
		_areas.clear();
		_map.SelectAreas(_areas);
		return _areas;
	}
}


