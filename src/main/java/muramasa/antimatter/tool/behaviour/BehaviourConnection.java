package muramasa.antimatter.tool.behaviour;

import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.Data;
import muramasa.antimatter.behaviour.IItemTicker;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class BehaviourConnection implements IItemTicker<IAntimatterTool> {
    @Override
    public void onInventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!entityIn.world.isRemote) {
            return;
        }
        if (itemSlot > 9) {
            return;
        }
        if (entityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityIn;
            if (/*stack != player.getActiveItemStack() || */Utils.getToolType(player) != Data.WRENCH) {
                return;
            }
            Vec3d lookPos = player.getEyePosition(1), rotation = player.getLook(1), realLookPos = lookPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);

            BlockRayTraceResult result = entityIn.getEntityWorld().rayTraceBlocks(new RayTraceContext(lookPos, realLookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));

            TileEntity tile = entityIn.getEntityWorld().getTileEntity(result.getPos());

            if (!(tile instanceof TileEntityBase)) {
                return;
            }

            GL11.glPushMatrix();
            GL11.glTranslated(-player.lastTickPosX + (player.getPosX() - player.lastTickPosX), -(player.lastTickPosY + (player.getPosY() - player.lastTickPosY) ), -(player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ)));
            GL11.glTranslated((float) result.getPos().getX() + 0.5F, (float) result.getPos().getY( )+ 0.5F, (float) result.getPos().getY() + 0.5F);
            //Rotation.sideRotations[target.sideHit].glApply();
            GL11.glTranslated(0.0D, -0.501D, 0.0D);
            GL11.glLineWidth(2.0F);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
            GL11.glBegin(1);
            GL11.glVertex3d(+.50D, .0D, -.25D);
            GL11.glVertex3d(-.50D, .0D, -.25D);
            GL11.glVertex3d(+.50D, .0D, +.25D);
            GL11.glVertex3d(-.50D, .0D, +.25D);
            GL11.glVertex3d(+.25D, .0D, -.50D);
            GL11.glVertex3d(+.25D, .0D, +.50D);
            GL11.glVertex3d(-.25D, .0D, -.50D);
            GL11.glVertex3d(-.25D, .0D, +.50D);
            GL11.glPopMatrix();
            GL11.glEnd();
        }

    }

    @Override
    public String getId() {
        return null;
    }
}
