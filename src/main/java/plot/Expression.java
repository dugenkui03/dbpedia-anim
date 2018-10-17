package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;




public class Expression {

	public static void main(String[] args)throws OntologyLoadException, DocumentException, SWRLRuleEngineException, IOException {
		// TODO Auto-generated method stub
		String owlPath = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl" ;
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist =new ArrayList<String>(); 
		alist.add("GladnessTemplate:gladnessTemplate");//测试用，无主题有模板
		
		File file = new File("E:\\Shock\\adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		System.out.println("开始");
		Expression shock=new Expression();
		Document document1=shock.ShockXml(alist,model,"room2.ma",document);
		XMLWriter writer = new XMLWriter(new FileWriter("E:\\Shock\\testExpression.xml"));
        writer.write(document1);
        System.out.println("结束");
        writer.close();
	}
	static Logger logger = Logger.getLogger(Expression.class.getName());
	public static SWRLFactory createSWRLFactory(OWLModel model)
	{
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}
    //创建规则执行器（这个是我瞎胡理解的啊）
	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException
	{
			SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
			return ruleEngine;
	}
	
	public static void executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> topicName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if (topicName.size() != 0) 
			{
				if (imp.getLocalName().contains("addFaceActionToSceneRule")) //找到名为addFaceActionToSceneRule的规则
				{
					for (Iterator<String> its = topicName.iterator(); its.hasNext();) //用迭代器逐条执行addFaceActionToSceneRule规则
					{
						String templateValue = its.next();
						String templateValue1=templateValue.replaceAll("Individual", "");
						if(imp.getBody().getBrowserText().contains(templateValue1))//再找到以templateValue开头的规则
						{
							logger.info("运行的规则名字为："+imp.getLocalName());
							imp.enable();//执行此规则，它会给所有场景添加规则里的结果
						}	
						
					}
				}
			}
		}
		ruleEngine.infer();
	}
	public static void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if (templateName.size() != 0) 
			{
				if (imp.getLocalName().contains("addFaceActionToSceneRule")) //找到名字包含“addFaceActionToSceneRule”的规则
				{
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) //用迭代器逐条执行addFaceActionToSceneRule规则
					{
						String templateValue = its.next();
						String templateValue1="p2:"+templateValue;
						String templateValue2=templateValue1.substring(0, templateValue1.indexOf(":",templateValue1.indexOf(":")+1 ));
						if(imp.getBody().getBrowserText().contains(templateValue2))
						{
							logger.info("运行的规则名字为："+imp.getLocalName());
							imp.enable();//执行此规则
						}	
					}
				}
			}
		}
		ruleEngine.infer();
	}
	public Document ShockXml(ArrayList<String> list,OWLModel model,String sceneName,Document document) throws DocumentException, IOException, OntologyLoadException, SWRLRuleEngineException{
		if(sceneName.equals("empty.ma")||sceneName.equals("nothing.ma")){ 
			System.out.println("场景为空，不添加表情");
			return document;
			}
		ArrayList<String> humanList= new ArrayList();
		ArrayList<String> modelList=new ArrayList();
		ArrayList<String> actionList=new ArrayList();
		ArrayList<String> action=new ArrayList();
		Element root=(Element) document.getRootElement();
		System.out.println(root.getName()+"----root");//根节点
		Iterator it=root.elementIterator();
		//int x=0;
		while(it.hasNext()){
			Element el=(Element)it.next();
			System.out.println(el.getName()+"----el");//取各条记录
			Iterator itt=el.elementIterator();
			
			while(itt.hasNext()){
				Element el1=(Element)itt.next();//取得记录中的各字段
				if(el1.attribute(0).getName().equals("ruleType")&&el1.attribute(1).getName().equals("addModel")){
					if(el1.attributeValue("ruleType").equals("addToMa")&&isHuman(el1.attributeValue("addModel"))){//找到addtoma规则，看add的ma是不是人物模型
						System.out.println(el1.attributeValue("addModel"));
						System.out.println(el1.attributeValue("addModelID"));
						humanList.add(el1.attributeValue("addModel"));
						modelList.add(el1.attributeValue("addModelID"));
					}
				}
				if(el1.attribute(0).getName().equals("ruleType")){
					if(el1.attributeValue("ruleType").equals("addActionToMa")&&el1.attributeValue("type").equals("action")){
						if(!actionList.contains(el1.attributeValue("usedModelID"))){
							actionList.add(el1.attributeValue("usedModelID"));
							
						}
						actionList.add(el1.attributeValue("actionName"));
						
					}
				}
			}
			//System.out.println(actionList);
		}
		if(!humanList.isEmpty())//添加了人物模型，即可对模型表情处理
		{
			System.out.println(humanList.size());
			for(int i=0;i<humanList.size();i++){//添加了几个人，就对几个人做表情
				if(!actionList.isEmpty())
				{
					for(int j=0;j<actionList.size();j++){//遍历动作列表
						if(actionList.get(j).equals(modelList.get(i))){//以addModel为隔断，先找到该人物对应的动作在list中的位置
							int k=j+1;
							while(k<actionList.size())
							{
							if(!actionList.get(k).contains("addModelID")){
								action.add(actionList.get(k));
								
								k++;
							}
							
							else
								break;
							}
							break;
						}
					}
				}
				
				System.out.println(action);
				document=Shock(list,model,action,humanList.get(i),modelList.get(i),document);//action可能为空，可能有1-2个动作
				action.clear();
			}
		}
		else System.out.println("没有添加人物模型，不添加表情！");
    	return document;
	}
	public static boolean isHuman(String temp)
	{
    	
    	if(temp.equals("M_boy.ma")||temp.equals("M_girl.ma"))
		//if(temp.equals("M_girl.ma"))
    		return true;
    	else
    		return false;
    }
	public static int randomNumber(int i){ 
		int p = (int)( Math.random()*i);
		return p;
	}
	public static String changeExpression(String expression){
		int len=expression.length();
		if(expression.charAt(3)=='b')
		{
			return expression.substring(7, len-3);
		}
		if(expression.charAt(3)=='g')
		{
			return expression.substring(8, len-3);
		}
		return null;
	}
	public static int isFacialTopic(OWLModel model,String topic)//判断topic是不是情绪主题（包括**Plot和**Topic）
	{
		OWLNamedClass emotionPlot=model.getOWLNamedClass("EmotionRelatedPlot");
		Collection emotionPlotSub=emotionPlot.getSubclasses(true);
		
		//System.out.println("emotionAction类总共有子类：" + emotionPlotSub.size());
		for (Iterator it = emotionPlotSub.iterator(); it.hasNext();)
		{
			OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
			if(xxsubclass.getBrowserText().equals(topic))
			{
				//System.out.println("为合成场景情绪主题");
				return 1;
			}
		}
		OWLNamedClass emotionTopic=model.getOWLNamedClass("EmotionTopic");
		Collection emotionTopicSub=emotionTopic.getSubclasses(true);
		
		//System.out.println("emotionTopic类总共有子类：" + emotionTopicSub.size());
		
		for (Iterator it = emotionTopicSub.iterator(); it.hasNext();)
		{
			
			OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
			if(xxsubclass.getBrowserText().equals(topic.trim()))
			{
				//System.out.println("为普通场景情绪主题");
				return 0;
			}
		}
		
		return -1;
	}
	public String ArrayList2String(ArrayList<String> arrayList) {
        String result = "";
        if (arrayList != null && arrayList.size() > 0) {
            for (String item : arrayList) {
                // 把列表中的每条数据用逗号分割开来，然后拼接成字符串
                result += item + " ";
            }
            // 去掉最后一个逗号
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
	public Document Shock(ArrayList<String> list,OWLModel model,ArrayList<String> action, String hasma,String modelId,Document doc) throws OntologyLoadException, SWRLRuleEngineException{
		String str1="p14:";
		String str2="p2:";
		String expression="";
		String ex="";
		boolean iftopic=true;
		Document doc1=doc;
		String source="";
		
		//用到的各种属性
		//OWLObjectProperty hasTopicProperty=model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty topicForExpression=model.getOWLObjectProperty(str1+"topicForFacialExpress");
		OWLObjectProperty templateForExpression=model.getOWLObjectProperty(str1+"templateForFacialExpress");
		OWLObjectProperty suitableAU=model.getOWLObjectProperty(str1+"hasAU");
		OWLObjectProperty actionSuitableForTopic=model.getOWLObjectProperty(str2+"actionSuitableForTopic");
		OWLObjectProperty similarnext=model.getOWLObjectProperty(str1+"similarnext");
		
		//z最后抽取到的信息
		ArrayList<String> topicList= new ArrayList();//用于存放主题下的表情
		ArrayList<String> templateList= new ArrayList();//用于存放模板下的表情
		ArrayList<String>   tempList   =   new   ArrayList();//临时存放表情实例
		ArrayList<String> autempList = new ArrayList();
		char ma=hasma.charAt(2);//判断xml中add的ma是男是女
		
		Collection temp = null;//实例缓存
		Collection temp1 = null;//au缓存
		
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String sceneName=name.attributeValue("name");
		System.out.println("场景获取名为:"+sceneName);
		String topic = name.attributeValue("topic");//adl获取的主题
		if(!topic.isEmpty())
		{
			System.out.println("adlTopic="+topic);
		}
		if(isFacialTopic(model,topic)==-1&&list.isEmpty()&&!action.isEmpty())//无主题，无模板，从动作推出主题
		{
			System.out.println("adl未获取主题，从动作获取");
			 OWLNamedClass emotionAction=model.getOWLNamedClass(str2+"EmotionAction");
			 Collection emotionActionSub=emotionAction.getSubclasses(true);
			
			 //System.out.println("emotionAction类总共有子类：" + emotionActionSub.size());
			 for (Iterator it = emotionActionSub.iterator(); it.hasNext();)
			 {
				 OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
				 Collection subclassIndividuals = xxsubclass.getInstances(true);
				 if (subclassIndividuals.size() != 0)
				 {
					 for (Iterator<?> it1 = subclassIndividuals.iterator(); it1.hasNext();)
					 {
						 OWLIndividual xxType_1 = (OWLIndividual) it1.next();
						 if (xxsubclass.getSomeValuesFrom(actionSuitableForTopic) == null)
								continue;
						 for(int i=0;i<action.size();i++)
						 {
							 if(xxType_1.getBrowserText().equals(str2+action.get(i)))
							 {
								 
								 String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(actionSuitableForTopic).getBrowserText();
								 if(result_getSomeValuesFrom!=null)
								 {
									 String hasValues = result_getSomeValuesFrom;
									 if(hasValues.contains("or"))
									 {
										 String[] hasValuesSpilt = hasValues.split("or");
										 topic=hasValuesSpilt[0].toString();
									 }
									 else
										 topic=result_getSomeValuesFrom;
								 }
							 }
						 }
					 }
				 }
			 }
		}
		if(isFacialTopic(model,topic)==-1)
		{
			System.out.println("非情绪相关主题");
		}
		if(isFacialTopic(model,topic)!=-1)
		{
			source="topic";
			if(isFacialTopic(model,topic)==1)
			{
				if(topic.equals("GladActionPlot")||topic.equals("HappyActionPlot"))
				{
					topic="GladTopic";
					System.out.println("合成场景主题："+topic);
				}
				else
				{
					topic=topic.replace("Plot", "Topic");
					System.out.println("合成场景主题："+topic);
				}
			}
			System.out.println("场景主题处理---------------");
			//执行主题规则
			ArrayList<String>   topicrule  =   new   ArrayList();
		 
		   //executeTopicToBackgroundSceneSWRLEngine(model,topicList);
			
		   System.out.println("抽取的主题有：");
		   System.out.println(topic);
		 
		   OWLNamedClass topicClass=model.getOWLNamedClass(topic.trim());
		   Collection curCls = topicClass.getInstances(true);
		   OWLIndividual indi = null;
		   String topicName="";
		   for (Iterator itIns = curCls.iterator(); itIns.hasNext();){
			   indi = (OWLIndividual) itIns.next();
			   topicName=indi.getBrowserText();
			   System.out.println(topicName);
			   if(topicName!=""&&!topicName.isEmpty())
				   break;
		   } 
		   OWLIndividual TopIndividual=model.getOWLIndividual(topicName);
		   temp=TopIndividual.getPropertyValues(topicForExpression);
		   OWLIndividual[] expre = (OWLIndividual[]) temp.toArray(new OWLIndividual[0]);//将表情实例转换为数组
		   if(expre.length!=0)
		   {
			   System.out.println("该主题对应的表情实例有：");
			   for(int j=0;j<expre.length;j++)
			   {
				   tempList.add(expre[j].getBrowserText());//在添加每一个表情实例
				   System.out.println(tempList.get(j).toString());
			   }
			   String z[]=tempList.get(0).toString().split("_");
			   String subname=z[0];//表情对应的情绪类名称
			  
			   OWLNamedClass express=model.getOWLNamedClass(str1+"FacilaExpression");
			   Collection expressSub=express.getSubclasses(true);
				
				 //System.out.println("emotionAction类总共有子类：" + emotionActionSub.size());
				
			   for (Iterator it = expressSub.iterator(); it.hasNext();)
			   {
				   OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
				   //System.out.println(xxsubclass.getBrowserText());
				   if(xxsubclass.getBrowserText().equals(subname))
				   {
					   if (xxsubclass.getSomeValuesFrom(similarnext) == null)//该表情没有similarnext，只需在它对应的au中进行选取
					   {
						   break;
					   }
					   else//该表情有similar，找到他的next，把next对应的表情实例也加入到tempList[]
					   {
						   String exname=xxsubclass.getSomeValuesFrom(similarnext).getBrowserText();
						   OWLNamedClass express1=model.getOWLNamedClass(exname);
						   Collection curex=express1.getInstances(true);
						   for(Iterator itInss = curex.iterator(); itInss.hasNext();)
						   {
							   OWLIndividual indi1 = (OWLIndividual) itInss.next();
							   String exInsName=indi1.getBrowserText();
							   System.out.println(exInsName);//找到next对应的实例名
							   tempList.add(exInsName);
						   }
					   }
				   }
			   }
			   /////////////////////////////新增逻辑
		   }
		   else
			   System.out.println("该主题不适合添加表情....");
		   if(!tempList.isEmpty()){
			   Random random=new Random();
			   ArrayList<String>tempList2=new ArrayList();//新选择的表情
			   int s=random.nextInt(3)+2;//随机选择的表情个数（2-4）
			   if(tempList.size()>4)
			   {
				   int refer=-1;
				   for(int r=0;r<s;r++)
				   {
					   int ss=random.nextInt(tempList.size());
					   while(ss==refer)
					   {
						   ss=random.nextInt(tempList.size());
					   }
					   tempList2.add(tempList.get(ss));
					   refer=ss;
				   }
				   tempList.clear();
				   tempList.addAll(tempList2);
			   }
			   for(int i=0;i<tempList.size();i++){
				   System.out.println("选择的表情实例为："+tempList.toString());
				   String[] b=tempList.get(i).split("_");
				   String expressionname=b[0].substring(4);
				   OWLIndividual AuIndividual=model.getOWLIndividual(tempList.get(i));
				   temp1=AuIndividual.getPropertyValues(suitableAU);
				   OWLIndividual[] expre1 = (OWLIndividual[]) temp1.toArray(new OWLIndividual[0]);
				   if(expre1.length!=0)
				   {
					   System.out.println("该表情对应AU为：");
					   String refer="bem";
						for(int n=0;n<3;n++)
						{
							for(int ii=0;ii<expre1.length;ii++)
							{
								if(expre1[ii].getBrowserText().substring(4).contains("mix"))
								{
									if(expre1[ii].getBrowserText().substring(4).charAt(4)==refer.charAt(n))
									{
										autempList.add(expre1[ii].getBrowserText().substring(4));
									}
								}
								else
								{
									if(expre1[ii].getBrowserText().substring(0).charAt(4)==refer.charAt(n))
									{
										autempList.add(expre1[ii].getBrowserText().substring(4));
									}
								}
							}
						}
				   }
				   System.out.println(ArrayList2String(autempList));
				   doc1 = printExpressionRule(doc,source,hasma,modelId,expressionname,ArrayList2String(autempList));
				   autempList.clear();
			   }
			}
		   else{
			   System.out.println("根据抽取的主题不适合添加表情！");
			   System.out.println("场景主题处理完毕！---------------");
			   tempList.clear();
			   topicList.clear();
			   iftopic=true;
		   }
		}
		//////////////////////////无主题，处理模板////////////////////////////////////
		else if(list.size()>0){
			source="Template";
			System.out.println("场景模板处理---------------"); 
			String templist="";
			String template[]=new String[30];//存储模板实例名
			if(!list.isEmpty()){
				System.out.println("传递进来的模板有：");
				for(int i=0;i<list.size();i++)
				{
					templist=list.get(i).toString();
					int pos = templist.indexOf(":");
					template[i]=(String) templist.subSequence(pos+1, templist.length());
					System.out.println("模板："+template[i]);
					OWLIndividual TemIndividual=model.getOWLIndividual(template[i]);
					temp=TemIndividual.getPropertyValues(templateForExpression);//获取templateForExpression的实例
					OWLIndividual[] expre = (OWLIndividual[]) temp.toArray(new OWLIndividual[0]);//将表情实例转换为数组
					if(expre.length!=0)
					{
						System.out.println("该模板对应的表情实例有：");
						for(int j=0;j<expre.length;j++)
						{
							tempList.add(expre[j].getBrowserText());//在添加每一个表情实例
						    System.out.println(tempList.get(j).toString());
					    }
						String z[]=tempList.get(0).toString().split("_");
						String subname=z[0];//表情对应的情绪类名称
						  
						OWLNamedClass express=model.getOWLNamedClass(str1+"FacilaExpression");
						Collection expressSub=express.getSubclasses(true);
						
						for (Iterator it = expressSub.iterator(); it.hasNext();)
					    {
							OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
						    //System.out.println(xxsubclass.getBrowserText());
						    if(xxsubclass.getBrowserText().equals(subname))
						    {
							    if (xxsubclass.getSomeValuesFrom(similarnext) == null)//该表情没有similarnext，只需在它对应的au中进行选取
							    {
								    break;
							    }
							    else//该表情有similar，找到他的next，把next对应的表情实例也加入到tempList[]
							    {
								    String exname=xxsubclass.getSomeValuesFrom(similarnext).getBrowserText();
								    OWLNamedClass express1=model.getOWLNamedClass(exname);
								    Collection curex=express1.getInstances(true);
								    for(Iterator itInss = curex.iterator(); itInss.hasNext();)
								    {
									    OWLIndividual indi1 = (OWLIndividual) itInss.next();
									    String exInsName=indi1.getBrowserText();
									    System.out.println(exInsName);//找到next对应的实例名
									    tempList.add(exInsName);
								    }
							    }
						    }
						}
					}
					else
						System.out.println("该模板不适合添加表情....");
				}
			}
			if(!tempList.isEmpty())
			{
				Random random=new Random();
			    ArrayList<String>tempList2=new ArrayList();//新选择的表情
			    int s=random.nextInt(3)+2;//随机选择的表情个数（2-4）
			    if(tempList.size()>4)
			    {
				    int refer=-1;
				    for(int r=0;r<s;r++)
				    {
					    int ss=random.nextInt(tempList.size());
					    while(ss==refer)
					    {
						    ss=random.nextInt(tempList.size());
					    }
					    tempList2.add(tempList.get(ss));
					    refer=ss;
				    }
				    tempList.clear();
				    tempList.addAll(tempList2);
			    }
				for(int i=0;i<tempList.size();i++)
				{
					System.out.println("最终选择的表情实例为："+tempList.toString());
					String[] b=tempList.get(i).split("_");
					String expressionname=b[0].substring(4);
					OWLIndividual AuIndividual1=model.getOWLIndividual(tempList.get(i));
					temp1=AuIndividual1.getPropertyValues(suitableAU);
					OWLIndividual[] expre1 = (OWLIndividual[]) temp1.toArray(new OWLIndividual[0]);
					if(expre1.length!=0)
					{
						System.out.println("该表情对应AU为：");
						String refer="bem";
						for(int n=0;n<3;n++)
						{
							for(int ii=0;ii<expre1.length;ii++)
							{
								if(expre1[ii].getBrowserText().substring(4).contains("mix"))
								{
									if(expre1[ii].getBrowserText().substring(4).charAt(4)==refer.charAt(n))
									{
										autempList.add(expre1[ii].getBrowserText().substring(4));
									}
								}
								else
								{
									if(expre1[ii].getBrowserText().substring(0).charAt(4)==refer.charAt(n))
									{
										autempList.add(expre1[ii].getBrowserText().substring(4));
									}
								}
							}
						}
					}
					System.out.println(ArrayList2String(autempList));
					doc1 = printExpressionRule(doc,source,hasma,modelId,expressionname,ArrayList2String(autempList));
					autempList.clear();
				}
			}
			else
				System.out.println("根据进来的模板不适合添加表情！");
			tempList.clear();
			templateList.clear();
			System.out.println("场景模板处理完毕！---------------");
			/*
			if(expression.length()==0)
			{
				System.out.println("没有合适的表情...");
				return doc;
			} 
			*/
		}
		else
			System.out.print("无主题，无模板，无法添加人物表情\n");
			
		return doc1;
	}
	public static Document printExpressionRule(Document doc,String source,String hasma,String modelId,String expression,String controller){
		System.out.println("开始生成xml—rule");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addExpressionToMa");
		ruleName.addAttribute("type", "FacialExpression");
		ruleName.addAttribute("usedModelInMa",hasma);
		ruleName.addAttribute("usedModelID",modelId);
		ruleName.addAttribute("source", source);
		ruleName.addAttribute("expression", expression);
		ruleName.addAttribute("controller", controller);
		System.out.println("xml—rule生成完毕");
		return doc;
	}

}
