package com.cr3eperall.avaritiaalpha.setup;

import com.cr3eperall.avaritiaalpha.handler.AvaritiaAlphaEventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy implements IProxy{
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
