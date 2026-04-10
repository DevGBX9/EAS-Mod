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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EssentialAutoSprintClient implements ClientModInitializer
{
	public static net.minecraft.client.KeyMapping toggleKeyMapping;

	@Override
	public void onInitializeClient()
	{
		EASConfig.load();
		
		int initialKey = EASConfig.INSTANCE.toggleKey != 0 ? EASConfig.INSTANCE.toggleKey : org.lwjgl.glfw.GLFW.GLFW_KEY_H;

		//#if MC >= 11600
		//$$ toggleKeyMapping = new net.minecraft.client.KeyMapping("Auto Sprint (EAS)", com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM, initialKey, "key.categories.movement");
		//#else
		toggleKeyMapping = new net.minecraft.client.KeyMapping("Auto Sprint (EAS)", initialKey, "key.categories.movement");
		//#endif
		EssentialAutoSprint.LOGGER.info("[EAS] Client initialized — Auto Sprint ready.");
	}
}
