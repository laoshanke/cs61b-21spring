package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The .gitlet/objects directory.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /**
     * The .gitlet/refs directory.
     */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /**
     * The .gitlet/refs/heads directory.
     */
    public static final File HEAD_FILE = join(REFS_DIR, "HEAD");
    /**
     * The .gitlet/staging_area directory.
     */
    public static final File STAGING_AREA = join(GITLET_DIR, "staging_area");
    /**
     * The .gitlet/staging_area/remove directory.
     */
    public static final File REMOVE_DIR = join(STAGING_AREA, "remove");
    /**
     * The .gitlet/staging_area/add directory.
     */
    public static final File ADD_DIR = join(STAGING_AREA, "add");


    /**
     * Initializes a new repository. init
     * Usage: java gitlet.Main init
     * <p>
     * Description: Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit:
     * a commit that contains no files and has the commit message initial commit
     * (just like that, with no punctuation).
     * It will have a single branch: master,
     * which initially points to this initial commit, and master will be the current branch. T
     * he timestamp for this initial commit will be 00:00:00 UTC,
     * Thursday, 1 January 1970 in whatever format you choose for dates
     * (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     * Since the initial commit in all repositories created by Gitlet will have exactly the same content,
     * it follows that all repositories will automatically share this commit (they will all have the same UID) and all commits in all repositories will trace back to it.
     * <p>
     * Runtime: Should be constant relative to any significant measure.
     * <p>
     * Failure cases: If there is already a Gitlet version-control system in the current directory, it should abort. It should NOT overwrite the existing system with a new one. Should print the error message A Gitlet version-control system already exists in the current directory.
     * <p>
     * Dangerous?: No
     * <p>
     * Our line count: ~15
     */
    public static void init() {
        /**如果文件夹存在*，则报错退出*/
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        }
        /**创建文件夹*/
        try {
            GITLET_DIR.mkdir();
            OBJECTS_DIR.mkdir();
            REFS_DIR.mkdir();
            HEAD_FILE.createNewFile();
            STAGING_AREA.mkdir();
            REMOVE_DIR.mkdir();
            ADD_DIR.mkdir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /**初始化commit，并新建master分支，并存储commit，更新头指针*/
        Commit initial_commit = new Commit();
        initial_commit.save();
        branch_create_update("master", initial_commit);
        head_pointer_update("master");
    }

    /**
     * 创建分支或更新
     */
    public static void branch_create_update(String branch_name, Commit commit) {
        File branch_file = join(REFS_DIR, branch_name);
        if(!branch_file.exists()){
            try {
                branch_file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeContents(branch_file, commit.get_id());
    }

    /**
     * 更新头指针
     */
    public static void head_pointer_update(String branch_name) {
        writeContents(HEAD_FILE, branch_name);
    }

    /**
     * add
     * Usage: java gitlet.Main add [file name]
     * <p>
     * Description: Adds a copy of the file as it currently exists to the staging area (see the description of the commit command). For this reason, adding a file is also called staging the file for addition. Staging an already-staged file overwrites the previous entry in the staging area with the new contents. The staging area should be somewhere in .gitlet. If the current working version of the file is identical to the version in the current commit, do not stage it to be added, and remove it from the staging area if it is already there (as can happen when a file is changed, added, and then changed back to it’s original version). The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     * <p>
     * Runtime: In the worst case, should run in linear time relative to the size of the file being added and lgN
     * , for N
     * the number of files in the commit.
     * <p>
     * Failure cases: If the file does not exist, print the error message File does not exist. and exit without changing anything.
     * <p>
     * Dangerous?: No
     * <p>
     * Our line count: ~20
     * <p>
     * Differences from real git: In real git, multiple files may be added at once. In gitlet, only one file may be added at a time.
     * <p>
     * Suggested Lecture(s): Lecture 16 (Sets, Maps, ADTs), Lecture 19 (Hashing)
     */
    public static void add(String file_name) {
        File file = join(CWD, file_name);
        /** 如果文件不存在，则打印错误信息并退出 */
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Commit now_commit = get_head_branch_pointer_commit();
        blob nowblob = from_name_get_blob(file_name);
        /** 如果文件已经存在commit和add区中，则删除add区中文件
         * 不然只是退出 */
        String existingBlobId = now_commit.getblobids().get(file_name);
        if (existingBlobId != null && existingBlobId.equals(nowblob.getId())) {
            // 当前提交已存在同名且内容相同的文件，无需暂存
            File addFile = join(ADD_DIR, nowblob.getId());
            if (addFile.exists()) {
                addFile.delete(); // 清理暂存区中的重复文件
            }
            return;
        }
        /** 如果文件已经存在rm区中，则删除rm区中文件 */
        File rmfile = join(REMOVE_DIR, nowblob.getId());
        if (rmfile.exists()) {
            rmfile.delete();
        }
        nowblob.save_add();
    }
    /**
     * 返回工作区一个文件名对应生成的blob对象
     */
    public static blob from_name_get_blob(String file_name) {
        File file = join(CWD, file_name);
        byte[] content = readContents(file);
        String id = sha1(file_name,content);
        blob nowblob = new blob(file_name, content);
        return nowblob;
    }
    /**
     * 返回当前头指针指着的分支的当前提交地址
     */
    public static File get_head_branch_pointer_file() {
        String head_pointer_id = readContentsAsString(HEAD_FILE);
        File branch_file = join(REFS_DIR, head_pointer_id);
        File head_pointer_file = join(OBJECTS_DIR, readContentsAsString(branch_file));
        return head_pointer_file;
    }

    /**
     * 返回头指针指着的分支的当前提交
     */
    public static Commit get_head_branch_pointer_commit() {
        File head_pointer_file = get_head_branch_pointer_file();
        Commit head_pointer_commit = readObject(head_pointer_file, Commit.class);
        return head_pointer_commit;
    }

    /**
     * 返回对应指针的blob
     */
    public static blob get_blob(String id) {
        File blob_file = join(OBJECTS_DIR, id);
        blob nowblob = readObject(blob_file, blob.class);
        return nowblob;
    }

    /**
     * 返回对应指针Blob的名字
     */
    public static String get_blob_name(String id) {
        return get_blob(id).getName();
    }

    /**
     * 从储存库删除一个id对应的对象
     */
    public static void rm_objects(String id) {
        File blob_file = join(OBJECTS_DIR, id);
        blob_file.delete();
    }

    public static void commit(String message) {
        Commit new_commit = new Commit(message);
        List<String> addfiles = Utils.plainFilenamesIn(Repository.ADD_DIR);
        /** add the blob in add dir to the blobids 如果文件名重复那么更新id，否则加新的键值对并储存blob到object文件夹*/
        if (addfiles != null) {
            for (String addfile : addfiles) {
                String name_add = Repository.get_blob_name(addfile);
                String id_same_name = new_commit.blobids_get(name_add);
                if (id_same_name != null) {
                    new_commit.blobids_remove(name_add);
                }
                new_commit.blobids_put(name_add, addfile);
                get_blob(addfile).save_objects();
            }
        }
        /** remove the blob in rm dir to the blobids*/
        List<String> rmfiles = Utils.plainFilenamesIn(Repository.REMOVE_DIR);
        if (rmfiles != null) {
            for (String rmfile : rmfiles) {
                String name_rm = Repository.get_blob_name(rmfile);
                if (new_commit.blobids_containsKey(name_rm)) {
                    new_commit.blobids_remove(name_rm);
                }
            }
        }
        new_commit.save();
        branch_create_update(readContentsAsString(HEAD_FILE), new_commit);
        /** 更新暂存区*/
        deleteDirectory(Repository.ADD_DIR);
        deleteDirectory(Repository.REMOVE_DIR);
        REMOVE_DIR.mkdir();
        ADD_DIR.mkdir();
    }
    /**
     * 删除一个目录和它下的所有文件
     */
    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    boolean success = deleteDirectory(child);
                    if (!success) return false;
                }
            }
        }
        return true;
    }
    /**
     * rm命令
     */
    public static void rm (String file_name){
        boolean file_exist = false;
        /** 如果在add区中，取消暂存*/
     List<String> addfiles = Utils.plainFilenamesIn(Repository.ADD_DIR);
     for( String addfile : addfiles){
         if(Repository.get_blob_name(addfile).equals(file_name)){
             File addfile_file = join(Repository.ADD_DIR,addfile);
             file_exist = true;
             addfile_file.delete();
             break;
         }
     }
     /**被追踪则取代哦追踪且把文件从cwd区域中移除 */
     Commit now_commit = get_head_branch_pointer_commit();
     if(now_commit.blobids_containsKey(file_name)){
         file_exist = true;
         File rmfile = join(Repository.REMOVE_DIR,now_commit.blobids_get(file_name));
         if(!rmfile.exists()){
             try {
                 rmfile.createNewFile();
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
         join(CWD, file_name).delete();
     }
     if(!file_exist){
         System.out.println("No reason to remove the file.");
     }
    }
    public static  void log(){
        Commit now_commit = get_head_branch_pointer_commit();
        log_print_helper(now_commit);
    }
    /** 返回父提交*/
    public static Commit[] get_parent_commit(Commit now_commit){
        Commit[] parent_commit = new Commit[2];
        if(now_commit.getParent_ids()[0] != null){
            parent_commit[0] = readObject(join(Repository.OBJECTS_DIR,now_commit.getParent_ids()[0]),Commit.class);
            if(now_commit.getParent_ids()[1] != null){
                parent_commit[1] = readObject(join(Repository.OBJECTS_DIR,now_commit.getParent_ids()[1]),Commit.class);
            }
        }
        return parent_commit;
    }
    private static void log_print_helper(Commit now_commit){
        now_commit.dump();
        if(now_commit.getParent_ids()[0] != null){
            log_print_helper(get_parent_commit(now_commit)[0]);
        }
    }
    public static void  global_log(){

    }
}





