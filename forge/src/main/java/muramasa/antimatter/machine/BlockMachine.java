package muramasa.antimatter.machine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.SoundHelper;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.machine.MachineFlag.BASIC;

public class BlockMachine extends BlockBasic implements IItemBlockProvider, EntityBlock {

    public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.create("horizontal_facing", Direction.Plane.HORIZONTAL);

    protected Machine<?> type;
    protected Tier tier;
    protected final StateDefinition<Block, BlockState> stateContainer;

    public BlockMachine(Machine<?> type, Tier tier) {
        this(type, tier, Properties.of(WRENCH_MATERIAL).strength(1.0f, 10.0f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    }

    public BlockMachine(Machine<?> type, Tier tier, Properties properties) {
        super(type.getDomain(), type.getId() + "_" + tier.getId(), properties);
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.type = type;
        this.tier = tier;
        this.createBlockStateDefinition(builder);
        this.stateContainer = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateContainer.any());
    }

    public Machine<?> getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (type == null) return; // means this is the first run
        if (type.allowVerticalFacing()) {
            builder.add(BlockStateProperties.FACING).add(HORIZONTAL_FACING);
        } else {
            builder.add(BlockStateProperties.HORIZONTAL_FACING);
        }
    }

    @Override
    public StateDefinition<Block, BlockState> getStateDefinition() {
        return stateContainer;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (type.allowVerticalFacing()) {
            Direction dir = context.getNearestLookingDirection().getOpposite();
            dir = dir.getAxis() == Axis.Y ? dir.getOpposite() : dir;
            return this.defaultBlockState().setValue(HORIZONTAL_FACING, type.handlePlacementFacing(context, BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())).setValue(BlockStateProperties.FACING, type.handlePlacementFacing(context, BlockStateProperties.FACING, dir));
        } else {
            return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, type.handlePlacementFacing(context, HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()));
        }
    }

    //TODO 1.18
