package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockCoil;
import muramasa.antimatter.block.BlockDynamic;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DebugScannerItem extends ItemBasic {

    public DebugScannerItem(String domain, String id, String tooltip) {
        super(domain, id, tooltip);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent(this.tooltip));
        if (Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent("Blocks: " + AntimatterAPI.all(Block.class).size()));
            tooltip.add(new StringTextComponent("Machines: " + Machine.getTypes(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH).size()));
            tooltip.add(new StringTextComponent("Pipes: " + /*GregTechAPI.all(BlockPipe.class).size()*/ "TODO"));
            tooltip.add(new StringTextComponent("Casings: " + /*GregTechAPI.all(BlockCasing.class).size()*/ "TODO"));
            tooltip.add(new StringTextComponent("Coils: " + AntimatterAPI.all(BlockCoil.class).size()));
            tooltip.add(new StringTextComponent("Storage: " + AntimatterAPI.all(BlockStorage.class).size()));
            tooltip.add(new StringTextComponent("Ores: " + AntimatterAPI.all(BlockOre.class).size()));
            tooltip.add(new StringTextComponent("Stones: " + AntimatterAPI.all(BlockStone.class).size()));
            tooltip.add(new StringTextComponent("Data:"));
            tooltip.add(new StringTextComponent("Ore Materials: " + MaterialType.ORE.all().size()));
            tooltip.add(new StringTextComponent("Small Ore Materials: " + MaterialType.ORE_SMALL.all().size()));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getWorld().isRemote) return super.onItemUse(context);
        BlockState state = context.getWorld().getBlockState(context.getPos());
        if (state.getBlock() instanceof BlockDynamic && context.getPlayer() != null) {
            ((BlockDynamic) state.getBlock()).getInfo(new ArrayList<>(), context.getWorld(), state, context.getPos()).forEach(s -> {
                context.getPlayer().sendMessage(new StringTextComponent(s));
            });
            return ActionResultType.SUCCESS;
        } else {

        }
        return super.onItemUse(context);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return super.onItemUseFirst(stack, context);
    }

    //    @Override
//    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        ItemStack stack = player.getHeldItem(hand);
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile != null) {
//            if (Data.DebugScanner.isEqual(stack)) {
//                if (tile instanceof TileEntityMachine) {
//                    if (tile instanceof TileEntityMultiMachine) {
//                        if (!world.isRemote) {
//                            if (!((TileEntityMultiMachine) tile).isStructureValid()) {
//                                ((TileEntityMultiMachine) tile).checkStructure();
//                            }
//                        }
//                        ((TileEntityMultiMachine) tile).checkRecipe();
//                    } else if (tile instanceof TileEntityHatch) {
////                        MachineFluidHandler handler = ((TileEntityHatch) tile).getFluidHandler();
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
//                } else if (tile instanceof TileEntityPipe) {
//                    player.sendMessage(new StringTextComponent("C: " + ((TileEntityPipe) tile).getConnections() + (((TileEntityPipe) tile).getConnections() > 63 ? " (Culled)" : " (Non Culled)")));
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
