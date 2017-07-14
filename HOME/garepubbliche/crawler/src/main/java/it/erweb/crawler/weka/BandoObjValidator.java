package it.erweb.crawler.weka;

import java.io.File;

import java.io.PrintWriter;
import it.erweb.crawler.configurations.PropertiesManager;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * 	Data mining methods: train the agent and use it for classify the ban object 
 */
public class BandoObjValidator
{
	private static Instances train;
	
	/**
	 * Agent Training: learns to classify the objects, basing on a train.arff file
	 */
	public static void train()
	{ 
		try
		{
			// import file di training
			ConverterUtils.DataSource source1 = new ConverterUtils.DataSource(PropertiesManager.VALIDATOR_TRAIN_PATH);
			// converte in oggetto weka
			train = source1.getDataSet();

			// se non legge l'attributo su cui classificare (class:'oggetto'/'nonOggetto'), allora
			// lo imposta prendendo l'ultimo tra gli attributi dichiarati nell'arff (convention)
			if(train.classIndex() == -1)
				train.setClassIndex(train.numAttributes() - 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Classifies the input test case in "Valid / Not Valid", based on what the agent has learned
	 * 
	 * @return	true if the test case is classified as valid; else otherwise
	 */
	public static boolean validate(String oggettoBando)
	{
		boolean ret = false;
		String validObj;
		
		try(PrintWriter writer = new PrintWriter(new File(PropertiesManager.VALIDATOR_TEST_PATH)))
		{
			//converte oggettoBando togliendo tutti i "'" e "\n"
			validObj = removeIllegalChars(oggettoBando);
			
			
			//scrive sul file di test l'oggettoBando (da testare)
			writer.write("@relation weka_mymodel_model\n@attribute text string\n@attribute class {t,f}\n@data\n"
					+ "'" + validObj + "',?\n");
			writer.close();
			
			// import file di test appena scritto
			ConverterUtils.DataSource source2 = new ConverterUtils.DataSource(PropertiesManager.VALIDATOR_TEST_PATH);
			Instances test = source2.getDataSet();
			if(test.classIndex() == -1)
				test.setClassIndex(train.numAttributes() - 1);

			// model: train basato sul primo file
			NaiveBayesMultinomialText naiveBayes = new NaiveBayesMultinomialText();
			naiveBayes.buildClassifier(train);

			// fa la prediction
			double label = naiveBayes.classifyInstance(test.instance(0));
			test.instance(0).setClassValue(label);

			//trova il risultato
			ret = (test.instance(0).stringValue(1).equals("t"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/*
	public static boolean classifyNoFile(String oggettoBando)
	{
		boolean ret = false;
		
		try
		{
			ArrayList<Attribute> attrs = new ArrayList<Attribute>();
			attrs.add(new Attribute("text"));
			attrs.add(new Attribute("class"));
						
			Instances test = new Instances("Test dataset", attrs, 0);			
			double[] values = new double[test.numAttributes()];
			values[0] = test.attribute(0).addStringValue(oggettoBando);
			values[1] = Utils.missingValue();
			test.add(new DenseInstance(1.0, values));
			
			 if(test.classIndex() == -1)
				test.setClassIndex(train.numAttributes() - 1);

			// model: train basato sul primo file
			NaiveBayesMultinomialText naiveBayes = new NaiveBayesMultinomialText();
			naiveBayes.buildClassifier(train);

			// fa la prediction
			double label = naiveBayes.classifyInstance(test.instance(0));
			test.instance(0).setClassValue(label);

			//trova il risultato
			  String prediction = test.classAttribute().value((int) label);
			
			  return false;
			  //ret = (test.instance(0).stringValue(1).equals("t"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	*/
	
	/**
	 * 	Removes WEKA .arff file illegal characters
	 * 
	 * @param oggettoBando	string supposed to be not valid
	 * @return	the validated and converted string
	 */
	public static String removeIllegalChars(String oggettoBando)
	{
		String ret = "";
		
		//rimpiazza "'" e "<new line>" con "<blank>"
		ret = oggettoBando.replaceAll("'|\n\n\n", " ").replace("\n", " ");
		return ret;
	}
}
