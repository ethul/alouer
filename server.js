var express = require("express")
  , gzippo = require("gzippo")
  , alouer = require("./app/alouer")
  , app = express();

app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(express.logger("dev"));
app.use(express.favicon());

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

app.get("/", function(req, res){
  res.sendfile(__dirname + "/public/index.html");
});

app.get("/apartments", function(req, res){
  alouer.apartments(function(body){
    res.json(200, body);
  });
});

var port = process.env.PORT || 5000
  , env = process.env.NODE_ENV || "development";

app.listen(port);

console.log("Express server listening on port " + port + " in " + env);
