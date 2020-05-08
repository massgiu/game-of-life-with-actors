package actor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import model.MyPoint;
import msg.CompleteRowMsg;
import msg.CalcRowMsg;
import msg.ControlPanelMsg;
import msg.FillPercentMsg;
import msg.InitializeListMsg;
import util.Chrono;
import view.ControlPanel;
import view.GamePanel;

public class ActController extends AbstractActor{

	private static final int NUM_OF_ROW = 1500;
	private static final int NUM_OF_COL = 1500;
	private final static int NUM_OF_ACTOR = 50;
	
	private ActorSystem system;
	
	private int actId;
	private static int actorCount, nState, fillPercent;
	private ActorRef[] actWorker;
	private GamePanel gamePanel;
	private ControlPanel controlPanel;
	private List<MyPoint> cellList = new ArrayList<>();
	private Set<Integer> checkJobSet = new HashSet<>();
	private boolean stopped,exit;
	private ActorRef utilsAct;
	private Chrono chrono = new Chrono();
	
	public static Props props(ActorSystem system) {
	    return Props.create(ActController.class, () -> new ActController(system));
	}
	
	public ActController(ActorSystem system) {
		this.system = system;
	}
	
	@Override
	public void preStart() throws Exception {
		utilsAct = system.actorOf(ActUtils.props(NUM_OF_ROW,NUM_OF_COL));
		//Create worker actors
		actWorker = new ActorRef[NUM_OF_ACTOR];
		for (int actId=0; actId<NUM_OF_ACTOR; actId++) {
			actWorker[actId]= system.actorOf(ActWorker.props(NUM_OF_ACTOR,NUM_OF_ROW,NUM_OF_COL,utilsAct));
		}
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
		.match(ControlPanelMsg.class, msg->{
			controlPanel = msg.getControlPanel();
			gamePanel = controlPanel.getGamePanel();
		})
		.matchEquals("Start", msg->{
			System.out.println("Start Pressed");
//			chrono.start();
			stopped=false;
			for (int actId=0; actId<NUM_OF_ACTOR; actId++) {
				actWorker[actId].tell(new CalcRowMsg(actId,new ArrayList<>(cellList)), self());
			}
		})
		.matchEquals("Stop", msg -> {
			System.out.println("Stop Pressed");
			stopped = true;
		})
		.match(InitializeListMsg.class,msg-> {
			cellList = msg.getCellList();
			System.out.println(cellList.size());
			gamePanel.updateView(cellList, nState);

		})
		.match(CompleteRowMsg.class, msg -> {
			actId = msg.getActorId();
			if (checkJobSet.add(actId)) {
				cellList.addAll(msg.getRowComputed());
				actorCount++;
			}
			if(actorCount==NUM_OF_ACTOR && !stopped) {
				gamePanel.updateView(cellList, nState);
				Thread.sleep(50);
				nState += 1;
				//Send messages for a new computation
					for (int actId=0;actId<NUM_OF_ACTOR;actId++) {
						actWorker[actId].tell(new CalcRowMsg(actId,new ArrayList<>(cellList)),self());
					}
				cellList.clear();
				checkJobSet.clear();
				actorCount=0;
			} else if (actorCount==NUM_OF_ACTOR && exit) {
				System.exit(0);
			}
		})
		.match(FillPercentMsg.class, msg -> {
			List<MyPoint> tempCellList = new ArrayList<>();
			fillPercent = msg.getFillPercent();
			utilsAct.tell(new InitializeListMsg(fillPercent,tempCellList),self());
		})
		.matchEquals("Reset", msg -> {
			System.out.println("Reset pressed");
			utilsAct.tell("Reset",self());
		})
		.matchEquals("Exit", msg -> {
			System.out.println("Exit Pressed");
			if (stopped) {
				System.exit(0);
			} else {
				stopped = true;
				exit = true;
			}
		})
		.build();
	}
	
	public static int getNumOfRow() {
		return NUM_OF_ROW;
	}

	public static int getNumOfCol() {
		return NUM_OF_COL;
	}

}
