package muramasa.antimatter.pipe;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PipeCache {

    //private static Int2ObjectMap<Long2ObjectMap<INodeHandler>> LOOKUP = new Int2ObjectOpenHashMap<>();

    //TODO: For testing purpose, covers should do that
    /*@SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (tile == null || tile instanceof TileEntityMachine) return;
        tile.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            LOOKUP.computeIfAbsent(e.getWorld().getDimension().getType().getId(), Long2ObjectOpenHashMap::new)
                  .computeIfAbsent(e.getPos().toLong(), n -> new EnergyNode(tile, handler));
        });
        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            LOOKUP.computeIfAbsent(e.getWorld().getDimension().getType().getId(), Long2ObjectOpenHashMap::new)
                  .computeIfAbsent(e.getPos().toLong(), n -> new FluidNode(tile, handler));
        });
        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            LOOKUP.computeIfAbsent(e.getWorld().getDimension().getType().getId(), Long2ObjectOpenHashMap::new)
                  .computeIfAbsent(e.getPos().toLong(), n -> new ItemNode(tile, handler));
        });
    }*/

    /*@SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        Long2ObjectMap<INodeHandler> data = LOOKUP.remove(e.getWorld().getDimension().getType().getId());
        if (data != null) {
            INodeHandler node = data.remove(e.getPos().toLong());
            if (node != null) {
                node.onRemove(null);
            }
        }
    }*/

    // TODO: Add nodes with covers
    /*private static Int2ObjectMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().getDimension().getType().getId());
    }
]
    public static void addElectric(World world, BlockPos pos, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.addElectric(pos, tile);
    }

    public static void addFluid(World world, BlockPos pos, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.addFluid(pos, tile);
    }

    public static void addItem(World world, BlockPos pos, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.addItem(pos, tile);
    }*/

    /*public static class DimensionEntry {

        private Long2ObjectMap<IRemovable[]> CAPABILITY_BLOCK = new Long2ObjectOpenHashMap<>();

        public DimensionEntry() {
        }

        public void addElectric(BlockPos pos, TileEntity tile) {
            LazyOptional<IEnergyStorage> capability = tile.getCapability(CapabilityEnergy.ENERGY);
            capability.ifPresent(handler -> {
                IRemovable[] data = CAPABILITY_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new IRemovable[3]);
                if (data[0] == null) {
                    data[0] = new EnergyNode(tile, handler);
                    capability.addListener(c -> data[0].remove());
                }
                //((EnergyNode)data[0]).()
            });
        }

        public void addFluid(BlockPos pos, TileEntity tile) {
            LazyOptional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            capability.ifPresent(handler -> {
                IRemovable[] data = CAPABILITY_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new IRemovable[3]);
                if (data[1] == null) {
                    data[1] = new FluidNode(tile, handler);
                    capability.addListener(c -> data[1].remove());
                }
                //((FluidNode)data[1]).()
            });
        }

        public void addItem(BlockPos pos, TileEntity tile) {
            LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            capability.ifPresent(handler -> {
                IRemovable[] data = CAPABILITY_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new IRemovable[3]);
                if (data[2] == null) {
                    data[2] = new ItemNode(tile, handler);
                    capability.addListener(c -> data[2].remove());
                }
                //((ItemNode)data[2]).()
            });
        }
    }*/
}
