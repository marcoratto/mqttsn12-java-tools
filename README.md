# mqttsn12-java-tools
Command line tools written in Java for the MQTT-SN (MQTT for Sensor Networks) protocol.

## mqtt-sn-java-sub.sh

```
./mqtt-sn-java-sub.sh 
 * Usage:
	io.github.marcoratto.mqttsn.tools.subscriber.Runme [parameters]

 * Parameters:
	-h <host>               MQTT-SN host to connect to. Defaults to '127.0.0.1'.
	-p <port>               Network port to connect to. Defaults to 2442.
	-i <clientid>           ID to use for this client. Defaults to 'mqtt-sn-java-' with random value 16 bit.
	-c                      Disable 'clean session' (store subscription and pending messages when client disconnects).
	-t <topic>              MQTT-SN topic name to subscribe to. It may repeat multiple times.
	-T <topicid>            Pre-defined MQTT-SN topic ID to subscribe to. It may repeat multiple times.
	-q <qos>                QoS level to subscribe with (0 or 1). Defaults to 0.
	-k <keepalive>          Keep alive in seconds for this client. Defaults to 30.
	-timeout                Timeout. Defaults to 60.
	-e <sleep>              Sleep duration in seconds when disconnecting. Defaults to 0.
	-1                      Exit after receiving a single message.
	-will-payload <message> Payload for the client Will, which is sent by the broker in case of
	                        unexpected disconnection. If not given and will-topic is set, a zero
	                        length message will be sent.
	-will-qos <qos>         QoS level for the client Will (0 or 1). Defaults to 0.
	-will-retain            If given, make the client Will retained. Defaults to false.
	-will-topic <topic>     The topic on which to publish the client Will.

ERROR: java return error code 1.
```

## mqtt-sn-java-pub.sh
```
./mqtt-sn-java-pub.sh 
 * Usage:
	io.github.marcoratto.mqttsn.tools.publisher.Runme [parameters]

 * Parameters:
	-h <host>      MQTT-SN host to connect to. Defaults to '127.0.0.1'.
	-p <port>      Network port to connect to. Defaults to 2442.
	-i <clientid>  ID to use for this client. Defaults to 'mqtt-sn-java-' with random value 16 bit.
	-t <topic>     MQTT-SN topic name to publish to. Valid forn Normal or Short Topic.
	-T <topicid>   Pre-defined MQTT-SN topic ID to publish to.
	-m <message>   Message payload to send.
	-q <qos>       Quality of Service value (0, 1 or -1). Defaults to 0.
	-r             Message should be retained.
	-n             Send a null (zero length) message.
	-f <file>      A file to send as the message payload.
	-k <keepalive> Keep alive in seconds for this client. Defaults to 30.
	-timeout       Timeout. Defaults to 60.
	-e <sleep>     Sleep duration in seconds when disconnecting. Defaults to 0.

ERROR: java return error code 1.
```
