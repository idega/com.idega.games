/*
 * $Id: BJServer.java,v 1.1 2007/01/29 09:08:46 eiki Exp $
 * Created on May 30, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.games.blackjack.network;

import com.captiveimagination.jgn.IP;
import com.captiveimagination.jgn.JGN;
import com.captiveimagination.jgn.MessageServer;
import com.captiveimagination.jgn.UDPMessageServer;
import com.captiveimagination.jgn.event.MessageListener;
import com.captiveimagination.jgn.message.Message;


public class BJServer {

   public static void main(String[] args) throws Exception {
       // All messages must be registered before they
       // can be sent or received
       JGN.registerMessage(BJMessage.class, (short)1);
       
       // The MessageServer listens for incoming messages
       final MessageServer server = new UDPMessageServer(null, 9000);
       
       // The MessageServer that sends the message
       final MessageServer server2 = new UDPMessageServer(IP.getLocalHost(), 9001);
       server2.startUpdateThread();
       
       // Add a listener to let us know the message was received
       server.addMessageListener(new MessageListener() {
          
           public void messageReceived(Message message) {
        	   	BJMessage bjMessage = (BJMessage) message;
               System.out.println("Received Message: " + bjMessage.getText());
               if (bjMessage.getNumbers() != null) {
                   int[] numbers = bjMessage.getNumbers();
                   for (int i = 0; i < numbers.length; i++) {
                       System.out.print(numbers[i] + " ");
                   }
               } else {
                   System.out.println("Numbers is null!");
               }
               // Lets shutdown the server now
               server.shutdown();
               server2.shutdown();
           }

           public int getListenerMode() {
               return MessageListener.CLOSEST;
           }
       });
       // We start the update thread - this is an alternative to calling update() in your game thread
       server.startUpdateThread();
       
       // Send a message to the server for processing
       BJMessage message = new BJMessage();
       message.setText("Hello Server!");
       message.setNumbers(new int[] {1, 2, 3, 4, 5});
       server2.sendMessage(message, null, 9000);
   }
}