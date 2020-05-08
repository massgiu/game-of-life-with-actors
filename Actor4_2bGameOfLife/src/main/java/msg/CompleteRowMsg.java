package msg;

import java.util.List;

import model.MyPoint;

public class CompleteRowMsg {
	
	private int actId;
	private List<MyPoint> rowList;
	
	public CompleteRowMsg(int actId, List<MyPoint> rowList) {
		this.actId = actId;
		this.rowList = rowList;
	}
	
	public int getActorId() {
		return actId;
	}
	
	public List<MyPoint> getRowComputed(){
		return rowList;
	}

}
