/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

// This is a project skeleton file

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;


/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {
    private int semantErrors;
    private PrintStream errorStream;
    
    /**
     * Directed Acyclic Graph used to represent the hierarchy existing 
     * between the classes and their types.
     * Needed to calculate the LUB of two classes.
     */
    private DefaultDirectedGraph<AbstractSymbol, DefaultEdge> dag;
    
    /**
     * Hash Table containing the association 
     * < ClassName, ReferenceToClassNodeInAST >
     * 
     * It simplifies retrieving what fields and methods
     * a certain class offers, keeping in mind inheritance
     * 
     * What about a more meaningful name? xD
     * classRegister sounds nice to me :)
     */
    private HashMap<AbstractSymbol, Class_> table; 

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     * */
    private void installBasicClasses() {
		AbstractSymbol filename 
		    = AbstractTable.stringtable.addString("<basic class>");
		
		// The following demonstrates how to create dummy parse trees to
		// refer to basic Cool classes.  There's no need for method
		// bodies -- these are already built into the runtime system.
	
		// IMPORTANT: The results of the following expressions are
		// stored in local variables.  You will want to do something
		// with those variables at the end of this method to make this
		// code meaningful.
	
		// The Object class has no parent class. Its methods are
		//        cool_abort() : Object    aborts the program
		//        type_name() : Str        returns a string representation 
		//                                 of class name
		//        copy() : SELF_TYPE       returns a copy of the object
	
		class_c Object_class = 
		    new class_c(0, 
			       TreeConstants.Object_, 
			       TreeConstants.No_class,
			       new Features(0)
				   .appendElement(new method(0, 
						      TreeConstants.cool_abort, 
						      new Formals(0), 
						      TreeConstants.Object_, 
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.type_name,
						      new Formals(0),
						      TreeConstants.Str,
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.copy,
						      new Formals(0),
						      TreeConstants.SELF_TYPE,
						      new no_expr(0))),
			       filename);
		
		// The IO class inherits from Object. Its methods are
		//        out_string(Str) : SELF_TYPE  writes a string to the output
		//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
		//        in_string() : Str            reads a string from the input
		//        in_int() : Int                "   an int     "  "     "
	
		class_c IO_class = 
		    new class_c(0,
			       TreeConstants.IO,
			       TreeConstants.Object_,
			       new Features(0)
				   .appendElement(new method(0,
						      TreeConstants.out_string,
						      new Formals(0)
							  .appendElement(new formalc(0,
									     TreeConstants.arg,
									     TreeConstants.Str)),
						      TreeConstants.SELF_TYPE,
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.out_int,
						      new Formals(0)
							  .appendElement(new formalc(0,
									     TreeConstants.arg,
									     TreeConstants.Int)),
						      TreeConstants.SELF_TYPE,
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.in_string,
						      new Formals(0),
						      TreeConstants.Str,
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.in_int,
						      new Formals(0),
						      TreeConstants.Int,
						      new no_expr(0))),
			       filename);
	
		// The Int class has no methods and only a single attribute, the
		// "val" for the integer.
	
		class_c Int_class = 
		    new class_c(0,
			       TreeConstants.Int,
			       TreeConstants.Object_,
			       new Features(0)
				   .appendElement(new attr(0,
						    TreeConstants.val,
						    TreeConstants.prim_slot,
						    new no_expr(0))),
			       filename);
	
		// Bool also has only the "val" slot.
		class_c Bool_class = 
		    new class_c(0,
			       TreeConstants.Bool,
			       TreeConstants.Object_,
			       new Features(0)
				   .appendElement(new attr(0,
						    TreeConstants.val,
						    TreeConstants.prim_slot,
						    new no_expr(0))),
			       filename);
	
		// The class Str has a number of slots and operations:
		//       val                              the length of the string
		//       str_field                        the string itself
		//       length() : Int                   returns length of the string
		//       concat(arg: Str) : Str           performs string concatenation
		//       substr(arg: Int, arg2: Int): Str substring selection
	
		class_c Str_class =
		    new class_c(0,
			       TreeConstants.Str,
			       TreeConstants.Object_,
			       new Features(0)
				   .appendElement(new attr(0,
						    TreeConstants.val,
						    TreeConstants.Int,
						    new no_expr(0)))
				   .appendElement(new attr(0,
						    TreeConstants.str_field,
						    TreeConstants.prim_slot,
						    new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.length,
						      new Formals(0),
						      TreeConstants.Int,
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.concat,
						      new Formals(0)
							  .appendElement(new formalc(0,
									     TreeConstants.arg, 
									     TreeConstants.Str)),
						      TreeConstants.Str,
						      new no_expr(0)))
				   .appendElement(new method(0,
						      TreeConstants.substr,
						      new Formals(0)
							  .appendElement(new formalc(0,
									     TreeConstants.arg,
									     TreeConstants.Int))
							  .appendElement(new formalc(0,
									     TreeConstants.arg2,
									     TreeConstants.Int)),
						      TreeConstants.Str,
						      new no_expr(0))),
			       filename);
	
		/* Do somethind with Object_class, IO_class, Int_class,
	           Bool_class, and Str_class here */
	
			//Object deve essere registrato a mano perche' non ha parent
			dag.addVertex(TreeConstants.Object_);
			table.put(TreeConstants.Object_, Object_class);
	
		
			//registerClass(TreeConstants.Object_, Object_class, TreeConstants.No_class);
			registerClass(TreeConstants.IO, IO_class, TreeConstants.Object_);
			registerClass(TreeConstants.Int, Int_class, TreeConstants.Object_);
			registerClass(TreeConstants.Bool, Bool_class, TreeConstants.Object_);
			registerClass(TreeConstants.Str, Str_class, TreeConstants.Object_);
			
		
			
	
    }
	/**
	 * This method registers in the class table and its dag, the given class 
	 * described by its reference, its node and its parent's reference
	 * @param cls reference to class
	 * @param impl reference to node
	 * @param parent reference to the parent
	 */
    public void registerClass(AbstractSymbol cls, Class_ impl, AbstractSymbol parent)
    {
    	table.put(cls, impl);
    	dag.addVertex(cls);
    
		//dag.addEdge(cls, parent);
	
    }
    
    /**
     * This method registers in the class table and its dag, the given class 
	 * described by its reference, its node. It sets the parent by calculating it.
     * @param cls reference to class
     * @param impl reference to node
     * 
     */
    public void registerClass(AbstractSymbol cls, Class_ impl)
    {
    	registerClass(cls, impl, impl.getParent());
    }
    
    /**
     * This method registers in the class table and its dag, the given class 
	 * described by its node
     * @param impl reference to node
     */
    public void registerClass(Class_ impl)
    {
    	registerClass(impl.getName(), impl, impl.getParent());
    }

    
    private static ClassTable instance = null;
    
    public static ClassTable getInstance()
    {
    	if (instance == null)
    		instance = new ClassTable();
    	return instance;
    }

    private ClassTable() 
    {
    	semantErrors = 0;
		errorStream = System.err;
		
		/* fill this in */
		table = new HashMap<AbstractSymbol, Class_>();
		dag = new DefaultDirectedGraph<AbstractSymbol, DefaultEdge>(DefaultEdge.class);
	
		this.installBasicClasses();
	
    }

    /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(Class_ c) {
    	return semantError(c.getFilename(), c.getName(),  c);
    }
    
    /** Prints line number and file name of the given class,
    * prints the custom message
    * Also increments semantic error count.
    *
    * @param c the class
    * @param msg the message to be printed
    * @return a print stream to which the rest of the error message is
    * to be printed.
    *
    * */
   public PrintStream semantError(Class_ c, String msg) {
	   PrintStream stream = semantError(c.getFilename(), c.getName(),  c);
	   stream.println(msg);
	   return stream;
   }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, AbstractSymbol class_name, TreeNode t) {
    	errorStream.print(filename + ":" + class_name  + ":" + t.getLineNumber() + ": ");
    	return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
    	semantErrors++;
    	return errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
    	return semantErrors != 0;
    }
    
	public void dump() 
	{
		for (AbstractSymbol sym : table.keySet())
		{
			System.out.println(sym.getString());
		}
		System.out.println(dag.toString());
	}
	
	/**
	 * This method checks if there is a cycle in the inheritance graph
	 * @return true if there is a cycle
	 */
	public void validateDag() 
	{
		CycleDetector<AbstractSymbol, DefaultEdge> detector = new CycleDetector<AbstractSymbol, DefaultEdge>(dag); 
		if (detector.detectCycles())
		{
			//semantError(c.getFilename(), c.getName(), c);
			Set<AbstractSymbol> bad_vertices = detector.findCycles();
			for (AbstractSymbol sym : bad_vertices)
			{
				Class_ cls = table.get(sym);
				semantError(cls, "creates a cycle");
			}
		}
	}
	
	/**
	 * This method checks if a class has a valid parent
	 * if such situation happens, a semantError is raised
	 * otherwise, edge (node, parent) is added in the inheritance graph
	 */
	public void validateTable() 
	{
		for (AbstractSymbol sym : table.keySet())
		{
			if (sym.getString().equals("Object"))
				continue;
			Class_ cls = table.get(sym);
			AbstractSymbol parent = cls.getParent();
			if (!table.containsKey(parent))
			{
				semantError(cls, "invalid parent");
			}
			else
			{
				dag.addEdge(cls.getName(), parent);
			}
		}
		
	}
	
	/**
	 * This Method validates the classes table and analyses the produced graph for cycles
	 */
	public void validate()
	{
		validateTable();
		validateDag();
	}
	
	/**
	 * this method finds and retrieves the ast node in the table associated to the symbol
	 * @param symb the symbol to lookup
	 * @return the ast node associated to the symbol
	 */
	public Class_ lookup(AbstractSymbol symb)
	{
		if (table.containsKey(symb))
			return table.get(symb);
		return null;
	}
	
	/**
	 * checks if the given symbol is registered into the table
	 * @param symb the symbol to check
	 * @return true if the symbol is registered
	 */
    public boolean isClassRegistered(AbstractSymbol symb)
    {
    	return lookup(symb) != null;
    }    
    
    /**
     * It returns the inheritance list for a given symbol, that is
     * the list of all parents for the symbol
     * @param sym the symbol to calculate the list
     * @return the inheritance list
     */
    public ArrayList<AbstractSymbol> getParents(AbstractSymbol sym)
    {
    	ArrayList<AbstractSymbol> ret = new ArrayList<AbstractSymbol>();
    	AbstractSymbol object = AbstractTable.idtable.addString("Object");
    	AbstractSymbol parent = sym;
    	while (!parent.equals(object))
    	{
    		parent = table.get(parent).getParent();
    		ret.add(parent);
    	}
    	return ret;
    }
    
    /**
     * this method calculates the least upper bound for the given symbols, that is
     * the closer common parent for the symbols
     * @param symbols the symbols to calculate the lub
     * @return the lub for the symbols
     */
    public AbstractSymbol leastUpperBound(AbstractSymbol... symbols)
    {
    	HashMap<AbstractSymbol, ArrayList<AbstractSymbol>> inheritance_list = 
    			new HashMap<AbstractSymbol, ArrayList<AbstractSymbol>>();
    	
    	for (AbstractSymbol sym : symbols)
    	{
    		inheritance_list.put(sym, getParents(sym));
    		inheritance_list.get(sym).add(0, sym); // occorre aggiungere anche se stesso alla lista dei parent 	
    	}
    	
    	AbstractSymbol temp = leastUpperBound(inheritance_list.get(symbols[0]), inheritance_list.get(symbols[1]));
    	for (int i = 2; i < symbols.length; i++)
    	{
    		if (!inheritance_list.containsKey(temp))
    		{
    			inheritance_list.put(temp, getParents(temp));
    			inheritance_list.get(temp).add(0, temp); 
    		}
    		temp = leastUpperBound(inheritance_list.get(temp), inheritance_list.get(symbols[i]));
    	}
    	
    	return temp;
    }
    
    /**
     * this method helps in the leastUpperBound calculation process, it retrieves the closer common parent
     * for two given inheritance list symbols 
     * @param list_sym1 the inheritance list for the first symbol
     * @param list_sym2 the inheritance list for the second symbol
     * @return the lub for the 2 symbols
     */
    private AbstractSymbol leastUpperBound(ArrayList<AbstractSymbol> list_sym1, ArrayList<AbstractSymbol> list_sym2)
    {
    	for (AbstractSymbol it1 : list_sym1)
    	{
    		for (AbstractSymbol it2 : list_sym2)
    		{
    			if (it1.equals(it2))
    			{
    				return it1;
    			}
    		}
    	}    	
    	return AbstractTable.idtable.addString("Object");
    }
    
}
			  
    
