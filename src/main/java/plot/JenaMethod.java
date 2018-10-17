package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.BitSet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
//import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class JenaMethod {
    /**
     * 创建一个空owl文件
     */
    static String maName = "";
    static OWLNamedClass englishTopicClass = null;
    static String englishTopicStr = "";
    static String englishTopicIE = "";
    static String englishTopicMG = "";
    static String englishTopicQiu = "";
    static ArrayList<String> englishTemplate = new ArrayList();
    static ArrayList<String> TemplateName = new ArrayList();
    static ArrayList<String> colorTemplate = new ArrayList();
    static ArrayList<String> modelWithColor = new ArrayList();
    static ArrayList<String> modelWithColors = new ArrayList();
    static Logger logger = Logger.getLogger(JenaMethod.class.getName());
    static ArrayList<SceneCase> sceneList = new ArrayList();
    public static ArrayList<SceneCase> sceneListTopic = new ArrayList();
    public static String maNameWord = "";
    static BitSet sCaseDataUsable = new BitSet(30);//
    static ArrayList<String> actionTemplateAttr = new ArrayList();// 用来保存动作模板及其原子信息
    static ArrayList<String> usedModelAttr = new ArrayList();// 用来保存在添加和更改规则中用到的模型，为删除规则所用
    static ArrayList<String> colorChangeAttr = new ArrayList();// 保存传递给刘畅的变色数组
    static int colorModelNum = 0;// 保存传递给刘畅的变色数组数目
    static ArrayList<String> timeweatherandfog = new ArrayList();// 保存传递给刘畅的时间参数
    public static ArrayList<String> timeweatherandfog1 = new ArrayList();// 用于字幕的获取
    static ArrayList<String> moodTemplateAttr = new ArrayList();// 用来保存情绪模板及其原子信息
    static ArrayList<String> weatherAndmoodAttr = new ArrayList();
    static ArrayList<ArrayList> windRainSnowNeedAttr = new ArrayList();// 用来保存LHH需要的模板信息
    static ArrayList<String> ExpressionList = new ArrayList();// 用来保存许向辉的模板信息
    static boolean bIsBackgroundScene = false;
    static boolean hasWeatherTeplate = false;
    static boolean hasTimeTemplate = false;
    static boolean people = false;
    static ArrayList<String> WindAttr = new ArrayList();
    static ArrayList<String> RainAttr = new ArrayList();
    static ArrayList<String> SnowAttr = new ArrayList();
    static ArrayList<String> ActionNeedPeople = new ArrayList();
    static ArrayList<String> LightList = new ArrayList();// 用来保存HL需要的模板信息
    static ArrayList<String> SeasonList = new ArrayList();
    static ArrayList<String> WeatherList = new ArrayList();
    static ArrayList<String> topiclist = new ArrayList();//存放英文主题的链表
    static ArrayList<String> topictemplate = new ArrayList();
    static int[] num = new int[2];
    static boolean ifActionOrExpression = false;

    public JenaMethod() {

    }

    /**
     * 主程序，处理各种规则
     *
     * @param topic：IE抽取的主题
     * @param templateAttr:中文模板
     * @throws OntologyLoadException
     * @throws SWRLFactoryException
     * @throws SWRLRuleEngineException
     * @throws IOException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static void processMaFile(ArrayList<String> topic, ArrayList<String> templateAttr,
                                     ArrayList<String> templateWithColor, ArrayList<String> colorMark, ArrayList<String> topicFromMG,
                                     ArrayList<String> topicFromQiu, String strNegType, int count[], ArrayList<String> TopicAndTemplate)
            throws OntologyLoadException, SWRLFactoryException, SWRLRuleEngineException, SecurityException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

        ArrayList<String> englishTopicAndTemplate = new ArrayList();

        // 打开owl文件,通过url获得owl模型
        String url = "file:///c:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
        OWLModel model = createOWLFile1(url);

        // OWL模型对象和"主题名+模板名称s"==》英文Topic+模板名称链表
        ArrayList<String> englishTemplateW = new ArrayList();
        englishTemplateW = chineseTemplate3English(TopicAndTemplate, model);
        System.out.println("最终选的模板名字是：" + englishTemplateW);

        // 云雨雪模板在上行代码函数中赋值，与各种主题对应
        windRainSnowNeedAttr.add(0, WindAttr);
        windRainSnowNeedAttr.add(1, RainAttr);
        windRainSnowNeedAttr.add(2, SnowAttr);

        if (ProgramEntrance.isMiddleMessage) {
            // "只有中间结果，直接将短信信息以文字形式打入空场景中"
            maName = "empty.ma";
        }

        // 遍历英文模板放在链表中：带概率值后截取即不带概率值
        for (int i = 0; i < englishTemplateW.size(); i++) {// 去掉最后的分值
            int iP = englishTemplateW.get(i).lastIndexOf(":");
            englishTemplate.add(englishTemplateW.get(i).substring(0, iP));
        }

        // ***********************从plotTemplate中由中文到英文***************************8//
        ArrayList englishTemplatePlot = new ArrayList();
        englishTemplatePlot = chineseTemplate2EnglishFromPlot(templateAttr, model);
        // ***********************从plotTemplate中由中文到英文***************************8//
        num = count;// 数组复制，count[0]存放的是人物模板的个数，count[1]存放的是非人物模板个数

        // 收集场景并评分：重点、重要重点、重要重点、重要重点、重要： 获取全部的场景,包括对风雨雪动作的作用
        SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName, englishTemplatePlot, num);
        // OWLModel类对象、英文模板、颜色模板
        TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// 收集模板

        CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);

        maName = SceneSelected();
        //重点，重要，还没看
        if (maName == null || maName.length() == 0) {//如果没有选择出场景
            logger.info("没有根据评分选出场景");
            ifActionOrExpression = true;
            System.out.println(num[0] + "\t" + num[1]);
            num[1] = 0;// num[1]存放的是是否含有非人物模板，
            if (num[0] != 0) {
                SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName,
                        englishTemplatePlot, num);// 获取全部的场景,包括对风雨雪动作的作用
                TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// 收集模板
                CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);
                maName = SceneSelected();
            } else {

                SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName,
                        englishTemplatePlot, num);// 获取全部的场景,包括对风雨雪动作的作用
                TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// 收集模板
                CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);
                maName = SceneSelected();
            }

        }

        PlotResGenerate(model, topic, templateAttr, templateWithColor, colorMark, topicFromMG, strNegType,
                englishTemplatePlot);

        int sceneCount = sceneList.size();

        for (int i = 0; i < sceneCount; i++) {
            SceneCase temp = sceneList.get(i);
        }
        PrintSceneCase();
        for (int j = 0; j < sceneCount; j++) {
            sceneListTopic.add(sceneList.get(j));
        }
        maNameWord = maName;

        InitAllStaticValue();
    }

    /**
     * 根据位置获取OWL模型对象
     *
     * @param url：owl文件存在的路径
     * @return OWL模型对象
     * @throws OntologyLoadException
     */
    public static OWLModel createOWLFile1(String url) throws OntologyLoadException {
        try {
            OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
            return owlModel;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * 将中文主题+模板——转换———英文topic+英文模板
     *
     * @param topicAndTemplate 中文主题+模板s
     * @param model
     * @return 英文主题+英文模板s
     */
    private static ArrayList<String> chineseTemplate3English(ArrayList<String> topicAndTemplate, OWLModel model) {

        ArrayList<String> englishTemplate = new ArrayList();
        // 重要,重点：获取名为 chineseName 的数据属性对象
        OWLDatatypeProperty chinesename = model.getOWLDatatypeProperty("chineseName");

        for (Iterator it = topicAndTemplate.iterator(); it.hasNext(); ) {
            String str = (String) it.next();// next()
            String hasvalue[] = str.split("-");// 将主题+模板分别放在数组中
            String stemplate = "";
            String strin = "", strin1 = "", strin2 = "";
            String temp = "", temp2 = "", template = "";

            for (int i = 0; i < hasvalue.length; i++) {
                temp = hasvalue[i];

                if (i == 0) {// 如果是主题
                    // 根据主题中文名，获取主题相应的英文名
                    strin = chineseTemplateEnglish(temp, "Topic", model);
                    if (strin == null)
                        strin = "";
                } else {
                    temp2 = "";
                    strin2 = "";
                    template = "";
                    String[] hasvla = temp.split(":");// hasvla=[天气, 雪, 中雪]
                    String temp1 = hasvla[0];// temp1=天气

                    strin1 = chineseTemplateEnglish(hasvla[0], "Template", model);// 匹配英文模板
                    if (strin1 != null) {
                        stemplate = strin1;// stemplate=天气

                        if (strin1.equals("CharacterTemplate")) {
                            people = true;
                        }

                        strin2 = chineseTemplateEnglish(hasvla[1], strin1, model);
                        boolean flage_inver = false;
                        if (strin2 != null) {
                            OWLNamedClass temps = model.getOWLNamedClass(strin2);
                            if (hasvla[hasvla.length - 1].equals("1")) {

                                OWLObjectProperty mood_inver = model.getOWLObjectProperty("Mood_Inverse");

                                RDFResource obj = temps.getSomeValuesFrom(mood_inver);

                                System.out.println("反义（否定）=" + obj);
                                if (obj != null) {
                                    flage_inver = true;
                                    strin2 = obj.getBrowserText();
                                    temps = model.getOWLNamedClass(strin2);
                                }
                            }
                            strin1 = strin1 + ":" + strin2;
                            template = strin2;

                            if (temps.getInstanceCount() != 0) {
                                Collection ind = temps.getInstances(true);

                                boolean isget = false;
                                if (flage_inver) {
                                    for (Iterator ina = ind.iterator(); ina.hasNext(); ) {
                                        OWLIndividual in = (OWLIndividual) ina.next();
                                        isget = true;
                                        strin2 = in.getBrowserText();
                                        temp2 = template + ":" + strin2;//
                                        template = template + ":" + strin2 + ":1.0";
                                        strin1 = strin1 + ":" + strin2;
                                        break;
                                    }
                                } else {
                                    loop:
                                    for (Iterator ina = ind.iterator(); ina.hasNext(); ) {
                                        OWLIndividual in = (OWLIndividual) ina.next();
                                        if (in.getPropertyValueCount(chinesename) > 0) {
                                            Collection chineseValues = in.getPropertyValues(chinesename);
                                            for (Iterator its = chineseValues.iterator(); its.hasNext(); )// 循环实例所对应的多个中文名称
                                            {
                                                String cValue = its.next().toString();
                                                if (cValue.trim().equals(hasvla[hasvla.length - 2].trim())) {
                                                    isget = true;
                                                    strin2 = in.getBrowserText();
                                                    temp2 = template + ":" + strin2;//
                                                    template = template + ":" + strin2 + ":1.0";
                                                    strin1 = strin1 + ":" + strin2;
                                                    break loop;
                                                }

                                            }
                                        }

                                    }
                                }

                                if (isget == false) {
                                    Collection instance = temps.getInstances(false);
//									System.out.println(instance.size());
                                    if (instance.size() != 0) {
                                        Random r = new Random();
                                        int k = r.nextInt(instance.size());
                                        ArrayList<OWLIndividual> arrlist1 = new ArrayList<OWLIndividual>();
                                        for (Iterator insr = instance.iterator(); insr.hasNext(); ) {
                                            arrlist1.add((OWLIndividual) insr.next());
                                        }
                                        strin2 = arrlist1.get(k).getBrowserText();
                                        temp2 = template + ":" + strin2;//
                                        template = template + ":" + strin2 + ":0.5";
                                        strin1 = strin1 + ":" + strin2;
                                    } else {
                                        ArrayList<OWLIndividual> ils = new ArrayList<OWLIndividual>();
                                        for (Iterator ir = ind.iterator(); ir.hasNext(); ) {
                                            ils.add((OWLIndividual) ir.next());
                                        }
                                        Random ran = new Random();
                                        int k = ran.nextInt(ils.size());
                                        strin2 = ils.get(k).getBrowserText().toString();

                                        temp2 = template + ":" + strin2;

                                        template = template + ":" + strin2 + ":0.5";
                                        strin1 = strin1 + ":" + strin2;
                                    }

                                }
                            }

                        } else if (!stemplate.equals("WeatherTemplate") && strin2 == null) {
                            OWLNamedClass superclass1 = model.getOWLNamedClass(strin1);
                            if (superclass1.getInstanceCount(false) > 0) {

                                Collection instance = superclass1.getInstances(false);
//								System.out.println("size=" + instance.size());
                                Random r = new Random();
                                int k = r.nextInt(instance.size());
                                ArrayList<OWLIndividual> arrlist1 = new ArrayList<OWLIndividual>();
                                for (Iterator insr = instance.iterator(); insr.hasNext(); ) {
                                    arrlist1.add((OWLIndividual) insr.next());
                                }
                                strin2 = arrlist1.get(k).getBrowserText();
                                if (template == null || template.equals("")) {
                                    Collection Insuperclass = arrlist1.get(k).getDirectTypes();
                                    Random r1 = new Random();
                                    int m = r1.nextInt(Insuperclass.size());
                                    ArrayList<OWLNamedClass> arrlist2 = new ArrayList<OWLNamedClass>();
                                    for (Iterator insr1 = Insuperclass.iterator(); insr1.hasNext(); ) {
                                        arrlist2.add((OWLNamedClass) insr1.next());
                                    }
                                    template = arrlist2.get(m).getBrowserText().toString();

                                }

                                temp2 = template + ":" + strin2;//
                                strin1 = strin1 + ":" + temp2;
                                template = template + ":" + strin2 + ":0.5";

//								System.out.println("strin1beizhu=" + strin1);
//								System.out.println("template=" + temp2);
                            } else {
                                OWLNamedClass superclass = model.getOWLNamedClass(strin1);
                                if (superclass.getSubclassCount() > 0) {

                                    Collection clo = superclass.getSubclasses(true);
                                    ArrayList<OWLNamedClass> arrlist = new ArrayList<OWLNamedClass>();
                                    for (Iterator ins = clo.iterator(); ins.hasNext(); ) {
                                        arrlist.add((OWLNamedClass) ins.next());
                                    }
                                    Random ran1 = new Random();
                                    int k1 = ran1.nextInt(arrlist.size());
                                    OWLNamedClass temps = arrlist.get(k1);
                                    strin2 = temps.getBrowserText().toString();
                                    strin1 = strin1 + ":" + strin2;
                                    template = strin2;
                                    if (temps.getInstanceCount() != 0) {
                                        boolean isget = false;
                                        Collection ind = temps.getInstances(true);
                                        loop:
                                        for (Iterator ina = ind.iterator(); ina.hasNext(); ) {
                                            OWLIndividual in = (OWLIndividual) ina.next();
                                            if (in.getPropertyValueCount(chinesename) > 0) {
                                                Collection chineseValues = in.getPropertyValues(chinesename);
                                                for (Iterator its = chineseValues.iterator(); its.hasNext(); )// 循环实例所对应的多个中文名称
                                                {
                                                    String cValue = its.next().toString();
                                                    if (cValue.trim().equals(hasvla[hasvla.length - 2].trim())) {
                                                        isget = true;
                                                        strin2 = in.getBrowserText();
                                                        temp2 = template + ":" + strin2;//
                                                        template = template + ":" + strin2 + ":1.0";
                                                        strin1 = strin1 + ":" + strin2;
                                                        break loop;
                                                    }

                                                }
                                            }

                                        }
                                        if (isget == false) {
                                            ArrayList<OWLIndividual> ils = new ArrayList<OWLIndividual>();
                                            for (Iterator ir = ind.iterator(); ir.hasNext(); ) {
                                                ils.add((OWLIndividual) ir.next());
                                            }
                                            Random ran = new Random();
                                            int k = ran.nextInt(ils.size());
                                            strin2 = ils.get(k).getBrowserText().toString();

                                            temp2 = template + ":" + strin2;

                                            template = template + ":" + strin2 + ":0.5";
                                            strin1 = strin1 + ":" + strin2;
                                        }
                                    }

                                }
                            }
                        }

                    }
                    String temp3 = "";
                    if (hasvla[0].equals("时间") && !temp2.isEmpty()) {
                        LightList.add(temp2);
                    }
                    if (hasvla[0].equals("动作") && !temp2.isEmpty()) {
                        actionTemplateAttr.add(temp2);
                    }
                    if (hasvla[0].equals("人物") && !temp2.isEmpty()) {
                        ActionNeedPeople.add(temp2);
                    }
                    if (hasvla[0].equals("情绪") && !temp2.isEmpty()) {
                        temp3 = temp2 + ":" + "0";
                        // temp3=temp2+":"+hasvla[hasvla.length-1];
                        moodTemplateAttr.add(temp3);
                        weatherAndmoodAttr.add(temp3);
                        ExpressionList.add(temp2);
                        actionTemplateAttr.add(temp2);
                        WindAttr.add(temp3);
                        RainAttr.add(temp3);
                        SnowAttr.add(temp3);
                        LightList.add(temp2);
                    }
                    if ((hasvla[0].equals("天气") || hasvla[0].equals("季节")) && !temp2.isEmpty()) {
                        temp3 = temp2 + ":" + hasvla[hasvla.length - 1];
                        weatherAndmoodAttr.add(temp3);
                        RainAttr.add(temp3);
                        WindAttr.add(temp3);
                        SnowAttr.add(temp3);
                        LightList.add(temp2);
                        SeasonList.add(temp2);
                    }
                    if (hasvla[1].equals("天气温度") && !temp2.isEmpty()) {
                        actionTemplateAttr.add(temp2);
                    }
                }

                // 如果有模板
                if (template != null && template.length() != 0 && template.contains(":")) {
                    englishTemplate.add(template);// 英文模板添加英文模板内容
                }

                if (strin1 != null && strin1.length() != 0 && strin1.contains(":"))
                    strin = strin + "-" + strin1;
            }
            // 如果有动作模板，则随机添加人物模板
            if (!actionTemplateAttr.isEmpty() && ActionNeedPeople.isEmpty()) {
                Random r = new Random();
                int sel = r.nextInt(2);
                if (sel == 1) {
                    strin = strin + "-" + "CharacterTemplate:SingleCharacterTemplate:singlePersonTemplate";// random
                    // one
                    englishTemplate.add("SingleCharacterTemplate:singlePersonTemplate:1.0");
                } else {
                    strin = strin + "-" + "CharacterTemplate:ManCharacterTemplate:botherTemplate";
                    englishTemplate.add("ManCharacterTemplate:botherTemplate:1.0");
                }

            }
            topictemplate.add(strin);
        }

        return englishTemplate;
    }

    /**
     * 中文模板翻译成对应的英文模板
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> chineseTemplate2EnglishFromPlot(ArrayList<String> chineseTemplate, OWLModel model)
            throws SecurityException, IOException {
        ArrayList<String> englishTemplate = new ArrayList();

        OWLNamedClass templateClass = model.getOWLNamedClass("ModelRelatedPlot");
        OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection subTemplateClass = templateClass.getSubclasses(true);// 打印子类，并打印子类的子类
        for (Iterator<String> ist = chineseTemplate.iterator(); ist.hasNext(); ) {// 循环处理中文原子模板对应的原子信息
            String tempName = ist.next();

            boolean isStop = false;
            boolean findCls = false;
            A:
            for (Iterator itTemplate = subTemplateClass.iterator(); itTemplate// 循环Template下面的所有子类
                    .hasNext(); ) {
                if (isStop)
                    break;
                cls = (OWLNamedClass) itTemplate.next();// 到ontology中寻找模板，通过chineseName这个属性来查找
                Object cc = cls.getHasValue(chineseNameProperty);
                // System.out.println("hhhh:"+cls.getBrowserText()+"
                // test111:"+tempName);
                if (cc != null && cls.getHasValue(chineseNameProperty).toString().trim().equals(tempName.trim()))// 通过hasValue属性来获得模板的名字
                // ，表明是什么模板（时间或地点）
                {// 先查找对应的模板
                    String tempNameW = cls.getBrowserText();
                    // englishTemplate.add(tempNameW);
                    String templateVlaue = "";
                    if (ist.hasNext())
                        templateVlaue = ist.next();
                    // String[] splitTempName = new String[2];
                    String[] splitTempName = templateVlaue.split(":");
                    Collection subsubTemplateClass = cls.getSubclasses(true);// 打印出已查找的模板的子类
                    B:
                    for (Iterator itsTemplate = subsubTemplateClass.iterator(); itsTemplate.hasNext(); ) {
                        if (isStop)
                            break;

                        OWLNamedClass clss = (OWLNamedClass) itsTemplate.next();
                        Collection clssHasValues = clss.getHasValues(chineseNameProperty);

                        C:
                        for (Iterator itValue = clssHasValues.iterator(); itValue// 查找所对应模板下的子类
                                .hasNext(); ) {
                            if (isStop)
                                break;
                            Object value = itValue.next();
                            if (value.toString().trim().equals(splitTempName[0].trim()))// 用来匹配模板值的冒号前面的字段：如学校：小学，这里就是匹配“学校”的
                            {
                                findCls = true;
                                isStop = true;
                                String templateName = clss.getBrowserText() + ":";
                                if (clss.getInstanceCount() != 0) {
                                    ArrayList<String> templateInstan = new ArrayList();
                                    String autoValue = "";
                                    Collection templateInstances = clss.getInstances();// 当找到了模板所对应的类，则处理模板类对应的实例，也就是原子信息
                                    D:
                                    for (Iterator it = templateInstances.iterator(); it.hasNext(); ) {
                                        OWLIndividual templateIndividual = (OWLIndividual) it.next();// 打印出实例
                                        templateInstan.add(templateIndividual.getBrowserText());// 用来存储所有实例，主要是为了当没有找到符合要求的实例时就从中随机的选择一个
                                        if (templateIndividual.getPropertyValueCount(chineseNameProperty) > 0) {
                                            Collection chineseValues = templateIndividual
                                                    .getPropertyValues(chineseNameProperty);
                                            for (Iterator its = chineseValues.iterator(); its.hasNext(); )// 循环实例所对应的多个中文名称
                                            {
                                                String cValue = its.next().toString();
                                                if (cValue.trim()
                                                        .equals(splitTempName[splitTempName.length - 1].trim())) {
                                                    // templateIndividual.getpro
                                                    autoValue = templateIndividual.getBrowserText();

                                                    break D;
                                                } // 所对应实例的中文名称
                                            } // 结束实例的中文名字的循环
                                        } // 结束实例是否有中文名字

                                    } // 结束对应类的实例
                                    if (autoValue != "")// 模板实例中找到了相应的模板原子对应的信息
                                        templateName = templateName + autoValue + ":1.0";

                                    else // 当实例中找不到相应的模板原子信息，就从已经有的模板实例原子中随机挑选一个
                                    {
                                        Random rand = new Random();
                                        int kk = rand.nextInt(templateInstan.size());
                                        templateName = templateName + templateInstan.get(kk) + ":0.5";
                                    }

                                } // 结束实例的判断

                                if (!englishTemplate.contains(templateName)) {
                                    englishTemplate.add(templateName);
                                }
                                logger.info("模板信息翻译成英文后的值：" + templateName);
                            }

                        }
                        // Object ccc=cls.getHasValue(chineseNameProperty);

                    } // 模板名下子类的结束

                } // 判断是否有相对于的模板
            } // template类下的所有子类
        }

        return englishTemplate;
    }

    /**
     * 调用 getSceneFromClass()和getSceneFromTopic()收集场景
     *
     * @param model               OWLModel对象
     * @param topic               主题链表
     * @param topicFromMG         马庚主题链表
     * @param topicFromQiu        邱雄主题链表
     * @param englishTemplateList     英文模板：带分值
     * @param englishTempW        英文模板
     * @param englishTemplatePlot 英文模板
     * @param count               数组，count[0]保存人物模板个数,count[1]保存其他模板个数
     * @throws SecurityException
     * @throws IOException
     * @throws SWRLRuleEngineException
     * @throws OntologyLoadException
     */
    public static void SceneCollect(OWLModel model, ArrayList<String> topic, ArrayList<String> topicFromMG,
                                    ArrayList<String> topicFromQiu, ArrayList<String> englishTemplateList, ArrayList<String> englishTempW,
                                    ArrayList<String> englishTemplatePlot, int count[])
            throws SecurityException, IOException, SWRLRuleEngineException, OntologyLoadException {

        sceneList.clear();// 初始化保存场景的链表

        // ==================如果只有人物模板======================================
        if (count[0] != 0 && count[1] == 0) {
            // 背景场景分为4钟，地面、房间、雪地（和水下）
            OWLNamedClass backgroundLandScene = model.getOWLNamedClass("BackgroundLandScene");
            OWLNamedClass backgroundRoomScene = model.getOWLNamedClass("BackgroundRoomScene");
            OWLNamedClass backgroundSnowScene = model.getOWLNamedClass("BackgroundSnowScene");

            // 获取人物模板类的子类
            OWLNamedClass superclass = model.getOWLNamedClass("CharacterTemplate");
            Collection clo = superclass.getSubclasses();


            /**
             * 遍历英文模板类(不是实例),如果是CharacterTemplate子类，则添加背景场景
             */
            for (int c = 0; c < englishTemplateList.size(); c++) {
                String ele = englishTemplateList.get(c);
                String englishTemplate = ele.substring(0, ele.indexOf(":"));// 取到英文模板值
                // 如果人物人物模板子类不为0（在知识库定义，肯定的）
                if (clo.size() != 0) {
                    // 遍历人物模板CharacterTemplate的子类
                    for (Iterator ii = clo.iterator(); ii.hasNext(); ) {
                        OWLNamedClass subclass = (OWLNamedClass) ii.next();
                        String subCharacterTemplate = subclass.getBrowserText().toString();

                        if (subCharacterTemplate.equals(englishTemplate)) {// 如果人物模板与抽到的子类模板一致
                            // 已经知道抽到的模板，只有人物模板，他们是北京场景（来源于人物模板),1
                            getSceneFromClass(model, backgroundLandScene.getBrowserText().toString(), "BackGroundScene", 1);
                            getSceneFromClass(model, backgroundRoomScene.getBrowserText().toString(), "BackGroundScene", 1);
                            getSceneFromClass(model, backgroundSnowScene.getBrowserText().toString(), "BackGroundScene", 1);
                            break;
                        }
                    }
                }
            }

        } else {// 如果不是只有人物模板
            for (int i = 0; i < englishTemplateList.size(); i++) {// 遍历英文模板
                String Temp = englishTemplateList.get(i);
                String subTemp = Temp.split(":")[0];// 分离分值，取得模板名称
                OWLObjectProperty hasBG = model.getOWLObjectProperty("hasBackgroundScene");// 获取hasBackgroundScene对象属性对象
                OWLObjectProperty hasBGMa = model.getOWLObjectProperty("hasMa");// 获取hasMa对象属性对象
                OWLNamedClass templateClass = model.getOWLNamedClass("Template");// 得到模板类对象
                Collection templateClas = templateClass.getSubclasses(true); // 得到模板类的子孙类

                for (Iterator it = templateClas.iterator(); it.hasNext(); ) {

                    OWLNamedClass cls = (OWLNamedClass) it.next();// next()取得模板类的子孙类对象
                    if (cls.getBrowserText().trim().equals(subTemp.trim())) {// 如果模板类中类名有与所对比的模板名称相同的（英文）

                        if (cls.getSomeValuesFrom(hasBG) != null) {// 如果此模板有hasBackgroundScene属性
                            Object hasBGScene;
                            hasBGScene = cls.getSomeValuesFrom(hasBG);// 取出此属性值

                            if (hasBGScene.getClass().getName()
                                    .equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
                                OWLUnionClass hasBGUnion = (OWLUnionClass) cls.getSomeValuesFrom(hasBG);// 获取并集类
                                // 获取所有明明类（OWLNamedClass)的操作数（RDFSNamedClass）
                                Collection<?> hasBG_C = hasBGUnion.getNamedOperands();

                                for (Iterator<?> itBG = hasBG_C.iterator(); itBG.hasNext(); ) {
                                    OWLNamedClass hasBGClass = (OWLNamedClass) itBG.next();
                                    getSceneFromClass(model, hasBGClass.getBrowserText().toString(), "BackGroundScene",
                                            1);
                                }
                            } // end of("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")
                            else {
                                getSceneFromClass(model, ((RDFResource) hasBGScene).getBrowserText(), "BackGroundScene",
                                        1);
                            }
                        } // end 此模板是否有hasBackgroundScene属性

                        if (cls.getSomeValuesFrom(hasBGMa) != null) {// hasBGMa为OWLObjectProperty类的对象：hasMa，表示此模板有对应场景
                            Object hasMa = cls.getSomeValuesFrom(hasBGMa);// 获取属性值
                            // 获取场景名称、场景类型，进行评分
                            getSceneFromClass(model, ((RDFResource) hasMa).getBrowserText(), "TemplateScene", 1);
                        }
                    }
                }
            }
        } // end 如果不是只有人物模板

        double topicProbabilityMG = 0.0;
        if (topicFromMG.size() != 0) {// 如果马庚主题不为空
            int topicCount = topicFromMG.size();
            for (int i = 0; i < topicCount; i += 2) {
                String topicNameFromMG = topicFromMG.get(i);// 遍历马庚主题
                String topicProbabilityStr = topicFromMG.get(i + 1).trim();// 马庚主题概率
                topicProbabilityMG = Float.parseFloat(topicProbabilityStr);
                if (topicProbabilityMG > 0.6) {// 如果马庚主题》0.6
                    OWLNamedClass englishTopicClass = getEnglishTopic(model, topicNameFromMG);// 获取马庚主题类对象
                    if (englishTopicClass != null) {// 英文主题类不为空
                        englishTopicMG = englishTopicClass.getBrowserText();
                        getSceneFromClass(model, englishTopicMG, "MGTopic", topicProbabilityMG);
                        getSceneFromTopic(model, englishTopicClass, "MGTopic", topicProbabilityMG);
                    }
                }

            }
        }
        if (topicFromQiu.size() > 0) {// 处理邱雄主题
            int topicCount = topicFromQiu.size();
            for (int i = 0; i < topicCount; i += 2) {
                String topicNameFromQ = topicFromQiu.get(i);
                String topicProbabilityStr = topicFromQiu.get(i + 1).trim();
                double topicProbability = Float.parseFloat(topicProbabilityStr);
                if (topicProbability > 0.3) {
                    OWLNamedClass englishTopicClass = getEnglishTopic(model, topicNameFromQ);
                    if (englishTopicClass != null) {
                        englishTopicQiu = englishTopicClass.getBrowserText();

                        System.out.println("邱雄情绪主题:" + englishTopicQiu);
                        getSceneFromClass(model, englishTopicQiu, "QTopic", topicProbability);
                        getSceneFromTopic(model, englishTopicClass, "QiuTopic", topicProbability);
                    }
                }

            }
        }

        if (topic.size() != 0)// IEc抽到的topic
        {
            int topicCount = topic.size();
            for (int i = 0; i < topicCount; i++) {
                String topicName = topic.get(i);
                OWLNamedClass englishTopicClass = getEnglishTopic(model, topicName);
                if (englishTopicClass != null) {
                    englishTopicIE = englishTopicClass.getBrowserText();
                    System.out.println("IE部分的主题对应的英文主题：" + englishTopicIE);
                    getSceneFromClass(model, englishTopicIE, "IETopic", 1);
                    getSceneFromTopic(model, englishTopicClass, "IETopic", 1);
                }
            }
        }

        if (englishTemplateList.size() != 0)// 模板推场景
        {

            ArrayList<String> englishTemplateW = new ArrayList();
            for (int j = 0; j < englishTemplateList.size(); j++) {
                int iP = englishTemplateList.get(j).lastIndexOf(":");
                englishTemplateW.add(englishTemplateList.get(j).substring(0, iP));
            }

            logger.info("===========推理规则：利用模板信息推动画场景");
//			SWRLMethod.executeSWRLEngine1(model, "getTopicFromTemplate", "", englishTemplateW);// 通过模板推出主题

            logger.info("===========推理规则：利用模板推主题实体");// englishTemplateW为不带分数的英文模板
            String topicIndividual = getTopicFromTemplateAfterSWRL(model, englishTemplateW);

            if (!topicIndividual.isEmpty()) {
                System.out.println("推理规则：利用模板退出主题(ruleTopic):" + topicIndividual);
                OWLIndividual indi = model.getOWLIndividual(topicIndividual);// 获取主题实体
                String clasName = indi.getDirectType().getBrowserText();
                getSceneFromClass(model, clasName, "RuleTopic", 1);
            }
        }
        /**
         * 由sceneCollet 得到各个英文主题 设置主题englishTopicStr
         */
        // 添加ADL上面的Topic值
        if (!englishTopicIE.equals("")) {// 如果IE主题不为空
            englishTopicStr = englishTopicIE;
        } else {// 如果IE主题为空
            if (topicProbabilityMG > 0.6) {// 如果IE主题为空，而且MG主题概率大于0.6
                englishTopicStr = englishTopicMG;
            }
        }

        if (topicProbabilityMG > 0.6) {// 如果马庚主题概率大于0.6
            if (topiclist.size() != 0) {// 如果马庚主题概率大于0.6，而且主题链表不为空
                boolean topicflage = false;// 标识概率>0.6的马庚主题是否在主题链表中
                for (int k = 0; k < topiclist.size(); k++) {
                    if (topiclist.get(k).equals(englishTopicMG)) {// 如果马庚主题概率大于0.6，而且主题链表不为空,马庚主题在主题链表其中
                        topicflage = true;// 标识设置为true
                        break;
                    }
                }
                if (topicflage == false) {// 如果马庚主题>0.6，而且不在主题链表中，将马庚主题放到主题链表中
                    topiclist.add(englishTopicMG);
                }
            } else {// 如果马庚主题概率大于0.6，但是主题链表为空，则吧马庚主题放进主题链表中==不是场景链表
                topiclist.add(englishTopicMG);
            }
        } // end 如果马庚主题概率大于0.6

        /**
         * 判断模板动作与人物是否匹配(动作<->人物):
         * 1.如果匹配，将通过动作模板和人物模板的动画加入，否则从模板中去掉这两个模板值======？？？？？？
         */
        if (englishTemplateList.size() != 0) {// 如果英文模板不为空
            Action action = new Action();
            try {
                action.actionInfer(actionTemplateAttr, model, englishTopicStr);
                if (!action.isActionFlag()) {
                    englishTemplateList = delActionAndPeeople(englishTemplateList);
                    logger.info("动作模板和人物模板不匹配，去掉这两个模板：动作程序相关：");
                } else {// 动作和人物匹配
                    if (ifActionOrExpression == false)
                        ifActionOrExpression = true;
                }
            } catch (OntologyLoadException e) {
                logger.info("Action is error（动作程序出错)");
            }

            getAnim(englishTemplateList, model);// 此处将概率值去掉
        }

        // 2014.10.30删除不能从模板值直接得来
        if (englishTempW.size() != 0) {// 如果英文模板不为空
            ArrayList tempIndual = new ArrayList();
            for (int i = 0; i < englishTempW.size(); i++) {
                String Temp = englishTempW.get(i);// 遍历英文模板
                OWLNamedClass temp = model.getOWLNamedClass(Temp);// 获得英文模板类（遍历得来）
                Collection induals = temp.getDirectInstances();// 获得此英文模板类下的直接实例
                for (Iterator it = induals.iterator(); it.hasNext(); ) {
                    OWLIndividual tempInd = (OWLIndividual) it.next();
                    tempIndual.add(tempInd.getBrowserText());

                }

            }
            System.out.println("模板实例：" + tempIndual);
            getAnim(tempIndual, model);
        }

        /**重点：重点：自动生成场景
         * bjut.plot design 王金娟  新设计的用新的中文主题。 其中场景的名字以plot中设置的主题+“.ma”，组成新的场景名字 model
         * 为OWLAllFile外面的sum2 路径,先选定主题，对主题设计，没有主题才以模型命名新场景
         */
        if (topic.size() > 0) {//如果主题链表不为空，主题链表来自于getXML()
            for (int i = 0; i < topic.size(); i++) {
                String topicIE = topic.get(i);
                OWLNamedClass topicNamedCls = getEnglishTopicFromPlot(model, topicIE);//根据IE主题在TopicRelatedPlot类下找chineseName中文名与其对应的类
                if (topicNamedCls != null) {
                    String topicName = topicNamedCls.getBrowserText();//获取类名
                    if (englishTopicStr.equals("")) {//如果现在英文主题为空
                        englishTopicStr = topicName;//现在的英文主题设置为从TopicRelatedPlot找来的主题
                    }//自动生成场景的场景名为：主题名+ma
                    sceneList.add(new SceneCase(topicName + ".ma", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
            }
        } else {//如果主题链表为空
            if (topicFromMG.size() != 0) {
                int topicCount = topicFromMG.size();
                for (int i = 0; i < topicCount; i += 2) {
                    String topicNameFromMG = topicFromMG.get(i);
                    OWLNamedClass topicNameMGCls = getEnglishTopicFromPlot(model, topicNameFromMG);
                    if (topicNameMGCls != null) {
                        String topicNameMG = topicNameMGCls.getBrowserText();

                        String topicProbabilityStr = topicFromMG.get(i + 1).trim();
                        double topicProbability = Float.parseFloat(topicProbabilityStr);
                        if (englishTopicStr.equals("") && topicProbability > 0.6) {
                            englishTopicStr = topicNameMG;
                        }
                        sceneList.add(new SceneCase(topicNameMG + ".ma",
                                topicProbability > 0.6 ? topicProbability : 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    }
                }
            } else {
                // *******************************bjut.plot design
                // 模型部分**14.12.11****************************************//

                if (englishTemplatePlot.size() > 0) {
                    System.out.println("templatePlot" + englishTemplatePlot.size());
                    sceneList.add(new SceneCase("TemplatePlot.ma", 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));

                }
                // *******************************bjut.plot design 模型部分 end
                // ******************************************//

            }
        }

        //最后选用的场景是
        String scenes = "";
        for (SceneCase ele : sceneList) {
            scenes += ele.getSceneName();
        }
        System.out.println("最后选择的场景是(" + sceneList.size() + "个):" + scenes);


        int sceneCount = sceneList.size();
        //遍历所有的场景，与各种特效(风雪雨雾)适配
        logger.info("开始遍历所有的场景，与各种特效(风雪雨雾)适配");
        for (int i = 0; i < sceneCount; i++) {
            System.out.println("第" + (i + 1) + "个场景：" + sceneList.get(i).getSceneName() + "=======================");
            fogInsert fog = new fogInsert();
            Action action = new Action();
            try {
                action.actionInfer(actionTemplateAttr, model, englishTopicStr);
            } catch (Exception ex) {
                logger.info("ERROR: Action judge Exception");
            }
            if (action.isActionFlag()) {
                sceneList.get(i).ActionScore = 1;// 分子
            }

            if (sceneList.get(i).sceneName.contains("Plot.ma")) {//如果是自动合成场景dugenkui重点重要
//				System.out.println("action." + action.isActionFlag());
                System.out.println("场景名包含Plot.ma");
                if (action.isActionFlag()) {
                    sceneList.get(i).ActionScore = 1;// 分子
                }
            } else {
                OWLIndividual temp = model.getOWLIndividual(sceneList.get(i).sceneName);

                OWLObjectProperty time = model.getOWLObjectProperty("time");
                // hasWeatherTeplate
//				System.out.println("sceneName=" + temp.getBrowserText());
                if (temp.hasPropertyValue(time)) {
                    sceneList.get(i).timeable = 1;
//					System.out.println("timeable=" + sceneList.get(i).timeable + temp.hasPropertyValue(time));
                }
                try {
//					logger.info("fog num");
                    fog.fogInfer2(weatherAndmoodAttr, model, sceneList.get(i).sceneName);
                    if (fog.getFog() > 0) {
                        sceneList.get(i).isWeatherable = 1;// 分子
                    }
//					logger.info("jiali finish");
                } catch (Exception exJiali) {
                    logger.info("ERROR: Jiali Exception");
                }
//				logger.info("wind rain snow" + windRainSnowNeedAttr);
                Effect effect = new Effect();
                ArrayList<String> list = new ArrayList();
                for (int m = 0; m < windRainSnowNeedAttr.size(); m++) {
                    for (int j = 0; j < windRainSnowNeedAttr.get(m).size(); j++) {
                        list.add(windRainSnowNeedAttr.get(m).get(j).toString());
                    }

                }
                boolean weatherflag = effect.IsWeather(list, model, sceneList.get(i).sceneName, englishTopicStr);
                if (weatherflag == true) {
                    sceneList.get(i).isWeatherable = 1;
//					System.out.println("Weather:" + sceneList.get(i).isWeatherable);
                }
            }

        }
        logger.info("结束遍历所有的场景，与各种特效(风雪雨雾)适配");
        System.gc();
    }

    public static void InitAllStaticValue() {
        maName = "";
        englishTopicClass = null;
        topiclist.clear();
        topictemplate.clear();
        englishTopicStr = "";
        englishTopicIE = "";
        englishTopicMG = "";
        englishTopicQiu = "";
        englishTemplate.clear();
        TemplateName.clear();
        colorTemplate.clear();
        modelWithColor.clear();
        modelWithColors.clear();
        logger = Logger.getLogger(JenaMethod.class.getName());
        sceneList.clear();
        // sceneListTopic.clear();
        // maNameWord="";
        sCaseDataUsable.clear();
        actionTemplateAttr.clear();
        usedModelAttr.clear();
        colorChangeAttr.clear();
        colorModelNum = 0;// 保存传递给刘畅的变色数组数目
        timeweatherandfog.clear();

        moodTemplateAttr.clear();
        weatherAndmoodAttr.clear();
        windRainSnowNeedAttr.clear();
        ExpressionList.clear();
        WindAttr.clear();
        RainAttr.clear();
        SnowAttr.clear();
        LightList.clear();
        ActionNeedPeople.clear();
        bIsBackgroundScene = false;
        hasWeatherTeplate = false;
        hasTimeTemplate = false;
        ifActionOrExpression = false;
        SeasonList.clear();
        people = false;

    }

    public static OntModel createOWLModelFile2(String url) {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        model.read(url);
        return model;
    }

    /**
     * 保存owl文件
     *
     * @param owlModel owlModel对象
     * @param fileName C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl
     */
    @SuppressWarnings("unchecked")
    public static void saveOWLFile(JenaOWLModel owlModel, String fileName) {
        Collection errors = new ArrayList();
        owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
        System.out.println("File saved with " + errors.size() + " errors.");
    }

    /**
     * 判断名为sname的场景是否已经在放在了备选场景列表sceneList（ArraryList）中
     *
     * @param sname 场景名称
     * @return -1代表否定，i代表场景在备选场景列表的位置
     */
    private static int isOldSceneCase(String sname) {
        if (sceneList.isEmpty())
            return -1;
        int sCount = sceneList.size();
        for (int i = 0; i < sCount; i++) {
            if (sceneList.get(i).sceneName.equals(sname))
                return i;
        }

        return -1;
    }

    /**
     * 获取“中文类名”对应的英文类名
     *
     * @param chiname   中文类名
     * @param classname 英文类名：检索这个类的子孙类，有类的中文名与“中文类名”相同的，则返回
     * @param model     OWLmodel对象
     * @return
     */
    public static String chineseTemplateEnglish(String chiname, String classname, OWLModel model) {
        OWLNamedClass temp = model.getOWLNamedClass(classname);// 获取类名为classname的OWLNamedClass对象
        OWLDatatypeProperty chinesename = model.getOWLDatatypeProperty("chineseName");// 获取chineseName数据属性对象
        Collection clo = temp.getSubclasses(true);// 获取类下的所有子孙类
        String str = null;
        if (clo.size() != 0) {
            for (Iterator in = clo.iterator(); in.hasNext(); ) {// 遍历所有子孙类，查看是否有类的中文名与参数“中文类名”相同
                OWLNamedClass ols = (OWLNamedClass) in.next();
                Object ob = ols.getHasValue(chinesename);
                if (ob != null && ob.toString().equals(chiname)) {
                    str = ols.getBrowserText().toString();
                    break;// 有则break，返回
                }
            }
        }
        return str;
    }

    /**
     * 注意：1.类的属性在类下查看，不同于实体的属性，两者不同
     *
     * @param model
     * @param englishTopicClass
     * @param sceneType
     * @param value
     */
    private static void getSceneFromTopic(OWLModel model, OWLNamedClass englishTopicClass, String sceneType,
                                          double value) {
        OWLObjectProperty hasMa = model.getOWLObjectProperty("hasMa");// 获取hasMa对象属性对象
        RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMa);// 获取此类的hasMa属性值
        if (resource != null) {// 如果吃主题有相应的 hasMa 属性，即此主题有对应的动画场景
            String hasValues = resource.getBrowserText();// 获得主题对应的场景类名
            String[] hasValuesSplit = hasValues.split(" or ");// 可能对应多个 场景类;
            int sceneCount = hasValuesSplit.length;// 主题对应场景个数
            for (int i = 0; i < sceneCount; i++) {
                String sceneNameCls = hasValuesSplit[i];// 场景类型名称
                OWLNamedClass sceneClass = model.getOWLNamedClass(sceneNameCls);// 获取场景对应的类名
                Collection curCls = sceneClass.getInstances(true);// 对应的场景类名及其子孙类的实体
                OWLIndividual indi = null;
                for (Iterator itIns = curCls.iterator(); itIns.hasNext(); ) {// 遍历对应的场景实例
                    indi = (OWLIndividual) itIns.next();// next()
                    String sceneName = indi.getBrowserText();// 场景实例名称
                    int readyScene = isOldSceneCase(sceneName);// 判断为已存在实例
                    if (readyScene < 0) {// 如果未存在
                        if (sceneType.equals("MGTopic")) {// 如果未存在而且是马庚主题场景
                            sceneList.add(new SceneCase(sceneName, value > 0.6 ? value : 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0));
                            System.out.println("抽到马庚主题场景(getSceneFromClass)" + sceneName);
                        }
                        if (sceneType.equals("IETopic")) {
                            sceneList.add(new SceneCase(sceneName, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                            System.out.println("抽到IE主题场景(getSceneFromClass):" + sceneName);
                        }
                        if (sceneType.equals("RuleTopic")) {
                            sceneList.add(new SceneCase(sceneName, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                            System.out.println("抽到RuleTopic/推理主题场景(getSceneFromClass):" + sceneName);
                        }
                    } else {// 如果场景已经出现在了链表中
                        if (sceneType.equals("MGTopic") && sceneList.get(readyScene).MGProb == 0.0) {
                            sceneList.get(readyScene).MGProb += value;
                            System.out.println("抽到马庚主题场景(getSceneFromClass),而且场景已经出现在了场景链表中:" + sceneName);
                        }
                        if (sceneType.equals("IETopic") && sceneList.get(readyScene).IEProb == 0.0) {
                            sceneList.get(readyScene).IEProb += value;
                            System.out.println("抽到IE主题场景(getSceneFromClass),,而且场景已经出现在了场景链表中:" + sceneName);
                        }
                        if (sceneType.equals("RuleTopic") && sceneList.get(readyScene).ruleReason == 0.0) {
                            sceneList.get(readyScene).ruleReason += value;
                            System.out.println("抽到RuleTopic/推理主题场景(getSceneFromClass),而且场景已经出现在了场景链表中:" + sceneName);
                        }
                    }
                }
            }
        }
    }

    /**
     *  fixme 从类中获取场景
     * @param model     OWLModel 模型类：找他下边的实例，并更具场景类型评分
     * @param sceneCls  动画场景类的类名。例如：BackGroundLandScene
     * @param sceneType 场景类型。例如：BackGroundScene
     * @param value     后边那个值什么意思啊？
     */
    private static void getSceneFromClass(OWLModel model, String sceneCls, String sceneType, double value) {

        // 如果场景类型是 BackGroundScene 或者TemplateScene
        if (sceneType.equals("BackGroundScene") | sceneType.equals("TemplateScene")) {

            OWLNamedClass sceneClass = model.getOWLNamedClass(sceneCls);// 获得场景类对应的对象
            Collection curCls = sceneClass.getInstances(true);// 获得此场景类及其子孙类下的实体

            OWLIndividual indi = null;
            for (Iterator itIns = curCls.iterator(); itIns.hasNext(); ) {// 遍历此场景类及其子类下的场景实体
                indi = (OWLIndividual) itIns.next();// next
                // 判断场景是否重复
                int readyScene = isOldSceneCase(indi.getBrowserText());// 场景在备选场景列表sceneList中的位置，不存在则-1

                if (readyScene < 0 && sceneType.equals("BackGroundScene")) {
                    // 如果没在sceneList里边，而且是BackGroundScene场景，则加入场景链表，而且初始化值都为0，除了最后一个标识是否是背景场景的参数
                    sceneList.add(new SceneCase(indi.getBrowserText(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
                    System.out.println("抽到背景场景(getSceneFromClass)：" + indi.getBrowserText());
                }
                if (readyScene < 0 && sceneType.equals("TemplateScene")) {
                    // 如果没在sceneList里边，而且是TemplateScene场景，则加入场景链表，而且初始化值都为0，除了标识“是否是模板相关场景”的参数
                    sceneList.add(new SceneCase(indi.getBrowserText(), 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    System.out.println("抽到模板场景(getSceneFromClass)：" + indi.getBrowserText());
                }
            }
        } else {// 如果不是背景场景，也不是模板场景
            OWLObjectProperty hasMa = model.getOWLObjectProperty("hasMa");// 获取hasMa对象属性对象
            OWLIndividual indi = null;
            OWLNamedClass sceneClass = model.getOWLNamedClass(sceneCls);// 获取
            // 场景类OWLNamedClass
            // 对象
            Collection curCls = sceneClass.getInstances(true); // 获取场景类对象的实体链表
            for (Iterator itIns = curCls.iterator(); itIns.hasNext(); ) {// 遍历此种场景类下的每个实体
                indi = (OWLIndividual) itIns.next();// next()
                if (indi.hasPropertyValue(hasMa)) {// 如果这个实体下边有名为 hasMa 对象属性
                    if (indi.getPropertyValueCount(hasMa) > 0) {// 重复判断？？？？
                        Collection collection = indi.getPropertyValues(hasMa);// 获取每个hasMa属性值
                        for (Iterator iValues = collection.iterator(); iValues.hasNext(); ) {// 遍历hasMa值
                            OWLIndividual animationIndividual = (OWLIndividual) iValues.next(); // next()
                            String sceneName = animationIndividual.getBrowserText();// 场景名称
                            int readyScene = isOldSceneCase(sceneName);// 是否出现在场景链表sceneList中
                            if (readyScene < 0) {// 如果没有出现在场景链表sceneList中
                                if (sceneType.equals("MGTopic")) {// 如果是马庚主题场景
                                    sceneList.add(new SceneCase(sceneName, (value > 0.6 ? value : 0.1), 0, 0, 0, 0, 0,
                                            0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("抽到马庚主题场景(getSceneFromClass):" + sceneName);
                                }
                                if (sceneType.equals("QTopic")) {
                                    sceneList.add(new SceneCase(sceneName, 0, 0, 0, 0, 0, 0, 0, 0, 0, value, 0, 0, 0));
                                    System.out.println("抽到QTopic主题场景(getSceneFromClass):" + sceneName);
                                }
                                if (sceneType.equals("IETopic")) {
                                    sceneList.add(new SceneCase(sceneName, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("抽到IE主题场景(getSceneFromClass):" + sceneName);// 即IE分词部分分析的主题
                                }
                                if (sceneType.equals("RuleTopic"))// 如果是推理主题场景
                                // dugenkui
                                {
                                    sceneList.add(new SceneCase(sceneName, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("抽到RuleTopic/推理主题场景(getSceneFromClass):" + sceneName);// 根据规则推导？！
                                }
                                if (sceneType.equals("TemplateScene")) {
                                    sceneList.add(new SceneCase(sceneName, 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("抽到模板关联的场景(getSceneFromClass):" + sceneName);// 模板下边相关属性关联的场景
                                }
                            } else { // 如果出现在了场景链表sceneList中
                                // 如果是马庚主题，相应权值+1
                                if (sceneType.equals("MGTopic") && sceneList.get(readyScene).MGProb == 0.0) {
                                    sceneList.get(readyScene).MGProb += value;
                                    System.out.println("抽到马庚主题场景(getSceneFromClass),而且场景已经出现在了场景链表中:" + sceneName);
                                }
                                // 如果是IE主题。。。
                                if (sceneType.equals("IETopic") && sceneList.get(readyScene).IEProb == 0.0) {
                                    sceneList.get(readyScene).IEProb += value;
                                    System.out.println("抽到IE主题场景(getSceneFromClass),而且场景已经出现在了场景链表中:" + sceneName);
                                }
                                // 如果是规则推导主题。。。
                                if (sceneType.equals("RuleTopic") && sceneList.get(readyScene).ruleReason == 0.0) {
                                    sceneList.get(readyScene).ruleReason += value;
                                    System.out.println(
                                            "抽到RuleTopic/推理主题场景(getSceneFromClass),而且场景已经出现在了场景链表中:" + sceneName);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * 判断模板动作与人物是否匹配，如果匹配则将通过动作模板和人物模板的动画加入，否则从模板中去掉这两个模板值
     *
     * @param englishTemplate 所有的英语模板值,由于方法中匹配的英文名字，所以在人物与动物做模型的时候，父类必须带有Character与Animal字样
     * @return boolean 2014.12.8
     */

    public static ArrayList delActionAndPeeople(ArrayList englishTemplate) {
        ArrayList englishTemplateExActionP = new ArrayList();
        for (int i = 0; i < englishTemplate.size(); i++) {
            String tempTem = (String) englishTemplate.get(i);
            if (!tempTem.contains("Action") && !tempTem.contains("Character") && !tempTem.contains("Animal")) {
                englishTemplateExActionP.add(englishTemplate.get(i));
            }
        }
        return englishTemplateExActionP;
    }

    /**
     * OWL中获取一个类的实例
     *
     * @param cls owl中类
     * @return ArrayList IndividualList
     * @author AI
     */

    public static ArrayList getIndividualFromCls(OWLNamedClass cls) {
        ArrayList IndividualList = new ArrayList();

        Collection individual = cls.getInstances();
        for (Iterator it = individual.iterator(); it.hasNext(); ) {
            OWLIndividual tempIndividual = (OWLIndividual) it.next();

            IndividualList.add(tempIndividual.getBrowserText());
        }
        System.out.println("实例：" + IndividualList);
        return IndividualList;
    }

    /**
     * 由模板获取动画场景
     */
    static void getAnim(ArrayList englishTemplate, OWLModel model) {

        double value = 0.0;
        int tempCount = englishTemplate.size();
        // System.out.println("?????TemplateRelated--Scene: ");

        for (int i = 0; i < tempCount; i++) {// System.out.println("?????TemplateRelated--Scene:
            // ");
            OWLObjectProperty hasMa = model.getOWLObjectProperty("hasAnimationNameFromTemplate");// 对象属性对象，模板推导动画属性类
            String engTemp = "";
            if (((String) englishTemplate.get(i)).contains(":")) {
                int size = ((String) englishTemplate.get(i)).split(":").length;
                engTemp = ((String) englishTemplate.get(i)).split(":")[size - 2];// 取倒数第二个为模板值
                value = Double.parseDouble(((String) englishTemplate.get(i)).split(":")[size - 1]);// 取最后一个
                // 为模板相关值
                // System.out.println("engTemp and value:" + value + engTemp);
                // 最后的值应为模板相关value

            } else {
                engTemp = (String) englishTemplate.get(i);
                value = 0.5;
            }
            OWLIndividual indi = model.getOWLIndividual(engTemp);// 获取名字为engTemp的模板实体

            if (indi != null && indi.hasPropertyValue(hasMa)) {// 遍历模板实体
                // System.out.println(".....TemplateRelated--Individual: " +
                // indi.getBrowserText());
                if (indi.getPropertyValueCount(hasMa) > 0) {
                    Collection collection = indi.getPropertyValues(hasMa);
                    for (Iterator iValues = collection.iterator(); iValues.hasNext(); ) {// next()
                        OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
                        String sceneName = animationIndividual.getBrowserText();
                        System.out.println("与模板相关的场景:" + sceneName);

                        int readyScene = isOldSceneCase(sceneName);

                        if (readyScene < 0) {// value值为1or0.5，1表示为直接翻译过来的模型。0.5为随机选择
                            sceneList.add(new SceneCase(sceneName, 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                        } else {
                            sceneList.get(readyScene).templateRelated += value;
                        }
                    }
                }
            }

        }

    }

    /**
     * @param model           OWLModel模型类对象
     * @param englishTemplate 英文模板
     */
    public static void addModelToTemplate(OWLModel model, ArrayList<String> englishTemplate) {

        for (int i = 0; i < englishTemplate.size(); i++) {
            ArrayList clo = new ArrayList();
            String[] temp = englishTemplate.get(i).split(":");
            OWLObjectProperty obj = model.getOWLObjectProperty("hasModelFromTemplate");
            OWLDatatypeProperty ifchangemodel = model.getOWLDatatypeProperty("ifChangeModel");
            OWLIndividual ind = null;

            if (temp.length > 1) {
                ind = model.getOWLIndividual(temp[temp.length - 1]);
                if (ind.getPropertyValueCount(obj) == 0) {
                    OWLNamedClass namedclass = model.getOWLNamedClass(temp[0]);
                    ArrayList<String> modelclass = new ArrayList<String>();
                    RDFResource resource = namedclass.getSomeValuesFrom(obj);
                    if (resource != null) {
                        String hasValues = resource.getBrowserText();
                        if (hasValues.indexOf("or") >= 0 && hasValues.contains(" ")) {
                            String[] hasValuesSplit = hasValues.split("or");
                            if (hasValuesSplit.length > 0) {
                                for (int ii = 0; ii < hasValuesSplit.length; ii++) {
                                    modelclass.add(hasValuesSplit[i].toString().trim());
                                }
                            }
                        } else {
                            modelclass.add(resource.getBrowserText());
                        }
                    }
                    if (modelclass.size() != 0) {
                        for (int jj = 0; jj < modelclass.size(); jj++) {
                            OWLNamedClass nam = model.getOWLNamedClass(modelclass.get(jj));
                            if (nam.getInstanceCount(true) != 0) {
                                int count = nam.getInstanceCount(true);
                                Random r = new Random();
                                int k = 0;
                                if (count >= 5)
                                    k = r.nextInt(3) + 1;
                                else
                                    k = r.nextInt(count) + 1;
//								System.out.println("关联模型个数=" + k);
                                Collection incollect = nam.getInstances(true);

                                ArrayList<OWLIndividual> array = new ArrayList();
                                for (Iterator ite = incollect.iterator(); ite.hasNext(); ) {
                                    array.add((OWLIndividual) ite.next());
                                }

                                int n = 0;
                                while (n < k) {
                                    Random r1 = new Random();
                                    int length = array.size();
                                    int k1 = r1.nextInt(length);
                                    OWLIndividual indivi = (OWLIndividual) array.get(k1);
                                    array.remove(k1);
                                    clo.add(indivi);
                                    n++;
//									System.out.print(indivi.getBrowserText().toString() + "\t");
//									System.out.println();
                                }

                            }
                        }

                    }
                } else if (ind.getPropertyValueCount(obj) != 0) {
                    // if(ind.getPropertyValueCount(ifchangemodel)==0){

                    Collection linkt1 = ind.getPropertyValues(obj);
                    for (Iterator ites = linkt1.iterator(); ites.hasNext(); ) {
                        OWLIndividual linkt = (OWLIndividual) ites.next();
                        Collection col = linkt.getDirectTypes();
                        for (Iterator iter = col.iterator(); iter.hasNext(); ) {
                            OWLNamedClass linkc = (OWLNamedClass) iter.next();
                            if (linkc.getInstanceCount() > 1) {
                                int count = linkc.getInstanceCount();
                                Random r = new Random();
                                int k = 0;
                                if (count >= 3)
                                    k = r.nextInt(2) + 1;
                                else
                                    k = r.nextInt(count) + 1;
//								System.out.println("关联模型个数=" + k);
                                Collection incollect = linkc.getInstances();

                                ArrayList array = new ArrayList();
                                for (Iterator ite = incollect.iterator(); ite.hasNext(); ) {
                                    array.add(ite.next());
                                }
                                int n = 0;
                                // OWLIndividual arg1=(OWLIndividual)
                                // ind.getPropertyValue(obj);
                                ind.removePropertyValue(obj, linkt);

                                while (n < k) {
                                    Random r1 = new Random();
                                    int length = array.size();
                                    int k1 = r1.nextInt(length);
                                    OWLIndividual indivi = (OWLIndividual) array.get(k1);
                                    array.remove(k1);
                                    clo.add(indivi);
                                    n++;
//									System.out.print(indivi.getBrowserText().toString() + "\t");
//									System.out.println();
                                }

                            }

                        }
                    }
                    // }

                }
                if (clo.size() != 0) {

                    ind.setPropertyValues(obj, clo);
                }
            }
//			System.out.println(ind.getPropertyValueCount(obj));

        }
    }

    /**
     * 被TemplateCollect()调用：
     *
     * @param maName            场景名称
     * @param sceneNO           场景序列，标识在场景链表中的位置
     * @param model             OWLModel对象
     * @param englishTemplate   英文模板链表（模板类：类下边实体=DoubtWordTemplate:doubtWordTemplate)
     * @param templateWithColor 颜色模板
     * @throws SecurityException、SWRLFactoryException
     * @throws SWRLRuleEngineException
     * @throws IOException
     */
    public static void CoutModelPlacable(String maName, int sceneNO, OWLModel model, ArrayList<String> englishTemplate,
                                         ArrayList<String> templateWithColor)
            throws SWRLFactoryException, SecurityException, SWRLRuleEngineException, IOException {

        System.out.println("第" + (sceneNO + 1) + "个场景(" + sceneList.get(sceneNO).sceneName + ")----------------------------------------------");

        OWLObjectProperty obj = model.getOWLObjectProperty("hasModelFromTemplate");

        OWLModel owlModel = processSWRL(maName, "", model, englishTemplate);
        OWLIndividual maIndividual = model.getOWLIndividual(maName);
        OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
        OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
        OWLObjectProperty usedModelInMaProperty = model.getOWLObjectProperty("usedModelInMa");

        ArrayList<OWLIndividual> individualList = new ArrayList();
        //getIndividualFromEnglishTemplate应该明明为getModelsFromEnglishTemplate
        ArrayList<String> individualListFromTemplate = getIndividualFromEnglishTemplate(model, englishTemplate);
        ArrayList<String> colorIndividualList = getIndividualFromEnglishTemplate(model, templateWithColor);
        sceneList.get(sceneNO).templateModelNum = individualListFromTemplate.size();
        System.out.println("和模版相关的模型有" + individualListFromTemplate.size() + "个，分别是:" + individualListFromTemplate.toString());
        sceneList.get(sceneNO).colorModelNum = colorIndividualList.size();//颜色模板对应的模型

//		System.out.println("1111111individualListFromTemplate:" + individualListFromTemplate);
//		System.out.println("colorIndividualList:" + colorIndividualList);
        // @SuppressWarnings("unused")
        int count = 0;
        if (maName.contains("Plot.ma")) {
            sceneList.get(sceneNO).templateModelNum = individualListFromTemplate.size();
            sceneList.get(sceneNO).placableModelNum = individualListFromTemplate.size();
            sceneList.get(sceneNO).placableColorModelNum = colorIndividualList.size();
            System.out.println("可放入颜色模型数量" + sceneList.get(sceneNO).placableColorModelNum);
        } else {
            String name = maIndividual.getBrowserText();
//			System.out.println("maName:" + name);
            // 从usedSpaceInMa入手，主要更改addTomMa属性m.
            System.out.println("场景的可用空间的个数" + maIndividual.getPropertyValueCount(usedSpaceInMaProperty) + ",分别是:");
            if (maIndividual.getPropertyValueCount(usedSpaceInMaProperty) > 0) {//如果场景中有可用空间，场景都有啊！！
                ArrayList<OWLIndividual> innerIndividualList = new ArrayList();//不要被明明干扰，娘希匹
                ArrayList<OWLIndividual> colorIndi = new ArrayList();
                //场景实体‘usedSpaceInMaProperty’属性（可用空间）的值：可能有多个可用空间，因此有多个值
                Collection usedSpaceValues = maIndividual.getPropertyValues(usedSpaceInMaProperty);
                //System.out.println("场景可用空间" + usedSpaceValues.iterator());
                for (Iterator iValues = usedSpaceValues.iterator(); iValues.hasNext(); ) {//遍历此场景中的可用空间
                    OWLIndividual spaceIndividual = (OWLIndividual) iValues.next();//next()
                    //此可用空间可放入的模型链表，用hasPutObjectInSpaceProperty关联
                    Collection objectInSpaceValues = spaceIndividual.getPropertyValues(hasPutObjectInSpaceProperty);
                    // 通过Map的<key，value>对值来处理添加某个类下面的多个实例的问题
                    Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();
                    //遍历此场景的此可用空间可以放入的模型，用hasPutObjectInSpaceProperty关联的模型
                    System.out.print("\t场景可用空间中可放的模型(" + spaceIndividual.getBrowserText() + "):");
                    for (Iterator iiValues = objectInSpaceValues.iterator(); iiValues.hasNext(); ) {
                        OWLIndividual objectIndividual = null;
                        if (name.equals("empty.ma"))//如果是空场景，去除模型实体
                            objectIndividual = model.getOWLIndividual(iiValues.next().toString());
                        else//如果不是。。。不特么一样吗
                            objectIndividual = (OWLIndividual) iiValues.next();//场景——》可用空间——》模型：场景——》模型
                        System.out.print(objectIndividual.getBrowserText() + ",");

                        Iterator itd = individualListFromTemplate.iterator();
                        Iterator itd1 = colorIndividualList.iterator();
                        boolean isEqualTemplate = false;
                        while (itd.hasNext()) {//遍历模板相关的模型
                            String individualStr = (String) itd.next();
//							System.out.println("和模板相关的模型名字" + individualStr);
//							System.out.println("场景可用空间中可放的模型" + objectIndividual.getBrowserText());
                            if (individualStr.equals(objectIndividual.getBrowserText())) {//如果模板相关模型与此可用空间可以放入的模型相同
                                if (innerIndividualList.size() == 0) {
                                    innerIndividualList.add(objectIndividual);
                                } else {// 将重复的去掉，模型已经可以放入，就不在重复计算
                                    int nflag = 0;
                                    for (int ni = 0; ni < innerIndividualList.size(); ni++) {
                                        System.out.println(
                                                "innerIndividualList" + innerIndividualList.get(ni).getBrowserText());
                                        System.out.println("objectIndividual" + objectIndividual.getBrowserText());
                                        if (innerIndividualList.get(ni).getBrowserText()
                                                .equals(objectIndividual.getBrowserText())) {
                                            nflag = 1;
                                            break;
                                        }
                                    }
                                    if (nflag == 0) {
                                        innerIndividualList.add(objectIndividual);
                                    }
                                }
                            }//end of 如果模板相关模型与此可用空间可以放入的模型相同
                        }//end of 遍历模板相关的模型
                        while (itd1.hasNext()) {
                            String individualStr = (String) itd1.next();
                            if (individualStr.equals(objectIndividual.getBrowserText())
                                    && !colorIndi.contains(objectIndividual)) {

                                colorIndi.add(objectIndividual);
                            }

                        }
                    }
                }
                System.out.println();

                sceneList.get(sceneNO).placableModelNum = innerIndividualList.size();
                logger.info("经过计算(模板相关模型+可用空间关联模型),可放入此场景的模型有" + innerIndividualList.size() + "个,分别是:");
                for (OWLIndividual ele : innerIndividualList) {
                    System.out.print(ele.getBrowserText() + ",");
                }
                System.out.println();

                sceneList.get(sceneNO).placableColorModelNum = colorIndi.size();
                System.out.println("可放入颜色模型数量:" + sceneList.get(sceneNO).placableColorModelNum);
                // placableModelNum = innerIndividualList.size();
            }
        }

    }

    /**
     * @param model             OWLModel类对象
     * @param englishTemplate   英文模板
     * @param templateWithColor 颜色模板
     * @param colorMark         颜色标识
     * @throws SWRLFactoryException
     * @throws SecurityException
     * @throws SWRLRuleEngineException
     * @throws IOException
     */
    public static void TemplateCollect(OWLModel model, ArrayList<String> englishTemplate,
                                       ArrayList<String> templateWithColor, ArrayList<String> colorMark)
            throws SWRLFactoryException, SecurityException, SWRLRuleEngineException, IOException {

        int sceneCount = sceneList.size();// 收集场景个数
        // 模板模型数量、可放入模型数量、可放入颜色数量
        int templateModelNum = 0, placableModelNum = 0, placableColorModelNum = 0;
        boolean time = false;
        timeweatherandfog.add(0, "");// 传递给刘畅用，/(ㄒoㄒ)/~~，你写这儿干嘛
        timeweatherandfog.add(1, "");
        timeweatherandfog.add(2, "");
        timeweatherandfog.add(3, "");

        // 颜色模板： W修改5.4
        ArrayList<String> colorTemplateW = new ArrayList();
        colorTemplateW = chineseTemplate2English(templateWithColor, model);
        // System.out.println("colorTemplateW:" + colorTemplateW);

        for (int i = 0; i < colorTemplateW.size(); i++) {// 遍历颜色模板
            int iP = colorTemplateW.get(i).lastIndexOf(":");
            colorTemplate.add(colorTemplateW.get(i).substring(0, iP));// 添加颜色模板
        }

        addModelToTemplate(model, englishTemplate);

        //遍历所有的场景
        logger.info("开始遍历所有场景，");
        for (int i = 0; i < sceneCount; i++) {
            CoutModelPlacable(sceneList.get(i).sceneName, i, model, englishTemplate, colorTemplate);
        }
        logger.info("结束遍历所有场景，");
        if (englishTemplate.size() != 0) {//如果不带分值的英文模板不为空
            // 遍历英文模板，参看是否有关于时间的模板
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();//next()
                if (tempWord.contains("Time") || tempWord.contains("DayTemplate")) {//如果模板包含时间
                    time = true;
                }
                if (tempWord.contains("EveningNightTemplate"))
                    timeweatherandfog.set(0, (String) "Evening");// 傍晚
                else if (tempWord.contains("LateNightTemplate"))
                    timeweatherandfog.set(0, (String) "Night");// 深夜
                else if (tempWord.contains("MorningTemplate"))
                    timeweatherandfog.set(0, (String) "EarlyMorning");// 清晨
                else if (tempWord.contains("NightTemplate"))
                    timeweatherandfog.set(0, (String) "Night");// 晚上，深夜或者傍晚都可，只要是晚上就行
                else if (tempWord.contains("daybreakTemplate"))
                    timeweatherandfog.set(0, (String) "EarlyMorning");
                else if (tempWord.contains("forenoonTemplate"))
                    timeweatherandfog.set(0, (String) "Morning");
                else if (tempWord.contains("noonTemplate"))
                    timeweatherandfog.set(0, (String) "Noon");
                else if (tempWord.contains("afternoonTemplate"))
                    timeweatherandfog.set(0, (String) "Afternoon");
            }
            //在特么遍历英文模板，查看是否有关于时间和云的模板
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();
                if (tempWord.contains("sunshineTemplate"))
                    timeweatherandfog.set(1, (String) "Sunshine");// 晴
                else if (tempWord.contains("cloudyTemplate"))
                    timeweatherandfog.set(1, (String) "Overcast");// 多云
            }//同上
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();
                if (tempWord.contains("sunshineTemplate"))
                    timeweatherandfog.set(2, (String) "NoCloud");// 晴
                else if (tempWord.contains("cloudyTemplate"))
                    timeweatherandfog.set(2, (String) "Cloudy");// 多云
            }//雾气
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();
                if (tempWord.contains("strongFogTemplate"))
                    timeweatherandfog.set(3, (String) "Heavyfog");// 大雾
                else if (tempWord.contains("fogTemplate"))
                    timeweatherandfog.set(3, (String) "Lightfog");// 淡雾
            }
            for (int i = 0; i < timeweatherandfog.size(); i++) {
                String temp = timeweatherandfog.get(i);
                timeweatherandfog1.add(i, temp);// 用于字幕的使用
            }
            if (time | !(timeweatherandfog.get(0).isEmpty()))// 0 时间,Time时间模板
                hasTimeTemplate = true;// 分母
        }// end of 如果不带分值的英文模板不为空
        if (!(timeweatherandfog.get(1).isEmpty()) || // 1、2、3天气不为空
                (!timeweatherandfog.get(2).isEmpty()) || (!timeweatherandfog.get(3).isEmpty())) {
            hasWeatherTeplate = true;
        }

//		System.out.println("！！！！！！！！！！！！！！！！englishTemplate:" + englishTemplate);
//		System.out.println("！！！！！！！！！！！！！！！！timeweatherandfog:" + timeweatherandfog.size() + timeweatherandfog
//				+ hasTimeTemplate + hasWeatherTeplate);
        // colorTemplate = chineseTemplate2English(templateWithColor, model);
        // System.out.println("！！！！！！！！！！！！！！！！colorTemplate:"+colorTemplate);
        modelWithColor = colorTemplate2Individual(colorTemplate, colorMark, model);
//		System.out.println("！！！！！！！！！！！！！！！！modelWithColor:" + modelWithColor);
    }

    //MG主题、Qiu主题，主题链表，颜色模板：：错误场景重点
    public static void CountSceneCaseBitMap(ArrayList<String> topicFromMG, ArrayList<String> topicFromQiu,
                                            ArrayList<String> topic, ArrayList<String> templateWithColor) {
        logger.info("开始遍历所有场景，按照各个要素（主题、模板相关、可放入模型数量）给各个场景评分");
        sCaseDataUsable.clear();
        int sceneCount = sceneList.size();//场景个数
        SceneCase tempSceneCase;//打分实例
        for (int i = 0; i < sceneCount; i++) {//遍历场景
            tempSceneCase = sceneList.get(i);//遍历场景
            if (topicFromMG.size() > 0)//如果马庚主题不为空
                sCaseDataUsable.set(1);//打分那儿设为1
            if (topic.size() > 0)//如果主题不为空
                sCaseDataUsable.set(2);
            if (tempSceneCase.ruleReason > 0)//RuleTopic
                sCaseDataUsable.set(3);
            if (tempSceneCase.templateRelated > 0)//TemplateRelated
                sCaseDataUsable.set(4);
            if (tempSceneCase.templateModelNum > 0)
                sCaseDataUsable.set(5);
            if (templateWithColor.size() > 0)
                sCaseDataUsable.set(6);
            if (hasWeatherTeplate)
                sCaseDataUsable.set(7);
            if (topicFromQiu.size() > 0)
                sCaseDataUsable.set(8);
            if (hasTimeTemplate) {
                sCaseDataUsable.set(9);
            }
            if (tempSceneCase.isBackgroundScene > 0)//如果是背景场景
                sCaseDataUsable.set(10);
        }
        logger.info("结束遍历所有场景，按照各个要素（主题、模板相关、可放入模型数量）给各个场景评分");
    }

    /*
     * static public void SceneListSort() {
     *
     * for (int i = 0; i < sceneList.size(); i++) {
     *
     * // 保证前i+1个数排好序
     *
     * SceneCase temp = sceneList.get(i); int j; for (j = i; j > 0 &&
     * sceneList.get(j-1).score < temp.score; j--) { sceneList.set(j,
     * sceneList.get(j-1)); } sceneList.set(j, temp); } }
     */
    static public ArrayList<SceneCase> SceneListSort(ArrayList<SceneCase> s) {

        for (int i = 0; i < s.size(); i++) {

            // 保证前i+1个数排好序

            SceneCase temp = s.get(i);
            int j;
            for (j = i; j > 0 && s.get(j - 1).score < temp.score; j--) {
                s.set(j, s.get(j - 1));
            }
            s.set(j, temp);
        }
        return s;
    }

    static double randomtemp = 0;// 用于xml输出

    // 蒋孟馨
    public static String SceneSelected() throws SQLException {

//		if(1==1) return "childrenRoom.ma";

        int sceneCount = sceneList.size();
        ArrayList topicscene = new ArrayList();
        ArrayList templatescene = new ArrayList();
        ArrayList backscene = new ArrayList();
        ArrayList scene = new ArrayList();
        int weather = hasWeatherTeplate ? 1 : 0;
        logger.info("开始选择场景===============================");
        for (int i = 0; i < windRainSnowNeedAttr.size(); i++) {

            if (windRainSnowNeedAttr.get(i).isEmpty() == false) {
                System.out.println("windRainSnowNeedAttr.isEmpty()" + windRainSnowNeedAttr.size() + windRainSnowNeedAttr
                        + windRainSnowNeedAttr.isEmpty());
                weather = 1;
            }
        }
        int time = hasTimeTemplate ? 1 : 0;
        double modelNum, cModelNum, templateRelated;
        if (ExpressionList.size() != 0) {
            ifActionOrExpression = true;
        }
//		System.out.println("是否抽到相应的动作或表情：" + ifActionOrExpression);
        int k = 0;
        while (k < sceneCount) {
            if (sceneList.get(k).isBackgroundScene == 1.0 && ifActionOrExpression == false && people == true) {// 动作不是随机添加的
                sceneList.remove(k);
                sceneCount = sceneList.size();
            } else {
                k++;
            }
        }
        sceneCount = sceneList.size();

        //TODO 此处应该判定每个场景是否分值为0值，如果是的话就移除：1.具体内容参见Scene的属性；2.不要在页面打印，但是打印在日志中
        int index = 0;
        try {
            while (sceneList.get(index) != null) {
                SceneCase temp = sceneList.get(index);
                double s = temp.MGProb * 15 + temp.IEProb * 20
                        + temp.ruleReason * 15 + temp.templateRelated * 10 + (temp.placableModelNum) * 10
                        + (temp.placableColorModelNum) * 10 + temp.isWeatherable * weather * 5 + temp.timeable * time * 5
                        + temp.QProb * 10 + temp.ActionScore * 5;
                if (s == 0) {
                    sceneList.remove(temp);
                } else
                    index++;
            }
        } catch (Exception e) {
        }
        sceneCount = sceneList.size();


//		System.out.println("最后获得的场景数量为" + sceneCount); // 最终的候选场景
        // 如果纯空白场景不能添加任何东西，则不加入计算
        double actionScore = 5;

        // TODO 注释蒋孟馨师姐程序，每次更新程序都要撤销注释
        sceneselectbyDecisionTree Tree = new sceneselectbyDecisionTree();

        double leafrate[] = new double[sceneCount];
//		leafrate = Tree.tmain(englishTopicStr, englishTemplate, sceneList, sceneCount, leafrate);

        for (int i = 0; i < sceneCount; i++) {
            sceneList.get(i).decisionvalue = leafrate[i];
        }

        for (int i = 0; i < sceneCount; i++) {
            SceneCase temp = sceneList.get(i);
            modelNum = temp.templateModelNum > 1 ? temp.templateModelNum : 1;
            cModelNum = temp.colorModelNum > 1 ? temp.colorModelNum : 1;
            templateRelated = temp.templateRelated > 1 ? temp.templateRelated : 1;
            templateRelated = Math.round(templateRelated);

            if (sceneCount <= 4)
                temp.decisionvalue = temp.decisionvalue * 80;
            if (sceneCount > 4 && sceneCount <= 9)
                temp.decisionvalue = temp.decisionvalue * 100;
            if (sceneCount > 9)
                temp.decisionvalue = temp.decisionvalue * 150;

            sceneList.get(i).indualScore = temp.decisionvalue + temp.MGProb * 15 + temp.IEProb * 20
                    + temp.ruleReason * 15 + temp.templateRelated * 10 + (temp.placableModelNum) * 10
                    + (temp.placableColorModelNum) * 10 + temp.isWeatherable * weather * 5 + temp.timeable * time * 5
                    + temp.QProb * 10 + temp.ActionScore * 5;

            sceneList.get(i).fullScore = 15 + 15 + 15 + templateRelated * 10 + modelNum * 10 + cModelNum * 10 + 5 + 5
                    + actionScore + 10;

            sceneList.get(i).score = // 每个场景的概率值
                    sceneList.get(i).indualScore / sceneList.get(i).fullScore;

//			System.out.print("sceneScore：" + sceneList.get(i).indualScore);
//			System.out.print("\t" + sceneList.get(i).fullScore);
//			System.out.println("\t" + sceneList.get(i).score);

            if ((temp.MGProb != 0 || temp.IEProb != 0 || temp.ruleReason != 0 || temp.QProb != 0) && temp.score != 0)
                topicscene.add(temp);
            else if (temp.templateRelated != 0 && temp.score != 0)
                templatescene.add(temp);
            else if (temp.isBackgroundScene != 0 && temp.score != 0)
                backscene.add(temp);
        }
        SceneListSort(sceneList);
        SceneListSort(topicscene);
        SceneListSort(templatescene);
        SceneListSort(backscene);

        if (!(topicscene.isEmpty()) && !(templatescene.isEmpty()) && !(backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
//			System.out.println(randomscore);
            if (randomscore < 20 && randomscore >= 0) {
                scene = backscene;
//				System.out.println("当前选择的场景是背景场景");
            }
            if (randomscore < 50 && randomscore >= 20) {
//				System.out.println("当前选择的场景和模板相关");
                scene = templatescene;
            }
            if (randomscore < 100 && randomscore >= 50) {
//				System.out.println("当前选择的场景和主题相关");
                scene = topicscene;
            }

        } else if (!(topicscene.isEmpty()) && !(templatescene.isEmpty()) && (backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
            System.out.println(randomscore);
            if (randomscore < 40 && randomscore >= 0) {
//				System.out.println("当前选择的场景和模板相关");
                scene = templatescene;
            }
            if (randomscore < 100 && randomscore >= 40) {
//				System.out.println("当前选择的场景和主题相关");
                scene = topicscene;
            }

        } else if (!(topicscene.isEmpty()) && (templatescene.isEmpty()) && !(backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
//			System.out.println(randomscore);

            if (randomscore < 30 && randomscore >= 0) {
                scene = backscene;
//				System.out.println("当前选择的场景是背景场景");
            }

            if (randomscore < 100 && randomscore >= 30) {
//				System.out.println("当前选择的场景和主题相关");
                scene = topicscene;
            }

        } else if ((topicscene.isEmpty()) && !(templatescene.isEmpty()) && !(backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
            System.out.println(randomscore);
            if (randomscore < 40 && randomscore >= 0) {
                scene = backscene;
//				System.out.println("当前选择的场景是背景场景");
            }
            if (randomscore < 100 && randomscore >= 40) {
                scene = backscene;
//				System.out.println("当前选择的场景和模板相关");
            }

        } else if ((topicscene.isEmpty()) && (templatescene.isEmpty()) && !(backscene.isEmpty())) {
            scene = backscene;
//			System.out.println("当前选择的场景是背景场景");
        } else if ((topicscene.isEmpty()) && !(templatescene.isEmpty()) && (backscene.isEmpty())) {
            scene = templatescene;
//			System.out.println("当前选择的场景和模板相关");
        } else if (!(topicscene.isEmpty()) && (templatescene.isEmpty()) && (backscene.isEmpty())) {
            scene = topicscene;
//			System.out.println("当前选择的场景和主题相关");
        }

        /**
         * 对所有场景所得分数进行加和，做随机数选择
         *
         * @author WJJ 得到sceneListScore值为【ma1,1,20,ma2,21,40】
         */

        /*
         * ArrayList<String> sceneListScore=new ArrayList();
         *
         * SceneCase temp2 = null; double tempScore=0; double allfullScore=0;
         * for(int i=0;i<sceneList.size();i++){ if(sceneList.get(i).score!=0){
         * temp2=sceneList.get(i); tempScore=allfullScore;
         * allfullScore=allfullScore+temp2.score;//将每个场景的值加和
         * sceneListScore.add(temp2.sceneName);
         * sceneListScore.add(Double.toString((tempScore)));
         * sceneListScore.add(Double.toString(allfullScore));
         * System.out.println("allScore="+allfullScore); } }
         * System.out.println("allScore="+allfullScore);
         */
//		System.out.println("开始选择场景,场景的长度为：" + scene.size());
        ArrayList<String> sceneListScore1 = new ArrayList();
        SceneCase temp3 = null;
        double tempScore1 = 0;
        double allfullScore1 = 0;

        for (int ii = 0; ii < scene.size(); ii++) {
            temp3 = (SceneCase) scene.get(ii);
//			System.out.println(temp3.score);
            if (temp3.score != 0) {
                tempScore1 = allfullScore1;
                allfullScore1 = allfullScore1 + temp3.score;
                sceneListScore1.add(temp3.sceneName);
                sceneListScore1.add(Double.toString((tempScore1)));
                sceneListScore1.add(Double.toString(allfullScore1));
//				System.out.println("allScore1=" + allfullScore1);
            }
        }

        /**
         * 进行场景的选择
         */
        if (allfullScore1 > 0) {
            randomtemp = Math.random() * allfullScore1;
//			System.out.println("randomtemp 场景值=" + randomtemp);
            for (int j = 0; j < sceneListScore1.size(); ) {
                double score1 = Double.parseDouble(sceneListScore1.get(j + 1));
                double score2 = Double.parseDouble(sceneListScore1.get(j + 2));
                if (score1 <= randomtemp && randomtemp < score2) {
                    logger.info("选定场景" + (String) sceneListScore1.get(j) + "===============================");
                    return (String) sceneListScore1.get(j);// 返回场景名称
                }
                if (j == sceneListScore1.size() && score2 == randomtemp) {
                    logger.info("选定场景" + (String) sceneListScore1.get(j) + "===============================");
                    return (String) sceneListScore1.get(j);
                }
                j = j + 3;
            }
        }
        /*
         * else{ int m=(int) (Math.random()*(sceneList.size())); return
         * sceneList.get(m).sceneName; }
         */
        logger.info("还未选择场景===============================");
        return "";
    }

    /**
     * 将中文主题链表转换成英文主题链表，
     * 放进类开始声明的 topiclist 中，全局变量
     *
     * @param topic
     * @param model
     */
    public static void getEnglishTopic(ArrayList<String> topic, OWLModel model) {
        ArrayList<String> englishTopic = new ArrayList();
        OWLNamedClass topicn = model.getOWLNamedClass("Topic");
        OWLDatatypeProperty chineseTopic = model.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection clo = topicn.getSubclasses(true);
        for (Iterator in = clo.iterator(); in.hasNext(); ) {
            cls = (OWLNamedClass) in.next();
            Object hasvali = cls.getHasValue(chineseTopic);
            for (int i = 0; i < topic.size(); i++) {
                if (hasvali != null && hasvali.toString().equals(topic.get(i))) {
                    if (topiclist.size() != 0) {
                        boolean topicflage = false;
                        for (int k = 0; k < topiclist.size(); k++) {
                            if (topiclist.get(k).equals(cls.getBrowserText().toString())) {
                                topicflage = true;
                                break;
                            }
                        }
                        if (topicflage == false) {
                            topiclist.add(cls.getBrowserText().toString());
                        }
                    } else {
                        topiclist.add(cls.getBrowserText().toString());
                    }
                }
            }
        }
//		System.out.println("topicllist=" + topiclist);
    }


    /**
     * 蒋孟馨0903
     * <p>
     * 情节规划的主程序的处理：判断现在选用的场景，根据三种情况处理
     * 1.还未选定，使用empty.ma；
     * 2.如果是自动生成的场景；
     * 3.其他
     *
     * @param model               对象模型
     * @param topic               主题链表
     * @param templateAttr        中文模板
     * @param templateWithColor   颜色模板
     * @param colorMark           颜色标识
     * @param topicFromMG         马庚主题+评分
     * @param strNegType
     * @param englishTemplatePlot
     * @throws OntologyLoadException
     * @throws SWRLFactoryException
     * @throws SWRLRuleEngineException
     * @throws SecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void PlotResGenerate(OWLModel model, ArrayList<String> topic, ArrayList<String> templateAttr,
                                       ArrayList<String> templateWithColor, ArrayList<String> colorMark, ArrayList<String> topicFromMG,
                                       String strNegType, ArrayList<String> englishTemplatePlot)
            throws OntologyLoadException, SWRLFactoryException, SWRLRuleEngineException, SecurityException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("\n\n");
        logger.info("========================开始进行情节规划的主程序的处理===============================================");
        System.out.println("各项参数为:================"
                + "\n[主题:" + topic + "],\n[中文模板:" + templateAttr + "],\n[templateWithColor:" + templateWithColor
                + "],\n[colorMark:" + colorMark + "],\n[topicFromMG:" + topicFromMG
                + "],\n[strNegType:" + strNegType + "],\n[englishTemplate:" + englishTemplate);
        System.out.println("参数打印完毕===============");
        getEnglishTopic(topic, model);//讲英文主题转换成英文主题

        if (ProgramEntrance.isMiddleMessage) {
            logger.info("只有中间结果，直接将短信信息以文字形式打入空场景中");
            maName = "empty.ma";
        }
        // 处理当主题和模板都为空时，直接输出Nothing.ma文件,如果有多个NothingScene，则随机从里面选一个ma文件
        else if (maName == "" && topic.size() == 0 && templateAttr.size() == 0 && topicFromMG.size() == 0) {
            OWLNamedClass nothingClass = model.getOWLNamedClass("NothingScene");
            Collection nothingIndividual = nothingClass.getInstances();
            ArrayList<OWLIndividual> nothingIndivi = new ArrayList();
            for (Iterator iNothing = nothingIndividual.iterator(); iNothing.hasNext(); ) {
                OWLIndividual iindivi = (OWLIndividual) iNothing.next();
                nothingIndivi.add(iindivi);
            }
            Random rand = new Random();
            Date date = new Date();
            rand.setSeed(date.getTime());
            int kk = rand.nextInt(nothingIndivi.size());
            maName = nothingIndivi.get(kk).getBrowserText();
            logger.info("没有抽到主题和模板信息，直接输出nothing_0.ma文件：" + maName);

        }
        // END处理当主题和模板都为空时，直接输出Nothing.ma文件，如果有处理当主题和模板都为空时，直接输出Nothing.ma文件,如果有多个NothingScene，则随机从里面选一个ma文件/////////////////////////////////////////////
        if (true) {
            // 给所有没有实例的主题创建一个实例
            OWLNamedClass topicClass = model.getOWLNamedClass("Topic");//主题类对象
            Collection topicList1 = topicClass.getSubclasses(true);//主题类的子孙类
            OWLIndividual individual = null;
            for (Iterator itTopic = topicList1.iterator(); itTopic.hasNext(); ) {//遍历主题类
                OWLNamedClass classOne = (OWLNamedClass) itTopic.next();//next()
                if (classOne.getSubclassCount() == 0) {//如果这个主题类不在有子孙类（叶节点？？？）
                    if (classOne.getInstanceCount() == 0) {//如果这个主题下边没有实体
                        String individualName = classOne.getBrowserText() + "Individual";
                        individual = classOne.createOWLIndividual(individualName);
//						System.out.println("！！！！！！！！！！！！！！！！individualName:" + individualName);
                    }
                }
            }//结束遍历主题类？如果叶主题下没有实体
            // END//////////////给所有没有实例的主题创建一个实例
//			logger.info("目前所选的maName为：" + maName);
            if (maName == "") {//如果还未选定场景
                maName = "empty.ma";
                logger.info("还未选定场景，因此使用empty.ma");
            }
            // maName="Bridge04.ma"; 蒋孟馨0903
//			logger.info("目前所选的maName为：" + maName);
            OWLModel owlModel = null;
            if (maName.contains("Plot.ma")) {//如果是自动生成场景
                // 生成ma文件和ma中的space值
                logger.info("选用的是自动生成场景" + maName + "生成ma文件和ma中的space值");
                PlotDesign p = new PlotDesign();

                p.GenerateMa(maName, model);

                System.out.println("bjut.plot 中的topic:" + maName.substring(0, maName.length() - 3));

                // System.out.println("bjut.plot 中的topic:"+plotTopic);

                owlModel = new PlotAddModelToMa().processSWRL2(maName, maName.substring(0, maName.length() - 3), model,
                        englishTemplate, englishTemplatePlot);// 获得plot中的模型。放进ontology中描述
                // owlModel=new
                // PlotAddModelToMa().processSWRL2(maName,plotTopic,model,englishTemplate,englishTemplatePlot);//获得plot中的模型。放进ontology中描述

                String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";// 修改由AllOWL转回

                saveOWLFile((JenaOWLModel) model, fileName);
                String url = "C:/ontologyOWL/AllOwlFile/sumoOWL2";
                CopyFile c = new CopyFile();
                c.createFile(url);
            } else {
//				System.out.println(englishTopicStr);
                // addModelToTemplate(model,englishTemplate);
                owlModel = processSWRL(maName, englishTopicStr, model, englishTemplate);
                owlModel = perProcessBeforePrint(maName, model, topictemplate);
                String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";// 14.5.4
                // 日修改由AllOWL转回
                saveOWLFile((JenaOWLModel) owlModel, fileName);
            }
//			logger.info("record SceneName " + maName);
            FileWriter SceneRec = new FileWriter("C:/ontologyOWL/SceneRecord.txt", true);
            SceneRec.write(maName + " ");
            SceneRec.close();
            printToXML(owlModel, maName, englishTopicStr, strNegType);
            logger.info("========================结束情节规划的主程序===============================================");
            System.out.println("\n\n\n");
        }
    }

    // 蒋孟馨
    public static void PrintSceneCase() {
        int njnum = 0;
        double allfullScore1 = 0;
        String SceneResPath = XMLInfoFromIEDom4j.writeXML("SceneCasePath.xml");
        Document doc = XMLInfoFromIEDom4j.readXMLFile(SceneResPath);// 获得要输出的XML文件的头部
        colorModelNum = 0;
        colorChangeAttr.clear();
        Element rootName = doc.getRootElement();

        Element SceneCondition = rootName.addElement("SceneOptionCondition");
        String allScene = "";
        for (int i = 0; i < sceneList.size(); i++) {
            if (sceneList.get(i).score != 0) {
                njnum = njnum + 1;
                allScene += sceneList.get(i).sceneName;
                allScene += ";";
                allfullScore1 = allfullScore1 + sceneList.get(i).score;
            }
        }
        SceneCondition.addAttribute("SceneNum", String.valueOf(njnum));
        SceneCondition.addAttribute("AllOption", allScene);
        SceneCondition.addAttribute("allFullScore", Double.toString(allfullScore1));// 将总分值写入场景
        SceneCondition.addAttribute("SelectedScore", Double.toString(randomtemp));
        // 添加显示权值的属性
        Element WeightCondition = rootName.addElement("Weight");

        WeightCondition.addAttribute("DTree", "30");
        WeightCondition.addAttribute("MGTopic", "15");
        WeightCondition.addAttribute("IETopic", "15");
        WeightCondition.addAttribute("RuleTopic", "15");
        WeightCondition.addAttribute("TemplateRelated", "10");
        WeightCondition.addAttribute("PlacableModel", "10");
        WeightCondition.addAttribute("PlacableColorModel", "10");
        WeightCondition.addAttribute("Weather", "5");
        WeightCondition.addAttribute("Time", "5");
        WeightCondition.addAttribute("Action", "5");
        WeightCondition.addAttribute("QTopic", "10");
        // WeightCondition.addAttribute("BackGroundScene", "5");

        Element ScoreCondition = rootName.addElement("ScoreOptionCondition");

        if (sCaseDataUsable.get(1))
            ScoreCondition.addAttribute("MGTopic", "1");
        else
            ScoreCondition.addAttribute("MGTopic", "0");
        if (sCaseDataUsable.get(2))
            ScoreCondition.addAttribute("IETopic", "1");
        else
            ScoreCondition.addAttribute("IETopic", "0");
        if (sCaseDataUsable.get(3))
            ScoreCondition.addAttribute("RuleTopic", "1");
        else
            ScoreCondition.addAttribute("RuleTopic", "0");
        if (sCaseDataUsable.get(4))
            ScoreCondition.addAttribute("TemplateRelated", "1");
        else
            ScoreCondition.addAttribute("TemplateRelated", "0");
        if (sCaseDataUsable.get(5))
            ScoreCondition.addAttribute("PlacableModel", "1");
        else
            ScoreCondition.addAttribute("PlacableModel", "0");
        if (sCaseDataUsable.get(6))
            ScoreCondition.addAttribute("PlacableColorModel", "1");
        else
            ScoreCondition.addAttribute("PlacableColorModel", "0");
        if (sCaseDataUsable.get(7) | !windRainSnowNeedAttr.get(0).isEmpty() | !windRainSnowNeedAttr.get(1).isEmpty()
                | !windRainSnowNeedAttr.get(2).isEmpty())
            ScoreCondition.addAttribute("Weather", "1");
        else
            ScoreCondition.addAttribute("Weather", "0");
        if (sCaseDataUsable.get(8))
            ScoreCondition.addAttribute("QTopic", "1");
        else
            ScoreCondition.addAttribute("QTopic", "0");
        if (sCaseDataUsable.get(9))
            ScoreCondition.addAttribute("Time", "1");
        else
            ScoreCondition.addAttribute("Time", "0");
        if (sCaseDataUsable.get(10))
            ScoreCondition.addAttribute("Action", "1");
        else
            ScoreCondition.addAttribute("Action", "0");

        double allFullScore = 0;// 场景得分和
        double tempScore = 0;
        double actionAttr = 0;

        for (int i = 0; i < sceneList.size(); i++) {

            if (sceneList.get(i).score != 0) {
                float weather = hasWeatherTeplate ? 1 : 0;
                for (int j = 0; j < windRainSnowNeedAttr.size(); j++)
                    if (!windRainSnowNeedAttr.get(j).isEmpty()) {
                        weather = 1;
                    }
                if (!actionTemplateAttr.isEmpty() || sceneList.get(i).ActionScore > 0) {
                    actionAttr = 1;
                }
                float time = hasTimeTemplate ? 1 : 0;

                Element scene = ScoreCondition.addElement(sceneList.get(i).sceneName);

                // Element decisionvalue =
                // ScoreCondition.addElement(Double.toString(sceneList.get(i).decisionvalue));

                /****** 蒋梦馨 ******/
                Element decisionvalue = scene.addElement("decisionvalue");
                decisionvalue.addAttribute("decisionvalue", Double.toString(sceneList.get(i).decisionvalue));
                /****** 蒋梦馨 ******/

                Element MGTopic = scene.addElement("MGTopic");
                MGTopic.addAttribute("MGTopic", Double.toString(sceneList.get(i).MGProb));
                Element IETopic = scene.addElement("IETopic");
                IETopic.addAttribute("IETopic", Double.toString(sceneList.get(i).IEProb));
                Element RuleTopic = scene.addElement("RuleTopic");
                RuleTopic.addAttribute("RuleTopic", Double.toString(sceneList.get(i).ruleReason));
                Element TemplateRelated = scene.addElement("TemplateRelated");
                TemplateRelated.addAttribute("TemplateRelated", Double.toString(sceneList.get(i).templateRelated));
                Element PlacableModel = scene.addElement("PlacableModel");
                PlacableModel.addAttribute("PlacableModel", Double.toString(sceneList.get(i).placableModelNum) + "/"
                        + Double.toString(sceneList.get(i).templateModelNum));
                Element PlacableColorModel = scene.addElement("PlacableColorModel");
                PlacableColorModel.addAttribute("PlacableColorModel",
                        Double.toString(sceneList.get(i).placableColorModelNum) + "/"
                                + Double.toString(sceneList.get(i).colorModelNum));
                Element Weather = scene.addElement("Weather");
                Weather.addAttribute("Weather", Double.toString(sceneList.get(i).isWeatherable) + "/" + weather);
                Element Time = scene.addElement("Time");
                Time.addAttribute("Time", Double.toString(sceneList.get(i).timeable) + "/" + time);
                Element QProb = scene.addElement("QProb");
                QProb.addAttribute("Probably", Double.toString(sceneList.get(i).QProb));
                Element Action = scene.addElement("Action");
                Action.addAttribute("Action", Double.toString(sceneList.get(i).ActionScore) + "/" + actionAttr);

                // double fscore=sceneList.get(i).MGProb*15+
                Element indualScore = scene.addElement("IndualScore");
                indualScore.addAttribute("IndualScore", Double.toString(sceneList.get(i).indualScore));
                Element FullScore = scene.addElement("FullScore");
                FullScore.addAttribute("FullScore", Double.toString(sceneList.get(i).fullScore));
                Element Probably = scene.addElement("Probably");
                Probably.addAttribute("Probably", Double.toString(sceneList.get(i).score));

                Element ScoreSegment = scene.addElement("ScoreSegment");// 分值段
                double allFullScoretemp = sceneList.get(i).score;
                allFullScore = tempScore + allFullScoretemp;

                ScoreSegment.addAttribute("ScoreSegment",
                        "[" + (Double.toString(tempScore)) + "," + Double.toString(allFullScore) + ")");
                tempScore = allFullScore;
            }
        }
        boolean yesNo = XMLInfoFromIEDom4j.doc2XmlFile(doc, SceneResPath);
    }

    public static int addModelFromTopic(OWLModel model, String maName, String topicName, int count)
            throws SWRLRuleEngineException {
//		System.out.println("count=" + count);
        if (maName.startsWith("nothing") || maName.startsWith("empty.ma")) {
            return count;
        } else {
//			logger.info("与主题匹配型");
            OWLObjectProperty hasModelFromTopicProperty = model.getOWLObjectProperty("hasModelFromTopic");
            OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");
            OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
            OWLIndividual maIndividual = model.getOWLIndividual(maName);
            OWLNamedClass englishTopicClass = model.getOWLNamedClass(topicName);
            if (englishTopicClass.getInstanceCount() != 0) {
                Collection individualList = englishTopicClass.getInstances();
                OWLIndividual topicIndividualValue = null;
                ArrayList addModelToSpaceList = new ArrayList();
                if (individualList.size() > 0) {
                    Iterator its = individualList.iterator();
                    while (its.hasNext()) {
                        topicIndividualValue = (OWLIndividual) its.next();
                        if (topicIndividualValue.getPropertyValueCount(hasModelFromTopicProperty) > 0) {
                            SWRLMethod.addModelFromTopicToScene(model, topicName);
                            Collection col = topicIndividualValue.getPropertyValues(hasModelFromTopicProperty);
                            for (Iterator i = col.iterator(); i.hasNext(); ) {
                                OWLIndividual olw = (OWLIndividual) i.next();
                                addModelToSpaceList.add(olw);
                            }
                        }
                    }

                }
                System.out.println("由主题得到的模型:" + addModelToSpaceList);
                boolean flage = false;
                if (maIndividual.getPropertyValueCount(hasSceneSpaceProperty) > 0) {
                    Collection cols = maIndividual.getPropertyValues(hasSceneSpaceProperty);//获取场景用hasSceneSpaceProperty关联的场景集合
                    for (Iterator is = cols.iterator(); is.hasNext(); ) {//遍历场景下的所有可用空间
                        OWLIndividual ind = (OWLIndividual) is.next();//next();可用空间
                        if (ind.getPropertyValueCount(hasPutObjectInSpaceProperty) > 0) {
                            Collection cla = ind.getPropertyValues(hasPutObjectInSpaceProperty);//获取可用空间用hasPutObjectInSpaceProperty属性关联的模型

                            for (Iterator it = cla.iterator(); it.hasNext(); ) {//遍历可用空间关联的模型
                                OWLIndividual spaceModel = (OWLIndividual) it.next();

                                ArrayList arr = new ArrayList();//存放“主题相关模型”与“这个可用空间可放入的模型”的交集
                                for (int ig = 0; ig < addModelToSpaceList.size(); ig++) {//遍历由主题得到的模型
                                    if (addModelToSpaceList.get(ig).equals(spaceModel)) {//如果有主题得到的模型与可用空间关联的模型相同
                                        arr.add(addModelToSpaceList.get(ig));//如果相同就放进去
                                    }
                                }
                                System.out.println("主题得到的模型" + addModelToSpaceList + "可以放到可用空间的是：" + ind.getBrowserText());
                                count = setNumberToAddModel(new ArrayList(), arr, model, ind, count, topicName);
                            }//end 遍历可用空间关联的模型
                        }
                    }
                }

            }
        }
        return count;
    }

    /**
     * 通过枚举类来给所选场景的空间添加模型， 使场景有多变性效果，与模板和主题都无关
     * 不能随意添加人
     * 1.如果是空场景，直接返回
     * 2.如果不是空场景...
     * 1）遍历场景下的可用空间：
     * (1)AxiomClass类的子孙类集合中以Axiom的类，如果其值集合（OWLEnumeratedClass.getOneOfValues()）中有与可用空间 类名/实体名 相同的值，flag设置为true，break
     * (1.5)--》
     *
     * @param model
     * @param maName
     * @return
     */

    @SuppressWarnings("unchecked")
    public static OWLModel addModelFromEnumerateClass(OWLModel model, String maName, int count) {
//		System.out.println("count=" + count);
        if (maName.startsWith("nothing") || maName.startsWith("empty.ma")) {//如果是空场景
            return model;//不用添加模型，直接返回
        } else {//如果不是空场景
//			logger.info("AxiomClass得到的模型");
            OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");//对象属性：有可用空间
            OWLObjectProperty hasUsableModelProperty = model.getOWLObjectProperty("hasUsableModel");//对象属性：可用模型
            OWLIndividual maIndividual = model.getOWLIndividual(maName);//场景对象（maIndividual）
            // 获得所有的space
            Collection maSpaces = maIndividual.getPropertyValues(hasSceneSpaceProperty);//场景下可用空间集合（maSpaces）
            Iterator its = maSpaces.iterator();
            OWLEnumeratedClass enumeratedClass = null;
            while (its.hasNext()) {//遍历下的可用空间
                boolean isHasEnumClass = false;
                ArrayList<OWLIndividual> allIndividualCollection = new ArrayList();
                OWLIndividual spaceIndividual = (OWLIndividual) its.next();//next();取一个可用空间对象==============

                OWLNamedClass spaceClass = model.getOWLNamedClass("AxiomClass");//AxiomClass类对象
                Collection subSpaceClassList = spaceClass.getSubclasses(true);//AxiomClass类的子孙类集合===========
                Iterator spaceIts = subSpaceClassList.iterator();//AxiomClass类的子孙类集合
                RDFSClass subSpaceClass = null;
                loop:
                while (spaceIts.hasNext()) {//遍历AxiomClass类的子孙类
                    subSpaceClass = (RDFSClass) spaceIts.next();//next()
                    String strSubSpaceClass = subSpaceClass.getBrowserText();//子孙类类名
                    if (strSubSpaceClass.startsWith("Axiom")) {//如果是以Axiom开头=================
                        Collection enumeList = subSpaceClass.getEquivalentClasses();//获取可用空间集合

                        Iterator enumeListIts = enumeList.iterator();
                        while (enumeListIts.hasNext()) {//遍历AxiomClass类的子孙类集合中以Axiom开始的类=================
                            enumeratedClass = (OWLEnumeratedClass) enumeListIts.next();//next();

                            Collection enumOneOfList = enumeratedClass.getOneOfValues();
                            Iterator enumOneOfListIts = enumOneOfList.iterator();
                            while (enumOneOfListIts.hasNext()) {//遍历这个类的某种值
                                OWLIndividual oneOfIndividual = (OWLIndividual) enumOneOfListIts.next();//next()
                                if (oneOfIndividual.getBrowserText().equals(spaceIndividual.getBrowserText())) {
                                    isHasEnumClass = true;
                                    System.out.println("场景的可用空间" + oneOfIndividual.getBrowserText() + "在枚举类Axiom出现了");
//									System.out.println("subSpaceClass:" + subSpaceClass.getBrowserText());
                                    // System.out.println("enumeratedClass:"+enumeratedClass.getBrowserText());
                                    break loop;
                                }
                            }

                        }

                    }

                }
                if (isHasEnumClass) {//可用空间名有于枚举类中以Axio开头且oneOfValue相同的
                    OWLNamedClass aa = (OWLNamedClass) subSpaceClass;//上转型
                    int random = 1;
                    if (aa.getBrowserText().toString().contains("Air")) {//如果类名包含 Air
                        random = (int) Math.random();
                        System.out.println("场景可用空间" + aa.getBrowserText().toString() + "为Air类型，放弃添加Axiom模型");
                    }
                    if (random == 1) {//如果随机数为1
                        RDFResource hasUsableModelClass = aa.getAllValuesFrom(hasUsableModelProperty);//这个枚举类下边可用的模型类
                        String strHasUsableModelClass = hasUsableModelClass.getBrowserText();
                        String[] strHasUsableModelClassSplit = strHasUsableModelClass.split(" or ");//将枚举类下边可用的模型类放到数组里边
                        for (int i = 0; i < strHasUsableModelClassSplit.length; i++) {//遍历枚举类可用空间下的模型类
                            String modelClassStr = strHasUsableModelClassSplit[i].trim();
//							System.out.println("modelClassStr: " + modelClassStr);
                            OWLNamedClass modelClass = model.getOWLNamedClass(modelClassStr.trim());//得到相应的模型类
                            // modelClass.getins
                            if (modelClass != null) {
                                Collection modelClassIndividuals = modelClass.getInstances(true);//得到这个模型类下边的模型实体
                                if (modelClassIndividuals.size() > 0) {
                                    allIndividualCollection.addAll(modelClassIndividuals);//将模型类下边所有实体都放进allIndividualCollection链表中：ArrayList<OWLIndividual>
//									System.out.print("ok");
                                }
                            }
                        }// end of 遍历枚举类可用空间下的模型类
                        int kk = 0;
                        if (allIndividualCollection.size() > 5)//如果随机模型链已经放进大于5个模型，kk=5;否则kk=链表容量
                            kk = 5;
                        else
                            kk = allIndividualCollection.size();
                        Random rand = new Random();
                        int kkk = rand.nextInt(kk);//kkk为 [0,kk]之间的数
//						System.out.println("kkk:" + kkk);
                        HashSet<Integer> set = new HashSet<Integer>();// 在总的规则中随机选着kk条规则
                        for (int i = 0; i <= kkk; i++) {
                            int t = (int) (Math.random() * allIndividualCollection.size());
                            set.add(t);//set集合添加kkk个随机数
                        }
                        ArrayList<OWLIndividual> addModelToSpaceList = new ArrayList();
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {//遍历set集合
                            Integer num = (Integer) iterator.next();
                            if (allIndividualCollection.get(num).getBrowserText().contains(".ma")) {//如果模型链表中选出来的元素时模型
                                // 判断模型的是否可以放在ma场景中，即模型location属性集合-》isEquivalOf值是否与场景hasValueOfPlace属性值相同
                                boolean flage = isOwdToMa(model, maName, allIndividualCollection.get(num));//
//								System.out.println(flage);
                                if (flage == true) {// 判断模型的是否可以放在ma场景中，即模型location属性集合-》isEquivalOf值是否与场景hasValueOfPlace属性值相同
                                    addModelToSpaceList.add(allIndividualCollection.get(num));
                                    System.out.println(maName + "的可用空间(hasSceneSpace):" + spaceIndividual.getBrowserText() + "可以再枚举类(Axiom)中找到，而且此枚举类对应的模型"
                                            + allIndividualCollection.get(num).getBrowserText() + "可以放置的位置与ma场景可用空间对应:模型的:location集合-->isEquivalOf集合=场景的hasValueOfPlace");
                                }

                            }

                        }
                        count = setNumberToAddModel(new ArrayList(), addModelToSpaceList, model, spaceIndividual, count,
                                "");
                    }//end of 随机数为1
                }//end of 场景的可用空间在枚举类中

            }//end of 遍历尝尽下的可用空间
        }
        return model;
    }

    /**
     * 通过回溯取得ma场景，当所选的背景场景没有所需的空间时，则选择其他的场景
     *
     * @param model
     * @param maName
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getMaThroughBack(OWLModel model, String maName) {
        // usedSpaceInMa
        String maName1 = "";
        OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
        OWLIndividual maIndividual = model.getOWLIndividual(maName);
        OWLNamedClass fatherClass = getClassFromIndividual(model, maIndividual);
        OWLNamedClass backGroundClass = model.getOWLNamedClass("BackgroundScene");
        Collection individualList = backGroundClass.getInstances(true);// 获得类下的所有实例

        Collection parentClassList = fatherClass.getSuperclasses(true);// 获得类的所有父类
        Iterator<OWLNamedClass> its = parentClassList.iterator();
        boolean isBackground = false;
        while (its.hasNext()) {
            OWLNamedClass ppClass = its.next();
            if (ppClass.getBrowserText().equals("BackgroundScene")) {
                isBackground = true;
                break;
            }
        }

        // s所选的背景场景所用的场景空间为0
        Collection spaceList = maIndividual.getPropertyValues(usedSpaceInMaProperty);
        if (isBackground) {
            logger.info("所选的场景为背景场景");
            ArrayList<OWLIndividual> individualList2 = new ArrayList();
            if (spaceList.size() == 0 && individualList.size() > 1) {
                logger.info("所选的背景场景  " + maName + "  没有适用的可放物空间，并有其他兄弟实例");
                Iterator<OWLIndividual> its1 = individualList.iterator();
                while (its1.hasNext()) {
                    OWLIndividual individualP = its1.next();
                    Collection usedSpaceList1 = individualP.getPropertyValues(usedSpaceInMaProperty);
                    if (!individualP.getBrowserText().equals(maName) && usedSpaceList1.size() > 0) {
                        individualList2.add(individualP);
                    }
                }
                if (individualList2.size() > 0) {
                    logger.info("兄弟实例有适用的可放物空间");
                    Random rand = new Random();
                    int k = rand.nextInt(individualList2.size());
                    maName1 = individualList2.get(k).getBrowserText();
                    return maName1;
                } else {
                    logger.info("虽然有兄弟实例，但兄弟实例也没有适用的可放物空间，则选择空场景");
                    return "empty.ma";
                }
            } else if (spaceList.size() == 0 && individualList.size() == 1) {
                logger.info("没有兄弟实例，本身又没有适用的可放物空间，则选择空场景");
                return "empty.ma";
            } else {
                logger.info("所选的背景场景有可用的空间");
                return maName;
            }

        } else
            return maName;

    }

    /**
     * fixme 通过模板原子来查找模板原子所对应的模型（hasModelFromTemplate）,应该命名为getModelFromEnglishTemplate
     * fixme 添加了从DBpedia中获取模型的代码
     *
     * @param modelx
     * @param englishTemplate
     * @return
     */
    public static ArrayList<String> getIndividualFromEnglishTemplate(OWLModel modelx, ArrayList<String> englishTemplate) {


        ArrayList<String> individualList = new ArrayList();
        OWLObjectProperty hasModelFTpProperty = modelx.getOWLObjectProperty("hasModelFromTemplate");
        for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); )// 遍历所有的模板原子
        {
            String templateAllName = its.next();
            int iPostion = templateAllName.indexOf(":");
            String[] temp = templateAllName.split(":");

            String templateAutmName = templateAllName.split(":")[temp.length - 1];
            // String templateAutmName1=temp.get();
            if (!templateAllName.equals(templateAutmName)) {
                OWLIndividual templateIndividual = modelx.getOWLIndividual(templateAutmName);
                if (!templateIndividual.equals(null))// 查看模板原子所对应的实例是否存在
                // if(!templateIndividual.equals(null))//查看模板原子所对应的实例是否存在
                {
                    OWLObjectProperty hs = modelx.getOWLObjectProperty("hasModelFromTemplate");
                    int k = templateIndividual.getPropertyValueCount(hs);
                    // System.out.println(k);
                    int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
                    if (valueNum > 0)// 对应的model数量是否大于0
                    {
                        Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);

                        for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                            String value = its2.next().getBrowserText();

                            individualList.add(value);

                        }
                    }
                } else
                    continue;
            }

        }
        HashSet h = new HashSet();
        h.addAll(individualList);
        individualList.clear();
        individualList.addAll(h);
        return individualList;
//
//        //20180527:此时之前打开的model数据貌似已经被修改，故读取备份数据内容
//        OWLModel model=null;
//		try {
//			model = createOWLFile1("file:///c:/ontologyOWL/sumoOWL2/sumo_phone3.owl");
//		} catch (OntologyLoadException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        //20180527
//
//		ArrayList<String> resIndividualList = new ArrayList();
//		//fixme 使用模板实例的这个属性找模板相关的model
//		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate"),
//				broHasModelFTpProperty = modelx.getOWLObjectProperty("hasModelFromTemplate");
//
//		Iterator<String> its = englishTemplate.iterator();
//		while ( its.hasNext()){// 遍历所有的英文模板
//			String templateAllName = its.next();//当前英文模板取值
//			String[] temp = templateAllName.split(":");
//			String templateAutmName = templateAllName.split(":")[temp.length - 1];
//
//			if (!templateAllName.equals(templateAutmName)) {
//				OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName),
//                        broTemplateIndividual=modelx.getOWLIndividual(templateAutmName);
//				if (!templateIndividual.equals(null)){// 查看模板原子所对应的实例是否存在
//
//					//fixme 查找模板是否通过hasModelFromTemplate关联到了模型，没有的话在else逻辑中使用已经改变的model查找
//					if (templateIndividual.getPropertyValueCount(hasModelFTpProperty) > 0){// fixme 如果模板有关联的模型，遍历所有模型
//                        Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);
//
//                        Iterator<OWLIndividual> its2 = templateModelVlaues.iterator();
//                        while (its2.hasNext()) {
//                            String value = its2.next().getBrowserText();
//                            resIndividualList.add(value);//fixme 模板相关的模型加入模板链表中
//                        }
//                    }else{
//                    	//fixme 从兄弟节点中选择
//                        Collection templateModelVlaues = broTemplateIndividual.getPropertyValues(broHasModelFTpProperty);
//                        Iterator<OWLIndividual> its2 = templateModelVlaues.iterator();
//                        while (its2.hasNext()) {
//                            String value = its2.next().getBrowserText();
//                            resIndividualList.add(value);//fixme 模板相关的模型加入模板链表中
//                        }
//                    }
//                } else
//					continue;
//			}
//
//		}//以下几行代码是去重
//		HashSet h = new HashSet();
//		h.addAll(resIndividualList);
//		resIndividualList.clear();
//		resIndividualList.addAll(h);
//
//		return resIndividualList;
    }

    /**
     * 由模板得到动画场景
     *
     * @param model
     * @param englishTemplate
     * @return
     * @throws SWRLRuleEngineException
     */
    public static String getMaFromTemplate(OWLModel model, ArrayList<String> englishTemplate)
            throws SWRLRuleEngineException {
        String maName = "";
        logger.info("如果模板有对应的动画场景。则通过模板选择动画场景");
        maName = getAnimationSceneFromTemplateUsingSWRL(model, englishTemplate);
        if (maName.equals("") || maName.equals(null)) {
            maName = getAnimationSceneFromTemplate(model, englishTemplate);
            if (maName.equals("") || maName.equals(null)) {
                logger.info("运行规则，由模板原子没有推出主题，则选择背景场景或空场景");
                boolean bHasModelFromTemplate = false;
                OWLObjectProperty hasModelFromTemplateProperty = model.getOWLObjectProperty("hasModelFromTemplate");
                OWLObjectProperty hasBackgroundSceneProperty = model.getOWLObjectProperty("hasBackgroundScene");
                for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); )// 遍历所有的模板原子
                {
                    String templateAllName = its.next();
                    int iPostion = templateAllName.indexOf(":");
                    String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());

                    OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
                    if (!templateIndividual.equals(null))// 查看模板原子所对应的实例是否存在
                    {
                        int num = templateIndividual.getPropertyValueCount(hasModelFromTemplateProperty);
                        if (num > 0) {
                            bHasModelFromTemplate = true;
                            break;
                        }

                    }
                }
                if (bHasModelFromTemplate)// 当模板原子有对应的模型时，则选择背景场景
                {
                    OWLNamedClass backgroundClass = null;
                    ArrayList<String> BackgroundSceneName = new ArrayList();// 用来保存每次短信所抽到模板所对应的所有背景场景类
                    for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); ) {
                        String templateAllName = its.next();
                        int iPostion = templateAllName.indexOf(":");
                        String templateTemp = templateAllName.substring(0, iPostion);
                        OWLNamedClass templateNClass = model.getOWLNamedClass(templateTemp);//
                        logger.info("获得模板名:" + templateNClass.getBrowserText() + "所对应的hasBackgroundScene对象属性值");

                        RDFResource resource = templateNClass.getSomeValuesFrom(hasBackgroundSceneProperty);
                        if (resource != null)// 有些模板名没有hasBackgroundScene的值
                        {
                            String hasValues = resource.getBrowserText();// 获得主题对应的音乐的类名
                            String[] hasValuesSplit = hasValues.split("or");
                            if (hasValuesSplit.length > 1) {// 当有多个音乐类时，先判断每个音乐类是否都有实例
                                for (int i = 0; i < hasValuesSplit.length; i++) {
                                    BackgroundSceneName.add(hasValuesSplit[i].trim());
                                }

                            } else
                                BackgroundSceneName.add(hasValuesSplit[0].trim());
                        }
                    }
                    if (BackgroundSceneName.size() > 1) {
                        Random rand = new Random();
                        Date date = new Date();
                        rand.setSeed(date.getTime());
                        int kk = rand.nextInt(BackgroundSceneName.size());
                        backgroundClass = model.getOWLNamedClass(BackgroundSceneName.get(kk));
                    } else
                        backgroundClass = model.getOWLNamedClass(BackgroundSceneName.get(0));
                    logger.info("最后选的背景场景类是:" + backgroundClass.getBrowserText());
                    if (backgroundClass != null) {
                        if (backgroundClass.getInstanceCount(false) > 0) {
                            ArrayList<OWLIndividual> backgroundIndividualList = new ArrayList();
                            Collection backgroundIndividualList1 = backgroundClass.getInstances();
                            for (Iterator<OWLIndividual> itt = backgroundIndividualList1.iterator(); itt.hasNext(); )
                                backgroundIndividualList.add(itt.next());
                            Random rand = new Random();
                            Date date = new Date();
                            rand.setSeed(date.getTime());
                            int kk = rand.nextInt(backgroundIndividualList.size());
                            OWLIndividual backgroundIndividual = backgroundIndividualList.get(kk);
                            maName = backgroundIndividual.getBrowserText();
                            bIsBackgroundScene = true;
                        } else {
                            maName = "Tropical45.ma";
                            bIsBackgroundScene = true;
                        }
                        // OWLIndividual
                        // emptyIndividual=model.getOWLIndividual("empty.ma");

                    } else
                        maName = "empty.ma";

                } else
                    maName = "empty.ma";
            }
        }
        return maName;
    }

    /**
     * 通过模板利用规则推导出符合模板信息的动画场景
     *
     * @param model
     * @param englishTemplate
     * @return
     * @throws SWRLRuleEngineException
     */
    public static String getAnimationSceneFromTemplateUsingSWRL(OWLModel model, ArrayList<String> englishTemplate)
            throws SWRLRuleEngineException {
        OWLObjectProperty hasAnimationNameFProperty = model.getOWLObjectProperty("hasMaFromTemplateUsingSWRL");// usedSpaceInMa
        ArrayList<OWLIndividual> animationList = new ArrayList();
        Random rand = new Random();
        Iterator its = englishTemplate.iterator();
        while (its.hasNext()) {
            String templateAllName = (String) its.next();
            int iPostion = templateAllName.indexOf(":");
            String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());
            OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
            if (templateIndividual.getPropertyValueCount(hasAnimationNameFProperty) > 0) {
                Collection collection = templateIndividual.getPropertyValues(hasAnimationNameFProperty);
                for (Iterator iValues = collection.iterator(); iValues.hasNext(); ) {
                    OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
                    animationList.add(animationIndividual);
                }
            }
        }
        if (animationList.size() > 0) {
            return animationList.get(animationList.size() - 1).getBrowserText();
        }
        return "";
    }

    /**
     * 当信息抽取没有抽到主题，模板也没有推出主题，则由模板通过hasAnimationNameFromTemplate
     * 看可否得到相应的动画场景（直接由属性值得出）
     *
     * @param model
     * @param englishTemplate
     * @return
     */


    /**
     * ma之前先处理addToMa,exChangedModelInMa属性，因为通过规则推导可能某个类下面有多个实例，这样经过添加规则推导后，会
     * 把所有的实例都添加上去，而实际上只要所有实例中的随机的某个
     *
     * @param maName
     * @param model
     * @param englishTemplate1
     * @return
     * @throws SWRLRuleEngineException
     */
    public static OWLModel perProcessBeforePrint(String maName, OWLModel model, ArrayList<String> englishTemplate1)
            throws SWRLRuleEngineException {
//		System.out.println("88888888888888888888maName:" + maName);
//		System.out.println("88888888888888888888englishTemplate:" + englishTemplate1);
        int count = 0;
        int sum = 0;
        // ArrayList<String> englishtemplate=new ArrayList<String>();
        String topicName = "";
        for (Iterator in = englishTemplate1.iterator(); in.hasNext(); ) {
//			System.out.println(++sum);
            ArrayList<String> englishtemplate = new ArrayList<String>();
            String str = (String) in.next();
            String[] hasvalue = str.split("-");
            if (topiclist.size() != 0) {
                boolean flage = false;
                for (int l = 0; l < topiclist.size(); l++) {
                    topicName = topiclist.get(l);
                    if (hasvalue[0].equals(topicName)) {
                        flage = true;
                        for (int is = 1; is < hasvalue.length; is++) {
                            int i = hasvalue[is].indexOf(":");
                            englishtemplate.add(hasvalue[is].substring(i + 1));
                        }
                        break;
                    }
                }
                if (flage == false) {
                    topicName = "";
                    for (int is = 1; is < hasvalue.length; is++) {
                        int i = hasvalue[is].indexOf(":");
                        englishtemplate.add(hasvalue[is].substring(i + 1));
                    }
                }

            } else {
                for (int it = 1; it < hasvalue.length; it++) {
                    int k = hasvalue[it].indexOf(":");
                    englishtemplate.add(hasvalue[it].substring(k + 1));
                }
            }
//			System.out.println("88888888888888888888englishtemplate:" + englishtemplate);
            OWLIndividual maIndividual = model.getOWLIndividual(maName);
            OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
            OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
            OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
            OWLObjectProperty usedModelInMaProperty = model.getOWLObjectProperty("usedModelInMa");
            OWLObjectProperty exchangedModelInMaProperty = model.getOWLObjectProperty("exchangedModelInMa");
            OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
            OWLObjectProperty deleteProperty = model.getOWLObjectProperty("subtractFromMa");
            OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");

            // 用来处理update规则，使场景中的某个objet只能被改为所有被选择可以改的实例当中的一个
            if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0)// 判断usedModelInMa属性
            {
                Collection usedModelValues = maIndividual.getPropertyValues(usedModelInMaProperty);
                for (Iterator iValues = usedModelValues.iterator(); iValues.hasNext(); ) {
                    OWLIndividual usedModelIndividual = (OWLIndividual) iValues.next();
                    // 判断每个usedModelInMa属性的exChangedModelInMa属性
                    Collection exChangedModelInMaValues = usedModelIndividual
                            .getPropertyValues(exchangedModelInMaProperty);
                    ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// 用来存放每个space上存放的物体
                    // 通过Map的<key，value>对值来处理添加某个类下面的多个实例的问题
                    Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();
                    for (Iterator iiValues = exChangedModelInMaValues.iterator(); iiValues.hasNext(); ) {
                        OWLIndividual objectIndividual = (OWLIndividual) iiValues.next();
                        OWLNamedClass classN = getClassFromIndividual(model, objectIndividual);
                        ArrayList<OWLIndividual> values = map.get(classN);
                        if (values == null) {
                            values = new ArrayList<OWLIndividual>();
                            map.put(classN, values);
                        }
                        values.add(objectIndividual);

                    }
                    Set<OWLNamedClass> keys = map.keySet();// 逐个处理每个类
                    Object[] choosedClassNums = keys.toArray();
                    int length = choosedClassNums.length;
                    Random rand = new Random();
                    Date date = new Date();
                    rand.setSeed(date.getTime());
                    int kk = rand.nextInt(length);
                    OWLNamedClass classNmae = (OWLNamedClass) choosedClassNums[kk];// 多个类随机的选一个
                    ArrayList<OWLIndividual> values2 = map.get(classNmae);// 获得exChandedModelInMa中某个类下面的所有实例
                    int k2 = rand.nextInt(values2.size());
                    OWLIndividual changedIndividual = values2.get(k2);
                    usedModelIndividual.setPropertyValue(exchangedModelInMaProperty, changedIndividual);

                }
            }
            Collection hasModelList = maIndividual.getPropertyValues(hasModelProperty);

            // ArrayList hasModelList=(ArrayList)hasModelList2.;
            if (maIndividual.getPropertyValueCount(deleteProperty) > 0)// 剪掉删除规则中的模型
            {
                Collection deleteModelList = maIndividual.getPropertyValues(deleteProperty);
                Iterator<OWLIndividual> its = deleteModelList.iterator();
                while (its.hasNext()) {
                    ArrayList<OWLIndividual> hasModelList2 = new ArrayList();
                    OWLIndividual deleteModel = its.next();
                    for (Iterator<OWLIndividual> its2 = hasModelList.iterator(); its2.hasNext(); ) {
                        OWLIndividual hasModelValue = its2.next();
                        if (!deleteModel.getBrowserText().equals(hasModelValue.getBrowserText())) {
                            // hasModelList.remove(deleteModel);
                            // its2.remove();
                            hasModelList2.add(hasModelValue);
                        }
                    }
                    hasModelList = hasModelList2;
                    hasModelList2 = null;
                }
            }
            // 从hasModel中删除更改规则中的模型
            if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0) {
                Collection usedModelInMaPropertyList = maIndividual.getPropertyValues(usedModelInMaProperty);
                Iterator<OWLIndividual> its = usedModelInMaPropertyList.iterator();
                while (its.hasNext()) {
                    ArrayList<OWLIndividual> hasModelList3 = new ArrayList();
                    OWLIndividual deleteModel = its.next();
                    for (Iterator<OWLIndividual> its2 = hasModelList.iterator(); its2.hasNext(); ) {
                        OWLIndividual hasModelValue = its2.next();
                        if (!deleteModel.getBrowserText().equals(hasModelValue.getBrowserText())) {
                            // hasModelList.remove(deleteModel);
                            // its2.remove();
                            hasModelList3.add(hasModelValue);
                        }
                    }
                    hasModelList = hasModelList3;
                    hasModelList3 = null;
                }
            }
            if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0
                    || maIndividual.getPropertyValueCount(deleteProperty) > 0)
                maIndividual.setPropertyValues(hasModelProperty, hasModelList);
            ArrayList<OWLIndividual> individualList = new ArrayList();
            ArrayList<OWLNamedClass> classList = new ArrayList();
            ArrayList<String> individualListFromTemplate = getIndividualFromEnglishTemplate(model, englishtemplate);
//			System.out.println("qqqqqqqqqqqqqqqqqqqq individualListFromTemplate:" + individualListFromTemplate);
            // @SuppressWarnings("unused")

            String name = maIndividual.getBrowserText();
//			System.out.println("maName:" + name);
            // 从usedSpaceInMa入手，主要更改addTomMa属性
            if (maIndividual.getPropertyValueCount(usedSpaceInMaProperty) > 0) {

                Collection usedSpaceValues = maIndividual.getPropertyValues(usedSpaceInMaProperty);
                for (Iterator iValues = usedSpaceValues.iterator(); iValues.hasNext(); ) {// iVlaues是space的名字
                    OWLIndividual spaceIndividual = (OWLIndividual) iValues.next();
                    Collection objectInSpaceValues = spaceIndividual.getPropertyValues(hasPutObjectInSpaceProperty);
//					System.out.println("qqqqqqqqqqqqqqqqqqqq spaceIndividual:" + spaceIndividual);
//					System.out.println("qqqqqqqqqqqqqqqqqqqq objectInSpaceValues:" + objectInSpaceValues);

                    // objectInSpaceValues可以放入空间的模型列表
                    ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// 用来存放每个space上存放的模型个体的OWLIndividual
                    ArrayList<OWLIndividual> outterIndividualList = new ArrayList();// 用来存放不是由模板规则推出来的所添加的模型

                    // 通过Map的<key，value>对值来处理添加某个类下面的多个实例的问题
                    Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();

                    for (Iterator iiValues = objectInSpaceValues.iterator(); iiValues.hasNext(); ) {// iiValues每个space上面放的物体
                        OWLIndividual objectIndividual = null;
                        if (name.equals("empty.ma"))
                            objectIndividual = model.getOWLIndividual(iiValues.next().toString());
                        else
                            objectIndividual = (OWLIndividual) iiValues.next();

                        Iterator itd = individualListFromTemplate.iterator();// 模板对应的模型列表
                        boolean isEqualTemplate = false;
                        // 比对模型名字，是模板对应的模型放入innerIndividualList，不是则放入
                        while (itd.hasNext()) {
                            // isEqualTemplate=false;
                            // 跟模板对应的模型一定要加到addToMa中
                            String individualStr = (String) itd.next();
                            if (individualStr.equals(objectIndividual.getBrowserText())) {
                                innerIndividualList.add(objectIndividual);
                                individualList.add(objectIndividual);
                                isEqualTemplate = true;
                            } else
                                continue;
                        }

                        // 如果不是与模板相关的模型，则加到outterIndividualList
                        if (!isEqualTemplate)
                            outterIndividualList.add(objectIndividual);
                    }
//					System.out.println("qqqqqqqqqqqqqqqqqqqq outterIndividualList:" + outterIndividualList);
                    HashSet h = new HashSet(innerIndividualList);
                    innerIndividualList.clear();
                    innerIndividualList.addAll(h);
//					System.out.println("qqqqqqqqqqqqqqqqqqqq individualList:" + innerIndividualList);
//					System.out.println(topicName);
                    count = setNumberToAddModel(innerIndividualList, new ArrayList(), model, spaceIndividual, count,
                            topicName);
//					System.out.println("qqqqqqqqqqqqqqqqqqqq count:" + count);
                }
                maIndividual.setPropertyValues(addToMaProperty, individualList);
            }
            if (!topicName.equals("")) {
                System.out.println("从主题" + topicName + "选取模型");
                count = addModelFromTopic(model, maName, topicName, count);
                maIndividual.setPropertyValue(topicNameProperty, topicName);
//				System.out.println("****************#topicName#####:" + maIndividual.getPropertyValue(topicNameProperty));
            }

        }
        logger.info("开始通过Axiom类随机添加模型=============================");
        model = addModelFromEnumerateClass(model, maName, count);// axiom类选择模型
        logger.info("开始通过Axiom类随机添加模型=============================");
        return model;
    }


    /**
     * 给每个space上添加的模型进行编号, 编号后同时添加到“AddModelRelated”类中
     *
     * @param individualList  模型链表，每个space上所添加的模型
     * @param randIndiList
     * @param model           OWL模型对象
     * @param spaceIndividual 可用空间
     * @param count
     * @param topicName
     * @return
     */
    public static int setNumberToAddModel(ArrayList<OWLIndividual> individualList,
                                          ArrayList<OWLIndividual> randIndiList, OWLModel model, OWLIndividual spaceIndividual, int count,
                                          String topicName) {
//		System.out.println(topicName);
        OWLNamedClass addModelRelatedClass = model.getOWLNamedClass("AddModelRelated");//对象属性
        OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");//对象属性
        OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");//对象属性
        OWLDatatypeProperty isTempObject = model.getOWLDatatypeProperty("isTemplateObject");//数据属性
        OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");//数据属性
        OWLDatatypeProperty addModelNumberProperty = model.getOWLDatatypeProperty("addModelNumber");//数据属性
        OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");//数据属性
        OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");//数据属性
        // 20170508 Yangyong
        OWLDatatypeProperty isDeal = model.getOWLDatatypeProperty("isUsed");//数据属性
        /////////////// end
        Iterator its = individualList.iterator();
        Iterator its2 = randIndiList.iterator();
        while (its.hasNext()) {
            count++;
            OWLIndividual addModelValue = (OWLIndividual) its.next();
//			System.out.println("addModelValue:" + addModelValue);
            // OWLDatatypeProperty
            // addModelNumProperty=model.getOWLDatatypeProperty("massNum");
            OWLObjectProperty addModelNumProperty1 = model.getOWLObjectProperty("massScale");
            OWLDatatypeProperty massNumber = model.getOWLDatatypeProperty("massNumber");
            OWLIndividual addNumIndi = (OWLIndividual) addModelValue.getPropertyValue(addModelNumProperty1);
            String addNum = "";
            if (addNumIndi != null) {
                OWLNamedClass massNum = (OWLNamedClass) addNumIndi.getDirectType();
                int maxNumber = massNum.getMaxCardinality(massNumber);
                int minNumber = massNum.getMinCardinality(massNumber);
                if (maxNumber == minNumber) {
                    addNum = String.valueOf(minNumber);
                } else {
                    Random r = new Random();
                    addNum = String.valueOf(r.nextInt(maxNumber - minNumber) + minNumber);
                }

            } else {
                addNum = "1";
            }
            String modelIdStr = "addModelID" + count;
//			System.out.println("modelID=" + modelIdStr + "\t" + "addModelNum=" + addNum);
            OWLIndividual addIndividual = addModelRelatedClass.createOWLIndividual(modelIdStr);
            addIndividual.setPropertyValue(modelIDProperty, modelIdStr);

            addIndividual.setPropertyValue(addModelRelatedSpaceProperty, spaceIndividual);
            addIndividual.setPropertyValue(hasModelNameProperty, addModelValue);
            addIndividual.setPropertyValue(isTempObject, "1");
            if (!topicName.equals(""))
                addIndividual.setPropertyValue(topicNameProperty, topicName);
            if (addModelValue.getBrowserText().contains("ParticleEffect"))
                addIndividual.setPropertyValue(addModelTypeProperty, "ParticleEffect");
            else if (addModelValue.getBrowserText().contains(".ma")) {

                OWLNamedClass classN = getClassFromIndividual(model, addModelValue);
                Collection parentClassList = classN.getSuperclasses(true);
                Iterator its1 = parentClassList.iterator();
                boolean isHuman = false;
                while (its1.hasNext()) {
                    Object obj = its1.next();
                    if (obj.toString().contains("NamedClass")) {
                        RDFResource parentClass = (RDFResource) obj;
                        String temp = parentClass.getBrowserText();
                        if (parentClass.getBrowserText().equals("p1:Woman")
                                || parentClass.getBrowserText().equals("p1:Man")) {
                            isHuman = true;
                            break;
                        }
                    }

                }
                if (isHuman) {

                    addIndividual.setPropertyValue(addModelTypeProperty, "people");
                    addIndividual.setPropertyValue(addModelNumberProperty, "1");
                    addIndividual.setPropertyValue(isTempObject, "1");
                    // 20170508 Yangyong
                    addIndividual.setPropertyValue(isDeal, "false");
                    ///////// end
                } else {
                    addIndividual.setPropertyValue(addModelNumberProperty, addNum);
                    addIndividual.setPropertyValue(addModelTypeProperty, "model");
                    if (addModelValue.getBrowserText().equalsIgnoreCase("m_floor.ma")) {// 将地面设为0
                        addIndividual.setPropertyValue(isTempObject, "0");
                    } else {
                        addIndividual.setPropertyValue(isTempObject, "1");
                    }
                }
            }
        }

        while (its2.hasNext()) {
            count++;
            OWLIndividual addModelValue = (OWLIndividual) its2.next();
            System.out.println("addModelValue:" + addModelValue);
            // OWLDatatypeProperty
            // addModelNumProperty=model.getOWLDatatypeProperty("massNum");
            // String
            // addNum=(String)addModelValue.getPropertyValue(addModelNumProperty);
            // String
            // addNum=(String)addModelValue.getPropertyValue(addNumProperty);
            OWLObjectProperty addModelNumProperty1 = model.getOWLObjectProperty("massScale");
            OWLDatatypeProperty massNumber = model.getOWLDatatypeProperty("massNumber");
            OWLIndividual addNumIndi = (OWLIndividual) addModelValue.getPropertyValue(addModelNumProperty1);
            String addNum = "";
            if (addNumIndi != null)

            {
                OWLNamedClass massNum = (OWLNamedClass) addNumIndi.getDirectType();
                int maxNumber = massNum.getMaxCardinality(massNumber);
                int minNumber = massNum.getMinCardinality(massNumber);
                if (maxNumber == minNumber) {
                    addNum = String.valueOf(minNumber);
                } else {
                    Random r = new Random();
                    addNum = String.valueOf(r.nextInt(maxNumber - minNumber) + minNumber);
                }

            } else {
                addNum = "1";
            }
            String modelIdStr = "addModelID" + count;
//			System.out.println("addModelID=" + count + "\tmodelNum=" + addNum);
            // if(modelIdStr.e)
            OWLIndividual addIndividual = addModelRelatedClass.createOWLIndividual(modelIdStr);

            addIndividual.setPropertyValue(modelIDProperty, modelIdStr);
            addIndividual.setPropertyValue(addModelRelatedSpaceProperty, spaceIndividual);
            addIndividual.setPropertyValue(hasModelNameProperty, addModelValue);
            addIndividual.setPropertyValue(isTempObject, "0");
            if (addModelValue.getBrowserText().contains("ParticleEffect"))
                addIndividual.setPropertyValue(addModelTypeProperty, "ParticleEffect");
            else if (addModelValue.getBrowserText().contains(".ma")) {

                OWLNamedClass classN = getClassFromIndividual(model, addModelValue);
                Collection parentClassList = classN.getSuperclasses(true);
                Iterator its1 = parentClassList.iterator();
                boolean isHuman = false;
                while (its1.hasNext()) {
                    Object obj = its1.next();
                    if (obj.toString().contains("NamedClass")) {
                        RDFResource parentClass = (RDFResource) obj;
                        String temp = parentClass.getBrowserText();
                        if (parentClass.getBrowserText().equals("p1:Woman")
                                || parentClass.getBrowserText().equals("p1:Man")) {
                            isHuman = true;
                            break;
                        }
                    }

                }
                if (isHuman) {
                    addIndividual.setPropertyValue(addModelTypeProperty, "people");
                    addIndividual.setPropertyValue(addModelNumberProperty, "1");
                    addIndividual.setPropertyValue(isTempObject, "0");
                    // 20170508 Yangyong
                    addIndividual.setPropertyValue(isDeal, "false");
                } else {
                    addIndividual.setPropertyValue(addModelNumberProperty, addNum);
                    addIndividual.setPropertyValue(addModelTypeProperty, "model");
                    addIndividual.setPropertyValue(isTempObject, "0");
                }
            }
        }
        return count;
    }

    /**
     * 给添加的模型添加ID
     *
     * @param individualList:添加的模型list
     * @param model
     */

    public static void setIDToAddModel(ArrayList<OWLIndividual> individualList, OWLModel model) {
        OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
        Iterator its = individualList.iterator();
        int count = 0;
        while (its.hasNext()) {
            count++;
            OWLIndividual addModelValue = (OWLIndividual) its.next();
            String modelIdStr = "addModelID" + count;
            addModelValue.setPropertyValue(modelIDProperty, modelIdStr);

        }
    }

    /**
     * 根据规则和主题的名字来选择运行哪类规则
     *
     * @param maName：ma文件的名字
     * @param topicName：主题的名字
     * @param model
     * @return
     * @throws SWRLRuleEngineException
     * @throws SWRLFactoryException
     * @throws IOException
     * @throws SecurityException
     */

    public static OWLModel processSWRL(String maName, String topicName, OWLModel model,
                                       ArrayList<String> englishTemplate)
            throws SWRLRuleEngineException, SWRLFactoryException, SecurityException, IOException {

        if (maName == "empty.ma") {
            // 当选择的ma是empty.ma,时，首先看看是不是由模板可以往empty.ma中添加对应的模型
            boolean isOK = SWRLMethod.executeSWRLEnginetoEmptyMa(model, "addModelToEmpty.ma", topicName,
                    englishTemplate);
            // 将原子所对应的模型添加到空场景中
            if (englishTemplate.size() > 0) {
                ArrayList<String> modelValues = new ArrayList();
                ArrayList<String> effectValues = new ArrayList();
                ArrayList<String> allValues = new ArrayList();
                OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
                OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
                OWLObjectProperty hasPutObjectProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
                OWLIndividual emptyIndividual = model.getOWLIndividual("empty.ma");
                OWLIndividual emptyGroundIndividual = model.getOWLIndividual("emptySceneSpaceA");
                OWLIndividual emptyAirIndividual = model.getOWLIndividual("emptySceneSpaceB");
                OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");
                for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); )// 遍历所有的模板原子
                {
                    String templateAllName = its.next();
                    int iPostion = templateAllName.indexOf(":");
                    // String
                    // templateAutmName=templateAllName.substring(iPostion+1,
                    // templateAllName.length());
                    String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length() - 4);
                    OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);

                    if (templateIndividual != null)// 查看模板原子所对应的实例是否存在
                    // if(!templateIndividual.equals(null))//查看模板原子所对应的实例是否存在
                    {
                        int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
                        if (valueNum > 0)// 对应的model数量是否大于0
                        {
                            Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);

                            for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                                String value = its2.next().getBrowserText();
                                allValues.add(value);
                                if (value.contains(".ma"))
                                    modelValues.add(value);
                                else {
                                    effectValues.add(value);
                                    OWLIndividual effectIndividual = model.getOWLIndividual(value);
                                    effectIndividual.setPropertyValue(degreeProperty, "normal");
                                }
                            }
                        }
                    }

                }
                emptyGroundIndividual.setPropertyValues(hasPutObjectProperty, modelValues);
                emptyAirIndividual.setPropertyValues(hasPutObjectProperty, effectValues);
                emptyIndividual.setPropertyValues(addToMaProperty, allValues);

            }

        } else if (maName != "nothing.ma" && maName != "empty.ma") {

            // 添加hasMa,hasTopic属性值，也包括跟模板原子有关的规则
            if (bIsBackgroundScene) {
                logger.info("运行往背景场景中添加模型的规则");
                SWRLMethod.executeTemplateToBackgroundSceneSWRLEngine(model, englishTemplate);
                try {
                    try {
                        maName = getMaThroughBack(model, maName);
                    } catch (Exception exGetBack) {
                        maName = getMaThroughBack(model, maName);
                    }
                } catch (Exception exGetBack2) {
                    System.out.print("ERROR: exGetBack");
                }
            } else {

                SWRLMethod.executeTemplateToBackgroundSceneSWRLEngine(model, englishTemplate);
            }

        }
        if (bIsBackgroundScene) {
            SWRLMethod.changeBackgroundPictureSky(model, maName, englishTemplate);
        }

        return model;

    }

    /**
     * 输出摄像机版本2，
     *
     * @param doc
     * @param model
     * @param copyIndividual
     * @return
     */
    public static Document printCameraVersion2(Document doc, OWLModel model, OWLIndividual copyIndividual) {
        Element rootName = doc.getRootElement();
        Element name = rootName.element("maName");
        OWLNamedClass addModelClass = model.getOWLNamedClass("AddModelRelated");
        if (addModelClass.getInstanceCount() == 0)
            return doc;
        else {
            OWLNamedClass parentClassName = getClassFromIndividual(model, copyIndividual);
            Collection parentClassList = parentClassName.getSuperclasses(true);
            Iterator its = parentClassList.iterator();
            boolean isBackgroundScene = false;// first：所限判断是否是背景场景
            while (its.hasNext()) {
                Object obj = its.next();
                if (obj.toString().contains("NamedClass")) {
                    RDFResource parentClass = (RDFResource) obj;
                    String temp = parentClass.getBrowserText();
                    if (parentClass.getBrowserText().equals("BackgroundScene")) {
                        isBackgroundScene = true;
                        break;
                    }
                }
            }

            OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
            ArrayList<String> allList = new ArrayList();
            ArrayList<String> humanList = new ArrayList();
            Collection addModelList = addModelClass.getInstances();
            Iterator its1 = addModelList.iterator();
            while (its1.hasNext()) {
                OWLIndividual addModel = (OWLIndividual) its1.next();
                allList.add(addModel.getBrowserText());
                if (addModel.getPropertyValue(addModelTypeProperty).toString().equals("people"))
                    humanList.add(addModel.getBrowserText());
            }

            if (isBackgroundScene) {
                Element ruleName = name.addElement("rule");
                ruleName.addAttribute("ruleType", "Camera");
                String perName = "name";
                int count = 0;
                for (Iterator its4 = allList.iterator(); its4.hasNext(); ) {
                    count++;
                    String modelName = (String) its4.next();
                    String fullName = perName + count;
                    ruleName.addAttribute(fullName, modelName);
                }

            } else {
                if (humanList.size() > 0) {
                    Element ruleName = name.addElement("rule");
                    ruleName.addAttribute("ruleType", "Camera");
                    String perName = "name";
                    int count = 0;
                    for (Iterator its4 = humanList.iterator(); its4.hasNext(); ) {
                        count++;
                        String modelName = (String) its4.next();
                        String fullName = perName + count;
                        ruleName.addAttribute(fullName, modelName);
                    }
                }
            }
        }

        return doc;
    }

    /**
     * 输出摄像机类
     *
     * @param doc
     * @param model
     * @param copyIndividual
     * @return
     */
    public static Document printCamera(Document doc, OWLModel model, OWLIndividual copyIndividual) {
        Element rootName = doc.getRootElement();
        Element name = rootName.element("maName");
        OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
        OWLObjectProperty hasRelationProperty = model.getOWLObjectProperty("hasRelation");
        OWLObjectProperty hasHumanProperty = model.getOWLObjectProperty("hasHuman");
        OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
        OWLNamedClass parentClassName = getClassFromIndividual(model, copyIndividual);
        Collection parentClassList = parentClassName.getSuperclasses(true);
        Iterator its = parentClassList.iterator();
        boolean isBackgroundScene = false;// first：所限判断是否是背景场景
        while (its.hasNext()) {
            Object obj = its.next();
            if (obj.toString().contains("NamedClass")) {
                RDFResource parentClass = (RDFResource) obj;
                String temp = parentClass.getBrowserText();
                if (parentClass.getBrowserText().equals("BackgroundScene")) {
                    isBackgroundScene = true;
                    break;
                }
            }
        }
        if (isBackgroundScene && copyIndividual.getPropertyValueCount(addToMaProperty) > 0) {// 如果是空场景，则打印空场景中addToMa所有的值
            Element ruleName = name.addElement("rule");
            ruleName.addAttribute("ruleType", "Camera");
            String perName = "name";
            int count = 0;
            Collection addToMaList = copyIndividual.getPropertyValues(addToMaProperty);
            Iterator its1 = addToMaList.iterator();
            while (its1.hasNext()) {
                count++;
                OWLIndividual individualValue = (OWLIndividual) its1.next();
                String fullName = perName + count;
                ruleName.addAttribute(fullName, individualValue.getBrowserText());
            }
            return doc;

        } else {// 不是背景场景的话，则要考虑hasmodel和addToMa里面的人物
            ArrayList<String> humanList = new ArrayList();

            // 判断添加属性
            if (copyIndividual.getPropertyValueCount(addToMaProperty) > 0) {
                Collection hasmodelList = copyIndividual.getPropertyValues(addToMaProperty);
                for (Iterator its2 = hasmodelList.iterator(); its2.hasNext(); ) {
                    OWLIndividual indicidualV = (OWLIndividual) its2.next();
                    OWLNamedClass parentClassName1 = getClassFromIndividual(model, indicidualV);
                    Collection parentClassList1 = parentClassName1.getSuperclasses(true);
                    Iterator its3 = parentClassList1.iterator();
                    while (its3.hasNext()) {
                        Object obj = its3.next();
                        if (obj.toString().contains("NamedClass")) {
                            RDFResource parentClass = (RDFResource) obj;
                            String temp = parentClass.getBrowserText();
                            if (parentClass.getBrowserText().equals("p1:Woman")
                                    || parentClass.getBrowserText().equals("p1:Man")) {
                                humanList.add(indicidualV.getBrowserText());
                                break;

                            }
                        }
                    }
                }
            }
            if (humanList.size() > 0) {
                // 判断hasmodel属性
                if (copyIndividual.getPropertyValueCount(hasRelationProperty) > 0) {
                    Collection hasmodelList = copyIndividual.getPropertyValues(hasRelationProperty);
                    for (Iterator its2 = hasmodelList.iterator(); its2.hasNext(); ) {
                        OWLIndividual indicidualV = (OWLIndividual) its2.next();
                        if (indicidualV.getPropertyValueCount(hasHumanProperty) > 0) {
                            Collection hasHumanList = indicidualV.getPropertyValues(hasHumanProperty);
                            for (Iterator its3 = hasHumanList.iterator(); its3.hasNext(); ) {
                                OWLIndividual indicidualV2 = (OWLIndividual) its3.next();
                                OWLNamedClass parentClassName1 = getClassFromIndividual(model, indicidualV2);
                                Collection parentClassList1 = parentClassName1.getSuperclasses(true);
                                Iterator its4 = parentClassList.iterator();
                                while (its4.hasNext()) {
                                    Object obj = its4.next();
                                    if (obj.toString().contains("NamedClass")) {
                                        RDFResource parentClass = (RDFResource) obj;
                                        String temp = parentClass.getBrowserText();
                                        if (parentClass.getBrowserText().equals("p1:Woman")
                                                || parentClass.getBrowserText().equals("p1:Man")) {
                                            humanList.add(indicidualV2.getBrowserText());
                                            break;

                                        }
                                    }
                                }
                            }
                        }

                    }
                }

                Element ruleName = name.addElement("rule");
                ruleName.addAttribute("ruleType", "Camera");
                String perName = "name";
                int count = 0;
                for (Iterator its4 = humanList.iterator(); its4.hasNext(); ) {
                    count++;
                    String modelName = (String) its4.next();
                    String fullName = perName + count;
                    ruleName.addAttribute(fullName, modelName);
                }
                return doc;
            }
        }

        return doc;
    }

    /**
     * 通过主题选音乐music,
     *
     * @param englishTopic:主题
     * @param model
     * @param doc
     * @return
     * @throws IOException
     * @throws SecurityException
     */
    public static String getMusic(String englishTopic, OWLModel model) throws SecurityException, IOException {
        OWLIndividual chooseMusic = null;
        String choosedMusic = "";
        OWLObjectProperty hasMusicProperty = model.getOWLObjectProperty("hasMusic");
        Random rand = new Random();
        Date date = new Date();

        if (englishTopic.equals("") || englishTopic == null)// 没有选到主题
        {
            OWLNamedClass commonMusicClass = model.getOWLNamedClass("CommonMusic");
            if (commonMusicClass.getInstanceCount() != 0) {
                Collection musicClass = commonMusicClass.getInstances();
                ArrayList<OWLIndividual> musicIndividual = new ArrayList();
                for (Iterator itInstance = musicClass.iterator(); itInstance.hasNext(); ) {
                    OWLIndividual individual1 = (OWLIndividual) itInstance.next();
                    musicIndividual.add(individual1);
                }
                rand.setSeed(date.getTime());
                int kk = rand.nextInt(musicIndividual.size());
                chooseMusic = musicIndividual.get(kk);
            }
        } else {
            OWLNamedClass englishTopicClass = model.getOWLNamedClass(englishTopic);
            RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMusicProperty);
            logger.info("通过主题来获得主题所对应的ma场景的名字，该主题所对应的音乐类名：" + resource.getBrowserText());
            String hasValues = resource.getBrowserText();// 获得主题对应的音乐的类名
            String[] hasValuesSplit = hasValues.split("or");// 可能对应多个音乐类
            ArrayList<String> hasValuesClass = new ArrayList();
            OWLNamedClass resourceClass = null;
            if (hasValuesSplit.length > 1) {// 当有多个音乐类时，先判断每个音乐类是否都有实例
                for (int i = 0; i < hasValuesSplit.length; i++) {
                    OWLNamedClass resourceClass0 = model.getOWLNamedClass(hasValuesSplit[i].trim());
                    int instanceCount0 = resourceClass0.getInstanceCount();
                    if (instanceCount0 > 0)
                        hasValuesClass.add(hasValuesSplit[i].trim());
                }
                if (hasValuesClass.size() > 0)// 当多个音乐类都有实例时，则随机选择一个
                {
                    rand.setSeed(date.getTime());
                    int kk = rand.nextInt(hasValuesClass.size());
                    resourceClass = model.getOWLNamedClass(hasValuesClass.get(kk));
                } else// 当多个音乐类都没有实例时，则随机选择一个长场景类
                {
                    int kk = rand.nextInt(hasValuesSplit.length);
                    resourceClass = model.getOWLNamedClass(hasValuesSplit[kk].trim());

                }
            } else
                // 处理只有一个场景类的情况
                resourceClass = model.getOWLNamedClass(hasValuesSplit[0].trim());
            int instanceCount = resourceClass.getInstanceCount();
            if (instanceCount != 0) {
                Collection musicClass = resourceClass.getInstances();
                ArrayList<OWLIndividual> musicIndividual = new ArrayList();
                for (Iterator itInstance = musicClass.iterator(); itInstance.hasNext(); ) {
                    OWLIndividual individual1 = (OWLIndividual) itInstance.next();
                    musicIndividual.add(individual1);
                }
                rand.setSeed(date.getTime());
                int kk = rand.nextInt(musicIndividual.size());
                chooseMusic = musicIndividual.get(kk);
            }
        }
        if (chooseMusic != null)
            choosedMusic = chooseMusic.getBrowserText();
        if (choosedMusic.startsWith("a00"))
            choosedMusic = choosedMusic.substring(1);
        return choosedMusic;
    }

    /**
     * 打印类
     *
     * @param model
     * @throws IOException
     * @throws SWRLRuleEngineException
     * @throws OntologyLoadException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws SWRLFactoryException
     */
    public static void printToXML(OWLModel model, String maName, String topicName, String strNegType)
            throws SWRLRuleEngineException, IOException, OntologyLoadException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, SWRLFactoryException {
        // OWLModel owlModel=copyMaIndividual(maName,model,topicName);
        String xmlPath = XMLInfoFromIEDom4j.writeXML("adl_result.xml");
        Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);// 获得要输出的XML文件的头部
        Element rootElement = doc.getRootElement();
        Element name = rootElement.addElement("maName");//添加maName节点
        OWLIndividual copyIndividual = model.getOWLIndividual(maName);//场景对象
        OWLDatatypeProperty maSenceNameProperty = model.getOWLDatatypeProperty("maSceneName");//对象属性maSceneName
        OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");//获得数据属性topicName

        // String
        // maName=(String)copyIndividual.getPropertyValue(maSenceNameProperty);
        // String
        // topicClassName=(String)copyIndividual.getPropertyValue(topicNameProperty);
        name.addAttribute("name", maName);//给maName节点添加值“name=场景名称”
        name.addAttribute("topic", topicName);//给maName节点添加值“topic=选定主题”
        String musicName = getMusic(topicName, model);
        name.addAttribute("music", musicName);//给maName节点添加值“music=通过主题选定的音乐”

//		if (maName.equals("nothing.ma"))// 2017.5.31；产生错误短信8042—8044后添加
//			name.addAttribute("maFrame", "300");

        // *******************打印添加规则*****************
        OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");//对象属性addToMa

        doc = printCycleAddRuleVersion2(doc, model, copyIndividual, englishTemplate, colorModelNum, colorChangeAttr);
        // 打印添加规则
        doc = printExchangeRule(doc, model, copyIndividual);
        doc = printDeleteRule(doc, model, copyIndividual);
        doc = printTimeToClock(doc, model, copyIndividual, englishTemplate);

        // doc=printCameraVersion2(doc,model,copyIndividual);
        // doc=printNegType(doc,strNegType);
        // doc=printBackgroundPicture(doc,model,copyIndividual);

        String individualName = maName;
        /* 各部分整合起来 */
        String uri = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
        // String uri = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
        boolean flage = false;
        boolean flage1 = false;
        boolean flage2 = false;
        boolean ifInterFlage = false;
        ArrayList expressiontopic = new ArrayList();
        expressiontopic = topiclist;

        ArrayList scene = new ArrayList();//存放场景的hasSceneSpace属性值
        OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");//对象属性
        if (copyIndividual.getPropertyValueCount(hasSceneSpace) != 0) {//如果场景有hasSceneSpace属性
            if (copyIndividual.getPropertyValueCount(hasSceneSpace) == 1) {//如果场景只有一个hasSceneSpace属性
                scene.add(copyIndividual.getPropertyValue(hasSceneSpace));
            } else if (copyIndividual.getPropertyValueCount(hasSceneSpace) > 1) {//场景有多个hasSceneSpace属性
                Collection clo = copyIndividual.getPropertyValues(hasSceneSpace);//获取场景对应的多个hasSceneSpace
                for (Iterator it = clo.iterator(); it.hasNext(); ) {//遍历场景的hasSceneSpace，添加进scene
                    scene.add(it.next());
                }
            }
        }
        for (int k = 0; k < scene.size(); k++) {//遍历场景的多个hasSceneSpace属性，查看属性值对应的类是否有包含“Ground”
            OWLIndividual scenespace = (OWLIndividual) scene.get(k);
            OWLNamedClass place = (OWLNamedClass) scenespace.getDirectType();//获取属性对应的类
            String s = place.getBrowserText().toString();
            if (s.contains("Ground")) {//查看是否包含地面
                flage2 = true;
                break;

            }
        }
        if (flage2 == false) {
            logger.info("没有地面可用空间");
        }


        // 20170508 Yangyong

        if (!maName.equals("clock.ma") && flage2 == true) {//如果不是时钟场景而且有地面空间可用

            try {
                //20171110Yangyong
                logger.info("InterAction begin");
                Random r = new Random();
                int random = r.nextInt(6) + 1;
                System.out.println("random:" + random);
                InterAction interaction = new InterAction();
                String interflage = interaction.hasInterActionInfer(actionTemplateAttr, owlModel, maName, doc);
                if (interflage.equals("interActionIsOk") && (random != 6)) {
                    //					ifInterFlage=true;

                    doc = interaction.InterActionInfer(actionTemplateAttr, owlModel, maName, doc);
                }
                //现阶段测试使用，用于给抽出单个人物的动画添加人物，使其完成交互运动
//					if(interflage.equals("peopleNotEnough")){
//						Plot.setIndividualToInteraction(null, null, owlModel, maName, doc);
//						doc = interaction.InterActionInfer(actionTemplateAttr, owlModel, maName, doc);
//
//					}
                logger.info("InterAction end");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ERROR: InterAction Exception");
            }
//			System.out.println(ifInterFlage);
//			 if(ifInterFlage==false){//如果没有交互动作，则进行

            try {
                logger.info("===========Event begin,动作模板：" + actionTemplateAttr + "=======================");
                Plot plotplan = new Plot();
//				System.out.println("topiclist=" + topiclist);
//				System.out.println("actionTemplate=" + actionTemplateAttr);

                doc = plotplan.EventInfer(topiclist, actionTemplateAttr, owlModel, maName, doc);
                flage1 = plotplan.ifContainEvent();
                System.out.println("是否抽到对应的事件：" + flage1);
                logger.info("===========Event finish=======================");
            } catch (Exception e) {
                flage = true;
                e.printStackTrace();
                logger.info("ERRPR: Event Exception");
            }

            // }
            // else{
            // flage=true;
            // }

            ///////////////////////////////////////////////////////////////////////////////////
            try {
                logger.info("--------------------------------------changeMaFrame begin------------------------------------");
                doc = Plot.changeMaFrame(owlModel, maName, doc);
                logger.info("--------------------------------------changeMaFrame end--------------------------------------");
                String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
                saveOWLFile((JenaOWLModel) model, fileName);
                logger.info("--------------------------------------changeMaFrame finally--------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ERROR: Frame Exception");
            }

            ///////////////////////////////////////////////////////////////////////////////////
            // QIU p2
            if (flage == true || flage1 == false) {
                try {
                    logger.info("Action begin");

                    Action action = new Action();
                    ArrayList actionTemplate = new ArrayList();

                    ArrayList list = windRainSnowNeedAttr.get(0);

                    System.out.println("actionTemplateAttr" + actionTemplateAttr);
                    doc = action.actionInfer(actionTemplateAttr, owlModel, individualName, doc);
                    logger.info("Action finish");
                } catch (Exception exQiu) {
                    logger.info("ERROR: Action Exception");
                }
            }
            //赵檬
            try {
                logger.info("Expression begin,模板名称：" + ExpressionList);
                Expression shock = new Expression();
//				System.out.println("XUXH需要实例：" + individualName + "模板名称：" + ExpressionList);
//				System.out.println(topiclist.size());
                //	doc = shock.ShockXml(ExpressionList, expressiontopic, model, maName, doc);//1024，切换成下行代码；
                doc = shock.ShockXml(ExpressionList, model, maName, doc);
                logger.info("Expression finish");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ERROR: Expression Exception");
            }

        } else {//添加帧数
            OWLIndividual indOfmaName = model.getOWLIndividual(maName);
            name.addAttribute("maFrame", String.valueOf(Plot.getFrame(model, indOfmaName)));
        }

        /////////////////////// end
        // zheng p6
        try {
            logger.info("deform begin");
            // String
            // urlx="file:///C:/ontologyOWL/AllOwlFile/zhenOWL/10.1(test).owl";
            // OWLModel owlModelx =ProtegeOWL.createJenaOWLModelFromURI(urlx);
            CRebuild deform = new CRebuild();
            doc = deform.FinalOutPut(owlModel, individualName, moodTemplateAttr, doc);
            logger.info("deform finish");
        } catch (Exception exZheng) {
            logger.info("ERROR: deform Exception:" + exZheng.getMessage());
        }
        //////
        // zhao
        ArrayList ss = new ArrayList();
        for (int ii = 0; ii < SeasonList.size(); ii++) {
            String str = SeasonList.get(ii);
            String str2 = str.substring(0, str.indexOf("Template"));
            ss.add(str2);
        }
        System.out.println("seasonlist=" + ss);
        System.out.println("modelWithColors=" + modelWithColors + "and" + modelWithColor);
        try {
            logger.info("Color begin");
            MaToXML usez = new MaToXML();
            doc = usez.setColorAndLight(owlModel, individualName, doc, modelWithColors, SeasonList);
            logger.info("Color finish");
        } catch (Exception exZhao) {
            logger.info("ERROR: Color Exception");
        }
        //////////////////////////////////////////////////////////////////////////////
        // jiali
        try {
            // System.out.println("模板"+moodTemplateAttr.get(0));
            logger.info("fog begin");

            fogInsert tt = new fogInsert();

            doc = tt.fogInfer(weatherAndmoodAttr, owlModel, individualName, doc);
            logger.info("fog finish");
        } catch (Exception exJiali) {
            logger.info("ERROR: fog Exception");
        }
        ////////////////////////////////////////////////////////////////////////
        // 林海华加风的程序 p2
        WRSGernate(owlModel, individualName, doc);

        try {

            logger.info("Light begin");
            LightInsert light = new LightInsert();
            // doc=light.LightInfer(englishTemplate, model, maName,doc);
            doc = light.LightInfer(LightList, model, individualName, doc);

            logger.info("Light finish");
        } catch (Exception exHL) {
            logger.info("ERROR: Light Exception");
        }

        ////////////////////////////////////////////////////////////////////////
        // nidejuan布局 p
        try {
            logger.info("Layout begin");
            // String
            // urlNDJ="file:///C:/ontologyOWL/AllOwlFile/Layout/sumo_phone3.owl";
            // OWLModel
            // owlModelndj=ProtegeOWL.createJenaOWLModelFromURI(urlNDJ);
            Layout lo = new Layout();

            doc = lo.setLayout(owlModel, individualName, doc);
            logger.info("Layout finish");
        } catch (Exception nidejuan) {
            logger.info("ERROR: Layout Exception");
        }
        ///////////////////////////////////////////////////////////////////////////////////
        // 刘畅 标号p5

        // 罗明 烟花，2017.11.07修改程序
//		try {
//
//			logger.info("Firework begin");
//			FireworkInsert firework = new FireworkInsert();
//			doc = firework.fireworkInfer(englishTemplate, model, individualName, doc);
//
//			logger.info("Firework finish");
//		} catch (Exception exHL) {
//			logger.info("ERROR: Firework Exception");
//		}
        // 海洋 刘俊君
        try {

            logger.info("MakeBoatsCheck begin");
            MakeBoats makeBoats = new MakeBoats();
            doc = makeBoats.makeBoatsInfer(englishTemplate, model, individualName, doc);
            logger.info("MakeBoatsCheck end");
        } catch (Exception ex) {
            logger.info("Error: MakeBoatsCheck Exception.");

        }

        //材质  李晶
        try {
            logger.info("newMaterialCheck begin");
            newMaterial newMaterial = new newMaterial();
            doc = newMaterial.NewMaterialInfer(englishTemplate, model, individualName, doc);
            logger.info("newMaterial end");
        } catch (Exception ex) {
            logger.info(ex);
            logger.info("Error: newMaterialCheck Exception.");
        }

        try {
            logger.info("Camera begin");
            // String uri =
            // "file:///C:/ontologyOWL/AllOwlFile/zhaoOWL/ColorAndLight.owl";
            // OWLModel owlModel=ProtegeOWL.createJenaOWLModelFromURI(uri);
            // String fileName = "3688.xml";
            System.out.println("摄像机实例" + maName);
            CameraToXML chang = new CameraToXML();
            doc = chang.CreateCamera(owlModel, maName, doc);
            // chang.doc2XmlFile(doc, fileName);
            logger.info("Camera finish");

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ERROR: Camera Exception");
        }

        /////////////////////////////////////////////////////////////////////////////////
        boolean yesNo = XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
        model = null;
        System.gc();
    }

    /**
     * 将林海华的抽取出来，作为最终选取weatherable时的依据，如果能添加天气，则weatherable设为true否则设为false
     * owlModel 在ALLOWLFile中的sumowl2
     *
     * @param individualName 选取的场景的名字
     * @param doc            生成的ADL文档
     */

    public static void WRSGernate(OWLModel owlModel, String individualName, Document doc) {
        // 林海华加风的程序 p2
        ArrayList<String> list = new ArrayList();
        for (int i = 0; i < windRainSnowNeedAttr.size(); i++) {

            for (int j = 0; j < windRainSnowNeedAttr.get(i).size(); j++) {
                list.add(windRainSnowNeedAttr.get(i).get(j).toString());
            }

        }
        System.out.println(list);
        try {

            logger.info("WindAndRainAndSnow begin");
            Effect effect = new Effect();
            Document document1 = effect.runEffect(list, owlModel, individualName, doc);
            logger.info("WindAndRainAndSnow finish");
        } catch (Exception exLHH) {
            logger.info("ERROR:WindAndRainAndSnow Exception");
        }

    }

    public static Document printNegType(Document doc, String strNegType) {
        String ma = "SP_" + maName.substring(0, maName.length() - 3) + "_A";
        if (strNegType.equals(""))
            return doc;
        else {
            Element rootName = doc.getRootElement();
            Element name = rootName.element("maName");
            Element ruleName = name.addElement("rule");
            ruleName.addAttribute("ruleType", "addToMa");
            ruleName.addAttribute("addModel", "M_NoModel.ma");
            ruleName.addAttribute("spaceName", ma);
            ruleName.addAttribute("type", "noModel");
            ruleName.addAttribute("degree", "");
            ruleName.addAttribute("number", "1");
            ruleName.addAttribute("addModelID", "NoModelId");
            return doc;
        }
    }

    /**
     * 当IE抽取有抽到时间时，则往clock_clock这个模型上添加时间
     *
     * @param doc            ADL输出
     * @param model
     * @param copyIndividual 场景对象
     * @param templateAttr   英文模板
     * @return
     */
    public static Document printTimeToClock(Document doc, OWLModel model, OWLIndividual copyIndividual,
                                            ArrayList<String> templateAttr) {
        if (templateAttr.size() == 0)//如果英文模板为空，直接返回
            return doc;
        else {
            Iterator its = templateAttr.iterator();
            boolean hasTime = false;
            String strTime = "";
            while (its.hasNext()) {//遍历英文模板，看是否有时间模板
                String str = (String) its.next();
                if (str.equals("时间")) {//这尼玛遍历的英文模板，能跟中文对的上
                    hasTime = true;
                    strTime = (String) its.next();
                    break;
                }
            }
            if (!hasTime)//如果没有时间，则返回
                return doc;
            else {

                int iPostion = strTime.indexOf(":");
                String timeNodeName = strTime.substring(0, iPostion);
                String subStrTimeName = strTime.substring(iPostion + 1);
                Element rootName = doc.getRootElement();
                Element name = rootName.element("maName");

                if (copyIndividual.getBrowserText().equals("clock.ma") && timeNodeName.equals("时分秒")) {
                    Element ruleName = name.addElement("rule");
                    ruleName.addAttribute("ruleType", "addTimeToMa");
                    ruleName.addAttribute("usedModelInMa", "clock_clock.ma");
                    ruleName.addAttribute("addTime", subStrTimeName);
                    ruleName.addAttribute("type", "time");

                } else {
                    OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");
                    Collection spaceList = copyIndividual.getPropertyValues(hasSceneSpaceProperty);
                    if (spaceList.size() == 0)
                        return doc;
                    else {
                        Iterator its1 = spaceList.iterator();
                        OWLNamedClass spaceParent = null;
                        boolean hasGroundSpace = false;
                        OWLIndividual spaceName = null;
                        while (its1.hasNext()) {
                            spaceName = (OWLIndividual) its1.next();
                            spaceParent = getClassFromIndividual(model, spaceName);
                            if (spaceParent.getBrowserText().equals("PlaneSceneSpaceIntsideRoomOnGround")
                                    || spaceParent.getBrowserText().equals("PlaneSceneSpaceOutsideRoomOnGround")) {
                                hasGroundSpace = true;
                                break;
                            }
                        }
                        if (hasGroundSpace && timeNodeName.equals("时分秒")) {
                            Element ruleName1 = name.addElement("rule");
                            ruleName1.addAttribute("ruleType", "addToMa");
                            ruleName1.addAttribute("addModel", "clock_clock.ma");
                            ruleName1.addAttribute("spaceName", spaceName.getBrowserText());
                            ruleName1.addAttribute("degree", "");
                            ruleName1.addAttribute("type", "model");
                            ruleName1.addAttribute("number", "1");

                            Element ruleName = name.addElement("rule");
                            ruleName.addAttribute("ruleType", "addTimeToMa");
                            ruleName.addAttribute("usedModelInMa", "clock_clock.ma");
                            ruleName.addAttribute("addTime", subStrTimeName);
                            ruleName.addAttribute("type", "time");
                        }
                    }
                }
                return doc;
            }
        }
    }

    /**
     * 打印删减类规则
     *
     * @param doc
     * @param model
     * @param copyIndividual
     * @return
     */
    public static Document printDeleteRule(Document doc, OWLModel model, OWLIndividual copyIndividual) {
        Element rootName = doc.getRootElement();
        Element name = rootName.element("maName");
        OWLObjectProperty subtractFromMaProperty = model.getOWLObjectProperty("subtractFromMa");
        OWLObjectProperty usedModelProperty = model.getOWLObjectProperty("usedModelInMa");
        if (copyIndividual.getPropertyValueCount(subtractFromMaProperty) > 0) {
            Collection deleteValues = copyIndividual.getPropertyValues(subtractFromMaProperty);
            for (Iterator its = deleteValues.iterator(); its.hasNext(); ) {
                OWLIndividual values = (OWLIndividual) its.next();
                boolean isDelete = false;
                if (copyIndividual.getPropertyValueCount(usedModelProperty) > 0) {
                    Collection usedValue = copyIndividual.getPropertyValues(usedModelProperty);
                    for (Iterator usedV = usedValue.iterator(); usedV.hasNext(); ) {
                        OWLIndividual uv = (OWLIndividual) usedV.next();
                        if (values.getBrowserText().equals(uv.getBrowserText())) {
                            isDelete = true;
                            break;
                        }
                    }
                }
                if (!isDelete) {// 确保删除的模型没有在更改规则中使用
                    Element ruleName = name.addElement("rule");
                    ruleName.addAttribute("ruleType", "deleteFromMa");// type="model"
                    ruleName.addAttribute("type", "model");// type="model"
                    ruleName.addAttribute("usedModelID", values.getBrowserText());// type="model"
                }

            }
        }
        return doc;

    }

    public static Document printBackgroundPicture(Document doc, OWLModel model, OWLIndividual copyIndividual) {
        OWLObjectProperty changeBackgroundPictureProperty = model.getOWLObjectProperty("changeBackgroundPicture");

        Random rand = new Random();
        Date date = new Date();
        rand.setSeed(date.getTime());
        if (copyIndividual.getPropertyValueCount(changeBackgroundPictureProperty) > 0) {
            Collection pictureList = copyIndividual.getPropertyValues(changeBackgroundPictureProperty);
            int size = pictureList.size();
            if (bIsBackgroundScene && size > 0) {
                ArrayList<OWLIndividual> pictureArryList = new ArrayList();
                for (Iterator itss = pictureList.iterator(); itss.hasNext(); ) {
                    OWLIndividual fatherV = (OWLIndividual) itss.next();
                    pictureArryList.add(fatherV);
                }
                int k = rand.nextInt(pictureArryList.size());
                String backgroundPictureName = pictureArryList.get(k).getBrowserText();
                Element rootName = doc.getRootElement();
                Element name = rootName.element("maName");
                Element ruleName = name.addElement("rule");
                ruleName.addAttribute("ruleType", "changeBackgroundPicture");
                ruleName.addAttribute("pictureType", "3");
                ruleName.addAttribute("changeName", "0");
                ruleName.addAttribute("pictureName", backgroundPictureName);

            }
            return doc;
        } else {
            int kk = rand.nextInt(2);
            if (kk == 0) {
                logger.info("通过随机选择，不改变背景图片");
                return doc;
            } else {
                OWLObjectProperty hasBackgroundPictureProperty = model.getOWLObjectProperty("hasBackgroundPicture");
                OWLDatatypeProperty backgroundPictureChangeNameProperty = model
                        .getOWLDatatypeProperty("backgroundPictureChangeName");
                OWLDatatypeProperty backgroundPictureTypeProperty = model
                        .getOWLDatatypeProperty("backgroundPictureType");
                if (copyIndividual.getPropertyValueCount(hasBackgroundPictureProperty) > 0) {
                    logger.info("随机选择，改变背景图片，并有背景图片改变");
                    Element rootName = doc.getRootElement();
                    Element name = rootName.element("maName");
                    Collection hasBackgroundPictureValues = copyIndividual
                            .getPropertyValues(hasBackgroundPictureProperty);
                    ArrayList<OWLIndividual> fInstance = new ArrayList();
                    OWLIndividual lastFatherInstance = null;
                    for (Iterator itss = hasBackgroundPictureValues.iterator(); itss.hasNext(); ) {
                        OWLIndividual fatherV = (OWLIndividual) itss.next();
                        fInstance.add(fatherV);
                    }
                    rand.setSeed(date.getTime());
                    int k = rand.nextInt(fInstance.size());
                    String backgroundPictureName = fInstance.get(k).getBrowserText();
                    String pictureChangeName = copyIndividual.getPropertyValue(backgroundPictureChangeNameProperty)
                            .toString();
                    String pictureChangeType = copyIndividual.getPropertyValue(backgroundPictureTypeProperty)
                            .toString();
                    Element ruleName = name.addElement("rule");
                    ruleName.addAttribute("ruleType", "changeBackgroundPicture");
                    if (bIsBackgroundScene) {
                        ruleName.addAttribute("pictureType", "3");
                        ruleName.addAttribute("changeName", "0");
                    } else {
                        ruleName.addAttribute("pictureType", pictureChangeType);
                        ruleName.addAttribute("changeName", pictureChangeName);
                    }
                    ruleName.addAttribute("pictureName", backgroundPictureName);
                    return doc;
                } else {
                    logger.info("随机选择改变背景图片，但所选的ma文件没有背景图片去改变");
                    return doc;
                }
            }
        }
    }

    /**
     * 用于打印更改规则
     *
     * @param doc
     * @param model
     * @param copyIndividual
     * @return
     */
    public static Document printExchangeRule(Document doc, OWLModel model, OWLIndividual copyIndividual) {
        Element rootName = doc.getRootElement();
        Element name = rootName.element("maName");
        OWLObjectProperty usedModelProperty = model.getOWLObjectProperty("usedModelInMa");
        OWLObjectProperty exchangedModelProperty = model.getOWLObjectProperty("exchangedModelInMa");
        OWLDatatypeProperty exchangedTypeProperty = model.getOWLDatatypeProperty("exchangedType");
        OWLObjectProperty modelForAddProperty = model.getOWLObjectProperty("modelForAdd");
        // 打印更改规则
        if (copyIndividual.getPropertyValueCount(usedModelProperty) > 0) {
            Collection usedModelValues = copyIndividual.getPropertyValues(usedModelProperty);
            if (usedModelValues.size() > 0) {
                for (Iterator iModel = usedModelValues.iterator(); iModel.hasNext(); ) {
                    OWLIndividual values = (OWLIndividual) iModel.next();
                    boolean isUsedForAdd = false;
                    if (modelForAddProperty.getPropertyValueCount(modelForAddProperty) > 0) {
                        Collection modelForAddValue = modelForAddProperty.getPropertyValues(modelForAddProperty);
                        for (Iterator modelAddV = modelForAddValue.iterator(); modelAddV.hasNext(); ) {
                            OWLIndividual mav = (OWLIndividual) modelAddV.next();
                            if (values.getBrowserText().equals(mav.getBrowserText())) {
                                isUsedForAdd = true;
                                break;
                            }
                        }
                    }
                    if (!isUsedForAdd) {// 即当要改变的这个模型没有在添加规则中有操作时，就对它进行相应的变化
                        if (values.getPropertyValueCount(exchangedTypeProperty) > 0) {
                            Collection typeValues = values.getPropertyValues(exchangedTypeProperty);
                            for (Iterator iType = typeValues.iterator(); iType.hasNext(); ) {
                                Object type = iType.next();
                                if (type.toString().equals("model")) {
                                    Collection modelValues = values.getPropertyValues(exchangedModelProperty);
                                    for (Iterator imodelV = modelValues.iterator(); imodelV.hasNext(); ) {
                                        OWLIndividual values2 = (OWLIndividual) imodelV.next();
                                        Element ruleName = name.addElement("rule");
                                        ruleName.addAttribute("ruleType", "exchangeToMa");// type="model"

                                        ruleName.addAttribute("usedModelID", values.getBrowserText());
                                        ruleName.addAttribute("addModel", values2.getBrowserText());
                                        ruleName.addAttribute("type", type.toString());

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        return doc;
    }

    /**
     * 通过联想的方法往空场景中添加物体，包括纵向的添加，也包括利用已有场景中的object来往空场景中添加
     *
     * @param model
     * @param individual
     * @return
     */
    public static ArrayList<OWLIndividual> processAddToEmptyMa(OWLModel model, OWLIndividual individual) {
        ArrayList<OWLIndividual> addIndividuals = new ArrayList();
        OWLObjectProperty hasSceneProperty = model.getOWLObjectProperty("hasScene");
        Random rand = new Random();
        Date dt = new Date();
        rand.setSeed(dt.getTime());
        if (individual.getPropertyValueCount(hasSceneProperty) > 0) {
            int kk = rand.nextInt(2);// 在0和1之间产生随机数
            if (kk == 0) {
                addIndividuals = processAddToEmptyMaThroughZongxiang(model, individual);
            } else
                addIndividuals = processAddToEmptyMaThroughHasScene(model, individual);
        } else
            addIndividuals = processAddToEmptyMaThroughZongxiang(model, individual);
        return addIndividuals;
    }

    /**
     * 通过纵向往empty.ma中添加object
     *
     * @param model
     * @param individual
     * @return
     */
    public static ArrayList<OWLIndividual> processAddToEmptyMaThroughZongxiang(OWLModel model,
                                                                               OWLIndividual individual) {
        ArrayList<OWLIndividual> addIndividuals = new ArrayList();
        OWLObjectProperty hasSceneProperty = model.getOWLObjectProperty("hasScene");
        OWLNamedClass className = getClassFromIndividual(model, individual);
        Collection fatherClassNameCollection = null;// 用来打印纵向的一些信息
        OWLNamedClass fatherClassName = null;
        Random rand = new Random();
        Date dt = new Date();
        rand.setSeed(dt.getTime());
        int parentFloorNum = rand.nextInt(2) + 1;// 随机选择一个父层次
        for (int i = 0; i < parentFloorNum; i++) {
            fatherClassNameCollection = className.getSuperclasses(false);
            System.out.println("father count:" + fatherClassNameCollection.size());
            int count = 0;
            for (Iterator fc = fatherClassNameCollection.iterator(); fc.hasNext(); ) {
                fatherClassName = (OWLNamedClass) fc.next();
                count++;
                if (count == 1)
                    break;

            }
            className = fatherClassName;

        }
        System.out.println("the father name:" + fatherClassName.getBrowserText());
        Collection fatherInstance = fatherClassName.getInstances(true);
        ArrayList<OWLIndividual> fInstance = new ArrayList();
        OWLIndividual lastFatherInstance = null;
        for (Iterator itss = fatherInstance.iterator(); itss.hasNext(); ) {
            OWLIndividual fatherV = (OWLIndividual) itss.next();
            fInstance.add(fatherV);
        }
        if (fInstance.size() > 0) {
            int fNum = rand.nextInt(fInstance.size()); // 随机选择父类中的某一实例
            lastFatherInstance = fInstance.get(fNum);
            addIndividuals.add(lastFatherInstance);
            if (lastFatherInstance.getPropertyValueCount(hasSceneProperty) > 0) {
                int kk2 = rand.nextInt(2);
                if (kk2 == 1)// 当随机数为1时，说明把此物体所属的场景的onject加入到空场景中
                {
                    ArrayList<OWLIndividual> hasSceneObject = processAddToEmptyMaThroughHasScene(model,
                            lastFatherInstance);
                    for (Iterator tt = hasSceneObject.iterator(); tt.hasNext(); )// 把场景中的某些物体加入到addIndividuals中；
                    {
                        OWLIndividual aa = (OWLIndividual) tt.next();
                        addIndividuals.add(aa);
                    }
                }
            }
        }
        return addIndividuals;
    }

    /**
     * 通过hasScene属性往empty.ma中添加object
     *
     * @param model
     * @param individual
     * @return
     */
    public static ArrayList<OWLIndividual> processAddToEmptyMaThroughHasScene(OWLModel model,
                                                                              OWLIndividual individual) {
        ArrayList<OWLIndividual> addIndividuals = new ArrayList();// 保存所有Scenes
        ArrayList<OWLIndividual> addIndividuals2 = new ArrayList();// 保存某一
        // 场景中的所有Object
        ArrayList<OWLIndividual> addIndividuals3 = new ArrayList();// 保存某一
        // 场景中的某些Object
        OWLObjectProperty hasSceneProperty = model.getOWLObjectProperty("hasScene");
        OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
        Random rand = new Random();
        Date dt = new Date();
        rand.setSeed(dt.getTime());
        Collection sceneIndividuals = individual.getPropertyValues(hasSceneProperty);
        for (Iterator its = sceneIndividuals.iterator(); its.hasNext(); ) {
            OWLIndividual individualss = (OWLIndividual) its.next();
            addIndividuals.add(individualss);
        }
        int kk = rand.nextInt(addIndividuals.size());
        OWLIndividual sceneName = addIndividuals.get(kk);
        if (sceneName.getPropertyValueCount(hasModelProperty) > 0) {
            Collection modelObject = sceneName.getPropertyValues(hasModelProperty);
            for (Iterator t2 = modelObject.iterator(); t2.hasNext(); ) {
                OWLIndividual sObject = (OWLIndividual) t2.next();
                addIndividuals2.add(sObject);
            }
        }
        if (addIndividuals2.size() > 0) {
            int kkk = rand.nextInt(addIndividuals2.size());
            int i = 0;
            while (i <= kkk) {
                int k4 = rand.nextInt(addIndividuals2.size());
                OWLIndividual object2 = addIndividuals2.get(k4);
                addIndividuals3.add(object2);
            }
        }

        return addIndividuals3;
    }

    /**
     * 重要：重点：被打印类调用：场景类型分三种情况处理：
     * 1.nothing：不做处理，doc直接返回
     * 2.empty：添加文字类模型且不添加其他模型，即给场景添加文字，然后返回doc
     *
     * @param doc             ADL输出文档，maName节点还有“maFrame=XX”没有打印上
     * @param model           模型对象
     * @param copyIndividual  场景实体对象
     * @param englishTemplate 英文模板
     * @param colorModelNum   颜色模板
     * @param colorChangeAttr
     * @return
     */
    public static Document printCycleAddRuleVersion2(Document doc, OWLModel model, OWLIndividual copyIndividual,
                                                     ArrayList<String> englishTemplate, int colorModelNum, ArrayList<String> colorChangeAttr) {
        colorModelNum = 0;
        colorChangeAttr.clear();
        Element rootName = doc.getRootElement();//存放了“场景+音乐+主题”等内容
        Element name = rootName.element("maName");//存放了“场景+音乐+主题”等内容
        String maName = name.attributeValue("name");//场景
        String topicName = name.attributeValue("topic");//主题
        if (maName.contains("nothing.ma"))//如果是选择的是nothing场景，doc不做改变返回
        {
            doc = doc;
        } else if (maName.contains("empty.ma")) {//如果是empty场景
            String messValue = ProgramEntrance.messageValue;//短信 内容
            Element addRule = name.addElement("rule");//添加rule节点
            addRule.addAttribute("ruleType", "addToMa");// ruleType="addToMa"
            addRule.addAttribute("addWord", messValue);//addWord="评分系统第二个版本上线了"empty场景打印的文字
            addRule.addAttribute("spaceName", "");
            addRule.addAttribute("degree", "");
            addRule.addAttribute("type", "word");//添加模型内容的类型：word（文字）：：或者people或者model等
            addRule.addAttribute("number", "1");
            addRule.addAttribute("class", "");
        } else {//如果不是空场景，一般都会走到这儿
            OWLNamedClass addModelRelatedClass = model.getOWLNamedClass("AddModelRelated");//AddModelRelated类对象

            OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");//对象属性
            OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");//对象属性
            OWLDatatypeProperty isTarget = model.getOWLDatatypeProperty("isTemplateObject");//数据属性
            OWLDatatypeProperty setColor = model.getOWLDatatypeProperty("setColor");//数据属性
            OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");//数据属性
            OWLDatatypeProperty addModelNumberProperty = model.getOWLDatatypeProperty("addModelNumber");//数据属性
            OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");//数据属性

            OWLObjectProperty hasColorProperty = model.getOWLObjectProperty("hasColor");//对象属性
            OWLObjectProperty heightProperty = model.getOWLObjectProperty("height");//对象属性
            if (addModelRelatedClass.getInstanceCount() > 0) {//如果addModelRelatedSpace类下边有实体：有4个
                OWLDatatypeProperty maFrameNumberProperty = model.getOWLDatatypeProperty("maFrameNumber");//重点：数据属性maFrameNumber
                OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");//数据属性
                OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");//数据属性
                int maFrameNum = Integer.parseInt(copyIndividual.getPropertyValue(maFrameNumberProperty).toString());//获取此场景帧数
                Collection<OWLIndividual> addModelList = addModelRelatedClass.getInstances();//获取addModelRelatedSpace类下边有实体：有4个
                Iterator its = addModelList.iterator();//获取addModelRelatedSpace类下边有实体：有4个
                while (its.hasNext()) {//遍历addModelRelatedSpace类下边有实体：有4个
                    OWLIndividual addIndividual = (OWLIndividual) its.next();//取得addModelRelatedSpace下边有实体：addModelID1，addModelID2...
                    String modelID = (String) addIndividual.getPropertyValue(modelIDProperty);//ID序列：addModelID1...
                    //addModelRelatedSpace属性值
                    OWLIndividual relatedSpce = (OWLIndividual) addIndividual.getPropertyValue(addModelRelatedSpaceProperty);
                    //关联模型的名称
                    OWLIndividual modelName = (OWLIndividual) addIndividual.getPropertyValue(hasModelNameProperty);
                    Object modelType = addIndividual.getPropertyValue(addModelTypeProperty);//关联模型的类型（people，model等）
                    Object topicname = addIndividual.getPropertyValue(topicNameProperty);
                    String modelNumber = "";
                    String isTar = "";
                    modelNumber = (String) addIndividual.getPropertyValue(addModelNumberProperty);
                    isTar = (String) addIndividual.getPropertyValue(isTarget);//TODO

                    Object degreeStr = null;
                    String degree = "";

                    OWLIndividual particleIndividual = (OWLIndividual) modelName;
                    Element addRule = name.addElement("rule");// rule 节点
                    addRule.addAttribute("ruleType", "addToMa");// type="model"
                    addRule.addAttribute("addModel", modelName.getBrowserText());//重点，重要：随机添加的模型
                    logger.info("随机添加模型（来自AxiomClass）：" + modelName.getBrowserText());
                    // 对短信中提到的具体颜色和模型的ID做处理，添加ID
                    int flags = 0;

                    for (int n = 0; n < modelWithColor.size(); ) {//start 000
//						System.out.println("modelWithColorsize=" + modelWithColor.size());
                        String mname = modelWithColor.get(n);
                        String color = modelWithColor.get(n + 1);
                        n = n + 2;

                        if (mname.equals(modelName.getBrowserText()) && isTar.equals("1")) {
                            modelWithColors.add(mname.toString());
                            modelWithColors.add(modelID.toString());
                            modelWithColors.add(color.toString());
                        }
                    }//end 000
                    Collection temp = modelName.getDirectTypes();
                    Iterator it = temp.iterator();
                    while (it.hasNext()) {
                        OWLNamedClass cls = (OWLNamedClass) it.next();
//						System.out.println("class:" + cls.getBrowserText());
                        if (!cls.getBrowserText().contains("Model"))
                            addRule.addAttribute("class", cls.getBrowserText());
                    }

                    if (modelName.hasPropertyValue(setColor)) {
                        addRule.addAttribute("color", (String) modelName.getPropertyValue(setColor));
                        colorChangeAttr.add((String) modelName.getBrowserText());
                        colorChangeAttr.add((String) modelName.getPropertyValue(setColor));
                        colorModelNum++;
                    }

                    // String browserText = relatedSpce.getBrowserText();
                    // System.out.println("+++"+browserText);

                    addRule.addAttribute("spaceName", relatedSpce.getBrowserText());
                    // if(addIndividual.getBrowserText().equals("boy.ma")||
                    // addIndividual.getBrowserText().equals("girl.ma"))
                    // addRule.addAttribute("type", "people");
                    // else
                    addRule.addAttribute("type", modelType.toString());
                    addRule.addAttribute("isTarget", isTar);//重点：重要，是否是目标
                    if (modelType.toString().equals("ParticleEffect")) {
                        degreeStr = particleIndividual.getPropertyValue(degreeProperty);
                        if (degreeStr == null || degreeStr.toString() == "")
                            degree = "normal";
                        else
                            degree = degreeStr.toString();
                        if (particleIndividual.getBrowserText().contains("rain")
                                || particleIndividual.getBrowserText().contains("snow")) {
                            modelNumber = "1";
                        }
                        Random rand = new Random();
                        if (modelName.toString().contains("fireWork")) {
                            addRule.addAttribute("frameNumber", "");
                            int startF = rand.nextInt(maFrameNum / 4) + 1;
                            addRule.addAttribute("startFrame", Integer.toString(startF));
                            addRule.addAttribute("endFrame", "");
                        } else {
                            addRule.addAttribute("frameNumber", "2");
                            int startF = rand.nextInt(maFrameNum / 4) + 1;
                            int endF = rand.nextInt(maFrameNum / 4) + maFrameNum / 2;
                            addRule.addAttribute("startFrame", Integer.toString(startF));
                            addRule.addAttribute("endFrame", Integer.toString(endF));
                        }

                        if (modelName.toString().contains("smoke")) {
                            Object smokeColor = particleIndividual.getPropertyValue(hasColorProperty);
                            String smokeC = "";
                            if (smokeColor == null)
                                smokeC = "white";
                            else
                                smokeC = smokeColor.toString();
                            addRule.addAttribute("color", smokeC);
                            Object smokeHeight = particleIndividual.getPropertyValue(heightProperty);
                            String smokeH = "";
                            if (smokeHeight == null)
                                smokeH = "normal";
                            else
                                smokeH = smokeHeight.toString();
                            addRule.addAttribute("height", smokeH);
                        }

                    }
                    addRule.addAttribute("degree", degree);
                    addRule.addAttribute("number", modelNumber);
                    // 处理烟的颜色，高度
                    addRule.addAttribute("addModelID", modelID.toString());
//					System.out.println(topicname);
                    /*
                     * if(topicname!=null){ addRule.addAttribute("topicname",
                     * topicname.toString()); }
                     */
                }//end 遍历addModelRelatedSpace类下边有实体：有4个
            }
        }
        return doc;
    }

    /**
     * 对有背景的ma文件进行背景图片的更换
     *
     * @param model
     * @param individualName
     * @return
     */
    public String exchangeMaBackground(OWLModel model, OWLIndividual individualName) {
        String picture = null;
        OWLDatatypeProperty hasBPictureProperty = model.getOWLDatatypeProperty("maHasBackgroundPicutre");

        return picture;
    }

    /**
     * 当信息抽取没有抽到主题，模板也没有推出主题，则由模板通过hasAnimationNameFromTemplate
     * 看可否得到相应的动画场景（直接由属性值得出）
     *
     * @param model
     * @param englishTemplate
     * @return
     */
    public static String getAnimationSceneFromTemplate(OWLModel model, ArrayList<String> englishTemplate) {
        OWLObjectProperty hasAnimationNameFProperty = model.getOWLObjectProperty("hasAnimationNameFromTemplate");// usedSpaceInMa
        ArrayList<OWLIndividual> animationList = new ArrayList();
        Random rand = new Random();
        Iterator its = englishTemplate.iterator();
        while (its.hasNext()) {
            String templateAllName = (String) its.next();
            int iPostion = templateAllName.indexOf(":");
            String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());
            OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
            if (templateIndividual.getPropertyValueCount(hasAnimationNameFProperty) > 0) {
                Collection collection = templateIndividual.getPropertyValues(hasAnimationNameFProperty);
                for (Iterator iValues = collection.iterator(); iValues.hasNext(); ) {
                    OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
                    animationList.add(animationIndividual);
                }
            }
        }
        if (animationList.size() > 0) {
            return animationList.get(animationList.size() - 1).getBrowserText();
        }
        return "";
    }

    /**
     * 获得实例的类名
     *
     * @param model
     * @param individualName
     * @return
     */
    public static OWLNamedClass getClassFromIndividual(OWLModel model, OWLIndividual individualName) {
        OWLNamedClass className = null;
        RDFResource resource = individualName;
        String classNameStr = resource.getRDFType().getBrowserText();
        className = model.getOWLNamedClass(classNameStr);
        return className;

    }

    /**
     * 复制一个ma场景文件的实例
     *
     * @param maName：已经选好的ma文件
     * @param model
     * @return
     */

    @SuppressWarnings("deprecation")
    public static OWLModel copyMaIndividual(String maName, OWLModel model, String topicName) {
        if (!maName.equals("")) {
            OWLIndividual i = model.getOWLIndividual(maName);
            OWLIndividual shalowCopy = (OWLIndividual) i.copy(null, null, false);
            // OWLIndividual shalowCopy = (OWLIndividual) i.deepCopy(null,
            // null);
            OWLDatatypeProperty maSenceNameProperty = model.getOWLDatatypeProperty("maSceneName");
            OWLDatatypeProperty selectedMaProperty = model.getOWLDatatypeProperty("selectedMa");
            OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");
            shalowCopy.setPropertyValue(maSenceNameProperty, maName);
            shalowCopy.setPropertyValue(topicNameProperty, topicName);
            String maName11 = (String) shalowCopy.getPropertyValue(maSenceNameProperty);
            System.out.println("manEEE:" + maName11);
            shalowCopy.rename(shalowCopy.getNamespace() + "copyMaSceneIndividual");
            OWLIndividual ii = model.getOWLIndividual("copyMaSceneIndividual");
            ii.setPropertyValue(selectedMaProperty, new Integer(1));// 设置所选的ma的selectedMa属性值为1
            // ii.c
            System.out.println("copyIndividual:" + ii.getBrowserText());
        }
        return model;
    }

    /**
     * 通过中文topic到ontology中寻找相应的英文topic类
     *
     * @param owlModel：owl                      model
     * @param chineseTopic:中文topic名称（通过IE抽取的主题）
     * @return 中文Topic名称对应的英文类
     */
    public static OWLNamedClass getEnglishTopicFromPlot(OWLModel owlModel, String chineseTopic) {

        OWLNamedClass TopicClass = owlModel.getOWLNamedClass("TopicRelatedPlot");//主题相关情节类
        OWLDatatypeProperty chineseNameProperty = owlModel.getOWLDatatypeProperty("chineseName");//属性对象
        OWLNamedClass cls = null;
        Collection subTopicClass = TopicClass.getSubclasses(true);//获取TopicRelatedPlot的子孙类
        for (Iterator itTopic = subTopicClass.iterator(); itTopic.hasNext(); ) {//遍历他的子孙类
            cls = (OWLNamedClass) itTopic.next();//next()
            if (cls.getDirectSubclassCount() == 0)// 判断这个类已经没有直接子类
            {
                Object hasValueName = cls.getHasValue(chineseNameProperty);//获取其chineseName属性的值
                if (hasValueName != null && hasValueName.toString().equals(chineseTopic)) {//如果有chineseName属性并且其值域后与说找的中文主题相同，返回
                    break;
                }
                cls = null;
            }
        }
        return cls;
    }

    /**
     * 把中文模板变成英文的,通过这个方法处理完后，英文模板中已经 没有模板的名字只有相应的值，如“天气”，“时间”等词已经没有 只有“雪：中雪”等字样
     *
     * @param chineseTemplate
     * @param model
     * @return
     * @throws IOException
     * @throws SecurityException
     */
    public static ArrayList<String> colorTemplate2Individual(ArrayList<String> colorTemplate,
                                                             ArrayList<String> colorMark, OWLModel model) {
        ArrayList<String> individualWithColor = new ArrayList();
        ArrayList<String> modelWithColor = new ArrayList();
        OWLObjectProperty modelFromTemplateProperty = model.getOWLObjectProperty("hasModelFromTemplate");
        OWLDatatypeProperty modelColorProperty = model.getOWLDatatypeProperty("setColor");
        Iterator<String> itsCol = colorMark.iterator();
        for (Iterator<String> itsTemp = colorTemplate.iterator(); itsTemp.hasNext(); ) {
            String color = itsCol.next();
            String templateValue = itsTemp.next();
            int postion = templateValue.indexOf(":");
            String individual = templateValue.substring(postion + 1);
            OWLIndividual indi = model.getOWLIndividual(individual);
            Iterator temp = indi.getPropertyValues(modelFromTemplateProperty).iterator();
            while (temp.hasNext()) {
                String modelName = temp.next().toString();
                int p1 = modelName.indexOf("#");
                int p2 = modelName.indexOf(" of");
                modelName = modelName.substring(p1 + 1, p2);
                logger.info("~~~~~~modelName:" + modelName);
                OWLIndividual modelIndi = model.getOWLIndividual(modelName);
                modelIndi.setPropertyValue(modelColorProperty, color);
                // 对特殊的模型添加特殊颜色做处理。
                modelWithColor.add(modelName);
                modelWithColor.add(color);
            }
        }
        return modelWithColor;
    }

    /**
     * 中文模板转换为英文模板
     * <p>
     * ArrayList<String> chineseTemplate 要转换的中文模板 return ArrayList<String>
     * 中文模板转换后的英文模板
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> chineseTemplate2English(ArrayList<String> chineseTemplate, OWLModel model)
            throws SecurityException, IOException {
        ArrayList<String> englishTemplate = new ArrayList();
        OWLNamedClass templateClass = model.getOWLNamedClass("Template");
        OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection subTemplateClass = templateClass.getSubclasses(true);// 打印子类，并打印子类的子类
        for (Iterator<String> ist = chineseTemplate.iterator(); ist.hasNext(); ) {// 循环处理中文原子模板对应的原子信息
            String tempName = ist.next();

            boolean isStop = false;
            boolean findCls = false;
            A:
            for (Iterator itTemplate = subTemplateClass.iterator(); itTemplate// 循环Template下面的所有子类
                    .hasNext(); ) {
                if (isStop)
                    break;
                cls = (OWLNamedClass) itTemplate.next();// 到ontology中寻找模板，通过chineseName这个属性来查找
                Object cc = cls.getHasValue(chineseNameProperty);
                // System.out.println("hhhh:"+cls.getBrowserText()+"
                // test111:"+tempName);
                if (cls.getHasValue(chineseNameProperty).toString().trim().equals(tempName.trim()))// 通过hasValue属性来获得模板的名字
                // ，表明是什么模板（时间或地点）
                {// 先查找对应的模板

                    String tempNameW = cls.getBrowserText();
                    // englishTemplate.add(tempNameW);
                    String templateVlaue = "";
                    if (ist.hasNext())
                        templateVlaue = ist.next();
                    // String[] splitTempName = new String[2];
                    String[] splitTempName = templateVlaue.split(":");
                    Collection subsubTemplateClass = cls.getSubclasses(true);// 打印出已查找的模板的子类
                    B:
                    for (Iterator itsTemplate = subsubTemplateClass.iterator(); itsTemplate.hasNext(); ) {
                        if (isStop)
                            break;

                        OWLNamedClass clss = (OWLNamedClass) itsTemplate.next();
                        Collection clssHasValues = clss.getHasValues(chineseNameProperty);

                        C:
                        for (Iterator itValue = clssHasValues.iterator(); itValue// 查找所对应模板下的子类
                                .hasNext(); ) {
                            if (isStop)
                                break;
                            Object value = itValue.next();
                            if (value.toString().trim().equals(splitTempName[0].trim()))// 用来匹配模板值的冒号前面的字段：如学校：小学，这里就是匹配“学校”的
                            {
                                findCls = true;
                                isStop = true;
                                String templateName = clss.getBrowserText() + ":";
                                if (clss.getInstanceCount() != 0) {
                                    ArrayList<String> templateInstan = new ArrayList();
                                    String autoValue = "";
                                    Collection templateInstances = clss.getInstances();// 当找到了模板所对应的类，则处理模板类对应的实例，也就是原子信息
                                    D:
                                    for (Iterator it = templateInstances.iterator(); it.hasNext(); ) {
                                        OWLIndividual templateIndividual = (OWLIndividual) it.next();// 打印出实例
                                        templateInstan.add(templateIndividual.getBrowserText());// 用来存储所有实例，主要是为了当没有找到符合要求的实例时就从中随机的选择一个
                                        if (templateIndividual.getPropertyValueCount(chineseNameProperty) > 0) {
                                            Collection chineseValues = templateIndividual
                                                    .getPropertyValues(chineseNameProperty);
                                            for (Iterator its = chineseValues.iterator(); its.hasNext(); )// 循环实例所对应的多个中文名称
                                            {
                                                String cValue = its.next().toString();
                                                if (cValue.trim()
                                                        .equals(splitTempName[splitTempName.length - 1].trim())) {
                                                    // templateIndividual.getpro
                                                    autoValue = templateIndividual.getBrowserText();

                                                    break D;
                                                } // 所对应实例的中文名称
                                            } // 结束实例的中文名字的循环
                                        } // 结束实例是否有中文名字

                                    } // 结束对应类的实例
                                    if (autoValue != "")// 模板实例中找到了相应的模板原子对应的信息
                                        templateName = templateName + autoValue + ":1.0";

                                    else // 当实例中找不到相应的模板原子信息，就从已经有的模板实例原子中随机挑选一个
                                    {
                                        Random rand = new Random();
                                        int kk = rand.nextInt(templateInstan.size());
                                        templateName = templateName + templateInstan.get(kk) + ":0.5";
                                    }

                                } // 结束实例的判断

                                if (!englishTemplate.contains(templateName)) {
                                    englishTemplate.add(templateName);
                                }
                                int i = templateName.lastIndexOf(":");// 去掉最后的分值
                                String temp = templateName.substring(0, i);

                                if (tempName.equals("动作")) {

                                    actionTemplateAttr.add(temp);// to qiu
                                }
                                if (tempName.equals("人物") || tempName.equals("经典人物角色")) {
                                    ActionNeedPeople.add(temp);
                                }
                                if (tempName.equals("情绪")) {
                                    moodTemplateAttr.add(temp);
                                    weatherAndmoodAttr.add(temp);
                                    ExpressionList.add(temp);
                                    actionTemplateAttr.add(temp);
                                    WindAttr.add(temp);
                                    RainAttr.add(temp);
                                    SnowAttr.add(temp);
                                    LightList.add(temp);
                                }
                                if (tempName.equals("天气") | tempName.equals("季节")) {
                                    weatherAndmoodAttr.add(temp);
                                    RainAttr.add(temp);
                                    WindAttr.add(temp);
                                    SnowAttr.add(temp);
                                    LightList.add(temp);
                                    SeasonList.add(temp);
                                }
                                // if(tempName.equals("时间"))
                                // WindAttr.add(templateName);
                                // if(tempName.equals("情绪"))

                                System.out.println("test:" + templateName);
                                logger.info("模板信息翻译成英文后的值：" + templateName);
                            }

                        }
                        // Object ccc=cls.getHasValue(chineseNameProperty);

                    } // 模板名下子类的结束
                    if (findCls == false)
                        TemplateName.add(tempNameW);// 单独添加模板名称
                } // 判断是否有相对于的模板
            } // template类下的所有子类
        }

//		System.out.println("各种模板englishiTemplate=" + englishTemplate + windRainSnowNeedAttr);
        return englishTemplate;
    }

    // ********************************************中文翻译成英文结束**********************************************************//

    /**
     * 通过中文topic到ontology中寻找相应的英文topic类
     *
     * @param owlModel：owl                   model
     * @param chineseTopic:中文topic类（通过IE抽取的）
     * @return
     */
    public static OWLNamedClass getEnglishTopic(OWLModel owlModel, String chineseTopic) {

        OWLNamedClass TopicClass = owlModel.getOWLNamedClass("Topic");
        OWLDatatypeProperty chineseNameProperty = owlModel.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection subTopicClass = TopicClass.getSubclasses(true);// 打印子类，并打印子类的子类

        for (Iterator itTopic = subTopicClass.iterator(); itTopic.hasNext(); ) {
            cls = (OWLNamedClass) itTopic.next();
            if (cls.getDirectSubclassCount() == 0)// 判断这个类已经没有子类了
            {
                Object hasValueName = cls.getHasValue(chineseNameProperty);
                if (hasValueName != null && hasValueName.toString().equals(chineseTopic)) {
                    break;
                }
                cls = null;
            }

        }
        return cls;
    }

    /**
     * 从owl中BackgroundScene 类下面选取一个实例
     *
     * @param model
     * @return
     */
    public static String chooseBackgroundScene(OWLModel model) {
        String strBackgroundScene = "";
        OWLNamedClass backgrounScene = model.getOWLNamedClass("BackgroundScene");
        if (backgrounScene.getInstanceCount() > 0) {
            Collection backgroundList = backgrounScene.getInstances();
            ArrayList<OWLIndividual> backgroundList2 = (ArrayList) backgroundList;
            Random rand = new Random();
            int kk = rand.nextInt(backgroundList2.size());
            strBackgroundScene = backgroundList2.get(kk).getBrowserText();

        }
        return strBackgroundScene;
    }

    /**
     * 通过addModelFromTopic类规则来选择场景文件
     *
     * @param model
     * @param englishTopicClass
     * @return
     * @throws SWRLRuleEngineException
     */
    public static String getMaFromAddModelFromTopicRule(OWLModel model, OWLNamedClass englishTopicClass)
            throws SWRLRuleEngineException {
        String choosedMaName = "";
        OWLObjectProperty hasModelFromTopicProperty = model.getOWLObjectProperty("hasModelFromTopic");
        Collection individualList = englishTopicClass.getInstances();
        OWLIndividual topicIndividualValue = null;
        if (individualList.size() > 0) {
            Iterator its = individualList.iterator();
            while (its.hasNext()) {
                topicIndividualValue = (OWLIndividual) its.next();
            }

        }
        if (topicIndividualValue.getPropertyValueCount(hasModelFromTopicProperty) > 0) {
            SWRLMethod.addModelFromTopicToScene(model, englishTopicClass.getBrowserText());
            choosedMaName = chooseBackgroundScene(model);
        }
        return choosedMaName;

    }

    /**
     * 通过主题来获得ma，前提是主题不为空
     *
     * @param owlModel
     * @param EnglishTopic:通过中文topic获得的英文topic
     * @return
     * @throws IOException
     * @throws SecurityException
     * @throws SWRLRuleEngineException
     */
    @SuppressWarnings("deprecation")
    public static String getMaFromTopic(OWLModel model, OWLNamedClass englishTopicClass)
            throws SecurityException, IOException, SWRLRuleEngineException {
        String choosedMaName = "";
        int zeroCount = 0;// 用来记录标记为0的动画场景的个数
        int oneCount = 0;// 用来记录标记为1的动画场景的个数
        ArrayList<String> instanceSave = new ArrayList();// 用来存储实例
        ArrayList<Integer> instanceMarkSave = new ArrayList();// 用来存储实例的标志，看之前是否被选中过
        OWLObjectProperty hasMaProperty = model.getOWLObjectProperty("hasMa");
        OWLDatatypeProperty senceMarkProperty = model.getOWLDatatypeProperty("animationSceneMark");
        RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMaProperty);
        logger.info("通过主题来获得主题所对应的ma场景的名字，该主题所对应的场景类名：" + resource.getBrowserText());
        String hasValues = resource.getBrowserText();// 获得主题对应的场景的类名
        String[] hasValuesSplit = hasValues.split(" or ");// 可能对应多个 场景类;

        ArrayList<String> hasValuesClass = new ArrayList();
        OWLNamedClass resourceClass = null;
        System.out.println("geshu:" + hasValuesSplit.length);
        if (hasValuesSplit.length > 1) {// 当有多个场景类时，先判断每个场景类是否都有实例
            for (int i = 0; i < hasValuesSplit.length; i++) {
                OWLNamedClass resourceClass0 = model.getOWLNamedClass(hasValuesSplit[i].trim());
                System.out.println("name::" + hasValuesSplit[i].trim());
                int instanceCount0 = resourceClass0.getInstanceCount();
                if (instanceCount0 > 0)
                    hasValuesClass.add(hasValuesSplit[i].trim());
            }
            Random rand = new Random();
            if (hasValuesClass.size() > 0)// 当多个场景类都有实例时，则随机选择一个
            {
                int kk = rand.nextInt(hasValuesClass.size());
                resourceClass = model.getOWLNamedClass(hasValuesClass.get(kk));
            } else// 当多个场景类都没有实例时，则随机选择一个长场景类
            {
                int kk = rand.nextInt(hasValuesSplit.length);
                resourceClass = model.getOWLNamedClass(hasValuesSplit[kk].trim());

            }
        } else// 处理只有一个场景类的情况
            resourceClass = model.getOWLNamedClass(hasValuesSplit[0].trim());

        logger.info("最终该主题所对应的场景类名：" + resourceClass.getBrowserText());
        int instanceCount = resourceClass.getInstanceCount();
        System.out.print("此场景类拥有的实例个数是：" + instanceCount);

        logger.info("最终该主题所对应的场景类名的实例个数：" + instanceCount);
        if (instanceCount != 0) {// 即topic对应的场景类中有实例
            System.out.println(";分别为：");
            Collection resourceInstance = resourceClass.getInstances(true);
            for (Iterator it = resourceInstance.iterator(); it.hasNext(); ) {
                OWLIndividual individual = (OWLIndividual) it.next();// 打印出实例
                System.out.println("实例为：" + individual.getBrowserText());
                instanceSave.add(individual.getBrowserText());// 存储每个实例
                if (individual.getPropertyValue(senceMarkProperty) == null)
                    individual.setPropertyValue(senceMarkProperty, new Integer(0));
                int instanceMark = Integer.parseInt(individual.getPropertyValue(senceMarkProperty).toString());
                instanceMarkSave.add(instanceMark);// 存储每个实例的标志
                System.out.println("引用位为：" + instanceMark);
            }
            /// ************“多次机会”算法(start)*********
            for (int i = 0; i < instanceMarkSave.size(); i++)// 统计0和1的个数
            {
                int temp = instanceMarkSave.get(i);
                if (temp == 0)
                    zeroCount++;
                else
                    oneCount++;
            }
            // oneCount==instanceMarkSave.size()
            if (zeroCount == 0)// 当每个动画都被选过时，将本体中senceMark清零
            {
                for (Iterator it = resourceInstance.iterator(); it.hasNext(); ) {
                    OWLIndividual individual = (OWLIndividual) it.next();
                    individual.setPropertyValue(senceMarkProperty, new Integer(0));
                }
                Random random = new Random();
                int k = random.nextInt(instanceMarkSave.size());
                choosedMaName = instanceSave.get(k);
                OWLIndividual individual = model.getOWLIndividual(choosedMaName);
                individual.setPropertyValue(senceMarkProperty, new Integer(1));
            } else if (oneCount == 0) {
                Random random = new Random();
                int k = random.nextInt(instanceMarkSave.size());
                choosedMaName = instanceSave.get(k);
                OWLIndividual individual = model.getOWLIndividual(choosedMaName);
                individual.setPropertyValue(senceMarkProperty, new Integer(1));

            } else {
                float scale = oneCount / zeroCount;
                choosedMaName = multiChanceArithmetic(scale, instanceSave, instanceMarkSave, model);

            }
            /// ************“多次机会”算法(end)*********

        } else// 当有主题，但主题所对应的场景类没有实例,则查找场景类父节点下的其他场景类（可互换）
        {
            ArrayList<OWLNamedClass> otherClass = new ArrayList();
            logger.info("主题：" + englishTopicClass.getBrowserText() + "所对应的ma场景文件为0个，则找其父节点下的其他场景类");
            Collection resourceSuperClass = resourceClass.getSuperclasses(false);
            // 查找父类其他实例不为0的场景类
            int classCount = 0;
            for (Iterator itTopic = resourceSuperClass.iterator(); itTopic.hasNext(); ) {
                classCount++;
                Object rdfclass = itTopic.next();
                // NamedClass
                if (rdfclass.toString().contains("NamedClass")) {
                    OWLNamedClass cls = (OWLNamedClass) rdfclass;
                    if (cls.getInstanceCount(true) != 0)
                        otherClass.add(cls);
                    System.out.println("subTopic:" + cls.getBrowserText());
                    if (classCount == 1)
                        break;
                } else
                    continue;
                System.out.println("subTopic:" + rdfclass.toString());
            }
            if (otherClass.size() != 0) {
                ArrayList<OWLIndividual> lastClassInstanceName = new ArrayList();
                Random rand = new Random();
                Date date = new Date();
                rand.setSeed(date.getTime());
                int kk = rand.nextInt(otherClass.size());
                OWLNamedClass lastClass = otherClass.get(kk);
                Collection lastClassInstance = lastClass.getInstances(true);
                for (Iterator itTopic = lastClassInstance.iterator(); itTopic.hasNext(); ) {
                    OWLIndividual individual = (OWLIndividual) itTopic.next();
                    lastClassInstanceName.add(individual);
                }

                rand.setSeed(date.getTime());
                int k = rand.nextInt(lastClassInstanceName.size());
                OWLIndividual lastIndividualMa = lastClassInstanceName.get(k);
                choosedMaName = lastIndividualMa.getBrowserText();
                int instanceMark = Integer.parseInt(lastIndividualMa.getPropertyValue(senceMarkProperty).toString());
                if (instanceMark == 0)
                    lastIndividualMa.setPropertyValue(senceMarkProperty, new Integer(1));
                else
                    lastIndividualMa.setPropertyValue(senceMarkProperty, new Integer(0));

            } else {

                logger.info("主题：" + englishTopicClass.getBrowserText() + "所对应的ma场景文件为0个，则找其父节点下的其他场景类也没有实例");
                choosedMaName = "";
            }

        }
        JenaOWLModel owlModel = (JenaOWLModel) model;
        // String fileName = "sumoOWL2/sumo_phone3.owl";//保存ma标记
        // String fileName = "C:/ontologyOWL/rootOWL/sumoOWL2/sumo_phone3.owl";
        String fileName = "C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";//
        saveOWLFile(owlModel, fileName);
        // System.out.println("最终选的ma名字是：" + choosedMaName);
        logger.info("对应主题：" + englishTopicClass.getBrowserText() + "所选的ma文件为：" + choosedMaName);
        return choosedMaName;
    }

    /**
     * 多次机会算法
     */
    public static String multiChanceArithmetic(float scale, ArrayList<String> instanceSave,
                                               ArrayList<Integer> instanceMarkSave, OWLModel model) {
        int temp = 0;
        if (scale > 2)
            temp = 8;
        else if (1 < scale && scale <= 2)
            temp = 4;
        else
            temp = 2;
        String choosedMaName = null;
        for (int i = 0; i < temp; i++) {
            Random random = new Random();
            int k = random.nextInt(instanceMarkSave.size());
            choosedMaName = instanceSave.get(k);
            OWLDatatypeProperty senceMarkProperty = model.getOWLDatatypeProperty("animationSceneMark");
            OWLIndividual individual = model.getOWLIndividual(choosedMaName);
            int instanceMark = Integer.parseInt(individual.getPropertyValue(senceMarkProperty).toString());
            if (instanceMark == 0)// 在2次之内，如果选到没被选的动画，则循环停止
            {
                OWLIndividual individual1 = model.getOWLIndividual(choosedMaName);
                individual1.setPropertyValue(senceMarkProperty, new Integer(1));
                break;
            }
        }
        return choosedMaName;

    }

    /**
     * 通过模板来推出主题
     *
     * @param model        OWLNameModel对象
     * @param templateAttr 不带分值的英文模板
     * @return
     */
    private static String getTopicFromTemplateAfterSWRL(OWLModel model, ArrayList<String> templateAttr) {

        String topicName = "";

        OWLObjectProperty hasTopicFromTemplateProperty = model.getOWLObjectProperty("hasTopicFromTemplate");// 获取对象属性
        // hasTopicFromTemplate
        // 的对象
        ArrayList<String> topicN = new ArrayList();// 当有多个模板时，可能每个模板值都推出一个或多个主题，

        for (Iterator its = templateAttr.iterator(); its.hasNext(); ) {// 遍历所有的模板
            String templateValue = (String) its.next();// next();遍历所有模板名字
            String[] splitTempName = new String[2];
            splitTempName = templateValue.split(":");// 将模板值分存在字符数组：分别是 “类名+实体名”
            OWLIndividual individual = model.getOWLIndividual(splitTempName[1]);
            int count = individual.getPropertyValueCount(hasTopicFromTemplateProperty);
            boolean isAdd = false;
            if (count > 0) {
                Collection topicValue = individual.getPropertyValues(hasTopicFromTemplateProperty);
                for (Iterator its1 = topicValue.iterator(); its1.hasNext(); )// 规则推导后每个模板值中可能存在多个模板值
                {
                    OWLIndividual value = (OWLIndividual) its1.next();
                    if (topicN.size() > 0) {
                        isAdd = true;
                        for (Iterator<String> its2 = topicN.iterator(); its2.hasNext(); )// 防止topicN中出现重复的值
                        {
                            String its2Value = its2.next();
                            if (its2Value.equals(value.getBrowserText())) {
                                isAdd = false;
                                break;
                            }
                        }
                        if (isAdd)
                            topicN.add(value.getBrowserText());
                    } else
                        topicN.add(value.getBrowserText());
                }

            }

        }
        if (topicN.size() > 0) {
            Random rand = new Random();
            int kk = rand.nextInt(topicN.size());
            topicName = topicN.get(kk);
        }
        return topicName;
    }

    /**
     * 模型可以放置的位置(Location属性，多个)与场景的可用空间(hasValueOfPlace属性)通过isEquivalOf(可用空间属性)关联，即相同，则返回true
     *
     * @param model
     * @param maName     场景名称
     * @param individual 模型实体
     * @return
     */
    public static boolean isOwdToMa(OWLModel model, String maName, OWLIndividual individual) {
        OWLIndividual maname = model.getOWLIndividual(maName);
//		System.out.println(maname.getBrowserText());
        OWLObjectProperty hasValueOfPlace = model.getOWLObjectProperty("hasValueOfPlace");
        OWLObjectProperty Location = model.getOWLObjectProperty("Location");
        OWLObjectProperty isEquivalOf = model.getOWLObjectProperty("isEquivalOf");
        // OWLIndividual place=(OWLIndividual)
        // maname.getPropertyValue(hasValueOfPlace);

        Collection clo = individual.getPropertyValues(Location);//模型位置属性值集合

        if (clo.size() != 0) {
            for (Iterator i = clo.iterator(); i.hasNext(); ) {//遍历模型位置属性值集合
                OWLIndividual local = (OWLIndividual) i.next();//next()
                OWLIndividual place = (OWLIndividual) maname.getPropertyValue(hasValueOfPlace);//场景
                if (local.getPropertyValue(isEquivalOf).equals(place)) {
                    return true;
                }

            }
        }
        return false;
    }

}
