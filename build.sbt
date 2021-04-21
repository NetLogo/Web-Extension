enablePlugins(org.nlogo.build.NetLogoExtension)

name := "web"

resolvers      += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/"
netLogoVersion := "6.2.0-d27b502"

netLogoClassManager := "org.nlogo.extensions.web.WebExtension"

scalaVersion := "2.12.8"

version := "2.1.0"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature",
                      "-encoding", "us-ascii", "-language:_")

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "org.apache.httpcomponents" % "httpmime" % "4.2",
  "org.picocontainer" % "picocontainer" % "2.13.6",
  "log4j" % "log4j" % "1.2.17"
)
