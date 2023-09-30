package data;

import com.oocourse.elevator3.MaintainRequest;

import java.util.ArrayList;
import java.util.Iterator;

public class MaintainQueue {
    private ArrayList<MaintainRequest> requests;
    private boolean isEnd;

    public MaintainQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public boolean isEnd() {
        return isEnd;
    }

    public synchronized void addRequest(MaintainRequest maintainRequest) {
        requests.add(maintainRequest);
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return requests.isEmpty();
    }

    public synchronized MaintainRequest getOneMaintainOut() {
        if (requests.isEmpty() && !this.isEnd) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Iterator<MaintainRequest> it = requests.iterator();
        while (it.hasNext()) {
            MaintainRequest request = it.next();
            it.remove();
            return request;
        }
        return null;
    }
}
