package com.cr3eperall.avaritiaalpha.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL="general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER=new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER=new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue NEUTRONCOLLECTOR_RATE;
    public static ForgeConfigSpec.IntValue INFINITYAXE_RANGE;

    static {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        NEUTRONCOLLECTOR_RATE=COMMON_BUILDER.comment("Time to get one pile of neutrons in ticks")
                .defineInRange("neutronColletorRate", 7111,1,Integer.MAX_VALUE);

        INFINITYAXE_RANGE=COMMON_BUILDER.comment("range for the infinity axe shift+right click")
                        .defineInRange("infinityAxeRange",13,0,1024);
        COMMON_BUILDER.pop();



        COMMON_CONFIG=COMMON_BUILDER.build();
        CLIENT_CONFIG=CLIENT_BUILDER.build();

    }

    public static void loadConfig(ForgeConfigSpec spec, Path path){
        final CommentedFileConfig configData= CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }

}
