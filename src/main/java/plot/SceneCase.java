package plot;

public class SceneCase {
	public String sceneName="";
	public double decisionvalue=0;
	public double MGProb=0;
	public double IEProb=0;
	public double ruleReason=0;
	public double templateRelated=0;
	public double templateModelNum=0;
	public double placableModelNum=0;
	public double isBackgroundScene=0;

	public double colorModelNum=0;
	public double placableColorModelNum=0;
	public double isWeatherable=0;
	public double timeable=0;
	public double indualScore=0;
	public double score=0;
	public double fullScore=0;
	public double ActionScore=0;

	public double QProb=0;


	public SceneCase(String sName)
	{
		sceneName = sName;
	}

	public SceneCase(
			String sName,
			double MProb,
			double ieProb,
			double rReason,
			double tRelated,
			double tModelNum,
			double pModelNum0,
			double cModelNum,
			double pColorModelNum,
			double isW,
			double QP,
			double isT,
			double actionScore,
			double isBG)
	{

		sceneName = sName;
		MGProb =MProb;
		IEProb = ieProb;
		ruleReason =rReason;
		templateRelated = tRelated;

		templateModelNum=tModelNum;
		placableModelNum=pModelNum0;

		colorModelNum=cModelNum;
		placableColorModelNum=pColorModelNum;
		isWeatherable = isW;
		timeable=isT;
		QProb = QP;
		ActionScore=actionScore;
		isBackgroundScene=isBG;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public double getDecisionvalue() {
		return decisionvalue;
	}

	public void setDecisionvalue(double decisionvalue) {
		this.decisionvalue = decisionvalue;
	}

	public double getMGProb() {
		return MGProb;
	}

	public void setMGProb(double mGProb) {
		MGProb = mGProb;
	}

	public double getIEProb() {
		return IEProb;
	}

	public void setIEProb(double iEProb) {
		IEProb = iEProb;
	}

	public double getRuleReason() {
		return ruleReason;
	}

	public void setRuleReason(double ruleReason) {
		this.ruleReason = ruleReason;
	}

	public double getTemplateRelated() {
		return templateRelated;
	}

	public void setTemplateRelated(double templateRelated) {
		this.templateRelated = templateRelated;
	}

	public double getTemplateModelNum() {
		return templateModelNum;
	}

	public void setTemplateModelNum(double templateModelNum) {
		this.templateModelNum = templateModelNum;
	}

	public double getPlacableModelNum() {
		return placableModelNum;
	}

	public void setPlacableModelNum(double placableModelNum) {
		this.placableModelNum = placableModelNum;
	}

	public double getIsBackgroundScene() {
		return isBackgroundScene;
	}

	public void setIsBackgroundScene(double isBackgroundScene) {
		this.isBackgroundScene = isBackgroundScene;
	}

	public double getColorModelNum() {
		return colorModelNum;
	}

	public void setColorModelNum(double colorModelNum) {
		this.colorModelNum = colorModelNum;
	}

	public double getPlacableColorModelNum() {
		return placableColorModelNum;
	}

	public void setPlacableColorModelNum(double placableColorModelNum) {
		this.placableColorModelNum = placableColorModelNum;
	}

	public double getIsWeatherable() {
		return isWeatherable;
	}

	public void setIsWeatherable(double isWeatherable) {
		this.isWeatherable = isWeatherable;
	}

	public double getTimeable() {
		return timeable;
	}

	public void setTimeable(double timeable) {
		this.timeable = timeable;
	}

	public double getIndualScore() {
		return indualScore;
	}

	public void setIndualScore(double indualScore) {
		this.indualScore = indualScore;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getFullScore() {
		return fullScore;
	}

	public void setFullScore(double fullScore) {
		this.fullScore = fullScore;
	}

	public double getActionScore() {
		return ActionScore;
	}

	public void setActionScore(double actionScore) {
		ActionScore = actionScore;
	}

	public double getQProb() {
		return QProb;
	}

	public void setQProb(double qProb) {
		QProb = qProb;
	}


}
