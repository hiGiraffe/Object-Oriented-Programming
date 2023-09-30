import java.math.BigInteger;
import java.util.HashMap;

public class Poly extends Deriver {

    //系数
    private int hashlast = 0;
    private int hash = 0;
    private int hashall = 0;
    private StringBuilder ans = new StringBuilder();

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

        for (int i = 0; i < this.getVariables().size(); i++) {
            printAdd(i);

            hash = 0; //这一次前面有无输出过东西
            hashlast = 0;

            if (getVariables().get(i).getNumDigit().equals(BigInteger.ONE)) { //先不输出1
            } else if (getVariables().get(i).getNumDigit().equals(BigInteger.ZERO)) {
                continue;
            } else if (getVariables().get(i).getNumDigit().equals(BigInteger.valueOf(-1))) {
                ans.append("-");
            } else {
                ans.append(getVariables().get(i).getNumDigit().toString());
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
                ans.append("1");
            }
        }
        if (hashall == 0) {
            ans.append("0");
        }
    }

    public StringBuilder ansString() {
        return this.ans;
    }

    public void build() {
        HashMap<Variable, BigInteger> varAns = new HashMap<>();
        for (Variable variable : this.getVariables()) {
            if (varAns.containsKey(variable)) {
                varAns.put(variable, varAns.get(variable).add(variable.getNumDigit()));
            } else {
                varAns.put(variable, variable.getNumDigit());
            }
        }
        this.getVariables().clear();
        for (Variable key : varAns.keySet()) {
            Variable tmp = new Variable();
            tmp.setVariable(key.readXExponent(), key.readYExponent()
                    , key.readZExponent(), varAns.get(key));
            tmp.setSin(key.getSin());
            tmp.setCos(key.getCos());
            this.getVariables().add(tmp);
        }
    }

    public void printAdd(int i) {
        if (i != 0 && hashall != 0
                && (!getVariables().get(i).getNumDigit().equals(BigInteger.ZERO))) {
            ans.append("+");
        }
    }

    public void printX(int i) {
        if (!getVariables().get(i).readXExponent().equals(BigInteger.ZERO)) {
            if (hash == 1) {
                ans.append("*");
            }
            if (getVariables().get(i).readXExponent().equals(BigInteger.ONE)) {
                ans.append("x");
            } else {
                ans.append("x**" + getVariables().get(i).readXExponent());
            }
            hash = 1;
        }
    }

    public void printY(int i) {
        if (!getVariables().get(i).readYExponent().equals(BigInteger.ZERO)) {
            if (hash == 1) {
                ans.append("*");
            }
            if (getVariables().get(i).readYExponent().equals(BigInteger.ONE)) {
                ans.append("y");
            } else {
                ans.append("y**" + getVariables().get(i).readYExponent());
            }
            hash = 1;
        }
    }

    public void printZ(int i) {
        if (!getVariables().get(i).readZExponent().equals(BigInteger.ZERO)) {
            if (hash == 1) {
                ans.append("*");
            } else { //无
            }
            if (getVariables().get(i).readZExponent().equals(BigInteger.ONE)) {
                ans.append("z");
            } else {
                ans.append("z**" + getVariables().get(i).readZExponent());
            }
            hash = 1;
        }
    }

    public void printTri(int i) {


        for (Factor factor : getVariables().get(i).getSin().keySet()) {
            factor.build();
            if (getVariables().get(i).getSin().get(factor) != BigInteger.ZERO) {
                if (hash == 1) {
                    ans.append("*");
                }
                ans.append("sin(");
                //            if (factor.getVariable().size() <= 1) {
                //                factor.print();
                //            } else {
                ans.append("(");
                factor.print();
                ans.append(factor.ansString());
                factor.clearAns();
                ans.append(")");
                //            }

                ans.append(")");

                if (getVariables().get(i).getSin().get(factor) != BigInteger.ONE) {
                    ans.append("**" + getVariables().get(i).getSin().get(factor).toString());
                }

                hash = 1;
            }
        }
        for (Factor factor : getVariables().get(i).getCos().keySet()) {
            if (getVariables().get(i).getCos().get(factor) != BigInteger.ZERO) {
                if (hash == 1) {
                    ans.append("*");
                }
                ans.append("cos(");
                //            if (factor.getVariable().size() <= 1) {
                //                factor.print();
                //            } else {
                ans.append("(");
                factor.print();
                ans.append(factor.ansString());
                factor.clearAns();
                ans.append(")");
                //            }

                ans.append(")");

                if (getVariables().get(i).getCos().get(factor) != BigInteger.ONE) {
                    ans.append("**" + getVariables().get(i).getCos().get(factor).toString());
                }

                hash = 1;
            }
        }

    }

    public void printAns() {
        System.out.print(ans);
    }

}