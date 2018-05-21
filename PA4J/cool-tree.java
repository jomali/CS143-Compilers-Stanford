
abstract class Class_ extends TreeNode {
    public abstract void semant(ClassTable classTable);
}

class Classes extends ListNode {

    public void semant(ClassTable classTable) {
	// Parse classes
	for(Enumeration<class_c> e=getElements(); e.hasMoreElements();) {
	    (e.nextElement()).semant(classTable);
	}
    }

    public boolean containsClass(AbstractSymbol symbol) {

	for(Enumeration<class_c> e=getElements();
	    e.hasMoreElements();) {
	    if(e.nextElement().getName().equals(symbol)) {
		return true;
	    }
	}

	return false;
    }

    public boolean containsMethod(AbstractSymbol symbol) {

	for(Enumeration<class_c> e=getElements();
	    e.hasMoreElements();) {

	    if(e.nextElement().containsMethod(symbol)) {
		return true;
	    }
	}

	return false;
    }
}

abstract class Feature extends TreeNode {

    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
    public abstract AbstractSymbol get_type();
    public abstract void logFeature(SymbolTable objectTable,
				    SymbolTable methodTable,
				    ClassTable classTable,
				    class_c c);

}

class Features extends ListNode {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

		for(Enumeration<Feature> e=getElements();
		    e.hasMoreElements();) {
		    (e.nextElement()).semant(objectTable,
					     methodTable,
					     classTable,
					     c);
		}
    }

    /**
     * Log all features in list to current
     * Symbol Table scopes
     */
    public void logFeatures(SymbolTable objectTable,
			    SymbolTable methodTable,
			    ClassTable classTable,
			    class_c c) {

	for(Enumeration<Feature> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).logFeature(objectTable,
					 methodTable,
					 classTable,
					 c);
	}
    }

    /**
     * Utility method for getting instance of feature
     */
    public Feature getFeature(AbstractSymbol featureName) {
	for(Enumeration<Feature> e=getElements();
	    e.hasMoreElements();) {

	    // Compare name of feature
	    Feature feature = e.nextElement();
	    if(feature.getName().equals(featureName)) {
		return feature;
	    }
	}

	// If no feature found, return null
	return null;
    }
}

abstract class Formal extends TreeNode {

    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
    public abstract AbstractSymbol get_type();
    public abstract void compareArg(Expression expression,
				    ClassTable classTable,
				    class_c c);
}


class Formals extends ListNode {



    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Formal> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				     methodTable,
				     classTable,
				     c);
	}
    }


    /**
     * Compare passed arguments to declared arguments
     */
    public void compareArgs(Expressions expressions,
			    ClassTable classTable,
			    class_c c) {
	if(expressions.getLength() != getLength()) {

	    classTable.semantError(c.getFilename(), c)
		.println("Wrong number of arguments passed to function: "
			 + " Expected " + getLength() + ", got "
			 + expressions.getLength());

	    return;
	}

	// Compare all passed arguments to formal arguments
	Enumeration<Formal> e=getElements();
	Enumeration<Expression> exs=expressions.getElements();
	while(e.hasMoreElements()) {
	    (e.nextElement()).compareArg(exs.nextElement(),
					 classTable,
					 c);
	}
    }
}

abstract class Expression extends TreeNode {

    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
}

class Expressions extends ListNode {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Expression> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				      methodTable,
				      classTable,
				      c);
	}
    }
}

abstract class Case extends TreeNode {

    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
    public abstract AbstractSymbol get_type();
}


