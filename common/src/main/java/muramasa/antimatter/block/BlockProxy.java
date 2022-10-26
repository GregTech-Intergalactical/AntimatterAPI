package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class BlockProxy extends BlockBasic implements IRegistryEntryProvider, EntityBlock {

    public BlockEntityType<?> TYPE;

    public BlockProxy(String domain, String id, Properties properties) {
        super(domain, id, properties);
        AntimatterAPI.register(IRegistryEntryProvider.class, this);
    }

    public BlockProxy(String domain, String id) {
        super(domain, id);
    }

    

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(BlockStateProperties.FACING);
        pBuilder.add(BlockMachine.HORIZONTAL_FACING);
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        builder.loader(AntimatterModelManager.LOADER_PROXY);
        prov.state(block, builder);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        TileEntityFakeBlock tile = (TileEntityFakeBlock) world.getBlockEntity(pos);
        return tile != null && tile.getState() != null ? tile.getState().getBlock().asItem().getDefaultInstance() : ItemStack.EMPTY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRegistryBuild(RegistryType registry) {
        if (registry == RegistryType.BLOCK_ENTITIES) {
            TYPE = new BlockEntityType<>((a,b) -> new TileEntityFakeBlock(this,a,b), Set.of(this), null);
            //((IForgeRegistry<BlockEntityType<?>>)registry).register(TYPE);
            AntimatterAPI.register(BlockEntityType.class, getId(), getDomain(), TYPE);
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tileentity instanceof TileEntityFakeBlock) {
            TileEntityFakeBlock fake = (TileEntityFakeBlock) tileentity;
            AntimatterPlatformUtils.getAllItems().parallelStream().filter(t -> t.isCorrectToolForDrops(fake.getState())).findFirst().ifPresent(item -> {
                builder.withParameter(LootContextParams.TOOL, new ItemStack(item));
            });
            return fake.getState().getDrops(builder);
        }
        return super.getDrops(state, builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityFakeBlock(this, pos, state);
    }
}
