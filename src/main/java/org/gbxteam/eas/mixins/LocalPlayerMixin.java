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
import net.minecraft.client.player.LocalPlayer;
import org.gbxteam.eas.EASConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into LocalPlayer's AI step to automatically enable sprinting
 * whenever the player is pressing forward (W key) and EAS is enabled.
 */
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin
{
	@Inject(method = "aiStep", at = @At("RETURN"))
	private void eas_onAiStep(CallbackInfo ci)
	{
		LocalPlayer self = (LocalPlayer) (Object) this;

		if (!EASConfig.INSTANCE.enabled) return;
		if (self.isSpectator()) return;

		//#if MC >= 11600
		boolean movingForward = self.input.forwardImpulse > 0.0F;
		//#else
		//$$ boolean movingForward = self.input.up;
		//#endif

		//#if MC >= 11700
		//$$ boolean flying = self.getAbilities().flying;
		//#else
		boolean flying = self.abilities.flying;
		//#endif

		if (movingForward && !flying)
		{
			self.setSprinting(true);
		}
	}
}
