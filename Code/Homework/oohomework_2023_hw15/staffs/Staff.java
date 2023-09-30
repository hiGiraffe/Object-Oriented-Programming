package staffs;

import books.Book;
import library.School;

import java.time.LocalDate;
import java.util.HashMap;

public abstract class Staff {
    private HashMap<Book, Integer> shelf;
    private HashMap<Book, Integer> booksUnavailable;

    private School school;

    public Staff(HashMap<Book, Integer> shelf,
                 HashMap<Book, Integer> booksUnavailable, School school) {
        this.shelf = shelf;
        this.booksUnavailable = booksUnavailable;
        this.school = school;
    }

    public HashMap<Book, Integer> getBooksUnavailable() {
        return booksUnavailable;
    }

    public HashMap<Book, Integer> getShelf() {
        return shelf;
    }

    public LocalDate getToday() {
        return school.getToday();
    }

    public School getSchool() {
        return school;
    }
}
