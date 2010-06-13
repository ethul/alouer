/**
 * 
 */
package com.alouer

/**
 * @author ethul
 *
 */
class MapGenerator {
  private[this] val html = """
    <html>
    <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
    <script type="text/javascript">
      function initialize() {
        var latlng = new google.maps.LatLng(45.508867, -73.554242);
        var options = {
          zoom: 11,
          center: latlng,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById("map_canvas"), options);
        $markerjs
        $eventjs
      }
    </script>
    </head>
    <body onload="initialize()">
      <div id="map_canvas" style="width:100%; height:100%"></div>
    </body>
    </html>
  """

  private[this] val markerjs = """  
    var marker_$id = new google.maps.Marker({
      position: new google.maps.LatLng($lat, $lng),
      title: "$title",
      map: map
    });
  """
   
  private[this] val eventjs = """
    google.maps.event.addListener(marker_$id, 'click', function() { 
      var content =
        "<b>$title</b>" +
        "<p>$address</p>" +
        "<p>$description</p>" +
        '<a href="$link">more details</a>';
      new google.maps.InfoWindow({content: content}).open(map, marker_$id);
    });
  """
    
  def generate(markers: Seq[MapMarker]): String = {
    val markerResults = markers.map { a =>
      markerjs.
        replace("$lat", a.geolocation.latitude).
        replace("$lng", a.geolocation.longitude).
        replace("$title", a.item.title).
        replace("$id", a.geolocation.id)
    }
    
    val eventResults = markers.map { a =>
      eventjs.
        replace("$title", a.item.title).
        replace("$address", a.item.address).
        replace("$description", a.item.description).
        replace("$link", a.item.link).
        replace("$id", a.geolocation.id)
    }
    
    html.
      replace("$markerjs", markerResults.mkString("")).
      replace("$eventjs", eventResults.mkString(""))
  }
}