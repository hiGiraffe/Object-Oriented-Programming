package staffs;

import books.Book;
import library.School;
import requests.Request;
import users.Student;

import java.util.HashMap;

public class SelfServiceMachine extends Staff {
    public SelfServiceMachine(HashMap<Book, Integer> shelf,
                              HashMap<Book, Integer> booksUnavailable, School school) {
        super(shelf, booksUnavailable, school);
    }

    public void work(Request request) {
        Student student = getSchool().getStudent(request.getStudent().getStudentId());
        //若符合则成功借书
        if (student.canBorrowBookC(request.getNeedBook())) {
            System.out.println("[" + getToday() + "]"
                    + " self-service machine lent " + getSchool().getName()
                    + "-" + request.getNeedBook() + " to " + student);
            System.out.println("(State)[" + getToday() + "]" + request.getNeedBook()
                    + " transfers from NotBorrow to Borrow");
            System.out.println("[" + getToday() + "]" + " " + student
                    + " borrowed " + request.getNeedBook().getSchoolName()
                    + "-" + request.getNeedBook() + " from self-service machine");
            getShelf().put(request.getNeedBook(), getShelf().get(request.getNeedBook()) - 1);
            student.borrowBookC(request.getNeedBook(), getToday());
            getSchool().flushBookC(student.getStudentId(), request.getNeedBook());
        }
        //否则书变成unavailable
        else {
            getShelf().put(request.getNeedBook(), getShelf().get(request.getNeedBook()) - 1);
            getBooksUnavailable().put(request.getNeedBook()
                    , getBooksUnavailable().get(request.getNeedBook()) + 1);
            System.out.println("[" + getToday() + "]"
                    + " self-service machine refused lending " + getSchool().getName()
                    + "-" + request.getNeedBook() + " to " + request.getStudent());
            System.out.println("(State)[" + getToday() + "]" + request.getNeedBook()
                    + " transfers from NotBorrow to NotBorrow");
        }
        System.out.println("(Sequence)[" + getToday() + "] "
                + "SelfServiceMachine sends a message to Student");
    }

}
