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


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 65536;
	private final int YY_EOF = 65537;

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
		string_buf.append(text);
	}
	private String getStringFromBuffer() {
		return string_buf.length() <= MAX_STR_CONST
			? string_buf.toString() : null;
	}
	/*
	 * Reinicia el StringBuffer utilizado para formar constantes de tipo cadena
	 * de caracteres.
	 */
	private void restartBuffer() {
		string_buf.delete(0, string_buf.length());
		string_buf.setLength(0);
	}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*
 *  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially.
 */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING_CONSTANT = 2;
	private final int BLOCK_COMMENT = 1;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0,
		60,
		82
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NOT_ACCEPT,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,65538,
"30,23:8,1,2,1:2,27,23:18,1,23,28,23:5,24,26,25,41,35,22,37,42,61:10,34,33,3" +
"6,39,40,23,38,4,60,3,17,6,9,60,12,10,60:2,7,60,11,16,18,60,13,5,14,21,15,19" +
",60:3,23,29,23:2,62,23,44,45,46,47,48,8,45,49,50,45:2,51,45,52,53,54,45,55," +
"56,20,57,58,59,45:3,31,23,32,43,23:65409,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,168,
"0,1,2,1,3,4,5,1,6,7,1:7,8,1:2,9,1:3,10,11,12,13,12,14,1:5,12:9,11,12,11,12:" +
"3,1:10,15,16,17,12,11,18,11:14,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33" +
",34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58" +
",59,60,61,62,12,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82" +
",83,84,85,86,87,88,89,90,91,92,93,94,12,11,95,96,97,98,99,100,101,102,103")[0];

	private int yy_nxt[][] = unpackFromString(104,63,
"1,2,3,4,157:2,159,116,5,61,83,118,157:2,161,157,86,157,163,165,62,157,6,7,8" +
",9,10,2,11,7:2,12,13,14,15,16,17,18,19,20,7,21,22,23,158:2,160,158,162,158," +
"84,117,119,87,164,158:4,166,157,24,7,-1:64,2,-1:25,2,-1:38,157,120,157:2,12" +
"2,157:14,-1:22,120,157:6,122,157:9,124:2,-1:3,158,167,158:5,25,158:11,-1:22" +
",167,158:5,25,158:12,-1:22,29,-1:65,30,-1:63,31,-1:58,32,-1:16,33,-1:63,34," +
"-1:83,24,-1:4,158:19,-1:22,158:19,-1:3,157:19,-1:22,157:17,124:2,-1:3,157:9" +
",144,157:9,-1:22,157:5,144,157:11,124:2,-1,29,-1,29:60,1,50,51,50:21,80,85," +
"50:37,-1:3,157:7,63,157:11,-1:22,157:6,63,157:10,124:2,-1:3,158:9,121,123,1" +
"58:8,-1:22,158:5,121,158:5,123,158:7,-1:3,158:9,147,158:9,-1:22,158:5,147,1" +
"58:13,-1:25,52,-1:38,58,59,58:24,-1,58:35,1,54,55,54:25,56,81,57,54:32,-1:3" +
",157:2,132,157:2,26:2,157,27,157:10,-1:22,157:8,27,157:3,132,157:4,124:2,-1" +
":3,158:2,133,158:2,64:2,158,65,158:10,-1:22,158:8,65,158:3,133,158:6,-1:26," +
"53,-1:39,157:5,28:2,157:12,-1:22,157:17,124:2,-1:3,158:5,66:2,158:12,-1:22," +
"158:19,-1:3,157:11,35,157:5,35,157,-1:22,157:17,124:2,-1:3,158:11,67,158:5," +
"67,158,-1:22,158:19,-1:3,157:16,36,157:2,-1:22,157:15,36,157,124:2,-1:3,158" +
":16,68,158:2,-1:22,158:15,68,158:3,-1:3,157:11,37,157:5,37,157,-1:22,157:17" +
",124:2,-1:3,158:11,69,158:5,69,158,-1:22,158:19,-1:3,157:3,38,157:15,-1:22," +
"157:4,38,157:12,124:2,-1:3,158:8,74,158:10,-1:22,158:8,74,158:10,-1:3,39,15" +
"7:18,-1:22,157:2,39,157:14,124:2,-1:3,158:3,44,158:15,-1:22,158:4,44,158:14" +
",-1:3,157:3,40,157:15,-1:22,157:4,40,157:12,124:2,-1:3,158:3,70,158:15,-1:2" +
"2,158:4,70,158:14,-1:3,157:15,41,157:3,-1:22,157:10,41,157:6,124:2,-1:3,71," +
"158:18,-1:22,158:2,71,158:16,-1:3,157:8,42,157:10,-1:22,157:8,42,157:8,124:" +
"2,-1:3,158:3,72,158:15,-1:22,158:4,72,158:14,-1:3,157:4,43,157:14,-1:22,157" +
":7,43,157:9,124:2,-1:3,158:15,73,158:3,-1:22,158:10,73,158:8,-1:3,157:2,45," +
"157:16,-1:22,157:12,45,157:4,124:2,-1:3,158:4,75,158:14,-1:22,158:7,75,158:" +
"11,-1:3,157:3,47,157:15,-1:22,157:4,47,157:12,124:2,-1:3,158:3,46,158:15,-1" +
":22,158:4,46,158:14,-1:3,157:14,48,157:4,-1:22,157:3,48,157:13,124:2,-1:3,1" +
"58:2,76,158:16,-1:22,158:12,76,158:6,-1:3,157:2,49,157:16,-1:22,157:12,49,1" +
"57:4,124:2,-1:3,158:3,77,158:15,-1:22,158:4,77,158:14,-1:3,158:14,78,158:4," +
"-1:22,158:3,78,158:15,-1:3,158:2,79,158:16,-1:22,158:12,79,158:6,-1:3,157:3" +
",88,157:9,130,157:5,-1:22,157:4,88,157:4,130,157:7,124:2,-1:3,158:3,89,158:" +
"9,135,158:5,-1:22,158:4,89,158:4,135,158:9,-1:3,157:3,90,157:9,92,157:5,-1:" +
"22,157:4,90,157:4,92,157:7,124:2,-1:3,158:3,91,158:9,93,158:5,-1:22,158:4,9" +
"1,158:4,93,158:9,-1:3,157:2,94,157:16,-1:22,157:12,94,157:4,124:2,-1:3,158:" +
"3,95,158:15,-1:22,158:4,95,158:14,-1:3,157,140,157:17,-1:22,140,157:16,124:" +
"2,-1:3,158:18,97,-1:22,158:13,97,158:5,-1:3,158:2,99,158:16,-1:22,158:12,99" +
",158:6,-1:3,157,96,157:17,-1:22,96,157:16,124:2,-1:3,158,143,158:17,-1:22,1" +
"43,158:18,-1:3,157:2,98,157:16,-1:22,157:12,98,157:4,124:2,-1:3,158,101,158" +
":17,-1:22,101,158:18,-1:3,157:13,100,157:5,-1:22,157:9,100,157:7,124:2,-1:3" +
",158:2,103,158:16,-1:22,158:12,103,158:6,-1:3,157:12,142,157:6,-1:22,157:14" +
",142,157:2,124:2,-1:3,158:12,145,158:6,-1:22,158:14,145,158:4,-1:3,157:3,10" +
"2,157:15,-1:22,157:4,102,157:12,124:2,-1:3,158:13,105,158:5,-1:22,158:9,105" +
",158:9,-1:3,157:13,104,157:5,-1:22,157:9,104,157:7,124:2,-1:3,158:13,107,15" +
"8:5,-1:22,158:9,107,158:9,-1:3,157:7,146,157:11,-1:22,157:6,146,157:10,124:" +
"2,-1:3,158:7,149,158:11,-1:22,158:6,149,158:12,-1:3,157:2,106,157:16,-1:22," +
"157:12,106,157:4,124:2,-1:3,158:2,109,158:16,-1:22,158:12,109,158:6,-1:3,15" +
"7:13,148,157:5,-1:22,157:9,148,157:7,124:2,-1:3,158:2,111,158:16,-1:22,158:" +
"12,111,158:6,-1:3,157:3,150,157:15,-1:22,157:4,150,157:12,124:2,-1:3,158:13" +
",151,158:5,-1:22,158:9,151,158:9,-1:3,157:4,108,157:14,-1:22,157:7,108,157:" +
"9,124:2,-1:3,158:3,153,158:15,-1:22,158:4,153,158:14,-1:3,157:7,110,157:11," +
"-1:22,157:6,110,157:10,124:2,-1:3,158:4,113,158:14,-1:22,158:7,113,158:11,-" +
"1:3,157:10,152,157:8,-1:22,157:11,152,157:5,124:2,-1:3,158:7,114,158:11,-1:" +
"22,158:6,114,158:12,-1:3,157:7,154,157:11,-1:22,157:6,154,157:10,124:2,-1:3" +
",158:10,155,158:8,-1:22,158:11,155,158:7,-1:3,157:11,112,157:5,112,157,-1:2" +
"2,157:17,124:2,-1:3,158:7,156,158:11,-1:22,158:6,156,158:12,-1:3,158:11,115" +
",158:5,115,158,-1:22,158:19,-1:3,157:2,126,157,128,157:14,-1:22,157:7,128,1" +
"57:4,126,157:4,124:2,-1:3,158,125,158:2,127,158:14,-1:22,125,158:6,127,158:" +
"11,-1:3,157:9,134,157:9,-1:22,157:5,134,157:11,124:2,-1:3,158:2,129,158,131" +
",158:14,-1:22,158:7,131,158:4,129,158:6,-1:3,157:13,136,157:5,-1:22,157:9,1" +
"36,157:7,124:2,-1:3,158:13,137,158:5,-1:22,158:9,137,158:9,-1:3,157:9,138,1" +
"57:9,-1:22,157:5,138,157:11,124:2,-1:3,158:9,139,158:9,-1:22,158:5,139,158:" +
"13,-1:3,158:4,141,158:14,-1:22,158:7,141,158:11");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{
	// Ref. Manual #10.5 - White Space
	/*
	 *	JLEX no reconoce el tabulador vertical '\v' (uno de los caracteres
	 *	considerados espacio en blanco en Cool). Por ello se utiliza el valor
	 *	hexadecimal (0b) de su representación ASCII (11). Ref.:
	 *	<https://groups.google.com/forum/#!topic/ucb.class.cs164/vCfBi-kkieg>
	 */
}
					case -3:
						break;
					case 3:
						{
	// Ref. Manual #10.5 - White Space
	curr_lineno++;
}
					case -4:
						break;
					case 4:
						{
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
					case -5:
						break;
					case 5:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -6:
						break;
					case 6:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.MINUS);
}
					case -7:
						break;
					case 7:
						{
	// This rule should be the very last in your lexical specification and will
	// match everything not matched by other lexical rules. When a lexical
	// error is encountered, the routine CoolLexer.next token should return a
	// java cup.runtime.Symbol object whose syntactic category is
	// TokenConstants.ERROR and whose semantic value is the error message str.
	System.err.println("LEXER BUG - UNMATCHED: " + yytext());
	return new Symbol(TokenConstants.ERROR, yytext());
}
					case -8:
						break;
					case 8:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LPAREN);
}
					case -9:
						break;
					case 9:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.MULT);
}
					case -10:
						break;
					case 10:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.RPAREN);
}
					case -11:
						break;
					case 11:
						{
	// Ref. Manual #10.2 -  Strings
	restartBuffer();
	yybegin(STRING_CONSTANT);
}
					case -12:
						break;
					case 12:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LBRACE);
}
					case -13:
						break;
					case 13:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.RBRACE);
}
					case -14:
						break;
					case 14:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.SEMI);
}
					case -15:
						break;
					case 15:
						{
	// Types - Ref.Manual#4
	return new Symbol(TokenConstants.COLON);
}
					case -16:
						break;
					case 16:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.COMMA);
}
					case -17:
						break;
					case 17:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LT);
}
					case -18:
						break;
					case 18:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.DOT);
}
					case -19:
						break;
					case 19:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.AT);
}
					case -20:
						break;
					case 20:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.EQ);
}
					case -21:
						break;
					case 21:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.PLUS);
}
					case -22:
						break;
					case 22:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.DIV);
}
					case -23:
						break;
					case 23:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.NEG);
}
					case -24:
						break;
					case 24:
						{
	// Ref. Manual #10.1 - Integers
	/*
		Los enteros son cadenas no vacías de los dígitos 0-9. Debe registrarse
		el valor semántico del número con un objeto de tipo "AbstractSymbol".
	 */
	AbstractSymbol value = AbstractTable.inttable.addString(yytext());
	return new Symbol(TokenConstants.INT_CONST, value);
}
					case -25:
						break;
					case 25:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.FI);
}
					case -26:
						break;
					case 26:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.IF);
}
					case -27:
						break;
					case 27:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.IN);
}
					case -28:
						break;
					case 28:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.OF);
}
					case -29:
						break;
					case 29:
						{
	// Ref. Manual #10.3 -  Comments
	/*
	 * Inline comments.
	 */
}
					case -30:
						break;
					case 30:
						{
	// Ref. Manual #10.3 -  Comments
	nested_comment_level++;
	yybegin(BLOCK_COMMENT);
}
					case -31:
						break;
					case 31:
						{
	// Ref. Manual #10.3 -  Comments
	return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}
					case -32:
						break;
					case 32:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.ASSIGN);
}
					case -33:
						break;
					case 33:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.LE);
}
					case -34:
						break;
					case 34:
						{
	// Ref. Manual #10.1 -  Special Notation
	return new Symbol(TokenConstants.DARROW);
}
					case -35:
						break;
					case 35:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.LET);
}
					case -36:
						break;
					case 36:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.NEW);
}
					case -37:
						break;
					case 37:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.NOT);
}
					case -38:
						break;
					case 38:
						{
	// Ref. Manual #10.4 - Keywords
	/*
		Las palabras clave reservadas son insensibles a mayúsculas y minúsculas
		(salvo por 'true' y 'false').
	*/
	return new Symbol(TokenConstants.CASE);
}
					case -39:
						break;
					case 39:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ESAC);
}
					case -40:
						break;
					case 40:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ELSE);
}
					case -41:
						break;
					case 41:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.LOOP);
}
					case -42:
						break;
					case 42:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.THEN);
}
					case -43:
						break;
					case 43:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.POOL);
}
					case -44:
						break;
					case 44:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.TRUE);
}
					case -45:
						break;
					case 45:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.CLASS);
}
					case -46:
						break;
					case 46:
						{
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
					case -47:
						break;
					case 47:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.WHILE);
}
					case -48:
						break;
					case 48:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ISVOID);
}
					case -49:
						break;
					case 49:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.INHERITS);
}
					case -50:
						break;
					case 50:
						{
	// Ref. Manual #10.3 -  Comments
}
					case -51:
						break;
					case 51:
						{
	// Ref. Manual #10.3 -  Comments
	curr_lineno++;
}
					case -52:
						break;
					case 52:
						{
	// Ref. Manual #10.3 -  Comments
	nested_comment_level++;
}
					case -53:
						break;
					case 53:
						{
	// Ref. Manual #10.3 -  Comments
	nested_comment_level--;
	if (nested_comment_level == 0) yybegin(YYINITIAL);
}
					case -54:
						break;
					case 54:
						{
	// Ref. Manual #10.2 -  Strings
	appendToBuffer(yytext());
}
					case -55:
						break;
					case 55:
						{
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
					case -56:
						break;
					case 56:
						{
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
					case -57:
						break;
					case 57:
						{
	// Ref. Manual #10.2 -  Strings
	/*
		Una cadena de caracteres no puede contener el caracter nulo (\0).
	 */
	return new Symbol(TokenConstants.ERROR, "Null character in string");
}
					case -58:
						break;
					case 58:
						{
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
					case -59:
						break;
					case 59:
						{
	// Ref. Manual #10.2 -  Strings
	appendToBuffer("\n");
	curr_lineno++;
}
					case -60:
						break;
					case 61:
						{
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
					case -61:
						break;
					case 62:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -62:
						break;
					case 63:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.FI);
}
					case -63:
						break;
					case 64:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.IF);
}
					case -64:
						break;
					case 65:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.IN);
}
					case -65:
						break;
					case 66:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.OF);
}
					case -66:
						break;
					case 67:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.LET);
}
					case -67:
						break;
					case 68:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.NEW);
}
					case -68:
						break;
					case 69:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.NOT);
}
					case -69:
						break;
					case 70:
						{
	// Ref. Manual #10.4 - Keywords
	/*
		Las palabras clave reservadas son insensibles a mayúsculas y minúsculas
		(salvo por 'true' y 'false').
	*/
	return new Symbol(TokenConstants.CASE);
}
					case -70:
						break;
					case 71:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ESAC);
}
					case -71:
						break;
					case 72:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ELSE);
}
					case -72:
						break;
					case 73:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.LOOP);
}
					case -73:
						break;
					case 74:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.THEN);
}
					case -74:
						break;
					case 75:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.POOL);
}
					case -75:
						break;
					case 76:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.CLASS);
}
					case -76:
						break;
					case 77:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.WHILE);
}
					case -77:
						break;
					case 78:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.ISVOID);
}
					case -78:
						break;
					case 79:
						{
	// Ref. Manual #10.4 - Keywords
	return new Symbol(TokenConstants.INHERITS);
}
					case -79:
						break;
					case 80:
						{
	// Ref. Manual #10.3 -  Comments
}
					case -80:
						break;
					case 81:
						{
	// Ref. Manual #10.2 -  Strings
	appendToBuffer(yytext());
}
					case -81:
						break;
					case 83:
						{
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
					case -82:
						break;
					case 84:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -83:
						break;
					case 85:
						{
	// Ref. Manual #10.3 -  Comments
}
					case -84:
						break;
					case 86:
						{
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
					case -85:
						break;
					case 87:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -86:
						break;
					case 88:
						{
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
					case -87:
						break;
					case 89:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -88:
						break;
					case 90:
						{
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
					case -89:
						break;
					case 91:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -90:
						break;
					case 92:
						{
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
					case -91:
						break;
					case 93:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -92:
						break;
					case 94:
						{
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
					case -93:
						break;
					case 95:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -94:
						break;
					case 96:
						{
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
					case -95:
						break;
					case 97:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -96:
						break;
					case 98:
						{
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
					case -97:
						break;
					case 99:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -98:
						break;
					case 100:
						{
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
					case -99:
						break;
					case 101:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -100:
						break;
					case 102:
						{
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
					case -101:
						break;
					case 103:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -102:
						break;
					case 104:
						{
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
					case -103:
						break;
					case 105:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -104:
						break;
					case 106:
						{
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
					case -105:
						break;
					case 107:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -106:
						break;
					case 108:
						{
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
					case -107:
						break;
					case 109:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -108:
						break;
					case 110:
						{
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
					case -109:
						break;
					case 111:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -110:
						break;
					case 112:
						{
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
					case -111:
						break;
					case 113:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -112:
						break;
					case 114:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -113:
						break;
					case 115:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -114:
						break;
					case 116:
						{
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
					case -115:
						break;
					case 117:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -116:
						break;
					case 118:
						{
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
					case -117:
						break;
					case 119:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -118:
						break;
					case 120:
						{
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
					case -119:
						break;
					case 121:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -120:
						break;
					case 122:
						{
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
					case -121:
						break;
					case 123:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -122:
						break;
					case 124:
						{
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
					case -123:
						break;
					case 125:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -124:
						break;
					case 126:
						{
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
					case -125:
						break;
					case 127:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -126:
						break;
					case 128:
						{
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
					case -127:
						break;
					case 129:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -128:
						break;
					case 130:
						{
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
					case -129:
						break;
					case 131:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -130:
						break;
					case 132:
						{
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
					case -131:
						break;
					case 133:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -132:
						break;
					case 134:
						{
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
					case -133:
						break;
					case 135:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -134:
						break;
					case 136:
						{
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
					case -135:
						break;
					case 137:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -136:
						break;
					case 138:
						{
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
					case -137:
						break;
					case 139:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -138:
						break;
					case 140:
						{
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
					case -139:
						break;
					case 141:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -140:
						break;
					case 142:
						{
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
					case -141:
						break;
					case 143:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -142:
						break;
					case 144:
						{
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
					case -143:
						break;
					case 145:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -144:
						break;
					case 146:
						{
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
					case -145:
						break;
					case 147:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -146:
						break;
					case 148:
						{
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
					case -147:
						break;
					case 149:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -148:
						break;
					case 150:
						{
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
					case -149:
						break;
					case 151:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -150:
						break;
					case 152:
						{
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
					case -151:
						break;
					case 153:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -152:
						break;
					case 154:
						{
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
					case -153:
						break;
					case 155:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -154:
						break;
					case 156:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -155:
						break;
					case 157:
						{
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
					case -156:
						break;
					case 158:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -157:
						break;
					case 159:
						{
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
					case -158:
						break;
					case 160:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -159:
						break;
					case 161:
						{
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
					case -160:
						break;
					case 162:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -161:
						break;
					case 163:
						{
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
					case -162:
						break;
					case 164:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -163:
						break;
					case 165:
						{
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
					case -164:
						break;
					case 166:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -165:
						break;
					case 167:
						{
	// Ref. Manual #10.1 -  Identifiers
	/*
		Ídem a los identificadores de tipo, con la primera letra en minúscula.
	 */
	AbstractSymbol value = AbstractTable.idtable.addString(yytext());
	return new Symbol(TokenConstants.OBJECTID, value);
}
					case -166:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
