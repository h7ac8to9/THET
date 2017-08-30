package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyBullet extends JgfChara
{
	static public final int kType_None = 0;
	static public final int kType_Arrow = 1;
	static public final int kType_Sword = 2;
	static public final int kType_Fire = 3;
	static public final int kType_NinjaDash = 4;
	static public final int kType_NinjaJump = 5;
	static public final int kType_Shield = 6;
	static public final int kType_Heal = 7;
	static public final int kType_BalBlt = 8;
	static public final int kType_Gem = 9;
	
	int m_type;
	int m_ignore;
	boolean m_canHit;
	int m_lr;
	float m_lifeTimer;
	float m_outerLen;

	@Override
	public void dead()
	{
		super.dead();
		
		switch(m_type)
		{
		case kType_Fire:
			if(m_owner instanceof MyPlayer)
			{
				MyPlayer player = (MyPlayer)m_owner;
				player.EndAct();
			}
			break;
		}
	}
	
	public MyBullet(int charaId)
	{
		super(charaId);
	}
	
	public void hit(JgfChara chara)
	{
		super.hit(chara);
		
		switch(m_type)
		{
		case kType_Arrow:
		case kType_Fire:
			dead();
			break;
		}
	}
	
	public boolean isIgnore(JgfChara chara)
	{
		return(m_ignore == chara.getCharaId());
	}
	
	public boolean canHit()
	{
		return m_canHit;
	}
	
	public void setIgnore(int ignore)
	{
		m_ignore = ignore;
	}
	
	public void setLR(int lr)
	{
		m_lr = lr;
	}
	
	public void setupType(JgfChara owner, int type)
	{
		m_type = type;
		m_ignore = -1;
		m_canHit = true;
		m_lr = -1;
		m_lifeTimer = 9999f;
		switch(m_type)
		{
		case kType_Arrow:
			setup("arrow00.png", kOrigin_Center, 0.04f, 0.04f);
			m_sprite.setColor(Color.BLACK);
			m_outerLen = 0.43f;
			break;
		case kType_Sword:
			setup("sword00.png", kOrigin_Center, 0.1f, 0.05f);
			m_sprite.setColor(Color.BLACK);
			m_outerLen = 0.45f;
			m_lifeTimer =0.22f;
			break;
		case kType_Fire:
			setup("fire00.png", kOrigin_Center, 0.08f, 0.08f);
			m_sprite.setColor(Color.BLACK);
			m_outerLen = 0.41f;
			break;
		case kType_NinjaDash:
			setup("knife00.png", kOrigin_Foot, 0.15f, 0.1f);
			m_sprite.setColor(Color.BLACK);
			m_outerLen = 0.50f;
			m_lifeTimer = 0.15f;
			break;
		case kType_NinjaJump:
			setup("knife00.png", kOrigin_Center, 0.15f, 0.1f);
			m_sprite.setColor(Color.BLACK);
			m_outerLen = 0.50f;
			m_lifeTimer = 0.3f;
			break;
		case kType_Shield:
			setup("blt01_shield00.png", kOrigin_Foot, 0.15f, 0.15f);
			m_sprite.setColor(Color.YELLOW);
			m_outerLen = 0.50f;
			m_lifeTimer = 3f;
			m_canHit = false;
			break;
		case kType_Heal:
			setup("blt01_heal00.png", kOrigin_Center, 0.5f, 0.5f);
			m_sprite.setColor(Color.PINK);
			m_outerLen = 0.50f;
			m_lifeTimer = 0.5f;
			m_canHit = false;
			break;
		case kType_BalBlt:
			setup("fire00.png", kOrigin_Center, 0.04f, 0.04f);
			m_sprite.setColor(Color.BLACK);
			m_outerLen = 0.43f;
			break;
		case kType_Gem:
			setup("blt01_jem00.png", kOrigin_Center, 0.05f, 0.05f);
			m_sprite.setColor(Color.ORANGE);
			m_outerLen = 0.5f;
			m_lifeTimer = 0.5f;
			m_canHit = false;
			break;
		}
		m_owner = owner;
	}

	@Override
	public boolean update()
	{
		if(!super.update()) return false;
		
		switch(m_type)
		{
		case kType_Sword:
			float elapsed = Gdx.graphics.getDeltaTime();
			if(m_lr == 0)
			{//L
				m_pos.rotate(+120f * elapsed);
				setPos(m_pos);
				setRot(m_pos.angle());
			}
			else if(m_lr == 1)
			{//R
				m_pos.rotate(-120f * elapsed);
				setPos(m_pos);
				setRot(m_pos.angle() + 180f);
			}
			else
			{//C
				//m_linVelで飛ぶので処理しない
			}
			break;
		case kType_NinjaDash:
			if(m_owner != null)
			{
				Vector2 ownerPos = m_owner.getPos();
				setPos(ownerPos);
				setRot(m_pos.angle() + 90f);
			}
			break;
		case kType_NinjaJump:
			if(m_owner != null)
			{
				Vector2 ownerPos = m_owner.getPos();
				setPos(ownerPos);
				setRot(m_pos.angle());
			}
			break;
		case kType_Shield:
			if(m_owner != null)
			{
				Vector2 ownerPos = m_owner.getPos();
				Vector2 ofsPos = JgfUtil.mul(m_owner.getUpDir(), -0.03f);
				Vector2 shieldPos = JgfUtil.add(ownerPos, ofsPos);
				setPos(shieldPos);
				setRot(m_pos.angle() + 90f);
			}
			break;
		case kType_Gem:
			final float gemLifeTime = 0.5f;
			float alpha = m_lifeTimer / gemLifeTime;
			m_sprite.setAlpha(alpha);
			m_sprite.setScale(2f - alpha);
			break;
		}
		
		m_lifeTimer -= Gdx.graphics.getDeltaTime();
		if(m_lifeTimer < 0f)
		{
			dead();
		}
		
		if(m_outerLen < m_pos.len())
		{
			dead();
		}
		
		return true;
	}
}
