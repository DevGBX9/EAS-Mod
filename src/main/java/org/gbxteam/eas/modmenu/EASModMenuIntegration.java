/*
 * This file is part of the Essential Auto Sprint (EAS) project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  GBX Team and contributors
 */

package org.gbxteam.eas.modmenu;

//#if MC >= 11600
//$$ import com.terraformersmc.modmenu.api.ConfigScreenFactory;
//$$ import com.terraformersmc.modmenu.api.ModMenuApi;
//$$ import net.fabricmc.api.EnvType;
//$$ import net.fabricmc.api.Environment;
//$$ import org.gbxteam.eas.config.EASConfigScreen;
//$$
//$$ @Environment(EnvType.CLIENT)
//$$ public class EASModMenuIntegration implements ModMenuApi
//$$ {
//$$ 	@Override
//$$ 	public ConfigScreenFactory<?> getModConfigScreenFactory()
//$$ 	{
//$$ 		return EASConfigScreen::new;
//$$ 	}
//$$ }
//#else
public class EASModMenuIntegration {}
//#endif
