package staffs;

import books.Book;
import library.School;

import java.util.HashMap;

public class ArrangingLibrarian extends Staff {

    public ArrangingLibrarian(HashMap<Book, Integer> shelf,
                              HashMap<Book, Integer> booksUnavailable, School school) {
        super(shelf, booksUnavailable, school);
    }

    public void work() {
        for (Book book : this.getBooksUnavailable().keySet()) {
            if (this.getBooksUnavailable().get(book) > 0) {
                if (getShelf().containsKey(book)) {
                    this.getShelf().put(book,
                            this.getShelf().get(book) + this.getBooksUnavailable().get(book));
                }
                this.getBooksUnavailable().put(book, 0);
            }
        }
    }
}
