import bagel.*;
import java.util.Properties;
import bagel.util.Rectangle;

public abstract class Shootable {
    protected double x, y;

    protected Image projectileImage;
    protected double speed;  // this will be initialised in the bullet and banana classes

    protected double distanceTraveled = 0;

    protected String direction;

    /**
     * Constructs a Projectile at the specified position with the given image.
     */
    public Shootable(double x, double y , Image image ,String direction, double speed) {
        this.x = x;
        this.y = y;
        this.projectileImage=image;
        this.direction = direction;
        this.speed = speed;
    }

    /**
     * Method to move the projectile based on the speed
     */
    public void updatePosition() {
        if ("right".equals(direction)) {
            x += speed;
        } else if ("left".equals(direction)) {
            x -= speed;
        }
        distanceTraveled += speed;
    }


    /**
     * To stop rendering after 300 pixels
     *
     */
    public boolean isExpired() {
        return distanceTraveled >= 300;
    }


    /**
     * Draws the projectile on the screen.
     */
    public void draw() {
        projectileImage.draw(x, y);
    }

    /**
     * Gets the bullet’s bounding box for collision detection.
     * so that the bullet dissapears once it reaches the end of the window screen
     */

    public Rectangle getBoundingBox() {
        return new Rectangle(
                x - (projectileImage.getWidth() / 2),
                y - (projectileImage.getHeight() / 2),
                projectileImage.getWidth(),
                projectileImage.getHeight()
        );
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }



    public abstract void update();  // Let subclasses define behavior per frame

}
