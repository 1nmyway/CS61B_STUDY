package gitlet;
import com.jcraft.jsch.*;

import java.io.*;

public class SFTPExample {
    String user = "root";
    String host = "123.60.100.143";
    int port = 22;
    public  void pushJSCH(String targetPath,String localPath) {

        String privateKey = "C:/Users/guanh/.ssh/id_rsa"; // 你的私钥路径
        String localFile = localPath;  // 本地文件路径
        String remoteFile = targetPath;  // 远程文件路径

        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKey);  // 使用私钥认证

            Session session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 打开SFTP通道
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // 上传文件
            channelSftp.put(localFile, remoteFile);  // 上传本地文件到远程路径

            System.out.println("文件上传成功");

            // 断开连接
            channelSftp.disconnect();
            session.disconnect();
        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }
    public  void changeJSCH(String path, String fileContent) {

        String privateKey = "C:/Users/guanh/.ssh/id_rsa"; // 你的私钥路径
        //String localFile = path;  // 本地文件路径
        String remoteFile = "/home/root/gitlet-repo/.gitlet/objects/commit";  // 远程文件路径

        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKey);  // 使用私钥认证

            Session session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 打开SFTP通道
            String modifiedContent = fileContent;
            // 上传修改后的内容到远程文件
            uploadFile(session, remoteFile, modifiedContent);

            // 断开连接
            session.disconnect();
        }
         catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  String readJSCH(String path) {

        String privateKey = "C:/Users/guanh/.ssh/id_rsa"; // 你的私钥路径
        //String localFile = path;  // 本地文件路径
        String remoteFile = path;  // 远程文件路径

        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKey);  // 使用私钥认证

            Session session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            String command = "cat " + remoteFile; // 读取文件的内容
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);

            // 获取输出流
            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();

            // 读取远程文件内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder fileContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            channelExec.disconnect();
            session.disconnect();
            return fileContent.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private  void uploadFile(Session session, String remoteFilePath, String content) throws Exception {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();

        // 通过 SFTP 写文件
        InputStream inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        channelSftp.put(inputStream, remoteFilePath);

        channelSftp.disconnect();
    }
}
