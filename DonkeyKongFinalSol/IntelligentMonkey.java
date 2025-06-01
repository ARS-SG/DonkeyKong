import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Colour;

import java.awt.event.InputEvent;
import java.util.ArrayList;

/**
 * Represents IntelligentMonkey in the game, affected by gravity and platform collisions.
 * The IntelligentMonkey object moves downward due to gravity and lands on platforms when applicable.
 */
public class IntelligentMonkey extends Monkey {

    private ArrayList<Banana> bananas;  // Reference to the bananas list from Level2


    private static final double BANANA_THROW_DELAY = 5000;  // 5 seconds delay between banana throws
    private double lastThrowTime;


    /**
     * Constructs a new IntelligentMonkey at the specified starting position.
     * @param x      Initial x-coordinate.
     * @param y      Initial y-coordinate.
     * @param direction Direction the monkey is facing ("left" or "right").
     * @param route  The route the monkey follows.
     */
    public IntelligentMonkey(double x, double y, String direction, int[] route, ArrayList<Banana> bananas) {
        super(x, y, Physics.INTELLIGENT_MONKEY_GRAVITY, Physics.INTELLIGENT_MONKEY_TERMINAL_VELOCITY,
                 new Image("res/intelli_monkey_" + direction + ".png"),
                direction, route);
        
        this.lastThrowTime = System.currentTimeMillis();  // Initialize last throw time
        this.bananas = bananas;  // Initialize the bananas list

    }

     @Override
    public void update(Platform[] platforms) {
        super.update(platforms);

        // Check if it's time to throw a banana
        double currentTime = System.currentTimeMillis();
        if (currentTime - lastThrowTime >= BANANA_THROW_DELAY) {
            throwBanana();
            lastThrowTime = currentTime;
        }

    }

        private void throwBanana() {
        // Create a new banana in the direction the monkey is facing
        Banana banana = new Banana(x, y, direction);
        bananas.add(banana);
    }





}
