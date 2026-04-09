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

package org.gbxteam.eas.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import org.gbxteam.eas.EASConfig;
import org.lwjgl.glfw.GLFW;

//#if MC >= 1.20.0
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.Component;
//#elseif MC >= 1.19.0
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.Component;
//#elseif MC >= 1.17.0
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#elseif MC >= 1.16.0
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
//#else
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#endif

/**
 * EAS configuration screen.
 * Accessible via Mod Menu → Essential Auto Sprint (EAS) → Configure.
 *
 * Features:
 * - Toggle auto-sprint on/off
 * - View/change toggle keybind (shown as GLFW key name)
 * - Open the official Modrinth page
 * - Shows developer credit: GBX Team
 */
@Environment(EnvType.CLIENT)
public class EASConfigScreen extends Screen
{
	private final Screen parent;

	// UI state
	private boolean waitingForKey = false;

	// Colors
	private static final int COLOR_TITLE    = 0xFFFFD700; // Gold
	private static final int COLOR_SUBTITLE = 0xFFAAAAAA; // Gray
	private static final int COLOR_ENABLED  = 0xFF55FF55; // Green
	private static final int COLOR_DISABLED = 0xFFFF5555; // Red
	private static final int COLOR_LABEL    = 0xFFCCCCCC;
	private static final int COLOR_PANEL_BG = 0xCC000000; // Semi-transparent dark

	public EASConfigScreen(Screen parent)
	{
		//#if MC >= 1.19.0
		//$$ super(Component.translatable("eas.config.title"));
		//#elseif MC >= 1.17.0
		//$$ super(new TranslatableComponent("eas.config.title"));
		//#else
		super(new TranslatableComponent("eas.config.title"));
		//#endif
		this.parent = parent;
	}

	@Override
	protected void init()
	{
		super.init();

		int centerX = this.width / 2;
		int panelW   = Math.min(320, this.width - 40);
		int panelX   = centerX - panelW / 2;
		int startY   = this.height / 2 - 70;

		// --- Toggle Button ---
		addEASButton(
				panelX, startY,
				panelW, 20,
				getToggleLabel(),
				button -> {
					EASConfig.INSTANCE.enabled = !EASConfig.INSTANCE.enabled;
					EASConfig.INSTANCE.save();
					button.setMessage(makeText(getToggleLabel()));
				}
		);

		// --- Keybind Button ---
		addEASButton(
				panelX, startY + 28,
				panelW, 20,
				getKeybindLabel(),
				button -> {
					waitingForKey = true;
					button.setMessage(makeText("\u00a7ePress any key... (ESC to cancel)"));
				}
		);

		// --- Website Button ---
		addEASButton(
				panelX, startY + 56,
				panelW, 20,
				"\u00a77\uD83C\uDF10 Visit on Modrinth",
				button -> openWebsite()
		);

		// --- Done Button ---
		addEASButton(
				centerX - 75, this.height - 28,
				150, 20,
				"Done",
				button -> this.minecraft.setScreen(parent)
		);
	}

	// ---- Helpers ---------------------------------------------------------------

	private String getToggleLabel()
	{
		return EASConfig.INSTANCE.enabled
				? "\u00a7aAuto Sprint: \u00a7l\u00a7aON"
				: "\u00a7cAuto Sprint: \u00a7l\u00a7cOFF";
	}

	private String getKeybindLabel()
	{
		String keyName = glfwKeyName(EASConfig.INSTANCE.toggleKey);
		return "\u00a77Toggle Keybind: \u00a7f" + keyName;
	}

	private String glfwKeyName(int key)
	{
		String name = GLFW.glfwGetKeyName(key, 0);
		if (name != null) return name.toUpperCase();
		// Fallback names for special keys
		switch (key)
		{
			case GLFW.GLFW_KEY_H:         return "H";
			case GLFW.GLFW_KEY_ESCAPE:    return "ESC";
			case GLFW.GLFW_KEY_SPACE:     return "SPACE";
			case GLFW.GLFW_KEY_LEFT_ALT:  return "LALT";
			case GLFW.GLFW_KEY_RIGHT_ALT: return "RALT";
			default: return "Key " + key;
		}
	}

	private void openWebsite()
	{
		//#if MC >= 1.16.0
		net.minecraft.Util.getPlatform().openUri("https://modrinth.com/mod/essential-auto-sprint-(eas)");
		//#else
		//$$ try {
		//$$     java.awt.Desktop.getDesktop().browse(new java.net.URI("https://modrinth.com/mod/essential-auto-sprint-(eas)"));
		//$$ } catch (Exception ignored) {}
		//#endif
	}

	// Helper to create a text component cross-version
	//#if MC >= 1.19.0
	//$$ private Component makeText(String s) { return Component.literal(s); }
	//#elseif MC >= 1.16.0
	private net.minecraft.network.chat.Component makeText(String s) { return new TextComponent(s); }
	//#else
	//$$ private net.minecraft.network.chat.Component makeText(String s) { return new TextComponent(s); }
	//#endif

	// Helper to add a button cross-version
	private void addEASButton(int x, int y, int w, int h, String label, Button.OnPress action)
	{
		//#if MC >= 1.20.0
		//$$ this.addRenderableWidget(Button.builder(Component.literal(label), action).bounds(x, y, w, h).build());
		//#elseif MC >= 1.19.0
		//$$ this.addRenderableWidget(new Button(x, y, w, h, Component.literal(label), action));
		//#elseif MC >= 1.17.0
		//$$ this.addRenderableWidget(new Button(x, y, w, h, new TextComponent(label), action));
		//#else
		this.addWidget(new Button(x, y, w, h, new TextComponent(label), action));
		//#endif
	}

