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
    private String id;
    Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parent = new ArrayList<>();//注意为空不为null。不然无法被hash
        this.nametoblobs = new TreeMap<>();
        this.id = getId();
    }

    Commit(String message,  String parent2id) {
        this.message = message;
        this.timestamp = new Date();
        this.parent = new ArrayList<>();
        Commit parent1 = get_branch_point_commit(get_head_point_branch());
        parent.add(parent1.getId());
        if(parent2id != null) {
            parent.add(parent2id);
        }
        this.nametoblobs = parent1.getnametoblobs();
        this.id = getId();
    }
    void save_commit() {
        String id = getId();
        File dir = join(COMMIT_DIR, id.substring(0,2));
        if(!dir.exists()) {
            dir.mkdir();
        }
        createFileplus(join(COMMIT_DIR, id.substring(0,2), id.substring(2,40)));
        writeObject(join(COMMIT_DIR, id.substring(0,2), id.substring(2,40)), this);
    }
    void change_blobs(String name, String id) {
        nametoblobs.put(name, id);
    }
    void remove_blob(String name) {
        nametoblobs.remove(name);
    }
    String getId(){
        return sha1(this.message, dateToTimeStamp(this.timestamp), this.parent.toString(), this.nametoblobs.toString());
    }
    List<String> getparent() {
        return parent;
    }
    String getparent1() {
        return parent.get(0);
    }
    String getparent2() {
        return parent.get(1);
    }
    String getMessage() {
        return message;
    }
    String getBlobId(String name) {
        return nametoblobs.get(name);
    }
    Boolean contains_name(String name) {
        return nametoblobs.containsKey(name);
    }
    TreeMap <String, String> getnametoblobs() {
        return nametoblobs;
    }
    private static String dateToTimeStamp(Date date) {//把不标准的时间格式转化为标准的时间格式，而且作为字符串格式可以被hash
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    void log_print() {
        if( parent.size() == 2 ) {
            System.out.println( "===" );
            System.out.println( "commit " + getId() );
            System.out.println( "Merge: " + getparent1().substring(0,7) + " " + getparent2().substring(0,7) );
            System.out.println( "Date: " + dateToTimeStamp(timestamp) );
            System.out.println( message );
            System.out.println();
        } else {
            System.out.println("===");
            System.out.println("commit " + getId());
            System.out.println("Date: " + dateToTimeStamp(timestamp));
            System.out.println(message);
            System.out.println();
            }
    }
    void print_log_recursively() {
        log_print();
        if(parent.size()!=0) {
            String parent1id = getparent1();
            Commit parent1 = readObject(join(COMMIT_DIR, parent1id.substring(0,2), parent1id.substring(2,40)), Commit.class);
            parent1.print_log_recursively();
        }
    }
    /**
     * TODO: fill in the constructor here.

     /* TODO: fill in the rest of this class. */
}
