package com.draglantix.utils;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class QuadTree {

	private List<AABB> boxes = new ArrayList<AABB>();
	
	private Quad bound;
	private int capacity;
	private boolean divided;
	
	private QuadTree northEast;
	private QuadTree northWest;
	private QuadTree southEast;
	private QuadTree southWest;
	
	public QuadTree(Quad bound, int capacity) {
		this.bound = bound;
		this.capacity = capacity;
		this.divided = false;
	}
	
	public boolean insert(AABB b) {
		
		if(!bound.contains(b.getCenter()))
			return false;
		
		if(boxes.size() < capacity-1) {
			boxes.add(b);
			return true;
		}else {
			if(!divided) {
				subdivide();
			}
			if(northEast.insert(b)) {
				return true;
			}else if(northWest.insert(b)){
				return true;
			}else if(southEast.insert(b)) {
				return true;
			}else if(southWest.insert(b)) {
				return true;
			}
			return false;
		}
	}
	
	public void subdivide() {
		Quad ne = new Quad(new Vector2f(bound.getPos().x + (bound.getHalfLengths().x/2), bound.getPos().y - (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		Quad nw = new Quad(new Vector2f(bound.getPos().x - (bound.getHalfLengths().x/2), bound.getPos().y - (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		Quad se = new Quad(new Vector2f(bound.getPos().x + (bound.getHalfLengths().x/2), bound.getPos().y + (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		Quad sw = new Quad(new Vector2f(bound.getPos().x - (bound.getHalfLengths().x/2), bound.getPos().y + (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		
		northEast = new QuadTree(ne, capacity);
		northWest = new QuadTree(nw, capacity);
		southEast = new QuadTree(se, capacity);
		southWest = new QuadTree(sw, capacity);
		
		divided = true;
	}
	
	public List<AABB> query(Quad range) {
		return query(range, null);
	}
	
	private List<AABB> query(Quad range, List<AABB> last) {
		if(last == null) {
			last = new ArrayList<AABB>();
		}
			
		List<AABB> found = new ArrayList<AABB>();
		if(bound.intersects(range)) {
			for(AABB b : boxes) {
				if(range.contains(b.getCenter()))
					found.add(b);
			}
			
			if(divided) {
				found.addAll(northEast.query(range));
				found.addAll(northWest.query(range));
				found.addAll(southEast.query(range));
				found.addAll(southWest.query(range));
			}
		}
		return found;
	}
	
	public List<AABB> query(Vector2f pos, float radius) {
		return queryrad(new Quad(pos, new Vector2f(radius)), null);
	}
	
	private List<AABB> queryrad(Quad range, List<AABB> last) {
		if(last == null) {
			last = new ArrayList<AABB>();
		}
			
		List<AABB> found = new ArrayList<AABB>();
		if(bound.intersects(range)) {
			for(AABB b : boxes) {
				if(!(range.getPos().distance(b.getCenter()) > range.getHalfLengths().x))
					found.add(b);
			}
			
			if(divided) {
				found.addAll(northEast.query(range.getPos(), range.getHalfLengths().x));
				found.addAll(northWest.query(range.getPos(), range.getHalfLengths().x));
				found.addAll(southEast.query(range.getPos(), range.getHalfLengths().x));
				found.addAll(southWest.query(range.getPos(), range.getHalfLengths().x));
			}
		}
		return found;
	}
	
	public void render(Graphics g) {
		
		g.drawImage(Assets.debug, bound.getPos(), new Vector2f(bound.getHalfLengths().x * 2, bound.getHalfLengths().y * 2), new Vector2f(0), new Color(255, 255, 255, 1));
		
		if(divided) {
			northEast.render(g);
			northWest.render(g);
			southEast.render(g);
			southWest.render(g);
		}
		
		for(AABB b : boxes) {
			g.drawImage(Assets.debug, b.getCenter(), new Vector2f(1, 1), new Vector2f(90, 0), new Color(0, 255, 255, 1));
		}
	}
}
