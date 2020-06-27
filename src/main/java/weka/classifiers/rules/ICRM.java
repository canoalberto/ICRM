package weka.classifiers.rules;

import java.util.Enumeration;
import java.util.Vector;

import net.sf.jclec.problem.classification.classic.ClassicInstance;
import net.sf.jclec.problem.classification.classic.classifier.CrispRuleBase;
import net.sf.jclec.problem.classification.icrm.ICRMAlgorithm;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.TechnicalInformation;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;

public class ICRM extends ClassificationAlgorithm
{
	static final long serialVersionUID = 1310258880025902106L;
	
	public ICRM() {
		super();
	}
	
	/**
	 * Returns an enumeration describing the available options
	 * 
	 * @return an enumeration of all the available options
	 */
	public Enumeration<Option> listOptions() {
		Vector<Option> options = super.listCommonOptions();
		
		return options.elements();
	} 

	/**
	 * Parses a given list of options. <p/>
	 *
 	<!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -P &lt;population size&gt;
	 *The population size (default: 20).</pre>
	 * 
	 * <pre> -G &lt;number of generations&gt;
	 *The number of generations (default: 10).</pre>
	 *
	 * <pre> -S &lt;seed&gt;
	 *The seed for random values (default: 111111111).</pre>
	 *
	 * <pre> -C &lt;crossover probability&gt;
	 *The crossover probability (default: 0.5).</pre>
	 *
	 * <pre> -M &lt;mutation probability&gt;
	 *The mutation probability (default: 0.1).</pre>
	 *
 	<!-- options-end -->
	 *
	 * @param options the list of options as an array of strings
	 * @throws Exception if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {
		super.setOptions(options);
	} 

	/**
	 * Gets the current settings of the Classifier.
	 *
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String [] getOptions() {
		String[] options = super.getOptions();
		int current = 0;

		for(current = 0; options[current]!=""; current++);
		
		return options;
	}

	/**
	 * Returns default capabilities of the classifier, i.e., of LinearRegression.
	 *
	 * @return the capabilities of this classifier
	 */
	public Capabilities getCapabilities()
	{
		Capabilities result = new Capabilities(this);

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);

		// class
		result.enable(Capability.NOMINAL_CLASS);

		return result;
	}
	
	public void configureMetadata(Instances instances) throws Exception
	{
		super.setSpeciesClassname("net.sf.jclec.problem.classification.icrm.ICRMExprTreeSpecies");
		super.setProviderClassname("net.sf.jclec.problem.classification.icrm.ICRMExprTreeCreator");
		super.setEvaluatorClassname("net.sf.jclec.problem.classification.icrm.ICRMEvaluator");

		super.configureMetadata(instances);

		((ICRMAlgorithm) algorithm).setTrainSet(dataset);		
		((ICRMAlgorithm) algorithm).prepare();
	}

	/**
	 * Generates the classifier.
	 *
	 * @param instances the instances to be used for building the classifier
	 * @throws Exception if the classifier can't be built successfully
	 */
	public void buildClassifier(Instances instances) throws Exception
	{
		algorithm = new ICRMAlgorithm();
		
		configureMetadata(instances);
		
		algorithm.execute();
	}

	public double classifyInstance(Instance ins)
	{
		ClassicInstance instance = new ClassicInstance(ins.numAttributes());
		
		instance.setValues(ins.toDoubleArray());

		return ((CrispRuleBase) ((ICRMAlgorithm) algorithm).getClassifier()).classify(instance);
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing 
	 * detailed information about the technical background of this class,
	 * e.g., paper reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation 	result;

		result = new TechnicalInformation(Type.ARTICLE);
		result.setValue(Field.AUTHOR, "A. Cano and A. Zafra and S. Ventura");
		result.setValue(Field.TITLE, "An Interpretable Classification Rule Mining Algorithm");
		result.setValue(Field.JOURNAL,"Information Sciences");
		result.setValue(Field.YEAR,"2013");
		result.setValue(Field.VOLUME,"120");
		result.setValue(Field.PAGES, "1-20");

		return result;
	}
	
	/**
	 * Returns a string describing classifier
	 * @return a description suitable for
	 * displaying in the explorer/experimenter gui
	 */
	public String globalInfo()
	{
		return  "ICRM algorithm. For more information, see\n\n" + getTechnicalInformation().toString();
	}
	
	/**
	 * Returns the tip text for this property.
	 * @return tip text for this property suitable for
	 * displaying in the explorer/experimenter gui
	 */
	public String seedTipText() {
		return "Seed number for random initialization.";
	}

	/**
	 * Main method for testing this class
	 *
	 * @param argv the commandline options
	 */
	public static void main(String [] argv){
		runClassifier(new ICRM(), argv);
	}
}