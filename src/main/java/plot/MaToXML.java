package plot;



import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
//import org.jdom.Document;
import org.dom4j.Document;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;



public class MaToXML {

	static String fileName = "ColorAndLight_06_28.owl";
	static String prefixAll = "p5:";
	static Collection disContrastPlan;
	static Collection contrastPlan;
	static Random random = new Random(System.currentTimeMillis());
	static ArrayList<String>season=new ArrayList();
	//用于生成随机数，获取当前时间作为随机数因子
	static String human = "p1:Human";
	boolean flagFloor=false;///表明地面是否变颜色
	/**备份
	 * 根据主题，为ma场景中每个物体设置颜色,为ma场景挑选灯光布局，并设置灯光颜色
	 * 若无主题，随机挑选颜色搭配
	 * @param owlModel
	 * @param maName ma名称，字符串
	 * @param doc XML文档
	 * @return 返回修改后的XML文档（未保存）
	 */
	/*
	public Document setColorAndLight(OWLModel owlModel, String maName, Document doc)
		throws  SWRLFactoryException, SWRLRuleEngineException{
		OWLIndividual ma = owlModel.getOWLIndividual(maName);
		OWLDatatypeProperty topicNameProperty = owlModel.getOWLDatatypeProperty(prefixAll+"topicName");
		OWLDatatypeProperty hasContrastProperty = owlModel.getOWLDatatypeProperty("hasContrast");
		disContrastPlan = owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, false);
		contrastPlan = owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, true);
		//get the ma topic individual property

		Collection topicNames = ma.getPropertyValues(topicNameProperty);
		if(topicNames.isEmpty())
		{
			System.out.println("短信无主题，随机挑选颜色");
			setModelColor(owlModel, ma, doc);
			setLight(owlModel, ma, doc);
		}
		else{
			Iterator jt = topicNames.iterator();
			String topicName = (String) jt.next();
			String ImpClassName = getTopicImpClass(owlModel, prefixAll+topicName);
			if(ImpClassName.equals(""))
			{
 				//若找不到主题对应的颜色搭配类，随机挑选颜色
				System.out.println("找不到主题对应的颜色搭配类，随机挑选颜色");
				setModelColor(owlModel, ma, doc);
				setLight(owlModel, ma, doc);
			}
			else
			{
				setModelColor(owlModel, ma, doc, ImpClassName);
				setLight(owlModel, ma, doc, ImpClassName);
			}
		}
		System.out.println("-----------------------**********************THE PROGRAM IS FINISHED**********************--------------------------");
		return doc;
	}
	*/

