import javax.swing.*;
import java.awt.*;

// === 核心功能：添加/修改数据操作时使用独立窗口 ===
public class EditDialog extends JDialog {
    private LibraryService service = LibraryService.getInstance();
    private Object target; // null表示新增，否则为Book或User
    private JTextField idField, field1, field2;
    private String type; // "book" or "user"

    public EditDialog(JFrame parent, Object target, String type) {
        super(parent, (target == null ? "添加" : "修改") + (type.equals("book") ? "图书" : "用户"), true);
        this.target = target;
        this.type = type;
        initUI();
        if (target != null) loadData();
        else {
            // 预览下一个可用ID
            String nextId = type.equals("book") ? service.getNextBookIdPreview() : service.getNextUserIdPreview();
            idField.setText(nextId);
            idField.setEditable(false);
        }
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; add(new JLabel("ID:"), gbc);
        gbc.gridx=1; idField = new JTextField(15); idField.setEditable(false); add(idField, gbc);
        gbc.gridx=0; gbc.gridy=1; add(new JLabel(type.equals("book") ? "书名:" : "姓名:"), gbc);
        gbc.gridx=1; field1 = new JTextField(15); add(field1, gbc);
        gbc.gridx=0; gbc.gridy=2; add(new JLabel(type.equals("book") ? "作者:" : "电话:"), gbc);
        gbc.gridx=1; field2 = new JTextField(15); add(field2, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton ok = new JButton("确定");
        JButton cancel = new JButton("取消");
        btnPanel.add(ok); btnPanel.add(cancel);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; add(btnPanel, gbc);

        ok.addActionListener(e -> save());
        cancel.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(getParent());
    }

    private void loadData() {
        if (type.equals("book")) {
            Book b = (Book) target;
            idField.setText(b.getId());
            field1.setText(b.getTitle());
            field2.setText(b.getAuthor());
        } else {
            User u = (User) target;
            idField.setText(u.getId());
            field1.setText(u.getName());
            field2.setText(u.getPhone());
        }
    }

    private void save() {
        String v1 = field1.getText().trim();
        String v2 = field2.getText().trim();
        boolean success;
        if (target == null) {
            if (type.equals("book")) success = service.addBook(v1, v2);
            else success = service.addUser(v1, v2);
        } else {
            String id = idField.getText();
            if (type.equals("book")) success = service.updateBook(id, v1, v2);
            else success = service.updateUser(id, v1, v2);
        }
        if (success) dispose();
    }
}