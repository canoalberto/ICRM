package net.sf.jclec.problem.classification.icrm.fitting;

import net.sf.jclec.IIndividual;
import net.sf.jclec.IPopulation;
import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractMutator;
import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.problem.classification.blocks.RandomConstantOfContinuousValues;
import net.sf.jclec.problem.classification.blocks.RandomConstantOfDiscreteValues;
import net.sf.jclec.problem.classification.exprtree.MultiExprTreeRuleIndividual;
import net.sf.jclec.problem.classification.icrm.ICRMExprTreeSchema;
import net.sf.jclec.problem.classification.icrm.ICRMExprTreeSpecies;
import net.sf.jclec.problem.classification.rule.Rule;
import net.sf.jclec.problem.classification.rule.crisp.CrispRule;

/**
 * ICRM: An Interpretable Classification Rule Mining algorithm
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 */

public class ICRMFittingMutator extends AbstractMutator
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
	
	public ICRMFittingMutator(IPopulation context) 
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
		
		MultiExprTreeRuleIndividual parent_classifier = ((MultiExprTreeRuleIndividual) ind);
		
		for(int j = 0; j < parent_classifier.getPhenotype().getClassificationRules().size(); j++)
		{
			// Parents genotypes
			ExprTree parent_genotype = ((Rule) (parent_classifier.getPhenotype().getClassificationRule(j))).getAntecedent();
			
			ExprTree s0_genotype = parent_genotype.copy();
			
			for(int i = parent_genotype.size()-1; i >= 2; i-=3)
			{
				if(parent_genotype.getBlock(i) instanceof RandomConstantOfContinuousValues)
				{
					net.sf.jclec.util.range.Interval interval = ((RandomConstantOfContinuousValues) parent_genotype.getBlock(i)).getInterval();
					double value = ((RandomConstantOfContinuousValues) parent_genotype.getBlock(i)).getValue();
					double range = (interval.getRight() - interval.getLeft()) / 10.0;
					
					value += randgen.uniform(-range, range);
					
					((RandomConstantOfContinuousValues) s0_genotype.getBlock(i)).setValue(Math.max(Math.min(value,interval.getRight()), interval.getLeft()));
				}
				else if(parent_genotype.getBlock(i) instanceof RandomConstantOfDiscreteValues)
				{
					net.sf.jclec.util.intset.Interval interval = ((RandomConstantOfDiscreteValues) parent_genotype.getBlock(i)).getInterval();
					int value = (int) ((RandomConstantOfDiscreteValues) parent_genotype.getBlock(i)).getValue();
					Double range = new Double((interval.getRight() - interval.getLeft()) / 10.0);
					
					value += randgen.choose(-range.intValue(), range.intValue()+1);
					
					((RandomConstantOfDiscreteValues) s0_genotype.getBlock(i)).setValue(Math.max(Math.min(value,interval.getRight()), interval.getLeft()));
				}
			}
			
			CrispRule s0_newrule = new CrispRule();
			s0_newrule.setAntecedent(s0_genotype);
			s0_newrule.setConsequent(parent_classifier.getPhenotype().getClassificationRule(j).getConsequent());
			
			MultiExprTreeRuleIndividual s0_classifier = ((MultiExprTreeRuleIndividual) ind).copy();
			
			s0_classifier.getPhenotype().setClassificationRule(j,s0_newrule);
			
			sonsBuffer.add(s0_classifier);
		}
	}
}