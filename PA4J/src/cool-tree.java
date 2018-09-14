// -*- mode: java -*-
//
// file: cool-tree.m4
//
// This file defines the AST
//
//////////////////////////////////////////////////////////

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


// ////////////////////////////////////////////////////////////////////////////
// Program XXX

/** Defines simple phylum Program */
abstract class Program extends TreeNode {

    protected Program(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

    public abstract void semant();

}


// ////////////////////////////////////////////////////////////////////////////
// programc XXX

/** Defines AST constructor 'programc'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class programc extends Program {
    protected Classes classes;

    /** Creates "programc" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for classes
      */
    public programc(int lineNumber, Classes a1) {
        super(lineNumber);
        classes = a1;
    }

    public TreeNode copy() {
        return new programc(lineNumber, (Classes)classes.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "programc\n");
        classes.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_program");
        for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
            // sm: changed 'n + 1' to 'n + 2' to match changes elsewhere
		    ((Class_)e.nextElement()).dump_with_types(out, n + 2);
        }
    }

    /** This method is the entry point to the semantic checker. You will
        need to complete it in programming assignment 4.
	<p>
        Your checker should do the following two things:
	<ol>
	<li>Check that the program is semantically correct
	<li>Decorate the abstract syntax tree with type information
        by setting the type field in each Expression node.
        (see tree.h)
	</ol>
	<p>
	You are free to first do (1) and make sure you catch all semantic
    	errors. Part (2) can be done in a second stage when you want
	to test the complete compiler.
    */
    public void semant() {
		if (Flags.semant_debug) {
			System.out.println("DEBUG. Inicio del análisis semántico");
		}

		/* ClassTable constructor may do some semantic analysis */
		ClassTable classTable = new ClassTable(classes);

		// XXX - Ini.

		// TableWrapper puede registrar, además del conjunto de clases del
		// programa, dos conjuntos con los objetos y métodos que se encuentran
		// dentro de un cierto ámbito (scope). Inicialmente no son necesarios,
		// de modo que tables se inicializa con ellos con valor nulo:
		//		SymbolTable objectTable = null;
		//		SymbolTable methodTable = null;
		TableWrapper tables = new TableWrapper(classTable);

		// Análisis semántico principal. Empieza con el objeto 'classes', y el
		// análisis se va propagando de arriba abajo por todos los elementos
		// del programa:
		classes.semant(tables);

		// Comprueba que haya definida una clase principal:
		class_c mainClass = classes.getClass(TreeConstants.Main);
		if (mainClass == null) {
			tables.classTable.semantError()
				.println("Main class is not defined.");
		}
		// Comprueba que haya definido un método principal:
		else if (mainClass.getMethod(TreeConstants.main_meth) == null) {
			tables.classTable.semantError()
				.println("No 'main' method in class Main.");
		}

		// XXX - Fin

		if (tables.classTable.errors()) {
		    System.err
				.println("Compilation halted due to static semantic errors.");
		    System.exit(1);
		}

		if (Flags.semant_debug) {
			System.out.println("DEBUG. Final del análisis semántico");
		}
    }
}


// ////////////////////////////////////////////////////////////////////////////
// Classes XXX

