package net.nub31.nubsqol;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class Helpers {
	public static Entity findMobInPlayerRange(PlayerEntity player, World world, ClientPlayerInteractionManager interactionManager) {
		float playerReachDistance = interactionManager.getReachDistance();

		Vec3d camera = player.getCameraPosVec(1.0F);
		Vec3d rotation = player.getRotationVec(1.0F);

		HitResult hitResult = world.raycast(
				new RaycastContext(
						camera,
						camera.add(
								rotation.x * playerReachDistance,
								rotation.y * playerReachDistance,
								rotation.z * playerReachDistance
						),
						RaycastContext.ShapeType.COLLIDER,
						RaycastContext.FluidHandling.NONE,
						player
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
				player,
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

	public static <T extends Item> boolean isWearingItemOfType(PlayerEntity player, EquipmentSlot equipmentSlot, Class<T> itemType) {
		ItemStack stack = player.getEquippedStack(equipmentSlot);

		if (itemType.isInstance(stack.getItem())) {
			if (itemType.equals(ElytraItem.class) && equipmentSlot == EquipmentSlot.CHEST) {
				return true;
			}

			if (stack.getItem() instanceof ArmorItem armorItem) {
				return itemType.isInstance(armorItem);
			}
		}

		return false;
	}

	public static boolean isBlockSolid(World world, HitResult hitResult) {
		BlockHitResult blockHitResult = (BlockHitResult) hitResult;
		BlockPos blockPos = blockHitResult.getBlockPos();

		return world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
	}
}
