package util;


import java.util.*;

/**
 * @author ���޿�
 * @date 2018/3/9
 */
public class StringUtil {

    private static final List<String> USEFUL_CHAR_PERP_LIST = Arrays.asList("np", "no", "nn", "ng");
    private static final String SPLIT = " ";

    /**
     * �����ض����ԵĴ��飬��ȥ��
     */
    public static Set<String> getEle(String message) {
        Set<String> result = new HashSet<>();
        String[] mesSegs = message.split(SPLIT);
        Arrays.stream(mesSegs).distinct()
                .filter(x -> (x.split("#").length > 1 && USEFUL_CHAR_PERP_LIST.contains(x.split("#")[1])))
                .forEach(x -> result.add(x.split("#")[0]));
        return result;
    }
}
