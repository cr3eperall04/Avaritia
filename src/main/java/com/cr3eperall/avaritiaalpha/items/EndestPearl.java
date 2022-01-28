package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.entity.EndestPearlEntity;
import com.cr3eperall.avaritiaalpha.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class EndestPearl extends EnderPearlItem {

    public EndestPearl() {
        super(new Properties().group(AvaritiaAlpha.setup.itemGroup).maxStackSize(64).rarity(Rarity.RARE));
        setRegistryName("endest_pearl");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.abilities.isCreativeMode) {
            stack.shrink(1);
        }

        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            EndestPearlEntity pearl = new EndestPearlEntity(ModEntities.endestPearlEntityEntityType, player, world);
            pearl.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.addEntity(pearl);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

}
