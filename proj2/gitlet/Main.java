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

        if (args.length == 0){
            System.out.println("Please enter a command.");
            return;
//        }else if (!Repository.GITLET_DIR.exists()){
//            System.out.println("Not in an initialized Gitlet directory.");
//            return;
        }
        if (!Repository.isInitialized()&&!args[0].equals("init")) {
            System.out.println("Not in an initialized Gitlet directory.");
            return; // 如果没有初始化，直接返回，停止执行其他命令
        }
        String firstArg = args[0];

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

                if (args.length == 2) {
                    // `checkout [branch name]`
                    String branchName = args[1];
                    Repository.checkout3(branchName);
                } else if (args.length == 3 && args[1].equals("--")) {
                    // `checkout -- [file name]`
                    String fileName = args[2];
                    Repository.checkout1(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    // `checkout [commit id] -- [file name]`
                    String commitId = args[1];
                    String fileName = args[3];
                    Repository.checkout2(commitId, fileName);
                } 

                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rm_branch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.rm(args[1]);
                break;
            case "a":
                Repository.a();
                break;
            case "b":
                Repository.b();
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
