import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Colour;

import java.awt.event.InputEvent;
import java.util.ArrayList;

/**
 * Represents the player-controlled character, Mario.
 * Mario can move, jump, climb ladders, pick up a hammer, and interact with platforms.
 */
public class Mario {
    private double x, y; // Mario's position
    private double velocityY = 0; // Vertical velocity
    private boolean isJumping = false; // Whether Mario is currently jumping
    private boolean hasHammer = false; // Whether Mario has collected a hammer
    private boolean hasBlaster = false; // Whether Mario has collected a blaster
    // Mario images for different states
    private Image marioImage;
    private final Image MARIO_RIGHT_IMAGE;
    private final Image MARIO_LEFT_IMAGE;
    private final Image MARIO_HAMMER_LEFT_IMAGE;
    private final Image MARIO_HAMMER_RIGHT_IMAGE;
    private final Image MARIO_BLASTER_LEFT_IMAGE;
    private final Image MARIO_BLASTER_RIGHT_IMAGE;


    // Movement physics constants
    private static final double JUMP_STRENGTH = -5;
    private static final double MOVE_SPEED = 3.5;
    private static final double CLIMB_SPEED = 2;

    private static double height;
    private static double width;
    private boolean isFacingRight = true; // Mario's facing direction

    private enum HeldItem { NONE, HAMMER, BLASTER }
    private HeldItem currentItem = HeldItem.NONE;

    private ArrayList<Blaster> collectedBlasters = new ArrayList<>();



    /**
     * Constructs a Mario character at the specified starting position.
     *
     * @param startX Initial x-coordinate.
     * @param startY Initial y-coordinate.
     */
    public Mario(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        // Load images for left and right-facing Mario
        this.MARIO_RIGHT_IMAGE = new Image("res/mario_right.png");
        this.MARIO_LEFT_IMAGE = new Image("res/mario_left.png");
        this.MARIO_HAMMER_RIGHT_IMAGE = new Image("res/mario_hammer_right.png");
        this.MARIO_HAMMER_LEFT_IMAGE = new Image("res/mario_hammer_left.png");
        this.MARIO_BLASTER_LEFT_IMAGE = new Image("res/mario_blaster_left.png");
        this.MARIO_BLASTER_RIGHT_IMAGE = new Image("res/mario_blaster_right.png");

        // Default Mario starts facing right
        this.marioImage = MARIO_HAMMER_RIGHT_IMAGE;

        width = marioImage.getWidth();
        height = marioImage.getHeight();
    }

    /**
     * Sets whether Mario has picked up the hammer.
     *
     * @param status {@code true} if Mario has the hammer, {@code false} otherwise.
     */
    public void setHasHammer(boolean status) {
        this.hasHammer = status;
    }

    /**
     * Sets whether Mario has picked up the blaster.
     *
     * @param status {@code true} if Mario has the blaster, {@code false} otherwise.
     */
    public void setHasBlaster(boolean status) {
        this.hasBlaster = status;
    }


    /**
     * Checks if Mario has the hammer.
     *
     * @return {@code true} if Mario has the hammer, {@code false} otherwise.
     */
    public boolean holdHammer() {
        return this.hasHammer;
    }

    /**
     * Adding a method to fire bullets
     */
    public Bullet fireBullet() {
        // Can't shoot if holding hammer
        if (holdHammer()) {
            return null;
        }
        // Find first blaster with bullets remaining
        for (Blaster blaster : collectedBlasters) {
            if (blaster.hasBullets()) {
                blaster.useBullet();
                double bulletX = isFacingRight ? x + width / 2 : x - width / 2;
                double bulletY = y;
                String direction = isFacingRight ? "right" : "left";
                return new Bullet(bulletX, bulletY, direction);
            }
        }
        return null;  // No bullets left
    }
    
    /**
     * Retrieves the number of bullets remaining in all collected blasters.
     * @return
     */
    public int getTotalBullets() {
        int total = 0;
        for (Blaster blaster : collectedBlasters) {
            total += blaster.getBullets();
        }
        return total;
    }
    


