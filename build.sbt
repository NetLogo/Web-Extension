scalaVersion := "2.10.0"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii", "-language:_")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogoEngine" % "5.1.x-20885db" from
    "http://ccl.northwestern.edu/devel/NetLogoEngine-20885db.jar",
  "org.nlogo" % "NetLogoGUI" % "5.1.x-20885db" from
    "http://ccl.northwestern.edu/devel/NetLogoGUI-20885db.jar",
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "org.apache.httpcomponents" % "httpmime" % "4.2",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "org.picocontainer" % "picocontainer" % "2.13.6",
  "log4j" % "log4j" % "1.2.17"
)

name := "web"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.web.WebExtension"

