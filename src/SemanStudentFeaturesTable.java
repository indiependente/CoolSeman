import java.util.ArrayList;
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
		registerFeatures();
	}
	
	/**
	 * Registers all and only the features of the class owner.
	 */
	private void registerFeatures()
	{
		for (Enumeration e = owner.getFeatures().getElements(); e.hasMoreElements(); )
		{
			Feature f = (Feature) e.nextElement();
			featuresList.put( f.getFeatureName(), f );
		}
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
	
	
	
	/*		TEST		*/
	
}
