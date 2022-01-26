package com.cr3eperall.avaritiaalpha.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModEntities {
    public static EntityType<HeavenArrowEntity> heavenArrowEntityEntityType= (EntityType<HeavenArrowEntity>) EntityType.Builder.<HeavenArrowEntity>create(HeavenArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build("heaven_arrow").setRegistryName("heaven_arrow");
    public static EntityType<HeavenSubArrowEntity> heavenSubArrowEntityEntityType= (EntityType<HeavenSubArrowEntity>) EntityType.Builder.<HeavenSubArrowEntity>create(HeavenSubArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build("heaven_sub_arrow").setRegistryName("heaven_sub_arrow");
    public static EntityType<GapingVoidEntity> gapingVoidEntityEntityType= (EntityType<GapingVoidEntity>) EntityType.Builder.<GapingVoidEntity>create(GapingVoidEntity::new,EntityClassification.MISC).size(0.1F,0.1F).build("gaping_void").setRegistryName("gaping_void");
    public static EntityType<EndestPearlEntity> endestPearlEntityEntityType= (EntityType<EndestPearlEntity>) EntityType.Builder.<EndestPearlEntity>create(EndestPearlEntity::new,EntityClassification.MISC).size(0.25F,0.25F).build("endest_pearl").setRegistryName("endest_pearl");
}
