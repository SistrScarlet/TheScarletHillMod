package com.sistr.scarlethill.datagen;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(Registration.SCARLET_KEY_ITEM.get())
                .patternLine(" RR")
                .patternLine(" R ")
                .patternLine("C  ")
                .key('R', ScarletTags.Items.RED_THINGS)
                .key('C', Items.REDSTONE_BLOCK)
                .addCriterion("has_red_things", this.hasItem(ScarletTags.Items.RED_THINGS))
                .addCriterion("has_redstone_block", this.hasItem(Items.REDSTONE_BLOCK))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Registration.SCARLET_SNOW_BLOCK.get(), 2)
                .patternLine("BB")
                .key('B', Registration.SCARLET_SNOWBALL_ITEM.get())
                .addCriterion("has_scarlet_snowball", InventoryChangeTrigger.Instance.forItems(Registration.SCARLET_SNOWBALL_ITEM.get()))
                .build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Registration.SCARLET_SAND_BLOCK.get()), Registration.SCARLET_GLASS_BLOCK.get().asItem(), 0.1F, 200)
                .addCriterion("has_scarlet_sand", InventoryChangeTrigger.Instance.forItems(Registration.SCARLET_SAND_BLOCK.get()))
                .build(consumer);
    }
}