class Cases extends ListNode {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Case> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				     methodTable,
				     classTable,
				     c);
	}
    }

    public void checkTypes(SymbolTable objectTable,
			   SymbolTable methodTable,
			   ClassTable classTable,
			   class_c c) {
	Vector<AbstractSymbol> v = new Vector<AbstractSymbol>();

	for(Enumeration<Case> e=getElements();
	    e.hasMoreElements();) {
	    AbstractSymbol _type = e.nextElement().get_type();

	    if(v.contains(_type)) {
		classTable.semantError(c.getFilename(), c)
		    .println("Multiple cases for one type: " +
			     _type.getString());
		// } else if(!classTable.isSubtypeOf()) {

	    } else {
		v.add(_type);
	    }
	}
    }

    public AbstractSymbol get_type(SymbolTable objectTable,
				   SymbolTable methodTable,
				   ClassTable classTable,
				   typcase t) {

	Enumeration<Case> e = getElements();
	if(!e.hasMoreElements()) {
	    return TreeConstants.Object_;
	}

	// Get the first valid element type
	AbstractSymbol return_type = null;
	for(; e.hasMoreElements();) {
	    AbstractSymbol temp = e.nextElement().get_type();
	    if(classTable.isSubtypeOf(t.get_expr_type(), temp)) {
		return_type = temp;
		break;
	    }
	}

	// If no valid type found, return No_type
	if(return_type == null)
	    return TreeConstants.Object_;

	// Get valid return type
	e = getElements();
	for(; e.hasMoreElements();) {
	    AbstractSymbol temp = e.nextElement().get_type();

	    // Lub of all legal types is the return type
	    if(classTable.isSubtypeOf(temp, t.get_expr_type())) {
		return_type = classTable.lub(return_type,
					     temp);
	    }
	}

	return return_type;
    }
}


class programc extends Program {

    public void semant() {

	if(ClassTable.DEBUG) {
	    System.out.println("Analyzing Program...");
	}

	// Checking class inheritance heirarchy is deferred to
	// ClassTable
	ClassTable classTable = new ClassTable(classes);

	// Analyze class list
	classes.semant(classTable);

	if(!classes.containsClass(TreeConstants.Main)) {
	    classTable.semantError()
		.println("Class Main is not defined.");

	} else if(!classes.containsMethod(TreeConstants.main_meth)) {
	    classTable.semantError()
		.println("Method main is not defined.");
	}

	if (classTable.errors()) {
	    System.err.println("Compilation halted due to static semantic errors.");
	    System.exit(1);
	}
    }

}

class class_c extends Class_ {

    /**
     * Collect all inherited features in current class's
     * inheritance heirarchy
     */
    public void propogateInheritedFeatures(SymbolTable objectTable,
					   SymbolTable methodTable,
					   ClassTable classTable,
					   AbstractSymbol a) {

	if(a.equals(TreeConstants.No_class)) {
	    // At top of inheritance heirarchy
	    return;
	} else {
	    // For every parent class, log all class
	    // features into object and method table
	    // scopes
	    class_c c = classTable.getClass_c(a);
	    propogateInheritedFeatures(objectTable,
				       methodTable,
				       classTable,
				       c.getParent());
	    objectTable.enterScope();
	    methodTable.enterScope();
	    c.logFeatures(objectTable,
			  methodTable,
			  classTable,
			  c);
	}
    }

    /**
     * Log all features in current class into
     * Symbol table scopes
     */
    public void logFeatures(SymbolTable objectTable,
			    SymbolTable methodTable,
			    ClassTable classTable,
			    class_c c) {

	features.logFeatures(objectTable,
			     methodTable,
			     classTable,
			     c);
    }

    /**
     * Perform semantic analysis on current class
     */
    public void semant(ClassTable classTable) {

	if(ClassTable.DEBUG) {
	    System.out.println("-Class: " +
			       name.getString() + ", " +
			       getLineNumber());
	}

	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(getFilename(), this)
		.println("Illegal class Identifier " + name);
	}

	// Set the current class
	classTable.setCurrentClass(name);

	// Create a new item scope for holding local variables
	SymbolTable objectTable = new SymbolTable();
	SymbolTable methodTable = new SymbolTable();

	objectTable.enterScope();
	methodTable.enterScope();

	// Collect all inherited features into
	// current Symbol Table scopes
	propogateInheritedFeatures(objectTable,
				   methodTable,
				   classTable,
				   name);

	// Analyze list of features
	features.semant(objectTable,
			methodTable,
			classTable,
			this);

	methodTable.exitScope();
	objectTable.exitScope();
    }


    @Override
    public boolean equals(Object o) {
	return (o instanceof class_c) &&
	    ((class_c) o).getName().equals(name);
    }

    /**
     * Simple utility method for getting an instance of
     * a feature
     */
    public Feature getFeature(AbstractSymbol featureName) {
	return features.getFeature(featureName);
    }

}


