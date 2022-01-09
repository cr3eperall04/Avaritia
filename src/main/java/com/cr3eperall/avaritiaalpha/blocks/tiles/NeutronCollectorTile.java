package com.cr3eperall.avaritiaalpha.blocks.tiles;

import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorContainer;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NeutronCollectorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    public NeutronCollectorTile() {
        super(ModBlocks.NEUTRONCOLLECTOR_TILE);
    }
    LazyOptional<ItemStackHandler> itemHandler=LazyOptional.of(()-> createItemHandler());

    private ItemStackHandler createItemHandler(){
        return new ItemStackHandler(1){
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return stack.getItem() == ModItems.DIAMONDLATTICE;
                }

                @Nonnull
                @Override
                public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                    if (stack.getItem() == ModItems.DIAMONDLATTICE) {
                        return super.insertItem(slot, stack, simulate);
                    }
                    return stack;
                }
            };
    }
    @Override
    public void tick() {

    }



    @Override
    public void read(CompoundNBT compound) {
        itemHandler.ifPresent(h->((INBTSerializable<CompoundNBT>)h).deserializeNBT(compound.getCompound("inv")));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        itemHandler.ifPresent(h->{
            compound.put("inv", ((INBTSerializable<CompoundNBT>)h).serializeNBT());
        });
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("block.avaritiaalpha.neutron_collector");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new NeutronCollectorContainer(windowId,world,pos, playerInventory, playerEntity);
    }
}
