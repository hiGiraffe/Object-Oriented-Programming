package data;

import com.oocourse.elevator3.PersonRequest;

public class Person extends PersonRequest {

    private boolean isTargeted;
    private int downFloor;

    public Person(int fromFloor, int toFloor, int personId) {
        super(fromFloor, toFloor, personId);
        isTargeted = false;
        downFloor = 1;
    }

    public int getDownFloor() {
        return downFloor;
    }

    public void setDownFloor(int downFloor) {
        this.downFloor = downFloor;
    }

    public synchronized boolean isTargeted() {
        return isTargeted;
    }

    public synchronized void setTargeted(boolean targeted) {
        isTargeted = targeted;
    }

    public synchronized boolean isDirectionSameToElevator(int direction) {
        if (direction == 1 && this.getDownFloor() > this.getFromFloor()) {
            return true;
        }
        if (direction == -1 && this.getDownFloor() < this.getFromFloor()) {
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