class method extends Feature {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("--Method: " +
			       name.getString() + ", " +
			       getLineNumber());
	}

	objectTable.enterScope();

	// Check for illegal method names
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal name for function");
	}

	// Analyze arguments
	formals.semant(objectTable,
		       methodTable,
		       classTable,
		       c);

	// Analyze method body
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	if(!classTable.isSubtypeOf(expr.get_type(),
				   return_type)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Inferred return type " +
			 expr.get_type().getString() +
			 " of method " + name.getString() +
			 " does not conform to declared type " +
			 return_type.getString());
	}

	if(classTable.getFeature(c.getParent(), name) != null) {
	    // Method is overriding superclass method

	    method m = (method) classTable.getFeature(c.getParent(), name);

	    if(!m.get_type().equals(return_type)) {
		// Check return types are the same

		classTable.semantError(c.getFilename(), c)
		    .println("Overriding method " + name.getString() +
			     " has unexpected return type " +
			     return_type.getString());
		return;

	    } else if(m.getFormals().getLength() != formals.getLength()) {
		// Check argument lists are the same length

		classTable.semantError(c.getFilename(), c)
		    .println("Differing lengths to formal params in "
			     + " overriding method " + name.getString());
		return;

	    } else {
		// Check types of overriding parameter list

		Formals f = m.getFormals();
		Enumeration<Formal> e1 = f.getElements();
		Enumeration<Formal> e2 = formals.getElements();

		for(; e1.hasMoreElements() && e2.hasMoreElements();) {
		    AbstractSymbol t1 = e1.nextElement().get_type();
		    AbstractSymbol t2 = e2.nextElement().get_type();

		    if(!t1.equals(t2)) {
			classTable.semantError(c.getFilename(), c)
			    .println("Illegal argument in overriding method "
				     + name.getString() + " Expected "
				     + t1.getString() + ", Found "
				     + t2.getString());
		    }
		}
	    }
	}

	objectTable.exitScope();
    }


    /**
     * Get return type
     */
    public AbstractSymbol get_type() {
	return this.return_type;
    }


    /**
     * Log feature in table scopes
     */
    public void logFeature(SymbolTable objectTable,
			   SymbolTable methodTable,
			   ClassTable classTable,
			   class_c c) {

	// If method already exists in scope, report error
	if(methodTable.probe(name) != null) {

	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier: multiply defined method "
			 + name.getString());
	    return;
	}

	if(classTable.isSelfType(return_type)) {
	    methodTable.addId(name, c.getName());
	} else {
	    methodTable.addId(name, return_type);
	}
    }

    /**
     * Method for comparing passed arguments of
     * dispatch to declared arguments
     */
    public void compareArgs(Expressions expressions,
			    ClassTable classTable,
			    class_c c) {

	formals.compareArgs(expressions,
			    classTable,
			    c);
    }

    public Formals getFormals() {
	return this.formals;
    }
}

class attr extends Feature {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    protected Expression init;
    /** Creates "attr" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      * @param a2 initial value for init
      */
    public attr(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        init = a3;
    }
    public TreeNode copy() {
        return new attr(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)init.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "attr\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
        init.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_attr");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
	init.dump_with_types(out, n + 2);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("--Attribute: " +
			       name.getString() + ", " +
			       type_decl.getString() + ", " +
			       getLineNumber());
	}

	// Check for illegal name of attribute
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal name for class member");
	}

	init.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	objectTable.addId(name, type_decl);

	if(!classTable.isSubtypeOf(init.get_type(),
				   type_decl)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Illegal assignment: " +
			 type_decl.getString() + " = " +
			 init.get_type().getString());

	    init.set_type(TreeConstants.Object_);
	}
    }

    public AbstractSymbol get_type() {
	return this.type_decl;
    }

    /**
     * Log feature into symbol table scope
     */
    public void logFeature(SymbolTable objectTable,
			   SymbolTable methodTable,
			   ClassTable classTable,
			   class_c c) {

	// Check if attr is already defined in current scope
	if(objectTable.probe(name) != null) {

	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier: multiply define attribute "
			 + name.getString());

	} else if(objectTable.lookup(name) != null) {

	    classTable.semantError(c.getFilename(), c)
		.println("Overriding member of parent class: " +
			 name.getString());

	} else {
	    objectTable.addId(name, type_decl);
	}
    }

    /**
     * Get feature name
     */
    public AbstractSymbol getName() {
	return this.name;
    }
}