    /**
     * Gets Mario's bounding box for collision detection.
     *
     * @return A {@link Rectangle} representing Mario's collision area.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(
                x - (width / 2),
                y - (height / 2),
                width,
                height
        );
    }

    /**
     * Updates Mario's movement, jumping, ladder climbing, hammer collection, and interactions.
     * This method is called every frame to process player input and update Mario's state.
     *
     * @param input     The player's input (keyboard/mouse).
     * @param ladders   The array of ladders in the game that Mario can climb.
     * @param platforms The array of platforms in the game that Mario can walk on.
     * @param hammer    The hammer object that Mario can collect and use.
     */
    public void update(Input input, Ladder[] ladders, Platform[] platforms, Hammer hammer, Blaster[] blasters) {
        handleHorizontalMovement(input); // 1) Horizontal movement
        // updateSprite(hammer); // 2) Update Mario’s current sprite (hammer or not, facing left or right)
        handleHammerCollection(hammer); // 3) If you just picked up the hammer:
        handleBlasterCollection(blasters); //3.1) If you just picked up a blaster.


        updateSprite(); // 4) Now replace sprite (since either isFacingRight or hasHammer could have changed)

        // 5) Ladder logic – check if on a ladder
        boolean isOnLadder;
        isOnLadder = handleLadders(input, ladders);

        // 6) Jump logic: if on platform (we'll detect after we move) but let's queue jump if needed
        boolean wantsToJump = input.wasPressed(Keys.SPACE);

        // 7) If not on ladder, apply gravity, move Mario
        if (!isOnLadder) {
            velocityY += Physics.MARIO_GRAVITY;
            velocityY = Math.min(Physics.MARIO_TERMINAL_VELOCITY, velocityY);
        }

        // 8) Actually move Mario vertically after gravity
        y += velocityY;

        // 9) Check for platform collision AFTER Mario moves
        boolean onPlatform;
        onPlatform = handlePlatforms(platforms, hammer);

        // 10) If we are on the platform, allow jumping; Prevent Mario from falling below the ground
        handleJumping(onPlatform, wantsToJump);

        // 11) Enforce horizontal screen bounds
        enforceBoundaries();

        // 12) Draw Mario
        draw();
    }

    public void update(Input input, Ladder[] ladders, Platform[] platforms, Hammer hammer) {
        // Call the full version, passing an empty array for blasters
        update(input, ladders, platforms, hammer, new Blaster[0]);
    }

    /**
     * Handles Mario's interaction with platforms to determine if he is standing on one.
     * Mario will only snap to a platform if he is moving downward (velocityY >= 0),
     * preventing his jump from being interrupted in mid-air.
     *
     * @param platforms An array of {@link Platform} objects representing the platforms in the game.
     * @param hammer    A {@link Hammer} object (not used in this method, but might be for future logic).
     * @return {@code true} if Mario is standing on a platform, {@code false} otherwise.
     */
    private boolean handlePlatforms(Platform[] platforms, Hammer hammer) {
        boolean onPlatform = false;

        // We'll only snap Mario to a platform if he's moving downward (velocityY >= 0)
        // so we don't kill his jump in mid-air.
        if (velocityY >= 0) {
            for (Platform platform : platforms) {
                Rectangle marioBounds    = getBoundingBox();
                Rectangle platformBounds = platform.getBoundingBox();

                if (marioBounds.intersects(platformBounds)) {
                    double marioBottom = marioBounds.bottom();
                    double platformTop = platformBounds.top();

                    // If Mario's bottom is at or above the platform's top
                    // and not far below it (a small threshold based on velocity)
                    if (marioBottom <= platformTop + velocityY) {
                        // Snap Mario so his bottom = the platform top
                        y = platformTop - (marioImage.getHeight() / 2);
                        velocityY = 0;
                        isJumping = false;
                        onPlatform = true;
                        break; // We found a platform collision
                    }
                }
            }
        }
        return onPlatform;
    }

