package muramasa.gtu.api.worldgen.objects;

// Local class to track which orevein seeds must be checked when doing chunkified worldgen
public class NearbySeeds {

    public int mX;
    public int mZ;

    NearbySeeds(int x, int z) {
        this.mX = x;
        this.mZ = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NearbySeeds)) return false;
        NearbySeeds that = (NearbySeeds) o;
        if (this.mX != that.mX) return false;
        return this.mZ == that.mZ;
    }

    @Override
    public int hashCode() {
        int result = this.mX;
        result = 31 * result + this.mZ;
        return result;
    }
}
