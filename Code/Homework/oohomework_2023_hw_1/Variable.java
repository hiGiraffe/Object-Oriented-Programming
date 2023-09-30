import java.math.BigInteger;
import java.util.Objects;

public class Variable {

    //指数
    private int x1Exponent;
    private int y1Exponent;
    private int z1Exponent;

    private BigInteger numDigit;

    @Override
    public boolean equals(Object o) {
        Variable variable = (Variable) o;
        if (o instanceof Variable) {
            if (this.x1Exponent == variable.x1Exponent
                    && this.y1Exponent == variable.y1Exponent
                    && this.z1Exponent == variable.z1Exponent) {
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

    public int readXExponent() {
        return this.x1Exponent;
    }

    public int readYExponent() {
        return this.y1Exponent;
    }

    public int readZExponent() {
        return this.z1Exponent;
    }

    public BigInteger getNumDigit() {
        return numDigit;
    }

    public void setVariable(int x1Exponent, int y1Exponent, int z1Exponent, BigInteger numDigit) {
        this.x1Exponent = x1Exponent;
        this.y1Exponent = y1Exponent;
        this.z1Exponent = z1Exponent;
        this.numDigit = numDigit;
    }

}
