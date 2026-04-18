package com.ngocrong.calldragon;

import com.ngocrong.clan.Clan;
import com.ngocrong.clan.ClanManager;
import com.ngocrong.clan.ClanMember;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.NpcName;
import com.ngocrong.data.ClanData;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class CallDragonNamek extends CallDragon {

    private static final Logger logger = Logger.getLogger(CallDragonNamek.class);

    public static final int ADD_5_PERCENT_HP_KI = 0;
    public static final int ADD_5_PERCENT_SD = 1;
    public static final int X2_TNSM = 5;
    public static final int THOI_VANG = 6;
    public static final int LOC_PHAT = 7;
//    public static final int ADD_5_PERCENT_GIAP = 2;
//    public static final int ADD_1_NUMBER_MEMBER_CLAN = 3;

    public CallDragonNamek(Player _c, short x, short y) {
        super(_c, x, y);
        this.id = NpcName.RONG_THIENG;
        this.avatar = 0;
        this.isRongNamek = true;
        this.say = "Ta sẽ ban cho ngươi 1 điều ước, ngươi có 5 phút, hãy suy nghĩ thật kỹ trước khi quyết định";
        wishList = new KeyValue[2][];
        wishList[0] = new KeyValue[5];
        wishList[0][0] = new KeyValue(20004, "Tăng\n5% HP,KI\ncả bang hội", ADD_5_PERCENT_HP_KI);
        wishList[0][1] = new KeyValue(20004, "Tăng\n5% sức đánh\ncả bang hội", ADD_5_PERCENT_SD);
        wishList[0][2] = new KeyValue(20004, "Tăng\n10% TNSM\nđệ và sp", X2_TNSM);
        wishList[0][3] = new KeyValue(20004, "Ngẫu nhiên\n10-20\nthỏi vàng", THOI_VANG);
    }

    @Override
    public void accept() {
        int type = ((Integer) select.elements[0]);
        List<ClanMember> clanMembers = _c.clan.getMembers();
        int wishedNo = -1;
        switch (type) {
            case ADD_5_PERCENT_HP_KI:
                wishedNo = 0;
                break;
            case ADD_5_PERCENT_SD:
                wishedNo = 1;
                break;
            case X2_TNSM:
                wishedNo = 2;
                break;
            case THOI_VANG:
                for (ClanMember clanMember : clanMembers) {
                    Player p = SessionManager.findChar(clanMember.playerID);
                    if (p != null) {
                        int soluong = Utils.getParambyRandom(10, 20);
                        Item thoivang = new Item(457);
                        thoivang.quantity = soluong;
                        p.addItem(thoivang);
                        p.service.sendThongBao("Bạn nhận " + soluong + " thỏi vàng");
                    }
                }
                break;
            case LOC_PHAT:
                for (ClanMember clanMember : clanMembers) {
                    Player p = SessionManager.findChar(clanMember.playerID);
                    if (p != null) {
                        int soluong = 1;
                        for (int i = ItemName.NGOC_RONG_LOC_PHAT_1_SAO; i <= ItemName.NGOC_RONG_LOC_PHAT_7_SAO; i++) {
                            Item ngocrong = new Item(i);
                            ngocrong.quantity = soluong;
                            p.addItem(ngocrong);
                        }
                        p.service.sendThongBao("Bạn nhận 1 bộ ngọc rồng lộc phát");
                    }
                }
                break;
//            case ADD_1_NUMBER_MEMBER_CLAN:
//                Clan clan = ClanManager.getInstance().findClanById(_c.clanID);
//                if (clan != null && clan.maxMember < 15) {
//                    Optional<ClanData> clanDataOptional = GameRepository.getInstance().clan.findById(_c.clan.id);
//                    if (clanDataOptional.isPresent()) {
//                        ClanData clanData = clanDataOptional.get();
//                        int maxNumber = clanData.getMaxMember() + 1;
//                        clanData.setMaxMember((byte) maxNumber);
//                        GameRepository.getInstance().clan.save(clanData);
//                    }
//                    clan.maxMember += 1;
//                    clan.clanInfo();
//                } else {
//                    back("Số thành viên bang hội tối đa là 15, vui lòng chọn điều ước khác");
//                    return;
//                }
//                break;
        }

        if (!clanMembers.isEmpty() && wishedNo >= 0) {
            for (ClanMember clanMember : clanMembers) {
                Player p = SessionManager.findChar(clanMember.playerID);
                if (p != null) {
                    p.clan.addClanRewardForMember(wishedNo + 8);
                }
            }
        }
        _c.service.openUISay(type, "Điều ước của ngươi đã thành sự thực\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai", avatar);
        close();
    }

}
