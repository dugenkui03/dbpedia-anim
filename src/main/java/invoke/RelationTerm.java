package invoke;

import cons.Constants;
import cons.Triple;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 杜艮魁
 * @date 2018/9/26
 */
public class RelationTerm {

    public static Set<String> findTypeOfConnectedTermList(List<String> termsList){
        Set<String> typeOfConTerms = new HashSet<>();

        for (String ele : termsList) {
            typeOfConTerms.addAll(findTypeOfConnectedTerm(ele));
        }

        typeOfConTerms.forEach(x-> System.out.println(x));
        return typeOfConTerms;
    }

    /**
     * 查找与term关联的term的term集合的类型：关联的term包括(h,l,term)和(term,l,t)中的h和t的类型
     * todo 除了类型还应该包括 category:
     *
     * @param term
     * @return
     */
    public static Set<String> findTypeOfConnectedTerm(String term) {
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
}
