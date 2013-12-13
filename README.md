# Lightstreamer - Chat-Tile Demo - Java Adapter #

This project includes the resources needed to develop the Metadata and Data Adapters for the [Lightstreamer - Chat-Tile Demo - JQuery Client](https://github.com/Weswit/Lightstreamer-example-ChatTile-client-javascript) that is pluggable into Lightstreamer Server. Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.
The <b>Chat-Tile Demo</b> implements a simple chat/collaborative application fed in real time via a Lightstreamer server.<br>
<br>
The project is comprised of source code and a deployment example.

## Java Data Adapter and MetaData Adapter ##
A Java Adapter implementing both the [SmartDataProvider](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface.

The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.<br>
The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.

# Build #

If you want to skip the build process of this Adapter please note that in the [deploy release](https://github.com/Weswit/Lightstreamer-example-ChatTile-adapter-java/releases) of this project you can find the "deploy.zip" file that contains a ready-made deployment resource for the Lightstreamer server. <br>
Otherwise follow these steps:

* Get the `ls-adapter-interface.jar`, `ls-generic-adapters.jar`, and `log4j-1.2.15.jar` files from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download).
* Get the `ua-parser-1.2.2.jar` file from [ua_parser Java Library](https://github.com/tobie/ua-parser/tree/master/java);
* Get the `snakeyaml-1.11.jar` files from [SnakeYAML](https://code.google.com/p/snakeyaml/)
* Build the jar `LS_ChatTile_Demo_Adapters.jar` with commands like these:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.15.jar;lib/ls-adapter-interface.jar;lib/ls-generic-adapters.jar;lib/jbox2d-library-2.2.1.1.jar;lib/ua-parser-1.2.2.jar;lib/snakeyaml-1.11.jar -sourcepath src/ -d tmp_classes src/com/lightstreamer/adapters/ChatTileDemo/ChatTileAdapter.java
 
 >jar cvf LS_ChatTile_Demo_Adapters.jar -C tmp_classes com
```

# Deploy #

You have to create a specific folder to deploy the Chat-Tile Demo Adapters, otherwise get the ready-made `ChatTile` deploy folder from `deploy.zip` of the [latest release](https://github.com/Weswit/Lightstreamer-example-ChatTile-adapter-java/releases) of this project.<br>
If you choose to create you own folder, follow the next steps, otherwise skip them. 

1. Create a new folder, let's call it `ChatTile`, and a `lib` folder inside it.
2. Copy the jar file of the adapter `LS_ChatTile_Demo_Adapters.jar`, compiled in the previous section, in the newly created `lib` folder.
3. Copy the files `ua-parser-1.2.2.jar` and `snakeyaml-1.11` from `Lightstreamer/lib` in the newly created `lib` folder.
4. Create an `adapters.xml` file inside the `ChatTile` folder and use the following content (this is an example configuration, you can modify it to your liking):
```xml      
<?xml version="1.0"?>
<adapters_conf id="CHATTILE">
    <metadata_provider>
        <adapter_class>com.lightstreamer.adapters.ChatTileDemo.ChatTileMetaAdapter</adapter_class>

        <!--
          TCP port on which Sun/Oracle's JMXMP connector will be
          listening.
        -->
        <param name="jmxPort">9999</param>
        
    </metadata_provider>
    
    <data_provider>
        <adapter_class>com.lightstreamer.adapters.ChatTileDemo.ChatTileAdapter</adapter_class>
        
    </data_provider>
</adapters_conf>
```
<br> 

Now your `ChatTile` folder is ready to be deployed in the Lightstreamer server, please follow these steps:<br>

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the `ChatTile` directory and all of its files to the `adapters` subdirectory in your Lightstreamer Server installation home directory.
4. [Optional] Supply a specific "LS_ChatTileDemo_Logger" and "LS_demos_Logger" category in logback configuration `Lightstreamer/conf/lightstreamer_log_conf.xml`.
5. Lightstreamer Server is now ready to be launched.

Please test your Adapter with the [client](https://github.com/Weswit/Lightstreamer-example-ChatTile-adapter-java#clients-using-this-adapter) below.

# See Also #

## Clients using this Adapter ##

* [Lightstreamer - Chat-Tile Demo - JQuery Client](https://github.com/Weswit/Lightstreamer-example-ChatTile-client-javascript)

## Related projects ##

* [Lightstreamer - Chat Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java)
* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Basic Messenger Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-Messenger-client-javascript)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1

