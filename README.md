# NetLogo exporter extension

This extension allows you to hook into NetLogo functionality for various forms of output and export data from NetLogo via HTTP POST.

## Using

The just call the the `exporter:export` primitive with a string (representing your destination URL) and you're all set!

## Building

Use the NETLOGO environment variable to tell the Makefile which NetLogoLite.jar to compile against.  For example:

    NETLOGO=/Applications/NetLogo\\\ 5.0 make

If compilation succeeds, `exporter.jar` will be created.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Exporter extension is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.
