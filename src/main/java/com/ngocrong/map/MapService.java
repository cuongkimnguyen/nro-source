package com.ngocrong.map;

import com.ngocrong.map.tzone.Zone;
import com.ngocrong.consts.Cmd;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.model.Hold;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import com.ngocrong.skill.Skill;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MapService {

    private static Logger logger = Logger.getLogger(MapService.class);

    private static final int VIEW_DISTANCE = 1024;
    public Zone zone;

    public MapService(Zone zone) {
        this.zone = zone;
    }

    public void throwItem(Player _player, ItemMap itemMap) {
        try {
            Message ms = new Message(Cmd.ME_THROW);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(itemMap.item.indexUI);
            ds.writeShort(itemMap.id);
            ds.writeShort(itemMap.x);
            ds.writeShort(itemMap.y);
            ds.flush();
            _player.service.sendMessage(ms);
            ms.cleanup();

            ms = new Message(Cmd.PLAYER_THROW);
            ds = ms.writer();
            ds.writeInt(_player.id);
            ds.writeShort(itemMap.id);
            ds.writeShort(itemMap.item.id);
            ds.writeShort(itemMap.x);
            ds.writeShort(itemMap.y);
            ds.flush();
            sendMessage(ms, _player);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void removeItemMap(ItemMap item) {
        try {
            Message ms = new Message(Cmd.ITEMMAP_REMOVE);
            FastDataOutputStream ds = ms.writer();
            ds.writeShort(item.id);
            ds.flush();
            sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void sendPercentMabu(int p) {
        try {
            Message ms = new Message(Cmd.MABU);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(p);
            ds.flush();
            sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void attackPlayer(Player _player, long dameHit, boolean isCrit, byte eff) {
        try {
            Message msg = new Message(Cmd.HAVE_ATTACK_PLAYER);
            FastDataOutputStream ds = msg.writer();
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            ds.writeLong(dameHit);
            ds.writeBoolean(isCrit);
            ds.writeByte(eff);
            ds.flush();
            sendMessage(msg, null);
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerPickItem(ItemMap item, Player _player, String notification) {
        try {
            Message msg = new Message(Cmd.ITEMMAP_MYPICK);
            FastDataOutputStream ds = msg.writer();
            ds.writeInt(item.id);
            ds.writeUTF(notification);
            ds.writeInt(item.item.quantity);
            ds.flush();
            _player.service.sendMessage(msg);
            msg.cleanup();
            if (item.item.id != ItemName.DUA_BE) {
                msg = new Message(Cmd.ITEMMAP_PLAYERPICK);
                ds = msg.writer();
                ds.writeShort(item.id);
                ds.writeInt(_player.id);
                ds.flush();
                sendMessage(msg, _player);
            }
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void addItemMap(ItemMap item) {
        try {
            Message msg = new Message(Cmd.ITEMMAP_ADD);
            FastDataOutputStream ds = msg.writer();
            ds.writeShort(item.id);
            ds.writeShort(item.item.id);
            ds.writeShort(item.x);
            ds.writeShort(item.y);
            ds.writeInt(item.playerID);
            if (item.playerID == -2) {
                ds.writeShort(item.r);
            }
            ds.flush();
            sendMessage(msg, null);
            msg.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void attackHiduregarn(Mob mob) {
        try {

            Message mss = new Message(Cmd.MOB_HP);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(mob.getTemplateId());
            ds.writeInt(mob.mobId);
            ds.writeLong(mob.hp);
            ds.writeLong(1);
            ds.flush();
            sendMessage(mss, null);

        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void attackNpc(long damage, boolean isCrit, Mob mob, byte eff) {
        try {
            if (damage <= -1) {
                Message msg = new Message(Cmd.NPC_MISS);
                FastDataOutputStream ds = msg.writer();
                ds.writeInt(mob.mobId);
                ds.writeLong(mob.hp);
                ds.flush();
                sendMessage(msg, null);
            } else {
                Message mss = new Message(Cmd.MOB_HP);
                FastDataOutputStream ds = mss.writer();
                ds.writeInt(mob.getTemplateId());
                ds.writeInt(mob.mobId);
                ds.writeLong(mob.hp);
                ds.writeLong(damage);
                ds.writeBoolean(isCrit);
                ds.writeByte(eff);
                ds.flush();
                sendMessage(mss, null);
            }
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
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setSkillPaint_1(Mob mob, Player _player, byte skillId) {
        try {
            Message mss = new Message(Cmd.PLAYER_ATTACK_NPC);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeByte(skillId);
            ds.writeInt(mob.mobId);
            ds.flush();
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setPosition(Player _player, byte type, int x, int y) {
        try {
            _player.setX((short) x);
            _player.setY((short) y);
            Message mss = new Message(Cmd.SET_POS);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.writeByte(type);
            ds.flush();
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setPosition(Player _player, byte type) {
        try {
            Message mss = new Message(Cmd.SET_POS);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeShort(_player.getX());
            ds.writeShort(_player.getY());
            ds.writeByte(type);
            ds.flush();
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void playerSetTypePk(Player _player) {
        try {
            Message mss = Service.messageSubCommand(Cmd.UPDATE_TYPE_PK);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeByte(_player.typePk);
            ds.flush();
            sendMessage(mss, null);
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setSkillPaint_2(Player _player, ArrayList<Player> targets, byte skillId) {
        try {
            Message mss = new Message(Cmd.PLAYER_ATTACK_PLAYER);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeByte(skillId);
            ds.writeByte(targets.size());
            for (Player _t : targets) {
                ds.writeInt(_t.id);
            }
            ds.writeBoolean(false);
            ds.flush();
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
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
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void skillNotFocus(Player _player, byte type, ArrayList<Mob> mobs, ArrayList<Player> players) {
        try {
            Skill skill = _player.select;
            Message mss = new Message(Cmd.SKILL_NOT_FOCUS);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeInt(_player.id);
            if (type != 1 && type != 3) {
                ds.writeShort(skill.id);
            } else {
                ds.writeShort(-1);
            }
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
                ds.writeShort(_player.seconds);
            }
            ds.flush();
            sendMessage(mss, null);
        } catch (Exception e) {
            
            logger.error("failed!", e);
        }
    }

    public void playerLoadLive(Player _player) {
        try {
            Message mss = new Message(Cmd.RETURN_POINT_MAP);
            FastDataOutputStream ds = mss.writer();
            ds.writeInt(_player.id);
            ds.writeShort(_player.getX());
            ds.writeShort(_player.getY());
            ds.flush();
            sendMessage(mss, _player);
            mss.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void flag(Player _player) {
        try {
            Message ms = new Message(Cmd.CHANGE_FLAG);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(1);
            ds.writeInt(_player.id);
            ds.writeByte(_player.flag);
            ds.flush();
            sendMessage(ms, null);
            ms.cleanup();
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void respawn(Mob mob) {
        try {
            Message m = new Message(Cmd.NPC_LIVE);
            FastDataOutputStream ds = m.writer();
            ds.writeInt(mob.mobId);
            ds.writeByte(mob.sys);
            ds.writeByte(mob.levelBoss);
            ds.writeLong(mob.hp);
            ds.flush();
            sendMessage(m, null);
            m.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateBody(byte type, Player _player) {
        try {
            Message mss = new Message(Cmd.UPDATE_BODY);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeInt(_player.id);
            if (type != -1) {
                ds.writeShort(_player.getHead());
                ds.writeShort(_player.getBody());
                ds.writeShort(_player.getLeg());
                ds.writeBoolean(_player.isMonkey());
            }
            if (_player.danhHieu() == null) {
                ds.writeByte(0);
            } else {
                ds.writeByte(1);
                int[] imgDanhHieu = Player.GetImgDanhHieu(_player.danhHieu());
                ds.writeInt(imgDanhHieu[0]);
                ds.writeInt(imgDanhHieu[1]);
            }
            ds.flush();
            if (zone.map.isMapSingle()) {
                _player.service.sendMessage(mss);
            } else {
                sendMessage(mss, null);
            }
            mss.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void updateBag(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.updateBag(_player);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void changeBodyMob(Mob mob, byte type) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.changeBodyMob(mob, type);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void setIDAuraEff(int playerID, int effID) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.setIDAuraEff(playerID, effID);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void fusion(Player _player, byte type) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.fusion(_player, type);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void chat(Player _player, String text) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.chat(_player, text);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void npcChat(short npcID, String text) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.npcChat(npcID, text);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void mobMeUpdate(Player _player, Object target, long dame, byte skillId, byte type) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.mobMeUpdate(_player, target, dame, skillId, type);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadHP(Player _player, byte type) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerLoadHP(_player, type);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerSpeed(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerSpeed(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadAo(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerLoadAo(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadQuan(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerLoadQuan(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadWeapon(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerLoadWeapon(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadBody(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerLoadBody(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void npcHide(byte npcId, boolean isHide) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                _c.service.npcHide(npcId, isHide);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadAll(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerLoadAll(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerRemove(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerRemove(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerAdd(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.playerAdd(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void move2(Player _player) {
        if (_player.zone == null) {
            return;
        }
        TMap map = _player.zone.map;
        if (map.isCantChangeZone()) {
            return;
        }
        this.setPosition(_player, (byte) 0);
    }

    public void move(Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player && Utils.getDistance(_player.getX(), _player.getY(), _c.getX(), _c.getY()) <= VIEW_DISTANCE) {
                    _c.service.move(_player);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void sendMessage(Message ms, Player _player) {
        zone.lockChar.readLock().lock();
        try {
            for (Player _c : zone.players) {
                if (_c != _player) {
                    _c.service.sendMessage(ms);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }
}
