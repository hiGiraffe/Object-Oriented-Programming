package books;

public class Book {
    private String schoolName;
    //类别号 A是0 B是1 C是2
    private int categoryNum;
    //序列号
    private String serialNum;
    private boolean canIntercollegiateBorrow;

    public Book(String schoolName, int categoryNum,
                String serialNum, boolean canIntercollegiateBorrow) {
        this.schoolName = schoolName;
        this.categoryNum = categoryNum;
        this.serialNum = serialNum;
        this.canIntercollegiateBorrow = canIntercollegiateBorrow;
    }

    public boolean isB() {
        return categoryNum == 1;
    }

    public int getCategoryNum() {
        return categoryNum;
    }

    public boolean isCanIntercollegiateBorrow() {
        return canIntercollegiateBorrow;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getSerialNum() {
        return serialNum;
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
        return this.schoolName.equals(book.schoolName)
                && this.serialNum.equals(book.serialNum) && this.categoryNum == book.categoryNum;
    }

    @Override
    public int hashCode() {
        return categoryNum;
    }
}
