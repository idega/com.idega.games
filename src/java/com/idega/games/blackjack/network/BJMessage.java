/*
 * $Id: BJMessage.java,v 1.1 2007/01/29 09:08:46 eiki Exp $
 * Created on May 30, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.games.blackjack.network;

import com.captiveimagination.jgn.message.Message;

/**
 * This is a very basic message. Note that
 * the only thing explicitly that needed to
 * be defined was the abstract getType() method
 * which is used to determine the message type
 * when received on the remote server. The
 * message type MUST be unique from other
 * messages.
 * 
 * Notice that this is a simple bean that
 * we just add a getText setText method to
 * and it gets passed to the recipient.
 * You can any primitives, primitive wrapper,
 * or String. No other types are currently
 * supported as this is meant to be a very
 * fast transfer protocol.
 * 
 * @author Matthew D. Hicks
 */
public class BJMessage extends Message {
    private String text;
    private int[] numbers;
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }
    
    public int[] getNumbers() {
        return numbers;
    }
}