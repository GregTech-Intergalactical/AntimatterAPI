package muramasa.gtu.common.events;

import muramasa.gtu.GregTech;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.structure.IComponent;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BlockHandler {

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock e) {
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (tile instanceof IComponent && !e.getEntityPlayer().isSneaking()) {
            TileEntityMultiMachine controller = ((IComponent) tile).getComponentHandler().getFirstController();
            if (controller == null) return;
            if (!controller.getType().hasFlag(MachineFlag.GUI)) return;
            GuiData gui = controller.getType().getGui();
            e.getEntityPlayer().openGui(GregTech.INSTANCE, gui.getGuiId(), e.getEntityPlayer().getEntityWorld(), controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ());
            e.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        //TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        //if (!(tile instanceof IComponent)) return;
        //((IComponent) tile).getComponentHandler().onComponentRemoved();
    }
}
