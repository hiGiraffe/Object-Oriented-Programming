package staffs;

import books.Book;
import library.Scheduler;

import java.time.LocalDate;
import java.util.HashMap;

public abstract class Librarian {
    private HashMap<Book, Integer> shelf;
    private HashMap<Book, Integer> booksUnavailable;

    private Scheduler scheduler;

    public Librarian(HashMap<Book, Integer> shelf,
                     HashMap<Book, Integer> booksUnavailable, Scheduler scheduler) {
        this.shelf = shelf;
        this.booksUnavailable = booksUnavailable;
        this.scheduler = scheduler;
    }

    public HashMap<Book, Integer> getBooksUnavailable() {
        return booksUnavailable;
    }

    public HashMap<Book, Integer> getShelf() {
        return shelf;
    }

    public LocalDate getToday() {
        return scheduler.getToday();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
