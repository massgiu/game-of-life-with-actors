package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import msg.ControlPanelMsg;
import msg.FillPercentMsg;
import util.Chrono;

public class ControlPanel extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final int OPTION_WINDOW_WIDHT = 300;
	private static final int OPTION_WINDOW_HEIGHT = 80;

	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
    private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(400, 400);
    
    private final static String NUM_STATO = "Num stato: ";
    private final static String CELLE_ATTIVE = "Celle Attive: ";

    private JLabel cellAlive_lbl, nState_lbl;
    private JMenuBar mb_menu;
    private JMenu m_file, m_game;
    private JMenuItem mi_file_exit;
    private JMenuItem mi_game_autofill, mi_game_play, mi_game_stop, mi_game_reset;
    private GamePanel gamePanel;
    private Chrono chrono = new Chrono();
    private ActorSystem system;
    private ActorRef actRef;
    
	public ControlPanel(ActorSystem system, ActorRef actorRef) {
		this.system = system;
		this.actRef = actorRef;
		setMenuButton();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Game of Life - MultiThread");
        this.setSize(DEFAULT_WINDOW_SIZE);
        this.setMinimumSize(MINIMUM_WINDOW_SIZE);
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth())/2, 
                (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight())/2);
        this.setVisible(true);
        actRef.tell(new ControlPanelMsg(this),ActorRef.noSender());
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}
	
	private void setMenuButton(){
    	
    	JPanel labelPanel = new JPanel(new FlowLayout());
    	labelPanel.setPreferredSize(new Dimension(310, 40));
    	cellAlive_lbl= new JLabel(CELLE_ATTIVE);
    	cellAlive_lbl.setPreferredSize(new Dimension(150, 30));
    	Border border = BorderFactory.createLineBorder(Color.gray, 1);
    	cellAlive_lbl.setBorder(border);
    	labelPanel.add(cellAlive_lbl,BorderLayout.LINE_START);
    	nState_lbl =  new JLabel(NUM_STATO);
    	nState_lbl.setPreferredSize(new Dimension(150, 30));
    	nState_lbl.setBorder(border);
    	labelPanel.add(nState_lbl,BorderLayout.CENTER);

		mb_menu = new JMenuBar();
		setJMenuBar(mb_menu);
		// Aggiungo al menu File,Game, Help
		m_file = new JMenu("File");
		mb_menu.add(m_file);
		m_game = new JMenu("Game");
		mb_menu.add(m_game);

		// Voce del menu File->Exit
		mi_file_exit = new JMenuItem("Exit");
		mi_file_exit.setEnabled(false);
		mi_file_exit.addActionListener(this);
		// Aggiunge separatore tra Options e Exit
//		m_file.add(mi_file_options);
		m_file.add(new JSeparator());
		m_file.add(mi_file_exit);

		// Voce del menu Game->AutoFill
		mi_game_autofill = new JMenuItem("Autofill");
		mi_game_autofill.addActionListener(this);
		// Voce del menu Game->Play
		mi_game_play = new JMenuItem("Play");
		mi_game_play.addActionListener(this);
		// Voce del menu Game->Stop
		mi_game_stop = new JMenuItem("Stop");
		mi_game_stop.setEnabled(false);
		mi_game_stop.addActionListener(this);
		// Voce del menu Game->Reset
		mi_game_reset = new JMenuItem("Reset");
		mi_game_reset.addActionListener(this);
		m_game.add(mi_game_autofill);
		m_game.add(new JSeparator());
		m_game.add(mi_game_play);
		m_game.add(mi_game_stop);
		m_game.add(mi_game_reset);
		
		// Setup game panel
		//gamePanel = new GamePanel();
		gamePanel = new GamePanel(this);
		gamePanel.setPreferredSize(GamePanel.DEFAULT_SCROLL_PANEL_SIZE);
		JScrollPane scrlPanel  = new JScrollPane(gamePanel);
		setLayout(new BorderLayout());
		add(labelPanel,BorderLayout.NORTH);
        add(scrlPanel, BorderLayout.CENTER);
	}
    
	//gestione pulsanti start e stop
    public void setGameBeingPlayed(boolean isBeingPlayed) {
        if (isBeingPlayed) {
            mi_game_play.setEnabled(false);
            mi_game_stop.setEnabled(true);
            mi_file_exit.setEnabled(true);
            actRef.tell("Start",ActorRef.noSender());
            chrono.start();
        } else {
            mi_game_play.setEnabled(true);
            mi_game_stop.setEnabled(false);
            actRef.tell("Stop",ActorRef.noSender());
        }
    }
    
    public void setPlayEnabled(){
    	mi_game_play.setEnabled(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
    	
    	Object[] percentageOptions = {"Select",5,10,15,20,25,30,40,50,60,70};
    	
    	// Pressione File->Exit
        if (ae.getSource().equals(mi_file_exit)) {
            actRef.tell("Exit",ActorRef.noSender());
            // Pressione Game->Autofill
        } else if (ae.getSource().equals(mi_game_autofill)) {
            final JFrame f_autoFill = new JFrame();
            f_autoFill.setTitle("Autofill");
            f_autoFill.setSize(OPTION_WINDOW_WIDHT, OPTION_WINDOW_HEIGHT);
            f_autoFill.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - f_autoFill.getWidth())/2, 
                (Toolkit.getDefaultToolkit().getScreenSize().height - f_autoFill.getHeight())/2);
            f_autoFill.setResizable(false);
            JPanel p_autoFill = new JPanel();
            p_autoFill.setOpaque(false);
            f_autoFill.add(p_autoFill);
            p_autoFill.add(new JLabel("Which percentage should be filled? "));
            final JComboBox<Object> cb_percent = new JComboBox<Object>(percentageOptions);
            p_autoFill.add(cb_percent);
            cb_percent.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (cb_percent.getSelectedIndex() > 0) {
                        actRef.tell(new FillPercentMsg((Integer)cb_percent.getSelectedItem()),
                        		ActorRef.noSender());
                        f_autoFill.dispose();
                    }
                }
            });
            f_autoFill.setVisible(true);
         // Pressione Game->Reset
        } else if (ae.getSource().equals(mi_game_reset)) {
            actRef.tell("Reset",ActorRef.noSender());
            
         // Pressione Game->Play
        } else if (ae.getSource().equals(mi_game_play)) {
            setGameBeingPlayed(true);
         // Pressione Game->Stop
        } else if (ae.getSource().equals(mi_game_stop)) {
            setGameBeingPlayed(false);
        }
    }
    
	public void setLabel(int newState, int size) {
		nState_lbl.setText(NUM_STATO+newState);
		cellAlive_lbl.setText(CELLE_ATTIVE+size);
	}

}
