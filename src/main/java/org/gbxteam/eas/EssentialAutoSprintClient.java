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

		try {
			boolean created = false;
			Object keySymType = null;
			//#if MC >= 11600
			//$$ keySymType = com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
			//#endif

			for (java.lang.reflect.Constructor<?> c : net.minecraft.client.KeyMapping.class.getConstructors()) {
				Class<?>[] p = c.getParameterTypes();
				if (p.length == 3 && p[0] == String.class && p[1] == int.class && p[2] == String.class) {
					toggleKeyMapping = (net.minecraft.client.KeyMapping) c.newInstance("Auto Sprint (EAS)", initialKey, "key.categories.movement");
					created = true;
					break;
				} else if (p.length == 4 && p[0] == String.class && p[3] == String.class && keySymType != null) {
					toggleKeyMapping = (net.minecraft.client.KeyMapping) c.newInstance("Auto Sprint (EAS)", keySymType, initialKey, "key.categories.movement");
					created = true;
					break;
				} else if (p.length == 4 && p[0] == String.class && p[2] == int.class && p[3] != String.class && keySymType != null) {
					Object categoryObj = null;
					for (java.lang.reflect.Field f : p[3].getFields()) {
						if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) && f.getType() == p[3]) {
							if (f.getName().toUpperCase().contains("MOVE") || categoryObj == null) {
								categoryObj = f.get(null);
								if (f.getName().toUpperCase().contains("MOVE")) break;
							}
						}
					}
					if (categoryObj != null) {
						toggleKeyMapping = (net.minecraft.client.KeyMapping) c.newInstance("Auto Sprint (EAS)", keySymType, initialKey, categoryObj);
						created = true;
						break;
					}
				}
			}
			if (!created) EssentialAutoSprint.LOGGER.error("[EAS] Failed to create KeyMapping via reflection!");
		} catch (Exception e) {
			EssentialAutoSprint.LOGGER.error("[EAS] KeyMapping error:", e);
		}
		EssentialAutoSprint.LOGGER.info("[EAS] Client initialized — Auto Sprint ready.");
	}
}
