import bagel.*;

import java.util.Properties;

/**
 * Abstract class representing a level in the Shadow Donkey Kong game.
 * Handles shared game logic and properties such as score, timing, and rendering status info.
 */
public abstract class Level {
    /** Game configuration properties loaded from file */
    protected final Properties GAME_PROPS;

    /** Main game entities */
    protected Mario mario;
    protected Barrel[] barrels;
    protected Ladder[] ladders;
    protected Hammer hammer;
    protected Donkey donkey;
    protected Image background;
    protected Platform[] platforms;

    /** Frame tracking for time management */
    protected int currFrame = 0;
    protected final int MAX_FRAMES;

    /** Font and positioning for UI elements */
    protected final Font STATUS_FONT;
    protected final int SCORE_X;
    protected final int SCORE_Y;

    /**COunters for each scoring component */
    protected int barrelsDestroyed = 0;
    protected int barrelsJumped = 0;
    protected int monkeysDestroyed = 0;


    /** Score tracking */
    protected int score = 0;

    /** Game state flag */
    protected boolean isGameOver = false;

    /** UI labels */
    protected static final String SCORE_MESSAGE = "SCORE ";
    protected static final String TIME_MESSAGE = "Time Left ";
    protected static final String DONKEY_HEALTH_MESSAGE = "DONKEY HEALTH ";
    protected static final String BULLET_MESSAGE = "BULLET ";

    /** Display layout constant */
    protected static final int TIME_DISPLAY_DIFF_Y = 30;

    /** Score constants */
    protected static final int BARREL_SCORE = 100;
    protected static final int BARREL_CROSS_SCORE = 30;
    protected static final int MONKEY_SCORE = 100;

    /**
     * Constructs a Level using configuration from a properties file.
     *
     * @param gameProps Properties containing level-specific settings.
     */
    public Level(Properties gameProps) {
        this.GAME_PROPS = gameProps;
        this.MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        this.STATUS_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"))
        );
        this.SCORE_X = Integer.parseInt(gameProps.getProperty("gamePlay.score.x"));
        this.SCORE_Y = Integer.parseInt(gameProps.getProperty("gamePlay.score.y"));
        this.background = new Image("res/background.png");
    }

    /**
     * Gets the current score for the level.
     *
     * @return Current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the number of seconds remaining based on frame count.
     *
     * @return Time left in seconds.
     */
    public int getSecondsLeft() {
        return (MAX_FRAMES - currFrame) / 60;
    }

    /**
     * Displays the current score and time remaining on screen.
     */
    public void displayInfo() {
        STATUS_FONT.drawString(SCORE_MESSAGE + score, SCORE_X, SCORE_Y);
        int secondsLeft = getSecondsLeft();
        int TIME_X = SCORE_X;
        int TIME_Y = SCORE_Y + TIME_DISPLAY_DIFF_Y;
        STATUS_FONT.drawString(TIME_MESSAGE + secondsLeft, TIME_X, TIME_Y);
    }

    /**
     * Displays Level 2-specific information: Donkey Health and Bullet count.
     * Should only be called in Level 2.
     */
    public void displayLevel2Info() {
        String[] coords = GAME_PROPS.getProperty("gamePlay.donkeyhealth.coords").split(",");
        int HEALTH_X = Integer.parseInt(coords[0].trim());
        int HEALTH_Y = Integer.parseInt(coords[1].trim());

        STATUS_FONT.drawString(DONKEY_HEALTH_MESSAGE + donkey.getHealth(), HEALTH_X, HEALTH_Y);

        int BULLET_X = HEALTH_X;
        int BULLET_Y = HEALTH_Y + TIME_DISPLAY_DIFF_Y;
        STATUS_FONT.drawString(BULLET_MESSAGE + mario.getTotalBullets(), BULLET_X, BULLET_Y);

    }

    /**
     * Checks if the time for the level has run out.
     *
     * @return True if time has expired, false otherwise.
     */
    public boolean checkingGameTime() {
        return currFrame >= MAX_FRAMES;
    }

    /**
     * Updates the state of the level each frame.
     *
     * @param input Current input from player.
     * @return True if the level is over (win or loss), false otherwise.
     */
    public abstract boolean update(Input input);

    /**
     * Checks if the level has been successfully completed.
     *
     * @return True if the level has been won, false otherwise.
     */
    public abstract boolean isLevelCompleted();

    /**
     * Sets the initial score for the level.
     * @param score The initial score to set.
     */
    public void setInitialScore(int score) {
        this.score = score;
    }

    /** Getters */
    public int getBarrelsDestroyed() { return barrelsDestroyed; }
    public int getBarrelsJumped() { return barrelsJumped; }
    public int getMonkeysDestroyed() { return monkeysDestroyed; }
}
