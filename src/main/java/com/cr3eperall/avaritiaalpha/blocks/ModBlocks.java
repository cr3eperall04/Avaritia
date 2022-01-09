package com.cr3eperall.avaritiaalpha.blocks;

import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorContainer;
import com.cr3eperall.avaritiaalpha.blocks.tiles.NeutronCollectorTile;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("avaritiaalpha:crystal_matrix_block")
    public static CrystalMatrixBlock CRYSTALMATRIXBLOCK;

    @ObjectHolder("avaritiaalpha:neutron_collector")
    public static NeutronCollector NEUTRONCOLLECTOR;

    @ObjectHolder("avaritiaalpha:neutron_collector")
    public static TileEntityType<NeutronCollectorTile> NEUTRONCOLLECTOR_TILE;

    @ObjectHolder("avaritiaalpha:neutron_collector")
    public static ContainerType<NeutronCollectorContainer> NEUTRONCOLLECTOR_CONTAINER;
}
