import java.util.ArrayList;

public class Term extends Poly {
    private String str;
    private int index = 0;
    private boolean isPlus;//true为+ false为- 计算记得用
    private ArrayList<Factor> factors = new ArrayList<>();

    public boolean readIsPlus() {
        return this.isPlus;
    }

    public void parseTerm(String str) {
        this.str = str;
        while (index < str.length()) {
            int indexFirst = index;
            //拆分因子
            while (index < str.length()) {
                Factor factor = new Factor();
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
                } else if (index + 1 < str.length()
                        && isFactorSpecial(str, index + 1)) { //下一个是另外一个term
                    factor.parseFactor(str.substring(indexFirst, index + 1));
                    factors.add(factor);
                    index += 2;//乘号也去掉
                    break;
                } else {
                    index++;
                }
                if (index == str.length()) { //最后一个factor
                    factor.parseFactor(str.substring(indexFirst, index));
                    factors.add(factor);
                }
            }
        }
        count();
    }

    public void count() {

        ArrayList<Variable> varAnses = new ArrayList<>();
        ArrayList<Variable> varTmps = new ArrayList<>();

        int hash = 0;
        for (Factor factor : factors) {
            //第一个
            if (hash == 0) {
                for (Variable variable : factor.getVariable()) { //factor的变量
                    Variable varAns = new Variable();
                    varAns.setVariable(variable.readXExponent(),
                            variable.readYExponent(),
                            variable.readZExponent(),
                            variable.getNumDigit());
                    //sin
                    varAns.setSin(variable.getSin());
                    //cos
                    varAns.setCos(variable.getCos());

                    varTmps.add(varAns);
                }
                hash = 1;
            }
            //后面的
            else {
                //两两相乘
                for (Variable variable : factor.getVariable()) {
                    for (Variable varTmp : varTmps) {
                        Variable varAns = new Variable();
                        varAns.setVariable(varTmp.readXExponent().add(variable.readXExponent()),
                                varTmp.readYExponent().add(variable.readYExponent()),
                                varTmp.readZExponent().add(variable.readZExponent()),
                                varTmp.getNumDigit().multiply(variable.getNumDigit()));
                        varAns.mulSin(varTmp.getSin(), variable.getSin());
                        varAns.mulCos(varTmp.getCos(), variable.getCos());
                        varAnses.add(varAns);
                    }
                }
                varTmps.clear();
                for (Variable varAns : varAnses) {
                    varTmps.add(varAns);
                }
                varAnses.clear();
            }
        }
        for (Variable varTmp : varTmps) {
            this.getVariables().add(varTmp);
        }
    }

    public void setPlus(boolean isPlus) { //0为- 1为+ 且两个加号
        this.isPlus = isPlus;
    }

    public ArrayList<Variable> getVariable() {
        return this.getVariables();
    }
}



