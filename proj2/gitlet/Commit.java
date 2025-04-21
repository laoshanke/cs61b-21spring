package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.join;
import static gitlet.Utils.sha1;
import static java.nio.file.Files.createFile;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
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
    private String parent;
    private String secondParent;
    private TreeMap <String, String> nametoblobs;
    void Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
    }
    void save_commit() {
        String id = getId();
        File dir = join(OBJECT_DIR, id.substring(0,2));
        if(!dir.exists()) {
            dir.mkdir();
        }
        createFileplus(join(OBJECT_DIR, id.substring(0,2), id.substring(2,40)));

    }
    String getId(){
        return sha1(this.message, this.timestamp, this.parent, this.secondParent, this.nametoblobs);
    }
    void dump() {

    }
    String getparent1() {
        return parent;
    }
    String getParent2() {
        return secondParent;
    }
    /**
     * TODO: fill in the constructor here.

    /* TODO: fill in the rest of this class. */
}
