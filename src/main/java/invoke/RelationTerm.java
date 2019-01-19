package invoke;

import cons.Constants;
import cons.Triple;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.*;

/**
 * @author 杜艮魁
 * @date 2018/9/26
 */
public class RelationTerm {

    /**
     * 调用 findConnectedTypeTerms 查找某个term关联的term的类型。fixme 一个term的类型可能有很多种的
     */
    public static Set<String> findConnectedTypeTermList(List<String> termsList) {
        Set<String> typeOfConTerms = new HashSet<>();
        for (String ele : termsList) {
            typeOfConTerms.addAll(findConnectedTypeTerms(ele));
        }
        typeOfConTerms.forEach(x -> System.out.println(x));
        return typeOfConTerms;
    }

    /**
     * 查找与term关联的term的term集合的类型：关联的term包括(h,l,term)和(term,l,t)中的h和t的类型
     * todo 除了类型还应该包括 category:
     */
    public static Set<String> findConnectedTypeTerms(String term) {
        Set<String> typeOfConTerms = new HashSet<>();
        /**
         * 查出其作为尾节点时  头头头头头头头头head节点  及节点类型:todo 去重+并发执行减小计算量
         */
        List<Triple> tailTripleList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TAIL, term);
        for (int i = 0; i < tailTripleList.size() / 20; i++) {//todo 数据改过来
            List<Triple> classTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, (String) tailTripleList.get(i).getH());
            for (int j = 0; j < classTriple.size(); j++) {
                typeOfConTerms.add((String) classTriple.get(j).getT());
            }
        }

        /**
         * 作为头节点时，查看其 尾尾尾尾尾尾尾尾tail节点 及节点类型
         */
        List<Triple> headTripList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
        for (int i = 0; i < headTripList.size() / 20; i++) {//todo 数据改过来
            List<Triple> classTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, (String) headTripList.get(i).getH());
            for (int j = 0; j < classTriple.size(); j++) {
                typeOfConTerms.add((String) classTriple.get(j).getT());
            }
        }

        return typeOfConTerms;
    }

    /**
     * 见findConnectedTerms，返回数据结构含义为 Map<term，Set<与keyTerm关联的term>>
     */
    public static Map<String, Set<String>> findConnectedTermList(Collection<String> termsList) {
        Map<String, Set<String>> res = new HashMap<>();
        for (String ele : termsList) {
            Set<String> connectedTermList = new HashSet<>();
            connectedTermList.addAll(findConnectedTerms(ele));
            res.put(ele, connectedTermList);
        }
        return res;
    }

    /**
     * 查找以term为头或尾节点的 尾头节点的 term集合
     */
    public static Set<String> findConnectedTerms(String term) {
        /**
         * 查找关联的实体使，每种类型只选出一个实体（apache-jena不支持group去重，因此使用Map），防止选出过多的实体，比如使用dbo:author关联的海明威相关的书籍实体只选出一个即可。
         */
        Map<String, String> disTypeKeyMap = new HashMap<>();
        List<Triple> tailTripleList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
        for (Triple needTail : tailTripleList) {
            String tmpTail = (String) needTail.getT();
            if (tmpTail.contains("http://dbpedia.org/resource/") && !((String) needTail.getL()).contains("http://dbpedia.org/ontology/wikiPageWikiLink")) {
                disTypeKeyMap.put((String)needTail.getL(),tmpTail);
            }
        }
        List<Triple> headTripleList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TAIL, term);
        for (Triple needHead : headTripleList) {
            String tmpHead = (String) needHead.getH();
            if (tmpHead.contains("http://dbpedia.org/resource/") && !((String) needHead.getL()).contains("http://dbpedia.org/ontology/wikiPageWikiLink")) {
                disTypeKeyMap.put((String)needHead.getL(),tmpHead);
            }
        }

        return new HashSet<String>(disTypeKeyMap.values());
    }
}
