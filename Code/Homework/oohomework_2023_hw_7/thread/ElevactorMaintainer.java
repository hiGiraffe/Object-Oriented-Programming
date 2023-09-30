package thread;

import com.oocourse.elevator3.MaintainRequest;
import data.MaintainQueue;

import java.util.concurrent.CopyOnWriteArrayList;

public class ElevactorMaintainer extends Thread {
    private volatile MaintainQueue maintainQueue;
    private volatile CopyOnWriteArrayList<Elevator> elevators;

    public ElevactorMaintainer(MaintainQueue maintainQueue,
                               CopyOnWriteArrayList<Elevator> elevators) {
        this.maintainQueue = maintainQueue;
        this.elevators = elevators;
    }

    @Override
    public void run() {
        while (true) {
            if (maintainQueue.isEnd()) {
                return;
            }
            MaintainRequest maintainRequest = this.maintainQueue.getOneMaintainOut();
            if (maintainRequest == null) {
                break;
            }
            //TimableOutput.println("get one maintain");
            for (Elevator elevator : elevators) {
                if (elevator.getElevatorId() == maintainRequest.getElevatorId()) {
                    elevator.setMaintain(true);
                    break;
                }
            }
        }

    }
}