/** Defines AST constructor 'formalc'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class formalc extends Formal {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    /** Creates "formalc" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      */
    public formalc(int lineNumber, AbstractSymbol a1, AbstractSymbol a2) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
    }
    public TreeNode copy() {
        return new formalc(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "formalc\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_formal");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Formal: "
			       + name.getString() + ", " +
			       getLineNumber());
	}

	// Check for illegal name
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal name for formal argument "
			 + name.getString());
	}

	if(classTable.isSelfType(type_decl)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Illegal type for parameter: "
			 + type_decl.getString());
	}

	// If item already exists, throw multiply defined error
	if(objectTable.probe(name) != null) {
	    classTable.semantError(c.getFilename(), c)
		.println("Multiply defined argument "
			 + name.getString());

	    return;
	} else {
	    objectTable.addId(name, type_decl);
	}
    }

    public AbstractSymbol get_type() {
	return type_decl;
    }


    /**
     * Compare an expression to declared argument
     */
    public void compareArg(Expression expression,
			   ClassTable classTable,
			   class_c c) {

	// Compare types of argument
	if(!classTable.isSubtypeOf(expression.get_type(),
				   type_decl)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Illegal argument: "
			 + "Expected " + type_decl.getString()
			 + ", Found " + expression.get_type().getString());
	}
    }
}


/** Defines AST constructor 'branch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class branch extends Case {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    protected Expression expr;
    /** Creates "branch" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      * @param a2 initial value for expr
      */
    public branch(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        expr = a3;
    }
    public TreeNode copy() {
        return new branch(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "branch\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
        expr.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_branch");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
	expr.dump_with_types(out, n + 2);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Branch: " +
			       name.getString() + ", " +
			       getLineNumber());
	}

	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier in case: " +
			 name.getString());
	}

	// Enter a new scope to evaluate expression
	objectTable.enterScope();

	// Bind expression to identifier of case branch
	objectTable.addId(name, type_decl);

	// Analyze expression object
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	objectTable.exitScope();
    }

    public AbstractSymbol get_type() {
	return type_decl;
    }
}


/** Defines AST constructor 'assign'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class assign extends Expression {
    protected AbstractSymbol name;
    protected Expression expr;
    /** Creates "assign" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for expr
      */
    public assign(int lineNumber, AbstractSymbol a1, Expression a2) {
        super(lineNumber);
        name = a1;
        expr = a2;
    }
    public TreeNode copy() {
        return new assign(lineNumber, copy_AbstractSymbol(name), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "assign\n");
        dump_AbstractSymbol(out, n+2, name);
        expr.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_assign");
        dump_AbstractSymbol(out, n + 2, name);
	expr.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Assignment: " +
			       name.getString() + ", " +
			       getLineNumber());
	}

	// Evaluate expression first
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	// Check that identifier exists
	AbstractSymbol a = (AbstractSymbol) objectTable.lookup(name);

	// Cannot assign to illegal identifier
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Cannot assign to 'self'.");

	    set_type(TreeConstants.Object_);
	} else if(a == null) {
	    classTable.semantError(c.getFilename(), c)
		.println("Assignment to non-existent variable "
			 + name.getString());

	    set_type(TreeConstants.Object_);

	} else if(!classTable.isSubtypeOf(expr.get_type(), a)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Assignment to illegal type: "
			 + a.getString() + " <- "
			 + expr.get_type().getString());

	    set_type(TreeConstants.Object_);

	} else {
	    set_type(a);
	}
    }
}


