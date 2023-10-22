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
		if (canUseEasyElytraLaunch(player)) {
			if (NubsQol.hasServerSupport) {
				player.jump();
				player.startFallFlying();
			} else if (world.isClient) {
				PlayerUtils playerUtils = new PlayerUtils(player);
				playerUtils.sendStartFallFlyingPacket();
			}
		}
	}

	private boolean canUseEasyElytraLaunch(PlayerEntity player) {
		PlayerUtils playerUtils = new PlayerUtils(player);

		return playerUtils.isWearingItemOfType(EquipmentSlot.CHEST, ElytraItem.class)
				&& ElytraItem.isUsable(player.getEquippedStack(EquipmentSlot.CHEST))
				&& !player.isFallFlying()
				&& !player.inPowderSnow
				&& !player.isInLava();
	}
}
