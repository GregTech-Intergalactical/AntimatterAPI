package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

public class PatternBuilder {
    
    private List<String[]> slices = new ObjectArrayList<>();
    private Object2ObjectMap<String, BlockInfo> elementLookup = new Object2ObjectOpenHashMap<>();
    private Component description = Utils.translatable("");
    private final Int2ObjectMap<List<Pattern.PonderTooltip>> ponderTooltipMap = new Int2ObjectOpenHashMap<>();

    float scale = 1.0f;

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
        if (block.getType().isVerticalFacingAllowed()) {
            state = block.defaultBlockState().setValue(BlockStateProperties.FACING, frontSide.getAxis() == Direction.Axis.Y ? frontSide : frontSide.getOpposite());
        } else {
            state = block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, frontSide);
        }
        return at(key, new BlockInfo(state));
    }
    public PatternBuilder scale(float scale){
        this.scale = scale;
        return this;
    }

    public PatternBuilder tip(int x, int y, int z, String tip){
        ponderTooltipMap.computeIfAbsent(y, i -> new ArrayList<>()).add(new Pattern.PonderTooltip(x, z, tip));
        return this;
    }

    public PatternBuilder shallowCopy() {
        PatternBuilder builder = new PatternBuilder();
        builder.slices = new ObjectArrayList<>(this.slices);
        builder.elementLookup = new Object2ObjectOpenHashMap<>(this.elementLookup);
        builder.description = description;
        return builder;
    }

    public PatternBuilder description(String description) {
        return this.description(Utils.translatable(description));
    }

    public PatternBuilder description(Component description) {
        this.description = description;
        return this;
    }

    public Pattern build() {
        return new Pattern(bakeArray(), description, scale, ponderTooltipMap);
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
