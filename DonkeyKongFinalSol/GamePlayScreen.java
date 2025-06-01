/*


import bagel.*;
import bagel.Input;
import java.util.Properties;

*/
/**
 * Represents the main gameplay screen where the player controls Mario.
 * This class manages game objects, updates their states, and handles game logic.
 *//*

public class GamePlayScreen {
    private final Properties GAME_PROPS;

    // Game objects
    private Mario mario;
    private Barrel[] barrels;   // Array of barrels in the game
    private Ladder[] ladders;   // Array of ladders in the game
    private Hammer hammer;      // The hammer object that Mario can collect
    private Donkey donkey;      // Donkey Kong, the objective of the game
    private Image background;   // Background image for the game
    private Platform[] platforms; // Array of platforms in the game

    // Frame tracking
    private int currFrame = 0;  // Tracks the number of frames elapsed

    // Game parameters
    private final int MAX_FRAMES;  // Maximum number of frames before game ends

    // Display text variables
    private final Font STATUS_FONT;
    private final int SCORE_X;
    private final int SCORE_Y;
    private static final String SCORE_MESSAGE = "SCORE ";
    private static final String TIME_MESSAGE = "Time Left ";

    private static final int BARREL_SCORE = 100;
    private static final int TIME_DISPLAY_DIFF_Y = 30;
    private static final int BARREL_CROSS_SCORE = 30;
    private int score = 0;  // Player's score for jumping over barrels
    private boolean isGameOver = false; // Game over flag

    */
/**
     * Returns the player's current score.
     *
     * @return The player's score.
     *//*

    public int getScore() {
        return score;
    }

    */
/**
     * Calculates the remaining time left in seconds.
     *
     * @return The number of seconds remaining before the game ends.
     *//*

    public int getSecondsLeft() {
        return (MAX_FRAMES - currFrame) / 60;
    }

    */
/**
     * Constructs the gameplay screen, loading resources and initializing game objects.
     *
     * @param gameProps  Properties file containing game settings.
     *//*

    public GamePlayScreen(Properties gameProps) {
        this.GAME_PROPS = gameProps;

        // Load game parameters
        this.MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        this.STATUS_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"))
        );
        this.SCORE_X = Integer.parseInt(gameProps.getProperty("gamePlay.score.x"));
        this.SCORE_Y = Integer.parseInt(gameProps.getProperty("gamePlay.score.y"));
        this.background = new Image("res/background.png");

        // Initialize game objects
        initializeGameObjects();
    }

    */
/**
     * Initializes game objects such as Mario, Donkey Kong, barrels, ladders, platforms, and the hammer.
     *//*

    private void initializeGameObjects() {
        // 1) Create Mario
        String[] marioCoords = GAME_PROPS.getProperty("mario.level1").split(",");
        double marioX = Double.parseDouble(marioCoords[0].trim());
        double marioY = Double.parseDouble(marioCoords[1].trim());

        this.mario = new Mario(marioX, marioY);

        // 2) Create Donkey Kong
        String[] donkeyCoords = GAME_PROPS.getProperty("donkey.level1").split(",");
        double donkeyX = Double.parseDouble(donkeyCoords[0].trim());
        double donkeyY = Double.parseDouble(donkeyCoords[1].trim());
        this.donkey = new Donkey(donkeyX, donkeyY);

        // 3) Create the Barrels array
        int barrelCount = Integer.parseInt(GAME_PROPS.getProperty("barrel.level1.count"));
        this.barrels = new Barrel[barrelCount];
        for (int i = 1; i <= barrelCount; i++) {
            String barrelData = GAME_PROPS.getProperty("barrel.level1." + i);
            String[] coords = barrelData.split(",");
            double barrelX = Double.parseDouble(coords[0].trim());
            double barrelY = Double.parseDouble(coords[1].trim());
            barrels[i - 1] = new Barrel(barrelX, barrelY);
        }

        // 4) Create the Ladders array
        int ladderCount = Integer.parseInt(GAME_PROPS.getProperty("ladder.level1.count"));
        this.ladders = new Ladder[ladderCount];
        for (int i = 1; i <= ladderCount; i++) {
            String ladderData = GAME_PROPS.getProperty("ladder.level1." + i);
            String[] coords = ladderData.split(",");
            double ladderX = Double.parseDouble(coords[0].trim());
            double ladderY = Double.parseDouble(coords[1].trim());
            ladders[i - 1] = new Ladder(ladderX, ladderY);
        }


        // 5) Create the Platforms array
        String platformData = GAME_PROPS.getProperty("platforms.level1");
        if (platformData != null && !platformData.isEmpty()) {
            String[] platformEntries = platformData.split(";");
            this.platforms = new Platform[platformEntries.length];
            for (int i = 0; i < platformEntries.length; i++) {
                String[] coords = platformEntries[i].trim().split(",");
                double x = Double.parseDouble(coords[0].trim());
                double y = Double.parseDouble(coords[1].trim());
                platforms[i] = new Platform(x, y);
            }
        } else {
            this.platforms = new Platform[0];
        }

        // 6) Create Hammer (using hammer.level1.1)
        String hammerData = GAME_PROPS.getProperty("hammer.level1.1");
        String[] hammerCoords = hammerData.split(",");
        double hammerX = Double.parseDouble(hammerCoords[0].trim());
        double hammerY = Double.parseDouble(hammerCoords[1].trim());
        this.hammer = new Hammer(hammerX, hammerY);
    }

    */
