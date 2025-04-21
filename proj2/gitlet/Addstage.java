package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.STAGING;
import static gitlet.Utils.writeObject;

public class Addstage implements Serializable {
    TreeMap<String, String> stage;
    public Addstage() {
        stage = new TreeMap<>();
    }
    public void add(String fileName, String blobId) {
        stage.put(fileName, blobId);
    }
    public void remove(String fileName) {
        stage.remove(fileName);
    }
    public void save() {
            writeObject(STAGING, this);
    }
}
