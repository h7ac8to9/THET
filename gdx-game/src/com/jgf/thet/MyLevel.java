package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyLevel
{
	static private int CAVE_CNTMAX = 32;
	
	MyMain m_main;
	
	MyStg m_stg; //外部ファイルのコンテナ
	int m_roundNum; //現在のラウンド番号
	boolean m_isEndingStg; //全てのラウンドが終わったらtrue
	
	//----ラウンド毎----
	float m_playingTime; //プレイ秒
	int m_killCnt; //倒した敵の総数
	float m_spawnTimer; //次の敵出現までの秒数
	float m_spawnCnt; //出現した敵の総数
	MyProp[] m_highCaves; //高い洞窟たち
	MyProp[] m_lowCaves; //低い洞窟たち
	int m_highCaveCnt; //高い洞窟の配置数
	int m_lowCaveCnt; //低い洞窟の配置数
	
	public void addKillCnt()
	{
		m_killCnt++;
	}
	
	private void clear()
	{
		m_isEndingStg = false;
		for(int i = 0; i < MyMain.PLAYER_CNTMAX; i++)
		{
			m_main.players[i].dead();
		}
		for(int i = 0; i < MyMain.ENEMY_CNTMAX; i++)
		{
			m_main.enemies[i].dead();
		}
		clearBullets();
		clearProps();
	}
	
	public void clearBullets()
	{
		for(int i = 0; i < MyMain.BULLET_CNTMAX; i++)
		{
			m_main.bullets[i].dead();
		}
	}
	
	public void clearProps()
	{
		for(int i = 0; i < MyMain.PROP_CNTMAX; i++)
		{
			m_main.props[i].dead();
		}
	}
	
	public float getPlayingTime()
	{
		return m_playingTime;
	}
	
	public int getPrice(int item)
	{
		return m_stg.rounds[m_roundNum].getPrice(item);
	}
	
	private MyRound getRound()
	{
		return m_stg.rounds[m_roundNum];
	}
	
	public int getRoundMax()
	{
		return m_stg.roundCnt;
	}
	
	public int getRoundNum()
	{
		return m_roundNum;
	}
	
	public boolean isEndingStg()
	{
		return m_isEndingStg;
	}
	
	public MyLevel()
	{
		m_main = MyGame.getMain();
		m_stg = new MyStg();
		m_highCaves = new MyProp[CAVE_CNTMAX];
		m_lowCaves = new MyProp[CAVE_CNTMAX];
	}
	
	public boolean nextRound()
	{
		int roundIdx = m_roundNum + 1;
		if(roundIdx < m_stg.roundCnt)
		{//まだラウンドがある
			setRound(roundIdx);
			return true;
		}
		//もうラウンドない
		m_isEndingStg = true;
		return false;
	}
	
	public void setRound(int roundIdx)
	{
		clearBullets();
		clearProps();
		m_roundNum = roundIdx;
		m_playingTime = 0f;
		m_killCnt = 0;
		m_spawnTimer = 0f;
		m_spawnCnt = 0;
		setupHighCaves();
		setupLowCaves();
	}
	
	public void setStg(int stgIdx)
	{
		clear();
		m_stg.setup();
		setRound(0);
	}
	
	private void setupHighCaves()
	{
		int caveCnt = m_stg.highCaveCnt;
		int[] caveIdxs = new int[caveCnt];
		for(int i = 0; i < caveCnt; i++) caveIdxs[i] = i;
		JgfUtil.shuffle(caveIdxs);
		m_highCaveCnt = getRound().getHighCavePick();
		for(int i = 0; i < m_highCaveCnt; i++)
		{
			int caveIdx = caveIdxs[i];
			MyLayout layout = m_stg.highCaveLayouts[caveIdx];
			m_highCaves[i] = spawnProp(MyProp.kType_CaveHigh, layout.angle, layout.dist);
		}
	}
	
	private void setupLowCaves()
	{
		int caveCnt = m_stg.lowCaveCnt;
		int[] caveIdxs = new int[caveCnt];
		for(int i = 0; i < caveCnt; i++) caveIdxs[i] = i;
		JgfUtil.shuffle(caveIdxs);
		m_lowCaveCnt = getRound().getLowCavePick();
		for(int i = 0; i < m_lowCaveCnt; i++)
		{
			int caveIdx = caveIdxs[i];
			MyLayout layout = m_stg.lowCaveLayouts[caveIdx];
			m_lowCaves[i] = spawnProp(MyProp.kType_CaveLow, layout.angle, layout.dist);
		}
	}
	
	public void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		m_playingTime += deltaTime;
		int spawnCntMax = getRound().getSpawnCntMax();
		if(m_spawnCnt < spawnCntMax)
		{
			m_spawnTimer -= deltaTime;
			if(m_spawnTimer < 0f)
			{
				m_spawnTimer = 3f - 0.1f * m_spawnCnt;
				m_spawnTimer = Math.max(m_spawnTimer, 1f);
				m_spawnCnt++;
			
				MyPtrn ptrn = getRound().lotPtrn();
				int type = ptrn.enemy;
				boolean isHigh = (ptrn.address == MyStg.kAddress_HighCave);
			
				if(isHigh)
				{
					int caveIdx = MathUtils.random(0, m_highCaveCnt - 1);
					m_highCaves[caveIdx].setState(MyProp.kState_Act);
					spawnEnemy(type, isHigh, caveIdx);
				}
				else
				{
					int caveIdx = MathUtils.random(0, m_lowCaveCnt - 1);
					m_lowCaves[caveIdx].setState(MyProp.kState_Act);
					spawnEnemy(type, isHigh, caveIdx);
				}
			}
		}
		else
		{//もうラウンドのスポーンは完了してる
			if(spawnCntMax <= m_killCnt)
			{//全てのEnemyを倒した
				if(nextRound())
				{
					m_main.setState(MyMain.kState_Interval);
				}
				else
				{
					m_main.setState(MyMain.kState_Result);
				}
			}
		}
	}
	
	private void spawnEnemy(int type, boolean isHigh, int caveIdx)
	{
		if(isHigh)
		{
			m_highCaves[caveIdx].setSpawnType(type);
			m_highCaves[caveIdx].setState(MyProp.kState_Act);
		}
		else
		{
			m_lowCaves[caveIdx].setSpawnType(type);
			m_lowCaves[caveIdx].setState(MyProp.kState_Act);
		}
	}
	
	private MyProp spawnProp(int type, float rot, float dist)
	{
		int i = m_main.getVacantPropIdx();
		m_main.props[i].setupType(type);

		Vector2 pos = new Vector2();
		pos.x = 0f;
		pos.y = dist;
		pos.rotate(rot);
		
		switch(type)
		{
		case MyProp.kType_CaveLow:
			m_main.props[i].setPos(pos);
			m_main.props[i].setRot(rot + 180f);
			break;
		case MyProp.kType_CaveHigh:
			m_main.props[i].setPos(pos);
			m_main.props[i].setRot(rot + 180f);
			break;
		}
		
		return m_main.props[i];
	}
	
	private class MyStg
	{
		static final int ROUND_CNTMAX = 32;
		static final int CAVE_CNTMAX = 32;
		
		static final int kAddress_None = 0;
		static final int kAddress_HighCave = 1;
		static final int kAddress_LowCave = 2;
		
		public int highCaveCnt;
		public int lowCaveCnt;
		public MyLayout[] highCaveLayouts;
		public MyLayout[] lowCaveLayouts;
		
		public MyRound[] rounds;
		public int roundCnt;
		
		public MyStg()
		{
			highCaveCnt = 0;
			lowCaveCnt = 0;
			highCaveLayouts = new MyLayout[CAVE_CNTMAX];
			lowCaveLayouts = new MyLayout[CAVE_CNTMAX];
			for(int i = 0; i < CAVE_CNTMAX; i++)
			{
				highCaveLayouts[i] = new MyLayout();
				lowCaveLayouts[i] = new MyLayout();
			}
			roundCnt = 0;
			rounds = new MyRound[ROUND_CNTMAX];
			for(int i = 0; i < ROUND_CNTMAX; i++)
			{
				rounds[i] = new MyRound();
			}
			
			clear();
		}
		
		public void clear()
		{
			for(int i = 0; i < highCaveCnt; i++)
			{
				highCaveLayouts[i].clear();
			}
			highCaveCnt = 0;
			for(int i = 0; i < lowCaveCnt; i++)
			{
				lowCaveLayouts[i].clear();
			}
			lowCaveCnt = 0;
			for(int i = 0; i < roundCnt; i++)
			{
				rounds[i].clear();
			}
			roundCnt = 0;
		}
		
		public void setup()
		{
			clear();
			
			highCaveLayouts[0].set(32f, 0.23f);
			highCaveLayouts[1].set(75f, 0.2f);
			highCaveLayouts[2].set(202f, 0.24f);
			highCaveLayouts[3].set(282f, 0.26f);
			highCaveLayouts[4].set(310f, 0.32f);
			highCaveLayouts[5].set(340f, 0.28f);
			highCaveCnt = 6;
			
			lowCaveLayouts[0].set(8f, 0.45f);
			lowCaveLayouts[1].set(38f, 0.45f);
			lowCaveLayouts[2].set(75f, 0.45f);
			lowCaveLayouts[3].set(95f, 0.45f);
			lowCaveLayouts[4].set(122f, 0.45f);
			lowCaveLayouts[5].set(135f, 0.45f);
			lowCaveLayouts[6].set(153f, 0.45f);
			lowCaveLayouts[7].set(167f, 0.45f);
			lowCaveLayouts[8].set(198f, 0.45f);
			lowCaveLayouts[9].set(225f, 0.45f);
			lowCaveLayouts[10].set(248f, 0.45f);
			lowCaveLayouts[11].set(269f, 0.45f);
			lowCaveLayouts[12].set(279f, 0.45f);
			lowCaveLayouts[13].set(296f, 0.45f);
			lowCaveLayouts[14].set(310f, 0.45f);
			lowCaveLayouts[15].set(323f, 0.45f);
			lowCaveLayouts[16].set(336f, 0.45f);
			lowCaveLayouts[17].set(346f, 0.45f);
			lowCaveCnt = 18;
			
			rounds[0].setHighCavePick(2);
			rounds[0].setLowCavePick(3);
			rounds[0].setSpawnCntMax(2);
			rounds[0].setPrices(0, 0, 0, 0);
			rounds[0].addPtrn(MyEnemy.kType_Bat, kAddress_HighCave, 1f);
			rounds[0].addPtrn(MyEnemy.kType_Zombie, kAddress_LowCave, 0.3f);
			rounds[0].addPtrn(MyEnemy.kType_Balloon, kAddress_LowCave, 0.2f);
			
			rounds[1].setHighCavePick(2);
			rounds[1].setLowCavePick(3);
			rounds[1].setSpawnCntMax(2);
			rounds[1].setPrices(8, 1, 1, 1);
			rounds[1].addPtrn(MyEnemy.kType_Bat, kAddress_HighCave, 1f);
			rounds[1].addPtrn(MyEnemy.kType_Zombie, kAddress_LowCave, 0.3f);
			rounds[1].addPtrn(MyEnemy.kType_Balloon, kAddress_LowCave, 0.2f);
			
			roundCnt = 2;
		}
	}
	
	private class MyRound
	{
		static final int PTRN_CNTMAX = 8;
		static final int ITEM_CNTMAX = 4;
		
		int m_highCavePick;
		int m_lowCavePick;
		int m_spawnCntMax;
		int m_ptrnCnt;
		MyPtrn[] m_ptrns;
		int[] m_prices;
		
		public MyRound()
		{
			m_highCavePick = 0;
			m_lowCavePick = 0;
			m_spawnCntMax = 0;
			m_ptrnCnt = 0;
			m_ptrns = new MyPtrn[PTRN_CNTMAX];
			for(int i = 0; i < PTRN_CNTMAX; i++)
			{
				m_ptrns[i] = new MyPtrn();
			}
			m_prices = new int[ITEM_CNTMAX];
			clear();
		}
		
		public void addPtrn(int enemy, int address, float prob)
		{
			m_ptrns[m_ptrnCnt].enemy = enemy;
			m_ptrns[m_ptrnCnt].address = address;
			m_ptrns[m_ptrnCnt].prob = prob;
			m_ptrnCnt++;
		}
		
		public void clear()
		{
			m_highCavePick = 0;
			m_lowCavePick = 0;
			m_spawnCntMax = 0;
			for(int i = 0; i < m_ptrnCnt; i++)
			{
				m_ptrns[i].clear();
			}
			m_ptrnCnt = 0;
			for(int i = 0; i < ITEM_CNTMAX; i++)
			{
				m_prices[i] = 0;
			}
		}
		
		public int getHighCavePick()
		{
			return m_highCavePick;
		}
		
		public int getLowCavePick()
		{
			return m_lowCavePick;
		}
		
		public int getPrice(int item)
		{
			return m_prices[item];
		}
		
		public int getSpawnCntMax()
		{
			return m_spawnCntMax;
		}
		
		//パターンを抽選する
		public MyPtrn lotPtrn()
		{
			float sumProb = 0f;
			for(int i = 0; i < m_ptrnCnt; i++)
			{
				sumProb += m_ptrns[i].prob;
			}
			float prob = MathUtils.random(0f, sumProb);
			float prevProb = 0f;
			float curProb = 0f;
			
			for(int i = 0; i < m_ptrnCnt - 1; i++)
			{
				prevProb = curProb;
				curProb += m_ptrns[i].prob;
				if(prevProb <= prob && prob < curProb)
				{
					return m_ptrns[i];
				}
			}
			
			return m_ptrns[m_ptrnCnt - 1];
		}
		
		void setHighCavePick(int highCavePick)
		{
			m_highCavePick = highCavePick;
		}
		
		void setLowCavePick(int lowCavePick)
		{
			m_lowCavePick = lowCavePick;
		}
		
		void setSpawnCntMax(int spawnCntMax)
		{
			m_spawnCntMax = spawnCntMax;
		}
		
		void setPrices(int price0, int price1, int price2, int price3)
		{
			m_prices[0] = price0;
			m_prices[1] = price1;
			m_prices[2] = price2;
			m_prices[3] = price3;
		}
	}
	
	private class MyPtrn
	{
		public int enemy;
		public int address;
		public float prob;
		
		public MyPtrn()
		{
			clear();
		}
		
		public void clear()
		{
			enemy = MyEnemy.kType_None;
			address = MyStg.kAddress_None;
			prob = 0f;
		}
	}
	
	private class MyLayout
	{
		public float angle;
		public float dist;
		
		public MyLayout()
		{
			clear();
		}
		
		public void clear()
		{
			set(0f, 0f);
		}
		
		public void set(float _angle, float _dist)
		{
			angle = _angle;
			dist = _dist;
		}
	}
}
