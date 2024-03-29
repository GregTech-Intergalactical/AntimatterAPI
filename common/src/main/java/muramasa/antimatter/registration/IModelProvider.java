package muramasa.antimatter.registration;

import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface IModelProvider {

    default void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        if (item instanceof Block) prov.blockItem(item);
        else if (item instanceof ITextureProvider) prov.tex(item, ((ITextureProvider) item).getTextures());
        else prov.getBuilder(item);
    }

    default void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        if (block instanceof ITextureProvider) prov.state(block, ((ITextureProvider) block).getTextures());
    }
}
