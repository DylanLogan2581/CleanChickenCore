package codechicken.core.asm;

import java.io.IOException;
import java.lang.reflect.Field;

import com.google.common.collect.ImmutableBiMap;

import codechicken.lib.asm.ObfMapping;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class CodeChickenAccessTransformer extends AccessTransformer
{
    private static boolean makeAllPublic;
    private static Field f_classNameBiMap;
    private static Object emptyMap = ImmutableBiMap.of();

    public CodeChickenAccessTransformer() throws IOException {
        super();
    }


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        boolean setPublic = makeAllPublic && name.startsWith("net.minecraft.");
        if (setPublic)
            setClassMap(name);
        bytes = super.transform(name, transformedName, bytes);
        if (setPublic)
            restoreClassMap();
        return bytes;
    }

    private void restoreClassMap() {
        try {
            f_classNameBiMap.set(FMLDeobfuscatingRemapper.INSTANCE, emptyMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setClassMap(String name) {
        try {
            f_classNameBiMap.set(FMLDeobfuscatingRemapper.INSTANCE, ImmutableBiMap.of(name.replace('.', '/'), ""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
