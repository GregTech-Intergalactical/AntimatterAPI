package muramasa.gtu.api.network;

import io.netty.buffer.ByteBuf;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class FluidStackMessage implements IMessage {

    private int[] inputIds, outputIds, inputAmounts, outputAmounts;

    public FluidStackMessage() {
        //NOOP
    }

    public FluidStackMessage(@Nullable FluidStack[] inputs, @Nullable FluidStack[] outputs) {
        FluidStack current;
        if (inputs != null) {
            inputIds = new int[inputs.length];
            inputAmounts = new int[inputs.length];
            for (int i = 0; i < inputs.length; i++) {
                current = inputs[i];
                inputIds[i] = current != null ? Utils.getIdByFluid(current.getFluid()) : -1;
                inputAmounts[i] = current != null ? current.amount : -1;
            }
        }
        if (outputs != null) {
            outputIds = new int[outputs.length];
            outputAmounts = new int[outputs.length];
            for (int i = 0; i < outputs.length; i++) {
                current = outputs[i];
                outputIds[i] = current != null ? Utils.getIdByFluid(current.getFluid()) : -1;
                outputAmounts[i] = current != null ? current.amount : -1;
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(inputIds != null ? inputIds.length : 0);
        buf.writeInt(outputIds != null ? outputIds.length : 0);
        if (inputIds != null) {
            for (int i = 0; i < inputIds.length; i++) {
                buf.writeInt(inputIds[i]);
                buf.writeInt(inputAmounts[i]);
            }
        }
        if (outputIds != null) {
            for (int i = 0; i < outputIds.length; i++) {
                buf.writeInt(outputIds[i]);
                buf.writeInt(outputAmounts[i]);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int inputCount = buf.readInt();
        int outputCount = buf.readInt();
        if (inputCount > 0) {
            inputIds = new int[inputCount];
            inputAmounts = new int[inputCount];
            for (int i = 0; i < inputCount; i++) {
                inputIds[i] = buf.readInt();
                inputAmounts[i] = buf.readInt();
            }
        }
        if (outputCount > 0) {
            outputIds = new int[outputCount];
            outputAmounts = new int[outputCount];
            for (int i = 0; i < outputCount; i++) {
                outputIds[i] = buf.readInt();
                outputAmounts[i] = buf.readInt();
            }
        }
    }

    public static class FluidStackMessageHandler implements IMessageHandler<FluidStackMessage, IMessage> {

        @Override
        public IMessage onMessage(FluidStackMessage message, MessageContext ctx) {
            BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
            if (pos == null) return null; //Even tho IDEA says this is never null, it has caused NPEs
            if (!Minecraft.getMinecraft().world.isBlockLoaded(pos)) return null;
            TileEntity tile = Utils.getTile(Minecraft.getMinecraft().world, pos);
            if (tile instanceof TileEntityMachine) {
                TileEntityMachine machine = (TileEntityMachine) tile;
                if (machine.getFluidHandler() == null) return null;
                if (message.inputIds != null) {
                    FluidStack[] inputs = new FluidStack[message.inputIds.length];
                    Fluid currentFluid;
                    for (int i = 0; i < inputs.length; i++) {
                        if (message.inputIds[i] >= 0) {
                            currentFluid = Utils.getFluidById(message.inputIds[i]);
                            if (currentFluid == null) continue;
                            inputs[i] = new FluidStack(currentFluid, message.inputAmounts[i]);
                        } else {
                            inputs[i] = null;
                        }
                    }
                    machine.getFluidHandler().setInputs(inputs);
                }
                if (message.outputIds != null) {
                    FluidStack[] outputs = new FluidStack[message.outputIds.length];
                    Fluid currentFluid;
                    for (int i = 0; i < outputs.length; i++) {
                        if (message.outputIds[i] >= 0) {
                            currentFluid = Utils.getFluidById(message.outputIds[i]);
                            if (currentFluid == null) continue;
                            outputs[i] = new FluidStack(currentFluid, message.outputAmounts[i]);
                        } else {
                            outputs[i] = null;
                        }
                    }
                    machine.getFluidHandler().setOutputs(outputs);
                }
            }
            return null;
        }
    }
}
