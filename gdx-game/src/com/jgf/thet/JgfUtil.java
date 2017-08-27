package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class JgfUtil
{
	static Vector2 add(Vector2 v1, Vector2 v2)
	{
		Vector2 ret = new Vector2();
		ret.x = v1.x + v2.x;
		ret.y = v1.y + v2.y;
		return ret;
	}
	
	static public float dist(JgfChara c1, JgfChara c2)
	{
		return(sub(c1.getPos(), c2.getPos()).len());
	}
	
	static public Vector2 mul(Vector2 v, float f)
	{
		Vector2 ret = new Vector2();
		ret.x = v.x * f;
		ret.y = v.y * f;
		return ret;
	}
	
	static public void set(Vector2 v, float x, float y)
	{
		v.x = x;
		v.y = y;
	}
	
	static public void set(Vector2 v1, Vector2 v2)
	{
		v1.x = v2.x;
		v1.y = v2.y;
	}
	
	static public Rectangle scl(Rectangle r, float f)
	{
		Vector2 c = new Vector2();
		r.getCenter(c);
		Rectangle ret = new Rectangle();
		ret.x = (r.x - c.x) * f + c.x;
		ret.y = (r.y - c.y) * f + c.y;
		ret.width = r.width * f;
		ret.height = r.height * f;
		return ret;
	}
	
	static public void shuffle(int[] a)
	{
		int len = a.length;
		for(int i = 0; i < len; i++)
		{
			int j = MathUtils.random(0, len - 1);
			int t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}
	
	static public Vector2 sub(Vector2 v1, Vector2 v2)
	{
		Vector2 ret = new Vector2();
		ret.x = v1.x - v2.x;
		ret.y = v1.y - v2.y;
		return ret;
	}
}
