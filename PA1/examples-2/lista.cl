class Main inherits IO {

      main(): Object {
      let hello : String <- "Hello ",
          world : String <- "Work",
          newline: String <- "\n"
          in
          
          out_string(hello.concat(world.concat(newline)))
          
      };     

};
