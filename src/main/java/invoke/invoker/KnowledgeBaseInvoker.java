package invoke.invoker;

import cons.Constants;
import cons.TermInfo;
import cons.Triple;
import invoke.invocation.DBpediaInvocation;
import lombok.Data;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static cons.Constants.*;

/**
 * @author 杜艮魁
 * @date 2018/10/10
 */
@Data
public class KnowledgeBaseInvoker {
    private String localDir;
    private String netAdd;

    public KnowledgeBaseInvoker(InvokerBuilder builder) {
        this.localDir = builder.getLocalDir();
        this.netAdd = builder.getNetAdd();
    }

    public static InvokerBuilder newInvokerBuilder(){
        return new InvokerBuilder();
    }

    /**
     * 查找每个term的属性集合
     */
    @Deprecated
    public static List<TermInfo> termListInfo(List<String> termsParam, String queryType) throws ExecutionException, InterruptedException {
        List<TermInfo> result = new LinkedList<>();
        List<Future> propsFutureHolder = new ArrayList();

        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < termsParam.size(); i++) {
            Future propFuture = executor.submit(new DBpediaInvocation(termsParam.get(i), queryType));
            propsFutureHolder.add(propFuture);
        }
        executor.shutdown();

        for (Future f : propsFutureHolder) {
            if (f.get() != null) {
                result.add((TermInfo) f.get());
            }
        }
        return result;
    }

    /**
     * 查找以term为head的三元组
     */
    @Deprecated
    public static Map<String, String> termLabelTail(String term) {
        Map<String, String> result = new HashMap<>();

        Dataset dataSet = null;
        QueryExecution execution = null;

        try {
            dataSet = TDBFactory.createDataset(LOD_TDB_DIR);
            dataSet.begin(ReadWrite.READ);

            Query query = QueryFactory.create("select * where{<" + term + "> ?pred ?val}");
            execution = QueryExecutionFactory.create(query, dataSet);

            ResultSet resultSet = execution.execSelect();

            while (resultSet.hasNext()) {
                QuerySolution sol = resultSet.next();
                result.put(sol.get("val").toString(), sol.get("pred").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查找以term为head的三元组(head,l,b)
     */
    public List<Triple> hlt(String qrySchema, String param) {
        List<Triple> resTriple = new LinkedList<>();
        /**
         * 打开数据资源
         */
        FileManager.get().addLocatorClassLoader(KnowledgeBaseInvoker.class.getClassLoader());
        Model model = FileManager.get().loadModel(getLocalDir());
        /**
         * 构造查询
         */
        String queryStr = qrySchema.replace(QUERY_PARAM, param);
        Query query = QueryFactory.create(queryStr);
        QueryExecution execution = QueryExecutionFactory.create(query, model);
        /**
         * 执行查询并获取结果
         */
        try {
            ResultSet results = execution.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.next();
                Triple<String, String, String> triple;
                switch (qrySchema) {
                    case Constants.QUERY_HEAD:
                        triple = new Triple(param, sol.get(LABEL).toString(), sol.get(TAIL).toString());
                        break;
                    case Constants.QUERY_TAIL:
                        triple = new Triple(sol.get(HEAD).toString(), sol.get(LABEL).toString(), param);
                        break;
                    case Constants.QUERY_TERM_CLASS:
                        triple = new Triple(param, Constants.TERM_TYPE_DESC, sol.get(TAIL).toString());
                        break;
                    case Constants.QUERY_BY_CLASS:
                        triple = new Triple(sol.get(HEAD),TERM_TYPE_DESC,param);
                        break;
                    default:
                        continue;
                }
                resTriple.add(triple);
            }
        } finally {
            execution.close();
            model.close();
        }
        return resTriple;
    }

    /**
     * 通过网络查找以term为head的三元组(head,l,b)
     */
    public static List<Triple> hltNet(String qrySchema, String param) {
        List<Triple> resTriple = new LinkedList<>();

        FileManager.get().addLocatorClassLoader(KnowledgeBaseInvoker.class.getClassLoader());
        String apikey = System.getenv("KASABI_API_KEY");

        String queryString = qrySchema.replaceAll(Constants.QUERY_PARAM,param);

        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("https://dbpedia.org/sparql", query);
        qexec.addParam("apikey", apikey);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.next();
                Triple<String, String, String> triple;
                switch (qrySchema) {
                    case Constants.QUERY_HEAD:
                        triple = new Triple(param, sol.get(LABEL).toString(), sol.get(TAIL).toString());
                        break;
                    case Constants.QUERY_TAIL:
                        triple = new Triple(sol.get(HEAD).toString(), sol.get(LABEL).toString(), param);
                        break;
                    case Constants.QUERY_TERM_CLASS:
                        triple = new Triple(param, Constants.TERM_TYPE_DESC, sol.get(TAIL).toString());
                        break;
                    case Constants.QUERY_BY_CLASS:
                        triple = new Triple(sol.get(HEAD),TERM_TYPE_DESC,param);
                        break;
                    default:
                        continue;
                }
                resTriple.add(triple);
            }
        } finally {
            qexec.close();
        }
        return resTriple;
    }
}
