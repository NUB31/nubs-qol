package net.nub31.nubsqol.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {
	@Inject(at = @At("HEAD"), method = "use")
	private void useItem(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (isWearingElytra(player) && !player.inPowderSnow && !player.isFallFlying()) {
			// Client side code
			if (world.isClient && player.groundCollision) {
				player.jump();
			}

			// Server side code
			if (!world.isClient) {
				player.startFallFlying();
			}
		}
	}

	private boolean isWearingElytra(PlayerEntity player) {
		return player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem;
	}
}