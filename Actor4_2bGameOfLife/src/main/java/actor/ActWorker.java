package actor;
import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.MyPoint;
import msg.CalcRowMsg;
import msg.CompleteRowMsg;
import msg.ComputeCellMsg;

public class ActWorker extends AbstractActor{
	
	private int numOfActor, actId;
	private int numRow;
	private int numCol;
	private int baseOffSet, startRow, stopRow;
	private ActorRef utilsAct, actController;

	static Props props(int numOfActor, int numRow, int numCol, ActorRef utilsAct) {
		return Props.create(ActWorker.class, () -> new ActWorker(numOfActor, numRow, numCol, utilsAct));
	}
	
	public ActWorker(int numOfActor, int numRow, int numCol, ActorRef utilsAct) {
		this.numOfActor = numOfActor;
		this.numRow = numRow;
		this.numCol = numCol;
		this.utilsAct = utilsAct;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
		.match(CalcRowMsg.class, msg -> {
			boolean[][] gameBoard;
			final int rowGameBoard;
			
			actId = msg.getActId();
			actController = getSender();
			List<MyPoint> cellList = msg.getCellList();
			baseOffSet = numRow/numOfActor;
			startRow=baseOffSet*(actId);

			if (actId!=(numOfActor-1)){
				rowGameBoard=baseOffSet + 2;
				stopRow = baseOffSet*(actId+1);
			} else {
				int rest = numRow%numOfActor;
				rowGameBoard=baseOffSet+rest + 2;
				stopRow = baseOffSet*(actId+1) + rest;
			}
			gameBoard = new boolean[rowGameBoard][numCol + 2];
//			System.out.println("startRow "+startRow+" stopRow "+stopRow+" id"+actId);
			
			cellList.forEach(cell->{ // Considero solo le celle di competenza dell'attore
				if (cell.getY() >= startRow && cell.getY() <= stopRow) {
					gameBoard[(cell.getY()-baseOffSet*actId)+1][cell.getX()+1] = true; //shifto di 1 colonna e riga
				}
				//nella riga 0 inserisco la riga superiore altrimenti vengono inseriti zeri
				if (actId>0 && cell.getY() == (startRow-1)) { 
					gameBoard[0][cell.getX()+1] = true;
				}
			});
			utilsAct.tell(new ComputeCellMsg(numCol, (stopRow-startRow), gameBoard),self());
			
		})
		.match(ComputeCellMsg.class, msg->{
			//aggiorno nextGameBoard (celle al nuovo stato) e creo la lista con i punti
			boolean[][] gameBoardAct = msg.getGameBoard();
			List<MyPoint> nextRowList = new ArrayList<>();
			for (int i=0; i<(stopRow-startRow); i++) { // numRow
				for (int j = 0; j < numCol; j++) { // numCol
					if (gameBoardAct[i][j]) {
						nextRowList.add(new MyPoint(j, i+baseOffSet*actId));
					}
				}
			}
			actController.tell(new CompleteRowMsg(actId, nextRowList), getSelf());
		})
		.build();
	}
}
