package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private List<String> parents;
    private String author;
    public Date currentDate;
    public String ID;
    private String fileName;
    private Map<String,String> pathToBlobID = new HashMap<>();
    static int num;
    Commit(){}
    Commit (String message, List<String> parents,Date currentDate,Map<String,String> pathToBlobID ) {
        this.message = message;
        this.parents = parents;
        this.currentDate = new Date();
        this.pathToBlobID = pathToBlobID;


    }
    Commit (String message, List<String> parents,Date currentDate,Map<String,String> pathToBlobID ,String ID,String author,String fileName) {
        this.message = message;
        this.parents = parents;
        this.currentDate = new Date();
        this.pathToBlobID = pathToBlobID;
        this.ID = ID;
        this.author = author;
        this.fileName = fileName;

    }


    public static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public String generatelID(){
        return Utils.sha1(dateToTimeStamp(currentDate), message, parents.toString(), pathToBlobID.toString());
    }
    public  void initCommit() {
        this.currentDate=new Date(0);
        Commit commit = new Commit("initial commit", new ArrayList<>(), currentDate,new HashMap<>());
        String initCommitHashID = commit.generatelID();
        Commit initCommit =new Commit("initCommit", new ArrayList<>(), currentDate,new HashMap<>(),initCommitHashID,"","initCommit");
        File f = join(Repository.COMMIT_DIR, "initCommit");
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f, initCommit);//把initcommit对象写入文件
        writeContents(Repository.HEAD_FILE, initCommitHashID);//把头指针指向初始化的commit
    }

    public void commit(String message){
        num++;
        currentDate=new Date();
        parents=new ArrayList<>();
        parents.add(readContentsAsString(Repository.HEAD_FILE));
        Commit commit = new Commit(message, parents, currentDate,new HashMap<>());
        String commitHashID = commit.generatelID();
        Path filePath = Paths.get("E:\\CS61B\\skeleton-sp21\\proj2\\.gitlet\\objects\\commit", "commit" + num);
        File f = filePath.toFile();
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f, commit);//把initcommit对象写入文件
        writeContents(Repository.HEAD_FILE, commitHashID);
    }

    /* TODO: fill in the rest of this class. */
}
