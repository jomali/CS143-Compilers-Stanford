
(*
 * 1.1 - PROGRAMA "HOLA MUNDO"
 *
 * Author:	J. Francisco Martín
 * File:	p1_1b.cl
 * Date:	2018/02/13
 *)

class Main {
	io : IO <- new IO;
	main() : Object {
		io.out_string("Hola mundo.\n")
	};
};
