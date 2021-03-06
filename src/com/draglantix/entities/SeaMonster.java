package com.draglantix.entities;

import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.draglantix.flare.audio.Source;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public abstract class SeaMonster {

	protected Submarine sub;
	
	protected Random rand = new Random();
	protected Vector2f position = new Vector2f();
	protected Vector2f target = new Vector2f();
	
	protected int behaviorState = 0;
	
	protected Source sfx0 = new Source(1.5f, 1000, 0);
	protected Source sfx1 = new Source(1.5f, 1000, 0);
	
	protected float theta, dis;
	
	protected float health = 100f;
	protected boolean dead = false;
	
	protected int returnEvent;
	
	protected int type;
	
	public SeaMonster(Submarine sub, int type) {
		this.sub = sub;
		this.type = type;
		
		this.theta = (float) (rand.nextFloat() * Math.PI * 2);
		
		if(type == 1 || type == 2) {
			this.dis = rand.nextFloat() * 80 + 30f;
		} else {
			this.dis = 30;
		}
		
		float phi = (float) (rand.nextFloat() * Math.PI * 2);
		
		this.position = new Vector2f((float) (dis * Math.cos(theta)),
				(float) (dis * Math.sin(theta)));
		this.target = new Vector2f(position);
		
		sfx0.setPosition3D(new Vector3f(this.position.x, this.position.y, (float) (dis * Math.cos(phi))));
	}
	
	public abstract void tick();
	
	public void render(Graphics g, float sonarRadius, float alpha) {
		
		if(type == 1) {
			if(getRadialDistance() < sonarRadius) {
				g.drawImage(Assets.sonarDot, position.sub(sub.getPosition(), new Vector2f()).mul(2), new Vector2f(5),
						new Vector2f(0), new Color(150, 0, 0, alpha));
			}
		}else if(type == 2) {
			if(getRadialDistance() < sonarRadius) {
				g.drawImage(Assets.sonarDot, position.sub(sub.getPosition(), new Vector2f()).mul(2), new Vector2f(8),
						new Vector2f(0), new Color(150, 0, 0, alpha));
			}
		}else {
			if(getRadialDistance() < sonarRadius) {
				g.drawImage(Assets.sonarDot, position.sub(sub.getPosition(), new Vector2f()).mul(2), new Vector2f(15),
						new Vector2f(0), new Color(150, 0, 0, alpha));
			}
		}
	}
	
	protected void swim() {
		
		float lerpFactor;
		
		if(type == 1) {
			lerpFactor = 0.007f;
		}else if(type == 2) {
			lerpFactor = 0.009f;
		}else {
			lerpFactor = 0.1f;
		}
		
		if(type == 1 || type == 2) {
		
			theta += 0.01f;
			if(theta > (Math.PI * 2)) {
				theta = 0;
			}
			
			if(rand.nextInt(100) < 20) {
				if(dis > 0) {
					dis -= 0.1f;
				}
			}
			
			this.dis += 0.5 * Math.cos(theta * 10);
		
		}else {
			
			theta += 10f;
			if(theta > (Math.PI * 2)) {
				theta = 0;
			}
			
			dis -= 1f;
			
		}
		
		this.target = new Vector2f((float) (sub.getPosition().x + (dis * Math.cos(theta))),
				(float) (sub.getPosition().y + dis * Math.sin(theta)));
		
		this.position.lerp(new Vector2f(this.target), lerpFactor);
	}
	
	protected float getRadialDistance() {
		return sub.getPosition().distance(position);
	}
	
	public void fadeSFX(float value) {
		sfx0.setVolume(value);
		sfx1.setVolume(value);
	}
	
	public void resumeSFX() {
		sfx0.setVolume(1);
		sfx1.setVolume(1);
	}

	public boolean isDead() {
		return dead;
	}
	
}
