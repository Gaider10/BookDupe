package me.andrew.bookdupe.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    private static final ItemStack DUPE_BOOK;

    static {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < 21845; i++){
            stringBuilder.append((char) 2048);
        }
        String str1 = stringBuilder.toString();
        DUPE_BOOK = new ItemStack(Items.WRITABLE_BOOK, 1);
        DUPE_BOOK.putSubTag("title", StringTag.of("a"));
        ListTag listTag = new ListTag();
        listTag.addTag(0, StringTag.of(str1));
        for(int i = 1; i < 40; i++){
            listTag.addTag(i, StringTag.of("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        }
        DUPE_BOOK.putSubTag("pages", listTag);
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void onChatMessage(String message, CallbackInfo ci){
        if(message.equals(".d") || message.equals(".s")) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            if (player.inventory.getMainHandStack().getItem() == Items.WRITABLE_BOOK) {
                player.networkHandler.sendPacket(new BookUpdateC2SPacket(DUPE_BOOK, true, player.inventory.selectedSlot));
                if (message.equals(".s")) player.networkHandler.sendPacket(new ClickSlotC2SPacket(player.currentScreenHandler.syncId, 36 + player.inventory.selectedSlot, 0, SlotActionType.THROW, DUPE_BOOK, player.currentScreenHandler.getNextActionId(player.inventory)));
            }
            ci.cancel();
        }
    }
}
