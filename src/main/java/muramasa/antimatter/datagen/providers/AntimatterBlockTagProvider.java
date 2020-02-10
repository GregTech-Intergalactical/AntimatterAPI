package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blocks.BlockStone;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.materials.IMaterialTag;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static muramasa.antimatter.materials.MaterialType.ORE;
import static muramasa.antimatter.materials.MaterialType.ORE_SMALL;
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
        AntimatterAPI.all(StoneType.class).forEach(s -> {
            IMaterialTag.all(ORE, ORE_SMALL).stream().filter(m -> m.getDomain().equals(providerDomain)).forEach(m -> {
                if (m.has(ORE)) {
                    Block block = BlockOre.get(m, ORE, s).getBlock();
                    String name = String.join("", getConventionalStoneType(s), "_", "ores/", m.getId());
                    this.getBuilder(getForgeBlockTag(name)).add(block).replace(replace);
                    this.getBuilder(Tags.Blocks.ORES).add(block);
                }
                if (m.has(ORE_SMALL)) {
                    Block block = BlockOre.get(m, ORE_SMALL, s).getBlock();
                    String name = String.join("", getConventionalStoneType(s), "_", "small_ores/", m.getId());
                    this.getBuilder(getForgeBlockTag(name)).add(block).replace(replace);
                }
            });
        });
        AntimatterAPI.all(BlockStone.class).stream().filter(s -> s.getDomain().equals(providerDomain)).forEach(s -> {
            this.getBuilder(Tags.Blocks.STONE).add(s);
            this.getBuilder(getBlockTag(providerDomain, "blocks/".concat(s.getId()))).add(s);
        });
        AntimatterAPI.all(BlockStorage.class)
                .stream().filter(block -> block.getMaterial().getDomain().equals(providerDomain)).forEach(storage -> {
                    MaterialType type = storage.getType();
                    this.getBuilder(type.getTag()).add(storage);
                    String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
                    if (storage.getType() == MaterialType.BLOCK) this.getBuilder(Tags.Blocks.SUPPORTS_BEACON).add(storage);
                    this.getBuilder(getForgeBlockTag(name)).add(storage);
                });
    }

    @Override
    public String getName() {
        return providerName;
    }

}
