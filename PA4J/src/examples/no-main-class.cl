
(*
 * 1.4 - UTILIZACIÓN DE VARIAS CLASES, HERENCIA, self Y SELF_TYPE
 *
 * Author:	J. Francisco Martín
 * File:	p1_4.cl
 * Date:	2018/02/14
 *)

Class Item inherits IO {
	title: String;
	author: String;
	date: Int;

	init(pTitle: String, pAuthor: String, pDate: Int) : SELF_TYPE {
		{
			title <- pTitle;
			author <- pAuthor;
			date <- pDate;
			self;
		}
	};

	read_item() : SELF_TYPE {
		let	input_title: String <- read_title(),
			input_author: String <- read_author(),
			input_date: Int <- read_date() in
		(new SELF_TYPE).init(input_title, input_author, input_date)
	};

	write_item() : SELF_TYPE {
		{
			out_string("Título: ".concat(title.concat("\n")));
			out_string("Autor: ".concat(author.concat("\n")));
			out_string("Fecha: ");
			out_int(date);
			out_string("\n");
			self;
		}
	};

	read_title() : String {
		{out_string("Introduce el título > ");
		in_string();}
	};

	read_author() : String {
		{out_string("Introduce el autor > ");
		in_string();}
	};

	read_date() : Int {
		{out_string("Introduce la fecha de creación > ");
		in_int();}
	};
};

Class Film inherits Item {
	read_title() : String {
		{out_string("Introduce el título de la película > ");
		in_string();}
	};
};

Class Book inherits Item {
	read_title() : String {
		{out_string("Introduce el título del libro > ");
		in_string();}
	};
};
