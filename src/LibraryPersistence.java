import java.io.*;

// === 核心功能：数据持久化（文件存储）开始 ===
public class LibraryPersistence {
    private static final String FILE_NAME = "libraryData.ser";

    // === 核心功能：保存数据到文件 ===
    public static void save(LibraryData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // === 核心功能：从文件加载数据 ===
    public static LibraryData load() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return new LibraryData();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (LibraryData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LibraryData();
        }
    }
}
// === 核心功能：数据持久化（文件存储）结束 ===