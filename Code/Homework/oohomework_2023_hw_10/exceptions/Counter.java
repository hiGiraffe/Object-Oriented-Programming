package exceptions;

public class Counter {
    private int num;

    public Counter() {
        num = 0;
    }

    public void increase() {
        num++;
    }

    public int getNum() {
        return num;
    }
}
