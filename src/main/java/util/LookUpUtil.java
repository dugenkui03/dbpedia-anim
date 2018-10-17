package util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 杜艮魁
 * @date 2018/3/9
 */
public class LookUpUtil implements Callable<String> {
    //查询接口
    private static final String KEYWORD_SEARCH_ADDRESS = "http://lookup.dbpedia.org/api/search/KeywordSearch?";

    private String keyWordsEng;

    public LookUpUtil(String keyWordsEng) {
        this.keyWordsEng = keyWordsEng;
    }

    @Override
    public String call() {
        try {
            String path = "QueryString=" + keyWordsEng + "&QueryClass=" + "&MaxHits=1";

            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(9000).build();
            HttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

            HttpGet httpGet = new HttpGet(KEYWORD_SEARCH_ADDRESS + path);
            httpGet.addHeader("Accept", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpGet);

            String entities = EntityUtils.toString(httpResponse.getEntity());
            JsonNode entity = new ObjectMapper().readTree(entities).get("results").get(0);
            return entity.get("uri").getTextValue();
        } catch (NullPointerException e) {
            System.out.println("没有此关键字对应的term:" + keyWordsEng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找英文词语对应的term
     *
     * @param keyWordsEng
     * @return
     */
    public static List<String> lookUp(List<String> keyWordsEng) throws ExecutionException, InterruptedException {
        List<String> result = new LinkedList<>();
        List<Future> termsHolder = new ArrayList<>();

        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < keyWordsEng.size(); i++) {
            Future termTask = executor.submit(new LookUpUtil(keyWordsEng.get(i)));
            termsHolder.add(termTask);
        }
        executor.shutdown();

        for (Future ele : termsHolder) {
            if (ele.get() != null)
                result.add((String) ele.get());
        }

        return result;
    }
}