    /**
     * Handles Mario's interaction with ladders, allowing him to climb up or down
     * based on user input and position relative to the ladder.
     *
     * Mario can only climb if he is within the horizontal boundaries of the ladder.
     * He stops sliding unintentionally when not pressing movement keys.
     *
     * @param input   The {@link Input} object that checks for user key presses.
     * @param ladders An array of {@link Ladder} objects representing ladders in the game.
     * @return {@code true} if Mario is on a ladder, {@code false} otherwise.
     */
    private boolean handleLadders(Input input, Ladder[] ladders) {
        boolean isOnLadder = false;
        for (Ladder ladder : ladders) {
            double ladderLeft  = ladder.getX() - (ladder.getWidth() / 2);
            double ladderRight = ladder.getX() + (ladder.getWidth() / 2);
            double marioRight  = x + (marioImage.getWidth() / 2);
            double marioBottom = y + (marioImage.getHeight() / 2);
            double ladderTop    = ladder.getY() - (ladder.getHeight() / 2);
            double ladderBottom = ladder.getY() + (ladder.getHeight() / 2);

            if (isTouchingLadder(ladder)) {
                // Check horizontal overlap so Mario is truly on the ladder
                if (marioRight - marioImage.getWidth() / 2 > ladderLeft && marioRight - marioImage.getWidth() / 2 < ladderRight) {
                    isOnLadder = true;

                    // Stop Mario from sliding up when not moving**
                    if (!input.isDown(Keys.UP) && !input.isDown(Keys.DOWN)) {
                        velocityY = 0;  // Prevent sliding inertia effect
                    }

                    // ----------- Climb UP -----------
                    if (input.isDown(Keys.UP)) {
                        y -= CLIMB_SPEED;
                        velocityY = 0;
                    }

                    // ----------- Climb DOWN -----------
                    if (input.isDown(Keys.DOWN)) {
                        double nextY = y + CLIMB_SPEED;
                        double nextBottom = nextY + (marioImage.getHeight() / 2);

                        if (marioBottom > ladderTop && nextBottom <= ladderBottom) {
                            y = nextY;
                            velocityY = 0;
                        } else if (marioBottom == ladderBottom) {
                            velocityY = 0;
                        } else if (ladderBottom - marioBottom < CLIMB_SPEED) {
                            y = y + ladderBottom - marioBottom;
                            velocityY = 0;
                        }
                    }
                }
            } else if (marioBottom == ladderTop && input.isDown(Keys.DOWN) && (marioRight - marioImage.getWidth() / 2 > ladderLeft && marioRight - marioImage.getWidth() / 2  < ladderRight)) {
                double nextY = y + CLIMB_SPEED;
                y = nextY;
                velocityY = 0; // ignore gravity
            } else if (marioBottom == ladderBottom && input.isDown(Keys.DOWN) && (marioRight - marioImage.getWidth() / 2 > ladderLeft && marioRight - marioImage.getWidth() / 2  < ladderRight)) {
                velocityY = 0; // ignore gravity
            }
        }
        return isOnLadder;
    }

    /** Handles horizontal movement based on player input. */
    private void handleHorizontalMovement(Input input) {
        if (input.isDown(Keys.LEFT)) {
            x -= MOVE_SPEED;
            isFacingRight = false;
        } else if (input.isDown(Keys.RIGHT)) {
            x += MOVE_SPEED;
            isFacingRight = true;
        }
    }