/** Defines list phylum Classes
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Classes extends ListNode {
    public final static Class elementClass = Class_.class;

    /** Creates an empty "Classes" list */
    public Classes(int lineNumber) {
        super(lineNumber);
    }

    protected Classes(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /** Appends "Class_" element to this list */
    public Classes appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Classes(lineNumber, copyElements());
    }

    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }

	// XXX - Ini.

	// < programc.semant()
	public class_c getClass(AbstractSymbol symbol) {
		class_c result = null;
		for (Enumeration<class_c> e = getElements(); e.hasMoreElements(); ) {
			class_c temp = e.nextElement();
			if (temp.getName().equals(symbol)) {
				result = temp;
				break;
			}
		}
		return result;
	}

	// < programc.semant()
	public void semant(TableWrapper tables) {
		for (Enumeration<class_c> e = getElements(); e.hasMoreElements(); ) {
			e.nextElement().semant(tables);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Class_ XXX

/** Defines simple phylum Class_ */
abstract class Class_ extends TreeNode {

    protected Class_(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

	// XXX - Ini.

	public abstract method getMethod(AbstractSymbol symbol);

	public abstract void refreshScope(TableWrapper tables, class_c c);

	public abstract void semant(TableWrapper tables);

	// XXX - Fin
}


// ////////////////////////////////////////////////////////////////////////////
// class_c XXX

/** Defines AST constructor 'class_c'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class class_c extends Class_ {
    protected AbstractSymbol name;
    protected AbstractSymbol parent;
    protected Features features;
    protected AbstractSymbol filename;

    /** Creates "class_c" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for parent
      * @param a2 initial value for features
      * @param a3 initial value for filename
      */
    public class_c(int lineNumber, AbstractSymbol a1, AbstractSymbol a2,
		Features a3, AbstractSymbol a4) {
        super(lineNumber);
        name = a1;
        parent = a2;
        features = a3;
        filename = a4;
    }

    public TreeNode copy() {
        return new class_c(lineNumber, copy_AbstractSymbol(name),
			copy_AbstractSymbol(parent), (Features)features.copy(),
			copy_AbstractSymbol(filename));
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "class_c\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, parent);
        features.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, filename);
    }

    public AbstractSymbol getFilename() { return filename; }

    public AbstractSymbol getName()     { return name; }

    public AbstractSymbol getParent()   { return parent; }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_class");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, parent);
        out.print(Utilities.pad(n + 2) + "\"");
        Utilities.printEscapedString(out, filename.getString());
        out.println("\"\n" + Utilities.pad(n + 2) + "(");
        for (Enumeration e = features.getElements(); e.hasMoreElements();) {
		    ((Feature)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
    }

	// XXX - Ini.

	// < ClassTable.getFeature()
	public Features getFeatures() {
		return features;
	}

	// < programc.semant()
	@Override
	public method getMethod(AbstractSymbol symbol) {
		method result = null;
		for (Enumeration<Feature> e = features.getElements();
			e.hasMoreElements(); ) {
			Feature temp = e.nextElement();
			if (temp instanceof method && temp.getName().equals(symbol)) {
				result = (method)temp;
				break;
			}
		}
		return result;
	}

	@Override
	public void refreshScope(TableWrapper tables, class_c c) {
		features.refreshScope(tables, c);
	}

	// < Classes.semant()
	@Override
	public void semant(TableWrapper tables) {
		if (Flags.semant_debug) {
			System.out.println(":: class :: " + name.getString() + ", "
				+ getLineNumber());
		}

		// Comprueba que el identificador sea legal:
		if (tables.classTable.getIllegalIdentifiers().contains(name)) {
			tables.classTable.semantError(getFilename(), this)
				.println("Illegal class identifier: " + name);
		}

		// Establece ésta como clase actual en classTable:
		tables.classTable.setCurrentClass(name);

		// Crea nuevas tablas con el ámbito local de la clase:
		tables.objectTable = new SymbolTable();
		tables.methodTable = new SymbolTable();

		// Activa y determina el scope de la clase para tenerlo en cuenta
		// durante el próximo análisis semántico de sus elementos:
		tables.objectTable.enterScope();
		tables.methodTable.enterScope();
		initializeScope(tables, name);

		// Análisis semántico considerando el scope:
		features.semant(tables, this);

		// Sale del scope:
		tables.methodTable.exitScope();
		tables.objectTable.exitScope();
	}

	private void initializeScope(TableWrapper tables,
		AbstractSymbol a) {
		// Raíz del árbol de jerarquía:
		if (a.equals(TreeConstants.No_class)) {
			return;
		}
		// Llamada recursiva a todas las superclases de c:
		else {
			class_c c = tables.classTable.getClass_c(a);
			initializeScope(tables, c.getParent());
			tables.objectTable.enterScope();
			tables.methodTable.enterScope();
			c.refreshScope(tables, c);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Features XXX

/** Defines list phylum Features
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Features extends ListNode {
    public final static Class elementClass = Feature.class;

    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }

    protected Features(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /** Creates an empty "Features" list */
    public Features(int lineNumber) {
        super(lineNumber);
    }

    /** Appends "Feature" element to this list */
    public Features appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Features(lineNumber, copyElements());
    }

	// XXX - Ini.

	// < class_c.getFeature()
	public Feature getFeature(AbstractSymbol featureName) {
		for (Enumeration<Feature> e = getElements(); e.hasMoreElements(); ) {
			Feature feature = e.nextElement();
			if (feature.getName().equals(featureName)) {
				return feature;
			}
		}

		return null;
	}

	// < class_c.refreshScope()
	public void refreshScope(TableWrapper tables, class_c c) {
		for (Enumeration<Feature> e = getElements(); e.hasMoreElements(); ) {
			e.nextElement().refreshScope(tables, c);
		}
	}

	// < class_c.semant()
	public void semant(TableWrapper tables, class_c c) {
		for (Enumeration<Feature> e = getElements(); e.hasMoreElements(); ) {
			e.nextElement().semant(tables, c);
		}
	}

	// XXX - Fin
}


// ////////////////////////////////////////////////////////////////////////////
// Feature XXX

/** Defines simple phylum Feature */
abstract class Feature extends TreeNode {
    protected Feature(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

	// XXX - Ini.

	// < Features.getFeature()
	public abstract AbstractSymbol getName();

	public abstract AbstractSymbol get_type();

	// < Features.refreshScope()
	public abstract void refreshScope(TableWrapper tables, class_c c);

	// < Features.semant()
	public abstract void semant(TableWrapper tables, class_c c);

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// method

/** Defines AST constructor 'method'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class method extends Feature {
    protected AbstractSymbol name;
    protected Formals formals;
    protected AbstractSymbol return_type;
    protected Expression expr;

    /** Creates "method" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for formals
      * @param a2 initial value for return_type
      * @param a3 initial value for expr
      */
    public method(int lineNumber, AbstractSymbol a1, Formals a2,
		AbstractSymbol a3, Expression a4) {
        super(lineNumber);
        name = a1;
        formals = a2;
        return_type = a3;
        expr = a4;
    }

    public TreeNode copy() {
        return new method(lineNumber, copy_AbstractSymbol(name),
			(Formals)formals.copy(), copy_AbstractSymbol(return_type),
			(Expression)expr.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "method\n");
        dump_AbstractSymbol(out, n+2, name);
        formals.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, return_type);
        expr.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_method");
        dump_AbstractSymbol(out, n + 2, name);
        for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
		    ((Formal)e.nextElement()).dump_with_types(out, n + 2);
        }
        dump_AbstractSymbol(out, n + 2, return_type);
		expr.dump_with_types(out, n + 2);
    }

	// XXX - Ini.

	// < dispatch.semant()
	// < static_dispatch.semant()
	public void verifyArguments(TableWrapper tables, class_c c,
		Expressions expressions) {
		formals.verifyArguments(expressions, tables, c);
	}

	public Formals getFormals() {
		return formals;
	}

	@Override
	public AbstractSymbol getName() {
		return name;
	}

	@Override
	public AbstractSymbol get_type() {
		return this.return_type;
	}

	@Override
	public void refreshScope(TableWrapper tables, class_c c) {
		// Error
		if(tables.methodTable.probe(name) != null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal identifier: multiply defined method "
					+ name.getString());
			return;
		}

		if (tables.classTable.isSelfType(return_type)) {
			tables.methodTable.addId(name, c.getName());
		} else {
			tables.methodTable.addId(name, return_type);
		}
	}

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: method :: " + name.getString() + ", "
				+ getLineNumber());
		}

		tables.objectTable.enterScope();

		// Comprueba que no se utiliza un identificador ilegal:
		if (tables.classTable.getIllegalIdentifiers().contains(name)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal name for function: "
					+ name.getString());
		}

		formals.semant(tables, c);
		expr.semant(tables, c);

		// Verifica que el tipo de la expresión es subtipo de return_type:
		if (!tables.classTable.isSubtypeOf(expr.get_type(), return_type)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Inferred return type "
					+ expr.get_type().getString() + " of method "
					+ name.getString() + " does not conform to declared type "
					+ return_type.getString());
		}

		// Si el método está sobreescribiendo un método de una superclase:
		if (tables.classTable.getFeature(c.getParent(), name) != null) {
			method m
				= (method)tables.classTable.getFeature(c.getParent(), name);
			// Verifica que los tipos retornados coincidan:
			if (!m.get_type().equals(return_type)) {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Overriding method " + name.getString()
						+ " has unexpected return type "
						+ return_type.getString());
				return;
			}
			// Verifica que las listas de parámetros tengan la misma longitud:
			else if (m.formals.getLength() != formals.getLength()) {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Differing lengths to formal params in "
						+ "overriding method " + name.getString());
				return;
			}
			// Comprueba los tipos de la lista de parámetros:
			else {
				Enumeration<Formal> e1 = m.formals.getElements();
				Enumeration<Formal> e2 = formals.getElements();
				while (e1.hasMoreElements() && e2.hasMoreElements()) {
					AbstractSymbol type1 = e1.nextElement().get_type();
					AbstractSymbol type2 = e2.nextElement().get_type();
					if (!type1.equals(type2)) {
						tables.classTable.semantError(c.getFilename(), c)
							.println("Illegal argument in overriding method "
								+ name.getString() + ". Expected "
								+ type1.getString() + ", found "
								+ type2.getString());
					}
				}
			}
		}

		tables.objectTable.exitScope();
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// attr XXX