/**
     * Updates game state each frame.
     *
     * @param input The current player input.
     * @return {@code true} if the game ends, {@code false} otherwise.
     *//*

    public boolean update(Input input) {
        currFrame++;

        // Draw background
        background.drawFromTopLeft(0, 0);

        // 1) Draw and update platforms
        for (Platform platform : platforms) {
            if (platform != null) {
                platform.draw();
            }
        }

        // 2) Update ladders
        for (Ladder ladder : ladders) {
            if (ladder != null) {
                ladder.update(platforms);
            }
        }

        // 3) Update barrels
        for (Barrel barrel : barrels) {
            if (barrel == null) continue;
            if (mario.jumpOver(barrel)) {
                score += BARREL_CROSS_SCORE;
            }
            if (!barrel.isDestroyed() && mario.isTouchingBarrel(barrel)) {
                if (!mario.holdHammer()) {
                    isGameOver = true;
                } else {
                    barrel.destroy();
                    score += BARREL_SCORE;
                }
            }
            barrel.update(platforms);
        }

        // 4) Check game time and donkey status
        if (checkingGameTime()) {
            isGameOver = true;
        }
        donkey.update(platforms);

        // 5) Draw hammer and donkey
        hammer.draw();
        donkey.draw();

        // 6) Update Mario
        mario.update(input, ladders, platforms, hammer);

        // 7) Check if Mario reaches Donkey
        if (mario.hasReached(donkey) && !mario.holdHammer()) {
            isGameOver = true;
        }

        // 8) Display score and time left
        displayInfo();

        // 9) Return game state
        return isGameOver || isLevelCompleted();
    }

    */
/**
     * Displays the player's score & time left on the screen.
     *//*

    public void displayInfo() {
        STATUS_FONT.drawString(SCORE_MESSAGE + score, SCORE_X, SCORE_Y);

        // Time left in seconds
        int secondsLeft = (MAX_FRAMES - currFrame) / 60;
        int TIME_X = SCORE_X;
        int TIME_Y = SCORE_Y + TIME_DISPLAY_DIFF_Y;
        STATUS_FONT.drawString(TIME_MESSAGE + secondsLeft, TIME_X, TIME_Y);
    }

    */
/**
     * Checks whether the level is completed by determining if Mario has reached Donkey Kong
     * while holding a hammer. This serves as the game's winning condition.
     *
     * @return {@code true} if Mario reaches Donkey Kong while holding a hammer,
     *         indicating the level is completed; {@code false} otherwise.
     *//*

    public boolean isLevelCompleted() {
        return mario.hasReached(donkey) && mario.holdHammer();
    }

    */
/**
     * Checks if the game has reached its time limit by comparing the current frame count
     * against the maximum allowed frames. If the limit is reached, the game may trigger
     * a timeout condition.
     *
     * @return {@code true} if the current frame count has reached or exceeded
     *         the maximum allowed frames, indicating the time limit has been reached;
     *         {@code false} otherwise.
     *//*

    public boolean checkingGameTime() {
        return currFrame >= MAX_FRAMES;
    }
}


*/
