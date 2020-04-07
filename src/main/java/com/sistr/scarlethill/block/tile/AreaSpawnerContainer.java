package com.sistr.scarlethill.block.tile;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class AreaSpawnerContainer extends Container implements IClickableGUI {
    private BlockPos pos;
    private AreaSpawnerTile tile;
    private PlayerEntity player;
    private IItemHandler playerInventory;
    private int limit;
    private int exit;

    public AreaSpawnerContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(Registration.AREA_SPAWNER_CONTAINER.get(), windowId);
        this.pos = pos;
        this.tile = (AreaSpawnerTile) world.getTileEntity(pos);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        this.limit = this.tile.getSpawnerLogic().getSpawnLimit();
        this.exit = (int) (this.tile.getSpawnerLogic().getExitDistance() * 2F);

        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public void clickingGUI(PlayerEntity player, float x, float y) {

        //ボタンの左上を原点とした相対位置
        //右下が第一象限になる
        float relX = x - 7;
        float relY = y - 7;

        int sizeX = 9;
        int sizeY = 18;

        //Limitボタン
        if (0 <= relX && relX < sizeX && 0 <= relY && relY < sizeY) {
            if (relY < sizeY / 2F) {
                this.limit++;
            } else {
                this.limit--;
            }
            this.tile.getSpawnerLogic().setSpawnLimit(this.limit);
            return;
        }

        relX = x - 16;

        //Exitボタン
        if (0 <= relX && relX < sizeX && 0 <= relY && relY < sizeY) {
            if (relY < sizeY / 2F) {
                this.exit++;
            } else {
                this.exit--;
            }
            this.tile.getSpawnerLogic().setExitDistance(this.exit / 2F);
            return;
        }

        this.tile.getSpawnerLogic().spawnMarkerList.forEach(System.out::println);
        System.out.println(this.tile.getSpawnerLogic().relActiveArea);

    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getLimit() {
        return this.limit;
    }

    public float getExit() {
        return this.exit / 2F;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return player.isCreative() && isWithinUsableDistance(IWorldPosCallable.of(this.tile.getWorld(), this.tile.getPos()), player, Registration.AREA_SPAWNER_BLOCK.get());
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
