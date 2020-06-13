package muramasa.antimatter.pipe.types;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.tesseract.ITileWrapper;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PipeType<T extends PipeType<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected String domain;
    protected Material material;
    protected ImmutableSet<PipeSize> sizes = ImmutableSet.of();
    protected TileEntityType<?> tileType;
    protected Function<PipeType<?>, Supplier<? extends TileEntityPipe>> tileFunc = p -> () -> new TileEntityPipe(this);

    public PipeType(String domain, Material material) {
        this.domain = domain;
        this.material = material;
        sizes(PipeSize.VALUES);
        AntimatterAPI.register(PipeType.class, getId() + "_" + material.getId(), this);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain)) return;
        if (registry == null) tileType = new TileEntityType<>(tileFunc.apply(this), getBlocks(), null);
    }

    public abstract Set<Block> getBlocks();

    public String getDomain() {
        return domain;
    }

    @Override
    public abstract String getId();

    public abstract String getTypeName();

    public abstract ITileWrapper getTileWrapper(TileEntity tile);

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
