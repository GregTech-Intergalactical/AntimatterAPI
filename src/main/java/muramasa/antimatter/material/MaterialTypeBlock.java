package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.Arrays;

public class MaterialTypeBlock<T> extends MaterialType<T> {

    public MaterialTypeBlock(String id, int layers, boolean visible, int unitValue) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeBlock.class, id, this);
    }

    public static Container getEmptyBlockAndLog(MaterialType<?> type, IAntimatterObject... objects) {
        Utils.onInvalidData("Tried to create " + type.getId() + " for objects: " + Arrays.toString(Arrays.stream(objects).map(IAntimatterObject::getId).toArray(String[]::new)));
        return new Container(Blocks.AIR.getDefaultState());
    }

    public ITag.INamedTag<Item> getMaterialTag(Material m) {
        return TagUtils.getForgeItemTag(String.join("", Utils.getConventionalMaterialType(this), "/", m.getId()));
    }

    public RecipeIngredient getMaterialIngredient(Material m, int count) {
        return AntimatterIngredient.of(getMaterialTag(m),count);
    }

    public RecipeIngredient getMaterialIngredient(Material m) {
        return getMaterialIngredient(m,1);
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
             return AntimatterIngredient.of(asStack(1));
        }
    }
}
