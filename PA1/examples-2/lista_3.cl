class List inherits A2I{

      item: Object;
      next: List;

      init(i: Object, n: List): List
      {
        {
        item <- i;
        next <- n;
        self;
        }
      };
      flaten(): String {
       let string: String <-
           case item of
                i: Int => i2a(i);
                s: String => s;
                o: Object => {abort();"";};
           esac
       in
          if (isvoid next) then
             string
          else
             string.concat(next.flaten())
          fi
      };
}; 


class Main inherits IO {

      main(): Object {
      let hello : String <- "Hello ",
          world : String <- "Work",
          newline: String <- "\n",
          vacia: List,
          list: List <- (new List).init(hello,
                (new List).init(world,
                (new List).init(23,
                (new List).init(newline,vacia)
                )
                )
                )
          in
          
          out_string(list.flaten())
          
      };     

};
