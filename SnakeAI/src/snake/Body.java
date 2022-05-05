package snake;

import game.*;

import javax.swing.*;

// The structure of the body node of the snake
public class Body {
    public Position coor;
    public JLabel label;

    public Body(int x, int y, ImageIcon icon) {
        coor = new Position(x, y);
        label = new JLabel(icon);
    }

    public Body(Position coordinate, ImageIcon icon) {
        this.coor = coordinate;
        label = new JLabel(icon);
    }

    public Body(Body tempBody) {
        coor = tempBody.coor;
        label = tempBody.label;
    }

    public Position getCoordinate() {
        return coor;
    }

    public void setCoordinate(Position coordinate) {
        this.coor = coordinate;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }
}
