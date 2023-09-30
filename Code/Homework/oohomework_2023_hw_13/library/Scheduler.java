package library;

import books.Book;
import requests.Request;
import requests.RequestList;
import staffs.ArrangingLibrarian;
import staffs.BorrowingAndReturningLibrarian;
import staffs.LogisticsDivision;
import staffs.OrderingLibrarian;
import staffs.SelfServiceMachine;
import users.Student;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scheduler {
    private HashMap<Book, Integer> shelf;
    private HashMap<Book, Integer> booksUnavailable;
    private ArrayList<Student> students;
    private TreeMap<LocalDate, RequestList> requestsOfEachDay;
    private ArrangingLibrarian arrangingLibrarian;
    private BorrowingAndReturningLibrarian borrowingAndReturningLibrarian;
    private OrderingLibrarian orderingLibrarian;
    private SelfServiceMachine selfServiceMachine;
    private LogisticsDivision logisticsDivision;

    private LocalDate startDate;
    private LocalDate today;
    private LocalDate endDate;

    private Pattern pattern;
    private Matcher matcher;
    private String bookFactor = "(?<category>[ABC])\\-(?<serialNum>\\d+) (?<duplicateNum>\\d+)";

    public Scheduler() {
        shelf = new HashMap<>();
        booksUnavailable = new HashMap<>();
        students = new ArrayList<>();
        requestsOfEachDay = new TreeMap<>();
        startDate = LocalDate.of(2023, 1, 1);
        today = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 1);

        arrangingLibrarian = new ArrangingLibrarian(shelf, booksUnavailable, this);
        borrowingAndReturningLibrarian
                = new BorrowingAndReturningLibrarian(shelf, booksUnavailable, this);
        orderingLibrarian = new OrderingLibrarian(shelf, booksUnavailable, this);
        selfServiceMachine = new SelfServiceMachine(shelf, booksUnavailable, this);
        logisticsDivision = new LogisticsDivision(shelf, booksUnavailable, this);
    }

    public void addBook(String str) {
        pattern = Pattern.compile(bookFactor);
        matcher = pattern.matcher(str);
        int categoryNum = 0;
        String serialNum;
        int duplicateNum;
        if (matcher.matches()) {
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
            duplicateNum = Integer.parseInt(matcher.group("duplicateNum"));
            Book book = new Book(categoryNum, serialNum);
            shelf.put(book, duplicateNum);
            booksUnavailable.put(book, 0);
        }
    }

    public void addRequest(Request request) {
        if (requestsOfEachDay.containsKey(request.getDate())) {
            requestsOfEachDay.get(request.getDate()).addRequest(request);
        } else {
            RequestList requestList = new RequestList();
            requestList.addRequest(request);
            requestsOfEachDay.put(request.getDate(), requestList);
            if (request.getDate().isAfter(endDate)) {
                endDate = request.getDate();
            }
        }
    }

    public void deal() {
        long days = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        for (int i = 0; i < days; i++) {
            today = startDate.plusDays(i);

            //如果今天需要整理
            if (i % 3 == 0) {
                arrangingLibrarian.work();
            }
            //处理预定
            orderingLibrarian.work();
            //处理request
            if (requestsOfEachDay.containsKey(today)) {
                ArrayList<Request> requestList = requestsOfEachDay.get(today).getRequests();
                for (Request request : requestList) {
                    switch (request.getTodo()) {
                        case "borrowed":
                            borrowed(request);
                            break;
                        case "smeared":
                            smeared(request);
                            break;
                        case "lost":
                            lost(request);
                            break;
                        case "returned":
                            returned(request);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void borrowed(Request request) {
        Student student = getStudent(request.getStudentId());
        //咨询
        System.out.println("[" + today + "] " + request.getStudentId()
                + " queried " + request.getBook() + " from self-service machine");
        //借书
        if (shelf.get(request.getBook()) > 0) {
            switch (request.getBook().getCategoryNum()) {
                case 1: //B
                    borrowingAndReturningLibrarian.work(request);
                    break;
                case 2: //C
                    selfServiceMachine.work(request);
                    break;
                default:
                    break;
            }
        } else {
            orderingLibrarian.addRequest(request);
        }
    }

    public void smeared(Request request) {
        //记录 还书时查出
        Student student = getStudent(request.getStudentId());
        student.smearedBook(request.getBook());
    }

    public void lost(Request request) {
        Student student = getStudent(request.getStudentId());
        //立刻登记并罚款
        System.out.println("[" + today + "]" + " " + request.getStudentId()
                + " got punished by borrowing and returning librarian");
        if (request.getBook().isB()) {
            student.returnBookB(request.getBook());
        } else {
            student.returnBooksC(request.getBook());
        }
    }

    public void returned(Request request) {
        Student student = getStudent(request.getStudentId());
        if (request.getBook().isB()) {
            if (student.isSmearedBookB()) {
                System.out.println("[" + today + "]" + " " + request.getStudentId()
                        + " got punished by borrowing and returning librarian");
            }
            booksUnavailable.put(request.getBook(), booksUnavailable.get(request.getBook()) + 1);
            System.out.println("[" + today + "]" + " " + request.getStudentId()
                    + " returned " + request.getBook() + " to borrowing and returning librarian");
            if (student.isSmearedBookB()) {
                logisticsDivision.work(request);
            }
            student.returnBookB(request.getBook());
        } else {
            if (student.isSmearedBookC(request.getBook())) {
                System.out.println("[" + today + "]" + " " + request.getStudentId()
                        + " got punished by borrowing and returning librarian");
            }
            booksUnavailable.put(request.getBook(), booksUnavailable.get(request.getBook()) + 1);
            System.out.println("[" + today + "]" + " " + request.getStudentId()
                    + " returned " + request.getBook() + " to self-service machine");
            if (student.isSmearedBookC(request.getBook())) {
                logisticsDivision.work(request);
            }
            student.returnBooksC(request.getBook());
        }
    }

    public Student getStudent(String id) {
        for (Student student : students) {
            if (student.getStudentId().equals(id)) {
                return student;
            }
        }
        Student student = new Student(id);
        students.add(student);
        return student;
    }

    public void flushBookB(String studentId, Book book) {
        orderingLibrarian.flushBookB(studentId, book);
    }

    public void flushBookC(String studentId, Book book) {
        orderingLibrarian.flushBookC(studentId, book);
    }

    public LocalDate getToday() {
        return this.today;
    }
}
