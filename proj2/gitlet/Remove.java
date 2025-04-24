package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Repository.STAGING;
import static gitlet.Repository.STAGING_REMOVE;
import static gitlet.Utils.writeObject;

public class Remove implements Serializable {
    private List<String> remove;
    Remove() {
        remove = new ArrayList<>();
    }
    public void save(){
        writeObject(STAGING_REMOVE, this);
    }
    public void add(String fileName) {
        remove.add(fileName);
        save();
    }
    public void remove(String fileName) {
        remove.remove(fileName);
        save();
    }
    public boolean contains(String fileName) {
        return remove.contains(fileName);
    }
    public int size() {
        return remove.size();
    }
    public List<String> getRemove() {
        return remove;
    }
}
