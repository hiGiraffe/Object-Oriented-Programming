import com.oocourse.elevator3.TimableOutput;
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
                = new ElevatorManagement(elevatorQueue, elevators, requestPool);
        elevatorManagement.start();

        //电梯维修
        ElevactorMaintainer elevactorMaintainer
                = new ElevactorMaintainer(maintainQueue, elevators);
        elevactorMaintainer.start();
    }
}
/*
to do list
1、服务电梯最多4，接人电梯最多2
2、电梯定制化功能
3、电梯可达性 √
4、电梯停靠数量控制

requestPool里面安排十二层楼的数组，判断同一层楼是否有人
电梯加可达楼层
requestPool拿之前先判断可达，以转场比记录
*/
