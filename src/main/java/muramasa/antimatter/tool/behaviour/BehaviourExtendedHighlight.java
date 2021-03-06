package muramasa.antimatter.tool.behaviour;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.DrawHighlightEvent;

import java.util.function.Function;

public class BehaviourExtendedHighlight implements IItemHighlight<IAntimatterTool> {

    final float INDENTATION_SIDE = 0.25F;
    final double INTERACT_DISTANCE = 5;

    protected Function<Block, Boolean> validator;

    public BehaviourExtendedHighlight(Function<Block, Boolean> validator) {
        this.validator = validator;
    }

    @Override
    public String getId() {
        return "extended_highlight";
    }

    @Override
    public ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        Vector3d lookPos = player.getEyePosition(ev.getPartialTicks()), rotation = player.getLook(ev.getPartialTicks()), realLookPos = lookPos.add(rotation.x * INTERACT_DISTANCE, rotation.y * INTERACT_DISTANCE, rotation.z * INTERACT_DISTANCE);
        BlockRayTraceResult result = player.getEntityWorld().rayTraceBlocks(new RayTraceContext(lookPos, realLookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        BlockState state = player.getEntityWorld().getBlockState(result.getPos());
        if (!validator.apply(state.getBlock())) return ActionResultType.PASS;

        //Build up view & matrix.
        Vector3d viewPosition = ev.getInfo().getProjectedView();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        IVertexBuilder builderLines = ev.getBuffers().getBuffer(RenderType.LINES);

        MatrixStack matrix = ev.getMatrix();
        double modX = result.getPos().getX() - viewX, modY = result.getPos().getY() - viewY, modZ = result.getPos().getZ() - viewZ;
        matrix.push();

        // VoxelShape shape = player.getEntityWorld().getBlockState(pos).getShape(player.getEntityWorld(), pos, ISelectionContext.forEntity(player));
        float X = 1;//(float) shape.getEnd(Direction.Axis.X);
        float Y = 1;//(float) shape.getEnd(Direction.Axis.Y);
        //  float Z = 1//(float) shape.getEnd(Direction.Axis.Z);
        //TODO: Better way to do this. dont know if forge has this built in? blit on certain face
        //Rotate & translate to the correct face.
        switch (result.getFace()) {
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

        matrix.pop();
        return ActionResultType.SUCCESS;
    }
}
