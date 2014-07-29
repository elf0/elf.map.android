/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ZoomControls;

public class MainActivity extends Activity{
	private boolean _bCenter = false;
	private PointF _ptfGps = new PointF();

	private Map _map = new Map();
	private MapStyle _msMapStyle = new MapStyle();
	private MapPainter _mpMapPainter = new MapPainter();
	private MapView _mvMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Map
		String strMapFile = Environment.getExternalStorageDirectory().getAbsolutePath();
		strMapFile += "/elf/map";
		_map.Open(strMapFile);

		//PixelMap
		_mpMapPainter.SetMap(_map);
		_mpMapPainter.SetStyle(_msMapStyle);
		//		_mpMapPainter.SetLevel(10);

		//MapView
		_mvMap = new MapView(this, _mpMapPainter);
		_mvMap.SetLevel(10);
		_mvMap.SetCenter(_map.Center());

		setContentView(R.layout.activity_main);
		FrameLayout fl = (FrameLayout)findViewById(R.id.rlMainLayout);
		fl.addView(_mvMap, 0);

//		if(savedInstanceState != null){
//			_mvMap.SetGps(savedInstanceState.getFloat("Longitude", 0.0f), savedInstanceState.getFloat("Latitude", 0.0f));
//			//			_mvMap.setGps(2.0f, 2.0f);
//		}
//		else{
			SharedPreferences pref = this.getPreferences(Activity.MODE_PRIVATE);
			_mvMap.SetGps(pref.getFloat("Longitude", 0.0f), pref.getFloat("Latitude", 0.0f));
			//			_mvMap.setGps(1.0f, 1.0f);
//		}

		Button btnMapCenter = (Button)findViewById(R.id.btnMapCenter);
		btnMapCenter.setOnClickListener(_oclMapCenterListener); 

		Button btnGps = (Button)findViewById(R.id.btnGps);
		btnGps.setOnClickListener(_oclGpsListener); 

		ZoomControls zcsLevel = (ZoomControls)findViewById(R.id.zcsLevel);
		zcsLevel.setOnZoomInClickListener(_oclZoomInListener);
		zcsLevel.setOnZoomOutClickListener(_oclZoomOutListener);

		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null){
			_bCenter = true;
			float fX = (float)location.getLongitude();
			float fY = (float)location.getLatitude();
			_ptfGps.set(fX, fY);
			_mvMap.SetGps(fX, fY);
		}

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				float fX = (float)location.getLongitude();
				float fY = (float)location.getLatitude();
				_ptfGps.set(fX, fY);

				if(!_bCenter){
					_bCenter = true;
					_mvMap.SetCenter(fX, fY);
				}
				_mvMap.SetGps(fX, fY);
				_mvMap.invalidate();
				//				long id = Thread.currentThread().getId();
				//				id = 0;
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}});
	}

	private OnClickListener _oclMapCenterListener = new OnClickListener() {
		public void onClick(View v) {
			_mvMap.SetLevel(10);
			_mvMap.SetCenter(_map.Center());
			//			_mvMap.invalidate();
			ShowVisibilityDialog(_mvMap.GetVisibleTypes());
		}
	};

	private OnClickListener _oclGpsListener = new OnClickListener() {
		public void onClick(View v) {
			_mvMap.Home();
			//			_mvMap.invalidate();
		}
	};

	private OnClickListener _oclZoomOutListener = new OnClickListener() {
		public void onClick(View v) {
			_mvMap.DecreaseLevel();
			//			_mvMap.invalidate();
		}
	};

	private OnClickListener _oclZoomInListener = new OnClickListener() {
		public void onClick(View v) {
			_mvMap.IncreaseLevel();
		}
	};

	//private boolean _bDown = false;
	private PointF _ptfLastPoint = new PointF();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			_ptfLastPoint.set(x, y);
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:{
			float fOffsetX = x - _ptfLastPoint.x;
			float fOffsetY = y - _ptfLastPoint.y;
			if(fOffsetX >= 10.0f || fOffsetX <= -10.0f || fOffsetY >= 10.0f || fOffsetY <= -10.0f){
				_mvMap.PixelOffset(-(int)fOffsetX, -(int)fOffsetY);
				_mvMap.invalidate();
				_ptfLastPoint.set(x, y);
			}
		}break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		if(isFinishing()){
//			_exitDialog = new AlertDialog.Builder(this)
//			.setTitle("Save current GPS position?")
//			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//				@Override  
//				public void onClick(DialogInterface dialog, int which) {
//					SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);
//					SharedPreferences.Editor editor = pref.edit();
//					editor.putFloat("Longitude", _ptfGps.x);
//					editor.putFloat("Latitude", _ptfGps.y);
//					editor.commit();
//				}
//			})
//			.setNegativeButton("No", null)
//			.show();
//		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
//		_mvMap.SetGps(savedInstanceState.getFloat("Longitude", 0.0f), savedInstanceState.getFloat("Latitude", 0.0f));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
//		outState.putFloat("Longitude", _ptfGps.x);
//		outState.putFloat("Latitude", _ptfGps.y);
	}

	public void ShowVisibilityDialog(boolean[] szVisibleTypes){
		final String[] szItems = {"Location", "Water way", "Water", "Way", "Area"};    

		new AlertDialog.Builder(this)
		.setTitle("Select visible types")
		.setMultiChoiceItems(szItems, szVisibleTypes, new OnMultiChoiceClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				// TODO Auto-generated method stub
				_mvMap.SetVisibleType(Map.ObjectType.values()[which], isChecked);

				_mvMap.Redraw();
			}    
		})   
		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {

			@Override  
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		//		.setNegativeButton("Cancel", null)
		.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//		return super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.menu_filter:
			ShowVisibilityDialog(_mvMap.GetVisibleTypes());
			return true;
			//			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			new AlertDialog.Builder(this)
			.setTitle("Save current GPS position?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override  
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putFloat("Longitude", _ptfGps.x);
					editor.putFloat("Latitude", _ptfGps.y);
					editor.commit();
					finish();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override  
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putFloat("Longitude", 0.0f);
					editor.putFloat("Latitude", 0.0f);
					editor.commit();
					finish();
				}
			})
			.show();
			
		}

		return super.onKeyDown(keyCode, event);
	}
}
