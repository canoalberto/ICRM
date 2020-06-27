package net.sf.jclec.problem.classification.syntaxtree;

import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.IFitness;
import net.sf.jclec.problem.classification.IClassifierIndividual;
import net.sf.jclec.problem.classification.rule.Rule;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.syntaxtree.SyntaxTreeIndividual;

/**
 * Individual representation for SyntaxTree classification rules
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 * @author Jose M. Luna 
 * @author Juan Luis Olmo
 */

public class SyntaxTreeRuleIndividual extends SyntaxTreeIndividual implements IClassifierIndividual 
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	private static final long serialVersionUID = -4704573280824780256L;
	
	/** Individual phenotype */
	
	protected Rule phenotype;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Default (empty) constructor
	 */
	
	public SyntaxTreeRuleIndividual() 
	{
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param genotype the genotype of the individual
	 */
	
	public SyntaxTreeRuleIndividual(SyntaxTree genotype)
	{
		super(genotype);
	}
	
	/**
	 * Constructor
	 * 
	 * @param genotype the genotype of the individual
	 * @param phenotype the phenotype of the individual
	 */
	
	public SyntaxTreeRuleIndividual(SyntaxTree genotype, Rule phenotype)
	{
		this(genotype);
		this.phenotype = phenotype;
	}
	
	/**
	 * Constructor
	 * @param genotype the genotype of the individual
	 * @param phenotype the phenotype of the individual
	 * @param fitness the fitness of the individual
	 */

	public SyntaxTreeRuleIndividual(SyntaxTree genotype, Rule phenotype, IFitness fitness) 
	{
		this(genotype,phenotype);
		setFitness(fitness);
	}
	
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Access to individual phenotype
	 * 
	 * @return A rule contained in this individual
	 */

	public Rule getPhenotype() 
	{
		return phenotype;
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public boolean equals(Object other) 
	{
		if (other instanceof SyntaxTreeIndividual) {
			SyntaxTreeRuleIndividual cother = (SyntaxTreeRuleIndividual) other;
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(genotype, cother.getGenotype());
			eb.append(phenotype, cother.getPhenotype());
			return eb.isEquals();
		}
		else {
			return false;
		}
	}
	
	/**
	 * Copy the rule
	 * 
	 * @return rule copy
	 */
	
	@Override
	public SyntaxTreeRuleIndividual copy() 
	{
		if(this.getFitness() != null)
			return new SyntaxTreeRuleIndividual(genotype.copy(), phenotype.copy(), fitness.copy());
		else
			return new SyntaxTreeRuleIndividual(genotype.copy(), phenotype.copy());
	}
}