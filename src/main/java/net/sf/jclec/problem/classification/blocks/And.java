package net.sf.jclec.problem.classification.blocks;

import net.sf.jclec.exprtree.fun.AbstractPrimitive;
import net.sf.jclec.exprtree.fun.ExprTreeFunction;
import net.sf.jclec.exprtree.IPrimitive;

/**
 * Primitive implementation
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 * @author Jose M. Luna 
 * @author Juan Luis Olmo
 */

public class And extends AbstractPrimitive
{
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Argument types
	/////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = -6298104216422801606L;
	
	/** Argument types */
	
	private static final Class<?>[] ARG_TYPES = new Class<?> [] {Boolean.class, Boolean.class};

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty (default) constructor
	 */
	
	public And()
	{
		super(ARG_TYPES,Boolean.class);
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Execute this operation over the stack and context
	 * 
	 * @param context the ExprTreeFunction context
	 */

	public void evaluate(ExprTreeFunction context) 
	{
		// Lower interval extremum
		boolean arg0 = (Boolean) super.pop(context);
		// Upper interval extremum
		boolean arg1 = (Boolean) super.pop(context);
		// Operation to perform
		
		super.push(context, arg0 && arg1);
	}
	
	/**
	 * Default implementation of copy() return this.
	 * 
	 * {@inheritDoc}
	 */
	
	public IPrimitive copy() 
	{
		return this;
	}

	/**
	 * Default implementation of instance() return this.
	 * 
	 * {@inheritDoc}
	 */
	
	public IPrimitive instance() 
	{
		return this;
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------- Overwriting java.lang.Object methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare two objects
	 * 
	 * @param other object to compare
	 * 
	 * @return result of the comparison
	 */
	
	public boolean equals(Object other)
	{
		return other instanceof And;
	}	

	/**
	 * Shows this operation identification
	 * 
	 * @return AND
	 */
	
	public String toString()
	{
		return "AND";
	}
}