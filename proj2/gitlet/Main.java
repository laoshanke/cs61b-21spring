package gitlet;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.error;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if(args.length == 0) {
            error("Please enter a command.");
        }
        if(args[0].getClass() != String.class) {
            error("Incorrect operands.");
        }
        String firstArg = args[0];
        Repository repo = new Repository();
        switch(firstArg) {
            case "init":
                if(args.length != 1) {
                    error( "Incorrect operands.");
                }
                repo.init();
                break;
            case "add":
                if(args.length != 1 && args[1].getClass()!= String.class) {
                    error("Incorrect operands.");
                }
                initExam();
                repo.add(args[1]);
                break;
            case "commit":
                if(args.length!=2 && args[1].getClass()!= String.class){
                    error("Incorrect operands.");
                }
                initExam();
                repo.commit( args[1]);
                break;
            case "rm":
                if(args.length!=2 && args[1].getClass()!= String.class){
                    error("Incorrect operands.");
                }
                initExam();
                repo.rm(args[1]);
                break;
            case "log":
                if(args.length!=1){
                    error("Incorrect operands.");
                }
                initExam();
                repo.log();
                break;
            case "global-log":
                if(args.length!=1){
                    error("Incorrect operands.");
                }
                initExam();
                repo.global_log();
                break;
            case "find":
                if(args.length!=2 && args[1].getClass()!= String.class){
                    error("Incorrect operands.");
                }
                initExam();
                repo.find(args[1]);
            case "status":
                if(args.length!=1){
                    error("Incorrect operands.");
                }
                initExam();
                repo.status();
            default:
                error("No command with that name exists.");
        }
    }
    static void initExam(){
        if (!GITLET_DIR.exists()) {
            error("Not in an initialized Gitlet directory.");
        }
    }
}