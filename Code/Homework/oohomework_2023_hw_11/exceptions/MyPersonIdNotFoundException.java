package exceptions;

import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int id;
    private static final Counter COUNTER = new Counter();
    private static final HashMap<Integer, Integer> EXCEPTS = new HashMap<>();

    public MyPersonIdNotFoundException(int id) {
        this.id = id;
        if (!EXCEPTS.containsKey(this.id)) {
            EXCEPTS.put(this.id, 0);
        }
    }

    @Override
    public void print() {
        COUNTER.increase();
        EXCEPTS.put(id, EXCEPTS.get(id) + 1);
        System.out.println("pinf-" + COUNTER.getNum() + ", " + id + "-" + EXCEPTS.get(id));
    }
}
