package gitlet;

import java.io.File;
import java.io.IOException;

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

    public static final File OBJECT_DIR = join(MYGITLET_DIR, "objects");

    public static final File STAGING_DIR = join(MYGITLET_DIR, "staging");

    public static final File BRANCH_DIR = join(MYGITLET_DIR, "branches");

    public static final File HEAD = join(MYGITLET_DIR, "head");

    public static final File RM_Dir = join(MYGITLET_DIR, "rm");
    void init() {
        if (GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        MYGITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        STAGING_DIR.mkdir();
        BRANCH_DIR.mkdir();
        createFileplus(HEAD);
        RM_Dir.mkdir();
        Commit init = new Commit();
        init.save_commit();
        createBranch("master", init.getId());
        head_point_commit("master");
    }
    static void createFileplus(File file) {
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw Utils.error("crate file error");
            }
        }
    }
    static void createBranch(String name, String id) {
        File file = join(BRANCH_DIR, name);
        createFileplus(file);
        writeContents(file, id);
    }
    static void head_point_commit(String name) {
        writeContents(HEAD, name);
    }
    /* TODO: fill in the rest of this class. */
}
