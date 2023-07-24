package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tesseract.api.gt.IEnergyHandler;

import java.util.List;
import java.util.function.IntToLongFunction;

import static muramasa.antimatter.data.AntimatterDefaultTools.SOFT_HAMMER;

public class TileEntityTransformer<T extends TileEntityTransformer<T>> extends TileEntityMachine<T> {

    protected int voltage, amperage;
    protected IntToLongFunction capFunc;

    public TileEntityTransformer(Machine<?> type, BlockPos pos, BlockState state, int amps) {
        this(type, pos, state, amps, (v) -> (512L + v * 8L));
    }

    public TileEntityTransformer(Machine<?> type, BlockPos pos, BlockState state, int amps, IntToLongFunction capFunc) {
        super(type, pos, state);
        this.amperage = amps;
        this.capFunc = capFunc;
        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0L, capFunc.applyAsLong(getMachineTier().getVoltage()), getMachineTier().getVoltage() * 4, getMachineTier().getVoltage(), amperage, amperage * 4) {
            @Override
            public boolean canOutput(Direction direction) {
                return isDefaultMachineState() == (tile.getFacing().get3DDataValue() != direction.get3DDataValue());
            }

            @Override
            public boolean canInput(Direction direction) {
                return !canOutput(direction);
            }
        });
        // FIXME
        /*
        interactHandler.setup((tile, tag) -> new MachineInteractHandler<TileEntityMachine>(tile, tag) {
            @Override
            public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
                if (type == HAMMER && hand == Hand.MAIN_HAND) {
                    toggleMachine();
                    energyHandler.ifPresent(h -> {
                        int temp = h.getOutputAmperage();
                        h.setOutputAmperage(h.getInputAmperage());
                        h.setInputAmperage(temp);
                        temp = h.getOutputVoltage();
                        h.setOutputVoltage(h.getInputVoltage());
                        h.setInputVoltage(temp);
                        h.onReset();
                        player.sendMessage(new StringTextComponent((isDefaultMachineState() ? "Step Down, In: " : "Step Up, In") + h.getInputVoltage() + "V@" + h.getInputAmperage() + "Amp, Out: " + h.getOutputVoltage() + "V@" + h.getOutputAmperage() + "Amp"));
                    });
                    return true;
                }
                return super.onInteract(player, hand, side, type);
            }
        });
         */
    }

    @Override
    public InteractionResult onInteractServer(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, AntimatterToolType type) {
        if (type == SOFT_HAMMER && hand == InteractionHand.MAIN_HAND) {
            toggleMachine();
            energyHandler.ifPresent(h -> {
                long temp = h.getOutputAmperage();
                h.setOutputAmperage(h.getInputAmperage());
                h.setInputAmperage(temp);
                temp = h.getOutputVoltage();
                h.setOutputVoltage(h.getInputVoltage());
                h.setInputVoltage(temp);
                this.invalidateCap(IEnergyHandler.class);
                player.sendMessage(new TextComponent((isDefaultMachineState() ? "Step Down, In: " : "Step Up, In") + h.getInputVoltage() + "V@" + h.getInputAmperage() + "Amp, Out: " + h.getOutputVoltage() + "V@" + h.getOutputAmperage() + "Amp"), player.getUUID());
            });
            return InteractionResult.SUCCESS;
        }
        return super.onInteractServer(state, world, pos, player, hand, hit, type);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        this.voltage = getMachineTier().getVoltage();
    }

    @Override
    public MachineState getDefaultMachineState() {
        return MachineState.ACTIVE;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        energyHandler.ifPresent(h -> {
            info.add("Voltage In: " + h.getInputVoltage());
            info.add("Voltage Out: " + h.getOutputVoltage());
            info.add("Amperage In: " + h.getInputAmperage());
            info.add("Amperage Out: " + h.getOutputAmperage());
        });
        return info;
    }
}