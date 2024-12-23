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
                if (args.length>=2&&!args[1].trim().isEmpty()){
                    Repository.commit(args[1]);
                }else {
                    System.out.println("Please enter a commit message.");
                }
                break;
            case "rm":
                Repository.rm2(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if (args[1].equals("--")){
                    Repository.rm(args[1]);
                }else if (args[1].equals("--")){}
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
