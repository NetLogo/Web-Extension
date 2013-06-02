ifeq ($(origin NETLOGO), undefined)
  NETLOGO=../..
endif

ifeq ($(origin SCALA_HOME), undefined)
  SCALA_HOME=../..
endif

ifneq (,$(findstring CYGWIN,$(shell uname -s)))
  COLON=\;
  SCALA_HOME := `cygpath -up "$(SCALA_HOME)"`
else
  COLON=:
endif

SRCS=$(shell find src -type f -name '*.scala')
EXT_NAME=web

HTTP_CLIENT_NAME=httpclient-4.2
HTTP_CLIENT_JAR=$(HTTP_CLIENT_NAME).jar
HTTP_CLIENT_PACK=$(HTTP_CLIENT_JAR).pack.gz

HTTP_CORE_NAME=httpcore-4.2
HTTP_CORE_JAR=$(HTTP_CORE_NAME).jar
HTTP_CORE_PACK=$(HTTP_CORE_JAR).pack.gz

HTTP_MIME_NAME=httpmime-4.2
HTTP_MIME_JAR=$(HTTP_MIME_NAME).jar
HTTP_MIME_PACK=$(HTTP_MIME_JAR).pack.gz

COMMONS_CODEC_NAME=commons-codec-1.7
COMMONS_CODEC_JAR=$(COMMONS_CODEC_NAME).jar
COMMONS_CODEC_PACK=$(COMMONS_CODEC_JAR).pack.gz

JAR_REPO=http://ccl.northwestern.edu/devel/

$(EXT_NAME).jar $(EXT_NAME).jar.pack.gz: $(SRCS) $(HTTP_CLIENT_JAR) $(HTTP_CORE_JAR) $(HTTP_MIME_JAR) $(COMMONS_CODEC_JAR) manifest.txt Makefile
	mkdir -p classes
	$(SCALA_HOME)/bin/scalac -deprecation -unchecked -encoding us-ascii -classpath $(HTTP_CLIENT_JAR)$(COLON)$(HTTP_CORE_JAR)$(COLON)$(HTTP_MIME_JAR)$(COLON)$(COMMONS_CODEC_JAR)$(COLON)$(NETLOGO)/NetLogo.jar -d classes $(SRCS)
	jar cmf manifest.txt $(EXT_NAME).jar -C classes .
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip $(EXT_NAME).jar.pack.gz $(EXT_NAME).jar

$(HTTP_CLIENT_JAR) $(HTTP_CLIENT_PACK):
	curl -f -s -S $(JAR_REPO)$(HTTP_CLIENT_JAR) -o $(HTTP_CLIENT_JAR)
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip $(HTTP_CLIENT_PACK) $(HTTP_CLIENT_JAR)

$(HTTP_CORE_JAR) $(HTTP_CORE_PACK):
	curl -f -s -S $(JAR_REPO)$(HTTP_CORE_JAR) -o $(HTTP_CORE_JAR)
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip $(HTTP_CORE_PACK) $(HTTP_CORE_JAR)

$(HTTP_MIME_JAR) $(HTTP_MIME_PACK):
	curl -f -s -S $(JAR_REPO)$(HTTP_MIME_JAR) -o $(HTTP_MIME_JAR)
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip $(HTTP_MIME_PACK) $(HTTP_MIME_JAR)

$(COMMONS_CODEC_JAR) $(COMMONS_CODEC_PACK):
	curl -f -s -S $(JAR_REPO)$(COMMONS_CODEC_JAR) -o $(COMMONS_CODEC_JAR)
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip $(COMMONS_CODEC_PACK) $(COMMONS_CODEC_JAR)

$(EXT_NAME).zip: $(EXT_NAME).jar
	rm -rf $(EXT_NAME)
	mkdir $(EXT_NAME)
	cp -rp $(EXT_NAME).jar $(EXT_NAME).jar.pack.gz README.md Makefile src manifest.txt $(EXT_NAME)
	zip -rv $(EXT_NAME).zip $(EXT_NAME)
	rm -rf $(EXT_NAME)