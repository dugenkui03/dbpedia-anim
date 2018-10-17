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
		alist.add("GladnessTemplate:gladnessTemplate");//�����ã���������ģ��
		
		File file = new File("E:\\Shock\\adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		System.out.println("��ʼ");
		Expression shock=new Expression();
		Document document1=shock.ShockXml(alist,model,"room2.ma",document);
		XMLWriter writer = new XMLWriter(new FileWriter("E:\\Shock\\testExpression.xml"));
        writer.write(document1);
        System.out.println("����");
        writer.close();
	}
	static Logger logger = Logger.getLogger(Expression.class.getName());
	public static SWRLFactory createSWRLFactory(OWLModel model)
	{
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}
    //��������ִ�������������Ϲ�����İ���
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
				if (imp.getLocalName().contains("addFaceActionToSceneRule")) //�ҵ���ΪaddFaceActionToSceneRule�Ĺ���
				{
					for (Iterator<String> its = topicName.iterator(); its.hasNext();) //�õ���������ִ��addFaceActionToSceneRule����
					{
						String templateValue = its.next();
						String templateValue1=templateValue.replaceAll("Individual", "");
						if(imp.getBody().getBrowserText().contains(templateValue1))//���ҵ���templateValue��ͷ�Ĺ���
						{
							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
							imp.enable();//ִ�д˹�����������г�����ӹ�����Ľ��
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
				if (imp.getLocalName().contains("addFaceActionToSceneRule")) //�ҵ����ְ�����addFaceActionToSceneRule���Ĺ���
				{
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) //�õ���������ִ��addFaceActionToSceneRule����
					{
						String templateValue = its.next();
						String templateValue1="p2:"+templateValue;
						String templateValue2=templateValue1.substring(0, templateValue1.indexOf(":",templateValue1.indexOf(":")+1 ));
						if(imp.getBody().getBrowserText().contains(templateValue2))
						{
							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
							imp.enable();//ִ�д˹���
						}	
					}
				}
			}
		}
		ruleEngine.infer();
	}
	public Document ShockXml(ArrayList<String> list,OWLModel model,String sceneName,Document document) throws DocumentException, IOException, OntologyLoadException, SWRLRuleEngineException{
		if(sceneName.equals("empty.ma")||sceneName.equals("nothing.ma")){ 
			System.out.println("����Ϊ�գ�����ӱ���");
			return document;
			}
		ArrayList<String> humanList= new ArrayList();
		ArrayList<String> modelList=new ArrayList();
		ArrayList<String> actionList=new ArrayList();
		ArrayList<String> action=new ArrayList();
		Element root=(Element) document.getRootElement();
		System.out.println(root.getName()+"----root");//���ڵ�
		Iterator it=root.elementIterator();
		//int x=0;
		while(it.hasNext()){
			Element el=(Element)it.next();
			System.out.println(el.getName()+"----el");//ȡ������¼
			Iterator itt=el.elementIterator();
			
			while(itt.hasNext()){
				Element el1=(Element)itt.next();//ȡ�ü�¼�еĸ��ֶ�
				if(el1.attribute(0).getName().equals("ruleType")&&el1.attribute(1).getName().equals("addModel")){
					if(el1.attributeValue("ruleType").equals("addToMa")&&isHuman(el1.attributeValue("addModel"))){//�ҵ�addtoma���򣬿�add��ma�ǲ�������ģ��
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
		if(!humanList.isEmpty())//���������ģ�ͣ����ɶ�ģ�ͱ��鴦��
		{
			System.out.println(humanList.size());
			for(int i=0;i<humanList.size();i++){//����˼����ˣ��ͶԼ�����������
				if(!actionList.isEmpty())
				{
					for(int j=0;j<actionList.size();j++){//���������б�
						if(actionList.get(j).equals(modelList.get(i))){//��addModelΪ���ϣ����ҵ��������Ӧ�Ķ�����list�е�λ��
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
				document=Shock(list,model,action,humanList.get(i),modelList.get(i),document);//action����Ϊ�գ�������1-2������
				action.clear();
			}
		}
		else System.out.println("û���������ģ�ͣ�����ӱ��飡");
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
	public static int isFacialTopic(OWLModel model,String topic)//�ж�topic�ǲ����������⣨����**Plot��**Topic��
	{
		OWLNamedClass emotionPlot=model.getOWLNamedClass("EmotionRelatedPlot");
		Collection emotionPlotSub=emotionPlot.getSubclasses(true);
		
		//System.out.println("emotionAction���ܹ������ࣺ" + emotionPlotSub.size());
		for (Iterator it = emotionPlotSub.iterator(); it.hasNext();)
		{
			OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
			if(xxsubclass.getBrowserText().equals(topic))
			{
				//System.out.println("Ϊ�ϳɳ�����������");
				return 1;
			}
		}
		OWLNamedClass emotionTopic=model.getOWLNamedClass("EmotionTopic");
		Collection emotionTopicSub=emotionTopic.getSubclasses(true);
		
		//System.out.println("emotionTopic���ܹ������ࣺ" + emotionTopicSub.size());
		
		for (Iterator it = emotionTopicSub.iterator(); it.hasNext();)
		{
			
			OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
			if(xxsubclass.getBrowserText().equals(topic.trim()))
			{
				//System.out.println("Ϊ��ͨ������������");
				return 0;
			}
		}
		
		return -1;
	}
	public String ArrayList2String(ArrayList<String> arrayList) {
        String result = "";
        if (arrayList != null && arrayList.size() > 0) {
            for (String item : arrayList) {
                // ���б��е�ÿ�������ö��ŷָ����Ȼ��ƴ�ӳ��ַ���
                result += item + " ";
            }
            // ȥ�����һ������
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
		
		//�õ��ĸ�������
		//OWLObjectProperty hasTopicProperty=model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty topicForExpression=model.getOWLObjectProperty(str1+"topicForFacialExpress");
		OWLObjectProperty templateForExpression=model.getOWLObjectProperty(str1+"templateForFacialExpress");
		OWLObjectProperty suitableAU=model.getOWLObjectProperty(str1+"hasAU");
		OWLObjectProperty actionSuitableForTopic=model.getOWLObjectProperty(str2+"actionSuitableForTopic");
		OWLObjectProperty similarnext=model.getOWLObjectProperty(str1+"similarnext");
		
		//z����ȡ������Ϣ
		ArrayList<String> topicList= new ArrayList();//���ڴ�������µı���
		ArrayList<String> templateList= new ArrayList();//���ڴ��ģ���µı���
		ArrayList<String>   tempList   =   new   ArrayList();//��ʱ��ű���ʵ��
		ArrayList<String> autempList = new ArrayList();
		char ma=hasma.charAt(2);//�ж�xml��add��ma������Ů
		
		Collection temp = null;//ʵ������
		Collection temp1 = null;//au����
		
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String sceneName=name.attributeValue("name");
		System.out.println("������ȡ��Ϊ:"+sceneName);
		String topic = name.attributeValue("topic");//adl��ȡ������
		if(!topic.isEmpty())
		{
			System.out.println("adlTopic="+topic);
		}
		if(isFacialTopic(model,topic)==-1&&list.isEmpty()&&!action.isEmpty())//�����⣬��ģ�壬�Ӷ����Ƴ�����
		{
			System.out.println("adlδ��ȡ���⣬�Ӷ�����ȡ");
			 OWLNamedClass emotionAction=model.getOWLNamedClass(str2+"EmotionAction");
			 Collection emotionActionSub=emotionAction.getSubclasses(true);
			
			 //System.out.println("emotionAction���ܹ������ࣺ" + emotionActionSub.size());
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
			System.out.println("�������������");
		}
		if(isFacialTopic(model,topic)!=-1)
		{
			source="topic";
			if(isFacialTopic(model,topic)==1)
			{
				if(topic.equals("GladActionPlot")||topic.equals("HappyActionPlot"))
				{
					topic="GladTopic";
					System.out.println("�ϳɳ������⣺"+topic);
				}
				else
				{
					topic=topic.replace("Plot", "Topic");
					System.out.println("�ϳɳ������⣺"+topic);
				}
			}
			System.out.println("�������⴦��---------------");
			//ִ���������
			ArrayList<String>   topicrule  =   new   ArrayList();
		 
		   //executeTopicToBackgroundSceneSWRLEngine(model,topicList);
			
		   System.out.println("��ȡ�������У�");
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
		   OWLIndividual[] expre = (OWLIndividual[]) temp.toArray(new OWLIndividual[0]);//������ʵ��ת��Ϊ����
		   if(expre.length!=0)
		   {
			   System.out.println("�������Ӧ�ı���ʵ���У�");
			   for(int j=0;j<expre.length;j++)
			   {
				   tempList.add(expre[j].getBrowserText());//�����ÿһ������ʵ��
				   System.out.println(tempList.get(j).toString());
			   }
			   String z[]=tempList.get(0).toString().split("_");
			   String subname=z[0];//�����Ӧ������������
			  
			   OWLNamedClass express=model.getOWLNamedClass(str1+"FacilaExpression");
			   Collection expressSub=express.getSubclasses(true);
				
				 //System.out.println("emotionAction���ܹ������ࣺ" + emotionActionSub.size());
				
			   for (Iterator it = expressSub.iterator(); it.hasNext();)
			   {
				   OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
				   //System.out.println(xxsubclass.getBrowserText());
				   if(xxsubclass.getBrowserText().equals(subname))
				   {
					   if (xxsubclass.getSomeValuesFrom(similarnext) == null)//�ñ���û��similarnext��ֻ��������Ӧ��au�н���ѡȡ
					   {
						   break;
					   }
					   else//�ñ�����similar���ҵ�����next����next��Ӧ�ı���ʵ��Ҳ���뵽tempList[]
					   {
						   String exname=xxsubclass.getSomeValuesFrom(similarnext).getBrowserText();
						   OWLNamedClass express1=model.getOWLNamedClass(exname);
						   Collection curex=express1.getInstances(true);
						   for(Iterator itInss = curex.iterator(); itInss.hasNext();)
						   {
							   OWLIndividual indi1 = (OWLIndividual) itInss.next();
							   String exInsName=indi1.getBrowserText();
							   System.out.println(exInsName);//�ҵ�next��Ӧ��ʵ����
							   tempList.add(exInsName);
						   }
					   }
				   }
			   }
			   /////////////////////////////�����߼�
		   }
		   else
			   System.out.println("�����ⲻ�ʺ���ӱ���....");
		   if(!tempList.isEmpty()){
			   Random random=new Random();
			   ArrayList<String>tempList2=new ArrayList();//��ѡ��ı���
			   int s=random.nextInt(3)+2;//���ѡ��ı��������2-4��
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
				   System.out.println("ѡ��ı���ʵ��Ϊ��"+tempList.toString());
				   String[] b=tempList.get(i).split("_");
				   String expressionname=b[0].substring(4);
				   OWLIndividual AuIndividual=model.getOWLIndividual(tempList.get(i));
				   temp1=AuIndividual.getPropertyValues(suitableAU);
				   OWLIndividual[] expre1 = (OWLIndividual[]) temp1.toArray(new OWLIndividual[0]);
				   if(expre1.length!=0)
				   {
					   System.out.println("�ñ����ӦAUΪ��");
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
			   System.out.println("���ݳ�ȡ�����ⲻ�ʺ���ӱ��飡");
			   System.out.println("�������⴦����ϣ�---------------");
			   tempList.clear();
			   topicList.clear();
			   iftopic=true;
		   }
		}
		//////////////////////////�����⣬����ģ��////////////////////////////////////
		else if(list.size()>0){
			source="Template";
			System.out.println("����ģ�崦��---------------"); 
			String templist="";
			String template[]=new String[30];//�洢ģ��ʵ����
			if(!list.isEmpty()){
				System.out.println("���ݽ�����ģ���У�");
				for(int i=0;i<list.size();i++)
				{
					templist=list.get(i).toString();
					int pos = templist.indexOf(":");
					template[i]=(String) templist.subSequence(pos+1, templist.length());
					System.out.println("ģ�壺"+template[i]);
					OWLIndividual TemIndividual=model.getOWLIndividual(template[i]);
					temp=TemIndividual.getPropertyValues(templateForExpression);//��ȡtemplateForExpression��ʵ��
					OWLIndividual[] expre = (OWLIndividual[]) temp.toArray(new OWLIndividual[0]);//������ʵ��ת��Ϊ����
					if(expre.length!=0)
					{
						System.out.println("��ģ���Ӧ�ı���ʵ���У�");
						for(int j=0;j<expre.length;j++)
						{
							tempList.add(expre[j].getBrowserText());//�����ÿһ������ʵ��
						    System.out.println(tempList.get(j).toString());
					    }
						String z[]=tempList.get(0).toString().split("_");
						String subname=z[0];//�����Ӧ������������
						  
						OWLNamedClass express=model.getOWLNamedClass(str1+"FacilaExpression");
						Collection expressSub=express.getSubclasses(true);
						
						for (Iterator it = expressSub.iterator(); it.hasNext();)
					    {
							OWLNamedClass xxsubclass=(OWLNamedClass)it.next();
						    //System.out.println(xxsubclass.getBrowserText());
						    if(xxsubclass.getBrowserText().equals(subname))
						    {
							    if (xxsubclass.getSomeValuesFrom(similarnext) == null)//�ñ���û��similarnext��ֻ��������Ӧ��au�н���ѡȡ
							    {
								    break;
							    }
							    else//�ñ�����similar���ҵ�����next����next��Ӧ�ı���ʵ��Ҳ���뵽tempList[]
							    {
								    String exname=xxsubclass.getSomeValuesFrom(similarnext).getBrowserText();
								    OWLNamedClass express1=model.getOWLNamedClass(exname);
								    Collection curex=express1.getInstances(true);
								    for(Iterator itInss = curex.iterator(); itInss.hasNext();)
								    {
									    OWLIndividual indi1 = (OWLIndividual) itInss.next();
									    String exInsName=indi1.getBrowserText();
									    System.out.println(exInsName);//�ҵ�next��Ӧ��ʵ����
									    tempList.add(exInsName);
								    }
							    }
						    }
						}
					}
					else
						System.out.println("��ģ�岻�ʺ���ӱ���....");
				}
			}
			if(!tempList.isEmpty())
			{
				Random random=new Random();
			    ArrayList<String>tempList2=new ArrayList();//��ѡ��ı���
			    int s=random.nextInt(3)+2;//���ѡ��ı��������2-4��
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
					System.out.println("����ѡ��ı���ʵ��Ϊ��"+tempList.toString());
					String[] b=tempList.get(i).split("_");
					String expressionname=b[0].substring(4);
					OWLIndividual AuIndividual1=model.getOWLIndividual(tempList.get(i));
					temp1=AuIndividual1.getPropertyValues(suitableAU);
					OWLIndividual[] expre1 = (OWLIndividual[]) temp1.toArray(new OWLIndividual[0]);
					if(expre1.length!=0)
					{
						System.out.println("�ñ����ӦAUΪ��");
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
				System.out.println("���ݽ�����ģ�岻�ʺ���ӱ��飡");
			tempList.clear();
			templateList.clear();
			System.out.println("����ģ�崦����ϣ�---------------");
			/*
			if(expression.length()==0)
			{
				System.out.println("û�к��ʵı���...");
				return doc;
			} 
			*/
		}
		else
			System.out.print("�����⣬��ģ�壬�޷�����������\n");
			
		return doc1;
	}
	public static Document printExpressionRule(Document doc,String source,String hasma,String modelId,String expression,String controller){
		System.out.println("��ʼ����xml��rule");
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
		System.out.println("xml��rule�������");
		return doc;
	}

}
