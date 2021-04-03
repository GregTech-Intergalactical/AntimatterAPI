package muramasa.antimatter.recipe.map;

public abstract class AbstractMapIngredient {
    private int hash = Integer.MIN_VALUE;
    protected final int id;

    public AbstractMapIngredient(int id) {
        this.id = id;
    }

    protected abstract int hash();

    @Override
    public int hashCode() {
        if (hash == Integer.MIN_VALUE) hash = hash();
        return hash;
    }
}
