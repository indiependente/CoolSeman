import java.io.PrintStream;



class SemantErrorsManager
{
	private int semantErrors;
    private PrintStream errorStream;
    
    private static SemantErrorsManager instance = null;
    
    public static SemantErrorsManager getInstance()
    {
    	if (instance == null)
    		instance = new SemantErrorsManager();
    	return instance;
    }
    
    
    private SemantErrorsManager()
    {
    	semantErrors = 0;
		errorStream = System.err;
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
   	errorStream.print(filename + ":" + t.getLineNumber() + ": ");
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


	public void validate() {
		if (SemantErrorsManager.getInstance().errors()) {
			System.err.println("Compilation halted due to static semantic errors.");
			System.exit(1);
		}
		
	}
	
}
