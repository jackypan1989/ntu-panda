package ntu.im.bilab.panda.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ntu.im.bilab.panda.parameter.ApplicabilityIntegrity;
import ntu.im.bilab.panda.parameter.Diversity;
import ntu.im.bilab.panda.parameter.Innovation;
import ntu.im.bilab.panda.parameter.Profile;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/* Panda version 1.0.0.1
*  2012/4/3
*  Last Author : Jacky 	
*  Test Patent : D339456
*/

public class Classification extends JPanel{
	
	// for classification
	private FastVector fvWekaAttributes;
	private Instances isTrainingSet, isTestingSet;
	private Instance target;
	private String Patent_id;
	
	// for GUI
	private JPanel enterPanel;   //enter PID
	private JPanel resultPanel;  //result
	private JButton startCal;
	private JLabel enterPID;
	private JTextField textPID;
	private JTextArea calculatingArea;
	private JLabel NaiveBayse;
	private JLabel J48;
	private JLabel NaiveBayseR;
	private JLabel J48R;
	
	public Classification() {
		setView();
	}
	
	public void setView(){
		this.setBackground( Color.WHITE );
		
		enterPanel = new JPanel();
		add( enterPanel, BorderLayout.NORTH );
		enterPanel.setLayout( new GridLayout( 1,3 ) );
		
		enterPID = new JLabel( "Please enter patent number : " );
		enterPanel.add( enterPID );
		
		textPID = new JTextField(10);
		enterPanel.add( textPID );
		
		calculatingArea = new JTextArea(10,15);
		calculatingArea.setEditable(false);
		add( calculatingArea );
		
		startCal = new JButton( "start" );
		enterPanel.add( startCal );
		
		startCal.addActionListener(
				new ActionListener(){
					public void actionPerformed( ActionEvent event ){
						Patent_id = textPID.getText(); 
						System.out.println(Patent_id);
						setVector();
						setTrainingDataSet();
						setTarget();
					}
				}
		);

		resultPanel = new JPanel(); 
		add( resultPanel, BorderLayout.SOUTH );
		resultPanel.setLayout( new GridLayout( 2,2 ) );
		
		NaiveBayse = new JLabel( "NaiveBayes result : ");
		resultPanel.add( NaiveBayse );
		
		NaiveBayseR = new JLabel();
		resultPanel.add( NaiveBayseR );
		
		J48 = new JLabel( "J48 result : ");
		resultPanel.add( J48 );
		
		J48R = new JLabel();
		resultPanel.add( J48R );
	}
	
	// set the weka vector
	private void setVector() {
		//Declare 1 nominal attribute
		FastVector fvNominalVal = new FastVector(2);
		fvNominalVal.addElement("worthy");
		fvNominalVal.addElement("unworthy");
		Attribute Class = new Attribute("classes", fvNominalVal);
		
		//Declare 16 numeric attributes
		Attribute Attr1 = new Attribute("PatentGroup");
		Attribute Attr2 = new Attribute("PatentedBcite");
		Attribute Attr3 = new Attribute("ScienceLink");
		Attribute Attr4 = new Attribute("Backcitation");
		Attribute Attr5 = new Attribute("NumOfInventor");
		Attribute Attr6 = new Attribute("NumOfAss");
		Attribute Attr7 = new Attribute("NumOfCite");
		Attribute Attr8 = new Attribute("ApproveTime");
		Attribute Attr9 = new Attribute("TechScope");
		Attribute Attr10 = new Attribute("Generality");
		Attribute Attr11 = new Attribute("Originality");
		Attribute Attr12 = new Attribute("NumOfClaim");
		Attribute Attr13 = new Attribute("NumOfD_Claim");
		Attribute Attr14 = new Attribute("NumOfI_Claim");
		Attribute Attr15 = new Attribute("TransferTimes");
		Attribute Attr16 = new Attribute("Office");
		
		//Declare the feature vector
		fvWekaAttributes = new FastVector(17);
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
		fvWekaAttributes.addElement(Class);
		
	}
	
