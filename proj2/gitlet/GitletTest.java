package gitlet;

public class GitletTest {
    public static void main(String[] args) {
        Repository.init();
        Repository.add("test3.java");
        Repository.status();
        Repository.commit("test");
        Repository.status();
    }
}
