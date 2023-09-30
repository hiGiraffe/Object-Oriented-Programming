package exceptions;

import com.oocourse.spec3.exceptions.EqualGroupIdException;

import java.util.HashMap;

public class MyEqualGroupIdException extends EqualGroupIdException {
    private final int id;
    private static final Counter COUNTER = new Counter();
    private static final HashMap<Integer, Integer> EXCEPTS = new HashMap<>();

    public MyEqualGroupIdException(int id) {
        this.id = id;
        if (!EXCEPTS.containsKey(this.id)) {
            EXCEPTS.put(this.id, 0);
        }
    }

    @Override
    public void print() {
        COUNTER.increase();
        EXCEPTS.put(id, EXCEPTS.get(id) + 1);
        System.out.println("egi-" + COUNTER.getNum() + ", " + id + "-" + EXCEPTS.get(id));
    }
}
