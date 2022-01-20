package com.cr3eperall.avaritiaalpha.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;

public class InfinityArmor extends ArmorItem {
    public InfinityArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    /*public static final ArmorMaterial infinite_armor = EnumHelper.addArmorMaterial("avaritia_infinity", "", 9999, new int[] { 6, 16, 12, 6 }, 1000, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F);
    public final EntityEquipmentSlot slot;

    public InfinityArmor(EntityEquipmentSlot slot) {
        super(infinite_armor, 0, slot);
        this.slot = slot;
        setCreativeTab(Avaritia.tab);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "avaritia:textures/models/infinity_armor.png";
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @SuppressWarnings ("rawtypes")
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (armorType == EntityEquipmentSlot.HEAD) {
            player.setAir(300);
            player.getFoodStats().addStats(20, 20F);
            PotionEffect nv = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
            if(nv == null) {
                nv = new PotionEffect(MobEffects.NIGHT_VISION, 300,  0, false, false);
                player.addPotionEffect(nv);
            }
            nv.duration = 300;
        } else if (armorType == EntityEquipmentSlot.CHEST) {
            player.capabilities.allowFlying = true;
            List<PotionEffect> effects = Lists.newArrayList(player.getActivePotionEffects());
            for (PotionEffect potion : Collections2.filter(effects, potion -> potion.getPotion().isBadEffect())) {
                if (ModHelper.isHoldingCleaver(player) && potion.getPotion().equals(MobEffects.MINING_FATIGUE)) {
                    continue;
                }
                player.removePotionEffect(potion.getPotion());
            }
        } else if (armorType == EntityEquipmentSlot.LEGS) {
            if (player.isBurning()) {
                player.extinguish();
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return ModItems.COSMIC_RARITY;
    }

    @Override
    @SideOnly (Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemstack, EntityEquipmentSlot armorSlot, ModelBiped _deafult) {
        ModelArmorInfinity model = armorSlot == EntityEquipmentSlot.LEGS ? ModelArmorInfinity.legModel : ModelArmorInfinity.armorModel;

        model.update(entityLiving, itemstack, armorSlot);

        return model;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (slot == EntityEquipmentSlot.FEET) {
            tooltip.add("");
            tooltip.add(TextFormatting.BLUE + "+" + TextFormatting.ITALIC + TextUtils.makeSANIC("SANIC") + TextFormatting.RESET + "" + TextFormatting.BLUE + "% Speed");
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }*/

}
