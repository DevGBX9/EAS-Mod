/*
 * This file is part of the Essential Auto Sprint (EAS) project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  GBX Team and contributors
 */

package org.gbxteam.eas.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import org.gbxteam.eas.EASConfig;
import org.gbxteam.eas.EssentialAutoSprint;
import org.lwjgl.glfw.GLFW;

//#if MC >= 11900
//$$ import net.minecraft.network.chat.Component;
//#else
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
//#endif

//#if MC >= 260000
//$$ // No graphics class to import, Rendering logic uses UI render state pipeline now
//#elseif MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

import net.minecraft.client.gui.components.Button;

@Environment(EnvType.CLIENT)
public class EASConfigScreen extends Screen
{
	private final Screen parent;
	boolean waitingForKey = false;

	public EASConfigScreen(Screen parent)
	{
		//#if MC >= 11900
		//$$ super(Component.translatable("eas.config.title"));
		//#else
		super(new TranslatableComponent("eas.config.title"));
		//#endif
		this.parent = parent;
	}

	private String getToggleLabel()
	{
		return EASConfig.INSTANCE.enabled ? "\u00a7a[ ON ]" : "\u00a7c[ OFF ]";
	}

	private String getKeybindLabel()
	{
		String key = GLFW.glfwGetKeyName(EASConfig.INSTANCE.toggleKey, 0);
		if (key == null) key = "?";
		return "\u00a7b" + key.toUpperCase();
	}

