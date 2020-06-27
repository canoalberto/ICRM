package net.sf.jclec.problem.classification.icrm;

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractCreator;
import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.problem.util.dataset.attribute.AttributeType;

/**
 * ICRM: An Interpretable Classification Rule Mining algorithm
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 */

public class ICRMExprTreeCreator extends AbstractCreator 
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */
	
	private static final long serialVersionUID = 4365866784680115536L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Internal variables
	/////////////////////////////////////////////////////////////////

	/** Individual species */
	
	protected transient ICRMExprTreeSpecies species;
	
	/** Individuals schema */
	
	protected transient ICRMExprTreeSchema schema;
	
	/** Attribute index */
	
	protected int attribute;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty (default) constructor.
	 */
	
	public ICRMExprTreeCreator() 
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
	
	// java.lang.Object methods
	
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ICRMExprTreeCreator){
			return true;
		}
		else {
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------- Overwriting AbstractCreator methods
	/////////////////////////////////////////////////////////////////

	// AbstractCreator methods

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public List<IIndividual> provide(int attribute) 
	{
		this.attribute = attribute;
		this.numberOfIndividuals = 10;
		// Result list
		createdBuffer = new ArrayList<IIndividual> ();
		// Prepare process
		prepareCreation();
		
		if(species.getMetadata().getAttribute(attribute).getType() == AttributeType.Categorical)
			this.numberOfIndividuals = 2;
		
		// Provide individuals
		for (createdCounter=1; createdCounter<numberOfIndividuals; createdCounter++) {
			createNext();
		}
		// Returns result
		return createdBuffer;
	}
	
	@Override
	protected void prepareCreation() 
	{
		ISpecies spc = context.getSpecies();
		if (spc instanceof ICRMExprTreeSpecies) {
			// Type conversion 
			this.species = (ICRMExprTreeSpecies) spc;
			// Sets genotype schema
			this.schema = (ICRMExprTreeSchema) ((ICRMExprTreeSpecies) spc).getGenotypeSchema();
		}
		else {
			throw new IllegalStateException("Illegal species in context");
		}
	}

	@Override
	protected void createNext() 
	{
		// Create Expression tree
		List<ExprTree> genotypes = schema.createExprTree(attribute, createdCounter, species, randgen);
		
		for(ExprTree genotype : genotypes)
		{
			// Put new son in created buffer
			createdBuffer.add(species.createIndividual(genotype));
		}
	}
}