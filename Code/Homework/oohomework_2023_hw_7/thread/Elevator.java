package thread;

import com.oocourse.elevator3.TimableOutput;
import data.People;
import data.Person;
import data.RequestPool;

public class Elevator extends Thread {
    //Init
    private final int id;
    private final long moveTime;
    private final long openDoorTime;
    private final long closeDoorTime;
    private final int maxNumOfTake;
    private final RequestPool requestPool;
    private final int access;

    //Change
    private int currentFloor;
    private int direction; //1 is up,-1 is down
    private People people;
    private boolean isMaintain;
    private Person targetPerson;
    private boolean isOnlyPickingUp;

    public Elevator(int currentFloor, int id,
                    long moveTime, long openDoorTime, long closeDoorTime,
                    int maxNumOfTake, RequestPool requestPool, int access) {
        this.currentFloor = currentFloor;
        this.id = id;
        this.moveTime = moveTime;
        this.openDoorTime = openDoorTime;
        this.closeDoorTime = closeDoorTime;
        this.maxNumOfTake = maxNumOfTake;
        this.access = access;

        this.direction = 1;
        this.people = new People();
        this.isMaintain = false;
        this.requestPool = requestPool;
        this.targetPerson = null;
        this.isOnlyPickingUp = false;
    }

    public void setMaintain(boolean maintain) {
        isMaintain = maintain;
    }

    public boolean isMaintain() {
        return isMaintain;
    }

    public boolean isOnlyPickingUp() {
        return isOnlyPickingUp;
    }

    public void setOnlyPickingUp(boolean onlyPickingUp) {
        isOnlyPickingUp = onlyPickingUp;
    }

    @Override
    public void run() {
        while (true) {
            //TimableOutput.println("run elevator "+id);
            //目标还在不在，没有目标或者不在就返回null
            checkTarget();
            //TimableOutput.println("run elevator1 "+id);
            //是否维修
            checkMaintain();
            if (this.isMaintain) {
                //TimableOutput.println("end "+id);
                return;
            }
            //TimableOutput.println("run elevator2 "+id);
            //线程池结束了并且人走完了
            if (targetPerson == null && requestPool.isEnd()) {
                //TimableOutput.println("end "+id);
                return;
            }
            //电梯被唤醒了，但没东西，重新等
            if (targetPerson == null && people.isEmpty()) {
                continue;
            }
            //确定方向
            checkDirection();
            //进出人
            personOutAndIn();
            //移动
            move();
        }
    }

    private void checkMaintain() {
        if (this.isMaintain) {
            requestPool.wantOpen(this);
            requestPool.setTargetFalse(targetPerson);
            requestPool.deleteElevator(this);
            people.outAll(this);
            requestPool.endOpen(this);
            TimableOutput.println("MAINTAIN_ABLE-" + getElevatorId());
        }
    }

    public boolean hasPeople() {
        return !this.people.isEmpty();
    }

    private void checkTarget() {
        //TimableOutput.println("check target1 "+id);
        //不是空的就继续送
        if (!people.isEmpty()) {
            return;
        }
        //TimableOutput.println("check target2 "+id);
        //是空的就判断有没有目标
        //目标还在
        if (requestPool.isPersonInPool(targetPerson)) {
            return;
        }
        //TimableOutput.println("check target3 "+id);
        //目标无了
        targetPerson = null;
        targetPerson = requestPool.getTargetPerson(this);
        //没有找到合适的目标
        if (targetPerson == null) {
            try {
                sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void personOutAndIn() {
        //没有人要出电梯或等待队列中没有人要进电梯就不开门
        if (!shouldOpen()) {
            return;
        }
        requestPool.wantOpen(this);
        //开门
        TimableOutput.println("OPEN-" + this.currentFloor + "-" + this.id);
        //出人
        getPersonOutTheElevator(people);
        try {
            Thread.sleep(openDoorTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(closeDoorTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //进人
        requestPool.getPeopleInTheElevator(this);
        //关门
        TimableOutput.println("CLOSE-" + this.currentFloor + "-" + this.id);
        requestPool.endOpen(this);
    }

    private void checkDirection() {
        //同一层
        if (targetPerson.getFromFloor() == currentFloor) {
            //方向不一样变
            if (!targetPerson.isDirectionSameToElevator(direction)) {
                direction = -direction;
            }

            return;
        }
        //这个方向 没有人要下电梯 且 目标不在前面了，就调头
        if (!(people.hasPerson(this.currentFloor, this.direction)
                || targetPerson.hasPerson(this.currentFloor, this.direction))) {
            direction = -direction;
            return;
        }
    }

    public void getOneRequestToRequestQueue(Person person) {
        //TimableOutput.println("getOneRequestToRequestQueue in elevactor");
        requestPool.addRequest(person);
    }

    public long getOpenDoorTime() {
        return openDoorTime;
    }

    public long getCloseDoorTime() {
        return closeDoorTime;
    }

    public boolean isFloorOut() {
        return people.isFloorOut(this.currentFloor);
    }

    public Person getTargetPerson() {
        return this.targetPerson;
    }

    public boolean shouldOpen() {
        //有人要出
        if (people.isFloorOut(this.currentFloor)) {
            return true;
        }
        if (targetPerson == null) {
            return false;
        }
        if (!this.isFloorAccess(this.currentFloor)) {
            return false;
        };
        //如果targetPerson要去的方向和电梯同向
        if (targetPerson.isDirectionSameToElevator(this.direction)) {
            //有人要捎带
            if (requestPool.hasPerson(this) && !this.isFull()) {
                return true;
            }
        }
        //到目标了
        if (this.currentFloor == targetPerson.getFromFloor()
                && requestPool.isPersonInPool(targetPerson)) {
            return true;
        }
        return false;
    }

    public synchronized void getPersonOutTheElevator(People people) {
        while (!people.isEmpty()) {
            Person person = people.getOnePersonOut(this);
            if (person == null) {
                break;
            }
            TimableOutput.println("OUT-" + person.getPersonId()
                    + "-" + this.getCurrentFloor() + "-" + this.getElevatorId());
        }
    }

    public void getOnePersonIn(Person person) {
        people.getOnePersonIn(person, this);
    }

    private void move() {
        if (this.isEmpty()) {
            return;
        }

        try {
            Thread.sleep(moveTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.currentFloor += direction;
        TimableOutput.println("ARRIVE-" + this.currentFloor + "-" + this.id);
    }

    public boolean isFloorAccess(int floor) {
        return (access & (1 << (floor - 1))) != 0;
    }

    /* 这里指在电梯内或是目标 */
    public boolean isEmpty() {
        return this.people.isEmpty() && !requestPool.isPersonInPool(this.targetPerson);
    }

    public boolean isFull() {
        return this.people.getNum() == maxNumOfTake;
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }

    public int getDirection() {
        return this.direction;
    }

    public int getElevatorId() {
        return this.id;
    }
}
