import bagel.*;
import java.util.Properties;
import bagel.util.Rectangle;

public class Banana extends Shootable {
    private static final double BANANA_SPEED = 1.8;
    private static final Image BANANA_IMAGE = new Image("res/banana.png");

    /**
     * Constructs a Banana at the specified position with the given direction.
     *
     * @param x         Initial x-coordinate.
     * @param y         Initial y-coordinate.
     * @param direction Direction of the banana ("left" or "right").
     */
    public Banana(double x, double y, String direction) {
        super(x, y, BANANA_IMAGE, direction, BANANA_SPEED);
    }

    @Override
    public void update() {
        updatePosition();
        draw();
    }
    /**
     * Gets the bounding box of the banana for collision detection.
     *
     * @return A {@link Rectangle} representing the banana's bounding box.
     */
    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(
                x - (BANANA_IMAGE.getWidth() / 2),  // Center-based positioning
                y - (BANANA_IMAGE.getHeight() / 2),
                BANANA_IMAGE.getWidth(),
                BANANA_IMAGE.getHeight()
        );
    }
    /**
     * Checks if the banana has expired (moved off-screen).
     *
     * @return {@code true} if the banana is expired, {@code false} otherwise.
     */
    @Override
    public boolean isExpired() {
        return distanceTraveled >= 300;
    }
    /**
     * Draws the banana on the screen.
     */
    @Override
    public void draw() {
        BANANA_IMAGE.draw(x, y);
    }
}
