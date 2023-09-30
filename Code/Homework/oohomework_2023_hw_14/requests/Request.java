package requests;

import books.Book;
import library.Scheduler;
import users.Student;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private LocalDate date;
    private Student student;
    private String todo;
    private Book book;
    private String schoolName;
    private String requestStr = "\\[(?<year>\\d+)\\-(?<month>\\d+)\\-(?<day>\\d+)\\] " +
            "(?<schoolName>[A-Z]+)\\-(?<studentId>\\d+) " +
            "(?<todo>[a-zA-Z]+) (?<category>[ABC])\\-(?<serialNum>\\d+)";

    private Pattern pattern = Pattern.compile(requestStr);
    private Matcher matcher;

    public Request(String str, Scheduler scheduler) {
        matcher = pattern.matcher(str);
        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group("year"));
            int month = Integer.parseInt(matcher.group("month"));
            int day = Integer.parseInt(matcher.group("day"));
            date = LocalDate.of(year, month, day);

            this.schoolName = matcher.group("schoolName");
            String studentId = matcher.group("studentId");
            this.student = scheduler.getSchool(this).getStudent(studentId);

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
            this.book = new Book(schoolName, categoryNum, serialNum, false);
        }
    }

    public Student getStudent() {
        return student;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public LocalDate getDate() {
        return date;
    }

    public Book getNeedBook() {
        return book;
    }

    public Book getReturnedBook() {
        return student.getReturnBook(book.getCategoryNum(), book.getSerialNum());
    }

    public String getTodo() {
        return todo;
    }
}
