
# ANÁLISIS SEMÁNTICO

El análisis semántico compone la tercera fase de la compilación. En esta fase se utilizan como entrada los árboles de sintaxis abstractos (AST) generados en la fase anterior de análisis sintáctico, y se comprueba si el programa se adecua a la especificación formal del lenguaje. Si el programa es incorrecto, el analizador semántico lo descartará; si se trata de un programa correcto, por otra parte, el analizador producirá como resultado un árbol de sintaxis abstracto anotado con cierta información que será utilizada por el generador de código.




# TreeNode

## The base class for all AST nodes.

After lexical analysis and parsing, a Cool program is represented internally by the Cool compiler as an abstract syntax tree. The project comes with a definition of Cool abstract syntax trees (ASTs) built in. The AST package is by far the largest piece of code in the base system and requires the most time to learn. The learning process is made more complex because the AST code is generated automatically from a specification in the file <code>cool-tree.aps</code>. While the generated code is quite simple and regular in structure, it is also devoid of comments. This section serves as the documentation for the AST package.

## Phyla and Constructors

The AST data type provides a class for representing each type of Cool expression. There is a class for <code>let</code> expressions, another class of <code>+</code> expressions, and so on. Objects of these classes are nodes in Cool abstract syntax trees. For example, an expression <code>e1 + e2</code> is represented by a <code>+</code> expression object, which has two subtrees: one for the tree representing the expression <code>e1</code> and one for the tree representing the expression <code>e2</code>.

