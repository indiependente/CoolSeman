import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


/**
 * This class implements the decorator pattern
 *
 */
abstract class Decorator
{
	protected HashMap<String, Object> data = null; 
	
	
	 /**
     * register a key value for the current object
     * @param key the decorating attribute 
     * @param value the value of the decorating attribute
     */
    protected void decorate(String key, Object value)
    {
    	if (data == null)
    		data = new HashMap<String, Object>();
    	data.put(key, value);
    }
    /**
     * retrieves the value associated to the given key
     * it returns null if key is not present
     * @param keythe decorating attribute
     * @return the value associated to the given key
     */
    public Object getData(String key)
    {
    	if (data != null && data.containsKey(key))
    		return data.get(key);
    	return null;
    }
}

/**
 *  Interface for Visitor pattern.
 *  This interface provides a version of the visit method per each different kind of node in the AST
 *  The concrete implementations of the  methods in this interface will implement the logic needed to process the AST's nodes
 *
 */
interface ITreeVisitor {
	
	Object onVisitPostOrder(method itm);
	Object onVisitPostOrder(attr itm);
	Object onVisitPostOrder(Cases cases);
	Object onVisitPostOrder(Program program);
	Object onVisitPostOrder(Class_ cls);
	Object onVisitPostOrder(Formal formal);
	Object onVisitPostOrder(Case branch);
	Object onVisitPostOrder(Expression expr);
	Object onVisitPostOrder(Expressions expressions);
	
	Object onVisitPreOrder(method itm);
	Object onVisitPreOrder(attr itm);
	Object onVisitPreOrder(Cases cases);
	Object onVisitPreOrder(Program program);
	Object onVisitPreOrder(Class_ cls);
	Object onVisitPreOrder(Formal formal);
	Object onVisitPreOrder(Case branch);
	Object onVisitPreOrder(Expression expr);
	Object onVisitPreOrder(Expressions expressions);
	
	void onVisitEnd();
	
}



/**
 * Interface for Visitor pattern.
 * Each node, to be visited, must implement this interface.
 *
 */
interface IVisitable {
	/**
	 * This method allows to propagate the visit of the actual node to its children
	 * @param visitor The object visitor which will be propagated through the AST
	 * @return the type information requested by the parent node
	 */
	Object accept(ITreeVisitor visitor);
}



/**
 * Each kind of node perform always the same operations, based on its type.
 * This interface is used to map the action of each kind of node in the AST.
 *
 * @param <T>
 */
interface IAction <T>
{
	void action(T obj);
}




class ExpressionTypeSelector
{
	HashMap<Class<?>, ArrayList<IAction<Expression>>> binded_actions;
	
	public ExpressionTypeSelector()
	{
		binded_actions = new HashMap<Class<?>, ArrayList<IAction<Expression>>>();
	}
	
	public <T extends Expression> void register(Class<?> cls, IAction<T> act)
	{
		if (!binded_actions.containsKey(cls))
			binded_actions.put(cls, new ArrayList<IAction<Expression>>());
		//else
		//	throw new RuntimeException("already binded action for type " + cls.getName());
	
		binded_actions.get(cls).add((IAction<Expression>) act);
	
	}
	
	public ArrayList<IAction<Expression>> getActions(Class<?> cls)
	{
		if (binded_actions.containsKey(cls))
			return binded_actions.get(cls);
		else
			throw new RuntimeException("binded action for type " + cls.getName() + " not found");
	}
	
	public <T extends Expression> void execute(T object)
	{
		for (IAction<Expression> oper : getActions(object.getClass()))
			oper.action(object);				
	}
}

class DefaultVisitor implements ITreeVisitor
{

	@Override
	public Object onVisitPostOrder(method itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(attr itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Cases cases) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Class_ cls) {
		return null;
	}

	@Override
	public Object onVisitPostOrder(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Case branch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Expressions expressions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onVisitEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onVisitPreOrder(method itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(attr itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Cases cases) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Class_ cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Case branch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Expressions expressions) {
		// TODO Auto-generated method stub
		return null;
	}
}

/**
 * this is the first visit executed on the AST
 * it collects every type declared
 *
 */
class ClassesVisitor extends DefaultVisitor 
{
	@Override
	public Object onVisitPostOrder(Class_ cls) {
		ClassTable.getInstance().registerClass(cls);
		return null;
	}
	