/*
    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        TileEntityMachine machine = (TileEntityMachine) getType().getTileType().create();
        machine.ofState(state);
        return machine;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }*/

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isClientSide) {
            TileEntityMachine<?> tile = (TileEntityMachine<?>) worldIn.getBlockEntity(pos);
            if (tile != null) {
                tile.onBlockUpdate(fromPos);
            }
        }
    }


    @Nonnull
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult ty; //= onBlockActivatedBoth(state, world, pos, player, hand, hit);
        if (!world.isClientSide) {
            TileEntityMachine<?> tile = (TileEntityMachine<?>) world.getBlockEntity(pos);
            if (tile != null) {
                ItemStack stack = player.getItemInHand(hand);
                AntimatterToolType type = Utils.getToolType(player);
                ty = tile.onInteract(state, world, pos, player, hand, hit, type);
                if (ty.consumesAction()) return ty;
                if (hand == InteractionHand.MAIN_HAND) {
                    if (player.getItemInHand(hand).getItem() instanceof IHaveCover) {
                        CoverFactory factory = ((IHaveCover) stack.getItem()).getCover();
                        Direction dir = Utils.getInteractSide(hit);
                        boolean ok = tile.getCapability(AntimatterPlatformUtils.getCoverCap(), Utils.getInteractSide(hit)).map(i -> i.placeCover(player, Utils.getInteractSide(hit), stack, factory.get().get(i, ((IHaveCover) stack.getItem()).getTier(), dir, factory))).orElse(false);
                        if (ok) {
                            return InteractionResult.SUCCESS;
                        }
                    }
                    //Handle tool types.
                    if (type == WRENCH || type == ELECTRIC_WRENCH) {
                        if (tile.wrenchMachine(player, hit, player.isCrouching())) {
                            Utils.damageStack(stack, hand, player);
                            return InteractionResult.SUCCESS;
                        }
                    } else if (type == SOFT_HAMMER) {
                        tile.toggleMachine();
                        if (tile.getMachineState() == MachineState.DISABLED) {
                            player.sendMessage(new TextComponent("Disabled machine."), player.getUUID());
                        } else {
                            player.sendMessage(new TextComponent("Enabled machine."), player.getUUID());
                        }
                        Utils.damageStack(stack, player);
                        return InteractionResult.SUCCESS;
                    } else if (type == CROWBAR) {
                        if (!player.isCrouching()) {
                            if (tile.getCapability(AntimatterPlatformUtils.getCoverCap(), Utils.getInteractSide(hit)).map(h -> h.removeCover(player, Utils.getInteractSide(hit), false)).orElse(false)) {
                                Utils.damageStack(stack,hand, player);
                                return InteractionResult.SUCCESS;
                            }
                        } else {
                            if (tile.getCapability(AntimatterPlatformUtils.getCoverCap(), Utils.getInteractSide(hit)).map(h -> h.moveCover(player, hit.getDirection(), Utils.getInteractSide(hit))).orElse(false)) {
                                Utils.damageStack(stack,hand, player);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    } else if (type == SCREWDRIVER || type == ELECTRIC_SCREWDRIVER) {
                        ICover instance = tile.getCapability(AntimatterPlatformUtils.getCoverCap(), Utils.getInteractSide(hit)).map(h -> h.get(hit.getDirection())).orElse(ICover.empty);
                        if (!player.isCrouching()) {
                            if (!instance.isEmpty() && instance.openGui(player, hit.getDirection())) {
                                Utils.damageStack(stack,hand, player);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                    boolean coverInteract = tile.getCapability(AntimatterPlatformUtils.getCoverCap(), hit.getDirection()).map(h -> h.onInteract(player, hand, hit.getDirection(), Utils.getToolType(player))).orElse(false);
                    if (coverInteract) return InteractionResult.SUCCESS;
                    //Has gui?
                    if (tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getDirection()).map(fh -> {
                        fh = tile.fluidHandler.map(t -> t.getGuiHandler()).orElse(fh);
                        FluidActionResult res = FluidUtil.tryEmptyContainer(stack, fh, 1000, player, true);
                        if (res.isSuccess() && !player.isCreative()) {
                            boolean single = stack.getCount() == 1;
                            stack.shrink(1);
                            if (single) {
                                player.setItemInHand(hand, res.result);
                            } else {
                                if (!player.addItem(res.result)) {
                                    player.drop(res.result, true);
                                }
                            }

                        }
                        if (!res.isSuccess()) {
                            res = FluidUtil.tryFillContainer(stack, fh, 1000, player, true);
                            if (res.isSuccess() && !player.isCreative()) {
                                boolean single = stack.getCount() == 1;
                                stack.shrink(1);
                                if (single) {
                                    player.setItemInHand(hand, res.result);
                                } else {
                                    if (!player.addItem(res.result)) {
                                        player.drop(res.result, true);
                                    }
                                }
                            }
                        }
                        return res.isSuccess();
                    }).orElse(false)) {
                        return InteractionResult.SUCCESS;
                    }
                    if (getType().has(MachineFlag.GUI) && tile.canPlayerOpenGui(player)) {
                        AntimatterPlatformUtils.openGui((ServerPlayer) player, tile, extra -> {
                            extra.writeBlockPos(pos);
                        });
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    /* //This messes up cover logic.
        @Override
        public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
            if (placer != null) { //Y = 0 , reduce to xz plane
                float y = (float) (type.allowVerticalFacing() ? placer.getLookVec().y : 0);
                Direction dir = getFacingFromVector((float) placer.getLookVec().x, y, (float) placer.getLookVec().z).getOpposite();
                BlockState state1 = state.with((type.allowVerticalFacing() ? BlockStateProperties.FACING : BlockStateProperties.HORIZONTAL_FACING), dir);
                if (type.allowVerticalFacing()) state1 = state1.with(HORIZONTAL_FACING, placer.getHorizontalFacing().getOpposite());
                world.setBlockState(pos, state1);
            }
        }
    */

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!worldIn.isClientSide) {
                BlockEntity tile = worldIn.getBlockEntity(pos);
                if (tile == null) return;
                TileEntityMachine<?> machine = (TileEntityMachine<?>) tile;
                machine.itemHandler.ifPresent(t -> t.getAllItems().forEach(stack -> Containers.dropItemStack(worldIn, machine.getBlockPos().getX(), machine.getBlockPos().getY(), machine.getBlockPos().getZ(), stack)));
                machine.coverHandler.ifPresent(t -> t.getDrops().forEach(stack -> Containers.dropItemStack(worldIn, machine.getBlockPos().getX(), machine.getBlockPos().getY(), machine.getBlockPos().getZ(), stack)));
            } else {
                SoundHelper.clear(worldIn, pos);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public Component getDisplayName(ItemStack stack) {
        return getType().getDisplayName(getTier());
    }

    @Override
    public CreativeModeTab getItemGroup() {
        return getType().getGroup();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        if (getType().has(BASIC)) {
            if (getTier().getVoltage() > 0) {
                tooltip.add(new TranslatableComponent("machine.voltage.in").append(": ").append(new TextComponent(getTier().getVoltage() + " (" + getTier().getId().toUpperCase() + ")")).withStyle(ChatFormatting.GREEN));
                tooltip.add(new TranslatableComponent("machine.power.capacity").append(": ").append(new TextComponent("" + (getTier().getVoltage() * 64))).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        ItemModelBuilder b = prov.getBuilder(item).parent(prov.existing(Ref.ID, "block/preset/layered")).texture("base", type.getBaseTexture(tier)[0]);
        Texture[] base = type.getBaseTexture(tier);
        if (base.length >= 6) {
            for (int s = 0; s < 6; s++) {
                b.texture("base" + Utils.coverRotateFacing(Ref.DIRS[s], Direction.NORTH).getSerializedName(), base[s]);
            }
        }
        Texture[] overlays = type.getOverlayTextures(MachineState.ACTIVE, tier);
        for (int s = 0; s < 6; s++) {
            b.texture("overlay" + Utils.coverRotateFacing(Ref.DIRS[s], Direction.NORTH).getSerializedName(), overlays[s]);
        }
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        buildModelsForState(builder, MachineState.IDLE);
        buildModelsForState(builder, MachineState.ACTIVE);
        builder.loader(AntimatterModelManager.LOADER_MACHINE);
        builder.property("particle", getType().getBaseTexture(tier)[0].toString());
        prov.state(block, builder);
    }

    void buildModelsForState(AntimatterBlockModelBuilder builder, MachineState state) {
        Texture[] overlays = type.getOverlayTextures(state, tier);
        JsonArray arr = new JsonArray();

        for (Direction dir : Ref.DIRS) {
            JsonObject obj = builder.addModelObject(new JsonObject(), this.getType().getOverlayModel(dir).toString(), of("base", getType().getBaseTexture(tier, dir).toString(), "overlay", overlays[dir.get3DDataValue()].toString()));
           // obj.addProperty("loader", AntimatterModelManager.LOADER_MACHINE_SIDE.getLoc().toString());
            arr.add(obj);
        }

        builder.property(state.getDisplayName(), arr);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) entity;
            return machine.getWeakRedstonePower(side);
        }
        return super.getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) entity;
            return machine.getStrongRedstonePower(side);
        }
        return super.getDirectSignal(blockState, blockAccess, pos, side);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        TileEntityMachine<?> machine = (TileEntityMachine) getType().getTileType().create(pos, state);
        return machine;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() && !getType().canClientTick()) return null;
        return TileEntityTickable::commonTick;
    }
}
