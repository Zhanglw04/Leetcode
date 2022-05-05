package snake;

import game.Pos;
import game.Scene;
import game.Util;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AISnake1 {
    private Scene gameUI;
    private Deque<Body> body = new LinkedList<>();
    private final ImageIcon headIcon;
    private final ImageIcon bodyIcon;
    private ScheduledExecutorService executor;
    static Comparator<pair> cmpMin = pair::compareMin;
    static Comparator<path> pathCmpMin = path::compare;
    private int mapCols;

    public AISnake1(Scene gameUI) {
        this.gameUI = gameUI;
        headIcon = new ImageIcon("head//AI_head.png");
        headIcon.setImage(headIcon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
        bodyIcon = new ImageIcon("body//AI_body.png");
        bodyIcon.setImage(bodyIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
        int cols = gameUI.getMap()[0].length;
        Body head = new Body(0,cols-1, headIcon);
        body.addFirst(head);
        gameUI.add(head.getLabel());
        head.getLabel().setBounds(Util.getPixel(head.getCoordinate().getY(), gameUI.padding, gameUI.pixelPerUnit),
                Util.getPixel(head.getCoordinate().getX(), gameUI.padding, gameUI.pixelPerUnit), 20, 20);
        gameUI.setMap(0, cols-1, 4);
        mapCols = gameUI.getMap()[0].length;
        run();
    }

    private void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        int speed = 300;    // modify
        executor.scheduleAtFixedRate(() -> {
            if (!gameUI.pause && !gameUI.quit) {
                move();
                Util.printMap(gameUI.getMap(), "debug//map.txt");
                show();
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }

    private void show(){
        for (Body node : body) {
            node.getLabel().setBounds(Util.getPixel(node.getCoordinate().getY(), gameUI.padding, gameUI.pixelPerUnit),
                    Util.getPixel(node.getCoordinate().getX(), gameUI.padding, gameUI.pixelPerUnit), 20, 20);
            node.getLabel().setIcon(bodyIcon);
        }
        Body node = body.getFirst();
        node.getLabel().setIcon(headIcon);
    }

    private void move(){
        Pos head = body.getFirst().getCoordinate();
        PriorityQueue<path> allPath = new PriorityQueue<>(pathCmpMin);
        Vector<Pos> foodCoors = gameUI.getAllFoodCoor();

        for (Pos food : foodCoors) {
            Pos goal = new Pos(food.getX(), food.getY());
            path pathToFood = findPath(gameUI.getMap(), head, goal);
            if(pathToFood.cost > 0) {
                allPath.add(pathToFood);
            }
        }

        if(allPath.isEmpty()) {
            if(!moveToTail()) {
                gameUI.updateInfos("Status", "die");
                System.out.println("unable to find path");
                show();
                gameUI.repaint();
                goDie();
            }
            else {
                gameUI.updateInfos("Status", "go");
            }
        }
        else {
            while (!allPath.isEmpty()) {
                path p = allPath.poll();
                Vector<Pos> pathToGoal = p.posSets;
                Pos goal = pathToGoal.lastElement();

                if (attemptMove(head, goal, pathToGoal)) {
                    Pos next = pathToGoal.get(1);
                    moveOneStep(next);
                    gameUI.updateInfos("Status", "to eat");
                    return;
                }
            }

            if(!moveToTail()) {
                System.out.println("unable to find path");
                show();
                gameUI.repaint();
                goDie();
            }else {
                gameUI.updateInfos("Status", "go");
            }
        }
    }

    private path findPath(int[][] map, Pos start, Pos goal){
        PriorityQueue<pair> frontier = new PriorityQueue<>(cmpMin);
        frontier.add(new pair(start, 0));
        HashMap<Integer, Pos> comeFrom = new HashMap<>();
        comeFrom.put(Util.dim2ToDim1(start, mapCols), new Pos(-1, -1));
        HashMap<Integer, Double> costNow = new HashMap<>();
        costNow.put(Util.dim2ToDim1(start, mapCols), 0.);

        while(!frontier.isEmpty()){
            pair current = frontier.poll();
            Pos currentPosition = current.position;
            if(currentPosition.equal(goal)) break;

            Vector<Pos> neighbors = getNeighbors(map, currentPosition);
            for (Pos next : neighbors) {
                double newCost = costNow.get(Util.dim2ToDim1(currentPosition, mapCols)) + calCost(currentPosition, next);
                if (!comeFrom.containsKey(Util.dim2ToDim1(next, mapCols)) ||
                        newCost<costNow.get(Util.dim2ToDim1(next, mapCols))) {
                    costNow.put(Util.dim2ToDim1(next, mapCols), newCost);

                    double priority = newCost + heuristic(goal, next);
                    frontier.add(new pair(next, priority));
                    comeFrom.put(Util.dim2ToDim1(next, mapCols), currentPosition);
                }
            }
        }

        if(costNow.getOrDefault(Util.dim2ToDim1(goal, mapCols), -1.) > 0) {
            Vector<Pos> posSets = getPath(comeFrom, start, goal);
            return new path(posSets, costNow.getOrDefault(Util.dim2ToDim1(goal, mapCols), -1.));
        }
        else {
            return new path(null, -1.);
        }
    }

    private Vector<Pos> getNeighbors(int[][] map, Pos now){
        int rows = map.length;
        int cols = map[0].length;
        Vector<Pos> res = new Vector<>();
        int[][] dir = new int[][]{ {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        for (int i=0; i<4; i++) {
            Pos tmp = new Pos(now.getX()+dir[i][0], now.getY()+dir[i][1]);
            if (tmp.getX()>=0 && tmp.getX()<rows && tmp.getY()>=0 && tmp.getY()<cols &&
                    map[tmp.getX()][tmp.getY()] != 4 && map[tmp.getX()][tmp.getY()] != 3 ) {
                /* 1: Player snake
                   3: walls
                   4: AI snake
                   The AI snake is allowed to pass through the player snake,
                   but the player snake cannot pass through the AI snake.
                */
                res.add(tmp);
            }
        }
        return res;
    }

    private double calCost(Pos current, Pos next) {
        return Math.abs(current.getX()-next.getX()) + Math.abs(current.getY()-next.getY());
    }

    private double heuristic(Pos goal, Pos next) {
        return Math.abs(goal.getX()-next.getX()) + Math.abs(goal.getY()-next.getY());
    }

    private Vector<Pos> getPath(HashMap<Integer, Pos> comeFrom, Pos start, Pos goal) {
        Stack<Pos> s = new Stack<>();
        Vector<Pos> posSets = new Vector<>();

        Pos tmp = new Pos(goal.getX(), goal.getY());
        while(!tmp.equal(start)){
            s.add(tmp);
            tmp = comeFrom.get(Util.dim2ToDim1(tmp, mapCols));
        }
        s.add(start);

        while(!s.empty()){
            posSets.add(s.pop());
        }

        return posSets;
    }

    private boolean moveToTail(){
        Pos head = body.getFirst().getCoordinate();
        Pos tail = body.getLast().getCoordinate();
        Vector<Pos> pathToTail = null;
        Vector<Pos> neighbours = getNeighbors(gameUI.getMap(), tail);
        double longestPath = 0.;
        for(Pos next : neighbours){
            Vector<Pos> res = getBFSPath(head, next);
            if(res.size() > longestPath){
                pathToTail = res;
                longestPath = res.size();
            }
        }
        if(longestPath > 1){
            Pos next = pathToTail.get(1);
            moveOneStep(next);
            return true;
        }else{
            return false;
        }
    }

    private Vector<Pos> getBFSPath(Pos start, Pos end) {
        Queue<Pos> frontier = new LinkedList<>();
        frontier.add(start);
        HashMap<Integer, Pos> comeFrom = new HashMap<>();
        comeFrom.put(Util.dim2ToDim1(start, mapCols), new Pos(-1, -1));

        while(!frontier.isEmpty()) {
            Pos current = frontier.poll();
            Vector<Pos> neighbors = getNeighbors(gameUI.getMap(), current);
            for(Pos next : neighbors){
                if(!comeFrom.containsKey(Util.dim2ToDim1(next, mapCols))) {
                    frontier.add(next);
                    comeFrom.put(Util.dim2ToDim1(next, mapCols), current);
                }
            }
        }
        if(comeFrom.containsKey(Util.dim2ToDim1(end, mapCols))) {
            Deque<Pos> res = new LinkedList<>();
            Pos tmp = new Pos(end.getX(), end.getY());
            while(tmp.equal(start)){
                tmp = comeFrom.get(Util.dim2ToDim1(tmp, mapCols));
                res.addFirst(tmp);
            }
            Vector<Pos> path = new Vector<>();
            path.addAll(res);
            return path;
        }
        else {
            return new Vector<Pos>();
        }
    }

    private boolean checkEat(Pos next){
        Vector<Pos> foodCoors = gameUI.getAllFoodCoor();
        for (Pos food : foodCoors) {
            if(food.getX() == next.getX() && food.getY() == next.getY()) {
                gameUI.removeFood(new Pos(next.getX(), next.getY()));
                return true;
            }
        }
        return false;
    }

    private void moveOneStep(Pos next) {
        Pos nextCoordinate = new Pos(next.getX(), next.getY());
        Body nextNode = new Body(nextCoordinate, headIcon);
        body.addFirst(nextNode);
        gameUI.setMap(next.getX(), next.getY(), 4);
        nextNode.getLabel().setVisible(true);
        gameUI.add(nextNode.getLabel());

        if(!checkEat(next)) {
            Body tail = body.pollLast();
            if (tail != null) {
                gameUI.setMap(tail.getCoordinate().getX(), tail.getCoordinate().getY(), 0);
                tail.getLabel().setVisible(false);
                gameUI.remove(tail.getLabel());
            }
        }
        else {
            gameUI.updateInfos("AILength", "" + body.size());
        }

        Util.printMap(gameUI.getMap(), "debug//map.txt");
    }

    public void goDie() {
        quit();
        gameUI.pause = true;
        gameUI.quit = true;
        gameUI.ai_die = true;
    }

    public void quit() {
        executor.shutdownNow();
    }

    private boolean attemptMove(Pos start, Pos target, Vector<Pos> path1){
        Deque<Pos> virSnake = virtualSnake();
        Vector<Pos> path = new Vector<>();
        for(Pos p : path1) {
            path.add(new Pos(p.getX(), p.getY()));
        }

        while(!path.isEmpty()) {
            Pos next = path.get(0);
            path.remove(0);
            if(next.equal(start))
                continue;
            virSnake.addFirst(next);
            if (!next.equal(target)) {
                virSnake.pollLast();
            }
            else {
                Pos tail = virSnake.getLast();
                Pos head = virSnake.getFirst();

                return existPath(head, tail, virSnake);
            }
        }
        return false;
    }

    private Deque<Pos> virtualSnake() {
        Deque<Pos> virSnake = new LinkedList<>();
        for(Body tmp : body){
            Pos coordinate = tmp.getCoordinate();
            virSnake.add(new Pos(coordinate.getX(), coordinate.getY()));
        }
        return virSnake;
    }

    private boolean existPath(Pos start, Pos goal, Deque<Pos> virSnake) {
        int rows = gameUI.getMap().length;
        int cols = gameUI.getMap()[0].length;
        int[][] tmpMap = new int[rows][cols];
        for(int i=0; i<rows; i++){
            tmpMap[i] = gameUI.getMap()[i].clone();
        }

        for(Pos body : virSnake) {
            tmpMap[body.getX()][body.getY()] = 4;
        }
        Vector<Pos> neighbors = getNeighbors(tmpMap, start);

        if(!neighbors.isEmpty()) {
            for(Pos next : neighbors){
                tmpMap[next.getX()][next.getY()] = 4;
                tmpMap[goal.getX()][goal.getY()] = 0;
                path p = findPath(tmpMap, next, goal);
                if(p.cost > 0)
                    return true;
                else
                    tmpMap[next.getX()][next.getY()] = 0;
            }
        }
        else {
            tmpMap[goal.getX()][goal.getY()] = 4;
            path p = findPath(tmpMap, start, goal);
            return p.cost > 0;
        }
        return false;
    }

    private void removeAll(){
        for (Body node : body) {
            node.getLabel().setVisible(false);
            gameUI.remove(node.getLabel());
        }
    }

    public void restart(){
        removeAll();
        quit();
    }

}

class pair1{
    Pos position;
    double cost;

    pair(Pos position, double cost){
        this.position = position;
        this.cost = cost;
    }

    int compareMin(pair p2){
        return Double.compare(this.cost, p2.cost);
    }
}

class path1{
    Vector<Pos> posSets;
    Double cost;
    path(Vector<Pos> posSets, Double cost){
        this.posSets = posSets;
        this.cost = cost;
    }

    int compare(path p2){return Double.compare(this.cost, p2.cost);}
}
