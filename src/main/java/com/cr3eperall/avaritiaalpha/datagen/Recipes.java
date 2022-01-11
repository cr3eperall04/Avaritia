package com.cr3eperall.avaritiaalpha.datagen;

import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import com.cr3eperall.avaritiaalpha.items.ModItems;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;

import java.io.IOException;
import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.CRYSTALMATRIXBLOCK)
                .patternLine("iii")
                .patternLine("iii")
                .patternLine("iii")
                .key('i', ModItems.CRYSTALMATRIXINGOT)
                .setGroup(AvaritiaAlpha.MOD_ID)
                .addCriterion("crystal_matrix_ingot", InventoryChangeTrigger.Instance.forItems(ModItems.DIAMONDLATTICE));
    }
}
