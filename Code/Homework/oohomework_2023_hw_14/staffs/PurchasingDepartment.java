package staffs;

import books.Book;
import library.School;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.max;

public class PurchasingDepartment extends Staff {
    private ArrayList<Book> purchasedBooks;
    private ArrayList<Integer> purchasedNums;

    public PurchasingDepartment(HashMap<Book, Integer> shelf,
                                HashMap<Book, Integer> booksUnavailable, School school) {
        super(shelf, booksUnavailable, school);
        purchasedBooks = new ArrayList<>();
        purchasedNums = new ArrayList<>();
    }

    public void addPurchaseBook(Book book) {
        //如果有这本书了
        for (int i = 0; i < purchasedBooks.size(); i++) {
            if (purchasedBooks.get(i).equals(book)) {
                purchasedNums.set(i, purchasedNums.get(i) + 1);
                return;
            }
        }
        //如果没有
        if (!purchasedBooks.contains(book)) {
            purchasedBooks.add(book);
            purchasedNums.add(1);
        }
    }

    public void work() {
        for (int i = 0; i < purchasedBooks.size(); i++) {
            Book book = purchasedBooks.get(i);
            int purchasedNum = purchasedNums.get(i);
            System.out.println("[" + getToday() + "]" + " " + book.getSchoolName() + "-"
                    + book + " got purchased by purchasing department in " + getSchool().getName());
            //最多买三本
            int num = max(3, purchasedNum);
            getSchool().purchaseBook(book, num);
        }
        //清空
        purchasedBooks.clear();
        purchasedNums.clear();
    }
}
