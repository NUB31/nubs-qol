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
    private void useItem(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem && user.isSneaking() && !user.isFallFlying()) {
            if (world.isClient) user.jump();
            // Has to be done on the server unfortunately
            if (!world.isClient) user.startFallFlying();
        }
    }
}