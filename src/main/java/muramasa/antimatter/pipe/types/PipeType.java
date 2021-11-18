package muramasa.antimatter.pipe.types;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeItemBlock;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PipeType<T extends PipeType<T>> implements IRegistryEntryProvider, ISharedAntimatterObject {

    /**
     * Basic Members
     **/
    public final String domain;
    protected Material material;
    protected ImmutableSet<PipeSize> sizes = ImmutableSet.of();
    protected TileEntityType<?> tileType;
    protected TileEntityType<?> coveredType;
    protected Map<PipeSize, Block> registeredBlocks;

    private final Function<T, TileEntityPipe<T>> tileFunc;
    private final Function<T, TileEntityPipe<T>> coveredFunc;

    public PipeType(String domain, Material material, Function<T, TileEntityPipe<T>> func,
                    Function<T, TileEntityPipe<T>> covered) {
        this.domain = domain;
        this.material = material;
        sizes(PipeSize.VALUES);
        // AntimatterAPI.register(getClass(), getId() + "_" + material.getId(),
        // getDomain(), this);
        this.tileFunc = func;
        this.coveredFunc = covered;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry != ForgeRegistries.BLOCKS)
            return;
        Set<Block> blocks = getBlocks();
        registeredBlocks = blocks.stream().map(t -> new Pair<>(((BlockPipe<?>) t).getSize(), t.getBlock()))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        tileType = new TileEntityType<>(() -> tileFunc.apply((T) this), blocks, null).setRegistryName(getDomain(), getId());
        coveredType = new TileEntityType<>(() -> coveredFunc.apply((T) this), blocks, null).setRegistryName(getDomain(),
                getId() + "_covered");
        AntimatterAPI.register(TileEntityType.class, getId(), getDomain(), getTileType());
        AntimatterAPI.register(TileEntityType.class, getId() + "_covered", getDomain(), getCoveredType());
    }

    public Block getBlock(PipeSize size) {
        return registeredBlocks.get(size);
    }

    public PipeItemBlock getBlockItem(PipeSize size) {
        return (PipeItemBlock) Item.BY_BLOCK.get(getBlock(size));
    }

    public abstract Set<Block> getBlocks();

    /*
     * public String getDomain() { return domain; }
     */

    public abstract String getType();

    @Override
    public String getId() {
        return getType() + "_" + material.getId();
    }

    public abstract String getTypeName();

    public Material getMaterial() {
        return material;
    }

    public ImmutableSet<PipeSize> getSizes() {
        return sizes;
    }

    public TileEntityType<?> getTileType() {
        return tileType;
    }

    public TileEntityType<?> getCoveredType() {
        return coveredType;
    }

    public T sizes(PipeSize... sizes) {
        this.sizes = ImmutableSet.copyOf(sizes);
        return (T) this;
    }
}
