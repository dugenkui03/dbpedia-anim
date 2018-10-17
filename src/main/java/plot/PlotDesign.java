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
     * ������ȡ�����ģ���¶�Ӧ��ʵ�����������������������ﻷ���ȵȣ�Ȼ������ȡ��������IE��ȡ����Ӣ��ģ������Ӧ��ģ��ʵ��
     * ������ģ���¶����ʵ������IEģ����Ϊ׼
     * 0���Action 1��Ÿ���model 2����music
     */

    /**
     * ͨ������ģ��ԭ��������ģ��ԭ������Ӧ��ģ�ͣ�hasModelFromTemplate��
     *
     * @param model
     * @param englishTemplatePlot Ӣ��ģ�� [DayTemplate:dayTemplate:0.5, TemperatureTemplate:TemperatureTemplate_warm:1.0, ManCharacterTemplate:botherTemplate:1.0]
     * @return [model, super, super][model, super, super]
     */
    public Map<String, ArrayList<ArrayList>> getIndividualFromEnglishTemplate(OWLModel model, ArrayList<String> englishTemplatePlot) {
        Map<String, ArrayList<ArrayList>> models = new HashMap<>();

        //����Ӣ��ģ��
        for (Iterator<String> its = englishTemplatePlot.iterator(); its.hasNext(); ) {
            /**
             * ��ȡӢ��ģ��ʵ������
             */
            String templateAllName = its.next();
            //��ȡӢ��ģ��������ʵ������ȥ���������:[DayTemplate:dayTemplate:0.5]->[DayTemplate:dayTemplate]
            templateAllName = templateAllName.substring(0, templateAllName.lastIndexOf(":"));
            //temp[0]��ģ��������temp[1]��ģ��ʵ�����ơ�������ʵ�����ƻ�ȡ��Ӧʵ��
            String[] tempTypeAndIns = templateAllName.split(":");
            OWLIndividual templateIndividual = model.getOWLIndividual(tempTypeAndIns[1]);

            /**
             * ����ҵ���Ӧ��Ӣ��ģ��ʵ��
             */
            if (templateIndividual != null) {

                //��ȡ�� hasModelFromTemplate ���Թ�����ʵ��
                OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
                if (templateIndividual.getPropertyValueCount(hasModelFTpProperty) > 0) {
                    ArrayList<ArrayList> individualLists = new ArrayList();
                    Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);
                    //�������Թ�����ʵ��
                    for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                        OWLIndividual value = its2.next();
                        //���ѡ����һ���ֵ�ʵ��(��Ϊ�Լ�)����list[0],����ʵ��Location���Թ�����ʵ�����������ߣ�������ĳ��ģ�ͼ�����Է���Ŀռ�ʵ��
                        ArrayList individualList = getIndividualListFromClass(model, value);
                        individualLists.add(individualList);
                    }
                    models.put("1", individualLists);
                }

                OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
                if (templateIndividual.getPropertyValueCount(hasModelProperty) > 0) {//��Ӧ��hasmodel�����Ƿ����0
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
     * �Ķ�Ontology����ģ�������������⻻Ϊ��������
     * ��ʱһ��ģ��ֻ��һ�����࣬ͬʱ��ȡ������ͬlocation��superclass��ģ��ȡ��
     * */


    /**
     * ���ѡ����һ���ֵ�ʵ��(��Ϊ�Լ�)����list[0],����ʵ��Location���Թ�����ʵ������������
     *
     * @param owlIndividual ʵ��
     */
    public ArrayList<String> getIndividualListFromClass(OWLModel model, OWLIndividual owlIndividual) {
        ArrayList respList = new ArrayList();
        respList.add(owlIndividual.getBrowserText());

        //��ȡʵ����������
        OWLNamedClass individualType = (OWLNamedClass) owlIndividual.getDirectType();

        //��ȡʵ��ʹ�� Location ���Թ�����ʵ�����ϣ�����������뷵��ֵ������
        Collection locationIndividuals = owlIndividual.getPropertyValues(model.getOWLObjectProperty("Location"));
        if (locationIndividuals != null) {
            for (Iterator it = locationIndividuals.iterator(); it.hasNext(); ) { //�п����ж��location���������ⶼ���԰ڷ�
                OWLIndividual locationIndi = (OWLIndividual) it.next();
                respList.add(locationIndi.getBrowserText()); //[model,location1,location2] or[model,location]
            }

            //getDirectInstances����ȡ���±ߵ�ʵ�����������������ʵ����������ȡ����owlIndividual���ֵ�ʵ��
            Collection brothersIndividuals = individualType.getDirectInstances();
            respList.set(0, new ArrayList(brothersIndividuals).get(new Random().nextInt(brothersIndividuals.size())));
        }
        return respList;
    }


    /**
     * ��ָ���༰������������õ�һ��ʵ��
     */
    public OWLIndividual GetInstanceFromClass(OWLNamedClass owlC) {
        //��ȡ�����±�����ʵ����boolean��ʾ�Ƿ��ȡ�������ʵ��
        Collection instances = owlC.getInstances(true);

        //���ѡ��һ��ʵ������
        Random random = new Random(System.currentTimeMillis());
        return new ArrayList<OWLIndividual>(instances).get(random.nextInt(instances.size()));
    }

    /**
     * ��������ʵ���¶����ģ�ͣ�hasModelFromTemplate��
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
        if (valueNum > 0) {//��Ӧ��model�����Ƿ����0
            Collection templateModelVlaues = TopicIndiv.getPropertyValues(hasModelFTpProperty);

            for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext(); ) {
                OWLIndividual value = its2.next();

                System.out.println("value:Model=" + value.getBrowserText());
                ArrayList<String> modelList = getIndividualListFromClass(model, value);//���ص�����������ĳһ���ֵ�ʵ��
                individualLists.add(modelList);
            }
        }
        return individualLists;
    }

    /**
     * Ϊ�����ɵ�space���ֵ
     */
    public void setValueTospace(OWLIndividual spaceName, OWLModel model) {
        OWLObjectProperty hasLayout = model.getOWLObjectProperty("p3:hasLayout");
        OWLNamedClass outdoorGroundLayout = model.getOWLNamedClass("p3:OutdoorGroundLayout");
        OWLNamedClass SkyLayout = model.getOWLNamedClass("p3:SkyLayout");
        OWLDatatypeProperty Spacerotatex = model.getOWLDatatypeProperty("scenerotatex");
        OWLDatatypeProperty Spacerotatey = model.getOWLDatatypeProperty("scenerotatey");
        OWLDatatypeProperty Spacerotatez = model.getOWLDatatypeProperty("scenerotatez");
        spaceName.addPropertyValue(Spacerotatex, "0.0");//��ת
        spaceName.addPropertyValue(Spacerotatey, "0.0");
        spaceName.addPropertyValue(Spacerotatez, "0.0");

        OWLDatatypeProperty Spacescalex = model.getOWLDatatypeProperty("spacescalex");
        OWLDatatypeProperty Spacescaley = model.getOWLDatatypeProperty("spacescaley");
        OWLDatatypeProperty Spacescalez = model.getOWLDatatypeProperty("spacescalez");//����
        spaceName.addPropertyValue(Spacescalex, "1.0");//?�ɱ䣬�ӱ���
        spaceName.addPropertyValue(Spacescaley, "1.0");
        spaceName.addPropertyValue(Spacescalez, "1.0");

        OWLDatatypeProperty Spacecenterx = model.getOWLDatatypeProperty("spacecenterx");//���ĵ�
        OWLDatatypeProperty Spacecentery = model.getOWLDatatypeProperty("spacecentery");
        OWLDatatypeProperty Spacecenterz = model.getOWLDatatypeProperty("spacecenterz");
        String SpacecenterxValue = "";
        String SpacecenteryValue = "";
        String SpacecenterzValue = "";

        //��ʼplane��С��Ϊdepth and width ontology�洢��Ϊx.z�Ĳ�ֵ
        OWLDatatypeProperty Depth = model.getOWLDatatypeProperty("Depth");
        OWLDatatypeProperty Width = model.getOWLDatatypeProperty("Width");

        String depth = "";//�����ռ��y���ֵ
        String width = "";//�����ռ��x��ֵ
        //���������������ĵ�
        if (spaceName.getBrowserText().contains("Plot_OutFloor")) {
            Random r = new Random();
            int d = r.nextInt(100) + 100;//(100-200)
            depth = Integer.toString(d);
            width = Integer.toString(d);

            SpacecenterxValue = "0.0";
            SpacecenteryValue = "0.0";
            SpacecenterzValue = "0.0";
        }
        //���澲̬�����������ĵ�
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

        //����Ϊ�����Ͽ��е����ĵ�
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

        //����Ϊ�������е����ĵ�
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

        //��̬�����������ĵ�
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

        spaceName.addPropertyValue(Spacecenterx, SpacecenterxValue);//?���ݲ�ͬ��space�����ĵ㲻ͬ
        spaceName.addPropertyValue(Spacecentery, SpacecenteryValue);
        spaceName.addPropertyValue(Spacecenterz, SpacecenterzValue);

        OWLDatatypeProperty hasCamera = model.getOWLDatatypeProperty("hasCamera");
        spaceName.addPropertyValue(hasCamera, true);

        OWLObjectProperty hasShape = model.getOWLObjectProperty("hasShape");
        spaceName.addPropertyValue(hasShape, "Plane");
    }

    /**
     * ����ma�ļ����������ֵ
     *
     * @param maName ���ɵ�ma����
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
        //���ñ���ͼƬ����ҪΪ��ʵ�������  3�����ⳡ��
        OWLDatatypeProperty backgroundPictureType = model.getOWLDatatypeProperty("backgroundPictureType");
        OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//���ڻ��������

        //=========2016.3.24 ����  ���=============
//        OWLObjectProperty hasValueOfTime = model.getOWLObjectProperty("hasValueOfTime");

        //���ñ���ͼƬ
        OWLObjectProperty hasBackgroundPicture = model.getOWLObjectProperty("hasBackgroundPicture");
        Random r = new Random();
        ma.addPropertyValue(maFrameNum, r.nextInt(200) + 300);
        ma.addPropertyValue(scenerotatez, "0");
        ma.addPropertyValue(scenerotatex, "0");
        ma.addPropertyValue(scenerotatey, "0");
        ma.addPropertyValue(hasSky, true);
        String spaceName = maName.substring(0, maName.length() - 3);

        //��дspace����,��Ϊ��̬space �;�̬space
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

        setValueTospace(OutsideonGround, model);//������space���ֵ
        setValueTospace(OutsideFloor, model);//������Floorspace���ֵ
        setValueTospace(OutsideRoomInAir, model);//������space���ֵ
        setValueTospace(ActiveSpaceOnGround, model);//����������space���ֵ
        setValueTospace(OutsideRoomInHalfAir, model);//����������ӿռ�

        //���ڱ����������򵥶���
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
        ma.addPropertyValue(backgroundPictureType, 3);//����������������������λ��
        OWLIndividual outDoorDescription = model.getOWLIndividual("outDoorDescription");
        //System.out.println("outDoorDescription"+outDoorDescription+hasValueOfPlane);
        ma.addPropertyValue(hasValueOfPlane, outDoorDescription);

        //===============2016.3.24   ����  ���
        OWLIndividual dayTimeDescription = model.getOWLIndividual("dayTimeDescription");
        ma.addPropertyValue(hasValueOfPlane, dayTimeDescription);

        //=============================
        //���ñ���ͼƬ
        String bgPIC = "BackgroundScenePicture";
        OWLNamedClass bgPic = model.getOWLNamedClass(bgPIC);
        OWLIndividual backgroundPic = GetInstanceFromClass(bgPic);

        ma.addPropertyValue(hasBackgroundPicture, backgroundPic);
    }


    public static void main(String[] args) throws OntologyLoadException {
//        String url = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
//        //ͨ��url���owlģ��
//        OWLModel model = createOWLFile1(url);
//        //ɾ��p14�е�ʵ��
//        RDFSNamedClass html = model.getRDFSNamedClass("p14:html");
//        Collection in = html.getInstances(true);
//        for (Iterator it = in.iterator(); it.hasNext(); ) {
//            RDFIndividual indi = (RDFIndividual) it.next();
//            indi.delete();
//        }
    }

}