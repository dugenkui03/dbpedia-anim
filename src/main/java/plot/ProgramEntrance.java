package plot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import java.util.logging.Logger;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

public class ProgramEntrance {

    //保存IE输出内容用，getXMLDate(String IE_out.xml)中填充值
    static ArrayList<String> topicName = new ArrayList<String>();// 用来保存Topic的名字
    static ArrayList<String> topicFromQiu = new ArrayList<String>();// 用来保存Topic的名字
    static ArrayList<String> topicFromMG = new ArrayList<String>();// 用来保存马更信息抽取的主题及其概率值
    static ArrayList<String> templateWithColor = new ArrayList<String>();
    static ArrayList<String> colorMark = new ArrayList<String>();
    static ArrayList<String> TopicAndTemplate = new ArrayList<String>();
    static String topicProbability = "";// 用来保存Topic的Key值
    public static String messageValue = "";// 保存短信内容
    public static boolean isMiddleMessage = false;// 判断
    static String strNegType = "";// 保存否定
    static int count[] = new int[2];//count[0/1/2]
    static boolean isDouble = false;
    static Logger logger = Logger.getLogger(ProgramEntrance.class.getName());
    //重要： 用来保存模板及模板值
    static ArrayList<String> templateAttr = new ArrayList<String>();
    private static Object strTopic;


    /**
     * 程序入口，定性调用
     */
    public static void Entrance() {
        try {
            Utils.delDir("c:\\ontologyOWL\\AllOwlFile\\sumoOWL2");
            Utils.copyDir("c:\\ontologyOWL\\sumoOWL2", "c:\\ontologyOWL\\AllOwlFile\\sumoOWL2");
            logger.info("覆盖修改过的知识库");

            logger.info("开始读取IE文件");
            getXMLData("OntologyInPutFile\\result.xml");

            logger.info("============================开始定性主程序====================================================================================");
            JenaMethod.processMaFile(topicName, templateAttr, templateWithColor, colorMark, topicFromMG, // 带概率值
                    topicFromQiu, strNegType, count, TopicAndTemplate);
            logger.info("============================结束定性主程序====================================================================================");

            clearPar();//清除定性程序用到的参数缓存
        } catch (Exception e) {
            e.printStackTrace();
            isMiddleMessage = true;
        }
    }

    //清除定性程序用到的参数缓存
    private static void clearPar() {
        topicName.clear();// 用来保存Topic的名字
        topicFromMG.clear();// 用来保存马更信息抽取的主题及其概率值
        topicProbability = "";// 用来保存Topic的Key值
        messageValue = "";// 保存短信内容
        isMiddleMessage = false;// 判断
        strNegType = "";// 保存否定
        templateAttr.clear();// 用来保存模板及模板值
        topicFromQiu.clear();
        TopicAndTemplate.clear();
    }

