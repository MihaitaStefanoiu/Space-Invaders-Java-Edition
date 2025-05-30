package chicken.invaders;

import javafx.scene.image.ImageView;

public class Item {
    protected ImageView shape;

    // Constructor care creează ImageView dintr-un path și îl poziționează
    public Item(String path, double x, double y) {
        this.shape = new ImageView(path);
        this.shape.setX(x);
        this.shape.setY(y);
    }

    // Getter pentru forma grafică (ImageView)
    public ImageView getShape() {
        return shape;
    }

    // Setter pentru forma grafică (poate fi util la anumite extensii)
    public void setShape(ImageView shape) {
        this.shape = shape;
    }

    // Getters și setters pentru poziție X și Y pe ecran
    public double getX() {
        return shape.getX();
    }

    public double getY() {
        return shape.getY();
    }

    public void setX(double x) {
        shape.setX(x);
    }

    public void setY(double y) {
        shape.setY(y);
    }
}
