package muramasa.antimatter.registration;

import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface IModelProvider {

    default ItemModelBuilder onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        return getDefaultItemBuilder(item, prov);
    }

    default void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        if (block instanceof ITextureProvider) prov.state(block, ((ITextureProvider) block).getTextures());
    }

    default ItemModelBuilder getDefaultItemBuilder(IItemProvider item, AntimatterItemModelProvider prov) {
        if (item instanceof IForgeBlock) return prov.blockItem(item);
        if (item instanceof ITextureProvider) return prov.tex(item, ((ITextureProvider) item).getTextures());
        else return prov.getBuilder(item);
    }
}
