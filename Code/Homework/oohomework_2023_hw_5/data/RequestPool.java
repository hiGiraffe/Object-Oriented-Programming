package data;

import com.oocourse.elevator2.TimableOutput;
import thread.Elevator;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;

public class RequestPool {
    private final ArrayList<Person> people;
    private boolean isEnd;

    public RequestPool() {
        people = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void addRequest(Person person) {
        people.add(person);
        //TimableOutput.println("add request "+person.getPersonId());
        notifyAll();
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;

        //TimableOutput.println("end requestPool");
        notifyAll();
    }

    public synchronized boolean isPersonInPool(Person personCheck) {
        //这个电梯没有目标
        if (personCheck == null) {
            return false;
        }
        if (people.contains(personCheck)) {
            return true;
        }
        return false;
    }

    public synchronized Person getTargetPerson() {
        while (true) {
            //TimableOutput.println("get Target");
            if (this.isEnd&&people.isEmpty()) {
                return null;
            }
            if (people.isEmpty()) {
                try {
                    //TimableOutput.println("wait");
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Person person : people) {
                if (!person.isTargeted()) {
                    person.setTargeted(true);
                    return person;
                }
            }

//            try {
//                sleep(10);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            return null;
        }
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized void setTargetFalse(Person targetPerson) {
        for (Person person : people) {
            if (person.equals(targetPerson)) {
                person.setTargeted(false);
                notifyAll();
            }
        }
    }

    public synchronized boolean hasPerson(Elevator elevator) {
        if (people.isEmpty()) {
            return false;
        }
        for (Person person : people) {
            if ((elevator.getDirection() == 1
                    && (person.getToFloor() >= person.getFromFloor())
                    && (person.getFromFloor() == elevator.getCurrentFloor()))
                    ||
                    (elevator.getDirection() == -1 &&
                            (person.getToFloor() <= person.getFromFloor())
                            && (person.getFromFloor() == elevator.getCurrentFloor()))) {
                return true;
            }
        }
        return false;
    }

    public synchronized Person getOnePersonOut(Elevator elevator) {
        if (people.isEmpty()) {
            return null;
        }
        Iterator<Person> it = people.iterator();
        while (it.hasNext()) {
            Person person = it.next();
            //跟当前电梯同向且在同一个楼层的可以上
            if ((elevator.getDirection() == 1
                    && (person.getToFloor() >= person.getFromFloor())
                    && (person.getFromFloor() == elevator.getCurrentFloor())
                    && elevator.shouldOpen())
                    ||
                    (elevator.getDirection() == -1 &&
                            (person.getToFloor() <= person.getFromFloor())
                            && (person.getFromFloor() == elevator.getCurrentFloor())
                            && elevator.shouldOpen())) {
                it.remove();
                return person;
            }
        }
        return null;
    }

    public synchronized void getPeopleInTheElevator(Elevator elevator){
        while (!elevator.isFull()) {
            Person person = this.getOnePersonOut(elevator);
            if (person == null) {
                break;
            }
            elevator.getOnePersonIn(person);
        }
    }
}
