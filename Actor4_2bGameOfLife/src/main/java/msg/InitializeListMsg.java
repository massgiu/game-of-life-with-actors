package msg;

import java.util.List;

import model.MyPoint;

public class InitializeListMsg {
	
	private List<MyPoint> cellList;
	private int fillPercent;
	
	public InitializeListMsg(int fillPercent, List<MyPoint> cellList) {
		this.cellList = cellList;
		this.fillPercent = fillPercent;
	}

	public List<MyPoint> getCellList() {
		return cellList;
	}
	
	public int getFillPercent() {
		return fillPercent;
	}

}
