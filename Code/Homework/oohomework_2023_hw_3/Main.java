import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Expr expr = new Expr();
        Function function = new Function();
        int n = in.nextInt();
        in.nextLine();
        for (int i = 0; i < n; i++) {
            function.addFunction(in.nextLine().replaceAll("[ \t]", ""));
        }
        expr.parseExpr(function.replaceFunction(in.nextLine().replaceAll("[ \t]", "")));
        expr.print();
        expr.printAns();
        in.close();
    }
}
//


//自定义函数
//表达式树做子树替换

//评测机 sympy表达式 infunction和outfunction
//前导0有问题，去掉前导0

//数据生成器，注意cost，如果超过直接截断