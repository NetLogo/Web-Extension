scalaVersion := "2.9.3"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "us-ascii")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.0.4" from
    "http://ccl.northwestern.edu/netlogo/5.0.4/NetLogo.jar",
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "org.apache.httpcomponents" % "httpmime" % "4.2",
  "org.picocontainer" % "picocontainer" % "2.13.6",
  "log4j" % "log4j" % "1.2.17"
)

name := "web"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.web.WebExtension"

