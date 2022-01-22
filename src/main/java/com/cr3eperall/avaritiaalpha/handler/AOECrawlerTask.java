package com.cr3eperall.avaritiaalpha.handler;

import com.cr3eperall.avaritiaalpha.util.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.Set;

public class AOECrawlerTask {

    final World world;
    final PlayerEntity player;
    final ItemStack stack;
    final int steps;
    final BlockPos origin;
    final boolean leaves;
    final boolean force;
    final Set<BlockPos> posChecked;

    AOECrawlerTask(World world, PlayerEntity player, ItemStack stack, BlockPos origin, int steps, boolean leaves, boolean force, Set<BlockPos> posChecked) {
        this.world = world;
        this.player = player;
        this.stack = stack;
        this.origin = origin;
        this.steps = steps;
        this.leaves = leaves;
        this.force = force;
        this.posChecked = posChecked;
    }

    void tick() {
        BlockState originState = world.getBlockState(origin);
        Block originBlock = originState.getBlock();
        if (!force && originBlock.isAir(originState, world, origin)) {
            return;
        }
        ToolHelper.removeBlockWithDrops(player, stack, world, origin, null, ToolHelper.materialsAxe, true);
        if (steps == 0) {
            return;
        }
        for (Direction dir : Direction.values()) {
            BlockPos stepPos = origin.offset(dir);
            if (posChecked.contains(stepPos)) {
                continue;
            }
            BlockState stepState = world.getBlockState(stepPos);
            Block stepBlock = stepState.getBlock();
            boolean log = stepBlock.isIn(BlockTags.LOGS);
            boolean leaf = stepBlock.isIn(BlockTags.LEAVES);
            if (log || leaf) {
                int steps = this.steps - 1;
                steps = leaf ? leaves ? steps : 3 : steps;
                AvaritiaAlphaEventHandler.startCrawlerTask(world, player, stack, stepPos, steps, leaf, false, posChecked);
                posChecked.add(stepPos);
            }
        }
    }
}
