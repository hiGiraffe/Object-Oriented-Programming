package staffs;

import books.Book;
import library.Scheduler;
import requests.Request;
import users.Student;
import java.util.HashMap;

public class SelfServiceMachine {
    private HashMap<Book, Integer> shelf;
    private HashMap<Book, Integer> booksUnavailable;
    private Scheduler scheduler;

    public SelfServiceMachine(HashMap<Book, Integer> shelf,
                              HashMap<Book, Integer> booksUnavailable, Scheduler scheduler) {
        this.shelf = shelf;
        this.booksUnavailable = booksUnavailable;
        this.scheduler = scheduler;
    }

    public void work(Request request) {
        Student student = scheduler.getStudent(request.getStudentId());
        //若符合则成功借书
        if (student.canBorrowBookC(request.getBook())) {
            System.out.println("[" + scheduler.getToday() + "]" + " "
                    + request.getStudentId() + " borrowed " + request.getBook()
                    + " from self-service machine");
            shelf.put(request.getBook(), shelf.get(request.getBook()) - 1);
            student.borrowBookC(request.getBook());
            scheduler.flushBookC(student.getStudentId(), request.getBook());
        }
        //否则书变成unavailable
        else {
            shelf.put(request.getBook(), shelf.get(request.getBook()) - 1);
            booksUnavailable.put(request.getBook(), booksUnavailable.get(request.getBook()) + 1);
        }
    }

}
