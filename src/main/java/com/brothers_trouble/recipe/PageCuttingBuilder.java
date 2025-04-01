//package com.brothers_trouble.recipe;
//
//import net.minecraft.advancements.Criterion;
//import net.minecraft.data.recipes.RecipeBuilder;
//import net.minecraft.data.recipes.RecipeOutput;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//public class PageCuttingBuilder implements RecipeBuilder {
//    protected final List<Ingredient> mainIngredients;
//    protected final List<Ingredient> extras;
//    protected final ItemStack result;
//    protected final int energyCost;
//    protected final int timeInTicks;
//    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
//
//    public PageCuttingBuilder(List<Ingredient> mainIngredients, List<Ingredient> extras, int energyCost, int timeInTicks, ItemStack result){
//        this.mainIngredients = mainIngredients;
//        this.extras = extras;
//        this.energyCost = energyCost;
//        this.timeInTicks = timeInTicks;
//        this.result = result;
//    }
//
//    public PageCuttingBuilder(List<Ingredient> mainIngredients, List<Ingredient> extras, ItemStack result){
//        this.mainIngredients = mainIngredients;
//        this.extras = extras;
//        this.energyCost = 0;
//        this.timeInTicks = 0;
//        this.result = result;
//    }
//
//    @Override
//    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
//        this.criteria.put(s, criterion);
//        return this;
//    }
//
//    @Override
//    public RecipeBuilder group(@Nullable String s) {
//        return this;
//    }
//
//    @Override
//    public Item getResult() {
//        return this.result.getItem();
//    }
//
//    @Override
//    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
//
//    }
//
//    public static PageCuttingBuilder create(List<Ingredient> mainIngredients, List<Ingredient> extras, int energyCost, int timeInTicks, ItemStack result) {
//        return new PageCuttingBuilder(mainIngredients, extras, energyCost, timeInTicks, result);
//    }
//
//    public static PageCuttingBuilder create(List<Ingredient> mainIngredients, List<Ingredient> extras, ItemStack result) {
//        return new PageCuttingBuilder(mainIngredients, extras, result);
//    }
//}
