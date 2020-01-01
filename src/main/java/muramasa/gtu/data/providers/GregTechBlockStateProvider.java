package muramasa.gtu.data.providers;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import javax.annotation.Nonnull;

public class GregTechBlockStateProvider extends PublicBlockStateProvider {

    public GregTechBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Ref.MODID, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return Ref.MODID + " BlockStates";
    }

    @Override
    protected void registerStatesAndModels() {
        GregTechAPI.all(Block.class).forEach(b -> {
            if (b instanceof IModelProvider) ((IModelProvider) b).onBlockModelBuild(this);
        });
    }

    public void simpleBlock(Block block, ResourceLocation texture) {
        simpleBlock(block, cubeAll(block.getRegistryName().toString(), texture));
    }

    public void cubeAllTinted(Block block, ResourceLocation texture, int tint) {
        simpleBlock(block, getBuilder(block.getRegistryName().getPath())
            .parent(getExistingFile(mcLoc("block/block")))
            .texture("all", texture).texture("particle", texture)
            .element().allFaces((d, f) -> f.texture("#all").tintindex(tint)).end()
        );
    }

    public void cubeAllLayered(Block block, ResourceLocation base, ResourceLocation overlay) {
        simpleBlock(block, getBuilder(block.getRegistryName().getPath())
            .parent(getExistingFile(mcLoc("block/block")))
            .texture("base", base).texture("overlay", overlay).texture("particle", base)
            .element().allFaces((d, f) -> f.texture("#base")).end()
            .element().allFaces((d, f) -> f.texture("#overlay")).end()
        );
    }

    public void cubeAllLayeredTinted(Block block, ResourceLocation base, ResourceLocation overlay, int baseTint, int overlayTint) {
        simpleBlock(block, getBuilder(block.getRegistryName().getPath())
            .parent(getExistingFile(mcLoc("block/block")))
            .texture("base", base).texture("overlay", overlay).texture("particle", base)
            .element().allFaces((d, f) -> f.texture("#base").tintindex(baseTint)).end()
            .element().allFaces((d, f) -> f.texture("#overlay").tintindex(overlayTint)).end()
        );
    }
}
