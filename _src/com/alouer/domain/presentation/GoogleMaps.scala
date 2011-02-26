/**
 * 
 */
package com.alouer.domain.presentation

import com.alouer.domain.parser.RssItemizable
import com.alouer.domain.util.{Geolocation,Geopolygon}
import com.alouer.service.persistence.Configuration
import com.alouer.service.util.Logger
import com.google.gdata.client.{GoogleService,ClientLoginAccountType}
import com.google.gdata.client.Service.GDataRequest
import com.google.gdata.client.Service.GDataRequest.RequestType
import com.google.gdata.util.ContentType
import scala.collection.mutable.ListBuffer
import scala.xml.XML
import java.net.{URL,URLEncoder}
import java.io.OutputStreamWriter

/**
 * @author ethul
 *
 */
case class GoogleMaps(username: String, password: String) {
  private[this] val infolog = Logger.log(Logger.Info)
  private[this] val error = Logger.log(Logger.Error)
  private[this] val encoding = "utf-8"
  private[this] val application = "alouer"
  private[this] val defaultLat = Configuration.get("google.maps.default.latitude").get
  private[this] val defaultLng = Configuration.get("google.maps.default.longitude").get
  private[this] val tableId = Configuration.get("google.maps.tableid").get

  private[this] val fusionUri = "https://www.google.com/fusiontables/api/query"

  infolog("getting service for " + username)
  private[this] val service = try {
    val service = new GoogleService("fusiontables", application);
    service.setUserCredentials(username, password, ClientLoginAccountType.GOOGLE);
    service
  }
  catch {
    case e: Exception => {
      error(e)
      null
    }
  }

  def createFeatures(features: List[RssItemizable]) {
    features.foreach { a => 
      val title = a.title.replaceAll("'","\\'")
      val description = a.description.replaceAll("'","\\'")
      val address = a.address.replaceAll("'","\\'")
      val link = a.link.replaceAll("'","\\'")
      val sql = "insert into "+tableId+" (title,description,address,url) values ('"+title+"','"+description+"','"+address+"','"+link+"')"

      try {
        infolog("running sql: " + sql)
        runSql(sql)
      }
      catch {
        case e: Exception => {
          error(e)
          infolog("unable to insert feature: " + a)
        }
      }
    }
  }
  
  def deleteFeatures() {
  }

  def getFeaturePolygons(): List[Geopolygon] = {
    Nil
  }
  
  def listFeatures() {
  }

  private[this] def runSql(sql: String) {
    val url = new URL(fusionUri)
    val request = service.getRequestFactory().getRequest(RequestType.INSERT, url, 
        new ContentType("application/x-www-form-urlencoded"));
    val writer = new OutputStreamWriter(request.getRequestStream());
    writer.append("sql=" + URLEncoder.encode(sql,encoding));
    writer.flush();
    request.execute();
  }
}
