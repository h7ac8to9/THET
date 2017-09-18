package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyBg
{
	static public final int VTX_CNTMAX = 16;
	static final int STAR_CNTMAX = 32;
	
	static public final int kState_None = 0;
	static public final int kState_PlanetOut = 1;
	static public final int kState_PlanetIn = 2;
	static public final int kState_MoveL = 3;
	static public final int kState_MoveR = 4;
	static public final int kState_Stop = 5;
	
	MyMain m_main;
	
	int m_state;
	int m_stateStep;
	float m_stateTimer;
	
	Sprite m_planetIn;
	Sprite m_planetOut;
	JgfAtlas m_planetOutAtlas;
	float m_planetTimer;
	float m_planetInAlpha;
	Vector2[] vtxs;
	MyRocket[] m_rockets;
	MyStar[] m_stars;
	
	float m_planetSize;
	Vector2 m_planetPos;
	Vector2 m_planetMove;
	
	
	
	public void draw(SpriteBatch batch)
	{
		/*
		batch.end();
		ShapeRenderer m_shape = new ShapeRenderer();
		m_shape.setProjectionMatrix(batch.getProjectionMatrix());
		m_shape.begin(ShapeRenderer.ShapeType.Line);
		m_shape.setColor(Color.RED);
		for(int i = 0; i < VTX_MAXCNT-1; i++)
		{
			m_shape.line(vtxs[i].x, vtxs[i].y, vtxs[i+1].x, vtxs[i+1].y);
		}
		m_shape.line(vtxs[VTX_MAXCNT-1].x, vtxs[VTX_MAXCNT-1].y, vtxs[0].x, vtxs[0].y);
		m_shape.end();
		batch.begin();
		//*/
		
		//星の回転テスト
		/*
		float elapsed = Gdx.graphics.getDeltaTime();
		float rot = elapsed * (-2f);
		m_sprite.rotate(rot);
		//*/
		
		for(int i = 0; i < STAR_CNTMAX; i++)
		{
			m_stars[i].draw(batch);
		}
		
		{
			TextureRegion region = m_planetOutAtlas.getRegion(m_planetTimer);
			m_planetOut.setRegion(region);
			m_planetOut.draw(batch);
		}
		if(0f < m_planetInAlpha)
		{
			m_planetIn.setAlpha(m_planetInAlpha);
			m_planetIn.draw(batch);
		}
		
		for(int i = 0; i < 2; i++)
		{
			m_rockets[i].draw(batch);
		}
	}
	
	public MyBg()
	{
		m_main = JgfGame.getMain();
		
		m_planetSize = 1.07f;
		m_planetPos = new Vector2();
		m_planetPos.x = -m_planetSize / 2f;
		m_planetPos.y = -m_planetSize / 2f;
		m_planetMove = new Vector2();
		Texture tex = m_main.asset.getTex("bg01_planet00.png");
		m_planetIn = new Sprite(tex);
		m_planetIn.setSize(m_planetSize, m_planetSize);
		m_planetIn.setPosition(m_planetPos.x, m_planetPos.y);
		m_planetIn.setOrigin(m_planetSize/2f, m_planetSize/2f);
		m_planetOutAtlas = m_main.asset.getAtlas("bg00_planet00.png");
		m_planetOut = new Sprite(m_planetOutAtlas.getRegion(0f));
		m_planetOut.setSize(m_planetSize, m_planetSize);
		m_planetOut.setPosition(m_planetPos.x, m_planetPos.y);
		m_planetOut.setOrigin(m_planetSize/2f, m_planetSize/2f);
		m_planetInAlpha = 0f;
		m_planetTimer = 0f;
		
		m_rockets = new MyRocket[2];
		for(int i = 0; i < 2; i++)
		{
			m_rockets[i] = new MyRocket();
		}
		m_rockets[0].setup(-1f);
		m_rockets[1].setup(+1f);
		
		m_stars = new MyStar[STAR_CNTMAX];
		for(int i = 0; i < STAR_CNTMAX; i++)
		{
			m_stars[i] = new MyStar();
		}
		/*
		vtxs = new Vector2[VTX_MAXCNT];
		Vector2 v = new Vector2(0.45f, 0f);
		for(int i = 0; i < VTX_MAXCNT; i++)
		{
			vtxs[i] = new Vector2(v.x, v.y);
			v.rotate(360f / VTX_MAXCNT);
		}
		//*/
	}
	
	public void setState(int state)
	{
		m_state = state;
		m_stateStep = 0;
		m_stateTimer = 0f;
	}
	
	public void update()
	{
		for(int i = 0; i < STAR_CNTMAX; i++)
		{
			m_stars[i].update();
		}
		for(int i = 0; i < 2; i++)
		{
			m_rockets[i].update();
		}
		
		switch(m_state)
		{
		case kState_PlanetOut: updatePlanetOut(); break;
		case kState_PlanetIn: updatePlanetIn(); break;
		case kState_MoveL: updateMove(+1f); break;
		case kState_MoveR: updateMove(-1f); break;
		case kState_Stop: updateStop(); break;
		}
		m_stateTimer += Gdx.graphics.getDeltaTime();
	}
	
	void updatePlanetOut()
	{
		switch(m_stateStep)
		{
		case 0:
			m_stateStep++;
			m_planetInAlpha = 0f;
			break;
		case 1:
			m_planetTimer += Gdx.graphics.getDeltaTime();
			break;
		}
	}
	
	void updatePlanetIn()
	{
		switch(m_stateStep)
		{
		case 0:
			if(m_stateTimer < 1f)
			{
				m_planetInAlpha = m_stateTimer;
			}
			else
			{
				m_stateStep++;
				m_planetInAlpha = 1f;
			}
			break;
		}
	}
	
	void updateMove(float lr)
	{
		switch(m_stateStep)
		{
		case 0:
			m_stateStep++;
			m_planetMove.x = 3f * lr;
			m_planetMove.y = 0f;
			for(int i = 0; i < STAR_CNTMAX; i++)
			{
				m_stars[i].start(lr);
			}
			for(int i = 0; i < 2; i++)
			{
				m_rockets[i].start(lr);
			}
			break;
		case 1:
			if(m_stateTimer < 1.5f)
			{
				float elapsed = Gdx.graphics.getDeltaTime();
				m_planetPos = JgfUtil.add(m_planetPos, JgfUtil.mul(m_planetMove, elapsed));
				m_planetOut.setPosition(m_planetPos.x, m_planetPos.y);
				m_planetIn.setPosition(m_planetPos.x, m_planetPos.y);
			}
			else
			{
				m_stateStep++;
				m_planetPos.x = -m_planetSize / 2f - 1.5f * lr;
				int stgIdx = m_main.level.getStgIdx();
				/*
				String texName = "";
				switch(stgIdx)
				{
				case 0: texName = "bg01_planet00.png"; break;
				case 1: texName = "bg01_planet01.png"; break;
				case 2: texName = "bg01_planet02.png"; break;
				case 3: texName = "bg01_planet03.png"; break;
				case 4: texName = "bg01_planet04.png"; break;
				case 5: texName = "bg01_planet05.png"; break;
				}
				/*/
				String texName = m_main.level.getInTexName();
				//*/
				Texture tex = m_main.asset.getTex(texName);
				m_planetIn.setTexture(tex);
				/*
				switch(stgIdx)
				{
				case 0: texName = "bg00_planet00.png"; break;
				case 1: texName = "bg00_planet01.png"; break;
				case 2: texName = "bg00_planet02.png"; break;
				case 3: texName = "bg00_planet03.png"; break;
				case 4: texName = "bg00_planet04.png"; break;
				case 5: texName = "bg00_planet05.png"; break;
				}
				/*/
				texName = m_main.level.getOutTexName();
				//*/
				m_planetOutAtlas = m_main.asset.getAtlas(texName);
				for(int i = 0; i < STAR_CNTMAX; i++)
				{
					m_stars[i].end();
				}
				for(int i = 0; i < 2; i++)
				{
					m_rockets[i].end();
				}
			}
			break;
		case 2:
			{
				float elapsed = Gdx.graphics.getDeltaTime();
				m_planetPos = JgfUtil.add(m_planetPos, JgfUtil.mul(m_planetMove, elapsed));
			}
			if(0f < lr && -m_planetSize / 2f < m_planetPos.x)
			{
				setState(kState_PlanetOut);
				m_main.ui.setEnableLevelSelect();
				m_planetPos.x = -m_planetSize / 2f;
			}
			if(lr < 0f && m_planetPos.x < -m_planetSize / 2f)
			{
				setState(kState_PlanetOut);
				m_main.ui.setEnableLevelSelect();
				m_planetPos.x = -m_planetSize / 2f;
			}
			m_planetOut.setPosition(m_planetPos.x, m_planetPos.y);
			m_planetIn.setPosition(m_planetPos.x, m_planetPos.y);
			break;
		}
	}
	
	void updateStop()
	{
		
	}
	
	class MyRocket
	{
		static public final int kState_None = 0;
		static public final int kState_Move = 1;
		
		float m_ub;
		float m_lr;
		float m_size;
		Sprite m_sprite;
		Vector2 m_pos;
		Vector2 m_move;
		int m_state;
		
		public MyRocket()
		{
			m_pos = new Vector2();
			m_move = new Vector2();
			m_state = kState_None;
		}

		public void draw(SpriteBatch batch)
		{
			if(m_state == kState_None) return;
			m_sprite.draw(batch);
		}

		public void end()
		{
			m_state = kState_None;
			if(0f < m_lr)
			{
				m_sprite.setFlip(false, false);
			}
			m_lr = 0f;
		}

		public void setup(float ub)
		{
			m_size = 0.2f;
			Texture tex = m_main.asset.getTex("bg02_rocket00.png");
			m_sprite = new Sprite(tex);
			m_sprite.setSize(m_size, m_size);
			m_sprite.setOrigin(m_size/2f, m_size/2f);
			m_sprite.setColor(Color.WHITE);
			m_ub = ub;
		}
		
		public void start(float lr)
		{
			m_state = kState_Move;
			m_lr = lr;
			if(0f < m_lr)
			{
				m_sprite.setFlip(true, false);
			}
			m_move.x = -1f * m_lr;
			m_pos.x = 0.7f * m_lr;
			m_pos.y = 0.5f * m_ub;
			if(0f < m_ub)
			{
				m_pos.y -= m_size;
			}
			m_sprite.setPosition(m_pos.x, m_pos.y);
		}

		public void update()
		{
			if(m_state == kState_None) return;
			float elapsed = Gdx.graphics.getDeltaTime();
			m_pos = JgfUtil.add(m_pos, JgfUtil.mul(m_move, elapsed));
			m_sprite.setPosition(m_pos.x, m_pos.y);
		}
	}
	
	class MyStar
	{
		static public final int kState_None = 0;
		static public final int kState_Move = 1;
		static public final int kState_Stop = 2;
		
		Sprite m_sprite;
		Vector2 m_pos;
		Vector2 m_move;
		int m_state;
		
		public MyStar()
		{
			m_pos = new Vector2();
			m_move = new Vector2();
			m_state = kState_None;
		}
		
		public void draw(SpriteBatch batch)
		{
			if(m_state == kState_None) return;
			m_sprite.draw(batch);
		}
		
		public void end()
		{
			m_state = kState_Stop;
		}
		
		public void start(float lr)
		{
			m_state = kState_Move;
			m_move.x = lr * MathUtils.random(2f, 8f);
			m_pos.x = -1.1f * lr;
			m_pos.y = MathUtils.random(-2f, +2f);
			float size = 0.04f / m_move.x;
			Texture tex = m_main.asset.getTex("bg00_star00.png");
			m_sprite = new Sprite(tex);
			m_sprite.setSize(size, size);
			m_sprite.setPosition(m_pos.x, m_pos.y);
			m_sprite.setOrigin(size/2f, size/2f);
			m_sprite.setColor(Color.WHITE);
		}
		
		public void update()
		{
			if(m_state == kState_None) return;
			float elapsed = Gdx.graphics.getDeltaTime();
			m_pos = JgfUtil.add(m_pos, JgfUtil.mul(m_move, elapsed));
			if(m_move.x < 0f && m_pos.x < -1.1f)
			{
				if(m_state == kState_Move)
				{
					m_pos.x = +1.1f;
					m_pos.y = MathUtils.random(-1f, +1f);
				}
				else
				{
					m_move.x = 0f;
					m_state = kState_None;
				}
			}
			if(0f < m_move.x && +1.1f < m_pos.x)
			{
				if(m_state == kState_Move)
				{
					m_pos.x = -1.1f;
					m_pos.y = MathUtils.random(-1f, +1f);
				}
				else
				{
					m_move.x = 0f;
					m_state = kState_None;
				}
			}
			m_sprite.setPosition(m_pos.x, m_pos.y);
		}
	}
}
