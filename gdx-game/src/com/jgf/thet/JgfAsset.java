package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.assets.*;
import java.util.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

public class JgfAsset
{
	MyMain m_main;
	public AssetManager man;
	private HashMap<String, JgfAtlas> m_atlases;
	
	public void dispose()
	{
		man.dispose();
	}
	
	public JgfAtlas getAtlas(String name)
	{
		/*
		if(!m_atlases.containsKey(name))
		{
			m_main.log.print("err: "+name);
			return m_atlases.get("bat01.png");
		}
		*/
		return m_atlases.get(name);
	}
	
	public I18NBundle getBdl(String name)
	{
		return man.get(name);
	}
	
	public BitmapFont getFnt(String name)
	{
		return man.get(name);
	}
	
	public Music getMsc(String name)
	{
		return man.get(name);
	}
	
	public Sound getSnd(String name)
	{
		return man.get(name);
	}
	
	public Texture getTex(String name)
	{
		return man.get(name);
	}
	
	public JgfAsset()
	{
		m_main = JgfGame.getMain();
		man = new AssetManager();
		m_atlases = new HashMap<String, JgfAtlas>();
	}
	
	public void load()
	{
		//JgfLog
		loadFnt("verdana39.fnt");
		
		//BGM
		loadMsc("music01.mp3");
		
		//MyUi
		loadBdl("bdl");
		loadTex("btn00.png");
		loadTex("heart00.png");
		loadTex("icon00_jem00.png");
		loadTex("icon00_gloves00.png");
		loadTex("icon00_boots00.png");
		loadTex("ok00.png");
		loadTex("archer01.png");
		loadTex("knight01.png");
		loadTex("mage01.png");
		loadTex("ninja01.png");
		loadTex("btn00_move00.png");
		loadTex("btn00_skills00.png");
		loadTex("btn00_skillf00.png");
		loadTex("btn00_skillm00.png");
		loadTex("btn00_skilln00.png");
		loadTex("btn03_heart00.png");
		loadTex("btn03_gloves00.png");
		loadTex("btn03_boots00.png");
		loadTex("btn03_skills00.png");
		loadTex("btn03_skillf00.png");
		loadTex("btn03_skillm00.png");
		loadTex("btn03_skilln00.png");
		
		//BG
		loadTex("planet01.png");
		
		//Level
		loadAtlas("cave00.png", 2, 2, 0.15f, Animation.PlayMode.LOOP);
		loadAtlas("cave01.png", 4, 4, 0.15f, Animation.PlayMode.NORMAL);
		
		//MyPlayer
		loadAtlas("archer02.png", 2, 1, 0.2f, Animation.PlayMode.LOOP);
		loadAtlas("gunman00walk00.png", 2, 2, 0.1f, Animation.PlayMode.LOOP);
		loadAtlas("knight02.png", 2, 1, 0.2f, Animation.PlayMode.LOOP);
		loadAtlas("knight00walk00.png", 2, 2, 0.15f, Animation.PlayMode.LOOP);
		loadAtlas("mage02.png", 2, 1, 0.2f, Animation.PlayMode.LOOP);
		loadAtlas("mage00walk00.png", 2, 2, 0.1f, Animation.PlayMode.LOOP);
		loadAtlas("ninja02.png", 2, 1, 0.2f, Animation.PlayMode.LOOP);
		loadAtlas("ninja00walk00.png", 2, 2, 0.06f, Animation.PlayMode.LOOP);
		loadSnd("dmg00.mp3");
		
		//MyEnemy
		loadAtlas("bat00.png");
		loadAtlas("bat01.png", 2, 2, 0.15f, Animation.PlayMode.LOOP);
		loadAtlas("zombie00.png");
		loadAtlas("balloon00.png");
		
		//MyBullet
		loadAtlas("arrow00.png");
		loadAtlas("sword00.png");
		loadAtlas("fire00.png");
		loadAtlas("knife00.png");
		loadAtlas("blt01_shield00.png");
		loadAtlas("blt01_heal00.png");
		
		//man.load(".png", Texture.class);
		
		//TODO: そのうち非同期読み込みするとき消す
		man.finishLoading();
		
		for(HashMap.Entry<String, JgfAtlas> e : m_atlases.entrySet())
		{
			e.getValue().setup();
		}
	}
	
	private void loadAtlas(String name)
	{
		man.load(name, Texture.class);
		JgfAtlas atlas = new JgfAtlas();
		atlas.init(name);
		m_atlases.put(name, atlas);
	}
	
	private void loadAtlas(String name, int row, int col, float duration, Animation.PlayMode playMode)
	{
		man.load(name, Texture.class);
		JgfAtlas atlas = new JgfAtlas();
		atlas.init(name, row, col, duration, playMode);
		m_atlases.put(name, atlas);
	}
	
	private void loadBdl(String name)
	{
		man.load(name, I18NBundle.class);
	}
	
	private void loadFnt(String name)
	{
		man.load(name, BitmapFont.class);
	}
	
	private void loadMsc(String name)
	{
		man.load(name, Music.class);
	}
	
	public void loadSnd(String name)
	{
		man.load(name, Sound.class);
	}
	
	public void loadTex(String name)
	{
		man.load(name, Texture.class);
	}
	
	public void update()
	{
		//TODO: そのうち非同期読み込みやる
		/*
		if(man.update())
		{
		}
		*/
	}
}
