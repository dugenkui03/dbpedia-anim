package main;

import cons.Constants;
import cons.Triple;
import edu.stanford.smi.protege.model.DefaultInstance;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import engine.Similarity;
import engine.SimilarityX;
import invoke.RelationTerm;
import invoke.invoker.KnowledgeBaseInvoker;
import util.*;

import java.util.*;


/**
 * fixme��ȥ��term�Ĵ��࣬����owlThing��Agent��
 * �ҵ������Ĵ��ﶨλ����term��صı���֪ʶ��ʵ�� todo����������ӦAgent��Human�����࣬��������ѡ��Human�࣬û����ʹ��Agent�ࣻ2.Human�����ж��ʵ��(��Ů����)��ͨ����ӹ���ʹ���ܹ�ѡ�񵽺��ʵ�ʵ��;3.���ҹ��������ݣ�����Ӻ������ҵ���
 */
public class RelationFind {

    /**
     * �������
     *
     * @param message �ֺô���Ķ�������
     * @return
     */
    public static Map<String, String> relFin(String message) throws Exception {
        Map<String, String> result = new HashMap();

        /**
         * ��ȡ���������õķִʼ��ϣ���ȥ��
         */
        Set<String> wordsZh = StringUtil.getEle(message);
        System.out.println("���������õķִ�:" + wordsZh);
        /**
         * ��ȡ�ִʼ������Ӧ��Ӣ�ļ��ϡ�ʵ����[��ͩ;wutong, ȫ�۵�;quanjude, ����;whale, ������;Hemingway]
         */
        List<String> wordsEng = ZhToEng.zhsToEngs(new ArrayList<>(wordsZh));
        System.out.println("�ִʼ������Ӧ��Ӣ�ĵ��ʼ���:" + wordsEng);
        /**
         * ��ȡ���Ŷ�Ӧ��term: 1.ʹ��label����(����ԭ���Ҳ�������Ӣ��ԭ��)��2.ʹ��pageRank˼�룻3. lookup todo:����ʵ��ʶ���Ǽ�����·��
         * Map<word,termUri>  Map<������;ErnestHemingWay��
         */
        Map<String, String> wordTermMap = NamedEntityIdentity.findEntities(wordsEng);
//        List<String> terms = LookUpUtil.lookUp(wordsEng); fixme��ʹ���Լ��ĳ�����Ҵ�����ص�term
        System.out.println("���Ŷ�Ӧ��term��" + wordTermMap);
        /*==============================================================================================*/
        /**
         * ��Ҫ�ҵ��������ݣ�fixme����term�������Ĵ˵���������������ʹ�Ǵ� ȫ�۵� �õ�һ���� ȫ�۵� ���ˡ�������׼ȷ������һ�����⣻
         *      1. �������ݣ�term���ڱ���֪ʶ����������ģ��/�ص�����ƣ����硰���꣬���ˡ���
         *      2. �������ݣ�term���������ݡ�
         *
         * һ.�������ݣ�
         *      1.��ȡterm�����ͣ�
         *      2.��ѯ����֪ʶ������ͬ���͵�ʵ����
         *      3.��term����Ѱ��term�����ƵĲ�ͬ���͵ı�����Ʒ�������ء������߾�ֻѡһ����
         *
         * ��.�������ݣ�
         *      1.��ȡterm������term�����͡������ǹ������ݵ����ͣ�
         *      2.3Ҳͬ�ϡ�
         */
        /*==============================================================================================*/
        /**
         * �鿴term�������� �� term��������:
         *      1.termsType��term��������ͣ�ÿ��term��Ӧһ�����ϣ�
         *      2.termInfoTriple��term�������Ϣ���������Ժ�����ֵ��
         * fixme:1.�鿴�Ƿ��еȼ۹�ϵ�����Ķ���ʵ����2.�鿴�������Ƿ��붯��֪ʶ���������еȼ۹�ϵ��3.�鿴���Ƿ����������ʵ�����߹�����ʵ���Ƿ��붯����ʵ���еȼ۹�ϵ��4.�鿴�������ʵ��������Ƿ��붯�����������еȼ۹�ϵ��5.�ظ�3��4���裻
         */
        Map<String, Set<String>> termsDBpediaTypeList = new HashMap<>();//fixme tagX
        Map<String, List<Triple>> termsDesc = new HashMap<>();
        for (String term : wordTermMap.values()) {
            //��ѯterm�����ͣ�Ȼ���ȡ��������Ϣ
            Set<String> termsType = new LinkedHashSet<>();
            List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
            for (Triple triple : classTrips) {
                termsType.add((String) triple.getT());
            }
            termsType.remove("http://www.w3.org/2002/07/owl#Thing");
            termsType.remove("http://dbpedia.org/ontology/Agent");
            termsDBpediaTypeList.put(term, termsType);
            //��ѯterm����Ϣ
            List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
            termsDesc.put(term, infoTriple);
        }

        /**
         *  fixme ��ѯ��dbpedia_term���еȼ۹�ϵ�������ϵ�����͡���Խ����ȨֵԽ�� tagX
         *  ���ݽṹ˵����
         *         termsAnimTypeMap��Map<term_uri, List<termTypes>>  ,  Map<termUri��List<term������>>
         *         termsSubAnimTypeMap��Map<String, Map<String,List<String>>>  �� Map<termUri��Map<termĳ���ͣ�List<ĳ���͵���������>>
         */
        Map<String, List<String>> termsAnimTypeMap = new HashMap<>();
        Map<String, Map<String, List<OWLNamedClass>>> termsSubAnimTypeMap = new HashMap<>();
        OWLModel relOwlModel = OlwModelUtil.getProtegeXModel(Constants.RELATION_MODEL_PATH);
        OWLModel animModel = OlwModelUtil.getProtegeXModel(Constants.ANIM_MODEL_PATH);
        for (String term : termsDBpediaTypeList.keySet()) {
            //term��DBpedia�е�����
            Set<String> termDBpediaTypeSet = termsDBpediaTypeList.get(term);

            //term�Ķ�������
            List<String> equvilentClasses = new LinkedList<>();

            //term���������ݴ洢�ӽṹ��Map<termĳ���ͣ�List<ĳ���͵���������>
            Map<String, List<OWLNamedClass>> termTypeAndSubTypes = new HashMap<>();

            //����term�Ƿ����ж�����������
            for (String termDBpediaType : termDBpediaTypeSet) {
                try {
                    //1.term��DBpedia���Ͷ���2.ͨ��equivalentClass�����ҵ�term�ĵȼ�����;3.�Եȼ۵����ͽ��й��ˣ�ȡ�����еĶ�������
                    OWLNamedClass termDBpediaClz = relOwlModel.getOWLNamedClass(termDBpediaType);
                    Collection<RDFSNamedClass> equivalentAnimClasses = termDBpediaClz.getEquivalentClasses();
                    for (RDFSNamedClass equvalentAnimClz : equivalentAnimClasses) {
                        if (equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(0)) || equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(1)) || equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(2))) {
                            //fixme ����rel���ѯ��term�Ķ�������
                            equvilentClasses.add(equvalentAnimClz.getURI());

                            List<OWLNamedClass> subAnimClasses = new LinkedList<>();
                            subAnimClasses.addAll(animModel.getOWLNamedClass(equvalentAnimClz.getURI()).getSubclasses(true));
                            if (subAnimClasses != null && subAnimClasses.size() != 0) {
                                termTypeAndSubTypes.put(equvalentAnimClz.getURI(), subAnimClasses);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            termsSubAnimTypeMap.put(term, termTypeAndSubTypes);
            //term:DBpediaClz��1��n��,DBPEdiaClz:AnimCLz(1:n)
            termsAnimTypeMap.put(term, equvilentClasses);
        }


        /**
         * ���ұ��غ�term����ͬ���͵�ʵ��:
         * Map<termUri,Map<termRelatedType,List<Instances_of_termRelatedType>>>
         * Map<�����Ӧterm��uri,Map<term��صĶ�������(��ͬ���ͻ�������),List<keyֵ��Ӧ��ʵ��>>
         */
        Map<String, Map<String, List<DefaultOWLIndividual>>> terms2InstancesMap = new HashMap<>();
        for (String term : wordTermMap.values()) {
            boolean getEqualedInstances = false;
            Map<String, List<DefaultOWLIndividual>> relatedInstancesAndTypeMap = new HashMap<>();
            List<String> equvilentClasses = termsAnimTypeMap.get(term);
            if (equvilentClasses != null) {
                for (String equaledAnimClz : equvilentClasses) {
                    //fixme �����߼���ֻҪ�������ݣ���ģ�ͺͳ�������.ma��β��ʵ��
                    List<DefaultOWLIndividual> usefulInstances = CommonUtils.getUsefulInstances(animModel.getOWLNamedClass(equaledAnimClz).getInstances(false));
                    if (usefulInstances.size() != 0) {
                        relatedInstancesAndTypeMap.put(equaledAnimClz, usefulInstances);
                        getEqualedInstances = true;
                    }
                }
            }
            if (!getEqualedInstances) {
                //termsSubAnimTypeMap��Map<String, Map<String, List<OWLNamedClass>>> �����ǵ����Ķ���֪ʶ���е�����
                Map<String, List<OWLNamedClass>> subAnimClasses = termsSubAnimTypeMap.get(term);
                if (subAnimClasses != null && subAnimClasses.size() != 0) {
                    for (String type : subAnimClasses.keySet()) {
                        List<OWLNamedClass> oneTypeSubClasses = subAnimClasses.get(type);
                        for (OWLNamedClass subAnimClz : oneTypeSubClasses) {
                            //fixme �����߼���ֻҪ�������ݣ���ģ�ͺͳ�������.ma��β��ʵ��
                            List<DefaultOWLIndividual> usefulInstances = CommonUtils.getUsefulInstances(subAnimClz.getInstances(false));
                            if (usefulInstances.size() != 0) {
                                relatedInstancesAndTypeMap.put(subAnimClz.getURI(), usefulInstances);
                            }
                        }
                    }
                }
            }

            terms2InstancesMap.put(term, relatedInstancesAndTypeMap);
        }

        //��ӡ���ҵ�������ʵ�壻TODO��û�в���term������ʵ��
        terms2InstancesMap.keySet().forEach(x -> {
            Map<String, List<DefaultOWLIndividual>> xxx = terms2InstancesMap.get(x);
            terms2InstancesMap.get(x).keySet().forEach(y -> {
                xxx.get(y).forEach(yyy -> System.out.println(yyy.getURI()));
            });
        });


        /**
         * fixme ��ȡ����ʵ��������Ȼ��Ƚ����Ƴ̶�ѡ�� ����term ��Ӧ��term
         * termsDesc��terms2InstancesList�Ƚϣ��Ӻ���ѡ��������Ƶ�ʵ��
         */
        Map<String, String> term2Instance = SimilarityX.findSimInstances(termsDesc, terms2InstancesMap);//fixme tagX


/*====================================================����term��ѯ
                                                            ==============================================================================*/

        /**
         * fixme: �������ݲ�ѯ
         *       1.����term������connectedTerm������ �� ���ǵ���Ϣ��
         *       2.���Ҹ�connnectedTerm������ͬ�ı���֪ʶ���ʵ�壬�ҳ������Ƶ�ʵ��
         * TODO��category �����кܴ�Ĳο�����
         *
         * Map<term,Set<connectedTerm>>
         */
        Map<String, Set<String>> connectedTermsMap = RelationTerm.findConnectedTermList(wordTermMap.values());


        for (String mainTerm : connectedTermsMap.keySet()) {
            Set<String> xxx = connectedTermsMap.get(mainTerm);
            for (String yy : xxx) {
                System.out.println(yy);
            }
        }


        /**
         * ��ѯterm������term�����ͣ����˵��������¼������͵�term��
         *                    1.��termΪβ�ڵ㲢����Person�����ͣ���Ϊ�ܶණ���ķ����ߡ�ʹ���߶���һЩͨ�õ���Ʒ������term�ķ�����������������û��Ҫ����������ȥ����������鵽�˷����������ˣ�����������ȥ�������ڱ��ֶ�����
         *
         *      Map<connectedTerm,Set<connectedTermTypes>>
         *          todo������һ������10��term������ÿ��term�����ϰٸ�term�������Ҫÿ������ֻѡ��һ��term������̫�ķ�ʱ��
         */
        Map<String, Set<String>> connectedTermsTypeList = new HashMap<>();
        Map<String, List<Triple>> connectedTermsDesc = new HashMap<>();
        for (String mainTerm : connectedTermsMap.keySet()) {
            Set<String> connectedTermX = connectedTermsMap.get(mainTerm);
            for (String term : connectedTermX) {
                //��ѯterm�����ͣ�Ȼ���ȡ��������Ϣ
                Set<String> termsType = new LinkedHashSet<>();
                List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
                for (Triple triple : classTrips) {
                    String tmpType = (String) triple.getT();
                    if (!Constants.DBPEDIA_BIG_TYPE.contains(tmpType)) {
                        termsType.add(tmpType);
                    }
                }
                connectedTermsTypeList.put(term, termsType);

                //��ѯterm����Ϣ
                List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
                connectedTermsDesc.put(term, infoTriple);
            }
        }


        /**
         *  fixme ��ѯ��dbpedia_term���еȼ۹�ϵ�������ϵ�����͡���Խ����ȨֵԽ�� tagX
         *  ���ݽṹ˵����
         *         termsAnimTypeMap��Map<term_uri, List<termTypes>>  ,  Map<termUri��List<term������>>
         *         termsSubAnimTypeMap��Map<String, Map<String,List<String>>>  �� Map<termUri��Map<termĳ���ͣ�List<ĳ���͵���������>>
         */
        Map<String, List<String>> connectedTermsAnimTypeMap = new HashMap<>();
        Map<String, Map<String, List<OWLNamedClass>>> connectedTermsSubAnimTypeMap = new HashMap<>();
        for (String term : connectedTermsTypeList.keySet()) {
            Set<String> termDBpediaTypeSet = connectedTermsTypeList.get(term);

            List<String> equvilentClasses = new LinkedList<>();

            Map<String, List<OWLNamedClass>> termTypeAndSubTypes = new HashMap<>();

            for (String termDBpediaType : termDBpediaTypeSet) {
                try {
                    OWLNamedClass termDBpediaClz = relOwlModel.getOWLNamedClass(termDBpediaType);
                    Collection<RDFSNamedClass> equivalentAnimClasses = termDBpediaClz.getEquivalentClasses();
                    for (RDFSNamedClass equvalentAnimClz : equivalentAnimClasses) {
                        if (equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(0)) || equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(1)) || equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(2))) {
                            equvilentClasses.add(equvalentAnimClz.getURI());
                            List<OWLNamedClass> subAnimClasses = new LinkedList<>();
                            subAnimClasses.addAll(animModel.getOWLNamedClass(equvalentAnimClz.getURI()).getSubclasses(true));
                            if (subAnimClasses != null && subAnimClasses.size() != 0) {
                                termTypeAndSubTypes.put(equvalentAnimClz.getURI(), subAnimClasses);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("term:\t"+term+"\t term's animClzes:"+equvilentClasses);
            connectedTermsSubAnimTypeMap.put(term, termTypeAndSubTypes);
            connectedTermsAnimTypeMap.put(term, equvilentClasses);
        }

        /**
         * ���ұ��غ�term����ͬ���͵�ʵ��: todo:ֱ�ӹ��������������ǣ��������ֱ�������¶��Ҳ���ʵ�壬����������������ʵ�塣�����޸�Ϊ�����������ĳ���������ҵ�ʵ�壬��������������ʵ��
         * Map<termUri,Map<termRelatedType,List<Instances_of_termRelatedType>>>
         * Map<�����Ӧterm��uri,Map<term��صĶ�������(��ͬ���ͻ�������),List<keyֵ��Ӧ��ʵ��>>
         */
        Map<String, Map<String, List<DefaultOWLIndividual>>> connectedTerms2InstancesMap = new HashMap<>();
        for (String connectedTerm : connectedTermsAnimTypeMap.keySet()) {

            Map<String, List<DefaultOWLIndividual>> relatedInstancesAndTypeMap = new HashMap<>();
            List<String> equvilentClasses = connectedTermsAnimTypeMap.get(connectedTerm);
            if (equvilentClasses != null) {
                for (String equaledAnimClz : equvilentClasses) {
                    boolean getEqualedInstances = false;
                    //fixme �����߼���ֻҪ�������ݣ���ģ�ͺͳ�������.ma��β��ʵ��
                    List<DefaultOWLIndividual> usefulInstances = CommonUtils.getUsefulInstances(animModel.getOWLNamedClass(equaledAnimClz).getInstances(false));
                    if (usefulInstances.size() != 0) {
                        relatedInstancesAndTypeMap.put(equaledAnimClz, usefulInstances);
                        getEqualedInstances = true; //todo �����ϲ��ܼ��������࣬�������ﲻ�ܸ��ݾ���ҵ�food�����±ߵĶ���
                    }

                    if (!getEqualedInstances) {
                        //termsSubAnimTypeMap��Map<String, Map<String, List<OWLNamedClass>>> �����ǵ����Ķ���֪ʶ���е�����
                        Map<String, List<OWLNamedClass>> subAnimClasses = connectedTermsSubAnimTypeMap.get(connectedTerm);
                        if (subAnimClasses != null && subAnimClasses.size() != 0) {
                            for (String type : subAnimClasses.keySet()) {
                                List<OWLNamedClass> oneTypeSubClasses = subAnimClasses.get(type);
                                for (OWLNamedClass subAnimClz : oneTypeSubClasses) {
                                    //fixme �����߼���ֻҪ�������ݣ���ģ�ͺͳ�������.ma��β��ʵ��
                                    List<DefaultOWLIndividual> usefulInstancesx = CommonUtils.getUsefulInstances(subAnimClz.getInstances(false));
                                    if (usefulInstancesx.size() != 0) {
                                        relatedInstancesAndTypeMap.put(subAnimClz.getURI(), usefulInstancesx);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("connected Term:"+connectedTerm+"\t relatedIns:\t"+relatedInstancesAndTypeMap);
            connectedTerms2InstancesMap.put(connectedTerm, relatedInstancesAndTypeMap);
        }

        Map<String, String> connectedTerm2Instance = SimilarityX.findSimInstances(connectedTermsDesc, connectedTerms2InstancesMap);//fixme tagX

        System.out.println("connected TermMap:"+connectedTerm2Instance);

        /**
         *  fixme ��ʽ�������
         *  <word_term,anim_ins><connected_term,anim_ins>
         *  ʾ����<������,businessMan.ma> <Old_man_and_Sea,book.ma>
         *
         */
        Map<String,String> res=new HashMap<>();
        for (String key:term2Instance.keySet()) {
            res.put(key,term2Instance.get(key)+";1");
        }
        for (String key:connectedTerm2Instance.keySet()) {
            res.put(key,connectedTerm2Instance.get(key)+";0");
        }

        return res;
    }

}
