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

Dependencies
============

##bee-encode
get it from here: http://code.google.com/p/bee-encode

install it to your Maven local repository:
```
mvn install:install-file -Dfile=bee-encode-0.2.jar \
    -DgroupId=org.ardverk \ 
    -DartifactId=bee-encode \ 
    -Dversion=0.2 \
    -Dpackaging=jar \
    -DgeneratePom=true
```
 

Versions
========        
0.1.0 - Initial Release