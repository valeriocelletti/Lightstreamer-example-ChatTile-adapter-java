# Lightstreamer - Chat-Tile Demo - Java Adapter #

<!-- START DESCRIPTION lightstreamer-example-chattile-adapter-java -->

The **Chat-Tile Demo** implements a simple chat/collaborative application fed in real time via a Lightstreamer server.

## Details

As example of a client using this adapter, you may refer to the [Chat-Tile Demo - HTML (JQuery, Masonry) Client](https://github.com/Weswit/Lightstreamer-example-ChatTile-client-javascript) and view the corresponding [Live Demo](http://demos.lightstreamer.com/ChatTileDemo/).

This project includes the implementation of the [SmartDataProvider](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface for the *Lightstreamer Chat-Tile Demo*. Please refer to [General Concepts](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.

### Java Data Adapter and MetaData Adapter

The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.

The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.
<!-- END DESCRIPTION lightstreamer-example-chattile-adapter-java -->

## Install
* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy.zip` file of the [latest release](https://github.com/Weswit/Lightstreamer-example-ChatTile-adapter-java/releases), unzip it and copy the just unzipped `ChatTile` folder into the `adapters` folder of your Lightstreamer Server installation.
* Get the `ua-parser-1.2.2.jar` file from [ua_parser Java Library](https://github.com/tobie/ua-parser/tree/master/java) and copy it into the `adapters/ChatTile/lib` folder.
* Get the `snakeyaml-1.11.jar` files from [SnakeYAML](https://code.google.com/p/snakeyaml/) and copy it into the `adapters/ChatTile/lib` folder.
* [Optional] Supply a specific "LS_ChatTileDemo_Logger" and "LS_demos_Logger" category in logback configuration `Lightstreamer/conf/lightstreamer_log_conf.xml`.
* Launch Lightstreamer Server.
* Test the Adapter, launching the client listed in [Clients Using This Adapter](https://github.com/Weswit/Lightstreamer-example-ChatTile-adapter-java#clients-using-this-adapter).

## Build
To build your own version of `LS_ChatTile_Demo_Adapters.jar`, instead of using the one provided in the `deploy.zip` file, follow these steps.

* Download this project.
* Get the `ls-adapter-interface.jar`, `ls-generic-adapters.jar`, and `log4j-1.2.15.jar` files from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download), and copy them into the `lib` directory.
* Get the `ua-parser-1.2.2.jar` file from [ua_parser Java Library](https://github.com/tobie/ua-parser/tree/master/java), and copy it into the `lib` directory.
* Get the `snakeyaml-1.11.jar` files from [SnakeYAML](https://code.google.com/p/snakeyaml/), and copy it into the `lib` directory.
* Build the jar `LS_ChatTile_Demo_Adapters.jar` with commands like these:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.15.jar;lib/ls-adapter-interface.jar;lib/ls-generic-adapters.jar;lib/jbox2d-library-2.2.1.1.jar;lib/ua-parser-1.2.2.jar;lib/snakeyaml-1.11.jar -sourcepath src/ -d tmp_classes src/com/lightstreamer/adapters/ChatTileDemo/ChatTileAdapter.java
 
 >jar cvf LS_ChatTile_Demo_Adapters.jar -C tmp_classes com
```
* Stop Lightstreamer Server; copy the just compiled LS_ChatTile_Demo_Adapters.jar in the adapters/ChatTile/lib folder of your Lightstreamer Server installation; restart Lightstreamer Server.

## See Also 

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->
<!-- END RELATED_ENTRIES -->

* [Lightstreamer - Chat-Tile Demo - JQuery Client](https://github.com/Weswit/Lightstreamer-example-ChatTile-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [Lightstreamer - Chat Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java)
* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Basic Messenger Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-Messenger-client-javascript)

## Lightstreamer Compatibility Notes

- Compatible with Lightstreamer SDK for Java Adapters since 5.1