	// ---- Rendering -------------------------------------------------------------

	//#if MC >= 1.20.0
	//$$ @Override
	//$$ public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
	//$$ {
	//$$     renderBackground(graphics);
	//$$     drawPanel(graphics);
	//$$     drawTexts(graphics, mouseX, mouseY, delta);
	//$$     super.render(graphics, mouseX, mouseY, delta);
	//$$ }
	//#elseif MC >= 1.17.0
	//$$ @Override
	//$$ public void render(PoseStack pose, int mouseX, int mouseY, float delta)
	//$$ {
	//$$     renderBackground(pose);
	//$$     drawPanel(pose);
	//$$     drawTexts(pose, mouseX, mouseY, delta);
	//$$     super.render(pose, mouseX, mouseY, delta);
	//$$ }
	//#else
	@Override
	public void render(int mouseX, int mouseY, float delta)
	{
		renderBackground();
		drawPanel();
		drawTexts(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
	//#endif

	// Panel background
	//#if MC >= 1.20.0
	//$$ private void drawPanel(GuiGraphics g)
	//$$ {
	//$$     int cx = this.width / 2;
	//$$     int pw = Math.min(320, this.width - 40);
	//$$     int px = cx - pw / 2;
	//$$     int py = this.height / 2 - 85;
	//$$     int ph = 175;
	//$$     g.fill(px - 5, py, px + pw + 5, py + ph, COLOR_PANEL_BG);
	//$$ }
	//#elseif MC >= 1.17.0
	//$$ private void drawPanel(PoseStack pose)
	//$$ {
	//$$     int cx = this.width / 2;
	//$$     int pw = Math.min(320, this.width - 40);
	//$$     int px = cx - pw / 2;
	//$$     int py = this.height / 2 - 85;
	//$$     int ph = 175;
	//$$     fill(pose, px - 5, py, px + pw + 5, py + ph, COLOR_PANEL_BG);
	//$$ }
	//#else
	private void drawPanel()
	{
		int cx = this.width / 2;
		int pw = Math.min(320, this.width - 40);
		int px = cx - pw / 2;
		int py = this.height / 2 - 85;
		int ph = 175;
		fill(px - 5, py, px + pw + 5, py + ph, COLOR_PANEL_BG);
	}
	//#endif

	// Draw text labels
	//#if MC >= 1.20.0
	//$$ private void drawTexts(GuiGraphics g, int mouseX, int mouseY, float delta)
	//$$ {
	//$$     int cx = this.width / 2;
	//$$     int panelTopY = this.height / 2 - 85;
	//$$     // Title
	//$$     g.drawCenteredString(font, Component.literal("\u00a76\u00a7lEssential Auto Sprint"), cx, panelTopY + 8, COLOR_TITLE);
	//$$     // Version & dev
	//$$     g.drawCenteredString(font, Component.literal("\u00a77by GBX Team  \u2022  v" + org.gbxteam.eas.EssentialAutoSprint.MOD_VERSION), cx, panelTopY + 22, COLOR_SUBTITLE);
	//$$     // Divider line
	//$$     g.fill(cx - 100, panelTopY + 34, cx + 100, panelTopY + 35, 0x44FFFFFF);
	//$$ }
	//#elseif MC >= 1.17.0
	//$$ private void drawTexts(PoseStack pose, int mouseX, int mouseY, float delta)
	//$$ {
	//$$     int cx = this.width / 2;
	//$$     int panelTopY = this.height / 2 - 85;
	//$$     drawCenteredString(pose, font, "\u00a76\u00a7lEssential Auto Sprint", cx, panelTopY + 8, COLOR_TITLE);
	//$$     drawCenteredString(pose, font, "\u00a77by GBX Team  \u2022  v" + org.gbxteam.eas.EssentialAutoSprint.MOD_VERSION, cx, panelTopY + 22, COLOR_SUBTITLE);
	//$$     fill(pose, cx - 100, panelTopY + 34, cx + 100, panelTopY + 35, 0x44FFFFFF);
	//$$ }
	//#else
	private void drawTexts(int mouseX, int mouseY, float delta)
	{
		int cx = this.width / 2;
		int panelTopY = this.height / 2 - 85;
		drawCenteredString(font, "\u00a76\u00a7lEssential Auto Sprint", cx, panelTopY + 8, COLOR_TITLE);
		drawCenteredString(font, "\u00a77by GBX Team  \u2022  v" + org.gbxteam.eas.EssentialAutoSprint.MOD_VERSION, cx, panelTopY + 22, COLOR_SUBTITLE);
		fill(cx - 100, panelTopY + 34, cx + 100, panelTopY + 35, 0x44FFFFFF);
	}
	//#endif

	// ---- Key input for keybind capture -----------------------------------------

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (waitingForKey)
		{
			if (keyCode == GLFW.GLFW_KEY_ESCAPE)
			{
				// Cancel — keep old key
				waitingForKey = false;
			}
			else
			{
				EASConfig.INSTANCE.toggleKey = keyCode;
				EASConfig.INSTANCE.save();
				waitingForKey = false;
			}
			// Refresh buttons
			this.clearWidgets();
			this.init();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean shouldCloseOnEsc()
	{
		if (waitingForKey)
		{
			waitingForKey = false;
			this.clearWidgets();
			this.init();
			return false;
		}
		return true;
	}

	/**
	 * Compat helper: older Screen does not have clearWidgets().
	 * Use removeWidget or just re-init.
	 */
	//#if MC < 1.17.0
	//$$ private void clearWidgets() { this.children().clear(); this.narratables.clear(); this.renderables.clear(); }
	//#endif
}