/** Defines AST constructor 'attr'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
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
    public attr(int lineNumber, AbstractSymbol a1, AbstractSymbol a2,
		Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        init = a3;
    }

    public TreeNode copy() {
        return new attr(lineNumber, copy_AbstractSymbol(name),
			copy_AbstractSymbol(type_decl), (Expression)init.copy());
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

	// XXX - Ini.

	@Override
	public AbstractSymbol getName() {
		return name;
	}

	@Override
	public AbstractSymbol get_type() {
		return type_decl;
	}

	@Override
	public void refreshScope(TableWrapper tables, class_c c) {
		if (tables.objectTable.probe(name) != null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal identifier: multiply defined attribute: "
					+ name.getString());
		}
		else if (tables.objectTable.lookup(name) != null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Overriding member of parent class: "
					+ name.getString());
		}
		else {
			tables.objectTable.addId(name, type_decl);
		}
	}

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: attr :: " + name.getString() + ", "
				+ getLineNumber());
		}

		// Comprueba que no se utiliza un identificador ilegal:
		if (tables.classTable.getIllegalIdentifiers().contains(name)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal name for class member: "
					+ name.getString());
		}

		init.semant(tables, c);

		tables.objectTable.addId(name, type_decl);

		if (!tables.classTable.isSubtypeOf(init.get_type(), type_decl)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal assignment: "
					+ type_decl.getString() + " = "
					+ init.get_type().getString());
			init.set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Formals XXX

/** Defines list phylum Formals
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Formals extends ListNode {
    public final static Class elementClass = Formal.class;

	/** Creates an empty "Formals" list */
	public Formals(int lineNumber) {
		super(lineNumber);
	}

    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }

    protected Formals(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /** Appends "Formal" element to this list */
    public Formals appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Formals(lineNumber, copyElements());
    }

	// XXX - Ini.

	// < method.semant()
	public void semant(TableWrapper tables, class_c c) {
		for (Enumeration<Formal> e = getElements(); e.hasMoreElements(); ) {
			e.nextElement().semant(tables, c);
		}
	}

	// < method.verifyArguments()
	public void verifyArguments(Expressions expressions, TableWrapper tables,
		class_c c) {
		// El número de expresiones pasadas no coincide:
		if (expressions.getLength() != this.getLength()) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Wrong number of arguments passed to function: "
					+ " Expected " + this.getLength() + ", got "
					+ expressions.getLength());
			return;
		}
		// Se comparan los elementos uno a uno:
		Enumeration<Formal> e1 = this.getElements();
		Enumeration<Expression> e2 = expressions.getElements();
		while (e1.hasMoreElements()) {
			e1.nextElement().verifyArgument(tables, c, e2.nextElement());
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Formal XXX

/** Defines simple phylum Formal */
abstract class Formal extends TreeNode {
    protected Formal(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

	// XXX - Ini.

	public abstract AbstractSymbol get_type();

	public abstract void semant(TableWrapper tables, class_c c);

	// < Formals.verifyArguments()
	public abstract void verifyArgument(TableWrapper tables, class_c c,
		Expression expression);

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// formalc XXX

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
        return new formalc(lineNumber, copy_AbstractSymbol(name),
			copy_AbstractSymbol(type_decl));
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

	// XXX - Ini.

	@Override
	public AbstractSymbol get_type() {
		return type_decl;
	}

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: formal :: " + name.getString() + ", "
				+ getLineNumber());
		}

		// Comprueba que no se utiliza un identificador ilegal:
		if (tables.classTable.getIllegalIdentifiers().contains(name)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal name for formal argument: "
					+ name.getString());
		}
		if (tables.classTable.isSelfType(type_decl)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal type for parameter: "
					+ name.getString());
		}

		// Comprueba que el elemento no exista ya:
		if (tables.objectTable.probe(name) != null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Multiply defined argument: " + name.getString());
			return;
		} else {
			tables.objectTable.addId(name, type_decl);
		}
	}

	@Override
	public void verifyArgument(TableWrapper tables, class_c c,
		Expression expression) {
		if (!tables.classTable.isSubtypeOf(expression.get_type(), type_decl)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal argument: Expected " + type_decl.getString()
					+ ", found " + expression.get_type().getString());
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Expression XXX

/** Defines simple phylum Expression */
abstract class Expression extends TreeNode {
    protected Expression(int lineNumber) {
        super(lineNumber);
    }

    private AbstractSymbol type = null;

    public AbstractSymbol get_type() { return type; }

    public Expression set_type(AbstractSymbol s) { type = s; return this; }

    public abstract void dump_with_types(PrintStream out, int n);

    public void dump_type(PrintStream out, int n) {
        if (type != null)
            { out.println(Utilities.pad(n) + ": " + type.getString()); }
        else
            { out.println(Utilities.pad(n) + ": _no_type"); }
    }

	// XXX - Ini.

	// < method.semant()
	public abstract void semant(TableWrapper tables, class_c c);

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// assign XXX

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
        return new assign(lineNumber, copy_AbstractSymbol(name),
			(Expression)expr.copy());
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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: assign :: " + name.getString() + ", "
				+ getLineNumber());
		}

		expr.semant(tables, c);

		// Verifica que el identificador exista y no se corresponda con un
		// identificador ilegal, y que la asignación sea entre objetos del
		// mismo tipo:

		AbstractSymbol id = (AbstractSymbol)tables.objectTable.lookup(name);

		// Comprueba que no se utiliza un identificador ilegal:
		if (tables.classTable.getIllegalIdentifiers().contains(name)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal identifier in assignment.");
			set_type(TreeConstants.Object_);
		}
		else if (id == null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Assignment to non-existent variable "
					+ name.getString());
			set_type(TreeConstants.Object_);
		}
		else if (!tables.classTable.isSubtypeOf(expr.get_type(), id)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Assignment to illegal type: " + id.getString()
					+ " <- " + expr.get_type().getString());
			set_type(TreeConstants.Object_);
		} else {
			set_type(id);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// static_dispatch XXX

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
    public static_dispatch(int lineNumber, Expression a1, AbstractSymbol a2,
		AbstractSymbol a3, Expressions a4) {
        super(lineNumber);
        expr = a1;
        type_name = a2;
        name = a3;
        actual = a4;
    }

    public TreeNode copy() {
        return new static_dispatch(lineNumber, (Expression)expr.copy(),
			copy_AbstractSymbol(type_name), copy_AbstractSymbol(name),
			(Expressions)actual.copy());
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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: static_dispatch :: " + name.getString());
		}

		expr.semant(tables, c);
		actual.semant(tables, c);

		if (tables.classTable.getClass_c(type_name) != null) {
			Feature f = tables.classTable.getFeature(type_name, name);
			if (!tables.classTable.isSubtypeOf(expr.get_type(), type_name)) {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Illegal static dispatch: Referenced type "
						+ expr.get_type().getString() + " is not of specified "
						+ "type " + type_name.getString());
				set_type(TreeConstants.Object_);
			}
			else if (f == null) {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Call to non-existent static method " + name);
				set_type(TreeConstants.Object_);
			}
			else if (!(f instanceof method)) {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Illegal dispatch to attribute: " + name);
				set_type(TreeConstants.Object_);
			}
			else {
				method m = (method)f;
				m.verifyArguments(tables, c, actual);
				set_type(m.get_type());
			}
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal static dispatch to non-class object "
					+ type_name);
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// dispatch XXX

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
    public dispatch(int lineNumber, Expression a1, AbstractSymbol a2,
		Expressions a3) {
        super(lineNumber);
        expr = a1;
        name = a2;
        actual = a3;
    }

    public TreeNode copy() {
        return new dispatch(lineNumber, (Expression)expr.copy(),
			copy_AbstractSymbol(name), (Expressions)actual.copy());
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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: dispatch :: " + name.getString());
		}

		expr.semant(tables, c);
		actual.semant(tables, c);


		// Verifica que el método y el número de párametros coincida:

		Feature f = tables.classTable.getFeature(expr.get_type(), name);
		if (f == null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Call to non-existant method " + name.getString()
					+ " of class " + expr.get_type().getString());
			set_type(TreeConstants.Object_);
		} else if (f instanceof method) {
			method m = (method)f;
			if (tables.classTable.isSelfType(m.get_type())) {
				if (tables.classTable.isSelfType(expr.get_type())) {
					set_type(TreeConstants.SELF_TYPE);
				} else {
					set_type(expr.get_type());
				}
			} else {
				set_type(m.get_type());
			}
			m.verifyArguments(tables, c, actual);
		} else {
			tables.classTable.semantError()
				.println("Illegal dispatch to attr: " + name.getString());
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// cond XXX

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
        return new cond(lineNumber, (Expression)pred.copy(),
			(Expression)then_exp.copy(), (Expression)else_exp.copy());
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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: cond :: " + getLineNumber());
		}

		// Evalúa los componentes de la expresión:
		pred.semant(tables, c);
		then_exp.semant(tables, c);
		else_exp.semant(tables, c);

		// Verifica que la condición sea un booleano:
		if (!pred.get_type().equals(TreeConstants.Bool)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Expected boolean type in if statement; got "
					+ pred.get_type().getString());
		}

		// Establece el tipo en el límite superior mínimo:
		set_type(Helper.lub(tables, then_exp.get_type(), else_exp.get_type()));
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// loop XXX

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
        return new loop(lineNumber, (Expression)pred.copy(),
			(Expression)body.copy());
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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: loop :: " + getLineNumber());
		}

		pred.semant(tables, c);
		body.semant(tables, c);

		// Verifica que la condicion sea un booleano:
		if (!pred.get_type().equals(TreeConstants.Bool)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Expected boolean expression; Found "
					+ pred.get_type().getString());
		}

		// Establece el mismo tipo al que se evalua el cuerpo del bucle:
		set_type(body.get_type());
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// typcase XXX

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
        return new typcase(lineNumber, (Expression)expr.copy(),
			(Cases)cases.copy());
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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: typcase :: " + getLineNumber());
		}

		expr.semant(tables, c);
		cases.semant(tables, c);
		cases.verifyTypes(tables, c);

		set_type(this.get_type(tables));
	}

	private AbstractSymbol get_type(TableWrapper tables) {
		AbstractSymbol result = TreeConstants.Object_;
		AbstractSymbol temp;
		Enumeration<Case> e = cases.getElements();
		if (e.hasMoreElements()) {
			while (e.hasMoreElements()) {
				temp = e.nextElement().get_type();
				if (tables.classTable.isSubtypeOf(expr.get_type(), temp)) {
					result = temp;
					break;
				}
			} // while
			if (result != null) {
				e = cases.getElements();
				while (e.hasMoreElements()) {
					temp = e.nextElement().get_type();
					if (tables.classTable.isSubtypeOf(temp, expr.get_type())) {
						result = Helper.lub(tables, result, temp);
					}
				}
			}
		}

		return result;
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// block XXX

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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: block :: " + getLineNumber());
		}

		tables.objectTable.enterScope();
		body.semant(tables, c);

		// Determina el tipo en función del tipo de la última expresión del
		// cuerpo:
		Expression lastExpression
			= (Expression)body.getNth(body.getLength() - 1);
		set_type(lastExpression.get_type());

		tables.objectTable.exitScope();
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// let XXX

