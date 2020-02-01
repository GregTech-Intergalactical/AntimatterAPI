package muramasa.antimatter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

import static java.lang.Math.abs;

public class IntegerAxisTransform {
    private final Vec3i forFirstAxis;
    private final Vec3i forSecondAxis;
    private final Vec3i forThirdAxis;

    public IntegerAxisTransform(@Nonnull Direction forFirstAxis, @Nonnull Direction forSecondAxis, @Nonnull Direction forThirdAxis) {
        this.forFirstAxis = forFirstAxis.getDirectionVec();
        this.forSecondAxis = forSecondAxis.getDirectionVec();
        this.forThirdAxis = forThirdAxis.getDirectionVec();
        if(abs(this.forFirstAxis.getX())+abs(this.forSecondAxis.getX())+abs(this.forThirdAxis.getX())!=1 ||
                abs(this.forFirstAxis.getY())+abs(this.forSecondAxis.getY())+abs(this.forThirdAxis.getY())!=1 ||
                abs(this.forFirstAxis.getZ())+abs(this.forSecondAxis.getZ())+abs(this.forThirdAxis.getZ())!=1){
            throw new IllegalArgumentException("Axis are overlapping/missing! "+
                    forFirstAxis.getName2()+" "+
                    forSecondAxis.getName2()+" "+
                    forThirdAxis.getName2());
        }
    }

    public Vec3i translate(@Nonnull Vec3i point){
        return new Vec3i(
                forFirstAxis.getX()*point.getX() +forFirstAxis.getY()*point.getY() +forFirstAxis.getZ()*point.getZ(),
                forSecondAxis.getX()*point.getX()+forSecondAxis.getY()*point.getY()+forSecondAxis.getZ()*point.getZ(),
                forThirdAxis.getX()*point.getX() +forThirdAxis.getY()*point.getY() +forThirdAxis.getZ()*point.getZ()
        );
    }

    public Vec3i inverseTranslate(@Nonnull Vec3i point){
        return new Vec3i(
                forFirstAxis.getX()*point.getX()+forSecondAxis.getX()*point.getY()+forThirdAxis.getX()*point.getZ(),
                forFirstAxis.getY()*point.getX()+forSecondAxis.getY()*point.getY()+forThirdAxis.getY()*point.getZ(),
                forFirstAxis.getZ()*point.getX()+forSecondAxis.getZ()*point.getY()+forThirdAxis.getZ()*point.getZ()
        );
    }

    public int3 translate(@Nonnull int3 point){
        return new int3(
                forFirstAxis.getX()*point.x +forFirstAxis.getY()*point.y +forFirstAxis.getZ()*point.z,
                forSecondAxis.getX()*point.x+forSecondAxis.getY()*point.y+forSecondAxis.getZ()*point.z,
                forThirdAxis.getX()*point.x +forThirdAxis.getY()*point.y +forThirdAxis.getZ()*point.z
        );
    }

    public int3 inverseTranslate(@Nonnull int3 point){
        return new int3(
                forFirstAxis.getX()*point.x+forSecondAxis.getX()*point.y+forThirdAxis.getX()*point.z,
                forFirstAxis.getY()*point.x+forSecondAxis.getY()*point.y+forThirdAxis.getY()*point.z,
                forFirstAxis.getZ()*point.x+forSecondAxis.getZ()*point.y+forThirdAxis.getZ()*point.z
        );
    }
}
