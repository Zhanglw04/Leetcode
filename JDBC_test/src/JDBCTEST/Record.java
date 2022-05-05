package JDBCTEST;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

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

public class Record extends JDialog {
    private JPanel contentPane;
    private Font f = new Font("Times New Roman",Font.PLAIN,15);
    private Vector rowData, columnName;
    private JTable jt;
    private JScrollPane scroll;


    public Record(Vector<Vector<String>> recordings) {
        setTitle("Game Instructions");
        Image img=Toolkit.getDefaultToolkit().getImage("title.png");
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(1150,380);
        setResizable(false);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();
        contentPane.add(shadePanel, BorderLayout.CENTER);
        shadePanel.setLayout(null);

        columnName = new Vector();
        columnName.add("ID");
        columnName.add("Record Time");
        columnName.add("Score");
        columnName.add("Game Time");
        columnName.add("Snake Length");
        columnName.add("AI Length");
        columnName.add("Food Amount");
        rowData = new Vector();
        for (int i=0; i<recordings.size(); i++) {
            Vector line = new Vector();
            line.add(i+1);
            for (int j=0; j<recordings.get(i).size(); j++) {
                line.add(recordings.get(i).get(j));
            }
            rowData.add(line);
        }
        jt = new JTable(rowData, columnName);
        jt.setFocusable(false);
        jt.setFont(f);
        jt.setEnabled(false);
        jt.setOpaque(false);
//        jt.setLineWrap(true);
//        JTextArea J1 = new JTextArea("The game instructions are: \n\n" +
//                "Use the arrow keys or WASD keys on the keyboard to control the snake.\n\n" +
//                "Press ESC to restart the game, and press the space key to pause and start the game.\n\n" +
//                "Setting menu bar can set snake speed, " +
//                "set whether the grid and padding are visible or not, and change the game mode\n\n" +
//                "The right side of the game screen displays your current length, the time and the score.\n\n" +
//                "There are a variety of foods, which correspond to different points and different probabilities of occurrence.\n\n" +
//                "The AI snake uses algorithms to find its way automatically.\n\n");
//        J1.setFocusable(false);
//        J1.setFont(f);
//        J1.setEditable(false);
//        J1.setOpaque(false);
//        J1.setLineWrap(true);

        scroll = new JScrollPane(jt,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createTitledBorder("The 10 Best Scores"));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        shadePanel.add(scroll);
        scroll.setBounds(10, 10, 1100, 330);
//        setEnabled(false);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        SnakeTest st = new SnakeTest();
        Vector<Vector<String>> v = st.getNewRecord(10);
        Record r = new Record(v);
    }
}
