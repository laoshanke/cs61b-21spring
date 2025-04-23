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
            if(stage.getstage().containsKey(fileName)){
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
        if(addstage.getstage().size()==0&&remove.size()==0){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(message);
        Set<String> set = deepcopy(addstage.getstage().keySet());
        for(String name:set){
            commit.change_blobs(name, addstage.getstage().get(name));//注意这两条指令的先后顺序
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
        if(addstage.getstage().containsKey(fileName)){
            addstage.remove(fileName);
            addstage.save();
            flag = true;
        }
        if(get_branch_point_commit(get_head_point_branch()).contains_name(fileName)){
            remove.add(fileName);
            flag = true;
            File file = join(CWD, fileName);
            if(file.exists()){
                file.delete();
            }
        }
        if(!flag){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
    void log(){
        get_branch_point_commit(get_head_point_branch()).print_log_recursively();
    }
    void global_log(){
        List<String > dirlist = getSubdirectoryNames(COMMIT_DIR);
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
        List<String > dirlist = getSubdirectoryNames(COMMIT_DIR);
        for(String name:dirlist){
            List<String> list = plainFilenamesIn(join(COMMIT_DIR, name));
            if(list.size()!=0) {
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
        List<String> list2 = new ArrayList<>(addstage.getstage().keySet());
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
        List<String> list3 = plainFilenamesIn(CWD);
        List<String> list4 = new ArrayList<>();
        for (String name : list3) {
            if(check_file_in_nowcommit(name)&&!addstage.getstage().containsKey(name)&&!fileGetBLOB(name).getId().equals(get_branch_point_commit(get_head_point_branch()).getBlobId(name))){
            String id = name+ " (modified)";
            list4.add(id);
            }
            if(addstage.getstage().containsKey(name)&&!fileGetBLOB(name).getId().equals(addstage.getstage().get(name))){
                String id = name+ " (modified)";
                list4.add(id);
            }

        }
        Set<String> set = addstage.getstage().keySet();
        for(String name:set){
            if(!list3.contains(name)){
                String id = name+ " (deleted)";
                list4.add(id);
            }
        }
        Set<String> list5 = get_branch_point_commit(get_head_point_branch()).getnametoblobs().keySet();
        for(String name:list5){
            if(!list3.contains(name)&&!remove.contains(name)){
                String id = name+ " (deleted)";
                list4.add(id);
            }
        }
        Collections.sort(list4);
        for(String name:list4){
            System.out.println(name);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        List<String> list6 = new ArrayList<>();
        for(String name:list3){
            if(!check_file_in_nowcommit(name)&&!addstage.getstage().containsKey(name)){
                list6.add(name);
            }
            if(remove.contains(name)){
                list6.add(name);
            }
        }
        Collections.sort(list6);
        System.out.println();
    }
    void checkout1(String name) {//把头commit中的name文件复制到工作区
        checkout2( get_branch_point_commit(get_head_point_branch()).getId(), name);
    }
    void checkout2(String id, String fileName) {
        //把id对应的commit中的文件复制到工作区
        File dir = join(COMMIT_DIR, id.substring(0,2));
        File file = join(dir, id.substring(2,40));
        if(!dir.exists()||!file.exists()){
            System.out.println(" No commit with that id exists.");
            System.exit(0);
        }
        Commit pointcommit = readObject(join(COMMIT_DIR, id.substring(0,2), id.substring(2,40)), Commit.class);
        if(!pointcommit.contains_name(fileName)){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String id2 = pointcommit.getBlobId(fileName);
        blob blob = readObject(join(OBJECT_DIR, id2.substring(0,2), id2.substring(2,40)), blob.class);
        createFileplus(join(CWD, fileName));
        writeContents(join(CWD, fileName), blob.getContent());
    }
    void checkout3(String name) {//把分支name中的文件复制到工作区
        if(!plainFilenamesIn(BRANCH_DIR).contains(name)){
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if(get_head_point_branch().equals(name)){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        List<String> list = plainFilenamesIn(CWD);
        Commit nowcommit = get_branch_point_commit(get_head_point_branch());
        Commit commit = get_branch_point_commit(name);
        for(String name2:list){
            if(nowcommit.contains_name(name2)&&!commit.contains_name(name2)){
                join(CWD, name2).delete();
            }
            if(!nowcommit.contains_name(name2)&&commit.contains_name(name2)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        Set<String> set = commit.getnametoblobs().keySet();
        for(String name2:set) {
            checkout2(commit.getId(), name2);
        }
        head_point_branch(name);
        addstage_clear();
    }
    void branch(String name) {//创建一个分支并指向当前头提交
        Commit commit = get_branch_point_commit(get_head_point_branch());
        String id = commit.getId();
        File file = join(BRANCH_DIR, name);
        if(file.exists()){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeBranch(name, id);
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
    static void addstage_clear() {//清空暂存区
        Addstage stage = new Addstage();
        stage.save();
    }
    List<String> deepcopy(List<String> list) {
        List<String> newlist = new ArrayList<>();
        for(String name:list){
            newlist.add(name);
        }
        return newlist;
    }
    public static List<String> getSubdirectoryNames(File dir) {//获取子目录名列表
        List<String> dirNames = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            return dirNames; // 无效目录返回空列表
        }
        File[] subItems = dir.listFiles();
        if (subItems == null) {
            return dirNames; // 目录存在但无权限访问时返回空列表
        }
        for (File item : subItems) {
            if (item.isDirectory()) {
                dirNames.add(item.getName()); // 添加目录名（非路径）
            }
        }
        return dirNames;
    }

    boolean check_file_in_nowcommit(String fileName) {//判断当前commit中是否有这个文件
        Commit nowcommit = get_branch_point_commit(get_head_point_branch());
        return nowcommit.contains_name(fileName);
    }
    /* TODO: fill in the rest of this class. */
}