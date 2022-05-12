package game;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

public class Record extends JDialog {
    private JPanel contentPane;
    private Font f = new Font("Times New Roman", Font.PLAIN, 20);
    private Vector rowData, columnName;
    private JTable jt;
    private JScrollPane scroll;
    private Data newDatabase;
    private final int num = 10;

    public void best() {
        setTitle("The Best Record");
        Image img = Toolkit.getDefaultToolkit().getImage("title.png");
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(850, 460);
        setResizable(false);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();
        contentPane.add(shadePanel, BorderLayout.CENTER);
        shadePanel.setLayout(null);

        newDatabase = new Data();
        newDatabase.creatDatabase();
        newDatabase.createTable();

        Vector<Vector<String>> recordings = newDatabase.getBestRecord(num);
        columnName = new Vector();
        columnName.add("ID");
        columnName.add("Record Time");
        columnName.add("Score");
        columnName.add("Game Time");
        columnName.add("Snake Length");
        columnName.add("AI Length");
        columnName.add("Food Amount");
        rowData = new Vector();
        for (int i = 0; i < recordings.size(); i++) {
            Vector line = new Vector();
            line.add(i + 1);
            for (int j = 0; j < recordings.get(i).size(); j++) {
                line.add(recordings.get(i).get(j));
            }
            rowData.add(line);
        }
        jt = new JTable(rowData, columnName);
        jt.setFocusable(false);
        jt.setFont(f);
        jt.setEnabled(false);
        jt.setOpaque(false);
        FitTableColumns(jt);
        makeFace(jt);
        jt.setRowHeight(35);

        scroll = new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createTitledBorder("10 Best scores"));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        shadePanel.add(scroll);
        scroll.setBounds(20, 10, 800, 410);
        setResizable(false);
        setVisible(true);
    }

    public void recent() {
        setTitle("The Recent Record");
        Image img = Toolkit.getDefaultToolkit().getImage("title.png");
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(850, 460);
        setResizable(false);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();
        contentPane.add(shadePanel, BorderLayout.CENTER);
        shadePanel.setLayout(null);

        newDatabase = new Data();
        newDatabase.creatDatabase();
        newDatabase.createTable();

        Vector<Vector<String>> recordings = newDatabase.getNewRecord(num);
        columnName = new Vector();
        columnName.add("ID");
        columnName.add("Record Time");
        columnName.add("Score");
        columnName.add("Game Time");
        columnName.add("Snake Length");
        columnName.add("AI Length");
        columnName.add("Food Amount");
        rowData = new Vector();
        for (int i = 0; i < recordings.size(); i++) {
            Vector line = new Vector();
            line.add(i + 1);
            for (int j = 0; j < recordings.get(i).size(); j++) {
                line.add(recordings.get(i).get(j));
            }
            rowData.add(line);
        }
        jt = new JTable(rowData, columnName);
        jt.setFocusable(false);
        jt.setFont(f);
        jt.setEnabled(false);
        jt.setOpaque(false);
        FitTableColumns(jt);
        makeFace(jt);
        jt.setRowHeight(35);

        scroll = new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createTitledBorder("10 Recent scores"));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        shadePanel.add(scroll);
        scroll.setBounds(20, 10, 800, 410);
        setResizable(false);
        setVisible(true);
    }

    public void FitTableColumns(JTable myTable)
    {
        JTableHeader header = myTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setFont(f);
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements())
        {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(
                    column.getIdentifier());
            int width = (int) myTable
                    .getTableHeader()
                    .getDefaultRenderer()
                    .getTableCellRendererComponent(myTable,
                            column.getIdentifier(), false, false, -1, col)
                    .getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++)
            {
                int preferedWidth = (int) myTable
                        .getCellRenderer(row, col)
                        .getTableCellRendererComponent(myTable,
                                myTable.getValueAt(row, col), false, false,
                                row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width + myTable.getIntercellSpacing().width + 10);
        }
    }

    public void makeFace(JTable table) {

        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    if (row % 2 == 0)
                        setBackground(Color.white);
                    else if (row % 2 == 1)
                        setBackground(Color.pink);
                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
