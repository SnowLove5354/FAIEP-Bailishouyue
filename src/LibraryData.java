import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LibraryData implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Book> books;
    private List<User> users;
    private List<BorrowRecord> borrowRecords;
    private int nextBookNum;      // 下一个图书编号数字部分（从1开始）
    private int nextUserNum;      // 下一个用户编号数字部分
    private int nextBorrowRecordNum; // 下一个借阅记录编号数字部分

    public LibraryData() {
        books = new ArrayList<>();
        users = new ArrayList<>();
        borrowRecords = new ArrayList<>();
        nextBookNum = 1;
        nextUserNum = 1;
        nextBorrowRecordNum = 1;
    }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    public int getNextBookNum() { return nextBookNum; }
    public void setNextBookNum(int nextBookNum) { this.nextBookNum = nextBookNum; }
    public int getNextUserNum() { return nextUserNum; }
    public void setNextUserNum(int nextUserNum) { this.nextUserNum = nextUserNum; }
    public int getNextBorrowRecordNum() { return nextBorrowRecordNum; }
    public void setNextBorrowRecordNum(int nextBorrowRecordNum) { this.nextBorrowRecordNum = nextBorrowRecordNum; }
}