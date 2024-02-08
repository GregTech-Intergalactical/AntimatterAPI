package muramasa.antimatter.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.blockentity.BlockEntityBase;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScannerItem extends ItemBasic<ScannerItem> {
    final boolean simple;

    public ScannerItem(String domain, String id, boolean simple) {
        this(domain, id, simple, "", new Properties().tab(Ref.TAB_ITEMS));

    }

    public ScannerItem(String domain, String id, boolean simple, String subDir, Properties properties) {
        super(domain, id, subDir, properties);
        this.simple = simple;
    }

    public ScannerItem(String domain, String id, boolean simple, Properties properties) {
        this(domain, id, simple, "", properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Utils.literal(this.tooltip));
        if (Screen.hasShiftDown() && this == Data.DEBUG_SCANNER) {
            tooltip.add(Utils.literal("Blocks: " + AntimatterAPI.all(Block.class).size()));
            tooltip.add(Utils.literal("Machines: " + Machine.getTypes(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH).size()));
            tooltip.add(Utils.literal("Pipes: " + AntimatterAPI.all(BlockPipe.class).size()));
            tooltip.add(Utils.literal("Storage: " + AntimatterAPI.all(BlockStorage.class).size()));
            tooltip.add(Utils.literal("Ores: " + AntimatterAPI.all(BlockOre.class).size()));
            tooltip.add(Utils.literal("Stones: " + AntimatterAPI.all(BlockStone.class).size()));
            tooltip.add(Utils.literal("Data:"));
            tooltip.add(Utils.literal("Ore Materials: " + AntimatterMaterialTypes.ORE.all().size()));
            tooltip.add(Utils.literal("Small Ore Materials: " + AntimatterMaterialTypes.ORE_SMALL.all().size()));
        }
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResult.CONSUME;
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
        boolean success = false;
        if (tile instanceof BlockEntityBase<?> base) {
            base.getInfo(simple).forEach(s -> context.getPlayer().sendMessage(Utils.literal(s), context.getPlayer().getUUID()));
            success = true;
        }
        if (state.getBlock() instanceof BlockDynamic dynamic && context.getPlayer() != null) {
            dynamic.getInfo(new ObjectArrayList<>(), context.getLevel(), state, context.getClickedPos()).forEach(s -> {
                context.getPlayer().sendMessage(Utils.literal(s), context.getPlayer().getUUID());
            });
            success = true;
        }
        if (success) return InteractionResult.SUCCESS;
        return super.useOn(context);
    }

    /*@Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return super.onItemUseFirst(stack, context);
    }*/

    //    @Override
//    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        ItemStack stack = player.getHeldItem(hand);
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile != null) {
//            if (Data.DebugScanner.isEqual(stack)) {
//                if (tile instanceof BlockEntityMachine) {
//                    if (tile instanceof BlockEntityMultiMachine) {
//                        if (!world.isRemote) {
//                            if (!((BlockEntityMultiMachine) tile).isStructureValid()) {
//                                ((BlockEntityMultiMachine) tile).checkStructure();
//                            }
//                        }
//                        ((BlockEntityMultiMachine) tile).checkRecipe();
//                    } else if (tile instanceof BlockEntityHatch) {
////                        MachineFluidHandler handler = ((BlockEntityHatch) tile).getFluidHandler();
////                        if (handler != null) {
////                            System.out.println(handler.toString());
////                        }
//                    } /*else if (tile instanceof TileEntityItemFluidMachine) {
//                        MachineFluidHandler fluidHandler = ((TileEntityItemFluidMachine) tile).getFluidHandler();
//                        for (FluidStack fluid : fluidHandler.getInputs()) {
//                            System.out.println(fluid.getLocalizedName() + " - " + fluid.amount);
//                        }
//                        tile.markDirty();
//                    }*/
//                } else if (tile instanceof BlockEntityPipe) {
//                    player.sendMessage(new StringTextComponent("C: " + ((BlockEntityPipe) tile).getConnections() + (((BlockEntityPipe) tile).getConnections() > 63 ? " (Culled)" : " (Non Culled)")));
//                } else if (tile instanceof TileEntityMaterial) {
//                    if (!world.isRemote) {
//                        TileEntityMaterial ore = (TileEntityMaterial) tile;
//                        player.sendMessage(new StringTextComponent(ore.getMaterial().getId()));
//                    }
//                }
//            }
//        } else {
//            if (Data.DebugScanner.isEqual(stack)) {
//                BlockState state = world.getBlockState(pos);
//                if (state.getBlock() instanceof BlockTurbineCasing) {
//                    BlockState casingState = state.getBlock().getExtendedState(state, world, pos);
//                    if (casingState instanceof IExtendedBlockState) {
//                        IExtendedBlockState exState = (IExtendedBlockState) casingState;
//                        try {
//                            int[] ct = exState.getValue(BlockTurbineCasing.CONFIG);
//                            player.sendMessage(new StringTextComponent("ct: " + Arrays.toString(ct)));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else if (state.getBlock() instanceof BlockDynamic) {
//                    BlockState ctState = state.getBlock().getExtendedState(state, world, pos);
//                    if (ctState instanceof IExtendedBlockState) {
//                        IExtendedBlockState exState = (IExtendedBlockState) ctState;
//                        try {
//                            int[] ct = exState.getValue(BlockDynamic.CONFIG);
//                            player.sendMessage(new StringTextComponent("ct: " + Arrays.toString(ct)));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                //if (!world.isRemote) {
//                    //Data.RUBBER_SAPLING.generateTree(world, pos, Ref.RNG);
//                    //RecipeMap.dumpHashCollisions();
//                //}
//            }
//        }
//        return EnumActionResult.FAIL; //TODO FAIL?
//    }

//    public ItemType required(String... mods) {
//        for (int i = 0; i < mods.length; i++) {
//            if (!Utils.isModLoaded(mods[i])) {
//                enabled = false;
//                break;
//            }
//        }
//        return this;
//    }
//
//    public ItemType optional(String... mods) {
//        enabled = false;
//        for (int i = 0; i < mods.length; i++) {
//            if (Utils.isModLoaded(mods[i])) {
//                enabled = true;
//                break;
//            }
//        }
//        return this;
//    }
}
