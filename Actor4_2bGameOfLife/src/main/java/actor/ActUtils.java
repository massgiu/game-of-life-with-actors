package actor;

import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.MyPoint;
import msg.ComputeCellMsg;
import msg.InitializeListMsg;

public class ActUtils extends AbstractActor{
	
	private int numOfCol, numOfRow;
	private List<MyPoint> cellList;

	static Props props(int numOfRow, int numOfCol) {
	    return Props.create(ActUtils.class, () -> new ActUtils(numOfRow, numOfCol));
	}
	
	public ActUtils(int numOfRow, int numOfCol) {
		this.numOfCol = numOfCol;
		this.numOfRow = numOfRow;
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
		.match(InitializeListMsg.class, msg -> {
			cellList =msg.getCellList();

			int percent = msg.getFillPercent();
			for (int i = 0; i < numOfCol; i++) { // width ->x
				for (int j = 0; j < numOfRow; j++) { // height ->y
					if (Math.random() * 100 <= percent) {
						if (!cellList.contains(new MyPoint(i, j))) {
							cellList.add(new MyPoint(i, j));
						}
					}
				}
			}
			getSender().tell(new InitializeListMsg(percent,cellList), ActorRef.noSender());

		})
		.matchEquals("Reset", msg -> {
			getSender().tell(new InitializeListMsg(0,new ArrayList<>()), ActorRef.noSender());
		})
		.match(ComputeCellMsg.class, msg -> {
			int stopRow = msg.getStopRow();
			int numOfCol = msg.getNumCol();
			boolean[][] gameBoard = msg.getGameBoard();
			boolean[][] gameBoardNext = new boolean[stopRow+2][numOfCol+2];
			
			// Indici estremi della matrice non devono essere considerati altrimenti i-1, i+1->errore
	    	for (int i=1; i<=stopRow; i++) { //numRow
	    		for (int j=1; j<=numOfCol; j++) {//numCol
	                int surrounding = 0;
	                if (gameBoard[i-1][j-1]) { surrounding++; }
	                if (gameBoard[i-1][j])   { surrounding++; }
	                if (gameBoard[i-1][j+1]) { surrounding++; }
	                if (gameBoard[i][j-1])   { surrounding++; }
	                if (gameBoard[i][j+1])   { surrounding++; }
	                if (gameBoard[i+1][j-1]) { surrounding++; }
	                if (gameBoard[i+1][j])   { surrounding++; }
	                if (gameBoard[i+1][j+1]) { surrounding++; }
	                if (gameBoard[i][j]) { //caso in cui la cella è true
	                    // Cell is alive, Can the cell live? (2-3)
	                    if ((surrounding == 2) || (surrounding == 3)) {
	                    	gameBoardNext[i-1][j-1]=true;
	                    } 
	                } else if (surrounding == 3){ // Cell is dead, will the cell be given birth? (3) - Regola2
	                	gameBoardNext[i-1][j-1]=true;
	                }
	            }
	        }
	    	getSender().tell(new ComputeCellMsg(numOfCol,stopRow,gameBoardNext), self());
		})
		.build();
	}
}
