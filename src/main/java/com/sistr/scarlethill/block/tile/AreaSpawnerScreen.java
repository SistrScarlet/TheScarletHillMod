package com.sistr.scarlethill.block.tile;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.network.Networking;
import com.sistr.scarlethill.network.PacketGUIClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AreaSpawnerScreen extends ContainerScreen<AreaSpawnerContainer> {
    private ResourceLocation GUI = new ResourceLocation(ScarletHillMod.MODID, "textures/gui/area_spawner_gui.png");

    public AreaSpawnerScreen(AreaSpawnerContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickButton) {
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;

        float x = (float) (mouseX - relX);
        float y = (float) (mouseY - relY);


        this.container.clickingGUI(ScarletHillMod.proxy.getClientPlayer(), x, y);
        Networking.INSTANCE.sendToServer(new PacketGUIClick(this.container.windowId, x, y));

        return super.mouseClicked(mouseX, mouseY, clickButton);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(Minecraft.getInstance().fontRenderer, "SpawnLimit : " + this.container.getLimit(), 7, 52, 0xffffff);
        drawString(Minecraft.getInstance().fontRenderer, "ExitDistance : " + this.container.getExit() + "M", 7, 52 + 9, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
