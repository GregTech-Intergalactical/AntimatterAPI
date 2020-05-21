package muramasa.antimatter.tool.behaviour;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.DrawHighlightEvent;
import tesseract.api.IConnectable;
import tesseract.graph.Connectivity;


public class BehaviourConnection implements IItemHighlight<IAntimatterTool> {

    final float INDENTATION_SIDE = 0.25F;
    final double INTERACT_DISTANCE = 5;

    private IBlockChecker verifier;

    public interface IBlockChecker {
        boolean canDraw(TileEntity tile);
    }

    public BehaviourConnection(IBlockChecker checker) {
        verifier = checker;
    }

    public ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        //Get block player is looking at
        Vec3d lookPos = player.getEyePosition(ev.getPartialTicks()), rotation = player.getLook(ev.getPartialTicks()), realLookPos = lookPos.add(rotation.x * INTERACT_DISTANCE, rotation.y * INTERACT_DISTANCE, rotation.z * INTERACT_DISTANCE);
        BlockRayTraceResult result =
                player.getEntityWorld().rayTraceBlocks(new RayTraceContext(lookPos, realLookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        BlockPos pos = result.getPos();
        Direction dir = result.getFace();
        TileEntity tile = player.getEntityWorld().getTileEntity(pos);

        //This does NULL-Check.
        if (tile == null || !verifier.canDraw(tile)) {
            return ActionResultType.PASS;
        }
        //Build up view & matrix.
        Vec3d viewPosition = ev.getInfo().getProjectedView();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        IVertexBuilder builderLines = ev.getBuffers().getBuffer(RenderType.LINES);

        MatrixStack matrix = ev.getMatrix();
        double modX = pos.getX() - viewX, modY = pos.getY() - viewY, modZ = pos.getZ() - viewZ;
        matrix.push();

       // VoxelShape shape = player.getEntityWorld().getBlockState(pos).getShape(player.getEntityWorld(), pos, ISelectionContext.forEntity(player));
        float X = 1;//(float) shape.getEnd(Direction.Axis.X);
        float Y = 1;//(float) shape.getEnd(Direction.Axis.Y);
      //  float Z = 1//(float) shape.getEnd(Direction.Axis.Z);
        //TODO: Better way to do this. dont know if forge has this built in? blit on certain face
        //Rotate & translate to the correct face.
        switch (dir) {
            case UP:
                matrix.translate(modX, modY + 1, modZ + 1);
                matrix.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                break;
            case DOWN:
                matrix.translate(modX, modY, modZ + 1);
                matrix.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                break;
            case EAST:
                matrix.translate(modX + 1, modY, modZ);
                matrix.rotate(new Quaternion(new Vector3f(0, 1, 0), -90, true));
                break;
            case WEST:
                matrix.translate(modX, modY, modZ);
                matrix.rotate(new Quaternion(new Vector3f(0, 1, 0), -90, true));
                break;
            case SOUTH:
                matrix.translate(modX, modY, modZ + 1);
                break;
            case NORTH:
                matrix.translate(modX, modY, modZ);
                break;
        }

        //Draw 4 lines.
        Matrix4f matrix4f = matrix.getLast().getMatrix();

        //TODO: Use SHAPE to get actual size of box.

        builderLines.pos(matrix4f, (float) (INDENTATION_SIDE), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (INDENTATION_SIDE), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0 + INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (0 + INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (X - INDENTATION_SIDE), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X - INDENTATION_SIDE), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (Y - INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (Y - INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (0), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (X), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (X), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (0), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        //Draw outline.
        //TODO: Use Capability?
        //TODO: i dont know how to render a square but most likely the sides should be a translucent square
        if (tile instanceof IConnectable) {
            byte sides = Connectivity.of((IConnectable) tile);
            boolean left, right, up, down, back;
            back = Connectivity.has(sides, dir.getOpposite().getIndex());
            if (dir.getAxis().isVertical()) {
                if (dir == Direction.UP) {
                    right = Connectivity.has(sides, 4);
                    left = Connectivity.has(sides, 5);
                    up = Connectivity.has(sides, 2);
                    down = Connectivity.has(sides, 3);
                } else {
                    right = Connectivity.has(sides, 5);
                    left = Connectivity.has(sides, 4);
                    up = Connectivity.has(sides, 3);
                    down = Connectivity.has(sides, 2);
                }
            } else {
                if (dir == Direction.EAST || dir == Direction.NORTH) {
                    right = Connectivity.has(sides, dir.rotateYCCW().getIndex());
                    left = Connectivity.has(sides, dir.rotateY().getIndex());
                } else {
                    right = Connectivity.has(sides, dir.rotateY().getIndex());
                    left = Connectivity.has(sides, dir.rotateYCCW().getIndex());
                }
                up = Connectivity.has(sides, 1);
                down = Connectivity.has(sides, 0);
            }
            if (back) {
                drawX(builderLines, matrix4f, 0, 0, INDENTATION_SIDE, INDENTATION_SIDE);
                drawX(builderLines, matrix4f, X, 0, X - INDENTATION_SIDE, INDENTATION_SIDE);
                drawX(builderLines, matrix4f, X, Y, X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
                drawX(builderLines, matrix4f, 0, Y, INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
            if (left) {
                drawX(builderLines, matrix4f, X, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
            if (right) {
                drawX(builderLines, matrix4f, 0, INDENTATION_SIDE, INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
            if (up) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, Y - INDENTATION_SIDE, X - INDENTATION_SIDE, Y);
            }
            if (down) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, 0, X - INDENTATION_SIDE, INDENTATION_SIDE);
            }
        }
        matrix.pop();
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
