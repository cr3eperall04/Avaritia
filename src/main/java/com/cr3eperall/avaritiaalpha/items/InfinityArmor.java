package com.cr3eperall.avaritiaalpha.items;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.items.tools.InfinityMaterial;
import com.cr3eperall.avaritiaalpha.render.entity.InfinityArmorModel;
import com.cr3eperall.avaritiaalpha.util.TextUtils;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.List;

public class InfinityArmor extends ArmorItem {
    public InfinityArmor(EquipmentSlotType slot) {
        super(new IArmorMaterial() {
            @Override
            public int getDurability(EquipmentSlotType equipmentSlotType) {
                return 9999;
            }

            @Override
            public int getDamageReductionAmount(EquipmentSlotType equipmentSlotType) {
                switch (equipmentSlotType){
                    case MAINHAND:
                    case OFFHAND:
                        return 0;
                    case FEET:
                    case HEAD:
                        return 6;
                    case LEGS:
                        return 12;
                    case CHEST:
                        return 16;
                }
                return 0;
            }

            @Override
            public int getEnchantability() {
                return 1000;
            }

            @Override
            public SoundEvent getSoundEvent() {
                return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
            }

            @Override
            public Ingredient getRepairMaterial() {
                return null;
            }

            @Override
            public String getName() {
                return "avaritiaalpha_infinity";
            }

            @Override
            public float getToughness() {
                return 1;
            }
        }, slot, new Properties().rarity(InfinityMaterial.COSMIC_RARITY).group(AvaritiaAlpha.setup.itemGroup));

    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return "avaritiaalpha:textures/models/infinity_armor.png";
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }


    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (slot == EquipmentSlotType.HEAD) {
            player.setAir(300);
            player.getFoodStats().addStats(20, 20F);
            EffectInstance nv = player.getActivePotionEffect(Effects.NIGHT_VISION);
            if(nv == null) {
                nv = new EffectInstance(Effects.NIGHT_VISION, 300,  0, false, false);
                player.addPotionEffect(nv);
            }
            nv.combine(new EffectInstance(Effects.NIGHT_VISION, 300,  0, false, false));
        } else if (slot == EquipmentSlotType.CHEST) {
            player.abilities.allowFlying=true;
            List<EffectInstance> effects = Lists.newArrayList(player.getActivePotionEffects());
            for (EffectInstance potion : Collections2.filter(effects, potion -> !potion.getPotion().isBeneficial())) {
                //if (ModHelper.isHoldingCleaver(player) && potion.getPotion().equals(MobEffects.MINING_FATIGUE)) {
                //    continue;
                //}
                player.removePotionEffect(potion.getPotion());
            }
        } else if (slot == EquipmentSlotType.LEGS) {
            if (player.isBurning()) {
                player.extinguish();
            }
        }
    }

    @Nullable
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        InfinityArmorModel model = armorSlot == EquipmentSlotType.LEGS ? InfinityArmorModel.legModel : InfinityArmorModel.armorModel;

        model.update(entityLiving, itemStack, armorSlot);
        return (A)model;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (slot == EquipmentSlotType.FEET) {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(TextUtils.makeSANIC("SANIC").applyTextStyle(TextFormatting.ITALIC).appendSibling(new StringTextComponent("% Speed").applyTextStyles(TextFormatting.BLUE)));
        }
        super.addInformation(stack, world, tooltip, flag);
    }
}
