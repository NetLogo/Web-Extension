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

artifactName := { (_, _, _) => "web.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "web"),
    ("Class-Manager", "org.nlogo.extensions.web.WebExtension"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile <<= (packageBin in Compile, baseDirectory, streams) map {
  (jar, base, s) =>
    IO.copyFile(jar, base / "web.jar")
    Process("pack200 --modification-time=latest --effort=9 --strip-debug " +
            "--no-keep-file-order --unknown-attribute=strip " +
            "web.jar.pack.gz web.jar").!!
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o web.zip --prefix=web/ HEAD").!!
      IO.createDirectory(base / "web")
      IO.copyFile(base / "web.jar", base / "web" / "web.jar")
      IO.copyFile(base / "web.jar.pack.gz", base / "web" / "web.jar.pack.gz")
      Process("zip web.zip web/web.jar web/web.jar.pack.gz").!!
      IO.delete(base / "web")
    }
    else {
      s.log.warn("working tree not clean; no zip archive made")
      IO.delete(base / "web.zip")
    }
    jar
  }

cleanFiles <++= baseDirectory { base =>
  Seq(base / "web.jar",
      base / "web.jar.pack.gz",
      base / "web.zip") }

