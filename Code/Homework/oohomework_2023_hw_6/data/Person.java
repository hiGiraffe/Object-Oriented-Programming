package data;

import com.oocourse.elevator2.PersonRequest;

public class Person extends PersonRequest {

    private boolean isTargeted;

    public Person(int fromFloor, int toFloor, int personId) {
        super(fromFloor, toFloor, personId);
        isTargeted = false;
    }

    public synchronized  boolean isTargeted() {
        return isTargeted;
    }

    public synchronized  void setTargeted(boolean targeted) {
        isTargeted = targeted;
    }

    public synchronized  boolean isDirectionSameToElevator(int direction) {
        if (direction == 1 && this.getToFloor() > this.getFromFloor()) {
            return true;
        }
        if (direction == -1 && this.getToFloor() < this.getFromFloor()) {
            return true;
        }
        return false;
    }

    public synchronized boolean hasPerson(int currentFloor, int direction) {

        if (direction == 1
                && (this.getFromFloor() > currentFloor)) {
            return true;
        } else if (direction == -1
                && (this.getFromFloor() < currentFloor)) {
            return true;
        }

        return false;
    }
}
