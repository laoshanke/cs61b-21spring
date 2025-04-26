package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.createFileplus;
import static gitlet.Utils.join;

public class Blob implements Serializable {
   private String name;
    private byte[] content;
    private String id;
    public Blob(File file) {
        content = Utils.readContents(file);
        name = file.getName();
        id = getId();
    }
    byte[] getContent() {
        return content;
    }
    void saveObject() {//把一个blob对象保存到对象文件夹中
        File dir = join(Repository.OBJECT_DIR, id.substring(0,2));
        if (!dir.exists() ) {
            dir.mkdir();
        }
        createFileplus(join(Repository.OBJECT_DIR, id.substring(0,2), id.substring(2,40)));
        Utils.writeObject(join(Repository.OBJECT_DIR, id.substring(0,2),id.substring(2,40)), this);
    }
    void savestage() {//把一个blob对象保存到暂存区文件夹中
        saveObject();
        Addstage stage = Utils.readObject(Repository.STAGING, Addstage.class);
        stage.add(name, id);
        stage.save();
    }
    String getId(){
        return Utils.sha1( "blob" + " " + content.length + "\0" + content);
    }
}
