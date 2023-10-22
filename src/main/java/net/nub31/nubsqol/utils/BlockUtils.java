package net.nub31.nubsqol.utils;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtils {
	private final World world;

	public BlockUtils(World world) {

		this.world = world;
	}

	public boolean isSolid(HitResult crosshairTarget) {
		if (crosshairTarget.getType() != HitResult.Type.BLOCK) return false;

		BlockHitResult blockHitResult = (BlockHitResult) crosshairTarget;
		BlockPos blockPos = blockHitResult.getBlockPos();

		return world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
	}

	public boolean isSolid(BlockPos blockPos) {
		return world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
	}
}