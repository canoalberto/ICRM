package weka.classifiers.rules;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import net.sf.jclec.IEvaluator;
import net.sf.jclec.IMutator;
import net.sf.jclec.IProvider;
import net.sf.jclec.IRecombinator;
import net.sf.jclec.ISelector;
import net.sf.jclec.ISpecies;
import net.sf.jclec.problem.classification.classic.ClassicClassificationMetadata;
import net.sf.jclec.problem.classification.classic.ClassicInstance;
import net.sf.jclec.problem.util.dataset.ArffDataSet;
import net.sf.jclec.problem.util.dataset.IExample;
import net.sf.jclec.problem.util.dataset.attribute.CategoricalAttribute;
import net.sf.jclec.problem.util.dataset.attribute.NumericalAttribute;
import net.sf.jclec.util.random.IRandGenFactory;
import net.sf.jclec.util.random.RanecuFactory;
import net.sf.jclec.util.range.Closure;
import net.sf.jclec.util.range.Interval;

import org.apache.commons.configuration.ConfigurationRuntimeException;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

/**
 * Abstract class for connecting JCLEC classification algorithm with Weka
 * 
 * @author Alberto Cano 
 */

public abstract class ClassificationAlgorithm extends AbstractClassifier implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler
{
	private static final long serialVersionUID = 6639194007580838497L;

	protected net.sf.jclec.problem.classification.ClassificationAlgorithm algorithm;

	protected int populationSize = 100;
	
	protected int generations = 100;

	protected double crossoverProb = 0.8;
	
	protected double mutationProb = 0.1;
	
	protected int seed = 123456789;
	
	protected ArffDataSet dataset;

	protected ClassicClassificationMetadata metadata;

	protected ArrayList<String> classNames;
	
	protected String classAttName;

	protected ISelector parentsSelector;

	protected IRecombinator recombinator;

	protected IMutator mutator;

	protected ISpecies species;

	protected IEvaluator evaluator;

	private String speciesClassname;

	private String evaluatorClassname;

	private String providerClassname;

	private String parentsSelectorClassname;

	private String recombinatorClassname;
	
	private String mutatorClassname;

	protected String recbaseOpClassname;

	protected String mutbaseOpClassname;
	
	private int parentSelector = 1;

	private static final int TOURNAMENT_SELECTOR = 1;
	private static final int ROULETTE_SELECTOR = 2;
	private static final int RANDOM_SELECTOR = 3;
	private static final int BETTERS_SELECTOR = 4;
	
	protected static final Tag[] selectorTags = {
			new Tag(TOURNAMENT_SELECTOR, "Tournament Selector"),
			new Tag(ROULETTE_SELECTOR, "Roulette Selector"),
			new Tag(RANDOM_SELECTOR, "Random Selector"),
			new Tag(BETTERS_SELECTOR, "Better Selector")
	};
	
	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the generations
	 */
	public int getGenerations() {
		return generations;
	}

	/**
	 * @param generations the generations to set
	 */
	public void setGenerations(int generations) {
		this.generations = generations;
	}

	/**
	 * @return the crossoverProb
	 */
	public double getCrossoverProb() {
		return crossoverProb;
	}

	/**
	 * @param crossoverProb the crossoverProb to set
	 */
	public void setCrossoverProb(double crossoverProb) {
		this.crossoverProb = crossoverProb;
	}

	/**
	 * @return the mutationProb
	 */
	public double getMutationProb() {
		return mutationProb;
	}

