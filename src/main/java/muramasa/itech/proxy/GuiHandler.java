package muramasa.itech.proxy;

    import muramasa.itech.api.enums.MachineFlag;
    import muramasa.itech.api.gui.GuiBasicMachine;
    import muramasa.itech.api.gui.GuiHatch;
    import muramasa.itech.api.gui.GuiMultiMachine;
    import muramasa.itech.api.gui.container.ContainerBasicMachine;
    import muramasa.itech.api.gui.container.ContainerHatch;
    import muramasa.itech.api.gui.container.ContainerMachine;
    import muramasa.itech.api.gui.container.ContainerMultiMachine;
    import muramasa.itech.api.machines.Machine;
    import muramasa.itech.common.tileentities.base.TileEntityMachine;
    import muramasa.itech.common.tileentities.overrides.TileEntityBasicMachine;
    import muramasa.itech.common.utils.Ref;
    import net.minecraft.entity.player.EntityPlayer;
    import net.minecraft.tileentity.TileEntity;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.fml.common.network.IGuiHandler;

    import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityMachine) {
            Machine machine = ((TileEntityMachine) tile).getMachineType();
            if (!machine.hasFlag(MachineFlag.GUI)) return null;
            if (machine.getGuiId() == Ref.MACHINE_ID) {
                return new ContainerBasicMachine((TileEntityBasicMachine) tile, player.inventory);
            } else if (machine.getGuiId() == Ref.MULTI_MACHINE_ID) {
                return new ContainerMultiMachine((TileEntityMachine) tile, player.inventory);
            } else if (machine.getGuiId() == Ref.HATCH_ID) {
                return new ContainerHatch((TileEntityMachine) tile, player.inventory);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityMachine) {
            Machine machine = ((TileEntityMachine) tile).getMachineType();
            if (!machine.hasFlag(MachineFlag.GUI)) return null;
            if (machine.getGuiId() == Ref.MACHINE_ID) {
                return new GuiBasicMachine((TileEntityMachine) tile, new ContainerBasicMachine((TileEntityMachine) tile, player.inventory));
            } else if (machine.getGuiId() == Ref.MULTI_MACHINE_ID) {
                return new GuiMultiMachine((TileEntityMachine) tile, new ContainerMultiMachine((TileEntityMachine) tile, player.inventory));
            } else if (machine.getGuiId() == Ref.HATCH_ID) {
                return new GuiHatch((TileEntityMachine) tile, new ContainerHatch((TileEntityMachine) tile, player.inventory));
            }
        }
        return null;
    }
}
