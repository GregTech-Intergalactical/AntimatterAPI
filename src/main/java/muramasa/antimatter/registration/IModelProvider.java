package muramasa.antimatter.registration;

import muramasa.antimatter.datagen.providers.GregTechBlockStateProvider;
import muramasa.antimatter.datagen.providers.GregTechItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface IModelProvider {

    default void onItemModelBuild(IItemProvider item, GregTechItemModelProvider provider) {
        if (item instanceof IForgeBlock) provider.blockItem(item);
        else if (item instanceof ITextureProvider) provider.textured(item, ((ITextureProvider) item).getTextures());
    }

    default void onBlockModelBuild(Block block, GregTechBlockStateProvider provider) {
        if (block instanceof ITextureProvider) provider.texturedState(block, ((ITextureProvider) block).getTextures());
    }
}
