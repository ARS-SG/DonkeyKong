import bagel.*;
import java.util.Properties;

/**
 * A class representing the home screen of the game.
 * This screen displays the game title, a prompt for the player, and a background image.
 */
public class HomeScreen {

    private final Image BACKGROUND_IMAGE;  // Background image for the home screen
    private final String TITLE;            // Title text displayed at the top
    private final String PROMPT;           // Instruction prompt (e.g., "PRESS ENTER TO START LEVEL 1 PRESS 2 TO START LEVEL 2")

    private final Font TITLE_FONT;         // Font used for the title
    private final Font PROMPT_FONT;        // Font used for the prompt

    private final int TITLE_Y;             // Vertical position of the title
    private final int PROMPT_Y;            // Vertical position of the prompt

    private int selectedLevel = 0;         // Variable to track the selected level

    /**
     * Constructs the HomeScreen, loading images, fonts, and text properties.
     *
     * @param gameProps Properties file containing image paths and font details.
     * @param msgProps  Properties file containing title and prompt text.
     */
    public HomeScreen(Properties gameProps, Properties msgProps) {
        // Load the background image from properties
        BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage"));

        // Load title and prompt text from properties
        TITLE = msgProps.getProperty("home.title");
        PROMPT = msgProps.getProperty("home.prompt");   // e.g., "PRESS ENTER TO START LEVEL 1 PRESS 2 TO START LEVEL 2"

        // Load title font and its position
        TITLE_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("home.title.fontSize"))
        );
        TITLE_Y = Integer.parseInt(gameProps.getProperty("home.title.y"));

        // Load prompt font and its position
        PROMPT_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("home.prompt.fontSize"))
        );
        PROMPT_Y = Integer.parseInt(gameProps.getProperty("home.prompt.y"));
    }

    /**
     * Displays the home screen with the title and background.
     * Waits for the player to press ENTER to proceed.
     *
     * @param input The current mouse/keyboard input.
     * @return {@code true} if ENTER key is pressed (to start level 1), 2 key is pressed (to start level 2)
     * {@code false} otherwise.
     */
    public boolean update(Input input) {
        // 1) Draw the background image at the top-left corner
        BACKGROUND_IMAGE.drawFromTopLeft(0, 0);

        // 2) Draw the game title, centered horizontally
        double titleX = Window.getWidth() / 2 - TITLE_FONT.getWidth(TITLE) / 2;
        TITLE_FONT.drawString(TITLE, titleX, TITLE_Y);

        // 3) Draw the prompt text (e.g., "PRESS ENTER TO START LEVEL 1"), centered horizontally
        double promptX = Window.getWidth() / 2 - PROMPT_FONT.getWidth(PROMPT) / 2;
        PROMPT_FONT.drawString(PROMPT, promptX, PROMPT_Y);

        // 4) If ENTER is pressed, transition from the home screen to level 1
        if (input.wasPressed(Keys.ENTER)) {
            selectedLevel=1;
            return true;
        }
        // 5) If 2 is pressed, transition from the home screen to level 2
        if(input.wasPressed(Keys.NUM_2)) {
            selectedLevel=2;
            return true;
        }

        // 6) Otherwise, remain on the home screen
        return false;
    }

    /**
     *
     *  Getter method to return the selected level
     */
    public int getSelectedLevel() {
        return selectedLevel;
    }

}
