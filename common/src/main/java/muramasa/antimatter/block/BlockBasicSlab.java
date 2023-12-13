package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.VariantBlockStateBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;

public class BlockBasicSlab extends SlabBlock implements IAntimatterObject, ITextureProvider, IModelProvider {
    protected final String domain, id;

    public BlockBasicSlab(String domain, String id, Properties properties) {
        super(properties);
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(getClass(), this);
    }

    public BlockBasicSlab(String domain, String id) {
        this(domain, id, Properties.of(Material.METAL).strength(1.0f, 1.0f).sound(SoundType.STONE));
    }

    public String getDomain() {
        return this instanceof ISharedAntimatterObject ? Ref.SHARED_ID : domain;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[0];
    }

    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        Texture texture = getTextures()[0];
        ResourceLocation both = prov.existing(this.getDomain(), "block/" + this.getId().replace("_slab", ""));
        AntimatterBlockModelBuilder top = prov.models().getBuilder(getId() + "_top").parent(prov.existing("minecraft", "block/slab_top")).texture("bottom", texture).texture("top", texture).texture("side", texture);
        AntimatterBlockModelBuilder bottom = prov.models().getBuilder(getId()).parent(prov.existing("minecraft", "block/slab")).texture("bottom", texture).texture("top", texture).texture("side", texture);
        ResourceLocation finalBoth = both;
        prov.getVariantBuilder(block).forAllStates(s -> {
            if (s.getValue(TYPE) == SlabType.DOUBLE) {
                return new VariantBlockStateBuilder.VariantBuilder().modelFile(finalBoth);
            }
            return new VariantBlockStateBuilder.VariantBuilder().modelFile(s.getValue(TYPE) == SlabType.TOP ? top : bottom);
        });
    }
}
