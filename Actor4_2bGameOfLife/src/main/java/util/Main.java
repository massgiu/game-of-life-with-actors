package util;

import javax.swing.SwingUtilities;

import actor.ActController;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import view.ControlPanel;


public class Main {
	
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("MySystem");
		ActorRef coordAct = system.actorOf(ActController.props(system));
		ControlPanel controlPanel = new ControlPanel(system,coordAct);
		SwingUtilities.invokeLater(() -> {
			controlPanel.setVisible(true);
		});
	}
}
