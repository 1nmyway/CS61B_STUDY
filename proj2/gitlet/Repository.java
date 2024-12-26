package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public static File currentBranch = join(REFS_DIR, "currentBranch");
    public static boolean is_changed ;




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
            try {
                currentBranch.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(currentBranch,MASTER_DIR);

            Commit commit = new Commit();
            commit.initCommit();

        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static boolean isInitialized() {
        if (!GITLET_DIR.exists())
            return false;
        return true;
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

    private static String findFileRecursively(final File directory,final String fileName) {
        // 列出目录中的所有文件和子目录
         final File[] files = directory.listFiles();

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

    private static String findFileRecursivelyParent(File directory, String fileName) {
        // 列出目录中的所有文件和子目录
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                // 如果是文件且文件名匹配，则打印文件路径并返回
                if (file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
                    return file.getParent();
                }
                // 如果是子目录，则递归查找并检查返回值
                else if (file.isDirectory()) {
                    String result = findFileRecursivelyParent(file, fileName);
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
        Commit commit = getCommitFromHead();
        if (filePath == null) {
            System.out.println("File does not exist.");
        } else {
            File file = new File(filePath);
            String contents = readContentsAsString(file);
            String fileName = file.getName();
            Blob blob0 = new Blob();
            //System.out.println(filePath);
            //System.out.println("contens:"+contents);
            String hashBlobID = blob0.generatelID(contents,fileName);//得到hash id
            if (commit.blobID!=null) {
                if (commit.blobID.contains(hashBlobID)) { //如果两次add的文件完全一致，则不添加
                    //System.out.println(commit.blobID+" "+hashBlobID+"666");
                    return;
                }
            }
            File removeFile = join(REMOVESATGE_DIR, filename);
            if (removeFile.exists()) {
                if (removeFile.getName().equals(filename)) {       //如果文件名相同就删除
                    removeFile.delete();
                    //System.out.println("dddd");
                    return;
                }
            }
//            if(removeFile.exists()) {
//                System.out.println(hashBlobID+" "+readContentsAsString(removeFile));
//                if (hashBlobID.equals(readContentsAsString(removeFile))) {
//                    removeFile.delete();
//                    return;
//                }
//            }
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
        List<String> stagefileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);//从addstage暂存区中提取所有的文件名
        List<String> removeFileNames = Utils.plainFilenamesIn(REMOVESATGE_DIR);//从removestage暂存区中提取所有的文件名
        //System.out.println(is_changed);

        assert stagefileNames != null;
        assert removeFileNames != null;
        if (stagefileNames.isEmpty() && removeFileNames.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        deleteAllFiles(REMOVESATGE_DIR); //清空removestage

                                         //addstage中文件 文件名p.txt 内容 hash blobid

       // readContentsAsString(join(REMOVESATGE_DIR,removeFileName)).equals(headCommitblobID)
        Commit headcommit=getCommitFromHead();//先把headcommit里的所有blob id加进来，除了addstage里有的
        List<String> headCommitblobIDList = headcommit.blobID;
        List<String> removeFileNameList = Utils.plainFilenamesIn(REMOVESATGE_DIR);

        if (headCommitblobIDList != null) {
            headCommitblobIDList.stream()
                    .filter(headCommitblobID -> {
                        try {
                            Blob blob = readObject(join(BLOB_DIR, headCommitblobID), Blob.class);
                            boolean shouldAdd = !stagefileNames.contains(blob.fileName);

                            if (shouldAdd) {
                                shouldAdd = removeFileNameList.stream()
                                        .noneMatch(removeFileName -> readContentsAsString(join(REMOVESATGE_DIR, removeFileName)).equals(headCommitblobID));
                            }

                            return shouldAdd;
                        } catch (Exception e) {
                            // 处理异常，例如记录日志
                            System.err.println("Error processing headCommitblobID: " + headCommitblobID + ", error: " + e.getMessage());
                            return false;
                        }
                    })
                    .forEach(blobIDList::add);
        }



//        if (headCommitblobIDList!=null) {
//            for (String headCommitblobID : headCommitblobIDList) {
//                for (int i = 0; i < stagefileNames.size(); i++) {
//                    //System.out.println(readObject(join(BLOB_DIR, headCommitblobID), Blob.class).fileName+" "+fileNames.get(i));
//                    for (int j=0;j<removeFileNameList.size();j++) {
//                        if (!readObject(join(BLOB_DIR, headCommitblobID), Blob.class).fileName.equals(stagefileNames.get(i))
//                                && !readContentsAsString(join(REMOVESATGE_DIR,removeFileNameList.get(j))).equals(headCommitblobID)) {
//                            blobIDList.add(headCommitblobID);
//                            //System.out.println(headCommitblobID);
//                            //System.out.println(headCommitblobID);
//                        }
//                    }
//                }
//            }
//        }

//        assert headCommitblobIDList != null;
//        headCommitblobIDList.stream().filter()




        if (!stagefileNames.isEmpty()) {
            for (int i = 0; i < stagefileNames.size(); i++) {       //再把addstage里存在的blob id加进来
                String fileName = stagefileNames.get(i);

                File file3 = join(ADDSTAGE_DIR, fileName); //addstage目录下的文件
                //File file4 = join(BLOB_DIR, fileName);//blob目录下的文件
                //String blobPath = file4.getPath();    //.gitlet/objects/blob/Main.java
                //File file3 = new File(addstageFilePath);
                String hashBlobID = readContentsAsString(file3);
                blobIDList.add(hashBlobID); //在commit文件里存放addstage中的blob id

                file3.delete();          //删除addstage中的内容
            }
        }
        //List<String> blobFileNames = Utils.plainFilenamesIn(BLOB_DIR);
        Commit commit = new Commit(message, parents, date, blobIDList);//创建新的commit,作用是生成hashid
        String commitHashID = commit.generatelID();

        Commit commit2 = new Commit(message, parents, Commit.dateToTimeStamp(date), blobIDList, commitHashID, "", commitHashID,readObject(currentBranch, File.class));//填入所有commit信息
        File f2 = join(COMMIT_DIR, commitHashID);//commit的文件名使用hash id
        try {
            f2.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f2, commit2);
        writeContents(Repository.HEAD_FILE, commitHashID);//把头指针指向commit
        //writeContents(readObject(currentBranch,File.class), commitHashID);//当前分支指向head//TODO
        writeContents(readObject(currentBranch, File.class), commitHashID); //当前分支指向head
        List<String> branchNames = new ArrayList<>(plainFilenamesIn(HEADS_DIR));
        branchNames.remove("master");

//        for (String branchName : branchNames) {
////            System.out.println(readObject(currentBranch, File.class).equals(MASTER_DIR)+" "
////                    +readContentsAsString(MASTER_DIR).equals(readContentsAsString(join(HEADS_DIR, branchName))));
////            System.out.println(readContentsAsString(MASTER_DIR)+"  "+readContentsAsString(join(HEADS_DIR, branchName)) );
//            if (readObject(currentBranch, File.class).equals(MASTER_DIR) //如果当前分支是主分支，并且主分支内容和分支的内容一样
//                    && readContentsAsString(MASTER_DIR).equals(readContentsAsString(join(HEADS_DIR, branchName)))) {
//                writeContents(MASTER_DIR, commitHashID);
//                writeContents(join(HEADS_DIR, branchName), commitHashID);
//            }
//        }
//        if(!readObject(currentBranch, File.class).equals(MASTER_DIR)){
//            writeContents(readObject(currentBranch, File.class), commitHashID);
//        }


        //System.out.println(blobIDList);
    }

    public static void rm2(String filename) {
        //if the file is not staged, print an error message
        File f = join(REMOVESATGE_DIR, filename);
        //remove the file from the staging area
        String filePath = findFileRecursively(CWD, filename);
        if (filePath == null){
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        File file = new File(filePath);
        //System.out.println(filePath);
        String contents = readContentsAsString(file);
        String workfileName = file.getName();
       Blob blob0 = new Blob();
       String hashBlobID = blob0.generatelID(contents,workfileName);//得到工作目录中文件的hash id
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
        //System.out.println(headCommit.blobID+" "+hashBlobID);//删除工作目录中的文件
        if (headCommit.blobID!=null) {
            if (headCommit.blobID.contains(hashBlobID)) {
                //headCommit.blobID.remove(hashBlobID);
                file.delete();
                if (join(ADDSTAGE_DIR, filename).exists()) {
                    join(ADDSTAGE_DIR, filename).delete();
                }
                try {
                    f.createNewFile();         //从工作目录和stage中都删除，并且放入removestage
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                writeContents(f, hashBlobID); //removestage中的文件存放hash id
            }
            }else {
            System.out.println("No reason to remove the file.");
        }


    }


    public static void rm(String filename) {
        //File stageFile = join(ADDSTAGE_DIR,filename);
        String headHashID = readContentsAsString(HEAD_FILE);
        //List<String> fileNames = Utils.plainFilenamesIn(ADDSTAGE_DIR);
        //String path =
        File file2 = join(COMMIT_DIR, headHashID);
        File blobFile = join(BLOB_DIR, filename);
        //String blobFilePath = blobFile.getPath();
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
            System.out.println("commit "+commit.ID.substring(0, 8));
            System.out.println("Date: "+commit.timestamp);
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


    public static void globalLog() {
        //String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        //File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        List<String> commitFileNameList =  plainFilenamesIn(COMMIT_DIR);
        for (String fileName : commitFileNameList)
        {
            File f = join(COMMIT_DIR, fileName);
            Commit commit = readObject(f, Commit.class);
            System.out.println("===");
            System.out.println("commit "+commit.ID.substring(0,8));
            System.out.println("Date: "+commit.timestamp);
            System.out.println(commit.message);
            //System.out.println(commit.parents);
            //System.out.println(commit.blobID);
            //System.out.println(commit.author);
            //System.out.println(commit.fileName);
            System.out.print("\n");
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
                System.out.println(commit.ID.substring(0, 8));
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
            if (readObject(currentBranch,File.class).getName().equals(fileName)) {
                System.out.println("*" + fileName);
            }else {
                System.out.println(fileName);
            }
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
        if (commit.blobID!=null) {
            for (String blobID : commit.blobID) {
                blobFilesList.add(join(BLOB_DIR, blobID));
                //System.out.println(blobID);
            }
        }
//        for (File file: blobFilesList){
//            Blob
//        }
        return blobFilesList;
    }
    public static void checkout(Commit commit,String fileName) {
         int a=0;
        String filePath = findFileRecursively(CWD, fileName);
        if (filePath == null){
            System.out.println("File does not exist in that commit.");
            return;
        }
            File f = new File(filePath);                       //要修改的文件，工作目录中
            //commit.pathToBlobID.get(fileName);
            List<File> files = getBlobFileListFromCommit(commit);
            //System.out.println(files);
            for (File file : files) {
                //System.out.println(file.getPath());
                Blob blob = readObject(file, Blob.class);
                if (blob.fileName.equals(fileName)) {
                    writeContents(f, blob.fileContent);
                    a=1;
                    //System.out.println("content:"+blob.fileContent);
                    //System.out.println("filename:"+blob.fileName);
                }
            }
            if (a==0){
                System.out.println("File does not exist in that commit.");
            }

        }


    public static void checkout1(String fileName) { //只换指定的文件
    //String head = readContentsAsString(HEAD_FILE); //提取头指针指向的commit
        Commit headCommit = getCommitFromHead();
        if (headCommit.blobID.size() != 0) {
            checkout(headCommit, fileName);
        }else{
        System.out.println("File does not exist in that commit.");
    }
    }
    public static void checkout2(String ID,String fileName) {
        List<String> commitfilesNames = plainFilenamesIn(COMMIT_DIR);
        for(String commitfilesName : commitfilesNames){
            if (commitfilesName.substring(0,8).equals(ID)||commitfilesName.equals(ID)){
                Commit commit = readObject(join(COMMIT_DIR, commitfilesName), Commit.class);
                checkout(commit, fileName);
                return;
            }
        }
        System.out.println("No commit with that id exists.");
    }
    public static void checkout3(String branchName) {
        File branch = join(HEADS_DIR, branchName); //提取分支指针指向的commit
        //String currentCommitID = readContentsAsString(currentBranch);

        if (!branch.exists()) {
            System.out.println("No such branch exists.");
        }
        else if(readObject(currentBranch,File.class).equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else {
            //System.out.println(hasUntrackedFiles());
            if (hasUntrackedFiles()){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
            String headCommitID = readContentsAsString(branch); //提取分支指针指向的commit
            Commit commit = readObject(join(COMMIT_DIR, headCommitID), Commit.class);//从分支指向的commit提取commit对象
             //头指针指向分支指向的commit

            List<File> blobfiles = getBlobFileListFromCommit(commit);
            List<String> blobfileNames = new ArrayList<>();
            //System.out.println("sad "+blobfiles);
            for (File file5 : blobfiles){
                Blob blob = readObject(file5, Blob.class);
                blobfileNames.add(blob.fileName);
            }

            //File testfile = join(CWD, "test");
            File[] files = CWD.listFiles();
            //findFileRecursivelyParent(CWD, "test");


            if (files != null) {
                for (File file : files) {
                    String f = file.getName(); //工作目录中的文件
                    if (!blobfileNames.contains(f)){
                        file.delete();
                    }
                }
            }

            //System.out.println(files);

            for (File blobfile : blobfiles) {
                //System.out.println(file.getPath());
                Blob blob = readObject(blobfile, Blob.class);

                writeContents(join(CWD, blob.fileName), blob.fileContent);//把当前分支中commit中的文件写入工作目录的同名文件
            }
            writeObject(currentBranch, branch);
            writeContents(HEAD_FILE, headCommitID);
            //更新当前分支指针
//            if (commit.blobID!=null) {
//                for (String fileName : commit.blobID) {  //从commit里拿blobid
//
//                    File f2 = join(BLOB_DIR, fileName); //找到blob对象
//                    Blob blob = readObject(f2, Blob.class);
//
//                    String filePath = findFileRecursively(CWD, blob.fileName);
//                    File workfile = new File(filePath);   //工作目录中要修改的文件
//
//                    String contents = readContentsAsString(workfile);
//                    String workfileName = blob.fileName;
//                    Blob blob2 = new Blob();
//                    String workFileBlobID = blob2.generatelID(contents, workfileName);
//                    String currentBranchCommitID = readContentsAsString(currentBranch);
//                    Commit currentBranchCommit = readObject(join(COMMIT_DIR, currentBranchCommitID), Commit.class);
//                    if (!currentBranchCommit.blobID.contains(workFileBlobID)) {
//                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
//                    }
//                    writeContents(workfile, blob.fileContent);
//                }
//            }
                 //currentBranch = branch;

            }
        }

    public static boolean hasUntrackedFiles() {


        File[] workingFiles = CWD.listFiles();

        List<String> blobIDs = plainFilenamesIn(BLOB_DIR);
        if (blobIDs==null){
            return false;
        }
        for (File file : workingFiles) {
            if (file.isFile() ) {
                String content = readContentsAsString(file);
                String fileName = file.getName();
                Blob blob = new Blob();
                String blobID = blob.generatelID(content, fileName);
                if (!blobIDs.contains(blobID)) {
                    return true;  // 发现未跟踪文件
                }
            }
        }
        return false;  // 没有未跟踪文件
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
        }else if(branch.equals(readObject(currentBranch,File.class))){
            System.out.println("Cannot remove the current branch.");
        }else{
        branch.delete();
        }
    }

    public static void reset(String commitID){
        int i =0;
    List<String> commitfilesNames = plainFilenamesIn(COMMIT_DIR);
    Commit commit = null;
    for (String commitfileName : commitfilesNames) {
        if (commitfileName.equals(commitID)||commitfileName.substring(0,8).equals(commitID)) {
             commit = readObject(join(COMMIT_DIR, commitfileName), Commit.class);
             if (commit.branch.equals(readObject(currentBranch, File.class))) {
                 writeContents(HEAD_FILE, commitfileName);
                 writeContents(readObject(currentBranch, File.class), commitfileName);
             }else{
                 writeContents(HEAD_FILE, commitfileName);
             }
            i=1;
        }
    }
        if (hasUntrackedFiles()){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
    if (i==0){
        System.out.println("No commit with that id exists.");
        return;
    }
                                                                                //提取分支指针指向的commit
        //从分支指向的commit提取commit对象
        //头指针指向分支指向的commi
        List<File> blobfiles = getBlobFileListFromCommit(commit);
        List<String> blobfileNames = new ArrayList<String>();
        for (File file5 : blobfiles){
            Blob blob = readObject(file5, Blob.class);
            blobfileNames.add(blob.fileName);
        }
        List<String> stagefiles = plainFilenamesIn(ADDSTAGE_DIR);
        for (String fileName : stagefiles){
            join(ADDSTAGE_DIR, fileName).delete();
        }

        //File testfile = join(CWD, "test");
        File[] files = CWD.listFiles();
        //findFileRecursivelyParent(CWD, "test");

        if (files != null) {
            for (File file : files) {
                String f = file.getName(); //工作目录中的文件
                if (!blobfileNames.contains(f)){
                    file.delete();
                }
            }
        }
        for (File blobfile : blobfiles) {
            //System.out.println(file.getPath());
            Blob blob = readObject(blobfile, Blob.class);

            writeContents(join(CWD, blob.fileName), blob.fileContent);//把当前分支中commit中的文件写入工作目录的同名文件
        }

        //writeContents(HEAD_FILE, headCommitID);




//    Commit commit = readObject(join(COMMIT_DIR, commitID), Commit.class);
//        List<String> blobIDList = commit.blobID;
//        for (String blobID : blobIDList){
//            //File f2 = join(BLOB_DIR, blobID);
//            //Blob blob=readObject(f2, Blob.class);
//            deleteAllFilesExceptTracked(blobID);
//        }
//        writeContents(currentBranch, commitID);
    }
    public static void deleteAllFilesExceptTracked(String blobID) {
        // 获取工作目录中的所有文件和子目录
        File[] files = CWD.listFiles();

        if (files != null) {
            for (File file : files) {
                String c = readContentsAsString(file);
                String f = file.getName();
                Blob blob =new Blob();
                String fileblobID = blob.generatelID(c,f);
                if (!fileblobID.equals(blobID)) {            //blob id相同的文件不删除
                    //System.out.println("Deleting file: " + file.getName());
                    file.delete();
                }
            }
        }
    }

//    public static void deleteAllFilesExceptNameEqual(String fileName) {
//        // 获取工作目录中的所有文件和子目录
//        File[] files = CWD.listFiles();
//
//        if (files != null) {
//            for (File file : files) {
//                String f = file.getName();
//                if (!f.equals(fileName)) {            //文件名相同的文件不删除
//                    //System.out.println("Deleting file: " + file.getName());
//                    file.delete();
//                }
//            }
//        }
//    }
    public static void deleteAllFiles(File dir) {
        // 获取工作目录中的所有文件和子目录
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                    file.delete();
                }
            }
        }

    public static void merge(String branchName){

    }
}
