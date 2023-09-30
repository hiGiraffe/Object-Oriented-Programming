package data;

import com.oocourse.elevator2.ElevatorRequest;

import java.util.ArrayList;
import java.util.Iterator;

public class ElevatorQueue {
    private ArrayList<ElevatorRequest> requests;
    private boolean isEnd;

    public ElevatorQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public boolean isEmpty() {
        return requests.isEmpty();
    }

    public synchronized void addRequest(ElevatorRequest elevatorRequest) {
        requests.add(elevatorRequest);
        notifyAll();
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized ElevatorRequest getOneElevatorOut() {
        if (requests.isEmpty() && !this.isEnd()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Iterator<ElevatorRequest> it = requests.iterator();
        while (it.hasNext()) {
            ElevatorRequest request = it.next();
            it.remove();
            return request;
        }
        return null;
    }
}
