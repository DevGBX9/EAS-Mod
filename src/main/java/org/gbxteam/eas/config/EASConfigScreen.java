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

//#if MC >= 1.19.0
//$$ import net.minecraft.network.chat.Component;
//#else
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
//#endif

//#if MC >= 1.20.0
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 1.17.0
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

import net.minecraft.client.gui.components.Button;

/**
 * In-game configuration screen for Essential Auto Sprint.
 * Accessible via Mod Menu. Handles toggle, keybind capture, and website link.
 */
@Environment(EnvType.CLIENT)
public class EASConfigScreen extends Screen
{
	private final Screen parent;
	// true while we are waiting for the player to press a new toggle key
	boolean waitingForKey = false;

	// ─── Constructor ──────────────────────────────────────────────────────────

	public EASConfigScreen(Screen parent)
	{
		//#if MC >= 1.19.0
		//$$ super(Component.translatable("eas.config.title"));
		//#else
		super(new TranslatableComponent("eas.config.title"));
		//#endif
		this.parent = parent;
	}

	// ─── Helpers ──────────────────────────────────────────────────────────────

	private String getToggleLabel()
	{
		return EASConfig.INSTANCE.enabled ? "§a● Auto Sprint: ON" : "§c○ Auto Sprint: OFF";
	}

	private String getKeybindLabel()
	{
		return "Toggle Key: §e" + GLFW.glfwGetKeyName(EASConfig.INSTANCE.toggleKey, 0);
	}

	private net.minecraft.network.chat.Component makeText(String s)
	{
		//#if MC >= 1.19.0
		//$$ return Component.literal(s);
		//#else
		return new TextComponent(s);
		//#endif
	}

	// ─── Init ─────────────────────────────────────────────────────────────────

	@Override
	protected void init()
	{
		super.init();

		int panelW = Math.min(320, this.width - 40);
		int panelX = (this.width - panelW) / 2;
		int startY = this.height / 2 - 70;

		// Toggle button — reopens screen so label always reflects current state
		addEASButton(panelX, startY, panelW, 20, getToggleLabel(), button -> {
			EASConfig.INSTANCE.enabled = !EASConfig.INSTANCE.enabled;
			EASConfig.INSTANCE.save();
			if (!EASConfig.INSTANCE.enabled && this.minecraft != null && this.minecraft.player != null)
			{
				this.minecraft.player.setSprinting(false);
			}
			this.minecraft.setScreen(new EASConfigScreen(this.parent));
		});

		// Keybind button — activates waiting state on click
		String keybindLabel = waitingForKey
				? "\u00a7ePress any key\u2026 (ESC to cancel)"
				: getKeybindLabel();

		addEASButton(panelX, startY + 28, panelW, 20, keybindLabel, button -> {
			EASConfigScreen next = new EASConfigScreen(this.parent);
			next.waitingForKey = true;
			this.minecraft.setScreen(next);
		});

		// Website button
		addEASButton(panelX, startY + 60, panelW, 20, "\uD83C\uDF10  View on Modrinth", button -> {
			try
			{
				java.awt.Desktop.getDesktop().browse(
						java.net.URI.create("https://modrinth.com/mod/essential-auto-sprint-(eas)")
				);
			}
			catch (Exception ignored) {}
		});

		// Done button
		addEASButton(panelX, startY + 100, panelW, 20, "Done", button ->
				this.minecraft.setScreen(this.parent));
	}

	// ─── Key handling ─────────────────────────────────────────────────────────

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
			// Reopen in normal state
			this.minecraft.setScreen(new EASConfigScreen(this.parent));
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	// ─── Rendering ────────────────────────────────────────────────────────────

