/*
 * $Id: RaceCarControl.java,v 1.1 2006/05/31 19:19:33 eiki Exp $
 * Created on May 31, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.games.test.racer;

/**
 * A controller for a car Sprite, could also be used for other moving vehicles
 * 
 *  Last modified: $Date: 2006/05/31 19:19:33 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class RaceCarControl {

RaceCar car;
int state = 0;

double turn_speed = 0;
int turn_dur = 0;
double speed = 0;
double max_speed = 0;
double accelaration = 0;

boolean key = false;

public RaceCarControl (RaceCar p) {
this(p, p.turn_speed, p.turn_duration, p.max_speed, p.accelaration);
}

public RaceCarControl (RaceCar p, double speed, int dur, double max, double a) {
car = p;
turn_dur = dur;
turn_speed = speed;
max_speed = max;
accelaration = a;
state = 0;
}

public void update() {
key = false;
if(speed > 0) {
speed -= 0.03; // Weerstand in Dutch.
car.setLocation (car.getX () + Math.sin(car.angle) * speed, car.getY () - Math.cos(car.angle) * speed);
}

// Turn the car!
if(state != 0) {
car.setAngle (turn_speed * state);
}
}

public void noKey() {
if(key == false) {
if(state < 0)
state++;
if(state > 0)
state--;
}
}

public void left() {
if (state > (turn_dur * -1) && speed > 0) {
state--;
speed -= 0.03;
}
key = true;
}

public void right() {
if (state < turn_dur && speed > 0) {
state++;
speed -= 0.03;
}
key = true;
}
public void up () {
if(speed < max_speed)
speed += accelaration;
}
public void brake () {
if(speed > 0)
speed -= 0.1; // Good brakes!
}

}