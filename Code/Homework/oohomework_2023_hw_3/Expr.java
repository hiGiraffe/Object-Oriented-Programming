import java.math.BigInteger;
import java.util.ArrayList;

public class Expr extends Poly {
    private String str;
    private int index = 0;
    private ArrayList<Term> terms = new ArrayList<>();

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
        this.str = str;
        int indexFirst = index;
        //分成一个个term
        while (index < str.length()) {
            Term term = new Term();
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
                    int numBacket = 1;
                    index++;
                    while (index < str.length() && numBacket > 0) {
                        if (str.charAt(index) == ')') {
                            numBacket--;
                        } else if (str.charAt(index) == '(') {
                            numBacket++;
                        }
                        if (numBacket != 0) {
                            index++;
                        }
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
        count();
    }

    public void count() {
        for (Term term : terms) { //项目
            for (Variable variable : term.getVariable()) { //项目下的变量
                if (variable.getNumDigit().equals(BigInteger.ZERO)) {
                    continue;
                } else if (term.readIsPlus()) {
                    this.getVariables().add(variable);
                } else {
                    variable.setVariable(variable.readXExponent(), variable.readYExponent()
                            , variable.readZExponent(),
                            variable.getNumDigit().multiply(BigInteger.valueOf(-1)));
                    this.getVariables().add(variable);
                }

            }
        }
    }

    public ArrayList<Variable> getVariable() {
        return this.getVariables();
    }

}
