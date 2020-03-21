package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterItemTagProvider extends ItemTagsProvider {

    private String providerDomain, providerName;
    private boolean replace;

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    protected void registerTags() {
        processTags(providerDomain);
    }

    public void processTags(String domain) {
        Tag<Block> blockTag = BLOCK.getTag();
        Tag<Block> frameTag = FRAME.getTag();
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(blockTag, blockToItemTag(blockTag));
        this.copy(frameTag, blockToItemTag(frameTag));
        this.copy(Tags.Blocks.STONE, Tags.Items.STONE);
        IMaterialTag.all(ORE, ORE_SMALL).stream().filter(m -> m.getDomain().equals(domain)).forEach(m -> {
            AntimatterAPI.all(StoneType.class).forEach(s -> {
                if (m.has(ORE)) {
                    String name = String.join("", getConventionalStoneType(s), "_", "ores/", m.getId());
                    this.copy(getForgeBlockTag(name), getForgeItemTag(name));
                }
                if (m.has(ORE_SMALL)) {
                    String name = String.join("", getConventionalStoneType(s), "_", "small_ores/", m.getId());
                    this.copy(getForgeBlockTag(name), getForgeItemTag(name));
                }
            });
        });
        AntimatterAPI.all(BlockStone.class).stream().filter(s -> s.getDomain().equals(domain)).forEach(s -> {
            String id = "blocks/".concat(s.getId());
            this.copy(getBlockTag(new ResourceLocation(domain, id)), getItemTag(new ResourceLocation(domain, id)));
        });
        AntimatterAPI.all(BlockStorage.class)
                .stream().filter(block -> block.getMaterial().getDomain().equals(domain)).forEach(storage -> {
                    MaterialType<?> type = storage.getType();
                    String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
                    this.copy(getForgeBlockTag(name), getForgeItemTag(name));
                });
        AntimatterAPI.all(MaterialItem.class).stream()
                .filter(i -> i.getMaterial().getDomain().equals(domain)).forEach(item -> {
                    MaterialType<?> type = item.getType();
                    this.getBuilder(type.getTag()).add(item);
                    String name = String.join("", getConventionalMaterialType(type), "/", item.getMaterial().getId());
                    this.getBuilder(getForgeItemTag(name)).add(item).replace(replace);
                    if (type == INGOT || type == GEM) this.getBuilder(Tags.Items.BEACON_PAYMENT).add(item);
                });
        AntimatterAPI.all(IAntimatterTool.class).stream().filter(t -> t.getDomain().equals(domain)).forEach(tool -> {
            this.getBuilder(tool.getType().getTag()).add(tool.asItem());
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

}