Learning Registry Java Library
=========================================================

This is a java library that may be used to publish data to, and harvest
from, a Learning Registry node.

License & Copyright
===================

Copyright 2014 Navigation North

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and

This project has been funded at least or in part with Federal funds from
the U.S. Department of Education under Contract Number ED-04-CO-0040/0010.
The content of this publication does not necessarily reflect the views or
policies of the U.S. Department of Education nor does mention of trade
names, commercial products, or organizations imply endorsement by the U.S.
Government.

---
Dependencies
============
##bee-encode

 * [http://code.google.com/p/bee-encode](http://code.google.com/p/bee-encode)
 * version: 0.2
 * license: [Apache 2.0](http://www.apache.org/licenses/)
 * install it to your Maven local repository:
 ```mvn install:install-file -Dfile=bee-encode-0.2.jar -DgroupId=org.ardverk -DartifactId=bee-encode -Dversion=0.2 -Dpackaging=jar -DgeneratePom=true
```

##bouncy castle

 * [http://www.bouncycastle.org/java.html](http://www.bouncycastle.org/java.html)
 * version: 1.47
 * license: [custom, based on MIT X11](http://www.bouncycastle.org/licence.html)

## commons-codec

 * [http://commons.apache.org/codec/](http://commons.apache.org/codec/)
 * version: 1.3
 * license: [Apache 2.0](http://www.apache.org/licenses/)

## commons-io

 * [http://commons.apache.org/io/](http://commons.apache.org/io/)
 * version: 1.32
 * license: [Apache 2.0](http://www.apache.org/licenses/)

## httpcomponents

 * [http://hc.apache.org/httpcomponents-core-ga](http://hc.apache.org/httpcomponents-core-ga)
 * version: 4.1
 * license: [Apache 2.0](http://www.apache.org/licenses/)

## json

 * [http://www.json.org/java/](http://www.json.org/java/)
 * version: 20131018
 * license: free, as-is

## jackson

 * [http://jackson.codehaus.org/](http://jackson.codehaus.org/)
 * version: 1.9.4
 * license: [Apache 2.0](http://www.apache.org/licenses/) or [LGPL 2.1](http://www.gnu.org/licenses/lgpl.html)

---

Release History
========
full details: https://github.com/navnorth/LRJavaLib/wiki/Release-Notes

0.1.5 - *2014-04-10*
Added **replaces** property, and pre-bencode normalization (thanks to [Benetech](https://github.com/navnorth/LRJavaLib/commit/0a5e92a4a8ddff4ba2f1dce454a80fbf00212c7d)). Now converts resource_data to string before publish, matching 0.51.0 spec change. Increased doc_version to 0.51.0 for recent signature changes.

0.1.4 - *2014-02-28*
Added **replaces** property, and pre-bencode normalization (thanks to [Benetech](https://github.com/navnorth/LRJavaLib/commit/0a5e92a4a8ddff4ba2f1dce454a80fbf00212c7d)).

0.1.2 - *2012-03-28*
Fix bug in signature creation - was failing to put a linebreak before BEGIN PGP SIGNATURE

0.1.1 - *2012-02-20*
Simplified http/https usage, better examples

0.1.0 - *2011-12-08*
Initial Release