    /**
     * 读取IE部分输出文件
     */
    @SuppressWarnings("unchecked")
    private static void getXMLData(String location) {
        Document doc = XMLInfoFromIEDom4j.readXMLFile(location);
        Element element = doc.getRootElement();
        Attribute attrNeg = element.attribute("negType");
        //判断是否有否定词
        if (attrNeg != null)
            strNegType = attrNeg.getText();//获取否定词
        Element ele = element.element("message");//获取节点名为“message”的对象
        Attribute attr = ele.attribute("value");//获取对象‘value’元素值
        messageValue = attr.getText(); //获取短信内容
        List<?> list = doc.selectNodes("//result/topic");

        // 对邱雄的程序输出的结果解析，如果是“主观的”情感，则判断是“喜怒哀惧”四中里边的哪一种
        Element textT = element.element("textType");
        if (textT != null)//2017.11.10yangyong添加代码，仅此一行
            if (textT.attribute("attribute").getText().equals("subjective")) {//是主观情感
                double prob = 0.0;
                if (textT.element("happy") != null) {//喜
                    Element happy = textT.element("happy");
                    prob = Double.parseDouble(happy.attribute("attribute").getText()); //感情概率值
                    if (prob > 0.3) {//概率超过临界点，则加入主题
                        topicFromQiu.add("喜悦");
                        topicFromQiu.add(Double.toString(prob));
                    }

                } else {
                    if (textT.element("angry") != null) {
                        Element angry = textT.element("angry");
                        prob = Double.parseDouble(angry.attribute("attribute").getText());
                        System.out.println("angry prab:" + prob);
                        if (prob > 0.3) {
                            topicFromQiu.add("生气");
                            topicFromQiu.add(Double.toString(prob));
                        }
                    } else {
                        if (textT.element("sad") != null) {
                            Element sad = textT.element("sad");
                            prob = Double.parseDouble(sad.attribute("attribute").getText());
                            System.out.println("sad prob:" + prob);
                            if (prob > 0.3) {
                                topicFromQiu.add("悲");
                                topicFromQiu.add(Double.toString(prob));
                            }
                        } else {
                            if (textT.element("fear") != null) {
                                Element fear = textT.element("fear");
                                prob = Double.parseDouble(fear.attribute("attribute").getText());
                                System.out.println("fear prab:" + prob);
                                if (prob > 0.3) {
                                    topicFromQiu.add("恐惧");
                                    topicFromQiu.add(Double.toString(prob));
                                }
                            }
                        } // fear else end
                    } // sad else end
                } // angry else end
            }

        if (list.isEmpty()) {//根据推理没有获取主题：result\\下的topic节点放进List
            isMiddleMessage = true;    //没有主题
        } else {//如果有推理的主题

            Iterator<?> its1 = list.iterator();//将List信息放到Iterator<E>中
            while (its1.hasNext()) {//hasNext()+next();
                String str = "";
                String strTopic = "";
                Element topic = (Element) its1.next();//next()获取topic对象

                // 获得Topic节点的相应属性
                for (Iterator<Attribute> topicAttribute = topic.attributeIterator(); topicAttribute.hasNext(); ) {
                    Attribute topicName0 = (Attribute) topicAttribute.next();//hasNext()+next();

                    //Topic对象属性：name和probability
                    if (topicName0.getName().equals("name")) {
                        String aa = topicName0.getText().toString();
                        // 获得topic的名字
                        if (topicName0.getText().toString() != "") {
                            strTopic = topicName0.getText().toString();
                        }
                    } else if (topicName0.getName().equals("probability")) { // 获得Topic的key值
                        topicProbability = topicName0.getText().toString();
                    }
                }

                if (strTopic != "") {
                    if (topicProbability != "") {//如果有主题且概率不为空
                        topicFromMG.add(strTopic);//马庚主题加入
                        topicFromMG.add(topicProbability);
                    } else
                        topicName.add(strTopic);
                }

                List<Element> root = topic.elements();//从主题对象获取

                String d = "";
                for (Iterator<Element> its = root.iterator(); its.hasNext(); ) {

                    Element template = (Element) its.next();// 取每个template的属性值

                    String templatename = "", templateName1 = "";//模板名称
                    String templatevalue = "";//模板值
                    for (Iterator<Attribute> templateAttribute = template.attributeIterator();
                         templateAttribute.hasNext(); ) {

                        Attribute templateName0 = (Attribute) templateAttribute.next();//next()

                        //如果是name属性值
                        if (templateName0.getName().equals("name")) {
                            // 获得template的名字：就是IE输出文件<topic name..<root name="时间/人物/动作"模板
                            templateAttr.add(templateName0.getText());

                            templateName1 = templateName0.getText();
                            templatename = templateName0.getText();

                            logger.info("模板名字:" + templateName0.getText());

                            if (templateName0.getText().equals("人物") || templateName0.getText().equals("经典人物角色")) {
                                count[0]++;//模板如果是人物，count[0]加1
                            } else
                                count[1]++;//如果不是人物，count[1]加1
                        }

                        //如果是value属性值
                        else if (templateName0.getName().equals("value")) {
                            // 获得template的value值
                            templatevalue = templateName0.getText();
                            // 模板value有多个原子节点时，比如“白天:下午:下午|某日:今天”，取得其值放入链表
                            ArrayList<String> templateV = processTemplateValue(templatevalue);
                            //遍历这个模板下的所有模板原子节点
                            for (Iterator<String> its5 = templateV.iterator(); its5.hasNext(); ) {

                                String templateVV = its5.next();//next
                                templateAttr.add(templateVV);
                                templatevalue = templateVV;

                                templatename = templatename + ":" + templatevalue;//模板名字+总节点值

                                if (its5.hasNext()) {//模板值有多个节点值时
                                    templateAttr.add(templateName1);//ArrayList<String>中放进模板名称
                                    templatename = templatename + "-" + templateName1;//模板名字+总节点值+模板名字
                                }
                            }
                            logger.info("模板的元素：" + templateV);
                        }
                        //如果是颜色/否定词 等信息
                        else if (templateName0.getName().equals("color") && (!templateName0.getText().isEmpty())) {
                            templateWithColor.add(templateName1);
                            templateWithColor.add(templatevalue);
                            colorMark.add(templateName0.getText());
                        } else if (templateName0.getName().equals("negFlag")) {
                            if (isDouble) {
                                int position = templatename.indexOf("-");
                                String[] split = templatename.split("-");
                                StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < split.length; j++) {
                                    sb.append(split[j]);
                                    if (j == split.length - 1)
                                        sb.append(":" + templateName0.getText());
                                    else
                                        sb.append(":" + templateName0.getText() + "-");
                                }
                                templatename = sb.toString();

                            } else
                                templatename = templatename + ":" + templateName0.getText();
                        }

                    }//结束遍历某个模板下的所有原子元素
                    d = d + "-" + templatename;//模板名字累加
                }//结束遍历所有模板
                str = strTopic + d;//主题名+主题下的所有模板名
                if (root.size() != 0)
                    TopicAndTemplate.add(str);//如果主题不为空，即上述代码运行过
            }
            //删除重复的模板原子
            templateAttr = deleteRepeatemplateValue(templateAttr);
        }
    }

    /*
     * 被getXMLData使用
     * 删除重复的模板原子信息
     */
    private static ArrayList<String> deleteRepeatemplateValue(ArrayList<String> aa) {

        ArrayList<String> template = new ArrayList<String>();
        Iterator<String> its = aa.iterator();
        while (its.hasNext()) {
            boolean isRepeat = false;
            String templateName = (String) its.next();// 获得模板名
            String templateAutoName = (String) its.next();// 获得原子信息
            if (template.size() == 0) {
                template.add(templateName);
                template.add(templateAutoName);
            } else {
                for (Iterator<String> its1 = template.iterator(); its1.hasNext(); ) {
                    String temp = (String) its1.next();
                    if (templateAutoName.equals(temp)) {
                        isRepeat = true;
                        break;
                    }
                }
                if (!isRepeat) {
                    template.add(templateName);
                    template.add(templateAutoName);
                }
            }
        }
        return template;
    }

    /**
     * 拆分字符串的"低小下"函数，被getXMLData使用，用来处理当一个模板下有多个原子信息时
     *
     * @param templateValue 模板value字符串
     * @return List<>:返回此模板下的所有原子信息
     */
    private static ArrayList<String> processTemplateValue(String templateValue) {
        ArrayList<String> tempAtom = new ArrayList<String>();
        ArrayList<String> tempAtom2 = new ArrayList<String>();
        ArrayList<ArrayList<String>> tempAtom3 = new ArrayList<ArrayList<String>>();
        // 以“|”为界限，把模板值分解出来，一一用函数processSameTemplateMuchValue进行处理
        if (templateValue.contains("|"))// 当一个模板中的原子信息存在多个，进行随机处理
        {
            isDouble = true;
            while (templateValue.contains("|")) {
                int iPostion = templateValue.indexOf("|");
                String subAtom = templateValue.substring(0, iPostion);
                tempAtom = processSameTemplateMuchValue(subAtom);
                tempAtom3.add(tempAtom);
                templateValue = templateValue.substring(iPostion + 1, templateValue.length());

            }
            tempAtom = processSameTemplateMuchValue(templateValue);
            tempAtom3.add(tempAtom);
        } else {
            tempAtom = processSameTemplateMuchValue(templateValue);
            tempAtom3.add(tempAtom);
        }
        // END以“|”为界限，把模板值分解出来，一一用函数processSameTemplateMuchValue进行处理////////////////////
        ArrayList<String> temp4 = new ArrayList<String>();
        // 用来取得中间模板的情况
        for (Iterator<ArrayList<String>> its2 = tempAtom3.iterator(); its2.hasNext(); ) {
            ArrayList<String> tempW = its2.next();
            for (Iterator<String> its3 = tempW.iterator(); its3.hasNext(); ) {
                temp4.add(its3.next());
            }
        }
        // 用来处理模板值中有中间模板的情况
        for (Iterator<String> its = temp4.iterator(); its.hasNext(); ) {
            templateValue = its.next();
            int postion = templateValue.indexOf(":");
            String value1 = templateValue.substring(0, postion);// 取第一个
            String value2 = "";
            String temp = templateValue.substring(postion + 1);
            if (temp.contains(":")) {
                int iipostion = 0;
                iipostion = temp.indexOf(":");
                value2 = temp.substring(0, iipostion);
                templateValue = value1 + ":" + value2;
            }
            tempAtom2.add(templateValue);
        }
        return tempAtom2;
    }

    /**
     * 被getXMLData使用
     * 处理一个模板下一个节点下的多个原子信息
     *
     * @param value
     * @return
     */
    private static ArrayList<String> processSameTemplateMuchValue(String value) {
        ArrayList<String> tempAtom = new ArrayList<String>();
        if (value.contains(";")) {
            isDouble = true;
            int count = 0;
            String nodeName = "";
            while (value.contains(";")) {
                int iPostion = value.indexOf(";");
                String subAtom = value.substring(0, iPostion);
                count++;
                if (count == 1) {
                    int iiPostion = subAtom.lastIndexOf(":");
                    //////////////////////////// 找出第一个元素的最终节点名///////////////////////////////////////////////////////////
                    nodeName = subAtom.substring(0, iiPostion);
                } else {
                    subAtom = nodeName + ":" + subAtom;
                }
                tempAtom.add(subAtom);

                value = value.substring(iPostion + 1, value.length());
            }
            value = nodeName + ":" + value;
            tempAtom.add(value);
        } else
            tempAtom.add(value);
        return tempAtom;
    }
}
