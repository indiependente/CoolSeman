import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.jgrapht.generate.RingGraphGenerator;


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
	
	void onVisitStart();
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
	Object action(T obj);
}




class ExpressionTypeSelector
{
	HashMap<Class<?>, IAction<Expression>> binded_actions;
	
	public ExpressionTypeSelector()
	{
		binded_actions = new HashMap<Class<?>, IAction<Expression>>();
	}
	
	public <T extends Expression> void register(Class<?> cls, IAction<T> act)
	{
		if (!binded_actions.containsKey(cls))
			binded_actions.put(cls, (IAction<Expression>) act);
		else
			throw new RuntimeException("already binded action for type " + cls.getName());
	
	
	}
	
	public IAction<Expression> getAction(Class<?> cls)
	{
		if (binded_actions.containsKey(cls))
			return binded_actions.get(cls);
		else
			throw new RuntimeException("binded action for type " + cls.getName() + " not found");
	}
	
	public <T extends Expression> Object execute(T object)
	{
		return getAction(object.getClass()).action(object);				
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

	@Override
	public void onVisitStart() {
		// TODO Auto-generated method stub
		
	}
}

/**
 * this is the first visit executed on the AST
 * it collects every type declared
 * @deprecated
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
 * This is the second visit executed on the AST.
 * Registers all the features for every Class in the AST. 
 *
 */
class FeaturesVisitor extends DefaultVisitor
{
	@Override
	public Object onVisitPostOrder(attr attr) {
		Class_ cls = SemantState.getInstance().getCurrentClass();
		cls.getFeaturesTable().registerAttr(attr);
		/*	TEST	*/
		System.out.println(cls.getName() + ": "+ attr.name.str + ": " + cls.getFeaturesTable().isAttributeRegistered(attr.name));
		/*	END TEST	*/
		return null;
	}
	
	@Override
	public Object onVisitPostOrder(method meth) {
		Class_ cls = SemantState.getInstance().getCurrentClass();
		cls.getFeaturesTable().registerMethod(meth);
		/*	TEST	*/
		System.out.println(cls.getName() + ": "+ meth.name.str + ": " + cls.getFeaturesTable().isMethodRegistered(meth.name));
		/*	END TEST	*/
		return null;
	}
	
	@Override
	public void onVisitEnd() 
	{
		SemantErrorsManager.getInstance().validate();
	}
	
	@Override
	public void onVisitStart()
	{
		ClassTable tbl = ClassTable.getInstance();
		tbl.installBasicClasses();
	    tbl.validate();
		SemantErrorsManager.getInstance().validate();
	}
}





/**
 * this is the third visit of the AST 
 * it checks types
 *
 */
class TypeCheckerVisitor implements ITreeVisitor
{
	
	protected ExpressionTypeSelector selector;
	protected SemantState semant_state = SemantState.getInstance();
	protected SemantErrorsManager semant_errors = SemantErrorsManager.getInstance();
	
