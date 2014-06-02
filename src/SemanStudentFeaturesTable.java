import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * GIVE ME YOUR EYES:
 * 
 * I SEE SOME PROBLEM WITH ACTUAL DESIGN OF THIS CLASS, MANY METHODS MAY BE CALLED
 * FROM ANOTHER CLASS, HENCE THEY MUST BE STATIC OR YOU SHOULD PROVIDE STATIC METHOD 
 * FOR GENERAL PURPOSE CALLS, FOR INSTANCE THE VALIDATION OF EITHER A STATIC OR A SIMPLE
 * DISPATCH MAY BE ASKED FROM A DIFFERENT CLASS. ANYWAY IT'S EASY TO FIX THIS DESIGN ISSUE.
 * 
 *  MOREOVER I WONDER WHY I AM WRITING THIS MESSAGE IN ENGLISH
 * 
 */

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
	
	public HashMap<AbstractSymbol, Feature> getFeaturesList() {
		return featuresList;
	}

	public Class_ getOwner() {
		return owner;
	}
	
	/**
	 * Registers an attributes of the class owner.
	 * @param a The Feature to be registered.
	 */
	public void registerAttr(attr a)
	{
		/*	Check if the attribute is already defined	*/
		if (featuresList.containsKey(a.getFeatureName()))
		{
			SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(), "Attribute %s is multiply defined.", 
					a.getFeatureName());
			return;
		}
		
		/*	Check if the attribute is already defined in an ancestor class	*/
		if (lookupAttr(a.getFeatureName()) != null)
		{
			SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(), "Attribute %s is an attribute of an inherited class.", 
					a.getFeatureName());
			return;
		}
		
		try {
			TypeCheckerHelper.validateType(TypeCheckerHelper.inferSelfType(a.getReturnType()));
		} catch (SemanticException e) {
			SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(), "Class %s of attribute %s is undefined.", 
					a.getReturnType(), a.getFeatureName());
			return;
		}

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
		{
			SemantErrorsManager.getInstance().semantError(SemantState.getInstance().getCurrentClass(),
					"Method %s is multiply defined.", m.getFeatureName());
			return;
		}

		/*	Check for overriding: 
		 * 	If m overrides a method x,
		 * 	it must have the same formals and return type as x	*/
		method ancestorMeth = lookupMethod(m.getFeatureName());
		if (ancestorMeth != null)	/*	so you're overriding it uh	*/
		{	
			/*	redefined method does not match original return type	*/
			if (!ancestorMeth.getReturnType().getString().equals(m.getReturnType().getString()))
			{	
				SemantErrorsManager.getInstance()
				.semantError(SemantState.getInstance().getCurrentClass(),
						"In redefined method "+ m.getName().getString() 
						+ ", return type "+ m.getReturnType().getString()
						+ " is different from original return type "+ ancestorMeth.getReturnType().getString() +" .");
				return;
			}
			if (!validateFormals(m, ancestorMeth))
				return;
		}	/*	End of overriding checks	*/

		/*	Method return type checking	*/
		try {
			TypeCheckerHelper.validateType(TypeCheckerHelper.inferSelfType(m.getReturnType()));
		} catch (SemanticException e) {
			SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(),
					"Undefined return type %s in method %s.",
					m.getReturnType(), m.getName());
			return;
		}
		
		/*	Formals type checking	*/
		for (Enumeration e = m.getFormals().getElements(); e.hasMoreElements(); )
		{
			Formal f = (Formal) e.nextElement();
			AbstractSymbol as = f.getTypeDecl();
			/*	Declared type for Formal is SELF_TYPE*/
			if (as.getString().equals("SELF_TYPE"))
			{
				SemantErrorsManager.getInstance()
				.semantError(SemantState.getInstance().getCurrentClass(),
						"Formal parameter %s cannot have type SELF_TYPE.",
						f.getName(), f.getTypeDecl());
				return;
			}
			try {
				TypeCheckerHelper.validateType( f.getTypeDecl() );
			} catch (SemanticException exc) {
				/*	Undefined class for parameter type*/
				SemantErrorsManager.getInstance()
				.semantError(SemantState.getInstance().getCurrentClass(),
						"Class %s of formal parameter %s is undefined.",
						f.getTypeDecl(), f.getName());
				return;
			}
			
			
		}

		featuresList.put( m.getFeatureName(), m );
	}

	/**	Check for formals types.
	 * @param m
	 * @param ancestorMeth
	 * @return True if both methods have got the same formals, false otherwise.
	 */
	private boolean validateFormals(method m, method ancestorMeth) {
		Enumeration mForm = m.getFormals().getElements();
		Enumeration ancestorMethForm = ancestorMeth.getFormals().getElements();

		while(mForm.hasMoreElements() && ancestorMethForm.hasMoreElements())
		{
			Formal ancestorParam = (Formal) ancestorMethForm.nextElement();
			Formal mParam = (Formal) mForm.nextElement();

			/*	redefined method param does not match original param's type	*/
			if(!mParam.getTypeDecl().equals(ancestorParam.getTypeDecl()))
			{
				SemantErrorsManager.getInstance()
				.semantError(SemantState.getInstance().getCurrentClass(),
						"In redefined method "+m.getName().getString()
						+ ", parameter type "+mParam.getTypeDecl().getString()
						+ " is different from original type "
						+ ancestorParam.getTypeDecl().getString());	
				return false;
			}
		}

		/*	If method m has more or less parameters than his ancestor's method	*/
		if(mForm.hasMoreElements() || ancestorMethForm.hasMoreElements() )
		{	SemantErrorsManager.getInstance()
			.semantError(SemantState.getInstance().getCurrentClass(),
					"Incompatible number of formal parameters in redefined method "+
							m.getName().getString() + ".");
			return false;
		}
		return true;
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
			if (!owner.getName().getString().equals("Object"))
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
			if (!owner.getName().getString().equals("Object"))
			{
				return ClassTable.getInstance().lookup(owner.getParent())
						.getFeaturesTable()
						.lookupMethod(sym);
			}
			else
				return null;
	}
	
	
	/**
	 * Checks if the AbstractSymbol sym that represents the Feature,
	 * belongs to the owner class' methods.
	 * @param sym	The feature to be checked
	 * @return	The method node associated to the sym parameter.
	 */
	public method lookupStaticMethod(AbstractSymbol sym)
	{
		if(featuresList.containsKey(sym))
		{
			return (method) featuresList.get(sym);
		}
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
	
	public boolean isStaticMethodRegistered(AbstractSymbol sym)
	{
		return lookupStaticMethod(sym) != null;
	}
	
	
	/**
	 * This method checks if a dispatch node respects the signature of the method
	 * @param d the dispatch node to validate
	 * @return true if the dispatch is correct, else false
	 */
	public boolean validateDispatch(dispatch d){
		AbstractSymbol dName = d.getName();
		method meth = lookupMethod(dName);
		
		if(meth == null)
			return false;
		
		Expressions actuals = d.getActual();
		Formals formals = meth.getFormals();
		
		Enumeration eForm = formals.getElements();
		Enumeration eAct = actuals.getElements();
				
		return validateActualsFormals(eForm, eAct);
	}
	
	/**
	 * Creates as many scopes as the sym's ancestors are, from Object to sym's father.
	 * Adds the features for every ancestor to his scope.
	 * Creates the sym's scope.
	 * Adds his features.
	 * @param sym Class name
	 * @return The number of scopes pushed in the SymbolTable's stack.
	 */
	public int loadClassScope(AbstractSymbol sym)
	{
		ClassTable cTbl = ClassTable.getInstance();
		ArrayList<AbstractSymbol> ancestors = cTbl.getParents(sym);
		
		int numLevels = 0;
		SymbolTable symTab = SemantState.getInstance().getScopeManager();
		/*	Push the scope of sym's ancestors	*/
		for (int i = ancestors.size()-1; i >= 0; i--)
		{
			numLevels++;
			addClassFeaturesToLocalScope(ancestors.get(i), cTbl, symTab);
		}
		
		/*	Now it's my turn :D	*/
		symTab.enterScope();
		numLevels++;
		addClassFeaturesToLocalScope(sym, cTbl, symTab);
		
		return numLevels;
	}

	/**
	 * @param cTbl
	 * @param ancestors
	 * @param symTab
	 * @param i
	 */
	private void addClassFeaturesToLocalScope(AbstractSymbol sym, ClassTable cTbl, SymbolTable symTab) {
		symTab.enterScope();
		Class_ ancClazz = cTbl.lookup(sym);
		for (Feature f : ancClazz.getFeaturesTable().getFeaturesList().values())
		{
			
			System.out.println("Return type: "+TypeCheckerHelper.inferSelfType(f.getReturnType()));
			Class_ featureClazz = cTbl.lookup(TypeCheckerHelper.inferSelfType(f.getReturnType()));
			
			symTab.addId(f.getFeatureName(), featureClazz);
		}
	}
	
	/**
	 * Creates the new scope for the method.
	 * Loads the formals' return type classes to the scope.
	 * @param sym Method name
	 */
	public void loadMethodScope(AbstractSymbol sym)
	{
		method meth = this.lookupMethod(sym);
		if (meth == null) return; // it should never be null

		SymbolTable symTab = SemantState.getInstance().getScopeManager();
		Enumeration formals = meth.getFormals().getElements();
		symTab.enterScope();
		
		while (formals.hasMoreElements())
		{
			Formal f = (Formal) formals.nextElement();
			Class_ classOfThisFormal = ClassTable.getInstance().lookup(f.getTypeDecl());
			symTab.addId(f.getName(), classOfThisFormal);
		}
	}
	
	/**
	 * Takes two enumerations parameter list and compare their types.
	 * @param eForm	List of formals params
	 * @param eAct	List of actuals params
	 * @return True if the actuals' types are same or subclass of formals' types.
	 */
	private boolean validateActualsFormals(Enumeration eForm, Enumeration eAct)
	{
		while(eForm.hasMoreElements() && eAct.hasMoreElements())
		{
			Expression actualParam = (Expression) eAct.nextElement();
			Formal formalParam = (Formal) eForm.nextElement();
			//if the actual param is subclass of the formal param then continues, else exit
			if(!ClassTable.getInstance().isSubClass(actualParam.get_type(), formalParam.getTypeDecl()))
				return false;
		}
		
		//if the dispatch uses more params than the params needed, one of the two lists has still some elements
		//so the invocation is wrong
		if(eForm.hasMoreElements() || eAct.hasMoreElements() )
			return false;
		
		return true;
	}
	
	
	/**
	 * This method checks if a static dispatch node respects the signature of the method
	 * @param sd the static dispatch node to validate
	 * @return true if the static dispatch is correct, else false
	 */
	public boolean validateStaticDispatch(static_dispatch sd)
	{
		AbstractSymbol sdName = sd.getName();
		method meth = lookupStaticMethod(sdName);
		
		if(meth == null)
			return false;
		
		Expressions actuals = sd.getActual();
		Formals formals = meth.getFormals();
		
		Enumeration eForm = formals.getElements();
		Enumeration eAct = actuals.getElements();
				
		return validateActualsFormals(eForm, eAct);
	}
}
