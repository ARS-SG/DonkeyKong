/**
 * The Physics class contains constants and methods related to the physics mechanics of the game.
 * It defines values for gravity, terminal velocity, and other movement constraints
 * to ensure realistic and smooth character motion.
 */
public class Physics {

    /**
     * The gravitational acceleration applied to Mario.
     */
    public static final double MARIO_GRAVITY = 0.2;

    /**
     * The gravitational acceleration applied to Donkey Kong.
     */
    public static final double DONKEY_GRAVITY = 0.4;

    /**
     * The gravitational acceleration applied while climbing ladders.
     */
    public static final double LADDER_GRAVITY = 0.25;

    /**
     * The gravitational acceleration applied to barrels.
     */
    public static final double BARREL_GRAVITY = 0.4;

    /**
     * The gravitational acceleration applied to Normal Monkey.
     */
    public static final double NORMAL_MONKEY_GRAVITY = 0.4;

    /**
     * The gravitational acceleration applied to IntelligentMonkey.
     */
    public static final double INTELLIGENT_MONKEY_GRAVITY = 0.4;

    /**
     * The maximum falling speed (terminal velocity) that Mario can reach.
     */
    public static final double MARIO_TERMINAL_VELOCITY = 10.0;

    /**
     * The maximum falling speed (terminal velocity) for barrels.
     */
    public static final double BARREL_TERMINAL_VELOCITY = 5.0;

    /**
     * The maximum falling speed (terminal velocity) while climbing ladders.
     */
    public static final double LADDER_TERMINAL_VELOCITY = 5.0;

    /**
     * The maximum falling speed (terminal velocity) for Donkey Kong.
     */
    public static final double DONKEY_TERMINAL_VELOCITY = 5.0;

    /**
     * The maximum falling speed (terminal velocity) for Normal Monkey.
     */
    public static final double NORMAL_MONKEY_TERMINAL_VELOCITY = 5.0;

    /**
     * The maximum falling speed (terminal velocity) for Intelligent Monkey.
     */
    public static final double INTELLIGENT_MONKEY_TERMINAL_VELOCITY = 5.0;
}