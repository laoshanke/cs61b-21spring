package gitlet;

// TODO: any imports you need here

import com.sun.source.tree.ReturnTree;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.join;
import static gitlet.Utils.readContentsAsString;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit  implements Dumpable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The timestamp of this Commit. */
    private Date timestamp;
    /** The message of this Commit. */
    private String message;
    /** The parent id of this Commit. */
    private String[] parent_ids;
    /** The tracked blob  of this Commit. */
    private HashMap<String,String> blobids;
    public Date getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
    public String[] getParent_ids() {
        return parent_ids;
    }
    public HashMap<String,String> getblobids() {
        return blobids;
    }

    /** Creates a default commit  */
    public Commit(){
        timestamp = new Date(0);
        message = "initial commit";
        parent_ids = new String[2];
        blobids = new HashMap<String,String>();
    }
    /** Creates a commit with a message. */
    public Commit(String message1){
        timestamp = new Date();
        message = message1;
        parent_ids = new String[2];
        Commit commit = get_head_branch_pointer_commit();
        parent_ids[0] = commit.get_id();
        blobids = new HashMap<String,String>();
        for( String name:commit.getblobids().keySet()){
            blobids.put(name,commit.blobids_get(name));
        }
    }

    /**return the id of the commit*/
    public String get_id(){
        return  Utils.sha1(this);
    }

    public String blobids_get( String Name_id){
        return blobids.get(Name_id);
    }
    public void blobids_put(String Name_id,String blob_id){
        blobids.put(Name_id,blob_id);
    }
    public void blobids_remove(String Name_id){
        blobids.remove(Name_id);
    }
    public boolean blobids_containsKey(String Name_id){
        return blobids.containsKey(Name_id);
    }
    public void dump(){
        System.out.println("===");
        System.out.println("commit " + get_id());
        if(parent_ids[1] != null){
            System.out.println("Merge: " + parent_ids[0].substring(0,7) + " " + parent_ids[1].substring( 0,7));
        }
        String time = String.format("Date: %1$ta %1$tb %1$te %1$tH:%1$tM:%1$tS %1$tY %1$tz", timestamp);
        System.out.println(message);
        System.out.println();
    }



    /* TODO: fill in the rest of this class. */
}
