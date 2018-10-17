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
        //读取上面输出的xml文件中的add规则中的信息存到类数组中
        Element root = doc.getRootElement();
        Element name = root.element("maName");
        String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");

        ArrayList al = Readxml.getAddinfo(name);//获取xml中add中的信息
        ArrayList a = Readxml.getSpace2(al);//处理上面输出的信息将重复的合并后去掉其中一个

        ArrayList<OWLIndividual> spIndividual = ReadOwl.getInstance(layoutowl, "SceneSpace");    //读取owl文件中sencespace下面的所有实例

        for (int i = 0; i < a.size(); i++) {
            SceneSpace sp = (SceneSpace) a.get(i);
            if (sp.getSpname().contains("Floor")) {
                a.remove(i);
            }
        }
        ArrayList<String> layEffect = SelectLayout2.selectLayout2(layoutowl, ieTopic, a, areashape);

        for (int i = 0; i < areashape.size(); i++) {
            System.out.println("第" + (i + 1) + "个可用空间");
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
