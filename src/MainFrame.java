import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainFrame extends JFrame {
    private LibraryService service = LibraryService.getInstance();
    private JTable bookTable, userTable, recordTable;
    private DefaultTableModel bookModel, userModel;
    private JTextField bookSearch, userSearch;

    public MainFrame() {
        setTitle("图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // ========== 核心功能：菜单驱动界面 开始 ==========
        JMenuBar menuBar = new JMenuBar();

        // 借阅管理菜单项
        JMenu borrowMenu = new JMenu("借阅管理");
        JMenuItem borrowItem = new JMenuItem("借阅管理窗口");
        borrowItem.addActionListener(e -> new BorrowManagerWindow().setVisible(true));
        borrowMenu.add(borrowItem);
        menuBar.add(borrowMenu);

        // 系统菜单项（退出）
        JMenu sysMenu = new JMenu("系统");
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "退出?", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.exit(0);
        });
        sysMenu.add(exitItem);
        menuBar.add(sysMenu);

        setJMenuBar(menuBar);
        // ========== 核心功能：菜单驱动界面 结束 ==========

        // 左右面板：同时显示图书管理和用户管理
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createBookPanel(), createUserPanel());
        split.setDividerLocation(0.5);
        split.setResizeWeight(0.5);
        add(split);
    }

    // ========== 核心功能：图书管理界面（含表格、查询、增删改按钮）==========
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图书管理"));

        // 表格模型
        bookModel = new DefaultTableModel(new String[]{"ID","书名","作者","状态"}, 0) {
            public boolean isCellEditable(int r,int c) { return false; }
        };
        bookTable = new JTable(bookModel);
        JScrollPane sp = new JScrollPane(bookTable);

        // 查询面板
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("关键字:"));
        bookSearch = new JTextField(12);
        top.add(bookSearch);
        JButton searchBtn = new JButton("查询");
        searchBtn.addActionListener(e -> refreshBooks());
        JButton resetBtn = new JButton("显示全部");
        resetBtn.addActionListener(e -> { bookSearch.setText(""); refreshBooks(); });
        top.add(searchBtn); top.add(resetBtn);

        // 按钮面板：添加、修改、删除
        JPanel btns = new JPanel(new FlowLayout());
        JButton add = new JButton("添加");
        JButton edit = new JButton("修改");
        JButton del = new JButton("删除");

        // === 独立窗口：添加图书 ===
        add.addActionListener(e -> {
            new EditDialog(this, null, "book").setVisible(true);
            refreshBooks();
        });
        // === 独立窗口：修改图书 ===
        edit.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) bookModel.getValueAt(row, 0);
                Book b = service.findBookById(id);
                if (b != null) {
                    new EditDialog(this, b, "book").setVisible(true);
                    refreshBooks();
                }
            } else JOptionPane.showMessageDialog(this, "请先选择");
        });
        // === 删除图书 ===
        del.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "确认删除?", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String id = (String) bookModel.getValueAt(row, 0);
                service.deleteBook(id);
                refreshBooks();
            }
        });

        btns.add(add); btns.add(edit); btns.add(del);

        panel.add(top, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        refreshBooks();
        return panel;
    }

    // 刷新图书表格（调用服务层查询）
    private void refreshBooks() {
        bookModel.setRowCount(0);
        List<Book> books = service.searchBooks(bookSearch.getText());
        for (Book b : books) {
            bookModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.isBorrowed() ? "已借出" : "可借"});
        }
    }

    // ========== 核心功能：用户管理界面（含表格、查询、增删改按钮）==========
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("用户管理"));

        userModel = new DefaultTableModel(new String[]{"ID","姓名","电话"}, 0) {
            public boolean isCellEditable(int r,int c) { return false; }
        };
        userTable = new JTable(userModel);
        JScrollPane sp = new JScrollPane(userTable);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("关键字:"));
        userSearch = new JTextField(12);
        top.add(userSearch);
        JButton searchBtn = new JButton("查询");
        searchBtn.addActionListener(e -> refreshUsers());
        JButton resetBtn = new JButton("显示全部");
        resetBtn.addActionListener(e -> { userSearch.setText(""); refreshUsers(); });
        top.add(searchBtn); top.add(resetBtn);

        JPanel btns = new JPanel(new FlowLayout());
        JButton add = new JButton("添加");
        JButton edit = new JButton("修改");
        JButton del = new JButton("删除");

        // === 独立窗口：添加用户 ===
        add.addActionListener(e -> {
            new EditDialog(this, null, "user").setVisible(true);
            refreshUsers();
        });
        // === 独立窗口：修改用户 ===
        edit.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) userModel.getValueAt(row, 0);
                User u = service.findUserById(id);
                if (u != null) {
                    new EditDialog(this, u, "user").setVisible(true);
                    refreshUsers();
                }
            } else JOptionPane.showMessageDialog(this, "请先选择");
        });
        // === 删除用户 ===
        del.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "确认删除?", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String id = (String) userModel.getValueAt(row, 0);
                service.deleteUser(id);
                refreshUsers();
            }
        });

        btns.add(add); btns.add(edit); btns.add(del);

        panel.add(top, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        refreshUsers();
        return panel;
    }

    private void refreshUsers() {
        userModel.setRowCount(0);
        List<User> users = service.searchUsers(userSearch.getText());
        for (User u : users) {
            userModel.addRow(new Object[]{u.getId(), u.getName(), u.getPhone()});
        }
    }

    // ========== 核心功能：借阅管理（独立窗口，包含借书、还书）==========
    private class BorrowManagerWindow extends JFrame {
        public BorrowManagerWindow() {
            setTitle("借阅管理");
            setSize(800, 500);
            setLocationRelativeTo(MainFrame.this);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            DefaultTableModel model = new DefaultTableModel(new String[]{"记录ID","用户ID","用户名","图书ID","图书名","借书日期","归还日期","状态"}, 0) {
                public boolean isCellEditable(int r,int c) { return false; }
            };
            recordTable = new JTable(model);
            JScrollPane sp = new JScrollPane(recordTable);

            JPanel btnPanel = new JPanel(new FlowLayout());
            JButton borrowBtn = new JButton("借书");
            JButton returnBtn = new JButton("还书");
            JButton refreshBtn = new JButton("刷新");

            // === 借书：弹出输入框 ===
            borrowBtn.addActionListener(e -> {
                String uid = JOptionPane.showInputDialog(this, "请输入用户ID (例如 U001):");
                String bid = JOptionPane.showInputDialog(this, "请输入图书ID (例如 B001):");
                if (uid != null && bid != null && !uid.trim().isEmpty() && !bid.trim().isEmpty()) {
                    service.borrowBook(uid.trim(), bid.trim());
                    refreshTable(model);
                } else if (uid != null && bid != null) {
                    JOptionPane.showMessageDialog(this, "ID不能为空");
                }
            });

            // === 还书：下拉选择未归还记录 ===
            returnBtn.addActionListener(e -> {
                List<BorrowRecord> unreturned = service.getUnreturnedRecords();
                if (unreturned.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "没有未归还记录");
                    return;
                }
                String[] options = unreturned.stream()
                        .map(r -> "记录ID:" + r.getId() + " 图书:" + service.getBookTitle(r.getBookId()) + " 用户:" + service.getUserName(r.getUserId()))
                        .toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(this, "选择要归还的记录", "还书",
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (selected != null) {
                    String recordId = selected.split(":")[1].split(" ")[0];
                    service.returnBook(recordId);
                    refreshTable(model);
                }
            });

            refreshBtn.addActionListener(e -> refreshTable(model));
            btnPanel.add(borrowBtn); btnPanel.add(returnBtn); btnPanel.add(refreshBtn);
            add(sp, BorderLayout.CENTER);
            add(btnPanel, BorderLayout.SOUTH);
            refreshTable(model);
        }

        private void refreshTable(DefaultTableModel model) {
            model.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (BorrowRecord r : service.getAllRecords()) {
                model.addRow(new Object[]{
                        r.getId(), r.getUserId(), service.getUserName(r.getUserId()),
                        r.getBookId(), service.getBookTitle(r.getBookId()),
                        sdf.format(r.getBorrowDate()),
                        r.isReturned() ? sdf.format(r.getReturnDate()) : "未归还",
                        r.isReturned() ? "已归还" : "借出中"
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}