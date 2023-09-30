package exceptions;

import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private final int id;
    private static final Counter COUNTER = new Counter();
    private static final HashMap<Integer, Integer> EXCEPTIONS = new HashMap<>();

    public MyAcquaintanceNotFoundException(int id) {
        this.id = id;
        if (!EXCEPTIONS.containsKey(this.id)) {
            EXCEPTIONS.put(this.id, 0);
        }
    }

    @Override
    public void print() {
        COUNTER.increase();
        EXCEPTIONS.put(id, EXCEPTIONS.get(id) + 1);
        System.out.println("anf-" + COUNTER.getNum() + ", " + id + "-" + EXCEPTIONS.get(id));
    }
}
