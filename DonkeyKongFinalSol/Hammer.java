import bagel.DrawOptions;
import bagel.Drawing;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Rectangle;

/**
 * Represents a Hammer collectible in the game.
 * The hammer can be collected by the player, at which point it disappears from the screen.
 */
public class Hammer {
    private final Image HAMMER_IMAGE;
    private final double WIDTH, HEIGHT;
    private final double X, Y;
    private boolean isCollected = false;

    /**
     * Constructs a Hammer at the specified position.
     *
     * @param startX The initial x-coordinate of the hammer.
     * @param startY The initial y-coordinate of the hammer.
     */
    public Hammer(double startX, double startY) {
        this.HAMMER_IMAGE = new Image("res/hammer.png");
        this.X = startX;
        this.Y = startY;
        this.WIDTH = HAMMER_IMAGE.getWidth();
        this.HEIGHT = HAMMER_IMAGE.getHeight();
    }

    /**
     * Returns the bounding box of the hammer for collision detection.
     * If the hammer has been collected, it returns an off-screen bounding box.
     *
     * @return A {@link Rectangle} representing the hammer's bounding box.
     */
    public Rectangle getBoundingBox() {
        if (isCollected) {
            return new Rectangle(-1000, -1000, 0, 0); // Move off-screen if collected
        }
        return new Rectangle(
                X - (WIDTH / 2),  // Center-based positioning
                Y - (HEIGHT / 2),
                WIDTH,
                HEIGHT
        );
    }

    /**
     * Draws the hammer on the screen if it has not been collected.
     */
    public void draw() {
        if (!isCollected) {
            HAMMER_IMAGE.draw(X, Y); // Bagel centers images automatically
//            drawBoundingBox(); // Uncomment for debugging
        }
    }

    /**
     * Marks the hammer as collected, removing it from the screen.
     */
    public void collect() {
        isCollected = true;
    }

    /**
     * Checks if the hammer has been collected.
     *
     * @return {@code true} if the hammer is collected, {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

}
