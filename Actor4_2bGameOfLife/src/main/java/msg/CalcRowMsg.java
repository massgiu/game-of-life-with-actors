package msg;

import java.util.List;

import model.MyPoint;

public class CalcRowMsg {
	
	private int actId;
	private List<MyPoint> cellList;
	
	public CalcRowMsg(int actId, List<MyPoint> cellList) {
		this.actId = actId;
		this.cellList = cellList;
	}
	
	public int getActId() {
		return actId;
	}
	
	public List<MyPoint> getCellList(){
		return cellList;
	}
}
