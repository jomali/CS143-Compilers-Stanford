/*
 *  The scanner definition for COOL.
 *
 *	IMPORTANTE: Debe utilizarse la directiva '%unicode' para que el analizador
 *	sintáctico generado pueda aceptar ficheros con una codificación de
 *	caracteres Unicode de 16 bits. En caso contrario se pueden producir errores
 *	de desbordamiento en tiempo de ejecución al utilizar el analizador con
 *	ficheros de entrada que utilicen codificación de caracteres UTF-8.
 */

import java_cup.runtime.Symbol;

%%

%{

/*
 *	Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.
 */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

	// Registra el número de línea actual
    private int curr_lineno = 1;

	// Registra el nombre del fichero
	private AbstractSymbol filename;

	// Registra el nivel de anidamiento en los comentarios
	private int nested_comment_level = 0;

    // For assembling string constants
    private StringBuffer string_buf = new StringBuffer();

    int get_curr_lineno() {
		return curr_lineno;
    }

    void set_filename(String fname) {
		filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
		return filename;
    }

	/*
	 * Añade una cadena de caracteres al StringBuffer.
	 */
	private void appendToBuffer(String text) {
		// FIXME - Comprobar tamaño máximo del string
		string_buf.append(text);
	}

	private String getStringFromBuffer() {
		// FIXME - Comprobar tamaño máximo del string
		return string_buf.toString();
	}

	/*
	 * Reinicia el StringBuffer utilizado para formar constantes de tipo cadena
	 * de caracteres.
	 */
	private void restartBuffer() {
		string_buf.delete(0, string_buf.length());
		string_buf.setLength(0);
	}

%}

%init{

/*
 *  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially.
 */

    // empty for now

%init}

%eofval{

/*
 *  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.
 */

    switch(yy_lexical_state) {
		case BLOCK_COMMENT:
			yybegin(YYINITIAL);
			return new Symbol(TokenConstants.ERROR, "EOF in comment");
		case STRING_CONSTANT:
			yybegin(YYINITIAL);
			return new Symbol(TokenConstants.ERROR, "EOF in string constant");
	    case YYINITIAL:
			return new Symbol(TokenConstants.EOF);
    }

%eofval}

%class CoolLexer
%unicode
%cup

%state BLOCK_COMMENT
%state STRING_CONSTANT

DIGIT = [0-9]
LETTER = [A-Za-z]
WHITESPACE = [ \f\r\t\x0b]+

%%

<YYINITIAL> {WHITESPACE} {
	// Ref. Manual #10.5 - White Space
	/*
	 *	JLEX no reconoce el tabulador vertical '\v' (uno de los caracteres
	 *	considerados espacio en blanco en Cool). Por ello se utiliza el valor
	 *	hexadecimal (0b) de su representación ASCII (11). Ref.:
	 *	<https://groups.google.com/forum/#!topic/ucb.class.cs164/vCfBi-kkieg>
	 */

}

<YYINITIAL> \n {
	// Ref. Manual #10.5 - White Space
	curr_lineno++;
}

<YYINITIAL> [Cc][Aa][Ss][Ee] {
	// Ref. Manual #10.4 - Keywords
	/*
		Las palabras clave reservadas son insensibles a mayúsculas y minúsculas
		(salvo por 'true' y 'false').
	*/
	return new Symbol(TokenConstants.CASE);
}

<YYINITIAL> [Cc][Ll][Aa][Ss][Ss] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.CLASS);
}

<YYINITIAL> [Ee][Ll][Ss][Ee] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ELSE);
}

<YYINITIAL> [Ee][Ss][Aa][Cc] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ESAC);
}

<YYINITIAL> f[Aa][Ll][Ss][Ee] {
	// Ref. Manual #10.4 - Keywords
	/*
		Para ajustarse a las reglas del resto de objetos, la primera letra de
		las palabras clave 'true' y 'false' deber ser minúscula (el resto de
		letras pueden ser indistintamente mayúsculas o minúsculas). Para las
		constantes booleanas, debe registrarse su valor semántico como tipo
		"java.lang.Boolean".
	 */
	return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.FALSE);
}

<YYINITIAL> [Ff][Ii] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.FI);
}

<YYINITIAL> [Ii][Ff] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.IF);
}

<YYINITIAL> [Ii][Nn] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.IN);
}

<YYINITIAL> [Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.INHERITS);
}

<YYINITIAL> [Ii][Ss][Vv][Oo][Ii][Dd] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ISVOID);
}

<YYINITIAL> [Ll][Ee][Tt] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.LET);
}

<YYINITIAL> [Ll][Oo][Oo][Pp] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.LOOP);
}

<YYINITIAL> [Nn][Ee][Ww] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.NEW);
}

<YYINITIAL> [Nn][Oo][Tt] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.NOT);
}

<YYINITIAL> [Oo][Ff] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.OF);
}

<YYINITIAL> [Pp][Oo][Oo][Ll] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.POOL);
}

<YYINITIAL> [Tt][Hh][Ee][Nn] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.THEN);
}

<YYINITIAL> t[Rr][Uu][Ee] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.TRUE);
}

<YYINITIAL> [Ww][Hh][Ii][Ll][Ee] {
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.WHILE);
}

<YYINITIAL> --[^\n]* {
	// Ref. Manual #10.3 -  Comments
	/*
	 * Inline comments.
	 */

}

<YYINITIAL> "(*" {
	// Ref. Manual #10.3 -  Comments
	nested_comment_level++;
	yybegin(BLOCK_COMMENT);
}

