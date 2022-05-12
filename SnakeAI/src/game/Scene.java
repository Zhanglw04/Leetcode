package game;

import snake.AISnake;
import snake.PlayerSnake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scene extends JFrame {
    // GUI
    private final Font f = new Font("Times New Roman", Font.PLAIN, 15);
    private final Font f2 = new Font("Times New Roman", Font.PLAIN, 12);
    private JRadioButtonMenuItem[] modeItems;
    private JRadioButtonMenuItem[] speedItems;
    private JPanel paintPanel;
    private final JLabel label1 = new JLabel("Length: ");
    private final JLabel label2 = new JLabel("Time: ");
    private final JLabel label3 = new JLabel("Score: ");
    private final JLabel label4 = new JLabel("AI Length: ");
    private final JLabel label5 = new JLabel("Food：");
    private final JLabel length = new JLabel("1");
    private final JLabel time = new JLabel("");
    private final JLabel score = new JLabel("0");
    private final JLabel AILength = new JLabel("1");
    private final JLabel Amount = new JLabel("0");

    private final HashMap<String, JLabel> infos = new HashMap<>();
    private final HashMap<Integer, JLabel> walls = new HashMap<>();
    private final JPanel p = new JPanel();
    private Timer timer;

    public boolean pause = true;
    public boolean quit = false;
    public boolean die = false;
    public boolean repeat = false;
    public boolean ai_die = false;
    private boolean showGrid = true;
    private boolean showPadding = true;


    private PlayerSnake snake;
    private AISnake ai;
    private FoodSet food;
    public final int pixelPerUnit = 22;
    public final int pixelRightBar = 110;
    public final int padding = 5;
    private String mapFile; // The file path of game map
    private int[][] gameMap;
    /*
       0: path
       1: player snake
       2: food
       3: wall
       4: AI snake
     */
    public int gameMode = 2;    // Game mode
    /*
        0: only player snake
        1: only AI snake (for testing)
        2: player snake and AI snake
     */
    public boolean debug = false;
    public Vector<Position> food_history = new Vector<>();
    public Data newDatabase = new Data();

    public synchronized int[][] getMap() {
        return gameMap;
    }

    public synchronized void setMap(int i, int j, int e) {
        gameMap[i][j] = e;
    }

    public void initMenuBar() {
        // Menu
        JMenuBar bar = new JMenuBar();
        bar.setBackground(Color.white);
        setJMenuBar(bar);
        JMenu Settings = new JMenu("Settings");
        Settings.setFont(f);
        JMenu Help = new JMenu("Help");
        Help.setFont(f);
        JMenu Record = new JMenu("Record");
        Record.setFont(f);
        bar.add(Settings);
        bar.add(Help);
        bar.add(Record);

        JMenu changeMode = new JMenu("Game Mode");
        changeMode.setFont(f2);
        JMenu setSpeed= new JMenu("Speed Settings");
        setSpeed.setFont(f2);
        JMenuItem removeGrid= new JMenuItem("Remove Grid");
        removeGrid.setFont(f2);
        JMenuItem removePadding= new JMenuItem("Remove Padding");
        removePadding.setFont(f2);
        JMenuItem exit = new JMenuItem("Exit");
        exit.setFont(f2);

        Settings.add(changeMode);
        Settings.add(setSpeed);
        Settings.add(removeGrid);
        Settings.add(removePadding);
        Settings.add(exit);

        JMenuItem help = new JMenuItem("Guide...");
        help.setFont(f2);
        Help.add(help);

        JMenuItem bestRecord = new JMenuItem("Best Record...");
        bestRecord.setFont(f2);
        JMenuItem recentRecord = new JMenuItem("Recent Record");
        recentRecord.setFont(f2);
        Record.add(bestRecord);
        Record.add(recentRecord);

        initWalls();

        this.addKeyListener(new MyKeyListener());
        removeGrid.addActionListener(e -> {
            if(!showGrid) {
                showGrid = true;
                removeGrid.setText("Remove Grid");
            } else {
                showGrid = false;
                removeGrid.setText("Show Grid");
            }
            paintPanel.repaint();
        });
        removePadding.addActionListener(e -> {
            if(!showPadding) {
                showPadding = true;
                removePadding.setText("Remove Padding");
            } else {
                showPadding = false;
                removePadding.setText("Show Padding");
            }
            paintPanel.repaint();
        });
        exit.addActionListener(e -> {
            System.exit(0);
        });

        // Set mode
        String[] modes = {"Only Player Snake", "Only AI Snake", "Player Snake and AI Snake"};
        modeItems = new JRadioButtonMenuItem[modes.length];
        ButtonGroup modeGroup = new ButtonGroup();
        for(int i=0; i<modes.length; i++){
            modeItems[i] = new JRadioButtonMenuItem(modes[i]);
            modeItems[i].setFont(f2);
            changeMode.add(modeItems[i]);
            modeGroup.add(modeItems[i]);
            modeItems[i].addActionListener(e -> {
                for(int j = 0; j < modeItems.length; j++){
                    if(modeItems[j].isSelected()){
                        if(j == gameMode){
                            return;
                        }else{
                            changeGameMode(gameMode, j);
                        }
                    }
                }
            });
        }
        modeItems[gameMode].setSelected(true);

        // Set speed
        String[] speed = {"Low", "Medium", "High", "Very High"};
        speedItems = new JRadioButtonMenuItem[speed.length];
        ButtonGroup speedGroup = new ButtonGroup();
        for(int i=0; i<speed.length; i++) {
            speedItems[i] = new JRadioButtonMenuItem(speed[i]);
            speedItems[i].setFont(f2);
            setSpeed.add(speedItems[i]);
            speedGroup.add(speedItems[i]);
            speedItems[i].addActionListener(e -> {
                for(int i1=0; i1<speedItems.length; i1++) {
                    if(speedItems[i1].isSelected()) {
                        if(i1 == 0) {
                            snake.setDefaultSpeed(600);
                            snake.resetSpeed();
                        } else if(i1 == 1) {
                            snake.setDefaultSpeed(500);
                            snake.resetSpeed();
                        } else if(i1 == 2) {
                            snake.setDefaultSpeed(300);
                            snake.resetSpeed();
                        } else if(i1 == 3) {
                            snake.setDefaultSpeed(100);
                            snake.resetSpeed();
                        }
                    }
                }
            });
        }
        speedItems[2].setSelected(true);

        help.addActionListener(e -> new Help());

        Record r1 = new Record();
        bestRecord.addActionListener(e -> r1.best());
        Record r2 = new Record();
        recentRecord.addActionListener(e -> r2.recent());

    }

    public void initRightBar(){
        remove(label1);remove(label2);remove(label3);remove(label4);remove(label5);
        remove(AILength);remove(length);remove(score);remove(time);remove(Amount);remove(p);

        int info_x = padding*3 + gameMap[0].length*pixelPerUnit;
        if(gameMode == 0){  //player snake
            add(label1);label1.setBounds(info_x, 10, 80, 20);label1.setFont(f);
            add(length);length.setBounds(info_x, 35, 80, 20);length.setFont(f);
            add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);
            add(time);time.setBounds(info_x, 95, 80, 20);time.setFont(f);
            add(label3);label3.setBounds(info_x, 130, 80, 20);label3.setFont(f);
            add(score);score.setBounds(info_x, 155, 80, 20);score.setFont(f);
            add(label5);label5.setBounds(info_x, 190, 80, 20);label5.setFont(f);
            add(Amount);Amount.setBounds(info_x, 215, 80, 20);Amount.setFont(f);
        }else if(gameMode == 1){    //ai snake
            add(label4);label4.setBounds(info_x, 10, 80, 20);label4.setFont(f);
            add(AILength);AILength.setBounds(info_x, 35, 80, 20);AILength.setFont(f);
            add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);
            add(time);time.setBounds(info_x, 95, 80, 20);time.setFont(f);
            add(label5);label5.setBounds(info_x, 130, 80, 20);label5.setFont(f);
            add(Amount);Amount.setBounds(info_x, 155, 80, 20);Amount.setFont(f);
        }else if(gameMode == 2){
            add(label1);label1.setBounds(info_x, 10, 80, 20);label1.setFont(f);
            add(length);length.setBounds(info_x, 35, 80, 20);length.setFont(f);
            add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);
            add(time);time.setBounds(info_x, 95, 80, 20);time.setFont(f);
            add(label3);label3.setBounds(info_x, 130, 80, 20);label3.setFont(f);
            add(score);score.setBounds(info_x, 155, 80, 20);score.setFont(f);
            add(label5);label5.setBounds(info_x, 190, 80, 20);label5.setFont(f);
            add(Amount);Amount.setBounds(info_x, 215, 80, 20);Amount.setFont(f);
            add(p);p.setBounds(info_x, 240, 70, 1);p.setBorder(BorderFactory.createLineBorder(Color.white));
            add(label4);label4.setBounds(info_x, 255, 80, 20);label4.setFont(f);
            add(AILength);AILength.setBounds(info_x, 280, 80, 20);AILength.setFont(f);
        }

        infos.put("AILength", AILength);
        infos.put("Length", length);
        infos.put("Score", score);
        infos.put("Time", time);
        infos.put("Amount", Amount);

        label1.setForeground(Color.black);
        label2.setForeground(Color.black);
        label3.setForeground(Color.black);
        label4.setForeground(Color.black);
        label5.setForeground(Color.black);
        AILength.setForeground(Color.red);
        length.setForeground(Color.red);
        score.setForeground(Color.red);
        time.setForeground(Color.red);
        Amount.setForeground(Color.red);
    }

    public void initUI(){
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

        Image img = Toolkit.getDefaultToolkit().getImage("image//title.png");
        setIconImage(img);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int rows = gameMap.length, cols = gameMap[0].length;
        setSize(cols*pixelPerUnit+pixelRightBar, rows * pixelPerUnit + 75);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JPanel imagePanel = (JPanel) this.getContentPane();
        imagePanel.setOpaque(false);
        paintPanel = new JPanel(){
            public void paint(Graphics g1){
                super.paint(g1);
                Graphics2D g = (Graphics2D) g1;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);

                if(showPadding){
                    g.setPaint(new GradientPaint(115,135,Color.CYAN,230,135,Color.MAGENTA,true));
                    g.setStroke( new BasicStroke(4,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
                    g.drawRect(2, 2, padding*2-4+cols*pixelPerUnit, rows*pixelPerUnit+6);
                }

                if(showGrid) {
                    for(int i = 0; i <= cols; i++) {
                        g.setStroke( new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 3.5f, new float[] { 15, 10, }, 0f));
                        g.setColor(Color.black);
                        g.drawLine(padding+i*pixelPerUnit,padding,padding+i*pixelPerUnit,padding+rows*pixelPerUnit);
                    }

                    for(int i = 0;i <= rows; i++){
                        g.drawLine(padding,padding+i*pixelPerUnit,padding+cols*pixelPerUnit,padding+i*22);
                    }
                }

                if(die || ai_die) {
                    g.setFont(new Font("Times New Roman",Font.BOLD | Font.ITALIC,30));
                    g.setColor(Color.red);
                    g.setStroke( new BasicStroke(10,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

                    int x = this.getWidth()/2, y = this.getHeight()/2;
                    recordDB();
                    if(die){
                        g.drawString("Game over", x-130, y-30);
                        g.drawString("Press Esc to restart", x-180, y+30);
                    }
                    if(ai_die){
                        g.drawString("The stupid AI can not find a way out", x-250, y-30);
                        g.drawString("Press Esc to restart", x-180, y+30);
                    }
                }
            }
        };
        paintPanel.setOpaque(false);
        paintPanel.setBounds(0, 0, 900, 480);
        add(paintPanel);

        initRightBar();
        initMenuBar();

    }

    public void resetLabel(){
        AILength.setText("1");
        length.setText("1");
        score.setText("0");
        time.setText("");
        Amount.setText("0");
    }

    public void restart(){
        quit = true;
        resetLabel();
        speedItems[2].setSelected(true);

        food.removeAll();
        food = null;
        food = new FoodSet(this);
        removeAllBrick();

        loadGameMap(mapFile);
        Util.printMap(gameMap,"debug//map.txt");
        initWalls();

        if(gameMode == 0){
            snake.restart();
            snake = null;
            snake = new PlayerSnake(this);
        }else if(gameMode == 1){
            ai.restart();
            ai = null;
            ai = new AISnake(this);
        }else if(gameMode == 2){
            snake.restart();
            snake = null;
            snake = new PlayerSnake(this);
            ai.restart();
            ai = null;
            ai = new AISnake(this);
        }

        timer.reset();
        die = false;
        repeat = false;
        ai_die = false;
        quit = false;
        pause = true;
        System.out.println("\nGame restart...\t" + Util.getSystemTime());
    }

    public void changeGameMode(int currentMode, int newMode){
        quit = true;
        resetLabel();
        speedItems[2].setSelected(true);
        food.removeAll();
        food = null;
        food = new FoodSet(this);

        removeAllBrick();

        loadGameMap(mapFile);
        Util.printMap(gameMap,"debug//map.txt");
        initWalls();

        if(currentMode == 0){
            snake.restart();
            snake = null;
        }else if(currentMode == 1){
            ai.restart();
            ai = null;
        }else if(currentMode == 2){
            snake.restart();
            snake = null;
            ai.restart();
            ai = null;
        }

        if(newMode == 0){
            snake = new PlayerSnake(this);
        }else if(newMode == 1){
            ai = new AISnake(this);
        }else if(newMode == 2){
            snake = new PlayerSnake(this);
            ai = new AISnake(this);
        }

        gameMode = newMode;
        initRightBar();

        timer.reset();

        die = false;
        repeat = false;
        ai_die = false;
        quit = false;
        pause = true;

        System.out.println("\nGame restart...\t" + Util.getSystemTime());
    }

    public Position randomCoordinate(){
        int rows = gameMap.length;
        int cols = gameMap[0].length;
        Random rand = new Random();
        int x = rand.nextInt(rows-1);
        int y = rand.nextInt(cols-1);

        while(true) {
            if(gameMap[x][y] != 0 || y == cols-1) {
                x = rand.nextInt(rows-1);
                y = rand.nextInt(cols-1);
            } else {
                break;
            }
        }

        return new Position(x,y);
    }

    public void initWalls() {
        int rows = gameMap.length;
        int cols = gameMap[0].length;
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if(gameMap[i][j] == 3) {
                    // Load the wall picture
                    ImageIcon brickIcon = new ImageIcon("image//brick.png");
                    brickIcon.setImage(brickIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(brickIcon);
                    this.add(label);

                    Position coordinate = new Position(i, j);
                    label.setBounds(Util.getPixel(j, padding, pixelPerUnit), Util.getPixel(i, padding,pixelPerUnit), 20, 20);
                    walls.put(Util.dim2ToDim1(new Position(i, j), gameMap[0].length), label);
                }
            }
        }
    }

    public void removeAllBrick() {
        for(int key:walls.keySet()){
            JLabel label = walls.get(key);
            label.setVisible(false);
            this.remove(label);
        }
        walls.clear();
    }

    public void loadGameMap(String filePath) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffReader = new BufferedReader(reader);

        Vector<String> lines = new Vector<>();
        String line = "";
        while(true){
            try {
                if ((line = buffReader.readLine()) == null)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            lines.add(line);
        }
        try {
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rows = lines.size();
        int cols = lines.get(0).length()/2;

        gameMap = new int[rows][cols];
        for(int i=0; i<rows; i++) {
            line = lines.get(i);
            for (int j=0; j<cols; j++) {
                gameMap[i][j] = line.charAt(2*j)-'0';
            }
        }
    }

    public void updateInfos(String key, String value){
        infos.get(key).setText(value);
    }

    public String getInfos (String key) {
        return infos.get(key).getText();
    }

    public Vector<Position> getAllFoodCoor(){
        return food.getAllFoodCoors();
    }

    public void removeFood(Position coordinate){
        food.removeFoodCoordinate(coordinate);
    }

    public int getFoodPoint(Position coordinate){
        return food.getFoodPoint(coordinate);
    }

    public synchronized void recordDB() {
        if (!repeat) {
            List<String> list = new ArrayList<>();
            list.add(getInfos("Score"));
            list.add(getInfos("Time"));
            list.add(getInfos("Length"));
            list.add(getInfos("AILength"));
            list.add(getInfos("Amount"));
            if (newDatabase.isResultDB() && newDatabase.isResultTable()) {
                newDatabase.recording(list);
            }
            else if (!newDatabase.isResultDB()) {
                newDatabase.creatDatabase();
                newDatabase.createTable();
                newDatabase.recording(list);
            }
            else {
                newDatabase.createTable();
                newDatabase.recording(list);
            }
            repeat = true;
        }
    }

    public void run(){
        food = new FoodSet(this);
        if(gameMode == 0) snake = new PlayerSnake(this);
        else if(gameMode == 1) ai = new AISnake(this);
        else if(gameMode == 2) {
            snake = new PlayerSnake(this);
            ai = new AISnake(this);
        }
        setFocusable(true);
        setVisible(true);
        timer = new Timer();
        pause = true;
    }

    // Main
    public static void main(String[] args) {
        System.out.println("starting...\t" + Util.getSystemTime());

        Scene game = new Scene();
        game.mapFile = "map//map.txt";  // modify
        game.gameMode = 2;
        game.loadGameMap(game.mapFile);

        game.newDatabase.creatDatabase();
        game.newDatabase.createTable();

        Util.printMap(game.getMap(),"debug//map.txt");

        game.initUI();
        game.run();
        System.out.println("\nGame start...\t" + Util.getSystemTime());
    }

    // Timer class
    private class Timer {
        private int hour = 0;
        private int min = 0;
        private int sec = 0;

        public Timer(){
            this.run();
        }

        public void run() {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                if (!quit && !pause) {
                    sec +=1 ;
                    if(sec >= 60){
                        sec = 0;
                        min +=1 ;
                    }
                    if(min>=60){
                        min=0;
                        hour+=1;
                    }
                    showTime();
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }

        public void reset() {
            hour = 0;
            min = 0;
            sec = 0;
        }

        private void showTime() {
            String strTime;
            if(hour < 10)
                strTime = "0"+hour+":";
            else
                strTime = ""+hour+":";

            if(min < 10)
                strTime = strTime+"0"+min+":";
            else
                strTime =strTime+ ""+min+":";

            if(sec < 10)
                strTime = strTime+"0"+sec;
            else
                strTime = strTime+""+sec;

            time.setText(strTime);
        }
    }

    private class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            Direction direction = null;
            if(gameMode != 1)
                direction = snake.getDirection();

            if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {  // Turn right
                if(!quit && direction != Direction.LEFT && gameMode != 1) {
                    snake.setDirection(Direction.RIGHT);
                }
            } else if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {    //Turn left
                if(!quit && direction != Direction.RIGHT && gameMode != 1) {
                    snake.setDirection(Direction.LEFT);
                }
            } else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {  // Up
                if(!quit && direction != Direction.DOWN && gameMode != 1) {
                    snake.setDirection(Direction.UP);
                }
            } else if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {    // Down
                if(!quit && direction != Direction.UP && gameMode != 1) {
                    snake.setDirection(Direction.DOWN);
                }
            } else if(key == KeyEvent.VK_ESCAPE) {  // Restart
                restart();
            } else if(key == KeyEvent.VK_SPACE) {
                if(!pause) {
                    pause = true;
                    System.out.println("Pause...");
                } else {
                    pause = false;
                    System.out.println("Start...");
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
