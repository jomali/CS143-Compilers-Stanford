class List {

      item: String;
      next: List;

      init(i: String, n: List): List
      {
        {
        item <- i;
        next <- n;
        self;
        }
      };
      flaten(): String {
       if (isvoid next) then
          item
       else
          item.concat(next.flaten())
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
                (new List).init(newline,vacia)))
          in
          
          out_string(list.flaten())
          
      };     

};