	public TypeCheckerVisitor()
	{
		selector = new ExpressionTypeSelector();
		
		selector.register(object.class, new IAction<object>()
		{
			@Override
			public Object action(object obj) 
			{
				Class_ type = (Class_) semant_state.getScopeManager().lookup(obj.getName());
				return obj.set_type(type.getName());	
			}
	
		});
		
		selector.register(int_const.class, new IAction<int_const>()
		{
			@Override
			public Object action(int_const obj) 
			{
				return obj.set_type(TreeConstants.Int);	
			}
	
		});
		
		selector.register(bool_const.class, new IAction<bool_const>()
		{
			@Override
			public Object action(bool_const obj) 
			{
				return obj.set_type(TreeConstants.Bool);	
			}
	
		});
		
		selector.register(isvoid.class, new IAction<isvoid>()
		{
			@Override
			public Object action(isvoid obj) 
			{
				return obj.set_type(TreeConstants.Bool);	
			}
	
		});
		
		selector.register(string_const.class, new IAction<string_const>()
		{
			@Override
			public Object action(string_const obj) 
			{
				return obj.set_type(TreeConstants.Str);	
			}
	
		});
		
		selector.register(comp.class, new IAction<comp>()
		{
			@Override
			public Object action(comp obj) 
			{
				AbstractSymbol child_type = ((AbstractSymbol) obj.getData("child"));
				try {
					TypeCheckerHelper.validateType(child_type);
					TypeCheckerHelper.typeMatch(child_type, TreeConstants.Bool);
				} catch (SemanticException e) {
					semant_errors.semantError(obj, "Argument of 'not' has type %s instead of Bool.", child_type);	
				}
				
				return obj.set_type(TreeConstants.Bool);	
			}
	
		});
		
		
		selector.register(new_.class, new IAction<new_>()
		{
			@Override
			public Object action(new_ obj) 
			{
				AbstractSymbol type = obj.getTypeName();
				try 
				{
					TypeCheckerHelper.validateType(type);
				} 
				catch (SemanticException e) {
					semant_errors.semantError(obj, "'new' used with undefined class %s", type);
				}
				return obj.set_type(type);	
			}
	
		});
		
		
		selector.register(leq.class, new IAction<leq>()
		{
			@Override
			public Object action(leq obj) 
			{
				AbstractSymbol left_type = (AbstractSymbol) obj.getData("left");
				AbstractSymbol right_type = (AbstractSymbol) obj.getData("right");
				
				try {
					TypeCheckerHelper.validateType(left_type);
					TypeCheckerHelper.validateType(right_type);
					TypeCheckerHelper.typeMatch(left_type, TreeConstants.Int);
					TypeCheckerHelper.typeMatch(right_type, TreeConstants.Int);
				} catch (SemanticException e) {
					semant_errors.semantError(obj, "non-Int arguments: %s <= %s", left_type, right_type);	
				}
				
				return  obj.set_type(TreeConstants.Bool);
			}
	
		});
		
		
		selector.register(eq.class, new IAction<eq>()
		{
			@Override
			public Object action(eq obj) 
			{
				AbstractSymbol left_type = (AbstractSymbol) obj.getData("left");
				AbstractSymbol right_type = (AbstractSymbol) obj.getData("right");
				
				try
				{
					TypeCheckerHelper.validateType(left_type);
					TypeCheckerHelper.validateType(right_type);
					TypeCheckerHelper.typeMatchAny(left_type, TreeConstants.Int, 
							TreeConstants.Bool, TreeConstants.Str);
					TypeCheckerHelper.typeMatchAny(right_type, TreeConstants.Int,
							TreeConstants.Bool, TreeConstants.Str);
					TypeCheckerHelper.typeMatch(left_type, right_type);
				}
				catch (SemanticException e) {
					semant_errors.semantError(obj, "Illegal comparison with a basic type");	
				}
				
				return  obj.set_type(TreeConstants.Bool);
			}
	
		});
		
		selector.register(no_expr.class, new IAction<no_expr>()
		{

			@Override
			public Object action(no_expr obj) {
				// TODO Auto-generated method stub
				return null;
			}
	
		});
		
		
		selector.register(lt.class, new IAction<lt>()
		{
			@Override
			public Object action(lt obj) 
			{
				AbstractSymbol left_type = (AbstractSymbol) obj.getData("left");
				AbstractSymbol right_type = (AbstractSymbol) obj.getData("right");
				
				try {
					TypeCheckerHelper.validateType(left_type);
					TypeCheckerHelper.validateType(right_type);
					TypeCheckerHelper.typeMatch(left_type, TreeConstants.Int);
					TypeCheckerHelper.typeMatch(right_type, TreeConstants.Int);
				} catch (SemanticException e) {
					semant_errors.semantError(obj, "non-Int arguments: %s < %s", left_type, right_type);	
				}
				
				return  obj.set_type(TreeConstants.Bool);
			}
	
		});
		
		
		selector.register(neg.class, new IAction<neg>()
		{
			@Override
			public Object action(neg obj) 
			{
				AbstractSymbol child_type = (AbstractSymbol) obj.getData("child");
				try
				{
					TypeCheckerHelper.validateType(child_type);
					TypeCheckerHelper.typeMatch(child_type, TreeConstants.Int);
				}
				catch (SemanticException ex)
				{
					semant_errors.semantError(obj, "Argument of '~' has type %s instead of Int.", child_type);	
				}
				return obj.set_type(TreeConstants.Int);
			}
	
		});
		
		
		selector.register(divide.class, new IAction<divide>()
		{
			@Override
			public Object action(divide obj) 
			{
				 AbstractSymbol left_child = (AbstractSymbol) obj.getData("left");
				 AbstractSymbol right_child = (AbstractSymbol) obj.getData("right");
				 
				 try {
					TypeCheckerHelper.validateType(left_child);
					TypeCheckerHelper.typeMatch(left_child, TreeConstants.Int);
					
					TypeCheckerHelper.validateType(right_child);
					TypeCheckerHelper.typeMatch(right_child, TreeConstants.Int);
				 } catch (SemanticException e) {
					 semant_errors.semantError(obj,"non-Int arguments: %s / %s",left_child,right_child);
				}
				 return obj.set_type(TreeConstants.Int);
			}
	
		});
		
		
		selector.register(mul.class, new IAction<mul>()
		{
			@Override
			public Object action(mul obj) 
			{
				AbstractSymbol left_child_type = (AbstractSymbol) obj.getData("left");
				AbstractSymbol right_child_type = (AbstractSymbol) obj.getData("right");
				try{
					TypeCheckerHelper.validateType(left_child_type);
					TypeCheckerHelper.typeMatch(left_child_type, TreeConstants.Int);
					TypeCheckerHelper.validateType(right_child_type);
					TypeCheckerHelper.typeMatch(right_child_type, TreeConstants.Int);
				}catch (SemanticException e){
					semant_errors.semantError(obj, "non-Int arguments: %s * %s", left_child_type, right_child_type);	
				}
				return obj.set_type(TreeConstants.Int);
			}
	
		});
		
		
		selector.register(sub.class, new IAction<sub>()
		{
			@Override
			public Object action(sub obj) 
			{
				AbstractSymbol left_child_type = (AbstractSymbol) obj.getData("left");
				AbstractSymbol right_child_type = (AbstractSymbol) obj.getData("right");
				try{
					TypeCheckerHelper.validateType(left_child_type);
					TypeCheckerHelper.typeMatch(left_child_type, TreeConstants.Int);
					TypeCheckerHelper.validateType(right_child_type);
					TypeCheckerHelper.typeMatch(right_child_type, TreeConstants.Int);
				}catch (SemanticException e){
					semant_errors.semantError(obj, "non-Int arguments: %s - %s", left_child_type, right_child_type);	
				}
				return obj.set_type(TreeConstants.Int);
			}
	
		});
		
		
		selector.register(plus.class, new IAction<plus>()
		{
			@Override
			public Object action(plus obj) 
			{
				AbstractSymbol left_child_type = (AbstractSymbol) obj.getData("left");
				AbstractSymbol right_child_type = (AbstractSymbol) obj.getData("right");
				try{
					TypeCheckerHelper.validateType(left_child_type);
					TypeCheckerHelper.typeMatch(left_child_type, TreeConstants.Int);
					TypeCheckerHelper.validateType(right_child_type);
					TypeCheckerHelper.typeMatch(right_child_type, TreeConstants.Int);
				}catch (SemanticException e){
					semant_errors.semantError(obj, "non-Int arguments: %s + %s", left_child_type, right_child_type);	
				}
				return obj.set_type(TreeConstants.Int);
			}
	
		});
		
		
		selector.register(let.class, new IAction<let>()
		{
			@Override
			public Object action(let obj) 
			{
				return null;
			}
	
		});
		
		
		selector.register(block.class, new IAction<block>()
		{
			@Override
			public Object action(block obj) 
			{
				AbstractSymbol ret_block=(AbstractSymbol)obj.getData("ret_block");
				try {
					TypeCheckerHelper.validateType(ret_block);
				} catch (SemanticException e) {
					/**
					 * Nothing to do.
					 */
				}
				
				return obj.set_type(ret_block);
			}
	
		});
		
		
		selector.register(typcase.class, new IAction<typcase>()
		{
			@Override
			public Object action(typcase obj) 
			{
				return null;
			}
	
		});
		
		
		selector.register(loop.class, new IAction<loop>()
		{
			@Override
			public Object action(loop obj) 
			{
				AbstractSymbol pred_type = (AbstractSymbol)obj.getData("pred");
				
				try {
					TypeCheckerHelper.validateType(pred_type);
					TypeCheckerHelper.typeMatch(pred_type, TreeConstants.Bool);
				} catch (SemanticException e) {
					 semant_errors.semantError(obj,"Loop condition does not have type Bool.");
				}
				
				return obj.set_type(TreeConstants.Object_);
				
			}
	
		});
		
		
		selector.register(cond.class, new IAction<cond>()
		{
			@Override
			public Object action(cond obj) 
			{
				AbstractSymbol ret_pred = (AbstractSymbol) obj.getData("ret_pred");
				AbstractSymbol ret_then_exp = (AbstractSymbol) obj.getData("ret_then_exp");
				AbstractSymbol ret_else_exp = (AbstractSymbol) obj.getData("ret_else_exp");
				
				if(!ClassTable.getInstance().isSubClass(ret_pred,TreeConstants.Bool))
					{
						semant_errors.semantError(obj, "Predicate of 'if' does not have type Bool.");
						return obj.set_type(TreeConstants.Object_);
					}
				
				try 
				{
					TypeCheckerHelper.validateType(ret_then_exp);
				} 
				catch (SemanticException e) 
				{
					semant_errors.semantError(obj, "Undeclared identifier %s", ret_then_exp);
				}
				
				try 
				{
					TypeCheckerHelper.validateType(ret_else_exp);
				} 
				catch (SemanticException e) 
				{
					semant_errors.semantError(obj, "Undeclared identifier %s", ret_else_exp);
				}
				
				AbstractSymbol lub = ClassTable.getInstance().leastUpperBound(ret_then_exp,ret_else_exp);
				return obj.set_type(lub);
			}
	
		});
		
		
		selector.register(dispatch.class, new IAction<dispatch>()
		{
			@Override
			public Object action(dispatch obj) 
			{
				return null;
			}
	
		});
		
		
		selector.register(static_dispatch.class, new IAction<static_dispatch>()
		{
			@Override
			public Object action(static_dispatch obj) 
			{
				return null;
			}
	
		});
		
		
		selector.register(assign.class, new IAction<assign>()
		{
			@Override
			public Object action(assign obj) 
			{
				return null;
			}
	
		});

	}

