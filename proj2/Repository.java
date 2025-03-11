private static void setupDirectories() {
    GITLET_DIR.mkdir();
    Utils.join(GITLET_DIR, "objects").mkdir();
    Utils.join(GITLET_DIR, "objects", "blobs").mkdir();
    Utils.join(GITLET_DIR, "objects", "commits").mkdir();
    Utils.join(GITLET_DIR, "refs").mkdir();
    // 其他必要的目录...
}

private static File getBlobFile(String id) {
    return Utils.join(GITLET_DIR, "objects", "blobs", id.substring(0, 2), id.substring(2));
}

public static void saveBlob(blob b) {
    String id = b.getID();
    File dir = Utils.join(GITLET_DIR, "objects", "blobs", id.substring(0, 2));
    if (!dir.exists()) {
        dir.mkdir();
    }
    File blobFile = Utils.join(dir, id.substring(2));
    Utils.writeObject(blobFile, b);
    
    // 验证文件是否成功保存
    if (!blobFile.exists()) {
        throw new GitletException("Failed to save blob: " + id);
    }
}

public static blob readBlob(String id) {
    return Utils.readObject(getBlobFile(id), blob.class);
} 