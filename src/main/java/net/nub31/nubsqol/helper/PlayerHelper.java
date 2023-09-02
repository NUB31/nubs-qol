package net.nub31.nubsqol.helper;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
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
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PlayerHelper {
	private final PlayerEntity player;

	public PlayerHelper(PlayerEntity player) {
		this.player = player;
	}

	public <Item> boolean isHoldingItem(Item item) {
		return (player.getStackInHand(Hand.MAIN_HAND).getItem() == item || player.getStackInHand(Hand.OFF_HAND).getItem() == item);
	}

	public <T extends Item> boolean isWearingItemOfType(EquipmentSlot equipmentSlot, Class<T> itemType) {
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

	public void sendStartFallFlyingPacket() {
		ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity) player;
		ClientPlayNetworkHandler networkHandler = clientPlayerEntity.networkHandler;

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
	}

	public Entity findMobInPlayerCrosshair(World world, ClientPlayerInteractionManager interactionManager) {
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
}
