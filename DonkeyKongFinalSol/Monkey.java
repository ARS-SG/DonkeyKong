import bagel.Image;
import bagel.util.Rectangle;

/**
 * Abstract class representing a Monkey in the game.
 * Handles common behavior like gravity, collision detection, and drawing.
 */
public abstract class Monkey {
    protected double x, y;
    protected double velocityY = 0;
    protected boolean isDestroyed = false;

    protected Image monkeyImage;

    protected double gravity;
    protected double terminalVelocity;

    protected String direction;
    protected int[] route;
    protected int currentRouteIndex;
    protected double distanceWalked;
    protected boolean isMoving = true;
    protected boolean onPlatform=false;

    /**
     * Constructs a Monkey at the specified position with the given image.
     *
     * @param x               Initial x-coordinate.
     * @param y               Initial y-coordinate.
     * @param gravity         The gravitational acceleration affecting the monkey.
     * @param terminalVelocity The maximum falling speed.
     * @param image           The image representing the monkey.
     * @param direction       Initial walking direction ("left" or "right").
     * @param route           Distance route pattern for monkey to follow.
     */
    public Monkey(double x, double y, double gravity, double terminalVelocity, Image image, String direction, int[] route) {
        this.x = x;
        this.y = y;
        this.gravity = gravity;
        this.terminalVelocity = terminalVelocity;
        this.monkeyImage = image;
        this.direction = direction;
        this.route = route;
        this.currentRouteIndex = 0;
        this.distanceWalked = 0;
    }


    /**
     * Updates the monkey's position based on gravity, movement, and collision.
     *
     * @param platforms Platforms the monkey can interact with.
     */
    public void update(Platform[] platforms) {
        if (!isDestroyed) {
            // Reset platform status before checks
            onPlatform = false;

            // Check if standing on any platform
            for (Platform platform : platforms) {
                if (getBoundingBox().intersects(platform.getBoundingBox())) {
                    y = platform.getY() - (platform.getHeight() / 2) - (monkeyImage.getHeight() / 2);
                    velocityY = 0;
                    onPlatform = true;
                    isMoving = true;
                    break;
                }
            }

            velocityY += gravity;
                y += velocityY;
                if (velocityY > terminalVelocity) {
                    velocityY = terminalVelocity;
                }

            // Only move horizontally if on a platform
            if (onPlatform && isMoving) {
                moveHorizontally(platforms);
            }
            


            /** Apply gravity if not on platform*/
            if (!onPlatform){
                reverseDirection();
                isMoving=false;
            }
            /** Draw the monkey */
            draw();
        }
    }

    /**
     * Handles horizontal movement and edge detection logic.
     *
     * @param platforms Platforms to interact with while moving.
     */
    protected void moveHorizontally(Platform[] platforms) {


        double moveDistance = 0.5; // Movement speed
        double newX = x;
        double halfWidth = monkeyImage.getWidth() / 2;
        double halfHeight = monkeyImage.getHeight() / 2;

        /** Determine new x based on current direction */
        if (direction.equals("left")) {
            newX -= moveDistance;
        } else {
            newX += moveDistance;
        }

        /** Ensure monkey doesn't move outside the screen */
        if (newX < halfWidth || newX > ShadowDonkeyKong.getScreenWidth() - halfWidth) {
            reverseDirection();
            return;
        }



        /** Check if the monkey is still on a platform */
        for (Platform platform : platforms) {
            Rectangle platformBox = platform.getBoundingBox();

            // Only check platforms we're currently on
            if (getBoundingBox().intersects(platformBox)) {
                /** Check for platform edge; reverse if falling off */
                double platformLeft = platform.getX() - (platform.getWidth() / 2);
                double platformRight = platform.getX() + (platform.getWidth() / 2);
                double monkeyLeft = newX - halfWidth;
                double monkeyRight = newX + halfWidth;

                if ((direction.equals("left") && monkeyLeft < platformLeft)) {
                    reverseDirection();
                    return;
                } else if (direction.equals("right") && monkeyRight > platformRight) {
                    reverseDirection();
                    return;
                }

                break; // Only need to check one platform
            }
        }


        /** Update position and distance walked */
        x = newX;
        distanceWalked += moveDistance;

        /** Check if segment of route is completed, then reverse direction */
        if (distanceWalked >= route[currentRouteIndex]) {
            distanceWalked = 0;
            currentRouteIndex = (currentRouteIndex + 1) % route.length;
            reverseDirection();
        }
    }

    /**
     * Reverses the monkey's direction and updates the image accordingly.
     */
    protected void reverseDirection() {
        direction = direction.equals("left") ? "right" : "left";
        
        // Update the sprite based on the new direction
        String imagePath;
        if (this instanceof IntelligentMonkey) {
            imagePath = "res/intelli_monkey_" + direction + ".png";
        } else { // NormalMonkey
            imagePath = "res/normal_monkey_" + direction + ".png";
        }
        
        monkeyImage = new Image(imagePath);
    }


    /**
     * Draws the monkey on the screen.
     */
    public void draw() {
        monkeyImage.draw(x, y);
    }

    /**
     * Destroys the monkey so it no longer updates or renders.
     */
    public void destroy() {
        isDestroyed = true;
        System.out.println(getClass().getSimpleName() + " destroyed!");
    }

    /**
     * Gets the monkey’s bounding box for collision detection.
     *
     * @return A Rectangle representing the monkey's bounds.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(
                x - (monkeyImage.getWidth() / 2),
                y - (monkeyImage.getHeight() / 2),
                monkeyImage.getWidth(),
                monkeyImage.getHeight()
        );
    }

    /**
     * Indicates whether the monkey has been destroyed.
     *
     * @return True if destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

}
