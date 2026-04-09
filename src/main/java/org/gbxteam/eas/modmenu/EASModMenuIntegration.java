/*
 * This file is part of the Essential Auto Sprint (EAS) project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  GBX Team and contributors
 */

package org.gbxteam.eas.modmenu;

//#if MC >= 1.15.0 && MC < 26.0.0
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.gbxteam.eas.config.EASConfigScreen;

/**
 * Registers EAS with Mod Menu.
 * Supported from MC 1.15.2 through 1.21.11.
 * Excluded for 1.14.4 (no Maven artifact) and 26.x+ (ModMenu not yet available).
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
//$$ /** Stub for versions without ModMenu support (1.14.4 and 26.x+). */
//$$ public class EASModMenuIntegration {}
//#endif
