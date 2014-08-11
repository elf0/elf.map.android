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
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

public class MapView extends View{
	private int _nLevel = 0;
	private MapPainter _mpMapPainter;
	private Bitmap _bmpMap;

	private Paint _paint;

	private PointF _ptfGps = new PointF();
	private Point _ptGps = new Point();
	private List<Point> _gpsTrace = new ArrayList<Point>();

	private Point _ptLocationCenter = new Point();
	private Size _szMapMargin = new Size();

	private int _nWidth;
	private int _nHeight;
	private android.graphics.Rect _rtSrc = new android.graphics.Rect();
	private RectF _rtfDest = new RectF();


	public MapView(Context context, MapPainter painter) {
		super(context);
		_mpMapPainter = painter;

		_paint = new Paint();
		_paint.setAntiAlias(true);
		_paint.setTextSize(20.0f);
		_paint.setTextAlign(Align.CENTER);

		for(int i = 0, n = _szVisibleTypes.length; i < n; ++i)
			_szVisibleTypes[i] = true;
	}

	public void SetGps(float fLongitude, float fLatitude){
		if(fLongitude == _ptfGps.x && fLatitude == _ptfGps.y)
			return;

		long nLongitude = Map.LongitudeFloatToLong(fLongitude);
		long nLatitude = Map.LatitudeFloatToLong(fLatitude);


		_ptfGps.set(fLongitude, fLatitude);
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
			_Redraw();
			invalidate();
		}
	}

	private void resetSourceRect(){
		_rtSrc.set((int)_szMapMargin.Width(), (int)_szMapMargin.Height(), (int)_szMapMargin.Width() + _nWidth, (int)_szMapMargin.Height() + _nHeight);
	}

	private void _SetCenter(float fLongitude, float fLatitude){
		long nLongitude = Map.LongitudeFloatToLong(fLongitude);
		long nLatitude = Map.LatitudeFloatToLong(fLatitude);

		_ptLocationCenter.Set(nLongitude, nLatitude);
	}

	public void SetCenter(Point ptCenter){
		_ptLocationCenter.Set(ptCenter);

		if(_bmpMap != null){
			_Redraw();
			invalidate();
		}
	}

	public void SetCenter(float fLongitude, float fLatitude){
		_SetCenter(fLongitude, fLatitude);
		if(_bmpMap != null){
			_Redraw();
			invalidate();
		}
	}

	public void PixelOffset(int nX, int nY){
		if(_bmpMap == null)
			return;

		long nLongitudeOffset = _mpMapPainter.PixelToLocationLong(nX);
		long nLatitudeOffset = _mpMapPainter.PixelToLocationLong(nY);
		long nNewLongitude = _ptLocationCenter.X() + nLongitudeOffset;
		long nNewLatitude = _ptLocationCenter.Y() + nLatitudeOffset;

		if(nNewLongitude < 0 || nNewLongitude >= 4294967296L || nNewLatitude < 0 || nNewLatitude >= 2147483648L)
			return;

		_ptLocationCenter.Set(nNewLongitude, nNewLatitude);
		_rtSrc.offset(nX, nY);

		if((_rtSrc.left < 0 || _rtSrc.top < 0 || _rtSrc.right > _nBmpSize || _rtSrc.bottom > _nBmpSize)){
			_Redraw();
		}

		invalidate();
	}

	public void SetLevel(int nLevel){
		_nLevel = nLevel;

		if(_bmpMap != null){
			_Redraw();
			invalidate();
		}
	}

	public void DecreaseLevel(){
		--_nLevel;

		if(_bmpMap != null){
			_Redraw();
			invalidate();
		}
	}

	public void IncreaseLevel(){
		++_nLevel;

		if(_bmpMap != null){
			_Redraw();
			invalidate();
		}
	}

	private boolean[] _szVisibleTypes = new boolean[5];

	public boolean[] GetVisibleTypes(){
		return _szVisibleTypes;
	}

	public void SetVisibleType(Map.ObjectType type, boolean bVisible){
		_szVisibleTypes[type.ordinal()] = bVisible;
	}

	public void Redraw(){
		_Redraw();
		invalidate();
	}

	private void _Redraw(){
		resetSourceRect();
		Canvas canvas = new Canvas(_bmpMap);
		_nLevel = _mpMapPainter.Begin(canvas, _ptLocationCenter, _nLevel);
		_mpMapPainter.DrawBackground();

		if(_szVisibleTypes[Map.ObjectType.Area.ordinal()])
			_mpMapPainter.DrawAreas();

		if(_szVisibleTypes[Map.ObjectType.Water.ordinal()])
			_mpMapPainter.DrawWaters();

		if(_szVisibleTypes[Map.ObjectType.WaterWay.ordinal()])
			_mpMapPainter.DrawWaterWays();

		if(_szVisibleTypes[Map.ObjectType.Way.ordinal()])
			_mpMapPainter.DrawLines();

		if(_szVisibleTypes[Map.ObjectType.Location.ordinal()])
			_mpMapPainter.DrawLocations();

		if(_szVisibleTypes[Map.ObjectType.Area.ordinal()])
			_mpMapPainter.DrawNameOfAreas();

		if(_szVisibleTypes[Map.ObjectType.WaterWay.ordinal()])
			_mpMapPainter.DrawNameOfWaterWays();

		if(_szVisibleTypes[Map.ObjectType.Water.ordinal()])
			_mpMapPainter.DrawNameOfWaters();

		if(_szVisibleTypes[Map.ObjectType.Way.ordinal()])
			_mpMapPainter.DrawNameOfWays();

		_paint.setColor(Color.BLUE);
		_mpMapPainter.DrawOval(_ptGps, 5.0f, 5.0f, _paint);

		_paint.setColor(Color.RED);
		_mpMapPainter.DrawText(_ptGps, String.valueOf(_ptfGps.x) + ", " + String.valueOf(_ptfGps.y), _paint);

		_mpMapPainter.End();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//		if(_bmpMap != null)
		//		canvas.drawColor(0xFF808080);
		canvas.drawBitmap(_bmpMap, _rtSrc, _rtfDest, _paint);
		//		_paint.setColor(Color.RED);
		//		canvas.drawText(String.valueOf(_nLevel), 100, 100, _paint);

		//		canvas.drawText(String.valueOf(_rtSrc.left) + ", " + String.valueOf(_rtSrc.top) + ", " + String.valueOf(_rtSrc.width()) + ", " + String.valueOf(_rtSrc.height()), 320, 20, _textPaint);
		//		canvas.drawText(String.valueOf(_rtfDest.left) + ", " + String.valueOf(_rtfDest.top) + ", " + String.valueOf(_rtfDest.width()) + ", " + String.valueOf(_rtfDest.height()), 320, 60, _textPaint);

	}

	private int _nBmpSize;
	private int _nBmpOneQuarterSize;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		_nWidth = w;
		_nHeight = h;

		_rtSrc.right = _rtSrc.left + w;
		_rtSrc.bottom = _rtSrc.top + h;
		_rtfDest.right = w;
		_rtfDest.bottom = h;


		int nBmpHalfSize = (w > h? w : h);
		_nBmpSize = nBmpHalfSize << 1;
		_nBmpOneQuarterSize = nBmpHalfSize >> 1;

		_szMapMargin.Set(_nBmpOneQuarterSize + ((nBmpHalfSize - w) >> 1), _nBmpOneQuarterSize + ((nBmpHalfSize - h) >> 1));

		if(_bmpMap != null)
			_bmpMap.recycle();


		_bmpMap = Bitmap.createBitmap(_nBmpSize, _nBmpSize,  Bitmap.Config.RGB_565);

		_Redraw();
	}
}