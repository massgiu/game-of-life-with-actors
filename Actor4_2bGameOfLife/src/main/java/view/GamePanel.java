package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import actor.ActController;
import model.MyPoint;

public class GamePanel extends JPanel implements ComponentListener, 
							MouseListener{
	
	private static final long serialVersionUID = 1L;
	private static final int BLOCK_SIZE = 8;
	private static int num_of_row;
	private static int num_of_col;
	
    protected static Dimension DEFAULT_SCROLL_PANEL_SIZE;
    
	private Dimension gamePanelSize = null;
    private static List<MyPoint> cellList = new ArrayList<MyPoint>(0);
    private ControlPanel controlPanel;
    
    public GamePanel(ControlPanel controlPanel) {
    	num_of_col = ActController.getNumOfCol();
    	num_of_row = ActController.getNumOfRow();
    	DEFAULT_SCROLL_PANEL_SIZE = new Dimension(num_of_col*GamePanel.BLOCK_SIZE,num_of_row*GamePanel.BLOCK_SIZE);
        // Add resizing listener
        addComponentListener(this);
        addMouseListener(this);
        this.controlPanel=controlPanel;
    }
    
    public void updateView(List<MyPoint> newCellList, int newState) {
    	cellList = newCellList;
    	SwingUtilities.invokeLater(() -> {
			repaint();
		});
    	controlPanel.setLabel(newState,newCellList.size());
    }
    
    private synchronized void updateArraySize() {
        List<MyPoint> removeList = new ArrayList<MyPoint>(0);
        for (MyPoint current : cellList) {
            if ((current.getX() > gamePanelSize.width-1) || (current.getY() > gamePanelSize.height-1)) {
                removeList.add(current);
            }
        }
        cellList.removeAll(removeList);
        repaint();
    }

    public synchronized void addPoint(int x, int y) {
        if (!cellList.contains(new MyPoint(x,y))) {
            cellList.add(new MyPoint(x,y));
        }
        repaint();
    }

    public void addPoint(MouseEvent me) {
        int x = me.getPoint().x/BLOCK_SIZE-1;
        int y = me.getPoint().y/BLOCK_SIZE-1;
        if ((x >= 0) && (x < gamePanelSize.width) && (y >= 0) && (y < gamePanelSize.height)) {
            addPoint(x,y);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
        try {
            for (MyPoint newPoint : cellList) {
            // Draw new point
        		g.setColor(Color.BLUE);
        		g.fillRect(BLOCK_SIZE + (BLOCK_SIZE*newPoint.getX()), 
        				BLOCK_SIZE + (BLOCK_SIZE*newPoint.getY()), BLOCK_SIZE, BLOCK_SIZE);
            }
        } catch (ConcurrentModificationException cme) {}
        // Setup grid
        g.setColor(Color.BLACK);
        for (int i=0; i<=gamePanelSize.width; i++) {
            g.drawLine(((i*BLOCK_SIZE)+BLOCK_SIZE), BLOCK_SIZE, (i*BLOCK_SIZE)+BLOCK_SIZE, BLOCK_SIZE + (BLOCK_SIZE*gamePanelSize.height));
        }
        for (int i=0; i<=gamePanelSize.height; i++) {
            g.drawLine(BLOCK_SIZE, ((i*BLOCK_SIZE)+BLOCK_SIZE), BLOCK_SIZE*(gamePanelSize.width+1), ((i*BLOCK_SIZE)+BLOCK_SIZE));
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // Setup the game board size with proper boundries
        gamePanelSize = new Dimension(num_of_col, num_of_row);
        updateArraySize();
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {
    }
    
    @Override
    public void componentShown(ComponentEvent e) {
    }
    
    @Override
    public void componentHidden(ComponentEvent e) {
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        addPoint(e);
        System.out.println(e.getX()+" "+e.getY());
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
