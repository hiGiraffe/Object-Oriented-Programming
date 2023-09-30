package data;

import com.oocourse.elevator3.TimableOutput;
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
            if (person.getDownFloor() == currentFloor) {
                return true;
            }
        }
        return false;
    }

    /* 在这个方向上没有人要出电梯了 */
    public boolean hasPerson(int currentFloor, int direction) {
        for (Person person : requests) {
            if (direction == 1
                    && (person.getDownFloor() > currentFloor)) {
                return true;
            } else if (direction == -1
                    && (person.getDownFloor() < currentFloor)) {
                return true;
            }
        }
        return false;
    }

    public Person getOnePersonOut(Elevator elevator) {
        if (requests.isEmpty()) {
            return null;
        }
        Iterator<Person> it = requests.iterator();
        while (it.hasNext()) {
            Person person = it.next();
            if (person.getDownFloor() == elevator.getCurrentFloor()) {
                it.remove();
                if (person.getToFloor() != elevator.getCurrentFloor()) {
                    Person personBack = new Person(elevator.getCurrentFloor()
                            , person.getToFloor(), person.getPersonId());
                    elevator.getOneRequestToRequestQueue(personBack);
                }
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
        //开门
        TimableOutput.println("OPEN-" + elevator.getCurrentFloor() +
                "-" + elevator.getElevatorId());
        try {
            Thread.sleep(elevator.getOpenDoorTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (it.hasNext()) {
            Person person = it.next();
            TimableOutput.println("OUT-" + person.getPersonId()
                    + "-" + elevator.getCurrentFloor() + "-" + elevator.getElevatorId());
            if (person.getToFloor() != elevator.getCurrentFloor()) {
                Person personBack = new Person(elevator.getCurrentFloor()
                        , person.getToFloor(), person.getPersonId());
                elevator.getOneRequestToRequestQueue(personBack);
            }
            it.remove();
        }
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
