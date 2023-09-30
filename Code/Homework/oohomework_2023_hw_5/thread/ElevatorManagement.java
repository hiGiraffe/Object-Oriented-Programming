package thread;

import com.oocourse.elevator2.ElevatorRequest;
import data.ElevatorQueue;
import data.RequestPool;
import java.util.concurrent.CopyOnWriteArrayList;

import static data.Config.FLOOR_INITIAL;
import static data.Config.MOVE_TIME;
import static data.Config.OPEN_DOOR_TIME;
import static data.Config.CLOSE_DOOR_TIME;
import static data.Config.MAX_NUM_OF_TAKE;

public class ElevatorManagement extends Thread {

    private ElevatorQueue elevatorQueue;
    private CopyOnWriteArrayList<Elevator> elevators;
    private  RequestPool requestPool;

    public ElevatorManagement(ElevatorQueue elevatorQueue, CopyOnWriteArrayList<Elevator> elevators, RequestPool requestPool) {
        this.elevatorQueue = elevatorQueue;
        this.elevators = elevators;
        this.requestPool = requestPool;
        for (int i = 1; i <= 6; i++) {
            Elevator elevator = new Elevator(FLOOR_INITIAL,
                    i, MOVE_TIME, OPEN_DOOR_TIME, CLOSE_DOOR_TIME, MAX_NUM_OF_TAKE,this.requestPool);
            elevator.start();
            this.elevators.add(elevator);
        }
    }

    @Override
    public void run() {
        while (true) {
            ElevatorRequest elevatorRequest = this.elevatorQueue.getOneElevatorOut();
            if (elevatorRequest == null) {
                return;
            }
            Elevator elevator
                    = new Elevator(elevatorRequest.getFloor(), elevatorRequest.getElevatorId(),
                    (long) (elevatorRequest.getSpeed() * 1000), OPEN_DOOR_TIME, CLOSE_DOOR_TIME,
                    elevatorRequest.getCapacity(), this.requestPool);
            elevator.start();
            this.elevators.add(elevator);
        }
    }
}
