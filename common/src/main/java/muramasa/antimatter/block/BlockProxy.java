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

public class BlockProxy extends BlockBasic {

    public BlockEntityType<TileEntityFakeBlock> TYPE;

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
}
