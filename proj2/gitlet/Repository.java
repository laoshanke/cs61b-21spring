package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public static final File STAGING = join(MYGITLET_DIR, "staging");

    public static final File BRANCH_DIR = join(MYGITLET_DIR, "branches");

    public static final File HEAD = join(MYGITLET_DIR, "head");
    public List<String> remove = new ArrayList<>();
    void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        MYGITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        COMMIT_DIR.mkdir();
        createFileplus(STAGING);
        BRANCH_DIR.mkdir();
        createFileplus(HEAD);
        Commit init = new Commit();
        init.save_commit();
        writeBranch("master", init.getId());
        head_point_branch("master");
        Addstage staging = new Addstage();
        staging.save();
    }
    void add(String fileName){
        if(!join(CWD, fileName).exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        blob blob = fileGetBLOB(fileName);
        String id = blob.getId();
        String id2 =get_branch_point_commit(get_head_point_branch()).getBlobId(fileName);
        if(id2!=null&&id2.equals(id)){//如果文件的当前工作版本与当前提交中的版本相同，则不要将其暂存以待添加；若该文件已在暂存区中
            // ，则将其从暂存区移除（比如当一个文件先被修改、添加，然后又改回其原始版本时就会出现这种情况）。
            Addstage stage = readObject(STAGING, Addstage.class);
            if(stage.stage.containsKey(fileName)){
                stage.remove(fileName);
                stage.save();
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
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Addstage addstage = readObject(STAGING, Addstage.class);
        if(addstage.stage.size()==0&&remove.size()==0){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(message);
        Set<String> set = deepcopy(addstage.stage.keySet());
        for(String name:set){
            commit.change_blobs(name, addstage.stage.get(name));//注意这两条指令的先后顺序
            addstage.remove(name);
        }
        List<String> set2 = deepcopy(remove);
        for(String name:set2){
            commit.remove_blob(name);
            remove.remove(name);//不要忘了移除remove
        }
        addstage.save();
        commit.save_commit();
        writeBranch( get_head_point_branch(), commit.getId());
    }
    void rm(String fileName){
        boolean flag = false;
        Addstage addstage = readObject(STAGING, Addstage.class);
        if(addstage.stage.containsKey(fileName)){
            addstage.remove(fileName);
            addstage.save();
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
        if(flag){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
    void log(){
        get_branch_point_commit(get_head_point_branch()).print_log_recursively();
    }
    void global_log(){
        List<String > dirlist = plainFilenamesIn(COMMIT_DIR);
        for(String name:dirlist){
            List<String> list = plainFilenamesIn(join(COMMIT_DIR, name));
            if(list.size()!=0) {
                for (String id : list) {
                    Commit commit = readObject(join(COMMIT_DIR, name, id), Commit.class);
                    commit.log_print();
                }
            }
        }
    }
    void find(String message){
        Boolean flag = false;
        List<String > dirlist = plainFilenamesIn(COMMIT_DIR);
        for(String name:dirlist){
            List<String> list = plainFilenamesIn(join(COMMIT_DIR, name));            if(list.size()!=0) {
                for (String id : list) {
                    Commit commit = readObject(join(COMMIT_DIR, name, id), Commit.class);
                    if(commit.getMessage().equals(message)){
                        System.out.println(commit.getId());
                        flag = true;
                    }
                }
            }
        }
        if(!flag){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }
    void status(){
        System.out.println("=== Branches ===");
        List<String> list = plainFilenamesIn(BRANCH_DIR);
        String head = get_head_point_branch();

        System.out.println("*"+head);
        Collections.sort(list);
        for(String name:list){
            if(!name.equals(head)){
                System.out.println(name);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Addstage addstage = readObject(STAGING, Addstage.class);
        List<String> list2 = new ArrayList<>(addstage.stage.keySet());
        Collections.sort(list2);
        for(String name:list2){
            System.out.println(name);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        Collections.sort(remove);
        for(String name:remove){
            System.out.println(name);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    static void createFileplus(File file) {//超级创造文件
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("file create error");
                System.exit(0);
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
            System.out.println("File does not exist.");
            System.exit(0);
        }
        return new blob(file);

    }
    Set<String> deepcopy(Set<String> set) {
        Set<String> newset = new HashSet<>();
        for(String name:set){
            newset.add(name);
        }
        return newset;
    }
    TreeMap<String, String> deepcopy(TreeMap<String, String> map) {
        TreeMap<String, String> newmap = new TreeMap<>();
        for(String name:map.keySet()){
            newmap.put(name, map.get(name));
        }
        return newmap;
    }
    List<String> deepcopy(List<String> list) {
        List<String> newlist = new ArrayList<>();
        for(String name:list){
            newlist.add(name);
        }
        return newlist;
    }
    /* TODO: fill in the rest of this class. */
}