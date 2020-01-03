//package muramasa.antimatter.capability.impl;
//
//import muramasa.antimatter.capability.IConfigHandler;
//import muramasa.antimatter.tileentities.pipe.TileEntityPipe;
//import muramasa.antimatter.tools.GregTechToolType;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.util.Hand;
//
//public class PipeConfigHandler implements IConfigHandler {
//
//    private TileEntityPipe tile;
//
//    public PipeConfigHandler(TileEntityPipe tile) {
//        this.tile = tile;
//    }
//
//    @Override
//    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, GregTechToolType type) {
//        if (type == null) return false;
//        switch (type) {
//            case WRENCH:
//                getTile().toggleConnection(side);
//                return true;
//            default: return false;
//        }
//    }
//
//    @Override
//    public TileEntityPipe getTile() {
//        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
//        return tile;
//    }
//}
