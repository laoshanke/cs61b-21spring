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
        if(args[0].getClass() != String.class){
            System.out.println("Incorrect operands");
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
                if(args.length != 2|| args[1].getClass() != String.class){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                check_init();
                Repository.add(args[1]);
                break;
            case "commit":
                if(args.length != 2|| args[1].getClass() != String.class){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                check_init();
                Repository.commit(args[1]);
                break;
            case "rm":
                if(args.length != 2|| args[1].getClass() != String.class){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
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
            case "find":
                if(args.length != 2|| args[1].getClass() != String.class){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                check_init();
                Repository.find(args[1]);
                break;
            case "status":
                args_num_exam(args,1);
                check_init();
                Repository.status();
                break;
            case "checkout":
                if(!((args.length == 2 && args[1].getClass() == String.class)||(args.length == 3 && args[1] == "--" && args[2].getClass() == String.class)||(args.length == 4 && args[2] == "--" && args[1].getClass() == String.class && args[3].getClass() == String.class))){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                check_init();
                switch (args.length){
                    case 2:
                        Repository.checkout3(args[1]);
                        break;
                    case 3:
                        Repository.checkout1(args[2]);
                        break;
                    case 4:
                        Repository.checkout2(args[1],args[3]);
                        break;
                }

                break;
            case "branch":
                if(args.length != 2|| args[1].getClass() != String.class){
                System.out.println("Incorrect operands");
                System.exit(0);
                }
                Repository.branch(args[1]);
            case "rm-branch":
                if(args.length != 2|| args[1].getClass() != String.class){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                Repository.rm_branch(args[1]);
            case "reset":
                if(args.length != 2|| args[1].getClass() != String.class){
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                check_init();
                Repository.reset(args[1]);
                
            default:
            System.out.println("message No command with that name exists");
            System.exit(0);
        }
    }
    public static void args_num_exam( String[] args, int num){
        if(args.length != num){
            System.out.println("Incorrect operands");
            System.exit(0);
        }
    }
    public static void check_init(){
        if(!Repository.GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
