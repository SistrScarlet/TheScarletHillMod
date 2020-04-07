package com.sistr.scarlethill.network;

import com.sistr.scarlethill.item.ILeftClickable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketLeftClick {

    public PacketLeftClick(PacketBuffer buf) {

    }

    public PacketLeftClick() {

    }

    public void toBytes(PacketBuffer buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            Item item = player.getHeldItem(Hand.MAIN_HAND).getItem();
            if (item instanceof ILeftClickable) {
                ((ILeftClickable) item).onLeftClick(player.world, player, Hand.MAIN_HAND);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
