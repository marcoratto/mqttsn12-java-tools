/*
 * MIT License
 * 
 * Copyright (c) 2025 Marco Ratto
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.marcoratto.mqttsn.tools.subscriber;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.marcoratto.mqttsn.MqttSnClient;
import io.github.marcoratto.mqttsn.MqttSnClientException;
import io.github.marcoratto.mqttsn.MqttSnListener;

public class Runme implements MqttSnListener {

private final static Logger logger = LoggerFactory.getLogger(Runme.class);
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println(HELP);
			System.exit(1);
		}
		int retCode = 1;
		try {
			Runme instance = new Runme();
			instance.runme(args);
			retCode = 0;
		} catch (Throwable t) {
			System.err.println(t.getMessage());
			retCode = 2;
		}
		System.exit(retCode);
	}	
	
	public int runme(String[] args) throws RunmeException {
		if (args == null) {
			throw new RunmeException("Why the param 'args' is null ?");
		}
		if (args.length == 0) {
			throw new RunmeException("Why the param 'args' is empty ?");
		}
		String host = "127.0.0.1";
		int port = 2442;
		String clientID = null;
		int qos = 0;
		byte timeout = 60;
		short keepalive = 30;
		short sleep = 0;
		
		boolean cleanSession = true;
		
		String willTopic = null;
		String willPayload = null;
		int willQos = 0;
		boolean willRetain = false;
		ArrayList<String> listOfTopic = new ArrayList<String>();
		ArrayList<Short> listOfTopicID = new ArrayList<Short>();
		
		for (int j = 0; j < args.length; j++) {
			logger.debug(args[j]);
			if (args[j].equalsIgnoreCase("-h") == true) {
				if (++j < args.length) {
					host = args[j];
				}	
			} else if (args[j].equalsIgnoreCase("-will-payload") == true) {
				if (++j < args.length) {
					willPayload = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-will-topic") == true) {
				if (++j < args.length) {
					willTopic = args[j];
				}	
			} else if (args[j].equalsIgnoreCase("-will-retain") == true) {
				if (++j < args.length) {
					willRetain = args[j].equals("true");
				}	
			} else if (args[j].equalsIgnoreCase("-will-qos") == true) {
				if (++j < args.length) {
					willQos = Integer.parseInt(args[j], 10);
				}	
			} else if (args[j].equalsIgnoreCase("-p") == true) {
				if (++j < args.length) {
					port = Integer.parseInt(args[j], 10);
				}					
			} else if (args[j].equalsIgnoreCase("-i") == true) {
				if (++j < args.length) {
					clientID = args[j];
				}
			} else if (args[j].equals("-T") == true) {
				if (++j < args.length) {
					listOfTopicID.add(Short.parseShort(args[j], 10));
				}
			} else if (args[j].equalsIgnoreCase("-c") == true) {
				cleanSession = false;
			} else if (args[j].equalsIgnoreCase("-1") == true) {
				singleMessage = true;
			} else if (args[j].equals("-t") == true) {
				if (++j < args.length) {
					listOfTopic.add(args[j]);
				}					
			} else if (args[j].equalsIgnoreCase("-q") == true) {
				if (++j < args.length) {
					qos = Integer.parseInt(args[j], 10);
				}
			} else if (args[j].equalsIgnoreCase("-timeout") == true) {
				if (++j < args.length) {
					timeout = Byte.parseByte(args[j], 10);
				}
			} else if (args[j].equalsIgnoreCase("-keepalive") == true) {
				if (++j < args.length) {
					keepalive = Short.parseShort(args[j], 10);
				}
			} else if (args[j].equalsIgnoreCase("-e") == true) {
				if (++j < args.length) {
					sleep = Short.parseShort(args[j], 10);
				}
			} else {
				throw new RunmeException("Parameter '" + args[j] + "' unknown!");
			}
		}
		if ((listOfTopic.size() == 0) && (listOfTopicID.size() == 0)) {
			throw new RunmeException("Parameter '-t' or '-T' is mandatory!");
		}			
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			keepRunning = false;
        }));
		
	    try {
	    	MqttSnClient mqttsnClient = new MqttSnClient();
	    	if (clientID != null) {
	    		mqttsnClient.setClientID(clientID);	
	    	}
        	
        	mqttsnClient.setCleanSession(cleanSession);
        	mqttsnClient.setTimeout(timeout);
        	mqttsnClient.setKeepAlive(keepalive);
        	
        	if ((willTopic != null) && (willPayload != null)) {
        		mqttsnClient.setWill(willTopic, willPayload, willQos, willRetain);
        	}
        	
        	mqttsnClient.open(host, port);
        	
        	if (qos >= 0) {
        		mqttsnClient.sendConnect();
        	}
        	for (Iterator<Short> iterator = listOfTopicID.iterator(); iterator.hasNext();) {
				Short topicID = (Short) iterator.next();
				mqttsnClient.sendSubscribe(topicID, qos, this);
			}
        	for (Iterator<String> iterator = listOfTopic.iterator(); iterator.hasNext();) {
				String topic = (String) iterator.next();
				mqttsnClient.sendSubscribe(topic, qos, this);
			}
        	while (keepRunning) {
        		mqttsnClient.polling();
        	}
        	
        	if (sleep > 0) {
        		mqttsnClient.sendDisconnect(sleep);
        	} else {
        		mqttsnClient.sendDisconnect();
        	} 
        	
        	mqttsnClient.close();
	    	  
        } catch (Throwable t) {
        	Thread.currentThread().interrupt();
			throw new RunmeException(t);
		} 
			
		return 0;
	}
	
	boolean singleMessage = false;
	private boolean keepRunning = true;
	
	private static String HELP= " * Usage:" +
			"\n" +
			 "\tio.github.marcoratto.mqttsn.tools.subscriber.Runme [parameters]" +
			 "\n\n" +
			 " * Parameters:" +
			 "\n" +
			 "\t-h <host>               MQTT-SN host to connect to. Defaults to '127.0.0.1'." +
			 "\n" +
			 "\t-p <port>               Network port to connect to. Defaults to 2442." +
			 "\n" +
			 "\t-i <clientid>           ID to use for this client. Defaults to 'mqtt-sn-java-' with random value 16 bit." +
			 "\n" +
			 "\t-c                      Disable 'clean session' (store subscription and pending messages when client disconnects)." +
			 "\n" +
			 "\t-t <topic>              MQTT-SN topic name to subscribe to. It may repeat multiple times." +
			 "\n" + 
			 "\t-T <topicid>            Pre-defined MQTT-SN topic ID to subscribe to. It may repeat multiple times." +
			 "\n" +
			 "\t-q <qos>                QoS level to subscribe with (0 or 1). Defaults to 0." +
			 "\n" +
			 "\t-k <keepalive>          Keep alive in seconds for this client. Defaults to 30." +
			 "\n" +
			 "\t-timeout                Timeout. Defaults to 60." +
			 "\n" + 
			 "\t-e <sleep>              Sleep duration in seconds when disconnecting. Defaults to 0." +
			 "\n" +
			 "\t-1                      Exit after receiving a single message." +
			 "\n" +
			 "\t-will-payload <message> Payload for the client Will, which is sent by the broker in case of" +
			 "\n" +
			 "\t                        unexpected disconnection. If not given and will-topic is set, a zero" +
			 "\n" +
			 "\t                        length message will be sent." +
			 "\n" +
			 "\t-will-qos <qos>         QoS level for the client Will (0 or 1). Defaults to 0." + 
			 "\n" +
			 "\t-will-retain            If given, make the client Will retained. Defaults to false." +
			 "\n" +
			 "\t-will-topic <topic>     The topic on which to publish the client Will." +
			 "\n";

	@Override
	public void messageArrived(short topicID, String topicName, byte[] message) throws MqttSnClientException {
		System.out.println("TopicID:" + topicID);
		System.out.println("topicName:" + topicName);
		System.out.println("Message:\n" + new String(message));
		if (singleMessage) {
			this.keepRunning = false;
		}
	}

}
