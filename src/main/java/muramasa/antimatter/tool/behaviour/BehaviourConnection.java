package muramasa.antimatter.tool.behaviour;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.client.event.DrawHighlightEvent;

import java.util.function.Function;


public class BehaviourConnection extends BehaviourExtendedHighlight {

    public BehaviourConnection(Function<Block, Boolean> validator) {
        super(validator);
    }

    public ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        if (super.onDrawHighlight(player, ev) == ActionResultType.SUCCESS) return ActionResultType.SUCCESS;



//        //Draw outline.
//        //TODO: Use Capability?
//        //TODO: i dont know how to render a square but most likely the sides should be a translucent square
//        if (tile instanceof IConnectable) {
//            byte sides = Connectivity.of((IConnectable) tile);
//            boolean left, right, up, down, back;
//            back = Connectivity.has(sides, dir.getOpposite().getIndex());
//            if (dir.getAxis().isVertical()) {
//                if (dir == Direction.UP) {
//                    right = Connectivity.has(sides, 4);
//                    left = Connectivity.has(sides, 5);
//                    up = Connectivity.has(sides, 2);
//                    down = Connectivity.has(sides, 3);
//                } else {
//                    right = Connectivity.has(sides, 5);
//                    left = Connectivity.has(sides, 4);
//                    up = Connectivity.has(sides, 3);
//                    down = Connectivity.has(sides, 2);
//                }
//            } else {
//                if (dir == Direction.EAST || dir == Direction.NORTH) {
//                    right = Connectivity.has(sides, dir.rotateYCCW().getIndex());
//                    left = Connectivity.has(sides, dir.rotateY().getIndex());
//                } else {
//                    right = Connectivity.has(sides, dir.rotateY().getIndex());
//                    left = Connectivity.has(sides, dir.rotateYCCW().getIndex());
//                }
//                up = Connectivity.has(sides, 1);
//                down = Connectivity.has(sides, 0);
//            }
//            if (back) {
//                drawX(builderLines, matrix4f, 0, 0, INDENTATION_SIDE, INDENTATION_SIDE);
//                drawX(builderLines, matrix4f, X, 0, X - INDENTATION_SIDE, INDENTATION_SIDE);
//                drawX(builderLines, matrix4f, X, Y, X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
//                drawX(builderLines, matrix4f, 0, Y, INDENTATION_SIDE, Y - INDENTATION_SIDE);
//            }
//            if (left) {
//                drawX(builderLines, matrix4f, X, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
//            }
//            if (right) {
//                drawX(builderLines, matrix4f, 0, INDENTATION_SIDE, INDENTATION_SIDE, Y - INDENTATION_SIDE);
//            }
//            if (up) {
//                drawX(builderLines, matrix4f, INDENTATION_SIDE, Y - INDENTATION_SIDE, X - INDENTATION_SIDE, Y);
//            }
//            if (down) {
//                drawX(builderLines, matrix4f, INDENTATION_SIDE, 0, X - INDENTATION_SIDE, INDENTATION_SIDE);
//            }
//        }
//        matrix.pop();
        return ActionResultType.SUCCESS;
    }

    private void drawX(IVertexBuilder builder, Matrix4f matrix, float x1, float y1, float x2, float y2) {
        builder.pos(matrix, x1, y1, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builder.pos(matrix, x2, y2, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builder.pos(matrix, x2, y1, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builder.pos(matrix, x1, y2, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
    }

    @Override
    public String getId() {
        return "connection";
    }
}
