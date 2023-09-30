package staffs;

import books.Book;
import library.School;
import requests.Request;
import users.Student;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderingLibrarian extends Staff {
    private ArrayList<Request> orderingRequest;

    public OrderingLibrarian(HashMap<Book, Integer> shelf,
                             HashMap<Book, Integer> booksUnavailable, School school) {
        super(shelf, booksUnavailable, school);
        orderingRequest = new ArrayList<>();
    }

    public void work() {
        while (!orderingRequest.isEmpty()) {
            int flag = 0;
            String studentId = " ";
            int categoryNum = 0;
            Book book = null;
            for (Request request : orderingRequest) {
                //有书
                if (getShelf().get(request.getNeedBook()) > 0) {
                    flag = 1;
                    studentId = request.getStudent().getStudentId();
                    book = request.getNeedBook();
                    categoryNum = book.getCategoryNum();
                    //取书成功
                    System.out.println("[" + getToday() + "]"
                            + " ordering librarian lent " + getSchool().getName()
                            + "-" + request.getNeedBook() + " to " + request.getStudent());
                    System.out.println("(State)[" + getToday() + "]" + request.getNeedBook()
                            + " transfers from NotBorrow to Borrow");
                    System.out.println("[" + getToday() + "]" + " " + request.getStudent()
                            + " borrowed " + getSchool().getName() + "-"
                            + request.getNeedBook() + " from ordering librarian");
                    getShelf().put(request.getNeedBook(),
                            getShelf().get(request.getNeedBook()) - 1);
                    break;
                }
            }
            //取书成功后取消其他预定
            if (flag == 0) {
                break;
            } else {
                switch (categoryNum) {
                    case 1:
                        flushBookB(studentId, book);
                        getSchool().getStudent(studentId).borrowBookB(book);
                        break;
                    case 2:
                        flushBookC(studentId, book);
                        getSchool().getStudent(studentId).borrowBookC(book);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void addRequest(Request request) {
        int categoryNum = request.getNeedBook().getCategoryNum();
        String studentId = request.getStudent().getStudentId();
        Student student = getSchool().getStudent(studentId);
        Book book = request.getNeedBook();
        //有B类书 不让借
        //有C类同一本书 不让借
        switch (categoryNum) {
            case 1:
                if (!student.canBorrowBookB()) {
                    return;
                }
                break;
            case 2:
                if (!student.canBorrowBookC(book)) {
                    return;
                }
                break;
            default:
                break;
        }
        //不是第一次预定同一本书，不让借
        if (student.hadOrderingBook(book)) {
            return;
        }
        //同一天内预定了多于三本
        if (student.hadOrderThreeBooksOneDay(getToday())) {
            return;
        }
        System.out.println("[" + getToday() + "]" + " " + request.getStudent()
                + " ordered " + getSchool().getName()
                + "-" + request.getNeedBook() + " from ordering librarian");
        System.out.println("[" + getToday() + "]"
                + " ordering librarian recorded " + request.getStudent()
                + "'s order of " + getSchool().getName() + "-" + request.getNeedBook());
        this.orderingRequest.add(request);
        student.addOrderingBook(book, getToday());
    }

    //把所有B都去掉
    public void flushBookB(String studentId, Book book) {
        orderingRequest.removeIf(request ->
                request.getStudent().getStudentId().equals(studentId)
                        && request.getNeedBook().isB());
        getSchool().getStudent(studentId).deleteOrderingBookB();
    }

    //把同一本C去掉
    public void flushBookC(String studentId, Book book) {
        orderingRequest.removeIf(request ->
                request.getStudent().getStudentId().equals(studentId)
                        && request.getNeedBook().equals(book));
        getSchool().getStudent(studentId).deleteOrderingBookC(book);
    }
}
