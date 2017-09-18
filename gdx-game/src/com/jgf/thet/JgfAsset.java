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
	HashMap<String, JgfAtlas> m_atlases;
	HashMap<String, JgfTbl> m_tbls;
	
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
	
	public JgfTbl getTbl(String name)
	{
		return m_tbls.get(name);
	}
	
	public Texture getTex(String name)
	{
		return man.get(name);
	}
	
	public JgfAsset()
	{
		m_main = MyGame.getMain();
		man = new AssetManager();
		m_atlases = new HashMap<String, JgfAtlas>();
		m_tbls = new HashMap<String, JgfTbl>();
	}
	
	public void load()
	{
		JgfTbl assetTbl = new JgfTbl();
		assetTbl.setupCsv("tbl00_asset00.csv", false, true);
		for(int r = 0; r < assetTbl.getRowCnt(); r++)
		{
			String type = assetTbl.getCellS(r, "type");
			String filepath = assetTbl.getCellS(r, "filepath");
			String arg0 = assetTbl.getCellS(r, "arg0");
			String arg1 = assetTbl.getCellS(r, "arg1");
			String arg2 = assetTbl.getCellS(r, "arg2");
			String arg3 = assetTbl.getCellS(r, "arg3");
			switch(type)
			{
			case "atlas":
				int row = 1;
				if(0 < arg0.length()) row = Integer.valueOf(arg0);
				int col = 1;
				if(0 < arg1.length()) col = Integer.valueOf(arg1);
				float duration = 0f;
				if(0 < arg2.length()) duration = Float.valueOf(arg2);
				switch(arg3)
				{
				case "LOOP":
					if(filepath.equals("gunman00walk00.png"))
					{
						loadAtlas(filepath, 2, 2, 0.1f, Animation.PlayMode.LOOP);
					}
					else
					{
						loadAtlas(filepath, row, col, duration, Animation.PlayMode.LOOP);
					}
					break;
				case "NORMAL":
					loadAtlas(filepath, row, col, duration, Animation.PlayMode.NORMAL);
					break;
				default:
					loadAtlas(filepath);
					break;
				}
				break;
			case "bdl":
				loadBdl(filepath);
				break;
			case "csv":
				boolean useKey = arg0.equals("true");
				boolean useLabel = arg1.equals("true");
				loadCsv(filepath, useKey, useLabel);
				break;
			case "fnt":
				loadFnt(filepath);
				break;
			case "msc":
				loadMsc(filepath);
				break;
			case "snd":
				loadSnd(filepath);
				break;
			case "tex":
				loadTex(filepath);
				break;
			}
		}
		
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
	
	public void loadCsv(String name, boolean useKey, boolean useLabel)
	{
		JgfTbl tbl = new JgfTbl();
		tbl.setupCsv(name, useKey, useLabel);
		m_tbls.put(name, tbl);
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
