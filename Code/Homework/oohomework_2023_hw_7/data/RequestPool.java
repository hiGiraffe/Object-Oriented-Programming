package data;

import thread.Elevator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestPool {
    private final ArrayList<Person> people;
    private boolean isEnd;
    private CopyOnWriteArrayList<Elevator> elevators;
    private Integer[][] floyd;
    private int servingElevator;
    private int onlyPickingUpElevator;

    public RequestPool(CopyOnWriteArrayList<Elevator> elevators) {
        people = new ArrayList<>();
        this.isEnd = false;
        this.elevators = elevators;
        this.floyd = new Integer[11][11];
        servingElevator = 0;
        onlyPickingUpElevator = 0;
    }

    public synchronized void deleteElevator(Elevator elevator) {
        elevators.remove(elevator);
        //TimableOutput.println(" delete "+elevator.getElevatorId());
    }

    public synchronized void addRequest(Person person) {
        people.add(person);
        //TimableOutput.println("add request "+person.getPersonId());
        notifyAll();
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;

        //TimableOutput.println("end requestPool");
        //TimableOutput.println("setEnd notify");
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

    public synchronized Person getTargetPerson(Elevator elevator) {
        while (true) {
            //TimableOutput.println("get Target");
            //没人了
            if (this.isEnd() && people.isEmpty()) {
                return null;
            }
            //TimableOutput.println("get Target1");
            //人还没来就等
            if (people.isEmpty()) {
                try {
                    //TimableOutput.println("wait");
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Person person : people) {
                //TimableOutput.println("2 "+person.isTargeted()+" "+
                // elevator.isFloorAccess(person.getFromFloor())+" "+isShortest(person,elevator));
                //如果人未被标记、电梯可以到这一层、电梯是最短换乘路线之一
                if (!person.isTargeted()
                        && elevator.isFloorAccess(person.getFromFloor())
                        && isShortest(person, elevator)) {
                    person.setTargeted(true);
                    //TimableOutput.println("out "+person.getPersonId()
                    // +" "+elevator.getElevatorId());
                    return person;
                }
            }
            return null;
        }
    }

    public synchronized boolean isEnd() {
        for (Elevator elevator : elevators) {
            if (elevator.hasPeople()) {
                return false;
            }
        }
        return isEnd && people.isEmpty();
    }

    public synchronized void setTargetFalse(Person targetPerson) {
        for (Person person : people) {
            if (person.equals(targetPerson)) {
                person.setTargeted(false);
                //TimableOutput.println("setpersonfalse "+targetPerson.getPersonId());
                //TimableOutput.println("setTarget notify");
                notifyAll();
            }
        }
    }

    public synchronized boolean hasPerson(Elevator elevator) {
        if (people.isEmpty()) {
            return false;
        }
        for (Person person : people) {
            if (checkHasperson(person, elevator)) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean checkHasperson(Person person, Elevator elevator) {
        if (elevator.getDirection() == 1
                && (person.getToFloor() >= person.getFromFloor())
                && (person.getFromFloor() == elevator.getCurrentFloor())
                && (isShortest(person, elevator))) {
            return true;
        }
        if ((elevator.getDirection() == -1 &&
                (person.getToFloor() <= person.getFromFloor())
                && (person.getFromFloor() == elevator.getCurrentFloor())
                && (isShortest(person, elevator)))) {
            return true;
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

            if (person == elevator.getTargetPerson()
                    && (person.getFromFloor() == elevator.getCurrentFloor())
                    && elevator.shouldOpen()
                    && isShortest(person, elevator)
                    && elevator.isFloorAccess(person.getFromFloor())) {
                it.remove();
                return person;
            }
            //跟当前电梯同向且在同一个楼层的可以上
            if (elevator.getDirection() == 1
                    && (person.getToFloor() >= person.getFromFloor())
                    && (person.getFromFloor() == elevator.getCurrentFloor())
                    && elevator.shouldOpen()
                    && isShortest(person, elevator)
                    && elevator.isFloorAccess(person.getFromFloor())) {
                it.remove();
                return person;
            }
            if (elevator.getDirection() == -1 &&
                    (person.getToFloor() <= person.getFromFloor())
                    && (person.getFromFloor() == elevator.getCurrentFloor())
                    && elevator.shouldOpen()
                    && isShortest(person, elevator)
                    && elevator.isFloorAccess(person.getFromFloor())) {
                it.remove();
                return person;
            }
        }
        return null;
    }

    public synchronized void getPeopleInTheElevator(Elevator elevator) {
        while (!elevator.isFull()) {
            Person person = this.getOnePersonOut(elevator);
            if (person == null) {
                break;
            }
            elevator.getOnePersonIn(person);
        }
    }

    //是最短的之一就安排downFloor
    private synchronized boolean isShortest(Person person, Elevator elevator) {
        setFloyd();
        for (int i = 1; i <= 11; i++) {
            //TimableOutput.println("1");
            if (elevator.isFloorAccess(i)) {
                //电梯可以到这一层
                //电梯可以直达
                if (i == person.getToFloor()
                        && floyd[person.getFromFloor() - 1][person.getToFloor() - 1] == 1) {
                    person.setDownFloor(i);
                    return true;
                }
                //如果电梯是最短转线的第一班
                if (floyd[person.getFromFloor() - 1][person.getToFloor() - 1]
                        == floyd[i - 1][person.getToFloor() - 1] + 1) {
                    person.setDownFloor(i);
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized void setFloyd() {
        //初始化
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                floyd[i][j] = 100;
            }
        }
        for (Elevator elevator : elevators) {
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    if (elevator.isFloorAccess(i + 1) && elevator.isFloorAccess(j + 1)) {
                        floyd[i][j] = 1;
                    }
                }
            }
        }
        for (int k = 0; k < 11; k++) {
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    if (floyd[i][j] > floyd[i][k] + floyd[k][j]) {
                        floyd[i][j] = floyd[i][k] + floyd[k][j];
                    }
                }
            }
        }
    }

    public synchronized void wantOpen(Elevator elevator) {
        if (!elevator.isFloorOut() && !elevator.isMaintain()) { //only serving
            while (servingElevator >= 4 || onlyPickingUpElevator >= 2) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            elevator.setOnlyPickingUp(true);
            servingElevator++;
            onlyPickingUpElevator++;
            return;
        }
        while (servingElevator >= 4) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        servingElevator++;
    }

    public synchronized void endOpen(Elevator elevator) {
        if (elevator.isOnlyPickingUp()) {
            onlyPickingUpElevator--;
            elevator.setOnlyPickingUp(false);
        }
        servingElevator--;
        //TimableOutput.println("endOPen notify");
        notifyAll();
    }
}
