package staffs;

import books.Book;
import library.Scheduler;
import requests.Request;
import users.Student;
import java.util.HashMap;

public class BorrowingAndReturningLibrarian extends Librarian {
    public BorrowingAndReturningLibrarian(HashMap<Book, Integer> shelf,
                                          HashMap<Book, Integer> booksUnavailable,
                                          Scheduler scheduler) {
        super(shelf, booksUnavailable, scheduler);
    }

    public void work(Request request) {
        Student student = getScheduler().getStudent(request.getStudentId());
        //若符合则成功借书
        if (student.canBorrowBookB()) {
            System.out.println("[" + getToday() + "]" + " " + request.getStudentId()
                    + " borrowed " + request.getBook() + " from borrowing and returning librarian");
            getShelf().put(request.getBook(), getShelf().get(request.getBook()) - 1);
            student.borrowBookB(request.getBook());
            getScheduler().flushBookB(student.getStudentId(), request.getBook());
        }
        //否则书变成unavailable
        else {
            getShelf().put(request.getBook(), getShelf().get(request.getBook()) - 1);
            getBooksUnavailable().put(request.getBook(),
                    getBooksUnavailable().get(request.getBook()) + 1);
        }
    }
}
