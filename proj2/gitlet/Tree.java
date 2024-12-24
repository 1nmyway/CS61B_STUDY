package gitlet;

import java.util.Map;

class Tree {
        Map<String, String> entries; // 文件名 -> 哈希值 (可以是 blob 或子 tree)

        public Tree(Map<String, String> entries) {
            this.entries = entries;
        }

        public String generateTreeObject() {
            StringBuilder content = new StringBuilder();
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                String mode = getMode(entry.getKey());
                String hash = entry.getValue();
                content.append(mode).append(" ").append(entry.getKey())
                        .append("\0").append(hash); // 注意：hash 应是二进制
            }
            return calculateHash(content.toString()); // 计算 tree 的哈希
        }

        private String calculateHash(String content) {
            // 使用 SHA-1 算法计算哈希
            return Utils.sha1(content);
        }

        private String getMode(String fileName) {
            // 返回模式，比如普通文件是 100644
            return "q";
        }
}
