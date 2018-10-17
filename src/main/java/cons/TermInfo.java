package cons;

import lombok.Data;

import java.util.List;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2018/3/26
 */

@Data
public class TermInfo {
    String termName;

    List<Node> infos;

    @Data
    public static class Node {
        String key;
        String value;
    }
}
