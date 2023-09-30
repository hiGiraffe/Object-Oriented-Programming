package exceptions;

import com.oocourse.spec3.exceptions.RelationNotFoundException;

import java.util.HashMap;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private final int id1;
    private final int id2;
    private static final Counter COUNTER = new Counter();
    private static final HashMap<Integer, Integer> EXCEPTIONS = new HashMap<>();

    public MyRelationNotFoundException(int id1, int id2) {
        this.id1 = (id1 <= id2) ? id1 : id2;
        if (!EXCEPTIONS.containsKey(this.id1)) {
            EXCEPTIONS.put(this.id1, 0);
        }
        this.id2 = (id1 <= id2) ? id2 : id1;
        if (!EXCEPTIONS.containsKey(this.id2)) {
            EXCEPTIONS.put(this.id2, 0);
        }
    }

    @Override
    public void print() {
        COUNTER.increase();
        EXCEPTIONS.put(id1, EXCEPTIONS.get(id1) + 1);
        EXCEPTIONS.put(id2, EXCEPTIONS.get(id2) + 1);
        System.out.println("rnf-" + COUNTER.getNum() + ", " + id1
                + "-" + EXCEPTIONS.get(id1) + ", " + id2 + "-" + EXCEPTIONS.get(id2));
    }
}
