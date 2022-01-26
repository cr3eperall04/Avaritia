package com.cr3eperall.avaritiaalpha.setup;

import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class ServerProxy implements IProxy{

    public static final GameProfile avaritiaFakePlayer = new GameProfile(UUID.fromString("32283731-bbef-487c-bb69-c7e32f84ed27"), "[AvaritiaAlpha]");

    @Override
    public void init() {
        //MinecraftForge.EVENT_BUS.register(new AvaritiaAlphaEventHandler());
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("This should only be run on the client!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("This should only be run on the client!");
    }
}
