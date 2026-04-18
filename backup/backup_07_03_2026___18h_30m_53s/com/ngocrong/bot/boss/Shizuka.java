///*
//package com.ngocrong.bot.boss;
//
//import com.ngocrong.bot.Boss;
//import com.ngocrong.consts.ItemName;
//import com.ngocrong.item.Item;
//import com.ngocrong.item.ItemMap;
//import com.ngocrong.server.PlayerManager;
//import com.ngocrong.skill.Skill;
//import com.ngocrong.skill.Skills;
//import com.ngocrong.user.Player;
//import org.apache.log4j.Logger;
//
//import java.util.ArrayList;
//
//public class Shizuka extends Boss {
//
//    private static Logger logger = Logger.getLogger(Shizuka.class);
//
//    private ArrayList<String> chats;
//    private short indexChat;
//
//    public Shizuka() {
//        super();
//        this.limit = -1;
//        this.name = "Shizuka";
//        this.sayTheLastWordBeforeDie = "Các ngươi cứ đợi đấy, ta đi đây";
//        setIdAuraEff((short) 0);
//        setInfo(1000000000, 2000000, 100000, 0, 50);
//        info.options[95] = 100;
//        setDefaultPart();
//        setTypePK((byte) 5);
//        setUpContentChat();
//    }
//
//    public void initSkill() {
//        try {
//            skills = new ArrayList();
//            Skill skill;
//            skill = Skills.getSkill((byte) 4, (byte) 7).clone();
//            skill.coolDown = 1000;
//            skills.add(skill);
//            skill = Skills.getSkill((byte) 1, (byte) 7).clone();
//            skill.coolDown = 3000;
//            skills.add(skill);
//            skill = Skills.getSkill((byte) 6, (byte) 7).clone();
//            skill.coolDown = 20000;
//            skills.add(skill);
//            skill = Skills.getSkill((byte) 19, (byte) 7).clone();
//            skills.add(skill);
//            skill = Skills.getSkill((byte) 12, (byte) 7).clone();
//            skills.add(skill);
//        } catch (Exception ex) { 
//            logger.error("init skill err", ex);
//        }
//    }
//
//    private void setUpContentChat() {
//        indexChat = 0;
//        chats = new ArrayList<>();
//        chats.add("Konna koto ii na iketara ii na");
//        chats.add("Anna yume konna yume ippai aru kedo");
//        chats.add("Minna minna minna");
//        chats.add("Kanaete kureru");
//        chats.add("Fushigina POKKE de kanaete kureru");
//        chats.add("Sora wo jiyuu ni tobitai na");
//        chats.add("(Hai! takekoputaa!)");
//        chats.add("AN AN AN");
//        chats.add("Tottemo daisuki |2|DORAEMON");
//        chats.add("Shukudai touban shiken ni otsukai");
//        chats.add("Anna koto konna koto taihen dakedo");
//        chats.add("Minna minna minna");
//        chats.add("Tasukete kureru");
//        chats.add("Benrina dougu de tasukete kureru");
//        chats.add("Omocha no heitai da");
//        chats.add("(Sore! tototsugeki!)");
//        chats.add("AN AN AN");
//        chats.add("Tottemo daisuki |2|DORAEMON");
//        chats.add("Anna toko ii na iketara ii na");
//        chats.add("Kono kuni ano shima takusan aru kedo");
//        chats.add("Minna minna minna");
//        chats.add("Ikasete kureru");
//        chats.add("Mirai no kikai de kanaete kureru");
//        chats.add("Sekai ryokou ni ikitai na");
//        chats.add("(Ufufufu doko demo DOA!)");
//        chats.add("AN AN AN");
//        chats.add("Tottemo daisuki |2|DORAEMON");
//        chats.add("AN AN AN");
//        chats.add("Tottemo daisuki |2|DORAEMON");
//    }
//
//    @Override
//    public void updateEveryFiveSeconds() {
//        super.updateEveryOneSeconds();
//        if (!isDead()) {
//            if (indexChat >= chats.size()) {
//                indexChat = 0;
//            }
//            chat(chats.get(indexChat));
//            indexChat++;
//        }
//    }
//
//    @Override
//    public void sendNotificationWhenAppear(String map) {
//        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s, hãy gặp Mr Popo để truy tìm Shizuka", this.name, map));
//    }
//
//    @Override
//    public void sendNotificationWhenDead(String name) {
//        SessionManager.chatVip(String.format("%s: Đã đánh bại và nhận được cải trang thành Shizuka", name));
//    }
//
//    @Override
//    public void throwItem(Object obj) {
//        if (obj != null) {
//            Player c = (Player) obj;
//            Item item = new Item(ItemName.CAI_TRANG_XUKA);
//            item.setDefaultOptions();
//            item.quantity = 1;
//            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//            itemMap.item = item;
//            itemMap.playerID = c.id;
//            itemMap.x = getX();
//            itemMap.y = zone.map.collisionLand(getX(), getY());
//            zone.addItemMap(itemMap);
//            zone.service.addItemMap(itemMap);
//        }
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        setLeg((short) 804);
//    }
//
//    @Override
//    public void setDefaultBody() {
//        setBody((short) 803);
//    }
//
//    @Override
//    public void setDefaultHead() {
//        setHead((short) 802);
//    }
//
//}
//*/
