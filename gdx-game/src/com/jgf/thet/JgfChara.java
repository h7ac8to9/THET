package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class JgfChara
{
	final int kOrigin_Default = 0;
	final int kOrigin_Foot = 1;
	final int kOrigin_Center = 2;
	
	MyMain m_main;
	int m_charaId;
	int m_owner;
	JgfAtlas m_atlas;
	Sprite m_sprite;
	ShapeRenderer m_shape;
	Vector2 m_pos;
	Vector2 m_linVel;
	Vector2 m_size;
	float m_rot;
	float m_animTimer;
	int m_origin;
	private boolean m_flipX;
	private boolean m_flipY;
	boolean m_isAlive;
	
	protected int m_state;
	int m_stateStep;
	float m_stateTimer;
	
	float m_colorTimer;
	Color m_colorBase;
	
	public void addLinVel(Vector2 linVel)
	{
		addLinVel(linVel.x, linVel.y);
	}
	
	public void addLinVel(float x, float y)
	{
		m_linVel.x += x;
		m_linVel.y += y;
	}
	
	public void dead()
	{
		m_isAlive = false;
		//ここでオブジェクト解放とかする
	}
	
	public boolean draw(SpriteBatch batch)
	{
		if(!m_isAlive) return false;
		
		TextureRegion region = m_atlas.getRegion(m_animTimer);
		m_sprite.setRegion(region);
		m_sprite.setFlip(m_flipX, m_flipY);
	//	m_sprite.setColor(m_color);
		m_sprite.draw(batch);
		
		/*
		 batch.end();
		 m_shape.setProjectionMatrix(batch.getProjectionMatrix());
		 m_shape.begin(ShapeRenderer.ShapeType.Line);
		 m_shape.setColor(Color.RED);
		 Rectangle rect = getRect();
		 m_shape.rect(rect.x, rect.y, rect.width, rect.height);
		 m_shape.end();
		 batch.begin();
		 //*/
		 
		 return true;
	}
	
	public Vector2 getCenterPos()
	{
		Vector2 centerPos = m_pos;
		switch(m_origin)
		{
		case kOrigin_Foot:
			centerPos = new Vector2();
			getRect().getCenter(centerPos);
			return centerPos;
		}
		return centerPos;
	}
	
	public int getCharaId()
	{
		return m_charaId;
	}
	
	public Vector2 getLeftDir()
	{
		Vector2 leftDir = new Vector2(-1f, 0f);
		return leftDir.rotate(m_rot);
	}
	
	public Vector2 getLinVel()
	{
		return m_linVel;
	}
	
	public int getOwner()
	{
		return m_owner;
	}
	
	public Vector2 getPos()
	{
		return m_pos;
	}
	
	public Rectangle getRect()
	{
		return m_sprite.getBoundingRectangle();
	}
	
	public Vector2 getRightDir()
	{
		Vector2 rightDir = new Vector2(+1f, 0f);
		return rightDir.rotate(m_rot);
	}
	
	protected int getState()
	{
		return m_state;
	}
	
	public Vector2 getUpDir()
	{
		Vector2 upDir = new Vector2(0f, +1f);
		return upDir.rotate(m_rot);
	}
	
	public void hit(JgfChara chara)
	{
		
	}
	
	public boolean isAlive()
	{
		return m_isAlive;
	}
	
	public JgfChara(int charaId)
	{
		m_main = MyGame.getMain();
		m_charaId = charaId;
		m_owner = -1;
		m_isAlive = false;
		m_shape = new ShapeRenderer();
		m_pos = new Vector2();
		m_linVel = new Vector2();
		m_size = new Vector2();
	}
	
	public void setAlive(boolean isAlive)
	{
		m_isAlive = isAlive;
	}
	
	public void setAtlas(String name)
	{
		m_atlas = m_main.asset.getAtlas(name);
		m_animTimer = 0f;
	}
	
	public void setColorBase(Color colorBase)
	{
		m_colorBase = colorBase;
		m_sprite.setColor(colorBase);
	}
	
	//time秒経過するとm_colorBaseに戻る.
	//timeをゼロにするとカラーは時間経過で戻らない.
	public void setColorImd(Color colorImd, float time)
	{
		m_sprite.setColor(colorImd);
		m_colorTimer = time;
	}
	
	//SpriteはsetRegion()するとflip情報が
	//初期値に戻ってしまうのでJgfCharaで覚えておく.
	public void setFlip(boolean flipX, boolean flipY)
	{
		m_flipX = flipX;
		m_flipY = flipY;
	}
	
	public void setPos(Vector2 pos)
	{
		setPos(pos.x, pos.y);
	}
	
	public void setPos(float x, float y)
	{
		m_pos.x = x;
		m_pos.y = y;
		switch(m_origin)
		{
		case kOrigin_Default:
			break;
		case kOrigin_Foot:
			//spriteは左下の座標を指定するのでオフセットする
			x = m_pos.x - m_size.x / 2f;
			break;
		case kOrigin_Center:
			x = m_pos.x - m_size.x / 2f;
			y = m_pos.y - m_size.y / 2f;
			break;
		}
		m_sprite.setPosition(x, y);
	}
	
	public void setLinVel(Vector2 linVel)
	{
		setLinVel(linVel.x, linVel.y);
	}
	
	public void setLinVel(float x, float y)
	{
		m_linVel.x = x;
		m_linVel.y = y;
	}
	
	protected void setOrigin(int origin)
	{
		m_origin = origin;
		switch(origin)
		{
			case kOrigin_Default:
				break;
			case kOrigin_Foot:
				m_sprite.setOrigin(m_size.x/2f, 0f);
				break;
			case kOrigin_Center:
				m_sprite.setOrigin(m_size.x/2f, m_size.y/2f);
				break;
		}
		setPos(m_pos);
	}
	
	public void setRot(float rot)
	{
		m_rot = rot;
		m_sprite.setRotation(m_rot);
		//m_rectにrotateがない！
	}
	
	public void setSize(float x, float y)
	{
		m_size.x = x;
		m_size.y = y;
		m_sprite.setSize(m_size.x, m_size.y);
	}
	
	protected void setState(int state)
	{
		m_state = state;
		m_stateStep = 0;
		m_stateTimer = 0f;
	}
	
	public void setup(String texName, int origin, float sizeX, float sizeY)
	{
		m_atlas = m_main.asset.getAtlas(texName);
		m_sprite = new Sprite(m_atlas.getRegion(0f));
		JgfUtil.set(m_pos, 0f, 0f);
		JgfUtil.set(m_linVel, 0f, 0f);
		setSize(sizeX, sizeY);
		setOrigin(origin);
		setPos(m_pos);
		setRot(m_rot);
		m_flipX = false;
		m_flipY = false;
		m_animTimer = 0f;
		m_colorTimer = 0f;
		setColorBase(Color.BLACK); //TODO: Whiteにすべき.
		m_isAlive = true;
	}
	
	public boolean update()
	{
		if(!m_isAlive) return false;
		
		float elapsed = Gdx.graphics.getDeltaTime();
		m_pos.x += m_linVel.x * elapsed;
		m_pos.y += m_linVel.y * elapsed;
		setPos(m_pos);
		m_sprite.setSize(m_size.x, m_size.y);

		m_stateTimer += elapsed;
		m_animTimer += elapsed;
		
		if(0f < m_colorTimer)
		{
			m_colorTimer -= elapsed;
			if(m_colorTimer <= 0f)
			{
				m_colorTimer = 0f;
				m_sprite.setColor(m_colorBase);
			}
		}
		
		return true;
	}
}
