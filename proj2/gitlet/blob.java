package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class blob implements Serializable {
    private byte[] content;
    private String name;
    private String id;
    public blob(String name, byte[] content) {
        this.name = name;
        this.content = content;
        this.id = Utils.sha1(name, content);
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void save_objects(){
        File file = Utils.join(Repository.OBJECTS_DIR, this.id);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(file, this);
    }
    public void save_add(){
        File file = Utils.join(Repository.ADD_DIR, this.id);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(file, this);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        blob blob = (blob) o;
        return id.equals(blob.id) && name.equals(blob.name);
    }
}
