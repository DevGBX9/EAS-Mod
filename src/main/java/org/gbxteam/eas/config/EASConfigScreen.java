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

//#if MC >= 12000
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
		return EASConfig.INSTANCE.enabled ? "\u00a7a\u25cf Auto Sprint: ON" : "\u00a7c\u25cb Auto Sprint: OFF";
	}

	private String getKeybindLabel()
	{
		return "Toggle Key: \u00a7e" + GLFW.glfwGetKeyName(EASConfig.INSTANCE.toggleKey, 0);
	}

	@Override
	protected void init()
	{
		super.init();
		int panelW = Math.min(320, this.width - 40);
		int panelX = (this.width - panelW) / 2;
		int startY = this.height / 2 - 60;

		addEASButton(panelX, startY, panelW, 20, getToggleLabel(), button -> {
			EASConfig.INSTANCE.enabled = !EASConfig.INSTANCE.enabled;
			EASConfig.INSTANCE.save();
			if (!EASConfig.INSTANCE.enabled && this.minecraft != null && this.minecraft.player != null)
				this.minecraft.player.setSprinting(false);
			this.minecraft.setScreen(new EASConfigScreen(this.parent));
		});

		String keybindLabel = waitingForKey ? "\u00a7ePress any key... (ESC to cancel)" : getKeybindLabel();
		addEASButton(panelX, startY + 28, panelW, 20, keybindLabel, button -> {
			EASConfigScreen next = new EASConfigScreen(this.parent);
			next.waitingForKey = true;
			this.minecraft.setScreen(next);
		});

		addEASButton(panelX, startY + 60, panelW, 20, "\u25b6  View on Modrinth", button -> {
			try { java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://modrinth.com/mod/essential-auto-sprint-(eas)")); }
			catch (Exception ignored) {}
		});

		addEASButton(panelX, startY + 88, panelW, 20, "Done", button -> this.minecraft.setScreen(this.parent));
	}

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

	// ─── Render ───────────────────────────────────────────────────────────────

	//#if MC >= 12000
	//$$ @Override
	//$$ public void render(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(g);
	//$$ 	int pw = Math.min(320, this.width - 40);
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 75;
	//$$ 	g.fill(px - 8, py - 8, px + pw + 8, py + 145 + 8, 0xCC111122);
	//$$ 	g.drawCenteredString(this.font, "\u00a7bEssential Auto Sprint", this.width / 2, py + 4, 0xFFFFFF);
	//$$ 	g.drawCenteredString(this.font, "\u00a77v" + EssentialAutoSprint.MOD_VERSION + " \u00a78by \u00a76GBX Team", this.width / 2, py + 16, 0xFFFFFF);
	//$$ 	super.render(g, mx, my, delta);
	//$$ }
	//#elseif MC >= 11600
	//$$ @Override
	//$$ public void render(PoseStack pose, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(pose);
	//$$ 	int pw = Math.min(320, this.width - 40);
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 75;
	//$$ 	fill(pose, px - 8, py - 8, px + pw + 8, py + 153, 0xCC111122);
	//$$ 	drawCenteredString(pose, this.font, "\u00a7bEssential Auto Sprint", this.width / 2, py + 4, 0xFFFFFF);
	//$$ 	drawCenteredString(pose, this.font, "\u00a77v" + EssentialAutoSprint.MOD_VERSION + " \u00a78by \u00a76GBX Team", this.width / 2, py + 16, 0xFFFFFF);
	//$$ 	super.render(pose, mx, my, delta);
	//$$ }
	//#else
	@Override
	public void render(int mx, int my, float delta)
	{
		renderBackground();
		int pw = Math.min(320, this.width - 40);
		int px = (this.width - pw) / 2;
		int py = this.height / 2 - 75;
		fill(px - 8, py - 8, px + pw + 8, py + 153, 0xCC111122);
		drawCenteredString(this.font, "\u00a7bEssential Auto Sprint", this.width / 2, py + 4, 0xFFFFFF);
		drawCenteredString(this.font, "\u00a77v" + EssentialAutoSprint.MOD_VERSION + " \u00a78by \u00a76GBX Team", this.width / 2, py + 16, 0xFFFFFF);
		super.render(mx, my, delta);
	}
	//#endif

	// ─── Button helper — handles all MC versions ──────────────────────────────

	private void addEASButton(int x, int y, int w, int h, String label, Button.OnPress action)
	{
		//#if MC >= 12000
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
