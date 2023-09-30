import java.math.BigInteger;
import java.util.ArrayList;

public class Deriver {
    private ArrayList<Variable> variables = new ArrayList<>();

    public ArrayList<Variable> getVariables() {
        return this.variables;
    }

    public Expr derive(String str, char deriveStyle) {
        Expr expr = new Expr();
        expr.parseExpr(str);//变量
        switch (deriveStyle) {
            case 'x':
                deriveX(expr);
                break;
            case 'y':
                deriveY(expr);
                break;
            case 'z':
                deriveZ(expr);
                break;
            default:
                return null;
        }
        return (Expr) this;
    }

    public void deriveX(Poly poly) { //注意三个复制


        for (Variable variable : poly.getVariables()) {
            if (variable.getSin().isEmpty() && variable.getCos().isEmpty()
                    && variable.readXExponent().equals(BigInteger.ZERO)) {
                continue;
            } else if (variable.getSin().isEmpty() && variable.getCos().isEmpty()) { //只有常数
                Variable variableAdd = new Variable();
                variableAdd.setVariable(variable.readXExponent().add(BigInteger.valueOf(-1)),
                        variable.readYExponent(),
                        variable.readZExponent(),
                        variable.getNumDigit().multiply(variable.readXExponent()));
                getVariables().add(variableAdd);
            } else if (variable.readXExponent().equals(BigInteger.ZERO)) {
                deriveXSin(variable);
                deriveXCos(variable);
            } else {
                //常数项和幂函数项
                Variable variableAdd = new Variable();
                variableAdd.setVariable(variable.readXExponent().add(BigInteger.valueOf(-1)),
                        variable.readYExponent(),
                        variable.readZExponent(),
                        variable.getNumDigit().multiply(variable.readXExponent()));
                variableAdd.setSin(variable.getSin());
                variableAdd.setCos(variable.getCos());
                getVariables().add(variableAdd);

                deriveXSin(variable);
                deriveXCos(variable);
            }
        }

    }

    public void deriveY(Poly poly) { //注意三个复制


        for (Variable variable : poly.getVariables()) {
            if (variable.getSin().isEmpty() && variable.getCos().isEmpty()
                    && variable.readYExponent().equals(BigInteger.ZERO)) {
                continue;
            } else if (variable.getSin().isEmpty() && variable.getCos().isEmpty()) { //只有常数
                Variable variableAdd = new Variable();
                variableAdd.setVariable(variable.readXExponent(),
                        variable.readYExponent().add(BigInteger.valueOf(-1)),
                        variable.readZExponent(),
                        variable.getNumDigit().multiply(variable.readYExponent()));
                getVariables().add(variableAdd);
            } else if (variable.readYExponent().equals(BigInteger.ZERO)) {
                deriveYSin(variable);
                deriveYCos(variable);
            } else {
                //常数项和幂函数项
                Variable variableAdd = new Variable();
                variableAdd.setVariable(variable.readXExponent(),
                        variable.readYExponent().add(BigInteger.valueOf(-1)),
                        variable.readZExponent(),
                        variable.getNumDigit().multiply(variable.readYExponent()));
                variableAdd.setSin(variable.getSin());
                variableAdd.setCos(variable.getCos());
                getVariables().add(variableAdd);

                deriveYSin(variable);
                deriveYCos(variable);
            }
        }

    }

    public void deriveZ(Poly poly) { //注意三个复制


        for (Variable variable : poly.getVariables()) {
            if (variable.getSin().isEmpty() && variable.getCos().isEmpty()
                    && variable.readZExponent().equals(BigInteger.ZERO)) {
                continue;
            } else if (variable.getSin().isEmpty() && variable.getCos().isEmpty()) { //只有常数
                Variable variableAdd = new Variable();
                variableAdd.setVariable(variable.readXExponent(), variable.readYExponent(),
                        variable.readZExponent().add(BigInteger.valueOf(-1)),
                        variable.getNumDigit().multiply(variable.readZExponent()));
                getVariables().add(variableAdd);
            } else if (variable.readZExponent().equals(BigInteger.ZERO)) { //有三角函数无幂指数
                deriveZSin(variable);
                deriveZCos(variable);
            } else {
                //常数项和幂函数项
                Variable variableAdd = new Variable();
                variableAdd.setVariable(variable.readXExponent(), variable.readYExponent(),
                        variable.readZExponent().add(BigInteger.valueOf(-1)),
                        variable.getNumDigit().multiply(variable.readZExponent()));
                variableAdd.setSin(variable.getSin());
                variableAdd.setCos(variable.getCos());
                getVariables().add(variableAdd);

                deriveZSin(variable);
                deriveZCos(variable);
            }
        }

    }

