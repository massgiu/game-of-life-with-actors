package actor;
import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.MyPoint;
import msg.CompleteRowMsg;
import msg.CalcRowMsg;

public class ActWorkerFull extends AbstractActor{
	
	private int numOfActor;
	private int numRow;
	private int numCol;

	static Props props(int numOfActor,int numRow, int numCol) {
	    return Props.create(ActWorkerFull.class, () -> new ActWorkerFull(numOfActor,numRow,numCol));
	}
	
	public ActWorkerFull(int numOfActor,int numRow, int numCol) {
		this.numOfActor = numOfActor;
		this.numRow = numRow;
		this.numCol = numCol;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(CalcRowMsg.class, msg -> {
				
				int actId = msg.getActId();
				int baseOffSet = numRow/numOfActor;
				int startRow=baseOffSet*(actId);
				boolean[][] gameBoard, gameBoardAct;
				final int stopRow;
				List<MyPoint> cellList  = msg.getCellList();
				int rowGameBoard;
				if (actId!=(numOfActor-1)){
					rowGameBoard=baseOffSet + 2;
					stopRow = baseOffSet*(actId+1);
				} else {
					int rest = numRow%numOfActor;
					rowGameBoard=baseOffSet+rest + 2;
					stopRow = baseOffSet*(actId+1) + rest;
				}
				gameBoard = new boolean[rowGameBoard][numCol + 2];
				gameBoardAct = new boolean[rowGameBoard][numCol + 2];
//				System.out.println("startRow "+startRow+" stopRow "+stopRow+" id"+actId);
				
				cellList.forEach(cell->{ // Considero solo le celle di competenza dell'attore
					if (cell.getY() >= startRow && cell.getY() <= stopRow) {
						gameBoard[(cell.getY()-baseOffSet*actId)+1][cell.getX()+1] = true; //shifto di 1 colonna e riga
					}
					if (actId>0 && cell.getY() == (startRow-1)) { 
						gameBoard[0][cell.getX()+1] = true;
					}
				});
				gameBoardAct = updateState(gameBoard,(stopRow-startRow),numCol);
				
				//aggiorno nextGameBoard (celle al nuovo stato) e creo la lista con i punti
				List<MyPoint> nextRowList = new ArrayList<>();
				for (int i=0; i<(stopRow-startRow); i++) { // numRow
					for (int j = 0; j < numCol; j++) { // numCol
						if (gameBoardAct[i][j]) {
							nextRowList.add(new MyPoint(j, i+baseOffSet*actId));
						}
					}
				}

				getSender().tell(new CompleteRowMsg(actId, nextRowList), getSelf());
				
			}).build();
	}
	
	public static boolean[][] updateState(boolean[][] gameBoard, int stopRow,int numOfCol){
        boolean[][] gameBoardAct = new boolean[stopRow+2][numOfCol+2];
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
                    	gameBoardAct[i-1][j-1]=true;
                    } 
                } else if (surrounding == 3){ // Cell is dead, will the cell be given birth? (3) - Regola2
                	gameBoardAct[i-1][j-1]=true;
                }
            }
        }
        return gameBoardAct;
	}
}
