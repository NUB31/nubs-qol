package net.nub31.nubsqol.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Nullable
    public ClientWorld world;

    // Redirect the attack behavior to handle custom interactions
    @Redirect(
            method = "doAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 0
            )
    )
    private BlockState onAttack(ClientWorld world, BlockPos pos) {
        // Get the block state at the specified position
        BlockState blockState = world.getBlockState(pos);

        // If the block is air, no further action is needed
        if (blockState.isAir()) {
            return blockState;
        }

        // Find an interactable entity, if present
        Entity target = findInteractableEntity(blockState, world, pos);

        if (target != null) {
            // Attack the interactable entity
            this.interactionManager.attackEntity(this.player, target);
            // Set the block state to air, ignoring the original block
            return Blocks.AIR.getDefaultState();
        }

        // Return the original block state if no interaction is performed
        return blockState;
    }


    // Redirect item use behavior to handle custom interactions
    @Redirect(
            method = "doItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
            )
    )
    private ActionResult onItemUse(
            ClientPlayerInteractionManager instance,
            ClientPlayerEntity player,
            Hand hand,
            BlockHitResult hitResult
    ) {
        // Find an interactable entity, if present
        Entity target = findInteractableEntity(
                this.world.getBlockState(hitResult.getBlockPos()),
                this.world,
                hitResult.getBlockPos()
        );

        if (target != null) {
            // Attempt a custom interaction with the entity
            ActionResult actionResult = this.interactionManager.interactEntityAtLocation(
                    this.player,
                    target,
                    new EntityHitResult(target),
                    hand
            );

            // If the custom interaction is not accepted, try a different interaction
            if (!actionResult.isAccepted()) {
                actionResult = this.interactionManager.interactEntity(this.player, target, hand);
            }

            // If still not accepted, return a pass result
            if (!actionResult.isAccepted()) {
                return ActionResult.PASS;
            }

            // If the custom interaction requires a hand swing, perform it and return a fail result
            if (actionResult.shouldSwingHand()) {
                this.player.swingHand(hand);
                return ActionResult.FAIL;
            }
        } else if (player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET) {
            // If the block has a collision shape, it can't be interacted with
            if (!isBlockSolid(this.world.getBlockState(hitResult.getBlockPos()), hitResult.getBlockPos())) {

                ActionResult actionResult = this.interactionManager.interactItem(player, hand);

                // If still not accepted, return a pass result
                if (!actionResult.isAccepted()) {
                    return ActionResult.PASS;
                }

                // If the custom interaction requires a hand swing, perform it and return a fail result
                if (actionResult.shouldSwingHand()) {
                    this.player.swingHand(hand);
                    return ActionResult.FAIL;
                }
            }


        }

        // If no custom interaction, perform the default block interaction
        return this.interactionManager.interactBlock(this.player, hand, hitResult);
    }


    // Find an interactable entity based on the block state
    private Entity findInteractableEntity(BlockState blockState, World world, BlockPos pos) {
        // If the block has a collision shape, it can't be interacted with
        if (isBlockSolid(blockState, pos)) return null;

        // Get the player's reach distance
        float playerReachDistance = this.interactionManager.getReachDistance();

        // Compute the camera and rotation vectors for raycasting
        Vec3d camera = this.player.getCameraPosVec(1.0F);
        Vec3d rotation = this.player.getRotationVec(1.0F);

        // Perform a raycast to find an entity
        HitResult hitResult = world.raycast(
                new RaycastContext(
                        camera,
                        camera.add(
                                rotation.x * playerReachDistance,
                                rotation.y * playerReachDistance,
                                rotation.z * playerReachDistance
                        ),
                        ShapeType.COLLIDER,
                        FluidHandling.NONE,
                        this.player
                )
        );

        // Determine the endpoint for the raycast
        Vec3d end = hitResult.getType() != Type.MISS
                ? hitResult.getPos()
                : camera.add(
                rotation.x * playerReachDistance,
                rotation.y * playerReachDistance,
                rotation.z * playerReachDistance
        );

        // Find a collision with an entity
        EntityHitResult result = ProjectileUtil.getEntityCollision(
                world,
                this.player,
                camera,
                end,
                new Box(camera, end),
                entity -> shouldAttackEntity(entity)
        );

        // Return the found entity or null
        return (result != null) ? result.getEntity() : null;
    }


    // Determine whether an entity should be attacked based on certain conditions
    private boolean shouldAttackEntity(Entity entity) {
        // Don't attack spectators or entities that cannot be damaged
        if (entity.isSpectator() || !entity.canHit()) {
            return false;
        }

        // Don't attack pets of the player
        if (entity instanceof Tameable tameableEntity) {
            LivingEntity owner = tameableEntity.getOwner();
            return owner == null || !owner.equals(this.player);
        }

        // Attack all other entities
        return true;
    }

    private boolean isBlockSolid(BlockState blockState, BlockPos pos) {
        // If the block has a collision shape, it can't be interacted with
        return !blockState.getCollisionShape(world, pos).isEmpty();
    }
}