    /*Updates Mario's sprite based on his current state.*//*
    private void updateSprite(Hammer hammer) {
        marioImage = hasHammer
                ? (isFacingRight ? MARIO_HAMMER_RIGHT_IMAGE : MARIO_HAMMER_LEFT_IMAGE)
                : (isFacingRight ? MARIO_RIGHT_IMAGE : MARIO_LEFT_IMAGE);
    }

    private void updateSprite( Blaster[] blasters) {
        marioImage = hasBlaster
                ? (isFacingRight ? MARIO_BLASTER_RIGHT_IMAGE : MARIO_BLASTER_LEFT_IMAGE)
                : (isFacingRight ? MARIO_RIGHT_IMAGE : MARIO_LEFT_IMAGE);
    }*/




    /** Handles collecting the hammer if Mario is in contact with it. */
    private void handleHammerCollection(Hammer hammer) {
        if (!hammer.isCollected() && isTouchingHammer(hammer)) {
            setHasHammer(true);
            setHasBlaster(false);  // to drop the blaster
            currentItem = HeldItem.HAMMER;
            hammer.collect();
            System.out.println("Hammer collected!");
            updateSprite(); // Update sprite immediately when Mario collects the hammer

        }
    }

    /** Handles collecting the hammer if Mario is in contact with it. */
    private void handleBlasterCollection(Blaster[] blasters) {
        for (Blaster blaster : blasters) {
            if (!blaster.isCollected() && isTouchingBlaster(blaster)) {
                blaster.collect();
                collectedBlasters.add(blaster);
                setHasHammer(false);  // to drop the hammer
                setHasBlaster(true);
                currentItem = HeldItem.BLASTER;
                System.out.println("Blaster collected! Total bullets: " + getTotalBullets());
                updateSprite(); // Update sprite immediately when Mario collects the Blaster
            }
        }
    }

