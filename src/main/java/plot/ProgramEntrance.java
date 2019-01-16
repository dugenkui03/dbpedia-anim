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

    //����IE��������ã�getXMLDate(String IE_out.xml)�����ֵ
    static ArrayList<String> topicName = new ArrayList<String>();// ��������Topic������
    static ArrayList<String> topicFromQiu = new ArrayList<String>();// ��������Topic������
    static ArrayList<String> topicFromMG = new ArrayList<String>();// �������������Ϣ��ȡ�����⼰�����ֵ
    static ArrayList<String> templateWithColor = new ArrayList<String>();
    static ArrayList<String> colorMark = new ArrayList<String>();
    static ArrayList<String> TopicAndTemplate = new ArrayList<String>();
    static String topicProbability = "";// ��������Topic��Keyֵ
    public static String messageValue = "";// �����������
    public static boolean isMiddleMessage = false;// �ж�
    static String strNegType = "";// �����
    static int count[] = new int[2];//count[0/1/2]
    static boolean isDouble = false;
    static Logger logger = Logger.getLogger(ProgramEntrance.class.getName());
    //��Ҫ�� ��������ģ�弰ģ��ֵ
    static ArrayList<String> templateAttr = new ArrayList<String>();
    private static Object strTopic;


    /**
     * ������ڣ����Ե���
     */
    public static void Entrance() {
        try {
            Utils.delDir("c:\\ontologyOWL\\AllOwlFile\\sumoOWL2");
            Utils.copyDir("c:\\ontologyOWL\\sumoOWL2", "c:\\ontologyOWL\\AllOwlFile\\sumoOWL2");
            logger.info("�����޸Ĺ���֪ʶ��");

            logger.info("��ʼ��ȡIE�ļ�");
            getXMLData("OntologyInPutFile\\result.xml");

            logger.info("============================��ʼ����������====================================================================================");
            JenaMethod.processMaFile(topicName, templateAttr, templateWithColor, colorMark, topicFromMG, // ������ֵ
                    topicFromQiu, strNegType, count, TopicAndTemplate);
            logger.info("============================��������������====================================================================================");

            clearPar();//������Գ����õ��Ĳ�������
        } catch (Exception e) {
            e.printStackTrace();
            isMiddleMessage = true;
        }
    }

    //������Գ����õ��Ĳ�������
    private static void clearPar() {
        topicName.clear();// ��������Topic������
        topicFromMG.clear();// �������������Ϣ��ȡ�����⼰�����ֵ
        topicProbability = "";// ��������Topic��Keyֵ
        messageValue = "";// �����������
        isMiddleMessage = false;// �ж�
        strNegType = "";// �����
        templateAttr.clear();// ��������ģ�弰ģ��ֵ
        topicFromQiu.clear();
        TopicAndTemplate.clear();
    }

    /**
     * ��ȡIE��������ļ�
     */
    @SuppressWarnings("unchecked")
    private static void getXMLData(String location) {
        Document doc = XMLInfoFromIEDom4j.readXMLFile(location);
        Element element = doc.getRootElement();
        Attribute attrNeg = element.attribute("negType");
        //�ж��Ƿ��з񶨴�
        if (attrNeg != null)
            strNegType = attrNeg.getText();//��ȡ�񶨴�
        Element ele = element.element("message");//��ȡ�ڵ���Ϊ��message���Ķ���
        Attribute attr = ele.attribute("value");//��ȡ����value��Ԫ��ֵ
        messageValue = attr.getText(); //��ȡ��������
        List<?> list = doc.selectNodes("//result/topic");

        // �����۵ĳ�������Ľ������������ǡ����۵ġ���У����ж��ǡ�ϲŭ���塱������ߵ���һ��
        Element textT = element.element("textType");
        if (textT != null)//2017.11.10yangyong��Ӵ��룬����һ��
            if (textT.attribute("attribute").getText().equals("subjective")) {//���������
                double prob = 0.0;
                if (textT.element("happy") != null) {//ϲ
                    Element happy = textT.element("happy");
                    prob = Double.parseDouble(happy.attribute("attribute").getText()); //�������ֵ
                    if (prob > 0.3) {//���ʳ����ٽ�㣬���������
                        topicFromQiu.add("ϲ��");
                        topicFromQiu.add(Double.toString(prob));
                    }

                } else {
                    if (textT.element("angry") != null) {
                        Element angry = textT.element("angry");
                        prob = Double.parseDouble(angry.attribute("attribute").getText());
                        System.out.println("angry prab:" + prob);
                        if (prob > 0.3) {
                            topicFromQiu.add("����");
                            topicFromQiu.add(Double.toString(prob));
                        }
                    } else {
                        if (textT.element("sad") != null) {
                            Element sad = textT.element("sad");
                            prob = Double.parseDouble(sad.attribute("attribute").getText());
                            System.out.println("sad prob:" + prob);
                            if (prob > 0.3) {
                                topicFromQiu.add("��");
                                topicFromQiu.add(Double.toString(prob));
                            }
                        } else {
                            if (textT.element("fear") != null) {
                                Element fear = textT.element("fear");
                                prob = Double.parseDouble(fear.attribute("attribute").getText());
                                System.out.println("fear prab:" + prob);
                                if (prob > 0.3) {
                                    topicFromQiu.add("�־�");
                                    topicFromQiu.add(Double.toString(prob));
                                }
                            }
                        } // fear else end
                    } // sad else end
                } // angry else end
            }

        if (list.isEmpty()) {//��������û�л�ȡ���⣺result\\�µ�topic�ڵ�Ž�List
            isMiddleMessage = true;    //û������
        } else {//��������������

            Iterator<?> its1 = list.iterator();//��List��Ϣ�ŵ�Iterator<E>��
            while (its1.hasNext()) {//hasNext()+next();
                String str = "";
                String strTopic = "";
                Element topic = (Element) its1.next();//next()��ȡtopic����

                // ���Topic�ڵ����Ӧ����
                for (Iterator<Attribute> topicAttribute = topic.attributeIterator(); topicAttribute.hasNext(); ) {
                    Attribute topicName0 = (Attribute) topicAttribute.next();//hasNext()+next();

                    //Topic�������ԣ�name��probability
                    if (topicName0.getName().equals("name")) {
                        String aa = topicName0.getText().toString();
                        // ���topic������
                        if (topicName0.getText().toString() != "") {
                            strTopic = topicName0.getText().toString();
                        }
                    } else if (topicName0.getName().equals("probability")) { // ���Topic��keyֵ
                        topicProbability = topicName0.getText().toString();
                    }
                }

                if (strTopic != "") {
                    if (topicProbability != "") {//����������Ҹ��ʲ�Ϊ��
                        topicFromMG.add(strTopic);//����������
                        topicFromMG.add(topicProbability);
                    } else
                        topicName.add(strTopic);
                }

                List<Element> root = topic.elements();//����������ȡ

                String d = "";
                for (Iterator<Element> its = root.iterator(); its.hasNext(); ) {

                    Element template = (Element) its.next();// ȡÿ��template������ֵ

                    String templatename = "", templateName1 = "";//ģ������
                    String templatevalue = "";//ģ��ֵ
                    for (Iterator<Attribute> templateAttribute = template.attributeIterator();
                         templateAttribute.hasNext(); ) {

                        Attribute templateName0 = (Attribute) templateAttribute.next();//next()

                        //�����name����ֵ
                        if (templateName0.getName().equals("name")) {
                            // ���template�����֣�����IE����ļ�<topic name..<root name="ʱ��/����/����"ģ��
                            templateAttr.add(templateName0.getText());

                            templateName1 = templateName0.getText();
                            templatename = templateName0.getText();

                            logger.info("ģ������:" + templateName0.getText());

                            if (templateName0.getText().equals("����") || templateName0.getText().equals("���������ɫ")) {
                                count[0]++;//ģ����������count[0]��1
                            } else
                                count[1]++;//����������count[1]��1
                        }

                        //�����value����ֵ
                        else if (templateName0.getName().equals("value")) {
                            // ���template��valueֵ
                            templatevalue = templateName0.getText();
                            // ģ��value�ж��ԭ�ӽڵ�ʱ�����硰����:����:����|ĳ��:���족��ȡ����ֵ��������
                            ArrayList<String> templateV = processTemplateValue(templatevalue);
                            //�������ģ���µ�����ģ��ԭ�ӽڵ�
                            for (Iterator<String> its5 = templateV.iterator(); its5.hasNext(); ) {

                                String templateVV = its5.next();//next
                                templateAttr.add(templateVV);
                                templatevalue = templateVV;

                                templatename = templatename + ":" + templatevalue;//ģ������+�ܽڵ�ֵ

                                if (its5.hasNext()) {//ģ��ֵ�ж���ڵ�ֵʱ
                                    templateAttr.add(templateName1);//ArrayList<String>�зŽ�ģ������
                                    templatename = templatename + "-" + templateName1;//ģ������+�ܽڵ�ֵ+ģ������
                                }
                            }
                            logger.info("ģ���Ԫ�أ�" + templateV);
                        }
                        //�������ɫ/�񶨴� ����Ϣ
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

                    }//��������ĳ��ģ���µ�����ԭ��Ԫ��
                    d = d + "-" + templatename;//ģ�������ۼ�
                }//������������ģ��
                str = strTopic + d;//������+�����µ�����ģ����
                if (root.size() != 0)
                    TopicAndTemplate.add(str);//������ⲻΪ�գ��������������й�
            }
            //ɾ���ظ���ģ��ԭ��
            templateAttr = deleteRepeatemplateValue(templateAttr);
        }
    }

    /*
     * ��getXMLDataʹ��
     * ɾ���ظ���ģ��ԭ����Ϣ
     */
    private static ArrayList<String> deleteRepeatemplateValue(ArrayList<String> aa) {

        ArrayList<String> template = new ArrayList<String>();
        Iterator<String> its = aa.iterator();
        while (its.hasNext()) {
            boolean isRepeat = false;
            String templateName = (String) its.next();// ���ģ����
            String templateAutoName = (String) its.next();// ���ԭ����Ϣ
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
     * ����ַ�����"��С��"��������getXMLDataʹ�ã���������һ��ģ�����ж��ԭ����Ϣʱ
     *
     * @param templateValue ģ��value�ַ���
     * @return List<>:���ش�ģ���µ�����ԭ����Ϣ
     */
    private static ArrayList<String> processTemplateValue(String templateValue) {
        ArrayList<String> tempAtom = new ArrayList<String>();
        ArrayList<String> tempAtom2 = new ArrayList<String>();
        ArrayList<ArrayList<String>> tempAtom3 = new ArrayList<ArrayList<String>>();
        // �ԡ�|��Ϊ���ޣ���ģ��ֵ�ֽ������һһ�ú���processSameTemplateMuchValue���д���
        if (templateValue.contains("|"))// ��һ��ģ���е�ԭ����Ϣ���ڶ���������������
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
        // END�ԡ�|��Ϊ���ޣ���ģ��ֵ�ֽ������һһ�ú���processSameTemplateMuchValue���д���////////////////////
        ArrayList<String> temp4 = new ArrayList<String>();
        // ����ȡ���м�ģ������
        for (Iterator<ArrayList<String>> its2 = tempAtom3.iterator(); its2.hasNext(); ) {
            ArrayList<String> tempW = its2.next();
            for (Iterator<String> its3 = tempW.iterator(); its3.hasNext(); ) {
                temp4.add(its3.next());
            }
        }
        // ��������ģ��ֵ�����м�ģ������
        for (Iterator<String> its = temp4.iterator(); its.hasNext(); ) {
            templateValue = its.next();
            int postion = templateValue.indexOf(":");
            String value1 = templateValue.substring(0, postion);// ȡ��һ��
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
     * ��getXMLDataʹ��
     * ����һ��ģ����һ���ڵ��µĶ��ԭ����Ϣ
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
                    //////////////////////////// �ҳ���һ��Ԫ�ص����սڵ���///////////////////////////////////////////////////////////
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
