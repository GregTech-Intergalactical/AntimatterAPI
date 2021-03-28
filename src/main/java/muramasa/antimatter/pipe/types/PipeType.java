package muramasa.antimatter.pipe.types;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class PipeType<T extends PipeType<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected String domain;
    protected Material material;
    protected ImmutableSet<PipeSize> sizes = ImmutableSet.of();
    protected TileEntityType<?> tileType;
    protected Function<PipeType<?>, Supplier<? extends TileEntityPipe>> tileFunc = p -> () -> new TileEntityPipe(this);
    protected Map<PipeSize, Block> registeredBlocks;

    public PipeType(String domain, Material material) {
        this.domain = domain;
        this.material = material;
        sizes(PipeSize.VALUES);
        AntimatterAPI.register(getClass(), getId() + "_" + material.getId(), this);

    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry != ForgeRegistries.BLOCKS) return;
        Set<Block> blocks = getBlocks();
        registeredBlocks = blocks.stream().map(t ->new Pair<>(((BlockPipe<?>)t).getSize(), t.getBlock())).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        tileType = new TileEntityType<>(tileFunc.apply(this), blocks, null).setRegistryName(domain, getId() + "_" + material.getId());
        AntimatterAPI.register(TileEntityType.class, getId() + "_" + material.getId(), getTileType());
    }

    public Block getBlock(PipeSize size) {
        return registeredBlocks.get(size);
    }

    public Item getBlockItem(PipeSize size) {
        return Item.BLOCK_TO_ITEM.get(getBlock(size));
    }

    public abstract Set<Block> getBlocks();

    public String getDomain() {
        return domain;
    }

    @Override
    public abstract String getId();

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

    public T sizes(PipeSize... sizes) {
        this.sizes = ImmutableSet.copyOf(sizes);
        return (T) this;
    }

    public T setTile(Function<PipeType<?>, Supplier<? extends TileEntityPipe>> func) {
        this.tileFunc = func;
        return (T) this;
    }

    public T setTile(Supplier<? extends TileEntityPipe> supplier) {
        setTile(m -> supplier);
        return (T) this;
    }
}
