import bagel.*;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents Level 2 of the Shadow Donkey Kong game.
 * This class extends the abstract {@code Level} class and is responsible for:
 * - Loading game entities (Mario, barrels, ladders, hammer, donkey, platforms)
 * - Managing gameplay logic for Level 2
 * - Handling rendering, input, scoring, and win/loss conditions
 */
public class Level2 extends Level {

    private Monkey[] monkeys;
    private Blaster[] blasters;

    private ArrayList<Bullet> bullets = new ArrayList<>(); // List to track bullets
    private ArrayList<Banana> bananas = new ArrayList<>();  // List to track bananas

    private boolean isLevelCompleted = false; // Flag to track level completion



    /**
     * Constructs Level2 by initializing all game objects and loading configuration from properties.
     *
     * @param gameProps Properties file containing settings and object positions for level 1
     */
    public Level2(Properties gameProps) {
        super(gameProps);
        initializeGameObjects();
    }

    /**
     * Initializes game objects specific to Level 2, including:
     * - Mario and Donkey Kong positions
     * - Barrels, ladders, platforms, and hammer
     * - Normal and Intelligent Monkeys
     * All object positions are read from the {@code GAME_PROPS} configuration.
     */
    private void initializeGameObjects() {
        /** Initialize Mario using coordinates from properties */
        String[] marioCoords = GAME_PROPS.getProperty("mario.level2").split(",");
        mario = new Mario(Double.parseDouble(marioCoords[0].trim()), Double.parseDouble(marioCoords[1].trim()));

        /** Initialize Donkey Kong using coordinates from properties */
        String[] donkeyCoords = GAME_PROPS.getProperty("donkey.level2").split(",");
        donkey = new Donkey(Double.parseDouble(donkeyCoords[0].trim()), Double.parseDouble(donkeyCoords[1].trim()));

        /** Initialize barrels using count and positions from properties */
        int barrelCount = Integer.parseInt(GAME_PROPS.getProperty("barrel.level2.count"));
        barrels = new Barrel[barrelCount];
        for (int i = 0; i < barrelCount; i++) {
            String[] coords = GAME_PROPS.getProperty("barrel.level2." + (i + 1)).split(",");
            barrels[i] = new Barrel(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()));
        }

