package users;

import books.Book;

import java.time.LocalDate;
import java.util.HashMap;

public class Student {
    private String schoolName;
    private String studentId;
    private Book bookB;
    private boolean isSmearedBookB;
    private HashMap<Book, Boolean> booksC; //是否弄坏了
    private HashMap<Book, LocalDate> booksOrdering;

    public Student(String schoolName, String studentId) {
        this.schoolName = schoolName;
        this.studentId = studentId;
        this.bookB = null;
        this.booksC = new HashMap<>();
        this.booksOrdering = new HashMap<>();
        this.isSmearedBookB = false;
    }

    public String getStudentId() {
        return studentId;
    }

    public Book getReturnBook(int categoryNum, String serialNum) {
        if (bookB != null && bookB.getCategoryNum() == categoryNum
                && bookB.getSerialNum().equals(serialNum)) {
            return bookB;
        }
        for (Book book : booksC.keySet()) {
            if (book.getCategoryNum() == categoryNum && book.getSerialNum().equals(serialNum)) {
                return book;
            }
        }
        return null;
    }

    public void borrowBookB(Book book, LocalDate today) {
        bookB = book;
        bookB.setTimeLoanService(today);
        isSmearedBookB = false;
    }

    public void borrowBookC(Book book, LocalDate today) {
        book.setTimeLoanService(today);
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

    public void getOrderedBook() {

    }

    @Override
    public String toString() {
        return schoolName + "-" + studentId;
    }

    @Override
    public boolean equals(Object object) {
        Student student = (Student) object;
        return this.schoolName.equals(student.schoolName) && this.studentId == student.studentId;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
