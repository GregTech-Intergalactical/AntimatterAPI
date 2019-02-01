//package muramasa.itech.api.capability.implementations;
//
//import muramasa.itech.api.enums.HatchTexture;
//import muramasa.itech.common.blocks.BlockHatches;
//import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
//import net.minecraft.util.math.BlockPos;
//
//public class ComponentHatch extends Component {
//
//    public ComponentHatch(String id, BlockPos pos) {
//        super(id, pos);
//    }
//
//    @Override
//    public void linkController(TileEntityMultiMachine tile) {
//        super.linkController(tile);
//        tile.setState(tile.getState().withProperty(BlockHatches.TEXTURE, HatchTexture.get(tile.getType())));
//        tile.markDirty();
//    }
//
//    @Override
//    public void unlinkController(TileEntityMultiMachine tile) {
//        super.unlinkController(tile);
//        tile.setState(tile.getState().withProperty(BlockHatches.TEXTURE, HatchTexture.get(tile.getTier)));
//    }
//}
