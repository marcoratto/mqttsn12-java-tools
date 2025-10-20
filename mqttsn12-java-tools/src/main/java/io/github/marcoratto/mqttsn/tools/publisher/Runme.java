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
package io.github.marcoratto.mqttsn.tools.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.marcoratto.mqttsn.MqttSnClient;

public class Runme {

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
		String topic = null;
		short topicID = -1;
		String message = null;
		boolean retained = false;
		byte timeout = 60;
		short keepalive = 30;
		short sleep = 0;
		File file = null;
		
		for (int j = 0; j < args.length; j++) {
			logger.debug(args[j]);
			if (args[j].equalsIgnoreCase("-h") == true) {
				if (++j < args.length) {
					host = args[j];
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
					topicID = Short.parseShort(args[j], 10);
				}
			} else if (args[j].equalsIgnoreCase("-r") == true) {
				retained = true;
			} else if (args[j].equalsIgnoreCase("-n") == true) {
					message = "";
			} else if (args[j].equalsIgnoreCase("-m") == true) {
				if (++j < args.length) {
					message = args[j];
				}	
			} else if (args[j].equalsIgnoreCase("-f") == true) {
				if (++j < args.length) {
					file = new File(args[j]);
				}
			} else if (args[j].equals("-t") == true) {
				if (++j < args.length) {
					topic = args[j];
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
		if (file != null) {
			if (file.exists()) {
				message = this.readFile(file);
			} else {
				throw new RunmeException("File '" + file + "' not found!");
			}
		}
		if ((topic == null) && (topicID == -1)) {
			throw new RunmeException("Parameter '-t' or '-T' is mandatory!");
		}			
		if (message == null) {
			throw new RunmeException("Parameter '-m' or '-f' is mandatory!");
		}
	    try {
	    	MqttSnClient mqttsnClient = new MqttSnClient();
	    	if (clientID != null) {
	    		mqttsnClient.setClientID(clientID);	
	    	}
        	
        	mqttsnClient.setCleanSession(false);
        	mqttsnClient.setTimeout(timeout);
        	mqttsnClient.setKeepAlive(keepalive);
        	
        	mqttsnClient.open(host, port);
        	
        	if (qos >= 0) {
        		mqttsnClient.sendConnect();
        	}
        	
        	if (topic != null) {
        		if (topic.length() == 2) { 
                	mqttsnClient.sendPublish(topic.getBytes(), 
        					message.getBytes(), 
        					qos, 
        					retained);
        		} else {
        			mqttsnClient.sendPublish(topic, 
        					message.getBytes(), 
        					qos, 
        					retained);
        		}
        		
        	} else if (topicID != -1) {
        		mqttsnClient.sendPublishPreDefined(topicID, 
        					message.getBytes(), 
        					qos, 
        					retained);
        	}
        	if (qos >= 0) {
            	if (sleep > 0) {
            		mqttsnClient.sendDisconnect(sleep);
            	} else {
            		mqttsnClient.sendDisconnect();
            	}        		
        	}
        	
        	mqttsnClient.close();
	    	
		} catch (Throwable t) {
			throw new RunmeException(t);
		} 
			
		return 0;
	}
	
	private String readFile(File aFile) {   
		if (aFile == null) {
			throw new NullPointerException("Why parameter 'aFile' is null ?");
		}
		if (aFile.exists() == false) {
	        logger.error("File '" + aFile.getAbsolutePath() + "' not found!");
			return null;
		}		
		int fileSize = (int) aFile.length();
		if (fileSize == 0) {
	        logger.error("File '" + aFile.getAbsolutePath() + "' is empty!");
			return null;
		}				
        logger.trace("File '" + aFile.getAbsolutePath() + "' has " + fileSize + " bytes.");
		InputStream fis = null;
		int bytesRead = 0;
		byte[] buffer = new byte[(int) fileSize];			 
	    try {
			fis = new FileInputStream(aFile);
			bytesRead = fis.read(buffer);	
		} catch(IOException e) {
	        logger.error(e.getMessage(), e);
	    } finally {
	    	if (fis != null) {
	    		try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
	    	}
	    }
        logger.trace("Buffer size is " + buffer.length + " bytes.");
		if (bytesRead != fileSize) {
	        logger.error("File and Bytes readed has a different size!");
		}				
		if (buffer.length != fileSize) {
	        logger.error("File and Buffer length with different size!");
		}				
	    return new String(buffer);
	}
	
	private static String HELP= " * Usage:" +
			"\n" +
			 "\tio.github.marcoratto.mqttsn.tools.publisher.Runme [parameters]" +
			 "\n\n" +
			 " * Parameters:" +
			 "\n" +
			 "\t-h <host>      MQTT-SN host to connect to. Defaults to '127.0.0.1'." +
			 "\n" +
			 "\t-p <port>      Network port to connect to. Defaults to 2442." +
			 "\n" +
			 "\t-i <clientid>  ID to use for this client. Defaults to 'mqtt-sn-java-' with random value 16 bit." +
			 "\n" +
			 "\t-t <topic>     MQTT-SN topic name to publish to. Valid forn Normal or Short Topic." +
			 "\n" + 
			 "\t-T <topicid>   Pre-defined MQTT-SN topic ID to publish to." +
			 "\n" +
			 "\t-m <message>   Message payload to send." +
			 "\n" +
			 "\t-q <qos>       Quality of Service value (0, 1 or -1). Defaults to 0." +
			 "\n" +
			 "\t-r             Message should be retained." +
			 "\n" +
			 "\t-n             Send a null (zero length) message." +
			 "\n" + 
			 "\t-f <file>      A file to send as the message payload." +
			 "\n" +
			 "\t-k <keepalive> Keep alive in seconds for this client. Defaults to 30." +
			 "\n" +
			 "\t-timeout       Timeout. Defaults to 60." +
			 "\n" + 
			 "\t-e <sleep>     Sleep duration in seconds when disconnecting. Defaults to 0." +
			 "\n";

}
