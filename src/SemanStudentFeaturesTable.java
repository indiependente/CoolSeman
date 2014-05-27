import java.util.Enumeration;
import java.util.HashMap;

/**
 * 	This class is the implementation of the Objects, Methods Table.
 * 	It stores the attributes and the methods that belong to a Class.
 * 	Methods and attributes are mapped into an HashMap < AbstractSymbol , Feature >.
 *
 */
class FeaturesTable
{
	/**
	 * The map that stores attributes and methods.
	 */
	private HashMap< AbstractSymbol , Feature > featuresList;
	
	/**
	 * The class that owns the features stored in the featureList
	 */
	private Class_ owner;
	
	/**
	 * Constructor of the FeaturesTable
	 * @param c	The class that owns the features
	 */
	public FeaturesTable(Class_ c)
	{
		this.owner = c;
		featuresList = new HashMap< AbstractSymbol , Feature >();
	}
	
	/**
	 * Registers an attributes of the class owner.
	 * @param a The Feature to be registered.
	 */
	public void registerAttr(attr a)
	{
		/*	Check if the attribute is already defined	*/
		if (featuresList.containsKey(a.getFeatureName()))
			SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(), "Attribute is multiply defined.");
		
		if (a.getReturnType().equals(AbstractTable.idtable.addString("SELF_TYPE")))  
			TypeCheckerHelper.validateType(SemantState.getInstance().getCurrentClass().getName());
		else
			TypeCheckerHelper.validateType(a.getReturnType());

		featuresList.put( a.getFeatureName(), a );
	}
	
	/**
	 * Registers a method of the class owner.
	 * @param m The Feature to be registered.
	 */
	public void registerMethod(method m)
	{
		/*	Check if the method is already defined	*/
		if (featuresList.containsKey(m.getFeatureName()))
			SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(), "Method is multiply defined.");
		
		/*	Method return type checking	*/
		if (m.getReturnType().equals(AbstractTable.idtable.addString("SELF_TYPE")))  
			TypeCheckerHelper.validateType(SemantState.getInstance().getCurrentClass().getName());
		else
			TypeCheckerHelper.validateType(m.getReturnType());
		
		/*	Formals type checking	*/
		for (Enumeration e = m.getFormals().getElements(); e.hasMoreElements(); )
		{
			TypeCheckerHelper.validateType( ((Formal)e.nextElement()).getTypeDecl() );	//this cast should be safe
		}
		
		featuresList.put( m.getFeatureName(), m );
	}
	
	/**
	 * Checks if the AbstractSymbol sym that represents the Feature,
	 * belongs to the owner class' attributes or its ancestors.
	 * @param sym	The feature to be checked
	 * @return	The attribute node associated to the sym parameter.
	 */
	public attr lookupAttr(AbstractSymbol sym)
	{
		if ( featuresList.containsKey(sym) )
		{
			if(featuresList.get(sym) instanceof attr)
				return (attr) featuresList.get(sym);
			else return null;
		}

		else
			if (!owner.getName().equals("Object"))
				return ClassTable.getInstance().lookup(owner.getParent()).getFeaturesTable().lookupAttr(sym);
			else
				return null;

	}
	
	/**
	 * Checks if the AbstractSymbol sym that represents the Feature,
	 * belongs to the owner class' methods or its ancestors.
	 * @param sym	The feature to be checked
	 * @return	The method node associated to the sym parameter.
	 */
	public method lookupMethod(AbstractSymbol sym)
	{
		if ( featuresList.containsKey(sym) )
		{

			if(featuresList.get(sym) instanceof method)
				return (method) featuresList.get(sym);
			else return null;
		}

		else
			if (!owner.getName().equals("Object"))
				return ClassTable.getInstance().lookup(owner.getParent()).getFeaturesTable().lookupMethod(sym);
			else
				return null;
	}
	
	public boolean isAttributeRegistered(AbstractSymbol sym)
	{
		return lookupAttr(sym) != null;
	}
	
	public boolean isMethodRegistered(AbstractSymbol sym)
	{
		return lookupMethod(sym) != null;
	}
}