<YYINITIAL> "*)" {
	// Ref. Manual #10.3 -  Comments
	return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}

<BLOCK_COMMENT> "(*" {
	// Ref. Manual #10.3 -  Comments
	nested_comment_level++;
}

<BLOCK_COMMENT> "*)" {
	// Ref. Manual #10.3 -  Comments
	nested_comment_level--;
	if (nested_comment_level == 0) yybegin(YYINITIAL);
}

<BLOCK_COMMENT> \n {
	// Ref. Manual #10.3 -  Comments
	curr_lineno++;
}

<BLOCK_COMMENT> \r|. {
	// Ref. Manual #10.3 -  Comments

}

<YYINITIAL> \" {
	// Ref. Manual #10.2 -  Strings
	restartBuffer();
	yybegin(STRING_CONSTANT);
}

<STRING_CONSTANT> \" {
	// Ref. Manual #10.2 -  Strings
	yybegin(YYINITIAL);
	String stringConstant = getStringFromBuffer();
	if (stringConstant != null) {
		AbstractSymbol value =
			AbstractTable.stringtable.addString(stringConstant);
		return new Symbol(TokenConstants.STR_CONST, value);
	} else {
		return new Symbol(TokenConstants.ERROR, "String constant too long");
	}
}
<STRING_CONSTANT> \\\n {
	// Ref. Manual #10.2 -  Strings
	appendToBuffer("\n");
	curr_lineno++;
}

<STRING_CONSTANT> \n {
	// Ref. Manual #10.2 -  Strings
	/*
		If a string contains an unescaped newline, report that error as
		"Unterminated string constant" and resume lexing at the beginning of
		the next line—we assume the programmer simply forgot the close-quote.
	 */
	curr_lineno++;
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
}

<STRING_CONSTANT> \0 {
	// Ref. Manual #10.2 -  Strings
	/*
		Una cadena de caracteres no puede contener el caracter nulo (\0).
	 */
	return new Symbol(TokenConstants.ERROR, "Null character in string");
}

<STRING_CONSTANT> \\. {
	// Ref. Manual #10.2 -  Strings
	/*
	 *	Dentro de una cadena de caracteres, la secuencia '\c' denota el
	 *	caracter 'c' con la excepción de:
	 *	-	\b: backspace
	 *	-	\t: tab
	 *	-	\n: newline
	 *	-	\f: formfeed
	 */
	String str = yytext();
	Character ch = new Character(str.charAt(str.length() - 1));
	switch (ch) {
		case 'b':
			appendToBuffer("\b");
			break;
		case 't':
			appendToBuffer("\t");
			break;
		case 'n':
			appendToBuffer("\n");
			break;
		case 'f':
			appendToBuffer("\f");
			break;
		default:
			appendToBuffer(ch.toString());
			break;
	}
}

<STRING_CONSTANT> \r|. {
	// Ref. Manual #10.2 -  Strings
	appendToBuffer(yytext());
}

<YYINITIAL> "{" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LBRACE);
}

<YYINITIAL> "}" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.RBRACE);
}

<YYINITIAL> ";" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.SEMI);
}

<YYINITIAL> ":" {
	// Types - Ref.Manual#4
	return new Symbol(TokenConstants.COLON);
}

<YYINITIAL> "(" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LPAREN);
}

<YYINITIAL> ")" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.RPAREN);
}

<YYINITIAL> "," {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.COMMA);
}

<YYINITIAL> "<-" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.ASSIGN);
}

<YYINITIAL> "." {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.DOT);
}

<YYINITIAL> "@" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.AT);
}

<YYINITIAL> "=>" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.DARROW);
}

<YYINITIAL> "+" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.PLUS);
}

<YYINITIAL> "-" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.MINUS);
}

<YYINITIAL> "*" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.MULT);
}

<YYINITIAL> "/" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.DIV);
}

<YYINITIAL> "<" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LT);
}

<YYINITIAL> "<=" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LE);
}

<YYINITIAL> "=" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.EQ);
}

<YYINITIAL> "~" {
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.NEG);
}

<YYINITIAL> [a-z]({LETTER}|{DIGIT}|_)* {
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}

<YYINITIAL> [A-Z]({LETTER}|{DIGIT}|_)* {
	// Ref. Manual #10.1 -  Identifiers
	/*
		Los identificadores son cadenas (distintas de las palabras clave
		reservadas) consistentes en letras, dígitos y el caracter barra baja
		'_'. En el caso de los identificadores de tipo, empiezan en mayúscula.
		Debe registrarse su nombre con un objeto de tipo "AbstractSymbol".
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.TYPEID, value);
}

<YYINITIAL> {DIGIT}+ {
	// Ref. Manual #10.1 - Integers
	/*
		Los enteros son cadenas no vacías de los dígitos 0-9. Debe registrarse
		el valor semántico del número con un objeto de tipo "AbstractSymbol".
	 */
	AbstractSymbol value = AbstractTable.inttable.addString(yytext());
	return new Symbol(TokenConstants.INT_CONST, value);
}

. {
	// This rule should be the very last in your lexical specification and will
	// match everything not matched by other lexical rules. When a lexical
	// error is encountered, the routine CoolLexer.next token should return a
	// java cup.runtime.Symbol object whose syntactic category is
	// TokenConstants.ERROR and whose semantic value is the error message str.
	System.err.println("LEXER BUG - UNMATCHED: " + yytext());
	return new Symbol(TokenConstants.ERROR, yytext());
}
