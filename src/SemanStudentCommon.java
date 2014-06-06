


class SemanticException extends Exception
{
	private static final long serialVersionUID = 1L;
	public SemanticException()
	{
		super();
	}
}

/**
 * this class helps the TypeChecker in common operations
 * see declared methods javadoc for further informations
 *
 */
class TypeCheckerHelper 
{
	static ClassTable class_table = ClassTable.getInstance();
	static SemantErrorsManager semant_error = SemantErrorsManager.getInstance();
	static SemantState semant_state = SemantState.getInstance();
	
	/**
	 * This method checks if the class is registered in the classTable.
	 * Handle correctly the Semantic Exception in a try-catch block.
	 * @param cls the class to check
	 */
	static void validateType(AbstractSymbol cls) throws SemanticException
	{
		if (!class_table.isClassRegistered(cls))
		{
			/*semant_error.semantError(semant_state.getCurrentClass(), "Invalid type %s", cls);	*/
			throw new SemanticException();
		}
	}
	
	/**
	 * this method checks if it's possible to cast a certain class to another one,
	 * that is if former class is equal or a subclass of the latter
	 * @param child 
	 * @param parent
	 */
	static void validateCast(TreeNode node, AbstractSymbol child, AbstractSymbol parent) throws SemanticException
	{
		if (!(child.equals(parent) || class_table.isSubClass(child, parent)))
		{
			if (node != null)
				semant_error.semantError(node, "Invalid cast: can't cast type %s to type %s", child, parent);	
			throw new SemanticException();
		}	
	}
	
	static void typeMatch(AbstractSymbol as1, AbstractSymbol as2) throws SemanticException
	{
		if(!as1.equals(as2)) 
			throw new SemanticException();
	}

	/**
	 * this method retrieves the real type of a symbol
	 * @param returnType the type to check
	 * @return the AbstractSymbol associated. 
	 * If it's a SELF_TYPE then it returns the current class
	 */
	public static AbstractSymbol inferSelfType(AbstractSymbol returnType) {
		if (returnType.equals(TreeConstants.SELF_TYPE))
		{
			return semant_state.getCurrentClass().getName();
		}
		return returnType;
	}
	
	public static AbstractSymbol inferSelfType(AbstractSymbol returnType, AbstractSymbol default_type) {
		if (returnType.equals(TreeConstants.SELF_TYPE))
		{
			return default_type;
		}
		return returnType;
	}

	public static void typeMatchAny(AbstractSymbol type, AbstractSymbol... symbols) throws SemanticException 
	{
		for (AbstractSymbol sym : symbols)
		{
			if (type.equals(sym)) 
				return;
		}
		throw new SemanticException();
	}

	public static AbstractSymbol[] inferSelfType(AbstractSymbol... rtArray) {
		AbstractSymbol[] inferArray = new AbstractSymbol[rtArray.length];
		for(int i=0; i<rtArray.length; i++)
		{
			inferArray[i] = inferSelfType(rtArray[i]);
		}
		return inferArray;
	}
	
}


/**
 * this class tracks the current semantic state
 * 
 */
class SemantState 
{
	static SemantState state=null;
	private Class_ current_class;
	private SymbolTable scope_manager;
	
	private SemantState()
	{
		current_class = null;
		scope_manager = new SymbolTable();
	}
	
	public SymbolTable getScopeManager() 
	{
		return scope_manager;
	}


	/**
	 * this method set the current class
	 * @param cls the class to set
	 */
	public void setCurrentClass(Class_ cls)
	{
		current_class = cls;
	}
	
	/**
	 * this method retrieves the current state
	 * @return current state
	 */
	static SemantState getInstance()
	{
		if (state==null) {
			state = new SemantState();
		}
		return state;
	}
	
	/**
	 * this method retrieves the current class
	 * @return current class
	 */
	public Class_ getCurrentClass()
	{
		return current_class;
	}
}
