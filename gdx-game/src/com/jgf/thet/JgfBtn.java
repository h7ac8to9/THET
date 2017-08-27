package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.collision.*;

public class JgfBtn
{
	final int kTch_None = -1;
	final int kTch_CntMax = 4;
	final int kState_None = 0;
	final int kState_Push = 1;
	final int kState_Pressed = 2;
	final int kState_Release = 3;
	
	MyMain m_main;
	Rectangle m_rect;
	Sprite m_sprite; //任意で使うのでこのクラスではnewしない
	int m_tch;
	int m_state;
	
	public void clear()
	{
		m_tch = kTch_None;
		m_state = kState_None;
	}
	
	public Sprite getSprite()
	{
		return m_sprite;
	}
	
	boolean isPush()
	{
		return(m_state == kState_Push);
	}

	boolean isPressed()
	{
		return(m_state == kState_Pressed);
	}

	boolean isRelease()
	{
		return(m_state == kState_Release);
	}
	
	public JgfBtn()
	{
		m_main = JgfGame.getMain();
	}
	
	public void update()
	{
		if(m_state == kState_Push)
		{
			m_state = kState_Pressed;
		}
		if(m_state == kState_Release)
		{
			m_state = kState_None;
		}
		if(m_tch == kTch_None)
		{//まだこのボタンを押してない
			if(m_sprite != null)
			{
				m_rect = m_sprite.getBoundingRectangle();
			}
			if(m_rect == null) return; //中止
			for(int iTch = 0; iTch < kTch_CntMax; iTch++)
			{
				if(!Gdx.input.isTouched(iTch)) continue;
				Vector3 tch = new Vector3();
				tch.x = Gdx.input.getX(iTch);
				tch.y = Gdx.input.getY(iTch);
				m_main.gameCam.m_cam.unproject(tch);
				boolean isPush = m_rect.contains(tch.x, tch.y);
				if(isPush)
				{
					m_tch = iTch;
					m_state = kState_Push;
				}
			}
		}
		else
		{//このボタンを押してる中
			if(!Gdx.input.isTouched(m_tch))
			{
				m_tch = kTch_None;
				m_state = kState_Release;
			}
		}
	}
	
	public void setRect(Rectangle rect)
	{
		m_rect = rect;
	}
	
	public void setSprite(Sprite sprite)
	{
		m_sprite = sprite;
	}
	
	public void setup()
	{
		m_tch = kTch_None;
		m_state = kState_None;
	}
}
