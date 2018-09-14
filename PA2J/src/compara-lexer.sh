#! /bin/sh

if [ "$1" != "" ]; then sampleFile=$1;
else
	echo -n "Introduce el nombre del fichero fuente Cool: ";
	read sampleFile;
	echo " ";
fi

make lexer

echo "Análisis del lexer:";
make lexer
./lexer "$sampleFile" | tee $sampleFile.xlex.txt
read key

echo "Diferencias con el lexer original:";
lexer "$sampleFile" > $sampleFile.lex.txt
diff $sampleFile.xlex.txt $sampleFile.lex.txt
rm lexc-$sampleFile.txt lex-$sampleFile.txt

exit 0;
