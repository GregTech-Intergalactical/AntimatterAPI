package muramasa.antimatter.util;

public class int2 {

    public int x;
    public int y;

    public int2() {

    }

    public int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof int2)) return false;
        int2 other = (int2) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
