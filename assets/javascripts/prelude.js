(function(){
  var factory = {
    gmaps: function(a){
      return new GMaps(a || {});
    },
    drawingManager: function(a){
      return new google.maps.drawing.DrawingManager(a || {});
    },
  };
  this["Alouer"] = {factory: factory};
}());
