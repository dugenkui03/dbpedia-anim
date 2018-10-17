package plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

public class InterAction {


	public static void main(String[] args) throws OntologyLoadException {

		String xmlPath ="PlotDataOut/adl_resultInteraction.xml";

		Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);//���Ҫ�����XML�ļ���ͷ��
		System.out.println("interaction begin");
		//����
		String urlq="file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		OWLModel model =ProtegeOWL.createJenaOWLModelFromURI(urlq);
		//���ԣ����ϵ��ܿ����Ҫ�޸�    interModel:ר����InterAction����������
		String url="file:///C:/InterAction/InterAction.owl";
		OWLModel interModel =ProtegeOWL.createJenaOWLModelFromURI(url);
		ArrayList<String> actionTemplate=new ArrayList<String>();
		ArrayList<String> topic=new ArrayList<String>();
		topic.add("������");
		topic.add("������");
		topic.add("��");
		topic.add("ϲ��");
		ArrayList<String> topiclist=new ArrayList();
		OWLNamedClass topicn=model.getOWLNamedClass("Topic");
		OWLDatatypeProperty chineseTopic=model.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls=null;
		Collection clo=topicn.getSubclasses(true);
		for(int i=0;i<topic.size();i++)
		{

			for(Iterator in=clo.iterator();in.hasNext();)
			{
				cls=(OWLNamedClass) in.next();
				Object hasvali=cls.getHasValue(chineseTopic);
				if(hasvali!=null && topic.get(i).equals(hasvali.toString()))
				{
					topiclist.add(cls.getBrowserText().toString());
				}
			}
		}
		System.out.println("topiclist="+topiclist);


//			       doc=new InterAction().InterActionInfer(actionTemplate,model, interModel,"Tropical45.ma", doc);
		String aaa=new InterAction().hasInterActionInfer(actionTemplate,model,"Tropical45.ma", doc);
		XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
		System.out.println("interaction finish");
	}
	/**
	 * �����򣺸������⡢����ma����ģ�����������
	 * @param list	ģ�弯��
	 * @param model	ontology����
	 * @param interModel	����������interaction
	 * @param maName	��������
	 * @param doc	adl.xml
	 * @return
	 * @throws OntologyLoadException
	 */

	public  Document InterActionInfer(ArrayList<String> list, OWLModel model,String maName,
									  Document doc) throws OntologyLoadException {
		String   str1="p2:";
		//��ʱ
		//String   str1="p3:";

		/*
		 * ��ȡdoc�ĸ��ڵ�
		 */
		Element rootName = doc.getRootElement();
		//���ڻ�ȡ�ӽڵ�
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println(adlTopic);
		/*
		 * ��ȡmaʵ��
		 */
		OWLIndividual maIndividual=model.getOWLIndividual(maName);

		/*
		 * �õ��ĸ�������
		 */
		System.out.println("model"+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		OWLDatatypeProperty  isDeal=model.getOWLDatatypeProperty("isUsed");
		OWLDatatypeProperty  maFrameNumber=model.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty  relateActionProperty=model.getOWLDatatypeProperty(str1+"relateAction");
		OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");
		int totalActionNum = 0;

		String[] actionList1 = new String[200]; // �����洢����
		int actFromTemNumber = 0;
		/*
		 * ��ȡtopic
		 */
		ArrayList<String> topicNameList = new ArrayList<String>();
		String topicName="";
		if (adlTopic != "") {
			topicNameList.add(adlTopic);
		}
		else {

			Collection hasTopicValues = maIndividual.getPropertyValues(topicNameProperty);
			if (hasTopicValues.isEmpty()) {
				System.out.println("topicΪ��");
			}
			else {
				for (Iterator it = hasTopicValues.iterator(); it.hasNext();) {
					topicNameList.add(it.next().toString());
				}
			}
		}



		/*
		 * ���ȴ���topic��Ϣ,��ȡ�����з��ϴ�topic�Ķ�������
		 */
		String   str11="p12:";
		ArrayList<String> actionList = new ArrayList<String>();
		//*
		OWLNamedClass actionClass = model.getOWLNamedClass(str11+"ExcuteAction");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
			topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);

			Collection ActionSubClass = actionClass.getSubclasses(true);//.getSubclasses(true)

			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for(Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
					continue;
				}
				String hasTopicClassType = (subclass.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
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
		if (actionList.size() != 0) {
			System.out.println("���������ȡ��" + actionList.size() + "������");
			actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
			totalActionNum = actionList.size();
		}
		//������û�г�ȡ������������ģ����Ϣ��ȡ����

		else
		{
			/*
			 * ������ģ����Ϣ
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str11 + "mapToInterAction");
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
		//�ڲ���������������������£�����������������ת��ִ�е��˳���
		if(actionList.size()==0&&actFromTemNumber==0){
			//TODO
			System.out.println("������ԭ��û�г�ȡ�����ʵĶ���,����ģ��,ִ�е��˶���");
			return null;
		}




		//Ϊ��ӵ�������Ӷ���
		//��ӵ������д�뵽֪ʶ���е�AddModelRelated��
		//���ԡ�����AddModelRelated�´���ʵ��addModelID1������������ֵisUsed=true

		OWLNamedClass AddModelRelatedClass =model.getOWLNamedClass("AddModelRelated");
			 /*
		OWLIndividual addIndividual = AddModelRelatedClass.createOWLIndividual("addModelID1");
		addIndividual.setPropertyValue(addModelTypeProperty, "people");
		addIndividual.setPropertyValue(modelIDProperty, "1");
		addIndividual.setPropertyValue(isDeal, "false");
		addIndividual.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");

		OWLIndividual addIndividual1 = AddModelRelatedClass.createOWLIndividual("addModelID2");
		addIndividual1.setPropertyValue(addModelTypeProperty, "people");
		addIndividual1.setPropertyValue(modelIDProperty, "2");
		addIndividual1.setPropertyValue(isDeal, "false");
		addIndividual1.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");
//		addIndividual.setPropertyValue(hasModelNameProperty, "boy");
		 */





		Collection  oldAllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);

		//������ģ������ѡ��������ͬ���ÿռ������ʵ��ģ��,�����ڣ������ÿռ������Լ�����ʵ�����д洢��Ȼ�󽫴洢������ʵ����������
		String tempSpaceName="";
		List<String> listSpace=new ArrayList<String>();
		List<String> newListSpace=new ArrayList<String>();
		List<OWLIndividual> individualSpaceList=new ArrayList<OWLIndividual>();

		List<List<OWLIndividual>> newIndividualSpaceList=new ArrayList<List<OWLIndividual>>();
		//��û�д���������Լ��������ڵĿ��ÿռ�����
		for(Iterator it=oldAllAddPeopleIndividuals.iterator();it.hasNext();){

			OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
			String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
			int pos = PeopleNameInOwl.indexOf(":");
			String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
			if(!PeopleName.equals("M_bikeboy.ma")&&addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false")){
				OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
//				String tempSpaceName= IndiviualOfSpace.getBrowserText();
				tempSpaceName=addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty).toString();
				listSpace.add(tempSpaceName);
				individualSpaceList.add(addModelIndiviual);
			}

		}
		if(individualSpaceList.size()<2){
			return null;
		}

		for(int i=0;i<listSpace.size();i++){
			for(int j=i+1;j<listSpace.size();j++){
				if(listSpace.get(i).equals(listSpace.get(j))){
					List<OWLIndividual> newIndividualSpaceListTemp=new ArrayList<OWLIndividual>();
//					if(newListSpace.size()==0){
					newListSpace.add(listSpace.get(i));
					newIndividualSpaceListTemp.add(individualSpaceList.get(i));
//					}
					newListSpace.add(listSpace.get(j));
					newIndividualSpaceListTemp.add(individualSpaceList.get(j));
					newIndividualSpaceList.add(newIndividualSpaceListTemp);
				}
				//20170530
				if(newListSpace.size()>=2){
					break;
				}
			}
			//�����һ�齻������

			if(newIndividualSpaceList.size()>=1){
				break;
			}

		}

		/*
		if(newListSpace.size()<2){
			//TODO
			return null;
		}
		*/
		Collection<OWLIndividual> AllAddPeopleIndividuals=new ArrayList<OWLIndividual>();
		for(int d=0;d<newIndividualSpaceList.size();d++){
			List<OWLIndividual> temp=newIndividualSpaceList.get(d);
			for(int f=0;f<temp.size();f++){

				AllAddPeopleIndividuals.add(temp.get(f));
			}
			int i=0;
			String otherInterAction="";
			List<String> preContainsList=new ArrayList<String>();
			int flag=0;
			String frame="";
			for(Iterator it=AllAddPeopleIndividuals.iterator();it.hasNext();)
			{  	OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
				if(i==2){
					break;

				}else if(i==0){

					if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false"))
					{
						String  PeopleID=addModelIndiviual.getPropertyValue(modelIDProperty).toString();
						//System.out.println(PeopleID);
						String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
						int pos = PeopleNameInOwl.indexOf(":");
						String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
						//System.out.println(PeopleName);
						int rd=(int)(Math.random()*totalActionNum);//����������������жϼ��ĸ�����

						int pos1 = actionList1[rd].indexOf(":");
						String finalActionName =(String)actionList1[rd].subSequence(pos1+1, actionList1[rd].length());
//						actionList1[rd]="p12:passSmallSomthing3";
						System.out.println("��Ӷ������ƣ�"+actionList1[rd]);

						//2014.6.18�޸�--------------
						OWLIndividual finalAddactionindivadual=model.getOWLIndividual(actionList1[rd]);
						//�õ���������֡��������ƴ��
						frame=finalAddactionindivadual.getPropertyValue(maFrameNumber).toString() ;
						//�������ֵPreAction��InterAction��ComplishAction��InterActionObject
						//PreAction
						//OWLObjectProperty preActionProperty = interModel.getOWLObjectProperty(str11+"PreAction");getDataPropertyActionName
						//String preAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,preActionProperty,i);
						OWLDatatypeProperty preActionProperty=model.getOWLDatatypeProperty(str11+"PreAction");
						String preAction = getDataPropertyActionName(finalAddactionindivadual,preActionProperty);
						//InterAction
						OWLObjectProperty interActionProperty = model.getOWLObjectProperty(str11+"InterAction");
						String interAction = getInterPropertyActionName(finalAddactionindivadual,interActionProperty);
//						OWLDatatypeProperty interActionProperty=model.getOWLDatatypeProperty(str11+"InterAction");
//						String interAction = getDataPropertyActionName(finalAddactionindivadual,interActionProperty);
						//������������
						otherInterAction=interAction;
						//ComplishAction
						//OWLObjectProperty complishActionProperty = interModel.getOWLObjectProperty(str11+"ComplishAction");
						//String complishAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,complishActionProperty,i);
						OWLDatatypeProperty complishActionProperty=model.getOWLDatatypeProperty(str11+"ComplishAction");
						String complishAction = getDataPropertyActionName(finalAddactionindivadual,complishActionProperty);

						//InterActionObject
						OWLDatatypeProperty interActionObjectProperty=model.getOWLDatatypeProperty(str11+"InteractionObject");
						String interActionObject = getDataPropertyActionName(finalAddactionindivadual,interActionObjectProperty);
						System.out.println("������ӽ����"+interActionObject);
						OWLDatatypeProperty contactRangeProperty=model.getOWLDatatypeProperty(str11+"contactRange");
						String contactRange = getDataPropertyActionName(finalAddactionindivadual,contactRangeProperty);
						String relateAction =getDataPropertyActionName(finalAddactionindivadual,relateActionProperty);
						OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
						String spaceName= IndiviualOfSpace.getBrowserText();
//						String spaceName = getInterPropertyActionName(finalAddactionindivadual,addModelRelatedSpaceProperty);
						//�����ö����뽻���Ķ����������Լ�����ֵ
						//д��doc
						Element ruleName = name.addElement("rule");
						ruleName.addAttribute("ruleType","addActionToMa");
						ruleName.addAttribute("type","interaction");
						ruleName.addAttribute("usedModelID",PeopleID);
						ruleName.addAttribute("usedModelInMa",PeopleName);
						if(!preAction.equals("")){
							ruleName.addAttribute("preparatoryStage",preAction);
						}else{
							ruleName.addAttribute("preparatoryStage","null");
						}
						if(!relateAction.equals("")){
							ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame+"+"+relateAction);
						}else{
							ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame);
						}

						if(!complishAction.equals("")){
							ruleName.addAttribute("accomplishmentStage",complishAction);
						}else{
							ruleName.addAttribute("accomplishmentStage","null");
						}
						ruleName.addAttribute("contactRange",contactRange);
						if(!interActionObject.equals("")){
							ruleName.addAttribute("interactionObject",interActionObject);
						}else{
							ruleName.addAttribute("interactionObject","null");
						}
//						ruleName.addAttribute("spaceName",spaceName);
						ruleName.addAttribute("spaceClass",spaceName+"1");
						//��־λ������������Ѿ�������������������
						addModelIndiviual.setPropertyValue(isDeal, "true");
						i++;
					}else{
						System.out.println("û����");
					}

				}else{

					if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false"))
					{
						String  PeopleID=addModelIndiviual.getPropertyValue(modelIDProperty).toString();
						//System.out.println(PeopleID);
						String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
						int pos = PeopleNameInOwl.indexOf(":");
						String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
//						pos=otherInterAction.indexOf(":");
//						otherInterAction=(String)otherInterAction.subSequence(pos+1, otherInterAction.length());
						//System.out.println(PeopleName);
						System.out.println("��Ӷ������ƣ�"+otherInterAction);

						//2014.6.18�޸�--------------
						OWLIndividual finalAddactionindivadual=model.getOWLIndividual(otherInterAction);
						frame=finalAddactionindivadual.getPropertyValue(maFrameNumber).toString() ;
						//�������ֵPreAction��InterAction��ComplishAction��InterActionObject
						//PreAction
//						OWLObjectProperty preActionProperty = interModel.getOWLObjectProperty("PreAction");
//						String preAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,preActionProperty,i);
						OWLDatatypeProperty preActionProperty=model.getOWLDatatypeProperty(str11+"PreAction");
						String preAction = getDataPropertyActionName(finalAddactionindivadual,preActionProperty);
						//InterAction
//						OWLObjectProperty interActionProperty = interModel.getOWLObjectProperty("InterAction");
//						String interAction = getInterPropertyActionName(finalAddactionindivadual,interActionProperty);
						//	OWLDatatypeProperty interActionProperty=model.getOWLDatatypeProperty(str11+"InterAction");
						//String interAction = getDataPropertyActionName(finalAddactionindivadual,interActionProperty);
						//������������
						//otherInterAction=interAction;
						int pos1 = otherInterAction.indexOf(":");
						String finalActionName =(String)otherInterAction.subSequence(pos1+1, otherInterAction.length());
						//ComplishAction
//						OWLObjectProperty complishActionProperty = interModel.getOWLObjectProperty("ComplishAction");
//						String complishAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,complishActionProperty,i);
						OWLDatatypeProperty complishActionProperty=model.getOWLDatatypeProperty(str11+"ComplishAction");
						String complishAction = getDataPropertyActionName(finalAddactionindivadual,complishActionProperty);

						//InterActionObject
						OWLDatatypeProperty interActionObjectProperty=model.getOWLDatatypeProperty(str11+"InteractionObject");
						String interActionObject = getDataPropertyActionName(finalAddactionindivadual,interActionObjectProperty);

						OWLDatatypeProperty contactRangeProperty=model.getOWLDatatypeProperty(str11+"contactRange");
						String contactRange = getDataPropertyActionName(finalAddactionindivadual,contactRangeProperty);

						String relateAction =getDataPropertyActionName(finalAddactionindivadual,relateActionProperty);

						//spaceName
						OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
						String spaceName= IndiviualOfSpace.getBrowserText();
//						String spaceName = getInterPropertyActionName(finalAddactionindivadual,addModelRelatedSpaceProperty);
						//�����ö����뽻���Ķ����������Լ�����ֵ
						//д��doc
						Element ruleName = name.addElement("rule");
						ruleName.addAttribute("ruleType","addActionToMa");
						ruleName.addAttribute("type","interaction");
						ruleName.addAttribute("usedModelID",PeopleID);
						ruleName.addAttribute("usedModelInMa",PeopleName);
						if(!preAction.equals("")){
							ruleName.addAttribute("preparatoryStage",preAction);
						}else{
							ruleName.addAttribute("preparatoryStage","null");
						}
						if(!relateAction.equals("")){
							ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame+"+"+relateAction);
						}else{
							ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame);
						}


						if(!complishAction.equals("")){
							ruleName.addAttribute("accomplishmentStage",complishAction);
						}else{
							ruleName.addAttribute("accomplishmentStage","null");
						}
						ruleName.addAttribute("contactRange",contactRange);
						if(!interActionObject.equals("")){
							ruleName.addAttribute("interactionObject",interActionObject);
						}else{
							ruleName.addAttribute("interactionObject","null");
						}

//						ruleName.addAttribute("spaceName",spaceName);
						ruleName.addAttribute("spaceClass",spaceName+"1");
						//��־λ������������Ѿ�������������������
						addModelIndiviual.setPropertyValue(isDeal, "true");
						i++;
					}else{
						System.out.println("û����");
					}



				}


			}

			AllAddPeopleIndividuals.clear();
		}






		return doc;
	}




	/**
	 * �����򣺸������⡢����ma����ģ�����������
	 * @param list	ģ�弯��
	 * @param model	ontology����
	 * @param interModel	����������interaction
	 * @param maName	��������
	 * @param doc	adl.xml
	 * @return
	 * @throws OntologyLoadException
	 */

	public String hasInterActionInfer(ArrayList<String> list, OWLModel model, String maName,
									  Document doc) throws OntologyLoadException {
		String   str1="p2:";
		//��ʱ
		//String   str1="p3:";

		/*
		 * ��ȡdoc�ĸ��ڵ�
		 */
		Element rootName = doc.getRootElement();
		//���ڻ�ȡ�ӽڵ�
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println(adlTopic);
		/*
		 * ��ȡmaʵ��
		 */
		OWLIndividual maIndividual=model.getOWLIndividual(maName);

		/*
		 * �õ��ĸ�������
		 */
		System.out.println("model"+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		OWLDatatypeProperty  isDeal=model.getOWLDatatypeProperty("isUsed");
		OWLDatatypeProperty  maFrameNumber=model.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty  relateActionProperty=model.getOWLDatatypeProperty(str1+"relateAction");
		OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");
		int totalActionNum = 0;

		String[] actionList1 = new String[200]; // �����洢����
		int actFromTemNumber = 0;
		/*
		 * ��ȡtopic
		 */
		ArrayList<String> topicNameList = new ArrayList<String>();
		String topicName="";
		if (adlTopic != "") {
			topicNameList.add(adlTopic);
		}
		else {

			Collection hasTopicValues = maIndividual.getPropertyValues(topicNameProperty);
			if (hasTopicValues.isEmpty()) {
				System.out.println("topicΪ��");
			}
			else {
				for (Iterator it = hasTopicValues.iterator(); it.hasNext();) {
					topicNameList.add(it.next().toString());
				}
			}
		}



		/*
		 * ���ȴ���topic��Ϣ,��ȡ�����з��ϴ�topic�Ķ�������
		 */
		String   str11="p12:";
		ArrayList<String> actionList = new ArrayList<String>();
		//*
		OWLNamedClass actionClass = model.getOWLNamedClass(str11+"ExcuteAction");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
			topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);

			Collection ActionSubClass = actionClass.getSubclasses(true);//.getSubclasses(true)

			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for(Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
					continue;
				}
				String hasTopicClassType = (subclass.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
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
		if (actionList.size() != 0) {
			System.out.println("���������ȡ��" + actionList.size() + "������");
			actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
			totalActionNum = actionList.size();
		}
		//������û�г�ȡ������������ģ����Ϣ��ȡ����
		else
		{
			/*
			 * ������ģ����Ϣ
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str11 + "mapToInterAction");
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
		//�ڲ���������������������£�����������������ת��ִ�е��˳���
		if(actionList.size()==0&&actFromTemNumber==0){
			//TODO
			System.out.println("������ԭ��û�г�ȡ�����ʵĶ���,����ģ��,ִ�е��˶���");
			return "onePeopleAction";
		}




		//Ϊ��ӵ�������Ӷ���
		//��ӵ������д�뵽֪ʶ���е�AddModelRelated��
		//���ԡ�����AddModelRelated�´���ʵ��addModelID1������������ֵisUsed=true
		/*	 */
		OWLNamedClass AddModelRelatedClass =model.getOWLNamedClass("AddModelRelated");
		/*
		OWLIndividual addIndividual = AddModelRelatedClass.createOWLIndividual("addModelID1");
		addIndividual.setPropertyValue(addModelTypeProperty, "people");
		addIndividual.setPropertyValue(modelIDProperty, "1");
		addIndividual.setPropertyValue(isDeal, "false");
		addIndividual.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");

		OWLIndividual addIndividual1 = AddModelRelatedClass.createOWLIndividual("addModelID2");
		addIndividual1.setPropertyValue(addModelTypeProperty, "people");
		addIndividual1.setPropertyValue(modelIDProperty, "2");
		addIndividual1.setPropertyValue(isDeal, "false");
		addIndividual1.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");
//		addIndividual.setPropertyValue(hasModelNameProperty, "boy");


		*/



		Collection  oldAllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);

		//������ģ������ѡ��������ͬ���ÿռ������ʵ��ģ��
		String tempSpaceName="";
		List<String> listSpace=new ArrayList<String>();
		List<String> newListSpace=new ArrayList<String>();
		List<OWLIndividual> individualSpaceList=new ArrayList<OWLIndividual>();
		List<OWLIndividual> newIndividualSpaceList=new ArrayList<OWLIndividual>();

		for(Iterator it=oldAllAddPeopleIndividuals.iterator();it.hasNext();){

			OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
			String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
			int pos = PeopleNameInOwl.indexOf(":");
			String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
			if(!PeopleName.equals("M_bikeboy.ma")&&addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false")){
				tempSpaceName=addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty).toString();
//				OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
//				tempSpaceName= IndiviualOfSpace.getBrowserText();
				listSpace.add(tempSpaceName);
				individualSpaceList.add(addModelIndiviual);
			}

		}
		if(individualSpaceList.size()<2){
			return "peopleNotEnough";
		}

		for(int i=0;i<listSpace.size();i++){
			for(int j=i+1;j<listSpace.size();j++){
				if(listSpace.get(i).equals(listSpace.get(j))){
					if(newListSpace.size()==0){
						newListSpace.add(listSpace.get(i));
						newIndividualSpaceList.add(individualSpaceList.get(i));
					}
					newListSpace.add(listSpace.get(j));
					newIndividualSpaceList.add(individualSpaceList.get(j));
				}
			}
			if(newListSpace.size()>=2){
				break;
			}
		}

		if(newListSpace.size()<2){
			//TODO
			return "peopleNotInOneSpace";
		}
		return "interActionIsOk";

	}





	/**
	 * CollectionתList
	 * @param objectValues
	 * @return
	 */
	public List<String> collectionToList(Collection objectValues){
		List<String> objList=new ArrayList<String>();

		for (Iterator obj = objectValues.iterator(); obj
				.hasNext();)

		{
			String actionIndiviual = (String) obj
					.next();
			objList.add(actionIndiviual);
		}
			/*
			int random=new Random().nextInt(2);
			if(random==1){
				Collections.reverse(objList);
			}
			*/
		return objList;


	}

	/**
	 * CollectionתList
	 * @param objectValues
	 * @return
	 */
	public List<String> collectionToListObjectValue(Collection objectValues){
		List<String> objList=new ArrayList<String>();
		for (Iterator obj = objectValues.iterator(); obj
				.hasNext();)

		{
			OWLIndividual actionIndiviual = (OWLIndividual) obj
					.next();
			objList.add(actionIndiviual.getBrowserText());
		}
		return objList;
	}

	/**
	 * �õ�ObjectProperty���Ե�ֵ��String��
	 * @param finalAddactionindivadual ��������ʵ��
	 * @param actionProperty	��������Object����
	 * @return
	 */
	public String getPreOrComplishPropertyActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty,int i){
		String action="";
		if(i==0){
			action=getWalkActionName(finalAddactionindivadual,actionProperty);
		}else{
			action=getNoWalkActionName(finalAddactionindivadual,actionProperty);
		}
		return action;
	}

	/**
	 * �õ���������
	 * @param finalAddactionindivadual
	 * @param actionProperty
	 * @return
	 */
	public String getInterPropertyActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty){
		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		if(!actionValues.isEmpty()){
			List<String> actionList = collectionToListObjectValue(actionValues);
			int random=(int)(Math.random()*actionList.size());
			System.out.println("action����"+actionList.get(random));
			if(actionList.get(random)!=null&&!actionList.get(random).equals("")){
				action=actionList.get(random);
			}
		}

		return action;
	}

	/**
	 * �õ�����λ����Ķ������ߡ��ܣ�
	 * @param finalAddactionindivadual
	 * @param actionProperty
	 * @return
	 */
	public String getWalkActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty){

		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		//�жϼ����Ƿ�Ϊ�ղ����Ƿ����������������ϵ�ֵ���������ж���������ֵ�Ƿ�����λ�ƵĶ���������
		if(!actionValues.isEmpty()){
			if(actionValues.size()>1){
				List<String> actionList = collectionToList(actionValues);
				for (String list : actionList) {
					if(list.contains("run")||list.contains("walk")||list.contains("Run")||list.contains("Walk")){
						return list;
					}
				}
				int random=(int)(Math.random()*actionList.size());
				System.out.println("action����"+actionList.get(random));
				if(actionList.get(random)!=null&&!actionList.get(random).equals("")){
					action=actionList.get(random);
				}
			}
		}

		return action;

	}

	/**
	 * �õ��ڶ�����λ���ද��
	 * @param finalAddactionindivadual
	 * @param actionProperty
	 * @return
	 */
	public String getNoWalkActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty){

		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		List<String> filterActionList=new ArrayList<String>();
		//�жϼ����Ƿ�Ϊ�ղ����Ƿ����������������ϵ�ֵ���������ж���������ֵ�Ƿ�����λ�ƵĶ���������
		if(!actionValues.isEmpty()){
			if(actionValues.size()>1){
				List<String> actionList = collectionToList(actionValues);
				for (String list : actionList) {
					if(list.contains("run")||list.contains("walk")||list.contains("Run")||list.contains("Walk")){
						continue;
					}else{
						filterActionList.add(list);
					}
				}
				int random=(int)(Math.random()*filterActionList.size());
				System.out.println("action����"+filterActionList.get(random));
				if(filterActionList.get(random)!=null&&!filterActionList.get(random).equals("")){
					action=filterActionList.get(random);
				}
			}
		}

		return action;
	}
	/**
	 * �õ�DataProperty���Ե�ֵ��String��
	 * @param finalAddactionindivadual	��������ʵ��
	 * @param actionProperty	��������Data����
	 * @return
	 */
	public String getDataPropertyActionName(OWLIndividual finalAddactionindivadual,OWLDatatypeProperty actionProperty){
		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		if(!actionValues.isEmpty()){
			List<String> actionList = collectionToList(actionValues);
			int random=(int)(Math.random()*actionList.size());
			System.out.println("action����"+actionList.get(random));
			if(actionList.get(random)!=null&&!actionList.get(random).equals("")){
				action=actionList.get(random);
			}
		}

		return action;
	}

}
