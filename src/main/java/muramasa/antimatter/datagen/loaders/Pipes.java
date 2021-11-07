package muramasa.antimatter.datagen.loaders;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.PipeItemBlock;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.recipe.RecipeBuilders.FLUID_PIPE_BUILDER;
import static muramasa.antimatter.recipe.RecipeBuilders.ITEM_PIPE_BUILDER;

public class Pipes {
    public static void loadRecipes(Consumer<IFinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final ICriterionInstance in = provider.hasSafeItem(WRENCH.getTag());
        List<ItemStack> stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.TINY)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.TINY), 12)).collect(Collectors.toList());
        Material[] mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.TINY.getId()), consumer, Ref.ID, "pipe_item_tiny", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "H W", "PPP");

        stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.SMALL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.SMALL), 6)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.SMALL.getId()), consumer, Ref.ID, "pipe_item_small", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PWP", "P P", "PHP");

        stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.NORMAL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.NORMAL), 2)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.NORMAL.getId()), consumer, Ref.ID, "pipe_item_normal", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "W H", "PPP");

        stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.LARGE)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.LARGE), 1)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.LARGE.getId()), consumer, Ref.ID, "pipe_item_large", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PHP", "P P", "PWP");

        stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.TINY)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.TINY), 12)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.TINY.getId()), consumer, Ref.ID, "pipe_fluid_tiny", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "H W", "PPP");

        stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.SMALL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.SMALL), 6)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.SMALL.getId()), consumer, Ref.ID, "pipe_fluid_small", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PWP", "P P", "PHP");

        stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.NORMAL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.NORMAL), 2)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.NORMAL.getId()), consumer, Ref.ID, "pipe_fluid_normal", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "W H", "PPP");

        stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.LARGE)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.LARGE), 1)).collect(Collectors.toList());
        mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
        if (stacks.size() > 0)
            provider.addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.LARGE.getId()), consumer, Ref.ID, "pipe_fluid_large", "antimatter_pipes",
                    "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PHP", "P P", "PWP");
    }
}
