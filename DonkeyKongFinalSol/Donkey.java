import bagel.*;
import bagel.util.Colour;
import bagel.util.Rectangle;

/**
 * Represents Donkey Kong in the game, affected by gravity and platform collisions.
 * The Donkey object moves downward due to gravity and lands on platforms when applicable.
 */
public class Donkey {
    private final Image DONKEY_IMAGE;
    private final double X; // constant because x does not change, only relying on falling
    private double y;
    private double velocityY = 0;
        
    private int health = 5;  // Initial health


    /**
     * Constructs a new Donkey at the specified starting position.
     *
     * @param startX The initial x-coordinate of Donkey.
     * @param startY The initial y-coordinate of Donkey.
     */
    public Donkey(double startX, double startY) {
        this.DONKEY_IMAGE = new Image("res/donkey_kong.png"); // Load Donkey Kong sprite
        this.X = startX;
        this.y = startY;
    }

    /**
     * Updates Donkey's position by applying gravity and checking for platform collisions.
     * If Donkey lands on a platform, the velocity is reset to zero.
     *
     * @param platforms An array of platforms Donkey can land on.
     */
    public void update(Platform[] platforms) {
        // Apply gravity
        velocityY += Physics.DONKEY_GRAVITY;
        y += velocityY;
        if (velocityY > Physics.DONKEY_TERMINAL_VELOCITY) {
            velocityY = Physics.DONKEY_TERMINAL_VELOCITY;
        }

        // Check for platform collisions
        for (Platform platform : platforms) {
            if (isTouchingPlatform(platform)) {
                // Position Donkey on top of the platform
                y = platform.getY() - (platform.getHeight() / 2) - (DONKEY_IMAGE.getHeight() / 2);
                velocityY = 0; // Stop downward movement
                break;
            }
        }

        // Draw Donkey
        draw();
    }

    /**
     * Checks if Donkey is colliding with a given platform.
     *
     * @param platform The platform to check for collision.
     * @return {@code true} if Donkey is touching the platform, {@code false} otherwise.
     */
    private boolean isTouchingPlatform(Platform platform) {
        Rectangle donkeyBounds = getBoundingBox();
        return donkeyBounds.intersects(platform.getBoundingBox());
    }

    /**
     * Draws Donkey on the screen.
     */
    public void draw() {
        DONKEY_IMAGE.draw(X, y);
//        drawBoundingBox(); // Uncomment for debugging
    }

    /**
     * Returns Donkey's bounding box for collision detection.
     *
     * @return A {@link Rectangle} representing Donkey's bounding box.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(
                X - (DONKEY_IMAGE.getWidth() / 2),
                y - (DONKEY_IMAGE.getHeight() / 2),
                DONKEY_IMAGE.getWidth(),
                DONKEY_IMAGE.getHeight()
        );
    }

    /**
     *  Reduces Donkey's health by 1.
     */
    public void decreaseHealth() {
        if (health > 0) {
            health--;
        }
    }

    /**
     * Returns the current health of Donkey.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the
     */
    public boolean isDefeated() {
        return health <= 0;
    }


}
