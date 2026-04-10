package org.gbxteam.eas.config;

import com.mojang.blaze3d.platform.InputConstants;
//#if MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import org.gbxteam.eas.EASConfig;
import org.lwjgl.glfw.GLFW;

//#if MC >= 11900
//$$ import net.minecraft.network.chat.Component;
//#else
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
//#endif

//#if MC >= 12000 && MC < 260000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

public class EASConfigScreen extends Screen
{
	private final Screen parent;
	private boolean waitingForKey = false;

	private final boolean tempEnabled;
	private final int tempKey;
	private final boolean isDirty;

	public EASConfigScreen(Screen parent, boolean tempEnabled, int tempKey, boolean isDirty)
	{
		//#if MC >= 11904
		//$$ super(Component.literal("EAS Config"));
		//#elseif MC >= 11700
		//$$ super(new TextComponent("EAS Config"));
		//#elseif MC >= 11600
		//$$ super(new TranslatableComponent("EAS Config"));
		//#else
		super(new TranslatableComponent("EAS Config"));
		//#endif
		this.parent = parent;
		this.tempEnabled = tempEnabled;
		this.tempKey = tempKey;
		this.isDirty = (tempEnabled != EASConfig.INSTANCE.enabled) || (tempKey != EASConfig.INSTANCE.toggleKey);
	}

	public EASConfigScreen(Screen parent)
	{
		this(parent, EASConfig.INSTANCE.enabled, EASConfig.INSTANCE.toggleKey, false);
	}

	private String getKeybindLabel()
	{
		if (this.tempKey == GLFW.GLFW_KEY_UNKNOWN)
			return "NONE";

		//#if MC >= 11900
		//$$ return InputConstants.Type.KEYSYM.getOrCreate(this.tempKey).getDisplayName().getString();
		//#else
		String keyName = org.lwjgl.glfw.GLFW.glfwGetKeyName(this.tempKey, 0);
		if (keyName != null && !keyName.isEmpty()) {
			return keyName.toUpperCase();
		}
		
		String name = InputConstants.Type.KEYSYM.getOrCreate(this.tempKey).getName();
		if (name != null) {
			if (name.startsWith("key.keyboard.")) name = name.substring(13);
			else if (name.startsWith("key.mouse.")) name = "Mouse " + name.substring(10);
			name = name.replace(".", " ");
			if (name.length() == 1) return name.toUpperCase();
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return "UNKNOWN";
		//#endif
	}

	private String getToggleLabel()
	{
		return this.tempEnabled ? "\u00a7a[ ON ]" : "\u00a7c[ OFF ]";
	}

	private void openUrl(String url)
	{
		try {
			//#if MC >= 11400
			net.minecraft.Util.getOperatingSystem().open(java.net.URI.create(url));
			//#else
			//$$ java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			//#endif
		} catch (Exception e) {
			try {
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			} catch (Exception e2) {
				org.gbxteam.eas.EssentialAutoSprint.LOGGER.error("Could not open URL: " + e2.getMessage());
			}
		}
	}

	@Override
	protected void init()
	{
		super.init();
		int pw = 240;
		int px = (this.width - pw) / 2;
		int py = this.height / 2 - 72;
		int btnW = 80;
		int btnX = px + pw - btnW - 8;

		addEASButton(btnX, py + 24, btnW, 20, getToggleLabel(), button -> {
			this.minecraft.setScreen(new EASConfigScreen(this.parent, !this.tempEnabled, this.tempKey, true));
		});

		String keybindLabel = waitingForKey ? "\u00a7e..." : getKeybindLabel();
		addEASButton(btnX, py + 48, btnW, 20, keybindLabel, button -> {
			EASConfigScreen next = new EASConfigScreen(this.parent, this.tempEnabled, this.tempKey, this.isDirty);
			next.waitingForKey = true;
			this.minecraft.setScreen(next);
		});

		addEASButton(btnX, py + 90, btnW, 20, "Open \u2192", button -> {
			openUrl("https://modrinth.com/mod/essential-auto-sprint-(eas)");
		});

		addEASButton(btnX, py + 114, btnW, 20, "Open \u2192", button -> {
			openUrl("https://github.com/DevGBX9/EAS-Mod/issues");
		});

		String closeLbl = isDirty ? "\u00a7lApply & Close" : "Close";
		addEASButton(this.width / 2 - 100, py + 154, 200, 20, closeLbl, button -> {
			if (isDirty) {
				EASConfig.INSTANCE.enabled = this.tempEnabled;
				EASConfig.INSTANCE.toggleKey = this.tempKey;
				EASConfig.INSTANCE.save();
				if (!EASConfig.INSTANCE.enabled && this.minecraft != null && this.minecraft.player != null)
					this.minecraft.player.setSprinting(false);
			}
			this.minecraft.setScreen(this.parent);
		});
	}

	//#if MC >= 12110
	//$$ @Override
	//$$ public boolean keyPressed(net.minecraft.client.input.KeyEvent event)
	//$$ {
	//$$ 	if (waitingForKey)
	//$$ 	{
	//$$ 		if (event.key() != GLFW.GLFW_KEY_ESCAPE)
	//$$ 			this.minecraft.setScreen(new EASConfigScreen(this.parent, this.tempEnabled, event.key(), true));
	//$$ 		else
	//$$ 			this.minecraft.setScreen(new EASConfigScreen(this.parent, this.tempEnabled, this.tempKey, this.isDirty));
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
				this.minecraft.setScreen(new EASConfigScreen(this.parent, this.tempEnabled, keyCode, true));
			else
				this.minecraft.setScreen(new EASConfigScreen(this.parent, this.tempEnabled, this.tempKey, this.isDirty));
			return true;
		}
		//#if MC >= 11400 && MC < 11500
		//$$ if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
		//$$ 	this.minecraft.setScreen(this.parent);
		//$$ 	return true;
		//$$ }
		//#endif
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	//#endif
	
