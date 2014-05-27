import java.util.HashMap;


class SemanStudentFeaturesTable
{
	
	private HashMap< AbstractSymbol , Feature > attributes;
	private HashMap< AbstractSymbol , Feature > methods;
	private Class_ owner;
	
	public SemanStudentFeaturesTable(Class_ c)
	{
		this.owner = c;
		attributes = new HashMap< AbstractSymbol , Feature >();
		methods = new HashMap< AbstractSymbol , Feature >();
		
	}
	
	
}
