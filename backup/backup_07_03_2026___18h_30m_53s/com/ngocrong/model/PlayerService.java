/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.model;

import com.ngocrong.bot.Disciple;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Language;
import com.ngocrong.server.Server;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillPet;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class PlayerService {

    public void uocRong(int type, Player player) {
        int indexDragonBall1Star = -1;
        int indexDragonBall2Star = -1;
        int indexDragonBall3Star = -1;
        int indexDragonBall4Star = -1;
        int indexDragonBall5Star = -1;
        int indexDragonBall6Star = -1;
        int indexDragonBall7Star = -1;
        for (Item itm : player.itemBag) {
            if (itm != null) {
                if (itm.id == ItemName.NGOC_RONG_1_SAO) {
                    indexDragonBall1Star = itm.indexUI;
                }
                if (itm.id == ItemName.NGOC_RONG_2_SAO) {
                    indexDragonBall2Star = itm.indexUI;
                }
                if (itm.id == ItemName.NGOC_RONG_3_SAO) {
                    indexDragonBall3Star = itm.indexUI;
                }
                if (itm.id == ItemName.NGOC_RONG_4_SAO) {
                    indexDragonBall4Star = itm.indexUI;
                }
                if (itm.id == ItemName.NGOC_RONG_5_SAO) {
                    indexDragonBall5Star = itm.indexUI;
                }
                if (itm.id == ItemName.NGOC_RONG_6_SAO) {
                    indexDragonBall6Star = itm.indexUI;
                }
                if (itm.id == ItemName.NGOC_RONG_7_SAO) {
                    indexDragonBall7Star = itm.indexUI;
                }
            }
        }
        if (indexDragonBall1Star != -1 && indexDragonBall2Star != -1 && indexDragonBall3Star != -1
                && indexDragonBall4Star != -1 && indexDragonBall5Star != -1 && indexDragonBall6Star != -1
                && indexDragonBall7Star != -1) {
            switch (type) {
                case 0:
                    if (player.info.power > 80000000000L) {
                        player.service.serverMessage("Bạn đã quá mạnh để sử dụng điều ước này");
                        return;
                    }
                    player.info.addPowerOrPotential(Info.POWER_AND_POTENTIAL, 500000000);
                    player.service.serverMessage("Bạn đã được cộng 500tr TNSM");
                    break;
                case 1:

                    int itemID = ItemName.AVATAR_VIP_TRAI_DAT;
                    if (player.gender == 1) {
                        itemID = ItemName.AVATAR_VIP_NAMEC;
                    } else if (player.gender == 2) {
                        itemID = ItemName.AVATAR_VIP_XAYDA;
                    }
                    Item item = new Item(itemID);
                    item.quantity = 1;
                    item.addItemOption(new ItemOption(97, Utils.nextInt(5, 15)));
                    item.addItemOption(new ItemOption(77, Utils.nextInt(10, 25)));
                    player.addItem(item);
                    break;
                case 2:
                    player.info.originalCritical += 2;
                    if (player.info.originalCritical > 35) {
                        player.info.originalCritical = 35;
                    }
                    player.info.setInfo();
                    player.service.loadPoint();
                    player.service.serverMessage("Bạn đã được cộng 2% chí mạng");
                    break;
                case 3: {
                    try {
                        Disciple disciple = player.myDisciple;
                        SkillPet skillPet = SkillPet.list.get(1);
                        byte skillID = skillPet.skills[Utils.nextInt(skillPet.skills.length)];
                        Skill skill = Skills.getSkill(skillID, (byte) 1);

                        if (skill != null) {
                            skill = skill.clone();
                            skill.coolDown = 1000;
                            disciple.skills.set(1, skill);
                        }
                        skill = null;
                        skillPet = SkillPet.list.get(2);
                        skillID = skillPet.skills[Utils.nextInt(skillPet.skills.length)];
                        skill = Skills.getSkill(skillID, (byte) 1);
                        if (skill != null) {
                            skill = skill.clone();
                            disciple.skills.set(2, skill);
                        }
                    } catch (Exception ex) {
                        
                    }
                    break;
                }
            }
            player.removeItem(indexDragonBall1Star, 1);
            player.removeItem(indexDragonBall2Star, 1);
            player.removeItem(indexDragonBall3Star, 1);
            player.removeItem(indexDragonBall4Star, 1);
            player.removeItem(indexDragonBall5Star, 1);
            player.removeItem(indexDragonBall6Star, 1);
            player.removeItem(indexDragonBall7Star, 1);
            Server server = DragonBall.getInstance().getServer();
//            todoplayer.setSummonDragonDaily(true);
//            server.summonDragonDaily.add(player.id);
        }
    }
}
