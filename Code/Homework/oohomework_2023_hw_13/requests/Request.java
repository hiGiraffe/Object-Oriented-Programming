package requests;

import books.Book;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private LocalDate date;
    private String studentId;
    private String todo;
    private Book book;
    private String requestStr = "\\[(?<year>\\d+)\\-(?<month>\\d+)\\-(?<day>\\d+)\\] " +
            "(?<studentId>\\d+) (?<todo>[a-zA-Z]+) (?<category>[ABC])\\-(?<serialNum>\\d+)";

    private Pattern pattern = Pattern.compile(requestStr);
    private Matcher matcher;

    public Request(String str) {
        matcher = pattern.matcher(str);
        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group("year"));
            int month = Integer.parseInt(matcher.group("month"));
            int day = Integer.parseInt(matcher.group("day"));
            date = LocalDate.of(year, month, day);
            this.studentId = matcher.group("studentId");
            this.todo = matcher.group("todo");
            int categoryNum = 0;
            String serialNum;
            switch (matcher.group("category")) {
                case "A":
                    categoryNum = 0;
                    break;
                case "B":
                    categoryNum = 1;
                    break;
                case "C":
                    categoryNum = 2;
                    break;
                default:
                    break;
            }
            serialNum = matcher.group("serialNum");
            this.book = new Book(categoryNum, serialNum);
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Book getBook() {
        return book;
    }

    public String getTodo() {
        return todo;
    }
}
