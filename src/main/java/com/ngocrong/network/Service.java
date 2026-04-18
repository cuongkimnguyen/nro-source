package com.ngocrong.network;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.clan.Clan;
import com.ngocrong.clan.ClanImage;
import com.ngocrong.clan.ClanMessage;
import com.ngocrong.clan.ClanMember;
import com.ngocrong.collection.Card;
import com.ngocrong.collection.CardTemplate;
import com.ngocrong.combine.Combine;
import com.ngocrong.consts.Cmd;
import com.ngocrong.consts.MapName;
import com.ngocrong.effect.Effect;
import com.ngocrong.effect.EffectChar;
import com.ngocrong.effect.EffectData;
import com.ngocrong.item.*;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.lib.Menu;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.MapService;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.mob.MobTemplate;
import com.ngocrong.model.*;
import com.ngocrong.server.*;
import com.ngocrong.shop.Shop;
import com.ngocrong.shop.Tab;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SpecialSkill;
import com.ngocrong.skill.SpecialSkillTemplate;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;
import com.ngocrong.network.FastDataOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Service implements IService {

    private static final Logger logger = Logger.getLogger(Service.class);
    public static int[][] PET = {{281, 361, 351}, {512, 513, 536}, {514, 515, 537}};
    private Session session;
    private Player player;
    private byte[] small, bg;

    public Service(Session session) {
        this.session = session;
    }

    @Override
    public void setResource() {
        Server server = DragonBall.getInstance().getServer();
        try {
            small = server.smallVersion[session.zoomLevel - 1];
            bg = server.backgroundVersion[session.zoomLevel - 1];
        } catch (NullPointerException ex) {
            
            logger.error("set resource err: " + ex.getMessage(), ex);
        }
    }

    public Service(Player deTu) {
        this.player = deTu;
    }

    @Override
    public void setChar(Player _player) {
        this.player = _player;
    }

    @Override
    public void close() {
        small = null;
        bg = null;
        session = null;
        player = null;
    }

    public void changeBodyMob(Mob mob, byte type) {
        try {
            Message msg = new Message(Cmd.CHAGE_MOD_BODY);
            FastDataOutputStream ds = msg.writer();
            ds.writeByte(type);
            ds.writeInt(mob.mobId);
            if (type == 1) {
                ds.writeShort(mob.body);
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void CHANGE_ONSKILL(byte[] ms) {
        try {
            Message msg = new Message(Cmd.CHANGE_ONSKILL);
            FastDataOutputStream ds = msg.writer();
            for (int i = 0; i < 10; i++) {
                try {
                    ds.writeByte(ms[i]);
                } catch (Exception e) {
                    
                    ds.writeByte(-1);
                }
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void mapTransport(List<KeyValue> list) {
        try {
            Message msg = new Message(Cmd.MAP_TRASPORT);
            FastDataOutputStream ds = msg.writer();
            ds.writeByte(list.size());
            for (KeyValue<Integer, String> keyValue : list) {
                ds.writeUTF(keyValue.value);
                ds.writeUTF((String) keyValue.elements[0]);
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void focus(int charID) {
        try {
            Message msg = new Message(Cmd.ME_CUU_SAT);
            FastDataOutputStream ds = msg.writer();
            ds.writeInt(charID);
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void messageTime(MessageTime message) {
        try {
            Message msg = new Message(Cmd.MESSAGE_TIME);
            FastDataOutputStream ds = msg.writer();
            ds.writeByte(message.getId());
            ds.writeUTF(message.getText());
            ds.writeShort(message.getTime());
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void specialSkill(byte type) {
        try {
            Message msg = new Message(Cmd.SPEACIAL_SKILL);
            FastDataOutputStream ds = msg.writer();
            ds.writeByte(type);
            if (type == 0) {
                SpecialSkill skill = player.getSpecialSkill();
                if (skill != null) {
                    ds.writeShort(skill.getIcon());
                    ds.writeUTF(skill.getInfo2());
                } else {
                    ds.writeShort(5223);
                    ds.writeUTF(Language.NO_SPECIAL_SKILLS_YET);
                }
            }
            if (type == 1) {
                List<SpecialSkillTemplate> list = SpecialSkill.getListSpecialSkill(player.gender);
                ds.writeByte(1);
                ds.writeUTF(Language.MENU_SPECIAL_SKILL_NAME);
                ds.writeByte(list.size());
                for (SpecialSkillTemplate sp : list) {
                    ds.writeShort(sp.getIcon());
                    ds.writeUTF(sp.getInfo());
                }
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void fusion(Player _player, byte type) {
        try {
            Message msg = new Message(Cmd.FUSION);
            FastDataOutputStream ds = msg.writer();
            ds.writeByte(type);
            ds.writeInt(_player.id);
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void captcha(byte type, Captcha captcha) {
        try {
            Message msg = new Message(Cmd.MOB_CAPCHA);
            FastDataOutputStream ds = msg.writer();
            ds.writeByte(type);
            if (type == 0) {
                byte[] data = captcha.getData();
                ds.writeShort(data.length);
                ds.write(data);
                ds.writeUTF(captcha.getKeyStr());
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void resetPoint() {
        try {
            Message msg = new Message(Cmd.RESET_POINT);
            FastDataOutputStream ds = msg.writer();
            ds.writeShort(player.getX());
            ds.writeShort(player.getY());
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void clanUpdate(byte type, ClanMember clanMember, int deleteId) {
        try {
            Message ms = new Message(Cmd.CLAN_UPDATE);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(type);
            if (type == 0 || type == 2) {
                ds.writeInt(clanMember.playerID);
                ds.writeShort(clanMember.head);
                ds.writeShort(clanMember.leg);
                ds.writeShort(clanMember.body);
                ds.writeUTF(clanMember.name);
                ds.writeByte(clanMember.role);
                ds.writeUTF(Utils.formatNumber(clanMember.powerPoint));
                ds.writeInt(clanMember.donate);
                ds.writeInt(clanMember.receiveDonate);
                ds.writeInt(clanMember.clanPoint);
                ds.writeUTF(clanMember.getStrJoinTime());
            }
            if (type == 1) {
                ds.writeInt(deleteId);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void mobMeUpdate(Player _player, Object target, long dame, byte skillId, byte type) {
        try {
            Message ms = new Message(Cmd.MOB_ME_UPDATE);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(type);
            if (type == 0) {
                ds.writeInt(_player.id);
                ds.writeShort(_player.mobMe.templateId);
                ds.writeLong(_player.mobMe.hp);
            }
            if (type == 1) {
                Mob mob = (Mob) target;
                ds.writeInt(_player.id);
                ds.writeInt(mob.mobId);
            }
            if (type == 2) {
                Player _c = (Player) target;
                ds.writeInt(_player.id);
                ds.writeInt(_c.id);
                ds.writeLong(dame);
                ds.writeLong(_c.info.hp);
            }
            if (type == 3) {
                Mob mob = (Mob) target;
                ds.writeInt(_player.id);
                ds.writeInt(mob.mobId);
                ds.writeLong(mob.hp);
                ds.writeLong(dame);
            }
            if (type == 5) {
                Mob mob = (Mob) target;
                ds.writeInt(_player.id);
                ds.writeByte(skillId);
                ds.writeInt(mob.mobId);
                ds.writeLong(dame);
                ds.writeLong(mob.hp);
            }
            if (type == 6) {
                ds.writeInt(_player.id);
            }
            if (type == 7) {
                ds.writeInt(_player.id);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void phaohoaZone() {
        player.zone.lockChar.readLock().lock();
        try {
            for (Player _c : player.zone.players) {
                _c.service.serverEffect((byte) 1, (byte) 2, (byte) 62, (short) player.getX(), (short) player.getY(), (short) -1);
                _c.service.serverEffect((byte) 1, (byte) 2, (byte) 63, (short) player.getX(), (short) player.getY(), (short) -1);
                _c.service.serverEffect((byte) 1, (byte) 2, (byte) 64, (short) player.getX(), (short) player.getY(), (short) -1);
                _c.service.serverEffect((byte) 1, (byte) 2, (byte) 65, (short) player.getX(), (short) player.getY(), (short) -1);
            }
        } finally {
            player.zone.lockChar.readLock().unlock();
        }
    }

    public void serverEffect(byte loop, byte layer, byte id4, short x2, int y2, short loopCount) {
        try {
            Message mss = new Message(Cmd.SERVER_EFFECT);
            FastDataOutputStream ds = mss.writer();

            ds.writeByte(loop);
            ds.writeByte(layer);
            ds.writeByte(id4);
            ds.writeShort(x2);
            ds.writeShort(y2);
            ds.writeShort(loopCount);

            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            java.util.logging.Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setEffect(Hold hold, int id, byte status, byte type, short effId) {
        try {
            Message mss = new Message(Cmd.HOLD);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(status);
            ds.writeByte(type);
            if (type == Skill.CHARACTER) {
                if (status == Skill.REMOVE_ALL_EFFECT) {
                    ds.writeInt(id);
                }
                ds.writeByte(effId);
                ds.writeInt(id);
                if (effId == 32) {
                    if (status == Skill.ADD_EFFECT) {
                        ds.writeInt(hold.holder.id);
                    }
                }
            }
            if (type == Skill.MONSTER) {
                ds.writeByte(effId);
                ds.writeInt(id);
                if (effId == 32) {
                    if (status == Skill.ADD_EFFECT) {
                        ds.writeInt(hold.holder.id);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void skillNotFocus(int charId, short skillId, byte type, ArrayList<Mob> mobs, ArrayList<Player> players) {
        try {
            Message mss = new Message(Cmd.SKILL_NOT_FOCUS);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeInt(charId);
            ds.writeShort(skillId);
            if (type == 0) {
                ds.writeByte(mobs.size());
                for (Mob mob : mobs) {
                    ds.writeInt(mob.mobId);
                    ds.writeByte(mob.seconds);
                }
                ds.writeByte(players.size());
                for (Player _c : players) {
                    ds.writeInt(_c.id);
                    ds.writeByte(_c.freezSeconds);
                }
            }
            if (type == 4 || type == 7) {
                ds.writeShort(player.seconds);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception e) {
            
            logger.error("failed!", e);
        }
    }

    public void setMapInfo() {
        Utils.setTimeout(()
                -> {
            try {
                Zone z = player.zone;
                if (z == null) {
                    logger.error("failed!1");
                    return;
                }
                TMap map = z.map;
                if (map == null) {
                    logger.error("failed!2");
                    return;
                }
                Message mss = new Message(Cmd.MAP_INFO);
                FastDataOutputStream ds = mss.writer();
                ds.writeByte(map.mapID);
                ds.writeByte(map.planet);
                ds.writeByte(map.tileID);
                ds.writeByte(map.bgID);
                ds.writeByte(map.typeMap);
                ds.writeUTF(map.name);
                ds.writeByte(z.zoneID);
                ds.writeShort(player.getX());
                ds.writeShort(player.getY());
                ds.writeByte(map.waypoints.length);
                for (Waypoint w : map.waypoints) {
                    ds.writeShort(w.minX);
                    ds.writeShort(w.minY);
                    ds.writeShort(w.maxX);
                    ds.writeShort(w.maxY);
                    ds.writeBoolean(w.isEnter);
                    ds.writeBoolean(w.isOffline);
                    ds.writeUTF(w.name);
                }
                List<Mob> mobs = z.getListMob();
                ds.writeByte(mobs.size());
                for (Mob mob : mobs) {
                    ds.writeInt(mob.mobId);
                    ds.writeBoolean(mob.isDisable);
                    ds.writeBoolean(mob.isDontMove);
                    ds.writeBoolean(mob.isFire);
                    ds.writeBoolean(mob.isIce);
                    ds.writeBoolean(mob.isWind);
                    ds.writeByte(mob.templateId);
                    ds.writeByte(mob.sys);
                    ds.writeLong(mob.hp);
                    ds.writeByte(mob.level);
                    ds.writeLong(mob.maxHp);
                    ds.writeShort(mob.x);
                    ds.writeShort(mob.y);
                    ds.writeByte(mob.status);
                    ds.writeByte(mob.levelBoss);
                    ds.writeBoolean(mob.isBoss);
                }
                ds.writeByte(0);//empty
                List<Npc> npcs = z.getListNpc(player);
                ds.writeByte(npcs.size());
                for (Npc npc : npcs) {
                    ds.writeByte(npc.status);
                    ds.writeShort(npc.x);
                    ds.writeShort(npc.y);
                    ds.writeByte(npc.templateId);
                    ds.writeShort(npc.avatar);
                }
                List<ItemMap> items = z.getListItemMap(player.taskMain);
                ds.writeShort(items.size());
                for (ItemMap item : items) {
                    ds.writeShort(item.id);
                    ds.writeShort(item.item.id);
                    ds.writeShort(item.x);
                    ds.writeShort(item.y);
                    ds.writeInt(item.playerID);
                    if (item.playerID == -2) {
                        ds.writeShort(item.r);
                    }
                }
                ds.writeShort(map.positionBgItems.length);
                for (BgItem bg : map.positionBgItems) {
                    ds.writeShort(bg.id);
                    ds.writeShort(bg.x);
                    ds.writeShort(bg.y);
                }
                ds.writeShort(map.effects.length);
                for (KeyValue<String, String> k : map.effects) {
                    ds.writeUTF(k.key);
                    ds.writeUTF(k.value);
                }
                ds.writeByte(map.bgType);
                ds.writeByte(player.getTeleport());
                ds.writeBoolean(map.isDoubleMap());
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            } catch (IOException ex) {
                
                logger.error("failed!", ex);
            }
        },
                50);
    }

    public void clearMap() {
        sendMessage(new Message(Cmd.MAP_CLEAR));
    }

    public void playerAdd(Player _player) {
        try {
            Message mss = new Message(Cmd.PLAYER_ADD);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.getId());
            ds.writeInt(_player.getClanID());
            writeCharInfo(mss, _player);
            ds.writeByte(_player.getTeleport());//teleport
            ds.writeBoolean(_player.isMonkey());
            ds.writeShort(_player.getIdMount());
            ds.writeByte(_player.getFlag());
            ds.writeBoolean(_player.isNhapThe());
            ds.writeShort(_player.getIdAuraEff());
            ds.writeShort(_player.getIdEffSetItem());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void petFollow(Player _c, byte type) {
        try {
            Message mss = new Message(Cmd.STATUS_PET);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_c.id);
            ds.writeByte(type);
            if (type == 1) {
                PetFollow pet = _c.getPetFollow();
                ds.writeShort(pet.getSmallID());
                ds.writeByte(pet.getImg());
                byte[] frame = pet.getFrame();
                ds.writeByte(frame.length);
                for (byte f : frame) {
                    ds.writeByte(f);
                }
                ds.writeShort(pet.getW());
                ds.writeShort(pet.getH());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void quaySo(byte type, String numWin, String finish, String yourNumber) {
        try {
            Message mss = new Message(Cmd.QUAYSO);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            if (type == 0) {
                ds.writeUTF(yourNumber);
            }
            if (type == 1) {
                ds.writeByte(0); //null
                ds.writeUTF(numWin);
                ds.writeUTF(finish);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void search(List<Clan> clans) {
        try {
            Message mss = new Message(Cmd.CLAN_SEARCH);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(clans.size());
            for (Clan clan : clans) {
                ds.writeInt(clan.id);
                ds.writeUTF(clan.name);
                ds.writeUTF(clan.slogan);
                ds.writeByte(clan.imgID);
                ds.writeUTF(Utils.formatNumber(clan.powerPoint));
                ds.writeUTF(clan.leaderName);
                ds.writeByte(clan.getNumberMember());
                ds.writeByte(clan.maxMember);
                ds.writeUTF(clan.getStrCreateTime());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void transport(int maxTime, int type) {
        try {
            Message mss = new Message(Cmd.TRANSPORT);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(maxTime);
            ds.writeByte(type);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void addTeleport(int id, byte type) {
        try {
            Message mss = new Message(Cmd.TELEPORT);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.writeByte(type);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendValidDll() {
        try {
            Message msg = messageNotLogin(3);
            Server server = DragonBall.getInstance().getServer();
            List<String> data = server.isValidDll;
            msg.writer().writeInt(data.size());
            for (var strs : data) {
                msg.writer().writeUTF(strs);
            }
            this.session.sendMessage(msg);
            msg.writer().flush();
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clanImage(Message ms) {
        try {
            Server server = DragonBall.getInstance().getServer();
            byte id = ms.reader().readByte();
            ClanImage clanImage = server.getClanImageByID(id);
            if (clanImage != null) {
                Message mss = new Message(Cmd.CLAN_IMAGE);
                FastDataOutputStream ds = mss.writer();
                ds.writeByte(clanImage.id);
                ds.writeByte(clanImage.idImages.length);
                for (short smallID : clanImage.idImages) {
                    ds.writeShort(smallID);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void combine(byte type, Combine combine, short icon1, short icon2) {
        try {
            Message mss = new Message(Cmd.COMBINNE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            if (type == 0) {
                ds.writeUTF(combine.getInfo());
                ds.writeUTF(combine.getInfo2());
            }
            if (type == 1) {
                ArrayList<Byte> itemCombines = combine.getItemCombine();
                ds.writeByte(itemCombines.size());
                for (int index : itemCombines) {
                    ds.writeByte(index);
                }
            }
            if (type >= 4) {
                ds.writeShort(icon1);
            }
            if (type == 6) {
                ds.writeShort(icon2);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerVsPlayer(byte type, int playerId, int gold, String info) {
        try {
            Message mss = new Message(Cmd.PLAYER_VS_PLAYER);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeInt(playerId);
            ds.writeInt(gold);
            ds.writeUTF(info);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void magicTree(byte action, MagicTree tree) {
        try {
            Message mss = new Message(Cmd.MAGIC_TREE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(action);
            if (action == 0) {
                ds.writeShort(tree.id);
                ds.writeUTF(tree.name);
                ds.writeShort(tree.x);
                ds.writeShort(tree.y);
                ds.writeByte(tree.level);
                ds.writeShort(tree.currPeas);
                ds.writeShort(tree.maxPeas);
                ds.writeUTF("");
                ds.writeInt(tree.seconds);
                ds.writeByte(tree.currPeas);
                for (int i = 0; i < tree.currPeas; i++) {
                    int x = (i % 2 == 0) ? 2 : 18;
                    int y = 11 + (i * 4);
                    if (tree.level >= 7) {
                        if (i % 5 == 0) {
                            x = 18;
                        } else if (i % 2 == 0) {
                            x = 2;
                        } else {
                            x = 34;
                        }
                    }
                    ds.writeByte(x);
                    ds.writeByte(y);
                }
                ds.writeBoolean(tree.isUpgrade);
            }
            if (action == 2) {
                ds.writeShort(tree.currPeas);
                ds.writeInt(tree.seconds);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setClientType(byte type) {
        try {
            Message mss = new Message(Cmd.SET_CLIENTTYPE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerRemove(Player _player) {
        try {
            Message mss = new Message(Cmd.PLAYER_REMOVE);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void chat(Player _player, String text) {

        try {
            Message mss = new Message(Cmd.CHAT_MAP);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void startYesNoDlg(byte type, String name, String key) {
        try {
            Message mss = new Message(-98);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            if (type == 0) {
                ds.writeUTF(name);
                ds.writeUTF(key);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void addCuuSat(int id) {
        try {
            Message mss = new Message(Cmd.ADD_CUU_SAT);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("meCuuSat err", ex);
        }
    }

    public void meCuuSat(int id) {
        try {
            Message mss = new Message(Cmd.ME_CUU_SAT);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("meCuuSat err", ex);
        }
    }

    public void clearCuuSat(int id) {
        try {
            Message mss = new Message(Cmd.CLEAR_CUU_SAT);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("meCuuSat err", ex);
        }
    }

    public void enemyAction(byte action) {
        try {
            Message ms = new Message(Cmd.ENEMY_LIST);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(action);
            if (action == 0) {
                ds.writeByte(player.enemies.size());
                for (Friend friend : player.enemies) {
                    Player _player = SessionManager.findChar(friend.id);
                    if (_player != null) {
                        friend.head = _player.getHead();
                        friend.body = _player.getBody();
                        friend.bag = _player.getBag();
                        friend.leg = _player.getLeg();
                        friend.power = _player.info.power;
                        friend.isOnline = true;
                    } else {
                        friend.isOnline = false;
                    }
                    ds.writeInt(friend.id);
                    ds.writeShort(friend.head);
                    ds.writeShort(friend.body);
                    ds.writeShort(friend.leg);
                    ds.writeShort(friend.bag);
                    ds.writeUTF(friend.name);
                    ds.writeUTF(Utils.formatNumber(friend.power));
                    ds.writeBoolean(friend.isOnline);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("enemy action", ex);
        }
    }

    public void setPowerInfo(PowerInfo p) {
        try {
            Message mss = new Message(Cmd.POWER_INFO);
            FastDataOutputStream ds = mss.writer();
            ds.writeUTF(p.getInfo());
            ds.writeShort(p.getPoint());
            ds.writeShort(p.getMaxPoint());
            ds.writeShort(p.getSeconds());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void useItem(byte type, byte where, byte index, String info) {
        try {
            Message mss = new Message(Cmd.USE_ITEM);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeByte(where);
            ds.writeByte(index);
            ds.writeUTF(info);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void npcHide(byte npcId, boolean isHide) {
        try {
            Message mss = new Message(Cmd.NPC_ADD_REMOVE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(npcId);
            ds.writeByte(isHide ? 0 : 1);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadHP(Player _player, byte type) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_LOAD_HP);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeByte(type);
            ds.writeLong(_player.info.hpFull);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadLevel(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_LOAD_LEVEL);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeLong(_player.info.hpFull);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadWeapon(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_LOAD_VUKHI);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeLong(_player.info.hpFull);
            ds.writeShort(_player.getEff5buffhp());
            ds.writeShort(_player.getEff5buffmp());
            ds.writeShort(_player.getWp());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadAo(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_LOAD_AO);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeLong(_player.info.hpFull);
            ds.writeShort(_player.getEff5buffhp());
            ds.writeShort(_player.getEff5buffmp());
            ds.writeShort(_player.getBody());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadQuan(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_LOAD_QUAN);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeLong(_player.info.hpFull);
            ds.writeShort(_player.getEff5buffhp());
            ds.writeShort(_player.getEff5buffmp());
            ds.writeShort(_player.getLeg());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadBody(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_LOAD_BODY);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeLong(_player.info.hpFull);
            ds.writeShort(_player.getEff5buffhp());
            ds.writeShort(_player.getEff5buffmp());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void move(Player _player) {
        try {
            Message mss = new Message(Cmd.PLAYER_MOVE);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeShort(_player.getX());
            ds.writeShort(_player.getY());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerSpeed(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.PLAYER_SPEED);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeByte(_player.info.speed);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public static void writeCharInfo(Message ms, Player _player) {
        try {
            String name = _player.getName();
            if (_player.isDisciple()) {
                name = "$" + name;
            } else if (_player.isMiniDisciple()) {
                name = "#" + name;
            }
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(_player.info.level);
            ds.writeBoolean(_player.isInvisible());
            ds.writeByte(_player.typePk);
            ds.writeByte(_player.classId);
            ds.writeByte(_player.gender);
            ds.writeShort(_player.getHead());
            ds.writeUTF(name);
            ds.writeLong(_player.info.hp);
            ds.writeLong(_player.info.hpFull);
            ds.writeShort(_player.getBody());
            ds.writeShort(_player.getLeg());
            ds.writeByte(_player.bag);
            ds.writeByte(0);
            ds.writeShort(_player.getX());
            ds.writeShort(_player.getY());
            ds.writeShort(_player.getEff5buffhp());
            ds.writeShort(_player.getEff5buffmp());
            ds.writeByte(_player.effects.size());
            for (EffectChar eff : _player.effects) {
                ds.writeByte(eff.template.id);
                ds.writeInt(eff.timeStart);
                ds.writeInt(eff.timeLenght);
                ds.writeShort(eff.param);
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void petInfo(byte type) {
        try {
            Message mss = new Message(Cmd.DISCIPLE_INFO);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            if (type == 2) {
                player.myDisciple.info.applyCharLevelPercent();
                ds.writeShort(player.myDisciple.getHead());
                ds.writeByte(player.myDisciple.itemBody.length);
                for (Item item : player.myDisciple.itemBody) {
                    if (item != null) {
                        ds.writeShort(item.id);
                        ds.writeInt(item.quantity);
                        ds.writeUTF(item.info);
                        ds.writeUTF(item.content);
                        ArrayList<ItemOption> options = item.getDisplayOptions();
                        ds.writeByte(options.size());
                        for (ItemOption option : options) {
                            int[] format = option.format();
                            ds.writeShort(format[0]);
                            ds.writeInt(format[1]);
                        }
                    } else {
                        ds.writeShort(-1);
                    }
                }
                ds.writeLong(player.myDisciple.info.hp);
                ds.writeLong(player.myDisciple.info.hpFull);
                ds.writeLong(player.myDisciple.info.mp);
                ds.writeLong(player.myDisciple.info.mpFull);
                ds.writeLong(player.myDisciple.info.damageFull);

                String name = player.myDisciple.getName();

                ds.writeUTF("$" + name);
                ds.writeUTF(player.myDisciple.info.getStrLevel());
                ds.writeLong(player.myDisciple.info.power);
                ds.writeLong(player.myDisciple.info.potential);
                ds.writeByte(player.myDisciple.discipleStatus);
                ds.writeByte(player.myDisciple.petBonus);
                ds.writeShort(player.myDisciple.info.stamina);
                ds.writeShort(player.myDisciple.info.maxStamina);
                ds.writeByte(player.myDisciple.info.criticalFull);
                ds.writeShort(player.myDisciple.info.defenseFull);
                ArrayList<KeyValue> skillInfos = player.myDisciple.getInfoSkill();
                ds.writeByte(skillInfos.size());
                for (KeyValue<Short, String> keyValue : skillInfos) {
                    short skillId = keyValue.key;
                    String moreInfo = keyValue.value;
                    ds.writeShort(skillId);
                    if (skillId == -1) {
                        ds.writeUTF(moreInfo);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setStamina() {
        try {
            Message mss = new Message(Cmd.STAMINA);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(player.info.stamina);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setMaxStamina() {
        try {
            Message mss = new Message(Cmd.MAXSTAMINA);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(player.info.maxStamina);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void addGold(long gold) {
        try {
            Message mss = new Message(Cmd.ME_UP_COIN_BAG);
            FastDataOutputStream ds = mss.writer();
            ds.writeLong(gold);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void addExp(byte type, long exp) {
        try {
            Message mss = new Message(Cmd.PLAYER_UP_EXP);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeLong(exp);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendMoney() {
        buy();
    }

    public void buy() {
        try {
            Message mss = new Message(Cmd.ITEM_BUY);
            FastDataOutputStream ds = mss.writer();
            ds.writeLong(player.gold);
            ds.writeInt(player.diamond);
            ds.writeInt(player.diamondLock);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void viewInfoPlayer(Player _player) {
        try {
            _player.info.applyCharLevelPercent();
            Message mss = new Message(Cmd.PLAYER_MENU);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.power);
            ds.writeUTF(_player.info.getStrLevel());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }

    }

    public void addMenuPlayer(Menu... menus) {

        try {
            Message mss = messageSubCommand(Cmd.GET_PLAYER_MENU);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(menus.length);
            for (int i = 0; i < menus.length; i++) {
                Menu menu = menus[i];
                ds.writeUTF(menu.getCaption());
                ds.writeUTF(menu.getCaption2());
                ds.writeShort(i);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }

    }

    public void loadInfo() {
        try {
            Message mss = messageSubCommand(Cmd.ME_LOAD_INFO);
            FastDataOutputStream ds = mss.writer();
            ds.writeLong(player.gold);
            ds.writeInt(player.diamond);
            ds.writeLong(player.info.hp);
            ds.writeLong(player.info.mp);
            ds.writeInt(player.diamondLock);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void loadSkill() {
        try {
            Message mss = messageSubCommand(Cmd.ME_LOAD_SKILL);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(player.skills.size());
            for (Skill skill : player.skills) {
                ds.writeShort(skill.id);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerLoadAll(Player _player) {
        try {
            Message mss = messageSubCommand(Cmd.PLAYER_LOAD_ALL);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeInt(_player.clanID);
            writeCharInfo(mss, _player);
            ds.writeShort(_player.getIdAuraEff());
            ds.writeShort(_player.getIdEffSetItem());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendDataBG() {
        try {
            Message mss = new Message(Cmd.ITEM_BACKGROUND);
            FastDataOutputStream ds = mss.writer();
            ds.write(BgItem.data);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setItemBody() {
        try {
            Message mss = new Message(Cmd.BODY);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeShort(player.getHead());
            ds.writeByte(player.itemBody.length);
            for (Item item : player.itemBody) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setItemBag() {
        try {
            Message mss = new Message(Cmd.BAG);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeByte(player.itemBag.length);
            for (Item item : player.itemBag) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateBag(int index, int quantity) {
        try {
            Message mss = new Message(Cmd.BAG);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeByte(index);
            ds.writeInt(quantity);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void showBox(boolean isClan) {
        try {
            Message mss = new Message(Cmd.BOX);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(1);
            ds.writeBoolean(isClan);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void openUIMenu(ArrayList<KeyValue> menus) {
        try {
            Message mss = new Message(Cmd.OPEN_UI_MENU);
            FastDataOutputStream ds = mss.writer();
            for (KeyValue<Integer, String> keyValue : menus) {
                ds.writeUTF(keyValue.value);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void openUINewMenu(ArrayList<KeyValue> menus) {
        try {
            Message mss = new Message(Cmd.OPEN_UI_NEWMENU);
            FastDataOutputStream ds = mss.writer();
            for (KeyValue<Integer, String> keyValue : menus) {
                ds.writeUTF(keyValue.value);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void openUISay(int npcId, String say, short avatar) {
        try {
            Message mss = new Message(Cmd.OPEN_UI_SAY);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(npcId);
            ds.writeUTF(say);
            ds.writeShort(avatar);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void openTextBox(InputDialog inputDialog) {
        try {
            Message mss = new Message(Cmd.CLIENT_INPUT);
            FastDataOutputStream ds = mss.writer();
            ds.writeUTF(inputDialog.getTitle());
            TextField[] fields = inputDialog.getFields();
            ds.writeByte(fields.length);
            for (TextField field : fields) {
                ds.writeUTF(field.getTitle());
                ds.writeByte(field.getType());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void viewShop(Shop shop) {
        try {
            Message mss = new Message(Cmd.SHOP);
            FastDataOutputStream ds = mss.writer();
            int typeShop = shop.getTypeShop();
            ds.writeByte(typeShop);
            ArrayList<Tab> tabs = shop.getTabs();
            ds.writeBoolean(shop.canBuyMore);
            ds.writeByte(tabs.size());
            int mul = 1;
            int npc = shop.getNpcId();
            if (npc == 2001) {
                mul = 4;
            } else if (npc == 2002) {
                mul = 100;
            }
            for (Tab tab : tabs) {
                ds.writeUTF(tab.getTabName());
                ArrayList<ItemTemplate> items = tab.getListItem(player);
                ds.writeByte(items.size());
                for (ItemTemplate item : items) {
                    ds.writeShort(item.id);
                    if (typeShop == 0) {
                        ds.writeLong(item.buyGold * mul);
                        ds.writeInt(item.buyGem * mul);
                    }
                    if (typeShop == 1) {
                        ds.writeLong(item.powerRequire);
                    }
                    if (typeShop == 3) {
                        ds.writeShort(item.iconSpec);
                        ds.writeInt(item.buySpec);
                    }
                    if (typeShop == 4) {
                        ds.writeUTF(item.reason);
                    }
                    if (item.type == 13) {
                        Amulet amulet = player.getAmulet(item.id);
                        ItemOption itemOption = null;
                        if (amulet != null) {
                            itemOption = amulet.getItemOption();
                        } else {
                            itemOption = new ItemOption(66, 0);
                        }
                        ds.writeByte(1);
                        int[] format = itemOption.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    } else {
                        ds.writeByte(item.options.size());
                        for (ItemOption option : item.options) {
                            int[] format = option.format();
                            ds.writeShort(format[0]);
                            ds.writeInt(format[1]);
                        }
                    }
                    ds.writeBoolean(item.isNew);
                    ds.writeBoolean(item.isPreview);
                    if (item.isPreview) {
                        short head = player.getHeadDefault();
                        short body = 1;
                        short leg = -1;
                        short bag = player.bag;
                        byte gender = player.gender;
                        Item[] itemBody = player.itemBody;
                        if (gender == 0) {
                            body = 57;
                            leg = 58;
                        } else if (gender == 1) {
                            body = 59;
                            leg = 60;
                        } else if (gender == 2) {
                            body = 57;
                            leg = 58;
                        }
                        if (itemBody[0] != null) {
                            body = itemBody[0].template.part;
                        }
                        if (itemBody[1] != null) {
                            leg = itemBody[1].template.part;
                        }
                        if (item.type == Item.TYPE_HAIR) {
                            head = item.part;
                            if (head == -1) {
                                head = item.head;
                                body = item.body;
                                leg = item.leg;
                            }
                        }
                        if (item.type == Item.TYPE_AO) {
                            body = item.part;
                        }
                        if (item.type == Item.TYPE_QUAN) {
                            leg = item.part;
                        }
                        ds.writeShort(head);
                        ds.writeShort(body);
                        ds.writeShort(leg);
                        ds.writeShort(bag);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void openUIConfirm(int npcId, String say, short avatar, ArrayList<KeyValue> menus) {
        try {
            Message mss = new Message(Cmd.OPEN_UI_CONFIRM);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(npcId);
            ds.writeUTF(say);
            ds.writeByte(menus.size());
            for (KeyValue<Integer, String> keyValue : menus) {
                ds.writeUTF(keyValue.value);
            }
            ds.writeShort(avatar);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void openUIZone() {
        try {
            if (player.zone == null) {
                return;
            }
            TMap map = player.zone.map;
            if (map.isCantChangeZone()) {
                dialogMessage("Không thể chuyển khu vực.");
                return;
            }
            Message mss = new Message(Cmd.OPEN_UI_ZONE);
            FastDataOutputStream ds = mss.writer();
            map.lock.readLock().lock();
            try {
                ds.writeByte(0);
                ds.writeByte(map.zones.size());
                for (Zone z : map.zones) {
                    ds.writeByte(z.zoneID);
                    ds.writeByte(z.getPts());
                    ds.writeByte(z.getNumPlayer());
                    ds.writeShort(z.getMaxPlayer());
                    ds.writeByte(0);
                }
            } finally {
                map.lock.readLock().unlock();
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void achievement(byte type, byte index) {
        try {
            Message ms = new Message(Cmd.ACHIEVEMENT);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(type);
            if (type == 0) {
                ds.writeByte(player.achievements.size());
                for (Achievement achive : player.achievements) {
                    ds.writeUTF(achive.getName());
                    ds.writeUTF(achive.getContent());
                    ds.writeShort(achive.getReward());
                    ds.writeBoolean(achive.isFinish());
                    ds.writeBoolean(achive.isRewarded());
                }
            }
            if (type == 1) {
                ds.writeByte(index);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateCoolDown(List<Skill> skills) {
        try {
            long now = System.currentTimeMillis();
            Message ms = new Message(Cmd.UPDATE_COOLDOWN);
            FastDataOutputStream ds = ms.writer();
            for (Skill skill : skills) {
                long time = now - skill.lastTimeUseThisSkill;
                ds.writeShort(skill.id);
                ds.writeInt((int) (skill.coolDown - time));
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setTask() {
        try {
            Message mss = new Message(Cmd.TASK_GET);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(player.taskMain.id);
            ds.writeByte(player.taskMain.index);
            ds.writeUTF(player.taskMain.name);
            ds.writeUTF(player.taskMain.detail);
            int lent = player.taskMain.subNames.length;
            ds.writeByte(lent);
            for (int i = 0; i < lent; i++) {
                ds.writeUTF(player.taskMain.subNames[i]);
                ds.writeByte(player.taskMain.tasks[i]);
                ds.writeShort(player.taskMain.mapTasks[i]);
                ds.writeUTF(player.taskMain.contents[i]);
            }
            ds.writeShort(player.taskMain.count);
            for (int i = 0; i < lent; i++) {
                ds.writeShort(player.taskMain.counts[i]);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void taskNext() {
        sendMessage(new Message(Cmd.TASK_NEXT));
    }

    public void updateTaskCount() {
        try {
            Message mss = new Message(Cmd.TASK_UPDATE);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(player.taskMain.count);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void loginDelay(int time) {
        try {
            Message mss = new Message(Cmd.LOGIN_DE);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(time);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void addBigMessage(short avatar, String chat, byte type, String p, String caption) {
        try {
            Message mss = new Message(Cmd.BIG_MESSAGE);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(avatar);
            ds.writeUTF(chat);
            ds.writeByte(type);
            if (type == 1) {
                ds.writeUTF(p);
                ds.writeUTF(caption);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void gameInfo() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message mss = new Message(Cmd.GAME_INFO);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(server.gameInfos.size());
            for (GameInfo gameInfo : server.gameInfos) {
                ds.writeShort(gameInfo.id);
                ds.writeUTF(gameInfo.title);
                ds.writeUTF(gameInfo.content);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void clanInvite(String strInvite, int clanID, int code) {
        try {
            Message mss = new Message(Cmd.CLAN_INVITE);
            FastDataOutputStream ds = mss.writer();
            ds.writeUTF(strInvite);
            ds.writeInt(clanID);
            ds.writeInt(code);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void clanInfo() {
        try {
            Message ms = new Message(Cmd.CLAN_INFO);
            FastDataOutputStream ds = ms.writer();
            ds.writeInt(player.clanID);
            if (player.clanID != -1) {
                Clan clan = player.clan;
                if (clan == null) {
                    return;
                }
                List<ClanMember> clanMembers = clan.getMembers();
                ds.writeUTF(clan.name);
                ds.writeUTF(clan.slogan);
                ds.writeByte(clan.imgID);
                ds.writeUTF(Utils.formatNumber(clan.powerPoint));
                ds.writeUTF(clan.leaderName);
                ds.writeByte(clan.getNumberMember());
                ds.writeByte(clan.maxMember);
                ds.writeByte(clan.getMember(player.id).role);
                ds.writeInt(clan.clanPoint);
                ds.writeByte(clan.level);
                for (ClanMember mem : clanMembers) {
                    ds.writeInt(mem.playerID);
                    ds.writeShort(mem.head);
                    ds.writeShort(mem.leg);
                    ds.writeShort(mem.body);
                    ds.writeUTF(mem.name);
                    ds.writeByte(mem.role);
                    ds.writeUTF(Utils.formatNumber(mem.powerPoint));
                    ds.writeInt(mem.donate);
                    ds.writeInt(mem.receiveDonate);
                    ds.writeInt(mem.clanPoint);
                    ds.writeInt(mem.currClanPoint);
                    ds.writeUTF(mem.getStrJoinTime());
                }
                ds.writeByte(clan.messages.size());
                for (ClanMessage message : clan.messages) {
                    writeClanMessage(ds, message);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void clanMessage(ClanMessage message) {
        try {
            Message ms = new Message(Cmd.CLAN_MESSAGE);
            FastDataOutputStream ds = ms.writer();
            writeClanMessage(ds, message);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void writeClanMessage(FastDataOutputStream ds, ClanMessage message) throws IOException {
        ds.writeByte(message.type);
        ds.writeInt(message.id);
        ds.writeInt(message.playerId);
        ds.writeUTF(message.playerName);
        ds.writeByte(message.role);
        ds.writeInt(message.time);
        if (message.type == 0) {
            ds.writeUTF(message.chat);
            ds.writeByte(message.color);
        } else if (message.type == 1) {
            ds.writeByte(message.receive);
            ds.writeByte(message.maxCap);
            ds.writeBoolean(message.isNewMessage);
        }
    }

    public void clanMember(Clan clan) {
        try {
            List<ClanMember> clanMembers = clan.getMembers();
            Message ms = new Message(Cmd.CLAN_MEMBER);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(clanMembers.size());
            for (ClanMember mem : clanMembers) {
                ds.writeInt(mem.playerID);
                ds.writeShort(mem.head);
                ds.writeShort(mem.leg);
                ds.writeShort(mem.body);
                ds.writeUTF(mem.name);
                ds.writeByte(mem.role);
                ds.writeUTF(Utils.formatNumber(mem.powerPoint));
                ds.writeInt(mem.donate);
                ds.writeInt(mem.receiveDonate);
                ds.writeInt(mem.clanPoint);
                ds.writeUTF(mem.getStrJoinTime());
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void createClanInfo(byte action) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message ms = new Message(Cmd.CLAN_CREATE_INFO);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(action);
            if (action == 1 || action == 3) {
                ArrayList<ClanImage> list = server.getListClanImageCanBuy();
                ds.writeByte(list.size());
                for (ClanImage clanImage : list) {
                    ds.writeByte(clanImage.id);
                    ds.writeUTF(clanImage.name);
                    ds.writeInt(clanImage.gold);
                    ds.writeInt(clanImage.gem);
                }
            }
            if (action == 4) {
                ds.writeByte(player.clan.imgID);
                ds.writeUTF(player.clan.slogan);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateActivePoint() {
        try {
            Message ms = new Message(Cmd.UPDATE_ACTIVEPOINT);
            FastDataOutputStream ds = ms.writer();
            ds.writeInt(player.info.activePoint);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void changeOnSkill(byte[] data) {
        try {
            Message ms = new Message(Cmd.CHANGE_ONSKILL);
            FastDataOutputStream ds = ms.writer();
            ds.write(data);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void chatVip(String text) {
        try {
            Message mss = new Message(Cmd.CHAT_VIP);
            FastDataOutputStream ds = mss.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void getBag(Message ms) {
        try {
            Server server = DragonBall.getInstance().getServer();
            byte id = ms.reader().readByte();
            ClanImage clanImage = server.getClanImageByID(id);
            if (clanImage != null) {
                Message mss = new Message(Cmd.GET_BAG);
                FastDataOutputStream ds = mss.writer();
                ds.writeByte(id);
                int lent = clanImage.idImages.length;
                ds.writeByte(lent - 1);
                for (int i = 1; i < lent; i++) {
                    ds.writeShort(clanImage.idImages[i]);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            
            logger.error("get bag err", ex);
        }
    }

    public void refreshItem(byte type, Item item) {
        try {
            Message mss = new Message(Cmd.REFRESH_ITEM);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeByte(item.indexUI);
            ds.writeShort(item.id);
            ds.writeInt(item.quantity);
            ds.writeUTF(item.info);
            ds.writeUTF(item.content);
            ArrayList<ItemOption> options = item.getDisplayOptions();
            ds.writeByte(options.size());
            for (ItemOption option : options) {
                int[] format = option.format();
                ds.writeShort(format[0]);
                ds.writeInt(format[1]);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("refresh item", ex);
        }
    }

    public void setItemBox() {
        try {
            Message mss = new Message(Cmd.BOX);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeByte(player.itemBox.length);
            for (Item item : player.itemBox) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setItemClan() {
        try {
            Message mss = new Message(Cmd.BOX);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(0);
            Item[] items = player.clan.items;
            ds.writeByte(items.length);
            for (Item item : items) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateBox(int index, int quantity) {
        try {
            Message mss = new Message(Cmd.BOX);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeByte(index);
            ds.writeInt(quantity);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setTileSet() {
        try {
            Message mss = new Message(Cmd.TILE_SET);
            FastDataOutputStream ds = mss.writer();
            int lent = TMap.tileIndex.length;
            ds.writeByte(lent);
            for (int i = 0; i < lent; i++) {
                int lent2 = TMap.tileIndex[i].length;
                ds.writeByte(lent2);
                for (int j = 0; j < lent2; j++) {
                    ds.writeInt(TMap.tileType[i][j]);
                    int lent3 = TMap.tileIndex[i][j].length;
                    ds.writeByte(lent3);
                    for (int k = 0; k < lent3; k++) {
                        ds.writeByte(TMap.tileIndex[i][j][k]);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void getImgByName(Message ms) {
        try {
            String imgName = ms.reader().readUTF();
            ImgByName img = ImgByName.getMount(imgName);
            if (img != null) {
                try {
                    Message mss = new Message(Cmd.GET_IMG_BY_NAME);
                    FastDataOutputStream ds = mss.writer();
                    ds.writeUTF(Utils.cutPng(img.filename));
                    ds.writeByte(img.nFrame);
                    byte[] ab = img.imageData[session.zoomLevel - 1];
                    if (ab == null) {
                        //System.err.println("Not find image by name :" + imgName);
                        return;
                    }
                    ds.writeInt(ab.length);
                    ds.write(ab);
                    ds.flush();
                    sendMessage(mss);
                    mss.cleanup();
                } catch (Exception e) {
                    //System.err.println("Error img by name at : " + imgName);
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void requestEffectData(Message ms) {
        try {
            short id = ms.reader().readShort();
            EffectData effData = Effect.getEfDataById(id);
            if (effData != null) {
                Message mss = new Message(Cmd.GET_EFFDATA);
                FastDataOutputStream ds = mss.writer();
                ds.writeShort(effData.ID);
                ds.writeInt(effData.data.length);
                ds.write(effData.data);
                ds.writeByte(0);
                byte[] ab = effData.img[session.zoomLevel - 1];
                ds.writeInt(ab.length);
                ds.write(ab);
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (Exception ex) {
            
            logger.error("requestEffectData", ex);
        }
    }

    public void loadPoint() {
        try {
            Message mss = new Message(Cmd.ME_LOAD_POINT);
            FastDataOutputStream ds = mss.writer();
            ds.writeLong(player.info.originalHP);
            ds.writeLong(player.info.originalMP);
            ds.writeLong(player.info.originalDamage);
            ds.writeLong(player.info.hpFull);
            ds.writeLong(player.info.mpFull);
            ds.writeLong(player.info.hp);
            ds.writeLong(player.info.mp);
            ds.writeByte(player.info.speed);
            ds.writeByte(Info.HP_FROM_1000_TIEM_NANG);
            ds.writeByte(Info.MP_FROM_1000_TIEM_NANG);
            ds.writeByte(Info.DAMAGE_FROM_1000_TIEM_NANG);
            ds.writeLong(player.info.damageFull);
            ds.writeInt(player.info.defenseFull);
            ds.writeByte(player.info.criticalFull);
            ds.writeLong(player.info.potential);
            ds.writeShort(Info.EXP_FOR_ONE_ADD);
            ds.writeShort(player.info.originalDefense);
            ds.writeByte(player.info.originalCritical);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void viewCollectionBook() {
        try {
            Message mss = new Message(Cmd.RADA_CARD);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(0);
            List<Card> cards = player.getCards();
            ds.writeShort(cards.size());
            for (Card card : cards) {
                CardTemplate cardT = card.template;
                ds.writeShort(card.id);
                ds.writeShort(cardT.icon);
                ds.writeByte(cardT.rank);
                ds.writeByte(card.amount);
                ds.writeByte(cardT.max_amount);
                ds.writeByte(cardT.type);
                if (cardT.type == 0) {
                    ds.writeShort(cardT.templateID);
                } else {
                    ds.writeShort(cardT.head);
                    ds.writeShort(cardT.body);
                    ds.writeShort(cardT.leg);
                    ds.writeShort(cardT.bag);
                }
                ds.writeUTF(cardT.name);
                ds.writeUTF(cardT.info);
                ds.writeByte(card.level);
                ds.writeBoolean(card.isUse);
                ds.writeByte(cardT.options.size());
                for (ItemOption option : cardT.options) {
                    int[] format = option.format();
                    ds.writeShort(format[0]);
                    ds.writeInt(format[1]);
                    ds.writeByte(option.activeCard);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void useCard(int id, boolean isUse) {
        try {
            Message mss = new Message(Cmd.RADA_CARD);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(1);
            ds.writeShort(id);
            ds.writeBoolean(isUse);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setCardLevel(int id, int level) {
        try {
            Message mss = new Message(Cmd.RADA_CARD);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeShort(id);
            ds.writeByte(level);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setCardExp(int id, int amount, int maxAmount) {
        try {
            Message mss = new Message(Cmd.RADA_CARD);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(3);
            ds.writeShort(id);
            ds.writeByte(amount);
            ds.writeByte(maxAmount);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setIDAuraEff(int id, int effID) {
        try {
            Message mss = new Message(Cmd.RADA_CARD);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(4);
            ds.writeInt(id);
            ds.writeShort(effID);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void loadHP() {
        try {
            Message mss = messageSubCommand(Cmd.ME_LOAD_HP);
            FastDataOutputStream ds = mss.writer();
            ds.writeLong(player.info.hp);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void loadMP() {
        try {
            Message mss = messageSubCommand(Cmd.ME_LOAD_MP);
            FastDataOutputStream ds = mss.writer();
            ds.writeLong(player.info.mp);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void loadAll() {
        try {
            Server server = DragonBall.getInstance().getServer();
            String name = player.getName();
            if (player.isDisciple()) {
                name = "$" + name;
            } else if (player.isMiniDisciple()) {
                name = "#" + name;
            }
            Message mss = messageSubCommand(Cmd.ME_LOAD_ALL);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(player.id);
            ds.writeByte(player.taskMain.id);
            ds.writeByte(player.gender);
            ds.writeShort(player.getHead());
            ds.writeUTF(name);
            ds.writeByte(player.pointPk);
            ds.writeByte(player.typePk);
            ds.writeLong(player.info.power);
            ds.writeShort(player.getEff5buffhp());
            ds.writeShort(player.getEff5buffmp());
            ds.writeByte(player.classId);//class
            ds.writeByte(player.skills.size());
            for (Skill skill : player.skills) {
                ds.writeShort(skill.id);
            }
            ds.writeLong(player.gold);
            ds.writeInt(player.diamondLock);
            ds.writeInt(player.diamond);

            ds.writeByte(player.itemBody.length);
            for (Item item : player.itemBody) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.writeByte(player.itemBag.length);
            for (Item item : player.itemBag) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());// so cs
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.writeByte(player.itemBox.length);
            for (Item item : player.itemBox) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }

            ds.writeShort(server.idHead.length);
            for (int i = 0; i < server.idHead.length; i++) {
                ds.writeShort(server.idHead[i]);
                ds.writeShort(server.idAvatar[i]);
            }
            int[] pet = PET[player.classId];
            ds.writeShort(pet[0]);
            ds.writeShort(pet[1]);
            ds.writeShort(pet[2]);
            ds.writeBoolean(player.isNhapThe());
            ds.writeInt(player.deltaTime);
            ds.writeBoolean(player.isNewMember());
            ds.writeShort(player.getIdAuraEff());
            ds.writeByte(-1);
            ds.writeShort(player.getIdEffSetItem());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();

        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void npcChat(short npcID, String text) {
        try {
            Message mss = new Message(Cmd.NPC_CHAT);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(npcID);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setItemTime(ItemTime item) {
        try {
            Message mss = new Message(Cmd.ITEM_TIME);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(item.icon);
            int seconds = item.seconds;
            if (seconds > Integer.MAX_VALUE) {
                seconds = Integer.MAX_VALUE;
            }
            ds.writeInt(seconds);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateBag(Player _player) {
        try {
            Message mss = new Message(Cmd.UPDATE_BAG);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeByte(_player.bag);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateCaption(Message ms) {
        try {
            byte planet = ms.reader().readByte();
            ArrayList<String> captions = Caption.getCaption(planet);
            if (captions != null) {
                Message mss = new Message(Cmd.UPDATE_CAPTION);
                FastDataOutputStream ds = mss.writer();
                ds.writeByte(captions.size());
                for (String cap : captions) {
                    ds.writeUTF(cap);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void createChar() {
        sendMessage(new Message(Cmd.CREATE_PLAYER));
    }

    public void requestMapTemplate(Message ms) {
        try {

            int map = ms.reader().readUnsignedByte();
            requestMapTemplate(map);
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void requestMapTemplate2(int mapId) {
        try {
            TMap map = MapManager.getInstance().getMap(mapId);
            byte[] ab = map.mapData;
            if (ab != null) {
                Message mss = messageNotMap(11);
                FastDataOutputStream ds = mss.writer();
                ds.writeInt(mapId);
                ds.write(ab);
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void requestMapTemplate(int mapId) {
        Utils.setTimeout(()
                -> {
            try {
                TMap map = MapManager.getInstance().getMap(mapId);
                byte[] ab = map.mapData;
                Message mss = messageNotMap(Cmd.REQUEST_MAPTEMPLATE);
                FastDataOutputStream ds = mss.writer();
                ds.write(ab);
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, 50);
    }

    public void requestIcon(int id) {
        try {
            if (id < 0 || id >= small.length) {
                String name = (player != null && player.name != null) ? player.name : 
                             (session != null && session.user != null) ? session.user.getUsername() : "Unknown";
                //System.err.println("_SmallIcon -> Thiếu Icon ID: " + id + " | " + name + " | Zoom " + session.zoomLevel);
                return;
            }
            byte[] ab = Utils.getFile(String.format("resources/image/%d/small/Small%d.png", session.zoomLevel, id));
            if (ab == null) {
                String name = (player != null && player.name != null) ? player.name : 
                             (session != null && session.user != null) ? session.user.getUsername() : "Unknown";
                //System.err.println("_SmallIcon -> Thiếu Icon ID: " + id + " | " + name + " | Zoom " + session.zoomLevel);
                return;
            }
            Message mss = new Message(Cmd.REQUEST_ICON);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
            //System.err.println("requestIcon " + id);
        } catch (IOException ex) {
            
            logger.error("request icon error", ex);
        }
    }

    public void requestIcon(Message ms) {
        try {
            int id = ms.reader().readInt();
            if (id < 0 || id >= small.length) {
                String name = (player != null && player.name != null) ? player.name : 
                             (session != null && session.user != null) ? session.user.getUsername() : "Unknown";
                //System.err.println("_SmallIcon -> Thiếu Icon ID: " + id + " | " + name + " | Zoom " + session.zoomLevel);
                return;
            }
            byte[] ab = Utils.getFile(String.format("resources/image/%d/small/Small%d.png", session.zoomLevel, id));
            if (ab == null) {
                String name = (player != null && player.name != null) ? player.name : 
                             (session != null && session.user != null) ? session.user.getUsername() : "Unknown";
                //System.err.println("_SmallIcon -> Thiếu Icon ID: " + id + " | " + name + " | Zoom " + session.zoomLevel);
                return;
            }
            Message mss = new Message(Cmd.REQUEST_ICON);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("request icon error", ex);
        }
    }

    public void requestBackgroundItem(int id) {
        try {
            if (id < 0 || id >= bg.length) {
                return;
            }
            byte[] ab = Utils.getFile(String.format("resources/image/%d/background/%d.png", session.zoomLevel, id));
            if (ab == null) {
                return;
            }
            Message mss = new Message(Cmd.BACKGROUND_TEMPLATE);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void requestBackgroundItem(Message ms) {
        try {
            int id = ms.reader().readShort();
            if (id < 0 || id >= bg.length) {
                return;
            }
            
             String backgroundFolder = "background";
            if (_HunrProvision.ConfigStudio.MODE_MAP_NOEL) {
                backgroundFolder = "background_noel";
            } else if (_HunrProvision.ConfigStudio.MODE_MAP_TET) {
                backgroundFolder = "background_newyear";
            }
            
            byte[] ab = Utils.getFile(String.format("resources/image/%d/%s/%d.png", session.zoomLevel, backgroundFolder, id));
            if (ab == null) {
                return;
            }
            Message mss = new Message(Cmd.BACKGROUND_TEMPLATE);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public static byte[] readFile(String url) {
        try {
            byte[] ab = null;
            FileInputStream fis = new FileInputStream(url);
            ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch (IOException e) {
            
            logger.error("failed!", e);
        }
        return null;
    }

    public void requestMobTemplate(int id) {
        Message msg;
        try {
            final byte[] mob = readFile("data/mob/x" + session.zoomLevel + "/" + id);
            msg = new Message(11);
            msg.writer().writeByte(id);
            msg.writer().write(mob);
            sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            
            logger.error("failed!", e);
        }
    }

    public void requestMobTemplate(Message ms) {
        try {
            int id = ms.reader().readByte();
            MobTemplate tm = Mob.getMobTemplate(id);
            if (tm.mobTemplateId == 70 || tm.mobTemplateId >= 80) {
                requestMobTemplate(tm.mobTemplateId);
                return;
            }
            if (tm != null && tm.isData) {
                byte[] data = tm.data;
                byte[] img = tm.img[session.zoomLevel - 1];
                Message mss = new Message(Cmd.REQUEST_NPCTEMPLATE);
                FastDataOutputStream ds = mss.writer();
                ds.writeByte(tm.mobTemplateId);
                ds.writeByte(tm.new1);
                ds.writeInt(data.length);
                ds.write(data);
                ds.writeInt(img.length);
                ds.write(img);
                if (tm.dataBoss != null) {
                    ds.writeByte(1);
                    ds.write(tm.dataBoss);
                } else {
                    ds.writeByte(0);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateData() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            Message ms = new Message(Cmd.UPDATE_DATA);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(config.getDataVersion());
            ds.writeInt(server.CACHE_DART.length);
            ds.write(server.CACHE_DART);
            ds.writeInt(server.CACHE_ARROW.length);
            ds.write(server.CACHE_ARROW);
            ds.writeInt(server.CACHE_EFFECT.length);
            ds.write(server.CACHE_EFFECT);
            ds.writeInt(server.CACHE_IMAGE.length);
            ds.write(server.CACHE_IMAGE);
            ds.writeInt(server.CACHE_PART.length);
            ds.write(server.CACHE_PART);
            ds.writeInt(server.CACHE_SKILL.length);
            ds.write(server.CACHE_SKILL);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void autoPlay(boolean canAutoPlay) {
        try {
            Message ms = new Message(Cmd.AUTOPLAY);
            FastDataOutputStream ds = ms.writer();
            ds.writeBoolean(canAutoPlay);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateMap() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message ms = messageNotMap(Cmd.UPDATE_MAP);
            FastDataOutputStream ds = ms.writer();
            ds.write(server.CACHE_MAP);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateSkill() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message ms = messageNotMap(Cmd.UPDATE_SKILL);
            FastDataOutputStream ds = ms.writer();
            ds.write(server.CACHE_SKILL_TEMPLATE);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateItem(byte type) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            Message ms = messageNotMap(Cmd.UPDATE_ITEM);
            FastDataOutputStream ds = ms.writer();
            if (type != 100) {
                ds.write(server.CACHE_ITEM[type]);
            } else {
                byte newVItem = (byte) Math.abs((server.iOptionTemplates.size() + server.iTemplates.size() + config.getItemVersion() + Server.arrHead.length));
                ds.writeByte(newVItem);
                ds.writeByte(100);
                ds.writeShort(Server.arrHead.length);
                for (int[] arrHead : Server.arrHead) {
                    ds.writeByte(arrHead.length);
                    for (int j = 0; j < arrHead.length; j++) {
                        ds.writeShort(arrHead[j]);
                    }
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendVersion() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();

            byte newVItem = (byte) Math.abs((server.iOptionTemplates.size() + server.iTemplates.size() + config.getItemVersion() + Server.arrHead.length));

            Message mss = messageNotMap(Cmd.UPDATE_VERSION);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(config.getDataVersion());
            ds.writeByte(config.getMapVersion());
            ds.writeByte(config.getSkillVersion());
            ds.writeByte(newVItem);
            ds.writeByte(server.powers.size());
            for (long sm : server.powers) {
                ds.writeLong(sm);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendSmallVersion() {
        try {
            Message mss = new Message(Cmd.SMALLIMAGE_VERSION);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(small.length);
            for (byte ver : small) {
                ds.writeByte(ver);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendBGSmallVersion() {
        try {
            Message mss = new Message(Cmd.BGITEM_VERSION);
            FastDataOutputStream ds = mss.writer();
            ds.writeShort(bg.length);
            for (byte ver : bg) {
                ds.writeByte(ver);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendResVersion() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeInt(server.resVersion[session.zoomLevel - 1]);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void giaoDich(Player _player, byte type, int index) {
        try {
            Message mss = new Message(Cmd.GIAO_DICH);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            if (type == 0 || type == 1) {
                ds.writeInt(_player.id);
            }
            if (type == 2) {
                ds.writeByte(index);
            }
            if (type == 6) {
                ds.writeInt(_player.goldTrading);
                ds.writeByte(_player.itemsTrading.size());
                for (KeyValue<Byte, Integer> keyValue : _player.itemsTrading) {
                    Item item = _player.itemBag[keyValue.key];
                    int quantity = keyValue.value;
                    ds.writeShort(item.id);
                    ds.writeInt(quantity);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void batchDownloadAction2(ArrayList<String> paths) {
        try {
            //System.err.println("Starting batch download (action 2) for " + paths.size() + " files");
            long startTime = System.currentTimeMillis();

            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2); // Vẫn sử dụng action 2

            // Ghi số lượng files đầu tiên
            ds.writeInt(paths.size());

            long totalSize = 0;
            int processedFiles = 0;

            for (String path : paths) {
                try {
                    String str = path.replace("\\", "/").replace("resources/data/" + session.zoomLevel, "");
                    str = Utils.cutPng(str);

                    byte[] raw = Utils.getFile(path);
                    if (raw != null) {
                        ds.writeUTF(str);           // Tên file
                        ds.writeInt(raw.length);    // Kích thước
                        ds.write(raw);              // Nội dung file
                        totalSize += raw.length;
                        processedFiles++;
                        if (processedFiles % 100 == 0) {
                            //System.err.println("Processed " + processedFiles + "/" + paths.size() + " files");
                        }
                    } else {
                        //System.err.println("Warning: Could not read file: " + path);
                        // Ghi file rỗng
                        ds.writeUTF(str);
                        ds.writeInt(0);
                    }
                } catch (Exception ex) {
                    //System.err.println("Error processing file " + path + ": " + ex.getMessage());
                    // Skip file bị lỗi, ghi file rỗng
                    try {
                        String str = path.replace("\\", "/").replace("resources/data/" + session.zoomLevel, "");
                        str = Utils.cutPng(str);
                        ds.writeUTF(str);
                        ds.writeInt(0);
                    } catch (Exception e2) {
                        //System.err.println("Failed to write empty file entry: " + e2.getMessage());
                    }
                }
            }

            ds.flush();
            sendMessage(mss);
            mss.cleanup();

            long endTime = System.currentTimeMillis();
            //System.err.println("Batch download completed:");
            //System.err.println("- Files processed: " + processedFiles + "/" + paths.size());
            //System.err.println("- Total size: " + (totalSize / 1024) + " KB");
            //System.err.println("- Time taken: " + (endTime - startTime) + "ms");
            //System.err.println("- Average: " + (totalSize / Math.max(1, endTime - startTime)) + " bytes/ms");

        } catch (IOException ex) {
            
            logger.error("batchDownloadAction2 error", ex);
        }
    }

    public void download(String path) {
        try {
            String str = path.replace("\\", "/").replace("resources/data/" + session.zoomLevel, "");
            str = Utils.cutPng(str);
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeUTF(str);
            byte[] raw = Utils.getFile(path);
            ds.writeInt(raw.length);
            ds.write(raw);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("download error", ex);
        }
    }

    /**
     * Download file nhỏ (< 100KB) - load toàn bộ vào memory
     */
    private void downloadSmallFile(String path) {
        try {
            String str = path.replace("\\", "/").replace("resources/data/" + session.zoomLevel, "");
            str = Utils.cutPng(str);
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeUTF(str);
            byte[] raw = Utils.getFile(path);
            byte[] compressed = Utils.compress(raw);
            ds.writeInt(raw.length);
            ds.writeInt(compressed.length);
            ds.write(compressed);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("downloadSmallFile error", ex);
        }
    }

    /**
     * Download file lớn (>= 100KB) với streaming để tránh load hết vào memory
     */
    private void downloadWithStreaming(String path) {
        FileInputStream fis = null;
        try {
            String str = path.replace("\\", "/").replace("resources/data/" + session.zoomLevel, "");
            str = Utils.cutPng(str);

            java.io.File file = new java.io.File(path);
            fis = new FileInputStream(file);

            final int CHUNK_SIZE = 32 * 1024; // 32KB chunks
            byte[] buffer = new byte[CHUNK_SIZE];
            int totalSize = (int) file.length();
            int totalChunks = (totalSize + CHUNK_SIZE - 1) / CHUNK_SIZE;

            // Gửi header chunk đầu tiên
            Message headerMsg = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream headerDs = headerMsg.writer();
            headerDs.writeByte(5); // Action 5 = streaming start
            headerDs.writeUTF(str);
            headerDs.writeInt(totalSize);
            headerDs.writeShort(totalChunks);
            headerDs.flush();
            sendMessage(headerMsg);
            headerMsg.cleanup();

            // Gửi từng chunk
            int chunkIndex = 0;
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                Message chunkMsg = new Message(Cmd.GET_IMAGE_SOURCE);
                FastDataOutputStream chunkDs = chunkMsg.writer();
                chunkDs.writeByte(6); // Action 6 = streaming chunk
                chunkDs.writeShort(chunkIndex);
                chunkDs.writeInt(bytesRead);

                // Compress chunk
                byte[] chunkData = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunkData, 0, bytesRead);
                byte[] compressed = Utils.compress(chunkData);

                chunkDs.writeInt(compressed.length);
                chunkDs.write(compressed);
                chunkDs.flush();
                sendMessage(chunkMsg);
                chunkMsg.cleanup();

                chunkIndex++;
            }

        } catch (Exception ex) {
            
            logger.error("downloadWithStreaming error", ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Batch download nhiều file nhỏ trong một message để giảm overhead
     */
    public void batchDownload(ArrayList<String> paths) {
        try {
            final int BATCH_SIZE = 10; // Gom 10 file nhỏ/lần
            final int MAX_FILE_SIZE = 50 * 1024; // File > 50KB sẽ gửi riêng

            ArrayList<String> batchFiles = new ArrayList<>();

            for (String path : paths) {
                try {
                    java.io.File file = new java.io.File(path);

                    // File lớn gửi riêng
                    if (file.length() > MAX_FILE_SIZE) {
                        // Gửi batch hiện tại trước
                        if (!batchFiles.isEmpty()) {
                            sendBatchFiles(batchFiles);
                            batchFiles.clear();
                        }
                        // Gửi file lớn riêng
                        download(path);
                    } else {
                        // Thêm vào batch
                        batchFiles.add(path);

                        // Gửi batch khi đủ số lượng
                        if (batchFiles.size() >= BATCH_SIZE) {
                            sendBatchFiles(batchFiles);
                            batchFiles.clear();
                        }
                    }
                } catch (Exception ex) {
                    
                    logger.error("Error processing file: " + path, ex);
                }
            }

            // Gửi batch cuối cùng
            if (!batchFiles.isEmpty()) {
                sendBatchFiles(batchFiles);
            }

        } catch (Exception ex) {
            
            logger.error("batchDownload error", ex);
        }
    }

    /**
     * Gửi một batch file trong một message
     */
    private void sendBatchFiles(ArrayList<String> paths) {
        try {
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(4); // Action 4 = batch download
            ds.writeShort(paths.size());

            // Ghi thông tin và data của từng file
            for (String path : paths) {
                String str = path.replace("\\", "/").replace("resources/data/" + session.zoomLevel, "");
                str = Utils.cutPng(str);

                byte[] raw = Utils.getFile(path);
                byte[] compressed = Utils.compress(raw);

                ds.writeUTF(str);
                ds.writeInt(raw.length);
                ds.writeInt(compressed.length);
                ds.write(compressed);
            }

            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("sendBatchFiles error", ex);
        }
    }

    public void size(int size) {
        try {
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(1);
            ds.writeShort(size);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void downloadOk() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(3);
            ds.writeInt(server.resVersion[session.zoomLevel - 1]);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void serverMessage(String text) {
        try {
            Message ms = new Message(Cmd.SERVER_MESSAGE);
            FastDataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    long lastThongBao;
    String lastText = "";

    public void sendThongBao(String text) {
        if (lastText.equals(text)) {
            if (System.currentTimeMillis() - lastThongBao < 1000) {
                return;
            }
        }
        lastText = text;
        lastThongBao = System.currentTimeMillis();
        try {
            Message ms = new Message(Cmd.SERVER_MESSAGE);
            FastDataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void serverMessage2(String text) {
        try {
            Message ms = messageNotMap(35);
            FastDataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendConfirm() {
        try {
            // // HoangAnhDz.logError(text);
            Message ms = new Message(48);
            FastDataOutputStream ds = ms.writer();
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void dialogMessage(String text) {
        try {
//            // HoangAnhDz.logError(text);
            Message ms = new Message(Cmd.DIALOG_MESSAGE);
            FastDataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setLinkListServer() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            Message ms = messageNotLogin(Cmd.CLIENT_INFO);
            FastDataOutputStream ds = ms.writer();
            ds.writeUTF(config.getListServers());
            ds.writeInts(Player.listTypeBody);
            ds.writeLong(server.partSum);
            ds.writeLong(server.itemSum);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendMessage(Message ms) {
        if (player != null && !player.isHuman()) {
            return;
        }
        if (session != null) {
            this.session.sendMessage(ms);
        }
    }

    public static Message messageNotLogin(int command) {
        try {
            Message ms = new Message(Cmd.NOT_LOGIN);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            
            ex.printStackTrace();
        }
        return null;
    }

    public void playGuest(String username) {
        try {
            Message ms = new Message(Cmd.LOGIN2);
            FastDataOutputStream ds = ms.writer();
            ds.writeUTF(username);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public static Message messageNotMap(int command) {
        try {
            Message ms = new Message(Cmd.NOT_MAP);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            
            ex.printStackTrace();
        }
        return null;
    }

    public static Message messageSubCommand(int command) {
        try {
            Message ms = new Message(Cmd.SUB_COMMAND);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            
            ex.printStackTrace();
        }
        return null;
    }

    public void sendMessAllPlayerInMap(Zone zone, Message msg) {
        if (zone == null) {
            msg.dispose();
            return;
        }
        List<Player> players = zone.getListChar();
        List<Player> listplayer = new ArrayList<>(players);

        if (listplayer.isEmpty()) {
            msg.dispose();
            return;
        }
        for (Player pl : listplayer) {
            if (pl != null) {
                pl.service.sendMessage(msg);
            }
        }
        msg.cleanup();
    }

    public void sendPetFollow(Player player, short smallId) {
        Message msg;
        try {
            msg = new Message(31);
            msg.writer().writeInt((int) player.id);
            if (smallId == 0) {
                msg.writer().writeByte(0);
            } else {
                msg.writer().writeByte(1);
                msg.writer().writeShort(smallId);
                msg.writer().writeByte(1);
                int[] fr = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
                msg.writer().writeByte(fr.length);
                for (int i = 0; i < fr.length; i++) {
                    msg.writer().writeByte(fr[i]);
                }
                msg.writer().writeShort(smallId == 15067 ? 65 : 75);
                msg.writer().writeShort(smallId == 15067 ? 65 : 75);
            }
            sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            
            e.printStackTrace();
            System.out.println("send pet follow");
        }
    }

    public void sendPetFollow(Player player, short smallId, int frame) {
        Message msg;
        try {
            msg = new Message(31);
            msg.writer().writeInt((int) player.id);
            if (smallId == 0) {
                msg.writer().writeByte(0);
            } else {
                msg.writer().writeByte(1);
                msg.writer().writeShort(smallId);
                msg.writer().writeByte(1);
                int[] fr = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
                msg.writer().writeByte(fr.length);
                for (int i = 0; i < fr.length; i++) {
                    msg.writer().writeByte(fr[i]);
                }
                msg.writer().writeShort(smallId == 15067 ? 65 : 75);
                msg.writer().writeShort(smallId == 15067 ? 65 : 75);
            }
            sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            
            e.printStackTrace();
            System.out.println("send pet follow");
        }
    }

    public void sendPetFollowToMe(Player me, Player pl) {
        Item linhThu = pl.itemBody[11];
        if (linhThu == null) {
            return;
        }
        short smallId = (short) (linhThu.template.iconID - 1);
        Message msg;
        try {
            msg = new Message(31);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(smallId);
            msg.writer().writeByte(1);
            int[] fr = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            msg.writer().writeByte(fr.length);
            for (int i = 0; i < fr.length; i++) {
                msg.writer().writeByte(fr[i]);
            }
            msg.writer().writeShort(smallId == 15067 ? 65 : 75);
            msg.writer().writeShort(smallId == 15067 ? 65 : 75);
            me.service.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            
            e.printStackTrace();
            System.out.println("send pet follow to me");
        }
    }

    public void phaohoaFull() {
        synchronized (SessionManager.getPlayers()) {
            for (Player _player : SessionManager.getPlayers()) {
                if (_player != null && player.zone != null && player.zone.map != null && player.zone.map.mapID == 175 && player.service != null) {
                    try {
                        int x = _player.getX();
                        int y = _player.getY() + 20;
                        _player.service.serverEffect((byte) 1, (byte) 2, (byte) 62, (short) x, (short) y, (short) -1);
                        _player.service.serverEffect((byte) 1, (byte) 2, (byte) 63, (short) x, (short) y, (short) -1);
                        _player.service.serverEffect((byte) 1, (byte) 2, (byte) 64, (short) x, (short) y, (short) -1);
                        _player.service.serverEffect((byte) 1, (byte) 2, (byte) 65, (short) x, (short) y, (short) -1);
                    } catch (Exception ex) {
                        
                        java.util.logging.Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
