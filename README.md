# NetLogo Web Extension

This extension allows you to directly import or export many different types of data while within NetLogo, including images and `export-world` states.  Please see the [documentation](https://github.com/NetLogo/Web-Extension/wiki/Primitives) for an exhaustive listing of the types of data that can be leveraged.

Despite the many different things it _can_ do, the extension does **not** currently support any of the following:
* OAuth
* SSL
* HTTPS
* HTTP header customization
* Web Sockets

__Requires NetLogo 5.0.4 or greater.__

## Building

Run `./sbt package` to build the extension.

If the build succeeds, `web.jar` will be created.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Web extension is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.
