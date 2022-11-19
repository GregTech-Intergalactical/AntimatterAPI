package muramasa.antimatter.integration.rei;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.integration.rei.category.RecipeMapCategory;
import muramasa.antimatter.integration.rei.category.RecipeMapDisplay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class AntimatterREIServerPlugin implements REIServerPlugin {

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                registry.register(CategoryIdentifier.of(tuple.map.getLoc()), new DisplaySerializer<RecipeMapDisplay>(){
                    @Override
                    public CompoundTag save(CompoundTag tag, RecipeMapDisplay display) {
                        tag.put("recipeDisplay", display.toNbt());
                        return tag;
                    }

                    @Override
                    public RecipeMapDisplay read(CompoundTag tag) {
                        return RecipeMapDisplay.fromNbt(tag.getCompound("recipeDisplay"));
                    }
                });
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
    }
}
