package game;

import javax.swing.*;
import java.awt.*;

class ShadePanel extends JPanel {
    public ShadePanel() {
        super();
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        GradientPaint paint = new GradientPaint(0, 0, Color.CYAN, 0, height,Color.MAGENTA);
        g.setPaint(paint);
        g.fillRect(0, 0, width, height);
    }
}

public class Help extends JDialog {
    private JPanel contentPane;
    private Font f = new Font("Times New Roman",Font.PLAIN,15);
    private JScrollPane scroll;

    public Help() {
        setTitle("Game Instructions");
        Image img=Toolkit.getDefaultToolkit().getImage("title.png");
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(410,380);
        setResizable(false);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();
        contentPane.add(shadePanel, BorderLayout.CENTER);
        shadePanel.setLayout(null);
        JTextArea J1 = new JTextArea("The game instructions are: \n\n" +
                "The player snake appears in the upper left corner of the map, the AI snake appears in the upper right corner of the map.\n\n" +
                "Use the arrow keys or WASD keys on the keyboard to control the snake.\n\n" +
                "Press ESC to restart the game, and press the space key to pause and start the game.\n\n" +
                "Setting menu bar can set snake speed, " +
                "set whether the grid and padding are visible or not, and change the game mode\n\n" +
                "The right side of the game screen displays your current length, the time and the score.\n\n" +
                "There are a variety of foods, which correspond to different points and different probabilities of occurrence.\n\n" +
                "The AI snake uses algorithms to find its way automatically.\n\n");
        J1.setFocusable(false);
        J1.setFont(f);
        J1.setEditable(false);
        J1.setOpaque(false);
        J1.setLineWrap(true);
        scroll = new JScrollPane(J1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createTitledBorder("How to play"));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        shadePanel.add(scroll);
        scroll.setBounds(10, 10, 385, 330);
        setVisible(true);
    }
}
