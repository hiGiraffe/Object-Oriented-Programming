import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Variable {

    //指数
    private BigInteger x1Exponent;
    private BigInteger y1Exponent;
    private BigInteger z1Exponent;

    private BigInteger numDigit;

    private HashMap<Factor, BigInteger> sin = new HashMap<>();
    private HashMap<Factor, BigInteger> cos = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        Variable variable = (Variable) o;
        if (o instanceof Variable) {
            if (this.x1Exponent == variable.x1Exponent
                    && this.y1Exponent == variable.y1Exponent
                    && this.z1Exponent == variable.z1Exponent
                    && this.sin.equals(variable.getSin())
                    && this.cos.equals(variable.getCos())
            ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1Exponent, y1Exponent, z1Exponent);
    }

    public BigInteger readXExponent() {
        return this.x1Exponent;
    }

    public BigInteger readYExponent() {
        return this.y1Exponent;
    }

    public BigInteger readZExponent() {
        return this.z1Exponent;
    }

    public BigInteger getNumDigit() {
        return this.numDigit;
    }

    public void setSin(HashMap<Factor, BigInteger> sin) {
        this.sin = sin;
    }

    public void setSin(Factor factor, BigInteger exponent) {
        sin.put(factor, exponent);
    }

    public void setCos(HashMap<Factor, BigInteger> cos) {
        this.cos = cos;
    }

    public void setCos(Factor factor, BigInteger exponent) {
        cos.put(factor, exponent);
    }

    //很多个sin和cos 如果有两个相等 相乘就直接系数相加，其他不变。 否则直接sin加入新数组，cos加入新数组
    public void mulSin(HashMap<Factor, BigInteger> sin1, HashMap<Factor, BigInteger> sin2) {
        for (Factor factor1 : sin1.keySet()) {
            if (this.sin.containsKey(factor1)) {
                this.sin.put(factor1, this.sin.get(factor1).add(sin1.get(factor1)));
            } else {
                this.sin.put(factor1, sin1.get(factor1));
            }
        }
        for (Factor factor2 : sin2.keySet()) {
            if (this.sin.containsKey(factor2)) {
                this.sin.put(factor2, this.sin.get(factor2).add(sin2.get(factor2)));
            } else {
                this.sin.put(factor2, sin2.get(factor2));
            }
        }
    }

    public void mulCos(HashMap<Factor, BigInteger> cos1, HashMap<Factor, BigInteger> cos2) {
        for (Factor factor1 : cos1.keySet()) {
            if (this.cos.containsKey(factor1)) {
                this.cos.put(factor1, this.cos.get(factor1).add(cos1.get(factor1)));
            } else {
                this.cos.put(factor1, cos1.get(factor1));
            }
        }
        for (Factor factor2 : cos2.keySet()) {
            if (this.cos.containsKey(factor2)) {
                this.cos.put(factor2, this.cos.get(factor2).add(cos2.get(factor2)));
            } else {
                this.cos.put(factor2, cos2.get(factor2));
            }
        }
    }

    public HashMap<Factor, BigInteger> getSin() {
        return this.sin;
    }

    public HashMap<Factor, BigInteger> getCos() {
        return this.cos;
    }

    public void setVariable(BigInteger x1Exponent, BigInteger y1Exponent,
                            BigInteger z1Exponent, BigInteger numDigit) {
        this.x1Exponent = x1Exponent;
        this.y1Exponent = y1Exponent;
        this.z1Exponent = z1Exponent;
        this.numDigit = numDigit;
    }

}
