var vows = require("vows")
  , assert = require("assert")
  , fs = require("fs")
  , injectr = require("injectr")
  , sinon = require("sinon")
  , request = sinon.stub()
  , geo = {google: "", geocoder: sinon.stub()}
  , alouer = injectr("./app/alouer.js", {
    request: request,
    geo: geo
  });

var rss = fs.readFileSync("./test/fixtures/small.rss").toString();

vows.describe("Alouer").addBatch({
  "#apartments": {
    "when the apartments are fetched successfully": {
      topic: function(){
        request.callsArgWith(1, null, null, rss);
        geo.geocoder.callsArgWith(3, "a", 0.0, 0.0, null);
        var that = this;
        alouer.apartments(function(a){
          // By using injectr, it seems that any time an Array is passed
          // to this function, it is converted to an arguments list. So
          // manually slicing it back to an array.
          that.callback(null, Array.prototype.slice.call(a, 0));
        });
      },
      "should result in an array": function(res){
        assert.isArray(res);
      },
      "should be non-empty": function(res){
        assert.isTrue(res.length > 0);
      },
      "should consist of apartment types": function(res){
        res.forEach(function(a){
          assert.include(a, "id");
          assert.include(a, "title");
          assert.include(a, "description");
          assert.include(a, "url");
          assert.include(a, "posted");
          assert.include(a, "address");
          assert.include(a, "lat");
          assert.include(a, "lng");
        });
      }
    }
  }
}).export(module);
