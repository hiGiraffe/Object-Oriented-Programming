import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Expr expr = new Expr();
        expr.parseExpr(in.nextLine().replaceAll("[ \t]", ""));
        expr.print();
        in.close();
    }
}
//(x*y+2*x)*(3x*y+2*x)