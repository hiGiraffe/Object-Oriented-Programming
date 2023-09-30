package exceptions;

import com.oocourse.spec2.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private final int id;
    private static final Counter COUNTER = new Counter();
    private static final HashMap<Integer, Integer> EXCEPTIONS = new HashMap<>();

    public MyEqualMessageIdException(int id) {
        this.id = id;
        if (!EXCEPTIONS.containsKey(this.id)) {
            EXCEPTIONS.put(this.id, 0);
        }
    }

    @Override
    public void print() {
        COUNTER.increase();
        EXCEPTIONS.put(id, EXCEPTIONS.get(id) + 1);
        System.out.println("emi-" + COUNTER.getNum() + ", " + id + "-" + EXCEPTIONS.get(id));
    }
}
