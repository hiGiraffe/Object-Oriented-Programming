
public class Poly {

    //常数

    //HashMap<Integer, BigInteger> Sites = new HashMap<Integer, BigInteger>();//[x指数，y指数，z指数]-系数
    //HashMap<Integer, BigInteger> x = new HashMap<Integer, BigInteger>();
    //HashMap<Integer, BigInteger> y = new HashMap<Integer, BigInteger>();
    //HashMap<Integer, BigInteger> z = new HashMap<Integer, BigInteger>();
    //系数

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
}
