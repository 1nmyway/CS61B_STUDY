�� sr gitlet.Blob�ݡ�_�E L IDt Ljava/lang/String;L blobSaveFileNamet Ljava/io/File;[ bytest [BL fileContentq ~ L fileNameq ~ L filePathq ~ xpt (85c4a07001d3e600cf430437b39a78c57539cfc6ppt�����sr��gitlet.Blob�ݡ�_�E��L��IDt��Ljava/lang/String;L��blobSaveFileNamet��Ljava/io/File;[��bytest��[BL��fileContentq��~��L��fileNameq��~��L��filePathq��~��xpt��(57968ac874388e09177996fc4bd47e3e94b58abappt�����sr��gitlet.Blob�ݡ�_�E��L��IDt��Ljava/lang/String;L��blobSaveFileNamet��Ljava/io/File;[��bytest��[BL��fileContentq��~��L��fileNameq��~��L��filePathq��~��xpt��(ae9cff648f935b9fb3cfdd4896be73f82f82029bppt
�����sr��gitlet.Blob�ݡ�_�E��L��IDt��Ljava/lang/String;L��blobSaveFileNamet��Ljava/io/File;[��bytest��[BL��fileContentq��~��L��fileNameq��~��L��filePathq��~��xpt��(5459489169897723cddf3eb318403abe8ca708e0ppt	 ����sr��gitlet.Blob/�g��XyN��L��IDt��Ljava/lang/String;L��blobSaveFileNamet��Ljava/io/File;[��bytest��[BL��fileNameq��~��L��filePathq��~��xpt��(a084c27c6d789260fb77c5cda3d2895656c82863pur��[B���T�����xp�����package gitlet;

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
sr��java.io.File-�E����L��pathq��~��xpt��-E:\CS61B\skeleton-sp21\proj2\gitlet\Main.javaw��\xt��-E:/CS61B/skeleton-sp21/proj2/gitlet/Main.javasr��java.io.File-�E����L��pathq��~��xpt��;E:\CS61B\skeleton-sp21\proj2\.gitlet\objects\blob\Main.javaw��\xq��~��	sr��java.io.File-�E����L��pathq��~��xpt��;E:\CS61B\skeleton-sp21\proj2\.gitlet\objects\blob\Main.javaw��\xq��~��	sr��java.io.File-�E����L��pathq��~��xpt��;E:\CS61B\skeleton-sp21\proj2\.gitlet\objects\blob\Main.javaw��\xq��~��	sr java.io.File-�E�� L pathq ~ xpt ;E:\CS61B\skeleton-sp21\proj2\.gitlet\objects\blob\Main.javaw \xq ~ 	