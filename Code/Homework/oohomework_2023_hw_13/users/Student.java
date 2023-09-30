package users;

import books.Book;

import java.time.LocalDate;
import java.util.HashMap;

public class Student {
    private String studentId;
    private Book bookB;
    private boolean isSmearedBookB;
    private HashMap<Book, Boolean> booksC;
    private HashMap<Book, LocalDate> booksOrdering;

    public Student(String studentId) {
        this.studentId = studentId;
        this.bookB = null;
        this.booksC = new HashMap<>();
        this.booksOrdering = new HashMap<>();
        this.isSmearedBookB = false;
    }

    public String getStudentId() {
        return studentId;
    }

    public void borrowBookB(Book book) {
        bookB = book;
        isSmearedBookB = false;
    }

    public void borrowBookC(Book book) {
        booksC.put(book, false);
    }

    public void returnBookB(Book book) {
        bookB = null;
    }

    public void returnBooksC(Book book) {
        booksC.remove(book);
    }

    public boolean canBorrowBookB() {
        return bookB == null;
    }

    public boolean canBorrowBookC(Book book) {
        return !booksC.containsKey(book);
    }

    public void addOrderingBook(Book book, LocalDate today) {
        booksOrdering.put(book, today);
    }

    public void deleteOrderingBookB() {
        booksOrdering.entrySet().removeIf(entry -> entry.getKey().isB());
    }

    public void deleteOrderingBookC(Book book) {
        booksOrdering.remove(book);
    }

    public boolean hadOrderingBook(Book book) {
        return booksOrdering.containsKey(book);
    }

    public boolean hadOrderThreeBooksOneDay(LocalDate date) {
        int n = 0;
        for (Book book : booksOrdering.keySet()) {
            if (booksOrdering.get(book).equals(date)) {
                n++;
            }
        }
        return n == 3;
    }

    public void smearedBook(Book book) {
        if (book.isB()) {
            isSmearedBookB = true;
        } else {
            booksC.put(book, true);
        }
    }

    public boolean isSmearedBookB() {
        return this.isSmearedBookB;
    }

    public boolean isSmearedBookC(Book book) {
        return booksC.get(book);
    }
}
