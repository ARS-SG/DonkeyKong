import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Colour;

/**
 * Represents a stationary platform in the game.
 * Platforms provide surfaces for Mario to walk on and interact with.
 */
public class Platform {
    private final Image PLATFORM_IMAGE; // Image representing the platform
    private final double X, Y;               // Position of the platform
    private final double WIDTH, HEIGHT; // Dimensions of the platform

    /**
     * Constructs a platform at the specified position.
     *
     * @param startX The initial x-coordinate of the platform.
     * @param startY The initial y-coordinate of the platform.
     */
    public Platform(double startX, double startY) {
        // Load platform sprite
        this.PLATFORM_IMAGE = new Image("res/platform.png");
        this.X = startX;
        this.Y = startY;

        // Set platform dimensions based on the image size
        this.WIDTH = PLATFORM_IMAGE.getWidth();
        this.HEIGHT = PLATFORM_IMAGE.getHeight();
    }

    /**
     * Draws the platform on the screen.
     */
    public void draw() {
        PLATFORM_IMAGE.draw(X, Y);
//        drawBoundingBox(); // Uncomment for debugging
    }

    /**
     * Retrieves the x-coordinate of the platform.
     *
     * @return The x-coordinate of the platform.
     */
    public double getX() {
        return X;
    }

    /**
     * Retrieves the y-coordinate of the platform.
     *
     * @return The y-coordinate of the platform.
     */
    public double getY() {
        return Y;
    }

    /**
     * Retrieves the width of the platform.
     *
     * @return The width of the platform.
     */
    public double getWidth() {
        return WIDTH;
    }

    /**
     * Retrieves the height of the platform.
     *
     * @return The height of the platform.
     */
    public double getHeight() {
        return HEIGHT;
    }

    /**
     * Returns a center-based bounding box that aligns with how the platform is drawn.
     * This bounding box is used for collision detection.
     *
     * @return A {@link Rectangle} representing the platform's bounding box.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(
                X - (WIDTH / 2),
                Y - (HEIGHT / 2),
                WIDTH,
                HEIGHT
        );
    }
}
