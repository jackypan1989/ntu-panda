package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;


import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class LicensabilityClassification {
	
	//private J48 treeClassifier;
	private FastVector fvWekaAttributes;
	private Instances trainingSet;
	private Instance target;

	public LicensabilityClassification() {
		setVector();
	}
	
	private void setVector() {
		//Declare 1 nominal attribute
		FastVector fvNominalVal = new FastVector(2);
		//0=Y 1=N
		fvNominalVal.addElement("Y");
		fvNominalVal.addElement("N");
		Attribute Class = new Attribute("class", fvNominalVal);
		
		//Declare 25 numeric attributes
		Attribute Attr1 = new Attribute("Patent_year");
		Attribute Attr2 = new Attribute("num_of_inventors");
		Attribute Attr3 = new Attribute("num_of_foreign_inventors");
		Attribute Attr4 = new Attribute("diversity_IPC");
		Attribute Attr5 = new Attribute("diversity_USPC");
		Attribute Attr6 = new Attribute("family_volume");
		Attribute Attr7 = new Attribute("family_size");
		Attribute Attr8 = new Attribute("major_market");
		Attribute Attr9 = new Attribute("num_of_claims");
		Attribute Attr10 = new Attribute("num_of_indep_claims");
		Attribute Attr11 = new Attribute("num_of_dep_claims");
		Attribute Attr12 = new Attribute("ave_length_of_indep_claims");
		Attribute Attr13 = new Attribute("num_of_bwd_citations");
		Attribute Attr14 = new Attribute("num_of_fwd_citations");
		Attribute Attr15 = new Attribute("num_of_fwd_3years");
		Attribute Attr16 = new Attribute("num_of_fwd_5years");
		Attribute Attr17 = new Attribute("ave_num_of_fwd");
		Attribute Attr18 = new Attribute("approval_time");
		Attribute Attr19 = new Attribute("length_of_description");
		Attribute Attr20 = new Attribute("bwd_selfcitation_rate");
		Attribute Attr21 = new Attribute("originality_IPC");
		Attribute Attr22 = new Attribute("originality_USPC");
		Attribute Attr23 = new Attribute("generality_IPC");
		Attribute Attr24 = new Attribute("generality_USPC");
		Attribute Attr25 = new Attribute("patent_age");
		
		
		//Declare the feature vector
		fvWekaAttributes = new FastVector(26);
		fvWekaAttributes.addElement(Attr1);
		fvWekaAttributes.addElement(Attr2);
		fvWekaAttributes.addElement(Attr3);
		fvWekaAttributes.addElement(Attr4);
		fvWekaAttributes.addElement(Attr5);
		fvWekaAttributes.addElement(Attr6);
		fvWekaAttributes.addElement(Attr7);
		fvWekaAttributes.addElement(Attr8);
		fvWekaAttributes.addElement(Attr9);
		fvWekaAttributes.addElement(Attr10);
		fvWekaAttributes.addElement(Attr11);
		fvWekaAttributes.addElement(Attr12);
		fvWekaAttributes.addElement(Attr13);
		fvWekaAttributes.addElement(Attr14);
		fvWekaAttributes.addElement(Attr15);
		fvWekaAttributes.addElement(Attr16);
		fvWekaAttributes.addElement(Attr17);
		fvWekaAttributes.addElement(Attr18);
		fvWekaAttributes.addElement(Attr19);
		fvWekaAttributes.addElement(Attr20);
		fvWekaAttributes.addElement(Attr21);
		fvWekaAttributes.addElement(Attr22);
		fvWekaAttributes.addElement(Attr23);
		fvWekaAttributes.addElement(Attr24);
		fvWekaAttributes.addElement(Attr25);
		fvWekaAttributes.addElement(Class);
		
	}
	
	private void setTrainingSet() throws SQLException {
		trainingSet = new Instances("Training", fvWekaAttributes, 10);
		trainingSet.setClassIndex(25);
		Connection conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER ,  Config.DATABASE_PASSWORD);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("SELECT * FROM `licensability_negative` UNION SELECT * FROM `licensability_positive`");
		while (rs.next()) {
			Instance iExample = new Instance(26);
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), rs.getInt(2));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), rs.getInt(3));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(2), rs.getInt(4));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(3), rs.getInt(5));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(4), rs.getInt(6));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(5), rs.getInt(7));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(6), rs.getInt(8));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(7), rs.getInt(9));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(8), rs.getInt(10));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(9), rs.getInt(11));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(10), rs.getInt(12));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(11), rs.getFloat(13));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(12), rs.getInt(14));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(13), rs.getInt(15));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(14), rs.getInt(16));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(15), rs.getInt(17));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(16), rs.getFloat(18));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(17), rs.getInt(19));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(18), rs.getFloat(20));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(19), rs.getFloat(21));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(20), rs.getFloat(22));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(21), rs.getFloat(23));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(22), rs.getFloat(24));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(23), rs.getFloat(25));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(24), rs.getInt(26));
			iExample.setValue((Attribute)fvWekaAttributes.elementAt(25), rs.getString(27));
			trainingSet.add(iExample);
			System.out.println(iExample);
		}
		rs.close();
		stat.close();
		conn.close();
	}
	
	private void setTarget(String PatentID) {
		Instance target;
		target = new Instance(26);
		//targetSet = new Instances("Target", fvWekaAttributes, 10);
		//targetSet.setClassIndex(25);
		PatentAge age = new PatentAge();
		age.ParseInventors(PatentID);
		age.CountPatentAge(PatentID);
		
		BackwardCitation bwd = new BackwardCitation();
		bwd.SelfCitationRate(PatentID);
		
		claims cl = new claims();
		cl.ExtractClaims(PatentID);
		
		Originality origin = new Originality();
		origin.OriginIndex(PatentID);
		/*PLEASE　FILL ALL ELEMENT**/
		target.setValue((Attribute)fvWekaAttributes.elementAt(0), age.GetNumOfInventors());
		target.setValue((Attribute)fvWekaAttributes.elementAt(1), ());
		target.setValue((Attribute)fvWekaAttributes.elementAt(2), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(3), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(4), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(5), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(6), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(7), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(8), cl.GetNumOfClaim());
		target.setValue((Attribute)fvWekaAttributes.elementAt(9), cl.GetNumOfIndepClaim());
		target.setValue((Attribute)fvWekaAttributes.elementAt(10), cl.GetNumOfDepClaim());
		target.setValue((Attribute)fvWekaAttributes.elementAt(11), cl.GetAveLengthOfIndepClaim());
		target.setValue((Attribute)fvWekaAttributes.elementAt(12), bwd.GetNumOfBwd());
		target.setValue((Attribute)fvWekaAttributes.elementAt(13), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(14), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(15), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(16), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(17), age.GetApprovalTime());
		target.setValue((Attribute)fvWekaAttributes.elementAt(18), ());
		target.setValue((Attribute)fvWekaAttributes.elementAt(19), bwd.GetBwdCitationRate());
		target.setValue((Attribute)fvWekaAttributes.elementAt(20), origin.GetOriginalityIPC());
		target.setValue((Attribute)fvWekaAttributes.elementAt(21), origin.GetOriginalityUSPC());
		target.setValue((Attribute)fvWekaAttributes.elementAt(22), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(23), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(24), age.GetPatentAgeIssued());
		target.setValue((Attribute)fvWekaAttributes.elementAt(25), ());
		//targetSet.add(target);
	}
	
	//return type 再看要怎樣
	public void classifyPatent(String PatentId) throws Exception {
		if (fvWekaAttributes == null) {
			setVector();
		}
		setTrainingSet();
		setTarget(PatentId);
		
		J48 classifier = new J48();
		classifier.buildClassifier(trainingSet);
		double classifiedClass = classifier.classifyInstance(target);
		//0=Y 1=N, see setVector() definition
		if (classifiedClass == 0.0) {
			//Licensability == Y
		} else {
			//Licensability == N
		}
	}
	
	public static void main(String[] args) throws Exception {
		
	}

}
