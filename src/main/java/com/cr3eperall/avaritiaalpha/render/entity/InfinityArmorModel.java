package com.cr3eperall.avaritiaalpha.render.entity;

import codechicken.lib.math.MathHelper;
import codechicken.lib.texture.TextureUtils;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import com.cr3eperall.avaritiaalpha.render.ColourHelper;
import com.cr3eperall.avaritiaalpha.render.shader.CosmicShaderHelper;
import com.cr3eperall.avaritiaalpha.setup.AvaritiaAlphaTextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Random;

public class InfinityArmorModel extends BipedModel<LivingEntity> {

    public static final InfinityArmorModel armorModel = new InfinityArmorModel(1.0f);
    public static final InfinityArmorModel legModel = new InfinityArmorModel(0.5f).setLegs(true);

    public static ResourceLocation eyeTex = new ResourceLocation("avaritiaalpha", "textures/models/infinity_armor_eyes.png");
    public static ResourceLocation wingTex = new ResourceLocation("avaritiaalpha", "textures/models/infinity_armor_wing.png");
    public static ResourceLocation wingGlowTex = new ResourceLocation("avaritiaalpha", "textures/models/infinity_armor_wingglow.png");
    public static int itempagewidth = 0;
    public static int itempageheight = 0;
    public boolean legs = false;

    public EquipmentSlotType currentSlot = EquipmentSlotType.HEAD;

    private Random randy = new Random();

    private Overlay overlay;
    private Overlay invulnOverlay;
    private boolean invulnRender = true;
    private boolean showHat;
    private boolean showChest;
    private boolean showLeg;
    private boolean showFoot;

    private float expand;

    public RendererModel bipedLeftWing;
    public RendererModel bipedRightWing;

