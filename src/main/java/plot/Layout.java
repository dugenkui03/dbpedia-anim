package plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class Layout {

    public static Document setLayout(OWLModel layoutowl, String maName, Document doc) {
        if ("nothing.ma".equals(maName) || "empty.ma".equals(maName) || "".equals(maName))
            return doc;
        ArrayList<HashMap<String, String>> areashape = new ArrayList();
        //��ȡ���������xml�ļ��е�add�����е���Ϣ�浽��������
        Element root = doc.getRootElement();
        Element name = root.element("maName");
        String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");

        ArrayList al = Readxml.getAddinfo(name);//��ȡxml��add�е���Ϣ
        ArrayList a = Readxml.getSpace2(al);//���������������Ϣ���ظ��ĺϲ���ȥ������һ��

        ArrayList<OWLIndividual> spIndividual = ReadOwl.getInstance(layoutowl, "SceneSpace");    //��ȡowl�ļ���sencespace���������ʵ��

        for (int i = 0; i < a.size(); i++) {
            SceneSpace sp = (SceneSpace) a.get(i);
            if (sp.getSpname().contains("Floor")) {
                a.remove(i);
            }
        }
        ArrayList<String> layEffect = SelectLayout2.selectLayout2(layoutowl, ieTopic, a, areashape);

        for (int i = 0; i < areashape.size(); i++) {
            System.out.println("��" + (i + 1) + "�����ÿռ�");
            System.out.println("layoutEffect: " + layEffect.get(i));
            HashMap testmap = areashape.get(i);
            Iterator iter = testmap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = (Entry) iter.next();
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        for (int i = 0; i < a.size(); i++) {
            SceneSpace sp = (SceneSpace) a.get(i);
            HashMap<String, String> testmap = areashape.get(i);
            String layeffect = layEffect.get(i);
            Readxml.writexml2(name, sp, testmap, layeffect);
        }

        System.out.println("1");
        return doc;
    }
}
