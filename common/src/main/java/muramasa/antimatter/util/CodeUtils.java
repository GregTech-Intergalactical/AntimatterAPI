package muramasa.antimatter.util;

public class CodeUtils {
    public static long divup(long number, long divider) {
        return number / divider + (number % divider == 0 ? 0 : 1);
    }

    public static float  bindF    (float  aBoundValue) {return        Math.max(0, Math.min(         1, aBoundValue));}
    public static double bindD    (double aBoundValue) {return        Math.max(0, Math.min(         1, aBoundValue));}
    public static byte   bind1    (long   aBoundValue) {return (byte) Math.max(0, Math.min(         1, aBoundValue));}
    public static byte   bind2    (long   aBoundValue) {return (byte) Math.max(0, Math.min(         3, aBoundValue));}
    public static byte   bind3    (long   aBoundValue) {return (byte) Math.max(0, Math.min(         7, aBoundValue));}
    public static byte   bind4    (long   aBoundValue) {return (byte) Math.max(0, Math.min(        15, aBoundValue));}
    public static byte   bind5    (long   aBoundValue) {return (byte) Math.max(0, Math.min(        31, aBoundValue));}
    public static byte   bind6    (long   aBoundValue) {return (byte) Math.max(0, Math.min(        63, aBoundValue));}
    public static byte   bind7    (long   aBoundValue) {return (byte) Math.max(0, Math.min(       127, aBoundValue));}
    public static short  bind8    (long   aBoundValue) {return (short)Math.max(0, Math.min(       255, aBoundValue));}
    public static short  bind15   (long   aBoundValue) {return (short)Math.max(0, Math.min(     32767, aBoundValue));}
    public static int    bind16   (long   aBoundValue) {return (int)  Math.max(0, Math.min(     65535, aBoundValue));}
    public static int    bind24   (long   aBoundValue) {return (int)  Math.max(0, Math.min(  16777215, aBoundValue));}
    public static int    bind31   (long   aBoundValue) {return (int)  Math.max(0, Math.min(2147483647, aBoundValue));}
    public static int    bindInt  (long   aBoundValue) {return (int)  Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, aBoundValue));}
    public static short  bindShort(long   aBoundValue) {return (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, aBoundValue));}
    public static byte   bindByte (long   aBoundValue) {return (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, aBoundValue));}
    public static byte   bindStack(long   aBoundValue) {return (byte) Math.max(1, Math.min(64, aBoundValue));}

    public static int getRGB(int r, int g, int b){
        return (bind8(r) << 16) | (bind8(g) << 8) | bind8(b);
    }

    public static short getR(int rgb) {return (short)((rgb >>> 16) & 255);}
    public static short getG(int rgb) {return (short)((rgb >>>  8) & 255);}
    public static short getB(int rgb) {return (short) (rgb         & 255);}
}
