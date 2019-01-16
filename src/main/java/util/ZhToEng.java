package util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * ��Ӣ�ķ���
 */
public class ZhToEng implements Callable<String> {

    ////////////////////////�ϱ����Լ��Ĵ��룬�±�������//////////////////////////////////////////////////////////////////
    private String zhWrod;

    public ZhToEng(String zhWrod) {
        this.zhWrod=zhWrod;
    }

    @Override
    public String call() {
        try {
            String sign = md5("61d24bb2920bcf31" + zhWrod + "33" + "dpSOAdmpTWhJhuOXPS46YZyJpMbTNFIq");
            Map params = new HashMap();
            params.put("q", zhWrod);
            params.put("from", "zh-CHS");
            params.put("to", "EN");
            params.put("sign", sign);
            params.put("salt", "33");
            params.put("appKey", "61d24bb2920bcf31");
            String enWord = requestForHttp("http://openapi.youdao.com/api", params);
            if (enWord != "") {
                enWord = enWord.replaceAll("the", "");
                enWord = enWord.replaceAll("The", "");
                enWord = enWord.replaceAll(" ", "_");
                if (enWord.startsWith("_")) {
                    enWord = enWord.substring(1);
                }
            }
            return zhWrod+";"+enWord;
        } catch (Exception e) {
            e.printStackTrace();
            return zhWrod+";";
        }
    }

    ////////////////////////�ϱ����Լ��Ĵ��룬�±�������//////////////////////////////////////////////////////////////////

    /**
     * ���ص��ʻ�������
     */
    private static String requestForHttp(String url, Map requestParams) throws Exception {
        String result;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List params = new ArrayList();
        Iterator it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Entry en = (Entry) it.next();
            String key = (String) en.getKey();
            String value = (String) en.getValue();
            if (value != null) {
                params.add(new BasicNameValuePair(key, value));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {
            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity, "utf-8");
            JsonNode entity = new ObjectMapper().readTree(response).get("translation").get(0);
            result = entity.getTextValue();

            EntityUtils.consume(httpEntity);//����������򿪣���ر�
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * ����32λMD5ֵ
     */
    private String md5(String content) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (content == null) {
            return null;
        }
        byte[] btInput = content.getBytes("utf-8");
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(btInput);
        byte[] md = mdInst.digest();

        /** ������ת����ʮ�����Ƶ��ַ�����ʽ */
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char str[] = new char[md.length * 2];

        int k = 0;
        for (byte byte0 : md) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }

        return new String(str);
    }

    /**
     * �����ĵ��ʼ��Ϸ����Ӣ��
     */
    public static List<String> zhsToEngs(List<String> wordsZh) throws ExecutionException, InterruptedException {
        List<String> result = new LinkedList<>();
        List<Future> engWordHolder = new LinkedList<>();
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < wordsZh.size(); i++) {
            Future task = executor.submit(new ZhToEng(wordsZh.get(i)));
            engWordHolder.add(task);
        }
        executor.shutdown();
        for (Future task : engWordHolder) {
            if (task.get() != null) {
                result.add((String) task.get());
            }
        }
        return result;
    }
}
