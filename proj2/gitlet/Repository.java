package gitlet;

import java.io.File;
import java.io.IOException;
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
            MASTER_DIR.mkdir();
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
    public static void add(String filename) {
        String path =  "E:/CS61B/skeleton-sp21/proj2/.gitlet/objects/blob";
        String filePath = "E:/CS61B/skeleton-sp21/proj2/"+"/"+filename;
        String addstagePath = "E:/CS61B/skeleton-sp21/proj2/.gitlet/addstage";
        File f = new File(filePath);  //要添加进blob的文件
        File f2 = new File(path);  //blob文件
        File f3 = new File(addstagePath);
        //
        Blob blobHash = new Blob(f);
        String hashBlobID = blobHash.generatelID();//得到hash id
        Blob blob = new Blob(f,filePath,hashBlobID,blobHash.bytes);
        writeObject(f2,blob);//把blob对象写入blob文件
        if (!f2.exists()){
            System.out.println("File does not exist.");
            return;
        }
        String hashFileName = sha1(f);
        f = join("E:/CS61B/skeleton-sp21/proj2/.gitlet/addstage",hashFileName);
        //如果版本相同，不添加，版本不同，添加
        //
        if (!f.exists()) { //哈希值不同
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{   //哈希值相同
            Repository.rm(hashFileName);
        }
    }

    public static void commit(String message) {
        File f = new File("E:/CS61B/skeleton-sp21/proj2/.gitlet/HEAD");
        Commit commit = new Commit(message,, new Date(),new HashMap<>(),"","","");


    }
    public static void addFileToLocal(File file){
        File newFile = join(GITLET_DIR,sha1(file));
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{

        }
    }

    public static void rm(String filename) {
        String path =  "E:/CS61B/skeleton-sp21/proj2/.gitlet/stage"+ "/" + filename;
        File f = new File(path);
        if(Utils.restrictedDelete(f)){
            return;
        }else{
            System.out.println("no reason to remove the file.");
        }
    }

}
