package com.sistr.scarlethill.block.tile;

import net.minecraft.entity.player.PlayerEntity;

public interface IHasWizardBlock {

    void startWizard(PlayerEntity player);

    void sendMessageTemplate(PlayerEntity player, String text);

    void receiveDate(PlayerEntity player, Object object);

    void cancelWizard(PlayerEntity player);

    void endWizard(PlayerEntity player);
}
