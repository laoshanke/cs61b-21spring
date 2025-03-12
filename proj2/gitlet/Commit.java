package gitlet;

// TODO: any imports you need here


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
    private TreeMap<String,String> blobids;
    public String getMessage() {
        return message;
    }
    public String[] getParent_ids() {
        return parent_ids;
    }
    public TreeMap<String,String> getblobids() {
        return blobids;
    }

    /** Creates a default commit  */
    public Commit(){
        timestamp = new Date(0);
        message = "initial commit";
        parent_ids = new String[2];
        blobids = new TreeMap<String,String>();
    }
    /** Creates a commit with a message. */
    public Commit(String message1){
        timestamp = new Date();
        message = message1;
        parent_ids = new String[2]; 
        Commit commit = get_commit_from_branch( get_head_branch());
        parent_ids[0] = commit.get_id();
        blobids = new TreeMap<String,String>();
        for( String name:commit.getblobids().keySet()){
            blobids.put(name,commit.blobids_get(name));
        }
    }

    /**return the id of the commit*/
    public String get_id(){
        List<Object> vals = new ArrayList<>();
        // 添加时间戳
        vals.add(timestamp.toString());
        // 添加提交信息
        vals.add(message);
        // 添加父提交ID（非空）
        for (String parentId : parent_ids) {
            if (parentId != null) {
                vals.add(parentId);
            }
        }
        // 添加按文件名排序的blob条目
        List<String> sortedFiles = new ArrayList<>(blobids.keySet());
        Collections.sort(sortedFiles);
        for (String file : sortedFiles) {
            vals.add(file);
            vals.add(blobids.get(file));
        }
        // 生成SHA-1哈希
        return Utils.sha1(vals);
    }

    public String blobids_get( String Name){
        return blobids.get(Name);
    }
    public void blobids_put(String Name,String blob_id){
        blobids.put(Name,blob_id);
    }
    public void blobids_remove(String Name){
        blobids.remove(Name);
    }
    public boolean blobids_containsKey(String Name){
        return blobids.containsKey(Name);
    }
    public void dump() {
        System.out.println("===");
        System.out.println("commit " + get_id());
        if (parent_ids[1] != null) {
            System.out.println("Merge: " + parent_ids[0].substring(0, 7) + " " + parent_ids[1].substring(0, 7));
        }
        // 4. 使用 Formatter 组合格式符
        String formatted = String.format(
                "Date: %1$ta %1$tb %1$td %1$tH:%1$tM:%1$tS %1$tY %1$tz",
                timestamp
        );

        System.out.println(formatted);
        System.out.println(message);
        System.out.println();
    }





    /* TODO: fill in the rest of this class. */
}
