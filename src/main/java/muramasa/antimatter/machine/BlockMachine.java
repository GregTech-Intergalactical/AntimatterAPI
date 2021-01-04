package muramasa.antimatter.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.network.packets.FluidStackPacket;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.Data.COVER_EMPTY;
import static muramasa.antimatter.machine.MachineFlag.BASIC;
import static net.minecraft.util.Direction.*;

public class BlockMachine extends BlockDynamic implements IAntimatterObject, IItemBlockProvider, IColorHandler {

    protected Machine<?> type;
    protected Tier tier;

    public BlockMachine(Machine<?> type, Tier tier) {
        super(type.getDomain(), type.getId() + "_" + tier.getId(), Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
        this.type = type;
        this.tier = tier;
    }

    public Machine<?> getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
         TileEntityMachine machine = (TileEntityMachine)getType().getTileType().create();
         machine.ofState(state);
         return machine;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            TileEntityMachine tile = (TileEntityMachine)world.getTileEntity(pos);
            if (tile != null) {
                AntimatterCaps.getCustomEnergyHandler(tile).ifPresent(e -> System.out.println(e.getEnergy()));
                ItemStack stack = player.getHeldItem(hand);
                AntimatterToolType type = Utils.getToolType(player);
                if (hand == Hand.MAIN_HAND) {
                    if (player.getHeldItem(hand).getItem() instanceof ItemCover) {
                        boolean ok = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY,hit.getFace()).map(i -> i.placeCover(player,hit.getFace(),stack,((ItemCover) stack.getItem()).getCover())).orElse(false);
                        if (ok) {
                            return ActionResultType.SUCCESS;
                        }
                    }
                    //Handle tool types.
                    if (type == WRENCH || type == ELECTRIC_WRENCH) {
                        boolean ok = player.isCrouching() ? tile.setFacing(Utils.getInteractSide(hit)) : tile.setOutputFacing(Utils.getInteractSide(hit));
                        return ok ? ActionResultType.SUCCESS : ActionResultType.PASS;
                    } else if (type == HAMMER) {
                        tile.toggleMachine();
                        // TODO: Replace by new TranslationTextComponent()
                        player.sendMessage(new StringTextComponent("Machine was " + (tile.getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
                        return ActionResultType.SUCCESS;
                    } else if (type == CROWBAR) {
                        return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.removeCover(player, hit.getFace())).orElse(false) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                    } else if (type == SCREWDRIVER || type == ELECTRIC_SCREWDRIVER) {
                        CoverStack<?> instance = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.get(hit.getFace())).orElse(COVER_EMPTY);
                        return !instance.isEmpty() && instance.getCover().hasGui() && instance.openGui(player, hit.getFace()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                    }
                    //Has gui?
                    if (getType().has(MachineFlag.GUI) ) {
                        NetworkHooks.openGui((ServerPlayerEntity) player, tile, extra -> {
                            extra.writeBlockPos(pos);
                            tile.coverHandler.ifPresent(ch -> {
                                CoverStack<?> cover = ch.get(ch.getOutputFacing());
                                if (cover.getCover().isEqual(COVEROUTPUT)) {
                                    CoverOutput cov = (CoverOutput) cover.getCover();
                                    extra.writeBoolean(cov.shouldOutputFluids(cover));
                                    extra.writeBoolean(cov.shouldOutputItems(cover));
                                }
                            });
                            tile.fluidHandler.ifPresent(fh -> FluidStackPacket.encode(new FluidStackPacket(tile.getPos(),fh.getInputs(), fh.getOutputs()), extra));
                        });
                        return ActionResultType.SUCCESS;
                    }
                    //Otherwise, interact with fluids
                    if (tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).map(fh -> FluidUtil.tryEmptyContainer(stack, fh, 1000, player, true).success).orElse(false)) {
                        return ActionResultType.SUCCESS;
                    }
                    return tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.onInteract(player, hand, hit.getFace(), Utils.getToolType(player))).orElse(false) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                }
            }
        }
        return ActionResultType.CONSUME;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null) { //Y = 0 , reduce to xz plane
            Direction dir = getFacingFromVector((float) placer.getLookVec().x, (float) 0, (float) placer.getLookVec().z).getOpposite();
            world.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, dir));
        }
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return Data.WRENCH.getToolType();
    }

