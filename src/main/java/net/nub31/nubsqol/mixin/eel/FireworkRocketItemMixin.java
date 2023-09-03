package net.nub31.nubsqol.mixin.eel;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.nub31.nubsqol.NubsQol;
import net.nub31.nubsqol.helper.PlayerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
abstract class FireworkRocketItemMixin {
	@Unique
	public boolean playerNotifiedOfInstallationStatus = false;

	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	private void useItem(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (!playerNotifiedOfInstallationStatus) {
			switch (NubsQol.RUNNING_CONFIG) {
				case CLIENT_ONLY -> {
					player.sendMessage(Text.literal("Nub's Qol mods is installed client-side. Double Right-click to initiate Easy Elytra Launch™"));
				}
				case CLIENT_AND_SERVER -> {
					player.sendMessage(Text.literal("Nub's Qol mods is installed server/client side. Right-click to initiate Easy Elytra Launch™"));
				}
			}
			playerNotifiedOfInstallationStatus = true;
		}

		switch (NubsQol.RUNNING_CONFIG) {
			case CLIENT_ONLY -> {
				if (world.isClient && canUseEasyElytraLaunch(player)) {
					if (player.isOnGround()) {
						player.refreshPositionAndAngles(
								player.getX(),
								player.getY() + 2.0,
								player.getZ(),
								player.getYaw(),
								player.getPitch()
						);

						cir.setReturnValue(TypedActionResult.pass(player.getStackInHand(hand)));
					}

					PlayerHelper playerHelper = new PlayerHelper(player);
					playerHelper.sendStartFallFlyingPacket();
				}
			}
			case SERVER_ONLY -> {
			}
			case CLIENT_AND_SERVER -> {
				if (canUseEasyElytraLaunch(player)) {
					if (world.isClient && player.isOnGround()) player.jump();
					if (!world.isClient) player.startFallFlying();
				}
			}
		}
	}

	private boolean canUseEasyElytraLaunch(PlayerEntity player) {
		PlayerHelper playerHelper = new PlayerHelper(player);

		return playerHelper.isWearingItemOfType(EquipmentSlot.CHEST, ElytraItem.class)
				&& ElytraItem.isUsable(player.getEquippedStack(EquipmentSlot.CHEST))
				&& !player.isFallFlying()
				&& !player.inPowderSnow
				&& !player.isInLava();
	}
}
