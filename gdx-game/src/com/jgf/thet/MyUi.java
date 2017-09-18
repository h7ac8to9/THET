package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.utils.*;

public class MyUi
{
	static private final int PLAYER_BTN_CNTMAX = 4;
	static private final int TXT_CNTMAX = 32;
	static private final int VERF_CNTMAX = 4;
	
	MyMain m_main;
	int m_mainState;
	int m_mainStateStep;
	float[] m_stateVarsF;
	Sprite m_heart;
	Sprite m_jem;
	Sprite m_gloves;
	Sprite m_boots;
	JgfBtn m_okBtn;
	JgfBtn[] m_nextBtns;
	JgfBtn[] m_moveBtns1P;
	JgfBtn[] m_moveBtns2P;
	JgfBtn[] m_playerBtns1P;
	JgfBtn[] m_playerBtns2P;
	JgfBtn m_skillBtn1P;
	JgfBtn m_skillBtn2P;
	int m_playerSel1P;
	int m_playerSel2P;
	MyTxt[] m_txts;
	int m_txtCnt;
	
	private void buyItem(int playerIdx, int itemIdx)
	{
			int price = m_main.level.getPrice(itemIdx);
			switch(itemIdx)
			{
			case 0: //ハート
				m_main.players[playerIdx].buyHeart(price);
				break;
			case 1: //グローブ
				m_main.players[playerIdx].buyGloves(price);
				break;
			case 2: //ブーツ
				m_main.players[playerIdx].buyBoots(price);
				break;
			case 3: //スキル
				m_main.players[playerIdx].buySkill(price);
				break;
			}
	}
	
	private boolean canBuyItem(int playerIdx, int itemIdx)
	{
		//買うアイテムがない
		if(itemIdx < 0) return false;
		
		int price = m_main.level.getPrice(itemIdx);
		int gemCnt = m_main.players[playerIdx].getGemCnt();
		if(gemCnt < price) return false;
		
		return true;
	}
	
	private void clearTxts()
	{
		for(int i = 0; i < TXT_CNTMAX; i++)
		{
			m_txts[i].clear();
		}
		m_txtCnt = 0;
	}
	
	public void draw(SpriteBatch batch)
	{
		switch(m_main.getState())
		{
		case MyMain.kState_PlayerSel: drawPlayerSel(batch); break;
		case MyMain.kState_Ready: drawReady(batch); break;
		case MyMain.kState_Play: drawPlay(batch); break;
		case MyMain.kState_Interval: drawInterval(batch); break;
		case MyMain.kState_Result: drawResult(batch); break;
		case MyMain.kState_GameOver: drawGameOver(batch); break;
		case MyMain.kState_Shop: drawShop((batch)); break;
		}
	}
	
