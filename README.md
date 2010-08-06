alouer
======

### introduction

the purpose of this program is to fetch information about apartments
for rent from kijiji and craigslist and then display this information
on a google map.

### installation

to install this program, look in the _release directory and get the
latest version of the alouer jar. additionally, this program requires
a third-party jars to be in the classpath (just put them in the same
directory as the alouer jar). 

download the following third-party jars. the jars listed below the 
urls are the only ones really needed:
 * http://code.google.com/p/gdata-java-client/downloads/detail?name=gdata.java-1.41.3.zip
   * gdata-client-1.0.jar
   * gdata-maps-2.0.jar
   * gdata-base-1.0.jar
   * gdata-core-1.0.jar
   * gdata-media-1.0.jar
 * guava-r05.zip [http://guava-libraries.googlecode.com/files/guava-r05.zip]
   * guava-r05.jar
 * http://www.oracle.com/technetwork/java/index-138643.html
   * mail.jar
 * http://home.ccil.org/~cowan/XML/tagsoup/tagsoup-1.2.jar
   * tagsoup-1.2.jar
 * http://www.scala-lang.org/downloads/distrib/files/scala-2.8.0.final.tgz
   * scala-library.jar

place all of the above jars along with the alouer jar.

also, you will need at least the java jre 1.5.

installation is now complete.

### configuration

 * step 1: create a google account
 * step 2: create a google map and name it whatever you like
 * step 3: edit the google map and draw at least one closed polygon, this
     polygon is a filter for the rss feed items, only items in that
     polygon will be inserted on the google map. you may have more than
     one polygon.
 * step 4: copy the template.config in the _release directory and
     place the file somewhere on your file system.
 * step 5: modify your config file (do not put spaces before/after the equals)
   * google.maps.map is the title of the map you just created
   * google.maps.username is your google account username
   * google.maps.password is your google account password
   * google.maps.default.latitude is the latitude used when the entry has no address
   * google.maps.default.longitude is the longitude used when the entry has no address
   * google.maps.geocache is the full path to a text file to cache geo coordinates
   * rss.feed.kijiji is the url of a kijiji feed to periodically check
   * rss.feed.craigslist is the url of a craigslist feed to periodically check
   * rss.feed.cache is the full path to a cache file to remember the state of the rss feeds
   * log.file is the full path to the text file used for log information

### running the program

to run the program (where x is the version you downloaded):
  java -jar alouer-x.jar /path/to/your/config.file

you should arrive at a command prompt. the available commands are:
 * help, which displays all the commands
 * start [frequency], which starts processing at the given frequency in minutes
 * show, which displays all map entries and polygons
 * delete, which deletes all map entries and polygons
 * stats, which shows current counters
 * version, which shows the current version
 * quit, which stops and exits the program

as an example if you are at the prompt and type "start 10" and hit enter, the
program will check the rss feeds every 10 minutes and insert any updates on
the google map.