    public void deriveXSin(Variable variable) {
        for (Factor factor : variable.getSin().keySet()) {

            Variable variableSin = new Variable();
            //该项求导并乘以常数项
            deriveSinExponent(variableSin, variable, factor);
            //求导Sin变Cos
            deriveSinToCos(variableSin, factor);
            Factor factor1 = new Factor();
            factor1.getVariables().add(variableSin);
            ArrayList<Factor> factors = new ArrayList<>();
            factors.add(factor1);

            //三角函数内求导
            Factor factor2 = new Factor();
            factor2.deriveX(factor);
            factors.add(factor2);

            //其他项算入factor3
            sinMulOther(variable, factor, factors);
            //相乘加到原来的目录下
            countMul(factors);
        }
    }

    public void deriveXCos(Variable variable) {
        //取每一个cos
        for (Factor factor : variable.getCos().keySet()) {
            Variable variableCos = new Variable();
            //该项求导并乘以常数项
            deriveCosExponent(variableCos, variable, factor);
            //求导cos变成sin
            deriveCosToSin(variableCos, factor);
            ArrayList<Factor> factors = new ArrayList<>();
            Factor factor1 = new Factor();
            factor1.getVariables().add(variableCos);
            factors.add(factor1);

            //三角函数内求导，得到一个有很多变量的因子
            Factor factor2 = new Factor();
            factor2.deriveX(factor);
            factors.add(factor2);

            //乘以其他项
            cosMulOther(variable, factor, factors);
            //相乘加到原来的变量下
            countMul(factors);
        }
    }

    public void deriveYSin(Variable variable) {
        for (Factor factor : variable.getSin().keySet()) {
            Variable variableSin = new Variable();
            //该项求导并乘以常数项
            deriveSinExponent(variableSin, variable, factor);
            //求导Sin变Cos
            deriveSinToCos(variableSin, factor);
            ArrayList<Factor> factors = new ArrayList<>();
            Factor factor1 = new Factor();
            factor1.getVariables().add(variableSin);
            factors.add(factor1);

            //三角函数内求导
            Factor factor2 = new Factor();
            factor2.deriveY(factor);
            factors.add(factor2);

            //其他项算入factor3
            sinMulOther(variable, factor, factors);
            //相乘加到原来的目录下
            countMul(factors);
        }
    }

    public void deriveYCos(Variable variable) {
        //取每一个cos
        for (Factor factor : variable.getCos().keySet()) {
            Variable variableCos = new Variable();
            //该项求导并乘以常数项
            deriveCosExponent(variableCos, variable, factor);
            //求导cos变成sin
            deriveCosToSin(variableCos, factor);
            ArrayList<Factor> factors = new ArrayList<>();
            Factor factor1 = new Factor();
            factor1.getVariables().add(variableCos);
            factors.add(factor1);

            //三角函数内求导，得到一个有很多变量的因子
            Factor factor2 = new Factor();
            factor2.deriveY(factor);
            factors.add(factor2);

            //乘以其他项
            cosMulOther(variable, factor, factors);
            //相乘加到原来的变量下
            countMul(factors);
        }
    }

    public void deriveZSin(Variable variable) {
        for (Factor factor : variable.getSin().keySet()) {
            Variable variableSin = new Variable();
            //该项求导并乘以常数项
            deriveSinExponent(variableSin, variable, factor);
            //求导Sin变Cos
            deriveSinToCos(variableSin, factor);
            ArrayList<Factor> factors = new ArrayList<>();
            Factor factor1 = new Factor();
            factor1.getVariables().add(variableSin);
            factors.add(factor1);

            //三角函数内求导
            Factor factor2 = new Factor();
            factor2.deriveZ(factor);
            factors.add(factor2);

            //其他项算入factor3
            sinMulOther(variable, factor, factors);
            //相乘加到原来的目录下
            countMul(factors);
        }
    }

