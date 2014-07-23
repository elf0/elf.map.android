/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
//import android.graphics.Point;
//import android.graphics.PointF;
//import android.graphics.RectF;
//import android.util.AttributeSet;
import android.view.View;

public class MapView extends View{
	private int _nLevel = 0;
	private MapPainter _mpMapPainter;
	private Bitmap _bmpMap;

	private Paint _paint;

	private Point _ptGps = new Point();
	private RectF _rtfGps = new RectF();
	private List<Point> _gpsTrace = new ArrayList<Point>();

	private Point _ptLocationCenter = new Point();
	private Size _szMapMargin = new Size();

	private android.graphics.Rect _rtSrc = new android.graphics.Rect();
	private RectF _rtfDest = new RectF();


	public MapView(Context context, MapPainter painter) {
		super(context);
		_mpMapPainter = painter;

		_paint = new Paint();
		_paint.setAntiAlias(true);
		_paint.setTextSize(20.0f);
		_paint.setTextAlign(Align.CENTER);
	}

	public void SetGps(float fLongitude, float fLatitude){
		long nLongitude = Map.Longitude_FloatToLong(fLongitude);
		long nLatitude = Map.Latitude_FloatToLong(fLatitude);

		if(nLongitude == _ptGps.X() && nLatitude == _ptGps.Y())
			return;

		_ptGps.Set(nLongitude, nLatitude);

		if(_gpsTrace.size() >= 1000)
			_gpsTrace.remove(0);

		_gpsTrace.add(new Point(nLongitude, nLatitude));
	}

	private void _Home(){
		_ptLocationCenter.Set(_ptGps);
	}

	public void Home(){
		_Home();

		if(_bmpMap != null){
			Redraw();
			invalidate();
		}
	}

	private void _SetCenter(float fLongitude, float fLatitude){
		long nLongitude = Map.Longitude_FloatToLong(fLongitude);
		long nLatitude = Map.Latitude_FloatToLong(fLatitude);

		_ptLocationCenter.Set(nLongitude, nLatitude);
		int w = getWidth();
		int h = getHeight();
		_rtSrc.set((int)_szMapMargin.Width(), (int)_szMapMargin.Height(), (int)_szMapMargin.Width() + w, (int)_szMapMargin.Height() + h);
	}

	public void SetCenter(float fLongitude, float fLatitude){
		_SetCenter(fLongitude, fLatitude);
		if(_bmpMap != null){
			Redraw();
			invalidate();
		}
	}

	public void PixelOffset(int nX, int nY){
		long nLongitudeOffset = _mpMapPainter.PixelToLocation(nX);
		long nLatitudeOffset = _mpMapPainter.PixelToLocation(nY);

		_ptLocationCenter.Offset(nLongitudeOffset, nLatitudeOffset);

		if(_bmpMap != null){
			Redraw();
			invalidate();
		}
	}

	public void SetLevel(int nLevel){
		if(nLevel > 20)
			nLevel = 20;
		else if(nLevel < 0)
			nLevel = 0;
		_nLevel = nLevel;
	}

	public void DecreaseLevel(){
		SetLevel(_nLevel - 1);

		if(_bmpMap != null){
			Redraw();
			invalidate();
		}
	}

	public void IncreaseLevel(){
		SetLevel(_nLevel + 1);

		if(_bmpMap != null){
			Redraw();
			invalidate();
		}
	}

	private void Redraw(){
		Canvas canvas = new Canvas(_bmpMap);
		_mpMapPainter.Begin(canvas, _ptLocationCenter, _nLevel);
		_mpMapPainter.DrawBackground();
		_mpMapPainter.DrawAreas();
		_mpMapPainter.DrawWays();
		_mpMapPainter.DrawLocations();
		_mpMapPainter.DrawNameOfAreas();

		_paint.setColor(Color.BLUE);
		_mpMapPainter.DrawOval(_ptGps, 5.0f, 5.0f, _paint);

		_paint.setColor(Color.RED);
		_mpMapPainter.DrawText(_ptGps, String.valueOf(Map.Longitude_LongToFloat(_ptGps.X())) + ", " + String.valueOf(Map.Latitude_LongToFloat(_ptGps.Y())), _paint);

		_mpMapPainter.End();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//		if(_bmpMap != null)
		canvas.drawBitmap(_bmpMap, _rtSrc, _rtfDest, _paint);

		//		canvas.drawText(String.valueOf(_rtSrc.left) + ", " + String.valueOf(_rtSrc.top) + ", " + String.valueOf(_rtSrc.width()) + ", " + String.valueOf(_rtSrc.height()), 320, 20, _textPaint);
		//		canvas.drawText(String.valueOf(_rtfDest.left) + ", " + String.valueOf(_rtfDest.top) + ", " + String.valueOf(_rtfDest.width()) + ", " + String.valueOf(_rtfDest.height()), 320, 60, _textPaint);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		_rtSrc.right = _rtSrc.left + w;
		_rtSrc.bottom = _rtSrc.top + h;
		_rtfDest.right = w;
		_rtfDest.bottom = h;


		//		int nBmpHalfSize = (w > h? w : h);
		//		int nBmpHalfSize = (w > h? w : h) / 2;
		//		int nBmpSize = nBmpHalfSize * 2;
		//		_szMapMargin.Set((nBmpSize - w) / 2, (nBmpSize - h) / 2);
		_Home();

		if(_bmpMap != null)
			_bmpMap.recycle();


		//		_bmpMap = Bitmap.createBitmap(nBmpSize, nBmpSize,  Bitmap.Config.RGB_565);
		_bmpMap = Bitmap.createBitmap(w, h,  Bitmap.Config.RGB_565);

		Redraw();
	}
}