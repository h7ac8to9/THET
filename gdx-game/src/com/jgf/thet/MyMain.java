package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import java.util.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

public class MyMain extends JgfMain
{
//+++
	static public final int kState_None = 0;
	static public final int kState_StgSel = 1;
	static public final int kState_PlayerSel = 2;
	static public final int kState_Ready = 3;
	static public final int kState_Play = 4;
	static public final int kState_Interval = 5;
	static public final int kState_Result = 6;
	static public final int kState_GameOver = 7;
	static public final int kState_Shop = 8;
	
	static public final int PLAYER_CNTMAX = 2;
	static public final int ENEMY_CNTMAX = 32;
	static public final int BULLET_CNTMAX = 128;
	static public final int PROP_CNTMAX = 32;
	
	static public final int PLAYER_ID_RANGE = 0;
	static public final int ENEMY_ID_RANGE = 1000;
	static public final int BULLET_ID_RANGE = 2000;
	static public final int PROP_ID_RANGE = 3000;
	
	public JgfLog log;
	
	private int m_state;
	public int stateStep;
	public float stateTimer;
	
	public MyUi ui;
	public MyPlayer[] players;
	public MyEnemy[] enemies;
	public MyBullet[] bullets;
	public MyProp[] props;
	public MyBg bg;
	public MyLevel level;
	
	private Music m_music;

	@Override
	public void dispose()
	{
		super.dispose();
	}
	
	public int getState()
	{
		return m_state;
	}
	
	public int getVacantBulletIdx()
	{
		for(int i = 0; i < BULLET_CNTMAX; i++)
		{
			if(!bullets[i].isAlive())
			{
				return i;
			}
		}
		return -1;
	}
	
	public int getVacantEnemyIdx()
	{
		for(int i = 0; i < ENEMY_CNTMAX; i++)
		{
			if(!enemies[i].isAlive())
			{
				return i;
			}
		}
		return -1;
	}
	
	public int getVacantPropIdx()
	{
		for(int i = 0; i < PROP_CNTMAX; i++)
		{
			if(!props[i].isAlive())
			{
				return i;
			}
		}
		return -1;
	}
	
	public void draw(SpriteBatch batch)
	{
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gameCam.apply(batch);
		batch.begin();
		
		bg.draw(batch);
		
		for(int i = 0; i < PROP_CNTMAX; i++)
		{
			props[i].draw(batch);
		}
		for(int i = 0; i < PLAYER_CNTMAX; i++)
		{
			players[i].draw(batch);
		}
		for(int i = 0; i < ENEMY_CNTMAX; i++)
		{
			enemies[i].draw(batch);
		}
		for(int i = 0; i < BULLET_CNTMAX; i++)
		{
			bullets[i].draw(batch);
		}
		
		ui.draw(batch);
		
		uiCam.apply(batch);
		log.draw(batch);
		batch.end();
	}
	
	public void init()
	{
		super.init();
		
		asset.load();
		
		log = new JgfLog();
		
		ui = new MyUi();
		ui.setup();
		players = new MyPlayer[PLAYER_CNTMAX];
		for(int i = 0; i < PLAYER_CNTMAX; i++)
		{
			players[i] = new MyPlayer(i + MyMain.PLAYER_ID_RANGE);
		}
		enemies = new MyEnemy[ENEMY_CNTMAX];
		for(int i = 0; i < ENEMY_CNTMAX; i++)
		{
			enemies[i] = new MyEnemy(i + MyMain.ENEMY_ID_RANGE);
		}
		bullets = new MyBullet[BULLET_CNTMAX];
		for(int i = 0; i < BULLET_CNTMAX; i++)
		{
			bullets[i] = new MyBullet(i + MyMain.BULLET_ID_RANGE);
		}
		props = new MyProp[PROP_CNTMAX];
		for(int i = 0; i < PROP_CNTMAX; i++)
		{
			props[i] = new MyProp(i + MyMain.PROP_ID_RANGE);
		}
		bg = new MyBg();
		level = new MyLevel();
		setState(kState_PlayerSel);
		
		//テスト
		/*
		FileHandle file = Gdx.files.internal("test.csv");
		String text = file.readString();
		log.print(text);
		Preferences prefs = Gdx.app.getPreferences("TestPrefs");
		//prefs.putString("name", "Ninja");
		//prefs.flush();
		String name = prefs.getString("name", "Error");
		log.print(name);
		*/
	}
	
	static public boolean isBullet(JgfChara chara)
	{
		int bulletId = BULLET_ID_RANGE / 1000;
		int charaId = chara.getCharaId() / 1000;
		return(bulletId == charaId);
	}
	
	static public boolean isEnemy(JgfChara chara)
	{
		int enemyId = ENEMY_ID_RANGE / 1000;
		int charaId = chara.getCharaId() / 1000;
		return(enemyId == charaId);
	}
	