/** Defines AST constructor 'static_dispatch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class static_dispatch extends Expression {
    protected Expression expr;
    protected AbstractSymbol type_name;
    protected AbstractSymbol name;
    protected Expressions actual;
    /** Creates "static_dispatch" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for type_name
      * @param a2 initial value for name
      * @param a3 initial value for actual
      */
    public static_dispatch(int lineNumber, Expression a1, AbstractSymbol a2, AbstractSymbol a3, Expressions a4) {
        super(lineNumber);
        expr = a1;
        type_name = a2;
        name = a3;
        actual = a4;
    }
    public TreeNode copy() {
        return new static_dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(type_name), copy_AbstractSymbol(name), (Expressions)actual.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "static_dispatch\n");
        expr.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, type_name);
        dump_AbstractSymbol(out, n+2, name);
        actual.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_static_dispatch");
	expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, type_name);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Static Dispatch: " +
			       name.getString());
	}

	// Remember: <expr>@<type>.<id>(<actual>);
	// Evaluate expression that static method is called on
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	// Evaluate list of arguments
	actual.semant(objectTable,
		      methodTable,
		      classTable,
		      c);

	// Check if method exists in scope of referenced object
	if(classTable.getClass_c(type_name) != null) {
	    Feature feature = classTable.getFeature
		(type_name, name);

	    if(!classTable.isSubtypeOf(expr.get_type(), type_name)) {
		// Check if calling class is a legal subtype
		// of specified class
		classTable.semantError(c.getFilename(), c)
		    .println("Illegal static dispatch: Referenced type "
			     + expr.get_type().getString()
			     + " is not of specified type "
			     + type_name.getString());

		set_type(TreeConstants.Object_);

	    } else if(feature == null) {
		// Check if called method exists
		classTable.semantError(c.getFilename(), c)
		    .println("Call to non-existent static method "
			     + name);

		set_type(TreeConstants.Object_);

	    } else if(feature instanceof method) {
		// Compare passed arguments of dispatch to
		// declared arguments of method
		method m = (method) feature;
		m.compareArgs(actual,
			      classTable,
			      c);

		set_type(m.get_type());

	    } else {
		// Calling non-method feature
		classTable.semantError(c.getFilename(), c)
		    .println("Illegal dispatch to attribute: "
			     + name.getString());

		set_type(TreeConstants.Object_);
	    }
	} else {
	    // Item which method was called on is not a class
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal static dispatch to non-class object "
			 + type_name);

	    set_type(TreeConstants.Object_);
	}
    }
}


/** Defines AST constructor 'dispatch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class dispatch extends Expression {
    protected Expression expr;
    protected AbstractSymbol name;
    protected Expressions actual;
    /** Creates "dispatch" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for name
      * @param a2 initial value for actual
      */
    public dispatch(int lineNumber, Expression a1, AbstractSymbol a2, Expressions a3) {
        super(lineNumber);
        expr = a1;
        name = a2;
        actual = a3;
    }
    public TreeNode copy() {
        return new dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(name), (Expressions)actual.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "dispatch\n");
        expr.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, name);
        actual.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_dispatch");
	expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Dispatch: " +
			       name.getString());
	}

	// Evaluate Expression
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	actual.semant(objectTable,
		      methodTable,
		      classTable,
		      c);

	// Set type to return type
	Feature feature = classTable.getFeature(expr.get_type(), name);

	// Check if called referenced feature exists
	if(feature == null) {

	    classTable.semantError(c.getFilename(), c)
		.println("Call to non-existant method " +
			 name.getString() + " of class " +
			 expr.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else if(feature instanceof method) {

	    method m = (method) feature;

	    if(classTable.isSelfType(m.get_type())) {
		if(classTable.isSelfType(expr.get_type())) {
		    //set_type(c.getName());
		    set_type(TreeConstants.SELF_TYPE);
		} else {
		    set_type(expr.get_type());
		}
	    } else {
		set_type(m.get_type());
	    }

	    m.compareArgs(actual, classTable, c);

	} else {

	    classTable.semantError()
		.println("Illegal dispatch to attr: "
			 + name.getString());
	}
    }
}