	private void drawPlayerSel(SpriteBatch batch)
	{
		//以下はステージ移動中は描かない
		if(2 <= m_mainStateStep) return;
		
		for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
		{
			if(m_playerSel1P == i)
			{
				m_playerBtns1P[i].getSprite().setColor(Color.YELLOW);
				m_playerBtns1P[i].getSprite().setScale(1.4f);
			}
			else
			{
				m_playerBtns1P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns1P[i].getSprite().setScale(1f);
			}
			m_playerBtns1P[i].getSprite().draw(batch);
			if(m_playerSel2P == i)
			{
				m_playerBtns2P[i].getSprite().setColor(Color.YELLOW);
				m_playerBtns2P[i].getSprite().setScale(1.4f);
			}
			else
			{
				m_playerBtns2P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns2P[i].getSprite().setScale(1f);
			}
			m_playerBtns2P[i].getSprite().draw(batch);
		}
		
		
		
		m_okBtn.getSprite().draw(batch);
		int stgIdx = m_main.level.getStgIdx();
		if(0 < stgIdx)
		{
			m_nextBtns[0].getSprite().draw(batch);
			if(m_nextBtns[0].isPressed())
			{
				m_nextBtns[0].getSprite().setColor(Color.YELLOW);
				m_nextBtns[0].getSprite().setScale(1.1f);
			}
			else
			{
				m_nextBtns[0].getSprite().setColor(Color.BLACK);
				m_nextBtns[0].getSprite().setScale(1f);
			}
		}
		if(stgIdx < MyLevel.STG_CNTMAX - 1)
		{
			m_nextBtns[1].getSprite().draw(batch);
			if(m_nextBtns[1].isPressed())
			{
				m_nextBtns[1].getSprite().setColor(Color.YELLOW);
				m_nextBtns[1].getSprite().setScale(1.1f);
			}
			else
			{
				m_nextBtns[1].getSprite().setColor(Color.BLACK);
				m_nextBtns[1].getSprite().setScale(1f);
			}
		}
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawReady(SpriteBatch batch)
	{
		//プレイ中のUIも描く
		drawPlayCmn(batch);
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawPlay(SpriteBatch batch)
	{
		//プレイ中のUIも描く
		drawPlayCmn(batch);
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawPlayCmn(SpriteBatch batch)
	{//プレイ中のUIを描く(テキスト以外)
		//if(false)
		{
			for(int i = 0; i < 2; i++)
			{
				if(m_moveBtns1P[i].isPressed())
				{
					m_moveBtns1P[i].getSprite().setColor(Color.YELLOW);
					m_moveBtns1P[i].getSprite().setScale(1.1f);
				}
				else
				{
					m_moveBtns1P[i].getSprite().setColor(Color.WHITE);
					m_moveBtns1P[i].getSprite().setScale(1f);
				}
				m_moveBtns1P[i].getSprite().draw(batch);
				if(m_moveBtns2P[i].isPressed())
				{
					m_moveBtns2P[i].getSprite().setColor(Color.YELLOW);
					m_moveBtns2P[i].getSprite().setScale(1.1f);
				}
				else
				{
					m_moveBtns2P[i].getSprite().setColor(Color.WHITE);
					m_moveBtns2P[i].getSprite().setScale(1f);
				}
				m_moveBtns2P[i].getSprite().draw(batch);
			}
			if(m_main.players[0].getSkillCnt() == 0)
			{
				m_skillBtn1P.getSprite().setColor(Color.DARK_GRAY);
				m_skillBtn1P.getSprite().setScale(1f);
			}
			else if(m_skillBtn1P.isPressed())
			{
				m_skillBtn1P.getSprite().setColor(Color.YELLOW);
				m_skillBtn1P.getSprite().setScale(1.1f);
			}
			else
			{
				m_skillBtn1P.getSprite().setColor(Color.WHITE);
				m_skillBtn1P.getSprite().setScale(1f);
			}
			m_skillBtn1P.getSprite().draw(batch);
			if(m_main.players[1].getSkillCnt() == 0)
			{
				m_skillBtn2P.getSprite().setColor(Color.DARK_GRAY);
				m_skillBtn2P.getSprite().setScale(1f);
			}
			else if(m_skillBtn2P.isPressed())
			{
				m_skillBtn2P.getSprite().setColor(Color.YELLOW);
				m_skillBtn2P.getSprite().setScale(1.1f);
			}
			else
			{
				m_skillBtn2P.getSprite().setColor(Color.WHITE);
				m_skillBtn2P.getSprite().setScale(1f);
			}
			m_skillBtn2P.getSprite().draw(batch);
		}
		{
			final float size = 0.05f;
			m_heart.setSize(size, size);
			m_heart.setOrigin(0f, 0f);
			m_heart.setColor(Color.PINK);
			int maxHp = m_main.players[0].getMaxHp();
			int hp = m_main.players[0].getHp();
			m_heart.setRotation(0f);
			for(int i = 0; i < hp; i++)
			{
				m_heart.setPosition(-size*maxHp/2f+size*i, -0.6f);
				m_heart.draw(batch);
			}
			maxHp = m_main.players[1].getMaxHp();
			hp = m_main.players[1].getHp();
			m_heart.setRotation(180f);
			for(int i = 0; i < hp; i++)
			{
				m_heart.setPosition(+size*maxHp/2f-size*i, +0.6f);
				m_heart.draw(batch);
			}
		}
		{
			final float size = 0.09f;
			m_jem.setSize(size, size);
			m_jem.setOrigin(size/2f, size/2f);
			m_jem.setColor(Color.ORANGE);
			m_jem.setRotation(0f);
			m_jem.setPosition(-size/2f, -0.65f-size/2f);
			m_jem.draw(batch);
			m_jem.setRotation(180f);
			m_jem.setPosition(-size/2f, +0.65f-size/2f);
			m_jem.draw(batch);
		}
		{
			final float size = 0.09f;
			m_gloves.setSize(size, size);
			m_gloves.setOrigin(size/2f, size/2f);
			m_gloves.setColor(Color.ORANGE);
			m_gloves.setRotation(0f);
			m_gloves.setPosition(-size/2f, -0.65f-size/2f);
			m_gloves.draw(batch);
			m_gloves.setRotation(180f);
			m_gloves.setPosition(-size/2f, +0.65f-size/2f);
			m_gloves.draw(batch);
		}
	}
	
	private void drawInterval(SpriteBatch batch)
	{
		//プレイ中のUIも描く
		drawPlayCmn(batch);
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawResult(SpriteBatch batch)
	{
		//プレイ中のUIも描く
		drawPlayCmn(batch);
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawGameOver(SpriteBatch batch)
	{
		//プレイ中のUIも描く
		drawPlayCmn(batch);
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawShop(SpriteBatch batch)
	{
		//プレイ中のUIも描く
		drawPlayCmn(batch);
		//アイテムのボタンを描く
		for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
		{
			if(m_playerSel1P == i)
			{
				m_playerBtns1P[i].getSprite().setColor(Color.YELLOW);
				m_playerBtns1P[i].getSprite().setScale(1.2f);
			}
			else if(!canBuyItem(0, i))
			{
				m_playerBtns1P[i].getSprite().setColor(Color.DARK_GRAY);
				m_playerBtns1P[i].getSprite().setScale(1f);
			}
			else
			{
				m_playerBtns1P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns1P[i].getSprite().setScale(1f);
			}
			m_playerBtns1P[i].getSprite().draw(batch);
			if(m_playerSel2P == i)
			{
				m_playerBtns2P[i].getSprite().setColor(Color.YELLOW);
				m_playerBtns2P[i].getSprite().setScale(1.2f);
			}
			else if(!canBuyItem(1, i))
			{
				m_playerBtns2P[i].getSprite().setColor(Color.DARK_GRAY);
				m_playerBtns2P[i].getSprite().setScale(1f);
			}
			else
			{
				m_playerBtns2P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns2P[i].getSprite().setScale(1f);
			}
			m_playerBtns2P[i].getSprite().draw(batch);
		}
		m_okBtn.getSprite().draw(batch);
		//アイテムのジェムを描く
		final float size = 0.13f;
		final float margin = 0.07f;
		final float posY = 0.25f;
		final float jemSize = 0.06f;
		m_jem.setSize(jemSize, jemSize);
		m_jem.setOrigin(jemSize/2f, jemSize/2f);
		m_jem.setColor(Color.ORANGE);
		m_jem.setRotation(0f);
		m_jem.setPosition(-size*1.5f-margin*1.5f-jemSize/2f, -posY-jemSize);
		m_jem.draw(batch);
		m_jem.setPosition(-size*0.5f-margin*0.5f-jemSize/2f, -posY-jemSize);
		m_jem.draw(batch);
		m_jem.setPosition(+size*0.5f+margin*0.5f-jemSize/2f, -posY-jemSize);
		m_jem.draw(batch);
		m_jem.setPosition(+size*1.5f+margin*1.5f-jemSize/2f, -posY-jemSize);
		m_jem.draw(batch);
		m_jem.setRotation(180f);
		m_jem.setPosition(-size*1.5f-margin*1.5f-jemSize/2f, +posY);
		m_jem.draw(batch);
		m_jem.setPosition(-size*0.5f-margin*0.5f-jemSize/2f, +posY);
		m_jem.draw(batch);
		m_jem.setPosition(+size*0.5f+margin*0.5f-jemSize/2f, +posY);
		m_jem.draw(batch);
		m_jem.setPosition(+size*1.5f+margin*1.5f-jemSize/2f, +posY);
		m_jem.draw(batch);
		//テキストを描く
		drawTxts(batch);
	}
	
	private void drawTxts(SpriteBatch batch)
	{
		if(m_txtCnt <= 0) return;

		//現在のMatrixをとっておく
		Matrix4 matProjPrev = batch.getProjectionMatrix().cpy();
		Matrix4 matTransPrev = batch.getTransformMatrix().cpy();
		
		//Matrixを一時的に変える
		Matrix4 matProj = new Matrix4();
		matProj.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setProjectionMatrix(matProj);
		Vector2 screen;
		Matrix4 matTrans = new Matrix4();
		BitmapFont font = m_main.log.getFont();
		
		for(int i = 0; i < m_txtCnt; i++)
		{
			//font
			font.setColor(m_txts[i].color);
			font.setScale(m_txts[i].scale);
			BitmapFont.TextBounds bnds = font.getBounds(m_txts[i].str);
			int type = m_txts[i].type;
			
			if(type == MyTxt.kType_1P2P || type == MyTxt.kType_1P)
			{//1P
				screen = m_main.gameCam.WorldToScreen(+m_txts[i].pos.x, +m_txts[i].pos.y);
				screen.x -= bnds.width / 2;
				screen.y -= bnds.height / 2; //いらないかも
				matTrans.idt();
				matTrans.trn(screen.x, screen.y, 0f);
				batch.setTransformMatrix(matTrans);
				font.draw(batch, m_txts[i].str, 0f, 0f);
			}
			
			if(type == MyTxt.kType_1P2P || type == MyTxt.kType_2P)
			{//2P
				screen = m_main.gameCam.WorldToScreen(-m_txts[i].pos.x, -m_txts[i].pos.y);
				screen.x += bnds.width / 2;
				screen.y += bnds.height / 2;
				matTrans.idt();
				matTrans.rotate(new Vector3(0f, 0f, 1f), 180f);
				matTrans.trn(screen.x, screen.y, 0f);
				batch.setTransformMatrix(matTrans);
				font.draw(batch, m_txts[i].str, 0f, 0f);
			}
		}

		//Matrixを元に戻す
		batch.setTransformMatrix(matTransPrev);
		batch.setProjectionMatrix(matProjPrev);
	}
	
	public JgfBtn getMoveBtn(int playerIdx, int lr)
	{
		switch(playerIdx)
		{
		case 0: return m_moveBtns1P[lr];
		case 1: return m_moveBtns2P[lr];
		}
		return null;
	}
	
	public JgfBtn getSkillBtn(int playerIdx)
	{
		switch(playerIdx)
		{
			case 0: return m_skillBtn1P;
			case 1: return m_skillBtn2P;
		}
		return null;
	}
	
	public MyUi()
	{
		m_main = MyGame.getMain();
		m_stateVarsF = new float[VERF_CNTMAX];
		m_txts = new MyTxt[TXT_CNTMAX];
		for(int i = 0; i < TXT_CNTMAX; i++)
		{
			m_txts[i] = new MyTxt();
		}
	}
	
	public void setup()
	{
		m_mainState = MyMain.kState_None;
		m_mainStateStep = 0;
		for(int i = 0; i < VERF_CNTMAX; i++)
		{
			m_stateVarsF[i] = 0f;
		}
		
		float size = 0f;
		
		Texture tex = m_main.asset.getTex("heart00.png");
		m_heart = new Sprite(tex);
		tex = m_main.asset.getTex("icon00_jem00.png");
		m_jem = new Sprite(tex);
		tex = m_main.asset.getTex("icon00_gloves00.png");
		m_gloves = new Sprite(tex);
		tex = m_main.asset.getTex("icon00_boots00.png");
		m_boots = new Sprite(tex);
		
		size = 0.25f;
		tex = m_main.asset.getTex("btn01_start00.png");
		m_okBtn = new JgfBtn();
		m_okBtn.setSprite(new Sprite(tex));
		m_okBtn.getSprite().setSize(size, size);
		m_okBtn.getSprite().setOrigin(size/2f, size/2f);
		m_okBtn.getSprite().setColor(Color.BLACK);
		m_okBtn.getSprite().setPosition(-size/2f, -size/2f);
		
		float x = 0.35f;
		size = 0.15f;
		tex = m_main.asset.getTex("btn01_next00.png");
		m_nextBtns = new JgfBtn[2];
		for(int i = 0; i < 2; i++)
		{
			m_nextBtns[i] = new JgfBtn();
			m_nextBtns[i].setSprite(new Sprite(tex));
			m_nextBtns[i].getSprite().setSize(size, size);
			m_nextBtns[i].getSprite().setOrigin(size/2f, size/2f);
			m_nextBtns[i].getSprite().setColor(Color.BLACK);
		}
		m_nextBtns[0].getSprite().setPosition(-x-size/2f, -size/2f);
		m_nextBtns[0].getSprite().setFlip(true, false);
		m_nextBtns[1].getSprite().setPosition(+x-size/2f, -size/2f);
		
		m_moveBtns1P = new JgfBtn[2];
		m_moveBtns2P = new JgfBtn[2];
		size = 0.35f;
		x = 0.5f;
		float y = m_main.gameCam.getSize().y / 2f;
		tex = m_main.asset.getTex("btn00_move00.png");
		for(int i = 0; i < 2; i++)
		{
			m_moveBtns1P[i] = new JgfBtn();
			m_moveBtns1P[i].setSprite(new Sprite(tex));
			m_moveBtns1P[i].getSprite().setSize(size, size);
			m_moveBtns1P[i].getSprite().setOrigin(size/2f, size/2f);
			m_moveBtns1P[i].getSprite().setColor(Color.WHITE);
			m_moveBtns2P[i] = new JgfBtn();
			m_moveBtns2P[i].setSprite(new Sprite(tex));
			m_moveBtns2P[i].getSprite().setSize(size, size);
			m_moveBtns2P[i].getSprite().setOrigin(size/2f, size/2f);
			m_moveBtns2P[i].getSprite().setColor(Color.WHITE);
		}
		m_moveBtns1P[0].getSprite().setPosition(-x, -y);
		m_moveBtns1P[1].getSprite().setFlip(true, false);
		m_moveBtns1P[1].getSprite().setPosition(+x-size, -y);
		m_moveBtns2P[1].getSprite().setFlip(false, true);
		m_moveBtns2P[1].getSprite().setPosition(-x, +y-size);
		m_moveBtns2P[0].getSprite().setFlip(true, true);
		m_moveBtns2P[0].getSprite().setPosition(+x-size, +y-size);
		
		m_playerBtns1P = new JgfBtn[PLAYER_BTN_CNTMAX];
		m_playerBtns2P = new JgfBtn[PLAYER_BTN_CNTMAX];
		for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
		{
			m_playerBtns1P[i] = new JgfBtn();
			m_playerBtns2P[i] = new JgfBtn();
		}
		
		m_skillBtn1P = new JgfBtn();
		m_skillBtn2P = new JgfBtn();
		
		clearTxts();
	}
	
	public void setEnableLevelSelect()
	{
		m_mainStateStep--;
	}
	
	public void update()
	{
		int mainState = m_main.getState();
		if(m_mainState != mainState)
		{
			m_mainState = mainState;
			m_mainStateStep = 0;
			for(int i = 0; i < VERF_CNTMAX; i++)
			{
				m_stateVarsF[i] = 0f;
			}
			clearTxts();
		}
		switch(m_main.getState())
		{
		case MyMain.kState_PlayerSel: updatePlayerSel(); break;
		case MyMain.kState_Ready: updateReady(); break;
		case MyMain.kState_Play: updatePlay(); break;
		case MyMain.kState_Interval: updateInterval(); break;
		case MyMain.kState_Result: updateResult(); break;
		case MyMain.kState_GameOver: updateGameOver(); break;
		case MyMain.kState_Shop: updateShop(); break;
		}
	}
	
	private void updatePlayerSel()
	{
		int iTxt = 0;
		
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			Texture tex = m_main.asset.getTex("archer01.png");
			m_playerBtns1P[0].setSprite(new Sprite(tex));
			m_playerBtns2P[0].setSprite(new Sprite(tex));
			tex = m_main.asset.getTex("knight01.png");
			m_playerBtns1P[1].setSprite(new Sprite(tex));
			m_playerBtns2P[1].setSprite(new Sprite(tex));
			tex = m_main.asset.getTex("mage01.png");
			m_playerBtns1P[2].setSprite(new Sprite(tex));
			m_playerBtns2P[2].setSprite(new Sprite(tex));
			tex = m_main.asset.getTex("ninja01.png");
			m_playerBtns1P[3].setSprite(new Sprite(tex));
			m_playerBtns2P[3].setSprite(new Sprite(tex));
			m_playerSel1P = 0;
			m_playerSel2P = 0;
			final float size = 0.13f;
			final float margin = 0.07f;
			final float posY = 0.7f;
			//1Pボタンの初期化
			float x = -(size * 2f + margin * 1.5f);
			float y = -posY;
			for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
			{
				m_playerBtns1P[i].getSprite().setOrigin(size/2f, size/2f);
				m_playerBtns1P[i].getSprite().setSize(size, size);
				m_playerBtns1P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns1P[i].getSprite().setRotation(0f);
				m_playerBtns1P[i].getSprite().setPosition(x, y);
				x += size + margin;
			}
			//2Pボタンの初期化
			x = -(size * 2f + margin * 1.5f);
			y = posY - size;
			for(int i = PLAYER_BTN_CNTMAX-1; 0 <= i; i--)
			{
				m_playerBtns2P[i].getSprite().setOrigin(size/2f, size/2f);
				m_playerBtns2P[i].getSprite().setSize(size, size);
				m_playerBtns2P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns2P[i].getSprite().setRotation(180f);
				m_playerBtns2P[i].getSprite().setPosition(x, y);
				x += size + margin;
			}
			//ラウンド数
			I18NBundle bdl = m_main.asset.getBdl("bdl");
			int roundMax = m_main.level.getRoundMax();
			m_txts[iTxt].str = bdl.format("RoundCnt", roundMax);
			m_txts[iTxt].scale = 1.5f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.2f;
			iTxt++;
			m_txtCnt = iTxt;
			break;
		case 1:
			//押されてるボタンのチェック
			for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
			{
				m_playerBtns1P[i].update();
				if(m_playerBtns1P[i].isPush())
				{
					m_playerSel1P = i;
				}
				m_playerBtns2P[i].update();
				if(m_playerBtns2P[i].isPush())
				{
					m_playerSel2P = i;
				}
			}
			m_okBtn.update();
			m_nextBtns[0].update();
			m_nextBtns[1].update();
			boolean isChangedStg = false;
			if(m_okBtn.isPush())
			{//OKボタンが押された
				m_main.setState(MyMain.kState_Ready);
				Texture skillTex;
				float skillSizeX = 0.3f;
				float skillSizeY = 0.15f;
				float skillY = m_main.gameCam.getSize().y / 2f;
				switch(m_playerSel1P)
				{
					case 0:
						m_main.players[0].setupType(MyPlayer.kType_Archer);
						skillTex = m_main.asset.getTex("btn00_skills00.png");
						break;
					case 1:
						m_main.players[0].setupType(MyPlayer.kType_Knight);
						skillTex = m_main.asset.getTex("btn00_skillf00.png");
						break;
					case 2:
						m_main.players[0].setupType(MyPlayer.kType_Mage);
						skillTex = m_main.asset.getTex("btn00_skillm00.png");
						break;
					default:
						m_main.players[0].setupType(MyPlayer.kType_Ninja);
						skillTex = m_main.asset.getTex("btn00_skilln00.png");
						break;
				}
				m_main.players[0].setPos(0f, -0.45f);
				m_main.players[0].setRot(0f);
				m_skillBtn1P.setSprite(new Sprite(skillTex));
				m_skillBtn1P.getSprite().setSize(skillSizeX, skillSizeY);
				m_skillBtn1P.getSprite().setOrigin(skillSizeX/2f, skillSizeY/2f);
				m_skillBtn1P.getSprite().setColor(Color.WHITE);
				m_skillBtn1P.getSprite().setFlip(false, false);
				m_skillBtn1P.getSprite().setPosition(-skillSizeX/2f, -skillY);
				switch(m_playerSel2P)
				{
					case 0:
						m_main.players[1].setupType(MyPlayer.kType_Archer);
						skillTex = m_main.asset.getTex("btn00_skills00.png");
						break;
					case 1:
						m_main.players[1].setupType(MyPlayer.kType_Knight);
						skillTex = m_main.asset.getTex("btn00_skillf00.png");
						break;
					case 2:
						m_main.players[1].setupType(MyPlayer.kType_Mage);
						skillTex = m_main.asset.getTex("btn00_skillm00.png");
						break;
					default:
						m_main.players[1].setupType(MyPlayer.kType_Ninja);
						skillTex = m_main.asset.getTex("btn00_skilln00.png");
						break;
				}
				m_main.players[1].setPos(0f, +0.45f);
				m_main.players[1].setRot(180f);
				m_skillBtn2P.setSprite(new Sprite(skillTex));
				m_skillBtn2P.getSprite().setSize(skillSizeX, skillSizeY);
				m_skillBtn2P.getSprite().setOrigin(skillSizeX/2f, skillSizeY/2f);
				m_skillBtn2P.getSprite().setColor(Color.WHITE);
				m_skillBtn2P.getSprite().setFlip(false, true);
				m_skillBtn2P.getSprite().setPosition(-skillSizeX/2f, +skillY-skillSizeY);
			}
			else if(m_nextBtns[0].isPush())
			{
				if(0 < m_main.level.getStgIdx())
				{
					m_mainStateStep++;
					m_main.level.addStg(-1);
					isChangedStg = true;
					m_main.bg.setState(MyBg.kState_MoveL);
				}
			}
			else if(m_nextBtns[1].isPush())
			{
				if(m_main.level.getStgIdx() < MyLevel.STG_CNTMAX - 1)
				{
					m_mainStateStep++;
					m_main.level.addStg(+1);
					isChangedStg = true;
					m_main.bg.setState(MyBg.kState_MoveR);
				}
			}
			if(isChangedStg)
			{
				//ラウンド数
				iTxt = 0;
				bdl = m_main.asset.getBdl("bdl");
				roundMax = m_main.level.getRoundMax();
				m_txts[iTxt].str = bdl.format("RoundCnt", roundMax);
				m_txts[iTxt].scale = 1.5f;
				m_txts[iTxt].pos.x = 0f;
				m_txts[iTxt].pos.y = -0.2f;
			}
			break;
		case 2:
			break;
		}
		
	}
	
	private void updateReady()
	{
		int iTxt = updatePlayCmn();
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			I18NBundle bdl = m_main.asset.getBdl("bdl");
			int roundNum = m_main.level.getRoundIdx() + 1;
			int roundMax = m_main.level.getRoundMax();
			m_txts[iTxt].str = bdl.format("RoundStart", roundNum, roundMax);
			m_txts[iTxt].scale = 1.5f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.2f;
			iTxt++;
			m_txtCnt = iTxt;
			break;
		}
	}
	
	private void updatePlay()
	{
		int iTxt = updatePlayCmn();
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			m_txtCnt = iTxt;
			break;
		}
	}
	
	private int updatePlayCmn()
	{
		int iTxt = 0;
		switch(m_mainStateStep)
		{
		case 0:
			m_txts[iTxt].type = MyTxt.kType_1P;
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.62f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			m_txts[iTxt].type = MyTxt.kType_2P;
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.62f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			float skillY = -m_main.gameCam.getSize().y / 2f + 0.075f;
			m_txts[iTxt].type = MyTxt.kType_1P;
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = 0.1f;
			m_txts[iTxt].pos.y = skillY;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			m_txts[iTxt].type = MyTxt.kType_2P;
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = 0.1f;
			m_txts[iTxt].pos.y = skillY;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			break;
		}
		//ジェム数の更新
		iTxt = 0;
		int gem = m_main.players[0].getGemCnt();
		m_txts[iTxt].str = String.format("%03d", gem);
		iTxt++;
		gem = m_main.players[1].getGemCnt();
		m_txts[iTxt].str = String.format("%03d", gem);
		iTxt++;
		//スキル数の更新
		int skillCnt = m_main.players[0].getSkillCnt();
		m_txts[iTxt].str = String.format("%02d", skillCnt);
		iTxt++;
		skillCnt = m_main.players[1].getSkillCnt();
		m_txts[iTxt].str = String.format("%02d", skillCnt);
		iTxt++;
		//ボタン
		m_moveBtns1P[0].update();
		m_moveBtns1P[1].update();
		m_moveBtns2P[0].update();
		m_moveBtns2P[1].update();
		m_skillBtn1P.update();
		m_skillBtn2P.update();
		return iTxt;
	}
	
	private void updateInterval()
	{
		int iTxt = updatePlayCmn();
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			I18NBundle bdl = m_main.asset.getBdl("bdl");
			int roundNum = m_main.level.getRoundIdx();
			int roundMax = m_main.level.getRoundMax();
			m_txts[iTxt].str = bdl.format("RoundCleared");
			m_txts[iTxt].scale = 1.5f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.2f;
			iTxt++;
			m_txtCnt = iTxt;
			break;
		}
	}
	
	private void updateResult()
	{
		int iTxt = updatePlayCmn();
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			I18NBundle bdl = m_main.asset.getBdl("bdl");
			m_txts[iTxt].str = bdl.format("LevelCleared");
			m_txts[iTxt].scale = 1.5f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.2f;
			iTxt++;
			m_txtCnt = iTxt;
			break;
		}
	}
	
	private void updateGameOver()
	{
		int iTxt = updatePlayCmn();
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			String time = String.valueOf((int)m_main.level.getPlayingTime());
			m_main.log.print("time = " + time);
			I18NBundle bdl = m_main.asset.getBdl("bdl");
			int roundNum = m_main.level.getRoundIdx() + 1;
			int roundMax = m_main.level.getRoundMax();
			m_txts[iTxt].str = bdl.format("RoundStart", roundNum, roundMax);
			m_txts[iTxt].scale = 1.5f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.15f;
			iTxt++;
			m_txts[iTxt].str = bdl.format("GameOver");
			m_txts[iTxt].scale = 1.5f;
			m_txts[iTxt].pos.x = 0f;
			m_txts[iTxt].pos.y = -0.2f;
			iTxt++;
			m_txtCnt = iTxt;
			break;
		}
	}
	
