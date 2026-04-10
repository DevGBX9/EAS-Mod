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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class EssentialAutoSprint implements ModInitializer
{
	public static class ModLogger {
		public void info(String msg) {
			System.out.println("[EAS] " + msg);
		}
		public void error(String msg) {
			System.err.println("[EAS] ERROR: " + msg);
		}
		public void error(String msg, Throwable t) {
			System.err.println("[EAS] ERROR: " + msg);
			if (t != null) t.printStackTrace(System.err);
		}
	}

	public static final ModLogger LOGGER = new ModLogger();

	public static final String MOD_ID = "eas";
	public static String MOD_VERSION = "unknown";
	public static String MOD_NAME = "unknown";

	@Override
	public void onInitialize()
	{
		ModMetadata metadata = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata();
		MOD_NAME = metadata.getName();
		MOD_VERSION = metadata.getVersion().getFriendlyString();
	}
}
