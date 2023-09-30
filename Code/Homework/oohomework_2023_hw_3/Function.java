import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
    private HashMap<Character, String> functions = new HashMap<>();
    private Pattern pp;
    private Matcher mm;
    private String function = "(?<function>[fgh]\\((?<a>.*?)(,(?<b>.*?))?(,(?<c>.*?))?\\)=)" +
            "(?<expr>.*)";

    public void addFunction(String str) {
        pp = Pattern.compile(function);
        mm = pp.matcher(str);
        mm.matches();
        StringBuilder strTmp = new StringBuilder();
        strTmp.append(mm.group("function"));
        strTmp.append(replaceFunction(mm.group("expr")));
        String strNext = derive(strTmp.toString());
        pp = Pattern.compile(function);
        mm = pp.matcher(strNext);
        mm.matches();
        if (mm.matches()) {
            strNext = strNext.replaceAll(mm.group("a"), "p");
            if (mm.group("b") != null) {
                strNext = strNext.replaceAll(mm.group("b"), "l");
            }
            if (mm.group("c") != null) {
                strNext = strNext.replaceAll(mm.group("c"), "m");
            }
        }
        mm = pp.matcher(strNext);
        mm.matches();
        this.functions.put(strNext.charAt(0), mm.group("expr"));
    }

    public String derive(String str) {
        Expr expr = new Expr();
        char deriveStyle;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 'd') {
                deriveStyle = str.charAt(i + 1);
                i += 3;
                int indexFirst = i;
                int numBacket = 1;

                while (i < str.length() && numBacket > 0) {
                    if (str.charAt(i) == ')') {
                        numBacket--;
                    } else if (str.charAt(i) == '(') {
                        numBacket++;
                    }
                    if (numBacket != 0) {
                        i++;
                    }
                }
                expr.derive(str.substring(indexFirst, i), deriveStyle).print();
                return str.replace(str.substring(indexFirst - 3, i + 1),
                        "(" + expr.ansString().toString() + ")");
            }
        }
        return str;
    }

    public String replaceFunction(String str) {
        String strNext = str;
        int flag = 0;
        ArrayList<String> rps = new ArrayList<>();
        int indexFunction = 0;//头
        int indexEnd = 0;//尾
        for (int i = 0; i < strNext.length(); i++) {
            if (strNext.charAt(i) == 'f' || strNext.charAt(i) == 'g' || strNext.charAt(i) == 'h') {
                indexFunction = i;
                i += 2;
                int indexFirst = i;
                while (strNext.charAt(i) != ')') {
                    if (strNext.charAt(i) == '(') {
                        int numBacket = 1;
                        i++;
                        while (numBacket > 0) {
                            if (strNext.charAt(i) == '(') {
                                numBacket++;
                            } else if (strNext.charAt(i) == ')') {
                                numBacket--;
                            }
                            if (numBacket != 0) {
                                i++;
                            }

                        }
                    } else if (strNext.charAt(i) == ',') {
                        rps.add("(" + strNext.substring(indexFirst, i) + ")");
                        indexFirst = i + 1;
                    }
                    i++;
                }
                indexEnd = i + 1;
                rps.add("(" + strNext.substring(indexFirst, i) + ")");
                flag = 1;
                break;
            }
        }


        if (flag == 0) {
            return derive(strNext);
        } else {
            //先代入f
            String tmp = functions.get(strNext.charAt(indexFunction));

            tmp = tmp.replaceAll("p", rps.get(0));
            if (rps.size() >= 2) {
                tmp = tmp.replaceAll("l", rps.get(1));
            }
            if (rps.size() >= 3) {
                tmp = tmp.replaceAll("m", rps.get(2));
            }
            //后替换
            strNext = strNext.replace(strNext.substring(indexFunction, indexEnd), "(" + tmp + ")");
            return derive(replaceFunction(strNext));
        }
    }
}
