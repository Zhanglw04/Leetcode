package game;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FoodSet {
    private Scene gameUI;
    private List<Food> food = new LinkedList<>();
    private Vector<Pos> foodCoors = new Vector<>();
    private Vector<JLabel> foodLabels = new Vector<>();
    private static final int foodKinds = 5;
    private int[] points = new int[]{50, 40, 30, 20, 10};
    private ImageIcon[] foodIcon = new ImageIcon[foodKinds];

    public FoodSet(Scene gameUI){
        this.gameUI = gameUI;
        for(int i=0; i<foodKinds; i++) {
            foodIcon[i] = new ImageIcon("food//food" + i + ".png");
            foodIcon[i].setImage(foodIcon[i].getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
        }

        if(!gameUI.debug) produceFood();
        else readHistory();
        show();
    }

    public void produceFood(){
        Random rand = new Random();
        int amount = rand.nextInt(3) + 3;
        double prob;
        int foodTag = 2;
        Food newFood;

        for(int i=0; i<amount; i++) {
            Pos coordinate = gameUI.randomCoordinate();
            foodCoors.add(coordinate);

            prob = rand.nextDouble();
            if(prob >= 0 && prob <0.1)
                foodTag = 0;    //10%
            else if(prob >= 0.1  && prob <0.25)
                foodTag = 4;    //15%
            else if(prob >= 0.25 && prob <0.5)
                foodTag = 3;    //25%
            else if(prob >= 0.5  && prob <0.8)
                foodTag = 2;    //30%
            else if(prob >= 0.8 && prob <1)
                foodTag = 1;    //20%

            gameUI.setMap(coordinate.getX(), coordinate.getY(), 2);
            newFood = new Food(foodTag, coordinate, foodIcon[foodTag]);
            food.add(newFood);
            gameUI.add(newFood.label);
            foodLabels.add(newFood.label);
        }
    }

    public void readHistory(){
        Vector<Pos> coors = gameUI.food_history;
        for (Pos coordinate : coors){
            foodCoors.add(coordinate);
            Food newFood = new Food(0, coordinate, foodIcon[0]);
            food.add(newFood);
            gameUI.add(newFood.label);
            foodLabels.add(newFood.label);
            gameUI.updateInfos("Amount of Food", food.size() + "");
            show();
            Util.printMap(gameUI.getMap(), "debug//map.txt");
        }
    }

    public void show(){
        for (Food node : food) {
            node.label.setBounds(Util.getPixel(node.coordinate.getY(), 5, 22),
                    Util.getPixel(node.coordinate.getX(), 5, 22), 20, 20);
            node.label.setVisible(true);
        }
    }

    public int getFoodPoint(Pos position){
        for (Iterator<Food> iter = food.iterator(); iter.hasNext();) {
            Food node = iter.next();
            if(node.coordinate.getX()==position.getX() && node.coordinate.getY()==position.getY()) {
                node.label.setVisible(false);
                gameUI.remove(node.label);
                iter.remove();
                return points[node.kind];
            }
        }
        return -1;
    }

    public Vector<Pos> getAllFoodCoors(){
        return foodCoors;
    }

    public void removeFoodCoordinate(Pos coordinate){
        for(int i=0; i<foodCoors.size(); i++) {
            if(foodCoors.get(i).getX()==coordinate.getX() && foodCoors.get(i).getY()==coordinate.getY()){
                foodCoors.remove(i);
                foodLabels.get(i).setVisible(false);
                gameUI.remove(foodLabels.get(i));
                foodLabels.remove(i);

                if(foodCoors.isEmpty()){
                    foodLabels.clear();
                    produceFood();
                }
                return;
            }
        }
    }

    public void removeAll(){
        for (Food node : food) {
            gameUI.setMap(node.coordinate.getX(), node.coordinate.getY(), 0);
            node.label.setVisible(false);
            gameUI.remove(node.label);
        }
        food.clear();
    }

    public class Food {
        int kind;
        JLabel label;
        Pos coordinate;

        public Food(int kind, Pos coordinate, ImageIcon icon){
            this.kind = kind;
            label = new JLabel(icon);
            this.coordinate = coordinate;
        }
    }
}
