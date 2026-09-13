import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

public class LibraryService {
    private static LibraryService instance;
    private LibraryData data;

    private LibraryService() { data = LibraryPersistence.load(); }
    public static LibraryService getInstance() {
        if (instance == null) instance = new LibraryService();
        return instance;
    }
    private void save() { LibraryPersistence.save(data); }

    // ===== ID生成辅助方法 =====
    private String generateBookId() {
        return String.format("B%03d", data.getNextBookNum());
    }
    private String generateUserId() {
        return String.format("U%03d", data.getNextUserNum());
    }
    private String generateRecordId() {
        return String.format("R%03d", data.getNextBorrowRecordNum());
    }

    // === 修复：添加预览ID的方法（供EditDialog使用）===
    public String getNextBookIdPreview() {
        return String.format("B%03d", data.getNextBookNum());
    }
    public String getNextUserIdPreview() {
        return String.format("U%03d", data.getNextUserNum());
    }

    // === 核心功能：图书查询 ===
    public List<Book> getAllBooks() { return data.getBooks(); }
    public List<Book> searchBooks(String kw) {
        if (kw == null || kw.trim().isEmpty()) return getAllBooks();
        String k = kw.trim().toLowerCase();
        return data.getBooks().stream().filter(b -> b.getTitle().toLowerCase().contains(k) ||
                b.getAuthor().toLowerCase().contains(k) || b.getId().toLowerCase().contains(k)).collect(Collectors.toList());
    }

    // === 核心功能：图书添加 ===
    public boolean addBook(String title, String author) {
        if (title == null || title.trim().isEmpty()) { showMsg("书名不能为空"); return false; }
        String id = generateBookId();
        Book b = new Book(id, title.trim(), author == null ? "" : author.trim());
        data.getBooks().add(b);
        data.setNextBookNum(data.getNextBookNum() + 1);
        save();
        return true;
    }

    // === 核心功能：图书修改 ===
    public boolean updateBook(String id, String title, String author) {
        Book b = findBookById(id);
        if (b == null) return false;
        if (title == null || title.trim().isEmpty()) { showMsg("书名不能为空"); return false; }
        b.setTitle(title.trim());
        b.setAuthor(author == null ? "" : author.trim());
        save();
        return true;
    }

    // === 核心功能：图书删除 ===
    public boolean deleteBook(String id) {
        if (data.getBorrowRecords().stream().anyMatch(r -> r.getBookId().equals(id) && !r.isReturned())) {
            showMsg("该书有未还记录，无法删除"); return false;
        }
        Book b = findBookById(id);
        if (b != null) { data.getBooks().remove(b); save(); return true; }
        return false;
    }

    public Book findBookById(String id) { return data.getBooks().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null); }

    // === 核心功能：用户查询 ===
    public List<User> getAllUsers() { return data.getUsers(); }
    public List<User> searchUsers(String kw) {
        if (kw == null || kw.trim().isEmpty()) return getAllUsers();
        String k = kw.trim().toLowerCase();
        return data.getUsers().stream().filter(u -> u.getName().toLowerCase().contains(k) ||
                u.getPhone().contains(k) || u.getId().toLowerCase().contains(k)).collect(Collectors.toList());
    }

    // === 核心功能：用户添加 ===
    public boolean addUser(String name, String phone) {
        if (name == null || name.trim().isEmpty()) { showMsg("用户名不能为空"); return false; }
        String id = generateUserId();
        User u = new User(id, name.trim(), phone == null ? "" : phone.trim());
        data.getUsers().add(u);
        data.setNextUserNum(data.getNextUserNum() + 1);
        save();
        return true;
    }

    // === 核心功能：用户修改 ===
    public boolean updateUser(String id, String name, String phone) {
        User u = findUserById(id);
        if (u == null) return false;
        if (name == null || name.trim().isEmpty()) { showMsg("用户名不能为空"); return false; }
        u.setName(name.trim());
        u.setPhone(phone == null ? "" : phone.trim());
        save();
        return true;
    }

    // === 核心功能：用户删除 ===
    public boolean deleteUser(String id) {
        if (data.getBorrowRecords().stream().anyMatch(r -> r.getUserId().equals(id) && !r.isReturned())) {
            showMsg("该用户有未还图书，无法删除"); return false;
        }
        User u = findUserById(id);
        if (u != null) { data.getUsers().remove(u); save(); return true; }
        return false;
    }

    public User findUserById(String id) { return data.getUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null); }

    // === 核心功能：借书 ===
    public boolean borrowBook(String userId, String bookId) {
        User u = findUserById(userId);
        Book b = findBookById(bookId);
        if (u == null) { showMsg("用户不存在"); return false; }
        if (b == null) { showMsg("图书不存在"); return false; }
        if (b.isBorrowed()) { showMsg("图书已借出"); return false; }
        String recordId = generateRecordId();
        BorrowRecord r = new BorrowRecord(recordId, userId, bookId, new Date());
        data.getBorrowRecords().add(r);
        data.setNextBorrowRecordNum(data.getNextBorrowRecordNum() + 1);
        b.setBorrowed(true);
        save();
        showMsg("借书成功");
        return true;
    }

    // === 核心功能：还书 ===
    public boolean returnBook(String recordId) {
        BorrowRecord r = data.getBorrowRecords().stream().filter(rec -> rec.getId().equals(recordId) && !rec.isReturned()).findFirst().orElse(null);
        if (r == null) { showMsg("记录不存在或已归还"); return false; }
        r.setReturnDate(new Date());
        Book b = findBookById(r.getBookId());
        if (b != null) b.setBorrowed(false);
        save();
        showMsg("还书成功");
        return true;
    }

    public List<BorrowRecord> getAllRecords() { return data.getBorrowRecords(); }
    public List<BorrowRecord> getUnreturnedRecords() { return data.getBorrowRecords().stream().filter(r -> !r.isReturned()).collect(Collectors.toList()); }
    public String getBookTitle(String bookId) { Book b = findBookById(bookId); return b == null ? "未知" : b.getTitle(); }
    public String getUserName(String userId) { User u = findUserById(userId); return u == null ? "未知" : u.getName(); }

    private void showMsg(String msg) { JOptionPane.showMessageDialog(null, msg); }
}