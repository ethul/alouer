(function(){var e={gmaps:function(e){return new GMaps(e||{})},drawingManager:function(e){return new google.maps.drawing.DrawingManager(e||{})}};this.Alouer={factory:e}})(),function(){var e=Alouer.factory.gmaps,t=Alouer.factory.drawingManager;$(document).ready(function(){var n=e({div:"#map",lat:45.4214,lng:-75.6919,width:$(window).width()+"px",height:$(window).height()+"px"}),r=t({drawingControlOptions:{drawingModes:[google.maps.drawing.OverlayType.POLYGON]},polygonOptions:{fillColor:"red",fillOpacity:.25,strokeWeight:1,clickable:!1,editable:!0}}),i=function(e){return e.forEach(function(e){return console.log(e.toString())})};return google.maps.event.addListener(r,"polygoncomplete",function(e){var t=e.getPath();return google.maps.event.addListener(t,"set_at",function(){return i(t)}),google.maps.event.addListener(t,"insert_at",function(){return i(t)}),google.maps.event.addListener(t,"remove_at",function(){return i(t)}),i(t)}),r.setMap(n.map)})}();