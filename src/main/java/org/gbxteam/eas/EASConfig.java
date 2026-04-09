/*
 * This file is part of the Essential Auto Sprint (EAS) project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  GBX Team and contributors
 *
 * Essential Auto Sprint (EAS) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Essential Auto Sprint (EAS) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Essential Auto Sprint (EAS).  If not, see <https://www.gnu.org/licenses/>.
 */

package org.gbxteam.eas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EASConfig
{
	public static EASConfig INSTANCE = new EASConfig();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File configFile;

	/** Whether auto-sprint is currently active */
	public boolean enabled = true;

	/**
	 * GLFW key code for the toggle keybind.
	 * Default: H = GLFW_KEY_H (72)
	 */
	public int toggleKey = 72;

	public static void load()
	{
		configFile = FabricLoader.getInstance().getConfigDir().resolve("eas.json").toFile();
		if (configFile.exists())
		{
			try (FileReader reader = new FileReader(configFile))
			{
				EASConfig loaded = GSON.fromJson(reader, EASConfig.class);
				if (loaded != null) INSTANCE = loaded;
			}
			catch (IOException e)
			{
				EssentialAutoSprint.LOGGER.error("[EAS] Failed to load config: " + e.getMessage());
			}
		}
		INSTANCE.save();
	}

	public void save()
	{
		if (configFile == null)
		{
			configFile = FabricLoader.getInstance().getConfigDir().resolve("eas.json").toFile();
		}
		try (FileWriter writer = new FileWriter(configFile))
		{
			GSON.toJson(this, writer);
		}
		catch (IOException e)
		{
			EssentialAutoSprint.LOGGER.error("[EAS] Failed to save config: " + e.getMessage());
		}
	}
}
