package muramasa.antimatter.recipe.ingredient;

public abstract class AbstractMapIngredient {
    private int hash = Integer.MIN_VALUE;

    protected abstract int hash();

    @Override
    public int hashCode() {
        if (hash == Integer.MIN_VALUE) hash = hash();
        return hash;
    }

    public boolean isSpecial() {
        return false;
    }
}
