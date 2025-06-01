import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Colour;

import java.awt.event.InputEvent;

/**
 * Represents Normal Monkey in the game, affected by gravity and platform collisions.
 * The NormalMonkey object moves downward due to gravity and lands on platforms when applicable.
 */
public class NormalMonkey extends Monkey{

    /**
     * Constructs a new Normal Monkey at the specified starting position.
     *
     */
    public NormalMonkey(double x, double y, String direction, int[] route) {
        super(x, y, Physics.NORMAL_MONKEY_GRAVITY, Physics.NORMAL_MONKEY_TERMINAL_VELOCITY,
                new Image("res/normal_monkey_" + direction + ".png"), direction, route);
    }


    /**
     * Updates NormalMonkey's position by applying gravity and checking for platform collisions.
     * If NormalMonkey lands on a platform, the velocity is reset to zero.
     *
     * @param platforms An array of platforms NormalMonkey can land on.
     */

}
