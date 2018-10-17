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
     * ����һ����owl�ļ�
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
    static ArrayList<String> actionTemplateAttr = new ArrayList();// �������涯��ģ�弰��ԭ����Ϣ
    static ArrayList<String> usedModelAttr = new ArrayList();// ������������Ӻ͸��Ĺ������õ���ģ�ͣ�Ϊɾ����������
    static ArrayList<String> colorChangeAttr = new ArrayList();// ���洫�ݸ������ı�ɫ����
    static int colorModelNum = 0;// ���洫�ݸ������ı�ɫ������Ŀ
    static ArrayList<String> timeweatherandfog = new ArrayList();// ���洫�ݸ�������ʱ�����
    public static ArrayList<String> timeweatherandfog1 = new ArrayList();// ������Ļ�Ļ�ȡ
    static ArrayList<String> moodTemplateAttr = new ArrayList();// ������������ģ�弰��ԭ����Ϣ
    static ArrayList<String> weatherAndmoodAttr = new ArrayList();
    static ArrayList<ArrayList> windRainSnowNeedAttr = new ArrayList();// ��������LHH��Ҫ��ģ����Ϣ
    static ArrayList<String> ExpressionList = new ArrayList();// ������������Ե�ģ����Ϣ
    static boolean bIsBackgroundScene = false;
    static boolean hasWeatherTeplate = false;
    static boolean hasTimeTemplate = false;
    static boolean people = false;
    static ArrayList<String> WindAttr = new ArrayList();
    static ArrayList<String> RainAttr = new ArrayList();
    static ArrayList<String> SnowAttr = new ArrayList();
    static ArrayList<String> ActionNeedPeople = new ArrayList();
    static ArrayList<String> LightList = new ArrayList();// ��������HL��Ҫ��ģ����Ϣ
    static ArrayList<String> SeasonList = new ArrayList();
    static ArrayList<String> WeatherList = new ArrayList();
    static ArrayList<String> topiclist = new ArrayList();//���Ӣ�����������
    static ArrayList<String> topictemplate = new ArrayList();
    static int[] num = new int[2];
    static boolean ifActionOrExpression = false;

    public JenaMethod() {

    }

    /**
     * �����򣬴�����ֹ���
     *
     * @param topic��IE��ȡ������
     * @param templateAttr:����ģ��
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

        // ��owl�ļ�,ͨ��url���owlģ��
        String url = "file:///c:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
        OWLModel model = createOWLFile1(url);

        // OWLģ�Ͷ����"������+ģ������s"==��Ӣ��Topic+ģ����������
        ArrayList<String> englishTemplateW = new ArrayList();
        englishTemplateW = chineseTemplate3English(TopicAndTemplate, model);
        System.out.println("����ѡ��ģ�������ǣ�" + englishTemplateW);

        // ����ѩģ�������д��뺯���и�ֵ������������Ӧ
        windRainSnowNeedAttr.add(0, WindAttr);
        windRainSnowNeedAttr.add(1, RainAttr);
        windRainSnowNeedAttr.add(2, SnowAttr);

        if (ProgramEntrance.isMiddleMessage) {
            // "ֻ���м�����ֱ�ӽ�������Ϣ��������ʽ����ճ�����"
            maName = "empty.ma";
        }

        // ����Ӣ��ģ����������У�������ֵ���ȡ����������ֵ
        for (int i = 0; i < englishTemplateW.size(); i++) {// ȥ�����ķ�ֵ
            int iP = englishTemplateW.get(i).lastIndexOf(":");
            englishTemplate.add(englishTemplateW.get(i).substring(0, iP));
        }

        // ***********************��plotTemplate�������ĵ�Ӣ��***************************8//
        ArrayList englishTemplatePlot = new ArrayList();
        englishTemplatePlot = chineseTemplate2EnglishFromPlot(templateAttr, model);
        // ***********************��plotTemplate�������ĵ�Ӣ��***************************8//
        num = count;// ���鸴�ƣ�count[0]��ŵ�������ģ��ĸ�����count[1]��ŵ��Ƿ�����ģ�����

        // �ռ����������֣��ص㡢��Ҫ�ص㡢��Ҫ�ص㡢��Ҫ�ص㡢��Ҫ�� ��ȡȫ���ĳ���,�����Է���ѩ����������
        SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName, englishTemplatePlot, num);
        // OWLModel�����Ӣ��ģ�塢��ɫģ��
        TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// �ռ�ģ��

        CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);

        maName = SceneSelected();
        //�ص㣬��Ҫ����û��
        if (maName == null || maName.length() == 0) {//���û��ѡ�������
            logger.info("û�и�������ѡ������");
            ifActionOrExpression = true;
            System.out.println(num[0] + "\t" + num[1]);
            num[1] = 0;// num[1]��ŵ����Ƿ��з�����ģ�壬
            if (num[0] != 0) {
                SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName,
                        englishTemplatePlot, num);// ��ȡȫ���ĳ���,�����Է���ѩ����������
                TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// �ռ�ģ��
                CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);
                maName = SceneSelected();
            } else {

                SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName,
                        englishTemplatePlot, num);// ��ȡȫ���ĳ���,�����Է���ѩ����������
                TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// �ռ�ģ��
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
     * ����λ�û�ȡOWLģ�Ͷ���
     *
     * @param url��owl�ļ����ڵ�·��
     * @return OWLģ�Ͷ���
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
     * ����������+ģ�塪��ת��������Ӣ��topic+Ӣ��ģ��
     *
     * @param topicAndTemplate ��������+ģ��s
     * @param model
     * @return Ӣ������+Ӣ��ģ��s
     */
    private static ArrayList<String> chineseTemplate3English(ArrayList<String> topicAndTemplate, OWLModel model) {

        ArrayList<String> englishTemplate = new ArrayList();
        // ��Ҫ,�ص㣺��ȡ��Ϊ chineseName ���������Զ���
        OWLDatatypeProperty chinesename = model.getOWLDatatypeProperty("chineseName");

        for (Iterator it = topicAndTemplate.iterator(); it.hasNext(); ) {
            String str = (String) it.next();// next()
            String hasvalue[] = str.split("-");// ������+ģ��ֱ����������
            String stemplate = "";
            String strin = "", strin1 = "", strin2 = "";
            String temp = "", temp2 = "", template = "";

            for (int i = 0; i < hasvalue.length; i++) {
                temp = hasvalue[i];

                if (i == 0) {// ���������
                    // ������������������ȡ������Ӧ��Ӣ����
                    strin = chineseTemplateEnglish(temp, "Topic", model);
                    if (strin == null)
                        strin = "";
                } else {
                    temp2 = "";
                    strin2 = "";
                    template = "";
                    String[] hasvla = temp.split(":");// hasvla=[����, ѩ, ��ѩ]
                    String temp1 = hasvla[0];// temp1=����

                    strin1 = chineseTemplateEnglish(hasvla[0], "Template", model);// ƥ��Ӣ��ģ��
                    if (strin1 != null) {
                        stemplate = strin1;// stemplate=����

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

                                System.out.println("���壨�񶨣�=" + obj);
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
                                            for (Iterator its = chineseValues.iterator(); its.hasNext(); )// ѭ��ʵ������Ӧ�Ķ����������
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
                                                for (Iterator its = chineseValues.iterator(); its.hasNext(); )// ѭ��ʵ������Ӧ�Ķ����������
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
                    if (hasvla[0].equals("ʱ��") && !temp2.isEmpty()) {
                        LightList.add(temp2);
                    }
                    if (hasvla[0].equals("����") && !temp2.isEmpty()) {
                        actionTemplateAttr.add(temp2);
                    }
                    if (hasvla[0].equals("����") && !temp2.isEmpty()) {
                        ActionNeedPeople.add(temp2);
                    }
                    if (hasvla[0].equals("����") && !temp2.isEmpty()) {
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
                    if ((hasvla[0].equals("����") || hasvla[0].equals("����")) && !temp2.isEmpty()) {
                        temp3 = temp2 + ":" + hasvla[hasvla.length - 1];
                        weatherAndmoodAttr.add(temp3);
                        RainAttr.add(temp3);
                        WindAttr.add(temp3);
                        SnowAttr.add(temp3);
                        LightList.add(temp2);
                        SeasonList.add(temp2);
                    }
                    if (hasvla[1].equals("�����¶�") && !temp2.isEmpty()) {
                        actionTemplateAttr.add(temp2);
                    }
                }

                // �����ģ��
                if (template != null && template.length() != 0 && template.contains(":")) {
                    englishTemplate.add(template);// Ӣ��ģ�����Ӣ��ģ������
                }

                if (strin1 != null && strin1.length() != 0 && strin1.contains(":"))
                    strin = strin + "-" + strin1;
            }
            // ����ж���ģ�壬������������ģ��
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
     * ����ģ�巭��ɶ�Ӧ��Ӣ��ģ��
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> chineseTemplate2EnglishFromPlot(ArrayList<String> chineseTemplate, OWLModel model)
            throws SecurityException, IOException {
        ArrayList<String> englishTemplate = new ArrayList();

        OWLNamedClass templateClass = model.getOWLNamedClass("ModelRelatedPlot");
        OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection subTemplateClass = templateClass.getSubclasses(true);// ��ӡ���࣬����ӡ���������
        for (Iterator<String> ist = chineseTemplate.iterator(); ist.hasNext(); ) {// ѭ����������ԭ��ģ���Ӧ��ԭ����Ϣ
            String tempName = ist.next();

            boolean isStop = false;
            boolean findCls = false;
            A:
            for (Iterator itTemplate = subTemplateClass.iterator(); itTemplate// ѭ��Template�������������
                    .hasNext(); ) {
                if (isStop)
                    break;
                cls = (OWLNamedClass) itTemplate.next();// ��ontology��Ѱ��ģ�壬ͨ��chineseName�������������
                Object cc = cls.getHasValue(chineseNameProperty);
                // System.out.println("hhhh:"+cls.getBrowserText()+"
                // test111:"+tempName);
                if (cc != null && cls.getHasValue(chineseNameProperty).toString().trim().equals(tempName.trim()))// ͨ��hasValue���������ģ�������
                // ��������ʲôģ�壨ʱ���ص㣩
                {// �Ȳ��Ҷ�Ӧ��ģ��
                    String tempNameW = cls.getBrowserText();
                    // englishTemplate.add(tempNameW);
                    String templateVlaue = "";
                    if (ist.hasNext())
                        templateVlaue = ist.next();
                    // String[] splitTempName = new String[2];
                    String[] splitTempName = templateVlaue.split(":");
                    Collection subsubTemplateClass = cls.getSubclasses(true);// ��ӡ���Ѳ��ҵ�ģ�������
                    B:
                    for (Iterator itsTemplate = subsubTemplateClass.iterator(); itsTemplate.hasNext(); ) {
                        if (isStop)
                            break;

                        OWLNamedClass clss = (OWLNamedClass) itsTemplate.next();
                        Collection clssHasValues = clss.getHasValues(chineseNameProperty);

                        C:
                        for (Iterator itValue = clssHasValues.iterator(); itValue// ��������Ӧģ���µ�����
                                .hasNext(); ) {
                            if (isStop)
                                break;
                            Object value = itValue.next();
                            if (value.toString().trim().equals(splitTempName[0].trim()))// ����ƥ��ģ��ֵ��ð��ǰ����ֶΣ���ѧУ��Сѧ���������ƥ�䡰ѧУ����
                            {
                                findCls = true;
                                isStop = true;
                                String templateName = clss.getBrowserText() + ":";
                                if (clss.getInstanceCount() != 0) {
                                    ArrayList<String> templateInstan = new ArrayList();
                                    String autoValue = "";
                                    Collection templateInstances = clss.getInstances();// ���ҵ���ģ������Ӧ���࣬����ģ�����Ӧ��ʵ����Ҳ����ԭ����Ϣ
                                    D:
                                    for (Iterator it = templateInstances.iterator(); it.hasNext(); ) {
                                        OWLIndividual templateIndividual = (OWLIndividual) it.next();// ��ӡ��ʵ��
                                        templateInstan.add(templateIndividual.getBrowserText());// �����洢����ʵ������Ҫ��Ϊ�˵�û���ҵ�����Ҫ���ʵ��ʱ�ʹ��������ѡ��һ��
                                        if (templateIndividual.getPropertyValueCount(chineseNameProperty) > 0) {
                                            Collection chineseValues = templateIndividual
                                                    .getPropertyValues(chineseNameProperty);
                                            for (Iterator its = chineseValues.iterator(); its.hasNext(); )// ѭ��ʵ������Ӧ�Ķ����������
                                            {
                                                String cValue = its.next().toString();
                                                if (cValue.trim()
                                                        .equals(splitTempName[splitTempName.length - 1].trim())) {
                                                    // templateIndividual.getpro
                                                    autoValue = templateIndividual.getBrowserText();

                                                    break D;
                                                } // ����Ӧʵ������������
                                            } // ����ʵ�����������ֵ�ѭ��
                                        } // ����ʵ���Ƿ�����������

                                    } // ������Ӧ���ʵ��
                                    if (autoValue != "")// ģ��ʵ�����ҵ�����Ӧ��ģ��ԭ�Ӷ�Ӧ����Ϣ
                                        templateName = templateName + autoValue + ":1.0";

                                    else // ��ʵ�����Ҳ�����Ӧ��ģ��ԭ����Ϣ���ʹ��Ѿ��е�ģ��ʵ��ԭ���������ѡһ��
                                    {
                                        Random rand = new Random();
                                        int kk = rand.nextInt(templateInstan.size());
                                        templateName = templateName + templateInstan.get(kk) + ":0.5";
                                    }

                                } // ����ʵ�����ж�

                                if (!englishTemplate.contains(templateName)) {
                                    englishTemplate.add(templateName);
                                }
                                logger.info("ģ����Ϣ�����Ӣ�ĺ��ֵ��" + templateName);
                            }

                        }
                        // Object ccc=cls.getHasValue(chineseNameProperty);

                    } // ģ����������Ľ���

                } // �ж��Ƿ�������ڵ�ģ��
            } // template���µ���������
        }

        return englishTemplate;
    }

    /**
     * ���� getSceneFromClass()��getSceneFromTopic()�ռ�����
     *
     * @param model               OWLModel����
     * @param topic               ��������
     * @param topicFromMG         �����������
     * @param topicFromQiu        ������������
     * @param englishTemplateList     Ӣ��ģ�壺����ֵ
     * @param englishTempW        Ӣ��ģ��
     * @param englishTemplatePlot Ӣ��ģ��
     * @param count               ���飬count[0]��������ģ�����,count[1]��������ģ�����
     * @throws SecurityException
     * @throws IOException
     * @throws SWRLRuleEngineException
     * @throws OntologyLoadException
     */
    public static void SceneCollect(OWLModel model, ArrayList<String> topic, ArrayList<String> topicFromMG,
                                    ArrayList<String> topicFromQiu, ArrayList<String> englishTemplateList, ArrayList<String> englishTempW,
                                    ArrayList<String> englishTemplatePlot, int count[])
            throws SecurityException, IOException, SWRLRuleEngineException, OntologyLoadException {

        sceneList.clear();// ��ʼ�����泡��������

        // ==================���ֻ������ģ��======================================
        if (count[0] != 0 && count[1] == 0) {
            // ����������Ϊ4�ӣ����桢���䡢ѩ�أ���ˮ�£�
            OWLNamedClass backgroundLandScene = model.getOWLNamedClass("BackgroundLandScene");
            OWLNamedClass backgroundRoomScene = model.getOWLNamedClass("BackgroundRoomScene");
            OWLNamedClass backgroundSnowScene = model.getOWLNamedClass("BackgroundSnowScene");

            // ��ȡ����ģ���������
            OWLNamedClass superclass = model.getOWLNamedClass("CharacterTemplate");
            Collection clo = superclass.getSubclasses();


            /**
             * ����Ӣ��ģ����(����ʵ��),�����CharacterTemplate���࣬����ӱ�������
             */
            for (int c = 0; c < englishTemplateList.size(); c++) {
                String ele = englishTemplateList.get(c);
                String englishTemplate = ele.substring(0, ele.indexOf(":"));// ȡ��Ӣ��ģ��ֵ
                // �����������ģ�����಻Ϊ0����֪ʶ�ⶨ�壬�϶��ģ�
                if (clo.size() != 0) {
                    // ��������ģ��CharacterTemplate������
                    for (Iterator ii = clo.iterator(); ii.hasNext(); ) {
                        OWLNamedClass subclass = (OWLNamedClass) ii.next();
                        String subCharacterTemplate = subclass.getBrowserText().toString();

                        if (subCharacterTemplate.equals(englishTemplate)) {// �������ģ����鵽������ģ��һ��
                            // �Ѿ�֪���鵽��ģ�壬ֻ������ģ�壬�����Ǳ�����������Դ������ģ��),1
                            getSceneFromClass(model, backgroundLandScene.getBrowserText().toString(), "BackGroundScene", 1);
                            getSceneFromClass(model, backgroundRoomScene.getBrowserText().toString(), "BackGroundScene", 1);
                            getSceneFromClass(model, backgroundSnowScene.getBrowserText().toString(), "BackGroundScene", 1);
                            break;
                        }
                    }
                }
            }

        } else {// �������ֻ������ģ��
            for (int i = 0; i < englishTemplateList.size(); i++) {// ����Ӣ��ģ��
                String Temp = englishTemplateList.get(i);
                String subTemp = Temp.split(":")[0];// �����ֵ��ȡ��ģ������
                OWLObjectProperty hasBG = model.getOWLObjectProperty("hasBackgroundScene");// ��ȡhasBackgroundScene�������Զ���
                OWLObjectProperty hasBGMa = model.getOWLObjectProperty("hasMa");// ��ȡhasMa�������Զ���
                OWLNamedClass templateClass = model.getOWLNamedClass("Template");// �õ�ģ�������
                Collection templateClas = templateClass.getSubclasses(true); // �õ�ģ�����������

                for (Iterator it = templateClas.iterator(); it.hasNext(); ) {

                    OWLNamedClass cls = (OWLNamedClass) it.next();// next()ȡ��ģ��������������
                    if (cls.getBrowserText().trim().equals(subTemp.trim())) {// ���ģ�����������������Աȵ�ģ��������ͬ�ģ�Ӣ�ģ�

                        if (cls.getSomeValuesFrom(hasBG) != null) {// �����ģ����hasBackgroundScene����
                            Object hasBGScene;
                            hasBGScene = cls.getSomeValuesFrom(hasBG);// ȡ��������ֵ

                            if (hasBGScene.getClass().getName()
                                    .equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
                                OWLUnionClass hasBGUnion = (OWLUnionClass) cls.getSomeValuesFrom(hasBG);// ��ȡ������
                                // ��ȡ���������ࣨOWLNamedClass)�Ĳ�������RDFSNamedClass��
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
                        } // end ��ģ���Ƿ���hasBackgroundScene����

                        if (cls.getSomeValuesFrom(hasBGMa) != null) {// hasBGMaΪOWLObjectProperty��Ķ���hasMa����ʾ��ģ���ж�Ӧ����
                            Object hasMa = cls.getSomeValuesFrom(hasBGMa);// ��ȡ����ֵ
                            // ��ȡ�������ơ��������ͣ���������
                            getSceneFromClass(model, ((RDFResource) hasMa).getBrowserText(), "TemplateScene", 1);
                        }
                    }
                }
            }
        } // end �������ֻ������ģ��

        double topicProbabilityMG = 0.0;
        if (topicFromMG.size() != 0) {// ���������ⲻΪ��
            int topicCount = topicFromMG.size();
            for (int i = 0; i < topicCount; i += 2) {
                String topicNameFromMG = topicFromMG.get(i);// �����������
                String topicProbabilityStr = topicFromMG.get(i + 1).trim();// ����������
                topicProbabilityMG = Float.parseFloat(topicProbabilityStr);
                if (topicProbabilityMG > 0.6) {// ���������⡷0.6
                    OWLNamedClass englishTopicClass = getEnglishTopic(model, topicNameFromMG);// ��ȡ������������
                    if (englishTopicClass != null) {// Ӣ�������಻Ϊ��
                        englishTopicMG = englishTopicClass.getBrowserText();
                        getSceneFromClass(model, englishTopicMG, "MGTopic", topicProbabilityMG);
                        getSceneFromTopic(model, englishTopicClass, "MGTopic", topicProbabilityMG);
                    }
                }

            }
        }
        if (topicFromQiu.size() > 0) {// ������������
            int topicCount = topicFromQiu.size();
            for (int i = 0; i < topicCount; i += 2) {
                String topicNameFromQ = topicFromQiu.get(i);
                String topicProbabilityStr = topicFromQiu.get(i + 1).trim();
                double topicProbability = Float.parseFloat(topicProbabilityStr);
                if (topicProbability > 0.3) {
                    OWLNamedClass englishTopicClass = getEnglishTopic(model, topicNameFromQ);
                    if (englishTopicClass != null) {
                        englishTopicQiu = englishTopicClass.getBrowserText();

                        System.out.println("������������:" + englishTopicQiu);
                        getSceneFromClass(model, englishTopicQiu, "QTopic", topicProbability);
                        getSceneFromTopic(model, englishTopicClass, "QiuTopic", topicProbability);
                    }
                }

            }
        }

        if (topic.size() != 0)// IEc�鵽��topic
        {
            int topicCount = topic.size();
            for (int i = 0; i < topicCount; i++) {
                String topicName = topic.get(i);
                OWLNamedClass englishTopicClass = getEnglishTopic(model, topicName);
                if (englishTopicClass != null) {
                    englishTopicIE = englishTopicClass.getBrowserText();
                    System.out.println("IE���ֵ������Ӧ��Ӣ�����⣺" + englishTopicIE);
                    getSceneFromClass(model, englishTopicIE, "IETopic", 1);
                    getSceneFromTopic(model, englishTopicClass, "IETopic", 1);
                }
            }
        }

        if (englishTemplateList.size() != 0)// ģ���Ƴ���
        {

            ArrayList<String> englishTemplateW = new ArrayList();
            for (int j = 0; j < englishTemplateList.size(); j++) {
                int iP = englishTemplateList.get(j).lastIndexOf(":");
                englishTemplateW.add(englishTemplateList.get(j).substring(0, iP));
            }

            logger.info("===========�����������ģ����Ϣ�ƶ�������");
//			SWRLMethod.executeSWRLEngine1(model, "getTopicFromTemplate", "", englishTemplateW);// ͨ��ģ���Ƴ�����

            logger.info("===========�����������ģ��������ʵ��");// englishTemplateWΪ����������Ӣ��ģ��
            String topicIndividual = getTopicFromTemplateAfterSWRL(model, englishTemplateW);

            if (!topicIndividual.isEmpty()) {
                System.out.println("�����������ģ���˳�����(ruleTopic):" + topicIndividual);
                OWLIndividual indi = model.getOWLIndividual(topicIndividual);// ��ȡ����ʵ��
                String clasName = indi.getDirectType().getBrowserText();
                getSceneFromClass(model, clasName, "RuleTopic", 1);
            }
        }
        /**
         * ��sceneCollet �õ�����Ӣ������ ��������englishTopicStr
         */
        // ���ADL�����Topicֵ
        if (!englishTopicIE.equals("")) {// ���IE���ⲻΪ��
            englishTopicStr = englishTopicIE;
        } else {// ���IE����Ϊ��
            if (topicProbabilityMG > 0.6) {// ���IE����Ϊ�գ�����MG������ʴ���0.6
                englishTopicStr = englishTopicMG;
            }
        }

        if (topicProbabilityMG > 0.6) {// ������������ʴ���0.6
            if (topiclist.size() != 0) {// ������������ʴ���0.6��������������Ϊ��
                boolean topicflage = false;// ��ʶ����>0.6����������Ƿ�������������
                for (int k = 0; k < topiclist.size(); k++) {
                    if (topiclist.get(k).equals(englishTopicMG)) {// ������������ʴ���0.6��������������Ϊ��,���������������������
                        topicflage = true;// ��ʶ����Ϊtrue
                        break;
                    }
                }
                if (topicflage == false) {// ����������>0.6�����Ҳ������������У����������ŵ�����������
                    topiclist.add(englishTopicMG);
                }
            } else {// ������������ʴ���0.6��������������Ϊ�գ�����������Ž�����������==���ǳ�������
                topiclist.add(englishTopicMG);
            }
        } // end ������������ʴ���0.6

        /**
         * �ж�ģ�嶯���������Ƿ�ƥ��(����<->����):
         * 1.���ƥ�䣬��ͨ������ģ�������ģ��Ķ������룬�����ģ����ȥ��������ģ��ֵ======������������
         */
        if (englishTemplateList.size() != 0) {// ���Ӣ��ģ�岻Ϊ��
            Action action = new Action();
            try {
                action.actionInfer(actionTemplateAttr, model, englishTopicStr);
                if (!action.isActionFlag()) {
                    englishTemplateList = delActionAndPeeople(englishTemplateList);
                    logger.info("����ģ�������ģ�岻ƥ�䣬ȥ��������ģ�壺����������أ�");
                } else {// ����������ƥ��
                    if (ifActionOrExpression == false)
                        ifActionOrExpression = true;
                }
            } catch (OntologyLoadException e) {
                logger.info("Action is error�������������)");
            }

            getAnim(englishTemplateList, model);// �˴�������ֵȥ��
        }

        // 2014.10.30ɾ�����ܴ�ģ��ֱֵ�ӵ���
        if (englishTempW.size() != 0) {// ���Ӣ��ģ�岻Ϊ��
            ArrayList tempIndual = new ArrayList();
            for (int i = 0; i < englishTempW.size(); i++) {
                String Temp = englishTempW.get(i);// ����Ӣ��ģ��
                OWLNamedClass temp = model.getOWLNamedClass(Temp);// ���Ӣ��ģ���ࣨ����������
                Collection induals = temp.getDirectInstances();// ��ô�Ӣ��ģ�����µ�ֱ��ʵ��
                for (Iterator it = induals.iterator(); it.hasNext(); ) {
                    OWLIndividual tempInd = (OWLIndividual) it.next();
                    tempIndual.add(tempInd.getBrowserText());

                }

            }
            System.out.println("ģ��ʵ����" + tempIndual);
            getAnim(tempIndual, model);
        }

        /**�ص㣺�ص㣺�Զ����ɳ���
         * bjut.plot design �����  ����Ƶ����µ��������⡣ ���г�����������plot�����õ�����+��.ma��������µĳ������� model
         * ΪOWLAllFile�����sum2 ·��,��ѡ�����⣬��������ƣ�û���������ģ�������³���
         */
        if (topic.size() > 0) {//�����������Ϊ�գ���������������getXML()
            for (int i = 0; i < topic.size(); i++) {
                String topicIE = topic.get(i);
                OWLNamedClass topicNamedCls = getEnglishTopicFromPlot(model, topicIE);//����IE������TopicRelatedPlot������chineseName�����������Ӧ����
                if (topicNamedCls != null) {
                    String topicName = topicNamedCls.getBrowserText();//��ȡ����
                    if (englishTopicStr.equals("")) {//�������Ӣ������Ϊ��
                        englishTopicStr = topicName;//���ڵ�Ӣ����������Ϊ��TopicRelatedPlot����������
                    }//�Զ����ɳ����ĳ�����Ϊ��������+ma
                    sceneList.add(new SceneCase(topicName + ".ma", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
            }
        } else {//�����������Ϊ��
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
                // ģ�Ͳ���**14.12.11****************************************//

                if (englishTemplatePlot.size() > 0) {
                    System.out.println("templatePlot" + englishTemplatePlot.size());
                    sceneList.add(new SceneCase("TemplatePlot.ma", 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));

                }
                // *******************************bjut.plot design ģ�Ͳ��� end
                // ******************************************//

            }
        }

        //���ѡ�õĳ�����
        String scenes = "";
        for (SceneCase ele : sceneList) {
            scenes += ele.getSceneName();
        }
        System.out.println("���ѡ��ĳ�����(" + sceneList.size() + "��):" + scenes);


        int sceneCount = sceneList.size();
        //�������еĳ������������Ч(��ѩ����)����
        logger.info("��ʼ�������еĳ������������Ч(��ѩ����)����");
        for (int i = 0; i < sceneCount; i++) {
            System.out.println("��" + (i + 1) + "��������" + sceneList.get(i).getSceneName() + "=======================");
            fogInsert fog = new fogInsert();
            Action action = new Action();
            try {
                action.actionInfer(actionTemplateAttr, model, englishTopicStr);
            } catch (Exception ex) {
                logger.info("ERROR: Action judge Exception");
            }
            if (action.isActionFlag()) {
                sceneList.get(i).ActionScore = 1;// ����
            }

            if (sceneList.get(i).sceneName.contains("Plot.ma")) {//������Զ��ϳɳ���dugenkui�ص���Ҫ
//				System.out.println("action." + action.isActionFlag());
                System.out.println("����������Plot.ma");
                if (action.isActionFlag()) {
                    sceneList.get(i).ActionScore = 1;// ����
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
                        sceneList.get(i).isWeatherable = 1;// ����
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
        logger.info("�����������еĳ������������Ч(��ѩ����)����");
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
        colorModelNum = 0;// ���洫�ݸ������ı�ɫ������Ŀ
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
     * ����owl�ļ�
     *
     * @param owlModel owlModel����
     * @param fileName C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl
     */
    @SuppressWarnings("unchecked")
    public static void saveOWLFile(JenaOWLModel owlModel, String fileName) {
        Collection errors = new ArrayList();
        owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
        System.out.println("File saved with " + errors.size() + " errors.");
    }

    /**
     * �ж���Ϊsname�ĳ����Ƿ��Ѿ��ڷ����˱�ѡ�����б�sceneList��ArraryList����
     *
     * @param sname ��������
     * @return -1����񶨣�i�������ڱ�ѡ�����б��λ��
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
     * ��ȡ��������������Ӧ��Ӣ������
     *
     * @param chiname   ��������
     * @param classname Ӣ�����������������������࣬������������롰������������ͬ�ģ��򷵻�
     * @param model     OWLmodel����
     * @return
     */
    public static String chineseTemplateEnglish(String chiname, String classname, OWLModel model) {
        OWLNamedClass temp = model.getOWLNamedClass(classname);// ��ȡ����Ϊclassname��OWLNamedClass����
        OWLDatatypeProperty chinesename = model.getOWLDatatypeProperty("chineseName");// ��ȡchineseName�������Զ���
        Collection clo = temp.getSubclasses(true);// ��ȡ���µ�����������
        String str = null;
        if (clo.size() != 0) {
            for (Iterator in = clo.iterator(); in.hasNext(); ) {// �������������࣬�鿴�Ƿ�������������������������������ͬ
                OWLNamedClass ols = (OWLNamedClass) in.next();
                Object ob = ols.getHasValue(chinesename);
                if (ob != null && ob.toString().equals(chiname)) {
                    str = ols.getBrowserText().toString();
                    break;// ����break������
                }
            }
        }
        return str;
    }

    /**
     * ע�⣺1.������������²鿴����ͬ��ʵ������ԣ����߲�ͬ
     *
     * @param model
     * @param englishTopicClass
     * @param sceneType
     * @param value
     */
    private static void getSceneFromTopic(OWLModel model, OWLNamedClass englishTopicClass, String sceneType,
                                          double value) {
        OWLObjectProperty hasMa = model.getOWLObjectProperty("hasMa");// ��ȡhasMa�������Զ���
        RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMa);// ��ȡ�����hasMa����ֵ
        if (resource != null) {// �������������Ӧ�� hasMa ���ԣ����������ж�Ӧ�Ķ�������
            String hasValues = resource.getBrowserText();// ��������Ӧ�ĳ�������
            String[] hasValuesSplit = hasValues.split(" or ");// ���ܶ�Ӧ��� ������;
            int sceneCount = hasValuesSplit.length;// �����Ӧ��������
            for (int i = 0; i < sceneCount; i++) {
                String sceneNameCls = hasValuesSplit[i];// ������������
                OWLNamedClass sceneClass = model.getOWLNamedClass(sceneNameCls);// ��ȡ������Ӧ������
                Collection curCls = sceneClass.getInstances(true);// ��Ӧ�ĳ������������������ʵ��
                OWLIndividual indi = null;
                for (Iterator itIns = curCls.iterator(); itIns.hasNext(); ) {// ������Ӧ�ĳ���ʵ��
                    indi = (OWLIndividual) itIns.next();// next()
                    String sceneName = indi.getBrowserText();// ����ʵ������
                    int readyScene = isOldSceneCase(sceneName);// �ж�Ϊ�Ѵ���ʵ��
                    if (readyScene < 0) {// ���δ����
                        if (sceneType.equals("MGTopic")) {// ���δ���ڶ�����������ⳡ��
                            sceneList.add(new SceneCase(sceneName, value > 0.6 ? value : 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0));
                            System.out.println("�鵽������ⳡ��(getSceneFromClass)" + sceneName);
                        }
                        if (sceneType.equals("IETopic")) {
                            sceneList.add(new SceneCase(sceneName, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                            System.out.println("�鵽IE���ⳡ��(getSceneFromClass):" + sceneName);
                        }
                        if (sceneType.equals("RuleTopic")) {
                            sceneList.add(new SceneCase(sceneName, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                            System.out.println("�鵽RuleTopic/�������ⳡ��(getSceneFromClass):" + sceneName);
                        }
                    } else {// ��������Ѿ���������������
                        if (sceneType.equals("MGTopic") && sceneList.get(readyScene).MGProb == 0.0) {
                            sceneList.get(readyScene).MGProb += value;
                            System.out.println("�鵽������ⳡ��(getSceneFromClass),���ҳ����Ѿ��������˳���������:" + sceneName);
                        }
                        if (sceneType.equals("IETopic") && sceneList.get(readyScene).IEProb == 0.0) {
                            sceneList.get(readyScene).IEProb += value;
                            System.out.println("�鵽IE���ⳡ��(getSceneFromClass),,���ҳ����Ѿ��������˳���������:" + sceneName);
                        }
                        if (sceneType.equals("RuleTopic") && sceneList.get(readyScene).ruleReason == 0.0) {
                            sceneList.get(readyScene).ruleReason += value;
                            System.out.println("�鵽RuleTopic/�������ⳡ��(getSceneFromClass),���ҳ����Ѿ��������˳���������:" + sceneName);
                        }
                    }
                }
            }
        }
    }

    /**
     *  fixme �����л�ȡ����
     * @param model     OWLModel ģ���ࣺ�����±ߵ�ʵ���������߳�����������
     * @param sceneCls  ��������������������磺BackGroundLandScene
     * @param sceneType �������͡����磺BackGroundScene
     * @param value     ����Ǹ�ֵʲô��˼����
     */
    private static void getSceneFromClass(OWLModel model, String sceneCls, String sceneType, double value) {

        // ������������� BackGroundScene ����TemplateScene
        if (sceneType.equals("BackGroundScene") | sceneType.equals("TemplateScene")) {

            OWLNamedClass sceneClass = model.getOWLNamedClass(sceneCls);// ��ó������Ӧ�Ķ���
            Collection curCls = sceneClass.getInstances(true);// ��ô˳����༰���������µ�ʵ��

            OWLIndividual indi = null;
            for (Iterator itIns = curCls.iterator(); itIns.hasNext(); ) {// �����˳����༰�������µĳ���ʵ��
                indi = (OWLIndividual) itIns.next();// next
                // �жϳ����Ƿ��ظ�
                int readyScene = isOldSceneCase(indi.getBrowserText());// �����ڱ�ѡ�����б�sceneList�е�λ�ã���������-1

                if (readyScene < 0 && sceneType.equals("BackGroundScene")) {
                    // ���û��sceneList��ߣ�������BackGroundScene����������볡���������ҳ�ʼ��ֵ��Ϊ0���������һ����ʶ�Ƿ��Ǳ��������Ĳ���
                    sceneList.add(new SceneCase(indi.getBrowserText(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
                    System.out.println("�鵽��������(getSceneFromClass)��" + indi.getBrowserText());
                }
                if (readyScene < 0 && sceneType.equals("TemplateScene")) {
                    // ���û��sceneList��ߣ�������TemplateScene����������볡���������ҳ�ʼ��ֵ��Ϊ0�����˱�ʶ���Ƿ���ģ����س������Ĳ���
                    sceneList.add(new SceneCase(indi.getBrowserText(), 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    System.out.println("�鵽ģ�峡��(getSceneFromClass)��" + indi.getBrowserText());
                }
            }
        } else {// ������Ǳ���������Ҳ����ģ�峡��
            OWLObjectProperty hasMa = model.getOWLObjectProperty("hasMa");// ��ȡhasMa�������Զ���
            OWLIndividual indi = null;
            OWLNamedClass sceneClass = model.getOWLNamedClass(sceneCls);// ��ȡ
            // ������OWLNamedClass
            // ����
            Collection curCls = sceneClass.getInstances(true); // ��ȡ����������ʵ������
            for (Iterator itIns = curCls.iterator(); itIns.hasNext(); ) {// �������ֳ������µ�ÿ��ʵ��
                indi = (OWLIndividual) itIns.next();// next()
                if (indi.hasPropertyValue(hasMa)) {// ������ʵ���±�����Ϊ hasMa ��������
                    if (indi.getPropertyValueCount(hasMa) > 0) {// �ظ��жϣ�������
                        Collection collection = indi.getPropertyValues(hasMa);// ��ȡÿ��hasMa����ֵ
                        for (Iterator iValues = collection.iterator(); iValues.hasNext(); ) {// ����hasMaֵ
                            OWLIndividual animationIndividual = (OWLIndividual) iValues.next(); // next()
                            String sceneName = animationIndividual.getBrowserText();// ��������
                            int readyScene = isOldSceneCase(sceneName);// �Ƿ�����ڳ�������sceneList��
                            if (readyScene < 0) {// ���û�г����ڳ�������sceneList��
                                if (sceneType.equals("MGTopic")) {// �����������ⳡ��
                                    sceneList.add(new SceneCase(sceneName, (value > 0.6 ? value : 0.1), 0, 0, 0, 0, 0,
                                            0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("�鵽������ⳡ��(getSceneFromClass):" + sceneName);
                                }
                                if (sceneType.equals("QTopic")) {
                                    sceneList.add(new SceneCase(sceneName, 0, 0, 0, 0, 0, 0, 0, 0, 0, value, 0, 0, 0));
                                    System.out.println("�鵽QTopic���ⳡ��(getSceneFromClass):" + sceneName);
                                }
                                if (sceneType.equals("IETopic")) {
                                    sceneList.add(new SceneCase(sceneName, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("�鵽IE���ⳡ��(getSceneFromClass):" + sceneName);// ��IE�ִʲ��ַ���������
                                }
                                if (sceneType.equals("RuleTopic"))// ������������ⳡ��
                                // dugenkui
                                {
                                    sceneList.add(new SceneCase(sceneName, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("�鵽RuleTopic/�������ⳡ��(getSceneFromClass):" + sceneName);// ���ݹ����Ƶ�����
                                }
                                if (sceneType.equals("TemplateScene")) {
                                    sceneList.add(new SceneCase(sceneName, 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                                    System.out.println("�鵽ģ������ĳ���(getSceneFromClass):" + sceneName);// ģ���±�������Թ����ĳ���
                                }
                            } else { // ����������˳�������sceneList��
                                // �����������⣬��ӦȨֵ+1
                                if (sceneType.equals("MGTopic") && sceneList.get(readyScene).MGProb == 0.0) {
                                    sceneList.get(readyScene).MGProb += value;
                                    System.out.println("�鵽������ⳡ��(getSceneFromClass),���ҳ����Ѿ��������˳���������:" + sceneName);
                                }
                                // �����IE���⡣����
                                if (sceneType.equals("IETopic") && sceneList.get(readyScene).IEProb == 0.0) {
                                    sceneList.get(readyScene).IEProb += value;
                                    System.out.println("�鵽IE���ⳡ��(getSceneFromClass),���ҳ����Ѿ��������˳���������:" + sceneName);
                                }
                                // ����ǹ����Ƶ����⡣����
                                if (sceneType.equals("RuleTopic") && sceneList.get(readyScene).ruleReason == 0.0) {
                                    sceneList.get(readyScene).ruleReason += value;
                                    System.out.println(
                                            "�鵽RuleTopic/�������ⳡ��(getSceneFromClass),���ҳ����Ѿ��������˳���������:" + sceneName);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * �ж�ģ�嶯���������Ƿ�ƥ�䣬���ƥ����ͨ������ģ�������ģ��Ķ������룬�����ģ����ȥ��������ģ��ֵ
     *
     * @param englishTemplate ���е�Ӣ��ģ��ֵ,���ڷ�����ƥ���Ӣ�����֣������������붯����ģ�͵�ʱ�򣬸���������Character��Animal����
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
     * OWL�л�ȡһ�����ʵ��
     *
     * @param cls owl����
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
        System.out.println("ʵ����" + IndividualList);
        return IndividualList;
    }

    /**
     * ��ģ���ȡ��������
     */
    static void getAnim(ArrayList englishTemplate, OWLModel model) {

        double value = 0.0;
        int tempCount = englishTemplate.size();
        // System.out.println("?????TemplateRelated--Scene: ");

        for (int i = 0; i < tempCount; i++) {// System.out.println("?????TemplateRelated--Scene:
            // ");
            OWLObjectProperty hasMa = model.getOWLObjectProperty("hasAnimationNameFromTemplate");// �������Զ���ģ���Ƶ�����������
            String engTemp = "";
            if (((String) englishTemplate.get(i)).contains(":")) {
                int size = ((String) englishTemplate.get(i)).split(":").length;
                engTemp = ((String) englishTemplate.get(i)).split(":")[size - 2];// ȡ�����ڶ���Ϊģ��ֵ
                value = Double.parseDouble(((String) englishTemplate.get(i)).split(":")[size - 1]);// ȡ���һ��
                // Ϊģ�����ֵ
                // System.out.println("engTemp and value:" + value + engTemp);
                // ����ֵӦΪģ�����value

            } else {
                engTemp = (String) englishTemplate.get(i);
                value = 0.5;
            }
            OWLIndividual indi = model.getOWLIndividual(engTemp);// ��ȡ����ΪengTemp��ģ��ʵ��

            if (indi != null && indi.hasPropertyValue(hasMa)) {// ����ģ��ʵ��
                // System.out.println(".....TemplateRelated--Individual: " +
                // indi.getBrowserText());
                if (indi.getPropertyValueCount(hasMa) > 0) {
                    Collection collection = indi.getPropertyValues(hasMa);
                    for (Iterator iValues = collection.iterator(); iValues.hasNext(); ) {// next()
                        OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
                        String sceneName = animationIndividual.getBrowserText();
                        System.out.println("��ģ����صĳ���:" + sceneName);

                        int readyScene = isOldSceneCase(sceneName);

                        if (readyScene < 0) {// valueֵΪ1or0.5��1��ʾΪֱ�ӷ��������ģ�͡�0.5Ϊ���ѡ��
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
     * @param model           OWLModelģ�������
     * @param englishTemplate Ӣ��ģ��
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
//								System.out.println("����ģ�͸���=" + k);
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
//								System.out.println("����ģ�͸���=" + k);
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
     * ��TemplateCollect()���ã�
     *
     * @param maName            ��������
     * @param sceneNO           �������У���ʶ�ڳ��������е�λ��
     * @param model             OWLModel����
     * @param englishTemplate   Ӣ��ģ������ģ���ࣺ���±�ʵ��=DoubtWordTemplate:doubtWordTemplate)
     * @param templateWithColor ��ɫģ��
     * @throws SecurityException��SWRLFactoryException
     * @throws SWRLRuleEngineException
     * @throws IOException
     */
    public static void CoutModelPlacable(String maName, int sceneNO, OWLModel model, ArrayList<String> englishTemplate,
                                         ArrayList<String> templateWithColor)
            throws SWRLFactoryException, SecurityException, SWRLRuleEngineException, IOException {

        System.out.println("��" + (sceneNO + 1) + "������(" + sceneList.get(sceneNO).sceneName + ")----------------------------------------------");

        OWLObjectProperty obj = model.getOWLObjectProperty("hasModelFromTemplate");

        OWLModel owlModel = processSWRL(maName, "", model, englishTemplate);
        OWLIndividual maIndividual = model.getOWLIndividual(maName);
        OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
        OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
        OWLObjectProperty usedModelInMaProperty = model.getOWLObjectProperty("usedModelInMa");

        ArrayList<OWLIndividual> individualList = new ArrayList();
        //getIndividualFromEnglishTemplateӦ������ΪgetModelsFromEnglishTemplate
        ArrayList<String> individualListFromTemplate = getIndividualFromEnglishTemplate(model, englishTemplate);
        ArrayList<String> colorIndividualList = getIndividualFromEnglishTemplate(model, templateWithColor);
        sceneList.get(sceneNO).templateModelNum = individualListFromTemplate.size();
        System.out.println("��ģ����ص�ģ����" + individualListFromTemplate.size() + "�����ֱ���:" + individualListFromTemplate.toString());
        sceneList.get(sceneNO).colorModelNum = colorIndividualList.size();//��ɫģ���Ӧ��ģ��

//		System.out.println("1111111individualListFromTemplate:" + individualListFromTemplate);
//		System.out.println("colorIndividualList:" + colorIndividualList);
        // @SuppressWarnings("unused")
        int count = 0;
        if (maName.contains("Plot.ma")) {
            sceneList.get(sceneNO).templateModelNum = individualListFromTemplate.size();
            sceneList.get(sceneNO).placableModelNum = individualListFromTemplate.size();
            sceneList.get(sceneNO).placableColorModelNum = colorIndividualList.size();
            System.out.println("�ɷ�����ɫģ������" + sceneList.get(sceneNO).placableColorModelNum);
        } else {
            String name = maIndividual.getBrowserText();
//			System.out.println("maName:" + name);
            // ��usedSpaceInMa���֣���Ҫ����addTomMa����m.
            System.out.println("�����Ŀ��ÿռ�ĸ���" + maIndividual.getPropertyValueCount(usedSpaceInMaProperty) + ",�ֱ���:");
            if (maIndividual.getPropertyValueCount(usedSpaceInMaProperty) > 0) {//����������п��ÿռ䣬�������а�����
                ArrayList<OWLIndividual> innerIndividualList = new ArrayList();//��Ҫ���������ţ���ϣƥ
                ArrayList<OWLIndividual> colorIndi = new ArrayList();
                //����ʵ�塮usedSpaceInMaProperty�����ԣ����ÿռ䣩��ֵ�������ж�����ÿռ䣬����ж��ֵ
                Collection usedSpaceValues = maIndividual.getPropertyValues(usedSpaceInMaProperty);
                //System.out.println("�������ÿռ�" + usedSpaceValues.iterator());
                for (Iterator iValues = usedSpaceValues.iterator(); iValues.hasNext(); ) {//�����˳����еĿ��ÿռ�
                    OWLIndividual spaceIndividual = (OWLIndividual) iValues.next();//next()
                    //�˿��ÿռ�ɷ����ģ��������hasPutObjectInSpaceProperty����
                    Collection objectInSpaceValues = spaceIndividual.getPropertyValues(hasPutObjectInSpaceProperty);
                    // ͨ��Map��<key��value>��ֵ���������ĳ��������Ķ��ʵ��������
                    Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();
                    //�����˳����Ĵ˿��ÿռ���Է����ģ�ͣ���hasPutObjectInSpaceProperty������ģ��
                    System.out.print("\t�������ÿռ��пɷŵ�ģ��(" + spaceIndividual.getBrowserText() + "):");
                    for (Iterator iiValues = objectInSpaceValues.iterator(); iiValues.hasNext(); ) {
                        OWLIndividual objectIndividual = null;
                        if (name.equals("empty.ma"))//����ǿճ�����ȥ��ģ��ʵ��
                            objectIndividual = model.getOWLIndividual(iiValues.next().toString());
                        else//������ǡ���������ôһ����
                            objectIndividual = (OWLIndividual) iiValues.next();//�������������ÿռ䡪����ģ�ͣ�����������ģ��
                        System.out.print(objectIndividual.getBrowserText() + ",");

                        Iterator itd = individualListFromTemplate.iterator();
                        Iterator itd1 = colorIndividualList.iterator();
                        boolean isEqualTemplate = false;
                        while (itd.hasNext()) {//����ģ����ص�ģ��
                            String individualStr = (String) itd.next();
//							System.out.println("��ģ����ص�ģ������" + individualStr);
//							System.out.println("�������ÿռ��пɷŵ�ģ��" + objectIndividual.getBrowserText());
                            if (individualStr.equals(objectIndividual.getBrowserText())) {//���ģ�����ģ����˿��ÿռ���Է����ģ����ͬ
                                if (innerIndividualList.size() == 0) {
                                    innerIndividualList.add(objectIndividual);
                                } else {// ���ظ���ȥ����ģ���Ѿ����Է��룬�Ͳ����ظ�����
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
                            }//end of ���ģ�����ģ����˿��ÿռ���Է����ģ����ͬ
                        }//end of ����ģ����ص�ģ��
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
                logger.info("��������(ģ�����ģ��+���ÿռ����ģ��),�ɷ���˳�����ģ����" + innerIndividualList.size() + "��,�ֱ���:");
                for (OWLIndividual ele : innerIndividualList) {
                    System.out.print(ele.getBrowserText() + ",");
                }
                System.out.println();

                sceneList.get(sceneNO).placableColorModelNum = colorIndi.size();
                System.out.println("�ɷ�����ɫģ������:" + sceneList.get(sceneNO).placableColorModelNum);
                // placableModelNum = innerIndividualList.size();
            }
        }

    }

    /**
     * @param model             OWLModel�����
     * @param englishTemplate   Ӣ��ģ��
     * @param templateWithColor ��ɫģ��
     * @param colorMark         ��ɫ��ʶ
     * @throws SWRLFactoryException
     * @throws SecurityException
     * @throws SWRLRuleEngineException
     * @throws IOException
     */
    public static void TemplateCollect(OWLModel model, ArrayList<String> englishTemplate,
                                       ArrayList<String> templateWithColor, ArrayList<String> colorMark)
            throws SWRLFactoryException, SecurityException, SWRLRuleEngineException, IOException {

        int sceneCount = sceneList.size();// �ռ���������
        // ģ��ģ���������ɷ���ģ���������ɷ�����ɫ����
        int templateModelNum = 0, placableModelNum = 0, placableColorModelNum = 0;
        boolean time = false;
        timeweatherandfog.add(0, "");// ���ݸ������ã�/(��o��)/~~����д�������
        timeweatherandfog.add(1, "");
        timeweatherandfog.add(2, "");
        timeweatherandfog.add(3, "");

        // ��ɫģ�壺 W�޸�5.4
        ArrayList<String> colorTemplateW = new ArrayList();
        colorTemplateW = chineseTemplate2English(templateWithColor, model);
        // System.out.println("colorTemplateW:" + colorTemplateW);

        for (int i = 0; i < colorTemplateW.size(); i++) {// ������ɫģ��
            int iP = colorTemplateW.get(i).lastIndexOf(":");
            colorTemplate.add(colorTemplateW.get(i).substring(0, iP));// �����ɫģ��
        }

        addModelToTemplate(model, englishTemplate);

        //�������еĳ���
        logger.info("��ʼ�������г�����");
        for (int i = 0; i < sceneCount; i++) {
            CoutModelPlacable(sceneList.get(i).sceneName, i, model, englishTemplate, colorTemplate);
        }
        logger.info("�����������г�����");
        if (englishTemplate.size() != 0) {//���������ֵ��Ӣ��ģ�岻Ϊ��
            // ����Ӣ��ģ�壬�ο��Ƿ��й���ʱ���ģ��
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();//next()
                if (tempWord.contains("Time") || tempWord.contains("DayTemplate")) {//���ģ�����ʱ��
                    time = true;
                }
                if (tempWord.contains("EveningNightTemplate"))
                    timeweatherandfog.set(0, (String) "Evening");// ����
                else if (tempWord.contains("LateNightTemplate"))
                    timeweatherandfog.set(0, (String) "Night");// ��ҹ
                else if (tempWord.contains("MorningTemplate"))
                    timeweatherandfog.set(0, (String) "EarlyMorning");// �峿
                else if (tempWord.contains("NightTemplate"))
                    timeweatherandfog.set(0, (String) "Night");// ���ϣ���ҹ���߰����ɣ�ֻҪ�����Ͼ���
                else if (tempWord.contains("daybreakTemplate"))
                    timeweatherandfog.set(0, (String) "EarlyMorning");
                else if (tempWord.contains("forenoonTemplate"))
                    timeweatherandfog.set(0, (String) "Morning");
                else if (tempWord.contains("noonTemplate"))
                    timeweatherandfog.set(0, (String) "Noon");
                else if (tempWord.contains("afternoonTemplate"))
                    timeweatherandfog.set(0, (String) "Afternoon");
            }
            //����ô����Ӣ��ģ�壬�鿴�Ƿ��й���ʱ����Ƶ�ģ��
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();
                if (tempWord.contains("sunshineTemplate"))
                    timeweatherandfog.set(1, (String) "Sunshine");// ��
                else if (tempWord.contains("cloudyTemplate"))
                    timeweatherandfog.set(1, (String) "Overcast");// ����
            }//ͬ��
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();
                if (tempWord.contains("sunshineTemplate"))
                    timeweatherandfog.set(2, (String) "NoCloud");// ��
                else if (tempWord.contains("cloudyTemplate"))
                    timeweatherandfog.set(2, (String) "Cloudy");// ����
            }//����
            for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext(); ) {
                String tempWord = ist.next();
                if (tempWord.contains("strongFogTemplate"))
                    timeweatherandfog.set(3, (String) "Heavyfog");// ����
                else if (tempWord.contains("fogTemplate"))
                    timeweatherandfog.set(3, (String) "Lightfog");// ����
            }
            for (int i = 0; i < timeweatherandfog.size(); i++) {
                String temp = timeweatherandfog.get(i);
                timeweatherandfog1.add(i, temp);// ������Ļ��ʹ��
            }
            if (time | !(timeweatherandfog.get(0).isEmpty()))// 0 ʱ��,Timeʱ��ģ��
                hasTimeTemplate = true;// ��ĸ
        }// end of ���������ֵ��Ӣ��ģ�岻Ϊ��
        if (!(timeweatherandfog.get(1).isEmpty()) || // 1��2��3������Ϊ��
                (!timeweatherandfog.get(2).isEmpty()) || (!timeweatherandfog.get(3).isEmpty())) {
            hasWeatherTeplate = true;
        }

//		System.out.println("��������������������������������englishTemplate:" + englishTemplate);
//		System.out.println("��������������������������������timeweatherandfog:" + timeweatherandfog.size() + timeweatherandfog
//				+ hasTimeTemplate + hasWeatherTeplate);
        // colorTemplate = chineseTemplate2English(templateWithColor, model);
        // System.out.println("��������������������������������colorTemplate:"+colorTemplate);
        modelWithColor = colorTemplate2Individual(colorTemplate, colorMark, model);
//		System.out.println("��������������������������������modelWithColor:" + modelWithColor);
    }

    //MG���⡢Qiu���⣬����������ɫģ�壺�����󳡾��ص�
    public static void CountSceneCaseBitMap(ArrayList<String> topicFromMG, ArrayList<String> topicFromQiu,
                                            ArrayList<String> topic, ArrayList<String> templateWithColor) {
        logger.info("��ʼ�������г��������ո���Ҫ�أ����⡢ģ����ء��ɷ���ģ����������������������");
        sCaseDataUsable.clear();
        int sceneCount = sceneList.size();//��������
        SceneCase tempSceneCase;//���ʵ��
        for (int i = 0; i < sceneCount; i++) {//��������
            tempSceneCase = sceneList.get(i);//��������
            if (topicFromMG.size() > 0)//���������ⲻΪ��
                sCaseDataUsable.set(1);//����Ƕ���Ϊ1
            if (topic.size() > 0)//������ⲻΪ��
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
            if (tempSceneCase.isBackgroundScene > 0)//����Ǳ�������
                sCaseDataUsable.set(10);
        }
        logger.info("�����������г��������ո���Ҫ�أ����⡢ģ����ء��ɷ���ģ����������������������");
    }

    /*
     * static public void SceneListSort() {
     *
     * for (int i = 0; i < sceneList.size(); i++) {
     *
     * // ��֤ǰi+1�����ź���
     *
     * SceneCase temp = sceneList.get(i); int j; for (j = i; j > 0 &&
     * sceneList.get(j-1).score < temp.score; j--) { sceneList.set(j,
     * sceneList.get(j-1)); } sceneList.set(j, temp); } }
     */
    static public ArrayList<SceneCase> SceneListSort(ArrayList<SceneCase> s) {

        for (int i = 0; i < s.size(); i++) {

            // ��֤ǰi+1�����ź���

            SceneCase temp = s.get(i);
            int j;
            for (j = i; j > 0 && s.get(j - 1).score < temp.score; j--) {
                s.set(j, s.get(j - 1));
            }
            s.set(j, temp);
        }
        return s;
    }

    static double randomtemp = 0;// ����xml���

    // ����ܰ
    public static String SceneSelected() throws SQLException {

//		if(1==1) return "childrenRoom.ma";

        int sceneCount = sceneList.size();
        ArrayList topicscene = new ArrayList();
        ArrayList templatescene = new ArrayList();
        ArrayList backscene = new ArrayList();
        ArrayList scene = new ArrayList();
        int weather = hasWeatherTeplate ? 1 : 0;
        logger.info("��ʼѡ�񳡾�===============================");
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
//		System.out.println("�Ƿ�鵽��Ӧ�Ķ�������飺" + ifActionOrExpression);
        int k = 0;
        while (k < sceneCount) {
            if (sceneList.get(k).isBackgroundScene == 1.0 && ifActionOrExpression == false && people == true) {// �������������ӵ�
                sceneList.remove(k);
                sceneCount = sceneList.size();
            } else {
                k++;
            }
        }
        sceneCount = sceneList.size();

        //TODO �˴�Ӧ���ж�ÿ�������Ƿ��ֵΪ0ֵ������ǵĻ����Ƴ���1.�������ݲμ�Scene�����ԣ�2.��Ҫ��ҳ���ӡ�����Ǵ�ӡ����־��
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


//		System.out.println("����õĳ�������Ϊ" + sceneCount); // ���յĺ�ѡ����
        // ������հ׳�����������κζ������򲻼������
        double actionScore = 5;

        // TODO ע�ͽ���ܰʦ�����ÿ�θ��³���Ҫ����ע��
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

            sceneList.get(i).score = // ÿ�������ĸ���ֵ
                    sceneList.get(i).indualScore / sceneList.get(i).fullScore;

//			System.out.print("sceneScore��" + sceneList.get(i).indualScore);
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
//				System.out.println("��ǰѡ��ĳ����Ǳ�������");
            }
            if (randomscore < 50 && randomscore >= 20) {
//				System.out.println("��ǰѡ��ĳ�����ģ�����");
                scene = templatescene;
            }
            if (randomscore < 100 && randomscore >= 50) {
//				System.out.println("��ǰѡ��ĳ������������");
                scene = topicscene;
            }

        } else if (!(topicscene.isEmpty()) && !(templatescene.isEmpty()) && (backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
            System.out.println(randomscore);
            if (randomscore < 40 && randomscore >= 0) {
//				System.out.println("��ǰѡ��ĳ�����ģ�����");
                scene = templatescene;
            }
            if (randomscore < 100 && randomscore >= 40) {
//				System.out.println("��ǰѡ��ĳ������������");
                scene = topicscene;
            }

        } else if (!(topicscene.isEmpty()) && (templatescene.isEmpty()) && !(backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
//			System.out.println(randomscore);

            if (randomscore < 30 && randomscore >= 0) {
                scene = backscene;
//				System.out.println("��ǰѡ��ĳ����Ǳ�������");
            }

            if (randomscore < 100 && randomscore >= 30) {
//				System.out.println("��ǰѡ��ĳ������������");
                scene = topicscene;
            }

        } else if ((topicscene.isEmpty()) && !(templatescene.isEmpty()) && !(backscene.isEmpty())) {
            int randomscore = (int) (Math.random() * 100);
            System.out.println(randomscore);
            if (randomscore < 40 && randomscore >= 0) {
                scene = backscene;
//				System.out.println("��ǰѡ��ĳ����Ǳ�������");
            }
            if (randomscore < 100 && randomscore >= 40) {
                scene = backscene;
//				System.out.println("��ǰѡ��ĳ�����ģ�����");
            }

        } else if ((topicscene.isEmpty()) && (templatescene.isEmpty()) && !(backscene.isEmpty())) {
            scene = backscene;
//			System.out.println("��ǰѡ��ĳ����Ǳ�������");
        } else if ((topicscene.isEmpty()) && !(templatescene.isEmpty()) && (backscene.isEmpty())) {
            scene = templatescene;
//			System.out.println("��ǰѡ��ĳ�����ģ�����");
        } else if (!(topicscene.isEmpty()) && (templatescene.isEmpty()) && (backscene.isEmpty())) {
            scene = topicscene;
//			System.out.println("��ǰѡ��ĳ������������");
        }

        /**
         * �����г������÷������мӺͣ��������ѡ��
         *
         * @author WJJ �õ�sceneListScoreֵΪ��ma1,1,20,ma2,21,40��
         */

        /*
         * ArrayList<String> sceneListScore=new ArrayList();
         *
         * SceneCase temp2 = null; double tempScore=0; double allfullScore=0;
         * for(int i=0;i<sceneList.size();i++){ if(sceneList.get(i).score!=0){
         * temp2=sceneList.get(i); tempScore=allfullScore;
         * allfullScore=allfullScore+temp2.score;//��ÿ��������ֵ�Ӻ�
         * sceneListScore.add(temp2.sceneName);
         * sceneListScore.add(Double.toString((tempScore)));
         * sceneListScore.add(Double.toString(allfullScore));
         * System.out.println("allScore="+allfullScore); } }
         * System.out.println("allScore="+allfullScore);
         */
//		System.out.println("��ʼѡ�񳡾�,�����ĳ���Ϊ��" + scene.size());
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
         * ���г�����ѡ��
         */
        if (allfullScore1 > 0) {
            randomtemp = Math.random() * allfullScore1;
//			System.out.println("randomtemp ����ֵ=" + randomtemp);
            for (int j = 0; j < sceneListScore1.size(); ) {
                double score1 = Double.parseDouble(sceneListScore1.get(j + 1));
                double score2 = Double.parseDouble(sceneListScore1.get(j + 2));
                if (score1 <= randomtemp && randomtemp < score2) {
                    logger.info("ѡ������" + (String) sceneListScore1.get(j) + "===============================");
                    return (String) sceneListScore1.get(j);// ���س�������
                }
                if (j == sceneListScore1.size() && score2 == randomtemp) {
                    logger.info("ѡ������" + (String) sceneListScore1.get(j) + "===============================");
                    return (String) sceneListScore1.get(j);
                }
                j = j + 3;
            }
        }
        /*
         * else{ int m=(int) (Math.random()*(sceneList.size())); return
         * sceneList.get(m).sceneName; }
         */
        logger.info("��δѡ�񳡾�===============================");
        return "";
    }

    /**
     * ��������������ת����Ӣ����������
     * �Ž��࿪ʼ������ topiclist �У�ȫ�ֱ���
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
     * ����ܰ0903
     * <p>
     * ��ڹ滮��������Ĵ����ж�����ѡ�õĳ��������������������
     * 1.��δѡ����ʹ��empty.ma��
     * 2.������Զ����ɵĳ�����
     * 3.����
     *
     * @param model               ����ģ��
     * @param topic               ��������
     * @param templateAttr        ����ģ��
     * @param templateWithColor   ��ɫģ��
     * @param colorMark           ��ɫ��ʶ
     * @param topicFromMG         �������+����
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
        logger.info("========================��ʼ������ڹ滮��������Ĵ���===============================================");
        System.out.println("�������Ϊ:================"
                + "\n[����:" + topic + "],\n[����ģ��:" + templateAttr + "],\n[templateWithColor:" + templateWithColor
                + "],\n[colorMark:" + colorMark + "],\n[topicFromMG:" + topicFromMG
                + "],\n[strNegType:" + strNegType + "],\n[englishTemplate:" + englishTemplate);
        System.out.println("������ӡ���===============");
        getEnglishTopic(topic, model);//��Ӣ������ת����Ӣ������

        if (ProgramEntrance.isMiddleMessage) {
            logger.info("ֻ���м�����ֱ�ӽ�������Ϣ��������ʽ����ճ�����");
            maName = "empty.ma";
        }
        // ���������ģ�嶼Ϊ��ʱ��ֱ�����Nothing.ma�ļ�,����ж��NothingScene�������������ѡһ��ma�ļ�
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
            logger.info("û�г鵽�����ģ����Ϣ��ֱ�����nothing_0.ma�ļ���" + maName);

        }
        // END���������ģ�嶼Ϊ��ʱ��ֱ�����Nothing.ma�ļ�������д��������ģ�嶼Ϊ��ʱ��ֱ�����Nothing.ma�ļ�,����ж��NothingScene�������������ѡһ��ma�ļ�/////////////////////////////////////////////
        if (true) {
            // ������û��ʵ�������ⴴ��һ��ʵ��
            OWLNamedClass topicClass = model.getOWLNamedClass("Topic");//���������
            Collection topicList1 = topicClass.getSubclasses(true);//�������������
            OWLIndividual individual = null;
            for (Iterator itTopic = topicList1.iterator(); itTopic.hasNext(); ) {//����������
                OWLNamedClass classOne = (OWLNamedClass) itTopic.next();//next()
                if (classOne.getSubclassCount() == 0) {//�����������಻���������ࣨҶ�ڵ㣿������
                    if (classOne.getInstanceCount() == 0) {//�����������±�û��ʵ��
                        String individualName = classOne.getBrowserText() + "Individual";
                        individual = classOne.createOWLIndividual(individualName);
//						System.out.println("��������������������������������individualName:" + individualName);
                    }
                }
            }//�������������ࣿ���Ҷ������û��ʵ��
            // END//////////////������û��ʵ�������ⴴ��һ��ʵ��
//			logger.info("Ŀǰ��ѡ��maNameΪ��" + maName);
            if (maName == "") {//�����δѡ������
                maName = "empty.ma";
                logger.info("��δѡ�����������ʹ��empty.ma");
            }
            // maName="Bridge04.ma"; ����ܰ0903
//			logger.info("Ŀǰ��ѡ��maNameΪ��" + maName);
            OWLModel owlModel = null;
            if (maName.contains("Plot.ma")) {//������Զ����ɳ���
                // ����ma�ļ���ma�е�spaceֵ
                logger.info("ѡ�õ����Զ����ɳ���" + maName + "����ma�ļ���ma�е�spaceֵ");
                PlotDesign p = new PlotDesign();

                p.GenerateMa(maName, model);

                System.out.println("bjut.plot �е�topic:" + maName.substring(0, maName.length() - 3));

                // System.out.println("bjut.plot �е�topic:"+plotTopic);

                owlModel = new PlotAddModelToMa().processSWRL2(maName, maName.substring(0, maName.length() - 3), model,
                        englishTemplate, englishTemplatePlot);// ���plot�е�ģ�͡��Ž�ontology������
                // owlModel=new
                // PlotAddModelToMa().processSWRL2(maName,plotTopic,model,englishTemplate,englishTemplatePlot);//���plot�е�ģ�͡��Ž�ontology������

                String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";// �޸���AllOWLת��

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
                // ���޸���AllOWLת��
                saveOWLFile((JenaOWLModel) owlModel, fileName);
            }
//			logger.info("record SceneName " + maName);
            FileWriter SceneRec = new FileWriter("C:/ontologyOWL/SceneRecord.txt", true);
            SceneRec.write(maName + " ");
            SceneRec.close();
            printToXML(owlModel, maName, englishTopicStr, strNegType);
            logger.info("========================������ڹ滮��������===============================================");
            System.out.println("\n\n\n");
        }
    }

    // ����ܰ
    public static void PrintSceneCase() {
        int njnum = 0;
        double allfullScore1 = 0;
        String SceneResPath = XMLInfoFromIEDom4j.writeXML("SceneCasePath.xml");
        Document doc = XMLInfoFromIEDom4j.readXMLFile(SceneResPath);// ���Ҫ�����XML�ļ���ͷ��
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
        SceneCondition.addAttribute("allFullScore", Double.toString(allfullScore1));// ���ܷ�ֵд�볡��
        SceneCondition.addAttribute("SelectedScore", Double.toString(randomtemp));
        // �����ʾȨֵ������
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

        double allFullScore = 0;// �����÷ֺ�
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

                /****** ����ܰ ******/
                Element decisionvalue = scene.addElement("decisionvalue");
                decisionvalue.addAttribute("decisionvalue", Double.toString(sceneList.get(i).decisionvalue));
                /****** ����ܰ ******/

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

                Element ScoreSegment = scene.addElement("ScoreSegment");// ��ֵ��
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
//			logger.info("������ƥ����");
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
                System.out.println("������õ���ģ��:" + addModelToSpaceList);
                boolean flage = false;
                if (maIndividual.getPropertyValueCount(hasSceneSpaceProperty) > 0) {
                    Collection cols = maIndividual.getPropertyValues(hasSceneSpaceProperty);//��ȡ������hasSceneSpaceProperty�����ĳ�������
                    for (Iterator is = cols.iterator(); is.hasNext(); ) {//���������µ����п��ÿռ�
                        OWLIndividual ind = (OWLIndividual) is.next();//next();���ÿռ�
                        if (ind.getPropertyValueCount(hasPutObjectInSpaceProperty) > 0) {
                            Collection cla = ind.getPropertyValues(hasPutObjectInSpaceProperty);//��ȡ���ÿռ���hasPutObjectInSpaceProperty���Թ�����ģ��

                            for (Iterator it = cla.iterator(); it.hasNext(); ) {//�������ÿռ������ģ��
                                OWLIndividual spaceModel = (OWLIndividual) it.next();

                                ArrayList arr = new ArrayList();//��š��������ģ�͡��롰������ÿռ�ɷ����ģ�͡��Ľ���
                                for (int ig = 0; ig < addModelToSpaceList.size(); ig++) {//����������õ���ģ��
                                    if (addModelToSpaceList.get(ig).equals(spaceModel)) {//���������õ���ģ������ÿռ������ģ����ͬ
                                        arr.add(addModelToSpaceList.get(ig));//�����ͬ�ͷŽ�ȥ
                                    }
                                }
                                System.out.println("����õ���ģ��" + addModelToSpaceList + "���Էŵ����ÿռ���ǣ�" + ind.getBrowserText());
                                count = setNumberToAddModel(new ArrayList(), arr, model, ind, count, topicName);
                            }//end �������ÿռ������ģ��
                        }
                    }
                }

            }
        }
        return count;
    }

    /**
     * ͨ��ö����������ѡ�����Ŀռ����ģ�ͣ� ʹ�����ж����Ч������ģ������ⶼ�޹�
     * �������������
     * 1.����ǿճ�����ֱ�ӷ���
     * 2.������ǿճ���...
     * 1�����������µĿ��ÿռ䣺
     * (1)AxiomClass��������༯������Axiom���࣬�����ֵ���ϣ�OWLEnumeratedClass.getOneOfValues()����������ÿռ� ����/ʵ���� ��ͬ��ֵ��flag����Ϊtrue��break
     * (1.5)--��
     *
     * @param model
     * @param maName
     * @return
     */

    @SuppressWarnings("unchecked")
    public static OWLModel addModelFromEnumerateClass(OWLModel model, String maName, int count) {
//		System.out.println("count=" + count);
        if (maName.startsWith("nothing") || maName.startsWith("empty.ma")) {//����ǿճ���
            return model;//�������ģ�ͣ�ֱ�ӷ���
        } else {//������ǿճ���
//			logger.info("AxiomClass�õ���ģ��");
            OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");//�������ԣ��п��ÿռ�
            OWLObjectProperty hasUsableModelProperty = model.getOWLObjectProperty("hasUsableModel");//�������ԣ�����ģ��
            OWLIndividual maIndividual = model.getOWLIndividual(maName);//��������maIndividual��
            // ������е�space
            Collection maSpaces = maIndividual.getPropertyValues(hasSceneSpaceProperty);//�����¿��ÿռ伯�ϣ�maSpaces��
            Iterator its = maSpaces.iterator();
            OWLEnumeratedClass enumeratedClass = null;
            while (its.hasNext()) {//�����µĿ��ÿռ�
                boolean isHasEnumClass = false;
                ArrayList<OWLIndividual> allIndividualCollection = new ArrayList();
                OWLIndividual spaceIndividual = (OWLIndividual) its.next();//next();ȡһ�����ÿռ����==============

                OWLNamedClass spaceClass = model.getOWLNamedClass("AxiomClass");//AxiomClass�����
                Collection subSpaceClassList = spaceClass.getSubclasses(true);//AxiomClass��������༯��===========
                Iterator spaceIts = subSpaceClassList.iterator();//AxiomClass��������༯��
                RDFSClass subSpaceClass = null;
                loop:
                while (spaceIts.hasNext()) {//����AxiomClass���������
                    subSpaceClass = (RDFSClass) spaceIts.next();//next()
                    String strSubSpaceClass = subSpaceClass.getBrowserText();//����������
                    if (strSubSpaceClass.startsWith("Axiom")) {//�������Axiom��ͷ=================
                        Collection enumeList = subSpaceClass.getEquivalentClasses();//��ȡ���ÿռ伯��

                        Iterator enumeListIts = enumeList.iterator();
                        while (enumeListIts.hasNext()) {//����AxiomClass��������༯������Axiom��ʼ����=================
                            enumeratedClass = (OWLEnumeratedClass) enumeListIts.next();//next();

                            Collection enumOneOfList = enumeratedClass.getOneOfValues();
                            Iterator enumOneOfListIts = enumOneOfList.iterator();
                            while (enumOneOfListIts.hasNext()) {//����������ĳ��ֵ
                                OWLIndividual oneOfIndividual = (OWLIndividual) enumOneOfListIts.next();//next()
                                if (oneOfIndividual.getBrowserText().equals(spaceIndividual.getBrowserText())) {
                                    isHasEnumClass = true;
                                    System.out.println("�����Ŀ��ÿռ�" + oneOfIndividual.getBrowserText() + "��ö����Axiom������");
//									System.out.println("subSpaceClass:" + subSpaceClass.getBrowserText());
                                    // System.out.println("enumeratedClass:"+enumeratedClass.getBrowserText());
                                    break loop;
                                }
                            }

                        }

                    }

                }
                if (isHasEnumClass) {//���ÿռ�������ö��������Axio��ͷ��oneOfValue��ͬ��
                    OWLNamedClass aa = (OWLNamedClass) subSpaceClass;//��ת��
                    int random = 1;
                    if (aa.getBrowserText().toString().contains("Air")) {//����������� Air
                        random = (int) Math.random();
                        System.out.println("�������ÿռ�" + aa.getBrowserText().toString() + "ΪAir���ͣ��������Axiomģ��");
                    }
                    if (random == 1) {//��������Ϊ1
                        RDFResource hasUsableModelClass = aa.getAllValuesFrom(hasUsableModelProperty);//���ö�����±߿��õ�ģ����
                        String strHasUsableModelClass = hasUsableModelClass.getBrowserText();
                        String[] strHasUsableModelClassSplit = strHasUsableModelClass.split(" or ");//��ö�����±߿��õ�ģ����ŵ��������
                        for (int i = 0; i < strHasUsableModelClassSplit.length; i++) {//����ö������ÿռ��µ�ģ����
                            String modelClassStr = strHasUsableModelClassSplit[i].trim();
//							System.out.println("modelClassStr: " + modelClassStr);
                            OWLNamedClass modelClass = model.getOWLNamedClass(modelClassStr.trim());//�õ���Ӧ��ģ����
                            // modelClass.getins
                            if (modelClass != null) {
                                Collection modelClassIndividuals = modelClass.getInstances(true);//�õ����ģ�����±ߵ�ģ��ʵ��
                                if (modelClassIndividuals.size() > 0) {
                                    allIndividualCollection.addAll(modelClassIndividuals);//��ģ�����±�����ʵ�嶼�Ž�allIndividualCollection�����У�ArrayList<OWLIndividual>
//									System.out.print("ok");
                                }
                            }
                        }// end of ����ö������ÿռ��µ�ģ����
                        int kk = 0;
                        if (allIndividualCollection.size() > 5)//������ģ�����Ѿ��Ž�����5��ģ�ͣ�kk=5;����kk=��������
                            kk = 5;
                        else
                            kk = allIndividualCollection.size();
                        Random rand = new Random();
                        int kkk = rand.nextInt(kk);//kkkΪ [0,kk]֮�����
//						System.out.println("kkk:" + kkk);
                        HashSet<Integer> set = new HashSet<Integer>();// ���ܵĹ��������ѡ��kk������
                        for (int i = 0; i <= kkk; i++) {
                            int t = (int) (Math.random() * allIndividualCollection.size());
                            set.add(t);//set�������kkk�������
                        }
                        ArrayList<OWLIndividual> addModelToSpaceList = new ArrayList();
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {//����set����
                            Integer num = (Integer) iterator.next();
                            if (allIndividualCollection.get(num).getBrowserText().contains(".ma")) {//���ģ��������ѡ������Ԫ��ʱģ��
                                // �ж�ģ�͵��Ƿ���Է���ma�����У���ģ��location���Լ���-��isEquivalOfֵ�Ƿ��볡��hasValueOfPlace����ֵ��ͬ
                                boolean flage = isOwdToMa(model, maName, allIndividualCollection.get(num));//
//								System.out.println(flage);
                                if (flage == true) {// �ж�ģ�͵��Ƿ���Է���ma�����У���ģ��location���Լ���-��isEquivalOfֵ�Ƿ��볡��hasValueOfPlace����ֵ��ͬ
                                    addModelToSpaceList.add(allIndividualCollection.get(num));
                                    System.out.println(maName + "�Ŀ��ÿռ�(hasSceneSpace):" + spaceIndividual.getBrowserText() + "������ö����(Axiom)���ҵ������Ҵ�ö�����Ӧ��ģ��"
                                            + allIndividualCollection.get(num).getBrowserText() + "���Է��õ�λ����ma�������ÿռ��Ӧ:ģ�͵�:location����-->isEquivalOf����=������hasValueOfPlace");
                                }

                            }

                        }
                        count = setNumberToAddModel(new ArrayList(), addModelToSpaceList, model, spaceIndividual, count,
                                "");
                    }//end of �����Ϊ1
                }//end of �����Ŀ��ÿռ���ö������

            }//end of ���������µĿ��ÿռ�
        }
        return model;
    }

    /**
     * ͨ������ȡ��ma����������ѡ�ı�������û������Ŀռ�ʱ����ѡ�������ĳ���
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
        Collection individualList = backGroundClass.getInstances(true);// ������µ�����ʵ��

        Collection parentClassList = fatherClass.getSuperclasses(true);// ���������и���
        Iterator<OWLNamedClass> its = parentClassList.iterator();
        boolean isBackground = false;
        while (its.hasNext()) {
            OWLNamedClass ppClass = its.next();
            if (ppClass.getBrowserText().equals("BackgroundScene")) {
                isBackground = true;
                break;
            }
        }

        // s��ѡ�ı����������õĳ����ռ�Ϊ0
        Collection spaceList = maIndividual.getPropertyValues(usedSpaceInMaProperty);
        if (isBackground) {
            logger.info("��ѡ�ĳ���Ϊ��������");
            ArrayList<OWLIndividual> individualList2 = new ArrayList();
            if (spaceList.size() == 0 && individualList.size() > 1) {
                logger.info("��ѡ�ı�������  " + maName + "  û�����õĿɷ���ռ䣬���������ֵ�ʵ��");
                Iterator<OWLIndividual> its1 = individualList.iterator();
                while (its1.hasNext()) {
                    OWLIndividual individualP = its1.next();
                    Collection usedSpaceList1 = individualP.getPropertyValues(usedSpaceInMaProperty);
                    if (!individualP.getBrowserText().equals(maName) && usedSpaceList1.size() > 0) {
                        individualList2.add(individualP);
                    }
                }
                if (individualList2.size() > 0) {
                    logger.info("�ֵ�ʵ�������õĿɷ���ռ�");
                    Random rand = new Random();
                    int k = rand.nextInt(individualList2.size());
                    maName1 = individualList2.get(k).getBrowserText();
                    return maName1;
                } else {
                    logger.info("��Ȼ���ֵ�ʵ�������ֵ�ʵ��Ҳû�����õĿɷ���ռ䣬��ѡ��ճ���");
                    return "empty.ma";
                }
            } else if (spaceList.size() == 0 && individualList.size() == 1) {
                logger.info("û���ֵ�ʵ����������û�����õĿɷ���ռ䣬��ѡ��ճ���");
                return "empty.ma";
            } else {
                logger.info("��ѡ�ı��������п��õĿռ�");
                return maName;
            }

        } else
            return maName;

    }

    /**
     * fixme ͨ��ģ��ԭ��������ģ��ԭ������Ӧ��ģ�ͣ�hasModelFromTemplate��,Ӧ������ΪgetModelFromEnglishTemplate
     * fixme ����˴�DBpedia�л�ȡģ�͵Ĵ���
     *
     * @param modelx
     * @param englishTemplate
     * @return
     */
    public static ArrayList<String> getIndividualFromEnglishTemplate(OWLModel modelx, ArrayList<String> englishTemplate) {


        ArrayList<String> individualList = new ArrayList();
        OWLObjectProperty hasModelFTpProperty = modelx.getOWLObjectProperty("hasModelFromTemplate");
        for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); )// �������е�ģ��ԭ��
        {
            String templateAllName = its.next();
            int iPostion = templateAllName.indexOf(":");
            String[] temp = templateAllName.split(":");

            String templateAutmName = templateAllName.split(":")[temp.length - 1];
            // String templateAutmName1=temp.get();
            if (!templateAllName.equals(templateAutmName)) {
                OWLIndividual templateIndividual = modelx.getOWLIndividual(templateAutmName);
                if (!templateIndividual.equals(null))// �鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
                // if(!templateIndividual.equals(null))//�鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
                {
                    OWLObjectProperty hs = modelx.getOWLObjectProperty("hasModelFromTemplate");
                    int k = templateIndividual.getPropertyValueCount(hs);
                    // System.out.println(k);
                    int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
                    if (valueNum > 0)// ��Ӧ��model�����Ƿ����0
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
//        //20180527:��ʱ֮ǰ�򿪵�model����ò���Ѿ����޸ģ��ʶ�ȡ������������
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
//		//fixme ʹ��ģ��ʵ�������������ģ����ص�model
//		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate"),
//				broHasModelFTpProperty = modelx.getOWLObjectProperty("hasModelFromTemplate");
//
//		Iterator<String> its = englishTemplate.iterator();
//		while ( its.hasNext()){// �������е�Ӣ��ģ��
//			String templateAllName = its.next();//��ǰӢ��ģ��ȡֵ
//			String[] temp = templateAllName.split(":");
//			String templateAutmName = templateAllName.split(":")[temp.length - 1];
//
//			if (!templateAllName.equals(templateAutmName)) {
//				OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName),
//                        broTemplateIndividual=modelx.getOWLIndividual(templateAutmName);
//				if (!templateIndividual.equals(null)){// �鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
//
//					//fixme ����ģ���Ƿ�ͨ��hasModelFromTemplate��������ģ�ͣ�û�еĻ���else�߼���ʹ���Ѿ��ı��model����
//					if (templateIndividual.getPropertyValueCount(hasModelFTpProperty) > 0){// fixme ���ģ���й�����ģ�ͣ���������ģ��
//                        Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);
//
//                        Iterator<OWLIndividual> its2 = templateModelVlaues.iterator();
//                        while (its2.hasNext()) {
//                            String value = its2.next().getBrowserText();
//                            resIndividualList.add(value);//fixme ģ����ص�ģ�ͼ���ģ��������
//                        }
//                    }else{
//                    	//fixme ���ֵܽڵ���ѡ��
//                        Collection templateModelVlaues = broTemplateIndividual.getPropertyValues(broHasModelFTpProperty);
//                        Iterator<OWLIndividual> its2 = templateModelVlaues.iterator();
//                        while (its2.hasNext()) {
//                            String value = its2.next().getBrowserText();
//                            resIndividualList.add(value);//fixme ģ����ص�ģ�ͼ���ģ��������
//                        }
//                    }
//                } else
//					continue;
//			}
//
//		}//���¼��д�����ȥ��
//		HashSet h = new HashSet();
//		h.addAll(resIndividualList);
//		resIndividualList.clear();
//		resIndividualList.addAll(h);
//
//		return resIndividualList;
    }

    /**
     * ��ģ��õ���������
     *
     * @param model
     * @param englishTemplate
     * @return
     * @throws SWRLRuleEngineException
     */
    public static String getMaFromTemplate(OWLModel model, ArrayList<String> englishTemplate)
            throws SWRLRuleEngineException {
        String maName = "";
        logger.info("���ģ���ж�Ӧ�Ķ�����������ͨ��ģ��ѡ�񶯻�����");
        maName = getAnimationSceneFromTemplateUsingSWRL(model, englishTemplate);
        if (maName.equals("") || maName.equals(null)) {
            maName = getAnimationSceneFromTemplate(model, englishTemplate);
            if (maName.equals("") || maName.equals(null)) {
                logger.info("���й�����ģ��ԭ��û���Ƴ����⣬��ѡ�񱳾�������ճ���");
                boolean bHasModelFromTemplate = false;
                OWLObjectProperty hasModelFromTemplateProperty = model.getOWLObjectProperty("hasModelFromTemplate");
                OWLObjectProperty hasBackgroundSceneProperty = model.getOWLObjectProperty("hasBackgroundScene");
                for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); )// �������е�ģ��ԭ��
                {
                    String templateAllName = its.next();
                    int iPostion = templateAllName.indexOf(":");
                    String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());

                    OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
                    if (!templateIndividual.equals(null))// �鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
                    {
                        int num = templateIndividual.getPropertyValueCount(hasModelFromTemplateProperty);
                        if (num > 0) {
                            bHasModelFromTemplate = true;
                            break;
                        }

                    }
                }
                if (bHasModelFromTemplate)// ��ģ��ԭ���ж�Ӧ��ģ��ʱ����ѡ�񱳾�����
                {
                    OWLNamedClass backgroundClass = null;
                    ArrayList<String> BackgroundSceneName = new ArrayList();// ��������ÿ�ζ������鵽ģ������Ӧ�����б���������
                    for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); ) {
                        String templateAllName = its.next();
                        int iPostion = templateAllName.indexOf(":");
                        String templateTemp = templateAllName.substring(0, iPostion);
                        OWLNamedClass templateNClass = model.getOWLNamedClass(templateTemp);//
                        logger.info("���ģ����:" + templateNClass.getBrowserText() + "����Ӧ��hasBackgroundScene��������ֵ");

                        RDFResource resource = templateNClass.getSomeValuesFrom(hasBackgroundSceneProperty);
                        if (resource != null)// ��Щģ����û��hasBackgroundScene��ֵ
                        {
                            String hasValues = resource.getBrowserText();// ��������Ӧ�����ֵ�����
                            String[] hasValuesSplit = hasValues.split("or");
                            if (hasValuesSplit.length > 1) {// ���ж��������ʱ�����ж�ÿ���������Ƿ���ʵ��
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
                    logger.info("���ѡ�ı�����������:" + backgroundClass.getBrowserText());
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
     * ͨ��ģ�����ù����Ƶ�������ģ����Ϣ�Ķ�������
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
     * ����Ϣ��ȡû�г鵽���⣬ģ��Ҳû���Ƴ����⣬����ģ��ͨ��hasAnimationNameFromTemplate
     * ���ɷ�õ���Ӧ�Ķ���������ֱ��������ֵ�ó���
     *
     * @param model
     * @param englishTemplate
     * @return
     */


    /**
     * ma֮ǰ�ȴ���addToMa,exChangedModelInMa���ԣ���Ϊͨ�������Ƶ�����ĳ���������ж��ʵ��������������ӹ����Ƶ��󣬻�
     * �����е�ʵ���������ȥ����ʵ����ֻҪ����ʵ���е������ĳ��
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

            // ��������update����ʹ�����е�ĳ��objetֻ�ܱ���Ϊ���б�ѡ����Ըĵ�ʵ�����е�һ��
            if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0)// �ж�usedModelInMa����
            {
                Collection usedModelValues = maIndividual.getPropertyValues(usedModelInMaProperty);
                for (Iterator iValues = usedModelValues.iterator(); iValues.hasNext(); ) {
                    OWLIndividual usedModelIndividual = (OWLIndividual) iValues.next();
                    // �ж�ÿ��usedModelInMa���Ե�exChangedModelInMa����
                    Collection exChangedModelInMaValues = usedModelIndividual
                            .getPropertyValues(exchangedModelInMaProperty);
                    ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// �������ÿ��space�ϴ�ŵ�����
                    // ͨ��Map��<key��value>��ֵ���������ĳ��������Ķ��ʵ��������
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
                    Set<OWLNamedClass> keys = map.keySet();// �������ÿ����
                    Object[] choosedClassNums = keys.toArray();
                    int length = choosedClassNums.length;
                    Random rand = new Random();
                    Date date = new Date();
                    rand.setSeed(date.getTime());
                    int kk = rand.nextInt(length);
                    OWLNamedClass classNmae = (OWLNamedClass) choosedClassNums[kk];// ����������ѡһ��
                    ArrayList<OWLIndividual> values2 = map.get(classNmae);// ���exChandedModelInMa��ĳ�������������ʵ��
                    int k2 = rand.nextInt(values2.size());
                    OWLIndividual changedIndividual = values2.get(k2);
                    usedModelIndividual.setPropertyValue(exchangedModelInMaProperty, changedIndividual);

                }
            }
            Collection hasModelList = maIndividual.getPropertyValues(hasModelProperty);

            // ArrayList hasModelList=(ArrayList)hasModelList2.;
            if (maIndividual.getPropertyValueCount(deleteProperty) > 0)// ����ɾ�������е�ģ��
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
            // ��hasModel��ɾ�����Ĺ����е�ģ��
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
            // ��usedSpaceInMa���֣���Ҫ����addTomMa����
            if (maIndividual.getPropertyValueCount(usedSpaceInMaProperty) > 0) {

                Collection usedSpaceValues = maIndividual.getPropertyValues(usedSpaceInMaProperty);
                for (Iterator iValues = usedSpaceValues.iterator(); iValues.hasNext(); ) {// iVlaues��space������
                    OWLIndividual spaceIndividual = (OWLIndividual) iValues.next();
                    Collection objectInSpaceValues = spaceIndividual.getPropertyValues(hasPutObjectInSpaceProperty);
//					System.out.println("qqqqqqqqqqqqqqqqqqqq spaceIndividual:" + spaceIndividual);
//					System.out.println("qqqqqqqqqqqqqqqqqqqq objectInSpaceValues:" + objectInSpaceValues);

                    // objectInSpaceValues���Է���ռ��ģ���б�
                    ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// �������ÿ��space�ϴ�ŵ�ģ�͸����OWLIndividual
                    ArrayList<OWLIndividual> outterIndividualList = new ArrayList();// ������Ų�����ģ������Ƴ���������ӵ�ģ��

                    // ͨ��Map��<key��value>��ֵ���������ĳ��������Ķ��ʵ��������
                    Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();

                    for (Iterator iiValues = objectInSpaceValues.iterator(); iiValues.hasNext(); ) {// iiValuesÿ��space����ŵ�����
                        OWLIndividual objectIndividual = null;
                        if (name.equals("empty.ma"))
                            objectIndividual = model.getOWLIndividual(iiValues.next().toString());
                        else
                            objectIndividual = (OWLIndividual) iiValues.next();

                        Iterator itd = individualListFromTemplate.iterator();// ģ���Ӧ��ģ���б�
                        boolean isEqualTemplate = false;
                        // �ȶ�ģ�����֣���ģ���Ӧ��ģ�ͷ���innerIndividualList�����������
                        while (itd.hasNext()) {
                            // isEqualTemplate=false;
                            // ��ģ���Ӧ��ģ��һ��Ҫ�ӵ�addToMa��
                            String individualStr = (String) itd.next();
                            if (individualStr.equals(objectIndividual.getBrowserText())) {
                                innerIndividualList.add(objectIndividual);
                                individualList.add(objectIndividual);
                                isEqualTemplate = true;
                            } else
                                continue;
                        }

                        // ���������ģ����ص�ģ�ͣ���ӵ�outterIndividualList
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
                System.out.println("������" + topicName + "ѡȡģ��");
                count = addModelFromTopic(model, maName, topicName, count);
                maIndividual.setPropertyValue(topicNameProperty, topicName);
//				System.out.println("****************#topicName#####:" + maIndividual.getPropertyValue(topicNameProperty));
            }

        }
        logger.info("��ʼͨ��Axiom��������ģ��=============================");
        model = addModelFromEnumerateClass(model, maName, count);// axiom��ѡ��ģ��
        logger.info("��ʼͨ��Axiom��������ģ��=============================");
        return model;
    }


    /**
     * ��ÿ��space����ӵ�ģ�ͽ��б��, ��ź�ͬʱ��ӵ���AddModelRelated������
     *
     * @param individualList  ģ������ÿ��space������ӵ�ģ��
     * @param randIndiList
     * @param model           OWLģ�Ͷ���
     * @param spaceIndividual ���ÿռ�
     * @param count
     * @param topicName
     * @return
     */
    public static int setNumberToAddModel(ArrayList<OWLIndividual> individualList,
                                          ArrayList<OWLIndividual> randIndiList, OWLModel model, OWLIndividual spaceIndividual, int count,
                                          String topicName) {
//		System.out.println(topicName);
        OWLNamedClass addModelRelatedClass = model.getOWLNamedClass("AddModelRelated");//��������
        OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");//��������
        OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");//��������
        OWLDatatypeProperty isTempObject = model.getOWLDatatypeProperty("isTemplateObject");//��������
        OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");//��������
        OWLDatatypeProperty addModelNumberProperty = model.getOWLDatatypeProperty("addModelNumber");//��������
        OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");//��������
        OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");//��������
        // 20170508 Yangyong
        OWLDatatypeProperty isDeal = model.getOWLDatatypeProperty("isUsed");//��������
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
                    if (addModelValue.getBrowserText().equalsIgnoreCase("m_floor.ma")) {// ��������Ϊ0
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
     * ����ӵ�ģ�����ID
     *
     * @param individualList:��ӵ�ģ��list
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
     * ���ݹ���������������ѡ�������������
     *
     * @param maName��ma�ļ�������
     * @param topicName�����������
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
            // ��ѡ���ma��empty.ma,ʱ�����ȿ����ǲ�����ģ�������empty.ma����Ӷ�Ӧ��ģ��
            boolean isOK = SWRLMethod.executeSWRLEnginetoEmptyMa(model, "addModelToEmpty.ma", topicName,
                    englishTemplate);
            // ��ԭ������Ӧ��ģ����ӵ��ճ�����
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
                for (Iterator<String> its = englishTemplate.iterator(); its.hasNext(); )// �������е�ģ��ԭ��
                {
                    String templateAllName = its.next();
                    int iPostion = templateAllName.indexOf(":");
                    // String
                    // templateAutmName=templateAllName.substring(iPostion+1,
                    // templateAllName.length());
                    String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length() - 4);
                    OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);

                    if (templateIndividual != null)// �鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
                    // if(!templateIndividual.equals(null))//�鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
                    {
                        int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
                        if (valueNum > 0)// ��Ӧ��model�����Ƿ����0
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

            // ���hasMa,hasTopic����ֵ��Ҳ������ģ��ԭ���йصĹ���
            if (bIsBackgroundScene) {
                logger.info("�������������������ģ�͵Ĺ���");
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
     * ���������汾2��
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
            boolean isBackgroundScene = false;// first�������ж��Ƿ��Ǳ�������
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
     * ����������
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
        boolean isBackgroundScene = false;// first�������ж��Ƿ��Ǳ�������
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
        if (isBackgroundScene && copyIndividual.getPropertyValueCount(addToMaProperty) > 0) {// ����ǿճ��������ӡ�ճ�����addToMa���е�ֵ
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

        } else {// ���Ǳ��������Ļ�����Ҫ����hasmodel��addToMa���������
            ArrayList<String> humanList = new ArrayList();

            // �ж��������
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
                // �ж�hasmodel����
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
     * ͨ������ѡ����music,
     *
     * @param englishTopic:����
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

        if (englishTopic.equals("") || englishTopic == null)// û��ѡ������
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
            logger.info("ͨ�������������������Ӧ��ma���������֣�����������Ӧ������������" + resource.getBrowserText());
            String hasValues = resource.getBrowserText();// ��������Ӧ�����ֵ�����
            String[] hasValuesSplit = hasValues.split("or");// ���ܶ�Ӧ���������
            ArrayList<String> hasValuesClass = new ArrayList();
            OWLNamedClass resourceClass = null;
            if (hasValuesSplit.length > 1) {// ���ж��������ʱ�����ж�ÿ���������Ƿ���ʵ��
                for (int i = 0; i < hasValuesSplit.length; i++) {
                    OWLNamedClass resourceClass0 = model.getOWLNamedClass(hasValuesSplit[i].trim());
                    int instanceCount0 = resourceClass0.getInstanceCount();
                    if (instanceCount0 > 0)
                        hasValuesClass.add(hasValuesSplit[i].trim());
                }
                if (hasValuesClass.size() > 0)// ����������඼��ʵ��ʱ�������ѡ��һ��
                {
                    rand.setSeed(date.getTime());
                    int kk = rand.nextInt(hasValuesClass.size());
                    resourceClass = model.getOWLNamedClass(hasValuesClass.get(kk));
                } else// ����������඼û��ʵ��ʱ�������ѡ��һ����������
                {
                    int kk = rand.nextInt(hasValuesSplit.length);
                    resourceClass = model.getOWLNamedClass(hasValuesSplit[kk].trim());

                }
            } else
                // ����ֻ��һ������������
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
     * ��ӡ��
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
        Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);// ���Ҫ�����XML�ļ���ͷ��
        Element rootElement = doc.getRootElement();
        Element name = rootElement.addElement("maName");//���maName�ڵ�
        OWLIndividual copyIndividual = model.getOWLIndividual(maName);//��������
        OWLDatatypeProperty maSenceNameProperty = model.getOWLDatatypeProperty("maSceneName");//��������maSceneName
        OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");//�����������topicName

        // String
        // maName=(String)copyIndividual.getPropertyValue(maSenceNameProperty);
        // String
        // topicClassName=(String)copyIndividual.getPropertyValue(topicNameProperty);
        name.addAttribute("name", maName);//��maName�ڵ����ֵ��name=�������ơ�
        name.addAttribute("topic", topicName);//��maName�ڵ����ֵ��topic=ѡ�����⡱
        String musicName = getMusic(topicName, model);
        name.addAttribute("music", musicName);//��maName�ڵ����ֵ��music=ͨ������ѡ�������֡�

//		if (maName.equals("nothing.ma"))// 2017.5.31�������������8042��8044�����
//			name.addAttribute("maFrame", "300");

        // *******************��ӡ��ӹ���*****************
        OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");//��������addToMa

        doc = printCycleAddRuleVersion2(doc, model, copyIndividual, englishTemplate, colorModelNum, colorChangeAttr);
        // ��ӡ��ӹ���
        doc = printExchangeRule(doc, model, copyIndividual);
        doc = printDeleteRule(doc, model, copyIndividual);
        doc = printTimeToClock(doc, model, copyIndividual, englishTemplate);

        // doc=printCameraVersion2(doc,model,copyIndividual);
        // doc=printNegType(doc,strNegType);
        // doc=printBackgroundPicture(doc,model,copyIndividual);

        String individualName = maName;
        /* �������������� */
        String uri = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
        // String uri = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
        boolean flage = false;
        boolean flage1 = false;
        boolean flage2 = false;
        boolean ifInterFlage = false;
        ArrayList expressiontopic = new ArrayList();
        expressiontopic = topiclist;

        ArrayList scene = new ArrayList();//��ų�����hasSceneSpace����ֵ
        OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");//��������
        if (copyIndividual.getPropertyValueCount(hasSceneSpace) != 0) {//���������hasSceneSpace����
            if (copyIndividual.getPropertyValueCount(hasSceneSpace) == 1) {//�������ֻ��һ��hasSceneSpace����
                scene.add(copyIndividual.getPropertyValue(hasSceneSpace));
            } else if (copyIndividual.getPropertyValueCount(hasSceneSpace) > 1) {//�����ж��hasSceneSpace����
                Collection clo = copyIndividual.getPropertyValues(hasSceneSpace);//��ȡ������Ӧ�Ķ��hasSceneSpace
                for (Iterator it = clo.iterator(); it.hasNext(); ) {//����������hasSceneSpace����ӽ�scene
                    scene.add(it.next());
                }
            }
        }
        for (int k = 0; k < scene.size(); k++) {//���������Ķ��hasSceneSpace���ԣ��鿴����ֵ��Ӧ�����Ƿ��а�����Ground��
            OWLIndividual scenespace = (OWLIndividual) scene.get(k);
            OWLNamedClass place = (OWLNamedClass) scenespace.getDirectType();//��ȡ���Զ�Ӧ����
            String s = place.getBrowserText().toString();
            if (s.contains("Ground")) {//�鿴�Ƿ��������
                flage2 = true;
                break;

            }
        }
        if (flage2 == false) {
            logger.info("û�е�����ÿռ�");
        }


        // 20170508 Yangyong

        if (!maName.equals("clock.ma") && flage2 == true) {//�������ʱ�ӳ��������е���ռ����

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
                //�ֽ׶β���ʹ�ã����ڸ������������Ķ���������ʹ����ɽ����˶�
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
//			 if(ifInterFlage==false){//���û�н��������������

            try {
                logger.info("===========Event begin,����ģ�壺" + actionTemplateAttr + "=======================");
                Plot plotplan = new Plot();
//				System.out.println("topiclist=" + topiclist);
//				System.out.println("actionTemplate=" + actionTemplateAttr);

                doc = plotplan.EventInfer(topiclist, actionTemplateAttr, owlModel, maName, doc);
                flage1 = plotplan.ifContainEvent();
                System.out.println("�Ƿ�鵽��Ӧ���¼���" + flage1);
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
            //����
            try {
                logger.info("Expression begin,ģ�����ƣ�" + ExpressionList);
                Expression shock = new Expression();
//				System.out.println("XUXH��Ҫʵ����" + individualName + "ģ�����ƣ�" + ExpressionList);
//				System.out.println(topiclist.size());
                //	doc = shock.ShockXml(ExpressionList, expressiontopic, model, maName, doc);//1024���л������д��룻
                doc = shock.ShockXml(ExpressionList, model, maName, doc);
                logger.info("Expression finish");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ERROR: Expression Exception");
            }

        } else {//���֡��
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
            // System.out.println("ģ��"+moodTemplateAttr.get(0));
            logger.info("fog begin");

            fogInsert tt = new fogInsert();

            doc = tt.fogInfer(weatherAndmoodAttr, owlModel, individualName, doc);
            logger.info("fog finish");
        } catch (Exception exJiali) {
            logger.info("ERROR: fog Exception");
        }
        ////////////////////////////////////////////////////////////////////////
        // �ֺ����ӷ�ĳ��� p2
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
        // nidejuan���� p
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
        // ���� ���p5

        // ���� �̻���2017.11.07�޸ĳ���
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
        // ���� ������
        try {

            logger.info("MakeBoatsCheck begin");
            MakeBoats makeBoats = new MakeBoats();
            doc = makeBoats.makeBoatsInfer(englishTemplate, model, individualName, doc);
            logger.info("MakeBoatsCheck end");
        } catch (Exception ex) {
            logger.info("Error: MakeBoatsCheck Exception.");

        }

        //����  �
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
            System.out.println("�����ʵ��" + maName);
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
     * ���ֺ����ĳ�ȡ��������Ϊ����ѡȡweatherableʱ�����ݣ�����������������weatherable��Ϊtrue������Ϊfalse
     * owlModel ��ALLOWLFile�е�sumowl2
     *
     * @param individualName ѡȡ�ĳ���������
     * @param doc            ���ɵ�ADL�ĵ�
     */

    public static void WRSGernate(OWLModel owlModel, String individualName, Document doc) {
        // �ֺ����ӷ�ĳ��� p2
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
     * ��IE��ȡ�г鵽ʱ��ʱ������clock_clock���ģ�������ʱ��
     *
     * @param doc            ADL���
     * @param model
     * @param copyIndividual ��������
     * @param templateAttr   Ӣ��ģ��
     * @return
     */
    public static Document printTimeToClock(Document doc, OWLModel model, OWLIndividual copyIndividual,
                                            ArrayList<String> templateAttr) {
        if (templateAttr.size() == 0)//���Ӣ��ģ��Ϊ�գ�ֱ�ӷ���
            return doc;
        else {
            Iterator its = templateAttr.iterator();
            boolean hasTime = false;
            String strTime = "";
            while (its.hasNext()) {//����Ӣ��ģ�壬���Ƿ���ʱ��ģ��
                String str = (String) its.next();
                if (str.equals("ʱ��")) {//�����������Ӣ��ģ�壬�ܸ����ĶԵ���
                    hasTime = true;
                    strTime = (String) its.next();
                    break;
                }
            }
            if (!hasTime)//���û��ʱ�䣬�򷵻�
                return doc;
            else {

                int iPostion = strTime.indexOf(":");
                String timeNodeName = strTime.substring(0, iPostion);
                String subStrTimeName = strTime.substring(iPostion + 1);
                Element rootName = doc.getRootElement();
                Element name = rootName.element("maName");

                if (copyIndividual.getBrowserText().equals("clock.ma") && timeNodeName.equals("ʱ����")) {
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
                        if (hasGroundSpace && timeNodeName.equals("ʱ����")) {
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
     * ��ӡɾ�������
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
                if (!isDelete) {// ȷ��ɾ����ģ��û���ڸ��Ĺ�����ʹ��
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
                logger.info("ͨ�����ѡ�񣬲��ı䱳��ͼƬ");
                return doc;
            } else {
                OWLObjectProperty hasBackgroundPictureProperty = model.getOWLObjectProperty("hasBackgroundPicture");
                OWLDatatypeProperty backgroundPictureChangeNameProperty = model
                        .getOWLDatatypeProperty("backgroundPictureChangeName");
                OWLDatatypeProperty backgroundPictureTypeProperty = model
                        .getOWLDatatypeProperty("backgroundPictureType");
                if (copyIndividual.getPropertyValueCount(hasBackgroundPictureProperty) > 0) {
                    logger.info("���ѡ�񣬸ı䱳��ͼƬ�����б���ͼƬ�ı�");
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
                    logger.info("���ѡ��ı䱳��ͼƬ������ѡ��ma�ļ�û�б���ͼƬȥ�ı�");
                    return doc;
                }
            }
        }
    }

    /**
     * ���ڴ�ӡ���Ĺ���
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
        // ��ӡ���Ĺ���
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
                    if (!isUsedForAdd) {// ����Ҫ�ı�����ģ��û������ӹ������в���ʱ���Ͷ���������Ӧ�ı仯
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
     * ͨ������ķ������ճ�����������壬�����������ӣ�Ҳ�����������г����е�object�����ճ��������
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
            int kk = rand.nextInt(2);// ��0��1֮����������
            if (kk == 0) {
                addIndividuals = processAddToEmptyMaThroughZongxiang(model, individual);
            } else
                addIndividuals = processAddToEmptyMaThroughHasScene(model, individual);
        } else
            addIndividuals = processAddToEmptyMaThroughZongxiang(model, individual);
        return addIndividuals;
    }

    /**
     * ͨ��������empty.ma�����object
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
        Collection fatherClassNameCollection = null;// ������ӡ�����һЩ��Ϣ
        OWLNamedClass fatherClassName = null;
        Random rand = new Random();
        Date dt = new Date();
        rand.setSeed(dt.getTime());
        int parentFloorNum = rand.nextInt(2) + 1;// ���ѡ��һ�������
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
            int fNum = rand.nextInt(fInstance.size()); // ���ѡ�����е�ĳһʵ��
            lastFatherInstance = fInstance.get(fNum);
            addIndividuals.add(lastFatherInstance);
            if (lastFatherInstance.getPropertyValueCount(hasSceneProperty) > 0) {
                int kk2 = rand.nextInt(2);
                if (kk2 == 1)// �������Ϊ1ʱ��˵���Ѵ����������ĳ�����onject���뵽�ճ�����
                {
                    ArrayList<OWLIndividual> hasSceneObject = processAddToEmptyMaThroughHasScene(model,
                            lastFatherInstance);
                    for (Iterator tt = hasSceneObject.iterator(); tt.hasNext(); )// �ѳ����е�ĳЩ������뵽addIndividuals�У�
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
     * ͨ��hasScene������empty.ma�����object
     *
     * @param model
     * @param individual
     * @return
     */
    public static ArrayList<OWLIndividual> processAddToEmptyMaThroughHasScene(OWLModel model,
                                                                              OWLIndividual individual) {
        ArrayList<OWLIndividual> addIndividuals = new ArrayList();// ��������Scenes
        ArrayList<OWLIndividual> addIndividuals2 = new ArrayList();// ����ĳһ
        // �����е�����Object
        ArrayList<OWLIndividual> addIndividuals3 = new ArrayList();// ����ĳһ
        // �����е�ĳЩObject
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
     * ��Ҫ���ص㣺����ӡ����ã��������ͷ������������
     * 1.nothing����������docֱ�ӷ���
     * 2.empty�����������ģ���Ҳ��������ģ�ͣ���������������֣�Ȼ�󷵻�doc
     *
     * @param doc             ADL����ĵ���maName�ڵ㻹�С�maFrame=XX��û�д�ӡ��
     * @param model           ģ�Ͷ���
     * @param copyIndividual  ����ʵ�����
     * @param englishTemplate Ӣ��ģ��
     * @param colorModelNum   ��ɫģ��
     * @param colorChangeAttr
     * @return
     */
    public static Document printCycleAddRuleVersion2(Document doc, OWLModel model, OWLIndividual copyIndividual,
                                                     ArrayList<String> englishTemplate, int colorModelNum, ArrayList<String> colorChangeAttr) {
        colorModelNum = 0;
        colorChangeAttr.clear();
        Element rootName = doc.getRootElement();//����ˡ�����+����+���⡱������
        Element name = rootName.element("maName");//����ˡ�����+����+���⡱������
        String maName = name.attributeValue("name");//����
        String topicName = name.attributeValue("topic");//����
        if (maName.contains("nothing.ma"))//�����ѡ�����nothing������doc�����ı䷵��
        {
            doc = doc;
        } else if (maName.contains("empty.ma")) {//�����empty����
            String messValue = ProgramEntrance.messageValue;//���� ����
            Element addRule = name.addElement("rule");//���rule�ڵ�
            addRule.addAttribute("ruleType", "addToMa");// ruleType="addToMa"
            addRule.addAttribute("addWord", messValue);//addWord="����ϵͳ�ڶ����汾������"empty������ӡ������
            addRule.addAttribute("spaceName", "");
            addRule.addAttribute("degree", "");
            addRule.addAttribute("type", "word");//���ģ�����ݵ����ͣ�word�����֣���������people����model��
            addRule.addAttribute("number", "1");
            addRule.addAttribute("class", "");
        } else {//������ǿճ�����һ�㶼���ߵ����
            OWLNamedClass addModelRelatedClass = model.getOWLNamedClass("AddModelRelated");//AddModelRelated�����

            OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");//��������
            OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");//��������
            OWLDatatypeProperty isTarget = model.getOWLDatatypeProperty("isTemplateObject");//��������
            OWLDatatypeProperty setColor = model.getOWLDatatypeProperty("setColor");//��������
            OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");//��������
            OWLDatatypeProperty addModelNumberProperty = model.getOWLDatatypeProperty("addModelNumber");//��������
            OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");//��������

            OWLObjectProperty hasColorProperty = model.getOWLObjectProperty("hasColor");//��������
            OWLObjectProperty heightProperty = model.getOWLObjectProperty("height");//��������
            if (addModelRelatedClass.getInstanceCount() > 0) {//���addModelRelatedSpace���±���ʵ�壺��4��
                OWLDatatypeProperty maFrameNumberProperty = model.getOWLDatatypeProperty("maFrameNumber");//�ص㣺��������maFrameNumber
                OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");//��������
                OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");//��������
                int maFrameNum = Integer.parseInt(copyIndividual.getPropertyValue(maFrameNumberProperty).toString());//��ȡ�˳���֡��
                Collection<OWLIndividual> addModelList = addModelRelatedClass.getInstances();//��ȡaddModelRelatedSpace���±���ʵ�壺��4��
                Iterator its = addModelList.iterator();//��ȡaddModelRelatedSpace���±���ʵ�壺��4��
                while (its.hasNext()) {//����addModelRelatedSpace���±���ʵ�壺��4��
                    OWLIndividual addIndividual = (OWLIndividual) its.next();//ȡ��addModelRelatedSpace�±���ʵ�壺addModelID1��addModelID2...
                    String modelID = (String) addIndividual.getPropertyValue(modelIDProperty);//ID���У�addModelID1...
                    //addModelRelatedSpace����ֵ
                    OWLIndividual relatedSpce = (OWLIndividual) addIndividual.getPropertyValue(addModelRelatedSpaceProperty);
                    //����ģ�͵�����
                    OWLIndividual modelName = (OWLIndividual) addIndividual.getPropertyValue(hasModelNameProperty);
                    Object modelType = addIndividual.getPropertyValue(addModelTypeProperty);//����ģ�͵����ͣ�people��model�ȣ�
                    Object topicname = addIndividual.getPropertyValue(topicNameProperty);
                    String modelNumber = "";
                    String isTar = "";
                    modelNumber = (String) addIndividual.getPropertyValue(addModelNumberProperty);
                    isTar = (String) addIndividual.getPropertyValue(isTarget);//TODO

                    Object degreeStr = null;
                    String degree = "";

                    OWLIndividual particleIndividual = (OWLIndividual) modelName;
                    Element addRule = name.addElement("rule");// rule �ڵ�
                    addRule.addAttribute("ruleType", "addToMa");// type="model"
                    addRule.addAttribute("addModel", modelName.getBrowserText());//�ص㣬��Ҫ�������ӵ�ģ��
                    logger.info("������ģ�ͣ�����AxiomClass����" + modelName.getBrowserText());
                    // �Զ������ᵽ�ľ�����ɫ��ģ�͵�ID���������ID
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
                    addRule.addAttribute("isTarget", isTar);//�ص㣺��Ҫ���Ƿ���Ŀ��
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
                    // �����̵���ɫ���߶�
                    addRule.addAttribute("addModelID", modelID.toString());
//					System.out.println(topicname);
                    /*
                     * if(topicname!=null){ addRule.addAttribute("topicname",
                     * topicname.toString()); }
                     */
                }//end ����addModelRelatedSpace���±���ʵ�壺��4��
            }
        }
        return doc;
    }

    /**
     * ���б�����ma�ļ����б���ͼƬ�ĸ���
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
     * ����Ϣ��ȡû�г鵽���⣬ģ��Ҳû���Ƴ����⣬����ģ��ͨ��hasAnimationNameFromTemplate
     * ���ɷ�õ���Ӧ�Ķ���������ֱ��������ֵ�ó���
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
     * ���ʵ��������
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
     * ����һ��ma�����ļ���ʵ��
     *
     * @param maName���Ѿ�ѡ�õ�ma�ļ�
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
            ii.setPropertyValue(selectedMaProperty, new Integer(1));// ������ѡ��ma��selectedMa����ֵΪ1
            // ii.c
            System.out.println("copyIndividual:" + ii.getBrowserText());
        }
        return model;
    }

    /**
     * ͨ������topic��ontology��Ѱ����Ӧ��Ӣ��topic��
     *
     * @param owlModel��owl                      model
     * @param chineseTopic:����topic���ƣ�ͨ��IE��ȡ�����⣩
     * @return ����Topic���ƶ�Ӧ��Ӣ����
     */
    public static OWLNamedClass getEnglishTopicFromPlot(OWLModel owlModel, String chineseTopic) {

        OWLNamedClass TopicClass = owlModel.getOWLNamedClass("TopicRelatedPlot");//������������
        OWLDatatypeProperty chineseNameProperty = owlModel.getOWLDatatypeProperty("chineseName");//���Զ���
        OWLNamedClass cls = null;
        Collection subTopicClass = TopicClass.getSubclasses(true);//��ȡTopicRelatedPlot��������
        for (Iterator itTopic = subTopicClass.iterator(); itTopic.hasNext(); ) {//��������������
            cls = (OWLNamedClass) itTopic.next();//next()
            if (cls.getDirectSubclassCount() == 0)// �ж�������Ѿ�û��ֱ������
            {
                Object hasValueName = cls.getHasValue(chineseNameProperty);//��ȡ��chineseName���Ե�ֵ
                if (hasValueName != null && hasValueName.toString().equals(chineseTopic)) {//�����chineseName���Բ�����ֵ�����˵�ҵ�����������ͬ������
                    break;
                }
                cls = null;
            }
        }
        return cls;
    }

    /**
     * ������ģ����Ӣ�ĵ�,ͨ����������������Ӣ��ģ�����Ѿ� û��ģ�������ֻ����Ӧ��ֵ���硰����������ʱ�䡱�ȴ��Ѿ�û�� ֻ�С�ѩ����ѩ��������
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
                // �������ģ�����������ɫ������
                modelWithColor.add(modelName);
                modelWithColor.add(color);
            }
        }
        return modelWithColor;
    }

    /**
     * ����ģ��ת��ΪӢ��ģ��
     * <p>
     * ArrayList<String> chineseTemplate Ҫת��������ģ�� return ArrayList<String>
     * ����ģ��ת�����Ӣ��ģ��
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> chineseTemplate2English(ArrayList<String> chineseTemplate, OWLModel model)
            throws SecurityException, IOException {
        ArrayList<String> englishTemplate = new ArrayList();
        OWLNamedClass templateClass = model.getOWLNamedClass("Template");
        OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection subTemplateClass = templateClass.getSubclasses(true);// ��ӡ���࣬����ӡ���������
        for (Iterator<String> ist = chineseTemplate.iterator(); ist.hasNext(); ) {// ѭ����������ԭ��ģ���Ӧ��ԭ����Ϣ
            String tempName = ist.next();

            boolean isStop = false;
            boolean findCls = false;
            A:
            for (Iterator itTemplate = subTemplateClass.iterator(); itTemplate// ѭ��Template�������������
                    .hasNext(); ) {
                if (isStop)
                    break;
                cls = (OWLNamedClass) itTemplate.next();// ��ontology��Ѱ��ģ�壬ͨ��chineseName�������������
                Object cc = cls.getHasValue(chineseNameProperty);
                // System.out.println("hhhh:"+cls.getBrowserText()+"
                // test111:"+tempName);
                if (cls.getHasValue(chineseNameProperty).toString().trim().equals(tempName.trim()))// ͨ��hasValue���������ģ�������
                // ��������ʲôģ�壨ʱ���ص㣩
                {// �Ȳ��Ҷ�Ӧ��ģ��

                    String tempNameW = cls.getBrowserText();
                    // englishTemplate.add(tempNameW);
                    String templateVlaue = "";
                    if (ist.hasNext())
                        templateVlaue = ist.next();
                    // String[] splitTempName = new String[2];
                    String[] splitTempName = templateVlaue.split(":");
                    Collection subsubTemplateClass = cls.getSubclasses(true);// ��ӡ���Ѳ��ҵ�ģ�������
                    B:
                    for (Iterator itsTemplate = subsubTemplateClass.iterator(); itsTemplate.hasNext(); ) {
                        if (isStop)
                            break;

                        OWLNamedClass clss = (OWLNamedClass) itsTemplate.next();
                        Collection clssHasValues = clss.getHasValues(chineseNameProperty);

                        C:
                        for (Iterator itValue = clssHasValues.iterator(); itValue// ��������Ӧģ���µ�����
                                .hasNext(); ) {
                            if (isStop)
                                break;
                            Object value = itValue.next();
                            if (value.toString().trim().equals(splitTempName[0].trim()))// ����ƥ��ģ��ֵ��ð��ǰ����ֶΣ���ѧУ��Сѧ���������ƥ�䡰ѧУ����
                            {
                                findCls = true;
                                isStop = true;
                                String templateName = clss.getBrowserText() + ":";
                                if (clss.getInstanceCount() != 0) {
                                    ArrayList<String> templateInstan = new ArrayList();
                                    String autoValue = "";
                                    Collection templateInstances = clss.getInstances();// ���ҵ���ģ������Ӧ���࣬����ģ�����Ӧ��ʵ����Ҳ����ԭ����Ϣ
                                    D:
                                    for (Iterator it = templateInstances.iterator(); it.hasNext(); ) {
                                        OWLIndividual templateIndividual = (OWLIndividual) it.next();// ��ӡ��ʵ��
                                        templateInstan.add(templateIndividual.getBrowserText());// �����洢����ʵ������Ҫ��Ϊ�˵�û���ҵ�����Ҫ���ʵ��ʱ�ʹ��������ѡ��һ��
                                        if (templateIndividual.getPropertyValueCount(chineseNameProperty) > 0) {
                                            Collection chineseValues = templateIndividual
                                                    .getPropertyValues(chineseNameProperty);
                                            for (Iterator its = chineseValues.iterator(); its.hasNext(); )// ѭ��ʵ������Ӧ�Ķ����������
                                            {
                                                String cValue = its.next().toString();
                                                if (cValue.trim()
                                                        .equals(splitTempName[splitTempName.length - 1].trim())) {
                                                    // templateIndividual.getpro
                                                    autoValue = templateIndividual.getBrowserText();

                                                    break D;
                                                } // ����Ӧʵ������������
                                            } // ����ʵ�����������ֵ�ѭ��
                                        } // ����ʵ���Ƿ�����������

                                    } // ������Ӧ���ʵ��
                                    if (autoValue != "")// ģ��ʵ�����ҵ�����Ӧ��ģ��ԭ�Ӷ�Ӧ����Ϣ
                                        templateName = templateName + autoValue + ":1.0";

                                    else // ��ʵ�����Ҳ�����Ӧ��ģ��ԭ����Ϣ���ʹ��Ѿ��е�ģ��ʵ��ԭ���������ѡһ��
                                    {
                                        Random rand = new Random();
                                        int kk = rand.nextInt(templateInstan.size());
                                        templateName = templateName + templateInstan.get(kk) + ":0.5";
                                    }

                                } // ����ʵ�����ж�

                                if (!englishTemplate.contains(templateName)) {
                                    englishTemplate.add(templateName);
                                }
                                int i = templateName.lastIndexOf(":");// ȥ�����ķ�ֵ
                                String temp = templateName.substring(0, i);

                                if (tempName.equals("����")) {

                                    actionTemplateAttr.add(temp);// to qiu
                                }
                                if (tempName.equals("����") || tempName.equals("���������ɫ")) {
                                    ActionNeedPeople.add(temp);
                                }
                                if (tempName.equals("����")) {
                                    moodTemplateAttr.add(temp);
                                    weatherAndmoodAttr.add(temp);
                                    ExpressionList.add(temp);
                                    actionTemplateAttr.add(temp);
                                    WindAttr.add(temp);
                                    RainAttr.add(temp);
                                    SnowAttr.add(temp);
                                    LightList.add(temp);
                                }
                                if (tempName.equals("����") | tempName.equals("����")) {
                                    weatherAndmoodAttr.add(temp);
                                    RainAttr.add(temp);
                                    WindAttr.add(temp);
                                    SnowAttr.add(temp);
                                    LightList.add(temp);
                                    SeasonList.add(temp);
                                }
                                // if(tempName.equals("ʱ��"))
                                // WindAttr.add(templateName);
                                // if(tempName.equals("����"))

                                System.out.println("test:" + templateName);
                                logger.info("ģ����Ϣ�����Ӣ�ĺ��ֵ��" + templateName);
                            }

                        }
                        // Object ccc=cls.getHasValue(chineseNameProperty);

                    } // ģ����������Ľ���
                    if (findCls == false)
                        TemplateName.add(tempNameW);// �������ģ������
                } // �ж��Ƿ�������ڵ�ģ��
            } // template���µ���������
        }

//		System.out.println("����ģ��englishiTemplate=" + englishTemplate + windRainSnowNeedAttr);
        return englishTemplate;
    }

    // ********************************************���ķ����Ӣ�Ľ���**********************************************************//

    /**
     * ͨ������topic��ontology��Ѱ����Ӧ��Ӣ��topic��
     *
     * @param owlModel��owl                   model
     * @param chineseTopic:����topic�ࣨͨ��IE��ȡ�ģ�
     * @return
     */
    public static OWLNamedClass getEnglishTopic(OWLModel owlModel, String chineseTopic) {

        OWLNamedClass TopicClass = owlModel.getOWLNamedClass("Topic");
        OWLDatatypeProperty chineseNameProperty = owlModel.getOWLDatatypeProperty("chineseName");
        OWLNamedClass cls = null;
        Collection subTopicClass = TopicClass.getSubclasses(true);// ��ӡ���࣬����ӡ���������

        for (Iterator itTopic = subTopicClass.iterator(); itTopic.hasNext(); ) {
            cls = (OWLNamedClass) itTopic.next();
            if (cls.getDirectSubclassCount() == 0)// �ж�������Ѿ�û��������
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
     * ��owl��BackgroundScene ������ѡȡһ��ʵ��
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
     * ͨ��addModelFromTopic�������ѡ�񳡾��ļ�
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
     * ͨ�����������ma��ǰ�������ⲻΪ��
     *
     * @param owlModel
     * @param EnglishTopic:ͨ������topic��õ�Ӣ��topic
     * @return
     * @throws IOException
     * @throws SecurityException
     * @throws SWRLRuleEngineException
     */
    @SuppressWarnings("deprecation")
    public static String getMaFromTopic(OWLModel model, OWLNamedClass englishTopicClass)
            throws SecurityException, IOException, SWRLRuleEngineException {
        String choosedMaName = "";
        int zeroCount = 0;// ������¼���Ϊ0�Ķ��������ĸ���
        int oneCount = 0;// ������¼���Ϊ1�Ķ��������ĸ���
        ArrayList<String> instanceSave = new ArrayList();// �����洢ʵ��
        ArrayList<Integer> instanceMarkSave = new ArrayList();// �����洢ʵ���ı�־����֮ǰ�Ƿ�ѡ�й�
        OWLObjectProperty hasMaProperty = model.getOWLObjectProperty("hasMa");
        OWLDatatypeProperty senceMarkProperty = model.getOWLDatatypeProperty("animationSceneMark");
        RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMaProperty);
        logger.info("ͨ�������������������Ӧ��ma���������֣�����������Ӧ�ĳ���������" + resource.getBrowserText());
        String hasValues = resource.getBrowserText();// ��������Ӧ�ĳ���������
        String[] hasValuesSplit = hasValues.split(" or ");// ���ܶ�Ӧ��� ������;

        ArrayList<String> hasValuesClass = new ArrayList();
        OWLNamedClass resourceClass = null;
        System.out.println("geshu:" + hasValuesSplit.length);
        if (hasValuesSplit.length > 1) {// ���ж��������ʱ�����ж�ÿ���������Ƿ���ʵ��
            for (int i = 0; i < hasValuesSplit.length; i++) {
                OWLNamedClass resourceClass0 = model.getOWLNamedClass(hasValuesSplit[i].trim());
                System.out.println("name::" + hasValuesSplit[i].trim());
                int instanceCount0 = resourceClass0.getInstanceCount();
                if (instanceCount0 > 0)
                    hasValuesClass.add(hasValuesSplit[i].trim());
            }
            Random rand = new Random();
            if (hasValuesClass.size() > 0)// ����������඼��ʵ��ʱ�������ѡ��һ��
            {
                int kk = rand.nextInt(hasValuesClass.size());
                resourceClass = model.getOWLNamedClass(hasValuesClass.get(kk));
            } else// ����������඼û��ʵ��ʱ�������ѡ��һ����������
            {
                int kk = rand.nextInt(hasValuesSplit.length);
                resourceClass = model.getOWLNamedClass(hasValuesSplit[kk].trim());

            }
        } else// ����ֻ��һ������������
            resourceClass = model.getOWLNamedClass(hasValuesSplit[0].trim());

        logger.info("���ո���������Ӧ�ĳ���������" + resourceClass.getBrowserText());
        int instanceCount = resourceClass.getInstanceCount();
        System.out.print("�˳�����ӵ�е�ʵ�������ǣ�" + instanceCount);

        logger.info("���ո���������Ӧ�ĳ���������ʵ��������" + instanceCount);
        if (instanceCount != 0) {// ��topic��Ӧ�ĳ���������ʵ��
            System.out.println(";�ֱ�Ϊ��");
            Collection resourceInstance = resourceClass.getInstances(true);
            for (Iterator it = resourceInstance.iterator(); it.hasNext(); ) {
                OWLIndividual individual = (OWLIndividual) it.next();// ��ӡ��ʵ��
                System.out.println("ʵ��Ϊ��" + individual.getBrowserText());
                instanceSave.add(individual.getBrowserText());// �洢ÿ��ʵ��
                if (individual.getPropertyValue(senceMarkProperty) == null)
                    individual.setPropertyValue(senceMarkProperty, new Integer(0));
                int instanceMark = Integer.parseInt(individual.getPropertyValue(senceMarkProperty).toString());
                instanceMarkSave.add(instanceMark);// �洢ÿ��ʵ���ı�־
                System.out.println("����λΪ��" + instanceMark);
            }
            /// ************����λ��ᡱ�㷨(start)*********
            for (int i = 0; i < instanceMarkSave.size(); i++)// ͳ��0��1�ĸ���
            {
                int temp = instanceMarkSave.get(i);
                if (temp == 0)
                    zeroCount++;
                else
                    oneCount++;
            }
            // oneCount==instanceMarkSave.size()
            if (zeroCount == 0)// ��ÿ����������ѡ��ʱ����������senceMark����
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
            /// ************����λ��ᡱ�㷨(end)*********

        } else// �������⣬����������Ӧ�ĳ�����û��ʵ��,����ҳ����ุ�ڵ��µ����������ࣨ�ɻ�����
        {
            ArrayList<OWLNamedClass> otherClass = new ArrayList();
            logger.info("���⣺" + englishTopicClass.getBrowserText() + "����Ӧ��ma�����ļ�Ϊ0���������丸�ڵ��µ�����������");
            Collection resourceSuperClass = resourceClass.getSuperclasses(false);
            // ���Ҹ�������ʵ����Ϊ0�ĳ�����
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

                logger.info("���⣺" + englishTopicClass.getBrowserText() + "����Ӧ��ma�����ļ�Ϊ0���������丸�ڵ��µ�����������Ҳû��ʵ��");
                choosedMaName = "";
            }

        }
        JenaOWLModel owlModel = (JenaOWLModel) model;
        // String fileName = "sumoOWL2/sumo_phone3.owl";//����ma���
        // String fileName = "C:/ontologyOWL/rootOWL/sumoOWL2/sumo_phone3.owl";
        String fileName = "C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";//
        saveOWLFile(owlModel, fileName);
        // System.out.println("����ѡ��ma�����ǣ�" + choosedMaName);
        logger.info("��Ӧ���⣺" + englishTopicClass.getBrowserText() + "��ѡ��ma�ļ�Ϊ��" + choosedMaName);
        return choosedMaName;
    }

    /**
     * ��λ����㷨
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
            if (instanceMark == 0)// ��2��֮�ڣ����ѡ��û��ѡ�Ķ�������ѭ��ֹͣ
            {
                OWLIndividual individual1 = model.getOWLIndividual(choosedMaName);
                individual1.setPropertyValue(senceMarkProperty, new Integer(1));
                break;
            }
        }
        return choosedMaName;

    }

    /**
     * ͨ��ģ�����Ƴ�����
     *
     * @param model        OWLNameModel����
     * @param templateAttr ������ֵ��Ӣ��ģ��
     * @return
     */
    private static String getTopicFromTemplateAfterSWRL(OWLModel model, ArrayList<String> templateAttr) {

        String topicName = "";

        OWLObjectProperty hasTopicFromTemplateProperty = model.getOWLObjectProperty("hasTopicFromTemplate");// ��ȡ��������
        // hasTopicFromTemplate
        // �Ķ���
        ArrayList<String> topicN = new ArrayList();// ���ж��ģ��ʱ������ÿ��ģ��ֵ���Ƴ�һ���������⣬

        for (Iterator its = templateAttr.iterator(); its.hasNext(); ) {// �������е�ģ��
            String templateValue = (String) its.next();// next();��������ģ������
            String[] splitTempName = new String[2];
            splitTempName = templateValue.split(":");// ��ģ��ֵ�ִ����ַ����飺�ֱ��� ������+ʵ������
            OWLIndividual individual = model.getOWLIndividual(splitTempName[1]);
            int count = individual.getPropertyValueCount(hasTopicFromTemplateProperty);
            boolean isAdd = false;
            if (count > 0) {
                Collection topicValue = individual.getPropertyValues(hasTopicFromTemplateProperty);
                for (Iterator its1 = topicValue.iterator(); its1.hasNext(); )// �����Ƶ���ÿ��ģ��ֵ�п��ܴ��ڶ��ģ��ֵ
                {
                    OWLIndividual value = (OWLIndividual) its1.next();
                    if (topicN.size() > 0) {
                        isAdd = true;
                        for (Iterator<String> its2 = topicN.iterator(); its2.hasNext(); )// ��ֹtopicN�г����ظ���ֵ
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
     * ģ�Ϳ��Է��õ�λ��(Location���ԣ����)�볡���Ŀ��ÿռ�(hasValueOfPlace����)ͨ��isEquivalOf(���ÿռ�����)����������ͬ���򷵻�true
     *
     * @param model
     * @param maName     ��������
     * @param individual ģ��ʵ��
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

        Collection clo = individual.getPropertyValues(Location);//ģ��λ������ֵ����

        if (clo.size() != 0) {
            for (Iterator i = clo.iterator(); i.hasNext(); ) {//����ģ��λ������ֵ����
                OWLIndividual local = (OWLIndividual) i.next();//next()
                OWLIndividual place = (OWLIndividual) maname.getPropertyValue(hasValueOfPlace);//����
                if (local.getPropertyValue(isEquivalOf).equals(place)) {
                    return true;
                }

            }
        }
        return false;
    }

}
