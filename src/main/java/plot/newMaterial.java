package plot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import javax.xml.parsers.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class newMaterial {
	static String human = "p1:Human";
	String str1 = "";
	String str = "p15:";
	static Random random = new Random(System.currentTimeMillis());
	String ieTopic;
	String ieTopic1;
	ArrayList<String> topicList = new ArrayList<String>();
	public Document NewMaterialInfer(ArrayList<String> list, OWLModel model, String maName, Document doc)
			throws OntologyLoadException, SWRLRuleEngineException{
		String newMat = "";
		String ModelName = "";
		//topicList���������
		@SuppressWarnings("unused")
		int scenceFlag = 0;//�������ǣ�1������   2������   3��ˮ��
		/*
		 * ��ȡdoc���ڵ�
		 */
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println("adlTopic="+adlTopic);
		/*
		 * ��ȡmaʵ��
		 */
		OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
		if(maIndividual == null){
			System.out.println("maʵ���޷���ȡ�����ܲ����ڻ�ʧ��");
			return doc;
		}else
			System.out.println("��ȡʵ���ɹ���maʵ����"+maIndividual);
		//�õ��ĸ�������
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		@SuppressWarnings("unused")
		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty hasValueOfPlaceFlag = model.getOWLObjectProperty(str1 + "hasValueOfPlace");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		System.out.println("....................."+addModelTypeProperty.toString()+".................");
		/*
		 * �����жϻ�ȡ��maʵ�������ڳ����������ⳡ��
		 */
		OWLIndividual individualOfPlaceFlag = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlaceFlag);
		if(individualOfPlaceFlag == null){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText()
					+"��hasValueOfPlace����");
			return doc;
		}else if(individualOfPlaceFlag.getBrowserText().equals("InWaterDeccription")){
			scenceFlag = 3;
			System.out.println("ˮ�³������������ʹ滮");
			return doc;
		}else if(individualOfPlaceFlag.getBrowserText().equals("outDoorDescription")){
			scenceFlag = 2;
		}else if(individualOfPlaceFlag.getBrowserText().equals("inDoorDescription")){
			scenceFlag = 1;
		}else
			return doc;
		/*
		 * ���������������ģ�ͣ�������ͼ�滮
		 */
		OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");
		@SuppressWarnings("unchecked")
		Collection<OWLIndividual> topicNames = maIndividual.getPropertyValues(topicNameProperty);
		System.out.println("topicName.size = "+topicNames.size());
		if(ieTopic.contains("Topic"))
			topicList.add(ieTopic);
		System.out.println("topicList.size= "+topicList.size());
		if(hasCharacter(model,maIndividual)){
			System.out.println("��������ģ�ͣ�����������ͼ�滮");
			setTexturePeople(doc,model,maIndividual,topicList,list);
		}
		return doc;
	}

	/*
	 * �жϳ������Ƿ��������ģ��
	 */
	private boolean hasCharacter(OWLModel model, OWLIndividual ma) {
		// TODO Auto-generated method stub
		Collection<OWLIndividual> modelCollection = getModelsInMa(model,ma);
		for(Iterator<OWLIndividual> jt = modelCollection.iterator(); jt.hasNext(); ){
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			//����modelid�ҵ�modelʵ��
			OWLIndividual modelNameTemp = getModelNameFromID(model, modelIDTemp);
			RDFSNamedClass modelClass = getClassFromIndividual(model, modelNameTemp);
			/*
			 * ��ȡʵ������������и��࣬�ж��Ƿ�Ϊ����ʵ��
			 */
			Collection<?> superClasses = new ArrayList<Object>();
			superClasses = modelClass.getSuperclasses(true);
			RDFSNamedClass humanClass = model.getRDFSNamedClass(human);
			if(superClasses.contains(humanClass))
				return true;
		}
		return false;
	}

	/*
	 * ����ģ����ͼ
	 */
	public Document setTexturePeople(Document doc, OWLModel owlModel, OWLIndividual ma, ArrayList<String> topicList, ArrayList<String> list){
		Collection<OWLIndividual> characterCollection = getCharacters(owlModel, ma);
		System.out.println("The ma scence has " + characterCollection.size() + " characters");
		if(characterCollection.isEmpty()){
			System.out.println("�����в���������ģ��");
			return doc;
		}
		for(Iterator<OWLIndividual> jm = characterCollection.iterator(); jm.hasNext();){
			OWLIndividual characterID = (OWLIndividual) jm.next();
			System.out.println("�����д�������ģ��: " + characterID.getBrowserText());
			texturePlanPerPerson(doc, owlModel, characterID, topicList, list);
		}
		return doc;
	}
	/*
	 * Ϊÿ��ģ�ͽ�����ͼ�滮
	 */
	@SuppressWarnings("null")
	public void texturePlanPerPerson(Document doc, OWLModel owlModel,
									 OWLIndividual model, ArrayList<String> topicList, ArrayList<String> list) {
		Collection<RDFSNamedClass> textureTypeCollection = new ArrayList<RDFSNamedClass>();
		Collection<RDFSNamedClass> textureIndividualClass = new ArrayList<RDFSNamedClass>();
		Collection<RDFSNamedClass> seasonTopicChangeClass = new ArrayList<RDFSNamedClass>();
		Collection<RDFSNamedClass> seasonTemplateChangeClass = new ArrayList<RDFSNamedClass>();
		Collection<RDFSNamedClass> topicChangeClass = new ArrayList<RDFSNamedClass>();
		Collection<RDFSNamedClass> templateChangeClass = new ArrayList<RDFSNamedClass>();
		Collection<?> suiTextures = new ArrayList<Object>();
		OWLObjectProperty hasSuitableModelProperty = owlModel.getOWLObjectProperty(str+"hassuitpeople");
		OWLObjectProperty hasSuitSeasonTopicProperty = owlModel.getOWLObjectProperty(str+"hassuitseasontopic");
		System.out.println("hasSuitSeasonTopicProperty= " +hasSuitSeasonTopicProperty.getBrowserText());
		OWLObjectProperty hasSuitSeasonTemplateProperty = owlModel.getOWLObjectProperty(str+"hassuitseasontemplate");
		OWLObjectProperty hasSuitTopicProperty = owlModel.getOWLObjectProperty(str+"hassuittopic");
		OWLObjectProperty hasSuitTemplateProperty = owlModel.getOWLObjectProperty(str+"hassuittemplate");
		//����modelID�ҵ�modelʵ��
		OWLIndividual modelName = getModelNameFromID(owlModel,model);
		System.out.println(modelName.getBrowserText());
		//�ҵ��ʺ��������ͼʵ��
		suiTextures = owlModel.getRDFResourcesWithPropertyValue(hasSuitableModelProperty, modelName);
		OWLIndividual[] suiTextureIndividual = (OWLIndividual[]) suiTextures.toArray(new OWLIndividual[0]);
		for(int i=0; i<suiTextureIndividual.length; i++){

			RDFSNamedClass textureClass =  getClassFromIndividual(owlModel, suiTextureIndividual[i]);
			if(!textureIndividualClass.contains(textureClass)){
				textureIndividualClass.add(textureClass);
				System.out.println("���ں�����ͼʵ�����ࣺ "+textureClass);
			}
		}
		//�Ӽ������⿪ʼƥ��
		System.out.println("���⣺==================================");
		for(int j=0; j<topicList.size(); j++)
			System.out.println(topicList.get(j));
		for(int k=0; k<list.size(); k++)
			System.out.println(list.get(k));

		ArrayList<OWLIndividual> suitSeasonTopicIndividual = new ArrayList<OWLIndividual>();
		ArrayList<OWLIndividual> suitSeasonTemplateIndividual = new ArrayList<OWLIndividual>();
		ArrayList<OWLIndividual> suitTopicIndividual = new ArrayList<OWLIndividual>();
		ArrayList<OWLIndividual> suitTemplateIndividual = new ArrayList<OWLIndividual>();
		int n=0, n2=0;
		for(int i=0; i<suiTextureIndividual.length; i++){
			OWLIndividual ind = suiTextureIndividual[i];
			System.out.println(suiTextureIndividual[i].getBrowserText());
			System.out.println("ind= "+ind.getBrowserText());
			Collection<OWLIndividual> col = ind.getPropertyValues(hasSuitSeasonTopicProperty);
			if(col.size() == 0){
				continue;
			}
			else{
				System.out.println("============================================"+col.size());
				for(Iterator<OWLIndividual> ite = col.iterator(); ite.hasNext();){
					OWLIndividual owl = (OWLIndividual) ite.next();
					System.out.println("owl="+owl);
					String owl_str = owl.getBrowserText();
					String owl_str_name = (String) owl_str.subSequence(0, owl_str.length()-10);
					System.out.println("owl===="+owl_str_name);
					if(topicList.contains(owl_str_name)){
						System.out.println("yyyyyyyyyyyy");
						suitSeasonTopicIndividual.add(ind);
					}
				}
			}
		}
		n = suitSeasonTopicIndividual.size();
		int randomNum = 0;
		if(n > 0){
			for(int i=0; i<4; i++){
				randomNum = random.nextInt(n);
				RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suitSeasonTopicIndividual.get(randomNum));
				if(!textureTypeCollection.contains(textureClass)){
					textureTypeCollection.add(textureClass);
					System.out.println("��ͼ���ͣ� "+textureClass.getBrowserText());
					String textureName = (String) suitSeasonTopicIndividual.get(randomNum).getBrowserText().toString();
					seasonTopicChangeClass.add(textureClass);
					setTextureColor(doc,owlModel,model,topicList,list,textureClass.getBrowserText(),textureName);
				}
			}
		}//--------------------------------�����������------------------------------
		//ģ�����⿪ʼ======================================================
		if(textureTypeCollection.size() != 4){//��������û�ж��������͹滮
			Collection<String> templateNames = new ArrayList();
			int listSize = 0;
			if(list != null)
				listSize = list.size();
			if(listSize > 0){
				String strMuban = null;
				//String strMubanIndividual = null;
				int listSizeMuban = list.size();
				String[] listToStr = new String[listSizeMuban];
				String[] templateList = new String[listSizeMuban];
				String[] templateListIndividual = new String[listSizeMuban];
				if(listSizeMuban != 0){
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);
					for(int i=0; i<listToStr.length; i++){
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						strMuban = templateList[i].trim();
						System.out.println(str2);
						System.out.println(str2.length());;
						System.out.println(pos);
						String ll = (String) str2.subSequence(pos+1, str2.length());
						templateListIndividual[i] = ll;
						templateNames.add(ll);
						//strMubanIndividual = templateListIndividual[i].trim();
					}
				}
			}
			for(int i=0; i<suiTextureIndividual.length; i++){
				OWLIndividual ind = suiTextureIndividual[i];
				@SuppressWarnings("unchecked")
				Collection<OWLIndividual> col = ind.getPropertyValues(hasSuitSeasonTemplateProperty);
				if(col.size() == 0){
					continue;
				}
				else{
					for(Iterator<OWLIndividual> ite = col.iterator(); ite.hasNext();){
						OWLIndividual owl = (OWLIndividual) ite.next();
						if(templateNames.contains(owl))
							suitSeasonTemplateIndividual.add(ind);
					}
				}
			}
			n2 = suitSeasonTemplateIndividual.size();
			if(n2 > 0){
				for(int i=0; i<4; i++){
					randomNum = random.nextInt(n2);
					RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suitSeasonTemplateIndividual.get(randomNum));
					if(!textureTypeCollection.contains(textureClass)){
						textureTypeCollection.add(textureClass);
						System.out.println("��ͼ���ͣ� "+textureClass.getBrowserText());
						String textureName = (String) suitSeasonTemplateIndividual.get(randomNum).getBrowserText().toString();
						seasonTemplateChangeClass.add(textureClass);
						setTextureColor(doc,owlModel,model,topicList,list,textureClass.getBrowserText(),textureName);
					}
				}
			}
		}
		if(textureTypeCollection.size() != 4){
			int n3=0;
			for(int i=0; i<suiTextureIndividual.length; i++){
				OWLIndividual ind = suiTextureIndividual[i];
				@SuppressWarnings("unchecked")
				Collection<OWLIndividual> col = ind.getPropertyValues(hasSuitTopicProperty);
				if(col.size() == 0){
					continue;
				}
				else{
					for(Iterator<OWLIndividual> ite = col.iterator(); ite.hasNext();){
						OWLIndividual owl = (OWLIndividual) ite.next();
						if(topicList.contains(owl))
							suitTopicIndividual.add(ind);
					}
				}
			}
			n3 = suitTopicIndividual.size();
			if(n3 > 0){
				for(int i=0; i<4; i++){
					randomNum = random.nextInt(n3);
					RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suitTopicIndividual.get(randomNum));
					if(!textureTypeCollection.contains(textureClass)){
						textureTypeCollection.add(textureClass);
						System.out.println("��ͼ���ͣ� "+textureClass.getBrowserText());
						String textureName = (String) suitTopicIndividual.get(randomNum).getBrowserText().toString();
						topicChangeClass.add(textureClass);
						setTextureColor(doc,owlModel,model,topicList,list,textureClass.getBrowserText(),textureName);
					}
				}
			}
		}
		if(textureTypeCollection.size() != 4){
			Collection<String> templateNames = new ArrayList();
			int listSize = 0;
			if(list != null)
				listSize = list.size();
			if(listSize > 0){
				String strMuban = null;
				//String strMubanIndividual = null;
				int listSizeMuban = list.size();
				String[] listToStr = new String[listSizeMuban];
				String[] templateList = new String[listSizeMuban];
				String[] templateListIndividual = new String[listSizeMuban];
				if(listSizeMuban != 0){
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);
					for(int i=0; i<listToStr.length; i++){
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						strMuban = templateList[i].trim();
						templateListIndividual[i] = (String) str2.subSequence(pos+1, str2.length());
						templateNames.add(templateListIndividual[i]);
						//strMubanIndividual = templateListIndividual[i].trim();
					}
				}
			}
			for(int i=0; i<suiTextureIndividual.length; i++){
				OWLIndividual ind = suiTextureIndividual[i];
				@SuppressWarnings("unchecked")
				Collection<OWLIndividual> col = ind.getPropertyValues(hasSuitTemplateProperty);
				if(col.size() == 0){
					continue;
				}
				else{
					for(Iterator<OWLIndividual> ite = col.iterator(); ite.hasNext();){
						OWLIndividual owl = (OWLIndividual) ite.next();
						if(templateNames.contains(owl))
							suitTemplateIndividual.add(ind);
					}
				}
			}
			n2 = suitTemplateIndividual.size();
			if(n2 > 0){
				for(int i=0; i<4; i++){
					randomNum = random.nextInt(n2);
					RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suitSeasonTemplateIndividual.get(randomNum));
					if(!textureTypeCollection.contains(textureClass)){
						textureTypeCollection.add(textureClass);
						System.out.println("��ͼ���ͣ� "+textureClass.getBrowserText());
						String textureName = (String) suitSeasonTemplateIndividual.get(randomNum).getBrowserText().toString();
						seasonTemplateChangeClass.add(textureClass);
						setTextureColor(doc,owlModel,model,topicList,list,textureClass.getBrowserText(),textureName);
					}
				}
			}
		}
		if(textureTypeCollection.size() == 0){
			ArrayList<OWLIndividual> textureForTop = new ArrayList<OWLIndividual>();
			ArrayList<OWLIndividual> textureForDown = new ArrayList<OWLIndividual>();
			for(int l=0; l<suiTextureIndividual.length; l++){
				RDFSNamedClass tempclass = getClassFromIndividual(owlModel,suiTextureIndividual[l]);
				if(tempclass.getBrowserText().contains("all")){
					int randonNum = random.nextInt(suiTextureIndividual.length);
					RDFSNamedClass textureClass = getClassFromIndividual(owlModel,suiTextureIndividual[randonNum]);
					System.out.println("��ͼ���ͣ� "+textureClass.getBrowserText());
					String textureName = (String) suiTextureIndividual[randonNum].getBrowserText();
					setTextureColor(doc,owlModel,model,topicList,list,textureClass.getBrowserText(),textureName);
					break;
				}
				System.out.println(tempclass.getBrowserText());
				if(tempclass.getBrowserText().contains("Shirt"))
					textureForTop.add(suiTextureIndividual[l]);
				else if(tempclass.getBrowserText().contains("Trousers"))
					textureForDown.add(suiTextureIndividual[l]);
			}
			if(textureForTop.size()>0){
				int randonNum1 = random.nextInt(textureForTop.size());
				RDFSNamedClass textureClass = getClassFromIndividual(owlModel,textureForTop.get(randonNum1));
				System.out.println("��ͼ���ͣ� "+textureClass.getBrowserText());
				String textureName = (String) textureForTop.get(randonNum1).getBrowserText();
				setTextureColor(doc,owlModel,model,topicList,list,textureClass.getBrowserText(),textureName);
			}
			if(textureForDown.size()>0){
				int randonNum2 = random.nextInt(textureForDown.size());
				RDFSNamedClass textureClass1 = getClassFromIndividual(owlModel,textureForDown.get(randonNum2));
				System.out.println("��ͼ����2�� "+textureClass1.getBrowserText());
				String textureName2 = (String) textureForDown.get(randonNum2).getBrowserText();
				setTextureColor(doc,owlModel,model,topicList,list,textureClass1.getBrowserText(),textureName2);
			}
		}
	}

	/*
	 * Ϊ��ͼ�����ɫ
	 */
	public void setTextureColor(Document doc, OWLModel owlModel,
								OWLIndividual model,ArrayList<String> topicList, ArrayList<String> list, String textureType, String textureName){
		System.out.println("��ʼ��ɫ�滮=====================");
		Collection colorIndividual = new ArrayList();
		Collection colorSpecific = new ArrayList();
		Collection topicName = new ArrayList();
		Collection templateName = new ArrayList();
		String color_h, color_s, color_v;
		OWLObjectProperty colorForTopicProperty = owlModel.getOWLObjectProperty(str+"colorForTopic");
		OWLObjectProperty colorForTemplateProperty = owlModel.getOWLObjectProperty(str+"colorForTemplate");
		for(int i=0; i<topicList.size(); i++){
			//OWLIndividual topicIndividual = owlModel.getOWLIndividual(topicList.get(i));
			OWLNamedClass topicClass = owlModel.getOWLNamedClass(topicList.get(i));
			topicName = topicClass.getInstances(true);
			if(topicName.size() != 0){
				OWLIndividual[] topicNameIndividual = (OWLIndividual[]) topicName.toArray(new OWLIndividual[0]);
				OWLIndividual topicInd = topicNameIndividual[0];
				colorIndividual.addAll(owlModel.getRDFResourcesWithPropertyValue(colorForTopicProperty, topicInd));
			}
		}
		if(colorIndividual.size() > 0){
			System.out.println("������ȷ����ɫ�ɹ�=======================");
			int colorNum = colorIndividual.size();
			int randomNum = random.nextInt(colorNum);
			OWLIndividual[] colorSimpleIndividual = (OWLIndividual[]) colorIndividual.toArray(new OWLIndividual[0]);
			OWLIndividual color_simple = colorSimpleIndividual[randomNum];
			RDFSNamedClass colorClassName = getClassFromIndividual(owlModel,color_simple);
			colorSpecific = colorClassName.getInstances(true);
			int colorSpecificNum = colorSpecific.size();
			int randomNum2 = random.nextInt(colorSpecificNum);
			OWLIndividual[] colorSpecificIndividual = (OWLIndividual[]) colorSpecific.toArray(new OWLIndividual[0]);
			OWLIndividual colorValue = colorSpecificIndividual[randomNum2];
			OWLDatatypeProperty hascolorHueProperty = owlModel.getOWLDatatypeProperty(str+"hasColorHue");
			OWLDatatypeProperty hascolorSaturationProperty = owlModel.getOWLDatatypeProperty(str+"hasColorSaturation");
			OWLDatatypeProperty hascolorValueProperty = owlModel.getOWLDatatypeProperty(str+"hasColorValue");
			color_h = colorValue.getPropertyValue(hascolorHueProperty).toString();
			color_s = colorValue.getPropertyValue(hascolorSaturationProperty).toString();
			color_v = colorValue.getPropertyValue(hascolorValueProperty).toString();
			printRule(doc,owlModel,model,textureType,textureName,colorValue.getBrowserText(),color_h,color_s,color_v);
		}
		else{
			System.out.println("������ȷ����ɫʧ��=======================");
			ArrayList templateNames = new ArrayList();
			int listSize = 0;
			if(list != null)
				listSize = list.size();
			if(listSize > 0){
				String strMuban = null;
				//String strMubanIndividual = null;
				int listSizeMuban = list.size();
				String[] listToStr = new String[listSizeMuban];
				String[] templateList = new String[listSizeMuban];
				String[] templateListIndividual = new String[listSizeMuban];
				if(listSizeMuban != 0){
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);
					for(int i=0; i<listToStr.length; i++){
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						strMuban = templateList[i].trim();
						templateListIndividual[i] = (String) str2.subSequence(pos+1, str2.length());
						templateNames.add(templateListIndividual[i]);
						//strMubanIndividual = templateListIndividual[i].trim();
					}
				}
				if(templateNames.size() != 0){
					for(int temNum=0; temNum<templateList.length; temNum++){
						OWLNamedClass templateClass = owlModel.getOWLNamedClass((String) templateList[temNum]);
						System.out.println(templateClass.getBrowserText());
						templateName = templateClass.getInstances(true);
						if(templateName.size() != 0){
							OWLIndividual[] newTemplateNameIndividual = (OWLIndividual[]) templateName.toArray(new OWLIndividual[0]);
							for(int i=0; i<newTemplateNameIndividual.length; i++){
								if(newTemplateNameIndividual[i].getBrowserText().equals(templateListIndividual[temNum])){
									System.out.println(newTemplateNameIndividual[i].getBrowserText());
									OWLIndividual templateInd = newTemplateNameIndividual[i];
									System.out.println(templateInd.getBrowserText());
									colorIndividual.addAll(owlModel.getRDFResourcesWithPropertyValue(colorForTemplateProperty, templateInd));
									break;
								}
							}
						}
					}
					if(colorIndividual.size() > 0){
						int colorNum = colorIndividual.size();
						int randomNum = random.nextInt(colorNum);
						OWLIndividual[] colorSimpleIndividual = (OWLIndividual[]) colorIndividual.toArray(new OWLIndividual[0]);
						OWLIndividual color_simple = colorSimpleIndividual[randomNum];
						System.out.println(color_simple.getBrowserText()+"==============++++++++++");
						RDFSNamedClass colorClassName = getClassFromIndividual(owlModel,color_simple);
						colorSpecific = colorClassName.getInstances(true);
						int colorSpecificNum = colorSpecific.size();
						int randomNum2 = random.nextInt(colorSpecificNum);
						OWLIndividual[] colorSpecificIndividual = (OWLIndividual[]) colorSpecific.toArray(new OWLIndividual[0]);
						OWLIndividual colorValue = colorSpecificIndividual[randomNum2];
						OWLDatatypeProperty hascolorHueProperty = owlModel.getOWLDatatypeProperty(str+"hasColorHue");
						OWLDatatypeProperty hascolorSaturationProperty = owlModel.getOWLDatatypeProperty(str+"hasColorSaturation");
						OWLDatatypeProperty hascolorValueProperty = owlModel.getOWLDatatypeProperty(str+"hasColorValue");
						color_h = colorValue.getPropertyValue(hascolorHueProperty).toString();
						color_s = colorValue.getPropertyValue(hascolorSaturationProperty).toString();
						color_v = colorValue.getPropertyValue(hascolorValueProperty).toString();
						printRule(doc,owlModel,model,textureType,textureName,colorValue.getBrowserText(),color_h,color_s,color_v);
					}
					else printRule(doc,owlModel,model,textureType,textureName);
				}
				else printRule(doc,owlModel,model,textureType,textureName);
			}
			else printRule(doc,owlModel,model,textureType,textureName);
		}
	}
	private void printRule(Document doc, OWLModel model, OWLIndividual characterID, String textureType,
						   String texturePath) {
		// TODO Auto-generated method stub
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setTexture");
		ruleName.addAttribute("type", "PeopleTexture");
		String modelID = characterID.getBrowserText();
		int modelIDBegin = modelID.lastIndexOf(":")+1;
		ruleName.addAttribute("usedModelID", modelID.substring(modelIDBegin, modelID.length()));
		OWLIndividual characterName = getModelNameFromID(model,characterID);
		String modelName = characterName.getBrowserText();
		int modelNameBegin = modelName.lastIndexOf(":")+1;
		ruleName.addAttribute("usedModelInMa", modelName.substring(modelNameBegin, modelName.length()));
		ruleName.addAttribute("textureType", textureType.substring(4));
		ruleName.addAttribute("textureName", texturePath.substring(4));
		ruleName.addAttribute("toChangeColor", "false");
	}

	/*
	 * ��ӡ������ͼ�滮
	 */
	public void printRule(Document doc, OWLModel model,
						  OWLIndividual characterID, String textureType, String texturePath, String colorName, String color_h, String color_s, String color_v) {
		// TODO Auto-generated method stub
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setTexture");
		ruleName.addAttribute("type", "PeopleTexture");
		String modelID = characterID.getBrowserText();
		int modelIDBegin = modelID.lastIndexOf(":")+1;
		ruleName.addAttribute("usedModelID", modelID.substring(modelIDBegin, modelID.length()));
		OWLIndividual characterName = getModelNameFromID(model,characterID);
		String modelName = characterName.getBrowserText();
		int modelNameBegin = modelName.lastIndexOf(":")+1;
		ruleName.addAttribute("usedModelInMa", modelName.substring(modelNameBegin, modelName.length()));
		ruleName.addAttribute("textureType", textureType.substring(4));
		ruleName.addAttribute("textureName", texturePath.substring(4));
		ruleName.addAttribute("toChangeColor", "true");
		ruleName.addAttribute("colorName",colorName.substring(4));
		ruleName.addAttribute("colorHue", color_h);
		ruleName.addAttribute("colorSaturation", color_s);
		ruleName.addAttribute("colorValue", color_v);

	}
	/*
	 * ��ȡ���е�����ģ�ͣ���������ģ��ʵ���ļ���
	 */
	public Collection<OWLIndividual> getCharacters(OWLModel owlModel, OWLIndividual ma){
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		Collection<OWLIndividual> characters = new ArrayList<OWLIndividual>();
		for(Iterator<OWLIndividual> jt = modelCollection.iterator(); jt.hasNext(); ){
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			//����modelid�ҵ�modelʵ��
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel, modelIDTemp);
			RDFSNamedClass modelClass = getClassFromIndividual(owlModel, modelNameTemp);
			/*
			 * ��ȡʵ������������и��࣬�ж��Ƿ�Ϊ����ʵ��
			 */
			Collection<?> superClasses = new ArrayList<Object>();
			superClasses = modelClass.getSuperclasses(true);
			RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
			if(superClasses.contains(humanClass))
				characters.add(modelIDTemp);
		}
		return characters;
	}
	private OWLIndividual getModelNameFromID(OWLModel owlModel, OWLIndividual modelID) {
		// TODO Auto-generated method stub
		OWLObjectProperty hasModelNameProperty = owlModel.getOWLObjectProperty("hasModelName");
		OWLIndividual modelName = (OWLIndividual) modelID.getPropertyValue(hasModelNameProperty);
		return modelName;
	}
	/*
	 * ��ȡ�����е�����ģ�ͣ�����������ӵ�ģ��
	 */
	private Collection<OWLIndividual> getModelsInMa(OWLModel owlModel, OWLIndividual ma) {
		// TODO Auto-generated method stub
		Collection<OWLIndividual> modelCollection = new ArrayList<OWLIndividual>();
		OWLObjectProperty hasModelProperty = owlModel.getOWLObjectProperty("hasmodel");
		Collection<OWLIndividual> hasModelCollection = ma.getPropertyValues(hasModelProperty);
		//��ȡ����ģ��ʵ��ID
		modelCollection.addAll(hasModelCollection);
		//�ڳ����м����ģ�͵���
		OWLNamedClass AddModelClass = owlModel.getOWLNamedClass("AddModelRelated");
		Collection<OWLIndividual> addModelCollection = AddModelClass.getInstances(true);
		//addmodel�����а�����ģ��Ҳ�ǳ����е�ģ��
		if(addModelCollection.size() != 0)
			modelCollection.addAll(addModelCollection);
		Collection<OWLIndividual> deleteModelList = new ArrayList<OWLIndividual>();
		//ɾ���������õ�ģ��
		for(Iterator<OWLIndividual> jt = modelCollection.iterator(); jt.hasNext();){
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel,modelIDTemp);
			if(modelNameTemp == null){
				System.out.println("modelID: "+modelIDTemp.getBrowserText()+" don't have modelName");
				deleteModelList.add(modelIDTemp);
			}
		}
		modelCollection.removeAll(deleteModelList);
		return modelCollection;
	}
	/*
	 * �ж��Ƿ�Ϊ����ģ��
	 */
	public boolean isCharater(OWLModel owlModel, OWLIndividual model){
		RDFSNamedClass modelClass = getClassFromIndividual(owlModel, model);
		/*
		 * ��ȡʵ������������и��࣬�ж��Ƿ����p1��Human��
		 */
		Collection<?> superClasses = new ArrayList<Object>();
		superClasses = modelClass.getSuperclasses(true);
		RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
		if(superClasses.contains(humanClass))
			return true;
		else
			return false;
	}
	/*
	 * ���ʵ�����ڵ�����
	 */
	private RDFSNamedClass getClassFromIndividual(OWLModel model, OWLIndividual individualName) {
		// TODO Auto-generated method stub
		String classNameStr = individualName.getRDFType().getBrowserText();
		RDFSNamedClass className = model.getRDFSNamedClass(classNameStr);
		return className;
	}
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
			JDOMException, DocumentException, OntologyLoadException, SWRLRuleEngineException {

		//String owlPath = "file:///E:/OWL/new/ontologyOWL/AllOwlFile/sumo_phone3.owl";
		String owlPath = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		OWLModel owlModel=ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist = new ArrayList<String>();
		ArrayList<String> tlist = new ArrayList<String>();
		//tlist.add("LaborDayTopic");
		//tlist.add("SmileTopic");
		alist.add("ComfortTemplate:comfortTemplate");
		alist.add("LikeTemplate:likeTemplate");
		alist.add("SummerTemplate:summerTemplate");
		String maName = "snake.ma";
		OWLIndividual individualName = owlModel.getOWLIndividual(maName);

		File file = new File("D:/OWL/adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);

		System.out.println("��ʼ!");
		newMaterial my_test = new newMaterial();
		Document document1 = my_test.setTexturePeople(document, owlModel, individualName, tlist, alist);
		//Document document1 = my_test.NewMaterialInfer(alist, owlModel, maName, document);
		XMLWriter writer = new XMLWriter(new FileWriter("D:/OWL/testMat.xml"));
		writer.write(document1);
		System.out.println("������");
		writer.close();

	}
}
