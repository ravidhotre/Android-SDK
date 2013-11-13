Android-SDK
===========

This is android client library for [Cloudengine][cloudengine]. It provides wrapper APIs around
the REST interfaces provided by the CloudEngine server. 

Usage
=======
Clone the project in your (Eclipse) workspace.

	git clone git@github.com:cloudengine/Android-SDK.git
	
Add the project as an Android library dependency to your application's project properties 
from Eclipse. If you're running CloudEngine on your own server, you'll need to change
the server names in the CloudEngineEndPoints.java file.

Technical Overview
===================

CloudEngine uses [socketio][socketio] for implementing push notifications. The library currently uses 
[socket.io-java-client][socketio-client] for implementing socketio client for android. The socketio 
library is included in the libs folder for convenience and is automatically linked with the application 
when the sdk library is added to your project.


Documentation & Support
========================

Complete documentation is available at - ?

For discussions, questions and support use the [CloudEngine discussion group][group]

or [Github issue tracking][issue-tracker]

You may also want to [follow the authors on twitter] [twitter]. 



License
========
See the LICENSE file for more info.


[twitter]: https://twitter.com/thecloudengine
[issue-tracker]: https://github.com/cloudengine/Android-SDK/issues
[group]: https://groups.google.com/forum/#!forum/cloudengine-dev
[cloudengine]: https://github.com/cloudengine/CloudEngine  
[socketio-client]: https://github.com/Gottox/socket.io-java-client
[socketio]: http://socket.io

