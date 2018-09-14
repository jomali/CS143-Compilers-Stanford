(*
 * 1.2 - EJEMPLO DE PROGRAMA CON UN BUCLE
 *
 * Author:	J. Francisco Martín
 * File:	p1_2.cl
 * Date:	2018/02/13
 *)

class Main inherits IO {

	read_number() : Int {
		{out_string("Introduce un número > ");
		in_int();}
	};

	main(): Object {
		let input: Int <- read_number(),
 			i: Int <- 0 in
		while (i < input) loop
			{i <- i + 1;
			out_string("Iteración ");
			out_int(i);
			out_string(" de ");
			out_int(input);
			out_string(".\n");}
		pool
	};

};