	private void openModrinth()
	{
		try {
			Class<?> utilClass = Class.forName("net.minecraft.Util");
			Object os;
			try { os = utilClass.getMethod("getPlatform").invoke(null); }
			catch (Exception e) { os = utilClass.getMethod("getOperatingSystem").invoke(null); }
			try { os.getClass().getMethod("openUri", java.net.URI.class).invoke(os, java.net.URI.create("https://modrinth.com/mod/essential-auto-sprint-(eas)")); }
			catch (Exception e) { os.getClass().getMethod("openUri", String.class).invoke(os, "https://modrinth.com/mod/essential-auto-sprint-(eas)"); }
		} catch (Exception e) {
			try { java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://modrinth.com/mod/essential-auto-sprint-(eas)")); } catch (Exception ignored) {}
		}
	}

	@Override
	protected void init()
	{
		super.init();
		int pw = 240;
		int px = (this.width - pw) / 2;
		int py = this.height / 2 - 60;
		int btnW = 80;
		int btnX = px + pw - btnW - 8;

		addEASButton(btnX, py + 24, btnW, 20, getToggleLabel(), button -> {
			EASConfig.INSTANCE.enabled = !EASConfig.INSTANCE.enabled;
			EASConfig.INSTANCE.save();
			if (!EASConfig.INSTANCE.enabled && this.minecraft != null && this.minecraft.player != null)
				this.minecraft.player.setSprinting(false);
			this.minecraft.setScreen(new EASConfigScreen(this.parent));
		});

		String keybindLabel = waitingForKey ? "\u00a7e..." : getKeybindLabel();
		addEASButton(btnX, py + 48, btnW, 20, keybindLabel, button -> {
			EASConfigScreen next = new EASConfigScreen(this.parent);
			next.waitingForKey = true;
			this.minecraft.setScreen(next);
		});

		addEASButton(btnX, py + 90, btnW, 20, "Open \u2192", button -> {
			openModrinth();
		});

		addEASButton(this.width / 2 - 100, py + 130, 200, 20, "Close", button -> this.minecraft.setScreen(this.parent));
	}

	//#if MC >= 12110
	//$$ @Override
	//$$ public boolean keyPressed(net.minecraft.client.input.KeyEvent event)
	//$$ {
	//$$ 	if (waitingForKey)
	//$$ 	{
	//$$ 		if (event.key() != GLFW.GLFW_KEY_ESCAPE)
	//$$ 		{
	//$$ 			EASConfig.INSTANCE.toggleKey = event.key();
	//$$ 			EASConfig.INSTANCE.save();
	//$$ 		}
	//$$ 		this.minecraft.setScreen(new EASConfigScreen(this.parent));
	//$$ 		return true;
	//$$ 	}
	//$$ 	return super.keyPressed(event);
	//$$ }
	//#else
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (waitingForKey)
		{
			if (keyCode != GLFW.GLFW_KEY_ESCAPE)
			{
				EASConfig.INSTANCE.toggleKey = keyCode;
				EASConfig.INSTANCE.save();
			}
			this.minecraft.setScreen(new EASConfigScreen(this.parent));
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	//#endif

	// ─── Render ───────────────────────────────────────────────────────────────

	//#if MC >= 260000
	//$$ @Override
	//$$ public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor g, int mx, int my, float delta)
	//$$ {
	//$$ 	super.extractRenderState(g, mx, my, delta);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 60;
	//$$ 	g.fill(px, py, px + pw, py + 120, 0xBB000000);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	try {
	//$$ 		java.lang.reflect.Method drawC = null, drawS = null, draw = null;
	//$$ 		for (java.lang.reflect.Method m : g.getClass().getMethods()) {
	//$$ 			Class<?>[] p = m.getParameterTypes();
	//$$ 			if (p.length >= 5 && p[0].isAssignableFrom(this.font.getClass()) && p[1] == String.class && p[2] == int.class && p[3] == int.class && p[4] == int.class) {
	//$$ 				if (m.getName().toLowerCase().contains("centered")) drawC = m;
	//$$ 				else if (p.length == 6 && p[5] == boolean.class) drawS = m;
	//$$ 				else draw = m;
	//$$ 			}
	//$$ 		}
	//$$ 		if (drawC != null) {
	//$$ 			if (drawC.getParameterCount() == 5) drawC.invoke(g, this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 			else if (drawC.getParameterCount() == 6) drawC.invoke(g, this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF, true);
	//$$ 		} else if (drawS != null) {
	//$$ 			drawS.invoke(g, this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2 - this.font.width("\u00a7lESSENTIAL AUTO SPRINT") / 2, py - 20, 0xFFFFFFFF, true);
	//$$ 		}
	//$$ 		if (drawS != null) {
	//$$ 			drawS.invoke(g, this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 			drawS.invoke(g, this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 			drawS.invoke(g, this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 			drawS.invoke(g, this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 			drawS.invoke(g, this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ 		} else if (draw != null) {
	//$$ 			draw.invoke(g, this.font, "General", px + 8, py + 8, 0xFFAAAAAA);
	//$$ 			draw.invoke(g, this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF);
	//$$ 			draw.invoke(g, this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF);
	//$$ 			draw.invoke(g, this.font, "Links", px + 8, py + 74, 0xFFAAAAAA);
	//$$ 			draw.invoke(g, this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF);
	//$$ 		}
	//$$ 	} catch (Exception ignored) {}
	//$$ }
	//#elseif MC >= 12102
	//$$ // In 1.21.2+, overriding renderBackground avoids double-blur while drawing under widgets.
	//$$ @Override
	//$$ public void renderBackground(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	super.renderBackground(g, mx, my, delta);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 60;
	//$$ 	g.fill(px, py, px + pw, py + 120, 0xBB000000);
	//$$ 	g.drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	g.drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ }
	//#elseif MC >= 12002
	//$$ @Override
	//$$ public void render(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(g, mx, my, delta);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 60;
	//$$ 	g.fill(px, py, px + pw, py + 120, 0xBB000000);
	//$$ 	g.drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	g.drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ 	super.render(g, mx, my, delta);
	//$$ }
	//#elseif MC >= 12000
	//$$ @Override
	//$$ public void render(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(g);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 60;
	//$$ 	g.fill(px, py, px + pw, py + 120, 0xBB000000);
	//$$ 	g.drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	g.drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ 	super.render(g, mx, my, delta);
	//$$ }
	//#elseif MC >= 11600
	//$$ @Override
	//$$ public void render(PoseStack pose, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(pose);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 60;
	//$$ 	fill(pose, px, py, px + pw, py + 120, 0xBB000000);
	//$$ 	drawCenteredString(pose, this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "General", px + 8, py + 8, 0xFFAAAAAA);
	//$$ 	fill(pose, px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	drawString(pose, this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "Links", px + 8, py + 74, 0xFFAAAAAA);
	//$$ 	fill(pose, px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	drawString(pose, this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF);
	//$$ 	super.render(pose, mx, my, delta);
	//$$ }
	//#else
	@Override
	public void render(int mx, int my, float delta)
	{
		renderBackground();
		int pw = 240;
		int px = (this.width - pw) / 2;
		int py = this.height / 2 - 60;
		fill(px, py, px + pw, py + 120, 0xBB000000);
		drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
		drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA);
		fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
		drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF);
		drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF);
		drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA);
		fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
		drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF);
		super.render(mx, my, delta);
	}
	//#endif

	// ─── Button helper — handles all MC versions ──────────────────────────────

	private void addEASButton(int x, int y, int w, int h, String label, Button.OnPress action)
	{
		//#if MC >= 11904
		//$$ this.addRenderableWidget(Button.builder(Component.literal(label), action).bounds(x, y, w, h).build());
		//#elseif MC >= 11900
		//$$ this.addRenderableWidget(new Button(x, y, w, h, Component.literal(label), action));
		//#elseif MC >= 11700
		//$$ this.addRenderableWidget(new Button(x, y, w, h, new TextComponent(label), action));
		//#elseif MC >= 11600
		//$$ this.addWidget(new Button(x, y, w, h, new TextComponent(label), action));
		//#else
		this.addButton(new Button(x, y, w, h, label, action));
		//#endif
	}
}
