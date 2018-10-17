package plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;

public class PlotDesign {

    /**
     * 首先提取出情节模板下对应的实例的描述，包括动作，人物环境等等；然后再提取出短信中IE提取到的英语模板所对应的模型实例
     * 如果情节模板下定义的实例，以IE模板结果为准
     * 0存放Action 1存放各种model 2放入music
     */

    /**
     * 通过所有模板原子来查找模板原子所对应的模型（hasModelFromTemplate）
     *
     * @param model
     * @param englishTemplatePlot 英文模板 [DayTemplate:dayTemplate:0.5, TemperatureTemplate:TemperatureTemplate_warm:1.0, ManCharacterTemplate:botherTemplate:1.0]
     * @return [model, super, super][model, super, super]
     */
    public Map<String, ArrayList<ArrayList>> getIndividualFromEnglishTemplate(OWLModel model, ArrayList<String> englishTemplatePlot) {
        Map<String, ArrayList<ArrayList>> models = new HashMap<>();

        //遍历英文模板
        for (Iterator<String> its = englishTemplatePlot.iterator(); its.hasNext(); ) {
            /**
             * 获取英文模板实例名称
             */
            String templateAllName = its.next();
            //获取英文模板类名和实例名，去掉后边数字:[DayTemplate:dayTemplate:0.5]->[DayTemplate:dayTemplate]
            templateAllName = templateAllName.substring(0, templateAllName.lastIndexOf(":"));
            //temp[0]是模板类名，temp[1]是模板实例名称。根据其实例名称获取对应实例
            String[] tempTypeAndIns = templateAllName.split(":");
            OWLIndividual templateIndividual = model.getOWLIndividual(tempTypeAndIns[1]);

            /**
             * 如果找到对应的英文模板实例
             */
            if (templateIndividual != null) {

                //获取其 hasModelFromTemplate 属性关联的实例
                OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
                if (templateIndividual.getPropertyValueCount(hasModelFTpProperty) > 0) {
                    ArrayList<ArrayList> individualLists = new ArrayList();
                    Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);
                    //遍历属性关联的实例
                    for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                        OWLIndividual value = its2.next();
                        //随机选出其一个兄弟实例(可为自己)放在list[0],参数实例Location属性关联的实例放在链表后边：用于求某类模型及其可以放入的空间实例
                        ArrayList individualList = getIndividualListFromClass(model, value);
                        individualLists.add(individualList);
                    }
                    models.put("1", individualLists);
                }

                OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
                if (templateIndividual.getPropertyValueCount(hasModelProperty) > 0) {//对应的hasmodel数量是否大于0
                    ArrayList<ArrayList> individualLists = new ArrayList();
                    Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelProperty);
                    for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                        OWLIndividual value = its2.next();
                        ArrayList modelList = getIndividualListFromClass(model, value);
                        individualLists.add(modelList);
                    }
                    models.put("2", individualLists);
                }

                OWLObjectProperty hasFloorProperty = model.getOWLObjectProperty("hasFloor");
                if (templateIndividual.getPropertyValueCount(hasFloorProperty) > 0) {
                    Collection templateModelVlaues = templateIndividual.getPropertyValues(hasFloorProperty);
                    ArrayList<ArrayList> individualLists = new ArrayList();
                    for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                        OWLIndividual value = its2.next();
                        ArrayList modelList = getIndividualListFromClass(model, value);
                        individualLists.add(modelList);
                    }
                    models.put("3", individualLists);
                }

            }
        }
        return models;
    }


    /***
     * 改动Ontology。将模型属于室内室外换为属性描述
     * 此时一个模型只有一个父类，同时获取具有相同location和superclass的模型取出
     * */


    /**
     * 随机选出其一个兄弟实例(可为自己)放在list[0],参数实例Location属性关联的实例放在链表后边
     *
     * @param owlIndividual 实例
     */
    public ArrayList<String> getIndividualListFromClass(OWLModel model, OWLIndividual owlIndividual) {
        ArrayList respList = new ArrayList();
        respList.add(owlIndividual.getBrowserText());

        //获取实例所述的类
        OWLNamedClass individualType = (OWLNamedClass) owlIndividual.getDirectType();

        //获取实体使用 Location 属性关联的实例集合，并遍历后放入返回值链表中
        Collection locationIndividuals = owlIndividual.getPropertyValues(model.getOWLObjectProperty("Location"));
        if (locationIndividuals != null) {
            for (Iterator it = locationIndividuals.iterator(); it.hasNext(); ) { //有可能有多个location即室内室外都可以摆放
                OWLIndividual locationIndi = (OWLIndividual) it.next();
                respList.add(locationIndi.getBrowserText()); //[model,location1,location2] or[model,location]
            }

            //getDirectInstances：获取类下边的实例，不包括其子类的实例——即获取参数owlIndividual的兄弟实例
            Collection brothersIndividuals = individualType.getDirectInstances();
            respList.set(0, new ArrayList(brothersIndividuals).get(new Random().nextInt(brothersIndividuals.size())));
        }
        return respList;
    }


    /**
     * 从指定类及其子类中随机得到一个实例
     */
    public OWLIndividual GetInstanceFromClass(OWLNamedClass owlC) {
        //获取此类下边所有实例，boolean表示是否获取其子类的实例
        Collection instances = owlC.getInstances(true);

        //随机选出一个实例返回
        Random random = new Random(System.currentTimeMillis());
        return new ArrayList<OWLIndividual>(instances).get(random.nextInt(instances.size()));
    }

    /**
     * 查找主题实例下定义的模型（hasModelFromTemplate）
     *
     * @param model
     * @param TopicIndiv EnglishTopic
     * @return individualLists [value,super1,super2][value2,super1...]
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public ArrayList<ArrayList> getIndividualFromPlotTemplate2(OWLModel model, OWLIndividual TopicIndiv, String factor) {
        ArrayList<ArrayList> individualLists = new ArrayList();
        OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty(factor);

        int valueNum = TopicIndiv.getPropertyValueCount(hasModelFTpProperty);
        if (valueNum > 0) {//对应的model数量是否大于0
            Collection templateModelVlaues = TopicIndiv.getPropertyValues(hasModelFTpProperty);

            for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                OWLIndividual value = its2.next();

                System.out.println("value:Model=" + value.getBrowserText());
                ArrayList<String> modelList = getIndividualListFromClass(model, value);//返回的是它和它的某一个兄弟实例
                individualLists.add(modelList);
            }
        }
        return individualLists;
    }

    /**
     * 为新生成的space添加值
     */
    public void setValueTospace(OWLIndividual spaceName, OWLModel model) {
        OWLObjectProperty hasLayout = model.getOWLObjectProperty("p3:hasLayout");
        OWLNamedClass outdoorGroundLayout = model.getOWLNamedClass("p3:OutdoorGroundLayout");
        OWLNamedClass SkyLayout = model.getOWLNamedClass("p3:SkyLayout");
        OWLDatatypeProperty Spacerotatex = model.getOWLDatatypeProperty("scenerotatex");
        OWLDatatypeProperty Spacerotatey = model.getOWLDatatypeProperty("scenerotatey");
        OWLDatatypeProperty Spacerotatez = model.getOWLDatatypeProperty("scenerotatez");
        spaceName.addPropertyValue(Spacerotatex, "0.0");//旋转
        spaceName.addPropertyValue(Spacerotatey, "0.0");
        spaceName.addPropertyValue(Spacerotatez, "0.0");

        OWLDatatypeProperty Spacescalex = model.getOWLDatatypeProperty("spacescalex");
        OWLDatatypeProperty Spacescaley = model.getOWLDatatypeProperty("spacescaley");
        OWLDatatypeProperty Spacescalez = model.getOWLDatatypeProperty("spacescalez");//缩放
        spaceName.addPropertyValue(Spacescalex, "1.0");//?可变，加倍变
        spaceName.addPropertyValue(Spacescaley, "1.0");
        spaceName.addPropertyValue(Spacescalez, "1.0");

        OWLDatatypeProperty Spacecenterx = model.getOWLDatatypeProperty("spacecenterx");//中心点
        OWLDatatypeProperty Spacecentery = model.getOWLDatatypeProperty("spacecentery");
        OWLDatatypeProperty Spacecenterz = model.getOWLDatatypeProperty("spacecenterz");
        String SpacecenterxValue = "";
        String SpacecenteryValue = "";
        String SpacecenterzValue = "";

        //初始plane大小即为depth and width ontology存储的为x.z的差值
        OWLDatatypeProperty Depth = model.getOWLDatatypeProperty("Depth");
        OWLDatatypeProperty Width = model.getOWLDatatypeProperty("Width");

        String depth = "";//表明空间的y轴差值
        String width = "";//表明空间的x轴值
        //地面区域与其中心点
        if (spaceName.getBrowserText().contains("Plot_OutFloor")) {
            Random r = new Random();
            int d = r.nextInt(100) + 100;//(100-200)
            depth = Integer.toString(d);
            width = Integer.toString(d);

            SpacecenterxValue = "0.0";
            SpacecenteryValue = "0.0";
            SpacecenterzValue = "0.0";
        }
        //地面静态区域与其中心点
        if (spaceName.getBrowserText().contains("Plot_OutOnLand")) {
            Random r = new Random();
            int d = r.nextInt(100) + 100;//(100-200)
            depth = Integer.toString(d);
            width = Integer.toString(d);

            int tempx = Integer.parseInt(depth) / 2;
            int tempz = Integer.parseInt(width) / 2;
            SpacecenterxValue = Integer.toString(tempx);
            SpacecenteryValue = "0.0";
            SpacecenterzValue = Integer.toString(tempz);
            OWLIndividual LayoutIndividual = GetInstanceFromClass(outdoorGroundLayout);
            spaceName.addPropertyValue(hasLayout, LayoutIndividual);
        }

        //表明为户外上空中的中心点
        if (spaceName.getBrowserText().contains("Plot_OutInAir")) {
            Random r = new Random();
            int d = r.nextInt(200) + 200;//(200-400)
            depth = Integer.toString(d);
            width = Integer.toString(d);
            int y = r.nextInt(20) + 180;

            SpacecenterxValue = "0.0";
            SpacecenteryValue = Integer.toString(y);
            SpacecenterzValue = "0.0";

            OWLIndividual LayoutIndividual = GetInstanceFromClass(SkyLayout);
            spaceName.addPropertyValue(hasLayout, LayoutIndividual);
        }

        //表明为户外半空中的中心点
        if (spaceName.getBrowserText().contains("Plot_OutInHalfAir")) {
            Random r = new Random();
            int d = r.nextInt(100) + 100;//(100-200)
            depth = Integer.toString(d);
            width = Integer.toString(d);
            int y = r.nextInt(20) + 50;
            SpacecenterxValue = "0.0";
            SpacecenteryValue = Integer.toString(y);
            SpacecenterzValue = "0.0";

            OWLIndividual LayoutIndividual = GetInstanceFromClass(SkyLayout);
            spaceName.addPropertyValue(hasLayout, LayoutIndividual);
        }

        //动态地面与其中心点
        if (spaceName.getBrowserText().contains("Plot_ActiveSpaceOnGround")) {
            Random r = new Random();
            int d = r.nextInt(100) + 100;//(100-200)
            depth = Integer.toString(d);
            int w = r.nextInt(100) + 100;
            width = Integer.toString(w);

            int tempx = d / 2;
            int tempz = w / 2;
            SpacecenterxValue = Integer.toString(tempx);
            SpacecenteryValue = "0.0";
            SpacecenterzValue = Integer.toString(tempz);

            OWLIndividual LayoutIndividual = GetInstanceFromClass(outdoorGroundLayout);
            spaceName.addPropertyValue(hasLayout, LayoutIndividual);
        }

        spaceName.addPropertyValue(Depth, depth);
        spaceName.addPropertyValue(Width, width);

        spaceName.addPropertyValue(Spacecenterx, SpacecenterxValue);//?根据不同的space，中心点不同
        spaceName.addPropertyValue(Spacecentery, SpacecenteryValue);
        spaceName.addPropertyValue(Spacecenterz, SpacecenterzValue);

        OWLDatatypeProperty hasCamera = model.getOWLDatatypeProperty("hasCamera");
        spaceName.addPropertyValue(hasCamera, true);

        OWLObjectProperty hasShape = model.getOWLObjectProperty("hasShape");
        spaceName.addPropertyValue(hasShape, "Plane");
    }

    /**
     * 生成ma文件里面的属性值
     *
     * @param maName 生成的ma名字
     */
    @SuppressWarnings("deprecation")
    public void GenerateMa(String maName, OWLModel model) {
        OWLNamedClass PlotMa = model.getOWLNamedClass("PlotMa");
        OWLIndividual ma = PlotMa.createOWLIndividual(maName);
        OWLDatatypeProperty maFrameNum = model.getOWLDatatypeProperty("maFrameNumber");
        OWLDatatypeProperty hasSky = model.getOWLDatatypeProperty("hasSky");

        OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
        OWLObjectProperty usedSpaceInMa = model.getOWLObjectProperty("usedSpaceInMa");
        OWLDatatypeProperty scenerotatex = model.getOWLDatatypeProperty("scenerotatex");
        OWLDatatypeProperty scenerotatey = model.getOWLDatatypeProperty("scenerotatey");
        OWLDatatypeProperty scenerotatez = model.getOWLDatatypeProperty("scenerotatez");
        //设置背景图片，主要为了实现摄像机  3代表户外场景
        OWLDatatypeProperty backgroundPictureType = model.getOWLDatatypeProperty("backgroundPictureType");
        OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//户内户外的描述

        //=========2016.3.24 黄蕾  添加=============
//        OWLObjectProperty hasValueOfTime = model.getOWLObjectProperty("hasValueOfTime");

        //设置背景图片
        OWLObjectProperty hasBackgroundPicture = model.getOWLObjectProperty("hasBackgroundPicture");
        Random r = new Random();
        ma.addPropertyValue(maFrameNum, r.nextInt(200) + 300);
        ma.addPropertyValue(scenerotatez, "0");
        ma.addPropertyValue(scenerotatex, "0");
        ma.addPropertyValue(scenerotatey, "0");
        ma.addPropertyValue(hasSky, true);
        String spaceName = maName.substring(0, maName.length() - 3);

        //编写space的类,分为动态space 和静态space
        OWLNamedClass OutsideonGroundspaceClass = model.getOWLNamedClass("PlaneSceneSpaceOutsideRoomOnGround");
        OWLNamedClass InsideRoomOnTablespaceClass = model.getOWLNamedClass("PlaneSceneSpaceInsideRoomOnTable");
        OWLNamedClass OutsideRoomInAirspaceClass = model.getOWLNamedClass("PlaneSceneSpaceOutsideRoomInAir");
        OWLNamedClass ActiveSpaceOnGroundClass = model.getOWLNamedClass("ActiveSpaceOnGround");
        OWLNamedClass OutsideRoomInHalfAirspaceClass = model.getOWLNamedClass("PlaneSceneSpaceOutsideRoomInHalfAir");

        OWLIndividual OutsideonGround = OutsideonGroundspaceClass.createOWLIndividual("SP_" + spaceName + "_OutOnLand");
        OWLIndividual OutsideFloor = OutsideonGroundspaceClass.createOWLIndividual("SP_" + spaceName + "_OutFloor");
        OWLIndividual OutsideRoomInAir = OutsideRoomInAirspaceClass.createOWLIndividual("SP_" + spaceName + "_OutInAir");
        OWLIndividual InsideRoomOnTable = InsideRoomOnTablespaceClass.createOWLIndividual("SP_" + spaceName + "_InRoomOnTable");
        OWLIndividual ActiveSpaceOnGround = ActiveSpaceOnGroundClass.createOWLIndividual("SP_" + spaceName + "_ActiveSpaceOnGround");
        OWLIndividual OutsideRoomInHalfAir = OutsideRoomInHalfAirspaceClass.createOWLIndividual("SP_" + spaceName + "_OutInHalfAir");

        setValueTospace(OutsideonGround, model);//给地面space添加值
        setValueTospace(OutsideFloor, model);//给地面Floorspace添加值
        setValueTospace(OutsideRoomInAir, model);//给空中space添加值
        setValueTospace(ActiveSpaceOnGround, model);//给动作地面space添加值
        setValueTospace(OutsideRoomInHalfAir, model);//给户外半空添加空间

        //对于背景场景的则单独算
        ma.addPropertyValue(hasSceneSpace, OutsideonGround);
        ma.addPropertyValue(hasSceneSpace, OutsideFloor);
        ma.addPropertyValue(hasSceneSpace, OutsideRoomInAir);
        ma.addPropertyValue(hasSceneSpace, InsideRoomOnTable);
        ma.addPropertyValue(hasSceneSpace, ActiveSpaceOnGround);
        ma.addPropertyValue(hasSceneSpace, OutsideRoomInHalfAir);
        //usedSpaceInMa
        ma.addPropertyValue(usedSpaceInMa, OutsideonGround);
        ma.addPropertyValue(usedSpaceInMa, OutsideFloor);
        ma.addPropertyValue(usedSpaceInMa, OutsideRoomInAir);
        ma.addPropertyValue(usedSpaceInMa, InsideRoomOnTable);
        ma.addPropertyValue(usedSpaceInMa, OutsideRoomInHalfAir);
        ma.addPropertyValue(usedSpaceInMa, ActiveSpaceOnGround);
        //backgroundPictureType
        ma.addPropertyValue(backgroundPictureType, 3);//设置摄像机拍摄的室内室外位置
        OWLIndividual outDoorDescription = model.getOWLIndividual("outDoorDescription");
        //System.out.println("outDoorDescription"+outDoorDescription+hasValueOfPlane);
        ma.addPropertyValue(hasValueOfPlane, outDoorDescription);

        //===============2016.3.24   黄蕾  添加
        OWLIndividual dayTimeDescription = model.getOWLIndividual("dayTimeDescription");
        ma.addPropertyValue(hasValueOfPlane, dayTimeDescription);

        //=============================
        //设置背景图片
        String bgPIC = "BackgroundScenePicture";
        OWLNamedClass bgPic = model.getOWLNamedClass(bgPIC);
        OWLIndividual backgroundPic = GetInstanceFromClass(bgPic);

        ma.addPropertyValue(hasBackgroundPicture, backgroundPic);
    }


    public static void main(String[] args) throws OntologyLoadException {
//        String url = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
//        //通过url获得owl模型
//        OWLModel model = createOWLFile1(url);
//        //删除p14中的实例
//        RDFSNamedClass html = model.getRDFSNamedClass("p14:html");
//        Collection in = html.getInstances(true);
//        for (Iterator it = in.iterator(); it.hasNext(); ) {
//            RDFIndividual indi = (RDFIndividual) it.next();
//            indi.delete();
//        }
    }

}