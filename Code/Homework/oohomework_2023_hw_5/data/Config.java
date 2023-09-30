package data;

public abstract class Config {
    //Elevator
    public static final int[] CAN_REACH_FLOOR = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    public static final int FLOOR_INITIAL = 1;
    public static final long MOVE_TIME = 400; //ms
    public static final long OPEN_DOOR_TIME = 200; //ms
    public static final long CLOSE_DOOR_TIME = 200; //ms
    public static final int MAX_NUM_OF_TAKE = 6;
}
