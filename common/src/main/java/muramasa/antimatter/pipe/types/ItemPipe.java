package muramasa.antimatter.pipe.types;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.blockentity.pipe.BlockEntityItemPipe;
import muramasa.antimatter.registration.RegistryType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemPipe<T extends ItemPipe<T>> extends PipeType<T> {

    protected int[] caps;
    protected int[] stepsizes;

    protected Map<PipeSize, Block> registeredRestrictedBlocks;

    public ItemPipe(String domain, Material material) {
        super(domain, material, BlockEntityItemPipe::new);
        material.flags(MaterialTags.ITEMPIPE);
        sizes(PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE);
    }

    @Override
    public String getType() {
        return "item_pipe";
    }

    @Override
    public String getTypeName() {
        return "item_pipe";
    }

    @Override
    public Set<Block> getBlocks() {
        return sizes.stream().map(s -> new BlockItemPipe(this, s, false)).collect(Collectors.toSet());
    }

    public Set<Block> getRestrictedBlocks() {
        return sizes.stream().map(s -> new BlockItemPipe(this, s, true)).collect(Collectors.toSet());
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        if (registry != RegistryType.BLOCKS)
            return;
        Set<Block> blocks = getBlocks();
        Set<Block> restrictedBlocks = getRestrictedBlocks();
        registeredBlocks = blocks.stream().map(t -> new Pair<>(((BlockPipe<?>) t).getSize(),t))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        registeredRestrictedBlocks = restrictedBlocks.stream().map(t -> new Pair<>(((BlockPipe<?>) t).getSize(),t))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        tileType = new BlockEntityType<>((pos, state) -> tileFunc.create((T) this, pos, state), Stream.concat(blocks.stream(), restrictedBlocks.stream()).collect(Collectors.toSet()), null);
        AntimatterAPI.registerTransferApiPipe(tileType);
        AntimatterAPI.register(BlockEntityType.class, getId(), getDomain(), getTileType());
    }

    public Block getRestrictedBlock(PipeSize size) {
        return registeredRestrictedBlocks.get(size);
    }

    public int getCapacity(PipeSize size) {
        return caps[size.ordinal()];
    }

    public int getStepsize(PipeSize size){
        return stepsizes[size.ordinal()];
    }

    public T caps(int baseCap) {
        this.caps = new int[]{baseCap, baseCap * 2, baseCap * 3, baseCap * 4, baseCap * 5, baseCap * 6};
        return (T) this;
    }

    public T caps(int... caps) {
        this.caps = caps;
        return (T) this;
    }

    public T stepsize(int baseStepsize) {
        this.stepsizes = new int[]{baseStepsize * 8, baseStepsize * 4, baseStepsize * 2, baseStepsize, baseStepsize / 2, baseStepsize / 4};
        return (T) this;
    }

    public T stepsize(int... stepsize) {
        this.stepsizes = stepsize;
        return (T) this;
    }
}
