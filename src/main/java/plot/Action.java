package plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;


import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;


public class Action{

	private boolean actionFlag=false;

	public boolean isActionFlag() {
		return actionFlag;
	}

	public void setActionFlag(boolean actionFlag) {
		this.actionFlag = actionFlag;
	}

	@SuppressWarnings("deprecation")
	public static  Document actionInfer(ArrayList<String> list, OWLModel model,String maName,Document doc) throws OntologyLoadException
	{

		String   str1="p2:";
		/*
		 * ��ȡdoc�ĸ��ڵ�
		 */
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");

		System.out.println("adlTopic="+adlTopic);
		/*
		 * ��ȡmaʵ��
		 */
		OWLIndividual maIndividual=model.getOWLIndividual(maName);
		/*
		 * �õ��ĸ�������
		 */
		System.out.println("model="+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty  isDeal=model.getOWLDatatypeProperty("isUsed");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		int totalActionNum = 0;

		String[] actionList1 = new String[200]; // �����洢����
		int actFromTemNumber = 0;
		/*
		 * ��ȡtopic
		 */
		ArrayList topicNameList = new ArrayList();
		String topicName="";
		if (adlTopic != "") {
			topicNameList.add(adlTopic);
		}
	/*	else {

			//System.out.println("topic����"+topicNameProperty.toString());
			Collection hasTopicValues = maIndividual.getPropertyValues(topicNameProperty);
			if (hasTopicValues.isEmpty()) {
				System.out.println("topicΪ��");
			}
			else {
				// topicName=maIndividual.getPropertyValue(topicNameProperty).toString();

				for (Iterator it = hasTopicValues.iterator(); it.hasNext();) {
					topicNameList.add(it.next().toString());
				}
			}
		}*/
		/*
		 * ���ȴ���topic��Ϣ,��ȡ�����з��ϴ�topic�Ķ�������
		 */

		ArrayList<String> actionList = new ArrayList<String>();
		OWLNamedClass actionClass = model.getOWLNamedClass(str1 + "Action");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
			topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);

			Collection ActionSubClass = actionClass.getSubclasses(true);
			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for (Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
					continue;
				}
				String hasTopicClassType = (subclass.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
				// System.out.println("####"+hasTopicClassType);
				if (hasTopicClassType.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
					OWLUnionClass hasTopicUnion = (OWLUnionClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					// ��ȡ������������
					// getNamedOperands is OWLNAryLogicalClass's method: get all
					// operands which are named classes in the union formula
					Collection hasTopic_collection = hasTopicUnion
							.getNamedOperands();
					for (Iterator jm = hasTopic_collection.iterator(); jm
							.hasNext();) {
						OWLNamedClass hasTopicClass = (OWLNamedClass) jm.next();
						// �ж��������Ƿ���ͬ
						// equalsStructurally is RDFObject's method: Determines
						// whether or not the specified class is structurally
						// equal to this class.
						if (hasTopicClass.equalsStructurally(topic)) {
							subclassIndiviual = subclass.getInstances(true);
							for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
								OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
								String actionName = actionIndiviual.getBrowserText();
								actionList.add(actionName);
							}
							// ClassName = ColorImpSub2.getBrowserText();
							// System.out.println("Topic "+topic.getBrowserText()+" suitable color implication "
							// + subclass);
							continue;
						}
					}
				} else {
					OWLNamedClass classname = (OWLNamedClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					System.out.println(classname.getBrowserText());
					if (classname == null)
						continue;
					if (classname.equalsStructurally(topic)) {
						subclassIndiviual = subclass.getInstances(true);
						for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
							OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
							String actionName = actionIndiviual.getBrowserText();
							actionList.add(actionName);
						}
						continue;
					}
				}
			}
		}
		//  if(actFromTemNumber==0&&actionList.size()!=0)
		if (actionList.size() != 0) {
			System.out.println("���������ȡ��" + actionList.size() + "������");
			actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
			totalActionNum = actionList.size();
		}
		else
		{
			/*
			 * ������ģ����Ϣ
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str1 + "mapToAction");
			int listSize = list.size();
			System.out.print(listSize + "\n");
			String[] listToStr = new String[listSize]; // ��arraylistת��Ϊstring[]
			String[] templateList = new String[listSize]; // ������template��Ϣ

			if (listSize != 0) {
				listToStr = (String[]) list.toArray(new String[listSize]);// ������(String[])
				for (int i = 0; i < listToStr.length; i++) {
					String str2 = listToStr[i];
					int pos = str2.indexOf(":");
					templateList[i] = (String) str2.subSequence(pos + 1,str2.length());
					System.out.println("templateList[" + i + "]="+ templateList[i]);
				}
			}
			if (templateList.length > 0) {
				for (int i = 0; i < templateList.length; i++) {
					OWLIndividual actTemIndividual = model.getOWLIndividual(templateList[i]);
					System.out.println("tempalteList[" + i + "]="
							+ templateList[i]);
					Collection mapToActionValues = actTemIndividual
							.getPropertyValues(mapToActionProperty);
					if (!mapToActionValues.isEmpty()) {
						for (Iterator it1 = mapToActionValues.iterator(); it1
								.hasNext();)

						{
							OWLIndividual actionIndiviual = (OWLIndividual) it1
									.next();
							actionList1[actFromTemNumber] = actionIndiviual
									.getBrowserText();
							System.out.println(actionList1[actFromTemNumber]);
							actFromTemNumber++;
						}
					}
				}
			}
			if (actFromTemNumber != 0) {
				totalActionNum = actFromTemNumber;
				System.out.println("û�����⣬����ԭ�ӹ���ȡ��" + actFromTemNumber + "������");
			}
		}


		//�ڲ�����������������£�����Ӷ���
		if(actionList.size()==0&&actFromTemNumber==0)
		{
			System.out.println("������ԭ��û�г�ȡ�����ʵĶ���,����ģ�ͣ�������Ӷ���");
			//û�г������������Ӷ���
			int  actionNum = 0;
			//String[]actionTemp=new  String[100];
			ArrayList actionlist=new ArrayList();
			actionlist.add("WalkAction");
			actionlist.add("RunAction");
			actionlist.add("WaitAction");
			for(int i=0;i<actionlist.size();i++)
			{
				String s=(String) actionlist.get(i);
				OWLNamedClass RandomClass = model.getOWLNamedClass(str1 + s);
				Collection actionAllIndividuals = RandomClass.getInstances(true);
				for(Iterator it=actionAllIndividuals.iterator();it.hasNext();)
				{
					OWLIndividual   actionIndiviual=(OWLIndividual)it.next();
					String actionName=actionIndiviual.getBrowserText();
					actionList1[actionNum] = actionName ;
					//System.out.println(actionTemp[actionNum]);
					actionNum++;
				}
				totalActionNum = actionNum;
			}

		}

		//Ϊ��ӵ�������Ӷ���

		OWLNamedClass AddModelRelatedClass =model.getOWLNamedClass("AddModelRelated");
		Collection    AllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		for(Iterator it=AllAddPeopleIndividuals.iterator();it.hasNext();)
		{  //20170506
			OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
			String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
			int pos = PeopleNameInOwl.indexOf(":");
			String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
			if(!PeopleName.equals("M_bikeboy.ma")&&addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people"))
			{	String isCheck=addModelIndiviual.getPropertyValue(isDeal).toString();
				if(isCheck.equals("false")){
					String  PeopleID=addModelIndiviual.getPropertyValue(modelIDProperty).toString();
					//System.out.println(PeopleID);
					PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
					pos = PeopleNameInOwl.indexOf(":");
					PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
					//System.out.println(PeopleName);
					int rd=(int)(Math.random()*totalActionNum);//����������������жϼ��ĸ�����

					int pos1 = actionList1[rd].indexOf(":");
					String finalActionName =(String)actionList1[rd].subSequence(pos1+1, actionList1[rd].length());
					System.out.println("��Ӷ������ƣ�"+finalActionName);




					//2014.6.18�޸�--------------
					OWLIndividual finalAddactionindivadual=model.getOWLIndividual(actionList1[rd]);

					//TODO 2016.10.16�޸�
					String i=new Random().nextInt(2)+"";
					OWLDatatypeProperty relateActionProperty=model.getOWLDatatypeProperty(str1+"relateAction");
					String relateAction="";
					System.out.println("XXXXXXX����"+finalAddactionindivadual.getPropertyValue(relateActionProperty));
					if(finalAddactionindivadual.getPropertyValue(relateActionProperty)!=null){
						relateAction=finalAddactionindivadual.getPropertyValue(relateActionProperty).toString() ;
					}


					OWLDatatypeProperty ifAddConstraintProperty=model.getOWLDatatypeProperty(str1+"ifAddConstraint");
					String  ifAddConstraint="0";
					System.out.println("����"+finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty));
					if(finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty)!=null){
						ifAddConstraint=finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty).toString() ;
					}

					if(ifAddConstraint.equals("1")){
						OWLDatatypeProperty constraintTypeProperty=model.getOWLDatatypeProperty(str1+"actionConstraintType");
						String constraintType;
						if (finalAddactionindivadual.getPropertyValue(constraintTypeProperty)!=null) {
							constraintType = finalAddactionindivadual.getPropertyValue(constraintTypeProperty).toString();
						} else {
							constraintType = "default";
						}

						OWLObjectProperty relativeModelProperty=model.getOWLObjectProperty(str1+"modelSuitableForAction");
						OWLIndividual relativeModelIndividual=null;
						String relativeModel;
						if (finalAddactionindivadual.getPropertyValue(relativeModelProperty)!=null) {
							relativeModelIndividual = (OWLIndividual) finalAddactionindivadual.getPropertyValue(relativeModelProperty);
							relativeModel=relativeModelIndividual.getBrowserText();
						} else {
							relativeModel = "default";
						}

						OWLDatatypeProperty modelRelativePositionProperty=model.getOWLDatatypeProperty(str1+"actionRelativeModelPosition");
						String relativeModelPosition;
						if (finalAddactionindivadual.getPropertyValue(modelRelativePositionProperty)!=null) {
							relativeModelPosition = finalAddactionindivadual.getPropertyValue(modelRelativePositionProperty).toString();
						} else {
							relativeModelPosition = "default";
						}

						ifAddConstraint="ture";

						Element ruleName = name.addElement("rule");
						ruleName.addAttribute("ruleType","addActionToMa");// type="model"
						ruleName.addAttribute("type","action");
						ruleName.addAttribute("usedModelID",PeopleID);
						ruleName.addAttribute("usedModelInMa",PeopleName);
						ruleName.addAttribute("actionName", finalActionName);
						ruleName.addAttribute("ifAddConstraint", ifAddConstraint);
						ruleName.addAttribute("actionConstraintType", constraintType);
						ruleName.addAttribute("relativeModelMa", relativeModel);
						ruleName.addAttribute("relativeModelPosition", relativeModelPosition);
						//TODO  2016.10.16�޸�

						if(!"".equals(relateAction)&&relateAction!=null&&i.equals("1")){
							String[] split = relateAction.split("_");
							System.out.println("split.length="+split.length);
							int random=new Random().nextInt(split.length);
							String randomRelateAction=split[random];
							ruleName.addAttribute("relateAction", randomRelateAction);
						}else{

							ruleName.addAttribute("relateAction", "null");
						}


					}else{
						//
						//дdoc
						ifAddConstraint="false";
						Element ruleName = name.addElement("rule");
						ruleName.addAttribute("ruleType","addActionToMa");// type="model"
						ruleName.addAttribute("type","action");
						ruleName.addAttribute("usedModelID",PeopleID);
						ruleName.addAttribute("usedModelInMa",PeopleName);
						ruleName.addAttribute("actionName", finalActionName);
						ruleName.addAttribute("ifAddConstraint", ifAddConstraint);


						//TODO  2016.10.16�޸�
						if(!"".equals(relateAction)&&relateAction!=null&&i.equals("1")){
							String[] split = relateAction.split("_");
							System.out.println("split.length="+split.length);
							int random=new Random().nextInt(split.length);
							String randomRelateAction=split[random];
							ruleName.addAttribute("relateAction", randomRelateAction);
						}else{

							ruleName.addAttribute("relateAction", "null");
						}
//				  if(finalActionName.equals("ElatedWalk142_06")){
//					  ruleName.addAttribute("PathConstraint", "round");
//				  }
					}
					//2014.6.18hll---------------------

				}
			}else{
				System.out.println("û����");
			}
		}

		return  doc;

	}

	/**
	 * �ж�����Ƿ���붯��
	 * @return void
	 * @param list ���ݹ�����ģ����Ϣ model ���ݵ�Ontology maName ѡ��ĳ�������
	 * */

	public void  actionInfer(ArrayList<String> list, OWLModel model,String adlTopic) throws OntologyLoadException {
		String   str1="p2:";

		System.out.println("adlTopic"+adlTopic);
		/*
		 * ��ȡmaʵ��
		 */
		//OWLIndividual maIndividual=model.getOWLIndividual(maName);
		/*
		 * �õ��ĸ�������
		 */
		//System.out.println("model"+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		//System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		int totalActionNum = 0;

		String[] actionList1 = new String[200]; // �����洢����
		int actFromTemNumber = 0;
		/*
		 * ��ȡtopic
		 */
		ArrayList topicNameList = new ArrayList();
		String topicName="";
		if(adlTopic!=""){
			topicNameList.add(adlTopic);
		}else{

			System.out.println("topicΪ��");

		}
		/*
		 * ���ȴ���topic��Ϣ,��ȡ�����з��ϴ�topic�Ķ�������
		 */
		ArrayList<String> actionList = new ArrayList<String>();
		OWLNamedClass actionClass = model.getOWLNamedClass(str1 + "Action");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
			topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);

			Collection ActionSubClass = actionClass.getSubclasses(true);
			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for (Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
					continue;
				}
				String hasTopicClassType = (subclass
						.getSomeValuesFrom(actionSuitableForTopicProperty)
						.getClass()).getName();
				// System.out.println("####"+hasTopicClassType);
				if (hasTopicClassType.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
					OWLUnionClass hasTopicUnion = (OWLUnionClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					// ��ȡ������������
					// getNamedOperands is OWLNAryLogicalClass's method: get all
					// operands which are named classes in the union formula
					Collection hasTopic_collection = hasTopicUnion
							.getNamedOperands();
					for (Iterator jm = hasTopic_collection.iterator(); jm
							.hasNext();) {
						OWLNamedClass hasTopicClass = (OWLNamedClass) jm.next();
						// �ж��������Ƿ���ͬ
						// equalsStructurally is RDFObject's method: Determines
						// whether or not the specified class is structurally
						// equal to this class.
						if (hasTopicClass.equalsStructurally(topic)) {
							subclassIndiviual = subclass.getInstances(true);
							for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
								OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
								String actionName = actionIndiviual.getBrowserText();
								actionList.add(actionName);
							}
							// ClassName = ColorImpSub2.getBrowserText();
							// System.out.println("Topic "+topic.getBrowserText()+" suitable color implication "
							// + subclass);
							continue;
						}
					}
				} else {
					System.out.println(subclass.getBrowserText());
					OWLNamedClass classname = (OWLNamedClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					System.out.println(classname.getBrowserText());
					// System.out.println(classname.getBrowserText());
					if (classname == null)
						continue;
					if (classname.equalsStructurally(topic)) {
						subclassIndiviual = subclass.getInstances(true);
						for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
							OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
							String actionName = actionIndiviual.getBrowserText();
							actionList.add(actionName);
						}
						continue;
					}
				}
			}
		}
		//  if(actFromTemNumber==0&&actionList.size()!=0)
		if (actionList.size() != 0) {
			System.out.println("���������ȡ��" + actionList.size() + "������");
			actionList1 = (String[]) actionList.toArray(new String[actionList
					.size()]);
			totalActionNum = actionList.size();

		} else {
			/*
			 * ������ģ����Ϣ
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str1 + "mapToAction");
			int listSize = list.size();
			System.out.print(listSize + "\n");
			String[] listToStr = new String[listSize]; // ��arraylistת��Ϊstring[]
			String[] templateList = new String[listSize]; // ������template��Ϣ

			if (listSize != 0) {
				listToStr = (String[]) list.toArray(new String[listSize]);// ������(String[])
				for (int i = 0; i < listToStr.length; i++) {
					String str2 = listToStr[i];
					int pos = str2.indexOf(":");
					templateList[i] = (String) str2.subSequence(pos + 1,str2.length());

					System.out.println("templateList[" + i + "]="+ templateList[i]);
				}
			}
			if (templateList.length > 0) {
				for (int i = 0; i < templateList.length; i++) {
					OWLIndividual actTemIndividual = model.getOWLIndividual(templateList[i]);
					System.out.println("tempalteList[" + i + "]"
							+ templateList[i]);
					Collection mapToActionValues = actTemIndividual
							.getPropertyValues(mapToActionProperty);
					if (!mapToActionValues.isEmpty()) {
						for (Iterator it1 = mapToActionValues.iterator(); it1
								.hasNext();)

						{
							OWLIndividual actionIndiviual = (OWLIndividual) it1
									.next();
							actionList1[actFromTemNumber] = actionIndiviual
									.getBrowserText();
							System.out.println(actionList1[actFromTemNumber]);
							actFromTemNumber++;
						}
					}
				}
			}
			if (actFromTemNumber != 0) {
				totalActionNum = actFromTemNumber;
				System.out.println("û�����⣬����ԭ�ӹ���ȡ��" + actFromTemNumber + "������");
			}
		}
		if(totalActionNum!=0)
			setActionFlag(true);

	}

	public Document actionInfer1(ArrayList<String> list, OWLModel model,String maName,Document doc)
	{

		return doc;
	}

	public static void main(String[] args) throws OntologyLoadException {
		// TODO Auto-generated method stub

		String xmlPath ="PlotDataOut/adl_result.xml";

		Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);//���Ҫ�����XML�ļ���ͷ��
//		try{
		System.out.println("qiu begin");
		String urlq="file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		OWLModel owlModel =ProtegeOWL.createJenaOWLModelFromURI(urlq);
		Action action=new Action();
		ArrayList<String> actionTemplate=new ArrayList<String>();
		actionTemplate.add("WearTemplate:WearTemplate1");

		System.out.println("���ݵ�ʵ��"+actionTemplate);
		doc=new Action().actionInfer(actionTemplate,owlModel, "danceFire.ma", doc);
		XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
		System.out.println("qiu finish");
//			}catch(Exception exQiu)
//			{
//				System.out.println("ERROR: Qiu Exception");
//			}				
	}


}
	