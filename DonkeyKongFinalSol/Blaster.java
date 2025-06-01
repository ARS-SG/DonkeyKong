import bagel.DrawOptions;
import bagel.Drawing;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Rectangle;

/**
 * Represents Blaster object in the game.
 */
public class Blaster {
    private final Image BLASTER_IMAGE;
    private final double WIDTH, HEIGHT;
    private final double X, Y;
    private boolean isCollected = false;

    private int bullets = 5;  // Each blaster starts with 5 bullets


    /**
     * Constructs a Blaster at the specified position.
     *
     * @param x The initial x-coordinate of the hammer.
     * @param y The initial y-coordinate of the hammer.
     */
    public Blaster(double x, double y) {
        this.X = x;
        this.Y = y;
        this.BLASTER_IMAGE = new Image("res/blaster.png");
        this.WIDTH = BLASTER_IMAGE.getWidth();
        this.HEIGHT = BLASTER_IMAGE.getHeight();
    }

    /**
     * Returns the bounding box of the blaster for collision detection.
     * If the blaster has been collected, it returns an off-screen bounding box.
     *
     * @return A {@link Rectangle} representing the blaster's bounding box.
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

    public void draw(){
        BLASTER_IMAGE.draw(X,Y);
    }
    /**
     * Marks the blaster as collected, removing it from the screen.
     */
    public void collect() {
        isCollected = true;
    }

    /**
     * Checks if the blaster has been collected.
     *
     * @return {@code true} if the blaster is collected, {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Retrieves the number of bullets remaining in the blaster.
     * @return
     */
    public int getBullets() {
        return bullets;
    }

    /**
     * Decreases the number of bullets in the blaster by one.
     */
    public void useBullet() {
        if (bullets > 0) {
            bullets--;
        }
    }
    /**
     * Checks if the blaster has any bullets left.
     */
    public boolean hasBullets() {
        return bullets > 0;
    }



}
