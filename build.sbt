enablePlugins(org.nlogo.build.NetLogoExtension)

name       := "web"
version    := "2.1.0"
isSnapshot := true

scalaVersion           := "3.7.0"
Compile / scalaSource  := baseDirectory.value / "src" / "main"
Test / scalaSource     := baseDirectory.value / "src" / "test"
scalacOptions          ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii", "-release", "17")

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "org.apache.httpcomponents" % "httpmime" % "4.2",
  "org.picocontainer" % "picocontainer" % "2.13.6",
  "log4j" % "log4j" % "1.2.17"
)

netLogoExtName      := "web"
netLogoClassManager := "org.nlogo.extensions.web.WebExtension"
netLogoVersion      := "7.0.0-beta1"
