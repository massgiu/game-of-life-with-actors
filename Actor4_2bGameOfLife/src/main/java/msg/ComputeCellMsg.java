package msg;
import java.util.List;

public class ComputeCellMsg {
	
	private int stopRow, numCol;
	private boolean[][] gameBoard;
	
	public ComputeCellMsg(int numCol, int stopRow, boolean[][] gameBoard) {
		this.numCol = numCol;
		this.gameBoard = gameBoard;
		this.stopRow = stopRow;
	}
	
	public int getStopRow() {
		return stopRow;
	}

	public int getNumCol() {
		return numCol;
	}

	public boolean[][] getGameBoard() {
		return gameBoard;
	}
}
