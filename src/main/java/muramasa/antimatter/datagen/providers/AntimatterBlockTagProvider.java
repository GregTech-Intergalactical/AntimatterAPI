package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterBlockTagProvider extends ForgeBlockTagsProvider implements IAntimatterProvider {

    private String providerDomain, providerName;
    private boolean replace;
    private ResourceMethod method = ResourceMethod.PROVIDER_GEN;

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run(ResourceMethod method) {
        this.method = method;
        this.tagToBuilder.clear();
        registerTags();
        TagCollection<Block> tags = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
        Map<ResourceLocation, Tag.Builder<Block>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().getId(), Map.Entry::getValue));
        tags.registerAll(map);
        tags.getTagMap().forEach((k, v) -> DynamicResourcePack.addBlockTag(k, v.serialize(this.registry::getKey)));
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
            String name = String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
            this.getBuilder(getForgeBlockTag(name)).add(o);
            this.getBuilder(Tags.Blocks.ORES).add(o);
        });
        AntimatterAPI.all(BlockStone.class, domain, s -> {
            this.getBuilder(Tags.Blocks.STONE).add(s);
            this.getBuilder(getBlockTag(new ResourceLocation(domain, "blocks/".concat(s.getId())))).add(s);
        });
        AntimatterAPI.all(BlockStorage.class, domain, block -> {
            MaterialType<?> type = block.getType();
            Tag<Block> tag = type.getTag();
            this.getBuilder(tag).add(block);
            String name = String.join("", tag.getId().getPath(), "/", block.getMaterial().getId());
            this.getBuilder(getForgeBlockTag(name)).add(block);
            // if (block.getType() == FRAME) add climbable tag in 1.16
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

}
