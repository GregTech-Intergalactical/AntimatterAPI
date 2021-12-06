package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class BlockProxy extends BlockBasic implements IRegistryEntryProvider {

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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        TileEntityFakeBlock tile = (TileEntityFakeBlock) world.getBlockEntity(pos);
        return tile != null && tile.getState() != null ? tile.getState().getBlock().asItem().getDefaultInstance() : ItemStack.EMPTY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry == ForgeRegistries.BLOCK_ENTITIES) {
            // ((IForgeRegistry<Block>)registry).register(this);
            TYPE = BlockEntityType.Builder.of((a,b) -> new TileEntityFakeBlock(this, a, b), Data.PROXY_INSTANCE).build(null);
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
            ForgeRegistries.ITEMS.getValues().parallelStream().filter(t -> t.isCorrectToolForDrops(fake.getState())).findFirst().ifPresent(item -> {
                builder.withParameter(LootContextParams.TOOL, new ItemStack(item));
            });
            return fake.getState().getDrops(builder);
        }
        return super.getDrops(state, builder);
    }
}
