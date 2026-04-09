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

package org.gbxteam.eas.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.gbxteam.eas.EASConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.19.0
//$$ import net.minecraft.network.chat.Component;
//#else
import net.minecraft.network.chat.TextComponent;
//#endif

/**
 * Handles the toggle keybind (default H) and shows an on-screen notification above the hotbar.
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin
{
	private boolean eas$lastKeyState = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void eas_onTick(CallbackInfo ci)
	{
		Minecraft mc = (Minecraft) (Object) this;

		if (mc.player == null || mc.screen != null) return;

		// --- Toggle keybind detection ---
		//#if MC >= 11500
		long windowHandle = mc.getWindow().getWindow();
		//#else
		//$$ long windowHandle = mc.window.getWindow();
		//#endif

		boolean currentKeyDown = GLFW.glfwGetKey(windowHandle, EASConfig.INSTANCE.toggleKey) == GLFW.GLFW_PRESS;

		if (currentKeyDown && !eas$lastKeyState)
		{
			EASConfig.INSTANCE.enabled = !EASConfig.INSTANCE.enabled;
			EASConfig.INSTANCE.save();

			// Disable sprinting immediately if toggled off
			if (!EASConfig.INSTANCE.enabled)
			{
				mc.player.setSprinting(false);
			}

			// Show action bar overlay notification
			String msgText = EASConfig.INSTANCE.enabled
					? "\u00a7aAuto Sprint: ON"
					: "\u00a7cAuto Sprint: OFF";

			//#if MC >= 1.19.0
			//$$ mc.player.displayClientMessage(Component.literal(msgText), true);
			//#elseif MC >= 1.17.0
			//$$ mc.player.displayClientMessage(new TextComponent(msgText), true);
			//#elseif MC >= 1.16.0
			mc.player.displayClientMessage(new TextComponent(msgText), true);
			//#else
			//$$ // 1.14-1.15: action bar API unavailable, skip overlay notification
			//#endif
		}

		eas$lastKeyState = currentKeyDown;
	}
}
