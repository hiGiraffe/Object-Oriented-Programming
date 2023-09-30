
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Factor extends Poly {
    private String str;
    private int index = 0;
    //    private ArrayList<Variable> variables = new ArrayList<>();
    //private ArrayList<Expr> exprs = new ArrayList<>();
    private String digitFactor = "[+-]?\\d+";
    private String miFactor = "[+-]?(?<xyz>[xyz])(\\*\\*(?<exponent>[+-]?\\d+))?";
    private String exprFactor = "[+-]?\\((?<expr>.*)\\)(\\*\\*(?<exponent>[+-]?\\d+))?";
    private String triFactor
            = "[+-]?(?<sign>sin|cos)\\((?<factor>.*)\\)(\\*\\*(?<exponent>[+-]?\\d+))?";

    private Pattern pp;
    private Matcher mm;
    private ArrayList<Variable> varAnses = new ArrayList<>();
    private ArrayList<Variable> varTmps = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        Factor factor = (Factor) o;
        this.build();
        factor.build();

        ArrayList<Variable> one = this.getVariable();
        ArrayList<Variable> two = factor.getVariable();


        // If either list is null, return whether the other is empty
        if (one.isEmpty()) {
            return two.isEmpty();
        }
        if (two.isEmpty()) {
            return one.isEmpty();
        }
        if (one.size() != two.size()) {
            return false;
        }
        final ArrayList<Variable> ctwo = new ArrayList<>(two);

        for (Variable itm : one) {
            Iterator<Variable> it = ctwo.iterator();
            boolean gotEq = false;
            while (it.hasNext()) {
                Variable tmp = it.next();
                if (itm.equals(tmp) && itm.getNumDigit().equals(tmp.getNumDigit())) {
                    it.remove();
                    gotEq = true;
                    break;
                }
            }
            if (!gotEq) {
                return false;
            }
        }
        // All elements in one were found in two, and they're the same size.
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public void parseFactor(String str) {
        this.str = str;
        pp = Pattern.compile(digitFactor);
        mm = pp.matcher(str);
        if (mm.matches()) { //数字因子
            parseNum(str);
        }
        pp = Pattern.compile(miFactor);
        mm = pp.matcher(str);
        if (mm.matches()) { //幂因子
            parseMi(str);
        }
        pp = Pattern.compile(exprFactor);
        mm = pp.matcher(str);
        if (mm.matches()) { //表达式因子
            parseExpr(str);
        }
        pp = Pattern.compile(triFactor);
        mm = pp.matcher(str);
        if (mm.matches()) {
            parseTri(str);
        }

    }

    public void parseNum(String str) {
        Variable var = new Variable();
        var.setVariable(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, new BigInteger(str));
        getVariables().add(var);
    }

    public void parseMi(String str) {
        Variable var = new Variable();

        switch (mm.group("xyz")) {
            case "x":
                if (mm.group("exponent") == null) { //无系数
                    var.setVariable(BigInteger.ONE, BigInteger.ZERO
                            , BigInteger.ZERO, BigInteger.valueOf(1));
                } else { //有系数
                    var.setVariable(BigInteger.valueOf(Integer.valueOf(mm.group("exponent"))),
                            BigInteger.ZERO, BigInteger.ZERO, BigInteger.valueOf(1));
                }

                break;
            case "y":
                if (mm.group("exponent") == null) {
                    var.setVariable(BigInteger.ZERO, BigInteger.ONE,
                            BigInteger.ZERO, BigInteger.valueOf(1));
                } else {
                    var.setVariable(BigInteger.ZERO,
                            BigInteger.valueOf(Integer.valueOf(mm.group("exponent")))
                            , BigInteger.ZERO, BigInteger.valueOf(1));
                }
                break;
            case "z":
                if (mm.group("exponent") == null) {
                    var.setVariable(BigInteger.ZERO, BigInteger.ZERO,
                            BigInteger.ONE, BigInteger.valueOf(1));
                } else {
                    var.setVariable(BigInteger.ZERO, BigInteger.ZERO,
                            BigInteger.valueOf(Integer.valueOf(mm.group("exponent")))
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
        getVariables().add(var);
    }

    public void clearAns() {
        this.ansString().setLength(0);
    }

    public void parseExpr(String str) {

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
            var.setVariable(BigInteger.ZERO, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ONE);
            if (str.charAt(0) == '-') {
                var.setVariable(BigInteger.ZERO, BigInteger.ZERO,
                        BigInteger.ZERO, BigInteger.valueOf(-1));
            }
            varTmps.add(var);
        } else { //指数是0 以外
            for (int i = 0; i < j; i++) {
                if (i == 0) { //第一项直接带入
                    for (Variable variable : expr.getVariable()) { //factor的变量
                        Variable varAns = new Variable();
                        varAns.setVariable(variable.readXExponent(),
                                variable.readYExponent(),
                                variable.readZExponent(),
                                variable.getNumDigit());
                        varAns.setSin(variable.getSin());
                        varAns.setCos(variable.getCos());
                        varTmps.add(varAns);
                    }
                } else {
                    for (Variable variable : expr.getVariable()) {
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
        }

        isExprPlus(str);

    }

    public void isExprPlus(String str) {
        //计算完成
        for (Variable varTmp : varTmps) {
            if (str.charAt(0) == '-') {
                varTmp.setVariable(varTmp.readXExponent(), varTmp.readYExponent(),
                        varTmp.readZExponent(),
                        varTmp.getNumDigit().multiply(BigInteger.valueOf(-1)));
            }
            this.getVariables().add(varTmp);
        }
    }

    public void parseTri(String str) {
        //计算一个变量
        Factor factor = new Factor();
        factor.parseFactor(mm.group("factor"));

        Variable var = new Variable();

        if (str.charAt(0) == '-') {
            var.setVariable(BigInteger.ZERO, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.valueOf(-1));
        } else {
            var.setVariable(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE);
        }

        if (mm.group("sign").equals("sin")) {
            if (mm.group("exponent") == null) {
                var.setSin(factor, BigInteger.ONE);
            }
            else {
                var.setSin(factor, new BigInteger(mm.group("exponent")));
            }
        } else if (mm.group("sign").equals("cos")) {
            if (mm.group("exponent") == null) {
                var.setCos(factor, BigInteger.ONE);
            } else {
                var.setCos(factor, new BigInteger(mm.group("exponent")));
            }
        }
        getVariables().add(var);
    }

    public ArrayList<Variable> getVariable() {
        return this.getVariables();
    }

}