//    @Override
//    public void breakBlock(World world, BlockPos pos, BlockState state) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityMachine) {
//            TileEntityMachine machine = (TileEntityMachine) tile;
//            machine.itemHandler.ifPresent(h -> {
//                h.getInputList().forEach(i -> Utils.spawnItems(world, pos, null, i));
//                h.getOutputList().forEach(i -> Utils.spawnItems(world, pos, null, i));
//            });
//        }
//    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return getType().getDisplayName(getTier());
    }

    @Override
    public ItemGroup getItemGroup() {
        return getType().getGroup();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (getType().has(BASIC)) {
            tooltip.add(new TranslationTextComponent("machine.voltage.in").appendText(TextFormatting.GREEN + "" + getTier().getVoltage() + " (" + getTier().getId().toUpperCase() + ")"));
            tooltip.add(new TranslationTextComponent("machine.power.capacity").appendText(TextFormatting.BLUE + "" + (getTier().getVoltage() * 64)));
        }
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockMachine) && world == null || pos == null) return -1;
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof TileEntityMachine && i == 0 ? /*((TileEntityMachine) tile).getTextureData().getTint()*/-1 : -1;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMachine) {
            MachineState machineState = ((TileEntityMachine) tile).getMachineState();
            if (((TileEntityMachine) tile).coverHandler.isPresent()) {
                CoverHandler<?> h = ((TileEntityMachine) tile).coverHandler.orElse(null);
                return config.set(new int[] {
                    h.get(DOWN).skipRender() ? getModelId(facing, DOWN, machineState) : 0,
                    h.get(UP).skipRender() ? getModelId(facing, UP, machineState) : 0,
                    h.get(NORTH).skipRender() ? getModelId(facing, Utils.coverRotateFacing(NORTH,facing), machineState) : 0,
                    h.get(SOUTH).skipRender() ? getModelId(facing, Utils.coverRotateFacing(SOUTH,facing), machineState) : 0,
                    h.get(WEST).skipRender() ? getModelId(facing, Utils.coverRotateFacing(WEST,facing), machineState) : 0,
                    h.get(EAST).skipRender() ? getModelId(facing, Utils.coverRotateFacing(EAST,facing), machineState) : 0
                });
            } else {
                return config.set(new int[] {
                    getModelId(facing, DOWN, machineState),
                    getModelId(facing, UP, machineState),
                    getModelId(facing, NORTH, machineState),
                    getModelId(facing, SOUTH, machineState),
                    getModelId(facing, WEST, machineState),
                    getModelId(facing, EAST, machineState)
                });
            }
        }
        return config.set(new int[]{0});
    }

    private int getModelId(Direction facing, Direction overlay, MachineState state) {
        state = (state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE; //Map to only ACTIVE/IDLE.
        return ((state.ordinal() + 1) * 10000) + ((facing.getIndex() + 1) * 1000) + (overlay.getIndex() + 1);
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ItemModelBuilder b = prov.getBuilder(item).parent(prov.existing(Ref.ID, "block/preset/layered")).texture("base", type.getBaseTexture(tier));
        Texture[] overlays = type.getOverlayTextures(MachineState.ACTIVE);
        for (int s = 0; s < 6; s++) {
            b.texture("overlay" + Ref.DIRS[s].getName(), overlays[s]);
        }
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        buildModelsForState(builder, MachineState.IDLE);
        buildModelsForState(builder, MachineState.ACTIVE);
        builder.loader(AntimatterModelManager.LOADER_MACHINE);
        prov.state(block, builder);
    }

    private void buildModelsForState(AntimatterBlockModelBuilder builder, MachineState state) {
        Texture[] overlays = type.getOverlayTextures(state);
        for (Direction f : Arrays.asList(NORTH, WEST, SOUTH, EAST)) {
            for (Direction o : Ref.DIRS) {
                builder.config(getModelId(f, o, state), (b, l) -> l.add(b.of(type.getOverlayModel(o)).tex(of("base", type.getBaseTexture(tier), "overlay", overlays[o.getIndex()])).rot(f)));
            }
        }
    }
}
