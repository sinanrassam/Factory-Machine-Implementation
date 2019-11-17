/*
 * A class which shows a graph of a running collection of Machine objects along
 * the x-axis and temperature along the y-axis. It shows guidelines showing safe
 * operating temperatures between 50 - 200 degrees, overheated lines at 250
 * degrees and overcooled at 0 degrees Celsius.
*/
package FactoryControl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

/**
 *
 * @author sinan
 */
public class FactoryControlGUI extends JPanel implements ActionListener {
    private JRadioButton startRadio, stopRadio;
    private GraphPanel graphPanel;
    
    private String graphTitle;
    int verticalSpacing, height;
    
    Timer timer;
    int numMachines;
    int numCoolers;
    
    private Machine[] machines;
    private MonitoringCooler[] coolers;
    private boolean AlreadyStarted;
    
    public FactoryControlGUI() {
        super(new BorderLayout());
        
        AlreadyStarted = false;
        graphTitle = "GRAPH OF MACHINES TEMPERATURES IN C";
        numMachines = 50;
        numCoolers = 15; // Living on the edge!!
        
        machines = new Machine[numMachines];
        coolers = new MonitoringCooler[numCoolers];
        
        // Setup radio buttons for starting and stoping machines
        startRadio = new JRadioButton("Start", false);
        stopRadio = new JRadioButton("Stop", true); // set stop to be default selected
        
        // Setup a Button Group to make sure buttons can not be pressed randomly
        ButtonGroup radioButtons = new ButtonGroup();
        // Add both buttons to the grid
        radioButtons.add(startRadio);
        radioButtons.add(stopRadio);
        
        // Setup a panel for the radios to appear in
        JPanel radioPanel = new JPanel();
        // Add the buttons to the Panel
        radioPanel.add(startRadio);
        radioPanel.add(stopRadio);
        
        // setup action listeners to both radios
        startRadio.addActionListener(this);
        stopRadio.addActionListener(this);
        
        add(radioPanel, BorderLayout.NORTH);
        
        // Setup the graph panel
        graphPanel = new GraphPanel();
        // Add graph panel to the GUI
        add(graphPanel, BorderLayout.CENTER);
        
        // Timer used to update the GUI every 200ms
        timer = new Timer(200,this);
        
        for (int i = 0; i < machines.length; i++) {
            machines[i] = new Machine(0, 250);
        }
        for (int i = 0; i < coolers.length; i++) {
            coolers[i] = new MonitoringCooler(machines, 25);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if ((source == startRadio) && !AlreadyStarted) {
            for (Machine m : machines) {
                new Thread(m).start();
            }
            for (MonitoringCooler c : coolers) {
                new Thread(c).start();
            }
            timer.start();
            AlreadyStarted = true;
        }
        if ((source == stopRadio) && AlreadyStarted) {
            for (Machine m : machines) {
                m.stopMachine();
            }
            for (MonitoringCooler c : coolers) {
                c.stopCooler();
            }
            timer.stop();
            AlreadyStarted = false;
        }
        graphPanel.repaint();
    }
    
    private class GraphPanel extends JPanel {
        
        public GraphPanel() {
            super();
            setPreferredSize(new Dimension(850, 600));
            setBackground(Color.WHITE);
        }
        
        public void drawGraph(Graphics g) {
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            int panelWidth = graphPanel.getWidth();
            int labelLength = 25;
            g.drawString(graphTitle, panelWidth/2 - g.getFontMetrics().stringWidth(graphTitle)/2, 30);
            int lineWidth = panelWidth - labelLength;
            
            // Draw Graph Levels
            g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            verticalSpacing = (graphPanel.getHeight() - g.getFontMetrics().getHeight())/6;
            height = verticalSpacing;
            // 250 Level
            g.drawLine(0, height, lineWidth, height);
            g.drawString("250", panelWidth - 25, height + 5);
            // 200
            height += verticalSpacing;
            g.setColor(Color.red);
            g.drawLine(0, height-10, lineWidth, height-10);
            g.drawString("200", panelWidth - 25, height + 5);
            g.setColor(Color.black);
            // 125
            height += verticalSpacing;
            int tempHeight = height + (verticalSpacing/2);
            g.drawLine(0, tempHeight, lineWidth, tempHeight);
            g.drawString("125", panelWidth - 25, tempHeight + 5);
            height += verticalSpacing;
            // 50
            height += verticalSpacing;
            g.setColor(Color.blue);
            g.drawLine(0, height, lineWidth, height);
            g.drawString("50", panelWidth - 25, height + 5);
            g.setColor(Color.black);
            // 0
            height += verticalSpacing;
            g.drawLine(0, height, lineWidth, height);
            g.drawString("0", panelWidth - 25, height + 5);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGraph(g);
            
            int temp;
            float tempFactor;
            int pos = 0;
            int loc, t;
            int barWidth = 13;
            int horizantalSpacing = barWidth + 1;
            
            g.setFont(new Font("TimesRoman", Font.BOLD, 15));
            
            for(Machine m : machines) {
                temp = m.getCurrentTemp();
                tempFactor = ((float) temp/250);
                if (tempFactor <= 0.2)
                    g.setColor(Color.blue);
                else if (tempFactor >= 0.8)
                    g.setColor(Color.red);
                else
                    g.setColor(Color.yellow);
                
                float factor = (graphPanel.getHeight() / 240);
                
                t = (int) (temp * (factor));
                loc = graphPanel.getHeight() - (t+20);
                
                g.fillRect(pos, loc, barWidth, t);
                
                g.setColor(Color.black);
                g.drawString(m.isCoolerConnected()? "+" : "-", pos + (barWidth/4), graphPanel.getHeight() - 5);
                pos += horizantalSpacing;
            }
            
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("GRAPH OF MACHINES, BEING COOLED");
        // kill all threads when frame closes
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FactoryControlGUI());
        frame.pack();
        // position the frame in the middle of the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimension = tk.getScreenSize();
        Dimension frameDimension = frame.getSize();
        frame.setLocation((screenDimension.width - frameDimension.width) / 2,
                (screenDimension.height - frameDimension.height) / 2);
        frame.setVisible(true);
        // now display something while the main thread is still alive
    }
}
