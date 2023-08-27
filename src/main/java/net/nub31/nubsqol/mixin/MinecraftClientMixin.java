package net.nub31.nubsqol.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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


	@Inject(at = @At("HEAD"), method = "doAttack")
	private void doAttack(CallbackInfoReturnable<Boolean> cir) {
		if (this.world.isClient && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
			BlockPos blockPos = blockHitResult.getBlockPos();

			// Check if block is solid
			if (this.world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
				// Check for entities within the non-solid block's bounds
				Entity entityInBlock = findEntityInPlayerRange();

				if (entityInBlock != null) this.crosshairTarget = new EntityHitResult(entityInBlock);
			}
		}
	}

	// Find an entity in the player's range
	private Entity findEntityInPlayerRange() {
		float playerReachDistance = this.interactionManager.getReachDistance();

		Vec3d camera = this.player.getCameraPosVec(1.0F);
		Vec3d rotation = this.player.getRotationVec(1.0F);

		HitResult hitResult = this.world.raycast(
				new RaycastContext(
						camera,
						camera.add(
								rotation.x * playerReachDistance,
								rotation.y * playerReachDistance,
								rotation.z * playerReachDistance
						),
						RaycastContext.ShapeType.COLLIDER,
						RaycastContext.FluidHandling.NONE,
						this.player
				)
		);

		Vec3d end = hitResult.getType() != HitResult.Type.MISS
				? hitResult.getPos()
				: camera.add(
				rotation.x * playerReachDistance,
				rotation.y * playerReachDistance,
				rotation.z * playerReachDistance
		);

		EntityHitResult result = ProjectileUtil.getEntityCollision(
				world,
				this.player,
				camera,
				end,
				new Box(camera, end),
				// Don't attack spectators, non-hittable entities and pets of the player
				entity -> !entity.isSpectator() && entity.canHit() && !(entity instanceof Tameable tameable && tameable.getOwner().equals(player))
		);

		if (result != null) {
			return result.getEntity();
		} else {
			return null;
		}
	}
}