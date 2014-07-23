/*
 * License: Public Domain
 * Author: elf
 * EMail: elf198012@gmail.com
 */

package elf.map;

public class Rect{
	public Rect(){
		_ptLeftTop = new Point();
		_ptRightBottom = new Point();
	}

	public Rect(long left, long top, long width, long height){
		_ptLeftTop = new Point(left, top);
		_ptRightBottom = new Point(left + width, top + height);
	}

	public Rect(Rect rect){
		_ptLeftTop = new Point(rect.LeftTop());
		_ptRightBottom = new Point(rect.RightBottom());
	}

	public long Left(){
		return _ptLeftTop.X();
	}

	public long Top(){
		return _ptLeftTop.Y();
	}

	public long Width(){
		return _ptRightBottom.X() - _ptLeftTop.X();
	}

	public long Height(){
		return _ptRightBottom.Y() - _ptLeftTop.Y();
	}

	public Point LeftTop(){
		return _ptLeftTop;
	}

	public Point RightBottom(){
		return _ptRightBottom;
	}

	public void GetSize(Size size){
		size.Set(Width(), Height());
	}

	public void SetLeft(long left){
		long w = Width();
		_ptLeftTop.SetX(left);
		_ptRightBottom.SetX(left + w);
	}

	public void SetTop(long top){
		long h = Height();
		_ptLeftTop.SetY(top);
		_ptRightBottom.SetY(top + h);
	}

	public void SetWidth(long width){
		_ptRightBottom.SetX(_ptLeftTop.X() + width);
	}

	public void SetHeight(long height){
		_ptRightBottom.SetY(_ptLeftTop.Y() + height);
	}

	public void SetLeftTop(long left, long top){
		SetLeft(left);
		SetTop(top);
	}

	public void SetLeftTop(Point ptLeftTop){
		SetLeftTop(ptLeftTop.X(), ptLeftTop.Y());
	}

	public void SetCenter(long x, long y){
		long w = Width();
		long h = Height();
		long w2 = w / 2;
		long h2 = h / 2;
		_ptLeftTop.Set(x - w2, y - h2);
		SetSize(w, h);
	}

	public void SetCenter(Point ptCenter){
		SetCenter(ptCenter.X(), ptCenter.Y());
	}

	public void GetCenter(Point ptCenter){
		long w = Width();
		long h = Height();
		long w2 = w / 2;
		long h2 = h / 2;
		ptCenter.Set(_ptLeftTop.X() + w2, _ptLeftTop.Y() + h2);
	}
	
	public void SetSize(long width, long height){
		SetWidth(width);
		SetHeight(height);
	}

	public void SetSize(Size size){
		SetSize(size.Width(), size.Height());
	}

	public void Set(long left, long top, long width, long height){
		_ptLeftTop.Set(left, top);
		SetSize(width, height);
	}

	public void Set(Rect rect){
		Set(rect.Left(), rect.Top(), rect.Width(), rect.Height());
	}

	public void Offset(long x, long y){
		_ptLeftTop.Offset(x, y);
		_ptRightBottom.Offset(x, y);
	}

	public boolean Contains(long x, long y){
		return x < _ptRightBottom.X() && y < _ptRightBottom.Y() && x >= _ptLeftTop.X() && y >= _ptLeftTop.Y();
	}

	public boolean Contains(Point point){
		return Contains(point.X(), point.Y());
	}

	private Point _ptLeftTop;
	private Point _ptRightBottom;
}