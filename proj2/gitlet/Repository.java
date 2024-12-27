package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
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
    public static boolean is_changed;


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
            writeObject(currentBranch, MASTER_DIR);

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

    private static String findFileRecursively(final File directory, final String fileName) {
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
            String hashBlobID = blob0.generatelID(contents, fileName);//得到hash id
            //System.out.println(fileName+" "+hashBlobID);
            if (commit.blobID != null) {
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
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(readObject(join(COMMIT_DIR, readContentsAsString(Repository.HEAD_FILE)), Commit.class));//把头指针中指向的commit文件加为父提交
        //TreeMap<String, String> blobMap = new TreeMap<>();
        ArrayList<String> blobIDList = new ArrayList<>();
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



        //addstage中文件 文件名p.txt 内容 hash blobid

        // readContentsAsString(join(REMOVESATGE_DIR,removeFileName)).equals(headCommitblobID)
        Commit headcommit = getCommitFromHead();//先把headcommit里的所有blob id加进来，除了addstage里有的
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
        Map<String, String> fileMap = new HashMap<>();
        for (String blobID : blobIDList) {
            Blob blob = readObject(join(BLOB_DIR, blobID), Blob.class);
            fileMap.put(blob.fileName, blobID);
        }

        for (String file:removeFileNameList) {
            String con = readContentsAsString(join(REMOVESATGE_DIR, file));
            blobIDList.remove(con);
        }

        Commit commit2 = new Commit(message, parents, Commit.dateToTimeStamp(date), blobIDList, commitHashID, "", commitHashID, readObject(currentBranch, File.class), fileMap);//填入所有commit信息
        File f2 = join(COMMIT_DIR, commitHashID);//commit的文件名使用hash id




        //填入所有commit信息

        deleteAllFiles(REMOVESATGE_DIR); //清空removestage
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
        if (filePath == null) {
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
        String hashBlobID = blob0.generatelID(contents, workfileName);//得到工作目录中文件的hash id
        Commit headCommit = getCommitFromHead();

        if (join(ADDSTAGE_DIR, filename).exists()) {
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
        if (headCommit.blobID != null) {
            if (headCommit.blobID.contains(hashBlobID)) {
                //System.out.println(headCommit.blobID);

                //System.out.println(headCommit.blobID);
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
        } else {
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
        if (commit.blobID.size() != 0) {  //在当前提交中跟踪
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

    public static void log2() {
        String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        Commit commit = readObject(f, Commit.class);
        while (!commit.ID.equals(" ")) {
            System.out.println("===");
            System.out.println("commit " + commit.ID);
            if( commit.parents.size() > 1){
                System.out.println("Merge: " + commit.parents.get(0).ID.substring(0,7) + " " + commit.parents.get(1).ID.substring(0,7));
            }
            System.out.println("Date: " + commit.timestamp);
            System.out.println(commit.message);
            System.out.print("\n");
            try {
//                File f2 = join(COMMIT_DIR, commit.parents.get(0));
//                commit = readObject(f2, Commit.class);
                commit = commit.parents.get(0);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }
    public static void log3() {
        String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        Commit commit = readObject(f, Commit.class);
        while (!commit.ID.equals(" ")) {
            System.out.println("===");
            if( commit.parents.size() > 1){
                System.out.println("commit " + commit.ID);
                System.out.println("Merge: " + commit.parents.get(0).ID.substring(0,7) + " " + commit.parents.get(1).ID.substring(0,7));
                System.out.println("Date: " + commit.timestamp);
                System.out.println(commit.message);
                System.out.print("\n");
                return;
            }
            System.out.println("commit " + commit.ID);
            //System.out.println("Merge: " + commit.parents.get(0).ID.substring(0,7) + " " + commit.parents.get(1).ID.substring(0,7));
            System.out.println("Date: " + commit.timestamp);
            System.out.println(commit.message);
            System.out.print("\n");

            try {
//                File f2 = join(COMMIT_DIR, commit.parents.get(0));
//                commit = readObject(f2, Commit.class);
                commit = commit.parents.get(0);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public static void log() {
        // 获取当前分支的最新提交
        Commit currentCommit = getCommitFromHead();
        printCommitHistory(currentCommit, new HashSet<>());
    }

    private static void printCommitHistory(Commit commit, Set<String> visited) {
        if (commit == null || visited.contains(commit.ID)) {
            return;
        }

        // 标记当前提交为已访问
        visited.add(commit.ID);

        // 打印当前提交的信息
        System.out.println("===");
        System.out.println("commit " + commit.ID);
        if (commit.parents.size() > 1) {
            System.out.println("Merge: " + commit.parents.get(0).ID.substring(0, 7) + " " + commit.parents.get(1).ID.substring(0, 7));
        }
        System.out.println("Date: " + commit.timestamp);
        System.out.println(commit.message);
        System.out.println();

        // 递归打印每个父提交的历史
        for (Commit parent : commit.parents) {
            printCommitHistory(parent, visited);
        }
    }


    //print the commit history of all commits


    public static void globalLog() {
        //String commitHashID = readContentsAsString(Repository.HEAD_FILE);
        //File f = join(COMMIT_DIR, commitHashID);//头指针指向的commit
        List<String> commitFileNameList = plainFilenamesIn(COMMIT_DIR);
        for (String fileName : commitFileNameList) {
            File f = join(COMMIT_DIR, fileName);
            Commit commit = readObject(f, Commit.class);
            System.out.println("===");
            System.out.println("commit " + commit.ID);
            System.out.println("Date: " + commit.timestamp);
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
            if (readObject(currentBranch, File.class).getName().equals(fileName)) {
                System.out.println("*" + fileName);
            } else {
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
        List<String> modifileNames = modifedFile();
        if (modifileNames != null) {
            for (int i = 0; i < modifileNames.size(); i++) {
                String fileName = modifileNames.get(i);
                System.out.println(fileName);
            }
        }
        List<String> deletedfileNames = deletedFile();
        if (deletedfileNames != null) {
            for (int i = 0; i < deletedfileNames.size(); i++) {
                String fileName = deletedfileNames.get(i);
                System.out.println(fileName);
            }
        }

        System.out.print("\n");
        System.out.println("=== Untracked Files ===");
        List<String> untrackedFilesNames = hasUntrackedFilesName();
        if (untrackedFilesNames != null) {
            for (int i = 0; i < untrackedFilesNames.size(); i++) {
                String fileName = untrackedFilesNames.get(i);
                System.out.println(fileName);
            }
        }
    }
    public static List<String> modifedFile() {

        Commit commit = getCommitFromHead();
        if (commit == null||commit.blobID==null){return null;}
        Blob blob = new Blob();
        List<String> fileNames = Utils.plainFilenamesIn(CWD);
        List<String> modifileNames = new ArrayList<>();
        for (String fileName : fileNames) {
            String filePath = findFileRecursively(CWD, fileName);
            if (filePath == null) {
                continue;
            }
            File file = new File(filePath);
            String fileContent = Utils.readContentsAsString(file);
            String blobID = blob.generatelID(fileContent, fileName);
            if (!commit.blobID.contains(blobID)) {
                 modifileNames.add(fileName);
            }
        }
        return modifileNames;
    }
    public static List<String> deletedFile() {
        Commit commit = getCommitFromHead();
        if (commit == null||commit.fileMap==null){return null;}
        List<String> fileNames = Utils.plainFilenamesIn(CWD);
        Set<String> map = commit.fileMap.keySet();

        assert fileNames != null;
        fileNames.forEach(map::remove);

        return fileNames;
    }



    public static Commit getCommitFromHead() {
        String head = readContentsAsString(HEAD_FILE);
        Commit commit = readObject(join(COMMIT_DIR, head), Commit.class);
        return commit;
    }

    public static List<File> getBlobFileListFromCommit(Commit commit) {
        List<File> blobFilesList = new ArrayList<>();
        if (commit.blobID != null) {
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

    public static void checkout(Commit commit, String fileName) { //把工作目录中的文件修改为commit里的
        int a = 0;
        String filePath = findFileRecursively(CWD, fileName);
        if (filePath == null) {
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
                a = 1;
                //System.out.println("content:"+blob.fileContent);
                //System.out.println("filename:"+blob.fileName);
            }
        }
        if (a == 0) {
            System.out.println("File does not exist in that commit.");
        }

    }


    public static void checkout1(String fileName) { //只换指定的文件
        //String head = readContentsAsString(HEAD_FILE); //提取头指针指向的commit
        Commit headCommit = getCommitFromHead();
        if (headCommit.blobID.size() != 0) {
            checkout(headCommit, fileName);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    public static void checkout2(String ID, String fileName) {
        List<String> commitfilesNames = plainFilenamesIn(COMMIT_DIR);
        for (String commitfilesName : commitfilesNames) {
            if (commitfilesName.substring(0, 8).equals(ID) || commitfilesName.equals(ID)) {
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
        } else if (readObject(currentBranch, File.class).equals(branch)) {
            System.out.println("No need to checkout the current branch.");

        } else {
            //System.out.println(hasUntrackedFiles());
            if (hasUntrackedFiles()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
            String headCommitID = readContentsAsString(branch); //提取分支指针指向的commit
            Commit commit = readObject(join(COMMIT_DIR, headCommitID), Commit.class);//从分支指向的commit提取commit对象
            //头指针指向分支指向的commit
            //System.out.println(commit.blobID);

            List<File> blobfiles = getBlobFileListFromCommit(commit);
            List<String> blobfileNames = new ArrayList<>();
            //System.out.println("sad "+blobfiles);
            for (File file5 : blobfiles) {
                Blob blob = readObject(file5, Blob.class);
                blobfileNames.add(blob.fileName);
            }

            File testfile = join(CWD, "test");
            File[] files = CWD.listFiles();
            //findFileRecursivelyParent(CWD, "test");


            if (files != null) {
                for (File file : files) {
                    String f = file.getName(); //工作目录中的文件
                    if (!blobfileNames.contains(f)) {
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

        File testfile = join(CWD, "test");
        File[] workingFiles = CWD.listFiles();

        List<String> blobIDs = plainFilenamesIn(BLOB_DIR);
        //System.out.println(blobIDs);
        if (blobIDs == null) {
            return false;
        }
        for (File file : workingFiles) {
            if (file.isFile()) {
                String content = readContentsAsString(file);
                String fileName = file.getName();
                Blob blob = new Blob();
                String blobID = blob.generatelID(content, fileName);
                //System.out.println(fileName+" "+blobID);

                if (!blobIDs.contains(blobID)) {
                    return true;  // 发现未跟踪文件
                }
            }
        }
        return false;  // 没有未跟踪文件
    }

    public static List<String> hasUntrackedFilesName() {

        File testfile = join(CWD, "test");
        File[] workingFiles = CWD.listFiles();

        List<String> blobIDs = plainFilenamesIn(BLOB_DIR);
        List<String> fileNames = new ArrayList<>();
        //System.out.println(blobIDs);
        if (blobIDs == null) {
            return null;
        }
        for (File file : workingFiles) {
            if (file.isFile()) {
                String content = readContentsAsString(file);
                String fileName = file.getName();
                Blob blob = new Blob();
                String blobID = blob.generatelID(content, fileName);
                //System.out.println(fileName+" "+blobID);

                if (!blobIDs.contains(blobID)) {
                    fileNames.add(fileName);
                     // 发现未跟踪文件
                }
            }
        }
        return fileNames;  // 没有未跟踪文件
    }



    public static void branch(String branchName) {
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
        //writeContents(newBranch, headCommitID);
        writeContents(newBranch,readContentsAsString(readObject(currentBranch, File.class)));
    }

    public static void rm_branch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
        } else if (branch.equals(readObject(currentBranch, File.class))) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branch.delete();
        }
    }

    public static void reset(String commitID) {
        int i = 0;
        List<String> commitfilesNames = plainFilenamesIn(COMMIT_DIR);
        Commit commit = null;
        for (String commitfileName : commitfilesNames) {
            if (commitfileName.equals(commitID) || commitfileName.substring(0, 8).equals(commitID)) {
                commit = readObject(join(COMMIT_DIR, commitfileName), Commit.class);
                if (commit.branch.equals(readObject(currentBranch, File.class))) {
                    writeContents(HEAD_FILE, commitfileName);
                    writeContents(readObject(currentBranch, File.class), commitfileName);
                } else {
                    writeContents(HEAD_FILE, commitfileName);
                }
                i = 1;
            }
        }
        if (hasUntrackedFiles()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        if (i == 0) {
            System.out.println("No commit with that id exists.");
            return;
        }
        //提取分支指针指向的commit
        //从分支指向的commit提取commit对象
        //头指针指向分支指向的commi
        List<File> blobfiles = getBlobFileListFromCommit(commit);
        List<String> blobfileNames = new ArrayList<String>();
        for (File file5 : blobfiles) {
            Blob blob = readObject(file5, Blob.class);
            blobfileNames.add(blob.fileName);
        }
        List<String> stagefiles = plainFilenamesIn(ADDSTAGE_DIR);
        for (String fileName : stagefiles) {
            join(ADDSTAGE_DIR, fileName).delete();
        }

        //File testfile = join(CWD, "test");
        File[] files = CWD.listFiles();
        //findFileRecursivelyParent(CWD, "test");

        if (files != null) {
            for (File file : files) {
                String f = file.getName(); //工作目录中的文件
                if (!blobfileNames.contains(f)) {
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
                Blob blob = new Blob();
                String fileblobID = blob.generatelID(c, f);
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


    // 获取文件在某个提交中的状态
    public static FileStatus getFileStatus(Commit commit, Commit mergeBaseCommit, String fileName) {

        if (commit.message.equals("initial commit")){
            return new FileStatus(false, false, false, null);
        }
        // 获取当前提交中的文件映射（文件名 -> Blob ID）
        Map<String, String> fileMap = commit.getFileMap();

        // 如果文件不存在于当前提交中，返回不存在的状态
        if (!fileMap.containsKey(fileName)) {
            return new FileStatus(false, false, true, null); // 文件不存在，因此返回删除状态
        }

        // 获取文件的 Blob ID
        String currentBlobId = fileMap.get(fileName);

        // 获取分割点（merge base）提交的文件映射
        Map<String, String> mergeBaseFileMap = mergeBaseCommit.getFileMap();

        // 如果分割点中没有该文件，那么说明它是新增的
        if (mergeBaseFileMap!=null) {
            if (!mergeBaseFileMap.containsKey(fileName)) {
                return new FileStatus(true, false, false, currentBlobId); // 文件是新增的
            }


            // 获取分割点中该文件的 Blob ID
            String mergeBaseBlobId = mergeBaseFileMap.get(fileName);


            // 如果当前提交中的 Blob ID 与分割点中的 Blob ID 不同，说明该文件在当前分支中被修改了
            if (!mergeBaseBlobId.equals(currentBlobId)) {
                return new FileStatus(true, true, false, currentBlobId); // 文件被修改了
            }
        }

        // 如果当前提交中的 Blob ID 和分割点中的 Blob ID 相同，说明该文件在当前分支中没有修改
        return new FileStatus(true, false, false, currentBlobId); // 文件未修改
    }

    public static boolean bothModified(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        // 空值检查
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        // 简化逻辑
        return (currentStatus.isModified() && (targetStatus.isModified() || targetStatus.isDeleted())) ||
                (currentStatus.isDeleted() && targetStatus.isModified());

//        if (currentStatus.isModified() && targetStatus.isModified() ){
//            return true;
//        }else if (currentStatus.isModified() && targetStatus.isDeleted()){
//            return true;
//        }else if (currentStatus.isDeleted() && targetStatus.isModified()){
//            return true;
//        }
//        return false;
    }

    public static void checkout41(Commit commit, String fileName) { //把工作目录中的文件修改为commit里的


        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //要修改的文件，工作目录中
        //commit.pathToBlobID.get(fileName);
        List<File> files = getBlobFileListFromCommit(commit);
        //System.out.println(files);
        for (File file : files) {
            //System.out.println(file.getPath());
            Blob blob = readObject(file, Blob.class);
            if (blob.fileName.equals(fileName)) {
                writeContents(f, blob.fileContent);
                //System.out.println("content:"+blob.fileContent);
                //System.out.println("filename:"+blob.fileName);
            }
        }
    }

    public static void checkout4(Commit commit, String fileName) {
        // 获取工作目录中的文件
        File f = new File(fileName);

        // 如果文件不存在，则创建新文件
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create file: " + fileName, e);
            }
        }

        // 从 commit 中获取所有 Blob 文件
        List<File> files = getBlobFileListFromCommit(commit);

        // 查找与 fileName 匹配的 Blob 文件
        boolean found = false;
        for (File blobFile : files) {
            Blob blob = readObject(blobFile, Blob.class);
            if (blob != null && blob.fileName.equals(fileName)) {
                // 将 Blob 的内容写入工作目录中的文件
                writeContents(f, blob.fileContent);
                found = true;
                break; // 找到并写入文件后退出
            }
        }

        // 如果没有找到匹配的 Blob 文件，打印错误信息
        if (!found) {
            System.out.println("File " + fileName + " not found in the commit.");
        }
    }

    public static boolean modifiedOnlyInCurrent(FileStatus currentStatus, FileStatus mergeBaseStatus) {
        if (currentStatus.isModified() && !mergeBaseStatus.isModified()) {
            return true;
        }
        return false;
    }

    public static boolean modifiedOnlyInTarget(FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (targetStatus.isModified() && !mergeBaseStatus.isModified()) {
            return true;
        }
        return false;
    }

    public static boolean bothDeleted(FileStatus currentStatus, FileStatus targetStatus) {
        if (currentStatus.isDeleted() && targetStatus.isDeleted()) {
            return true;
        }
        return false;
    }

//    public static boolean targetHasFileButNotInCurrent(FileStatus currentStatus,FileStatus targetStatus){
//        if (!currentStatus.exists() && targetStatus.exists()){
//            return true;
//        }
//        return false;
//    }

    public static void resolveConflict(String fileName, Commit currentCommit, Commit targetCommit) {
        System.out.println("Encountered a merge conflict.");
        String currentContent = getFileContent(currentCommit, fileName);
        String targetContent = getFileContent(targetCommit, fileName);

        // 生成冲突标记
        String conflictContent = "<<<<<<< HEAD\n" + currentContent +
                "=======\n" + targetContent +
                ">>>>>>>\n";

        // 将冲突文件内容写入工作目录

        writeConflictToFile(fileName, conflictContent);

        // 打印出冲突信息
//        System.out.println("Conflict in file: " + fileName);
//        System.out.println("Please resolve the conflict and stage the changes.");
    }

    public static void writeConflictToFile(String fileName, String content) {
        try {
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取文件内容的辅助函数
    public static String getFileContent(Commit commit, String fileName) {
        // 这里返回的是文件在提交中的内容，可以通过 Blob ID 获取
        String blobID = commit.getFileMap().get(fileName);
        File blobFile = join(BLOB_DIR, blobID);
        Blob blob = readObject(blobFile, Blob.class);
        return blob.fileContent; // 假设 BlobManager 获取文件内容
    }


    public static void merge(String branchName) {
        File givenBranchFile = join(HEADS_DIR, branchName);
        File currentBranchFile = readObject(currentBranch, File.class);
        List<String> stagedFiles = Utils.plainFilenamesIn(ADDSTAGE_DIR);
        if (!stagedFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!givenBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (givenBranchFile.equals(currentBranchFile)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        } else if (hasUntrackedFiles()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }


        //System.out.println("dada");
        String currentBranchCommitID = readContentsAsString(currentBranchFile);
        Commit currentBranchCommit = readObject(join(COMMIT_DIR, currentBranchCommitID), Commit.class);

        List<String> currentBranchCommitBlobIDList = currentBranchCommit.blobID;
        Set<String> currentFiles = new HashSet<>();
        for (String blobID : currentBranchCommitBlobIDList) {
            currentFiles.add(readObject(join(BLOB_DIR, blobID), Blob.class).fileName);
        }
        //System.out.println("currentFiles:"+currentFiles);
// 合并这两个集合，得到所有需要合并的文件
        Set<String> allFilesInMerge = new HashSet<>();


        String givenBranchCommitID = readContentsAsString(givenBranchFile);
        Commit givenBranchCommit = readObject(join(COMMIT_DIR, givenBranchCommitID), Commit.class);
        List<String> givenBranchCommitBlobIDList = givenBranchCommit.blobID;
        Set<String> targetFiles = new HashSet<>();
        for (String blobID : givenBranchCommitBlobIDList) {
            targetFiles.add(readObject(join(BLOB_DIR, blobID), Blob.class).fileName);
        }
        //System.out.println("targetFiles:"+targetFiles);
        Set<String> splitFiles = new HashSet<>();
        Commit splitPointCommit = findSplitPoint(currentBranchCommit, givenBranchCommit);

                List<String> splitPointCommitBlobIDList = splitPointCommit.blobID;
            if (splitPointCommitBlobIDList != null) {
                for (String blobID : splitPointCommitBlobIDList) {
                    splitFiles.add(readObject(join(BLOB_DIR, blobID), Blob.class).fileName);
                }
            }




        //System.out.println("splitFiles:"+splitFiles);



        allFilesInMerge.addAll(splitFiles);
        allFilesInMerge.addAll(targetFiles);
        allFilesInMerge.addAll(currentFiles);
        //System.out.println("dd"+splitPointCommit);
//        System.out.println("split " +splitPointCommit.ID);
//        System.out.println("curr "+currentBranchCommit.ID);

        if (splitPointCommit != null) {
            if (splitPointCommit.equals(givenBranchCommit)) {
                System.out.println("Given branch is an ancestor of the current branch.");
            } else if (splitPointCommit.ID.equals(currentBranchCommit.ID)) {

                checkout3(branchName); //切换分支,当前分支会被改写为给定分支
                writeContents(HEAD_FILE, givenBranchCommitID);  //头指针改为给定分支commit
                writeContents(readObject(currentBranch,File.class), givenBranchCommitID);//当前分支指向给定分支commit
                writeObject(currentBranch,currentBranchFile);//把当前分支

                System.out.println("Current branch fast-forwarded.");
            } else {
                ArrayList<String> blobIDList = new ArrayList<>();
                Map<String, String> fileMap = new HashMap<>();
                for (String fileName : allFilesInMerge) {
                    FileStatus currentStatus = getFileStatus(currentBranchCommit, splitPointCommit, fileName);
                    FileStatus targetStatus = getFileStatus(givenBranchCommit, splitPointCommit, fileName);
                    FileStatus mergeBaseStatus = getFileStatus(splitPointCommit, splitPointCommit, fileName);

//                    System.out.println("fileName: " + fileName);
//                    System.out.println("currentStatus: " + currentStatus);
//                    System.out.println("targetStatus: " + targetStatus);
//                    System.out.println("mergeBaseStatus: " + mergeBaseStatus);

                    if (One(currentStatus, targetStatus, mergeBaseStatus)) {
                        ////System.out.println("1");
                        checkout4(givenBranchCommit, fileName);
                        //add(fileName);
                        blobIDList.add(targetStatus.getBlobId());
                        fileMap.put(fileName, targetStatus.getBlobId());
                    } else if (Two(currentStatus, targetStatus, mergeBaseStatus)) {
                        //System.out.println("2");
                        blobIDList.add(currentStatus.getBlobId());
                        fileMap.put(fileName, currentStatus.getBlobId());
                    } else if (Three1(currentStatus, targetStatus, mergeBaseStatus)) {
                        //System.out.println("3");
                        if (currentStatus.getBlobId().equals(targetStatus.getBlobId())){
                            blobIDList.add(currentStatus.getBlobId());
                            fileMap.put(fileName, currentStatus.getBlobId());
                        } else if (!currentStatus.getBlobId().equals(targetStatus.getBlobId())) {
                            resolveConflict(fileName, currentBranchCommit, givenBranchCommit);
                        }
                    } else if (Three2(currentStatus, targetStatus, mergeBaseStatus)) {
                        resolveConflict(fileName, currentBranchCommit, givenBranchCommit);
                    } else if (Four(currentStatus, targetStatus, mergeBaseStatus)) {
                        blobIDList.add(currentStatus.getBlobId());
                        fileMap.put(fileName, currentStatus.getBlobId());
                        //System.out.println("4");
                    } else if (Five(currentStatus, targetStatus, mergeBaseStatus)) {
                        //System.out.println("5");
                        checkout4(givenBranchCommit, fileName);  //p进这了,
                        blobIDList.add(targetStatus.getBlobId());
                        fileMap.put(fileName, targetStatus.getBlobId());
                    } else if (Six(currentStatus, targetStatus, mergeBaseStatus)) {
                        //System.out.println("6");
                        String path =  findFileRecursively(CWD, fileName);
                        if (path != null) {
                            File file = new File(path);
                            file.delete();
                        };
                    } else if (Seven(currentStatus, targetStatus, mergeBaseStatus)) {
                        //System.out.println("7");
                        String path =  findFileRecursively(CWD, fileName);
                        if (path != null) {
                            File file = new File(path);
                            file.delete();
                        }
                    }

                }


                String currentBranchName = readObject(currentBranch,File.class).getName();
                List<Commit> parents = new ArrayList<>();
                parents.add(currentBranchCommit);
                parents.add(givenBranchCommit);
                Date date = new Date();
                Commit commit = new Commit("Merged "+branchName+" into "+currentBranchName, parents, date, blobIDList);//创建新的commit,作用是生成hashid
                String commitHashID = commit.generatelID();

                Commit commit2 = new Commit("Merged "+branchName+" into "+currentBranchName, parents, Commit.dateToTimeStamp(date), blobIDList, commitHashID, "",commitHashID, currentBranchCommit.branch, fileMap);//填入所有commit信息
                File f2 = join(COMMIT_DIR, commitHashID);//commit的文件名使用hash id
                try {
                    f2.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                writeObject(f2, commit2);
                writeContents(Repository.HEAD_FILE, commitHashID);
                writeContents(readObject(currentBranch,File.class), commitHashID);
            }
        }
    }


        public static Commit findSplitPoint (Commit currentBranchCommit, Commit givenBranchCommit){
            // 参数校验
            if (currentBranchCommit == null || givenBranchCommit == null) {
                return null;
            }

            Set<String> visited = new HashSet<>();  // 用来记录访问过的提交

            // 使用队列进行广度优先搜索，处理多父提交的情况
            Queue<Commit> queue = new LinkedList<>();
            queue.offer(currentBranchCommit);

            while (!queue.isEmpty()) {
                Commit commit = queue.poll();
                if (commit != null) {
                    visited.add(commit.ID);
                    for (Commit parent : commit.parents) {
                        queue.offer(parent);
                    }
                }
            }

            // 回溯 givenBranchCommit 的历史，直到找到第一个共同的提交
            while (givenBranchCommit != null) {
                if (visited.contains(givenBranchCommit.ID)) {  // 如果 givenBranchCommit 的提交在 visited 中，说明找到了分割点
                    return givenBranchCommit;  // 返回共同的提交
                }
                for (Commit parent : givenBranchCommit.parents) {
                    givenBranchCommit = parent;
                    break;  // 只取第一个父提交，保持原逻辑不变
                }
            }

            // 添加日志记录
            System.out.println("No common ancestor found between the two branches.");
            return null;
        }


    private static boolean One(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (!currentStatus.isModified() && !mergeBaseStatus.isModified() && targetStatus.isModified()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean Two(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (currentStatus.isModified() && !mergeBaseStatus.isModified() && !targetStatus.isModified()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean Three1(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (currentStatus.isModified() && !mergeBaseStatus.isModified() && targetStatus.isModified()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean Three3(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if ((currentStatus.isModified() && !mergeBaseStatus.isModified() && targetStatus.isDeleted())
                ||(currentStatus.isDeleted() && !mergeBaseStatus.isModified() && targetStatus.isModified())) {
            return true;
        } else {
            return false;
        }
    }


    private static boolean Three2(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        // 空值检查
        if (currentStatus == null || targetStatus == null || mergeBaseStatus == null) {
            throw new IllegalArgumentException("FileStatus parameters cannot be null");
        }

        // 简化布尔表达式
        return (currentStatus.isModified() && !mergeBaseStatus.isModified() && targetStatus.isDeleted()) ||
                (currentStatus.isDeleted() && !mergeBaseStatus.isModified() && targetStatus.isModified());
    }


    private static boolean Four(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (currentStatus.exists() && !mergeBaseStatus.exists() && !targetStatus.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean Five(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (!currentStatus.exists() && !mergeBaseStatus.exists() && targetStatus.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean Six(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (!currentStatus.isModified() && !mergeBaseStatus.isModified() && !targetStatus.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean Seven(FileStatus currentStatus, FileStatus targetStatus, FileStatus mergeBaseStatus) {
        if (!currentStatus.exists() && !mergeBaseStatus.isModified() && !targetStatus.isModified()) {
            return true;
        } else {
            return false;
        }
    }


    }
