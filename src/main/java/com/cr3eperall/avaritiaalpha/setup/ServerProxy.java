package com.cr3eperall.avaritiaalpha.setup;

import net.minecraft.world.World;

public class ServerProxy implements IProxy{
    @Override
    public World getClientWorld() {
        throw new IllegalStateException("This should only be run on the client!");
    }
}
