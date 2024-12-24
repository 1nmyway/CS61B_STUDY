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

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
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

    public static File currentBranch = MASTER_DIR;


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
        } else {
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
        if (filePath == null) {
            System.out.println("File does not exist.");
            return;
        } else {
            File file = new File(filePath);
            String contents = readContentsAsString(file);
            Blob blob0 = new Blob();
            String hashBlobID = blob0.generatelID(contents);//得到hash id
            File removeFile = join(REMOVESATGE_DIR, filename);
            if(removeFile.exists()) {
                //System.out.println(hashBlobID+" "+readContentsAsString(removeFile));
                if (contents.equals(readContentsAsString(removeFile))) {
                    removeFile.delete();
                }
            }
            File f4 = join(BLOB_DIR, hashBlobID);//新建blob目录下的储存blob的文件,名字使用hash id
            try {
                f4.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Blob blob = new Blob(filename, filePath, hashBlobID, contents); //新加的blob
//            System.out.println("Added: " + filename);
//            System.out.println("id"+hashBlobID);
//            System.out.println("content: "+contents);
//            System.out.println("path:"+filePath);
            writeObject(f4, blob);//把blob对象写入blob文件

            File f5 = join(ADDSTAGE_DIR, filename);
            //如果版本相同，不添加，版本不同，添加
            //
            if (!f5.exists()) {                      //哈希值不同
                try {
                    f5.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                writeContents(f5, hashBlobID);//把hash写入存在addstage文件里的blob
            } else {                         //哈希值相同
                writeContents(f5, hashBlobID);
            }
        }
    }

//    public static void commit(String message) {
//        File f = new File("E:/CS61B/skeleton-sp21/proj2/.gitlet/HEAD");
//        Commit commit = new Commit( new Date(),new HashMap<>(),"","","");
//    }

    public static void commit(String message) {
        Date date = new Date();
        ArrayList<String> parents = new ArrayList<>();
        parents.add(readContentsAsString(Repository.HEAD_FILE));//把头指针中指向的commit文件加为父提交
        //TreeMap<String, String> blobMap = new TreeMap<>();
        List<String> blobIDList = new ArrayList<>();
        //String addstagePath = "E:/CS61B/skeleton-sp21/proj2/.gitlet/addstage";
        //File f = new File(addstagePath);
        List<String> fileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);//从addstage暂存区中提取所有的文件名
        if (fileNames.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);

            File file3 = join(ADDSTAGE_DIR, fileName); //addstage目录下的文件
            //File file4 = join(BLOB_DIR, fileName);//blob目录下的文件
            //String blobPath = file4.getPath();    //.gitlet/objects/blob/Main.java
            //File file3 = new File(addstageFilePath);
            String hashBlobID = readContentsAsString(file3);
            blobIDList.add(hashBlobID); //在commit文件里存放blob id
            file3.delete();          //删除addstage中的内容
        }
        Commit commit = new Commit(message, parents, date, blobIDList);//创建新的commit,作用是生成hashid
        String commitHashID = commit.generatelID();

        Commit commit2 = new Commit(message, parents, Commit.dateToTimeStamp(date), blobIDList, commitHashID, "", commitHashID);//填入所有commit信息
        File f2 = join(COMMIT_DIR, commitHashID);//commit的文件名使用hash id
        try {
            f2.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f2, commit2);
        writeContents(Repository.HEAD_FILE, commitHashID);//把头指针指向commit
        writeContents(currentBranch, commitHashID);//当前分支指向head//TODO
    }

    public static void rm2(String filename) {
        //if the file is not staged, print an error message
        File f = join(REMOVESATGE_DIR, filename);
        //remove the file from the staging area
        String filePath = findFileRecursively(CWD, filename);
        File file = new File(filePath);
        //System.out.println(filePath);
        String contents = readContentsAsString(file);
       Blob blob0 = new Blob();
       String hashBlobID = blob0.generatelID(contents);//得到hash id
        Commit headCommit = getCommitFromHead();

        if (join(ADDSTAGE_DIR, filename).exists()){
        join(ADDSTAGE_DIR, filename).delete();
        //file.delete();

//        try {
//            f.createNewFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        writeContents(f, contents);
        return;
        }
        //System.out.println(headCommit.blobID+" "+hashBlobID);
        if (headCommit.blobID.contains(hashBlobID)){
            headCommit.blobID.remove(hashBlobID);
            file.delete();
            if (join(ADDSTAGE_DIR, filename).exists()) {
                join(ADDSTAGE_DIR, filename).delete();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(f, contents);
            return;
        }else{
            System.out.println("No reason to remove the file.");
            return;
        }


    }


    public static void rm(String filename) {
        //File stageFile = join(ADDSTAGE_DIR,filename);
        String headHashID = readContentsAsString(HEAD_FILE);
        //List<String> fileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);
        //String path =
        File file2 = join(COMMIT_DIR, headHashID);
        File blobFile = join(BLOB_DIR, filename);
        String blobFilePath = blobFile.getPath();
        File stageFile = join(ADDSTAGE_DIR, filename);

        //System.out.println(stageFile.exists());
        Commit commit = readObject(file2, Commit.class);
//        for (String key : commit.pathToBlobID.keySet()) {
//            System.out.println(key + ": " + commit.pathToBlobID.get(key));
//        }   测试，打印出pathToBlobID内的所有内容
        if (commit.blobID.size()!=0) {  //在当前提交中跟踪
            //add(filename);     //添加到暂存区
            restrictedDelete(blobFile);

        }


        //remove the file from the staging area


        //            File f = join(REMOVESATGE_DIR,filename);
//            try {
//                f.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        //如果已暂存，取消暂存
        else if (stageFile.exists()) {
            restrictedDelete(stageFile);
            //System.out.println(restrictedDelete(stageFile));
        }


//        if(Utils.plainFilenamesIn(COMMIT_DIR).contains(headHashID)){
//            File f = join(BLOB_DIR,filename);
//            restrictedDelete(f);
//        }
//        if(stageFile.exists()){
//            restrictedDelete(stageFile);
//            File f = join(REMOVESATGE_DIR,filename);
//            try {
//                f.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        else {
            System.out.println("no reason to remove the file.");
        }
    }

    public static void log() {
        String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        Commit commit = readObject(f, Commit.class);
        while (!commit.ID.equals(" ")) {
            System.out.println("===");
            System.out.println(commit.ID);
            System.out.println(commit.timestamp);
            System.out.println(commit.message);
            System.out.print("\n");
            try {
                File f2 = join(COMMIT_DIR, commit.parents.get(0));
                commit = readObject(f2, Commit.class);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    //print the commit history of all commits
    public void global_log2() {
        //get the global log of all commits
        File commit = COMMIT_DIR;
        for (File file : commit.listFiles()) {
            Commit current = readObject(file, Commit.class);
            System.out.println("===");
            System.out.println("commit " + current.ID);
            System.out.println("Date: " + current.timestamp);
            System.out.println(current.message);
            System.out.println();
        }
    }

    public static void globalLog() {
        String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        Commit commit = readObject(f, Commit.class);
        while (!commit.ID.equals(" ")) {
            System.out.println("===");
            System.out.println(commit.ID);
            System.out.println(commit.timestamp);
            System.out.println(commit.message);
            System.out.println(commit.parents);
            System.out.println(commit.blobID);
            System.out.println(commit.author);
            System.out.println(commit.fileName);
            System.out.print("\n");
            try {
                File f2 = join(COMMIT_DIR, commit.parents.get(0));
                commit = readObject(f2, Commit.class);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public static void find(String message) {
        int num = 0;
        List<String> fileNames = Utils.plainFilenamesIn(COMMIT_DIR);
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            File f = join(COMMIT_DIR, fileName);
            Commit commit = readObject(f, Commit.class);
            if (commit.message.equals(message)) {
                System.out.println(commit.ID);
                num++;
            }
        }
        if (num == 0) {
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
        for (int i = 0; i < branchNames.size(); i++) {
            String fileName = branchNames.get(i);
            System.out.println("*" + fileName);
        }
        System.out.print("\n");
        System.out.println("=== Staged Files ===");
        List<String> stageFileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);
        for (int i = 0; i < stageFileNames.size(); i++) {
            String fileName = stageFileNames.get(i);
            System.out.println(fileName);
        }
        System.out.print("\n");
        System.out.println("=== Removed Files ===");
        List<String> removeFileNames = Utils.plainFilenamesIn(REMOVESATGE_DIR);
        for (int i = 0; i < removeFileNames.size(); i++) {
            String fileName = removeFileNames.get(i);
            System.out.println(fileName);
        }
        System.out.print("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.print("\n");
        System.out.println("=== Untracked Files ===");
    }

    public static Commit getCommitFromHead(){
        String head = readContentsAsString(HEAD_FILE);
        Commit commit = readObject(join(COMMIT_DIR, head), Commit.class);
        return commit;
    }
    public static List<File> getBlobFileListFromCommit(Commit commit){
        List<File> blobFilesList = new ArrayList<>();
        for (String blobID : commit.blobID){
            blobFilesList.add(join(BLOB_DIR, blobID));
            //System.out.println(blobID);
        }
//        for (File file: blobFilesList){
//            Blob
//        }
        return blobFilesList;
    }

    public static void checkout1(String fileName) { //只换指定的文件
    //String head = readContentsAsString(HEAD_FILE); //提取头指针指向的commit
        Commit headCommit = getCommitFromHead();
    String filePath = findFileRecursively(CWD, fileName);
    if (headCommit.blobID.size() != 0) {
        File f = new File(filePath);   //要修改的文件，工作目录中
        //commit.pathToBlobID.get(fileName);
        List<File> files = getBlobFileListFromCommit(headCommit);
        Blob blob=new Blob();
        //System.out.println(files);
        for (File file : files)
        {
            System.out.println(file.getPath());
            blob = readObject(file, Blob.class);
            if (blob.fileName.equals(fileName)){
                writeContents(f, blob.fileContent);
                System.out.println("content:"+blob.fileContent);
                System.out.println("filename:"+blob.fileName);
            }
        }

        }else{
        System.out.println("File does not exist in that commit.");
    }
    }
    public static void checkout2(String ID,String fileName) {
        Commit commit = getCommitFromHead();
        while (true) {
            if (commit.ID.equals(ID)) {
                String filePath = findFileRecursively(CWD, fileName);
                File f = new File(filePath);   //要修改的文件
                //commit.pathToBlobID.get(fileName);
                List<File> files = getBlobFileListFromCommit(commit);
                for (File file : files)
                {
                    Blob blob = readObject(file, Blob.class);
                    if (blob.fileName.equals(fileName)){
                        writeContents(f, blob.fileContent);
                    }
                }
                return;
            }
            try {
                File f2 = join(COMMIT_DIR, commit.parents.get(0));
                commit = readObject(f2, Commit.class);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }
    }
    public static void checkout3(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
        }
        else if(currentBranch==branch) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else {
            currentBranch = branch;
            String headCommitID = readContentsAsString(branch); //提取分支指针指向的commit
            Commit commit = readObject(join(COMMIT_DIR, headCommitID), Commit.class);//葱commit目录读取分支的提取commit对象
            writeContents(HEAD_FILE, headCommitID);

            for (String fileName : commit.blobID) {  //从commit里拿blobid

                File f2 = join(BLOB_DIR, fileName); //找到blob对象
                Blob blob = readObject(f2, Blob.class);

                String filePath = findFileRecursively(CWD, blob.fileName);
                File workfile = new File(filePath);   //工作目录中要修改的文件
//                String contents = readContentsAsString(f);
//                Blob blob0 = new Blob();
//                String hashBlobID = blob0.generatelID(contents);//得到hash id
//                if (!commit.blobID.contains(hashBlobID)){
//                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
//                    return;
//                }
                    writeContents(workfile, blob.fileContent);
                }
            }
        }


    public static void branch(String branchName){
        File newBranch = join(HEADS_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        try {
            newBranch.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String headCommitID = readContentsAsString(HEAD_FILE);
        writeContents(newBranch, headCommitID);
    }

    public static void rm_branch(String branchName){
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }else if(readContents(branch).equals(readContents(HEAD_FILE))){
            System.out.println("Cannot remove the current branch.");
            return;
        }else{
        branch.delete();
        }
    }

    public static void reset(String ID){
    }
    public static void merge(String branchName){
    }
}