	/**
	 * @param mutationProb the mutationProb to set
	 */
	public void setMutationProb(double mutationProb) {
		this.mutationProb = mutationProb;
	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the parents selector tag
	 */
	public SelectedTag getParentsSelector() {
		return new SelectedTag(parentSelector, selectorTags);
	}

	/**
	 * @param selectorTag the selectorTag to set
	 */
	public void setParentsSelector(SelectedTag tag) {
		if(tag.getTags() == selectorTags)
			parentSelector = tag.getSelectedTag().getID();
	}

	/**
	 * @return the speciesClassname
	 */
	public String getSpeciesClassname() {
		return speciesClassname;
	}

	/**
	 * @param speciesClassname the speciesClassname to set
	 */
	public void setSpeciesClassname(String speciesClassname) {
		this.speciesClassname = speciesClassname;
	}

	/**
	 * @return the evaluatorClassname
	 */
	public String getEvaluatorClassname() {
		return evaluatorClassname;
	}

	/**
	 * @param evaluatorClassname the evaluatorClassname to set
	 */
	public void setEvaluatorClassname(String evaluatorClassname) {
		this.evaluatorClassname = evaluatorClassname;
	}

	/**
	 * @return the providerClassname
	 */
	public String getProviderClassname() {
		return providerClassname;
	}

	/**
	 * @param providerClassname the providerClassname to set
	 */
	public void setProviderClassname(String providerClassname) {
		this.providerClassname = providerClassname;
	}

	/**
	 * @return the parentsSelectorClassname
	 */
	public String getParentsSelectorClassname() {
		return parentsSelectorClassname;
	}

	/**
	 * @param parentsSelectorClassname the parentsSelectorClassname to set
	 */
	public void setParentsSelectorClassname(String parentsSelectorClassname) {
		this.parentsSelectorClassname = parentsSelectorClassname;
	}

	/**
	 * @return the recombinatorClassname
	 */
	public String getRecombinatorClassname() {
		return recombinatorClassname;
	}

	/**
	 * @param recombinatorClassname the recombinatorClassname to set
	 */
	public void setRecombinatorClassname(String recombinatorClassname) {
		this.recombinatorClassname = recombinatorClassname;
	}

	/**
	 * @return the recbaseOpClassname
	 */
	public String getRecbaseOpClassname() {
		return recbaseOpClassname;
	}

	/**
	 * @param recbaseOpClassname the recbaseOpClassname to set
	 */
	public void setRecbaseOpClassname(String recbaseOpClassname) {
		this.recbaseOpClassname = recbaseOpClassname;
	}

	/**
	 * @return the mutatorClassname
	 */
	public String getMutatorClassname() {
		return mutatorClassname;
	}

	/**
	 * @param mutatorClassname the mutatorClassname to set
	 */
	public void setMutatorClassname(String mutatorClassname) {
		this.mutatorClassname = mutatorClassname;
	}

	/**
	 * @return the mutbaseOpClassname
	 */
	public String getMutbaseOpClassname() {
		return mutbaseOpClassname;
	}

	/**
	 * @param mutbaseOpClassname the mutbaseOpClassname to set
	 */
	public void setMutbaseOpClassname(String mutbaseOpClassname) {
		this.mutbaseOpClassname = mutbaseOpClassname;
	}

	/**
	 * Returns an enumeration describing the available options
	 * 
	 * @return an enumeration of all the available options
	 */
	public Vector<Option> listCommonOptions()
	{
		Vector<Option> newVector = new Vector<Option>();

		newVector.addElement(new Option("\tSet the population size " + "\n\t(default 100)", "P",1,"-P <population size>"));
		newVector.addElement(new Option("\tSet the number of generations " +"\n\t(default 100)", "G",1,"-G <number of generations>"));
		newVector.addElement(new Option("\tSet mutation probability " +"\n\t(default 0.1)", "M",1,"-M <mutation probability>"));
		newVector.addElement(new Option("\tSet crossover probability" +"\n\t(default 0.8)", "C",1,"-C <crossover probability>"));
		newVector.addElement(new Option("\tSet seed" +"\n\t(default 123456789)", "D",1,"-D <seed>"));
		newVector.addElement(new Option("\tSet parent selector" +"\n", "S",1,"-S <parent selector>"));

		return newVector;
	}

	/**
	 * Parses a given list of options. <p/>
	 *
	<!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -P &lt;population size&gt;
	 *  The population size (default: 20).</pre>
	 * 
	 * <pre> -G &lt;number of generations&gt;
	 *  The number of generations (default: 10).</pre>
	 *  
	 * <pre> -S &lt;seed&gt;
	 *  The seed for random values (default: 111111111).</pre>
	 *  
	 * <pre> -C &lt;crossover probability&gt;
	 *  The crossover probability (default: 0.5).</pre>
	 *  
	 * <pre> -M &lt;mutation probability&gt;
	 *  The mutation probability (default: 0.1).</pre>
	 *  
	<!-- options-end -->
	 *
	 * @param options the list of options as an array of strings
	 * @throws Exception if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception
	{
		// Other options
		String optionString = Utils.getOption('P', options);
		if (optionString.length() != 0)
			populationSize = (new Integer(optionString)).intValue();

		optionString = Utils.getOption('G', options);
		if (optionString.length() != 0)
			generations = (new Integer(optionString)).intValue();

		optionString = Utils.getOption('D', options);
		if (optionString.length() != 0)
			seed = (new Integer(optionString)).intValue();

		optionString = Utils.getOption('C', options);
		if (optionString.length() != 0)
			crossoverProb = (new Double(optionString)).doubleValue();

		optionString = Utils.getOption('M', options);
		if (optionString.length() != 0)
			mutationProb  = (new Double(optionString)).doubleValue();
		
		if(Utils.getFlag('S', options))
			setParentsSelector(new SelectedTag(TOURNAMENT_SELECTOR, selectorTags));
		
		Utils.checkForRemainingOptions(options);
	} 	

	/**
	 * Gets the current settings of the Classifier.
	 *
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String [] getOptions()
	{
		String [] options = new String [30];
		int current = 0;

		options[current++] = "-P"; options[current++] = "" + populationSize;
		options[current++] = "-G"; options[current++] = "" + generations;
		options[current++] = "-M"; options[current++] = "" + mutationProb;
		options[current++] = "-C"; options[current++] = "" + crossoverProb;
		options[current++] = "-D"; options[current++] = "" + seed;
		options[current++] = "-S"; options[current++] = "" + parentSelector;

		while (current < options.length) {
			options[current++] = "";
		}
		return options;
	}

	/**
	 * Returns a string describing classifier
	 * @return a description suitable for
	 * displaying in the explorer/experimenter gui
	 */
	public String globalInfo()
	{
		return  "Classifier algorithm. For more information, see\n\n" + getTechnicalInformation().toString();
	}

	@SuppressWarnings("unchecked")
	public void configureMetadata(Instances instances) throws Exception
	{
		switch(parentSelector)
		{
			case RANDOM_SELECTOR:
				setParentsSelectorClassname("net.sf.jclec.selector.RandomSelector");
				break;
			case ROULETTE_SELECTOR:
				setParentsSelectorClassname("net.sf.jclec.selector.RouletteSelector");
				break;
			case TOURNAMENT_SELECTOR:
				setParentsSelectorClassname("net.sf.jclec.selector.TournamentSelector");
				break;
			case BETTERS_SELECTOR:
				setParentsSelectorClassname("net.sf.jclec.selector.BettersSelector");
				break;
		}
		
		// can classifier handle the data?
		getCapabilities().testWithFail(instances);

		// remove instances with missing class
		instances.deleteWithMissingClass();

		// Set population size
		algorithm.setPopulationSize(populationSize);

		// Set maximum of generations
		algorithm.setMaxOfGenerations(generations);

		// Configure Datasets
		dataset = new ArffDataSet();
		metadata = new ClassicClassificationMetadata();

		ArrayList<IExample> trainInstances = new ArrayList<IExample>();
		
		// Get Metadata
		Enumeration enumeration = instances.enumerateAttributes();
		
		int attindex = 0;
		
		while(enumeration.hasMoreElements())
		{
			Attribute att =  (Attribute) enumeration.nextElement();
			
			switch(att.type())
			{
				case Attribute.NUMERIC:
				{
					NumericalAttribute attribute = new NumericalAttribute();
					attribute.setName(att.name());

					Interval intervals = new Interval();
					intervals.setClosure(Closure.ClosedClosed);
					intervals.setLeft(instances.attributeStats(attindex).numericStats.min);
					intervals.setRight(instances.attributeStats(attindex).numericStats.max);
					attribute.setInterval(intervals);

					metadata.addAttribute(attribute);
					break;
				}
				case Attribute.NOMINAL:
				{
					CategoricalAttribute attribute = new CategoricalAttribute();
					attribute.setName(att.name());
					
					List<String> categoriesList = new ArrayList<String>();
					
					Enumeration<Object> categories = att.enumerateValues();
					while(categories.hasMoreElements())
					{
						categoriesList.add((String) categories.nextElement());
					}

					attribute.setCategories(categoriesList);

					metadata.addAttribute(attribute);
					break;
				}
			}

			attindex++;
		}

		metadata.setClassIndex(instances.classIndex());

		classNames = new ArrayList<String>();
		
		enumeration = instances.classAttribute().enumerateValues();
		while(enumeration.hasMoreElements())
		{
			classNames.add((String) enumeration.nextElement());
		}
		
		classAttName = instances.classAttribute().name();
		
		CategoricalAttribute attributeClass = new CategoricalAttribute();
		attributeClass.setName(classAttName);
		attributeClass.setCategories(classNames);
		metadata.addAttribute(attributeClass);
		
		dataset.setMetadata(metadata);

		// Get dataset instances
		enumeration = instances.enumerateInstances();
		while(enumeration.hasMoreElements())
		{
			Instance inst =  (Instance) enumeration.nextElement();
			ClassicInstance instance = new ClassicInstance(metadata.numberOfAttributes());
			
			for(int i = 0; i < metadata.numberOfAttributes(); i++)
				instance.setValue(i, inst.value(i));
			
			instance.setClassValue(inst.classValue());
			trainInstances.add(instance);
		}

		dataset.setExamples(trainInstances);
		
		/****************************** RANDGEN ***************************************/
		
		String randGenFactoryClassname = "net.sf.jclec.util.random.RanecuFactory";
		Class<? extends IRandGenFactory> randGenFactoryClass = (Class<? extends IRandGenFactory>) Class.forName(randGenFactoryClassname);
		IRandGenFactory randGenFactory = randGenFactoryClass.newInstance();
		((RanecuFactory) randGenFactory).setSeed(seed);
		
		algorithm.setRandGenFactory(randGenFactory);
		algorithm.initRandGen();
		
		/****************************** SPECIES ***************************************/

		try
		{
			Class<? extends ISpecies> speciesClass = (Class<? extends ISpecies>) Class.forName(speciesClassname);
			species = speciesClass.newInstance();
			algorithm.setSpecies(species);
		}
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal species classname");
		} 
			
		/****************************** EVALUATOR *************************************/

		try
		{
			Class<? extends IEvaluator> evaluatorClass = (Class<? extends IEvaluator>) Class.forName(evaluatorClassname);
			evaluator = evaluatorClass.newInstance();
			algorithm.setEvaluator(evaluator);
		}
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal evaluator classname");
		} 

