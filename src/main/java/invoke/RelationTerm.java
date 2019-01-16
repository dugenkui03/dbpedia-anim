package invoke;

import cons.Constants;
import cons.Triple;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.*;

/**
 * @author ���޿�
 * @date 2018/9/26
 */
public class RelationTerm {

    /**
     * ���� findConnectedTypeTerms ����ĳ��term������term�����͡�fixme һ��term�����Ϳ����кܶ��ֵ�
     */
    public static Set<String> findConnectedTypeTermList(List<String> termsList){
        Set<String> typeOfConTerms = new HashSet<>();
        for (String ele : termsList) {
            typeOfConTerms.addAll(findConnectedTypeTerms(ele));
        }
        typeOfConTerms.forEach(x-> System.out.println(x));
        return typeOfConTerms;
    }

    /**
     * ������term������term��term���ϵ����ͣ�������term����(h,l,term)��(term,l,t)�е�h��t������
     * todo �������ͻ�Ӧ�ð��� category:
     */
    public static Set<String> findConnectedTypeTerms(String term) {
        Set<String> typeOfConTerms = new HashSet<>();
        /**
         * �������Ϊβ�ڵ�ʱ  ͷͷͷͷͷͷͷͷhead�ڵ�  ���ڵ�����:todo ȥ��+����ִ�м�С������
         */
        List<Triple> tailTripleList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TAIL, term);
        for (int i = 0; i < tailTripleList.size() / 20; i++) {//todo ���ݸĹ���
            List<Triple> classTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, (String) tailTripleList.get(i).getH());
            for (int j = 0; j < classTriple.size(); j++) {
                typeOfConTerms.add((String) classTriple.get(j).getT());
            }
        }

        /**
         * ��Ϊͷ�ڵ�ʱ���鿴�� ββββββββtail�ڵ� ���ڵ�����
         */
        List<Triple> headTripList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
        for (int i = 0; i < headTripList.size() / 20; i++) {//todo ���ݸĹ���
            List<Triple> classTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, (String) headTripList.get(i).getH());
            for (int j = 0; j < classTriple.size(); j++) {
                typeOfConTerms.add((String) classTriple.get(j).getT());
            }
        }

        return typeOfConTerms;
    }

    /**
     *  ��findConnectedTerms
     */
    public static Set<String> findConnectedTermList(Collection<String> termsList){
        Set<String> connectedTermList = new HashSet<>();
        for (String ele:termsList) {
            connectedTermList.addAll(findConnectedTerms(ele));
        }
        return connectedTermList;
    }

    /**
     * ������termΪͷ��β�ڵ�� βͷ�ڵ�� term����
     */
    public static Set<String> findConnectedTerms(String term){
        Set<String> connectedTerm = new HashSet<>();
        List<Triple> tailTripleList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD,term);
        for (Triple needTail:tailTripleList) {
            connectedTerm.add((String)needTail.getT());
        }
        List<Triple> headTripleList = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TAIL,term);
        for (Triple needHead:headTripleList) {
            connectedTerm.add((String)needHead.getH());
        }
        return connectedTerm;
    }
}
