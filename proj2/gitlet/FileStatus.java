package gitlet;


public class FileStatus {
    private boolean exists;  // 文件是否存在
    private boolean modified;  // 文件是否修改过
    private boolean deleted;  // 文件是否被删除
    private String blobId;  // 文件内容的哈希值（如果文件存在且被修改）

    // 构造函数
    public FileStatus(boolean exists, boolean modified, boolean deleted, String blobId) {
        this.exists = exists;
        this.modified = modified;
        this.deleted = deleted;
        this.blobId = blobId;
    }

    // Getter 方法
    public boolean exists() {
        return exists;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getBlobId() {
        return blobId;
    }
}
