package com.cr3eperall.avaritiaalpha.items.tools;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.entity.HeavenArrowEntity;
import com.cr3eperall.avaritiaalpha.entity.ModEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;

public class InfinityBow extends BowItem {
    public InfinityBow() {
        super(new Properties().maxStackSize(1).group(AvaritiaAlpha.setup.itemGroup).maxDamage(9999).rarity(InfinityMaterial.COSMIC_RARITY));
        setRegistryName("infinity_bow");
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (count == 1) {
            fire(stack, player.world, player, 0);
        }
    }

    public void fire(ItemStack stack, World world, LivingEntity player, int useCount) {
        int max = getUseDuration(stack);
        float maxf = (float) max;
        int j = max - useCount/2;

        float f = j / maxf;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f < 0.1) {
            return;
        }

        if (f > 2.0) {
            f = 2.0F;
        }

        HeavenArrowEntity arrow = new HeavenArrowEntity(ModEntities.heavenArrowEntityEntityType, world);
        arrow.setShooter(player);
        arrow.setPosition(player.posX, player.posY + (double)player.getEyeHeight() - (double)0.1F, player.posZ);
        arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0, f * 3.0F, 1.0F);//TODO, no inaccuracy?
        arrow.setDamage(20.0);

        if (f == 1.0F) {
            arrow.setIsCritical(true);
        }

        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

        if (k > 0) {
            arrow.setDamage(arrow.getDamage() + k + 1);
        }

        int l = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

        if (l > 0) {
            arrow.setKnockbackStrength(l);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
            arrow.setFire(100);
        }

        //stack.damageItem(1, player);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F,  (1.0F / ((float)Math.random() * 0.4F + 1.2F) + f * 0.5F));

        arrow.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;

        if (!world.isRemote) {
            world.addEntity(arrow);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        fire(stack,worldIn,entityLiving,timeLeft);
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);
        ActionResult<ItemStack> event = ForgeEventFactory.onArrowNock(stack, worldIn, player, handIn, true);
        if (event != null) {
            return event;
        }

        player.setActiveHand(handIn);

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}