	@Override
	public void onVisitStart()
	{
		
	}
	
	
	/**
	 * this method analyses if a method node is semantically correct 
	 */
	public Object onVisitPostOrder(method mth) {
		AbstractSymbol absym = (AbstractSymbol) mth.getData("dyn_return_type");
		AbstractSymbol dynamic_return_type_symbol = TypeCheckerHelper.inferSelfType(absym);
		AbstractSymbol static_return_type_symbol = TypeCheckerHelper.inferSelfType(mth.getReturnType());
		try
		{
			TypeCheckerHelper.validateType(dynamic_return_type_symbol);
			TypeCheckerHelper.validateType(static_return_type_symbol);
			TypeCheckerHelper.validateCast(dynamic_return_type_symbol, static_return_type_symbol);
		}
		catch (SemanticException e)
		{		
		}
			
		
		return null;
	}

	/**
	 * it checks if attr node is semantically correct
	 */
	public Object onVisitPostOrder(attr itm) {
		AbstractSymbol absym = (AbstractSymbol) itm.getData("init_type");
		AbstractSymbol init_type_symbol = TypeCheckerHelper.inferSelfType(absym);
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
		int numScopes = cls.getFeaturesTable().loadClassScope(cls.getName());
		System.out.println(" PostOrder #SCOPES: "+numScopes);
		System.out.println("Class_ Symbol Table PostOrder: \n"+SemantState.getInstance().getScopeManager());
		return null;
	}

	@Override
	public Object onVisitPostOrder(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * it checks if a case node is semantically correct
	 * 
	 */
	public Object onVisitPostOrder(Case branch) {
		AbstractSymbol absym = (AbstractSymbol) branch.getData("branch_type");
		AbstractSymbol branch_type_symbol = TypeCheckerHelper.inferSelfType(absym);
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
	public Object onVisitPostOrder(Expression expr) 
	{
		return selector.execute(expr);
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
		SemantState.getInstance().getCurrentClass().getFeaturesTable().loadMethodScope(itm.getName());
		System.out.println("method Symbol Table PreOrder: \n"+SemantState.getInstance().getScopeManager());
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
		int numScopes = cls.getFeaturesTable().loadClassScope(cls.getName());
		System.out.println(" PreOrder #SCOPES: "+numScopes);
		System.out.println("Class_ Symbol Table PreOrder: \n"+SemantState.getInstance().getScopeManager());
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

