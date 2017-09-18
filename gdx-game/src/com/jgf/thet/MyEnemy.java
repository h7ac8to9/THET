package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyEnemy extends JgfChara
{
	static public final int kType_None = 0;
	static public final int kType_Bat = 1;
	static public final int kType_Zombie = 2;
	static public final int kType_Balloon = 3;
	
	static public final int kState_None = 0;
	static public final int kState_Outside = 1;
	static public final int kState_ComeIn = 2;
	static public final int kState_Inside = 3;
	
	int m_type;
	int m_hp;
	JgfChara m_tgt;
	float m_invTimer;
	float m_moveSpeed;
	
	boolean m_canComeIn;
	float m_comeInTime;

	Vector2 m_tmpPos;
	
	public void comeIn()
	{
		m_canComeIn = true;
	}
	
	@Override
	public boolean draw(SpriteBatch batch)
	{
		if(!super.draw(batch)) return false;
		
		return true;
	}

	@Override
	public Rectangle getRect()
	{
		return JgfUtil.scl(super.getRect(), 0.6f);
	}
	
	public void hit(JgfChara chara)
	{
		super.hit(chara);
		
		if(m_hp <= 0) return;
		if(getState() == kState_Outside) return;
		
		MyPlayer player = null;
		if(chara instanceof MyPlayer)
		{
			player = (MyPlayer)chara;
		}
		else
		{
			JgfChara owner = chara.getOwner();
			while(owner != null)
			{
				if(owner instanceof MyPlayer)
				{
					player = (MyPlayer)owner;
				}
				owner = owner.getOwner();
			}
		}
		
		m_hp--;
		if(0 < m_hp)
		{
			m_invTimer = 0.5f;
			setColorImd(Color.RED, 0.2f);
			switch(m_type)
			{
			case kType_Bat:
				m_moveSpeed = 0.1f;
				m_tgt = player;
				break;
			case kType_Zombie:
				m_moveSpeed *= -1f;
				setFlip(true, false);
				break;
			case kType_Balloon:
				setSize(0.1f, 0.1f);
				break;
			}
		}
		else
		{
			switch(m_type)
			{
			case kType_Bat:
				break;
			case kType_Zombie:
				break;
			case kType_Balloon:
				//ここで弾たち産む
				final int bulletCnt = 12;
				for(int i = 0; i < bulletCnt; i++)
				{
					int b = m_main.getVacantBulletIdx();
					m_main.bullets[b].setupType(chara, MyBullet.kType_BalBlt);
					Vector2 pos =JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.1f));
					m_main.bullets[b].setPos(pos);
					Vector2 dir = getUpDir().rotate(360f / (float)bulletCnt * (float)i);
					Vector2 linVel = JgfUtil.mul(dir, 0.3f);
					m_main.bullets[b].setLinVel(linVel);
					m_main.log.print("bullet"+b);
				}
				break;
			}
			{
				int b = m_main.getVacantBulletIdx();
				m_main.bullets[b].setupType(chara, MyBullet.kType_Gem);
				Vector2 pos = getCenterPos();
				m_main.bullets[b].setPos(pos);
			}
			if(player != null)
			{
				player.addGemCnt(1);
			}
			m_main.level.addKillCnt();
			dead();
		}
	}
	
	public boolean isInv()
	{
		return(0f < m_invTimer);
	}
	
	public boolean isOutside()
	{
		return(getState() == kState_Outside);
	}
	
	public MyEnemy(int charaId)
	{
		super(charaId);
		
		m_tmpPos = new Vector2();
	}
	
	public void setComeInTime(float comeInTime)
	{
		m_comeInTime = comeInTime;
	}
	
	public void setupType(int type)
	{
		m_type = type;
		setState(kState_Outside);
		switch(m_type)
		{
		case kType_Bat:
			setup("bat01.png", kOrigin_Center, 0.06f, 0.06f);
			m_hp = 2;
			m_moveSpeed = 0.02f;
			break;
		case kType_Zombie:
			setup("zombie00.png", kOrigin_Foot, 0.08f, 0.08f);
			m_hp = 2;
			m_moveSpeed = -5f;
			m_sprite.flip(true, false);
			break;
		case kType_Balloon:
			setup("balloon00.png", kOrigin_Center, 0.06f, 0.06f);
			m_hp = 2;
			m_moveSpeed = 0.02f;
			break;
		}
		m_canComeIn = false;
		m_comeInTime = 9999f;
		m_sprite.setColor(Color.BLACK);
		JgfUtil.set(m_tmpPos, 0f, 0f);
	}

	@Override
	public boolean update()
	{
		if(!super.update()) return false;
		
		//無敵
		if(0f < m_invTimer)
		{
			m_invTimer -= Gdx.graphics.getDeltaTime();
		}
		
		switch(m_type)
		{
		case kType_Bat: updateBat(); break;
		case kType_Zombie: updateZombie(); break;
		case kType_Balloon: updateBalloon(); break;
		}
		
		return true;
	}
	
	//-------- Bat --------
	
	private void updateBat()
	{
		switch(getState())
		{
		case kState_Outside: updateBatOutside(); break;
		case kState_ComeIn: updateBatComeIn(); break;
		case kState_Inside: updateBatInside(); break;
		}
	}
	
	private void updateBatOutside()
	{
		if(m_canComeIn || m_comeInTime < m_stateTimer)
		{
			setState(kState_ComeIn);
		}
	}
	
	private void updateBatComeIn()
	{
		switch(m_stateStep)
		{
		case 0:
			m_stateStep++;
			m_stateTimer = 0f;
			JgfUtil.set(m_tmpPos, m_pos);
			Vector2 linVel = JgfUtil.mul(m_pos, -1f);
			linVel.rotate((float)MathUtils.random(-90f, +90f));
			linVel.nor().scl(0.4f);
			setLinVel(linVel);
			m_comeInTime = 0.1f + 0.2f * (float)Math.random();
			break;
		case 1:
			if(m_comeInTime < m_stateTimer)
			{
				setState(kState_Inside);
			}
			break;
		}
	}
	
	private void updateBatInside()
	{
		if(m_hp == 2 || m_tgt == null)
		{//まだダメージを受けていなければ近いPlayerに近づく
			float dist1P = JgfUtil.dist(m_main.players[0], this);
			float dist2P = JgfUtil.dist(m_main.players[1], this);
			if(dist1P < dist2P) m_tgt = m_main.players[0];
			else m_tgt = m_main.players[1];
		}
		Vector2 linVel = JgfUtil.sub(m_tgt.getCenterPos(), m_pos);
		linVel.nor().scl(m_moveSpeed);
		setLinVel(linVel);
	}
	
	//-------- Zombie --------
	
	private void updateZombie()
	{
		switch(getState())
		{
		case kState_Outside: updateZombieOutside(); break;
		case kState_ComeIn: updateZombieComeIn(); break;
		case kState_Inside: updateZombieInside(); break;
		}
	}
	
	private void updateZombieOutside()
	{
	//	if(2f < m_stateTimer)
		if(m_canComeIn || m_comeInTime < m_stateTimer)
		{
			//setState(kState_ComeIn);
			
			setState(kState_Inside);
			m_pos.nor().scl(0.45f);
			setPos(m_pos);
			setRot(m_rot + 180f);
			m_sprite.flip(true, false);
		}
	}
	
	private void updateZombieComeIn()
	{
		switch(m_stateStep)
		{
		case 0:
			m_stateStep++;
			m_stateTimer = 0f;
			JgfUtil.set(m_tmpPos, m_pos);
			break;
		case 1:
			final float preTime = 1.5f;
			if(preTime < m_stateTimer)
			{
				m_stateStep++;
				m_stateTimer = 0f;
			}
			else
			{
				m_pos.x = m_tmpPos.x + 0.005f * (float)Math.sin(m_stateTimer * Math.PI * 2f / preTime);
				m_pos.y = m_tmpPos.y + 0.005f * (float)Math.sin(m_stateTimer * Math.PI * 4f / preTime);
				setPos(m_pos);
			}
			break;
		case 2:
			setState(kState_Inside);
			m_pos.nor().scl(0.45f);
			setPos(m_pos);
			setRot(m_rot + 180f);
			m_sprite.flip(true, false);
			break;
		}
	}
	
	private void updateZombieInside()
	{
		float rot = m_moveSpeed * Gdx.graphics.getDeltaTime();
		m_pos.rotate(rot);
		setPos(m_pos);
		setRot(m_pos.angle() + 90f);
	}
	
	//-------- Balloon --------

	private void updateBalloon()
	{
		switch(getState())
		{
			case kState_Outside: updateBalloonOutside(); break;
			case kState_ComeIn: updateBalloonComeIn(); break;
			case kState_Inside: updateBalloonInside(); break;
		}
	}

	private void updateBalloonOutside()
	{
		if(m_canComeIn || m_comeInTime < m_stateTimer)
		{
			setState(kState_ComeIn);
		}
	}

	private void updateBalloonComeIn()
	{
		switch(m_stateStep)
		{
		case 0:
			m_stateStep++;
			m_stateTimer = 0f;
			JgfUtil.set(m_tmpPos, m_pos);
			break;
		case 1:
			final float preTime = 1.5f;
			//if(preTime < m_stateTimer)
			if(true)
			{
				m_stateStep++;
				m_stateTimer = 0f;
				Vector2 linVel = JgfUtil.mul(m_pos, -1f);
				linVel.nor().scl(0.2f);
				setLinVel(linVel);
				m_comeInTime = 0.8f + 1.4f * (float)Math.random();
			}
			else
			{
				m_pos.x = m_tmpPos.x + 0.005f * (float)Math.sin(m_stateTimer * Math.PI * 2f / preTime);
				m_pos.y = m_tmpPos.y + 0.005f * (float)Math.sin(m_stateTimer * Math.PI * 4f / preTime);
				setPos(m_pos);
			}
			break;
		case 2:
			if(m_comeInTime < m_stateTimer)
			{
				setState(kState_Inside);
				setLinVel(0f, 0f);
				JgfUtil.set(m_tmpPos, m_pos);
			}
			break;
		}
	}

	private void updateBalloonInside()
	{
		float loopTime = 4f;
		m_pos.x = m_tmpPos.x + 0.005f * (float)Math.sin(m_stateTimer * Math.PI * 2f / loopTime);
		m_pos.y = m_tmpPos.y + 0.005f * (float)Math.sin(m_stateTimer * Math.PI * 4f / loopTime);
		setPos(m_pos);
	}
}
