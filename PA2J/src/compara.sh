#! /bin/sh

if [ "$1" != "" ]; then sampleFile=$1;
else
	echo -n "Introduce el nombre del fichero cool: ";
	read sampleFile;
	echo " ";
fi

make lexer
./lexer "$sampleFile" > result-custom.txt
lexer "$sampleFile" > result-default.txt
diff result-custom.txt result-default.txt
rm result-custom.txt result-default.txt

exit 0;
