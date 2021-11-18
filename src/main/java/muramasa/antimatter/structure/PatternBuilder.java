package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class PatternBuilder {
    
    private List<String[]> slices = new ObjectArrayList<>();
    private Object2ObjectMap<String, BlockInfo> elementLookup = new Object2ObjectOpenHashMap<>();
    private ITextComponent description = new TranslationTextComponent("");

    public PatternBuilder of(String... slices) {
        this.slices.add(slices);
        return this;
    }

    public PatternBuilder of(int i) {
        slices.add(slices.get(i));
        return this;
    }

    public PatternBuilder of(int i, String... slices) {
        this.slices.set(i, slices);
        return this;
    }

    public PatternBuilder at(String key, BlockInfo value) {
        this.elementLookup.put(key, value);
        return this;
    }

    public PatternBuilder at(String key, BlockState blockState) {
        return at(key, new BlockInfo(blockState));
    }

    public PatternBuilder at(String key, Machine<?> machine, Tier tier, Direction frontSide) {
        BlockMachine block = machine.getBlockState(tier);
        BlockState state;
        if (block.getType().allowVerticalFacing()) {
            state = block.defaultBlockState().setValue(BlockMachine.HORIZONTAL_FACING, Direction.EAST).setValue(BlockStateProperties.FACING, frontSide.getAxis() == Direction.Axis.Y ? frontSide : frontSide.getOpposite());
        } else {
            state = block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, frontSide);
        }
        TileEntity te = block.createTileEntity(state, null);
        return at(key, new BlockInfo(state, te));
    }

    public PatternBuilder shallowCopy() {
        PatternBuilder builder = new PatternBuilder();
        builder.slices = new ObjectArrayList<>(this.slices);
        builder.elementLookup = new Object2ObjectOpenHashMap<>(this.elementLookup);
        builder.description = description;
        return builder;
    }

    public PatternBuilder description(String description) {
        return this.description(new TranslationTextComponent(description));
    }

    public PatternBuilder description(ITextComponent description) {
        this.description = description;
        return this;
    }

    public Pattern build() {
        return new Pattern(bakeArray(), description);
    }

    private BlockInfo[][][] bakeArray() {
        BlockInfo[][][] blockInfos = new BlockInfo[slices.size()][][];
        for (int i = 0; i < blockInfos.length; i++) {
            String[] aisleEntry = slices.get(i);
            BlockInfo[][] aisleData = new BlockInfo[aisleEntry.length][];
            for (int j = 0; j < aisleData.length; j++) {
                String columnEntry = aisleEntry[j];
                BlockInfo[] columnData = new BlockInfo[columnEntry.length()];
                for (int k = 0; k < columnData.length; k++) {
                    columnData[k] = elementLookup.getOrDefault(columnEntry.charAt(k)+"", BlockInfo.EMPTY);
                }
                aisleData[j] = columnData;
            }
            blockInfos[i] = aisleData;
        }
        return blockInfos;
    }
}
