package library;

import books.Book;
import requests.Request;
import requests.RequestList;
import staffs.ArrangingLibrarian;
import staffs.BorrowingAndReturningLibrarian;
import staffs.LogisticsDivision;
import staffs.OrderingLibrarian;
import staffs.PurchasingDepartment;
import staffs.SelfServiceMachine;
import users.Student;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class School {

    private String name;
    private Integer bookNum;
    private HashMap<Book, Integer> shelf;
    private HashMap<Book, Integer> booksUnavailable;
    private HashMap<Book, Boolean> books;
    private ArrayList<Request> unprocessedRequests;
    private ArrayList<Student> students;
    private ArrangingLibrarian arrangingLibrarian;
    private BorrowingAndReturningLibrarian borrowingAndReturningLibrarian;
    private OrderingLibrarian orderingLibrarian;
    private SelfServiceMachine selfServiceMachine;
    private LogisticsDivision logisticsDivision;
    private PurchasingDepartment purchasingDepartment;

    private TreeMap<LocalDate, RequestList> requestsOfEachDay;

    private Pattern pattern;
    private Matcher matcher;
    private String bookFactor = "(?<category>[ABC])\\-(?<serialNum>\\d+)" +
            " (?<duplicateNum>\\d+) (?<canIntercollegiateBorrow>[YN])";
    private String schoolFactor = "(?<name>[A-Z]+) (?<bookNum>\\d+)";

    private Scheduler scheduler;

    public School(String str, Scheduler scheduler) {
        pattern = Pattern.compile(schoolFactor);
        matcher = pattern.matcher(str);
        if (matcher.matches()) {
            name = matcher.group("name");
            bookNum = Integer.parseInt(matcher.group("bookNum"));
        }
        shelf = new HashMap<>();
        booksUnavailable = new HashMap<>();
        books = new HashMap<>();
        unprocessedRequests = new ArrayList<>();
        students = new ArrayList<>();

        arrangingLibrarian = new ArrangingLibrarian(shelf, booksUnavailable, this);
        borrowingAndReturningLibrarian
                = new BorrowingAndReturningLibrarian(shelf, booksUnavailable, this);
        orderingLibrarian = new OrderingLibrarian(shelf, booksUnavailable, this);
        selfServiceMachine = new SelfServiceMachine(shelf, booksUnavailable, this);
        logisticsDivision = new LogisticsDivision(shelf, booksUnavailable, this);
        purchasingDepartment = new PurchasingDepartment(shelf, booksUnavailable, this);

        requestsOfEachDay = new TreeMap<>();
        this.scheduler = scheduler;
    }

    public void addRequest(Request request) {
        if (requestsOfEachDay.containsKey(request.getDate())) {
            requestsOfEachDay.get(request.getDate()).addRequest(request);
        } else {
            RequestList requestList = new RequestList();
            requestList.addRequest(request);
            requestsOfEachDay.put(request.getDate(), requestList);
        }
    }

    public LocalDate getToday() {
        return scheduler.getToday();
    }

    public Integer getBookNum() {
        return bookNum;
    }

    public void addBook(String str) {
        pattern = Pattern.compile(bookFactor);
        matcher = pattern.matcher(str);
        int categoryNum = 0;
        String serialNum;
        int duplicateNum;
        boolean canIntercollegiateBorrow = false;
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
            switch (matcher.group("canIntercollegiateBorrow")) {
                case "Y":
                    canIntercollegiateBorrow = true;
                    break;
                case "N":
                    canIntercollegiateBorrow = false;
                    break;
                default:
                    break;
            }
            Book book = new Book(this.getName(), categoryNum, serialNum, canIntercollegiateBorrow);
            shelf.put(book, duplicateNum);
            booksUnavailable.put(book, 0);
            books.put(book, canIntercollegiateBorrow);
        }
    }

    public void purchaseBook(Book book, int num) {
        booksUnavailable.put(book, num);
        shelf.put(book, 0);
        books.put(book, true);
    }

    public void workArrangingLibrarian() {
        arrangingLibrarian.work();
    }

    public void workOrderingLibrarian() {
        orderingLibrarian.work();
    }

    public void workPurchasingDepartment() {
        purchasingDepartment.work();
    }

    public void dealRequest(Request request) {
        //学校处理Request
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

    public void dealUnprocessedRequests() {
        for (Request request : unprocessedRequests) {
            Book book = request.getNeedBook();
            School school = scheduler.findBook(book);
            //其他学校有
            if (school != null) {
                Book needBook = new Book(school.getName(), book.getCategoryNum(),
                        book.getSerialNum(), book.isCanIntercollegiateBorrow());
                int categoryNum = book.getCategoryNum();
                Student student = request.getStudent();
                //有B类书 不让借
                //有C类同一本书 不让借
                switch (categoryNum) {
                    case 1:
                        if (!student.canBorrowBookB()) {
                            continue;
                        } else {
                            student.borrowBookB(needBook, getToday());
                            flushBookB(student.getStudentId(), needBook);
                        }
                        break;
                    case 2:
                        if (!student.canBorrowBookC(book)) {
                            continue;
                        } else {
                            student.borrowBookC(needBook, getToday());
                            flushBookC(student.getStudentId(), needBook);
                        }
                        break;
                    default:
                        break;
                }
                scheduler.addBooksForTransportation(school, this, needBook, request.getStudent());
            } else {
                orderingLibrarian.addRequest(request);
                //本校是否有馆藏
                if (shelf.containsKey(book) && booksUnavailable.containsKey(book)) {
                    continue;
                }
                //买新书
                else {
                    purchasingDepartment.addPurchaseBook(book);
                }
            }

        }
        unprocessedRequests.clear();
    }

    public void borrowed(Request request) {
        Student student = getStudent(request.getStudent().getStudentId());
        Book book = request.getNeedBook();
        //咨询
        System.out.println("[" + getToday() + "] " + student
                + " queried " + book + " from self-service machine");
        System.out.println("(Sequence)[" + getToday() + "] "
                + "Student sends a message to SelfServiceMachine");
        System.out.println("[" + getToday()
                + "] self-service machine provided information of " + book);
        System.out.println("(Sequence)[" + getToday() + "] "
                + "SelfServiceMachine sends a message to Student");
        //借书
        if (shelf.containsKey(book) && shelf.get(book) > 0) {
            switch (book.getCategoryNum()) {
                case 1: //B
                    System.out.println("(Sequence)[" + getToday() + "] "
                            + "Student sends a message to BorrowingAndReturningLibrarian");
                    borrowingAndReturningLibrarian.work(request);
                    break;
                case 2: //C
                    System.out.println("(Sequence)[" + getToday() + "] "
                            + "Student sends a message to SelfServiceMachine");
                    selfServiceMachine.work(request);
                    break;
                default:
                    break;
            }
        }
        //如果无余本，存起来晚上处理
        else {
            System.out.println("(Sequence)[" + getToday() + "] "
                    + "Student sends a message to OrderingLibrarian");
            unprocessedRequests.add(request);
        }
    }

    public void smeared(Request request) {
        //记录 还书时查出
        Student student = getStudent(request.getStudent().getStudentId());
        student.smearedBook(request.getReturnedBook());
    }

    public void lost(Request request) {
        Student student = getStudent(request.getStudent().getStudentId());
        //立刻登记并罚款
        System.out.println("[" + getToday() + "]" + " " + student
                + " got punished by borrowing and returning librarian");
        System.out.println("[" + getToday()
                + "]" + " borrowing and returning librarian received " + student
                + "'s fine");

        if (request.getReturnedBook().isB()) {
            student.returnBookB(request.getReturnedBook());
        } else {
            student.returnBooksC(request.getReturnedBook());
        }
    }

    public void returned(Request request) {
        Student student = getStudent(request.getStudent().getStudentId());
        if (request.getReturnedBook().isB()) {
            returnBookB(student, request);
        } else {
            returnBookC(student, request);
        }
    }

    public void returnBookB(Student student, Request request) {
        if (student.isSmearedBookB() || ChronoUnit.DAYS.between(
                request.getReturnedBook().getTimeLoanService(), getToday()) > 30) {
            System.out.println("[" + getToday() + "]" + " " + student
                    + " got punished by borrowing and returning librarian");
            System.out.println("[" + getToday() + "]"
                    + " borrowing and returning librarian received " + student
                    + "'s fine");
        }
        System.out.println("[" + getToday() + "]" + " " + student
                + " returned " + request.getReturnedBook().getSchoolName()
                + "-" + request.getReturnedBook() + " to borrowing and returning librarian");
        System.out.println("[" + getToday() + "]"
                + " borrowing and returning librarian collected "
                + request.getReturnedBook().getSchoolName() + "-"
                + request.getReturnedBook() + " from " + student);
        System.out.println("(State)[" + getToday() + "]" + request.getReturnedBook()
                + " transfers from Borrow to Collect");
        if (student.isSmearedBookB()) {
            logisticsDivision.work(request);
        }
        if (request.getReturnedBook().getSchoolName().equals(getName())) {
            booksUnavailable.put(request.getReturnedBook(),
                    booksUnavailable.get(request.getReturnedBook()) + 1);
        } else {
            scheduler.addBooksForTransportation(this,
                    scheduler.getSchool(request.getReturnedBook().getSchoolName()),
                    request.getReturnedBook(), null);
        }
        student.returnBookB(request.getReturnedBook());
    }

    public void returnBookC(Student student, Request request) {
        if (student.isSmearedBookC(request.getReturnedBook()) || ChronoUnit.DAYS.between(
                request.getReturnedBook().getTimeLoanService(), getToday()) > 60) {
            System.out.println("[" + getToday() + "]" + " " + student
                    + " got punished by borrowing and returning librarian");
            System.out.println("[" + getToday() + "]" + " borrowing and returning " +
                    "librarian received " + student + "'s fine");
        }
        System.out.println("[" + getToday() + "]" + " " + student
                + " returned " + request.getReturnedBook().getSchoolName()
                + "-" + request.getReturnedBook() + " to self-service machine");
        System.out.println("[" + getToday() + "]" + " self-service machine collected "
                + request.getReturnedBook().getSchoolName() + "-" + request.getReturnedBook() +
                " from " + student);
        System.out.println("(State)[" + getToday() + "]" + request.getReturnedBook()
                + " transfers from Borrow to Collect");
        if (student.isSmearedBookC(request.getReturnedBook())) {
            logisticsDivision.work(request);
        }
        if (request.getReturnedBook().getSchoolName().equals(getName())) {
            booksUnavailable.put(request.getReturnedBook(),
                    booksUnavailable.get(request.getReturnedBook()) + 1);
        } else {
            scheduler.addBooksForTransportation(this,
                    scheduler.getSchool(request.getReturnedBook().getSchoolName()),
                    request.getReturnedBook(), null);
        }
        student.returnBooksC(request.getReturnedBook());
    }

    public Student getStudent(String id) {
        for (Student student : students) {
            if (student.getStudentId().equals(id)) {
                return student;
            }
        }
        Student student = new Student(this.getName(), id);
        students.add(student);
        return student;
    }

    public void flushBookB(String studentId, Book book) {
        orderingLibrarian.flushBookB(studentId, book);
    }

    public void flushBookC(String studentId, Book book) {
        orderingLibrarian.flushBookC(studentId, book);
    }

    public String getName() {
        return name;
    }

    public boolean haveBookBorrowable(Book tmp) {
        Book book = new Book(getName(), tmp.getCategoryNum(),
                tmp.getSerialNum(), tmp.isCanIntercollegiateBorrow());
        return shelf.containsKey(book) && shelf.get(book) > 0 && books.get(book);
    }

    public void outBook(Book book) {
        if (book.getSchoolName().equals(this.getName())) { //是借出去的
            shelf.put(book, shelf.get(book) - 1);
        }
        System.out.println("[" + getToday() + "] " + book.getSchoolName()
                + "-" + book + " got transported by purchasing department in " + getName());
        System.out.println("(State)[" + getToday() + "]" + book
                + " transfers from NotBorrow to Transport");
    }

    public void inBook(Book book) {
        if (book.getSchoolName().equals(this.getName())) {
            booksUnavailable.put(book, booksUnavailable.get(book) + 1);
        }
        System.out.println("[" + getToday() + "] " + book.getSchoolName()
                + "-" + book + " got received by purchasing department in " + getName());
        System.out.println("(State)[" + getToday() + "]" + book
                + " transfers from Transport to NotBorrow");
    }

    public void distributeBook(Book book, Student student) {
        System.out.println("[" + getToday() + "]"
                + " purchasing department lent " + book.getSchoolName()
                + "-" + book + " to " + student);
        System.out.println("(State)[" + getToday() + "]" + book
                + " transfers from NotBorrow to Borrow");
        System.out.println("[" + getToday() + "]" + " " + student
                + " borrowed " + book.getSchoolName() + "-" + book + " from purchasing department");
    }

    public void orderNewBook() {

    }
}
