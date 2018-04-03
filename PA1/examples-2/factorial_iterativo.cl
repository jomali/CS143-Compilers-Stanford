class Main inherits A2I
{
        i : IO <- new IO;
        main() : Object {
               i.out_string(i2a(fact(a2i(i.in_string() ))).concat("\n"))

        };
        fact(i : Int) : Int {
               let  fact: Int  <- 1 in 
               {
                 while (not (i=0)) loop
                 {
                        fact <- fact * i;
                        i <- i - 1;
                 }
                 pool;
                 fact;             
               }
        };       
};