        /** Initialize ladders using count and positions from properties */
        int ladderCount = Integer.parseInt(GAME_PROPS.getProperty("ladder.level2.count"));
        ladders = new Ladder[ladderCount];
        for (int i = 0; i < ladderCount; i++) {
            String[] coords = GAME_PROPS.getProperty("ladder.level2." + (i + 1)).split(",");
            ladders[i] = new Ladder(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()));
        }

        /** Initialize platforms from semicolon-separated property string */
        String platformData = GAME_PROPS.getProperty("platforms.level2");
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

        /** Initialize hammer using coordinates from properties */
        String[] hammerCoords = GAME_PROPS.getProperty("hammer.level2.1").split(",");
        hammer = new Hammer(Double.parseDouble(hammerCoords[0].trim()), Double.parseDouble(hammerCoords[1].trim()));

        /** Initialize normal and intelligent monkeys using counts and property data */
        int monkeyCount = Integer.parseInt(GAME_PROPS.getProperty("normalMonkey.level2.count"));
        int intelligentMonkeyCount = Integer.parseInt(GAME_PROPS.getProperty("intelligentMonkey.level2.count"));
        int total = monkeyCount + intelligentMonkeyCount;

        monkeys = new Monkey[total];
        int index = 0;

        /** Load normal monkeys with position, direction, and route */
        for (int i = 0; i < monkeyCount; i++) {
            String monkeyData = GAME_PROPS.getProperty("normalMonkey.level2." + (i + 1));
            String[] parts = monkeyData.split(";");
            String[] coords = parts[0].split(",");
            String direction = parts[1].trim();
            String[] routeStr = parts[2].split(",");
            int[] route = new int[routeStr.length];
            for (int j = 0; j < routeStr.length; j++) {
                route[j] = Integer.parseInt(routeStr[j].trim());
            }
            monkeys[i] = new NormalMonkey(
                    Double.parseDouble(coords[0].trim()),
                    Double.parseDouble(coords[1].trim()),
                    direction,
                    route
            );
        }

        /** Load intelligent monkeys similarly to normal monkeys */
        for (int i = 0; i < intelligentMonkeyCount; i++) {
            String monkeyData = GAME_PROPS.getProperty("intelligentMonkey.level2." + (i + 1));
            String[] parts = monkeyData.split(";");
            String[] coords = parts[0].split(",");
            String direction = parts[1].trim();
            String[] routeStr = parts[2].split(",");
            int[] route = new int[routeStr.length];


            for (int j = 0; j < routeStr.length; j++) {
                route[j] = Integer.parseInt(routeStr[j].trim());
            }
            monkeys[monkeyCount + i] = new IntelligentMonkey(
                    Double.parseDouble(coords[0].trim()),
                    Double.parseDouble(coords[1].trim()),
                    direction,
                    route,
                    bananas  // Pass the bananas list
            );

        }

        /** Initialize blasters using positions from properties */
        int blasterCount = Integer.parseInt(GAME_PROPS.getProperty("blaster.level2.count"));
        blasters = new Blaster[blasterCount];
        for (int i = 0; i < blasterCount; i++) {
            String[] coords = GAME_PROPS.getProperty("blaster.level2." + (i + 1)).split(",");
            blasters[i] = new Blaster(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()));
        }
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

        /** Draw background image */
        background.drawFromTopLeft(0, 0);

        /** Update and draw platforms */
        for (Platform platform : platforms) {
            if (platform != null) platform.draw();
        }

        /** Update ladder states based on platform data */
        for (Ladder ladder : ladders) {
            if (ladder != null) ladder.update(platforms);
        }

        /** Update barrels and check for scoring or collision */
        for (Barrel barrel : barrels) {
            if (barrel == null) continue;

            /** Score for jumping over barrel */
            if (mario.jumpOver(barrel)) {
                barrelsJumped++; 
                score += BARREL_CROSS_SCORE;
            }

            /** Handle collision with barrel */
            if (!barrel.isDestroyed() && mario.isTouchingBarrel(barrel)) {
                if (mario.holdHammer()) {  // Only destroy barrel if holding hammer
                    barrel.destroy();
                    barrelsDestroyed++;
                    score += BARREL_SCORE; // 100 points for destroying a barrel
                } else {
                    isGameOver = true;
                }
            }

            /** Update barrel movement and behavior */
            barrel.update(platforms);
        }

        /** Check if time has expired */
        if (checkingGameTime()) {
            isGameOver = true;
        }

        /** Update Donkey Kong's state */
        donkey.update(platforms);

        /** Update and interact with Monkeys */
        for (Monkey monkey : monkeys) {
            if (monkey == null || monkey.isDestroyed()) continue;

            if ((monkey instanceof NormalMonkey && mario.isTouchingNormalMonkey((NormalMonkey)monkey)) ||
                    (monkey instanceof IntelligentMonkey && mario.isTouchingIntelligentMonkey((IntelligentMonkey)monkey))) {
                if (mario.holdHammer()) {
                    monkey.destroy();
                    monkeysDestroyed++;
                    score += MONKEY_SCORE; // 100 points for destroying a monkey
                } else {
                    isGameOver = true;
                }
            }

            monkey.update(platforms);
        }


        // Update and draw all bananas
        for (int i = 0; i < bananas.size(); i++) {
            Banana banana = bananas.get(i);
            banana.update();

            // Collision with Mario
            if (banana.getBoundingBox().intersects(mario.getBoundingBox())) {
                isGameOver = true;
            }
            if (banana.isExpired()) {
                bananas.remove(i);
                i--; // Adjust index after removal
            }
        }

        /** Draw hammer and Donkey Kong */
        hammer.draw();
        donkey.draw();

        /** Update Mario's movement and actions */
        mario.update(input, ladders, platforms, hammer, blasters);

        /** Draw blasters if they haven't been collected */
        for (Blaster blaster : blasters) {
            if (!blaster.isCollected()) {
                blaster.draw();
            }
        }

        /** Check losing condition: Mario reaches Donkey without hammer */
        if (mario.hasReached(donkey) && !mario.holdHammer()) {
            isGameOver = true;
        }

        /** Display score and time */
        displayInfo();

        /** Display level-specific info like Donkey health and bullets */
        displayLevel2Info();

        /** If input is detected, fire the bullet */
        if (input.wasPressed(Keys.S)) {
            Bullet bullet = mario.fireBullet();
            if (bullet != null) {
                bullets.add(bullet);
            }
        }
        // Update and draw all bullets
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();


            // Check for bullet collisions with monkeys
            for (int j = 0; j < monkeys.length; j++) {
                if (monkeys[j] != null && !monkeys[j].isDestroyed() && 
                    bullet.getBoundingBox().intersects(monkeys[j].getBoundingBox())) {
                    monkeys[j].destroy();
                    bullets.remove(i);
                    i--; // Adjust index after removal
                    score += MONKEY_SCORE;  // 100 points for hitting a monkey
                    break; // Exit monkey loop after hit
                }
            }

            if (bullet.getBoundingBox().intersects(donkey.getBoundingBox())) {
                donkey.decreaseHealth();
                bullets.remove(i);
                i--; // Adjust index after removal
                
                // Check if Donkey is defeated
                if (donkey.isDefeated()) {
                    isLevelCompleted= true;
                }
                break; // Exit collision checks
            }




            // Remove bullet if it goes off-screen
            if (bullet.getX() < 0 || bullet.getX() > ShadowDonkeyKong.getScreenWidth()) {
                bullets.remove(i);
                i--; // Adjust index after removal
            }
        }






        /** End level if game is over or completed */
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
        return mario.hasReached(donkey) && mario.holdHammer()|| donkey.isDefeated();
    }
}
