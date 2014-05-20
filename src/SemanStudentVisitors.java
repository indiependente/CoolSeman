import java.util.HashMap;



abstract class Decorator
{
	protected HashMap<String, Object> data = null; 
	 /**
     * 
     * @param key
     * @param value
     */
    protected void decorate(String key, Object value)
    {
    	if (data == null)
    		data = new HashMap<String, Object>();
    	data.put(key, value);
    }
    
    public Object getData(String key)
    {
    	if (data != null && data.containsKey(key))
    		return data.get(key);
    	return null;
    }
}

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
	
}


interface IVisitable {
	Object accept(ITreeVisitor visitor);
}

interface IAction <T>
{
	void action(T obj);
}

class TypeSelector
{
	HashMap<Class<?>, IAction<Expression>> binded_actions = new HashMap<Class<?>, IAction<Expression>>();
	
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
	
	public <T extends Expression> void execute(T object)
	{
		getAction(object.getClass()).action(object);
	}
}


class ConcreteVisitor implements ITreeVisitor
{
	static TypeSelector selector = new TypeSelector();
	
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
				//System.out.println("object");
				System.out.println(data.toString());
			}
			
		});
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
	
}