    public void deriveZCos(Variable variable) {
        //取每一个cos
        for (Factor factor : variable.getCos().keySet()) {
            Variable variableCos = new Variable();
            //该项求导并乘以常数项
            deriveCosExponent(variableCos, variable, factor);
            //求导cos变成sin
            deriveCosToSin(variableCos, factor);
            ArrayList<Factor> factors = new ArrayList<>();
            Factor factor1 = new Factor();
            factor1.getVariables().add(variableCos);
            factors.add(factor1);


            //三角函数内求导，得到一个有很多变量的因子
            Factor factor2 = new Factor();
            factor2.deriveZ(factor);
            factors.add(factor2);

            //乘以其他项
            cosMulOther(variable, factor, factors);
            //相乘加到原来的变量下
            countMul(factors);
        }
    }

    public void deriveSinExponent(Variable variableSin, Variable variable, Factor factor) {

        variableSin.setVariable(variable.readXExponent(), variable.readYExponent()
                , variable.readZExponent(),
                variable.getNumDigit().multiply(variable.getSin().get(factor)));
        variableSin.getSin().put(factor, variable.getSin().get(factor).add(BigInteger.valueOf(-1)));
    }

    public void deriveCosExponent(Variable variableCos, Variable variable, Factor factor) {
        variableCos.setVariable(variable.readXExponent(), variable.readYExponent()
                , variable.readZExponent(),
                variable.getNumDigit().multiply(variable.getCos().get(factor)));
        variableCos.getCos().put(factor, variable.getCos().get(factor).add(BigInteger.valueOf(-1)));
    }

    public void deriveSinToCos(Variable variableSin, Factor factor) {
        variableSin.getCos().put(factor, BigInteger.ONE);
    }

    public void deriveCosToSin(Variable variableCos, Factor factor) {
        variableCos.getSin().put(factor, BigInteger.ONE);
        variableCos.setVariable(variableCos.readXExponent(),
                variableCos.readYExponent(), variableCos.readZExponent(),
                variableCos.getNumDigit().multiply(BigInteger.valueOf(-1)));
    }

    public void sinMulOther(Variable variable, Factor factor, ArrayList<Factor> factors) { //乘以每一项
        for (Factor factor1 : variable.getSin().keySet()) {
            if (!factor1.equals(factor)) {
                Factor factorNew = new Factor();
                Variable variableNew = new Variable();
                variableNew.setVariable(BigInteger.ZERO,
                        BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE);
                variableNew.getSin().put(factor1, variable.getSin().get(factor1));
                factorNew.getVariables().add(variableNew);
                factors.add(factorNew);
            }
        }
        for (Factor factor2 : variable.getCos().keySet()) {
            Factor factorNew = new Factor();
            Variable variableNew = new Variable();
            variableNew.setVariable(BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE);
            variableNew.getCos().put(factor2, variable.getCos().get(factor2));
            factorNew.getVariables().add(variableNew);
            factors.add(factorNew);
        }
    }

    public void cosMulOther(Variable variable, Factor factor, ArrayList<Factor> factors) { //乘以每一项
        for (Factor factor1 : variable.getCos().keySet()) {
            if (!factor1.equals(factor)) {
                Factor factorNew = new Factor();
                Variable variableNew = new Variable();
                variableNew.setVariable(BigInteger.ZERO,
                        BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE);
                variableNew.getCos().put(factor1, variable.getCos().get(factor1));
                factorNew.getVariables().add(variableNew);
                factors.add(factorNew);
            }
        }
        for (Factor factor2 : variable.getSin().keySet()) {
            Factor factorNew = new Factor();
            Variable variableNew = new Variable();
            variableNew.setVariable(BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE);
            variableNew.getSin().put(factor2, variable.getSin().get(factor2));
            factorNew.getVariables().add(variableNew);
            factors.add(factorNew);
        }

    }

    public void countMul(ArrayList<Factor> factors) {

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
}
