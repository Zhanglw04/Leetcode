package snake;

import game.*;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerSnake {
    private Scene gameUI;
    private Direction direction = Direction.RIGHT;
    private int speed = 300;
    private int defaultSpeed = 300;
    private Deque<Body> body = new LinkedList<>();
    private int point = 0;
//    private int bulletNumber = 5;

//    private ImageIcon[] headIcon = new ImageIcon[4];
//    private int headIconTag = 0;
//    private ImageIcon[] bodyIcon = new ImageIcon[4];
//    private int bodyIconTag = 0;
    private final ImageIcon headIcon;
    private final ImageIcon bodyIcon;
    private ScheduledExecutorService executor;

    public PlayerSnake(Scene gameUI) {
        this.gameUI = gameUI;
        headIcon = new ImageIcon("head//head0.png");
        headIcon.setImage(headIcon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
        bodyIcon = new ImageIcon("body//body0.png");
        bodyIcon.setImage(bodyIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
        Body head = new Body(0,0,headIcon);
        body.addFirst(head);
        gameUI.add(head.getLabel());
        head.getLabel().setBounds(Util.getPixel(head.getCoordinate().getY(), gameUI.padding, gameUI.pixelPerUnit),
                Util.getPixel(head.getCoordinate().getX(), gameUI.padding, gameUI.pixelPerUnit), 20, 20);
        gameUI.setMap(0, 0, 1);
        run();
    }

    public boolean checkDeath(Position coordinate) {
        int[][] map = gameUI.getMap();
        int rows = map.length;
        int cols = map[0].length;
        int x = coordinate.getX();
        int y = coordinate.getY();
        /*
         Conditions of death:
            1. Hit walls
            2. Hit itself
            3. Hit AI snake
         */
        return x < 0 || x >= rows || y < 0 || y >= cols || map[x][y] == 1 || map[x][y] == 3 || map[x][y] == 4;
    }

    public boolean checkEat(Position coordinate) {
        int point1 = gameUI.getFoodPoint(coordinate);
        if(point1 == -1) {
            return false;
        } else {
//            new Music("music//eat.wav").start();
            point += point1;

            gameUI.removeFood(new Position(coordinate.getX(), coordinate.getY()));
            gameUI.updateInfos("Score", "" + point);
            gameUI.updateInfos("Length", "" + body.size());
//            gameUI.updateInfos("Amount", "" + gameUI.getAllFoodCoor().size());
            gameUI.setMap(coordinate.getX(), coordinate.getY(), 1);
            return true;
        }
    }

    public void move() {
        Position head;
        Position nextCoordinate = new Position(0,0);
        if(direction == Direction.UP) {
            head = body.getFirst().getCoordinate();
            nextCoordinate = new Position(head.getX()-1, head.getY());
        } else if(direction == Direction.DOWN) {
            head = body.getFirst().getCoordinate();
            nextCoordinate = new Position(head.getX()+1, head.getY());
        } else if(direction == Direction.LEFT) {
            head = body.getFirst().getCoordinate();
            nextCoordinate = new Position(head.getX(), head.getY()-1);
        } else if(direction == Direction.RIGHT){
            head = body.getFirst().getCoordinate();
            nextCoordinate = new Position(head.getX(), head.getY()+1);
        }

        if(checkDeath(nextCoordinate)) {
//            new Music("music//over.wav").start();
            gameUI.quit = true;
            gameUI.pause = true;
            gameUI.die = true;
            gameUI.repaint();
        }
        else {
            Body nextNode = new Body(nextCoordinate, headIcon);
            body.addFirst(nextNode);
            gameUI.setMap(nextCoordinate.getX(), nextCoordinate.getY(), 1);
            nextNode.getLabel().setVisible(true);
            gameUI.add(nextNode.getLabel());

            if(!checkEat(nextCoordinate)) {
                Body tail = body.pollLast();
                if (tail != null) {
                    gameUI.setMap(tail.getCoordinate().getX(), tail.getCoordinate().getY(), 0);
                    tail.getLabel().setVisible(false);
                    gameUI.remove(tail.getLabel());
                }
            }
            else {
                gameUI.removeFood(new Position(nextCoordinate.getX(), nextCoordinate.getY()));
            }
        }

    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDefaultSpeed(int speed) {
        this.defaultSpeed = speed;
    }

    public void resetSpeed() {
        this.speed = defaultSpeed;
        executor.shutdownNow();
        run();
    }

    public void removeAll(){
        for (Body node : body) {
            node.getLabel().setVisible(false);
            gameUI.remove(node.getLabel());
        }
    }

    public void restart() {
        removeAll();
        quit();
    }

    public void quit(){
        executor.shutdownNow();
    }

    public void show() {
        for (Body node : body) {
            node.getLabel().setBounds(Util.getPixel(node.getCoordinate().getY(), gameUI.padding, gameUI.pixelPerUnit),
                    Util.getPixel(node.getCoordinate().getX(), gameUI.padding, gameUI.pixelPerUnit), 20, 20);
            node.getLabel().setIcon(bodyIcon);
        }
        Body node = body.getFirst();
        node.getLabel().setIcon(headIcon);
    }

    public void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!gameUI.pause && !gameUI.quit) {
                move();
                show();
                Util.printMap(gameUI.getMap(), "debug//map.txt");
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }
}
