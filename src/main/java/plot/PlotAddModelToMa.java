package plot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class PlotAddModelToMa {

	/**
	 * 2014.9.17
	 * @param maName：ma文件的名字 Topic+bjut.plot
	 * @param topicName：英文主题名称 为plot中的主题
	 * @param model
	 * @return 将加入的model添加的AddRelatedModel 类中。并命名
	 * @throws SWRLRuleEngineException
	 * @throws SWRLFactoryException
	 * @throws IOException
	 * @throws SecurityException
	 */
	public   OWLModel processSWRL2(String maName,String topicName,OWLModel model,ArrayList<String> englishTemplate,ArrayList<String> englishTemplatePlot) throws SWRLRuleEngineException, SWRLFactoryException, SecurityException, IOException
	{

		ArrayList<String> modelValuesGround=new ArrayList();
		ArrayList<String> modelActiveGround=new ArrayList();
		ArrayList<String> modelValuesAir=new ArrayList();
		ArrayList<String> modelValuesHalfAir=new ArrayList();
		ArrayList<String> modelValuesOnTable=new ArrayList();

		ArrayList<String> modelValuesGround1=new ArrayList();
		ArrayList<String> modelActiveGround1=new ArrayList();
		ArrayList<String> modelValuesAir1=new ArrayList();
		ArrayList<String> modelValuesHalfAir1=new ArrayList();
		ArrayList<String> modelValuesOnTable1=new ArrayList();

		ArrayList<String> modelValuesGround0=new ArrayList();
		ArrayList<String> modelActiveGround0=new ArrayList();
		ArrayList<String> modelValuesAir0=new ArrayList();
		ArrayList<String> modelValuesHalfAir0=new ArrayList();
		ArrayList<String> modelValuesOnTable0=new ArrayList();

		ArrayList<String> modelValues=new ArrayList();
		ArrayList<String> FloorValues=new ArrayList();
		ArrayList<String> allValues=new ArrayList();
		ArrayList<ArrayList> modelLists=new ArrayList(); //存放model链表
		ArrayList<ArrayList> modelListFrTem=new ArrayList();//存放target=1
		ArrayList<ArrayList> plotModelListsFrHM=new ArrayList();//存放target=0的数值

		PlotDesign p=new PlotDesign();

		OWLObjectProperty hasModelFTpProperty = model
				.getOWLObjectProperty("hasModelFromTemplate");
		OWLObjectProperty addToMaProperty = model
				.getOWLObjectProperty("addToMa");
		OWLObjectProperty hasPutObjectProperty = model
				.getOWLObjectProperty("hasPutObjectInSpace");
		OWLIndividual PlotIndividual=model.getOWLIndividual(maName);
		OWLIndividual PlotGroundIndividual=model.getOWLIndividual("SP_"+topicName+"_OutOnLand");
		OWLIndividual PlotActiveGroundIndividual=model.getOWLIndividual("SP_"+topicName+"_ActiveSpaceOnGround");
		OWLIndividual PlotOnTableIndividual=model.getOWLIndividual("SP_"+topicName+"_InRoomOnTable");
		OWLIndividual PlotAirIndividual=model.getOWLIndividual("SP_"+topicName+"_OutInAir");
		OWLIndividual PlotHalfAirIndividual= model.getOWLIndividual("SP_"+topicName+"_OutInHalfAir");
		OWLIndividual PlotFloorIndividual=model.getOWLIndividual("SP_"+topicName+"_OutFloor");
		OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");
		//通过topicName得到topic实例
		//由哈工大分词得来的原子所对应的模型
		if(englishTemplate.size()>0)
		{


			for(Iterator<String> its=englishTemplate.iterator();its.hasNext();)//遍历所有的模板原子
			{
				String templateAllName=its.next();
				int iPostion=templateAllName.indexOf(":");
				// String templateAutmName=templateAllName.substring(iPostion+1, templateAllName.length());
				String templateAutmName=templateAllName.substring(iPostion+1);

				OWLIndividual templateIndividual=model.getOWLIndividual(templateAutmName);
				System.out.println("向空场景中添加的模型："+templateIndividual.getBrowserText());
				if(templateIndividual!=null)//查看模板原子所对应的实例是否存在
				{
					int valueNum=templateIndividual.getPropertyValueCount(hasModelFTpProperty);
					if(valueNum>0)//对应的model数量是否大于0
					{
						Collection templateModelVlaues=templateIndividual.getPropertyValues(hasModelFTpProperty);

						System.out.println();
						for(Iterator<OWLIndividual> its2=templateModelVlaues.iterator();its2.hasNext();)
						{
							ArrayList<String> modelList=new ArrayList();
							OWLIndividual modelIndividual=its2.next();
							modelList=new PlotDesign().getIndividualListFromClass(model,modelIndividual);
							modelLists.add(modelList);


						}
					}
				}

			}

		}
		if(!modelLists.isEmpty()){
			// System.out.println("IEModelList="+modelLists);
			modelListFrTem=modelLists;
			//System.out.println("modelListAll0="+modelListFrTem);
		}

		//通过情节规划定义的主题
		OWLNamedClass topicClass=model.getOWLNamedClass(topicName);
		if(topicClass!=null){
			System.out.print("主题是:");
			OWLIndividual topicIndividual=new PlotDesign(). GetInstanceFromClass(topicClass);//得到主题定义的一个实例
			if(topicIndividual!=null){
				String hasModelFromTemplate="hasModelFromTemplate";
				ArrayList<ArrayList> plotModelLists1=p.getIndividualFromPlotTemplate2(model, topicIndividual, "hasModelFromTemplate");
				plotModelListsFrHM=p.getIndividualFromPlotTemplate2(model, topicIndividual, "hasmodel");
				ArrayList<ArrayList> plotFloor=p.getIndividualFromPlotTemplate2(model, topicIndividual, "hasFloor");
				if(!plotModelLists1.isEmpty()){
					System.out.println("plotModelLists:"+plotModelLists1);
					for(int i=0;i<plotModelLists1.size();i++)
						modelListFrTem.add(plotModelLists1.get(i)); //将两个链表合并，最终放在一个modelList中
				}

				if(!plotFloor.isEmpty()){
					for(int i=0;i<plotFloor.size();i++)
						modelListFrTem.add(plotFloor.get(i));
				}
			}
		}
		System.out.println("modelListAll1="+modelListFrTem);
		//********************获得plot中的模板得来的模型*****************************//
		Map<String,ArrayList<ArrayList>> IEModelList=new HashMap<String,ArrayList<ArrayList>>();//1 是hasmodelFromTemplate  2 是hasmodel  3 hasFloor

		IEModelList=new PlotDesign().getIndividualFromEnglishTemplate(model, englishTemplatePlot);//获得IE模板中的所有模型
		System.out.println("IEModelListFromPlot"+IEModelList);
		for(Map.Entry<String, ArrayList<ArrayList>> entry:IEModelList.entrySet()){
			String key=entry.getKey();
			ArrayList<ArrayList> value=entry.getValue();
			if(key.equals("1")||key.equals("3")){
				for(int i=0;i<value.size();i++)
					modelListFrTem.add(value.get(i));
			}
			if(key.equals("2")){
				for(int i=0;i<value.size();i++)
					plotModelListsFrHM.add(value.get(i));
			}

		}
		//********************获得plot中的模板得来的模型*****************************//
		//target=1 的模型集合去重
		HashSet h=new HashSet(modelListFrTem);
		modelListFrTem.clear();
		modelListFrTem.addAll(h);
		System.out.println("modelListAll:"+modelListFrTem);

		for(int j=0;j<modelListFrTem.size();j++){
			ArrayList<String> modellist=modelListFrTem.get(j);
			for(int k=0;k<modellist.size();k++){
				//此处模型放置地点应该与space地点相对应。
				if(modellist.get(k).contains("Land")&&!modellist.get(0).contains("M_floor.ma")){
					modelValuesGround1.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("Young")){
					modelActiveGround1.add(modellist.get(0));
					System.out.println("model"+modelActiveGround);
					break;
				}
				if(modellist.get(k).contains("InAir")){
					modelValuesAir1.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("InhalfAir")){
					modelValuesHalfAir1.add(modellist.get(0));
				}
				if(modellist.get(k).contains("OnTable")){
					modelValuesOnTable1.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("M_floor.ma")){
					FloorValues.add(modellist.get(k));
				}

			}
		}

		//target=0的模型按空间层分开
		//target=0的模型按空间层分开
		HashSet ht0=new HashSet(plotModelListsFrHM);
		plotModelListsFrHM.clear();
		plotModelListsFrHM.addAll(ht0);
		System.out.println("plotModelListsFrHM="+plotModelListsFrHM);
		for(int j=0;j<plotModelListsFrHM.size();j++){
			ArrayList<String> modellist=plotModelListsFrHM.get(j);
			for(int k=0;k<modellist.size();k++){
				//此处模型放置地点应该与space地点相对应。
				if(modellist.get(k).contains("Land")&&!modellist.get(0).contains("M_floor.ma")){
					modelValuesGround0.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("Young")){
					modelActiveGround0.add(modellist.get(0));
					System.out.println("model"+modelActiveGround);
					break;
				}
				if(modellist.get(k).contains("InAir")){
					modelValuesAir0.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("InhalfAir")){
					modelValuesHalfAir0.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("OnTable")){
					modelValuesOnTable0.add(modellist.get(0));
					break;
				}
				if(modellist.get(k).contains("M_floor.ma")){
					FloorValues.add(modellist.get(k));
				}

			}
		}


		//target=0 与target=1联合
		for(int i=0; i<modelValuesGround1.size();i++){
			modelValuesGround.addAll(modelValuesGround0);
			modelValuesGround.add(modelValuesGround1.get(i));
		}
		for(int i=0;i<modelActiveGround1.size();i++){
			modelActiveGround.addAll(modelActiveGround0);
			modelActiveGround.add(modelActiveGround1.get(i));
		}
		for(int i=0;i<modelValuesAir1.size();i++){
			modelValuesAir.addAll(modelValuesAir0);
			modelValuesAir.add(modelValuesAir1.get(i));
		}
		for(int i=0;i<modelValuesHalfAir1.size();i++){
			modelValuesHalfAir.addAll(modelValuesHalfAir0);
			modelValuesHalfAir.add(modelValuesHalfAir1.get(i));
		}
		for(int i=0;i<modelValuesHalfAir1.size();i++){
			modelValuesHalfAir.addAll(modelValuesHalfAir0);
			modelValuesHalfAir.add(modelValuesHalfAir1.get(i));
		}
		for(int i=0;i<modelValuesOnTable1.size();i++){
			modelValuesOnTable.addAll(modelValuesOnTable0);
			modelValuesOnTable.add(modelValuesOnTable1.get(i));
		}



		//得到天空球与地面模型
//			 OWLObjectProperty hasSky=model.getOWLObjectProperty("hasSky");
//			 OWLIndividual Sky=model.getOWLIndividual("tiankong.ma");
//			 PlotAirIndividual.setPropertyValue(hasPutObjectProperty, Sky);
//			 //得到地面模型
//			 OWLObjectProperty hasFloor=model.getOWLObjectProperty("hasFloor");

		if(!modelValuesGround.isEmpty())//向可用空间中存放
		{
			PlotGroundIndividual.setPropertyValues(hasPutObjectProperty, modelValuesGround);//此处判断所放物体的大小，然后设置scale值
			//changeSpaceScale(model,PlotGroundIndividual);
		}
		if(!modelActiveGround.isEmpty()){//向动态空间存放
			PlotActiveGroundIndividual.setPropertyValues(hasPutObjectProperty, modelActiveGround);
			// changeSpaceScale(model,PlotActiveGroundIndividual);
		}

		if(!modelValuesAir.isEmpty()){
			PlotAirIndividual.setPropertyValues(hasPutObjectProperty, modelValuesAir);
			// changeSpaceScale(model,PlotAirIndividual);
		}
		if(!modelValuesHalfAir.isEmpty()){
			PlotHalfAirIndividual.setPropertyValues(hasPutObjectProperty,modelValuesHalfAir);
			//changeSpaceScale(model, PlotHalfAirIndividual);
		}
		if(! FloorValues.isEmpty()){
			PlotFloorIndividual.setPropertyValues(hasPutObjectProperty, FloorValues);
			// changeSpaceScale(model,PlotFloorIndividual);
		}
		ArrayList<OWLIndividual> modelIndividualsOnGround1=getOWLIndividual(modelValuesGround1, model);
		ArrayList<OWLIndividual> modelIndividualsActiveOnGround1=getOWLIndividual(modelActiveGround1, model);
		ArrayList<OWLIndividual> modelIndividualsInAir1=getOWLIndividual( modelValuesAir1, model);
		ArrayList<OWLIndividual> modelIndividualsInHalfAir1=getOWLIndividual( modelValuesHalfAir1, model);

		ArrayList<OWLIndividual> modelIndividualsOnGround0=getOWLIndividual(modelValuesGround0, model);
		ArrayList<OWLIndividual> modelIndividualsActiveOnGround0=getOWLIndividual(modelActiveGround0, model);
		ArrayList<OWLIndividual> modelIndividualsInAir0=getOWLIndividual( modelValuesAir0, model);
		ArrayList<OWLIndividual> modelIndividualsInHalfAir0=getOWLIndividual( modelValuesHalfAir0, model);

		ArrayList<OWLIndividual> modelIndividualsFloor=getOWLIndividual(FloorValues, model);


		int count=0;
		int countFrom=0;
		if( !modelValuesGround.isEmpty()){
			countFrom=count+1;
			count=JenaMethod.setNumberToAddModel(modelIndividualsOnGround1,modelIndividualsOnGround0,model, PlotGroundIndividual, 0,topicName);//给添加的实例添加modelID
			changeSpaceScale(model,PlotGroundIndividual,countFrom,count);//读取的AddModelRelated
		}
		if( !modelActiveGround.isEmpty()){
			countFrom=count+1;
			count=JenaMethod. setNumberToAddModel(modelIndividualsActiveOnGround1,modelIndividualsActiveOnGround0,model, PlotActiveGroundIndividual, count,topicName);//给添加的实例添加modelID
			changeSpaceScale(model,PlotActiveGroundIndividual,countFrom,count);
		}

		if( !modelValuesAir.isEmpty()){
			countFrom=count+1;
			count=JenaMethod. setNumberToAddModel(modelIndividualsInAir1,modelIndividualsInAir0,model, PlotAirIndividual, count,topicName);//给添加的实例添加modelID
			changeSpaceScale(model,PlotAirIndividual,countFrom,count);
		}

		if( !modelValuesHalfAir.isEmpty()){
			countFrom=count+1;
			count=JenaMethod. setNumberToAddModel(modelIndividualsInHalfAir1,modelIndividualsInHalfAir0,model, PlotAirIndividual, count,topicName);//给添加的实例添加modelID
			changeSpaceScale(model, PlotHalfAirIndividual,countFrom,count);
		}
		if(!FloorValues.isEmpty()){
			countFrom=count+1;
			count=JenaMethod. setNumberToAddModel(new ArrayList(),modelIndividualsFloor,model, PlotFloorIndividual, count,topicName);
			changeSpaceScale(model,PlotFloorIndividual,countFrom,count);
		}
		return model;
	}



	/**
	 * 将String 转成OWLIndividual
	 *
	 * */

	public  ArrayList<OWLIndividual> getOWLIndividual(ArrayList<String>  modelLists, OWLModel model){
		OWLIndividual modelIndividual=null;
		ArrayList<OWLIndividual> modelIndividuals=new ArrayList();
		for(int i=0;i<modelLists.size();i++){

			modelIndividual=model.getOWLIndividual(modelLists.get(i));
			modelIndividuals.add(modelIndividual);
		}

		return modelIndividuals;
	}
	/**
	 * 从space中获得应该放入物体的width and depth,然后判断空间的大小。如果查出空间的大小则设置scale值
	 * @version 2014.10.11
	 * */
	public void changeSpaceScale(OWLModel model,OWLIndividual individual,int countFrom,int countTo){
		OWLNamedClass AddModelRelated=model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addmodelNum=model.getOWLDatatypeProperty("addModelNumber");
		OWLObjectProperty hasModelName=model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty hasPutObjectInSpace=model.getOWLObjectProperty("hasPutObjectInSpace");
		OWLDatatypeProperty depthP=model.getOWLDatatypeProperty("Depth");
		OWLDatatypeProperty widthP=model.getOWLDatatypeProperty("Width");
		OWLDatatypeProperty scalex=model.getOWLDatatypeProperty("spacescalex");//width
		OWLDatatypeProperty scalez=model.getOWLDatatypeProperty("spacescalez");//depth
		String depth= (String) individual.getPropertyValue(depthP);
		String width=(String) individual.getPropertyValue(widthP);
		String obInSpacedepth="";
		String obInSpacewidth="";
		float allObjDepth=0;
		float allObjWidth=0;
		for(int i=countFrom;i<=countTo;i++){
			OWLIndividual value=model.getOWLIndividual("addModelID"+i);
			System.out.println("addModelId= "+value.getBrowserText());
			OWLIndividual modelss=(OWLIndividual) value.getPropertyValue(hasModelName);
			String modelName=modelss.getBrowserText();
			Object modelName1= value.getPropertyValue(addmodelNum);

			if(!modelName1.equals("null") || modelName1!=null){
				int modelNum=Integer.parseInt((String)(value.getPropertyValue(addmodelNum)));
				System.out.println("changeSpaceScalemodelName="+modelName+"modelNum"+modelNum);
				OWLIndividual modelIndiv=model.getOWLIndividual(modelName);
				obInSpacedepth=(String)(modelIndiv.getPropertyValue(depthP));
				obInSpacewidth=String.valueOf( modelIndiv.getPropertyValue(widthP));
				allObjDepth+=(Float.parseFloat((obInSpacedepth))*modelNum);
				allObjWidth+=(Float.parseFloat((obInSpacewidth))*modelNum);
			}



		}
			 /*Collection ObjectInSpace=individual.getPropertyValues(hasPutObjectInSpace);
			 for(Iterator it=ObjectInSpace.iterator();it.hasNext();){
				 String ob=it.next().toString();
				OWLIndividual value= model.getOWLIndividual( ob);
				System.out.println("objectInSpace:"+value.getBrowserText());
				obInSpacedepth=(String)(value.getPropertyValue(depthP));
				obInSpacewidth=String.valueOf( value.getPropertyValue(widthP));
				allObjDepth+=Float.parseFloat((obInSpacedepth));
				allObjWidth+=Float.parseFloat((obInSpacewidth));
			 }
			 System.out.println("depth值"+depth);*/
		if(allObjDepth>Integer.parseInt(depth)){

			individual.setPropertyValue(scalez, String.valueOf((allObjDepth)/Integer.parseInt(depth)+0.1));
			System.out.println("SpaceScale="+String.valueOf((allObjDepth)/Integer.parseInt(depth)+0.1));
		}
		if(allObjWidth>Integer.parseInt(width)){
			individual.setPropertyValue(scalex, String.valueOf((allObjWidth)/Integer.parseInt(width)+0.1));
			System.out.println("SpaceScalex="+String.valueOf((allObjWidth)/Integer.parseInt(width)+0.1));
		}
	}




	/**
	 * 打开一个owl文件
	 * @param url：owl文件存在的路劲
	 * @return
	 * @throws OntologyLoadException
	 */
	public static OWLModel createOWLFile1(String url)
			throws OntologyLoadException {
		try{
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;
		}
		catch( OntologyLoadException zz){
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;

		}
	}

	public static OntModel createOWLModelFile2(String url) {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
				null);
		model.read(url);
		return model;
	}
	public static void main(String args[]) throws OntologyLoadException{
		PlotAddModelToMa p=new PlotAddModelToMa();
		System.gc();
		System.out.println("1");
		String url = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		//通过url获得owl模型
		OWLModel model=createOWLFile1(url);


		System.out.println("ma");

		//p.changeSpaceScale(model, model.getOWLIndividual("SP_BalletDanceActionPlot_OutOnLand"),3);
	}

}
