package com.cr3eperall.avaritiaalpha.util;

import net.minecraft.item.ItemStack;

public class ItemStackWrapper {

    public final ItemStack stack;

    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean equals(Object otherobj) {
        if (otherobj instanceof ItemStackWrapper) {
            ItemStackWrapper other = (ItemStackWrapper) otherobj;

            if (stack.getItem().equals(other.stack.getItem()) && stack.getDamage() == other.stack.getDamage()) {

                if (stack.getTag() == null && other.stack.getTag() == null) {
                    return true;
                } else {
                    if (stack.getTag() == null ^ other.stack.getTag() == null) {
                        return false;
                    } else if (stack.getTag().equals(other.stack.getTag())) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = stack.getItem().hashCode();
        if (stack.getTag() != null) {
            h ^= stack.getTag().hashCode();
        }
        return h ^ stack.getDamage();
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