/** Defines AST constructor 'let'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class let extends Expression {
    protected AbstractSymbol identifier;
    protected AbstractSymbol type_decl;
    protected Expression init;
    protected Expression body;

    /** Creates "let" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for identifier
      * @param a1 initial value for type_decl
      * @param a2 initial value for init
      * @param a3 initial value for body
      */
    public let(int lineNumber, AbstractSymbol a1, AbstractSymbol a2,
		Expression a3, Expression a4) {
        super(lineNumber);
        identifier = a1;
        type_decl = a2;
        init = a3;
        body = a4;
    }

    public TreeNode copy() {
        return new let(lineNumber, copy_AbstractSymbol(identifier),
			copy_AbstractSymbol(type_decl), (Expression)init.copy(),
			(Expression)body.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "let\n");
        dump_AbstractSymbol(out, n+2, identifier);
        dump_AbstractSymbol(out, n+2, type_decl);
        init.dump(out, n+2);
        body.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_let");
		dump_AbstractSymbol(out, n + 2, identifier);
		dump_AbstractSymbol(out, n + 2, type_decl);
		init.dump_with_types(out, n + 2);
		body.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: let :: " + getLineNumber());
		}

		// Verifica la corrección semántica del cuerpo del "let" y
		// comprueba el alcance (scope) de la expresión:

		tables.objectTable.enterScope();
		init.semant(tables, c);

		// Comprueba que no se utiliza un identificador ilegal:
		if (tables.classTable.getIllegalIdentifiers().contains(identifier)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal identifier in let assignment: "
					+ identifier.getString());
		}
		else {
			// Verifica que el tipo del atributo type_decl coincide con el de
 			// la expresón init y, si es así, lo añade al scope:
			if (tables.classTable.isSubtypeOf(init.get_type(), type_decl)) {
				tables.objectTable.addId(identifier, type_decl);
			} else {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Illegal assignment to type: "
						+ type_decl.getString() + " <- "
						+ init.get_type().getString());
			}
		}

		body.semant(tables, c);
		set_type(body.get_type());
		tables.objectTable.exitScope();
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// plus XXX

