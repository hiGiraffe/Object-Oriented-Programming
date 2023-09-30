package books;

public class Book {
    //类别号 A是0 B是1 C是2
    private int categoryNum;
    //序列号
    private String serialNum;

    public Book(int categoryNum, String serialNum) {
        this.categoryNum = categoryNum;
        this.serialNum = serialNum;
    }

    public boolean isB() {
        return categoryNum == 1;
    }

    public int getCategoryNum() {
        return categoryNum;
    }

    @Override
    public String toString() {
        switch (categoryNum) {
            case 0:
                return "A-" + serialNum;
            case 1:
                return "B-" + serialNum;
            default:
                return "C-" + serialNum;
        }
    }

    @Override
    public boolean equals(Object object) {
        Book book = (Book) object;
        return this.serialNum.equals(book.serialNum) && this.categoryNum == book.categoryNum;
    }

    @Override
    public int hashCode() {
        return categoryNum;
    }
}
