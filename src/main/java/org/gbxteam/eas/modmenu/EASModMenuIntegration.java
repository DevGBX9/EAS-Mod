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

package org.gbxteam.eas.modmenu;

//#if MC < 26.0.0
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.gbxteam.eas.config.EASConfigScreen;

/**
 * Registers EAS with Mod Menu, providing a config screen factory.
 * Supported for all Minecraft versions up to 1.21.11.
 * Excluded from 26.x+ as ModMenu does not support it yet.
 */
@Environment(EnvType.CLIENT)
public class EASModMenuIntegration implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return EASConfigScreen::new;
	}
}
//#else
//#endif