/** Defines AST constructor 'cond'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class cond extends Expression {
    protected Expression pred;
    protected Expression then_exp;
    protected Expression else_exp;
    /** Creates "cond" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for pred
      * @param a1 initial value for then_exp
      * @param a2 initial value for else_exp
      */
    public cond(int lineNumber, Expression a1, Expression a2, Expression a3) {
        super(lineNumber);
        pred = a1;
        then_exp = a2;
        else_exp = a3;
    }
    public TreeNode copy() {
        return new cond(lineNumber, (Expression)pred.copy(), (Expression)then_exp.copy(), (Expression)else_exp.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "cond\n");
        pred.dump(out, n+2);
        then_exp.dump(out, n+2);
        else_exp.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_cond");
	pred.dump_with_types(out, n + 2);
	then_exp.dump_with_types(out, n + 2);
	else_exp.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---If: " + getLineNumber());
	}

	// Evaluate component expressions
	pred.semant(objectTable, methodTable, classTable, c);
	then_exp.semant(objectTable, methodTable, classTable, c);
	else_exp.semant(objectTable, methodTable, classTable, c);

	// Check type of bool expression
	if(!pred.get_type().equals(TreeConstants.Bool)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Expected boolean type in if statement; got "
			 + pred.get_type().getString());
	}

	// Set type to least upper bound of then and else
	set_type(classTable.lub(then_exp.get_type(),
				else_exp.get_type()));
    }
}


/** Defines AST constructor 'loop'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class loop extends Expression {
    protected Expression pred;
    protected Expression body;
    /** Creates "loop" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for pred
      * @param a1 initial value for body
      */
    public loop(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        pred = a1;
        body = a2;
    }
    public TreeNode copy() {
        return new loop(lineNumber, (Expression)pred.copy(), (Expression)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "loop\n");
        pred.dump(out, n+2);
        body.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_loop");
	pred.dump_with_types(out, n + 2);
	body.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Loop: " +
			       getLineNumber());
	}

	// Evaluate component expressions
	pred.semant(objectTable, methodTable, classTable, c);
	body.semant(objectTable, methodTable, classTable, c);

	// Check that predicate expression is boolean type
	if(!pred.get_type().equals(TreeConstants.Bool)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Expected boolean expression; Found "
			 + pred.get_type().getString());
	}

	// Set type to the type that body evaluates to
	set_type(body.get_type());
    }
}


/** Defines AST constructor 'typcase'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class typcase extends Expression {
    protected Expression expr;
    protected Cases cases;
    /** Creates "typcase" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for cases
      */
    public typcase(int lineNumber, Expression a1, Cases a2) {
        super(lineNumber);
        expr = a1;
        cases = a2;
    }
    public TreeNode copy() {
        return new typcase(lineNumber, (Expression)expr.copy(), (Cases)cases.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "typcase\n");
        expr.dump(out, n+2);
        cases.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_typcase");
	expr.dump_with_types(out, n + 2);
        for (Enumeration e = cases.getElements(); e.hasMoreElements();) {
	    ((Case)e.nextElement()).dump_with_types(out, n + 2);
        }
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Case: " +
			       getLineNumber());
	}

	// Evaluate component expressions
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	// Evaluate case expressions
	cases.semant(objectTable,
		     methodTable,
		     classTable,
		     c);

	cases.checkTypes(objectTable,
			 methodTable,
			 classTable,
			 c);

	// Type is the least upper bound of all expression
	// branches in list
	set_type(cases.get_type(objectTable,
				methodTable,
				classTable,
				this));
    }

    public AbstractSymbol get_expr_type() {
	return this.expr.get_type();
    }
}


/** Defines AST constructor 'block'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class block extends Expression {
    protected Expressions body;
    /** Creates "block" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for body
      */
    public block(int lineNumber, Expressions a1) {
        super(lineNumber);
        body = a1;
    }
    public TreeNode copy() {
        return new block(lineNumber, (Expressions)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "block\n");
        body.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_block");
        for (Enumeration e = body.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Expression Block: " +
			       getLineNumber());
	}

	objectTable.enterScope();

	// Evaluate all expressions in body list
	body.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	// Set type to type of body expression
	Expression e = (Expression) body
	    .getNth(body.getLength() - 1);

	set_type(e.get_type());
	objectTable.exitScope();
    }
}


