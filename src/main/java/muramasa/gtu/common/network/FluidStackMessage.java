package muramasa.gtu.common.network;

import io.netty.buffer.ByteBuf;
import muramasa.gtu.Ref;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Arrays;

public class FluidStackMessage implements IMessage {

    private int[] ids, amounts;

    public FluidStackMessage(FluidStack[] inputs) {
        ids = new int[inputs.length];
        amounts = new int[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] != null) {
                ids[i] = Utils.getIdByFluid(inputs[i].getFluid());
                amounts[i] = inputs[i].amount;
            } else {
                ids[i] = -1;
                amounts[i] = -1;
            }
        }
    }

    public FluidStackMessage() {
        //NOOP
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int count = Math.min(ids.length, amounts.length);
        buf.writeInt(count);
        for (int i = 0; i < count; i++) {
            buf.writeInt(ids[i]);
            buf.writeInt(amounts[i]);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Reads the int back from the buf. Note that if you have multiple values, you must read in the same order you wrote.
        int count = buf.readInt();
        ids = new int[count];
        amounts = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = buf.readInt();
            amounts[i] = buf.readInt();
        }
    }

    public static class FluidStackMessageHandler implements IMessageHandler<FluidStackMessage, IMessage> {

        @Override
        public IMessage onMessage(FluidStackMessage message, MessageContext ctx) {
            System.out.println("ids: " + Arrays.toString(message.ids));
            System.out.println("amounts: " + Arrays.toString(message.amounts));

            BlockPos pos = Ref.MC.objectMouseOver.getBlockPos();
            if (pos == null) return null; //Even tho IDEA says this is never null, it has caused NPEs
            TileEntity tile = Utils.getTile(Ref.MC.world, pos);
            if (tile instanceof TileEntityMachine) {
                TileEntityMachine machine = (TileEntityMachine) tile;
                if (machine.getFluidHandler() == null) return null;

                FluidStack[] inputs = new FluidStack[message.ids.length];
                Fluid currentFluid;
                for (int i = 0; i < inputs.length; i++) {
                    if (message.ids[i] >= 0) {
                        currentFluid = Utils.getFluidById(message.ids[i]);
                        if (currentFluid == null) continue;
                        inputs[i] = new FluidStack(currentFluid, message.amounts[i]);
                    } else {
                        inputs[i] = null;
                    }
                }
                machine.getFluidHandler().setInputs(inputs);
            }
            return null;
        }
    }
}
