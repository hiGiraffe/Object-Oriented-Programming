import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Expr extends Poly {
    private String str;
    private int index = 0;
    private ArrayList<Term> terms = new ArrayList<>();
    private ArrayList<Variable> variables = new ArrayList<>();
    private int hashlast = 0;
    private int hash = 0;
    private int hashall = 0;

    public boolean isEnd(char i) {
        switch (i) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'x':
            case 'y':
            case 'z':
            case ')':
                return true;
            default:
                return false;
        }
    }

    public void parseExpr(String str) {

        int indexFirst = index;
        //分成一个个term
        while (index < str.length()) {
            Term term = new Term();

            //            if (isTermSpecial(str, index) && str.charAt(index) == '-') { //-+ | --
            //                term.setPlus(false);
            //                index++;
            //            } else if (isTermSpecial(str, index)) { //+- | ++
            //                term.setPlus(true);
            //                index++;
            //            } else {
            //                term.setPlus(true);
            //            }
            //把+- ++ -- -+处理了先
            term.setPlus(true);
            while (isTermSpecial(str, index)) {
                if (str.charAt(index) == '-') {
                    term.setPlus(!term.readIsPlus());
                }
                index++;
            }

            indexFirst = index;

            while (index < str.length()) {

                if (str.charAt(index) == '(') { //读到）为止
                    while (index < str.length() && str.charAt(index) != ')') {
                        index++;
                    }
                } else if (index + 1 < str.length() && //还有两个加减号的情况
                        (str.charAt(index + 1) == '+' || str.charAt(index + 1) == '-') &&
                        (str.charAt(index) == '+' || str.charAt(index) == '-')
                ) {
                    if (str.charAt(index) == '-') {
                        term.setPlus(!term.readIsPlus());
                    }
                    index++;
                    indexFirst++;
                } else if (index + 1 < str.length() &&
                        (((str.charAt(index + 1) == '+' || str.charAt(index + 1) == '-')
                                && isEnd(str.charAt(index))))
                ) { //下一个是另外一个term
                    term.parseTerm(str.substring(indexFirst, index + 1));
                    terms.add(term);
                    index++;
                    break;
                } else {
                    index++;
                }
            }
            if (index == str.length()) { //最后一个term
                term.parseTerm(str.substring(indexFirst, index));
                terms.add(term);
            }
        }
        for (Term term : terms) { //项目
            for (Variable variable : term.getVariable()) { //项目下的变量
                if (term.readIsPlus()) {
                    this.variables.add(variable);
                } else {
                    variable.setVariable(variable.readXExponent(), variable.readYExponent()
                            , variable.readZExponent(),
                            variable.getNumDigit().multiply(BigInteger.valueOf(-1)));
                    this.variables.add(variable);
                }

            }
        }
    }

    public void print() {
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
            this.variables.add(tmp);
        }

        this.print1();
    }

    public void print1() {
        //输出

        for (int i = 0; i < variables.size(); i++) {
            if (i != 0 && hashall != 0
                    && (!variables.get(i).getNumDigit().equals(BigInteger.ZERO))) {
                System.out.print("+");
            }
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
            if (variables.get(i).readXExponent() != 0) {
                if (hash == 1) {
                    System.out.print("*");
                }
                if (variables.get(i).readXExponent() == 1) {
                    System.out.print("x");
                } else {
                    System.out.print("x**" + variables.get(i).readXExponent());
                }
                hash = 1;
            }
            //y
            if (variables.get(i).readYExponent() != 0) {
                if (hash == 1) {
                    System.out.print("*");
                }
                if (variables.get(i).readYExponent() == 1) {
                    System.out.print("y");
                } else {
                    System.out.print("y**" + variables.get(i).readYExponent());
                }
                hash = 1;
            }
            //z
            if (variables.get(i).readZExponent() != 0) {
                if (hash == 1) {
                    System.out.print("*");
                } else { //无
                }
                if (variables.get(i).readZExponent() == 1) {
                    System.out.print("z");
                } else {
                    System.out.print("z**" + variables.get(i).readZExponent());
                }
                hash = 1;
            }
            if (hash == 0) {
                System.out.print("1");
            }
        }
        print2();
    }

    public void print2() {
        if (hashall == 0) {
            System.out.print(0);
        }
    }

    public ArrayList<Variable> getVariable() {
        return this.variables;
    }
}