/** Defines AST constructor 'plus'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class plus extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "plus" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public plus(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new plus(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "plus\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_plus");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: plus :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Se verifica que que ambos operandos son enteros

		if (e1.get_type().equals(TreeConstants.Int)
			&& e2.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Int);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected types in integer expression: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// sub XXX

/** Defines AST constructor 'sub'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class sub extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "sub" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public sub(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new sub(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "sub\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_sub");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: sub :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Se verifica que que ambos operandos son enteros

		if (e1.get_type().equals(TreeConstants.Int)
			&& e2.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Int);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected types in integer expression: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// mul XXX

/** Defines AST constructor 'mul'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class mul extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "mul" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public mul(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new mul(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "mul\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_mul");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: mul :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Se verifica que que ambos operandos son enteros

		if (e1.get_type().equals(TreeConstants.Int)
			&& e2.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Int);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected types in integer expression: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// divide XXX

/** Defines AST constructor 'divide'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class divide extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "divide" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public divide(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new divide(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "divide\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_divide");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: divide :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Se verifica que que ambos operandos son enteros

		if (e1.get_type().equals(TreeConstants.Int)
			&& e2.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Int);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected types in integer expression: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin
}


// ////////////////////////////////////////////////////////////////////////////
// neg XXX

/** Defines AST constructor 'neg'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class neg extends Expression {
    protected Expression e1;

    /** Creates "neg" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public neg(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }

    public TreeNode copy() {
        return new neg(lineNumber, (Expression)e1.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "neg\n");
        e1.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_neg");
		e1.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: neg :: " + getLineNumber());
		}

		e1.semant(tables, c);

		// Verifica que el operando se corresponde con un entero:

		if (e1.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Int);
		} else {
			// XXX - First, an error message should be printed with the line
			// number and a description of what went wrong. It is relatively
			// easy to give informative error messages in the semantic analysis
			// phase, because it is generally obvious what the error is. We
			// expect you to give informative error messages. Second, the
			// semantic analyzer should attempt to recover and continue. We do
			// expect your semantic analyzer to recover, but we do not expect
			// it to avoid cascading errors. A simple recovery mechanism is to
			// assign the type Object to any expression that cannot otherwise
			// be given a type
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected type in integer expression: "
					+ e1.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// lt XXX

/** Defines AST constructor 'lt'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class lt extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "lt" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public lt(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new lt(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "lt\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_lt");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: lt :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Verifica que los operandos se corresponden con enteros, y que
		// retorna un valor booleano:

		if (e1.get_type().equals(TreeConstants.Int)
			&& e2.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Bool);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected type in comparison expression: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// eq XXX

/** Defines AST constructor 'eq'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class eq extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "eq" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public eq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new eq(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "eq\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_eq");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: eq :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Se verifica que si el primer tipo es de una de las clases básicas,
		// el segundo es del mismo tipo. Debe retornar un valor booleano

		if (this.checkExpressionTypes()) {
			set_type(TreeConstants.Bool);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Invalid comparison between types: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	private boolean checkExpressionTypes() {
		boolean result = false;
		// Ambas expresiones son enteros:
		if(e1.get_type().equals(TreeConstants.Int) ||
			e2.get_type().equals(TreeConstants.Int)) {
			result = (e1.get_type().equals(TreeConstants.Int)
				&& e2.get_type().equals(TreeConstants.Int));
		}
		// Ambas expresiones son booleanos:
		else if(e1.get_type().equals(TreeConstants.Bool) ||
			e2.get_type().equals(TreeConstants.Bool)) {
			result = (e1.get_type().equals(TreeConstants.Bool)
				&& e2.get_type().equals(TreeConstants.Bool));
		}
		// Ambas expresiones son cadenas de caracteres:
		else if(e1.get_type().equals(TreeConstants.Str) ||
			e2.get_type().equals(TreeConstants.Str)) {
			result = (e1.get_type().equals(TreeConstants.Str)
				&& e2.get_type().equals(TreeConstants.Str));
		}
		return result;
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// leq XXX

/** Defines AST constructor 'leq'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class leq extends Expression {
    protected Expression e1;
    protected Expression e2;

    /** Creates "leq" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public leq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new leq(lineNumber, (Expression)e1.copy(),
			(Expression)e2.copy());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "leq\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_leq");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: leq :: " + getLineNumber());
		}

		e1.semant(tables, c);
		e2.semant(tables, c);

		// Se verifica que los operandos se corresponden con enteros, y que
		// se retorna un valor booleano

		if (e1.get_type().equals(TreeConstants.Int)
			&& e2.get_type().equals(TreeConstants.Int)) {
			set_type(TreeConstants.Bool);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected type in comparison expression: "
					+ e1.get_type().getString() + ", "
					+ e2.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// comp XXX

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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: comp :: " + getLineNumber());
		}

		e1.semant(tables, c);

		if (e1.get_type().equals(TreeConstants.Bool)) {
			set_type(TreeConstants.Bool);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected type in boolean expression: "
					+ e1.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// int_const XXX

/** Defines AST constructor 'int_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class int_const extends Expression {
    protected AbstractSymbol token;

    /** Creates "int_const" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for token
      */
    public int_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }

    public TreeNode copy() {
        return new int_const(lineNumber, copy_AbstractSymbol(token));
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "int_const\n");
        dump_AbstractSymbol(out, n+2, token);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_int");
		dump_AbstractSymbol(out, n + 2, token);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: int :: " + token.getString() + ", "
				+ getLineNumber());
		}
		set_type(TreeConstants.Int);
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// bool_const XXX

