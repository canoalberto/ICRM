package net.sf.jclec.problem.classification.icrm.ga;

import net.sf.jclec.IIndividual;
import net.sf.jclec.IPopulation;
import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractMutator;
import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.problem.classification.blocks.RandomConstantOfContinuousValues;
import net.sf.jclec.problem.classification.blocks.RandomConstantOfDiscreteValues;
import net.sf.jclec.problem.classification.icrm.ICRMExprTreeSchema;
import net.sf.jclec.problem.classification.icrm.ICRMExprTreeSpecies;
import net.sf.jclec.problem.classification.icrm.ICRMIndividual;

/**
 * ICRM: An Interpretable Classification Rule Mining algorithm
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 */

public class ICRMGAMutator extends AbstractMutator
{
	private static final long serialVersionUID = 2746960613736624663L;

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Individual species */
	
	protected transient ICRMExprTreeSpecies species;
	
	/** Individual schema */
	
	protected transient ICRMExprTreeSchema schema;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 */
	
	public ICRMGAMutator(IPopulation context) 
	{
		super();
		contextualize(context);
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

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
	
	@Override
	protected void mutateNext() 
	{
		IIndividual ind = parentsBuffer.get(parentsCounter);
		// Parents genotypes
		ExprTree parent_genotype = ((ICRMIndividual) ind).getGenotype();
		
		ExprTree s0_genotype = parent_genotype.copy();
		ExprTree s1_genotype = parent_genotype.copy();
		
		if(parent_genotype.getBlock(2) instanceof RandomConstantOfContinuousValues)
		{
			net.sf.jclec.util.range.Interval interval = ((RandomConstantOfContinuousValues) parent_genotype.getBlock(2)).getInterval();
			double value = ((RandomConstantOfContinuousValues) parent_genotype.getBlock(2)).getValue();
			double range = (interval.getRight() - interval.getLeft()) / 10.0;
			
			double rand = randgen.uniform(0, range);
			
			((RandomConstantOfContinuousValues) s0_genotype.getBlock(2)).setValue(Math.min(value + rand,interval.getRight()));
			((RandomConstantOfContinuousValues) s1_genotype.getBlock(2)).setValue(Math.max(value - rand,interval.getLeft()));
		}
		else if(parent_genotype.getBlock(2) instanceof RandomConstantOfDiscreteValues)
		{
			net.sf.jclec.util.intset.Interval interval = ((RandomConstantOfDiscreteValues) parent_genotype.getBlock(2)).getInterval();
			int value = (int) ((RandomConstantOfDiscreteValues) parent_genotype.getBlock(2)).getValue();
			Double range = new Double((interval.getRight() - interval.getLeft()) / 10.0);
			
			int rand = randgen.choose(1, range.intValue()+1);
			
			((RandomConstantOfDiscreteValues) s0_genotype.getBlock(2)).setValue(Math.min(value + rand,interval.getRight()));
			((RandomConstantOfDiscreteValues) s1_genotype.getBlock(2)).setValue(Math.max(value - rand,interval.getLeft()));
		}
		
		// Add sons
		sonsBuffer.add(species.createIndividual(s0_genotype));
		sonsBuffer.add(species.createIndividual(s1_genotype));
	}
}