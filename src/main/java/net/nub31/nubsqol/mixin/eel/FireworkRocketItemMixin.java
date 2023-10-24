package net.nub31.nubsqol.mixin.eel;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.nub31.nubsqol.NubsQol;
import net.nub31.nubsqol.utils.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
abstract class FireworkRocketItemMixin {
	@Inject(at = @At("HEAD"), method = "use")
	private void useItem(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (world.isClient) {
			if (NubsQol.hasServerSupport && canUseEasyElytraLaunch(player)) {
				player.jump();
				PlayerUtils playerUtils = new PlayerUtils(player);
				playerUtils.sendStartFallFlyingPacket();
			}
		} else {
			player.startFallFlying();
		}
	}

	private boolean canUseEasyElytraLaunch(PlayerEntity player) {
		PlayerUtils playerUtils = new PlayerUtils(player);

		return playerUtils.isWearingItemOfType(EquipmentSlot.CHEST, ElytraItem.class)
				&& ElytraItem.isUsable(player.getEquippedStack(EquipmentSlot.CHEST))
				&& player.isOnGround()
				&& !player.isFallFlying()
				&& !player.inPowderSnow
				&& !player.isInLava();
	}
}
