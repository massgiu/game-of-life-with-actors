package model;

public class MyPoint {
	
	private int x,y;
	
	public MyPoint (int x, int y){
		this.x= x;
		this.y= y;
	}
	
	public MyPoint getPoint(){
		return this;
	}
	
	public void setX(int x){
		this.x=x;
	}
	
	public void setY(int y){
		this.y=y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

	@Override
	public String toString() {
		return "MyPoint [x=" + x + ", y=" + y + "]";
	}
	
	
}
