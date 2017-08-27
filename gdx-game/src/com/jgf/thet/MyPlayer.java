package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyPlayer extends JgfChara
{
	static public final int kType_Archer = 1;
	static public final int kType_Knight = 2;
	static public final int kType_Mage = 3;
	static public final int kType_Ninja = 4;
	
	static public final int kAct_None = 0;
	static public final int kAct_MageFire = 1;
	static public final int kAct_NinjaDash = 2;
	static public final int kAct_NinjaJump = 3;
	
	int m_type;
	int m_lr;
	int m_maxHp;
	int m_hp;
	int m_act;
	int m_actId;
	int m_killCnt;
	int m_gemCnt;
	int m_skillCnt;
	int m_bootsCnt;
	int m_glovesCnt;
	float m_invTimer;
	float m_actTimer;
	float m_moveSpeed;
	float m_stopTimer;
	float m_shootSpeed;
	float m_shootTime;
	float m_shootTimer;
	
	public void addGemCnt(int gemCnt)
	{
		m_killCnt++;
		m_gemCnt += gemCnt;
	}
	
	public void addHp(int hp)
	{
		if(m_hp <= 0) return;
		m_hp += hp;
		if(m_maxHp < m_hp)
		{
			m_hp = m_maxHp;
			return;
		}
		if(m_hp <= 0)
		{
			//TODO: 死亡関数呼ぶようにする.
			return;
		}
	}
	
	public void buyBoots(int price)
	{
		m_gemCnt -= price;
		m_bootsCnt++;
		m_moveSpeed = 30f + 3f * (m_bootsCnt - 1);
	}
	
	public void buyGloves(int price)
	{
		m_gemCnt -= price;
		m_glovesCnt++;
		float shootTime = 1.5f - 0.2f * (m_glovesCnt - 1);
		setShootTime(shootTime);
	}
	
	public void buyHeart(int price)
	{
		m_gemCnt -= price;
		if(m_maxHp <= m_hp)
		{//HP満タンなら
			m_maxHp++; //最大HPを増やす
		}
		m_hp = m_maxHp; //全回復
	}
	
	public void buySkill(int price)
	{
		m_gemCnt -= price;
		m_skillCnt++;
	}
	
	@Override
	public boolean draw(SpriteBatch batch)
	{
		if(!super.draw(batch)) return false;
		
		/*
		batch.end();
		ShapeRenderer m_shape = new ShapeRenderer();
		m_shape.setProjectionMatrix(batch.getProjectionMatrix());
		m_shape.begin(ShapeRenderer.ShapeType.Line);
		m_shape.setColor(0.8f, 0.8f, 0.8f, 1f);
		Vector2 up = getUpDir();
		Vector2 from = JgfUtil.add(m_pos, JgfUtil.mul(up, 0.1f));
		Vector2 to = JgfUtil.add(m_pos, JgfUtil.mul(up, 0.8f));
		m_shape.line(from.x, from.y, to.x, to.y);
		m_shape.end();
		batch.begin();
		//*/
		
		return true;
	}

	public int getGemCnt()
	{
		return m_gemCnt;
	}
	
	public int getMaxHp()
	{
		return m_maxHp;
	}
	
	public int getSkillCnt()
	{
		return m_skillCnt;
	}
	
	public void EndAct()
	{
		if(!isAct()) return;
		m_actTimer = 0.001f;
	}
	
	public MyPlayer(int charaId)
	{
		super(charaId);
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
		
		if(0 < m_stopTimer)
		{
			m_stopTimer -= Gdx.graphics.getDeltaTime();
			return true;
		}
		
		JgfBtn moveBtnL = m_main.ui.getMoveBtn(m_charaId, 0);
		JgfBtn moveBtnR = m_main.ui.getMoveBtn(m_charaId, 1);
		
		if(0f < m_actTimer)
		{
			m_actTimer -= Gdx.graphics.getDeltaTime();
			if(0f < m_actTimer)
			{//まだ攻撃中
				switch(m_act)
				{
				case kAct_MageFire:
					Vector2 linVel = m_main.bullets[m_actId].getLinVel();
					float rot = 90f * Gdx.graphics.getDeltaTime();
					float move = 0f;
					if(moveBtnL.isPressed())
					{
						move -= 1f;
						linVel.rotate(rot);
					}
					if(moveBtnR.isPressed())
					{
						move += 1f;
						rot *= -1f;
						linVel.rotate(rot);
					}
					move *= 0.5f;
					movePosRot(move);
					break;
				case kAct_NinjaDash:
					if(m_lr ==0) movePosRot(+10f);
					else movePosRot(-10f);
					break;
				case kAct_NinjaJump:
					break;
				}
			}
			else
			{//攻撃おわり
				switch(m_act)
				{
				case kAct_NinjaDash:
					break;
				case kAct_NinjaJump:
					movePosRot(0f);
					setLinVel(0f, 0f);
					break;
				}
			}
			return true;
		}

		float move = 0f;
		
		if(moveBtnL.isPressed())
		{
			move -= 1f;
		}
		if(moveBtnR.isPressed())
		{
			move += 1f;
		}
		movePosRot(move);
		
		//弾を撃つ
		if(m_actTimer <= 0f && m_main.getState() == MyMain.kState_Play)
		{
			m_shootTimer += Gdx.graphics.getDeltaTime();
		}
		if(m_shootTime < m_shootTimer)
		{
			m_shootTimer = 0f;
			int i = m_main.getVacantBulletIdx();
			Vector2 pos;
			Vector2 linVel;
			switch(m_type)
			{
			case kType_Archer:
				m_main.bullets[i].setupType(m_charaId, MyBullet.kType_Arrow);
				m_main.bullets[i].setIgnore(m_charaId);
				pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.1f));
				linVel = JgfUtil.mul(getUpDir(), m_shootSpeed);
				m_main.bullets[i].setPos(pos);
				m_main.bullets[i].setLinVel(linVel);
				m_main.bullets[i].setRot(m_rot);
				m_main.bullets[i].setLR(m_lr);
				break;
			case kType_Knight:
				m_main.bullets[i].setupType(m_charaId, MyBullet.kType_Sword);
				m_main.bullets[i].setIgnore(m_charaId);
				if(move == 0f)
				{
					pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.1f));
					linVel = JgfUtil.mul(getUpDir(), m_shootSpeed);
					m_main.bullets[i].setPos(pos);
					m_main.bullets[i].setLinVel(linVel);
					m_main.bullets[i].setRot(m_rot);
					m_main.bullets[i].setLR(-1); //C
				}
				else if(m_lr == 0)
				{
					pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.07f));
					pos.add(JgfUtil.mul(getRightDir(), 0.05f));
					m_main.bullets[i].setPos(pos);
					m_main.bullets[i].setRot(m_rot - 90f);
					m_main.bullets[i].setLR(m_lr);
				}
				else
				{
					pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.07f));
					pos.add(JgfUtil.mul(getLeftDir(), 0.05f));
					m_main.bullets[i].setPos(pos);
					m_main.bullets[i].setRot(m_rot + 90f);
					m_main.bullets[i].setLR(m_lr);
				}
				break;
			case kType_Mage:
				m_main.bullets[i].setupType(m_charaId, MyBullet.kType_Fire);
				m_main.bullets[i].setIgnore(m_charaId);
				pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.15f));
				linVel = JgfUtil.mul(getUpDir(), m_shootSpeed);
				m_main.bullets[i].setPos(pos);
				m_main.bullets[i].setLinVel(linVel);
				m_main.bullets[i].setRot(m_rot);
				m_main.bullets[i].setLR(m_lr);
				m_actTimer = 10f;
				m_act = kAct_MageFire;
				m_actId = i;
				break;
			case kType_Ninja:
				if(move == 0f)
				{
					m_shootTimer = m_shootTime;
				}
				else
				{
					m_act = kAct_NinjaDash;
					m_actTimer = 0.15f;
					m_invTimer = m_actTimer;
					m_main.bullets[i].setupType(m_charaId, MyBullet.kType_NinjaDash);
					m_main.bullets[i].setIgnore(m_charaId);
					m_main.bullets[i].setLR(m_lr);
					//m_main.log.print("ninja");
				}
				break;
			}
			
			//m_main.log.print("bullet"+i);
		}
		
		//スキル
	//	if(0 < m_skillCnt)
		{
			JgfBtn skillBtn = m_main.ui.getSkillBtn(m_charaId);
			if(skillBtn.isPush())
			{
				m_skillCnt--;
				m_shootTimer = 0f;
				switch(m_type)
				{
				case kType_Archer:
					{
						Vector2 pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.1f));
						Vector2 linVel = JgfUtil.mul(getUpDir(), m_shootSpeed);
						linVel.rotate(-45f);
						float rot = m_rot - 45f;
						for(int i = 0; i < 5; i++)
						{
							int bltIdx = m_main.getVacantBulletIdx();
							linVel.rotate(+15f);
							rot += 15f;
							m_main.bullets[bltIdx].setupType(m_charaId, MyBullet.kType_Arrow);
							m_main.bullets[bltIdx].setIgnore(m_charaId);
							m_main.bullets[bltIdx].setPos(pos);
							m_main.bullets[bltIdx].setLinVel(linVel);
							m_main.bullets[bltIdx].setRot(rot);
							m_main.bullets[bltIdx].setLR(m_lr);
						}
					}
					break;
				case kType_Knight:
					{
						m_invTimer = 3f;
						int bltIdx = m_main.getVacantBulletIdx();
						m_main.bullets[bltIdx].setupType(m_charaId, MyBullet.kType_Shield);
						m_main.bullets[bltIdx].setIgnore(m_charaId);
						m_main.bullets[bltIdx].setLR(m_lr);
					}
					break;
				case kType_Mage:
					{
						m_act = kAct_None; //アクションキャンセル
						Vector2 pos = JgfUtil.add(m_pos, JgfUtil.mul(getUpDir(), 0.15f));
						int bltIdx = m_main.getVacantBulletIdx();
						m_main.bullets[bltIdx].setupType(m_charaId, MyBullet.kType_Heal);
						m_main.bullets[bltIdx].setIgnore(m_charaId);
						m_main.bullets[bltIdx].setPos(pos);
						m_main.bullets[bltIdx].setRot(m_rot);
						m_main.bullets[bltIdx].setLR(m_lr);
						for(int i = 0; i < MyMain.PLAYER_CNTMAX; i++)
						{
							float dist = pos.dst(m_main.players[i].getCenterPos());
							if(dist < 0.25f)
							{
								m_main.players[i].addHp(1);
								m_main.players[i].setColorImd(Color.PINK, 0.2f);
							}
						}
					}
					break;
				case kType_Ninja:
					{
						m_act = kAct_NinjaJump;
						m_actTimer = 0.3f;
						m_invTimer = m_actTimer;
						Vector2 linVel = JgfUtil.mul(m_pos, -1f);
						linVel.nor().scl(3f);
						setLinVel(linVel);
						int bltIdx = m_main.getVacantBulletIdx();
						m_main.bullets[bltIdx].setupType(m_charaId, MyBullet.kType_NinjaJump);
						m_main.bullets[bltIdx].setIgnore(m_charaId);
						m_main.bullets[bltIdx].setLR(m_lr);
					}
					break;
				}
			}
		}
		
		return true;
	}
	
	public int getBootsCnt()
	{
		return m_bootsCnt;
	}
	
	public int getGlovesCnt()
	{
		return m_glovesCnt;
	}
	
	public int getHp()
	{
		return m_hp;
	}

	@Override
	public Rectangle getRect()
	{
		return JgfUtil.scl(super.getRect(), 0.3f);
	}

	public int getType()
	{
		return m_type;
	}
	
	@Override
	public void hit(JgfChara chara)
	{
		super.hit(chara);
		
		if(m_hp <= 0) return;
		
		m_hp--;
		m_main.asset.getSnd("dmg00.mp3").play();
		EndAct();
		if(0 < m_hp)
		{
			m_invTimer = 1f;
			m_stopTimer = 0.1f;
			setColorImd(Color.RED, 0.2f);
			m_main.log.print("players[" + m_charaId + "].hp = " + m_hp);
		}
		else
		{
			m_invTimer = 9999f;
			setColorImd(Color.RED, 0.2f);
			m_main.log.print("players[" + m_charaId + "].dead");
		}
	}
	
	public boolean isAct()
	{
		return(0f < m_actTimer);
	}
	
	public boolean isInv()
	{
		return(0f < m_invTimer);
	}
	
	private void movePosRot(float scl)
	{
		float move = scl * m_moveSpeed * Gdx.graphics.getDeltaTime();
		m_pos = m_pos.nor().scl(0.45f).rotate(move);
		setPos(m_pos);
		setRot(m_pos.angle() + 90f);
		
		if(move < 0f)
		{
			if(m_lr == 0)
			{
				m_lr = 1;
				setFlip(true, false);
			}
		}
		else if(0f < move)
		{
			if(m_lr == 1)
			{
				m_lr = 0;
				setFlip(false, false);
			}
		}
		
		String atlasName = m_atlas.getName();
		if(move == 0f)
		{
			switch(m_type)
			{
			case kType_Archer: atlasName = "archer02.png"; break;
			case kType_Knight: atlasName = "knight02.png"; break;
			case kType_Mage: atlasName = "mage02.png"; break;
			case kType_Ninja: atlasName = "ninja02.png"; break;	
			}
		}
		else
		{
			switch(m_type)
			{
			case kType_Archer: atlasName = "gunman00walk00.png"; break;
			case kType_Knight: atlasName = "knight00walk00.png"; break;
			case kType_Mage: atlasName = "mage00walk00.png"; break;
			case kType_Ninja: atlasName = "ninja00walk00.png"; break;
			}
		}
		if(atlasName != m_atlas.getName())
		{
			setAtlas(atlasName);
		}
	}
	
	public void setupType(int type)
	{
		m_type = type;
		switch(m_type)
		{
		case kType_Archer:
			setup("archer02.png", kOrigin_Foot, 0.1f, 0.1f);
			m_maxHp = 4;
			m_hp = 4;
			m_moveSpeed = 30f;
			m_shootSpeed = 1f;
			setShootTime(1.5f);
			break;
		case kType_Knight:
			setup("knight02.png", kOrigin_Foot, 0.1f, 0.1f);
			m_maxHp = 4;
			m_hp = 4;
			m_moveSpeed = 30f;
			m_shootSpeed = 0.7f;
			setShootTime(1.5f);
			break;
		case kType_Mage:
			setup("mage02.png", kOrigin_Foot, 0.1f, 0.1f);
			m_maxHp = 4;
			m_hp = 4;
			m_moveSpeed = 30f;
			m_shootSpeed = 0.4f;
			setShootTime(1.5f);
			break;
		case kType_Ninja:
			setup("ninja02.png", kOrigin_Foot, 0.1f, 0.1f);
			m_maxHp = 4;
			m_hp = 4;
			m_moveSpeed = 30f;
			m_shootSpeed = 0.8f;
			setShootTime(1.5f);
			break;
		}
		m_sprite.setColor(Color.BLACK);
		m_lr = 0;
		m_invTimer = 0f;
		m_stopTimer = 0f;
		m_act = kAct_None;
		m_actId = -1;
		m_actTimer = 0f;
		m_killCnt = 0;
		m_gemCnt = 0;
		m_skillCnt = 0;
		m_bootsCnt = 1;
		m_glovesCnt = 1;
	}
	
	public void setShootTime(float shootTime)
	{
		m_shootTime = shootTime;
		m_shootTimer = 0f;
	}
}
