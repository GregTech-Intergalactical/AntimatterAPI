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
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PipeType<T extends PipeType<T>> implements IRegistryEntryProvider, ISharedAntimatterObject {

    /**
     * Basic Members
     **/
    public final String domain;
    protected Material material;
    protected ImmutableSet<PipeSize> sizes = ImmutableSet.of();
    protected BlockEntityType<?> tileType;
    protected Map<PipeSize, Block> registeredBlocks;

    private final TileEntityBase.BlockEntitySupplier<TileEntityPipe<?>, T> tileFunc;

    public PipeType(String domain, Material material, TileEntityBase.BlockEntitySupplier<TileEntityPipe<?>, T> func) {
        this.domain = domain;
        this.material = material;
        sizes(PipeSize.VALUES);
        this.tileFunc = func;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry != ForgeRegistries.BLOCKS)
            return;
        Set<Block> blocks = getBlocks();
        registeredBlocks = blocks.stream().map(t -> new Pair<>(((BlockPipe<?>) t).getSize(),t))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        tileType = new BlockEntityType<>((pos,state) -> tileFunc.create((T) this, pos, state), blocks, null).setRegistryName(getDomain(), getId());
        AntimatterAPI.register(BlockEntityType.class, getId(), getDomain(), getTileType());
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

    public BlockEntityType<?> getTileType() {
        return tileType;
    }

    public T sizes(PipeSize... sizes) {
        this.sizes = ImmutableSet.copyOf(sizes);
        return (T) this;
    }
}
