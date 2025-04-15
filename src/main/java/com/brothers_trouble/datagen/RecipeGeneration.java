package com.brothers_trouble.datagen;

import com.brothers_trouble.recipe.PageCutting;
//import com.brothers_trouble.recipe.PageCuttingBuilder;
import com.brothers_trouble.registration.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGeneration extends RecipeProvider {
//    ArrayList<Ingredient> ingredients = new ArrayList<>();
//    ingredients.add(Items.PAPER);
//    ArrayList<Ingredient> extras = new ArrayList<>();
//    ingredients.add(Items.SHEARS);
    public RecipeGeneration(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput){
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.POST_IT_NOTE.get(), 4)
                .requires(Items.PAPER)
                .requires(Items.SLIME_BALL)
                .requires(Items.SHEARS)
                .unlockedBy("has_paper", has(Items.PAPER)).save(recipeOutput);

//        PageCuttingBuilder.create(List of, extras, ItemRegistry.POST_IT_NOTE.get())
//                .requires(Items.PAPER)
//                .unlockedBy("has_paper", has(Items.PAPER)).save(recipeOutput);
    }
}
