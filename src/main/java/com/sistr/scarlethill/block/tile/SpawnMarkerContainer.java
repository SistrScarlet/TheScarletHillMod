package com.sistr.scarlethill.block.tile;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class SpawnMarkerContainer extends Container implements IClickableGUI {
    private TileEntity tile;
    private PlayerEntity player;
    private IItemHandler playerInventory;
    private final IInventory inventory = new Inventory(1);

    public SpawnMarkerContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.SPAWNER_MARKER_CONTAINER.get(), windowId);
        this.tile = world.getTileEntity(pos);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        this.addSlot(new Slot(this.inventory, 0, 8, 53));

        layoutPlayerInventorySlots(8, 84);
    }

    public String getEntityType() {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.tile.write(new CompoundNBT()).getString("SpawnEntityType")));
        return type != null ? type.getTranslationKey() : "";
    }

    @Override
    public void clickingGUI(PlayerEntity player, float x, float y) {
        System.out.println(this.tile instanceof SpawnMarkerTile ? ((SpawnMarkerTile) this.tile).getEntityType().getRegistryName() : null);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn.isCreative() && isWithinUsableDistance(IWorldPosCallable.of(this.tile.getWorld(), this.tile.getPos()), playerIn, Registration.SPAWNER_MARKER_BLOCK.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (stack.getItem() == Registration.CREATIVE_WRENCH_ITEM.get()) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.mergeItemStack(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.mergeItemStack(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }


    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