	/**2013.3修改
	 * 根据主题，为ma场景中每个物体设置颜色,为ma场景挑选灯光布局，并设置灯光颜色
	 * 若无主题，随机挑选颜色搭配
	 * @param owlModel
	 * @param maName ma名称，字符串
	 * @param doc XML文档
	 * @return 返回修改后的XML文档（未保存）
	 */
	public Document setColorAndLight(OWLModel owlModel, String maName, Document doc, ArrayList<String> colorChangeAttr,ArrayList<String>seasonlist)
			throws  SWRLFactoryException, SWRLRuleEngineException{
		System.out.println("------------Color----------------");
		season=seasonlist;
		int tnum=colorChangeAttr.size();

		if(tnum>0){
			int i;
			for(i=0;i<tnum;){
				doc=setTargetModelColor(owlModel,colorChangeAttr.get(i),colorChangeAttr.get(i+1),colorChangeAttr.get(i+2), doc);
				i=i+3;
			}
		}//为指定物体分配颜色
		OWLIndividual ma = owlModel.getOWLIndividual(maName);

		OWLDatatypeProperty topicNameProperty = owlModel.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty hasContrastProperty = owlModel.getOWLDatatypeProperty(prefixAll+"hasContrast");
		disContrastPlan = owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, false);
		contrastPlan = owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, true);
		//get the ma topic individual property

		Collection topicNames= ma.getPropertyValues(topicNameProperty);
		System.out.println("topicNames.size="+topicNames.size());
		if(topicNames.isEmpty())
		{
			System.out.println("短信无主题，随机挑选颜色");
			setModelColor(owlModel, ma, doc, colorChangeAttr);

		}
		else{
			Iterator jt = topicNames.iterator();
			String topicName = (String) jt.next();
			String ImpClassName = getTopicImpClass(owlModel, topicName);
			if(ImpClassName.equals(""))
			{
				//若找不到主题对应的颜色搭配类，随机挑选颜色
				System.out.println("找不到主题对应的颜色搭配类，随机挑选颜色");
				setModelColor(owlModel, ma, doc, colorChangeAttr);
				//setLight(owlModel, ma, doc);
			}
			else
			{
				setModelColor(owlModel, ma, doc, colorChangeAttr, ImpClassName);
				//setLight(owlModel, ma, doc, ImpClassName);
			}
		}
		if(!flagFloor)
			getTexturePlanForFloor(doc,owlModel,ma);
		System.out.println("-----------------------**********************THE PROGRAM IS FINISHED**********************--------------------------");
		return doc;
	}

	/**
	 * 根据主题，为ma场景中每个物体设置颜色
	 * @param owlModel
	 * @param ma ma实例
	 * @param doc XML文档
	 * @param topicName
	 * @return 返回修改后的XML文档（未保存）
	 */
	public Document setModelColor(OWLModel owlModel, OWLIndividual ma, Document doc, ArrayList<String> colorChangeAttr, String ImpClassName)
	{
		//OWLNamedClass topic = owlModel.getOWLNamedClass(prefixAll + ImpClassName);
		Collection colorIndividual = getColorMatchingPlan(owlModel, ImpClassName, ma);
		if(colorIndividual.isEmpty())
		{
			System.out.println("未找到颜色搭配方案，不写入doc，直接返回");
			return doc;
		}
		Collection<OWLIndividual> modelAndColor = matchModelColor(owlModel, ma, colorIndividual, colorChangeAttr);
		if(modelAndColor == null)
			return doc;

		//////////////////////////////////////////////////////////////////////////////////////////////
		//若场景中有人物模型，进行贴图规划
		if(hasCharacter(owlModel,ma))
			getTexturePlan(doc,owlModel, ma);
		getTexturePlanForFloor(doc,owlModel,ma);
		printRule(doc, owlModel, modelAndColor);
		System.out.println("找到和主题匹配的颜色搭配方案，写入doc");
		return doc;
	}



	/**
	 * 随机选择配色方案，为ma场景中每个物体设置颜色
	 * @param owlModel
	 * @param ma ma实例
	 * @param doc XML文档
	 * @return 返回修改后的XML文档（未保存）
	 */
	public Document setModelColor(OWLModel owlModel, OWLIndividual ma, Document doc, ArrayList<String> colorChangeAttr)
	{
		Collection colorIndividual = getColorMatchingPlan(owlModel, ma);
		if(colorIndividual.isEmpty())
		{
			System.out.println("未随机找到颜色搭配方案，不写入doc，直接返回");
			return doc;
		}
		Collection<OWLIndividual> modelAndColor = matchModelColor(owlModel, ma, colorIndividual, colorChangeAttr);
		if(modelAndColor == null)
			return doc;

		//////////////////////////////////////////////////////////////////////////////////////////////
		//若场景中有人物模型，进行贴图规划
		if(hasCharacter(owlModel,ma))
		{
			System.out.println("@@@@@@@@@@@ The ma scene has character~~~~");
			getTexturePlan(doc,owlModel, ma);
		}
		//
		printRule(doc, owlModel, modelAndColor);

		System.out.println("随机找到颜色搭配方案，写入doc");
		return doc;
	}

	/**
	 * 为ma场景挑选灯光布局，并根据主题设置灯光颜色
	 * @param owlModel
	 * @param ma ma实例
	 * @param doc XML文档
	 * @param topicName
	 * @return 返回修改后的XML文档（未保存）
	 */
	public Document setLight(OWLModel owlModel, OWLIndividual ma, Document doc, String ImpClassName)
			throws  SWRLFactoryException, SWRLRuleEngineException{
		OWLIndividual lightLayout = chooseLightLayout(owlModel, ma);
		if(lightLayout == null)
		{
			System.out.println("未找到灯光布局方案，不写入doc，直接返回");
			return doc;
		}
		doc = printRule(doc,owlModel,lightLayout);
		Collection<OWLIndividual> lightAndColor = setLightColor(owlModel, ImpClassName, lightLayout);
		if(lightAndColor == null)
		{
			System.out.println("未找到符合主题的灯光颜色，不写入doc，直接返回");
			return doc;
		}
		printRule(doc, owlModel, lightAndColor);
		System.out.println("找到和ma匹配的灯光布局方案，根据主题设置灯光颜色，写入doc");
		return doc;
	}

	/**
	 * 为ma场景挑选灯光布局，并随机设置灯光颜色
	 * @param owlModel
	 * @param ma ma实例
	 * @param doc XML文档
	 * @param topicName
	 * @return 返回修改后的XML文档（未保存）
	 */
	public Document setLight(OWLModel owlModel, OWLIndividual ma, Document doc)
			throws  SWRLFactoryException, SWRLRuleEngineException{
		OWLIndividual lightLayout = chooseLightLayout(owlModel, ma);
		if(lightLayout == null)
		{
			System.out.println("未找到灯光布局方案，不写入doc，直接返回");
			return doc;
		}
		doc = printRule(doc,owlModel,lightLayout);
		Collection<OWLIndividual> lightAndColor = setLightColor(owlModel, lightLayout);
		if(lightAndColor == null)
		{
			System.out.println("未找到符合主题的灯光颜色，不写入doc，直接返回");
			return doc;
		}
		printRule(doc, owlModel, lightAndColor);
		System.out.println("找到和ma匹配的灯光布局方案，随机设置灯光颜色，写入doc");
		return doc;
	}

	/**
	 * 为ma场景挑选灯光布局
	 * @param owlModel owl文件
	 * @param ma ma实例
	 * @return 返回灯光布局实例短信无主题，随机挑选颜色
	 */
	public OWLIndividual chooseLightLayout(OWLModel owlModel, OWLIndividual ma)
	{

		String prefix = "";
		String maSceneName = ma.getBrowserText();
		System.out.println("maSceneName: "+maSceneName);
		OWLIndividual maScene = owlModel.getOWLIndividual(maSceneName);
		//OWLNamedClass LightLayoutClass = owlModel.getOWLNamedClass(prefix+"LightLayout");
		//Collection LightLayout_collection = LightLayoutClass.getInstances(true);
		OWLObjectProperty hasMaProperty = owlModel.getOWLObjectProperty(prefix+"hasMa");
		RDFSNamedClass superClass = getClassFromIndividual(owlModel, ma);
		System.out.println("superClass Name: "+superClass.getBrowserText());
		Collection SuiLightLayout = new ArrayList();
		if(superClass.getBrowserText().contains("Background") && superClass.getBrowserText().contains("Scene"))
		{
			OWLNamedClass BackgroundScene = owlModel.getOWLNamedClass("BackgroundScene");
			SuiLightLayout = owlModel.getRDFResourcesWithPropertyValue(hasMaProperty, BackgroundScene);
		}
		else if(superClass.getBrowserText().equals("EmptyScene"))
		{
			OWLNamedClass EmptyScene = owlModel.getOWLNamedClass("EmptyScene");
			SuiLightLayout = owlModel.getRDFResourcesWithPropertyValue(hasMaProperty, EmptyScene);
		}
		else
			SuiLightLayout = owlModel.getRDFResourcesWithPropertyValue(hasMaProperty, maScene);

		/**Gets all RDFResources that have a given value for a given property.
		 maScene必须是实例，不能为字符串，因为property中对应的是实例*/

		if(SuiLightLayout.isEmpty())
			return null;

		OWLIndividual[] SuiLightLayoutArr = (OWLIndividual[]) SuiLightLayout.toArray(new OWLIndividual[0]);

		int n = SuiLightLayout.size();
		int randomNum = random.nextInt(n);
		OWLIndividual LightLayout = SuiLightLayoutArr[randomNum];
		return LightLayout;
	}

	/**
	 * 获取主题对应在colorImplication中的类
	 * @param owlModel
	 * @param topic 主题
	 * @return 返回对应类字符串
	 */
	public String getTopicImpClass(OWLModel owlModel, String topicName)
	{
		String prefix = "p5:";
		String ImpClassName = "";
		//OWLNamedClass topic = owlModel.getOWLNamedClass(prefix+topicName);
		OWLNamedClass ColorImplicationClass = owlModel.getOWLNamedClass(prefix+"ColorImplication");
		OWLObjectProperty hasTopicProperty = owlModel.getOWLObjectProperty(prefix+"hasTopic");
		Collection ColorImpSub1_collection = ColorImplicationClass.getSubclasses(true);//原来为false

		/**对每个类使用getRestrictions函数，使用OWLRestriction类函数提取约束中所有类(String类型），进行判断，提高效率
		 不用判断约束是否为union，只要看约束中是否含有topic即可，提高效率*/
		for (Iterator jt = ColorImpSub1_collection.iterator(); jt.hasNext();) {
			OWLNamedClass ColorImpSubTemp = (OWLNamedClass) jt.next();
			Collection restrictions = ColorImpSubTemp.getRestrictions(hasTopicProperty, false);
			//false表示，不包括父类的约束条件
			for (Iterator jm = restrictions.iterator(); jm.hasNext();) {
				OWLRestriction restriction = (OWLRestriction) jm.next();
				String restrict = restriction.getFillerText();
				if(restrict.indexOf(topicName) >= 0)
					ImpClassName = ColorImpSubTemp.getBrowserText();
			}
		}

		System.out.println("ImpClassName is : "+ImpClassName);
		return ImpClassName;
	}

	/**
	 * 随机设置灯光颜色
	 * @param owlModel owl文件
	 * @param LightLayout ma场景灯光布局实例
	 * @return 返回颜色实例
	 */
	public Collection<OWLIndividual> setLightColor(OWLModel owlModel, OWLIndividual LightLayout)
			throws  SWRLFactoryException, SWRLRuleEngineException{

		//用于生成随机数
		String prefix = "p5:";
		OWLNamedClass ColorClass = owlModel.getOWLNamedClass(prefix+"Color");
		OWLObjectProperty hasLightProperty = owlModel.getOWLObjectProperty(prefix+"hasLight");
		OWLDatatypeProperty isLightColorProperty = owlModel.getOWLDatatypeProperty(prefix+"isLightColor");
		if(LightLayout.getPropertyValues(hasLightProperty) == null)
		{
			System.out.println("灯光布局方案中没有灯光实例，返回null");
			return null;
		}

		//set the suitable color isLightColorProperty true
		executeSWRLEngine(owlModel,"chooseLightColor");
		Collection SuiLightColor = owlModel.getRDFResourcesWithPropertyValue(isLightColorProperty, true);

		//Collection ColorIndividuals = ColorClass.getInstances(true);
		int n = SuiLightColor.size();
		int randomNum = random.nextInt(n);
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) SuiLightColor.toArray(new OWLIndividual[0]);
		Collection hasLight = LightLayout.getPropertyValues(hasLightProperty);
		Collection<OWLIndividual> modelAndColor = new ArrayList<OWLIndividual>();
		for (Iterator jt = hasLight.iterator(); jt.hasNext();) {
			OWLIndividual light = (OWLIndividual) jt.next();
			modelAndColor.add(light);
			modelAndColor.add(ColorIndividual[randomNum]);
		}
		return modelAndColor;
	}

	/**
	 * 根据主题获取灯光颜色
	 * @param owlModel owl文件
	 * @param topic 短信主题
	 * @param LightLayout ma场景灯光布局实例
	 * @return 返回颜色实例
	 */
	public Collection<OWLIndividual> setLightColor(OWLModel owlModel, String ImpClassName, OWLIndividual LightLayout)
			throws  SWRLFactoryException, SWRLRuleEngineException{

		//用于生成随机数
		String prefix = "p5:";
		OWLObjectProperty hasLightProperty = owlModel.getOWLObjectProperty(prefix+"hasLight");
		//OWLNamedClass ColorClass = owlModel.getOWLNamedClass(prefix+"Color");
		OWLNamedClass TopicSuiColorImpClass = owlModel.getOWLNamedClass(ImpClassName);
		OWLObjectProperty hasLightColorToneProperty = owlModel.getOWLObjectProperty(prefix+"hasLightColorTone");
		//OWLObjectProperty hasLightToneProperty = owlModel.getOWLObjectProperty(prefix+"hasLightTone");
		OWLDatatypeProperty isLightColorProperty = owlModel.getOWLDatatypeProperty(prefix+"isLightColor");
		//Collection<OWLNamedClass> SuiColorMatch = new ArrayList<OWLNamedClass>();

		if(TopicSuiColorImpClass.getSomeValuesFrom(hasLightColorToneProperty) == null)
		{
			System.out.println("主题对应类中没有灯光色调属性，返回null");
			return null;
		}
		if(LightLayout.getPropertyValues(hasLightProperty) == null)
		{
			System.out.println("灯光布局方案中没有灯光实例，返回null");
			return null;
		}
		//if the hasSuitableMean property has only one value,can't use collection
		//OWLNamedClass hasLightColorToneClass = (OWLNamedClass) TopicSuiColorImpClass.getSomeValuesFrom(hasLightColorToneProperty);
		//get the implication class's hasLightColor property to find the suitable color collection

		Collection lightColorTone_restrictions = TopicSuiColorImpClass.getRestrictions(hasLightColorToneProperty, false);
		String lightColorTone = "";
		OWLRestriction rest_temp = (OWLRestriction) (lightColorTone_restrictions.iterator()).next();
		lightColorTone = rest_temp.getFillerText();

		//if restriction contains any kind of tones, execute corresponding rules, set the suitable color isLightColorProperty true
		if(lightColorTone.contains("ColdTone"))
			executeSWRLEngine(owlModel,"chooseLightColor_cold");
		if(lightColorTone.contains("MiddleTone"))
			executeSWRLEngine(owlModel,"chooseLightColor_middle");
		if(lightColorTone.contains("WarmTone"))
			executeSWRLEngine(owlModel,"chooseLightColor_warm");
		if(!(lightColorTone.contains("ColdTone")||lightColorTone.contains("MiddleTone")||lightColorTone.contains("WarmTone")))
		{
			System.out.println("主题对应的implication类的hasLightColorToneProperty异常，不在正常值范围（ColdTone，MiddleTone或WarmTone），返回null");
			return null;
		}

		Collection SuiLightColor = owlModel.getRDFResourcesWithPropertyValue(isLightColorProperty, true);
		if(SuiLightColor.isEmpty())
		{
			System.out.println("没有找到和主题相关的颜色实例，返回null");
			return null;
		}
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) SuiLightColor.toArray(new OWLIndividual[0]);
		int n = ColorIndividual.length;
		int randomNum = random.nextInt(n);
		Collection hasLight = LightLayout.getPropertyValues(hasLightProperty);
		Collection<OWLIndividual> modelAndColor = new ArrayList<OWLIndividual>();
		for (Iterator jt = hasLight.iterator(); jt.hasNext();) {
			OWLIndividual light = (OWLIndividual) jt.next();
			modelAndColor.add(light);
			modelAndColor.add(ColorIndividual[randomNum]);
		}
		return modelAndColor;
	}

	/**
	 * 随机获取颜色搭配方案，从所有的配色方案中随机获取
	 * @param owlModel
	 * @return 返回颜色搭配方案中所有颜色实例的集合
	 */
	public Collection getColorMatchingPlan(OWLModel owlModel, OWLIndividual ma)
	{
		String prefix = "p5:";
		OWLNamedClass ColorMatchingPlanClass = owlModel.getOWLNamedClass(prefix+"ColorMatchingPlan");
		OWLObjectProperty hasColorProperty = owlModel.getOWLObjectProperty(prefix+"hasColor");

		Collection<OWLIndividual> SuiColorMatchIndividuals = new ArrayList<OWLIndividual>();//符合要求的所有配色方案实例
		SuiColorMatchIndividuals = ColorMatchingPlanClass.getInstances(true);
		if(SuiColorMatchIndividuals.isEmpty())
		{
			System.out.println("配色方案类中无实例，返回空集合");
			return SuiColorMatchIndividuals;
		}

		//判断ma场景中是否有人物模型，若有，设置为强烈对比，若没有，设置为和谐
		boolean contrast = hasCharacter(owlModel, ma);

		int modelNum = hasModelNum(owlModel, ma);
		boolean colorPlanMore = false;
		//false为场景中使用一个配色方案
		if(modelNum >= 8)
			colorPlanMore = true;
		Collection colorCollection = chooseThoughContrast(owlModel, contrast, colorPlanMore, SuiColorMatchIndividuals);
		return colorCollection;

	}

	/**
	 * 获取和短信主题对应的颜色搭配方案
	 * @param owlModel owl模型名
	 * @param topic 短信主题
	 * @return 返回颜色搭配方案中所有颜色实例的集合
	 */
	public Collection getColorMatchingPlan(OWLModel owlModel, String ImpClassName, OWLIndividual ma)
	{
		//get the implication class of the topic class, search rule to find topic
		String prefix = "p5:";
		OWLNamedClass ColorMatchingPlanClass = owlModel.getOWLNamedClass(prefix+"ColorMatchingPlan");
		OWLObjectProperty hasColorProperty = owlModel.getOWLObjectProperty(prefix+"hasColor");

		Collection colorCollection = new ArrayList();
		OWLNamedClass TopicSuiColorImpClass = owlModel.getOWLNamedClass(ImpClassName);
		//get the implication topic class and find its suitable mean, and find the color matching plan which has the same emotion tag
		OWLObjectProperty hasSuitableMeanProperty = owlModel.getOWLObjectProperty(prefix+"hasSuitableMean");
		OWLObjectProperty hasEmotionTagProperty = owlModel.getOWLObjectProperty(prefix+"hasEmotionTag");
		//Collection<OWLNamedClass> SuiColorMatchClass = new ArrayList<OWLNamedClass>();
		Collection<OWLIndividual> SuiColorMatchIndividuals = new ArrayList<OWLIndividual>();//符合要求的所有配色方案实例
		if (TopicSuiColorImpClass.getSomeValuesFrom(hasSuitableMeanProperty) == null){
			System.out.println(TopicSuiColorImpClass.getBrowserText() + " don't have " + hasSuitableMeanProperty.getBrowserText() + " rule");
			return colorCollection;
		}
		else{
			Collection SuiMean_restrictions = TopicSuiColorImpClass.getRestrictions(hasSuitableMeanProperty, false);
			String ColorImpSuiMean = "";
			for (Iterator jn = SuiMean_restrictions.iterator(); jn.hasNext();) {
				OWLRestriction rest_temp = (OWLRestriction) jn.next();
				ColorImpSuiMean = rest_temp.getFillerText();
			}
			Collection ColorMatch_collection = ColorMatchingPlanClass.getSubclasses(true);
			for (Iterator jt = ColorMatch_collection.iterator(); jt.hasNext();) {
				OWLNamedClass ColorMatTemp = (OWLNamedClass) jt.next();
				Collection restrictions = ColorMatTemp.getRestrictions(hasEmotionTagProperty, false);
				//false表示，不包括父类的约束条件
				for (Iterator jm = restrictions.iterator(); jm.hasNext();) {
					OWLRestriction restriction = (OWLRestriction) jm.next();
					String restrict = restriction.getFillerText();
					if(ColorImpSuiMean.indexOf(restrict) >= 0)
					{
						//SuiColorMatchClass.add(ColorMatTemp);
						SuiColorMatchIndividuals.addAll(ColorMatTemp.getInstances(true));
					}
					//配色方案中emotiontag只有一个含义，而ColorImp类中可能包含多个含义，所以用ColorImpSuiMean是否包含restrict来进行判断
				}
			}
		}

		/**
		 * 2012-6-14 添加
		 */
		//添加用场景的季节信息扩充配色方案集合+++++++++++
		Collection seasonPlan = getSeasonPlan(owlModel, ma);
		if(seasonPlan!=null)
			SuiColorMatchIndividuals.addAll(seasonPlan);
		System.out.println(SuiColorMatchIndividuals);

		if(SuiColorMatchIndividuals.isEmpty())
		{
			System.out.println("没有适合的配色方案，返回空集合");
			return colorCollection;
		}

		//判断ma场景中是否有人物模型，若有，设置为强烈对比，若没有，设置为和谐
		boolean contrast = hasCharacter(owlModel, ma);

		int modelNum = hasModelNum(owlModel, ma);
		boolean colorPlanMore = false;
		//false为场景中使用一个配色方案
		if(modelNum >= 8)
			colorPlanMore = true;
		colorCollection = chooseThoughContrast(owlModel, contrast, colorPlanMore, SuiColorMatchIndividuals);
		return colorCollection;

	}
	/**
	 * 为陆地模型进行贴图规划
	 * ma 场景名称，新的plotDesign肯定有M_Floor.ma
	 * 2015.1.5
	 * */
	public void getTexturePlanForFloor(Document doc, OWLModel owlModel,OWLIndividual ma){
		Collection<OWLIndividual>  models=getModelsInMa(owlModel,ma);//get the modelIDs
		for(Iterator model=models.iterator();model.hasNext();){
			OWLIndividual modelIDTemp=(OWLIndividual) model.next();
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel,modelIDTemp);
			if(modelNameTemp.getBrowserText().equals("M_floor.ma")){
				texturePlanPerModel(doc,owlModel,modelIDTemp);
			}
		}
	}
	/**
	 * 为people模型进行贴图规划
	 * @param owlModel
	 * @param ma 场景文件
	 */
	public void getTexturePlan(Document doc, OWLModel owlModel, OWLIndividual ma)
	{
		//Collection textureModelCollection = new ArrayList();
		//获取ma场景中所有模型
		Collection<OWLIndividual> characterCollection = getCharacters(owlModel, ma);
		System.out.println("The ma scene has "+characterCollection.size()+" characters");

		if(characterCollection.isEmpty())
		{
			System.out.println("场景中没有人物模型，返回空集合");
			return;
		}
		for (Iterator jm = characterCollection.iterator(); jm.hasNext();) {
			OWLIndividual characterID = (OWLIndividual) jm.next();
			System.out.println("场景中存在人物模型： "+ characterID.getBrowserText());
			//texturePlanPerModel(doc, owlModel, characterID);
		}
	}

	/**
	 * 为每个模型进行贴图规划
	 * @param owlModel
	 * @param model 模型实例
	 */
	public void texturePlanPerModel(Document doc, OWLModel owlModel, OWLIndividual model)
	{
		Collection<RDFSNamedClass> textureTypeCollection = new ArrayList<RDFSNamedClass>();
		Collection suiTextures = new ArrayList();
		OWLObjectProperty hasSuitableModelProperty = owlModel.getOWLObjectProperty("p5:hasSuitableModel");
		OWLDatatypeProperty hasTextureNameProperty = owlModel.getOWLDatatypeProperty("p5:hasTextureName");
		OWLObjectProperty hasSuitSeasonProperty=owlModel.getOWLObjectProperty("hasSuitSeason");
		//根据modelID找到model的实例
		OWLIndividual modelName = getModelNameFromID(owlModel,model);
		suiTextures = owlModel.getRDFResourcesWithPropertyValue(hasSuitableModelProperty, modelName);

		OWLIndividual[] suiTextureIndividual = (OWLIndividual[]) suiTextures.toArray(new OWLIndividual[0]);
		ArrayList<OWLIndividual> suitIndividual=new ArrayList();

		for(int ii=0;ii<suiTextureIndividual.length;ii++)
		{
			OWLIndividual ind=suiTextureIndividual[ii];
			Collection col=ind.getPropertyValues(hasSuitSeasonProperty);
			if(col.size()==0)
			{
				continue;
			}
			else
			{
				for(Iterator ite=col.iterator();ite.hasNext();)
				{
					OWLIndividual owl=(OWLIndividual) ite.next();
					String str=owl.getBrowserText().toString();
					String str1=str.substring(0, str.indexOf("Description"));
					for(int is=0;is<season.size();is++)
					{
						if(season.get(is).equals(str1))
						{

							suitIndividual.add(ind);
							System.out.println(ind.getBrowserText());
							break;
						}

					}

				}

			}
		}

		int n = suitIndividual.size();
		int randomNum;
		if(n>0)
		{
			for(int i=0;i<5;i++)
			{
				randomNum = random.nextInt(n);
				RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suitIndividual.get(randomNum));
				//未对此类型贴图进行规划
				if(!textureTypeCollection.contains(textureClass))
				{
					textureTypeCollection.add(textureClass);
					System.out.println("贴图类型： "+ textureClass.getBrowserText());
					String textureName = (String) suitIndividual.get(randomNum).getPropertyValue(hasTextureNameProperty);
					//打印规则
					printRule(doc, owlModel, model, textureClass.getBrowserText(), textureName);
					break;
				}
				else
					System.out.println("贴图类型冲突： "+ textureClass.getBrowserText());
			}

		}
		else{
			n = suiTextures.size();
			if(suiTextures.size()>0)
			{
				for(int i=0; i<5; i++)
				{
					randomNum = random.nextInt(n);
					RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suiTextureIndividual[randomNum]);
					//未对此类型贴图进行规划
					if(!textureTypeCollection.contains(textureClass))
					{
						textureTypeCollection.add(textureClass);
						System.out.println("贴图类型： "+ textureClass.getBrowserText());
						String textureName = (String) suiTextureIndividual[randomNum].getPropertyValue(hasTextureNameProperty);
						//打印规则
						printRule(doc, owlModel, model, textureClass.getBrowserText(), textureName);
					}
					else
						System.out.println("贴图类型冲突： "+ textureClass.getBrowserText());
				}
			}
		}

	}

	/**
	 * 2012-6-14 添加
	 */
	/**
	 * 根据场景季节信息找到对应配色方案，在getColorMatchingPlan函数中调用++++++++++++++++
	 * @param owlModel
	 * @param ma 动画场景文件
	 * @return 返回匹配的配色方案集合
	 */
	public Collection getSeasonPlan(OWLModel owlModel, OWLIndividual ma)
	{
		OWLObjectProperty hasSeasonProperty = owlModel.getOWLObjectProperty("hasSeason");
		Collection seasonPlan = new ArrayList();

		System.out.println("maSceneName: "+ma.getBrowserText());
		if(ma.getPropertyValue(hasSeasonProperty)==null)
		{
			System.out.println("There is no season attribute.");
			return null;
		}
		OWLIndividual seasonInfo = (OWLIndividual) ma.getPropertyValue(hasSeasonProperty);//不是唯一性的属性
		//季节实例所在类，春夏秋冬
		RDFSNamedClass season = getClassFromIndividual(owlModel, seasonInfo);
		System.out.println("seasonPlan: "+season.getBrowserText());

		if(season.getBrowserText().equals("Winter"))
		{
			OWLNamedClass winterPlan = owlModel.getOWLNamedClass("p5:ColorWinter");
			seasonPlan = winterPlan.getInstances(true);
			System.out.println("seasonPlan choosen : "+season.getBrowserText());
			return seasonPlan;
		}
		else if(season.getBrowserText().equals("Autumn"))
		{
			OWLNamedClass autumnPlan = owlModel.getOWLNamedClass("p5:ColorAutumn");
			seasonPlan = autumnPlan.getInstances(true);
			System.out.println("seasonPlan choosen : "+season.getBrowserText());
			return seasonPlan;
		}
		else if(season.getBrowserText().equals("Summer"))
		{
			OWLNamedClass summerPlan = owlModel.getOWLNamedClass("p5:ColorSummer");
			seasonPlan = summerPlan.getInstances(true);
			System.out.println("seasonPlan choosen : "+season.getBrowserText());
			return seasonPlan;
		}
		else if(season.getBrowserText().equals("Spring"))
		{
			OWLNamedClass springPlan = owlModel.getOWLNamedClass("p5:ColorSpring");
			seasonPlan = springPlan.getInstances(true);
			System.out.println("seasonPlan choosen : "+season.getBrowserText());
			return seasonPlan;
		}
		else
		{
			System.out.println("season individual has wrong type.");
			return null;
		}

	}

	/**@@@@@@@@@@@@@
	 * 为场景中所有模型设置颜色，使用求余数方式，用模型所在位置除以颜色数量，取得模型颜色
	 * 若场景中模型数量大于3，修改场景中30%-70%的模型，随机挑选，若场景中模型数量小于等于3，则改变所有物体的颜色
	 * @param owlModel owl模型
	 * @param ma ma实例
	 * @param colorCollection 配色方案中颜色集合
	 * @return 模型和颜色集合，一个模型实例，一个颜色实例
	 */
	public Collection<OWLIndividual> matchModelColor(OWLModel owlModel, OWLIndividual ma, Collection colorCollection, ArrayList<String> colorChangeAttr){
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) colorCollection.toArray(new OWLIndividual[0]);

		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		//获取ma场景中所有模型
		Collection<OWLIndividual> modelAndColor = new ArrayList<OWLIndividual>();
		int colorNum = ColorIndividual.length;
		int i=0;
		if(modelCollection.isEmpty())
		{
			System.out.println("场景中没有模型，返回空集合");
			return null;
		}

		/**
		 * 2012-03-19新添加
		 * 若场景中模型数量大于3，修改场景中30%-70%的模型，随机挑选，若场景中模型数量小于等于3，则改变所有物体的颜色
		 */
		//随机生成0-3的整数
		int randomNum = random.nextInt(4);
		//若场景中模型数量小于等于3，则改变所有物体的颜色
		int changeNum = modelCollection.size();;

		//若场景中模型数量大于3，挑选changeNum个模型修改颜色，比例为30%-70%
		if(modelCollection.size()>3)
			changeNum= modelCollection.size()*(3+randomNum)/10;



		OWLIndividual[] models = (OWLIndividual[]) modelCollection.toArray(new OWLIndividual[0]);
		System.out.println("@@@@@@ the scene has "+modelCollection.size()+" models");
		System.out.println("****** change "+changeNum+" models");
		int ii,jj;
		int tnum=colorChangeAttr.size();
		for(int j=0;j<changeNum;j++){
			//在现有模型的数量中随机选择随机数
			int num = random.nextInt(modelCollection.size());
			OWLIndividual modelTemp = models[num];
			//删除已经选中的模型，并重新转为数组
			modelCollection.remove(modelTemp);
			models = (OWLIndividual[]) modelCollection.toArray(new OWLIndividual[0]);

			//根据modelID找到model的实例
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel,modelTemp);

			if(!(modelNameTemp.getBrowserText()).endsWith(".ma"))
				//如果场景中对象不是模型，例如snowParticleEffect，不带有.ma后缀，则不修改其颜色
				continue;

			//判断模型是否为人物模型，若为人物模型，不放入颜色规划中
			if(isCharacter(owlModel,modelNameTemp))
				continue;
			//System.out.println(modelTemp.getBrowserText());
			if(modelNameTemp.getBrowserText().equals("M_floor.ma"))
				flagFloor=true;
			jj=0;
			if(tnum>0){
				for(ii=0;ii<tnum;){
					if(modelNameTemp.getBrowserText().contains(colorChangeAttr.get(ii)))
						jj=1;
					ii=ii+2;
				}
			}
			if(jj==1){
				System.out.println(modelNameTemp.getBrowserText()+"是指定颜色");
				continue;
			}
			modelAndColor.add(modelTemp);
			int randomColor = i%colorNum;
			modelAndColor.add(ColorIndividual[randomColor]);
			i++;
		}
		return modelAndColor;
	}

	/**@@@@@@@@@@@@@@@@@@@@
	 * 获取ma场景中的所有模型，包括后来添加的模型
	 * @param owlModel owl模型
	 * @param ma ma场景
	 * @return ma场景中所有模型
	 */
	public Collection<OWLIndividual> getModelsInMa(OWLModel owlModel, OWLIndividual ma){
		Collection<OWLIndividual> modelCollection = new ArrayList<OWLIndividual>();
		OWLObjectProperty hasModelProperty = owlModel.getOWLObjectProperty("hasmodel");

		Collection hasModelCollection = ma.getPropertyValues(hasModelProperty);
		//获取场景模型实例ID
		modelCollection.addAll(hasModelCollection);

		//在场景中加入的模型的类
		OWLNamedClass AddModelClass = owlModel.getOWLNamedClass("AddModelRelated");
		Collection addModelCollection = AddModelClass.getInstances(true);

		//addToMa属性中包含的模型也是场景中的模型
		//OWLObjectProperty addToMaProperty = owlModel.getOWLObjectProperty(prefixAll+"addToMa");
		//Collection addModelCollection = ma.getPropertyValues(addToMaProperty);
		if(addModelCollection.size() != 0)
			modelCollection.addAll(addModelCollection);

		Collection deleteModelList = new ArrayList();
		//删除不可重用的模型
		for (Iterator jt = modelCollection.iterator(); jt.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			//根据modelID找到model的实例
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel,modelIDTemp);
			//$$$$$$$$$$$$$$$$不可重用模型没有modelName的实例
			if(modelNameTemp==null)
			{
				System.out.println("modelID: "+modelIDTemp.getBrowserText()+" don't have modelName");
				deleteModelList.add(modelIDTemp);
			}
		}
		modelCollection.removeAll(deleteModelList);

		return modelCollection;
	}

	/**@@@@@@@@@@@@@@@@@
	 * 判断ma场景中是否有人物模型，若有，设置为强烈对比，返回true，若没有，设置为和谐，返回false
	 * @param owlModel owl模型
	 * @param ma ma场景
	 * @return 返回对比性，强烈为true，和谐为false
	 */
	public boolean hasCharacter(OWLModel owlModel, OWLIndividual ma){
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		for (Iterator jt = modelCollection.iterator(); jt.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			//根据modelID找到model的实例
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel,modelIDTemp);
			RDFSNamedClass modelClass = getClassFromIndividual(owlModel, modelNameTemp);

			/**@@@@@
			 * 获取实例所在类的所有父类，判断父类是否包含p1:Human类，包含即为人物实例
			 */
			Collection superClasses = new ArrayList();
			superClasses = modelClass.getSuperclasses(true);

			RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
			if(superClasses.contains(humanClass))
				return true;
		}
		return false;
	}

	/**@@@@@@@@@@@@@@@
	 * 获取ma场景中所有人物模型，返回人物模型实例的集合
	 * @param owlModel owl模型
	 * @param ma ma场景
	 * @return 返回人物模型实例的集合
	 */
	public Collection<OWLIndividual> getCharacters(OWLModel owlModel, OWLIndividual ma){
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);

		Collection<OWLIndividual> characters = new ArrayList<OWLIndividual>();
		for (Iterator jt = modelCollection.iterator(); jt.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			//根据modelID找到model的实例
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel,modelIDTemp);
			RDFSNamedClass modelClass = getClassFromIndividual(owlModel, modelNameTemp);

			/**@@@@@
			 * 获取实例所在类的所有父类，判断父类是否包含p1:Human类，包含即为人物实例
			 */
			Collection superClasses = new ArrayList();
			superClasses = modelClass.getSuperclasses(true);

			RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
			if(superClasses.contains(humanClass))
				characters.add(modelIDTemp);
		}
		return characters;
	}

	/**
	 * 判断实例是否为人物模型
	 * @param owlModel owl模型
	 * @param model 模型
	 * @return 返回是否为人物模型
	 */
	public boolean isCharacter(OWLModel owlModel, OWLIndividual model){

		RDFSNamedClass modelClass = getClassFromIndividual(owlModel, model);

		/**@@@@@
		 * 获取实例所在类的所有父类，判断父类是否包含p1:Human类，包含即为人物实例
		 */
		Collection superClasses = new ArrayList();
		superClasses = modelClass.getSuperclasses(true);

		RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
		if(superClasses.contains(humanClass))
			return true;
		else
			return false;
	}

	/**
	 * 获取ma场景中模型数量
	 * @param owlModel owl模型
	 * @param ma ma场景
	 * @return 返回场景中模型数量
	 */
	public int hasModelNum(OWLModel owlModel, OWLIndividual ma){
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		System.out.println("场景中模型数量："+modelCollection.size());
		return modelCollection.size();
	}

	/**
	 * 根据动画的对比性属性筛选配色方案
	 * @param owlModel owl模型
	 * @param contrast 动画的对比性
	 * @param planMore 是否需要多个配色方案，true为2个，false为1个
	 * @param SuiColorMatchIndividuals 符合要求的配色方案集合
	 * @return 经过对比性筛选后，并随机挑选后的配色方案集合(1-2个配色方案)
	 */
	public Collection chooseThoughContrast(OWLModel owlModel, boolean contrast, boolean planMore,
										   Collection<OWLIndividual> SuiColorMatchIndividuals){

		//用于生成随机数

		Collection<OWLIndividual> ColorMatchingIndividuals = new ArrayList<OWLIndividual>();
		ColorMatchingIndividuals.addAll(SuiColorMatchIndividuals);
		System.out.println("符合主题的配色方案数："+ColorMatchingIndividuals.size());
		OWLObjectProperty hasColorProperty = owlModel.getOWLObjectProperty("p5:hasColor");
		if(contrast && SuiColorMatchIndividuals.size()-disContrastPlan.size() > 4)
		{
			SuiColorMatchIndividuals.removeAll(disContrastPlan);
			System.out.println("对比性删除false后的配色方案数："+SuiColorMatchIndividuals.size());
		}
		else if(!contrast && SuiColorMatchIndividuals.size()-contrastPlan.size() > 4)
		{
			SuiColorMatchIndividuals.removeAll(contrastPlan);
			System.out.println("符对比性删除true后的配色方案数："+SuiColorMatchIndividuals.size());
		}
		if(SuiColorMatchIndividuals.size() == 0)
			SuiColorMatchIndividuals = ColorMatchingIndividuals;
		//若没有符合对比性的配色方案，则从符合主题的配色方案中随机挑选
		OWLIndividual[] ColorMatIndividual = (OWLIndividual[]) SuiColorMatchIndividuals.toArray(new OWLIndividual[0]);
		System.out.println("colorMatI-"+ColorMatIndividual);
		int n = SuiColorMatchIndividuals.size();
		int randomNum = random.nextInt(n);
		Collection colorCollection = new ArrayList<OWLIndividual>();
		System.out.println("colorP-"+((Collection) ColorMatIndividual[randomNum].getPropertyValues(hasColorProperty)).size());
		colorCollection.addAll((Collection) ColorMatIndividual[randomNum].getPropertyValues(hasColorProperty));
		if(planMore){
			System.out.println("场景中模型数量大于8，需要两个配色方案");
			if(randomNum < n-1)
			{
				Collection temp = (Collection) ColorMatIndividual[randomNum+1].getPropertyValues(hasColorProperty);
				colorCollection.addAll(temp);
			}
			else if(randomNum > 0)
			{
				Collection temp = (Collection) ColorMatIndividual[randomNum-1].getPropertyValues(hasColorProperty);
				colorCollection.addAll(temp);
			}
		}
		return colorCollection;
	}

	/**
	 * 获得实例所在的类名
	 * @param model
	 * @param individualName
	 * @return OWLNamedClass
	 */
	public RDFSNamedClass getClassFromIndividual(OWLModel model,OWLIndividual individualName)
	{
		String classNameStr=individualName.getRDFType().getBrowserText();
		RDFSNamedClass className = model.getRDFSNamedClass(classNameStr);
		return className;

	}


	/**
	 * 获取modelID的hasModelName实例
	 * @return 返回modelID的hasModelName实例
	 */
	public OWLIndividual getModelNameFromID(OWLModel owlModel, OWLIndividual modelID)
	{
		OWLObjectProperty hasModelNameProperty = owlModel.getOWLObjectProperty("hasModelName");
		OWLIndividual modelName = (OWLIndividual) modelID.getPropertyValue(hasModelNameProperty);
		//不可重用模型没有modelName的实例
		//if(modelName!=null)
		//	 System.out.println("The scene has models: modelID: "+modelID.getBrowserText()+" modelName: "+modelName.getBrowserText());
		//else
		//	 System.out.println("modelID: "+modelID.getBrowserText()+" don't have modelName");
		return modelName;
	}

	public void writeXMLRule(OWLModel owlModel, Collection<OWLIndividual> modelAndColor){
		try{
			String fileName = "test.xml";
			File xmlFile = new File(fileName);
			System.out.println("xml path is " + xmlFile.getAbsolutePath());
			SAXReader reader = new SAXReader();
			Document document = (Document) reader.read(xmlFile);
			Document changedDoc = printRule(document,owlModel ,modelAndColor);
			doc2XmlFile(changedDoc, fileName);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * An instance of the SWRLFactory can be created
	 * by passing an OWL model to its constructor
	 */
	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	/**
	 * 创建rule engine
	 * @throws SWRLRuleEngineException
	 */
	public static SWRLRuleEngine createRuleEngine(OWLModel model)
			throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(
				"SWRLJessBridge", model);
		return ruleEngine;
	}

	/**
	 * 写入要执行的规则名，执行对应规则
	 * @param model Ontology名
	 * @param rulename 待执行的规则名前缀
	 * @return 返回是否成功执行
	 * @throws SWRLRuleEngineException
	 * @throws SWRLFactoryException
	 */
	public static boolean executeSWRLEngine(OWLModel model, String rulename)
			throws SWRLRuleEngineException, SWRLFactoryException{

		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		boolean temp=false;
		Iterator<SWRLImp> iter = factory.getImps().iterator();

		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			if (imp.getLocalName().startsWith(rulename) == true) {
				//SWRLAtomList atomList = imp.getBody();
				temp=true;
				System.out.println("LocalName:"+imp.getLocalName());
				imp.enable();
			}
		}
		ruleEngine.reset();
		ruleEngine.infer();

		System.out.println("************************ infer success!");
		return temp;
	}

	/**@@@@@@@@@@@@@
	 * 打印更改规则（模型颜色和灯光颜色修改规则）
	 * @param doc xml文档
	 * @param model owl模型
	 * @param modelAndColor 包含模型和颜色实例的集合
	 * @return 返回xml文档
	 */
	public Document printRule(Document doc,OWLModel model,Collection<OWLIndividual> modelAndColor)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		OWLIndividual[] modelAndColorIndividual = (OWLIndividual[]) modelAndColor.toArray(new OWLIndividual[0]);
		int num = modelAndColorIndividual.length/2;
		OWLDatatypeProperty hasColorHueProperty = model.getOWLDatatypeProperty("p5:hasColorHue");
		OWLDatatypeProperty hasColorSaturationProperty = model.getOWLDatatypeProperty("p5:hasColorSaturation");
		OWLDatatypeProperty hasColorValueProperty = model.getOWLDatatypeProperty("p5:hasColorValue");
		OWLDatatypeProperty hasLightNodeNameValueProperty = model.getOWLDatatypeProperty("p5:hasLightNodeName");
		for(int i = 0; i < num; i++)
		{
			Element ruleName = name.addElement("rule");
			ruleName.addAttribute("ruleType", "setColor");

			RDFSNamedClass modelClass = getClassFromIndividual(model, modelAndColorIndividual[2*i]);

			if(modelClass.getBrowserText().contains("Light"))
			{
				ruleName.addAttribute("type", "lightColor");
				String lightNodeName = (String) modelAndColorIndividual[2*i].getPropertyValue(hasLightNodeNameValueProperty);
				ruleName.addAttribute("usedModelInMa", lightNodeName);
			}
			else
			{
				ruleName.addAttribute("type", "modelColor");
				String modelID = modelAndColorIndividual[2*i].getBrowserText();
				// int modelIDBegin = modelID.lastIndexOf(":")+1;
				ruleName.addAttribute("usedModelID", modelID);
				//根据modelID找到model的实例
				OWLIndividual modelNameTemp = getModelNameFromID(model,modelAndColorIndividual[2*i]);
				String modelName = modelNameTemp.getBrowserText();
				// int modelNameBegin = modelName.lastIndexOf(":")+1;
				ruleName.addAttribute("usedModelInMa", modelName);
			}
			RDFSNamedClass colorClass = getClassFromIndividual(model, modelAndColorIndividual[2*i+1]);
			String colorName = colorClass.getBrowserText();
			int colorNameBegin = colorName.lastIndexOf("_")+1;
			ruleName.addAttribute("color", colorName.substring(colorNameBegin, colorName.length()));
			Float colorHue = (Float)   modelAndColorIndividual[2*i+1].getPropertyValue(hasColorHueProperty);
			ruleName.addAttribute("hue", Float.toString(colorHue));
			Float colorSaturation = (Float)   modelAndColorIndividual[2*i+1].getPropertyValue(hasColorSaturationProperty);
			ruleName.addAttribute("saturation", Float.toString(colorSaturation));
			Float colorValue = (Float)   modelAndColorIndividual[2*i+1].getPropertyValue(hasColorValueProperty);
			ruleName.addAttribute("value", Float.toString(colorValue));

		}
		return doc;
	}

	/**2013.2.26更新
	 * 打印模型指定颜色规则
	 * @param doc xml文档
	 * @param modelname 模型名
	 * @param colorname 指定颜色
	 * @return 返回xml文档
	 */
	public Document printRule(Document doc,String modelname,String modelid,String colorname,float h,float s,float v)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setTargetModelColor");
		ruleName.addAttribute("usedModelID", modelid);
		ruleName.addAttribute("usedModelInMa", modelname);
		ruleName.addAttribute("color", colorname);
		ruleName.addAttribute("hue", Float.toString(h));
		ruleName.addAttribute("saturation", Float.toString(s));
		ruleName.addAttribute("value", Float.toString(v));
		return doc;
	}



	/**
	 * 打印灯光布局规则
	 * @param doc xml文档
	 * @param model owl模型
	 * @param lightLayout 灯光布局实例
	 * @return 返回xml文档
	 */
	public Document printRule(Document doc,OWLModel model,OWLIndividual lightLayout)
	{
		OWLDatatypeProperty hasLayoutMaProperty = model.getOWLDatatypeProperty("p5:hasLayoutMa");
		String hasLayoutMa = (String) lightLayout.getPropertyValue(hasLayoutMaProperty);
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setColor");
		ruleName.addAttribute("type", "chooseLightLayout");
		ruleName.addAttribute("addModel", hasLayoutMa);
		return doc;
	}

	/**@@@@@@@@@@@@
	 * 打印人物贴图规则
	 * @param doc xml文档
	 * @param model owl模型
	 * @param character 人物模型
	 * @param textureType 贴图类型
	 * @param texturePath 贴图路径
	 * @return 返回xml文档
	 */
	public Document printRule(Document doc,OWLModel model,OWLIndividual characterID, String textureType, String texturePath)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setColor");
		ruleName.addAttribute("type", "modelTexture");

		String modelID = characterID.getBrowserText();
		int modelIDBegin = modelID.lastIndexOf(":")+1;
		ruleName.addAttribute("usedModelID", modelID.substring(modelIDBegin, modelID.length()));
		//根据modelID找到model的实例
		OWLIndividual characterName = getModelNameFromID(model,characterID);
		String modelName = characterName.getBrowserText();
		int modelNameBegin = modelName.lastIndexOf(":")+1;
		ruleName.addAttribute("usedModelInMa", modelName.substring(modelNameBegin, modelName.length()));
		if(textureType.contains("Floor")){
			ruleName.addAttribute("textureType", textureType.substring(3));
		}else
		{
			ruleName.addAttribute("textureType", textureType.substring(3));
		}
		ruleName.addAttribute("texturePath", texturePath);

		return doc;
	}

	/**
	 * doc2XmlFile
	 * 将Document对象保存为一个xml文件到本地
	 * @return true:保存成功  flase:失败
	 * @param filename 保存的文件名
	 * @param document 需要保存的document对象
	 */
	public boolean doc2XmlFile(Document document,String filename)
	{
		boolean flag = true;
		try
		{
			/* 将document中的内容写入文件中 */
			//默认为UTF-8格式，指定为"GB2312"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)),format);
			writer.write(document);
			writer.close();
		}catch(Exception ex)
		{
			flag = false;
			ex.printStackTrace();
		}
		return flag;
	}

	/**
	 * 保存owl文件
	 * @param owlModel
	 */
	public void saveOWLFile(JenaOWLModel owlModel) {
		Collection errors = new ArrayList();
		owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev,errors);
		System.out.println("File saved with " + errors.size() + " errors.");

	}

	/**2013.2.26更新
	 * @param owlModel
	 */
	public Document setTargetModelColor(OWLModel owlModel,String ModelName,String modelid,String color0, Document doc) {
		String Tcolor="p5:color_"+color0;
		OWLNamedClass testcolor = owlModel.getOWLNamedClass(Tcolor);
		Collection color =testcolor.getInstances(true);
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) color.toArray(new OWLIndividual[0]);
		int colorNum = ColorIndividual.length;
		//System.out.println(colorNum);
		OWLDatatypeProperty hasColorHueProperty = owlModel.getOWLDatatypeProperty("p5:hasColorHue");
		OWLDatatypeProperty hasColorSaturationProperty = owlModel.getOWLDatatypeProperty("p5:hasColorSaturation");
		OWLDatatypeProperty hasColorValueProperty = owlModel.getOWLDatatypeProperty("p5:hasColorValue");
		int i=random.nextInt(colorNum);
		float h1,s1,v1;
		h1=(Float) ColorIndividual[i].getPropertyValue(hasColorHueProperty);
		s1=(Float) ColorIndividual[i].getPropertyValue(hasColorSaturationProperty);
		v1=(Float) ColorIndividual[i].getPropertyValue(hasColorValueProperty);
		printRule(doc,ModelName,modelid,color0, h1, s1, v1);
		System.out.println("写入指定模型方案");
		return doc;
	}

	/**2013.6
	 * 根据时间和天气信息设置天空背景
	 * @param owlModel
	 * @param ma ma实例
	 * @param doc XML文档
	 * @param timeandweather
	 * @return 返回修改后的XML文档（未保存）
	 */
	public Document setSkyBackground(OWLModel owlModel, Document doc, ArrayList<String> timeandweather)
	{
		String str="p5:";
		String time=null,weather=null,cloud=null,fog=null;
		float TotalBrightness,SunBrightness,AirDensity=0.5f,DustDensity=0.1f;
		int UseCloud;
		float CloudDensity=0.5f,CloudPower=0.5f;
		float ALIntensity=0.7f,DLIntensity=0.7f,DLX=-65.6f,DLY=70.0f,DLZ=-25.0f;
		float Brightnessratio=1.0f;
		int hasweather=0,hastime=0,hascloud=0,hasfog=0;
		int i,Num;
		OWLDatatypeProperty TotalBrightnessProperty = owlModel.getOWLDatatypeProperty(str+"TotalBrightness");
		OWLDatatypeProperty SunBrightnessProperty = owlModel.getOWLDatatypeProperty(str+"SunBrightness");
		OWLDatatypeProperty AirDensityProperty = owlModel.getOWLDatatypeProperty(str+"AirDensity");
		OWLDatatypeProperty DustDensityProperty = owlModel.getOWLDatatypeProperty(str+"DustDensity");
		OWLDatatypeProperty UseCloudProperty = owlModel.getOWLDatatypeProperty(str+"UseCloudTexture");
		OWLDatatypeProperty CloudDensityProperty = owlModel.getOWLDatatypeProperty(str+"CloudDensity");
		OWLDatatypeProperty CloudPowerProperty = owlModel.getOWLDatatypeProperty(str+"CloudPower");
		OWLDatatypeProperty ALIntensityProperty = owlModel.getOWLDatatypeProperty(str+"AmbientLightIntensity");
		OWLDatatypeProperty DLIntensityProperty = owlModel.getOWLDatatypeProperty(str+"DirectionalLightIntensity");
		OWLDatatypeProperty DLXProperty = owlModel.getOWLDatatypeProperty(str+"DirectionalLightRotateX");
		OWLDatatypeProperty DLYProperty = owlModel.getOWLDatatypeProperty(str+"DirectionalLightRotateY");
		OWLDatatypeProperty DLZProperty = owlModel.getOWLDatatypeProperty(str+"DirectionalLightRotateZ");

		if(timeandweather.get(0)!=null){
			time=timeandweather.get(0);
			hastime=1;
		}
		if(timeandweather.get(1)!=null){
			weather=timeandweather.get(1);
			hasweather=1;
		}
		if(timeandweather.get(2)!=null){
			cloud=timeandweather.get(2);
			hascloud=1;
		}
		if(timeandweather.get(3)!=null){
			fog=timeandweather.get(3);
			hasfog=1;
		}
		System.out.println("0:"+timeandweather.get(0)+" has:"+hastime);
		System.out.println("1:"+timeandweather.get(1)+" has:"+hasweather);
		System.out.println("2:"+timeandweather.get(2)+" has:"+hascloud);
		System.out.println("3:"+timeandweather.get(3)+" has:"+hasfog);
		System.out.println("time:"+time);
		System.out.println("weather:"+weather);

		if(hasweather==1){
			OWLNamedClass weather1 = owlModel.getOWLNamedClass(weather);
			Collection weather2 =weather1.getInstances(true);
			OWLIndividual[] WeatherIndividual = (OWLIndividual[]) weather2.toArray(new OWLIndividual[0]);
			Num = WeatherIndividual.length;
			i=random.nextInt(Num);
			TotalBrightness=(Float) WeatherIndividual[i].getPropertyValue(TotalBrightnessProperty);
			SunBrightness=(Float) WeatherIndividual[i].getPropertyValue(SunBrightnessProperty);
			AirDensity=(Float) WeatherIndividual[i].getPropertyValue(AirDensityProperty);
			DustDensity=(Float) WeatherIndividual[i].getPropertyValue(DustDensityProperty);
			ALIntensity=(Float) WeatherIndividual[i].getPropertyValue(ALIntensityProperty);
			DLIntensity=(Float) WeatherIndividual[i].getPropertyValue(DLIntensityProperty);
			if(weather=="overcast"){
				Brightnessratio=0.75f;
			}
			if(hastime==0){
				printWeatherRule(doc,weather,TotalBrightness,SunBrightness,AirDensity,DustDensity);
			}
		}
		if(hastime==1){
			OWLNamedClass time1 = owlModel.getOWLNamedClass(time);
			Collection time2 =time1.getInstances(true);
			OWLIndividual[] TimeIndividual = (OWLIndividual[]) time2.toArray(new OWLIndividual[0]);
			Num = TimeIndividual.length;
			i=random.nextInt(Num);
			TotalBrightness=(Float) TimeIndividual[i].getPropertyValue(TotalBrightnessProperty)*Brightnessratio;
			SunBrightness=(Float) TimeIndividual[i].getPropertyValue(SunBrightnessProperty)*Brightnessratio;
			ALIntensity=(Float) TimeIndividual[i].getPropertyValue(ALIntensityProperty);
			DLIntensity=(Float) TimeIndividual[i].getPropertyValue(DLIntensityProperty);
			DLX=(Float) TimeIndividual[i].getPropertyValue(DLXProperty);
			DLY=(Float) TimeIndividual[i].getPropertyValue(DLYProperty);
			DLZ=(Float) TimeIndividual[i].getPropertyValue(DLZProperty);
			if(hasweather==0)
				printTimeRule(doc,time,TotalBrightness,SunBrightness,AirDensity,DustDensity);
			else
				printWeatherAndTimeRule(doc,weather,time,TotalBrightness,SunBrightness,AirDensity,DustDensity);
		}
		if(hascloud==1){
			OWLNamedClass cloud1 = owlModel.getOWLNamedClass(cloud);
			Collection cloud2 =cloud1.getInstances(true);
			OWLIndividual[] CloudIndividual = (OWLIndividual[]) cloud2.toArray(new OWLIndividual[0]);
			Num = CloudIndividual.length;
			i=random.nextInt(Num);
			UseCloud=(Integer) CloudIndividual[i].getPropertyValue(UseCloudProperty);
			if(UseCloud!=0){
				CloudDensity=(Float) CloudIndividual[i].getPropertyValue(CloudDensityProperty);
				CloudPower=(Float) CloudIndividual[i].getPropertyValue(CloudPowerProperty);
			}
			printCloudRule(doc,UseCloud,CloudDensity,CloudPower);
		}
			/*
			if(hasfog==1){
				double fogdensity=0.001;
				String fogtype;
				if(fog=="LightFog"){
					fogtype="Fogthin";
				    OWLIndividual fog1 = owlModel.getOWLIndividual("p2:"+fogtype);
					OWLDatatypeProperty FogDensity = owlModel.getOWLDatatypeProperty("p2:fogdensity");
					fogdensity= (Float) fog1.getPropertyValue(FogDensity);
				}
				else if(fog=="HeavyFog"){
					fogtype="Fogmedium";
				    OWLIndividual fog1 = owlModel.getOWLIndividual("p2:"+fogtype);
					OWLDatatypeProperty FogDensity = owlModel.getOWLDatatypeProperty("p2:fogdensity");
					fogdensity= (Float) fog1.getPropertyValue(FogDensity);
				}
				else return doc;
			    float h1,s1,v1;
			    String FOGCOLOR="p2:Fogcolor";
			    OWLNamedClass fog1 = owlModel.getOWLNamedClass(FOGCOLOR);
			    Collection fog2 =fog1.getInstances(true);
			    OWLIndividual[] ColorIndividual = (OWLIndividual[]) fog2.toArray(new OWLIndividual[0]);
			    Num = ColorIndividual.length;
			    i=random.nextInt(Num);
			    OWLDatatypeProperty hasColorH = owlModel.getOWLDatatypeProperty("p2:hascolorH");
			    OWLDatatypeProperty hasColorS = owlModel.getOWLDatatypeProperty("p2:hascolorS");
			    OWLDatatypeProperty hasColorV = owlModel.getOWLDatatypeProperty("p2:hascolorV");
			    h1=(Float) ColorIndividual[i].getPropertyValue(hasColorH);
			    s1=(Float) ColorIndividual[i].getPropertyValue(hasColorS);
			    v1=(Float) ColorIndividual[i].getPropertyValue(hasColorV);
			    //String colorname=ColorIndividual[i].getBrowserText();
			    String colorname=ColorIndividual[i].getLocalName();
			    printFogRule(doc,fogtype,colorname,fogdensity,h1,s1,v1);
			}*/

		printLightRule(doc,time,weather,ALIntensity,DLIntensity,DLX,DLY,DLZ);
		return doc;
	}

	/**2013.6更新
	 * 打印天空背景规划
	 * @param doc xml文档
	 * @return 返回xml文档
	 */
	public Document printWeatherRule(Document doc,String weather,float TotalBrightness,float SunBrightness,float AirDensity,float DustDensity)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Sky");
		ruleName.addAttribute("Weather", weather);
		ruleName.addAttribute("TotalBrightness", Double.toString(TotalBrightness));
		ruleName.addAttribute("SunBrightness",Double.toString(SunBrightness));
		ruleName.addAttribute("AirDensity",Double.toString(AirDensity));
		ruleName.addAttribute("DustDensity",Double.toString(DustDensity));
		return doc;
	}

	public Document printWeatherAndTimeRule(Document doc,String weather,String time,float TotalBrightness,float SunBrightness,float AirDensity,float DustDensity)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Sky");
		ruleName.addAttribute("Weather", weather);
		ruleName.addAttribute("Time", time);
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		ruleName.addAttribute("TotalBrightness", dcmFmt.format(TotalBrightness));
		ruleName.addAttribute("SunBrightness",dcmFmt.format(SunBrightness));
		ruleName.addAttribute("AirDensity",dcmFmt.format(AirDensity));
		ruleName.addAttribute("DustDensity",dcmFmt.format(DustDensity));
		return doc;
	}

	public Document printTimeRule(Document doc,String time,float TotalBrightness,float SunBrightness,float AirDensity,float DustDensity)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Sky");
		ruleName.addAttribute("Time", time);
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		ruleName.addAttribute("TotalBrightness", dcmFmt.format(TotalBrightness));
		ruleName.addAttribute("SunBrightness",dcmFmt.format(SunBrightness));
		ruleName.addAttribute("AirDensity",dcmFmt.format(AirDensity));
		ruleName.addAttribute("DustDensity",dcmFmt.format(DustDensity));
		return doc;
	}

	public Document printCloudRule(Document doc,int UseCloud,float CloudDensity,float CloudPower)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Cloud");
		ruleName.addAttribute("useCloudTexture", Integer.toString(UseCloud));
		ruleName.addAttribute("CloudDensity", Float.toString(CloudDensity));
		ruleName.addAttribute("CloudPower",Float.toString( CloudPower));
		return doc;
	}

	public Document printLightRule(Document doc,String time,String weather,float ALIntensity,float DLIntensity,float DLX,float DLY,float DLZ)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setLight");
		ruleName.addAttribute("type", "OutdoorPlan1");
		if(time!=null)
			ruleName.addAttribute("Time", time);
		if(weather!=null)
			ruleName.addAttribute("Weather", weather);
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		ruleName.addAttribute("AmbientLightIntensity", Float.toString(ALIntensity));
		ruleName.addAttribute("DirectionalLightIntensity", Float.toString(DLIntensity));
		ruleName.addAttribute("DirectionalLightRotateX", dcmFmt.format(DLX));
		ruleName.addAttribute("DirectionalLightRotateY", dcmFmt.format(DLY));
		ruleName.addAttribute("DirectionalLightRotateZ", dcmFmt.format(DLZ));
		return doc;
	}

	public Document printFogRule(Document doc,String fogtype,String colorname,double fogdensity,float r,float g,float b)
	{
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addFogToMa");
		ruleName.addAttribute("type", fogtype);
		DecimalFormat dcmFmt = new DecimalFormat("0.000");
		ruleName.addAttribute("fogDensity", dcmFmt.format(fogdensity));
		ruleName.addAttribute("color", colorname);
		ruleName.addAttribute("colorR", Float.toString(r));
		ruleName.addAttribute("colorG", Float.toString(g));
		ruleName.addAttribute("colorB", Float.toString(b));
		return doc;
	}
}
