import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final Pattern numberPattern = Pattern.compile("^\\d+"); // TODO
    private final HashMap<String, Integer> parameters;

    public Parser(HashMap<String, Integer> parameters) {
        this.parameters = parameters;
    }

    public Operator parse(String expression) {
        int position = findAddOrSub(expression);//找到加减号
        if (position != -1) { //如果有
            if (expression.charAt(position) == '+') { //加法
                return new Add(parse(expression.substring(0, position)),//读入term
                        parse(expression.substring(position + 1)));//继续读后面
            } else {
                return new Sub(parse(expression.substring(0, position)),
                        parse(expression.substring(position + 1)));
            }
        } else {
            position = findMul(expression);//乘法
            if (position != -1) { //如果有
                return new Mul(parse(expression.substring(0, position))
                        , parse(expression.substring(position + 1)));
            } else {
                if (!expression.equals("")) { //不为空
                    Matcher matcher = numberPattern.matcher(expression);
                    //带入未知数
                    if (matcher.find()) {
                        return new Num(Integer.parseInt(matcher.group(0)));
                    } else {
                        return new Num(parameters.get(expression));
                    }
                    // TODO

                } else { //为空
                    return new Num(0);
                }
            }
        }
    }

    private int findAddOrSub(String expression) {
        int position = -1;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '+' || expression.charAt(i) == '-') {
                position = i;
            }
        }
        return position;
    }

    private int findMul(String expression) {
        int position = -1;

        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '*') {
                position = i;
            }
        }

        return position;
    }
}
