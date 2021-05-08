package muramasa.antimatter.recipe.ingredient;

public abstract class AbstractMapIngredient {
    private int hash = Integer.MIN_VALUE;
    private final Class<? extends AbstractMapIngredient> objClass;
    private final boolean insideMap;

    protected AbstractMapIngredient(boolean insideMap) {
        this.objClass = getClass();
        this.insideMap = insideMap;
    }

    protected abstract int hash();

    @Override
    public int hashCode() {
        if (hash == Integer.MIN_VALUE) hash = hash();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractMapIngredient) {
            AbstractMapIngredient ing = (AbstractMapIngredient) obj;
            if (ing.insideMap && this.insideMap) {
                return this.objClass == ing.objClass;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isSpecial() {
        return false;
    }
}
