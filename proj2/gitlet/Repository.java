package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

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

    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commit");
    public static final File BLOB_DIR = join(OBJECTS_DIR, "blob");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File MASTER_DIR = join(HEADS_DIR, "master");
    public static final File ADDSTAGE_DIR = join(GITLET_DIR, "addstage");
    public static final File REMOVESATGE_DIR = join(GITLET_DIR, "removestage");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");



    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            OBJECTS_DIR.mkdir();
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
            REFS_DIR.mkdir();
            HEADS_DIR.mkdir();
            try {
                MASTER_DIR.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ADDSTAGE_DIR.mkdir();
            REMOVESATGE_DIR.mkdir();
            try {
                HEAD_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Commit commit = new Commit();
            commit.initCommit();
        }else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static void initStageArea() {
        File stage = join(GITLET_DIR, "stage");
        if (!stage.exists()) {
            try {
                stage.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static String findFileRecursively(File directory, String fileName) {
        // 列出目录中的所有文件和子目录
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                // 如果是文件且文件名匹配，则打印文件路径并返回
                if (file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
                    return file.getAbsolutePath();
                }
                // 如果是子目录，则递归查找并检查返回值
                else if (file.isDirectory()) {
                    String result = findFileRecursively(file, fileName);
                    if (result != null) {
                        return result; // 如果递归调用找到了文件，则返回其路径
                    }
                }
            }
        }
        // 如果没有找到文件，则返回null
        return null;
    }

    public static void add(String filename) {

        String filePath = findFileRecursively(CWD, filename);
        if (filePath==null){
            System.out.println("File does not exist.");
        }
        File file = new File(filePath);
        String contents = readContentsAsString(file);
        Blob blob0 = new Blob();
        String hashBlobID = blob0.generatelID(contents);//得到hash id
        File f4 = join(BLOB_DIR,filename);//新建blob目录下的储存blob的文件
        try {
            f4.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Blob blob = new Blob(file,filePath,hashBlobID,contents); //新加的blob
        writeObject(f4,blob);//把blob对象写入blob文件

        File f5 = join(ADDSTAGE_DIR,filename);
        //如果版本相同，不添加，版本不同，添加
        //
        if (!f5.exists()) {                      //哈希值不同
            try {
                f5.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(f5,hashBlobID);//把hash写入存在addstage文件里的blob
        }else{                         //哈希值相同
            Repository.rm(hashBlobID);
        }
    }

//    public static void commit(String message) {
//        File f = new File("E:/CS61B/skeleton-sp21/proj2/.gitlet/HEAD");
//        Commit commit = new Commit( new Date(),new HashMap<>(),"","","");
//    }

    public static void commit(String message){
        Date date= new Date();
        ArrayList<String> parents = new ArrayList<>();
        parents.add(readContentsAsString(Repository.HEAD_FILE));//把头指针中指向的commit文件加为父提交
        TreeMap<String,String> blobMap = new TreeMap<>();
        //String addstagePath = "E:/CS61B/skeleton-sp21/proj2/.gitlet/addstage";
        //File f = new File(addstagePath);
        List<String> fileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);//从addstage暂存区中提取所有的文件名
        if (fileNames.isEmpty()){
            System.out.println("No changes added to the commit.");
        }
        for (int i=0;i<fileNames.size();i++) {
            String fileName = fileNames.get(i);
            String addstageFilePath = "E:/CS61B/skeleton-sp21/proj2/.gitlet/addstage" +fileName;
            blobMap.put(addstageFilePath,fileName);
            rm(fileName);           //删除addstage中的内容
        }
        Commit commit = new Commit(message, parents, date,blobMap);//创建新的commit,作用是生成hashid
        String commitHashID = commit.generatelID();

        Commit commit2 = new Commit(message, parents, Commit.dateToTimeStamp(date),blobMap,commitHashID,"",commitHashID);//填入所有commit信息
        File f2 = join(COMMIT_DIR, commitHashID);//commit的文件名使用hash id
        try {
            f2.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f2, commit2);
        writeContents(Repository.HEAD_FILE, commitHashID);//把头指针指向commit
    }


    public static void rm(String filename) {
        File newFile = join(ADDSTAGE_DIR,filename);

        if(Utils.restrictedDelete(newFile)){
            File f = join(REMOVESATGE_DIR,filename);
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("no reason to remove the file.");
        }
    }
    public static void log() {
        String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        Commit commit = readObject(f, Commit.class);
        while (!commit.message.equals("initCommit")) {
            System.out.println("===");
            System.out.println(commit.ID);
            System.out.println(commit.timestamp);
            System.out.println(commit.message);
            System.out.print("\n");
            File f2 = join(COMMIT_DIR, commit.parents.get(0));
            commit = readObject(f2, Commit.class);
        }
    }
    public static void globalLog() {
        String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        Commit commit = readObject(f, Commit.class);
        while (!commit.message.equals("initCommit")) {
            System.out.println("===");
            System.out.println(commit.ID);
            System.out.println(commit.timestamp);
            System.out.println(commit.message);
            System.out.println(commit.parents);
            System.out.println(commit.pathToBlobID);
            System.out.println(commit.author);
            System.out.println(commit.fileName);
            System.out.print("\n");
            File f2 = join(COMMIT_DIR, commit.parents.get(0));
            commit = readObject(f2, Commit.class);
        }
    }

    public static void find(String message){
        int num=0;
        List<String> fileNames = Utils.plainFilenamesIn(COMMIT_DIR);
        for (int i=0;i<fileNames.size();i++) {
            String fileName = fileNames.get(i);
            File f = join(COMMIT_DIR,fileName);
            Commit commit = readObject(f,Commit.class);
            if(commit.message.equals(message)) {
                System.out.println(commit.ID);
                num++;
            }
        }
        if (num==0){
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        try {
            MASTER_DIR.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("=== Branches ===");
        List<String> branchNames = Utils.plainFilenamesIn(HEADS_DIR);
        for (int i=0;i<branchNames.size();i++) {
            String fileName = branchNames.get(i);
            System.out.println("*"+fileName);
        }
        System.out.print("\n");
        System.out.println("=== Staged Files ===");
        List<String> stageFileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);
        for (int i=0;i<stageFileNames.size();i++) {
            String fileName = stageFileNames.get(i);
            System.out.println(fileName);
        }
        System.out.print("\n");
        System.out.println("=== Removed Files ===");
        List<String> removeFileNames = Utils.plainFilenamesIn(REMOVESATGE_DIR);
        for (int i=0;i<removeFileNames.size();i++) {
            String fileName = removeFileNames.get(i);
            System.out.println(fileName);
        }
        System.out.print("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.print("\n");
        System.out.println("=== Untracked Files ===");
    }

    public static void checkout(){}


}
