package com.ngocrong.calldragon;

import com.ngocrong.item.ItemOption;
import com.ngocrong.bot.Disciple;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.NpcName;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillPet;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

public class CallDragon1Star extends CallDragon {

    private static final Logger logger = Logger.getLogger(CallDragon1Star.class);

    public static final int PROTECT_STONE = 0;
    public static final int GOLD_BAR = 1;
    public static final int HONG_NGOC = 2;
    public static final int ADD_CRITICAL = 3;
    public static final int AVATAR_VIP = 4;
    public static final int CHANGE_SKILL_2 = 5;
    public static final int CHANGE_SKILL_3 = 6;

    public CallDragon1Star(Player _c, short x, short y) {
        super(_c, x, y);
        this.id = NpcName.RONG_THIENG;
        this.avatar = 0;
        this.isRongNamek = false;
        this.say = "Ta sẽ ban cho ngươi 1 điều ước, ngươi có 5 phút, hãy suy nghĩ thật kỹ trước khi quyết định";
        wishList = new KeyValue[2][];
        // trang 1
        wishList[0] = new KeyValue[5];
        wishList[0][0] = new KeyValue(20004, "10\nĐá bảo vệ", PROTECT_STONE);
        wishList[0][1] = new KeyValue(20004, "20\nthỏi vàng", GOLD_BAR);
        wishList[0][2] = new KeyValue(20004, "1500\nhồng ngọc", HONG_NGOC);
        wishList[0][3] = new KeyValue(20004, "Thêm\n2% chí\nmạng", ADD_CRITICAL);
        wishList[0][4] = new KeyValue(20000, "Điều ước khác");
        // trang 2
        wishList[1] = new KeyValue[4];
        wishList[1][0] = new KeyValue(20004, "Avatar\nđẹp trai", AVATAR_VIP);
        wishList[1][1] = new KeyValue(20004, "Đổi\nchiêu 2\nĐệ tử", CHANGE_SKILL_2);
        wishList[1][2] = new KeyValue(20004, "Đổi\nchiêu 3\nĐệ tử", CHANGE_SKILL_3);
        wishList[1][3] = new KeyValue(20000, "Điều ước khác");
    }

    @Override
    public void accept() {
        int type = ((Integer) select.elements[0]);
        switch (type) {
            case PROTECT_STONE: {
                if (_c.getSlotNullInBag() == 0) {
                    back("Hành trang đã đầy, vui lòng chọn điều ước khác");
                    return;
                }
                Item item = new Item(ItemName.DA_BAO_VE_987);
                item.quantity = 10;
                item.setDefaultOptions();
                _c.addItem(item);
            }
            break;

            case GOLD_BAR: {
                if (_c.getSlotNullInBag() == 0) {
                    back("Hành trang đã đầy, vui lòng chọn điều ước khác");
                    return;
                }
                Item item = new Item(ItemName.THOI_VANG);
                item.quantity = 20;
                item.setDefaultOptions();
                _c.addItem(item);
            }
            break;

            case HONG_NGOC:
                _c.addDiamondLock(1500);
                break;

            case ADD_CRITICAL:
               if (_c.info.originalCritical >= 25) {
                    back("Chí mạng của bạn đã đạt mức tối đa");
                    return;
                }
                _c.info.originalCritical += 2;
                if (_c.info.originalCritical > 25) {
                    _c.info.originalCritical = 25;
                }
                _c.info.setInfo();
                _c.service.loadPoint();
                _c.service.serverMessage("Bạn đã được cộng 2% chí mạng");
                break;

            case AVATAR_VIP: {
                if (_c.getSlotNullInBag() == 0) {
                    back("Hành trang đã đầy, vui lòng chọn điều ước khác");
                    return;
                }
                int itemID = ItemName.AVATAR_VIP_TRAI_DAT;
                if (_c.gender == 1) {
                    itemID = ItemName.AVATAR_VIP_NAMEC;
                } else if (_c.gender == 2) {
                    itemID = ItemName.AVATAR_VIP_XAYDA;
                }
                Item item = new Item(itemID);
                item.quantity = 1;
                item.addItemOption(new ItemOption(77, Utils.nextInt(10, 25)));
                item.addItemOption(new ItemOption(97, Utils.nextInt(5, 10)));
                _c.addItem(item);
            }
            break;

            case CHANGE_SKILL_2:
                if (_c.myDisciple != null) {
                    if (_c.myDisciple.skillOpened < 2) {
                        back("Không thể thực hiện, vui lòng chọn điều ước khác");
                        return;
                    }
                    try {
                        Disciple disciple = _c.myDisciple;
                        SkillPet skillPet = SkillPet.list.get(1);
                        byte skillID = skillPet.skills[Utils.nextInt(skillPet.skills.length)];
                        Skill skill = Skills.getSkill(skillID, (byte) 1);

                        if (skill != null) {
                            skill = skill.clone();
                            skill.coolDown = 1000;
                            disciple.skills.set(1, skill);
                        }
                    } catch (Exception ex) {
                        
                        logger.error("error", ex);
                    }
                } else {
                    back("Bạn không có đệ tử, vui lòng chọn điều ước khác");
                    return;
                }
                break;

            case CHANGE_SKILL_3:
                if (_c.myDisciple != null) {
                    if (_c.myDisciple.skillOpened < 3) {
                        back("Không thể thực hiện, vui lòng chọn điều ước khác");
                        return;
                    }
                    try {
                        Disciple disciple = _c.myDisciple;
                        SkillPet skillPet = SkillPet.list.get(2);
                        byte skillID = skillPet.skills[Utils.nextInt(skillPet.skills.length)];
                        Skill skill = Skills.getSkill(skillID, (byte) 1);
                        if (skill != null) {
                            skill = skill.clone();
                            disciple.skills.set(2, skill);
                        }
                    } catch (Exception ex) {
                        
                        logger.error("error", ex);
                    }
                } else {
                    back("Bạn không có đệ tử, vui lòng chọn điều ước khác");
                    return;
                }
                break;
        }
        _c.service.openUISay(type, "Điều ước của ngươi đã thành sự thực\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai", avatar);
        close();
    }

}
