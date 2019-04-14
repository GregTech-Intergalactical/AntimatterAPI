package muramasa.gtu.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

//TODO evaluate usage of a IT wrapper, instead of constructing a unique string
//TODO unknown which is faster, or which uses more memory
public class GTItemStack {

    private ItemStack stack;
    private boolean checkDamage = false, checkSize = false, checkNBT = false;

    public GTItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GTItemStack)) return false;

        GTItemStack gtStack = (GTItemStack) obj;

        if (Item.getIdFromItem(stack.getItem()) != Item.getIdFromItem(gtStack.stack.getItem())) {
            return false;
        }

//        if(doCheckDamage && iStack.getItemDamage() != hashStack.iStack.getItemDamage()){
//            return false;
//        }
//
//        if(doCheckNBT && iStack.stackTagCompound != hashStack.iStack.stackTagCompound){
//            return false;
//        }
//
//        if(doCheckStackSize && iStack.stackSize != hashStack.iStack.stackSize){
//            return false;
//        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Item.getIdFromItem(stack.getItem());
//        result = prime * result + (doCheckDamage ? iStack.getItemDamage() : 0);
//        result = prime * result + (doCheckStackSize ? iStack.stackSize : 0);
//        result = prime * result + (doCheckNBT && iStack.hasTagCompound() ? iStack.stackTagCompound.hashCode() : 0);
        return result;
    }
}
