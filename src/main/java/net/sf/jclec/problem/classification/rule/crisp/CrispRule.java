package net.sf.jclec.problem.classification.rule.crisp;

import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.problem.classification.blocks.And;
import net.sf.jclec.problem.classification.blocks.AttributeValue;
import net.sf.jclec.problem.classification.blocks.In;
import net.sf.jclec.problem.classification.blocks.Not;
import net.sf.jclec.problem.classification.blocks.Or;
import net.sf.jclec.problem.classification.blocks.Out;
import net.sf.jclec.problem.classification.blocks.RandomConstantOfContinuousValues;
import net.sf.jclec.problem.classification.classic.ClassicClassificationMetadata;
import net.sf.jclec.problem.classification.rule.Rule;
import net.sf.jclec.problem.util.dataset.IMetadata;
import net.sf.jclec.problem.util.dataset.attribute.NumericalAttribute;

/**
 * Crisp rule for classification
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 * @author Jose M. Luna 
 * @author Juan Luis Olmo
 */

public class CrispRule extends Rule
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */

	private static final long serialVersionUID = -8174242256644010121L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */

	public CrispRule()
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param antecedent the antecedent of the rule
	 */

	public CrispRule(ExprTree antecedent)
	{
		super(antecedent);
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Implementation of copy()
	 * 
	 * {@inheritDoc}
	 */

	@Override
	public CrispRule copy()
	{
		CrispRule newRule = new CrispRule();

		newRule.setAntecedent(code.copy());
		newRule.setConsequent(consequent);
		if(fitness != null) newRule.setFitness(fitness.copy());

		return newRule;
	}

	/**
	 * Obtain the number of conditions of the rule
	 * 
	 * @return number of conditions
	 */

	public int getConditions()
	{
		int count = 1;

		for(int j=0; j<code.size(); j++)
		{
			if(code.getBlock(j) instanceof And ||
					code.getBlock(j) instanceof Or  )
				count++;

			if(code.getBlock(j) instanceof Not)
			{
				int nots = 1;

				for(int k = j+1; k < code.size() && code.getBlock(k) instanceof Not; k++)
					nots++;

				if(nots % 2 != 0)
					count++;

				j+=nots-1;
			}
		}

		return count;
	}

	/** 
	 *  Shows the complete rule antecedent and consequent
	 *  
	 *  @param metadata the metadata
	 *  @return the rule
	 */

	public String toString(IMetadata metadata)
	{
		StringBuffer sb = new StringBuffer("IF (");

		for (int j=0; j<code.size(); j++) 
		{
			if(code.getBlock(j) instanceof AttributeValue)
			{
				sb.append(metadata.getAttribute(Integer.parseInt(code.getBlock(j).toString())).getName() + " ");

				double value = Double.valueOf(code.getBlock(j+1).toString());

				if(code.getBlock(j-1) instanceof In || code.getBlock(j-1) instanceof Out)
				{
					sb.append(((NumericalAttribute) metadata.getAttribute(Integer.parseInt(code.getBlock(j).toString()))).show(value,"0.00000") + " ");
					value = Double.valueOf(code.getBlock(j+2).toString());
					sb.append(((NumericalAttribute) metadata.getAttribute(Integer.parseInt(code.getBlock(j).toString()))).show(value,"0.00000") + " ");
					j+=2;
				}
				else
				{	
					if(code.getBlock(j+1) instanceof RandomConstantOfContinuousValues)
						sb.append(((NumericalAttribute) metadata.getAttribute(Integer.parseInt(code.getBlock(j).toString()))).show(value,"0.00000") + " ");
					else
						sb.append(metadata.getAttribute(Integer.parseInt(code.getBlock(j).toString())).show(value) + " ");
					j++;
				}
			}
			else
			{
				if(code.getBlock(j) instanceof Not)
				{
					int count = 1;

					for(int k = j+1; k < code.size() && code.getBlock(k) instanceof Not; k++)
						count++;

					if(count % 2 != 0)
						sb.append(code.getBlock(j).toString() + " ");

					j+=count-1;
				}
				else
					sb.append(code.getBlock(j).toString() + " ");
			}
		}

		sb.append(") THEN ("+ ((ClassicClassificationMetadata) metadata).getClassAttribute().getName() + " = " +
				((ClassicClassificationMetadata) metadata).getClassAttribute().show(consequent)+")");

		return sb.toString();
	}
}