class let extends Expression {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Let: " +
			       getLineNumber());
	}

	objectTable.enterScope();

	// Analyze initialization body
	init.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	if(classTable.checkIllegalIdentifier(identifier)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier in let assignment: "
			 + identifier.getString());
	} else {

	    // Check that type for declared argument matches
	    // initialization expression
	    if(!classTable.isSupertypeOf(type_decl,
					 init.get_type())) {
		classTable.semantError(c.getFilename(), c)
		    .println("Illegal assignment to type: "
			     + type_decl.getString() + " <- "
			     + init.get_type().getString());

	    } else {
		// Add identifier to object scope
		objectTable.addId(identifier, type_decl);
	    }
	}

	body.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	set_type(body.get_type());
	objectTable.exitScope();
    }
}




class lt extends Expression {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Less Than: " +
			       getLineNumber());
	}

	e1.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	e2.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in comparison expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


class eq extends Expression {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Equals: " +
			       getLineNumber());
	}

	e1.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	e2.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	if(!classTable.validComparisonTypes(e1, e2)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Invalid comparison between types: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
 	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


class leq extends Expression {

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Less Than Equals: " +
			       getLineNumber());
	}

	e1.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	e2.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in integer expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


/** Defines AST constructor 'comp'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class comp extends Expression {
    protected Expression e1;
    /** Creates "comp" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public comp(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new comp(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "comp\n");
        e1.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_comp");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("Complement: " +
			       getLineNumber());
	}

	e1.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	if(!e1.get_type().equals(TreeConstants.Bool)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in boolean expression: "
			 + e1.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}




/** Defines AST constructor 'new_'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class new_ extends Expression {
    protected AbstractSymbol type_name;
    /** Creates "new_" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for type_name
      */
    public new_(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        type_name = a1;
    }
    public TreeNode copy() {
        return new new_(lineNumber, copy_AbstractSymbol(type_name));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "new_\n");
        dump_AbstractSymbol(out, n+2, type_name);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_new");
	dump_AbstractSymbol(out, n + 2, type_name);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---New: " +
			       type_name.getString() + ", " +
			       getLineNumber());
	}

	if(classTable.isSelfType(type_name)) {
	    set_type(TreeConstants.SELF_TYPE);

	} else if(classTable.getClass_c(type_name) == null) {

	    classTable.semantError(c.getFilename(), c)
		.println("Invalid class type: "
			 + type_name.getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(type_name);
	}
    }
}


/** Defines AST constructor 'isvoid'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class isvoid extends Expression {
    protected Expression e1;
    /** Creates "isvoid" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public isvoid(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new isvoid(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "isvoid\n");
        e1.dump(out, n+2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_isvoid");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Isvoid: " +
			       getLineNumber());
	}

	e1.semant(objectTable,
		  methodTable,
		  classTable,
		  c);

	if(!e1.get_type().equals(TreeConstants.Bool)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in isvoid expression: "
			 + e1.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


/** Defines AST constructor 'no_expr'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class no_expr extends Expression {
    /** Creates "no_expr" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      */
    public no_expr(int lineNumber) {
        super(lineNumber);
    }
    public TreeNode copy() {
        return new no_expr(lineNumber);
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "no_expr\n");
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_no_expr");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---No_Expr: " +
			       getLineNumber());
	}

	set_type(TreeConstants.No_type);
    }
}


/** Defines AST constructor 'object'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class object extends Expression {
    protected AbstractSymbol name;
    /** Creates "object" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      */
    public object(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        name = a1;
    }
    public TreeNode copy() {
        return new object(lineNumber, copy_AbstractSymbol(name));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "object\n");
        dump_AbstractSymbol(out, n+2, name);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_object");
	dump_AbstractSymbol(out, n + 2, name);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(ClassTable.DEBUG) {
	    System.out.println("---Identifier: " +
			       name.getString() + ", " +
			       getLineNumber());
	}

	// Get reference to identifier in object scope
	AbstractSymbol o = (AbstractSymbol) objectTable.lookup(name);

	if(name.equals(TreeConstants.self) ||
	   name.equals(TreeConstants.SELF_TYPE)) {

	    set_type(TreeConstants.SELF_TYPE);
	} else if(o == null) {
	    // Referenced object does not exist
	    classTable.semantError(c.getFilename(), c)
		.println("Reference to non-existant object: " + name);

	    //set_type(TreeConstants.No_type);
	    set_type(TreeConstants.Object_);
	} else {
	    set_type(o);
	}
    }
}
