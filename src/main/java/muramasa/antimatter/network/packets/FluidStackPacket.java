package muramasa.antimatter.network.packets;

import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public class FluidStackPacket {

    private Fluid[] inputFluids, outputFluids;
    private int[] inputAmounts, outputAmounts;
    private BlockPos pos;

    private FluidStackPacket() {
    }

    public FluidStackPacket(BlockPos pos, @Nullable FluidStack[] inputs, @Nullable FluidStack[] outputs) {
        this.pos = pos;
        if (inputs != null) {
            inputFluids = new Fluid[inputs.length];
            inputAmounts = new int[inputs.length];
            for (int i = 0; i < inputs.length; i++) {
                inputFluids[i] = inputs[i].getFluid();
                inputAmounts[i] = inputs[i].getAmount();
            }
        }
        if (outputs != null) {
            outputFluids = new Fluid[outputs.length];
            outputAmounts = new int[outputs.length];
            for (int i = 0; i < outputs.length; i++) {
                outputFluids[i] = outputs[i].getFluid();
                outputAmounts[i] = outputs[i].getAmount();
            }
        }
    }

    public static void encode(FluidStackPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.inputFluids != null ? msg.inputFluids.length : 0);
        buf.writeInt(msg.outputFluids != null ? msg.outputFluids.length : 0);
        if (msg.inputFluids != null) {
            for (int i = 0; i < msg.inputFluids.length; i++) {
                buf.writeRegistryId(msg.inputFluids[i]);
                buf.writeInt(msg.inputAmounts[i]);
            }
        }
        if (msg.outputFluids != null) {
            for (int i = 0; i < msg.outputFluids.length; i++) {
                buf.writeRegistryId(msg.outputFluids[i]);
                buf.writeInt(msg.outputAmounts[i]);
            }
        }
    }

    public static FluidStackPacket decode(PacketBuffer buf) {
        FluidStackPacket packet = new FluidStackPacket();
        packet.pos = buf.readBlockPos();
        int inputCount = buf.readInt();
        int outputCount = buf.readInt();
        if (inputCount > 0) {
            packet.inputFluids = new Fluid[inputCount];
            packet.inputAmounts = new int[inputCount];
            for (int i = 0; i < inputCount; i++) {
                packet.inputFluids[i] = buf.readRegistryId();
                packet.inputAmounts[i] = buf.readInt();
            }
        }
        if (outputCount > 0) {
            packet.outputFluids = new Fluid[outputCount];
            packet.outputAmounts = new int[outputCount];
            for (int i = 0; i < outputCount; i++) {
                packet.outputFluids[i] = buf.readRegistryId();
                packet.outputAmounts[i] = buf.readInt();
            }
        }
        return packet;
    }

    public static void handle(final FluidStackPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(msg::executePacket);
        ctx.get().setPacketHandled(true);
    }

    public void executePacket() {
        if (inputFluids == null && outputFluids == null) return;
        TileEntity tile = Utils.getTile(Minecraft.getInstance().world, this.pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine) tile;
            machine.fluidHandler.ifPresent(h -> {
                if (this.inputFluids != null) {
                    FluidStack[] inputs = new FluidStack[this.inputFluids.length];
                    for (int i = 0; i < inputs.length; i++) {
                        inputs[i] = new FluidStack(this.inputFluids[i], this.inputAmounts[i]);
                    }
                    FluidTanks tanks = h.getInputTanks();
                    for (int i = 0; i < Math.min(inputs.length, tanks.getTanks()); i++) {
                        tanks.getTank(i).setFluid(inputs[i]);
                    }
                }
                if (this.outputFluids != null) {
                    FluidStack[] outputs = new FluidStack[this.outputFluids.length];
                    for (int i = 0; i < outputs.length; i++) {
                        outputs[i] = new FluidStack(this.outputFluids[i], this.outputAmounts[i]);
                    }
                    FluidTanks tanks = h.getOutputTanks();
                    for (int i = 0; i < Math.min(outputs.length, tanks.getTanks()); i++) {
                        tanks.getTank(i).setFluid(outputs[i]);
                    }
                }
            });
        }
    }
}