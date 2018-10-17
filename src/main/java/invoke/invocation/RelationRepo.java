package invoke.invocation;

import cons.TermInfo;
import org.apache.jena.query.*;
import org.apache.jena.tdb.TDBFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 用户查询关联信息的函数：
 *      1.TDB数据集类Dataset：1）打开本地TDB库；2）确定本地库的使用类型
 *      2.Query类：使用query语句构建查询类实例
 *      3.数据集实例和查询执行器不用的时候一定要关闭
 * @author 杜艮魁
 * @date 2018/3/11
 */
public class RelationRepo{
    //存放关联信息的TDB位置
    private static final String RELATION_TDB_DIR="E:\\TDB\\RELTDB";

    //使用请求参数的备份
    private static List<TermInfo> termInfoMapCopy;

    /**
     * 使用term对应的：
     *      1.类型，比如人、树；
     *      2.属性：比如“性别为”
     *      3.经过处理转换过的属性：比如“出生日期”转换为“年龄为”
     * 在关联数据库中查找关联属性——todo 注意应该直接在链表中增加内容，因为本地知识库扁平化的时候也可能用到DBpedia中的数据
     * @param termInfoMap term及其属性与属性值
     * @return 与此term相关的 “类型值”及属性列表，需要在关联数据库中查询
     */
    public Map<String,List<String>> queryLocal(List<TermInfo> termInfoMap) throws Exception{
        //复制链表
        List<TermInfo> termInfoMapCopy=new LinkedList<>();
        for (TermInfo termInfo:termInfoMap){
            termInfoMapCopy.add(termInfo);
        }

        //获取各个term的 类型与属性的集合
        Map<String,List<String>> propsAndType=getPropsAndType(termInfoMapCopy);

        //用term的每个 类型与属性的集合 在关联数据库中查找是否有关联的，有的话则追加进相应的链表
        propsAndType=addRelatedPropsAndType(propsAndType);

        return propsAndType;
    }

    /**
     *  获取各个term的类型+属性集合,fixme key是term，所以可以用map
     * @param termInfoMapCopyx
     * @return
     */
    private Map<String,List<String>> getPropsAndType(List<TermInfo> termInfoMapCopyx) throws InterruptedException {
        Map<String,List<String>> result=new ConcurrentHashMap<>();

        for (TermInfo termInfo : termInfoMapCopyx) {
            //获取其 类型与属性集合
            List<String> prodAndType=new ArrayList<>();
            List<TermInfo.Node> nodes=termInfo.getInfos();
            for (TermInfo.Node node:nodes) {
                //如果属性是类型，就将value放入里边，否则放入key。前者比如 person类，后者比如birthDay
                if((node.getKey().equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")||node.getKey().equals("<http://purl.org/dc/terms/subject>"))&&
                        !prodAndType.contains(node.getValue())){
                    prodAndType.add(node.getValue());
                }
                else if(!prodAndType.contains(node.getKey())){
                    prodAndType.add(node.getKey());
                }
            }
            result.put(termInfo.getTermName(),prodAndType);//放入一个term及其对应的 类型/属性 链表
        }

        return result;
    }

    /**
     * 将关联的 属性或者类追加 各个term的 类型和属性集合 fixme 注意是追加进去
     * @param termsAndTheirProdsAndType
     * @return
     */
    private Map<String,List<String>> addRelatedPropsAndType(Map<String,List<String>> termsAndTheirProdsAndType) throws InterruptedException {
        Map<String,List<String>> result=new HashMap<>();

        Set<String> keys=termsAndTheirProdsAndType.keySet();
        Dataset relDataset= TDBFactory.createDataset(RELATION_TDB_DIR);
        relDataset.begin(ReadWrite.READ);
        try {
            for (String key : keys) {
                List<String> prodsAndType = termsAndTheirProdsAndType.get(key);
                List<String> prodsAndTypeAndRelations=new ArrayList<>();

                for (String value:prodsAndType) {//fixme 避免 ConcurrentModificationException
                    prodsAndTypeAndRelations.add(value);
                    //构建Query实例 fixme:在关联知识库中，DBpedia中的东西都是属性值，本地知识库中的东西都是实体
                    Query queryS = QueryFactory.create("SELECT * WHERE { ?obj ?perd " + value + "}");//以term为属性的实例或者类

                    //fixme 使用query实例和TDB库做
                    QueryExecution qexecS = QueryExecutionFactory.create(queryS, relDataset);
                    try {
                        ResultSet resultSetS = qexecS.execSelect();
                        while (resultSetS.hasNext()) {
                            QuerySolution querySolution = resultSetS.nextSolution();
                            //如果term属性集合里边没有，则追加进去
                            if(!prodsAndTypeAndRelations.contains("<" + querySolution.get("obj").toString() + ">")){
                                prodsAndTypeAndRelations.add("<" + querySolution.get("obj").toString() + ">");
                            }
                        }
                        result.put(key,prodsAndTypeAndRelations);
                    } finally {
                        //关闭此次查询执行器
                        qexecS.close();
                    }
                }
            }
        }finally {
            //关闭本次数据集
            relDataset.end();
        }

        return result;
    }

    /**
     * 查看两个属性是否相同
     * @param mPred
     * @param tPred
     * @return
     */
    public static boolean sameAs(String mPred,String tPred){
        Dataset dataSet=null;
        QueryExecution execution=null;

        try {
            dataSet=TDBFactory.createDataset(RELATION_TDB_DIR);
            dataSet.begin(ReadWrite.READ);

            if(!(mPred.startsWith("http")&&tPred.startsWith("http"))){
                return false;
            }

            Query query=QueryFactory.create("select * where{<"+mPred+"> ?pred <"+tPred+">}");
            execution=QueryExecutionFactory.create(query,dataSet);

            ResultSet resultSet=execution.execSelect();

            while(resultSet.hasNext()){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(mPred);
            System.out.println(tPred);
        }finally {
            if(execution!=null)
                execution.close();
            if(dataSet!=null)
                dataSet.end();
        }
        return false;
    }
}
