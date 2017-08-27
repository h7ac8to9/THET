package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;

public class JgfGame extends Game
{
	SpriteBatch batch;

	private MyMain m_main; //MyMainは必ず存在する

	static public MyMain getMain()
	{
		return(((JgfGame)Gdx.app.getApplicationListener()).m_main);
	}

	@Override
	public void create()
	{
		m_main = new MyMain();
		m_main.init();

		batch = new SpriteBatch();
	}

	@Override
	public void render()
	{
		update();
		draw();
	}

	@Override
	public void dispose()
	{
		m_main.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	void update()
	{
		m_main.update();
	}

	void draw()
	{
		m_main.draw(batch);
	}
}