	static public boolean isPlayer(JgfChara chara)
	{
		int playerId = PLAYER_ID_RANGE / 1000;
		int charaId = chara.getCharaId() / 1000;
		return(playerId == charaId);
	}
	
	static public boolean isProp(JgfChara chara)
	{
		int propId = PROP_ID_RANGE / 1000;
		int charaId = chara.getCharaId() / 1000;
		return(propId == charaId);
	}
	
	public void setState(int state)
	{
		m_state = state;
		stateStep = 0;
		stateTimer = 0f;
		
		switch(m_state)
		{
		case kState_PlayerSel:
			level.setStg(0);
			break;
		case kState_Ready:
			m_music = asset.getMsc("music01.mp3");
			m_music.play();
			m_music.setLooping(true);
			break;
		case kState_Result:
			m_music.stop();
			break;
		case kState_GameOver:
			m_music.stop();
			break;
		}
	}
	
	public void update()
	{
		super.update();
		
		stateTimer += Gdx.graphics.getDeltaTime();
		switch(m_state)
		{
		case kState_PlayerSel: updatePlayerSel(); break;
		case kState_Ready: updateReady(); break;
		case kState_Play: updatePlay(); break;
		case kState_Interval: updateInterval(); break;
		case kState_Result: updateResult(); break;
		case kState_GameOver: updateGameOver(); break;
		case kState_Shop: updateShop(); break;
		}
		ui.update();
	}
	
	private void updatePlayerSel()
	{
	}
	
	private void updateReady()
	{
		if(3f < stateTimer)
		{
			setState(kState_Play);
			return;
		}
		
		for(int i = 0; i < PLAYER_CNTMAX; i++)
		{
			players[i].update();
		}
	}
	
	private void updatePlay()
	{
		level.update();

		for(int i = 0; i < PLAYER_CNTMAX; i++)
		{
			if(players[i].isInv()) continue;
			Rectangle playerRect = players[i].getRect();
			for(int j = 0; j < ENEMY_CNTMAX; j++)
			{
				if(!enemies[j].isAlive()) continue;
				if(Intersector.overlaps(playerRect, enemies[j].getRect()))
				{
					//Jgf.log.print("Collide players[" + i + "] enemies[" + j + "]");
					players[i].hit(enemies[j]);
					//enemies[j].hit(players[i]);
				}
			}
			for(int j = 0; j < BULLET_CNTMAX; j++)
			{
				if(!bullets[j].isAlive()) continue;
				if(!bullets[j].canHit()) continue;
				if(bullets[j].isIgnore(players[i])) continue;
				if(Intersector.overlaps(playerRect, bullets[j].getRect()))
				{
					//Jgf.log.print("Collide players[" + i + "] bullets[" + j + "]");
					players[i].hit(bullets[j]);
					bullets[j].hit(players[i]);
				}
			}
		}
		for(int i = 0; i < ENEMY_CNTMAX; i++)
		{
			if(enemies[i].isInv()) continue;
			if(!enemies[i].isAlive()) continue;
			for(int j = 0; j < BULLET_CNTMAX; j++)
			{
				if(!bullets[j].isAlive()) continue;
				if(!bullets[j].canHit()) continue;
				//if(bullets[j].isOwner(enemies[i])) continue;
				if(Intersector.overlaps(enemies[i].getRect(), bullets[j].getRect()))
				{
					//Jgf.log.print("Collide enemies[" + i + "] bullets[" + j + "]");
					enemies[i].hit(bullets[j]);
					bullets[j].hit(bullets[i]);
				}
			}
		}

		for(int i = 0; i < PLAYER_CNTMAX; i++)
		{
			players[i].update();
			if(players[i].getHp() <= 0)
			{//ゲームオーバー判定
				setState(MyMain.kState_GameOver);
			}
		}
		int enemyCnt = 0;
		for(int i = 0; i < ENEMY_CNTMAX; i++)
		{
			enemies[i].update();
			if(enemies[i].isAlive()) enemyCnt++;
		}
		for(int i = 0; i < BULLET_CNTMAX; i++)
		{
			bullets[i].update();
		}
		for(int i = 0; i < PROP_CNTMAX; i++)
		{
			props[i].update();
		}
	}
	
	private void updateInterval()
	{
		if(2f < stateTimer)
		{
			setState(kState_Shop);
		}
	}
	
	private void updateResult()
	{
		if(level.isEndingStg())
		{
			if(8f < stateTimer)
			{
				setState(kState_PlayerSel);
			}
		}
		else
		{
			if(2f < stateTimer)
			{
				setState(kState_Shop);
			}
		}
	}
	
	private void updateGameOver()
	{
		if(8f < stateTimer)
		{
			setState(kState_PlayerSel);
		}
	}
	
	private void updateShop()
	{
	}
}



