/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 DoubleDoorDevelopment
 *
 * I can't demand this, but I ask for respect and gratitude for the time and effort
 * put into the project by all developers, testers, designers and documenters. ~~Dries007
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.dries007.torchtools;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK;

/**
 * Main mod file
 *
 * Thanks for the idea Tinkers Construct
 *
 * @author Dries007
 * @author DoubleDoorDevelopment
 */
@Mod(modid = TorchTools.MODID, name = TorchTools.MODID)
public class TorchTools
{
    public static final String MODID = "TorchTools";

    @Mod.Instance(MODID)
    public static TorchTools instance;

    public TorchTools()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerInteractEventHandler(PlayerInteractEvent event)
    {
        // Server side and on block only.
        if (event.world.isRemote || event.action != RIGHT_CLICK_BLOCK) return;
        ItemStack heldItem = event.entityPlayer.inventory.getCurrentItem();
        // Only tools, not null
        if (heldItem == null || !(heldItem.getItem() instanceof ItemTool)) return;
        // Save old slot id
        int oldSlot = event.entityPlayer.inventory.currentItem;
        // Calculate new slot id
        int newSlot = oldSlot == 0 ? 8 : oldSlot + 1;
        // Get new item
        ItemStack slotStack = event.entityPlayer.inventory.getStackInSlot(newSlot);
        // No null please
        if (slotStack == null) return;
        // Set current slot to new slot to fool Minecraft
        event.entityPlayer.inventory.currentItem = newSlot;
        // Fake right click                                                                                                                                                   Oh look fake values :p
        ((EntityPlayerMP) event.entityPlayer).theItemInWorldManager.activateBlockOrUseItem(event.entityPlayer, event.world, slotStack, event.x, event.y, event.z, event.face, 0.5f, 0.5f, 0.5f);
        // Set old slot back properly
        event.entityPlayer.inventory.currentItem = oldSlot;
        // Update client
        ((EntityPlayerMP) event.entityPlayer).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(event.entityPlayer.openContainer.windowId, newSlot + 36, slotStack));
    }
}
