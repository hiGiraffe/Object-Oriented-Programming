package staffs;

import books.Book;
import library.School;
import requests.Request;

import java.util.HashMap;

public class LogisticsDivision extends Staff {

    public LogisticsDivision(HashMap<Book, Integer> shelf,
                             HashMap<Book, Integer> booksUnavailable, School school) {
        super(shelf, booksUnavailable, school);
    }

    public void work(Request request) {
        System.out.println("[" + getSchool().getToday() + "]"
                + " " + request.getReturnedBook().getSchoolName() + "-" + request.getReturnedBook()
                + " got repaired by logistics division in " + getSchool().getName());
        System.out.println("(State)[" + getToday() + "]" + request.getReturnedBook()
                + " transfers from Collect to Repair");
    }
}
