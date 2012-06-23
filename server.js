var express = require("express")
  , gzippo = require("gzippo")
  , http = require("http");

var app = express();

app.configure(function(){
  app.set("port", process.env.PORT || 5000);
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(express.logger("dev"));
});

app.configure("development", function(){
  app.use(express.errorHandler());
  app.use(express.static(__dirname + "/public"));
});

app.configure("production", function(){
  var fiveYears = 31557600000 * 5;
  app.use(gzippo.staticGzip(__dirname + "/public", {
    maxAge: fiveYears,
    clientMaxAge: fiveYears 
  }));
});

app.get("/", function(req,res){
  res.sendfile(__dirname + "/public/index.html");
});

http.createServer(app).listen(app.get("port"), function(){
  var port = app.get("port")
  var env = process.env.NODE_ENV || "development";
  console.log("Express server listening on port " + port + " in " + env);
});
