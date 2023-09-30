import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Poly {

    //常数

    //HashMap<Integer, BigInteger> Sites = new HashMap<Integer, BigInteger>();//[x指数，y指数，z指数]-系数
    //HashMap<Integer, BigInteger> x = new HashMap<Integer, BigInteger>();
    //HashMap<Integer, BigInteger> y = new HashMap<Integer, BigInteger>();
    //HashMap<Integer, BigInteger> z = new HashMap<Integer, BigInteger>();
    //系数
    private int hashlast = 0;
    private int hash = 0;
    private int hashall = 0;
    private ArrayList<Variable> variables = new ArrayList<>();

    public ArrayList<Variable> getVariables() {
        return this.variables;
    }

    public boolean isTermSpecial(String str, int index) {
        if ((str.charAt(index) == '-' || str.charAt(index) == '+')
                && (str.charAt(index + 1) == '-' ||
                str.charAt(index + 1) == '+' || str.charAt(index + 1) == '(')) {
            return true;
        }
        return false;
    }

    public boolean isFactorSpecial(String str, int index) { //正是一个* 错是两个*
        if (str.charAt(index) == '*' && str.charAt(index + 1) != '*'
                && str.charAt(index - 1) != '*') {
            return true;
        }
        return false;
    }

    public void print() {
        build();

        for (int i = 0; i < this.variables.size(); i++) {
            printAdd(i);

            hash = 0; //这一次前面有无输出过东西
            hashlast = 0;

            if (variables.get(i).getNumDigit().equals(BigInteger.ONE)) { //先不输出1
            } else if (variables.get(i).getNumDigit().equals(BigInteger.ZERO)) {
                continue;
            } else if (variables.get(i).getNumDigit().equals(BigInteger.valueOf(-1))) {
                System.out.print("-");
            } else {
                System.out.print(variables.get(i).getNumDigit().toString());
                hash = 1;
            }
            hashall = 1;
            hashlast = 1;
            //x
            printX(i);
            //y
            printY(i);
            //z
            printZ(i);
            //tri
            printTri(i);

            if (hash == 0) {
                System.out.print("1");
            }
        }
        if (hashall == 0) {
            System.out.print(0);
        }
    }

    public void build() {
        HashMap<Variable, BigInteger> varAns = new HashMap<>();
        for (Variable variable : this.variables) {
            if (varAns.containsKey(variable)) {
                varAns.put(variable, varAns.get(variable).add(variable.getNumDigit()));
            } else {
                varAns.put(variable, variable.getNumDigit());
            }
        }
        this.variables.clear();
        for (Variable key : varAns.keySet()) {
            Variable tmp = new Variable();
            tmp.setVariable(key.readXExponent(), key.readYExponent()
                    , key.readZExponent(), varAns.get(key));
            tmp.setSin(key.getSin());
            tmp.setCos(key.getCos());
            this.variables.add(tmp);
        }
    }

    public void printAdd(int i) {
        if (i != 0 && hashall != 0
                && (!variables.get(i).getNumDigit().equals(BigInteger.ZERO))) {
            System.out.print("+");
        }
    }

    public void printX(int i) {
        if (!variables.get(i).readXExponent().equals(BigInteger.ZERO)) {
            if (hash == 1) {
                System.out.print("*");
            }
            if (variables.get(i).readXExponent().equals(BigInteger.ONE)) {
                System.out.print("x");
            } else {
                System.out.print("x**" + variables.get(i).readXExponent());
            }
            hash = 1;
        }
    }

    public void printY(int i) {
        if (!variables.get(i).readYExponent().equals(BigInteger.ZERO)) {
            if (hash == 1) {
                System.out.print("*");
            }
            if (variables.get(i).readYExponent().equals(BigInteger.ONE)) {
                System.out.print("y");
            } else {
                System.out.print("y**" + variables.get(i).readYExponent());
            }
            hash = 1;
        }
    }

    public void printZ(int i) {
        if (!variables.get(i).readZExponent().equals(BigInteger.ZERO)) {
            if (hash == 1) {
                System.out.print("*");
            } else { //无
            }
            if (variables.get(i).readZExponent().equals(BigInteger.ONE)) {
                System.out.print("z");
            } else {
                System.out.print("z**" + variables.get(i).readZExponent());
            }
            hash = 1;
        }
    }

    public void printTri(int i) {


        for (Factor factor : variables.get(i).getSin().keySet()) {
            factor.build();
            if (hash == 1) {
                System.out.print("*");
            }
            System.out.print("sin(");
            //            if (factor.getVariable().size() <= 1) {
            //                factor.print();
            //            } else {
            System.out.print("(");
            factor.print();
            System.out.print(")");
            //            }

            System.out.print(")");

            if (variables.get(i).getSin().get(factor) != BigInteger.ONE) {
                System.out.print("**" + variables.get(i).getSin().get(factor).toString());
            }

            hash = 1;
        }
        for (Factor factor : variables.get(i).getCos().keySet()) {
            if (hash == 1) {
                System.out.print("*");
            }
            System.out.print("cos(");
            //            if (factor.getVariable().size() <= 1) {
            //                factor.print();
            //            } else {
            System.out.print("(");
            factor.print();
            System.out.print(")");
            //            }

            System.out.print(")");

            if (variables.get(i).getCos().get(factor) != BigInteger.ONE) {
                System.out.print("**" + variables.get(i).getCos().get(factor).toString());
            }

            hash = 1;
        }
    }
}
//zero的0次方