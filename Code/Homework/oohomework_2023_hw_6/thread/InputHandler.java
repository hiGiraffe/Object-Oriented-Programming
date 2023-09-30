package thread;

import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import data.ElevatorQueue;
import data.MaintainQueue;
import data.Person;
import data.RequestPool;

import java.io.IOException;

public class InputHandler extends Thread {
    private final RequestPool requestPool;
    private final ElevatorQueue elevatorQueue;
    private final MaintainQueue maintainQueue;

    public InputHandler(RequestPool requestPool, ElevatorQueue elevatorQueue,
                        MaintainQueue maintainQueue) {
        this.requestPool = requestPool;
        this.elevatorQueue = elevatorQueue;
        this.maintainQueue = maintainQueue;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                requestPool.setEnd(true);
                elevatorQueue.setEnd(true);
                maintainQueue.setEnd(true);
                break;
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    Person person
                            = new Person(((PersonRequest) request).getFromFloor(),
                            ((PersonRequest) request).getToFloor(),
                            ((PersonRequest) request).getPersonId());
                    requestPool.addRequest(person);
                    //TimableOutput.println("add person" +person.getPersonId());
                } else if (request instanceof ElevatorRequest) {
                    ElevatorRequest elevatorRequest
                            = new ElevatorRequest(((ElevatorRequest) request).getElevatorId(),
                            ((ElevatorRequest) request).getFloor(),
                            ((ElevatorRequest) request).getCapacity(),
                            ((ElevatorRequest) request).getSpeed());
                    elevatorQueue.addRequest(elevatorRequest);
                    //TimableOutput.println("add elevator" +elevatorRequest.getElevatorId());
                } else if (request instanceof MaintainRequest) {
                    MaintainRequest maintainRequest
                            = new MaintainRequest(((MaintainRequest) request).getElevatorId());
                    maintainQueue.addRequest(maintainRequest);
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
