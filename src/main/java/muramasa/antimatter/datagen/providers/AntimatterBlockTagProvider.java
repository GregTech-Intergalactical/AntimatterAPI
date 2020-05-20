package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterBlockTagProvider extends BlockTagsProvider {

    private String providerDomain, providerName;
    private boolean replace;

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    protected void registerTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(StoneType.class, s -> IMaterialTag.all(ORE, ORE_SMALL).stream().filter(m -> m.getDomain().equals(domain)).forEach(m -> {
            if (m.has(ORE)) {
                Block block = ORE.get().get(m, s).asBlock();
                String name = String.join("", getConventionalStoneType(s), "_", "ores/", m.getId());
                this.getBuilder(getForgeBlockTag(name)).add(block).replace(replace);
                this.getBuilder(Tags.Blocks.ORES).add(block);
            }
            if (m.has(ORE_SMALL)) {
                Block block = ORE_SMALL.get().get(m, s).asBlock();
                String name = String.join("", getConventionalStoneType(s), "_", "small_ores/", m.getId());
                this.getBuilder(getForgeBlockTag(name)).add(block).replace(replace);
            }
        }));
        AntimatterAPI.all(BlockStone.class).stream().filter(s -> s.getRegistryName().getNamespace().equals(domain)).forEach(s -> {
            this.getBuilder(Tags.Blocks.STONE).add(s);
            this.getBuilder(getBlockTag(new ResourceLocation(domain, "blocks/".concat(s.getId())))).add(s);
        });
        AntimatterAPI.all(BlockStorage.class).stream().filter(storage -> storage.getMaterial().getDomain().equals(domain)).forEach(block -> {
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
