package com.alex.asyncexamples.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command Line Interface for AsyncCounterClient
 * @author Alex
 */
public class CounterClientCLI {
	public static void main(String args[]) throws IOException {
		boolean running = true;
		// begin by showing help.	
		showHelp();
		//1. Client started, counter value is.
		//   would you like to (i)ncrease, (d)ecrease or (g)et counter value?
		AsyncEchoClient client = null;
		while (running) {
	        // Note uppercase and trim used to minimise typos.
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	        String input = bufferedReader.readLine().trim();   
	        String serverHostname = null;
	        String returnMessage = null;
	        String command = null;
	        
	        if (input.indexOf(" ") > 0) {
	        	String [] tokens = input.split(" ");
	        	command = tokens[0];
	            if (command.equalsIgnoreCase("CONNECT")) {
		        	serverHostname = tokens[1];
		        	String serverPortNumber = tokens[2];
		        	try {
		        		int port = Integer.parseInt(serverPortNumber.trim());
						client = new AsyncEchoClient("cli_client", serverHostname, 
													port);
						returnMessage = "connected to" + serverHostname + ":" + port;
					} catch (Exception e) {
						System.out.println("************** Cannot connect to server on host=" + 
									serverHostname + ",port=" + serverPortNumber + 
									", check you have no typos and server is running");
					}
	            } else if (command.equalsIgnoreCase("SEND")) {
	            	String message = tokens[1];
	            	returnMessage = client.sendMessage(message);
	            } 
	        	// have successful client.
	        } else {
	        	// cover other commands
	        	String inputUpperCase = input.toUpperCase();
	        	switch(inputUpperCase) {
	        		case "HELP":
	        			showHelp();
	        			break;
	        		case "EXIT":
	        			System.out.println("Bye bye...");
		            	System.exit(0);
	        			break;
	        		default: 
	        			returnMessage = "Did not recognise input: " + input + ", try again.";
	        			break;
	        	}
	        }
	        if (returnMessage != null && returnMessage.length() > 0) {
	        	System.out.println(returnMessage);
	        }
		}
	}
	
	private static void showHelp() {
		System.out.println("Enter one of the following...");
		System.out.println("help -- to display this message");
		System.out.println("connect %hostname% %port% -- connect to server at hostname port e.g. connect localhost 9999  ");
		System.out.println("send %message% -- sends message to server");
		System.out.println("exit -- to exit");
	}

}
