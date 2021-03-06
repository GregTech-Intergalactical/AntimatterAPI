package muramasa.antimatter.datagen.providers;

import com.google.common.collect.Maps;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.util.Utils.*;

public class AntimatterBlockTagProvider extends ForgeBlockTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen, ExistingFileHelper helper) {
        super(gen, helper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.tagToBuilder);
        this.tagToBuilder.clear();
        registerTags();
        //TagCollectionReader<Block> blocks = new TagCollectionReader<>(Registry.BLOCK::getOptional, "tags/blocks", "block");
        
       // blocks.buildTagCollectionFromMap(tagToBuilder).getIDTagMap().forEach((k, v) -> DynamicResourcePack.addTag("blocks", k, v.(this.registry::getKey)));
        tagToBuilder.forEach((k, v) -> DynamicResourcePack.addTag("blocks", k, v.serialize()));
        b.forEach(tagToBuilder::put);
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
            this.getOrCreateBuilder(getForgeBlockTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
            if (o.getOreType() == Data.ORE) this.getOrCreateBuilder(Tags.Blocks.ORES).add(o);
        });
        AntimatterAPI.all(BlockStone.class, domain, s -> {
            this.getOrCreateBuilder(Tags.Blocks.STONE).add(s);
            this.getOrCreateBuilder(getBlockTag(new ResourceLocation(domain, "blocks/".concat(s.getId())))).add(s).replace(replace);
        });
        AntimatterAPI.all(BlockOreStone.class, domain, s -> {
            // String id = getConventionalMaterialType(MaterialType.ORE_STONE);
            this.getOrCreateBuilder(Tags.Blocks.ORES).add(s);
            // this.getBuilder(getForgeBlockTag(id)).add(s);
        });
        AntimatterAPI.all(BlockStorage.class, domain, block -> {
            this.getOrCreateBuilder(block.getType().getTag()).add(block).replace(replace);
            String name = String.join("", block.getType().getTag().getName().getPath(), "/", block.getMaterial().getId());
            this.getOrCreateBuilder(getForgeBlockTag(name)).add(block);
            // if (block.getType() == FRAME) add climbable tag in 1.16
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

}
