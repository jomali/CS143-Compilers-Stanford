
(*
 * 1.6 - PROGRAMA CON OPERACIONES ARITMÉTICAS
 *
 * Author:	J. Francisco Martín
 * File:	p1_6.cl
 * Date:	2018/02/14
 *)

class Main inherits IO {
	read_integer(prompt : String) : Int {
		{out_string(prompt);
		in_int();}
	};

	write_result(text : String, p : Int, q : Int, gcd : Int) : Object {
		{out_string(text);
		out_string(": ");
		out_int(p);
		out_string(", ");
		out_int(q);
		out_string(" = ");
		out_int(gcd);
		out_string("\n");}
	};

	-- Método recursivo para calcular el máximo común divisor
	gcd_rec(p : Int, q : Int) : Int {
		if q = 0 then p
		else
			let	c : Int <- p / q,
				r : Int <- p - (q*c) in
			gcd_rec(q, r)
		fi
	};

	-- Método iterativo para calcular el máximo común divisor
	gcd_iter(p : Int, q : Int) : Int {
		let result: Int <- p in
		{
			while (not (q=0)) loop
				let	c : Int <- result / q,
					r : Int <- result - (q*c) in
				{
					result <- q;
					q <- r;
				}
			pool;
			result;
		}
	};

	main() : Object {
		let	p : Int <- read_integer("Introduce el número 'p': "),
			q : Int <- read_integer("Introduce el número 'q': "),
 			result_rec : Int <- gcd_rec(p, q),
 			result_iter : Int <- gcd_iter(p, q) in
		{write_result("Máximo común divisor (recursivo)", p, q, result_rec);
		write_result("Máximo común divisor (iterativo)", p, q, result_iter);}
	};
};