	@Override
	public void onClose()
	{
		this.minecraft.setScreen(this.parent);
	}

	// ─── Render ───────────────────────────────────────────────────────────────

	//#if MC >= 260000
	//$$ @Override
	//$$ public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor g, int mx, int my, float delta)
	//$$ {
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 72;
	//$$ 	g.fill(px, py, px + pw, py + 144, 0xBB000000);
	//$$ 	g.fill(px - 1, py - 1, px + pw + 1, py, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py + 144, px + pw + 1, py + 145, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py, px, py + 144, 0x44FFFFFF);
	//$$ 	g.fill(px + pw, py, px + pw + 1, py + 144, 0x44FFFFFF);
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
	//$$ 			drawS.invoke(g, this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF, true);
	//$$ 		} else if (draw != null) {
	//$$ 			draw.invoke(g, this.font, "General", px + 8, py + 8, 0xFFAAAAAA);
	//$$ 			draw.invoke(g, this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF);
	//$$ 			draw.invoke(g, this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF);
	//$$ 			draw.invoke(g, this.font, "Links", px + 8, py + 74, 0xFFAAAAAA);
	//$$ 			draw.invoke(g, this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF);
	//$$ 			draw.invoke(g, this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF);
	//$$ 		}
	//$$ 	} catch (Exception ignored) {}
	//$$ 	super.extractRenderState(g, mx, my, delta);
	//$$ }
	//#elseif MC >= 12102
	//$$ // In 1.21.2+, overriding renderBackground avoids double-blur while drawing under widgets.
	//$$ @Override
	//$$ public void renderBackground(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	super.renderBackground(g, mx, my, delta);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 72;
	//$$ 	g.fill(px, py, px + pw, py + 144, 0xBB000000);
	//$$ 	g.fill(px - 1, py - 1, px + pw + 1, py, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py + 144, px + pw + 1, py + 145, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py, px, py + 144, 0x44FFFFFF);
	//$$ 	g.fill(px + pw, py, px + pw + 1, py + 144, 0x44FFFFFF);
	//$$ 	g.drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	g.drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF, true);
	//$$ }
	//#elseif MC >= 12002
	//$$ @Override
	//$$ public void render(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(g, mx, my, delta);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 72;
	//$$ 	g.fill(px, py, px + pw, py + 144, 0xBB000000);
	//$$ 	g.fill(px - 1, py - 1, px + pw + 1, py, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py + 144, px + pw + 1, py + 145, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py, px, py + 144, 0x44FFFFFF);
	//$$ 	g.fill(px + pw, py, px + pw + 1, py + 144, 0x44FFFFFF);
	//$$ 	g.drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	g.drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF, true);
	//$$ 	super.render(g, mx, my, delta);
	//$$ }
	//#elseif MC >= 12000
	//$$ @Override
	//$$ public void render(GuiGraphics g, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(g);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 72;
	//$$ 	g.fill(px, py, px + pw, py + 144, 0xBB000000);
	//$$ 	g.fill(px - 1, py - 1, px + pw + 1, py, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py + 144, px + pw + 1, py + 145, 0x44FFFFFF);
	//$$ 	g.fill(px - 1, py, px, py + 144, 0x44FFFFFF);
	//$$ 	g.fill(px + pw, py, px + pw + 1, py + 144, 0x44FFFFFF);
	//$$ 	g.drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	g.drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA, true);
	//$$ 	g.fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	g.drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF, true);
	//$$ 	g.drawString(this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF, true);
	//$$ 	super.render(g, mx, my, delta);
	//$$ }
	//#elseif MC >= 11600
	//$$ @Override
	//$$ public void render(PoseStack pose, int mx, int my, float delta)
	//$$ {
	//$$ 	renderBackground(pose);
	//$$ 	int pw = 240;
	//$$ 	int px = (this.width - pw) / 2;
	//$$ 	int py = this.height / 2 - 72;
	//$$ 	fill(pose, px, py, px + pw, py + 144, 0xBB000000);
	//$$ 	fill(pose, px - 1, py - 1, px + pw + 1, py, 0x44FFFFFF);
	//$$ 	fill(pose, px - 1, py + 144, px + pw + 1, py + 145, 0x44FFFFFF);
	//$$ 	fill(pose, px - 1, py, px, py + 144, 0x44FFFFFF);
	//$$ 	fill(pose, px + pw, py, px + pw + 1, py + 144, 0x44FFFFFF);
	//$$ 	drawCenteredString(pose, this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "General", px + 8, py + 8, 0xFFAAAAAA);
	//$$ 	fill(pose, px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
	//$$ 	drawString(pose, this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "Links", px + 8, py + 74, 0xFFAAAAAA);
	//$$ 	fill(pose, px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
	//$$ 	drawString(pose, this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF);
	//$$ 	drawString(pose, this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF);
	//$$ 	super.render(pose, mx, my, delta);
	//$$ }
	//#else
	@Override
	public void render(int mx, int my, float delta)
	{
		renderBackground();
		int pw = 240;
		int px = (this.width - pw) / 2;
		int py = this.height / 2 - 72;
		fill(px, py, px + pw, py + 144, 0xBB000000);
		fill(px - 1, py - 1, px + pw + 1, py, 0x44FFFFFF); // border
		fill(px - 1, py + 144, px + pw + 1, py + 145, 0x44FFFFFF); // border
		fill(px - 1, py, px, py + 144, 0x44FFFFFF); // border
		fill(px + pw, py, px + pw + 1, py + 144, 0x44FFFFFF); // border
		drawCenteredString(this.font, "\u00a7lESSENTIAL AUTO SPRINT", this.width / 2, py - 20, 0xFFFFFFFF);
		drawString(this.font, "General", px + 8, py + 8, 0xFFAAAAAA);
		fill(px + 8, py + 18, px + pw - 8, py + 19, 0x55FFFFFF);
		drawString(this.font, "Mod Status", px + 8, py + 24 + 6, 0xFFFFFFFF);
		drawString(this.font, "Shortcut Key", px + 8, py + 48 + 6, 0xFFFFFFFF);
		drawString(this.font, "Links", px + 8, py + 74, 0xFFAAAAAA);
		fill(px + 8, py + 84, px + pw - 8, py + 85, 0x55FFFFFF);
		drawString(this.font, "Modrinth Page", px + 8, py + 90 + 6, 0xFFFFFFFF);
		drawString(this.font, "Report an Issue", px + 8, py + 114 + 6, 0xFFFFFFFF);
		super.render(mx, my, delta);
	}
	//#endif

	// ─── Button helper ─ handles all MC versions ─────────────────────────────────

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
