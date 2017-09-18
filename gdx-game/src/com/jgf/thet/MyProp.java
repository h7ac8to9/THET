package com.jgf.thet;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

public class MyProp extends JgfChara
{
	static public final int kType_None = 0;
	static public final int kType_CaveHigh = 1;
	static public final int kType_CaveLow = 2;
	
	static public final int kState_None = 0;
	static public final int kState_Idle = 1;
	static public final int kState_Act = 2;
	
	int m_type;
	int m_spawnType;
	
	public MyProp(int charaId)
	{
		super(charaId);
	}

	@Override
	public boolean draw(SpriteBatch batch)
	{
		switch(m_main.getState())
		{
		case MyMain.kState_Ready:
		case MyMain.kState_Play:
			break;
		default:
			return false;
		}
		
		if(!super.draw(batch)) return false;
		
		return true;
	}

	
	
	public void setSpawnType(int type)
	{
		m_spawnType = type;
	}
	
	@Override
	protected void setState(int state)
	{
		super.setState(state);
		
		switch(m_state)
		{
		case kState_Idle:
			setAtlas("cave00.png");
			break;
		case kState_Act:
			setAtlas("cave01.png");
			break;
		}
	}
	
	public void setupType(int type)
	{
		m_type = type;
		switch(m_type)
		{
		case kType_CaveLow:
			setup("cave00.png", kOrigin_Foot, 0.07f, 0.07f);
			break;
		case kType_CaveHigh:
			setup("cave00.png", kOrigin_Foot, 0.07f, 0.07f);
			break;
		}
		m_sprite.setColor(Color.BLACK);
		m_sprite.setAlpha(0.3f);
		m_spawnType = MyEnemy.kType_None;
	}

	@Override
	public boolean update()
	{
		if(!super.update()) return false;
		
		switch(m_type)
		{
		case kType_CaveHigh:
		case kType_CaveLow:
			updateCave();
			break;
		}
		
		return true;
	}
	
	private void updateCave()
	{
		switch(m_state)
		{
		case kState_Idle: updateCaveIdle(); break;
		case kState_Act: updateCaveAct(); break;
		}
	}
	
	private void updateCaveIdle()
	{
		
	}
	
	private void updateCaveAct()
	{
		if(1f < m_stateTimer)
		{
			setState(kState_Idle);
			
			int i = m_main.getVacantEnemyIdx();
			Vector2 pos = m_pos.cpy();
			if(m_type == kType_CaveHigh)
			{
				pos = getCenterPos();
			}
			m_main.enemies[i].setupType(m_spawnType);
			m_main.enemies[i].setPos(pos);
			m_main.enemies[i].setComeInTime(0f);
		}
	}
}
