package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static java.nio.file.Files.createFile;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private List<String> parent;
    private TreeMap <String, String> nametoblobs;
    void Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parent = new ArrayList<>();//注意为空不为null。不然无法被hash
        this.nametoblobs = new TreeMap<>();
    }
    void save_commit() {
        String id = getId();
        File dir = join(OBJECT_DIR, id.substring(0,2));
        if(!dir.exists()) {
            dir.mkdir();
        }
        createFileplus(join(OBJECT_DIR, id.substring(0,2), id.substring(2,40)));
        writeObject(join(OBJECT_DIR, id.substring(0,2), id.substring(2,40)), this);
    }
    String getId(){
        return sha1(this.message, dateToTimeStamp(this.timestamp), this.parent.toString(), this.nametoblobs.toString());
    }
    String getparent1() {
        return parent.get(0);
    }
    String getparent2() {
        return parent.get(1);
    }
    String getBlobIId(String name) {
        return nametoblobs.get(name);
    }
    private static String dateToTimeStamp(Date date) {//把不标准的时间格式转化为标准的时间格式，而且作为字符串格式可以被hash
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    /**
     * TODO: fill in the constructor here.

    /* TODO: fill in the rest of this class. */
}
