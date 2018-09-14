import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {

	private Map<AbstractSymbol, class_c> classMap;
    private  List<AbstractSymbol> illegalIdentifiers;

	// Referencia de soporte para analizar el tipo de datos self:
	private AbstractSymbol currentClass;

    private int semantErrors;
    private PrintStream errorStream;

	/** Constructor. */
    public ClassTable(Classes cls) {
		semantErrors = 0;
		errorStream = System.err;

		// Inicializa los identificadores ilegales:
		illegalIdentifiers = new ArrayList<AbstractSymbol>();
		illegalIdentifiers.add(TreeConstants.self);
		illegalIdentifiers.add(TreeConstants.SELF_TYPE);

		classMap = new HashMap<AbstractSymbol, class_c>();

		// Crea las clases básicas:
		installBasicClasses();

		// Añade las clases pasadas como parámetro del constructur a las
		// estructuras de datos y verifica que no haya más de una clase con el
		// mismo nombre:
		for(Enumeration<class_c> e = cls.getElements(); e.hasMoreElements();) {
		    class_c c = e.nextElement();
			if (classMap.put(c.getName(), c) != null) {
				semantError(c.getFilename(), c)
				    .println("Class " + c.getName().getString() +
					     " was previously defined.");
		    }
		}

		// Verifica que no haya bucles en la jerarquía de clases:
		for(Enumeration<class_c> e = cls.getElements(); e.hasMoreElements();) {
			checkInheritance(e.nextElement().getName());
		}

		if(errors()) {
		    System.err.println(
				"Compilation halted due to static semantic errors.");
		    System.exit(1);
		}
    }

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String). Please note: as is this method does not
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

		// Registra las clases generadas:
		classMap.put(TreeConstants.Object_, Object_class);
		classMap.put(TreeConstants.IO, IO_class);
		classMap.put(TreeConstants.Int, Int_class);
		classMap.put(TreeConstants.Bool, Bool_class);
		classMap.put(TreeConstants.Str, Str_class);

		// Análisis semántico de las clases generadas:
		TableWrapper tables = new TableWrapper(this);
		Object_class.semant(tables);
		IO_class.semant(tables);
		Int_class.semant(tables);
		Bool_class.semant(tables);
		Str_class.semant(tables);

		// Registra como términos de identificador inválidos los nombres
		// utilizados para declarar las clases básicas:
		illegalIdentifiers.add(TreeConstants.Object_);
		illegalIdentifiers.add(TreeConstants.IO);
		illegalIdentifiers.add(TreeConstants.Int);
		illegalIdentifiers.add(TreeConstants.Bool);
		illegalIdentifiers.add(TreeConstants.Str);
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
    public PrintStream semantError(class_c c) {
		return semantError(c.getFilename(), c);
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
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
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

	// < cond.lub()
	// < new_.semant()
    public class_c getClass_c(AbstractSymbol name) {
		return classMap.get(name);
    }

	// < cond.lub()
	public AbstractSymbol getCurrentClass() {
		return this.currentClass;
	}

	// < dispatch.semant()
	// < static_dispatch.semant()
	public Feature getFeature(AbstractSymbol className,
		AbstractSymbol featureName) {
		if(className.equals(TreeConstants.No_class)) {
			return null;
		} else if(isSelfType(className)) {
			return getFeature(getCurrentClass(), featureName);
		} else {
			class_c c = getClass_c(className);
			Feature feature = c.getFeatures().getFeature(featureName);
			return (feature == null)
				? getFeature(c.getParent(), featureName)
				: feature;
		}
	}

	// < class_c.semant()
	public List<AbstractSymbol> getIllegalIdentifiers() {
		return illegalIdentifiers;
	}

	// < new_.semant()
    public boolean isSelfType(AbstractSymbol symbol) {
		return (TreeConstants.self.equals(symbol) ||
		    TreeConstants.SELF_TYPE.equals(symbol));
    }

	// cond.lub()
    public boolean isSubtypeOf(AbstractSymbol c1, AbstractSymbol c2) {
		if(c1.equals(c2)) {
			return true;
		}
		else if(c1.equals(TreeConstants.No_type)) {
			return true;
		}
		else if(c2.equals(TreeConstants.No_type)) {
			return false;
		}
 		else if(c1.equals(TreeConstants.No_class)) {
			return false;
		}
		else if(isSelfType(c1)) {
			return isSubtypeOf(getCurrentClass(), c2);
		}
		else {
			return isSubtypeOf(getClass_c(c1).getParent(), c2);
		}
    }

	// < class_c.semant()
    public void setCurrentClass(AbstractSymbol symbol) {
		this.currentClass = symbol;
    }

	private void checkInheritance(AbstractSymbol a) {
		checkInheritance(a, new ArrayList<AbstractSymbol>(), a);
	}

	private void checkInheritance(AbstractSymbol a,
		List<AbstractSymbol> visited, AbstractSymbol superClass) {
		class_c c = getClass_c(a);
		// Raíz de la jerarquía de clases:
		if (a.equals(TreeConstants.No_class)) {
			return;
		}
		// El objeto hereda de un tipo no permitido:
		else if(a.equals(TreeConstants.Int) || a.equals(TreeConstants.Bool)
			|| a.equals(TreeConstants.Str) || a.equals(TreeConstants.self)
			|| a.equals(TreeConstants.SELF_TYPE)) {
			class_c parent = getClass_c(superClass);
			semantError(parent.getFilename(), parent)
				.println("Illegal inheritance from fundamental type: "
					+ superClass.getString() + " inherits " + a.getString());
		}
		// Hay un bucle en la jerarquía de clases:
		else if(visited.contains(a)) {
			semantError(c.getFilename(), c)
				.println("Illegal cyclic inheritance at " + c);
		}
		// Definición incorrecta de la jerarquía de clases:
		else if(c == null) {
			class_c s = getClass_c(superClass);
			semantError(s.getFilename(), s)
				.println("Illegal hierarchy definition");
		}
		// Llamada recursiva:
		else {
			visited.add(a);
			checkInheritance(c.getParent(), visited, superClass);
		}
	}

}
