//package muramasa.gtu.api.capability.impl;
//
//import muramasa.gtu.api.capability.IConfigHandler;
//import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
//import muramasa.gtu.api.tools.GregTechToolType;
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
