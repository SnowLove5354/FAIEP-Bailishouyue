import java.io.Serializable;
import java.util.Date;

public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;          // 格式：R001, R002...
    private String userId;
    private String bookId;
    private Date borrowDate;
    private Date returnDate;

    public BorrowRecord(String id, String userId, String bookId, Date borrowDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = null;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public boolean isReturned() { return returnDate != null; }
}