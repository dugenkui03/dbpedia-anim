package invoke.invocation;

import cons.TermInfo;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.util.*;
import java.util.concurrent.*;

/**
 * ����term������
 * @author ���޿�
 * @date 2018/3/11
 */
public class DBpediaInvocation implements Callable<TermInfo>{
    private String term;
    private String queryType;

    public DBpediaInvocation(String term, String queryType) {
        this.term = term;
        this.queryType=queryType;
    }

    @Override
    public TermInfo call() {
        TermInfo termInfo = new TermInfo();
        List<TermInfo.Node> termInfoMap = new LinkedList();

        FileManager.get().addLocatorClassLoader(DBpediaInvocation.class.getClassLoader());
        String uri = "http://dbpedia.org/sparql";
        String apikey = System.getenv("KASABI_API_KEY");//��Ȼϵͳû�ж���������������������ƺ�û������

        QueryExecution qexec = null;
        try {

            termInfo.setTermName(term);
            if(term.contains("Old_")){
                System.out.println("stop");
            }

            String strQuery;
            if(queryType.equals("SUBJECT")){
                strQuery = "select * where "+
                        " SERVICE <" + uri + ">{ <" + term + "> " + "?pred ?predValue .} }";
            }else{
                strQuery ="SELECT * WHERE { " +
                        "	SERVICE <" + uri + ">{ ?sub ?pred <"+term+"> .} }";
            }
            Query query = QueryFactory.create(strQuery);
            //����Դurl���ڲ�ѯ����У�ʹ��Ĭ��Model���󡪡�����QueryExecution����
            qexec = QueryExecutionFactory.create(query, ModelFactory.createDefaultModel());

            // Set additional parameters on a per SERVICE basis, see also: JENA-195
            //Ϊִ������Ӷ���Ĳ������±ߵ�ע����Ҳ��������
            Map<String, Map<String,List<String>>> serviceParams = new HashMap<String, Map<String,List<String>>>();
            Map<String,List<String>> params = new HashMap<String,List<String>>();
            List<String> values = new ArrayList<String>();

            values.add(apikey);
            params.put("apikey", values);
            serviceParams.put(uri, params);
            qexec.getContext().set(ARQ.serviceParams, serviceParams);

            ResultSet resultSet = qexec.execSelect();
            while (resultSet.hasNext()) {
                //todo ����ֵӦ�üȰ�������Ҳ��������ֵ
                TermInfo.Node node = new TermInfo.Node();
                QuerySolution querySolution = resultSet.next();
                node.setKey("<" + querySolution.get("pred").toString() + ">");
                if(queryType.equals("SUBJECT")) {
                    node.setValue("<" + querySolution.get("predValue").toString() + ">");
                }else{
                    node.setValue("<" + querySolution.get("sub").toString() + ">");
                }
                termInfoMap.add(node);
            }
            termInfo.setInfos(termInfoMap);

            return termInfo;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("term_name: " + termInfo.getTermName());
            return null;
        } finally {
            //fixme ��Ҫ
            if(qexec!=null){
                qexec.close();
            }
        }
    }

//    /**
//     * ����ÿ��term�����Լ���
//     *
//     */
//    public static List<TermInfo> termListInfo(List<String> termsParam, String queryType) throws ExecutionException, InterruptedException {
//        List<TermInfo> result = new LinkedList<>();
//        List<Future> propsFutureHolder = new ArrayList();
//
//        ExecutorService executor = Executors.newCachedThreadPool();
//        for (int i = 0; i < termsParam.size(); i++) {
//            Future propFuture=executor.submit(new DBpediaInvocation(termsParam.get(i),queryType));
//            propsFutureHolder.add(propFuture);
//        }
//        executor.shutdown();
//
//        for (Future f: propsFutureHolder) {
//            if(f.get()!=null){
//                result.add((TermInfo) f.get());
//            }
//        }
//        return result;
//    }

//    /**
//     * key������ֵ��value�����ԣ���Ϊ���Գ����ظ�
//     */
//    public static Map<String, String> termLabelTail(String term) {
//        Map<String, String> result = new HashMap<>();
//
//        Dataset dataSet = null;
//        QueryExecution execution = null;
//
//        try {
//            dataSet = TDBFactory.createDataset(LOD_TDB_DIR);
//            dataSet.begin(ReadWrite.READ);
//
//            Query query = QueryFactory.create("select * where{<" + term + "> ?pred ?val}");
//            execution = QueryExecutionFactory.create(query, dataSet);
//
//            ResultSet resultSet = execution.execSelect();
//
//            while (resultSet.hasNext()) {
//                QuerySolution sol = resultSet.next();
//                result.put(sol.get("val").toString(), sol.get("pred").toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
