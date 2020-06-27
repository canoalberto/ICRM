package net.sf.jclec.problem.classification.icrm;

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractMutator;
import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.problem.classification.blocks.And;
import net.sf.jclec.problem.classification.blocks.AttributeValue;
import net.sf.jclec.problem.classification.icrm.ga.ICRMGAAlgorithm;
import net.sf.jclec.problem.classification.rule.crisp.CrispRule;

/**
 * ICRM: An Interpretable Classification Rule Mining algorithm
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 */

public class ICRMMutator extends AbstractMutator
{
	private static final long serialVersionUID = 2746960613736624663L;

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Individual species */
	
	protected transient ICRMExprTreeSpecies species;
	
	/** Individual schema */
	
	protected transient ICRMExprTreeSchema schema;
	
	/** Parent Algorithm */

	private ICRMAlgorithm parentAlgorithm;
	
	/** Class to classify */

	private int Class;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 */
	
	public ICRMMutator(ICRMAlgorithm parentAlgorithm) 
	{
		super();
		this.parentAlgorithm = parentAlgorithm;
		contextualize(parentAlgorithm);
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	// AbstractMutator methods


	@Override
	protected void prepareMutation() 
	{
		ISpecies spc = context.getSpecies();
		if (spc instanceof ICRMExprTreeSpecies) {
			//  Assign species
			this.species = (ICRMExprTreeSpecies) spc;
			// Sets genotype schema
			this.schema = (ICRMExprTreeSchema) ((ICRMExprTreeSpecies) spc).getGenotypeSchema();
		}
		else {
			throw new IllegalStateException("Illegal species in context");
		}
	}
	
	public List<IIndividual> mutate(int Class, List<IIndividual> parents) 
	{
		// Sets p list to actual parents
		parentsBuffer = parents;
		
		this.Class = Class;
		
		// Prepare mutation process
		prepareMutation();
		// Create a new list to put sons in it
		sonsBuffer = new ArrayList<IIndividual>();
    	// For all individuals in "parents" ...
		int maxIter = parents.size();
    	for (parentsCounter = 0; parentsCounter < maxIter; parentsCounter++)
    	{
			mutateNext();
    	}
		// Returns sons list
		return sonsBuffer;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void mutateNext()
	{
		ICRMIndividual p0 = (ICRMIndividual) parentsBuffer.get(parentsCounter);
		
		if(p0.beatable == false)
		{
			sonsBuffer.add(null);
			return;
		}
		
		ExprTree p0_genotype = p0.getGenotype();
		
		List<Integer> attributeList = new ArrayList<Integer>();
		
		for(int i = 0; i < p0_genotype.size(); i++)
		{
			if(p0_genotype.getBlock(i) instanceof AttributeValue)
			{
				attributeList.add(((AttributeValue) p0_genotype.getBlock(i)).getAttributeIndex());
			}
		}
		
		if(attributeList.size() == parentAlgorithm.getNumberAttributes())
		{
			sonsBuffer.add(null);
			return;
		}
		
		List<IIndividual>[] rules = new ArrayList[parentAlgorithm.getNumberAttributes()];
		
		for(int i = 0; i < parentAlgorithm.getNumberAttributes(); i++)
		{
			rules[i] = new ArrayList<IIndividual>();
			
			if(!attributeList.contains(i))
				rules[i] = parentAlgorithm.getInitialRules(i);
		}
		
		ICRMGAAlgorithm gaAlgorithm = new ICRMGAAlgorithm(parentAlgorithm, Class, rules, p0.getDataset());
		
		gaAlgorithm.execute();
		
		ExprTree p1_genotype = null;
		double bestFitness = -1.0;
		
		for(int i = 0; i < parentAlgorithm.getNumberAttributes(); i++)
		{
			if(!attributeList.contains(i))
			{
				IIndividual ind = gaAlgorithm.getInhabitants(i).get(0);
				
				if(((SimpleValueFitness) ind.getFitness()).getValue() > bestFitness)
				{
					p1_genotype = ((ICRMIndividual) gaAlgorithm.getInhabitants(i).get(0)).getGenotype();
					bestFitness = ((SimpleValueFitness) ind.getFitness()).getValue();
				}
			}
		}
		
		ExprTree son_genotype = new ExprTree();
		
		son_genotype.addBlock(new And());
		
		for(int i = 0; i < p0_genotype.size(); i++)
			son_genotype.addBlock(p0_genotype.getBlock(i).copy());
		
		for(int i = 0; i < p1_genotype.size(); i++)
			son_genotype.addBlock(p1_genotype.getBlock(i).copy());
		
		CrispRule son_phenotype = new CrispRule(son_genotype);
		son_phenotype.setConsequent(p0.getPhenotype().getConsequent());
		
		// Add son
		sonsBuffer.add(new ICRMIndividual(son_genotype, son_phenotype, null, p0.getDataset().copy()));
	}
}