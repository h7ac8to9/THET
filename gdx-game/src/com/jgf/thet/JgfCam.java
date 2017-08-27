package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

public class JgfCam
{
	OrthographicCamera m_cam;
	Vector2 m_camSize;
	
	public JgfCam()
	{
		m_camSize = new Vector2();
		m_camSize.x = 1f;
		m_camSize.y = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
		m_cam = new OrthographicCamera();
		m_cam.setToOrtho(false, m_camSize.x, m_camSize.y);
		m_cam.position.set(0f, 0f, 0f);
	}
	
	public Vector2 getSize()
	{
		return m_camSize;
	}
	
	public void update()
	{
		m_cam.update();
	}
	
	public void apply(SpriteBatch batch)
	{
		batch.setProjectionMatrix(m_cam.combined);
	}
	
	public Vector2 WorldToScreen(float x, float y)
	{
		Vector3 world = new Vector3(x, y, 0f);
		m_cam.project(world);
		Vector2 screen = new Vector2(world.x, world.y);
		return screen;
	}
}
