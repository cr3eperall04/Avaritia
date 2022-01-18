package com.cr3eperall.avaritiaalpha.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModEntities {
    public static EntityType<HeavenArrowEntity> heavenArrowEntityEntityType= (EntityType<HeavenArrowEntity>) EntityType.Builder.<HeavenArrowEntity>create(HeavenArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build("heaven_arrow").setRegistryName("heaven_arrow");
    public static EntityType<HeavenSubArrowEntity> heavenSubArrowEntityEntityType= (EntityType<HeavenSubArrowEntity>) EntityType.Builder.<HeavenSubArrowEntity>create(HeavenSubArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build("heaven_sub_arrow").setRegistryName("heaven_sub_arrow");
}
