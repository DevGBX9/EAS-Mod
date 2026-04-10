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

//#if MC >= 11900
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
	private boolean eas$initializedKeys = false;
	private boolean eas$lastKeyState = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void eas_onTick(CallbackInfo ci)
	{
		Minecraft mc = (Minecraft) (Object) this;

		if (!eas$initializedKeys && mc.options != null && org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping != null) {
			eas$initializedKeys = true;
			try {
				//#if MC >= 12004
				//$$ // Options fields in 1.20.4+ might be final or differently structured, wait - they are final KeyMapping[] keyMappings
				//$$ net.minecraft.client.KeyMapping[] old = mc.options.keyMappings;
				//$$ boolean exists = false; for (net.minecraft.client.KeyMapping k : old) if (k == org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping) exists = true;
				//$$ if (!exists) {
				//$$ 	net.minecraft.client.KeyMapping[] newMappings = new net.minecraft.client.KeyMapping[old.length + 1];
				//$$ 	System.arraycopy(old, 0, newMappings, 0, old.length);
				//$$ 	newMappings[old.length] = org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping;
				//$$ 	mc.options.keyMappings = newMappings;
				//$$    net.minecraft.client.KeyMapping.resetMapping();
				//$$ }
				//#elseif MC >= 11500
				//$$ net.minecraft.client.KeyMapping[] old = mc.options.keyMappings;
				//$$ boolean exists = false; for (net.minecraft.client.KeyMapping k : old) if (k == org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping) exists = true;
				//$$ if (!exists) {
				//$$ 	net.minecraft.client.KeyMapping[] newMappings = new net.minecraft.client.KeyMapping[old.length + 1];
				//$$ 	System.arraycopy(old, 0, newMappings, 0, old.length);
				//$$ 	newMappings[old.length] = org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping;
				//$$ 	mc.options.keyMappings = newMappings;
				//$$    net.minecraft.client.KeyMapping.resetMapping();
				//$$ }
				//#else
				net.minecraft.client.KeyMapping[] old = mc.options.keyMappings; // wait, is it keyMappings in 1.14?
				boolean exists = false; for (net.minecraft.client.KeyMapping k : old) if (k == org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping) exists = true;
				if (!exists) {
					net.minecraft.client.KeyMapping[] newMappings = new net.minecraft.client.KeyMapping[old.length + 1];
					System.arraycopy(old, 0, newMappings, 0, old.length);
					newMappings[old.length] = org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping;
					mc.options.keyMappings = newMappings;
					net.minecraft.client.KeyMapping.resetMapping();
				}
				//#endif
			} catch (Exception e) {
				org.gbxteam.eas.EssentialAutoSprint.LOGGER.error("[EAS] Failed to inject vanilla keybinding directly:", e);
			}
		}

		if (mc.player == null || mc.screen != null) return;

		// --- Toggle keybind detection ---
		//#if MC >= 11500
		long windowHandle = mc.getWindow().getWindow();
		//#else
		//$$ long windowHandle = mc.window.getWindow();
		//#endif

		boolean currentKeyDown = GLFW.glfwGetKey(windowHandle, EASConfig.INSTANCE.toggleKey) == GLFW.GLFW_PRESS;
		
		boolean keyMappingPressed = false;
		if (org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping != null) {
			while (org.gbxteam.eas.EssentialAutoSprintClient.toggleKeyMapping.consumeClick()) {
				keyMappingPressed = true;
			}
		}

		if ((currentKeyDown && !eas$lastKeyState) || keyMappingPressed)
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

			//#if MC >= 260000
			//$$ mc.gui.setOverlayMessage(Component.literal(msgText), false);
			//#elseif MC >= 11900
			//$$ mc.player.displayClientMessage(Component.literal(msgText), true);
			//#elseif MC >= 11700
			//$$ mc.player.displayClientMessage(new TextComponent(msgText), true);
			//#elseif MC >= 11600
			mc.player.displayClientMessage(new TextComponent(msgText), true);
			//#else
			//$$ // 1.14-1.15: action bar API unavailable, skip overlay notification
			//#endif
		}

		eas$lastKeyState = currentKeyDown;
	}
}
