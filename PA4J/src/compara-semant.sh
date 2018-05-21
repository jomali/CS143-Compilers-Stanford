#! /bin/sh

if [ "$1" != "" ]; then sampleFile=$1;
else
	echo -n "Introduce el nombre del fichero fuente cool: ";
	read sampleFile;
	echo " ";
fi

if [ "$2" != "" ]; then lexerProgram=$2;
else lexerProgram=lexer
fi

if [ "$3" != "" ]; then parserProgram=$3;
else parserProgram=parser
fi

make semant
$lexerProgram "$sampleFile" | $parserProgram "$sampleFile" | ./mysemant "$sampleFile" > result-custom.txt
$lexerProgram "$sampleFile" | $parserProgram "$sampleFile" | semant "$sampleFile" > result-default.txt
diff result-custom.txt result-default.txt
# rm result-custom.txt result-default.txt

exit 0;
