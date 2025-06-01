import bagel.*;
import java.util.Properties;
import bagel.util.Rectangle;



public class Bullet extends Shootable {
    private static final double BULLET_SPEED = 3.8;

    public Bullet(double x, double y, String direction) {
        super(x, y, getBulletImage(direction), direction, BULLET_SPEED);
    }

    private static Image getBulletImage(String direction) {
        if ("right".equals(direction)) {
            return new Image("res/bullet_right.png");
        } else if ("left".equals(direction)) {
            return new Image("res/bullet_left.png");
        } else {
            // Default fallback to right-facing image if direction is invalid
            return new Image("res/bullet_right.png");
        }
    }

    @Override
    public void update() {
        updatePosition();
        draw();
    }
}
