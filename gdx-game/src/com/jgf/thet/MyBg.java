package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class MyBg
{
	static public final int VTX_MAXCNT = 16;
	
	private MyMain m_main;
	private Texture m_tex;
	private Sprite m_sprite;
	public Vector2[] vtxs;
	
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
		float elapsed = Gdx.graphics.getDeltaTime();
		float rot = elapsed * (-2f);
	//	m_sprite.rotate(rot);
		m_sprite.draw(batch);
	}
	
	public MyBg()
	{
		m_main = JgfGame.getMain();
		m_tex = m_main.asset.getTex("planet01.png");
		float size = 1.07f;
		m_sprite = new Sprite(m_tex);
		m_sprite.setSize(size, size);
		m_sprite.setPosition(-size/2f, -size/2f);
		m_sprite.setOrigin(size/2f, size/2f);
		
		vtxs = new Vector2[VTX_MAXCNT];
		Vector2 v = new Vector2(0.45f, 0f);
		for(int i = 0; i < VTX_MAXCNT; i++)
		{
			vtxs[i] = new Vector2(v.x, v.y);
			v.rotate(360f / VTX_MAXCNT);
		}
	}
}