	@Override
	public void onVisitEnd() 
	{
		ClassTable tbl = ClassTable.getInstance();
	    tbl.validate();
		SemantErrorsManager.getInstance().validate();
		
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
	 * this method checks if the class is registered in the classTable
	 * @param cls the class to check
	 */
	static void validateType(AbstractSymbol cls)
	{
		if (!class_table.isClassRegistered(cls))
		{
			semant_error.semantError(semant_state.getCurrentClass(), "Invalid type %s", cls);	
			throw new RuntimeException();
		}
	}
	
	/**
	 * this method checks if it's possible to cast a certain class to another one,
	 * that is if former class is equal or a subclass of the latter
	 * @param child 
	 * @param parent
	 */
	static void validateCast(AbstractSymbol child, AbstractSymbol parent)
	{
		if (!child.equals(parent) || !class_table.isSubClass(child, parent))
		{
			semant_error.semantError(semant_state.getCurrentClass(), "Invalid cast: can't cast type %s to type %s", child, parent);	
			throw new RuntimeException();
		}	
	}

	/**
	 * this method retrieves the real type of a symbol
	 * @param returnType the type to check
	 * @return the AbstractSymbol associated. 
	 * If it's a SELF_TYPE then it returns the current class
	 */
	public static AbstractSymbol inferSelfType(AbstractSymbol returnType) {
		if (returnType.getString().equals("SELF_TYPE"))
		{
			return semant_state.getCurrentClass().getName();
		}
		return returnType;
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
	private SemantState()
	{
		current_class = null;
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



/**
 * this is the third visit of the AST 
 * it checks types
 *
 */
class TypeCheckerVisitor implements ITreeVisitor
{

	/**
	 * this method analyses if a method node is semantically correct 
	 */
	public Object onVisitPostOrder(method itm) {
		Object dynamic_return_type = itm.getData("dyn_return_type");
		AbstractSymbol dynamic_return_type_symbol = TypeCheckerHelper.inferSelfType((AbstractSymbol) dynamic_return_type);
		AbstractSymbol static_return_type_symbol = TypeCheckerHelper.inferSelfType(itm.getReturnType());
		try
		{
			TypeCheckerHelper.validateType(dynamic_return_type_symbol);
			TypeCheckerHelper.validateType(static_return_type_symbol);
			TypeCheckerHelper.validateCast(dynamic_return_type_symbol, static_return_type_symbol);
		}
		catch (RuntimeException e)
		{		
		}
			
		
		return null;
	}

	/**
	 * it checks if attr node is semantically correct
	 */
	public Object onVisitPostOrder(attr itm) {
		AbstractSymbol init_type_symbol = TypeCheckerHelper.inferSelfType((AbstractSymbol) itm.getData("init_type"));
		AbstractSymbol static_type_symbol = TypeCheckerHelper.inferSelfType(itm.getReturnType());
		try
		{
			TypeCheckerHelper.validateType(init_type_symbol);
			TypeCheckerHelper.validateType(static_type_symbol);
			TypeCheckerHelper.validateCast(init_type_symbol, static_type_symbol);
		}
		catch(Exception e)
		{
		}
		return null;
	}

	@Override
	public Object onVisitPostOrder(Cases cases) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Class_ cls) {
		
		return null;
	}

	@Override
	public Object onVisitPostOrder(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * it checks if a case node is semantically correct
	 */
	public Object onVisitPostOrder(Case branch) {
		AbstractSymbol branch_type_symbol = TypeCheckerHelper.inferSelfType((AbstractSymbol) branch.getData("branch_type"));
		AbstractSymbol static_type_symbol = TypeCheckerHelper.inferSelfType(branch.getReturnType());
		try
		{
			TypeCheckerHelper.validateType(branch_type_symbol);
			TypeCheckerHelper.validateType(static_type_symbol);
			TypeCheckerHelper.validateCast(branch_type_symbol, static_type_symbol);
		}
		catch(Exception e)
		{
		}
		return branch_type_symbol;
	}

	@Override
	public Object onVisitPostOrder(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * this method returns the type of the last expression
	 */
	public Object onVisitPostOrder(Expressions expressions) {
		Vector vect = expressions.getElementsVector();
		Object obj = vect.elementAt(vect.size() - 1);
		return ((Expression) obj).get_type();
	}

	@Override
	public void onVisitEnd() {
		SemantErrorsManager.getInstance().validate();
		
	}

	@Override
	public Object onVisitPreOrder(method itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(attr itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Cases cases) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Class_ cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Case branch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Expressions expressions) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

/*
class ConcreteVisitor implements ITreeVisitor
{
	static ExpressionTypeSelector selector = new ExpressionTypeSelector();
	
	static {
		selector.register(dispatch.class, new IAction<dispatch>() {

			@Override
			public void action(dispatch data) {
				//System.out.println("dispatch");
				System.out.println(data.toString());
				
			}
			
		});
		
		selector.register(string_const.class, new IAction<string_const>() {

			@Override
			public void action(string_const data) {
				//System.out.println("str_const");
				System.out.println(data.toString());
				
			}
			
		});
		
		selector.register(object.class, new IAction<object>() {

			@Override
			public void action(object data) {
				System.out.println("???");
				System.out.println(data.toString());
			}
			
		});
		selector.register(object.class, new IAction<object>() {

			@Override
			public void action(object data) {
				System.out.println("!!!!");
				System.out.println(data.toString());
			}
			
		});
	}
	
	private ClassTable class_table;
	
	public ConcreteVisitor(ClassTable cls_tbl)
	{
		class_table = cls_tbl;
	}

	@Override
	public Object visit(method itm) {
		// TODO Auto-generated method stub
		System.out.println("method");
		return null;
	}

	@Override
	public Object visit(attr itm) {
		// TODO Auto-generated method stub
		System.out.println("attr");
		return null;
	}

	@Override
	public Object visit(Cases cases) {
		// TODO Auto-generated method stub
		System.out.println("Cases");
		return null;
	}

	@Override
	public Object visit(Program program) {
		// TODO Auto-generated method stub
		System.out.println("programc");
		return null;
	}

	@Override
	public Object visit(Class_ cls) {
		// TODO Auto-generated method stub
		System.out.println("Class_");
		class_table.registerClass(cls);
		return null;
	}

	@Override
	public Object visit(Formal formal) {
		// TODO Auto-generated method stub
		System.out.println("Formal");
		return null;
	}

	@Override
	public Object visit(Case branch) {
		// TODO Auto-generated method stub
		System.out.println("Case");
		return null;
	}

	@Override
	public Object visit(Expression expr) {
		// TODO Auto-generated method stub
		//System.out.println("Expression");
		selector.execute(expr);
		return null;
	}

	@Override
	public Object visit(Expressions expressions) {
		// TODO Auto-generated method stub
		System.out.println("Expressions");
		return null;
	}

	@Override
	public void onVisitEnd() {
		// TODO Auto-generated method stub
		AbstractSymbol[] list = {
				AbstractTable.idtable.addString("Object"),
				AbstractTable.idtable.addString("IO"),
				AbstractTable.idtable.addString("Main"),
				AbstractTable.idtable.addString("A"),
				AbstractTable.idtable.addString("B")
		};
		
		for (AbstractSymbol s : list)
		{
			System.out.println(s.getString());
			System.out.println("------->");
			for (AbstractSymbol el : tbl.getParents(s))
				System.out.println(el.getString());
			System.out.println("-------<");
			
			
			
		}
		
		System.out.println("Lub lista completa");
		System.out.println(tbl.leastUpperBound(list));
		
		System.out.println("----lub main, b--->");
		System.out.println(tbl.leastUpperBound(AbstractTable.idtable.addString("Main"), AbstractTable.idtable.addString("B")));
		System.out.println("-------<");
		
		System.out.println("---lub a, io, object---->");
		System.out.println(tbl.leastUpperBound(AbstractTable.idtable.addString("A"), AbstractTable.idtable.addString("IO"), AbstractTable.idtable.addString("Object")));
		System.out.println("-------<");
		
		System.out.println("---lub a, b ---->");
		System.out.println(tbl.leastUpperBound(AbstractTable.idtable.addString("A"), AbstractTable.idtable.addString("B")));
		System.out.println("-------<");
		
		
		AbstractSymbol[] list2 = {
				AbstractTable.idtable.addString("A"),
				AbstractTable.idtable.addString("B"),
				AbstractTable.idtable.addString("C"),
				AbstractTable.idtable.addString("D"),
				AbstractTable.idtable.addString("E"),
				AbstractTable.idtable.addString("F")
		};
		
		System.out.println("Lub lista2 completa");
		System.out.println(tbl.leastUpperBound(list2));
		
		
		System.out.println("Lub A,B,C completa");
		System.out.println(tbl.leastUpperBound(list2[0], list2[1], list2[2]));
		
		System.out.println("Lub A,E completa");
		System.out.println(tbl.leastUpperBound(list2[0], list2[4]));
		
		System.out.println("Lub A, F completa");
		System.out.println(tbl.leastUpperBound(list2[0], list2[5]));
		
		System.out.println("Lub D,E,F completa");
		System.out.println(tbl.leastUpperBound(list2[3], list2[4], list2[5]));
		
		System.out.println("Lub A,B,C,D,E completa");
		System.out.println(tbl.leastUpperBound(list2[3], list2[4], list2[0], list2[1], list2[2]));
	}
	}
	
}

*/

