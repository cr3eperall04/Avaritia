package com.cr3eperall.avaritiaalpha.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

    public void init();
    World getClientWorld();
    PlayerEntity getClientPlayer();
}
