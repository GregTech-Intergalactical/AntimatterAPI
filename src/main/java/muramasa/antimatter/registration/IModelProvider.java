package muramasa.antimatter.registration;

import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface IModelProvider {

    default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider provider) {
        if (item instanceof IForgeBlock) provider.blockItem(item);
        else if (item instanceof ITextureProvider) provider.textured(item, ((ITextureProvider) item).getTextures());
    }

    default void onBlockModelBuild(Block block, AntimatterBlockStateProvider provider) {
        if (block instanceof ITextureProvider) provider.texturedState(block, ((ITextureProvider) block).getTextures());
    }
}
