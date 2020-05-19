function initializeCoreMod() {

    var Opcodes = Java.type("org.objectweb.asm.Opcodes");
    var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
    var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

    return {

        "ServerWorld#notifyBlockUpdate": {

            target: {

                type: "METHOD",
                class: "net.minecraft.world.server.ServerWorld",
                methodName: "func_184138_a",
                methodDesc: "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V"

            },

            transformer: function(node) {

                node.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "muramasa/antimatter/structure/StructureCache",
                    "onNotifyBlockUpdate",
                    "(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V"));
                node.instructions.insert(new IntInsnNode(Opcodes.ALOAD, 3));
                node.instructions.insert(new IntInsnNode(Opcodes.ALOAD, 2));
                node.instructions.insert(new IntInsnNode(Opcodes.ALOAD, 1));
                node.instructions.insert(new IntInsnNode(Opcodes.ALOAD, 0));

                return node;

            }

        }
    }

}