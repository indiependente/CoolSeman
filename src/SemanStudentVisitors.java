import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
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
	
	IAction<Expression> dummy = new IAction<Expression>()
	{
		@Override
		public Object action(Expression obj) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
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
			return dummy;
			//throw new RuntimeException("binded action for type " + cls.getName() + " not found");
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
//		System.out.println(cls.getName() + ": "+ attr.name.str + ": " + cls.getFeaturesTable().isAttributeRegistered(attr.name));
		/*	END TEST	*/
		return null;
	}
	
	@Override
	public Object onVisitPostOrder(method meth) {
		Class_ cls = SemantState.getInstance().getCurrentClass();
		cls.getFeaturesTable().registerMethod(meth);
		/*	TEST	*/
//		System.out.println(cls.getName() + ": "+ meth.name.str + ": " + cls.getFeaturesTable().isMethodRegistered(meth.name));
		/*	END TEST	*/
		return null;
	}
	
	@Override
	public void onVisitEnd() 
	{
		SemantErrorsManager err_mgr = SemantErrorsManager.getInstance();
		ClassTable class_table = ClassTable.getInstance();
		
		class_table.lookup(TreeConstants.Object_).accept(this);
//		class_table.lookup(TreeConstants.Bool).accept(this);
//		class_table.lookup(TreeConstants.Int).accept(this);
		class_table.lookup(TreeConstants.IO).accept(this);
//		class_table.lookup(TreeConstants.Str).accept(this);
		
		class_table.lookup(TreeConstants.Str).accept(new DefaultVisitor()
		{
			@Override
			public Object onVisitPostOrder(method meth) {
				Class_ cls = SemantState.getInstance().getCurrentClass();
				cls.getFeaturesTable().registerMethod(meth);
				return null;
			}
		});

		if(!class_table.isClassRegistered(TreeConstants.Main))
			err_mgr.fatal("Class Main is not defined.");
		
		if(FeaturesTable.lookupMethod(TreeConstants.Main, TreeConstants.main_meth) == null)
			err_mgr.semantError(class_table.lookup(TreeConstants.Main),"No 'main' method in class Main.");

		err_mgr.validate();
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
	
	protected ExpressionTypeSelector postorder_binder, preorder_binder;
	protected SemantState semant_state = SemantState.getInstance();
	protected SemantErrorsManager semant_errors = SemantErrorsManager.getInstance();
	
	public TypeCheckerVisitor()
	{
		postorder_binder = new ExpressionTypeSelector();
		preorder_binder = new ExpressionTypeSelector();
		
		preorder_binder.register(let.class, new IAction<let>()
		{
			@Override
			public Object action(let obj) {
				// TODO Auto-generated method stub
				return null;
			}	
		});
		
		postorder_binder.register(object.class, new IAction<object>()
		{
			@Override
			public Object action(object obj) 
			{
				Class_ type = (Class_) semant_state.getScopeManager().lookup(obj.getName());
				if (type == null)
				{
					semant_errors.semantError(obj, "Undeclared identifier %s.", obj.getName());
				}
				return obj.set_type(TypeCheckerHelper.inferSelfType(type.getName()));	
			}
	
		});
		
		postorder_binder.register(int_const.class, new IAction<int_const>()
		{
			@Override
			public Object action(int_const obj) 
			{
				return obj.set_type(TreeConstants.Int);	
			}
	
		});
		
		postorder_binder.register(bool_const.class, new IAction<bool_const>()
		{
			@Override
			public Object action(bool_const obj) 
			{
				return obj.set_type(TreeConstants.Bool);	
			}
	
		});
		
		postorder_binder.register(isvoid.class, new IAction<isvoid>()
		{
			@Override
			public Object action(isvoid obj) 
			{
				return obj.set_type(TreeConstants.Bool);	
			}
	
		});
		
		postorder_binder.register(string_const.class, new IAction<string_const>()
		{
			@Override
			public Object action(string_const obj) 
			{
				return obj.set_type(TreeConstants.Str);	
			}
	
		});
		
		postorder_binder.register(comp.class, new IAction<comp>()
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
		
		
		postorder_binder.register(new_.class, new IAction<new_>()
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
		
		
		postorder_binder.register(leq.class, new IAction<leq>()
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
		
		
		postorder_binder.register(eq.class, new IAction<eq>()
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
		
		postorder_binder.register(no_expr.class, new IAction<no_expr>()
		{

			@Override
			public Object action(no_expr obj)
			{
				return obj.set_type(TreeConstants.No_type);
			}
	
		});
		
		
		postorder_binder.register(lt.class, new IAction<lt>()
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
		
		
		postorder_binder.register(neg.class, new IAction<neg>()
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
		
		
		postorder_binder.register(divide.class, new IAction<divide>()
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
		
		
		postorder_binder.register(mul.class, new IAction<mul>()
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
		
		
		postorder_binder.register(sub.class, new IAction<sub>()
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
		
		
		postorder_binder.register(plus.class, new IAction<plus>()
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
		
		
		postorder_binder.register(let.class, new IAction<let>()
		{
			@Override
			public Object action(let obj) 
			{
				//check the identifier name in the let stmt: it must not be 'self'
				AbstractSymbol letId = (AbstractSymbol)obj.getData("identifier");
				if(letId.getString().equals("self"))
				{
					//'self' cannot be bound in a 'let' expression.
					semant_errors.semantError(obj, "'self' cannot be bound in a 'let' expression.");	
				}
				
				//check id the identifier class in the let stmt is already defined
				AbstractSymbol letIdType = (AbstractSymbol)obj.getData("type_decl");
				try {
					TypeCheckerHelper.validateType(letIdType);
				} catch (SemanticException e) {
					semant_errors.semantError(obj, "Class "+ letIdType + 
							" of let-bound identifier " + letId + " is undefined.");	
				}
				
				//check if the init type is conform to the declared objectId type in the let stmt
				AbstractSymbol initType = (AbstractSymbol) obj.getData("init");
				try {
					TypeCheckerHelper.validateType(initType);
					TypeCheckerHelper.typeMatchAny(letIdType, initType);
				} catch (SemanticException e) {
					//Inferred type Int of initialization of x does not conform to identifier's declared type String.
					semant_errors.semantError(obj, "Inferred type " + letIdType + " of initialization of "
							+ letId + " does not conform to identifier's declared type " + initType);	
				}
				
				//set the return type to the block's return type
				Expression ret_type = (Expression) obj.getData("body");
				return obj.set_type(ret_type.get_type());
			}
	
		});
		
		
		postorder_binder.register(block.class, new IAction<block>()
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
		
		
		postorder_binder.register(typcase.class, new IAction<typcase>()
		{
			@Override
			public Object action(typcase obj) 
			{
				return obj.set_type((AbstractSymbol) obj.getData("lub"));
			}
	
		});
		
		
		postorder_binder.register(loop.class, new IAction<loop>()
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
		
		
		postorder_binder.register(cond.class, new IAction<cond>()
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
		
		
		postorder_binder.register(dispatch.class, new IAction<dispatch>()
		{
			@Override
			public Object action(dispatch obj) 
			{
				ClassTable cTbl = ClassTable.getInstance();
				
				Class_ myCls = cTbl.lookup((AbstractSymbol)obj.getData("expr_type")); // the expr class
				// it should never enter in this if statement
				if (myCls == null)	// if the dispatch caller class is not defined
				{
					return obj.set_type(TreeConstants.Object_);	// set dispatch type to object
				}
				
				// this validation, validates the actuals params too
				boolean isValid = FeaturesTable.validateDispatch(myCls.getName(), obj);
				if (!isValid)
				{
					return obj.set_type(TreeConstants.Object_);	// set dispatch type to object
				}
				method meth = FeaturesTable.lookupMethod(myCls.getName(), obj.getName());
				if (meth == null)
				{
					semant_errors.semantError(obj, "Dispatch to undefined method %s.", obj.getName());
				}
				
				return obj.set_type(meth.getReturnType());
			}
	
		});
		
		
		postorder_binder.register(static_dispatch.class, new IAction<static_dispatch>()
		{
			@Override
			public Object action(static_dispatch obj) 
			{
				ClassTable cTbl = ClassTable.getInstance();
				AbstractSymbol mySym = (AbstractSymbol)obj.getData("expr_type");
				AbstractSymbol typeSym = (AbstractSymbol)obj.getData("typeid_type");
				
				if (typeSym.equals("SELF_TYPE"))
				{
					semant_errors.semantError(obj, "Static dispatch to SELF_TYPE.");
					return obj.set_type(TreeConstants.Object_);	// set static dispatch type to object
				}
				
				Class_ myCls = cTbl.lookup(mySym); // the expr class
				Class_ typeCls = cTbl.lookup(typeSym); // the expr@type class
				
				// it should never enter in this if statement
				if (myCls == null)	// if the dispatch caller class is not defined
				{
					semant_errors.semantError(obj, "Static dispatch to undefined class %s.", typeSym);
					return obj.set_type(TreeConstants.Object_);	// set dispatch type to object
				}
				
				if (!ClassTable.getInstance().isSubClass(myCls, typeCls))
				{
					semant_errors.semantError(obj,
							"Expression type %s does not conform to declared static dispatch type %s.",
							mySym, typeSym);
				}
				
				// this validation, validates the actuals params too
				boolean isValid = FeaturesTable.validateDispatch(typeSym, obj);
				if (!isValid)
				{
					return obj.set_type(TreeConstants.Object_);	// set dispatch type to object
				}
				
				method meth = FeaturesTable.lookupMethod(typeSym, obj.getName());
				if (meth == null)
				{
					semant_errors.semantError(obj, "Static dispatch to undefined method %s.", obj.getName());
				}
				
				return obj.set_type(meth.getReturnType());

			}
	
		});
		
		
		postorder_binder.register(assign.class, new IAction<assign>()
		{
			@Override
			public Object action(assign obj) 
			{
				AbstractSymbol varName = obj.getName();
				AbstractSymbol exprType = (AbstractSymbol) obj.getData("expr");
				
				Class_ nameClass = (Class_) semant_state.getScopeManager().lookup(varName);  
				AbstractSymbol varType = nameClass.getName();
						
						
				//check if the identifier is declared
				if( varType == null)
				{
					semant_errors.semantError(obj, "Assignment to undeclared variable %s.", varName);
				}
				
				//check the name of the identifier
				if(varName.toString().equals("self"))
				{			
					semant_errors.semantError(obj, "Type " + exprType +
							" of assigned expression does not conform to declared type " +
							   varType +" of identifier "+ varName +".");
	
				}
				
				//check if the expression can be assigned to the variable
				if(!ClassTable.getInstance().isSubClass(exprType, varType))
				{
					semant_errors.semantError(obj," Type "+ exprType + " of assigned expression does not conform to declared type "+
									varType +" of identifier "+ varName +".");
				}
				
				return obj.set_type(varType);
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
//		System.out.println("method " + dynamic_return_type_symbol + " " + static_return_type_symbol);
		try
		{
			TypeCheckerHelper.validateType(dynamic_return_type_symbol);
			TypeCheckerHelper.validateType(static_return_type_symbol);
			TypeCheckerHelper.validateCast(mth, dynamic_return_type_symbol, static_return_type_symbol);
		}
		catch (SemanticException e)
		{		
		}
		
		semant_state.getScopeManager().exitScope();
		
		return mth;
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
			TypeCheckerHelper.validateCast(itm, init_type_symbol, static_type_symbol);
		}
		catch(Exception e)
		{
		}
		return null;
	}

	@Override
	public Object onVisitPostOrder(Cases cases) {
		AbstractSymbol[] type_list = (AbstractSymbol[]) cases.getData("type_list");
		return ClassTable.getInstance().leastUpperBound(type_list);	
	}

	@Override
	public Object onVisitPostOrder(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPostOrder(Class_ cls) {
		 //cls.getFeaturesTable().loadClassScope(cls.getName());
//		System.out.println(" PostOrder #SCOPES: "+numScopes);
//		System.out.println("Class_ Symbol Table PostOrder: \n"+SemantState.getInstance().getScopeManager());
		for (int numScopes = (int) cls.getData("numScopes"); numScopes >= 0; numScopes--)
			semant_state.getScopeManager().exitScope();
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
		AbstractSymbol static_type_symbol = branch.getReturnType();
		
		if(branch.getName().equals("self"))
		{
			SemantErrorsManager.getInstance().semantError(branch, "'self' bound in 'case'.");
		}
		
		
		if(static_type_symbol.equals(TreeConstants.SELF_TYPE))
		{
			SemantErrorsManager.getInstance().semantError(branch, "Identifier %s declared with type SELF_TYPE in case branch.", branch.getName());
		}
		
		
		//sposto scope e valide type del tipo della variabile (static) nella pre order
		
		try 
		{
			TypeCheckerHelper.validateType(branch_type_symbol);
			TypeCheckerHelper.validateCast(branch, branch_type_symbol, static_type_symbol);
		} 
		catch (SemanticException e) 
		{
		}
	
		semant_state.getScopeManager().exitScope();
		return branch_type_symbol;
	}

	
	@Override
	public Object onVisitPostOrder(Expression expr) 
	{
		return postorder_binder.execute(expr);
	}

	/**
	 * this method returns the type of the last expression
	 */
	public Object onVisitPostOrder(Expressions expressions) {
		Vector vect = expressions.getElementsVector();
		int size = vect.size();
		Object ret = null;
		if (size > 0)
			ret = ((Expression) (vect.elementAt(size - 1))).get_type();
		return ret;
	}

	@Override
	public void onVisitEnd() {
		//SemantErrorsManager.getInstance().validate();
		
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
		cls.decorate("numScopes", numScopes);
//		System.out.println(" PreOrder #SCOPES: "+numScopes);
//		System.out.println("Class_ Symbol Table PreOrder: \n"+SemantState.getInstance().getScopeManager());
		return null;
	}

	@Override
	public Object onVisitPreOrder(Formal formal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onVisitPreOrder(Case branch) {
		AbstractSymbol static_type_symbol = branch.getReturnType();
		try
		{
			TypeCheckerHelper.validateType(static_type_symbol);
		}
		catch(Exception e)
		{
		}
		
		semant_state.getScopeManager().enterScope();
		
		semant_state.getScopeManager().addId(branch.getName(), ClassTable.getInstance().lookup(static_type_symbol));
		
		return null;
	}

	
	
	@Override
	public Object onVisitPreOrder(Expression expr) {
		return preorder_binder.execute(expr);
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

