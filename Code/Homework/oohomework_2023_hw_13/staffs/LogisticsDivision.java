package staffs;

import books.Book;
import library.Scheduler;
import requests.Request;
import java.util.HashMap;

public class LogisticsDivision {
    private HashMap<Book, Integer> shelf;
    private HashMap<Book, Integer> booksUnavailable;
    private Scheduler scheduler;

    public LogisticsDivision(HashMap<Book, Integer> shelf,
                             HashMap<Book, Integer> booksUnavailable, Scheduler scheduler) {
        this.shelf = shelf;
        this.booksUnavailable = booksUnavailable;
        this.scheduler = scheduler;
    }

    public void work(Request request) {
        System.out.println("[" + scheduler.getToday() + "]"
                + " " + request.getBook() + " got repaired by logistics division");
    }
}
