package snake;

import game.Position;
import game.Scene;
import game.Util;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AISnake {
    private Scene gameUI;
    private Deque<Body> body = new LinkedList<>();
    private final ImageIcon headIcon;
    private final ImageIcon bodyIcon;
    private ScheduledExecutorService executor;
    static Comparator<pair> cmpMin = pair::compareMin;
    static Comparator<path> pathCmpMin = path::compare;
    private int mapCols;

    public AISnake(Scene gameUI) {
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
        int speed = 400;    // modify
        executor.scheduleAtFixedRate(() -> {
            if (!gameUI.pause && !gameUI.quit) {
                move();
                Util.printMap(gameUI.getMap(), "debug//map.txt");
                show();
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }

    private void show(){
        for (Body node:body) {
            node.getLabel().setBounds(Util.getPixel(node.getCoordinate().getY(), gameUI.padding, gameUI.pixelPerUnit),
                    Util.getPixel(node.getCoordinate().getX(), gameUI.padding, gameUI.pixelPerUnit), 20, 20);
            node.getLabel().setIcon(bodyIcon);
        }
        Body node = body.getFirst();
        node.getLabel().setIcon(headIcon);
    }

    private void move(){
        Position head = body.getFirst().getCoordinate();
        PriorityQueue<path> allPath = new PriorityQueue<>(pathCmpMin);
        Vector<Position> foodCoors = gameUI.getAllFoodCoor();

        for (Position food : foodCoors) {
            Position goal = new Position(food.getX(), food.getY());
            path pathToFood = findPath(gameUI.getMap(), head, goal);
            if(pathToFood.cost > 0) {
                allPath.add(pathToFood);
            }
        }

        if(allPath.isEmpty()) {
            if(!moveToTail()) {
//                gameUI.updateInfos("Status", "die");
                System.out.println("unable to find path");
                show();
                gameUI.repaint();
                goDie();
            }
            else {
//                gameUI.updateInfos("Status", "go");
            }
        }
        else {
            while (!allPath.isEmpty()) {
                path p = allPath.poll();
                Vector<Position> pathToGoal = p.positionSets;
                Position goal = pathToGoal.lastElement();

                if (attemptMove(head, goal, pathToGoal)) {
                    Position next = pathToGoal.get(1);
                    moveOneStep(next);
//                    gameUI.updateInfos("Status", "to eat");
                    return;
                }
            }

            if(!moveToTail()) {
                System.out.println("unable to find path");
                show();
                gameUI.repaint();
                goDie();
            }else {
//                gameUI.updateInfos("Status", "go");
            }
        }
    }

    private path findPath(int[][] map, Position start, Position goal){
        PriorityQueue<pair> frontier = new PriorityQueue<>(cmpMin);
        frontier.add(new pair(start, 0));
        HashMap<Integer, Position> comeFrom = new HashMap<>();
        comeFrom.put(Util.dim2ToDim1(start, mapCols), new Position(-1, -1));
        HashMap<Integer, Double> costNow = new HashMap<>();
        costNow.put(Util.dim2ToDim1(start, mapCols), 0.);

        while(!frontier.isEmpty()){
            pair current = frontier.poll();
            Position currentPosition = current.position;
            if(currentPosition.equal(goal)) break;

            Vector<Position> neighbors = getNeighbors(map, currentPosition);
            for (Position next : neighbors) {
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
            Vector<Position> positionSets = getPath(comeFrom, start, goal);
            return new path(positionSets, costNow.getOrDefault(Util.dim2ToDim1(goal, mapCols), -1.));
        }
        else {
            return new path(null, -1.);
        }
    }

    private Vector<Position> getNeighbors(int[][] map, Position now){
        int rows = map.length;
        int cols = map[0].length;
        Vector<Position> res = new Vector<>();
        int[][] dir = new int[][]{ {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        for (int i=0; i<4; i++) {
            Position tmp = new Position(now.getX()+dir[i][0], now.getY()+dir[i][1]);
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

    private double calCost(Position current, Position next) {
        return Math.abs(current.getX()-next.getX()) + Math.abs(current.getY()-next.getY());
    }

    private double heuristic(Position goal, Position next) {
        return Math.abs(goal.getX()-next.getX()) + Math.abs(goal.getY()-next.getY());
    }

    private Vector<Position> getPath(HashMap<Integer, Position> comeFrom, Position start, Position goal) {
        Stack<Position> s = new Stack<>();
        Vector<Position> positionSets = new Vector<>();

        Position tmp = new Position(goal.getX(), goal.getY());
        while(!tmp.equal(start)){
            s.add(tmp);
            tmp = comeFrom.get(Util.dim2ToDim1(tmp, mapCols));
        }
        s.add(start);

        while(!s.empty()){
            positionSets.add(s.pop());
        }

        return positionSets;
    }

    private boolean moveToTail(){
        Position head = body.getFirst().getCoordinate();
        Position tail = body.getLast().getCoordinate();
        Vector<Position> pathToTail = null;
        Vector<Position> neighbours = getNeighbors(gameUI.getMap(), tail);
        double longestPath = 0.;
        for(Position next : neighbours){
            Vector<Position> res = getBFSPath(head, next);
            if(res.size() > longestPath){
                pathToTail = res;
                longestPath = res.size();
            }
        }
        if(longestPath > 1){
            Position next = pathToTail.get(1);
            moveOneStep(next);
            return true;
        }else{
            return false;
        }
    }

    private Vector<Position> getBFSPath(Position start, Position end) {
        Queue<Position> frontier = new LinkedList<>();
        frontier.add(start);
        HashMap<Integer, Position> comeFrom = new HashMap<>();
        comeFrom.put(Util.dim2ToDim1(start, mapCols), new Position(-1, -1));

        while(!frontier.isEmpty()) {
            Position current = frontier.poll();
            Vector<Position> neighbors = getNeighbors(gameUI.getMap(), current);
            for(Position next : neighbors){
                if(!comeFrom.containsKey(Util.dim2ToDim1(next, mapCols))) {
                    frontier.add(next);
                    comeFrom.put(Util.dim2ToDim1(next, mapCols), current);
                }
            }
        }
        if(comeFrom.containsKey(Util.dim2ToDim1(end, mapCols))) {
            Deque<Position> res = new LinkedList<>();
            Position tmp = new Position(end.getX(), end.getY());
            while(tmp.equal(start)){
                tmp = comeFrom.get(Util.dim2ToDim1(tmp, mapCols));
                res.addFirst(tmp);
            }
            Vector<Position> path = new Vector<>();
            path.addAll(res);
            return path;
        }
        else {
            return new Vector<Position>();
        }
    }

    private boolean checkEat(Position next){
        Vector<Position> foodCoors = gameUI.getAllFoodCoor();
        for (Position food : foodCoors) {
            if(food.getX() == next.getX() && food.getY() == next.getY()) {
                gameUI.removeFood(new Position(next.getX(), next.getY()));
                return true;
            }
        }
        return false;
    }

    private void moveOneStep(Position next) {
        Position nextCoordinate = new Position(next.getX(), next.getY());
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

    private boolean attemptMove(Position start, Position target, Vector<Position> path1){
        Deque<Position> virSnake = virtualSnake();
        Vector<Position> path = new Vector<>();
        for(Position p : path1) {
            path.add(new Position(p.getX(), p.getY()));
        }

        while(!path.isEmpty()) {
            Position next = path.get(0);
            path.remove(0);
            if(next.equal(start))
                continue;
            virSnake.addFirst(next);
            if (!next.equal(target)) {
                virSnake.pollLast();
            }
            else {
                Position tail = virSnake.getLast();
                Position head = virSnake.getFirst();

                return existPath(head, tail, virSnake);
            }
        }
        return false;
    }

    private Deque<Position> virtualSnake() {
        Deque<Position> virSnake = new LinkedList<>();
        for(Body tmp : body){
            Position coordinate = tmp.getCoordinate();
            virSnake.add(new Position(coordinate.getX(), coordinate.getY()));
        }
        return virSnake;
    }

    private boolean existPath(Position start, Position goal, Deque<Position> virSnake) {
        int rows = gameUI.getMap().length;
        int cols = gameUI.getMap()[0].length;
        int[][] tmpMap = new int[rows][cols];
        for(int i=0; i<rows; i++){
            tmpMap[i] = gameUI.getMap()[i].clone();
        }

        for(Position body : virSnake) {
            tmpMap[body.getX()][body.getY()] = 4;
        }
        Vector<Position> neighbors = getNeighbors(tmpMap, start);

        if(!neighbors.isEmpty()) {
            for(Position next : neighbors){
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

class pair{
    Position position;
    double cost;

    pair(Position position, double cost){
        this.position = position;
        this.cost = cost;
    }

    int compareMin(pair p2){
        return Double.compare(this.cost, p2.cost);
    }
}

class path{
    Vector<Position> positionSets;
    Double cost;
    path(Vector<Position> positionSets, Double cost){
        this.positionSets = positionSets;
        this.cost = cost;
    }

    int compare(path p2){return Double.compare(this.cost, p2.cost);}
}
