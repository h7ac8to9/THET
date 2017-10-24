package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyLevel
{
	static public int STG_CNTMAX = 6;
	static int CAVE_CNTMAX = 32;
	
	MyMain m_main;
	
	MyStg m_stg; //外部ファイルのコンテナ
	int m_stgIdx; //現在のステージ番号
	int m_roundIdx; //現在のラウンド番号
	int m_squadIdx; //現在のスカッド番号
	boolean m_isEndStg; //全てのラウンドが終わったらtrue
	String m_inTexName;
	String m_outTexName;
	String m_selectTexName;
	String m_bgmName;
	
	//----ラウンド毎----
	float m_playingTime; //プレイ秒
	int m_killCnt; //倒した敵の総数
	float m_spawnTimer; //次の敵出現までの秒数
	int m_roundSpawnCnt; //出現した敵の総数
	int m_squadSpawnCnt;
	MyProp[] m_highCaves; //高い洞窟たち
	MyProp[] m_lowCaves; //低い洞窟たち
	int m_highCaveCnt; //高い洞窟の配置数
	int m_lowCaveCnt; //低い洞窟の配置数
	
	public void addKillCnt()
	{
		m_killCnt++;
	}
	
	public void addStg(int add)
	{
		setStg(m_stgIdx + add);
	}
	
	public void clear()
	{
		m_isEndStg = false;
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
	
	public String getBgmName()
	{
		return m_bgmName;
	}
	
	public String getInTexName()
	{
		return m_inTexName;
	}
	
	public String getOutTexName()
	{
		return m_outTexName;
	}
	
	public float getPlayingTime()
	{
		return m_playingTime;
	}
	
	public int getPrice(int item)
	{
		return m_stg.rounds[m_roundIdx].getPrice(item);
	}
	
	//現在のRoundを取得する.
	private MyRound getRound()
	{
		return m_stg.rounds[m_roundIdx];
	}
	
	public int getRoundMax()
	{
		return m_stg.roundCnt;
	}
	
	public int getRoundIdx()
	{
		return m_roundIdx;
	}
	
	public String getSelectTexName()
	{
		return m_selectTexName;
	}
	
	//現在のSquadを取得する.
	private MySquad getSquad()
	{
		return m_stg.rounds[m_roundIdx].squads[m_squadIdx];
	}
	
	public int getStgIdx()
	{
		return m_stgIdx;
	}
	
	public boolean isEndStg()
	{
		return m_isEndStg;
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
		int roundIdx = m_roundIdx + 1;
		if(roundIdx < m_stg.roundCnt)
		{//まだラウンドがある
			setRound(roundIdx);
			return true;
		}
		//もうラウンドない
		m_isEndStg = true;
		return false;
	}
	
	public void setRound(int roundIdx)
	{
		clearBullets();
		clearProps();
		m_roundIdx = roundIdx;
		m_squadIdx = 0;
		m_playingTime = 0f;
		m_killCnt = 0;
		m_spawnTimer = 0f;
		m_roundSpawnCnt = 0;
		m_squadSpawnCnt = 0;
		setupHighCaves();
		setupLowCaves();
	}
	
	public void setStg(int stgIdx)
	{
		clear();
		m_stg.setup(stgIdx);
		
		if(stgIdx < 0) stgIdx = 0;
		if(STG_CNTMAX <= stgIdx) stgIdx = STG_CNTMAX - 1;
		m_stgIdx = stgIdx;
		
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
		if(m_roundSpawnCnt < spawnCntMax)
		{
			m_spawnTimer -= deltaTime;
			int enemyCnt = m_roundSpawnCnt - m_killCnt;
			if(enemyCnt < getSquad().getSpawnLimit())
			{
				if(m_spawnTimer < 0f || enemyCnt <= 0)
				{//スポーン時間経過 or 敵数ゼロ
					//敵スポーン
					MyPtrn ptrn = getSquad().lotPtrn();
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
					m_squadSpawnCnt++;
					m_roundSpawnCnt++;
					if(getSquad().getSpawnCnt() <= m_squadSpawnCnt)
					{//現在のスカッドをスポーンし終えた
						if(m_squadIdx < getRound().squadCnt - 1)
						{//現在のスカッドが最終スカッドでなければ
							m_squadIdx++;
							m_squadSpawnCnt = 0;
						}
					}
					float base = getSquad().getSpawnTimeBase();
					float add = getSquad().getSpawnTimeAdd();
					m_spawnTimer = base + add * m_squadSpawnCnt;
					m_spawnTimer = Math.max(m_spawnTimer, 0.2f);
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
		
		public void setup(int stgIdx)
		{
			clear();
			
			String tblName ="";
			switch(stgIdx)
			{
			case 1: tblName = "tbl01_stg01.csv"; break;
			case 2: tblName = "tbl01_stg02.csv"; break;
			case 3: tblName = "tbl01_stg03.csv"; break;
			case 4: tblName = "tbl01_stg04.csv"; break;
			case 5: tblName = "tbl01_stg05.csv"; break;
			default: tblName = "tbl01_stg00.csv"; break;
			}
			JgfTbl tbl = m_main.asset.getTbl(tblName);
			
			roundCnt = -1;
			highCaveCnt = 0;
			lowCaveCnt = 0;
			int squadCnt = 0;
			
			for(int r = 0; r < tbl.getRowCnt(); r++)
			{
				String cmd = tbl.getCellS(r, "cmd");
				String arg0 = tbl.getCellS(r, "arg0");
				String arg1 = tbl.getCellS(r, "arg1");
				String arg2 = tbl.getCellS(r, "arg2");
				String arg3 = tbl.getCellS(r, "arg3");
				if(roundCnt < 0)
				{
					switch(cmd)
					{
					case "bgm":
						m_bgmName = arg0;
						break;
					case "high":
						float angle = Float.valueOf(arg0);
						float dist = Float.valueOf(arg1);
						highCaveLayouts[highCaveCnt].set(angle, dist);
						highCaveCnt++;
						break;
					case "in":
						m_inTexName = arg0;
						break;
					case "low":
						angle = Float.valueOf(arg0);
						dist = Float.valueOf(arg1);
						lowCaveLayouts[lowCaveCnt].set(angle, dist);
						lowCaveCnt++;
						break;
					case "out":
						m_outTexName = arg0;
						break;
					case "round":
						roundCnt++;
						squadCnt = 0;
						break;
					case "select":
						m_selectTexName = arg0;
						break;
					}
				}
				else
				{
					switch(cmd)
					{
					case "high":
						int pick = Integer.valueOf(arg0);
						rounds[roundCnt].setHighCavePick(pick);
						break;
					case "low":
						pick = Integer.valueOf(arg0);
						rounds[roundCnt].setLowCavePick(pick);
						break;
					case "prices":
						int heart = Integer.valueOf(arg0);
						int gloves = Integer.valueOf(arg1);
						int boots = Integer.valueOf(arg2);
						int skill = Integer.valueOf(arg3);
						rounds[roundCnt].setPrices(heart, gloves, boots, skill);
						break;
					case "ptrn":
						int enemy = MyEnemy.kType_None;
						switch(arg0)
						{
						case "balloon": enemy = MyEnemy.kType_Balloon; break;
						case "bat": enemy = MyEnemy.kType_Bat; break;
						case "zombie": enemy = MyEnemy.kType_Zombie; break;
						}
						int address = kAddress_None;
						switch(arg1)
						{
						case "high": address = kAddress_HighCave; break;
						case "low": address = kAddress_LowCave; break;
						}
						float prob = Float.valueOf(arg2);
						rounds[roundCnt].squads[squadCnt-1].addPtrn(enemy, address, prob);
						break;
					case "round":
						roundCnt++;
						squadCnt = 0;
						break;
					case "squad":
						int spawnCnt = Integer.valueOf(arg0);
						int spawnLimit = Integer.valueOf(arg1);
						float spawnTimeBase = Float.valueOf(arg2);
						float spawnTimeAdd = Float.valueOf(arg3);
						rounds[roundCnt].addSpawnCntMax(spawnCnt);
						rounds[roundCnt].addSquadCnt(1);
						rounds[roundCnt].squads[squadCnt].setSpawnCnt(spawnCnt, spawnLimit);
						rounds[roundCnt].squads[squadCnt].setSpawnTime(spawnTimeBase, spawnTimeAdd);
						squadCnt++;
						break;
					}
				}
			}
			roundCnt++;
		}
	}
	
	private class MyRound
	{
		static final int SQUAD_CNTMAX = 8;
		static final int ITEM_CNTMAX = 4;
		
		int m_highCavePick;
		int m_lowCavePick;
		int m_spawnCntMax;
		public MySquad[] squads;
		public int squadCnt;
		int[] m_prices;
		
		public MyRound()
		{
			m_highCavePick = 0;
			m_lowCavePick = 0;
			m_spawnCntMax = 0;
			m_prices = new int[ITEM_CNTMAX];
			squads = new MySquad[SQUAD_CNTMAX];
			for(int i = 0; i < SQUAD_CNTMAX; i++)
			{
				squads[i] = new MySquad();
			}
			clear();
		}
		
		public void addSpawnCntMax(int add)
		{
			m_spawnCntMax += add;
		}
		
		public void addSquadCnt(int add)
		{
			squadCnt += add;
		}
		
		public void clear()
		{
			m_highCavePick = 0;
			m_lowCavePick = 0;
			m_spawnCntMax = 0;
			for(int i = 0; i < ITEM_CNTMAX; i++)
			{
				m_prices[i] = 0;
			}
			for(int i = 0; i < SQUAD_CNTMAX; i++)
			{
				squads[i].clear();
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
		
		void setHighCavePick(int highCavePick)
		{
			m_highCavePick = highCavePick;
		}
		
		void setLowCavePick(int lowCavePick)
		{
			m_lowCavePick = lowCavePick;
		}
		
		void setPrices(int price0, int price1, int price2, int price3)
		{
			m_prices[0] = price0;
			m_prices[1] = price1;
			m_prices[2] = price2;
			m_prices[3] = price3;
		}
	}
	
	class MySquad
	{
		static final int PTRN_CNTMAX = 4;
		
		int m_ptrnCnt;
		MyPtrn[] m_ptrns;
		int m_spawnCnt;
		int m_spawnLimit;
		float m_spawnTimeBase;
		float m_spawnTimeAdd;
		
		public MySquad()
		{
			m_ptrnCnt = 0;
			m_ptrns = new MyPtrn[PTRN_CNTMAX];
			for(int i = 0; i < PTRN_CNTMAX; i++)
			{
				m_ptrns[i] = new MyPtrn();
			}
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
			for(int i = 0; i < m_ptrnCnt; i++)
			{
				m_ptrns[i].clear();
			}
			m_ptrnCnt = 0;
			m_spawnCnt = 0;
			m_spawnLimit = 0;
		}
		
		public int getSpawnCnt()
		{
			return m_spawnCnt;
		}
		
		public int getSpawnLimit()
		{
			return m_spawnLimit;
		}
		
		public float getSpawnTimeAdd()
		{
			return m_spawnTimeAdd;
		}
		
		public float getSpawnTimeBase()
		{
			return m_spawnTimeBase;
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
		
		public void setSpawnCnt(int spawnCnt, int spawnLimit)
		{
			m_spawnCnt = spawnCnt;
			m_spawnLimit = spawnLimit;
		}
		
		public void setSpawnTime(float base, float add)
		{
			m_spawnTimeBase = base;
			m_spawnTimeAdd = add;
		}
	}
	
	class MyPtrn
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
	
	class MyLayout
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
