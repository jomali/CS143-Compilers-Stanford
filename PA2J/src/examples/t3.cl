(*
	1.3 - PROGRAMA PARA TOMAR DATOS DE TECLADO

	J. Francisco Martín
	2018/02/14
*)

class Main inherits IO {

	read_name() : String {
		{out_string("Introduce tu nombre > ");
		in_string();}
	};

	read_dni() : Int {
		{out_string("Introduce los números de tu DNI (sin la letra) > ");
		in_int();}
	};

	main() : Object {
		let	name : String <- read_name(),
			dni : String <- (new A2I).i2a(read_dni())
			in {
				out_string(name.concat(" ".concat(dni.concat("\n"))));
			}
	};

};
