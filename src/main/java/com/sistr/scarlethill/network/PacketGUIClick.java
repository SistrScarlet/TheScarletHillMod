package com.sistr.scarlethill.network;

import com.sistr.scarlethill.block.tile.IClickableGUI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGUIClick {
    private final int windowId;
    private final float x;
    private final float y;

    public PacketGUIClick(PacketBuffer buf) {
        windowId = buf.readInt();
        x = buf.readFloat();
        y = buf.readFloat();
    }

    public PacketGUIClick(int windowId, float x, float y) {
        this.windowId = windowId;
        this.x = x;
        this.y = y;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(windowId);
        buf.writeFloat(x);
        buf.writeFloat(y);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player.openContainer instanceof IClickableGUI && player.openContainer.windowId == windowId && player.openContainer.getCanCraft(player) && !player.isSpectator()) {
                ((IClickableGUI) player.openContainer).clickingGUI(player, x, y);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
