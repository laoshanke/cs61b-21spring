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
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        if(args[0].getClass() != String.class) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String firstArg = args[0];
        Repository repo = new Repository();
        switch(firstArg) {
            case "init":
                if(args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.init();
                break;
            case "add":
                if(args.length != 1 && args[1].getClass()!= String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.add(args[1]);
                break;
            case "commit":
                if(args.length!=2 && args[1].getClass()!= String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.commit( args[1]);
                break;
            case "rm":
                if(args.length!=2 && args[1].getClass()!= String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.rm(args[1]);
                break;
            case "log":
                if(args.length!=1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.log();
                break;
            case "global-log":
                if(args.length!=1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.global_log();
                break;
            case "find":
                if(args.length!=2 && args[1].getClass()!= String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.find(args[1]);
                break;
            case "status":
                if(args.length!=1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.status();
                break;
            case "checkout":
                if(args.length==2 && args[1].getClass()== String.class) {
                    initExam();
                    repo.checkout3(args[1]);
                } else if(args.length==3 && args[1].equals("--")&& args[2].getClass()== String.class) {
                    initExam();
                    repo.checkout1(args[2]);
                } else if ( args.length==4 && args[1].getClass()== String.class&& args[2].equals("--")&& args[3].getClass()== String.class) {
                    initExam();
                    repo.checkout2(args[1], args[3]);
                }else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                if(args.length!=2 && args[1].getClass()== String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.branch(args[1]);
                break;
            case "rm-branch":
                if(args.length!=2 && args[1].getClass()== String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.rm_branch(args[1]);
                break;
            case "reset":
                if(args.length !=2 && args[1].getClass() == String.class) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.reset(args[1]);
                break;
            case "merge":
                if( args.length !=2 && args[1].getClass() == String.class ) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                initExam();
                repo.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
    static void initExam() {
        if ( !GITLET_DIR.exists() ) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
