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
        in.close();
    }
}
//本次作业支持嵌套多层括号。√ ((-1)+5*x)
//本次作业新增三角函数因子，
// 。
//variable里面放三角函数 expr sin 和 cos，要用arraylist和表达式
//本次作业新增自定义函数因子，但自定义函数的函数表达式中不会调用其他函数。
//sin equal记得考虑里面有sin的情况
//考虑实参形参，一次性替换，把x，y换成p，q
//sin0，cos0
//sin内正负相反的情况
//sin括号 除了数字因子和幂因子


//自定义函数
//表达式树做子树替换

//评测机 sympy表达式 infunction和outfunction
//前导0有问题，去掉前导0

//数据生成器，注意cost，如果超过直接截断