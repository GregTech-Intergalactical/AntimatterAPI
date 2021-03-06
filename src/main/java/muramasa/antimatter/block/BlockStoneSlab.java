package muramasa.antimatter.block;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class BlockStoneSlab extends SlabBlock implements IAntimatterObject, ITextureProvider, IModelProvider {
    protected String domain, id, suffix;
    CobbleStoneType type;

    public BlockStoneSlab(CobbleStoneType type, String suffix) {
        super(getProps(type));
        domain = type.getDomain();
        String s = suffix.isEmpty() ? "" : "_";
        id = type.getId() + s + suffix + "_slab";
        this.suffix = suffix;
        this.type = type;
        AntimatterAPI.register(getClass(), getId(), this);
    }

    private static Properties getProps(StoneType type){
        Properties props = Block.Properties.create(type.getBlockMaterial()).sound(type.getSoundType()).harvestLevel(type.getHarvestLevel()).harvestTool(type.getToolType()).hardnessAndResistance(type.getHardness(), type.getResistence());
        if (type.doesRequireTool()){
            props.setRequiresTool();
        }
        return props;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(type.getDomain(), type.getBeginningPath() + type.getId().replace("stone_", "") + "/" + (suffix.isEmpty() ? "stone" : suffix))};
    }

    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        BlockModelBuilder top = prov.models().getBuilder(getId() + "_top").parent(prov.existing("minecraft", "block/slab_top")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        BlockModelBuilder bottom = prov.models().getBuilder(getId()).parent(prov.existing("minecraft", "block/slab")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        ModelFile.ExistingModelFile both = prov.existing(type.getDomain(),"block/" + this.getId().replace("_slab", ""));
        prov.getVariantBuilder(block).forAllStates(s -> {
            if (s.get(TYPE) == SlabType.DOUBLE){
                return ConfiguredModel.builder().modelFile(both).build();
            }
            return ConfiguredModel.builder().modelFile(s.get(TYPE) == SlabType.TOP ? top : bottom).build();
        });
    }
}
