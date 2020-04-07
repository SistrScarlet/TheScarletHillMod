package com.sistr.scarlethill.network;

import com.sistr.scarlethill.ScarletHillMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ScarletHillMod.MODID, "scarlethill"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
                PacketLeftClick.class,
                PacketLeftClick::toBytes,
                PacketLeftClick::new,
                PacketLeftClick::handle);
        INSTANCE.registerMessage(nextID(),
                PacketGUIClick.class,
                PacketGUIClick::toBytes,
                PacketGUIClick::new,
                PacketGUIClick::handle);
    }
}