	//#if MC >= 1.20.0
	//$$ @Override
	//$$ public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
	//$$ {
	//$$ 	this.renderBackground(graphics);
	//$$
	//$$ 	int panelW = Math.min(320, this.width - 40);
	//$$ 	int panelX = (this.width - panelW) / 2;
	//$$ 	int panelH = 160;
	//$$ 	int panelY = this.height / 2 - 85;
	//$$
	//$$ 	// Panel background
	//$$ 	graphics.fill(panelX - 8, panelY - 8, panelX + panelW + 8, panelY + panelH + 8, 0xCC000000);
	//$$ 	graphics.fill(panelX - 6, panelY - 6, panelX + panelW + 6, panelY + panelH + 6, 0xFF1A1A2E);
	//$$
	//$$ 	// Title
	//$$ 	graphics.drawCenteredString(this.font, "§bEssential Auto Sprint", this.width / 2, panelY + 4, 0xFFFFFF);
	//$$ 	graphics.drawCenteredString(this.font, "§7v" + EssentialAutoSprint.MOD_VERSION + " §8by §6GBX Team", this.width / 2, panelY + 16, 0xAAAAAA);
	//$$
	//$$ 	super.render(graphics, mouseX, mouseY, delta);
	//$$ }
	//#elseif MC >= 1.17.0
	//$$ @Override
	//$$ public void render(PoseStack pose, int mouseX, int mouseY, float delta)
	//$$ {
	//$$ 	this.renderBackground(pose);
	//$$
	//$$ 	int panelW = Math.min(320, this.width - 40);
	//$$ 	int panelX = (this.width - panelW) / 2;
	//$$ 	int panelH = 160;
	//$$ 	int panelY = this.height / 2 - 85;
	//$$
	//$$ 	fill(pose, panelX - 8, panelY - 8, panelX + panelW + 8, panelY + panelH + 8, 0xCC000000);
	//$$ 	fill(pose, panelX - 6, panelY - 6, panelX + panelW + 6, panelY + panelH + 6, 0xFF1A1A2E);
	//$$
	//$$ 	drawCenteredString(pose, this.font, "§bEssential Auto Sprint", this.width / 2, panelY + 4, 0xFFFFFF);
	//$$ 	drawCenteredString(pose, this.font, "§7v" + EssentialAutoSprint.MOD_VERSION + " §8by §6GBX Team", this.width / 2, panelY + 16, 0xAAAAAA);
	//$$
	//$$ 	super.render(pose, mouseX, mouseY, delta);
	//$$ }
	//#else
	@Override
	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();

		int panelW = Math.min(320, this.width - 40);
		int panelX = (this.width - panelW) / 2;
		int panelH = 160;
		int panelY = this.height / 2 - 85;

		fill(panelX - 8, panelY - 8, panelX + panelW + 8, panelY + panelH + 8, 0xCC000000);
		fill(panelX - 6, panelY - 6, panelX + panelW + 6, panelY + panelH + 6, 0xFF1A1A2E);

		drawCenteredString(this.font, "§bEssential Auto Sprint", this.width / 2, panelY + 4, 0xFFFFFF);
		drawCenteredString(this.font, "§7v" + EssentialAutoSprint.MOD_VERSION + " §8by §6GBX Team", this.width / 2, panelY + 16, 0xAAAAAA);

		super.render(mouseX, mouseY, delta);
	}
	//#endif

	// ─── Versioned button helper ───────────────────────────────────────────────

	private void addEASButton(int x, int y, int w, int h, String label, Button.OnPress action)
	{
		//#if MC >= 1.20.0
		//$$ this.addRenderableWidget(Button.builder(Component.literal(label), action).bounds(x, y, w, h).build());
		//#elseif MC >= 1.19.0
		//$$ this.addRenderableWidget(new Button(x, y, w, h, Component.literal(label), action));
		//#elseif MC >= 1.17.0
		//$$ this.addRenderableWidget(new Button(x, y, w, h, new TextComponent(label), action));
		//#elseif MC >= 1.16.0
		this.addWidget(new Button(x, y, w, h, new TextComponent(label), action));
		//#else
		//$$ this.addButton(new Button(x, y, w, h, label, action));
		//#endif
	}
}
