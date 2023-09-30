
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Factor {
    private String str;
    private int index = 0;
    private ArrayList<Variable> variables = new ArrayList<>();
    //private ArrayList<Expr> exprs = new ArrayList<>();
    private String digitFactor = "[+-]?\\d+";
    private String miFactor = "[+-]?(?<xyz>[xyz])(\\*\\*(?<exponent>[+-]?\\d+))?";
    private String exprFactor = "[+-]?\\((?<expr>.*)\\)(\\*\\*(?<exponent>[+-]?\\d+))?";

    private Pattern pp;
    private Matcher mm;
    private ArrayList<Variable> varAnses = new ArrayList<>();
    private ArrayList<Variable> varTmps = new ArrayList<>();

    public void parseFactor(String str) {
        pp = Pattern.compile(digitFactor);
        mm = pp.matcher(str);
        if (mm.matches()) { //数字因子
            Variable var = new Variable();
            var.setVariable(0, 0, 0, new BigInteger(str));
            variables.add(var);
        } else {
            pp = Pattern.compile(miFactor);
            mm = pp.matcher(str);
            if (mm.matches()) { //幂因子
                Variable var = new Variable();

                switch (mm.group("xyz")) {
                    case "x":
                        if (mm.group("exponent") == null) { //无系数
                            var.setVariable(1, 0, 0, BigInteger.valueOf(1));
                        } else { //有系数
                            var.setVariable(Integer.valueOf(mm.group("exponent")),
                                    0, 0, BigInteger.valueOf(1));
                        }

                        break;
                    case "y":
                        if (mm.group("exponent") == null) {
                            var.setVariable(0, 1, 0, BigInteger.valueOf(1));
                        } else {
                            var.setVariable(0, Integer.valueOf(mm.group("exponent"))
                                    , 0, BigInteger.valueOf(1));
                        }
                        break;
                    case "z":
                        if (mm.group("exponent") == null) {
                            var.setVariable(0, 0, 1, BigInteger.valueOf(1));
                        } else {
                            var.setVariable(0, 0, Integer.valueOf(mm.group("exponent"))
                                    , BigInteger.valueOf(1));
                        }
                        break;
                    default:
                        break;
                }
                if (str.charAt(0) == '-') {
                    var.setVariable(var.readXExponent(), var.readYExponent()
                            , var.readZExponent(),
                            var.getNumDigit().multiply(BigInteger.valueOf(-1)));
                }
                variables.add(var);
            } else { //表达式因子
                parse1(str);
            }
        }
    }

    public void parse1(String str) {
        pp = Pattern.compile(exprFactor);
        mm = pp.matcher(str);
        if (mm.matches()) {
            //计算出一个expr
            Expr expr = new Expr();
            expr.parseExpr(mm.group("expr"));
            //乘以指数

            int j;
            if (mm.group("exponent") == null) {
                j = 1;
            } else {
                j = Integer.valueOf(mm.group("exponent"));
            }
            if (j == 0) { //指数是0
                Variable var = new Variable();
                var.setVariable(0, 0, 0, BigInteger.ONE);
                if (str.charAt(0) == '-') {
                    var.setVariable(0, 0, 0, BigInteger.valueOf(-1));
                }
                varTmps.add(var);
            } else { //指数是0，1 以外
                for (int i = 0; i < j; i++) {
                    if (i == 0) { //第一项直接带入
                        for (Variable variable : expr.getVariable()) { //factor的变量
                            Variable varAns = new Variable();
                            varAns.setVariable(variable.readXExponent(),
                                    variable.readYExponent(),
                                    variable.readZExponent(),
                                    variable.getNumDigit());
                            varTmps.add(varAns);
                        }
                    } else {
                        for (Variable variable : expr.getVariable()) {
                            for (Variable varTmp : varTmps) {
                                Variable varAns = new Variable();
                                varAns.setVariable(varTmp.readXExponent()
                                                + variable.readXExponent(),
                                        varTmp.readYExponent()
                                                + variable.readYExponent(),
                                        varTmp.readZExponent()
                                                + variable.readZExponent(),
                                        varTmp.getNumDigit().
                                                multiply(variable.getNumDigit()));
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
            }

            finishComputing(str);
        }
    }

    public void finishComputing(String str) {
        //计算完成
        for (Variable varTmp : varTmps) {
            if (str.charAt(0) == '0') {
                varTmp.setVariable(varTmp.readXExponent(), varTmp.readYExponent(),
                        varTmp.readZExponent(),
                        varTmp.getNumDigit().multiply(BigInteger.valueOf(-1)));
            }
            this.variables.add(varTmp);
        }
    }

    public ArrayList<Variable> getVariable() {
        return this.variables;
    }
}
