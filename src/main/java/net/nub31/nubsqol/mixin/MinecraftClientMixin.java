package net.nub31.nubsqol.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.nub31.nubsqol.Helpers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Shadow
	@Nullable
	public ClientWorld world;

	@Shadow
	@Nullable
	public HitResult crosshairTarget;

	@Shadow
	@Nullable
	public ClientPlayerInteractionManager interactionManager;

	public boolean skipBlockBreaking = false;

	@Inject(at = @At("HEAD"), method = "doItemUse")
	private void doItemUse(CallbackInfo ci) {

	}

	// If using firework rockets and targeting a non-solid block, ignore the non-solid block
	@Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
	private HitResult.Type getType(HitResult instance) {
		if (this.world.isClient && this.crosshairTarget.getType() == HitResult.Type.BLOCK && (this.player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.FIREWORK_ROCKET || this.player.getStackInHand(Hand.OFF_HAND).getItem() == Items.FIREWORK_ROCKET) && Helpers.isBlockSolid(this.world, this.crosshairTarget)) {
			return HitResult.Type.MISS;
		} else {
			return instance.getType();
		}
	}

	// Redirect attacks to mobs if a mob is present in a non-solid block
	@Inject(at = @At("HEAD"), method = "doAttack")
	private void doAttack(CallbackInfoReturnable<Boolean> cir) {
		skipBlockBreaking = false;

		if (this.world.isClient && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			if (Helpers.isBlockSolid(this.world, this.crosshairTarget)) {
				// Check for entities within the non-solid block's bounds
				Entity mobInPlayerRange = Helpers.findMobInPlayerRange(this.player, this.world, this.interactionManager);

				if (mobInPlayerRange != null) {
					skipBlockBreaking = true;
					this.crosshairTarget = new EntityHitResult(mobInPlayerRange);
				}
			}
		}
	}

	// Make sure blocks are not broken if a block attack is redirected to an entity
	@Inject(at = @At("HEAD"), method = "handleBlockBreaking", cancellable = true)
	private void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
		if (skipBlockBreaking) ci.cancel();
	}
}