    /** Handles jumping if Mario is on a platform and jump is requested. */
    private void handleJumping(boolean onPlatform, boolean wantsToJump) {
        if (onPlatform && wantsToJump) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
            System.out.println("Jumping!");
        }
        double bottomOfMario = y + (marioImage.getHeight() / 2);
        if (bottomOfMario > ShadowDonkeyKong.getScreenHeight()) {
            y = ShadowDonkeyKong.getScreenHeight() - (marioImage.getHeight() / 2);
            velocityY = 0;
            isJumping = false;
        }
    }

    /**
     * Enforces screen boundaries to prevent Mario from moving out of bounds.
     * Ensures Mario stays within the left, right, and bottom limits of the game window.
     */
    private void enforceBoundaries() {
        // Calculate half the width of the Mario image (used for centering and boundary checks)
        double halfW = marioImage.getWidth() / 2;

        // Prevent Mario from moving beyond the left edge of the screen
        if (x < halfW) {
            x = halfW;
        }

        // Prevent Mario from moving beyond the right edge of the screen
        double maxX = ShadowDonkeyKong.getScreenWidth() - halfW;
        if (x > maxX) {
            x = maxX;
        }

        // Calculate Mario's bottom edge position
        double bottomOfMario = y + (marioImage.getHeight() / 2);

        // Prevent Mario from falling below the bottom of the screen
        if (bottomOfMario > ShadowDonkeyKong.getScreenHeight()) {
            // Reposition Mario to stand on the bottom edge
            y = ShadowDonkeyKong.getScreenHeight() - (marioImage.getHeight() / 2);

            // Stop vertical movement and reset jumping state
            velocityY = 0;
            isJumping = false;
        }
    }


    /**
     * Switch Mario's sprite (left/right, or hammer/no-hammer).
     * Adjust Mario's 'y' so that the bottom edge stays consistent.
     */
    private void updateSprite() {
        // 1) Remember the old image and its bottom
        Image oldImage = marioImage;
        double oldHeight = oldImage.getHeight();
        double oldBottom = y + (oldHeight / 2);

        // 2) Assign the new image based on facing & hammer and blaster
        //    (Whatever logic you currently use in update())

        switch (currentItem) {
            case HAMMER:
                marioImage = isFacingRight ? MARIO_HAMMER_RIGHT_IMAGE : MARIO_HAMMER_LEFT_IMAGE;
                break;
            case BLASTER:
                marioImage = isFacingRight ? MARIO_BLASTER_RIGHT_IMAGE : MARIO_BLASTER_LEFT_IMAGE;
                break;
            default:
                marioImage = isFacingRight ? MARIO_RIGHT_IMAGE : MARIO_LEFT_IMAGE;
                break;
        }








        // 3) Now recalc Mario’s bottom with the new image
        double newHeight = marioImage.getHeight();
        double newBottom = y + (newHeight / 2);

        // 4) Shift 'y' so the bottom edge is the same as before
        //    (If new sprite is taller, we move Mario up so he doesn't sink into platforms)
        y -= (newBottom - oldBottom);

        // 5) Update the recorded width/height to match the new image
        width  = marioImage.getWidth();
        height = newHeight;
    }


    /**
     * Draws Mario on the screen.
     */
    public void draw() {
        marioImage.draw(x, y);
//    drawBoundingBox(); // Uncomment for debugging
    }


    /**
     * Checks if Mario is touching a ladder.
     *
     * @param ladder The ladder object to check collision with.
     * @return {@code true} if Mario is touching the ladder, {@code false} otherwise.
     */
    private boolean isTouchingLadder(Ladder ladder) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(ladder.getBoundingBox());
    }

    /**
     * Checks if Mario is touching the hammer.
     *
     * @param hammer The hammer object to check collision with.
     * @return {@code true} if Mario is touching the hammer, {@code false} otherwise.
     */
    private boolean isTouchingHammer(Hammer hammer) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(hammer.getBoundingBox());
    }

    /**
     * Checks if Mario is touching a barrel.
     *
     * @param barrel The barrel object to check collision with.
     * @return {@code true} if Mario is touching the barrel, {@code false} otherwise.
     */
    public boolean isTouchingBarrel(Barrel barrel) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(barrel.getBoundingBox());
    }

    /**
     * Checks if Mario is touching a Normal Monkey.
     *
     * @param normalMonkey The Normal Monkey object to check collision with.
     * @return {@code true} if Mario is touching a Normal Monkey, {@code false} otherwise.
     */
    public boolean isTouchingNormalMonkey(NormalMonkey normalMonkey) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(normalMonkey.getBoundingBox());
    }

    /**
     * Checks if Mario is touching an Intelligent Monkey.
     *
     * @param intelligentMonkey The Intelligent Monkey object to check collision with.
     * @return {@code true} if Mario is touching an Intelligent Monkey, {@code false} otherwise.
     */
    public boolean isTouchingIntelligentMonkey(IntelligentMonkey intelligentMonkey) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(intelligentMonkey.getBoundingBox());
    }

    /**
     * Checks if Mario is touching a blaster.
     *
     * @param blaster The blaster object to check collision with.
     * @return {@code true} if Mario is touching the blaster, {@code false} otherwise.
     */
    public boolean isTouchingBlaster(Blaster blaster) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(blaster.getBoundingBox());
    }



    /**
     * Checks if Mario has reached Donkey Kong.
     *
     * @param donkey The Donkey object to check collision with.
     * @return {@code true} if Mario has reached Donkey Kong, {@code false} otherwise.
     */
    public boolean hasReached(Donkey donkey) {
        Rectangle marioBounds = getBoundingBox();
        return marioBounds.intersects(donkey.getBoundingBox());
    }

    /**
     * Determines if Mario successfully jumps over a barrel.
     *
     * @param barrel The barrel object to check.
     * @return {@code true} if Mario successfully jumps over the barrel, {@code false} otherwise.
     */
    public boolean jumpOver(Barrel barrel) {
        return isJumping
                && Math.abs(this.x - barrel.getX()) <= 1
                && (this.y < barrel.getY())
                && ((this.y + height / 2) >= (barrel.getY() + barrel.getBarrelImage().getHeight() / 2
                - (JUMP_STRENGTH * JUMP_STRENGTH) / (2 * Physics.MARIO_GRAVITY) - height / 2));
    }

}