The Cool abstract syntax is specified in a language called APS. In APS terminology, the various kinds of abstract syntax tree nodes (<code>let</code>, <code>+</code>, etc.) are called <em>constructors</em>. (Don't confuse this use of the term "constructor" with Java constructors; while similar, this is a slightly different meaning taken from functional languages that predates Java.) The form of the AST is described by a set of <em>phyla</em>.  Each phylum has one or more constructors.

Phyla are really just types.  That is, instead of having one large group of undifferentiated constructors, the constructors are grouped together according to function, so that, for example, the constructors for expression ASTs are distinguished from the constructors for class ASTs. The phyla are defined at the beginning of <code>cool-tree.aps</code>:

	module COOL begin phylum
	Program;

	phylum Class_;
	phylum Classes = LIST[Class_];

	phylum Feature;
	phylum Features = LIST[Feature];

	phylum Formal;
	phylum Formals = LIST[Formal];

	phylum Expression;
	phylum Expressions = LIST[Expression];

	phylum Case;
	phylum Cases = LIST[Case];

From the definition it can be seen that there are two distinct kinds of phyla: "normal" phyla and list phyla.  "Normal" phyla each have associated constructors; list phyla have a fixed set of list operations.

Each constructor takes typed arguments and returns a typed result. The types may either be phyla or any ordinary Java type. In fact, the phyla declarations are themselves compiled into Java class declarations by an APS compiler. A sample constructor definition is

	constructor class_(name : AbstractSymbol;
		parent : AbstractSymbol;
	    features : Features;
	    filename : AbstractSymbol) : Class_;

This declaration specifies that the <code>class_</code> constructor takes four arguments: an <code>AbstractSymbol</code> (a type identifier) for the class name, an <code>AbstractSymbol</code> (another type identifier) for the parent class, a <code>Features</code>, and an <code>AbstractSymbol</code> for the filename in which the class definition occurs. (the name <code>class_</code> is chosen to avoid a conflict with the Java keyword <b>class</b>.)  The phylum <code>Features</code> is defined to be a list of <code>Feature</code>'s by the declaration

	phylum Features = LIST[Feature];

See <a href="ListNode.html">ListNode</a> for a description of the operations defined on AST lists.

To invoke the class constructor, you allocate a new node object supplying it with the right arguments, e.g. <code>new class_(...)</code>.  In <code>cool.cup</code> there is the following example of a use of the <code>class_</code> constructor:

	class ::= CLASS TYPEID:n INHERITS TYPEID:p LBRACE optional_feature_list:f RBRACE SEMI
	    {: RESULT = new class_(curr_lineno(), n, p, f, curr_filename()); :}

Allocating a new <code>class_</code> object, builds a <codeClass_</code> tree node with the four arguments as children. Because the phyla (types) of the arguments are declared, the Java type checker enforces that the <code>class_</code> constructor is applied only to arguments of the appropriate type. See Section 6.5 of the "Tour of Cool Support Code" and <code>cool-tree.aps</code> to learn the definitions of the other constructors. (Comments in <code>cool-tree.aps</code> begin with two hyphens "--".)

NOTE: there is a real danger of getting confused because the same names are used repeatedly for different entities in different contexts. In the example just above, small variations of the name <em>class</em> are used for a terminal (<code>CLASS</code>), a non-terminal (<code>class</code>), a constructor (<code>class_</code>), and a phylum (<code>Class_</code>). These uses are all distinct and mean different things. Most uses are distinguished consistently by capitalization, but a few are not. When reading the code it is important to keep in mind the role of each symbol.

## The AST Class Hierarchy

All AST classes are derived from this class (<em>TreeNode</em>). (The list classes are actually derived from <a href="ListNode.html">ListNode</a>, which is a refinement of <em>TreeNode</em>.) All of the lists are lists of <em>TreeNode</em>s.

The <em>TreeNode</em> class definition contains everything needed in an abstract syntax tree node except information specific to particular constructors.

Each of the constructors is a class derived from the appropriate phyla.

## Class Members

Each class definition of the tree package comes with a number of members. Some of the member functions are discussed below. This section describes the data members and some more (but not all) of the rest of the functions, as well as how to add new members to the classes.

Each constructor has data members defined for each component of that constructor. The name of the member is the name of the field in the constructor, and it is only visible to member functions of the constructor's class or derived classes. For example, the <code>class_</code> constructor has four data members:

	Symbol name;
	Symbol parent;
	Features features;
	Symbol filename;

Here is a complete use of one member:

	class class_ extends Class_ {
		...
	    AbstractSymbol getParent() { return parent; }
	    ...
	}

	...

	Class_ c;
	AbstractSymbol p;

	c = new class(lineno,
		AbstractTable.idtable.add_string("Foo",3),
		AbstractTable.idtable.add_string("Bar"),
		new Features(lineno),
		AbstractTable.stringtable.add_string("filename"));
	p = c->get_parent();  // Sets p to the symbol for "Bar"
	...

It will be useful in writing a Cool compiler to extend the AST with new functions such as <code>getParent()</code>. Simply modify the <code>cool-tree.java</code> file to add functions to the class of the appropriate phylum or constructor.

### Tips on Using the Tree Package

There are a few common errors people make using a tree package.

-	The tree package implements an abstract data type.  Violating the interface (e.g., by unwarranted casting, etc.) invites disaster. Stick to the interface as it is defined. When adding new members to the class declarations, be careful that those members do not perturb the interface for the existing functions.

-	The value <code>null</code> is not a valid component of any AST. Never use <code>null</code> as an argument to a constructor.

-	The tree package functions perform checks to ensure that trees are used correctly. If something bad is detected, the function <code>Utilities.fatalError()</code> is invoked to terminate execution and a stack trace is printed to the console.



##


Classes:ListNode

	- constructor (lineNumber:int)
	- constructor (lineNumber:int, elements:Vector)

	- getElementClass():Class
	- appendElement(elem:TreeNode):Classes
	- copy():TreeNode

	You can use <code>java.util.Enumeration</code> to iterate through lists:

<pre>
  for (Enumeration e = list.getElements(); e.hasMoreElements(); ) {
    Object n = e.nextElement();
    ... do something with n ...
  }
</pre>

    Alternatively, it is possible to iterate using an integer index:

<pre>
  for (int i = 0; i < list.getLength(); i++) {
    ... do something with list.getNth(i) ...
  }
</pre>
