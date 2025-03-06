package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if(args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                args_num_exam(args,1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                args_num_exam(args,2);
                check_init();
                Repository.add(args[1]);
                break;
            case "commit":
                args_num_exam(args,2);
                check_init();
                Repository.commit(args[1]);
                break;
            case "rm":
                args_num_exam(args,2);
                check_init();
                Repository.rm(args[1]);
                break;
            case "log":
                args_num_exam(args,1);
                check_init();
                Repository.log();
                break;
            case "global-log":
                args_num_exam(args,1);
                check_init();
                Repository.global_log();
                break;
            default:
            System.out.println("message No command with that name exists");
            System.exit(0);
        }
    }
    public static void args_num_exam( String[] args, int num){
        if(args.length != num){
            System.out.println("Incorrect operands");
        }
    }
    public static void check_init(){
        if(!Repository.GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
