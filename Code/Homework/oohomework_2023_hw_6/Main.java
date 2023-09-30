import com.oocourse.elevator2.TimableOutput;
import data.ElevatorQueue;
import data.MaintainQueue;
import thread.Elevator;
import thread.ElevatorManagement;
import thread.InputHandler;
import thread.ElevactorMaintainer;
import data.RequestPool;

import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        ElevatorQueue elevatorQueue = new ElevatorQueue();
        MaintainQueue maintainQueue = new MaintainQueue();
        CopyOnWriteArrayList<Elevator> elevators = new CopyOnWriteArrayList<>();
        RequestPool requestPool = new RequestPool(elevators);

        //处理输入
        InputHandler inputHandler
                = new InputHandler(requestPool, elevatorQueue, maintainQueue);
        inputHandler.start();



        //电梯增加
        ElevatorManagement elevatorManagement
                = new ElevatorManagement(elevatorQueue, elevators,requestPool);
        elevatorManagement.start();

        //电梯维修
        ElevactorMaintainer elevactorMaintainer
                = new ElevactorMaintainer(maintainQueue, elevators);
        elevactorMaintainer.start();



    }
}
