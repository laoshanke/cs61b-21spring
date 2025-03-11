package gitlet;

import java.io.File;
import java.io.FilenameFilter;
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
     * The .gitlet/objects/commit directory.
     */
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    /**
     * The .gitlet/objects/blobs directory.
     */
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
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
     * (this is called "The (Unix) Epoch", represented internally by the time 0.)
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
            COMMITS_DIR.mkdir();
            BLOBS_DIR.mkdir();
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
        save_commit(initial_commit);
        branch_create_update("master", initial_commit);
        head_pointer_update("master");
    }

    /**
     * 创建分支或更新
     */
    public static void branch_create_update(String branch_name, Commit commit) {
        File branch_file = join(REFS_DIR, branch_name);
        plus_file_create(branch_file);
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
     * Description: Adds a copy of the file as it currently exists to the staging area (see the description of the commit command). For this reason, adding a file is also called staging the file for addition. Staging an already-staged file overwrites the previous entry in the staging area with the new contents. The staging area should be somewhere in .gitlet. If the current working version of the file is identical to the version in the current commit, do not stage it to be added, and remove it from the staging area if it is already there (as can happen when a file is changed, added, and then changed back to it's original version). The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
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
        if (existingBlobId != null && existingBlobId.equals(nowblob.getID())) {
            // 当前提交已存在同名且内容相同的文件，无需暂存
            File addFile = join(ADD_DIR, nowblob.getID());
            if (addFile.exists()) {
                addFile.delete(); // 清理暂存区中的重复文件
            }
            return;
        }
        /** 如果文件已经存在rm区中，则删除rm区中文件 */
        File rmfile = join(REMOVE_DIR, nowblob.getID());
        if (rmfile.exists()) {
            rmfile.delete();
        }
        nowblob.save_add();
    }
    /**
     * 返回工作区一个文件名对应生成的blob对象
     */
    public static blob from_name_get_blob(String file_name) {
        return new blob(file_name,readContents(join(CWD, file_name)));
    }
    /**
     * 返回当前头指针指着的分支的当前提交地址
     */
    public static File get_head_branch_pointer_file() {
        String head_pointer_id = readContentsAsString(HEAD_FILE);
        File branch_file = join(REFS_DIR, head_pointer_id);
        File head_pointer_file = join(COMMITS_DIR, readContentsAsString(branch_file));
        return head_pointer_file;
    }

    /**
     * 返回头指针指着的分支的当前提交
     */
    public static Commit get_head_branch_pointer_commit() {
        String commit_id = readContentsAsString(join(REFS_DIR,readContentsAsString(HEAD_FILE)));
        return read_commit_from_id(commit_id);
    }
    /**
     * 返回对应指针的blob
     */
    public static blob read_blob_from_id(String id) {
        File file = join(BLOBS_DIR,id.substring(0,2),id.substring(2));
        return readObject(file,blob.class);
    }

    /**
     * 返回对应指针Blob的名字
     */
    public static String get_blob_name(String id) {
        return read_blob_from_id(id).getName();
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
                save_blob(read_blob_from_id(addfile));
            }
        }
        /** remove the blob in rm dir to the blobids*/
        List<String> rmfiles = Utils.plainFilenamesIn(Repository.REMOVE_DIR);
        if (rmfiles != null) {
            for (String rmfile : rmfiles) {
                File rmFile = join(REMOVE_DIR, rmfile);
                blob b = readObject(rmFile, blob.class);
                String name_rm = b.getName();
                if (new_commit.blobids_containsKey(name_rm)) {
                    new_commit.blobids_remove(name_rm);
                }
            }
        }
        save_commit(new_commit);
        branch_create_update(readContentsAsString(HEAD_FILE), new_commit);
        // 更新暂存区
        stage_update();
    }
    /**
     * 更新暂存区
     */
    public static void stage_update() {
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
            parent_commit[0] = read_commit_from_id(now_commit.getParent_ids()[0]);
            if(now_commit.getParent_ids()[1] != null){
                parent_commit[1] = read_commit_from_id(now_commit.getParent_ids()[1]);
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
        List<String> commit_ids = getAllCommitHashes(COMMITS_DIR);
        for(String commit_id : commit_ids){
            Commit now_commit = read_commit_from_id(commit_id);
            now_commit.dump();
        }
    }
    public static List<String> getAllCommitHashes(File commitDir) {
        if (!commitDir.isDirectory()) {
            return null;
        }
        // 获取所有两位名称的子目录
        File[] subDirs = commitDir.listFiles(file ->
                file.isDirectory() && file.getName().length() == 2
        );

        if (subDirs == null) {
            return Collections.emptyList();
        }

        List<String> hashes = new ArrayList<>();

        for (File subDir : subDirs) {
            // 获取子目录中所有38位长度的普通文件
            String[] files = subDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isFile() && name.length() == 38;
                }
            });

            if (files == null) {
                continue; // 跳过无法访问的子目录
            }
            String prefix = subDir.getName();
            for (String file : files) {
                hashes.add(prefix + file);
            }
        }
        Collections.sort(hashes); // 按字典序排序
        return hashes;
    }
    public  static void find( String message){
        boolean exists = false;
        List<String> commit_ids = getAllCommitHashes(COMMITS_DIR);
        for(String commit_id : commit_ids){
            Commit now_commit = read_commit_from_id(commit_id);
            if(now_commit.getMessage().equals(message)){
               System.out.println(commit_id);
               exists = true;
            }
        }
        if(!exists){
            System.out.println("Found no commit with that message.");
        }
    }
    public static void status(){
        System.out.println("=== Branches ===");
        String head_branch = readContentsAsString(HEAD_FILE);
        List<String> branch_names = Utils.plainFilenamesIn(Repository.REFS_DIR);
        branch_names.remove( head_branch);
        System.out.println("*" + head_branch);
        Collections.sort(branch_names);
        for(String branch_name : branch_names){
            System.out.println(branch_name);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> addfiles = Utils.plainFilenamesIn(Repository.ADD_DIR);
        Collections.sort(addfiles);
        for(String addfile : addfiles){
            System.out.println(Repository.get_blob_name(addfile));
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> rmfiles = Utils.plainFilenamesIn(Repository.REMOVE_DIR);
        Collections.sort(rmfiles);
        for(String rmfile : rmfiles){
            System.out.println(Repository.get_blob_name(rmfile));
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> cwd_files = Utils.plainFilenamesIn(CWD);
        List<String> wait_print = new ArrayList<>();
        for(String cwd_file : cwd_files){
           String blob_id =filename_track_commit(cwd_file);
           /**  在当前提交中被跟踪，但在工作目录中已修改且未暂存；*/
           if(blob_id != null && !blob_id.equals(Repository.from_name_get_blob(cwd_file).getID()) && filename_add(cwd_file) == null){
               String name_rm = cwd_file + " (modified)";
               wait_print.add(name_rm);
           }
           /** 已暂存以供添加，但其内容与工作目录中的内容不同；*/
           if(filename_add(cwd_file)!= null && !filename_add(cwd_file).equals(Repository.from_name_get_blob(cwd_file).getID()) ){
               String name_add = cwd_file + " (modified)";
               wait_print.add(name_add);
           }
        }
        /**已暂存以供添加，但在工作目录中被删除 */
        if(addfiles != null){
            for (String addfile : addfiles) {
                if (!cwd_files.contains(Repository.get_blob_name(addfile))) {
                    String name_rm = Repository.get_blob_name(addfile) + " (deleted)";
                    wait_print.add(name_rm);
                }
            }
        }
        /**未暂存以供删除，但在当前提交中被跟踪且从工作目录中被删除。 */
        Commit now_commit = get_head_branch_pointer_commit();
        List<String> filenames = new ArrayList<>( now_commit.getblobids().keySet());
        for( String filename : filenames){
            if (!cwd_files.contains(filename) && filename_rm(filename)== null) {
                String name_rm = filename + " (deleted)";
                wait_print.add(name_rm);
            }
        }
        Collections.sort( wait_print);
        for(String wait_print_file : wait_print){
            System.out.println(wait_print_file);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        Collections.sort(cwd_files);
        for(String cwd_file : cwd_files){
            if(filename_track_commit(cwd_file) == null && filename_add(cwd_file) == null){
                System.out.println(cwd_file);
            }
        }
    }
    /**
     * 获取文件名在当前commit中的blobid。没有被追踪则返回null
     */
    public static String filename_track_commit(String file_name){
        Map<String,String> blobids = get_head_branch_pointer_commit().getblobids();
        if(blobids.containsKey(file_name)){
            return blobids.get(file_name);
        }
        return null;
    }
    /**返回一个文件名在暂存区对应的blob值 ，不在暂存区则返回null*/
    public static  String filename_add(String file_name){
        List<String> addfiles = Utils.plainFilenamesIn(Repository.ADD_DIR);
        for(String addfile : addfiles){
            if(Repository.get_blob_name(addfile).equals(file_name)){
                return addfile;
            }
        }
        return null;
    }
    /**返回一个文件名在rm区对应的blob值 ，不在暂存区则返回null*/
    public static  String filename_rm(String file_name){
        List<String> rmfiles = Utils.plainFilenamesIn(Repository. REMOVE_DIR);
        for(String rmfile : rmfiles){
            if(Repository.get_blob_name(rmfile).equals(file_name)){
                return rmfile;
            }
        }
        return null;
    }
    /****获取给定分支头部提交中的所有文件，并将它们放入工作目录，覆盖已存在的文件版本。 */
    public static void checkout_commit_files(String commit_id){
        Commit now_commit = get_head_branch_pointer_commit();
        Commit checkout_commit = read_commit_from_id(commit_id);
        List<String> filenames = new ArrayList<>( checkout_commit.getblobids().keySet());
        for( String filename : filenames){
            File cwd_file = join(CWD,filename);
            plus_file_create(cwd_file);
            writeContents(cwd_file,Repository.read_blob_from_id(checkout_commit.blobids_get(filename)).getContent());
        }
    }
    /**  当前分支中被跟踪但在被检出分支中不存在的任何文件都将被删除。*/
    public static void rm_commit_notracked_files(String id) {
        Commit now_commit = get_head_branch_pointer_commit();
        Commit checkout_commit = read_commit_from_id(id);
        List<String> com_filenames =new ArrayList<>( now_commit.getblobids().keySet());
        for( String com_filename : com_filenames){
            if(!checkout_commit.blobids_containsKey( com_filename)){
                join( CWD, com_filename).delete();
                now_commit.blobids_remove( com_filename);
            }
        }
    }
    public static void checkout_reset_wronghelper(String id) {
        List<String> files = Utils.plainFilenamesIn(CWD);
        for(String file : files){
            if((!get_head_branch_pointer_commit().blobids_containsKey(file)) && read_commit_from_id(id).blobids_containsKey(file)){
                System.out.println( "There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }
    public static void checkout3(String name){
        List<String> branchname = plainFilenamesIn(REFS_DIR);
        if(name.equals(readContentsAsString(HEAD_FILE))){
            System.out.println("No need to checkout the current branch.");
            return;
        }
        if(branchname.contains(name)){/**获取给定分支头部提交中的所有文件，并将它们放入工作目录，覆盖已存在的文件版本。 此外，在该命令结束时，给定的分支将被视为当前分支 (HEAD)。
         当前分支中被跟踪但在被检出分支中不存在的任何文件都将被删除。暂存区将被清除，除非被检出的分支是当前分支（请参阅下面的失败情况）。 */
            // 先检查未跟踪文件
            checkout_reset_wronghelper(readContentsAsString(join(REFS_DIR,name)));
            
            checkout_commit_files(readContentsAsString(join(REFS_DIR,name)));
            stage_update();
            head_pointer_update(name);
            rm_commit_notracked_files(readContentsAsString(join(REFS_DIR,name)));
        }else{
            System.out.println("No such branch exists.");
            System.exit(0);
        }
    }
    public static void checkout1(String name) {
        /** Takes the version of the file as it exists in the head commit and puts it in the working directory,
         *  overwriting the version of the file that's already there if there is one. The new version of the file is not staged.
         *  If the file does not exist in the previous commit, abort,
         *  printing the error message File does not exist in that commit. Do not change the CWD.*/
        Commit now_commit = get_head_branch_pointer_commit();
        if(!now_commit.blobids_containsKey(name)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String id = now_commit.blobids_get(name);
        File blob_file = join(BLOBS_DIR,id.substring(0,2),id.substring(2));
        File cwd_file = join(CWD,name);
        plus_file_create(cwd_file);
        byte[] content = Utils.readContents(blob_file);
        Utils.writeContents(cwd_file,content);
    }
    public  static  void checkout2(String id, String file_name){
        /**Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory,
         * overwriting the version of the file that's already there if there is one. The new version of the file is not staged.*/
        // 支持缩短的commit ID
        String fullId = findCommitId(id);
        if (fullId == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        
        Commit now_commit = read_commit_from_id(fullId);
        if(!now_commit.blobids_containsKey(file_name)){
            System.out.println("File does not exist in that commit.");
            return;
        }
        String blob_id = now_commit.blobids_get(file_name);
        byte[] content = Utils.readContents(join(BLOBS_DIR,blob_id.substring(0,2),blob_id.substring(2)));
        plus_file_create(join(CWD,file_name));
        Utils.writeContents(join(CWD,file_name),content);
    }
    public static void branch( String branch_name) {
       /**: Creates a new branch with the given name, and points it at the current head commit.
        * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
        * This command does NOT immediately switch to the newly created branch (just as in real Git).
        * Before you ever call branch, your code should be running with a default branch called "master". */
        File branch_file = join(REFS_DIR,branch_name);
        if(branch_file.exists()){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        try {
            branch_file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(branch_file,readContentsAsString(get_head_branch_pointer_file()));
    }
    public static void rm_branch(String branch_name){
        File branch_file = join(REFS_DIR,branch_name);
        if(!branch_file.exists()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if(branch_name.equals(readContentsAsString(HEAD_FILE))){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch_file.delete();
    }
    public static void reset(String id){
        // 支持缩短的commit ID
        String fullId = findCommitId(id);
        if (fullId == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        
        // 先检查未跟踪文件
        checkout_reset_wronghelper(fullId);
        
        // 然后执行reset
        checkout_commit_files(fullId);
        rm_commit_notracked_files(fullId);
        
        // 更新当前分支指向
        branch_create_update(readContentsAsString(HEAD_FILE), read_commit_from_id(fullId));
        
        // 清空暂存区
        stage_update();
    }
    public static void save_commit(Commit commit){
        File dir = join(COMMITS_DIR,commit.get_id().substring(0,2));
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = join(dir,commit.get_id().substring(2));
        plus_file_create(file);
        writeObject(file,commit);
    }
    public static void save_blob(blob blob){
        File dir = Utils.join(Repository.BLOBS_DIR, blob.getID().substring(0,2));
        if(!dir.exists()){
            dir.mkdir();
        }
        File file =  Utils.join(dir,blob.getID().substring(2));
        plus_file_create(file);
        Utils.writeObject(file, blob);
    }
    /**一个考虑创建错误版本的创建文件 */
    public static void plus_file_create(File file){
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Commit read_commit_from_id(String id){
        File file = join(COMMITS_DIR,id.substring(0,2),id.substring(2));
        return readObject(file,Commit.class);
    }

    /**
     * 根据缩短的commit ID查找完整的commit ID
     */
    public static String findCommitId(String shortId) {
        List<String> allCommits = getAllCommitHashes(COMMITS_DIR);
        for (String id : allCommits) {
            if (id.startsWith(shortId)) {
                return id;
            }
        }
        return null;
    }

    }






