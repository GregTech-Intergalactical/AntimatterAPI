package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterItemTagProvider extends ForgeItemTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;
    private ResourceMethod method = ResourceMethod.PROVIDER_GEN;

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
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
        TagCollection<Item> tags = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
        Map<ResourceLocation, Tag.Builder<Item>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().getId(), Map.Entry::getValue));
        tags.registerAll(map);
        tags.getTagMap().forEach((k, v) -> DynamicResourcePack.addItemTag(k, v.serialize(this.registry::getKey)));
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
        Tag<Block> blockTag = BLOCK.getTag(), frameTag = FRAME.getTag();
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(Tags.Blocks.STONE, Tags.Items.STONE);
        this.copy(blockTag, blockToItemTag(blockTag));
        this.copy(frameTag, blockToItemTag(frameTag));
        AntimatterAPI.all(BlockOre.class, o -> {
            String name = String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
            copy(getForgeBlockTag(name), getForgeItemTag(name));
        });
        AntimatterAPI.all(BlockStone.class, domain, s -> {
            String id = "blocks/".concat(s.getId());
            copy(getBlockTag(new ResourceLocation(domain, id)), getItemTag(new ResourceLocation(domain, id)));
        });
        AntimatterAPI.all(BlockStorage.class, domain, storage -> {
                    MaterialType<?> type = storage.getType();
                    String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
                    copy(getForgeBlockTag(name), getForgeItemTag(name));
                });
        AntimatterAPI.all(MaterialItem.class, domain, item -> {
                    MaterialType<?> type = item.getType();
                    getBuilder(type.getTag()).add(item);
                    String name = String.join("", getConventionalMaterialType(type), "/", item.getMaterial().getId());
                    getBuilder(getForgeItemTag(name)).add(item).replace(replace);
                    if (type == INGOT || type == GEM) getBuilder(Tags.Items.BEACON_PAYMENT).add(item);
                });
        AntimatterAPI.all(IAntimatterTool.class, domain, tool -> getBuilder(tool.getType().getTag()).add(tool.asItem()));
    }

    @Override
    public String getName() {
        return providerName;
    }

}