/** Defines AST constructor 'bool_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class bool_const extends Expression {
    protected Boolean val;

    /** Creates "bool_const" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for val
      */
    public bool_const(int lineNumber, Boolean a1) {
        super(lineNumber);
        val = a1;
    }

    public TreeNode copy() {
        return new bool_const(lineNumber, copy_Boolean(val));
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "bool_const\n");
        dump_Boolean(out, n+2, val);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_bool");
		dump_Boolean(out, n + 2, val);
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: bool :: " + val + ", " +
				getLineNumber());
		}
		set_type(TreeConstants.Bool);
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// string_const XXX

/** Defines AST constructor 'string_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class string_const extends Expression {
    protected AbstractSymbol token;

    /** Creates "string_const" AST node.
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for token
      */
    public string_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }

    public TreeNode copy() {
        return new string_const(lineNumber, copy_AbstractSymbol(token));
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "string_const\n");
        dump_AbstractSymbol(out, n+2, token);
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_string");
		out.print(Utilities.pad(n + 2) + "\"");
		Utilities.printEscapedString(out, token.getString());
		out.println("\"");
		dump_type(out, n);
    }

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: string :: " + token.getString() + ", "
				+ getLineNumber());
		}
		set_type(TreeConstants.Str);
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// new_ XXX

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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: new :: " + type_name.getString() + ", "
 				+ getLineNumber());
		}

		// Verifica que el operador se utiliza sobre una clase existente
		if (tables.classTable.getClass_c(type_name) == null) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Invalid class type: " + type_name.getString());
			set_type(TreeConstants.Object_);
		} else if (tables.classTable.isSelfType(type_name)) {
			set_type(TreeConstants.SELF_TYPE);
		} else {
			set_type(type_name);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// isvoid XXX

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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: isvoid :: " + getLineNumber());
		}

		e1.semant(tables, c);

		// Verifica que se retorna un valor booleano:

		if (e1.get_type().equals(TreeConstants.Bool)) {
			set_type(TreeConstants.Bool);
		} else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Unexpected type in isvoid expression: "
					+ e1.get_type().getString());
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// no_expr XXX

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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: no_expr :: " + getLineNumber());
		}
		set_type(TreeConstants.No_type);
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// object XXX

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

	// XXX - Ini.

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: object :: " + name.getString() + ", "
				+ getLineNumber());
		}

		// Verifica que el objeto esté declarado:

		// Identificador del tipo del objeto:
		AbstractSymbol obj = (AbstractSymbol)tables.objectTable.lookup(name);
		// Tipo SELF_TYPE:
		if (name.equals(TreeConstants.self)
			|| name.equals(TreeConstants.SELF_TYPE)) {
			set_type(TreeConstants.SELF_TYPE);
		}
		// Tipo del objeto definido dentro del ámbito (scope):
		else if (obj != null) {
			set_type(obj);
		}
		// Tipo de objeto no declarado dentro del ámbito:
		else {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Reference to non-existant object: " + name);
			set_type(TreeConstants.Object_);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Expressions XXX

/** Defines list phylum Expressions
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Expressions extends ListNode {
    public final static Class elementClass = Expression.class;

	/** Creates an empty "Expressions" list */
	public Expressions(int lineNumber) {
		super(lineNumber);
	}

    protected Expressions(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /** Appends "Expression" element to this list */
    public Expressions appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Expressions(lineNumber, copyElements());
    }

    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }

	// XXX - Ini.

	public void semant(TableWrapper tables, class_c c) {
		for (Enumeration<Expression> e = getElements();
			e.hasMoreElements(); ) {
			e.nextElement().semant(tables, c);
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Cases XXX

/** Defines list phylum Cases
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Cases extends ListNode {
    public final static Class elementClass = Case.class;

	/** Creates an empty "Cases" list */
	public Cases(int lineNumber) {
		super(lineNumber);
	}

    protected Cases(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /** Appends "Case" element to this list */
    public Cases appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Cases(lineNumber, copyElements());
    }

    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }

	// XXX - Ini.

	public void semant(TableWrapper tables, class_c c) {
		for (Enumeration<Case> e = getElements(); e.hasMoreElements(); ) {
			e.nextElement().semant(tables, c);
		}
	}

	// < typcase.semant()
	public void verifyTypes(TableWrapper tables, class_c c) {
		Vector<AbstractSymbol> v = new Vector<AbstractSymbol>();
		for (Enumeration<Case> e = getElements(); e.hasMoreElements(); ) {
			AbstractSymbol t = e.nextElement().get_type();
			if (v.contains(t)) {
				tables.classTable.semantError(c.getFilename(), c)
					.println("Multiple cases for one type: " + t.getString());
			} else {
				v.add(t);
			}
		}
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////
// Case XXX

/** Defines simple phylum Case */
abstract class Case extends TreeNode {
    protected Case(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

	// XXX - Ini.

	public abstract AbstractSymbol get_type();

	// < Cases.semant()
	public abstract void semant(TableWrapper tables, class_c c);

	// XXX - Fin
}


// ////////////////////////////////////////////////////////////////////////////
// branch XXX

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
    public branch(int lineNumber, AbstractSymbol a1, AbstractSymbol a2,
		Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        expr = a3;
    }

    public TreeNode copy() {
        return new branch(lineNumber, copy_AbstractSymbol(name),
			copy_AbstractSymbol(type_decl), (Expression)expr.copy());
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

	// XXX - Ini.

	@Override
	public AbstractSymbol get_type() {
		return type_decl;
	}

	@Override
	public void semant(TableWrapper tables, class_c c) {
		if (Flags.semant_debug) {
			System.out.println(":: branch :: " + name.getString() + ", "
				+ getLineNumber());
		}

		// Comprueba que no se utiliza un identificador ilegal:
		if (tables.classTable.getIllegalIdentifiers().contains(name)) {
			tables.classTable.semantError(c.getFilename(), c)
				.println("Illegal identifier in case: " + name.getString());
		}

		// Evalúa la expresión de acuerdo a un nuevo scope:
		tables.objectTable.enterScope();
		tables.objectTable.addId(name, type_decl);

		expr.semant(tables, c);

		tables.objectTable.exitScope();
	}

	// XXX - Fin

}


// ////////////////////////////////////////////////////////////////////////////

class Helper {

	/* Least Upper Bound. */
	public static AbstractSymbol lub(TableWrapper tables, AbstractSymbol a1,
		AbstractSymbol a2) {
		AbstractSymbol result;
		if (tables.classTable.isSelfType(a1)) {
			result = lub(tables, tables.classTable.getCurrentClass(), a2);
		}
		else if (tables.classTable.isSelfType(a2)) {
			result = lub(tables, a1, tables.classTable.getCurrentClass());
		}
		else if (tables.classTable.isSubtypeOf(a2, a1)) {
			result = a1;
		}
		else {
			result = lub(tables,
				tables.classTable.getClass_c(a1).getParent(), a2);
		}

		return result;
	}

}
