package library;

import books.Book;
import requests.Request;
import requests.RequestList;
import users.Student;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.TreeMap;

public class Scheduler {

    private ArrayList<School> schools;

    private LocalDate startDate;
    private LocalDate today;
    private LocalDate endDate;

    private ArrayList<School> fromSchools;
    private ArrayList<School> toSchools;
    private ArrayList<Book> transportBooks;
    private ArrayList<Student> studentsReceiveBook;

    private TreeMap<LocalDate, RequestList> requestsOfEachDay;

    public Scheduler() {
        startDate = LocalDate.of(2023, 1, 1);
        today = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 1);
        schools = new ArrayList<>();

        fromSchools = new ArrayList<>();
        toSchools = new ArrayList<>();
        transportBooks = new ArrayList<>();
        studentsReceiveBook = new ArrayList<>();

        requestsOfEachDay = new TreeMap<>();
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
        School school = getSchool(request);
        school.addRequest(request);
    }

    public void deal() {
        long days = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        for (int i = 0; i < days; i++) {
            today = startDate.plusDays(i);
            //开馆前
            //运入校际借阅书籍
            for (int j = 0; j < toSchools.size(); j++) {
                toSchools.get(j).inBook(transportBooks.get(j));
            }
            //发放校际借阅书籍
            for (int j = 0; j < toSchools.size(); j++) {
                if (studentsReceiveBook.get(j) != null) {
                    Book book = transportBooks.get(j);
                    School fromSchool = fromSchools.get(j);
                    School toSchool = toSchools.get(j);
                    if (book.getSchoolName().equals(fromSchool.getName())) {
                        toSchool.distributeBook(book, studentsReceiveBook.get(j));
                    }
                }
            }
            fromSchools.clear();
            toSchools.clear();
            transportBooks.clear();
            studentsReceiveBook.clear();
            //如果是整理日(开馆前)
            if (i % 3 == 0) {
                //购入新书
                for (School school : schools) {
                    school.workPurchasingDepartment();
                }
                //整理图书
                System.out.println("[" + getToday() + "]"
                        + " arranging librarian arranged all the books");
                for (School school : schools) {
                    school.workArrangingLibrarian();
                }
                //处理预定
                for (School school : schools) {
                    school.workOrderingLibrarian();
                }
            }
            //开馆！
            //处理request
            if (requestsOfEachDay.containsKey(today)) {
                ArrayList<Request> requestList = requestsOfEachDay.get(today).getRequests();
                for (Request request : requestList) {
                    getSchool(request).dealRequest(request);
                }
            }
            //闭馆后
            //处理预定信息
            for (School school : schools) {
                school.dealUnprocessedRequests();
            }
            //运出书籍
            for (int j = 0; j < fromSchools.size(); j++) {
                fromSchools.get(j).outBook(transportBooks.get(j));
            }
        }
    }

    public void addSchool(School school) {
        schools.add(school);
    }

    public LocalDate getToday() {
        return this.today;
    }

    public School getSchool(Request request) {
        for (School school : schools) {
            if (school.getName().equals(request.getSchoolName())) {
                return school;
            }
        }
        return null;
    }

    public School getSchool(String schoolName) {
        for (School school : schools) {
            if (school.getName().equals(schoolName)) {
                return school;
            }
        }
        return null;
    }

    public School findBook(Book book) {
        for (School school : schools) {
            if (school.haveBookBorrowable(book)) {
                return school;
            }
        }
        return null;
    }

    public void addBooksForTransportation(School fromSchool,
                                          School toSchool, Book book, Student student) {
        fromSchools.add(fromSchool);
        toSchools.add(toSchool);
        transportBooks.add(book);
        studentsReceiveBook.add(student);
    }
}
