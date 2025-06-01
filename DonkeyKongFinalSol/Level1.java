import bagel.*;
import java.util.Properties;

/**
 * Represents Level 1 of the Shadow Donkey Kong game.
 * This class extends the abstract {@code Level} class and is responsible for:
 * - Loading game entities (Mario, barrels, ladders, hammer, donkey, platforms)
 * - Managing gameplay logic for Level 1
 * - Handling rendering, input, scoring, and win/loss conditions
 */
public class Level1 extends Level {

    /**
     * Constructs Level1 by initializing all game objects and loading configuration from properties.
     *
     * @param gameProps Properties file containing settings and object positions for level 1
     */
    public Level1(Properties gameProps) {
        super(gameProps);
        initializeGameObjects();
    }

    /**
     * Initializes game objects specific to Level 1, including:
     * - Mario and Donkey Kong positions
     * - Barrels, ladders, platforms, and hammer
     * All object positions are read from the {@code GAME_PROPS} configuration.
     */
    private void initializeGameObjects() {
        // 1) Initialize Mario
        String[] marioCoords = GAME_PROPS.getProperty("mario.level1").split(",");
        mario = new Mario(Double.parseDouble(marioCoords[0].trim()), Double.parseDouble(marioCoords[1].trim()));

        // 2) Initialize Donkey Kong
        String[] donkeyCoords = GAME_PROPS.getProperty("donkey.level1").split(",");
        donkey = new Donkey(Double.parseDouble(donkeyCoords[0].trim()), Double.parseDouble(donkeyCoords[1].trim()));

        // 3) Initialize Barrels
        int barrelCount = Integer.parseInt(GAME_PROPS.getProperty("barrel.level1.count"));
        barrels = new Barrel[barrelCount];
        for (int i = 0; i < barrelCount; i++) {
            String[] coords = GAME_PROPS.getProperty("barrel.level1." + (i + 1)).split(",");
            barrels[i] = new Barrel(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()));
        }

        // 4) Initialize Ladders
        int ladderCount = Integer.parseInt(GAME_PROPS.getProperty("ladder.level1.count"));
        ladders = new Ladder[ladderCount];
        for (int i = 0; i < ladderCount; i++) {
            String[] coords = GAME_PROPS.getProperty("ladder.level1." + (i + 1)).split(",");
            ladders[i] = new Ladder(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()));
        }

        // 5) Initialize Platforms
        String platformData = GAME_PROPS.getProperty("platforms.level1");
        if (platformData != null && !platformData.isEmpty()) {
            String[] entries = platformData.split(";");
            platforms = new Platform[entries.length];
            for (int i = 0; i < entries.length; i++) {
                String[] coords = entries[i].split(",");
                platforms[i] = new Platform(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()));
            }
        } else {
            platforms = new Platform[0];  // No platforms if property is missing
        }

        // 6) Initialize Hammer
        String[] hammerCoords = GAME_PROPS.getProperty("hammer.level1.1").split(",");
        hammer = new Hammer(Double.parseDouble(hammerCoords[0].trim()), Double.parseDouble(hammerCoords[1].trim()));
    }

    /**
     * Updates the game state every frame. Handles:
     * - Rendering all entities and background
     * - Updating positions and interactions
     * - Collision detection and scoring
     * - Victory/loss conditions
     *
     * @param input The current keyboard/mouse input
     * @return {@code true} if the level is over (either win or game over), {@code false} otherwise
     */
    @Override
    public boolean update(Input input) {
        currFrame++;

        // Draw background
        background.drawFromTopLeft(0, 0);

        // 1) Update and draw platforms
        for (Platform platform : platforms) {
            if (platform != null) platform.draw();
        }

        // 2) Update ladders
        for (Ladder ladder : ladders) {
            if (ladder != null) ladder.update(platforms);
        }

        // 3) Update barrels, handle collisions and scoring
        for (Barrel barrel : barrels) {
            if (barrel == null) continue;

            // Score if Mario jumps over a barrel
            if (mario.jumpOver(barrel)) {
                score += BARREL_CROSS_SCORE;
            }

            // Barrel collision
            if (!barrel.isDestroyed() && mario.isTouchingBarrel(barrel)) {
                if (!mario.holdHammer()) {
                    isGameOver = true;
                } else {
                    barrel.destroy();
                    score += BARREL_SCORE;
                }
            }

            // Update barrel physics
            barrel.update(platforms);
        }

        // 4) Check if time has run out
        if (checkingGameTime()) {
            isGameOver = true;
        }

        // 5) Update and draw Donkey and hammer
        donkey.update(platforms);
        hammer.draw();
        donkey.draw();

        // 6) Update Mario
        mario.update(input, ladders, platforms, hammer);

        // 7) Check for losing condition: Mario reaches Donkey without hammer
        if (mario.hasReached(donkey) && !mario.holdHammer()) {
            isGameOver = true;
        }

        // 8) Draw score and time remaining
        displayInfo();

        // 9) End level if game is over or won
        return isGameOver || isLevelCompleted();
    }

    /**
     * Checks if the level has been successfully completed.
     * Victory condition: Mario reaches Donkey while holding a hammer.
     *
     * @return {@code true} if the level is completed, {@code false} otherwise
     */
    @Override
    public boolean isLevelCompleted() {
        return mario.hasReached(donkey) && mario.holdHammer();
    }
}
