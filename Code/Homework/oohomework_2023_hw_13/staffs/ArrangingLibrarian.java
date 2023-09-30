package staffs;

import books.Book;
import library.Scheduler;
import java.util.HashMap;

public class ArrangingLibrarian extends Librarian {

    public ArrangingLibrarian(HashMap<Book, Integer> shelf,
                              HashMap<Book, Integer> booksUnavailable, Scheduler scheduler) {
        super(shelf, booksUnavailable, scheduler);
    }

    public void work() {
        for (Book book : this.getBooksUnavailable().keySet()) {
            if (this.getBooksUnavailable().get(book) > 0) {
                this.getShelf().put(book,
                        this.getShelf().get(book) + this.getBooksUnavailable().get(book));
                this.getBooksUnavailable().put(book, 0);
            }
        }
    }
}