	private void updateShop()
	{
		final float size = 0.13f;
		final float margin = 0.07f;
		final float posY = 0.25f;
		int iTxt = updatePlayCmn();
		int lv = 0;
		switch(m_mainStateStep)
		{
		case 0:
			m_mainStateStep++;
			I18NBundle bdl = m_main.asset.getBdl("bdl");
			lv = m_main.players[0].getGlovesCnt();
			m_txts[iTxt].type = MyTxt.kType_1P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = -(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			lv = m_main.players[1].getGlovesCnt();
			m_txts[iTxt].type = MyTxt.kType_2P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = -(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			lv = m_main.players[0].getBootsCnt();
			m_txts[iTxt].type = MyTxt.kType_1P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = +(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			lv = m_main.players[1].getBootsCnt();
			m_txts[iTxt].type = MyTxt.kType_2P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = +(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			int price = m_main.level.getPrice(0);
			m_txts[iTxt].str = String.format("%d", price);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = -(size * 1.5f + margin * 1.5f);
			m_txts[iTxt].pos.y = -posY;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			price = m_main.level.getPrice(1);
			m_txts[iTxt].str = String.format("%d", price);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = -(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			price = m_main.level.getPrice(2);
			m_txts[iTxt].str = String.format("%d", price);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = +(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			price = m_main.level.getPrice(3);
			m_txts[iTxt].str = String.format("%d", price);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = +(size * 1.5f + margin * 1.5f);
			m_txts[iTxt].pos.y = -posY;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			m_txtCnt = iTxt;
			Texture tex = m_main.asset.getTex("btn03_heart00.png");
			m_playerBtns1P[0].setSprite(new Sprite(tex));
			m_playerBtns2P[0].setSprite(new Sprite(tex));
			tex = m_main.asset.getTex("btn03_gloves00.png");
			m_playerBtns1P[1].setSprite(new Sprite(tex));
			m_playerBtns2P[1].setSprite(new Sprite(tex));
			tex = m_main.asset.getTex("btn03_boots00.png");
			m_playerBtns1P[2].setSprite(new Sprite(tex));
			m_playerBtns2P[2].setSprite(new Sprite(tex));
			String atlasName = "";
			switch(m_main.players[0].getType())
			{
				case MyPlayer.kType_Archer: atlasName = "btn03_skills00.png"; break;
				case MyPlayer.kType_Knight: atlasName = "btn03_skillf00.png"; break;
				case MyPlayer.kType_Mage: atlasName = "btn03_skillm00.png"; break;
				case MyPlayer.kType_Ninja: atlasName = "btn03_skilln00.png"; break;
			}
			tex = m_main.asset.getTex(atlasName);
			m_playerBtns1P[3].setSprite(new Sprite(tex));
			switch(m_main.players[1].getType())
			{
				case MyPlayer.kType_Archer: atlasName = "btn03_skills00.png"; break;
				case MyPlayer.kType_Knight: atlasName = "btn03_skillf00.png"; break;
				case MyPlayer.kType_Mage: atlasName = "btn03_skillm00.png"; break;
				case MyPlayer.kType_Ninja: atlasName = "btn03_skilln00.png"; break;
			}
			tex = m_main.asset.getTex(atlasName);
			m_playerBtns2P[3].setSprite(new Sprite(tex));
			m_playerSel1P = -1;
			m_playerSel2P = -1;
			//1Pボタンの初期化
			float x = -(size * 2f + margin * 1.5f);
			float y = -posY;
			for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
			{
				m_playerBtns1P[i].getSprite().setOrigin(size/2f, size/2f);
				m_playerBtns1P[i].getSprite().setSize(size, size);
				m_playerBtns1P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns1P[i].getSprite().setRotation(0f);
				m_playerBtns1P[i].getSprite().setPosition(x, y);
				x += size + margin;
			}
			//2Pボタンの初期化
			x = -(size * 2f + margin * 1.5f);
			y = posY - size;
			for(int i = PLAYER_BTN_CNTMAX-1; 0 <= i; i--)
			{
				m_playerBtns2P[i].getSprite().setOrigin(size/2f, size/2f);
				m_playerBtns2P[i].getSprite().setSize(size, size);
				m_playerBtns2P[i].getSprite().setColor(Color.WHITE);
				m_playerBtns2P[i].getSprite().setRotation(180f);
				m_playerBtns2P[i].getSprite().setPosition(x, y);
				x += size + margin;
			}
			break;
		case 1:
			//グローブとブーツのレベルを更新
			lv = m_main.players[0].getGlovesCnt();
			m_txts[iTxt].type = MyTxt.kType_1P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = -(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			lv = m_main.players[1].getGlovesCnt();
			m_txts[iTxt].type = MyTxt.kType_2P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = -(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			lv = m_main.players[0].getBootsCnt();
			m_txts[iTxt].type = MyTxt.kType_1P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = +(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			lv = m_main.players[1].getBootsCnt();
			m_txts[iTxt].type = MyTxt.kType_2P;
			m_txts[iTxt].str = String.format("Lv.%d", lv);
			m_txts[iTxt].scale = 1.0f;
			m_txts[iTxt].pos.x = +(size * 0.5f + margin * 0.5f);
			m_txts[iTxt].pos.y = -posY - 0.05f;
			m_txts[iTxt].color = Color.WHITE;
			iTxt++;
			//押されてるボタンのチェック
			m_okBtn.update();
			if(m_okBtn.isPush())
			{//OKボタンが押された
				m_main.setState(MyMain.kState_Ready);
			}
			else
			{
				if(m_playerSel1P < 0)
				{//まだ買ってない
					for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
					{
						m_playerBtns1P[i].update();
						if(m_playerBtns1P[i].isPush())
						{
							if(canBuyItem(0, i))
							{
								m_playerSel1P = i;
								m_stateVarsF[0] = 0f;
								buyItem(0, i);
								break;
							}
						}
					}
				}
				else
				{//買ってる中
					m_stateVarsF[0] += Gdx.graphics.getDeltaTime();
					if(0.5f < m_stateVarsF[0])
					{
						m_playerSel1P = -1;
					}
				}
				if(m_playerSel2P < 0)
				{//まだ買ってない
					for(int i = 0; i < PLAYER_BTN_CNTMAX; i++)
					{
						m_playerBtns2P[i].update();
						if(m_playerBtns2P[i].isPush())
						{
							if(canBuyItem(1, i))
							{
								m_playerSel2P = i;
								m_stateVarsF[1] = 0f;
								buyItem(1, i);
								break;
							}
						}
					}
				}
				else
				{//買ってる中
					m_stateVarsF[0] += Gdx.graphics.getDeltaTime();
					if(0.5f < m_stateVarsF[0])
					{
						m_playerSel2P = -1;
					}
				}
			}
			break;
		}
	}
	
	private class MyTxt
	{
		static public final int kType_1P2P = 0;
		static public final int kType_1P = 1;
		static public final int kType_2P = 2;
		
		public int type;
		public Color color;
		public float scale;
		public String str;
		public Vector2 pos;

		public void clear()
		{
			type = kType_1P2P;
			color = Color.BLACK;
			scale = 1f;
			str = "";
			pos.x = 0f;
			pos.y = 0f;
		}

		public MyTxt()
		{
			color = new Color();
			pos = new Vector2();
			clear();
		}
	}
}