		/****************************** PROVIDER **************************************/

		try
		{
			Class<? extends IProvider> providerClass = (Class<? extends IProvider>) Class.forName(providerClassname);
			IProvider provider = providerClass.newInstance();
			algorithm.setProvider(provider);
		}
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal provider classname");
		} 

		/****************************** SELECTOR **************************************/

		try
		{
			Class<? extends ISelector> parentsSelectorClass = (Class<? extends ISelector>) Class.forName(parentsSelectorClassname);
			parentsSelector = parentsSelectorClass.newInstance();
		}
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal selector classname");
		} 
		
		/****************************** RECOMBINATOR **********************************/

		if(recombinatorClassname != null)
			try
			{
				Class<? extends IRecombinator> recombinatorClass =(Class<? extends IRecombinator>) Class.forName(recombinatorClassname);
				recombinator = recombinatorClass.newInstance();
			}
			catch (ClassNotFoundException e) {
				throw new ConfigurationRuntimeException("Illegal recombinator classname");
			} 

		/****************************** MUTATOR ***************************************/

		if(mutatorClassname != null)
			try
			{
				Class<? extends IMutator> mutatorClass =(Class<? extends IMutator>) Class.forName(mutatorClassname);
				mutator = mutatorClass.newInstance();
			}
			catch (ClassNotFoundException e) {
				throw new ConfigurationRuntimeException("Illegal mutator classname");
			} 
	}
	
	/**
	 * Prints a description of the classifier.
	 *
	 * @return a description of the classifier as a string
	 */
	public String toString()
	{
		return algorithm.getClassifier().toString(metadata);
	}
	
	public abstract void buildClassifier(Instances instances) throws Exception;

	public abstract Capabilities getCapabilities();

	public abstract double classifyInstance(Instance ins);

	public abstract TechnicalInformation getTechnicalInformation();
}