package capers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = new File(".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils
    static final File STORY_FOLDER = join(CWD, ".story");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *
     *    - story -- file containing the current story
     */

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
    }
    public static void setupPersistence() {
        // TODO

            CAPERS_FOLDER.mkdir();
            STORY_FOLDER.mkdir();
            Dog.DOG_FOLDER.mkdir();

        //STORY_FOLDER.mkdir();

    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        File story = join(CAPERS_FOLDER,"story");
        boolean a = !story.exists();
        if (a){
            try {
                story.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String content = readContentsAsString(story);
        if (content.isEmpty()){
            writeContents(story, text);
        }else{
            writeContents(story,content+"\n"+text);
        }
        String content1=readContentsAsString(story);
        System.out.println(content1);

    }


    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog);

    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
        Dog dog = Dog.fromFile(name);
        dog.haveBirthday();
        dog.saveDog();
    }
}
