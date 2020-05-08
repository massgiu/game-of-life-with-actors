package msg;
import view.ControlPanel;

public class ControlPanelMsg {

	ControlPanel  controlPanel;
	
	public ControlPanelMsg(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}
}
