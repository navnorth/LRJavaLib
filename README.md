Learning Registry Java Library
=========================================================

This is a java library that may be used to publish data to, and harvest 
from, a Learning Registry node.

License & Copyright
===================

Copyright 2011 Navigation North Learning Solutions LLC

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
 * install it to your Maven local repository:```mvn install:install-file -Dfile=bee-encode-0.2.jar -DgroupId=org.ardverk -DartifactId=bee-encode -Dversion=0.2 -Dpackaging=jar -DgeneratePom=true
```

##bouncy castle

 * [http://www.bouncycastle.org/java.html](http://www.bouncycastle.org/java.html)
 * version: 1.46
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
 * version: 20090211
 * license: free, as-is

## jackson

 * [http://jackson.codehaus.org/](http://jackson.codehaus.org/)
 * version: 1.9.4
 * license: [Apache 2.0](http://www.apache.org/licenses/) or [LGPL 2.1](http://www.gnu.org/licenses/lgpl.html)
 
--- 

Release History - https://github.com/navnorth/LRJavaLib/wiki/Release-Notes
========        
0.1.1 - *2012-02-20*
Simplified http/https usage, better examples

0.1.0 - *2011-12-08*
Initial Release