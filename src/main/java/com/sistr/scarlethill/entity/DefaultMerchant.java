package com.sistr.scarlethill.entity;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MerchantOffers;

import javax.annotation.Nullable;

public abstract class DefaultMerchant implements IMerchant {
    @Nullable
    private PlayerEntity customer;

    @Override
    public void setCustomer(@Nullable PlayerEntity player) {
        this.customer = player;
    }

    @Nullable
    @Override
    public PlayerEntity getCustomer() {
        return this.customer;
    }

    @Override
    public void setClientSideOffers(@Nullable MerchantOffers offers) {

    }

    @Override
    public int getXp() {
        return 0;
    }

    @Override
    public void setXP(int xpIn) {

    }

    //行商人ではfalse。おそらく補充するか否か？
    @Override
    public boolean func_213705_dZ() {
        return true;
    }
}
