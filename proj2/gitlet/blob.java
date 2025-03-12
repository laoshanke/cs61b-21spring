package gitlet;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.save_blob;

public class blob implements Serializable {
    private byte[] content;
    private String name;
    private String id;
    public blob(String name, byte[] content) {
        this.name = name;
        this.content = content;

        // ==== 插入点1：替换原有ID生成逻辑 ====
        // Git标准的blob ID生成方式
        String header = "blob " + content.length + "\0";
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] store = new byte[headerBytes.length + content.length];
        System.arraycopy(headerBytes, 0, store, 0, headerBytes.length);
        System.arraycopy(content, 0, store, headerBytes.length, content.length);
        this.id = Utils.sha1(store);
        // ===== 插入结束 =====
    }
     public String getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public byte[] getContent() {
        return content;
    }
    public void save_add(){
        save_blob(this);
        File file = Utils.join(Repository.ADD_DIR, this.name);
        Repository.plus_file_create(file);
        Utils.writeContents(file, this.id);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        blob blob = (blob) o;
        return id.equals(blob.id)&& name.equals(blob.name);
    }
}
