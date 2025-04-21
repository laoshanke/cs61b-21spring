package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File MYGITLET_DIR = join(GITLET_DIR, "mygitlet");
    public static final File COMMIT_DIR = join(MYGITLET_DIR, "commits");
    public static final File OBJECT_DIR = join(MYGITLET_DIR, "objects");

    public static final File STAGING_DIR = join(MYGITLET_DIR, "staging");

    public static final File BRANCH_DIR = join(MYGITLET_DIR, "branches");

    public static final File HEAD = join(MYGITLET_DIR, "head");
    public List<String> remove = new ArrayList<>();
    void init() {
        if (GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        MYGITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        COMMIT_DIR.mkdir();
        STAGING_DIR.mkdir();
        BRANCH_DIR.mkdir();
        createFileplus(HEAD);
        Commit init = new Commit();
        init.save_commit();
        writeBranch("master", init.getId());
        head_point_branch("master");
    }
    void add(String fileName){
        if(!join(CWD, fileName).exists()) {
            throw Utils.error("File does not exist.");
        }
        blob blob = fileGetBLOB(fileName);
        String id = blob.getId();
        String id2 =get_branch_point_commit(get_head_point_branch()).getBlobId(fileName);
        if(id2!=null&&id2.equals(id)){//如果文件的当前工作版本与当前提交中的版本相同，则不要将其暂存以待添加；若该文件已在暂存区中
            // ，则将其从暂存区移除（比如当一个文件先被修改、添加，然后又改回其原始版本时就会出现这种情况）。
        File file = join(STAGING_DIR, fileName);
        if(file.exists()){
            file.delete();
        }
        }else{
            blob.savestage();//将当前状态下的文件副本添加到暂存区(对已暂存的文件再次执行添加操作，会用新内容覆盖暂存区中该文件的先前记录。
        }
        if(remove.contains(fileName)){//。如果在执行此命令时，该文件处于暂存待移除状态（请参阅 gitlet rm 命令），执行此命令后，该文件将不再处于暂存待移除状态。
            remove.remove(fileName);
        }

    }
    void commit(String message){
        if(message.equals("")){
            throw Utils.error("Please enter a commit message.");
        }
        List<String> list = plainFilenamesIn(STAGING_DIR);
        if(list.size()==0&&remove.size()==0){
            throw Utils.error("No changes added to the commit.");
        }
        Commit commit = new Commit(message);
        for(String name:list){
            blob blob = get_blob_from_stage(name);
            join(STAGING_DIR, name).delete();
            blob.saveObject();
            commit.change_blobs(name, blob.getId());
        }
        for(String name:remove){
            commit.remove_blob(name);
        }
        commit.save_commit();
    writeBranch( get_head_point_branch(), commit.getId());
    }
    void rm(String fileName){
        boolean flag = false;
        List<String> list = plainFilenamesIn(STAGING_DIR);
        if(list.contains(fileName)){
            join(STAGING_DIR, fileName).delete();
            flag = true;
        }
        if(get_branch_point_commit(get_head_point_branch()).getBlobId(fileName)!=null){
            remove.add(fileName);
            flag = true;
            File file = join(CWD, fileName);
            if(file.exists()){
                file.delete();
            }
        }
        if(!flag){
            throw Utils.error("No reason to remove the file.");
        }
    }
    void log(){
        get_branch_point_commit(get_head_point_branch()).log_print();
    }
    static void createFileplus(File file) {//超级创造文件
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw Utils.error("crate file error");
            }
        }
    }
    static void writeBranch(String name, String id) {//创造一个名字为name内容为id的分支
        File file = join(BRANCH_DIR, name);
        createFileplus(file);
        writeContents(file, id);
    }
    static void head_point_branch(String name) {//设置头指针指着的分支
        writeContents(HEAD, name);
    }
    static String get_head_point_branch() {//得到头指针指着的分支
        return readContentsAsString(HEAD);
    }
    static Commit get_branch_point_commit(String name) {//得到分支的commit
        String id = readContentsAsString(join(BRANCH_DIR, name));
        return readObject(join(COMMIT_DIR, id.substring(0,2), id.substring(2,40)), Commit.class);}
    static blob fileGetBLOB(String fileName) {//得到工作区中文件的对应Blob
        File file = join(CWD, fileName);
        if(!file.exists()) {
            throw Utils.error("File does not exist.");
        }else{
            return new blob(file);
        }
    }
    blob get_blob_from_stage(String name) {
        return readObject(join(STAGING_DIR, name), blob.class);
    }
    /* TODO: fill in the rest of this class. */
}
