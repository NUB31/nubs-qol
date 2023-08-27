package net.nub31.nubsqol.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.nub31.nubsqol.Helpers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
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


	// Redirect attacks to mobs if a mob is present in a non-solid block
	@Inject(at = @At("HEAD"), method = "doAttack")
	private void doAttack(CallbackInfoReturnable<Boolean> cir) {
		skipBlockBreaking = false;

		if (this.world.isClient) {
			switch (this.crosshairTarget.getType()) {
				case BLOCK:
					BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
					BlockPos blockPos = blockHitResult.getBlockPos();

					// Check if block is solid
					if (this.world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
						// Check for entities within the non-solid block's bounds
						Entity mobInPlayerRange = Helpers.findMobInPlayerRange(this.player, this.world, this.interactionManager);

						if (mobInPlayerRange != null) {
							skipBlockBreaking = true;
							this.crosshairTarget = new EntityHitResult(mobInPlayerRange);
						}
					}
					break;
			}
		}
	}

	// Make sure blocks are not broken if a block attack is redirected to an entity
	@Inject(at = @At("HEAD"), method = "handleBlockBreaking", cancellable = true)
	private void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
		if (skipBlockBreaking) ci.cancel();
	}
}