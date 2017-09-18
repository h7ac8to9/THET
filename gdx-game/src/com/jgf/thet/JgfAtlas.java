package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import java.util.*;
import com.badlogic.gdx.utils.*;

public class JgfAtlas
{
	MyMain m_main;
	String m_name;
	Texture m_tex;
	Array<TextureRegion> m_frames;
	Animation m_anim;
	int m_row;
	int m_col;
	float m_duration;
	Animation.PlayMode m_playMode;
	
	public String getName()
	{
		return m_name;
	}
	
	public TextureRegion getRegion(int row, int col)
	{
		int idx = row * m_col + col;
		return m_frames.get(idx);
	}
	
	public TextureRegion getRegion(float time)
	{
		TextureRegion region;
		if(0f < m_duration)
		{
			region = m_anim.getKeyFrame(time);
		}
		else
		{
			//region = getRegion(0, 0);
			region = m_frames.get(0);
		}
		return region;
	}
	
	public void init(String name)
	{
		m_name = name;
		m_row = 1;
		m_col = 1;
		m_duration = 0f;
		m_playMode = Animation.PlayMode.NORMAL;
	}
	
	public void init(String name, int row, int col, float duration, Animation.PlayMode playMode)
	{
		m_name = name;
		m_row = row;
		m_col = col;
		m_duration = duration;
		m_playMode = playMode;
	}
	
	public boolean isEndAnim(float time)
	{
		return m_anim.isAnimationFinished(time);
	}
	
	public JgfAtlas()
	{
		m_main = JgfGame.getMain();
	}
	
	public void setup()
	{
		m_tex = m_main.asset.man.get(m_name);
		m_frames = new Array<TextureRegion>();
		float u = 1f / (float)m_row;
		float v = 1f / (float)m_col;
		
		for(int c = m_col - 1; 0 <= c; c--)
		{
			for(int r = 0; r < m_row; r++)
			{
				TextureRegion region = new TextureRegion();
				region.setTexture(m_tex);
				region.setRegion(u*r, v*c, u*(r+1), v*(c+1));
				m_frames.add(region);
			}
		}
		if(0f < m_duration)
		{
			m_anim = new Animation(m_duration, m_frames);
			m_anim.setPlayMode(m_playMode);
		}
	}
}
