package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.util.Utils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;

public class MaterialTypeBlock<T> extends MaterialType<T> {

    public interface BlockSupplier {
        void createBlocks(String domain, MaterialType<?> type, Material material);
    }

    private final BlockSupplier supplier;

    public MaterialTypeBlock(String id, int layers, boolean visible, int unitValue, BlockSupplier supplier) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeBlock.class, this);
        this.supplier = supplier;
    }

    public static Container getEmptyBlockAndLog(MaterialType<?> type, IAntimatterObject... objects) {
        Utils.onInvalidData("Tried to create " + type.getId() + " for objects: " + Arrays.toString(Arrays.stream(objects).map(IAntimatterObject::getId).toArray(String[]::new)));
        return new Container(Blocks.AIR.defaultBlockState());
    }

    public RecipeIngredient getMaterialIngredient(Material m, int count) {
        return RecipeIngredient.of(getMaterialTag(m), count);
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        super.onRegistryBuild(registry);
        if (doRegister()) {
            for (Material material : this.materials) {
                if (!material.enabled) continue;
                supplier.createBlocks(material.materialDomain(), this, material);
            }
        }
    }

    public interface IBlockGetter {
        Container get(Material m);
    }

    public interface IOreGetter {
        Container get(Material m, StoneType s);
    }

    public static class Container {

        protected BlockState state;

        public Container(BlockState state) {
            this.state = state;
        }

        public BlockState asState() {
            return state;
        }

        public Block asBlock() {
            return state.getBlock();
        }

        public Item asItem() {
            return asBlock().asItem();
        }

        public ItemStack asStack(int count) {
            return new ItemStack(asItem(), count);
        }

        public ItemStack asStack() {
            return asStack(1);
        }

        public RecipeIngredient asIngredient() {
            return RecipeIngredient.of(asStack(1));
        }
    }
}
