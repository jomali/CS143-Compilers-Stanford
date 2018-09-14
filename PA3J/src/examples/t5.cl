
(*
 * 1.5 - PROGRAMA CON UN BUCLE IF
 *
 * Author:	J. Francisco Martín
 * File:	p1_5.cl
 * Date:	2018/02/14
 *)

class Main inherits IO {

	object_a: String <- "casa";
	object_b: String <- "caracol";

	prompt(): String {
		{out_string("'quit()' para salir > ");
		in_string();}
	};

	welcome_message() : SELF_TYPE {
		{
			out_string("Objetos: ");
			out_string(object_a);
			out_string(", ");
			out_string(object_b);
			out_string(".\n");
			self;
		}
	};

	main(): Object {
		let x : SELF_TYPE <- welcome_message() in
		while true loop
			(let input: String <- prompt() in
			if input = "quit()" then abort() else
			if input = object_a then out_string("¡Objeto reconocido!\n") else
			if input = object_b then out_string("¡Objeto reconocido!\n") else
			out_string("Nada con ese nombre.\n")
			fi fi fi)
		pool
	};
};
