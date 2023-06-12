package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlockFakeTile extends BlockBasic implements IRegistryEntryProvider, EntityBlock {
    public static BlockEntityType<TileEntityFakeBlock> TYPE;
    private static Set<Block> TILE_SET = new HashSet<>();
    public BlockFakeTile(String domain, String id, Properties properties) {
        super(domain, id, properties);
        AntimatterAPI.register(IRegistryEntryProvider.class, this);
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        if (registry == RegistryType.BLOCKS){
            TILE_SET.add(this);
        } else if (registry == RegistryType.BLOCK_ENTITIES) {
            if (TYPE == null){
                TYPE = new BlockEntityType<>((a,b) -> new TileEntityFakeBlock(this,a,b), TILE_SET, null);
                //((IForgeRegistry<BlockEntityType<?>>)registry).register(TYPE);
                AntimatterAPI.register(BlockEntityType.class, getId(), getDomain(), TYPE);
            }

        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TYPE.create(pos, state);
    }
}