    public InfinityArmorModel(float expand) {
        super(expand, 0, 64, 64);
        this.expand = expand;
        overlay = new Overlay(this, expand);
        invulnOverlay = new Overlay(this, 0);

        bipedHeadwear = new RendererModel(this, 32, 0);
        bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, expand * 0.5F);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F + 0, 0.0F);
    }

    public InfinityArmorModel setLegs(boolean islegs) {
        legs = islegs;

        int heightoffset = 0;
        int legoffset = islegs ? 32 : 0;

        bipedBody = new RendererModel(this, 16, 16 + legoffset);
        bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, expand);
        bipedBody.setRotationPoint(0.0F, 0.0F + heightoffset, 0.0F);
        bipedRightLeg = new RendererModel(this, 0, 16 + legoffset);
        bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, expand);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F + heightoffset, 0.0F);
        bipedLeftLeg = new RendererModel(this, 0, 16 + legoffset);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, expand);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F + heightoffset, 0.0F);

        return this;
    }

    public void rebuildWings() {

        // remove the old items from the list so that the new ones don't just stack up
        if (bipedBody.childModels == null) {
            bipedBody.childModels = new ArrayList();
        }
        if (bipedLeftWing != null) {
            bipedBody.childModels.remove(bipedLeftWing);
        }
        if (bipedRightWing != null) {
            bipedBody.childModels.remove(bipedRightWing);
        }

        // define new
        bipedLeftWing = new WingModelRenderer(this, 0, 0);
        bipedLeftWing.mirror = true;
        bipedLeftWing.addBox(0f, -11.6f, 0f, 0, 32, 32);
        bipedLeftWing.setRotationPoint(-1.5f, 0.0f, 2.0f);
        bipedLeftWing.rotateAngleY = (float) (Math.PI * 0.4);
        bipedBody.addChild(bipedLeftWing);

        bipedRightWing = new WingModelRenderer(this, 0, 0);
        bipedRightWing.mirror=true;
        bipedRightWing.addBox(0f, -11.6f, 0f, 0, 32, 32);
        bipedRightWing.setRotationPoint(1.5f, 0.0f, 2.0f);
        bipedRightWing.rotateAngleY = (float) (-Math.PI * 0.4);
        bipedBody.addChild(bipedRightWing);
    }

    @Override
    public void render(LivingEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Minecraft mc = Minecraft.getInstance();
        boolean isFlying = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isFlying && entity.isAirBorne;

        //copyBipedAngles(this, this.overlay);
        //copyBipedAngles(this, this.invulnOverlay);

        super.render(entity, f, f1, f2, f3, f4, f5);

        GlStateManager.color4f(1, 1, 1, 1);
        //CosmicShaderHelper.useShader();
        TextureUtils.bindBlockTexture();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        if (invulnRender) {
            GlStateManager.color4f(1, 1, 1, 0.2F);//TODO changed 0.2F
            //invulnOverlay.render(entity, f, f1, f2, f3, f4, f5);
        }
        //overlay.render(entity, f, f1, f2, f3, f4, f5);

        //CosmicShaderHelper.releaseShader();

        mc.textureManager.bindTexture(eyeTex);
        GlStateManager.disableLighting();
        mc.gameRenderer.disableLightmap();

        long time = mc.player.world.getGameTime();

        setGems();

        double pulse = Math.sin(time / 10.0) * 0.5 + 0.5;
        double pulse_mag_sqr = pulse * pulse * pulse * pulse * pulse * pulse;
        GlStateManager.color4f(0.84F, 1F, 0.95F, (float) (pulse_mag_sqr * 0.5F));

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        super.render(entity, f, f1, f2, f3, f4, f5);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();

        if (invulnRender) {
            long frame = time / 3;
            randy.setSeed(frame * 1723609L);
            float o = randy.nextFloat() * 6.0f;
            float[] col = ColourHelper.HSVtoRGB(o, 1.0f, 1.0f);

            GlStateManager.color4f(col[0], col[1], col[2], 1);//TODO changed 1F
            setEyes();
            super.render(entity, f, f1, f2, f3, f4, f5);
        }

        if (!CosmicShaderHelper.inventoryRender) {
            mc.gameRenderer.enableLightmap();
        }
        GlStateManager.enableLighting();
        GlStateManager.color4f(1, 1, 1, 1);

        // WINGS
        if (isFlying && !CosmicShaderHelper.inventoryRender) {
            setWings();
            mc.textureManager.bindTexture(wingTex);
            super.render(entity, f, f1, f2, f3, f4, f5);

            //CosmicShaderHelper.useShader();
            TextureUtils.bindBlockTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            //overlay.render(entity, f, f1, f2, f3, f4, f5);

            //CosmicShaderHelper.releaseShader();

            mc.textureManager.bindTexture(wingGlowTex);
            GlStateManager.disableLighting();
            mc.gameRenderer.disableLightmap();

            GlStateManager.color4f(0.84F, 1F, 0.95F, (float) (pulse_mag_sqr * 0.5));

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            super.render(entity, f, f1, f2, f3, f4, f5);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableAlphaTest();
            if (!CosmicShaderHelper.inventoryRender) {
                mc.gameRenderer.enableLightmap();
            }
            GlStateManager.enableLighting();
            GlStateManager.color4f(1, 1, 1, 1);

        }
    }

    public void update(LivingEntity entityLiving, ItemStack itemstack, EquipmentSlotType armorSlot) {
        
        boolean isSitting=entityLiving.isPassenger() && (entityLiving.getRidingEntity() != null && entityLiving.getRidingEntity().shouldRiderSit());
        currentSlot = armorSlot;

        invulnRender = true;

        ItemStack hat = entityLiving.getItemStackFromSlot(EquipmentSlotType.HEAD);
        ItemStack chest = entityLiving.getItemStackFromSlot(EquipmentSlotType.CHEST);
        ItemStack leg = entityLiving.getItemStackFromSlot(EquipmentSlotType.LEGS);
        ItemStack foot = entityLiving.getItemStackFromSlot(EquipmentSlotType.FEET);

        boolean hasHat = hat != null && hat.getItem() == ModItems.infinity_helmet; //&& !((ItemArmorInfinity) (ModItems.infinity_helmet)).hasPhantomInk(hat);
        boolean hasChest = chest != null && chest.getItem() == ModItems.infinity_chestplate; // && !((ItemArmorInfinity) (ModItems.infinity_chestplate)).hasPhantomInk(chest);
        boolean hasLeg = leg != null && leg.getItem() == ModItems.infinity_pants; // && !((ItemArmorInfinity) (ModItems.infinity_pants)).hasPhantomInk(leg);
        boolean hasFoot = foot != null && foot.getItem() == ModItems.infinity_boots; // && !((ItemArmorInfinity) (ModItems.infinity_boots)).hasPhantomInk(foot);

        if (armorSlot == EquipmentSlotType.HEAD) {//TODO, Wot.
            if (hasHat && hasChest && hasLeg && hasFoot) {
                invulnRender = true;
            }
        }

        showHat = hasHat && armorSlot == EquipmentSlotType.HEAD;
        showChest = hasChest && armorSlot == EquipmentSlotType.CHEST;
        showLeg = hasLeg && armorSlot == EquipmentSlotType.LEGS;
        showFoot = hasFoot && armorSlot == EquipmentSlotType.FEET;

        bipedHead.showModel = showHat;
        bipedHeadwear.showModel = showHat;
        bipedBody.showModel = showChest || showLeg;
        bipedRightArm.showModel = showChest;
        bipedLeftArm.showModel = showChest;
        bipedRightLeg.showModel = showLeg || showFoot;
        bipedLeftLeg.showModel = showLeg || showFoot;

        overlay.bipedHead.showModel = showHat;
        overlay.bipedHeadwear.showModel = showHat;
        overlay.bipedBody.showModel = showChest || showLeg;
        overlay.bipedRightArm.showModel = showChest;
        overlay.bipedLeftArm.showModel = showChest;
        overlay.bipedRightLeg.showModel = showLeg || showFoot;
        overlay.bipedLeftLeg.showModel = showLeg || showFoot;

        bipedLeftWing.showModel = false;
        bipedRightWing.showModel = false;
        overlay.bipedLeftWing.showModel = false;
        overlay.bipedRightWing.showModel = false;

        isSneak = entityLiving.isSneaking();
        this.isSitting = isSitting;
        isChild = entityLiving.isChild();

        overlay.isSneak = entityLiving.isSneaking();
        overlay.isSitting = isSitting;
        overlay.isChild = entityLiving.isChild();

        invulnOverlay.isSneak = entityLiving.isSneaking();
        invulnOverlay.isSitting = isSitting;
        invulnOverlay.isChild = entityLiving.isChild();

        overlay.swingProgress = swingProgress;
        invulnOverlay.swingProgress = swingProgress;

        leftArmPose = ArmPose.EMPTY;
        rightArmPose = ArmPose.EMPTY;

        overlay.leftArmPose = ArmPose.EMPTY;
        overlay.rightArmPose = ArmPose.EMPTY;

        invulnOverlay.leftArmPose = ArmPose.EMPTY;
        invulnOverlay.rightArmPose = ArmPose.EMPTY;

        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;

            ItemStack main_hand = player.getHeldItem(Hand.MAIN_HAND);

            if (main_hand != null) {
                rightArmPose = ArmPose.ITEM;
                overlay.rightArmPose = ArmPose.ITEM;
                invulnOverlay.rightArmPose = ArmPose.ITEM;

                if (player.getItemInUseCount() > 0) {

                    UseAction enumaction = main_hand.getUseAction();

                    if (enumaction == UseAction.BOW) {
                        rightArmPose = ArmPose.BOW_AND_ARROW;
                        overlay.rightArmPose = ArmPose.BOW_AND_ARROW;
                        invulnOverlay.rightArmPose = ArmPose.BOW_AND_ARROW;
                    } else if (enumaction == UseAction.BLOCK) {
                        rightArmPose = ArmPose.BLOCK;
                        overlay.rightArmPose = ArmPose.BLOCK;
                        invulnOverlay.rightArmPose = ArmPose.BLOCK;
                    }

                }

            }

            ItemStack off_hand = player.getHeldItem(Hand.OFF_HAND);
            if (off_hand != null) {
                leftArmPose = ArmPose.ITEM;
                overlay.leftArmPose = ArmPose.ITEM;
                invulnOverlay.leftArmPose = ArmPose.ITEM;

                if (player.getItemInUseCount() > 0) {

                    UseAction enumaction = off_hand.getUseAction();

                    if (enumaction == UseAction.BOW) {
                        leftArmPose = ArmPose.BOW_AND_ARROW;
                        overlay.leftArmPose = ArmPose.BOW_AND_ARROW;
                        invulnOverlay.leftArmPose = ArmPose.BOW_AND_ARROW;
                    } else if (enumaction == UseAction.BLOCK) {
                        leftArmPose = ArmPose.BLOCK;
                        overlay.leftArmPose = ArmPose.BLOCK;
                        invulnOverlay.leftArmPose = ArmPose.BLOCK;
                    }

                }

            }
        }

    }

    @Override
    public void setRotationAngles(LivingEntity entity, float f1, float speed, float ticks, float headYaw, float headPitch, float f6) {
        super.setRotationAngles(entity, f1, speed, ticks, headYaw, headPitch, f6);
        overlay.setRotationAngles(entity, f1, speed, ticks, headYaw, headPitch, f6);
        invulnOverlay.setRotationAngles(entity, f1, speed, ticks, headYaw, headPitch, f6);
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        if (manager.renderers.containsKey(entity.getClass())) {
            EntityRenderer r = manager.renderers.get(entity.getClass());

            if (r instanceof BipedRenderer) {
                BipedModel m = (BipedModel) ((BipedRenderer) r).getEntityModel();

                copyBipedAngles(m, this);
            }
        }
    }

    public void setEyes() {
        bipedHead.showModel = false;
        bipedBody.showModel = false;
        bipedRightArm.showModel = false;
        bipedLeftArm.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;
        bipedHeadwear.showModel = showHat ? true : false;
    }

    public void setGems() {
        bipedHead.showModel = false;
        bipedHeadwear.showModel = false;
        bipedBody.showModel = !legs && (showChest ? true : false);
        bipedRightArm.showModel = !legs && (showChest ? true : false);
        bipedLeftArm.showModel = !legs && (showChest ? true : false);
        bipedRightLeg.showModel = legs && (showLeg ? true : false);
        bipedLeftLeg.showModel = legs && (showLeg ? true : false);
    }

    public void setWings() {
        bipedBody.showModel = !legs && (showChest ? true : false);
        bipedLeftWing.showModel = true;
        bipedRightWing.showModel = true;
        bipedHeadwear.showModel = false;
        bipedRightArm.showModel = false;
        bipedLeftArm.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;
        bipedHead.showModel = false;

        overlay.bipedBody.showModel = !legs && (showChest ? true : false);
        overlay.bipedLeftWing.showModel = true;
        overlay.bipedRightWing.showModel = true;
        overlay.bipedHead.showModel = false;
        overlay.bipedHeadwear.showModel = false;
    }

    public void rebuildOverlay() {
        rebuildWings();
        overlay.rebuild(AvaritiaAlphaTextures.INFINITY_ARMOR_MASK, AvaritiaAlphaTextures.INFINITY_ARMOR_MASK_WINGS);
        invulnOverlay.rebuild(AvaritiaAlphaTextures.INFINITY_ARMOR_MASK_INV, null);
    }

    public static void copyPartAngles(RendererModel source, RendererModel dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
    }

    public static void copyBipedAngles(BipedModel source, BipedModel dest) {
        copyPartAngles(source.bipedBody, dest.bipedBody);
        copyPartAngles(source.bipedHead, dest.bipedHead);
        copyPartAngles(source.bipedHeadwear, dest.bipedHeadwear);
        copyPartAngles(source.bipedLeftArm, dest.bipedLeftArm);
        copyPartAngles(source.bipedLeftLeg, dest.bipedLeftLeg);
        copyPartAngles(source.bipedRightArm, dest.bipedRightArm);
        copyPartAngles(source.bipedRightLeg, dest.bipedRightLeg);
    }

    public static class Overlay extends BipedModel<LivingEntity> {

        public InfinityArmorModel parent;
        public float expand;

        public RendererModel bipedLeftWing;
        public RendererModel bipedRightWing;

        public Overlay(InfinityArmorModel parent, float expand) {
            this.parent = parent;
            this.expand = expand;
        }

        @SuppressWarnings ("rawtypes")
        public void rebuild(TextureAtlasSprite icon, TextureAtlasSprite wingicon) {
            int ox = MathHelper.floor(icon.getMinU() * itempagewidth);
            int oy = MathHelper.floor(icon.getMinV() * itempageheight);

            float heightoffset = 0.0f;
            int legoffset = parent.legs ? 32 : 0;

            textureWidth = itempagewidth;
            textureHeight = itempageheight;
            bipedHead = new RendererModel(this, 0 + ox, 0 + legoffset + oy);
            bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, expand);
            bipedHead.setRotationPoint(0.0F, 0.0F + heightoffset, 0.0F);
            bipedHeadwear = new RendererModel(this, 32 + ox, 0 + legoffset + oy);
            bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, expand * 0.5F);
            bipedHeadwear.setRotationPoint(0.0F, 0.0F + heightoffset, 0.0F);
            bipedBody = new RendererModel(this, 16 + ox, 16 + legoffset + oy);
            bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, expand);
            bipedBody.setRotationPoint(0.0F, 0.0F + heightoffset, 0.0F);
            bipedRightArm = new RendererModel(this, 40 + ox, 16 + legoffset + oy);
            bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, expand);
            bipedRightArm.setRotationPoint(-5.0F, 2.0F + heightoffset, 0.0F);
            bipedLeftArm = new RendererModel(this, 40 + ox, 16 + legoffset + oy);
            bipedLeftArm.mirror = true;
            bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, expand);
            bipedLeftArm.setRotationPoint(5.0F, 2.0F + heightoffset, 0.0F);
            bipedRightLeg = new RendererModel(this, 0 + ox, 16 + legoffset + oy);
            bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, expand);
            bipedRightLeg.setRotationPoint(-1.9F, 12.0F + heightoffset, 0.0F);
            bipedLeftLeg = new RendererModel(this, 0 + ox, 16 + legoffset + oy);
            bipedLeftLeg.mirror = true;
            bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, expand);
            bipedLeftLeg.setRotationPoint(1.9F, 12.0F + heightoffset, 0.0F);

            // rebuild wings!
            if (wingicon != null) {
                int oxw = MathHelper.floor(wingicon.getMinU() * itempagewidth);
                int oyw = MathHelper.floor(wingicon.getMinV() * itempageheight);

                if (bipedBody.childModels == null) {
                    bipedBody.childModels = new ArrayList();
                }
                if (bipedLeftWing != null) {
                    bipedBody.childModels.remove(bipedLeftWing);
                }
                if (bipedRightWing != null) {
                    bipedBody.childModels.remove(bipedRightWing);
                }

                bipedLeftWing = new WingModelRenderer(this, oxw, oyw);
                bipedLeftWing.mirror = true;
                bipedLeftWing.addBox(0f, -11.6f, 0f, 0, 32, 32);
                bipedLeftWing.setRotationPoint(-1.5f, 0.0f, 2.0f);
                bipedLeftWing.rotateAngleY = (float) (Math.PI * 0.4);
                bipedBody.addChild(bipedLeftWing);

                bipedRightWing = new WingModelRenderer(this, oxw, oyw);
                bipedRightWing.mirror=true;
                bipedRightWing.addBox(0f, -11.6f, 0f, 0, 32, 32);
                bipedRightWing.setRotationPoint(1.5f, 0.0f, 2.0f);
                bipedRightWing.rotateAngleY = (float) (-Math.PI * 0.4);
                bipedBody.addChild(bipedRightWing);
            }
        }

        @Override
        public void render(LivingEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            copyBipedAngles(parent, this);

            super.render(entity, f, f1, f2, f3, f4, f5);
        }

        @Override
        public void setRotationAngles(LivingEntity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
            super.setRotationAngles(entity, f1, f2, f3, f4, f5, f6);
            EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
            if (manager.renderers.containsKey(entity.getClass())) {
                EntityRenderer<? extends Entity> r = manager.renderers.get(entity.getClass());

                if (r instanceof BipedRenderer) {
                    BipedModel m = (BipedModel) ((BipedRenderer<?, ?>) r).getEntityModel();

                    copyBipedAngles(m, this);
                }
            }
        }
    }
}