	// set the training set
	private void setTrainingDataSet() {
		try {
			// print info
			System.out.println("Collecting training dataset, waiting...");
			calculatingArea.setText("Collecting training dataset, waiting..."+"\n");

			// create an empty training set
			isTrainingSet = new Instances("Training", fvWekaAttributes, 10);
			isTrainingSet.setClassIndex(16);
			
			// connect to patent data base
			Class.forName(Config.DRIVER);
			Connection conn = DriverManager.getConnection(
				      Config.DATABASE_URL, Config.DATABASE_USER ,  Config.DATABASE_PASSWORD);
			
			// fetch the unworthy patents
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from unworthy_patents");

			while(rs.next()) {
				Instance iExample = new Instance(17);
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
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(11), rs.getInt(13));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(12), rs.getInt(14));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(13), rs.getInt(15));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(14), rs.getInt(16));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(15), rs.getInt(17));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(16), "unworthy");
				isTrainingSet.add(iExample);	
			}
			rs.close();
			stat.close();
			
			// fetch the worthy patents
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from worthy_patents");

			while(rs.next()) {
				Instance iExample = new Instance(17);
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
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(11), rs.getInt(13));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(12), rs.getInt(14));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(13), rs.getInt(15));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(14), rs.getInt(16));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(15), rs.getInt(17));
				iExample.setValue((Attribute)fvWekaAttributes.elementAt(16), "worthy");
				isTrainingSet.add(iExample);
			}
			rs.close();
			stat.close();
			
		}catch(Exception e) { 
			e.printStackTrace(); 
		}
	}
	
	// set the target patent
	public void setTarget() {
		// print info
		System.out.println("Calculating" + Patent_id + "index, waiting...");
		calculatingArea.append("Calculating" + Patent_id + "index, waiting..."+"\n");
		
		// create the target and testing set
		target = new Instance(17);
		isTestingSet = new Instances("Testing", fvWekaAttributes, 10);
		isTestingSet.setClassIndex(16);
		
		// add the parameter
		Innovation inno = new Innovation(Patent_id);
		Profile prof = new Profile(Patent_id);
		Diversity div = new Diversity(Patent_id);
		ApplicabilityIntegrity AI = new ApplicabilityIntegrity(Patent_id);
		
		target.setValue((Attribute)fvWekaAttributes.elementAt(0), inno.PatentGroups());
		target.setValue((Attribute)fvWekaAttributes.elementAt(1), inno.PatentedBackwardCitations());
		target.setValue((Attribute)fvWekaAttributes.elementAt(2), inno.ScienceLinks());
		target.setValue((Attribute)fvWekaAttributes.elementAt(3), inno.BackwardCitations());
		target.setValue((Attribute)fvWekaAttributes.elementAt(4), prof.GetInventors());
		target.setValue((Attribute)fvWekaAttributes.elementAt(5), prof.GetAssignee());
		target.setValue((Attribute)fvWekaAttributes.elementAt(6), prof.GetCitation());
		target.setValue((Attribute)fvWekaAttributes.elementAt(7), prof.GetApproveTime());
		target.setValue((Attribute)fvWekaAttributes.elementAt(8), div.GetTechScope());
		target.setValue((Attribute)fvWekaAttributes.elementAt(9), div.GetGenerality());
		target.setValue((Attribute)fvWekaAttributes.elementAt(10), div.GetOriginality());
		target.setValue((Attribute)fvWekaAttributes.elementAt(11), AI.NoClaims());
		target.setValue((Attribute)fvWekaAttributes.elementAt(12), AI.NoDepClaim());
		target.setValue((Attribute)fvWekaAttributes.elementAt(13), AI.NoIndepClaim());
		target.setValue((Attribute)fvWekaAttributes.elementAt(14), AI.NoTransAs());
		target.setValue((Attribute)fvWekaAttributes.elementAt(15), AI.NoPatentOffice());
		isTestingSet.add(target);
		
		// print info
		System.out.println("\n index calculating completed");
		calculatingArea.append("\n index calculating completed"+"\n");

		classifyByNaiveByes();
		classifyByJ48();
	}
	
	public void classifyByNaiveByes() {
		try {
			Classifier classifier = (Classifier)new NaiveBayes();
			classifier.buildClassifier(isTrainingSet);
			System.out.print(": NaiveBayes ->");
			if(classifier.classifyInstance(isTestingSet.instance(0))== 0.0) {
				System.out.println(" " + Patent_id + "Τ基");
				NaiveBayseR.setText(" " + Patent_id + "Τ基");
			}
			
			else {
				System.out.println(" " + Patent_id + "⊿基");
				NaiveBayseR.setText(" " + Patent_id + "⊿基");
			}
		}

		catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	
	public void classifyByJ48() {
		try {
			J48 classifier = new J48();
			classifier.buildClassifier(isTrainingSet);
			System.out.print(": J48 ->");
			if(classifier.classifyInstance(isTestingSet.instance(0))== 0.0) {
				System.out.println(" " + Patent_id + "Τ基");
				J48R.setText(" " + Patent_id + "Τ基");
			}
			
			else {
				System.out.println(" " + Patent_id + "⊿基");
				J48R.setText(" " + Patent_id + "⊿基");
			}
		}

		catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	
	public static void main(String[] args) throws Exception {
		// create frame
		JFrame application = new JFrame( "Patent Value Prediction" ); 
		
		// create paint panel
		Classification instance = new Classification();
		
		// set the jframe
		application.add( instance, BorderLayout.CENTER ); 																	
		application.add( instance.enterPanel, BorderLayout.NORTH );
		application.add( instance.resultPanel, BorderLayout.SOUTH );
		application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		application.setSize( 550, 200 );
		application.setVisible( true );	
	}
}
