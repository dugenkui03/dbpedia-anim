package invoke.invocation;

import cons.TermInfo;
import org.apache.jena.query.*;
import org.apache.jena.tdb.TDBFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * �û���ѯ������Ϣ�ĺ�����
 *      1.TDB���ݼ���Dataset��1���򿪱���TDB�⣻2��ȷ�����ؿ��ʹ������
 *      2.Query�ࣺʹ��query��乹����ѯ��ʵ��
 *      3.���ݼ�ʵ���Ͳ�ѯִ�������õ�ʱ��һ��Ҫ�ر�
 * @author ���޿�
 * @date 2018/3/11
 */
public class RelationRepo{
    //��Ź�����Ϣ��TDBλ��
    private static final String RELATION_TDB_DIR="E:\\TDB\\RELTDB";

    //ʹ����������ı���
    private static List<TermInfo> termInfoMapCopy;

    /**
     * ʹ��term��Ӧ�ģ�
     *      1.���ͣ������ˡ�����
     *      2.���ԣ����硰�Ա�Ϊ��
     *      3.��������ת���������ԣ����硰�������ڡ�ת��Ϊ������Ϊ��
     * �ڹ������ݿ��в��ҹ������ԡ���todo ע��Ӧ��ֱ�����������������ݣ���Ϊ����֪ʶ���ƽ����ʱ��Ҳ�����õ�DBpedia�е�����
     * @param termInfoMap term��������������ֵ
     * @return ���term��ص� ������ֵ���������б���Ҫ�ڹ������ݿ��в�ѯ
     */
    public Map<String,List<String>> queryLocal(List<TermInfo> termInfoMap) throws Exception{
        //��������
        List<TermInfo> termInfoMapCopy=new LinkedList<>();
        for (TermInfo termInfo:termInfoMap){
            termInfoMapCopy.add(termInfo);
        }

        //��ȡ����term�� ���������Եļ���
        Map<String,List<String>> propsAndType=getPropsAndType(termInfoMapCopy);

        //��term��ÿ�� ���������Եļ��� �ڹ������ݿ��в����Ƿ��й����ģ��еĻ���׷�ӽ���Ӧ������
        propsAndType=addRelatedPropsAndType(propsAndType);

        return propsAndType;
    }

    /**
     *  ��ȡ����term������+���Լ���,fixme key��term�����Կ�����map
     * @param termInfoMapCopyx
     * @return
     */
    private Map<String,List<String>> getPropsAndType(List<TermInfo> termInfoMapCopyx) throws InterruptedException {
        Map<String,List<String>> result=new ConcurrentHashMap<>();

        for (TermInfo termInfo : termInfoMapCopyx) {
            //��ȡ�� ���������Լ���
            List<String> prodAndType=new ArrayList<>();
            List<TermInfo.Node> nodes=termInfo.getInfos();
            for (TermInfo.Node node:nodes) {
                //������������ͣ��ͽ�value������ߣ��������key��ǰ�߱��� person�࣬���߱���birthDay
                if((node.getKey().equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")||node.getKey().equals("<http://purl.org/dc/terms/subject>"))&&
                        !prodAndType.contains(node.getValue())){
                    prodAndType.add(node.getValue());
                }
                else if(!prodAndType.contains(node.getKey())){
                    prodAndType.add(node.getKey());
                }
            }
            result.put(termInfo.getTermName(),prodAndType);//����һ��term�����Ӧ�� ����/���� ����
        }

        return result;
    }

    /**
     * �������� ���Ի�����׷�� ����term�� ���ͺ����Լ��� fixme ע����׷�ӽ�ȥ
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

                for (String value:prodsAndType) {//fixme ���� ConcurrentModificationException
                    prodsAndTypeAndRelations.add(value);
                    //����Queryʵ�� fixme:�ڹ���֪ʶ���У�DBpedia�еĶ�����������ֵ������֪ʶ���еĶ�������ʵ��
                    Query queryS = QueryFactory.create("SELECT * WHERE { ?obj ?perd " + value + "}");//��termΪ���Ե�ʵ��������

                    //fixme ʹ��queryʵ����TDB����
                    QueryExecution qexecS = QueryExecutionFactory.create(queryS, relDataset);
                    try {
                        ResultSet resultSetS = qexecS.execSelect();
                        while (resultSetS.hasNext()) {
                            QuerySolution querySolution = resultSetS.nextSolution();
                            //���term���Լ������û�У���׷�ӽ�ȥ
                            if(!prodsAndTypeAndRelations.contains("<" + querySolution.get("obj").toString() + ">")){
                                prodsAndTypeAndRelations.add("<" + querySolution.get("obj").toString() + ">");
                            }
                        }
                        result.put(key,prodsAndTypeAndRelations);
                    } finally {
                        //�رմ˴β�ѯִ����
                        qexecS.close();
                    }
                }
            }
        }finally {
            //�رձ������ݼ�
            relDataset.end();
        }

        return result;
    }

    /**
     * �鿴���������Ƿ���ͬ
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
