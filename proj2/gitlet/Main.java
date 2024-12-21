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
        String firstArg = args[0];
        if (args.length == 0){
            System.out.println("Please enter a command.");
        }
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.init();
                break;
            case "add":
                Repository.add(args[1]);
                // TODO: handle the `add [filename]` command
                break;
            case "commit":
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.rm(args[1]);
                break;
            case "global-log":
                Repository.rm(args[1]);
                break;
            case "find":
                Repository.rm(args[1]);
                break;
            case "status":
                Repository.rm(args[1]);
                break;
            case "checkout":
                Repository.rm(args[1]);
                break;
            case "branch":
                Repository.rm(args[1]);
                break;
            case "rm-branch":
                Repository.rm(args[1]);
                break;
            case "reset":
                Repository.rm(args[1]);
                break;
            case "merge":
                Repository.rm(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
