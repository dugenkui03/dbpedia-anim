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
 * fixme：去掉term的大类，比如owlThing和Agent等
 * 找到与中文词语定位到的term相关的本地知识库实例 todo：海明威对应Agent和Human两个类，让其优先选择Human类，没有则使用Agent类；2.Human类下有多个实体(男女老少)，通过添加公理使其能够选择到合适的实体;3.查找关联的数据，比如从海明威找到人
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
         * 获取分词集合相对应的英文集合。实例：[梧桐;wutong, 全聚德;quanjude, 鲸鱼;whale, 海明威;Hemingway]
         */
        List<String> wordsEng = ZhToEng.zhsToEngs(new ArrayList<>(wordsZh));
        System.out.println("分词集合相对应的英文单词集合:" + wordsEng);
        /**
         * 获取短信对应的term: 1.使用label属性(中文原词找不到则用英文原词)；2.使用pageRank思想；3. lookup todo:命名实体识别那几个套路。
         * Map<word,termUri>  Map<海明威;ErnestHemingWay，
         */
        Map<String, String> wordTermMap = NamedEntityIdentity.findEntities(wordsEng);
//        List<String> terms = LookUpUtil.lookUp(wordsEng); fixme：使用自己的程序查找词语相关的term
        System.out.println("短信对应的term：" + wordTermMap);
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
         * fixme:1.查看是否有等价关系关联的动画实例；2.查看其类型是否与动画知识库中类型有等价关系；3.查看其是否关联动画库实例或者关联的实例是否与动画库实体有等价关系；4.查看其关联的实体的类型是否与动画库中数据有等价关系；5.重复3、4步骤；
         */
        Map<String, Set<String>> termsDBpediaTypeList = new HashMap<>();//fixme tagX
        Map<String, List<Triple>> termsDesc = new HashMap<>();
        for (String term : wordTermMap.values()) {
            //查询term的类型，然后获取其类型信息
            Set<String> termsType = new LinkedHashSet<>();
            List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
            for (Triple triple : classTrips) {
                termsType.add((String) triple.getT());
            }
            termsType.remove("http://www.w3.org/2002/07/owl#Thing");
            termsType.remove("http://dbpedia.org/ontology/Agent");
            termsDBpediaTypeList.put(term, termsType);
            //查询term的信息
            List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
            termsDesc.put(term, infoTriple);
        }

        /**
         *  fixme 查询和dbpedia_term具有等价关系和子类关系的类型——越靠近权值越高 tagX
         *  数据结构说明：
         *         termsAnimTypeMap：Map<term_uri, List<termTypes>>  ,  Map<termUri，List<term的类型>>
         *         termsSubAnimTypeMap：Map<String, Map<String,List<String>>>  ， Map<termUri，Map<term某类型，List<某类型的所有子类>>
         */
        Map<String, List<String>> termsAnimTypeMap = new HashMap<>();
        Map<String, Map<String, List<OWLNamedClass>>> termsSubAnimTypeMap = new HashMap<>();
        OWLModel relOwlModel = OlwModelUtil.getProtegeXModel(Constants.RELATION_MODEL_PATH);
        OWLModel animModel = OlwModelUtil.getProtegeXModel(Constants.ANIM_MODEL_PATH);
        for (String term : termsDBpediaTypeList.keySet()) {
            //term在DBpedia中的类型
            Set<String> termDBpediaTypeSet = termsDBpediaTypeList.get(term);

            //term的动画类型
            List<String> equvilentClasses = new LinkedList<>();

            //term子类型数据存储子结构：Map<term某类型，List<某类型的所有子类>
            Map<String, List<OWLNamedClass>> termTypeAndSubTypes = new HashMap<>();

            //查找term是否有有动画类型描述
            for (String termDBpediaType : termDBpediaTypeSet) {
                try {
                    //1.term的DBpedia类型对象；2.通过equivalentClass属性找到term的等价类型;3.对等价的类型进行过滤，取出其中的动画类型
                    OWLNamedClass termDBpediaClz = relOwlModel.getOWLNamedClass(termDBpediaType);
                    Collection<RDFSNamedClass> equivalentAnimClasses = termDBpediaClz.getEquivalentClasses();
                    for (RDFSNamedClass equvalentAnimClz : equivalentAnimClasses) {
                        if (equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(0)) || equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(1)) || equvalentAnimClz.getURI().contains(Constants.ANIM_TERM_PREFIX.get(2))) {
                            //fixme 根据rel库查询到term的动画类型
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
            //term:DBpediaClz（1：n）,DBPEdiaClz:AnimCLz(1:n)
            termsAnimTypeMap.put(term, equvilentClasses);
        }


        /**
         * 查找本地和term有相同类型的实例:
         * Map<termUri,Map<termRelatedType,List<Instances_of_termRelatedType>>>
         * Map<词语对应term的uri,Map<term相关的动画类型(相同类型或者子类),List<key值对应的实体>>
         */
        Map<String, Map<String, List<DefaultOWLIndividual>>> terms2InstancesMap = new HashMap<>();
        for (String term : wordTermMap.values()) {
            boolean getEqualedInstances = false;
            Map<String, List<DefaultOWLIndividual>> relatedInstancesAndTypeMap = new HashMap<>();
            List<String> equvilentClasses = termsAnimTypeMap.get(term);
            if (equvilentClasses != null) {
                for (String equaledAnimClz : equvilentClasses) {
                    //fixme 过滤逻辑：只要部分数据，即模型和场景，以.ma结尾的实体
                    List<DefaultOWLIndividual> usefulInstances = CommonUtils.getUsefulInstances(animModel.getOWLNamedClass(equaledAnimClz).getInstances(false));
                    if (usefulInstances.size() != 0) {
                        relatedInstancesAndTypeMap.put(equaledAnimClz, usefulInstances);
                        getEqualedInstances = true;
                    }
                }
            }
            if (!getEqualedInstances) {
                //termsSubAnimTypeMap：Map<String, Map<String, List<OWLNamedClass>>> 后者是单独的动画知识库中的类型
                Map<String, List<OWLNamedClass>> subAnimClasses = termsSubAnimTypeMap.get(term);
                if (subAnimClasses != null && subAnimClasses.size() != 0) {
                    for (String type : subAnimClasses.keySet()) {
                        List<OWLNamedClass> oneTypeSubClasses = subAnimClasses.get(type);
                        for (OWLNamedClass subAnimClz : oneTypeSubClasses) {
                            //fixme 过滤逻辑：只要部分数据，即模型和场景，以.ma结尾的实体
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

        //打印查找到的所有实体；TODO：没有查找term关联的实体
        terms2InstancesMap.keySet().forEach(x -> {
            Map<String, List<DefaultOWLIndividual>> xxx = terms2InstancesMap.get(x);
            terms2InstancesMap.get(x).keySet().forEach(y -> {
                xxx.get(y).forEach(yyy -> System.out.println(yyy.getURI()));
            });
        });


        /**
         * fixme 获取本地实例描述，然后比较相似程度选择 单词term 对应的term
         * termsDesc和terms2InstancesList比较，从后者选择出最相似的实例
         */
        Map<String, String> term2Instance = SimilarityX.findSimInstances(termsDesc, terms2InstancesMap);//fixme tagX


/*====================================================关联term查询
                                                            ==============================================================================*/

        /**
         * fixme: 关联数据查询
         *       1.查找term关联的connectedTerm的类型 和 他们的信息；
         *       2.查找跟connnectedTerm类型相同的本地知识库的实体，找出最相似的实体
         * TODO：category 属性有很大的参考意义
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
         * 查询term关联的term的类型，过滤掉具有以下几种类型的term：
         *                    1.以term为尾节点并且是Person的类型，因为很多东西的发明者、使用者都是一些通用的物品，比如term的发明者是汽车，但是没必要把汽车放上去，但是如果抽到了发明汽车的人，把汽车放上去则有助于表现动画；
         *
         *      Map<connectedTerm,Set<connectedTermTypes>>
         *          todo：短信一共不到10个term，这里每个term关联上百个term，因此需要每个类型只选出一个term，否则太耗费时间
         */
        Map<String, Set<String>> connectedTermsTypeList = new HashMap<>();
        Map<String, List<Triple>> connectedTermsDesc = new HashMap<>();
        for (String mainTerm : connectedTermsMap.keySet()) {
            Set<String> connectedTermX = connectedTermsMap.get(mainTerm);
            for (String term : connectedTermX) {
                //查询term的类型，然后获取其类型信息
                Set<String> termsType = new LinkedHashSet<>();
                List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
                for (Triple triple : classTrips) {
                    String tmpType = (String) triple.getT();
                    if (!Constants.DBPEDIA_BIG_TYPE.contains(tmpType)) {
                        termsType.add(tmpType);
                    }
                }
                connectedTermsTypeList.put(term, termsType);

                //查询term的信息
                List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
                connectedTermsDesc.put(term, infoTriple);
            }
        }


        /**
         *  fixme 查询和dbpedia_term具有等价关系和子类关系的类型——越靠近权值越高 tagX
         *  数据结构说明：
         *         termsAnimTypeMap：Map<term_uri, List<termTypes>>  ,  Map<termUri，List<term的类型>>
         *         termsSubAnimTypeMap：Map<String, Map<String,List<String>>>  ， Map<termUri，Map<term某类型，List<某类型的所有子类>>
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
         * 查找本地和term有相同类型的实例: todo:直接关联的数据类型是：如果所有直接类型下都找不到实体，则在所有子类下找实体。这里修改为，如果不能在某个类型下找到实体，则再其子类下找实体
         * Map<termUri,Map<termRelatedType,List<Instances_of_termRelatedType>>>
         * Map<词语对应term的uri,Map<term相关的动画类型(相同类型或者子类),List<key值对应的实体>>
         */
        Map<String, Map<String, List<DefaultOWLIndividual>>> connectedTerms2InstancesMap = new HashMap<>();
        for (String connectedTerm : connectedTermsAnimTypeMap.keySet()) {

            Map<String, List<DefaultOWLIndividual>> relatedInstancesAndTypeMap = new HashMap<>();
            List<String> equvilentClasses = connectedTermsAnimTypeMap.get(connectedTerm);
            if (equvilentClasses != null) {
                for (String equaledAnimClz : equvilentClasses) {
                    boolean getEqualedInstances = false;
                    //fixme 过滤逻辑：只要部分数据，即模型和场景，以.ma结尾的实体
                    List<DefaultOWLIndividual> usefulInstances = CommonUtils.getUsefulInstances(animModel.getOWLNamedClass(equaledAnimClz).getInstances(false));
                    if (usefulInstances.size() != 0) {
                        relatedInstancesAndTypeMap.put(equaledAnimClz, usefulInstances);
                        getEqualedInstances = true; //todo 理论上不能继续找子类，否则这里不能根据卷饼找到food子类下边的东西
                    }

                    if (!getEqualedInstances) {
                        //termsSubAnimTypeMap：Map<String, Map<String, List<OWLNamedClass>>> 后者是单独的动画知识库中的类型
                        Map<String, List<OWLNamedClass>> subAnimClasses = connectedTermsSubAnimTypeMap.get(connectedTerm);
                        if (subAnimClasses != null && subAnimClasses.size() != 0) {
                            for (String type : subAnimClasses.keySet()) {
                                List<OWLNamedClass> oneTypeSubClasses = subAnimClasses.get(type);
                                for (OWLNamedClass subAnimClz : oneTypeSubClasses) {
                                    //fixme 过滤逻辑：只要部分数据，即模型和场景，以.ma结尾的实体
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
         *  fixme 格式化输出：
         *  <word_term,anim_ins><connected_term,anim_ins>
         *  示例：<海明威,businessMan.ma> <Old_man_and_Sea,book.ma>
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
