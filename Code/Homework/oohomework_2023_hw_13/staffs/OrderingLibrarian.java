package staffs;

import books.Book;
import library.Scheduler;
import requests.Request;
import users.Student;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderingLibrarian extends Librarian {
    private ArrayList<Request> orderingRequest;

    public OrderingLibrarian(HashMap<Book, Integer> shelf,
                             HashMap<Book, Integer> booksUnavailable, Scheduler scheduler) {
        super(shelf, booksUnavailable, scheduler);
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
                if (getShelf().get(request.getBook()) > 0) {
                    //取消预定中的其他书
                    flag = 1;
                    studentId = request.getStudentId();
                    book = request.getBook();
                    categoryNum = book.getCategoryNum();
                    //取书成功
                    System.out.println("[" + getToday() + "]" + " " + request.getStudentId()
                            + " borrowed " + request.getBook() + " from ordering librarian");
                    getShelf().put(request.getBook(), getShelf().get(request.getBook()) - 1);
                    break;
                }
            }
            //清除已经约了的人
            if (flag == 0) {
                break;
            } else {
                switch (categoryNum) {
                    case 1:
                        flushBookB(studentId, book);
                        getScheduler().getStudent(studentId).borrowBookB(book);
                        break;
                    case 2:
                        flushBookC(studentId, book);
                        getScheduler().getStudent(studentId).borrowBookC(book);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    public void addRequest(Request request) {
        int categoryNum = request.getBook().getCategoryNum();
        String studentId = request.getStudentId();
        Student student = getScheduler().getStudent(studentId);
        Book book = request.getBook();
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
        System.out.println("[" + getToday() + "]" + " " + request.getStudentId()
                + " ordered " + request.getBook() + " from ordering librarian");
        this.orderingRequest.add(request);
        student.addOrderingBook(book, getToday());
    }

    //把所有B都去掉
    public void flushBookB(String studentId, Book book) {
        orderingRequest.removeIf(request ->
                request.getStudentId().equals(studentId)
                        && request.getBook().isB());
        getScheduler().getStudent(studentId).deleteOrderingBookB();
    }

    //把同一本C去掉
    public void flushBookC(String studentId, Book book) {
        orderingRequest.removeIf(request ->
                request.getStudentId().equals(studentId)
                        && request.getBook().equals(book));
        getScheduler().getStudent(studentId).deleteOrderingBookC(book);
    }
}
