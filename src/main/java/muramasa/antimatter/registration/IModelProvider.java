package muramasa.antimatter.registration;

import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.tools.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface IModelProvider {

    default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        if (item instanceof IForgeBlock) prov.blockItem(item);
        else if (item instanceof ITextureProvider) prov.tex(item, ((ITextureProvider) item).getTextures());
    }

    default void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        if (AntimatterModelManager.onBlockModelBuild(block, prov)) return;
        if (block instanceof ITextureProvider) prov.state(block, ((ITextureProvider) block).getTextures());
    }
}
