package gitlet;

import static gitlet.Repository.GITLET_DIR;

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
        switch(firstArg) {
            case "init":
                if(args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository repo = new Repository();
                repo.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            default:
                System.out.println("No command with that name exists.");
        }
    }
    void initExam(){
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
