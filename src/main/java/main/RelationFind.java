package main;

import cons.Constants;
import cons.Triple;
import invoke.RelationTerm;
import invoke.invoker.KnowledgeBaseInvoker;
import util.LookUpUtil;
import util.StringUtil;
import util.ZhToEng;

import java.util.*;

/**
 * 找到与中文词语定位到的term相关的本地知识库实例
 */
public class RelationFind {

    /**
     * 程序入口
     *
     * @param message 分好词语的短信内容
     * @return
     */
    public static Map<String, String> relFin(String message) throws Exception {
        Map<String, String> result = new HashMap();

        /**
         * 获取短信中有用的分词集合，并去重
         */
        Set<String> wordsZh = StringUtil.getEle(message);
        System.out.println("短信中有用的分词:" + wordsZh);
        /**
         * 获取分词集合相对应的英文集合
         */
        List<String> wordsEng = ZhToEng.zhsToEngs(new ArrayList<>(wordsZh));
        System.out.println("分词集合相对应的英文单词集合:" + wordsEng);
        /**
         * 获取短信对应的term
         */
        List<String> terms = LookUpUtil.lookUp(wordsEng);
        System.out.println("短信对应的term：" + terms);
        /*==============================================================================================*/
        /**
         * 需要找到两类数据：fixme：以term就是中文此的完美描述——即使是从 全聚德 得到一个叫 全聚德 的人——工具准确性是另一个问题；
         *      1. 相似数据：term和在本地知识库中描述的模型/地点很相似，比如“中年，男人”；
         *      2. 关联数据：term关联的数据。
         *
         * 一.相似数据：
         *      1.获取term的类型；
         *      2.查询本地知识库有相同类型的实例；
         *      3.从term描述寻找term最相似的不同类型的本地物品，并返回——或者就只选一个。
         *
         * 二.关联数据：
         *      1.获取term关联的term的类型——这是关联数据的类型；
         *      2.3也同上。
         */
        /*==============================================================================================*/
        /**
         * 查看term本身的类别 和 term本身描述:
         *      1.termsType：term本身的类型，每个term对应一个集合；
         *      2.termInfoTriple：term本身的信息，包括属性和属性值；
         */
        Map<String,Set<String>> termsTypeList = new HashMap<>();
        Map<String,List<Triple>> termsDesc = new HashMap<>();
        for (String term : terms) {
            //查询term的类型，然后获取其类型信息
            Set<String> termsType = new LinkedHashSet<>();
            List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
            for (Triple triple : classTrips) {
                termsType.add((String) triple.getT());
            }
            termsTypeList.put(term,termsType);
            //查询term的信息
            List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
            termsDesc.put(term,infoTriple);
        }
        /**
         * 查找本地和term有相同类型的实例
         */
        Map<String,Set<String>> terms2InstancesList = new HashMap<>();
        KnowledgeBaseInvoker kbi = KnowledgeBaseInvoker.newInvokerBuilder().localDir(Constants.ANIM_OWL_PATH).build();
        for (Map.Entry<String,Set<String>> termTypes : termsTypeList.entrySet()) {
            Set<String> sameTypesInstanceList = new HashSet<>();
            //一个term可以有很多个类型，sameTypesInstanceList即与某个term所有type之一类型相同的实例集合
            for (String type:termTypes.getValue()) {
                List<Triple> locakInstance = kbi.hlt(Constants.QUERY_BY_CLASS, type);
                for (Triple triple : locakInstance) {
                    String animTermType = triple.getH().toString();
                    sameTypesInstanceList.add(animTermType);
                }
            }
            terms2InstancesList.put(termTypes.getKey(),sameTypesInstanceList);
        }
        /**
         * todo 获取本地实例描述，然后比较相似程度选择 单词term 对应的term
         * termsDesc和terms2InstancesList比较，从后者选择出最相似的实例
         */


        /**
         * 获取term关联的term的类型
         */
        Set<String> relType = RelationTerm.findTypeOfConnectedTermList(terms);
        /**
         * 查找本地知识库中同种类型的term todo:改善：多次查询打开一次数据库：静态代码块和静态方法
         */
        List<String> sameTypeRelAnimTermList = new LinkedList<>();
        for (String type : relType) {
            List<Triple> locakInstance = kbi.hlt(Constants.QUERY_BY_CLASS, type);
            for (Triple triple : locakInstance) {
                String animTermType = triple.getH().toString();
                sameTypeRelAnimTermList.add(animTermType);
            }
        }

        for (String ele : sameTypeRelAnimTermList) {
            System.out.println(ele);
        }

        /**
         * todo 求相似度最高的本地模型
         */

        return null;
    }
}
