package data;

import com.oocourse.elevator2.TimableOutput;
import thread.Elevator;

import java.util.ArrayList;
import java.util.Iterator;

public class People {
    private final ArrayList<Person> requests;
    private int nextOutDirection;

    public People() {
        this.requests = new ArrayList<>();
        nextOutDirection = 1;
    }

    public boolean isEmpty() {
        return requests.isEmpty();
    }

    public boolean isFloorOut(int currentFloor) {
        for (Person person : requests) {
            if (person.getToFloor() == currentFloor) {
                return true;
            }
        }
        return false;
    }

    /* 在这个方向上没有人要出电梯了 */
    public boolean hasPerson(int currentFloor, int direction) {
        for (Person person  : requests) {
            if (direction == 1
                    && (person.getToFloor() > currentFloor)) {
                return true;
            } else if (direction == -1
                    && (person.getToFloor() < currentFloor)) {
                return true;
            }
        }
        return false;
    }

    public int getNextOutDirection() {
        return nextOutDirection;
    }

    public Person getOnePersonOut(Elevator elevator) {
        if (requests.isEmpty()) {
            return null;
        }
        Iterator<Person> it = requests.iterator();
        while (it.hasNext()) {
            Person person = it.next();
            if (person.getToFloor() == elevator.getCurrentFloor()) {
                it.remove();
                return person;
            }
        }
        return null;
    }

    public int getNum() {
        return requests.size();
    }

    public void getOnePersonIn(Person person, Elevator elevator) {
        this.requests.add(person);
        TimableOutput.println("IN-" + person.getPersonId()
                + "-" + elevator.getCurrentFloor() + "-" + elevator.getElevatorId());
    }

    public void outAll(Elevator elevator) {
        if (this.isEmpty()) {
            return;
        }
        Iterator<Person> it = requests.iterator();
        while (it.hasNext()) {
            //开门
            TimableOutput.println("OPEN-" + elevator.getCurrentFloor() +
                    "-" + elevator.getElevatorId());
            try {
                Thread.sleep(elevator.getOpenDoorTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Person person = it.next();
            it.remove();
            Person personRequestTrue = new Person(elevator.getCurrentFloor()
                    , person.getToFloor(), person.getPersonId());
            TimableOutput.println("OUT-" + person.getPersonId()
                    + "-" + elevator.getCurrentFloor() + "-" + elevator.getElevatorId());
            elevator.getOneRequestToRequestQueue(personRequestTrue);
            //关门
            try {
                Thread.sleep(elevator.getCloseDoorTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TimableOutput.println("CLOSE-" + elevator.getCurrentFloor() +
                    "-" + elevator.getElevatorId());
        }
    }
}
