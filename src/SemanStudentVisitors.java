import java.util.ArrayList;
import java.util.HashMap;


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
	
	Object visit(method itm);
	Object visit(attr itm);
	Object visit(Cases cases);
	Object visit(Program program);
	Object visit(Class_ cls);
	Object visit(Formal formal);
	Object visit(Case branch);
	Object visit(Expression expr);
	Object visit(Expressions expressions);
	
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
	public Object visit(method itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(attr itm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Cases cases) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Class_ cls) {
		return null;
	}

	@Override
	public Object visit(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Case branch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Expressions expressions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onVisitEnd() {
		// TODO Auto-generated method stub
		
	}
}


class ClassesVisitor extends DefaultVisitor 
{
	@Override
	public Object visit(Class_ cls) {
		ClassTable.getInstance().registerClass(cls);
		return null;
	}
	
	@Override
	public void onVisitEnd() 
	{
		ClassTable.getInstance().validate();
		SemantErrorsManager.getInstance().validate();
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

