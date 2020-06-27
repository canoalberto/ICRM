package net.sf.jclec.problem.classification.icrm;

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.exprtree.IPrimitive;
import net.sf.jclec.problem.classification.blocks.And;
import net.sf.jclec.problem.classification.blocks.Equal;
import net.sf.jclec.problem.classification.blocks.GreaterOrEqual;
import net.sf.jclec.problem.classification.blocks.LessOrEqual;
import net.sf.jclec.problem.classification.blocks.NotEqual;
import net.sf.jclec.problem.classification.exprtree.ExprTreeSpecies;
import net.sf.jclec.problem.classification.rule.crisp.CrispRule;
import net.sf.jclec.problem.util.dataset.attribute.IAttribute;

/**
 * ICRM: An Interpretable Classification Rule Mining algorithm
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 */

public class ICRMExprTreeSpecies extends ExprTreeSpecies
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = -2486155613178874903L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/** 
	 * Empty constructor 
	 * 
	 * */
	
	public ICRMExprTreeSpecies(){
		
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////
	
	@Override
	public void setSymbols()
	{
		// List of inputs Attributes
		List<IAttribute> inputAttributes = new ArrayList<IAttribute>();

		// Obtains the input attributes
		for(int i = 0; i < metadata.numberOfAttributes(); i++)
			inputAttributes.add(metadata.getAttribute(i));

		// Allocate memory fot genotype Schema
		genotypeSchema = new ICRMExprTreeSchema();
		
		List<IPrimitive> terminals = setTerminalSymbols(inputAttributes);
		List<IPrimitive> functions = setFunctionSymbols();
		
		setTerminalNodes(terminals);
		setNonTerminalNodes(functions);	
	}
	
	/**
	 * Establishes the terminal symbols
	 * 
	 * @param inputAttributes
	 * @return list of terminal symbols
	 */
	protected List<IPrimitive> setTerminalSymbols(List<IAttribute> inputAttributes)
	{
		List<IPrimitive> terminals = super.setTerminalSymbols(inputAttributes);
		
		if(existNumericalAttributes){
			terminals.add(new GreaterOrEqual());
			terminals.add(new LessOrEqual());
		}
		if(existCategoricalAttributes){
			terminals.add(new Equal());
			terminals.add(new NotEqual());
		}
		
		return terminals;
	}
	
	/**
	 * Establishes the function nodes
	 * 
	 * @return list of function nodes
	 */
	
	protected List<IPrimitive> setFunctionSymbols()
	{
		List<IPrimitive> functions = new ArrayList<IPrimitive>();
		
		functions.add(new And());

		return functions;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public ICRMIndividual createIndividual(ExprTree genotype)
	{
		return new ICRMIndividual(genotype, new CrispRule(genotype));
	}
}