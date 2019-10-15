//package muramasa.gtu.api.tree;
//
//import muramasa.gtu.Ref;
//import net.minecraft.block.properties.PropertyDirection;
//import net.minecraft.block.properties.PropertyEnum;
//import net.minecraft.block.state.BlockStateContainer;
//import net.minecraft.util.Direction;
//import net.minecraft.util.IStringSerializable;
//import net.minecraft.util.ResourceLocation;
//
//import java.util.Arrays;
//import java.util.Locale;
//
//public class BlockRubberLog extends BlockLogBase {
//
//    public static PropertyEnum<ResinState> RESIN_STATE = PropertyEnum.create("resin_state", ResinState.class);
//    public static PropertyDirection RESIN_FACING = PropertyDirection.create("resin_facing", Arrays.asList(Direction.HORIZONTALS));
//
//    public BlockRubberLog() {
//        super(new ResourceLocation(Ref.MODID, "rubber_log"));
//        setDefaultState(getDefaultState().withProperty(RESIN_STATE, ResinState.NONE).withProperty(RESIN_FACING, Direction.NORTH));
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer.Builder(this).add(LOG_AXIS, RESIN_STATE, RESIN_FACING).build();
//    }
//
//    public enum ResinState implements IStringSerializable {
//        NONE,
//        EMPTY,
//        FILLED;
//
//        @Override
//        public String getName() {
//            return name().toLowerCase(Locale.ENGLISH);
//        }
//    }
//}
