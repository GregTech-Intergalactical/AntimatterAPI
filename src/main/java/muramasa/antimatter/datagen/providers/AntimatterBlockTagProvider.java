package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static muramasa.antimatter.util.Utils.*;

public class AntimatterBlockTagProvider extends ForgeBlockTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        this.tagToBuilder.clear();
        registerTags();
        TagCollection<Block> tags = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
        Map<ResourceLocation, Tag.Builder<Block>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().getId(), Map.Entry::getValue));
        tags.registerAll(map);
        tags.getTagMap().forEach((k, v) -> DynamicResourcePack.addTag("blocks", k, v.serialize(this.registry::getKey)));
        this.setCollection(tags);
    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void registerTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(BlockOre.class, o -> {
            this.getBuilder(getForgeBlockTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
            if (o.getOreType() == MaterialType.ORE) this.getBuilder(Tags.Blocks.ORES).add(o);
        });
        AntimatterAPI.all(BlockStone.class, domain, s -> {
            this.getBuilder(Tags.Blocks.STONE).add(s);
            this.getBuilder(getBlockTag(new ResourceLocation(domain, "blocks/".concat(s.getId())))).add(s).replace(replace);
        });
        AntimatterAPI.all(BlockOreStone.class, domain, s -> {
            // String id = getConventionalMaterialType(MaterialType.ORE_STONE);
            this.getBuilder(Tags.Blocks.ORES).add(s);
            // this.getBuilder(getForgeBlockTag(id)).add(s);
        });
        AntimatterAPI.all(BlockStorage.class, domain, block -> {
            this.getBuilder(block.getType().getTag()).add(block).replace(replace);
            String name = String.join("", block.getType().getTag().getId().getPath(), "/", block.getMaterial().getId());
            this.getBuilder(getForgeBlockTag(name)).add(block);
            // if (block.getType() == FRAME) add climbable tag in 1.16
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

}
