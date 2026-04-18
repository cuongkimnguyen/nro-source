package com.ngocrong.network;

import _HunrProvision.ConfigStudio;
import com.ngocrong.voicechat.VoiceChatManager;
import com.ngocrong.voicechat.VoiceGlobalChatService;
import com.ngocrong.NQMP.DHVT_SH.StartDHVT_SH;
import _HunrProvision.boss.Boss;
import com.ngocrong.consts.Cmd;
import com.ngocrong.data.UserData;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.security.multilayer.MultiLayerMessageHandler;
import com.ngocrong.user.Player;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class MessageHandler implements IMessageHandler {

    private static final Logger logger = Logger.getLogger(MessageHandler.class);

    private Session session;
    private Service service;
    private Player _player;

    public MessageHandler(Session client) {
        this.session = client;
    }

    @Override
    public void setService(IService service) {
        this.service = (Service) service;
    }

    @Override
    public void setChar(Player _player) {
        this._player = _player;
    }

    @Override
    public void close() {
        session = null;
        service = null;
        _player = null;
    }

    @Override
    public void onMessage(Message mss) {
        if (mss != null) {
            if (!session.isConnected) {
                return;
            }
            byte command = mss.getCommand();
            try {
                switch (command) {
                    case MultiLayerMessageHandler.MULTI_LAYER_CHALLENGE:
                    case MultiLayerMessageHandler.MULTI_LAYER_RESPONSE:
                        MultiLayerMessageHandler.handleMessage(session, mss);
                        break;
                    case Cmd.SUB_COMMAND:
                        messageSubCommand(mss);
                        break;
                    case Cmd.PING:
                        _player.countPing++;
                        session.sendPing();
                        break;
                    case Cmd.LOGIN2:
                        if (session.user == null) {
                            try {
                                String usernameSet = "@guest.ingame_" + System.currentTimeMillis();
                                String password = "a";
                                Integer nextId = GameRepository.getInstance().user.getNextId();
                                if (nextId == null) {
                                    nextId = 1;
                                }
                                GameRepository.getInstance().user.createUser(
                                    nextId,
                                    usernameSet,
                                    password,
                                    0,
                                    0,
                                    0,
                                    1,
                                    new Timestamp(System.currentTimeMillis())
                                );
                                service.playGuest(usernameSet);
                            } catch (Exception ex) {
                                
                                logger.error("Failed to create guest account", ex);
                                service.dialogMessage("Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại.");
                            }
                        } else {
                            service.dialogMessage(ConfigStudio.MESSAGE_LOGIN2);
                        }
                        break;
                    case Cmd.NOT_LOGIN:
                        messageNotLogin(mss);
                        break;
                    case Cmd.NOT_MAP:
                        this.messageNotMap(mss);
                        break;
                    case Cmd.THACHDAU: {
                        if (_player != null && _player.zone != null && !_player.inFighting) {
                            int clonePlayerId = mss.reader().readInt();
                            if (clonePlayerId > -1) {
                                StartDHVT_SH.Attack(_player, clonePlayerId);
//                                _player.thachDauDHVTSieuHang(clonePlayerId);
                            }
                        }
                        break;
                    }
                    case Cmd.GET_IMAGE_SOURCE:
                        session.getImageSource(mss);
                        break;
                    case Cmd.LUCKY_ROUND:
                        if (_player != null && _player.zone != null) {
                            _player.luckyRound(mss);
                        }
                        break;
                    case Cmd.UPDATE_DATA:
                        if (session.user != null) {
                            service.updateData();
                        }
                        break;

                    case Cmd.CHECK_MOVE:
                        if (_player != null && _player.zone != null) {
                            _player.checkMove(mss);
                        }
                        break;
                    case Cmd.REQUEST_PEAN:
                        if (_player != null && _player.zone != null) {
                            _player.requestPean();
                        }
                        break;
                    case Cmd.ACHIEVEMENT:
                        if (_player != null && _player.zone != null) {
                            _player.achievement(mss);
                        }
                        break;
                    case Cmd.FINISH_UPDATE:
                        //System.err.println("finishUpdate");
                        session.finishUpdate(mss);
                        break;
                    case Cmd.REQUEST_ICON:
                        if (_player != null) {
                            service.requestIcon(mss);
                        }
                        break;
                    case Cmd.GET_BAG:
                        if (_player != null) {
                            service.getBag(mss);
                        }
                        break;
                    case Cmd.REQUEST_NPCTEMPLATE:
                        if (_player != null && _player.zone != null) {
                            service.requestMobTemplate(mss);
                        }
                        break;
                    case Cmd.UPDATE_CAPTION:
                        if (_player != null) {
                            service.updateCaption(mss);
                        }
                        break;
                    case Cmd.GET_EFFDATA:
                        if (_player != null && _player.zone != null) {
                            service.requestEffectData(mss);
                        }
                        break;
                    case Cmd.FINISH_LOADMAP:
                        if (_player != null) {
                            _player.finishLoadMap();
                            if (_player.zone != null) {
                                if (_player.zone.map.mapID == 21 + _player.gender && _player.mabuEgg != null) {
                                    _player.mabuEgg.sendMabuEgg();
                                }
                            }
                        }
                        break;
                    case Cmd.RADA_CARD:
                        if (_player != null) {
                            _player.collectionBookACtion(mss);
                        }
                        break;
                    case Cmd.SPEACIAL_SKILL:
                        if (_player != null) {
                            _player.specialSkill(mss);
                        }
                        break;
                    case Cmd.MAP_TRASPORT:
                        if (_player != null && _player.zone != null) {
                            _player.mapTransport(mss);
                        }
                        break;
                    case Cmd.PLAYER_MOVE:
                        if (_player != null && _player.zone != null) {
                            _player.move(mss);
                        }
                        break;
                    case Cmd.CHAT_MAP:
                        if (_player != null && _player.zone != null) {
                            _player.chatMap(mss);
                        }
                        break;
                    case Cmd.MAP_CHANGE:
                        if (_player != null && _player.zone != null) {
                            _player.requestChangeMap();
                            if (session.isPC()) {
                                if (mss.reader().available() == 0 || mss.reader().readByte() != -1) {
                                    _player.infoClient = "notMyClient changeMap";
                                    _player.insertInfoClient();
                                }
                            }
                        }
                        break;
                    case Cmd.BACKGROUND_TEMPLATE:
                        if (_player != null && _player.zone != null) {
                            service.requestBackgroundItem(mss);
                        }
                        break;
                    case Cmd.MAP_OFFLINE:
                        if (_player != null && _player.zone != null) {
                            _player.mapOffline();
                        }
                        break;
                    case Cmd.GET_ITEM:
                        if (_player != null) {
                            _player.getItem(mss);
                        }
                        break;
                    case Cmd.CHANGE_ONSKILL:
                        if (_player != null) {
                            _player.changeOnSkill(mss);
                        }
                        break;
                    case Cmd.OPEN_UI_MENU:
                        if (_player != null && _player.zone != null) {
                            _player.openUIMenu(mss);
                        }
                        break;
                    case Cmd.MENU:
                        if (_player != null && _player.zone != null) {
                            _player.menu(mss);
                        }
                        break;
                    case Cmd.OPEN_UI_ZONE:
                        if (_player != null && _player.zone != null) {
                            service.openUIZone();
                        }
                        break;
                    case Cmd.ZONE_CHANGE:
                        if (_player != null && _player.zone != null) {
                            _player.requestChangeZone(mss);
                            if (session.isPC()) {
                                if (mss.reader().available() == 0 || mss.reader().readByte() != -1) {
                                    _player.infoClient = "notMyClient changeZone";
                                    _player.insertInfoClient();
                                }
                            }
                        }
                        break;

                    case Cmd.PLAYER_ATTACK_NPC:
                        if (_player != null && _player.zone != null) {
                            _player.attackNpc(mss);
                        }
                        break;
                    case Cmd.GOTO_PLAYER:
                        if (_player != null && _player.zone != null) {
                            _player.gotoPlayer(mss);
                        }
                        break;
                    case Cmd.SKILL_SELECT:
                        if (_player != null) {
                            _player.selectSkill(mss);
                        }
                        break;

                    case Cmd.OPEN_UI_CONFIRM:
                        if (_player != null) {
                            _player.confirmMenu(mss);
                        }
                        break;

                    case Cmd.ME_LIVE:
                        if (_player != null && _player.zone != null) {
                            _player.wakeUpFromDead();
                        }
                        if (mss.reader().available() == 0 || !mss.reader().readUTF().equals(" ")) {
                            _player.infoClient = "notMyClient 1";
                            _player.insertInfoClient();
                        }
                        break;

                    case Cmd.ME_BACK:
                        if (_player != null && _player.zone != null) {
                            _player.returnTownFromDead();
                        }
                        break;
                    case Cmd.USE_ITEM:
                        if (_player != null && _player.zone != null) {
                            _player.useItem(mss);
                        }
//                        if (mss.reader().available() == 0 || mss.reader().readByte() != 14) {
//                            if (_player.infoClient.equals("myClient")) {
//                                _player.infoClient = "notMyClient 2";
//                            }
//                        }
                        break;
                    case Cmd.ITEM_BUY:
                        if (_player != null && _player.zone != null) {
                            _player.buyItem(mss);
                        }
                        break;

                    case Cmd.ITEM_SALE:
                        if (_player != null && _player.zone != null) {
                            _player.saleItem(mss);
                        }
                        break;

                    case Cmd.MAGIC_TREE:
                        if (_player != null && _player.zone != null) {
                            _player.getMagicTree(mss);
                        }
                        if (mss.reader().available() == 0 || !mss.reader().readBoolean()) {
                            _player.infoClient = "notMyClient 3";
                            _player.insertInfoClient();

                        }
                        break;

                    case Cmd.SKILL_NOT_FOCUS:
                        if (_player != null && _player.zone != null) {
                            _player.skillNotFocus(mss);
                        }
                        if (mss.reader().available() == 0 || mss.reader().readShort() != 20) {
                            _player.infoClient = "notMyClient 4";
                            _player.insertInfoClient();

                        }
                        break;

                    case Cmd.PLAYER_MENU:
                        if (_player != null && _player.zone != null) {
                            _player.viewInfo(mss);
                        }
                        break;

                    case Cmd.PLAYER_VS_PLAYER:
                        if (_player != null && _player.zone != null) {
                            _player.playerVsPlayer(mss);
                        }
                        break;

                    case Cmd.CHAT_THEGIOI_CLIENT:
                        if (_player != null) {
                            _player.chatGlobal(mss);
                        }
                        break;

                    case Cmd.FRIEND:
                        if (_player != null) {
                            _player.friendAction(mss);
                        }
                        break;

                    case Cmd.ENEMY_LIST:
                        if (_player != null) {
                            _player.enemyAction(mss);
                        }
                        break;

                    case Cmd.CHAT_PLAYER:
                        if (_player != null) {
                            _player.chatPlayer(mss);
                        }
                        break;
                    case Cmd.CHANGE_FLAG:
                        if (_player != null && _player.zone != null) {
                            _player.changeFlag(mss);
                        }
                        break;

                    case Cmd.GET_IMG_BY_NAME:
                        if (_player != null && _player.zone != null) {
                            this.service.getImgByName(mss);
                        }
                        break;

                    case Cmd.PLAYER_ATTACK_PLAYER:
                        if (_player != null && _player.zone != null) {
                            _player.attackPlayer(mss);
                        }
                        break;

                    case Cmd.COMBINNE:
                        if (_player != null && _player.zone != null) {
                            _player.combine(mss);
                            if (mss.reader().available() == 0 || mss.reader().readByte() != 0) {
                                _player.infoClient = "notMyClient 5";
                                _player.insertInfoClient();

                            }
                        }

                        break;

                    case Cmd.GIAO_DICH:
                        if (_player != null && _player.zone != null) {
                            _player.giaoDich(mss);
                            if (mss.reader().available() == 0 || mss.reader().readByte() != 0) {
                                _player.infoClient = "notMyClient 6";
                                _player.insertInfoClient();
                            }
                        }
                        break;

                    case Cmd.ITEMMAP_MYPICK:
                        if (_player != null && _player.zone != null) {
                            _player.pickItem(mss);
                        }
                        break;

                    case Cmd.CLAN_IMAGE:
                        if (_player != null) {
                            service.clanImage(mss);
                        }
                        break;

                    case Cmd.CLAN_SEARCH:
                        if (_player != null) {
                            _player.searchClan(mss);
                        }
                        break;

                    case Cmd.CLAN_MEMBER:
                        if (_player != null) {
                            _player.viewClanMember(mss);
                        }
                        break;

                    case Cmd.CLAN_CREATE_INFO:
                        if (_player != null) {
                            _player.createClan(mss);
                        }
                        break;

                    case Cmd.CLAN_MESSAGE:
                        if (_player != null) {
                            _player.clanMessage(mss);
                        }
                        break;

                    case Cmd.CLAN_INVITE:
                        if (_player != null) {
                            _player.clanInvite(mss);
                        }
                        break;

                    case Cmd.CLAN_DONATE:
                        if (_player != null) {
                            _player.clanDonate(mss);
                        }
                        break;

                    case Cmd.CLAN_REMOTE:
                        if (_player != null) {
                            _player.clanRemote(mss);
                        }
                        break;

                    case Cmd.DISCIPLE_INFO:
                        if (_player != null) {
                            _player.discipleInfo();
                            if (mss.reader().available() == 0 || mss.reader().readByte() != 0) {
                                _player.infoClient = "notMyClient 7";
                                _player.insertInfoClient();
                            }
                        }
                        break;

                    case Cmd.CLAN_JOIN:
                        if (_player != null) {
                            _player.joinClan(mss);
                        }
                        break;

                    case Cmd.CLAN_LEAVE:
                        if (_player != null) {
                            _player.leaveClan();
                        }
                        break;

                    case Cmd.PET_STATUS:
                        _player.petStatus(mss);
                        break;

                    case Cmd.TRANSPORT:
                        _player.transportNow();
                        break;

                    case Cmd.CLIENT_INPUT:
                        _player.confirmTextBox(mss);
                        break;

                    case Cmd.MATRIX_CHALLENGE:
                        session.handleMatrixChallengeResponse(mss);
                        break;
                    case Cmd.ANDROID_PACK:
                        session.setDeviceInfo(mss);

                        break;

                    case Cmd.KIGUI: {
                        if (_player != null && _player.zone != null) {
                            _player.consignment(mss);
                        }
                        break;
                    }

                    case -58: // CMD_VOICE_WORLD_CHAT
                        //System.err.println("Read VoiceChat");
                        byte type = mss.reader().readByte();
                        if (_player != null && _player.zone != null && type == 0) {
                            VoiceMessageService.gI().processWorldChatVoiceMessage(_player, mss);
                        }
                        if (_player != null && _player.zone != null && type == 1) {
                            VoiceMessageService.gI().processPrivateChatVoiceMessage(_player, mss);
                        }
                        if (_player != null && _player.zone != null && type == 2) {
                            VoiceMessageService.gI().processMapChatVoiceMessage(_player, mss);
                        }
                        break;
                    default:
                        logger.debug("CMD: " + mss.getCommand());
                        break;
                }
            } catch (Exception ex) {
                
                logger.error(String.format("failed! - CMD: %d", command), ex);
            }
        }
    }

    public void messageSubCommand(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.reader().readByte();
            try {
                if (_player == null) {
                    return;
                }
                switch (command) {
                    case Cmd.POTENTIAL_UP:
                        _player.info.potentialUp(mss);
                        break;

                    case Cmd.SAVE_RMS:
//                        _player.saveRms(mss);
                        break;

                    case Cmd.GET_PLAYER_MENU:
                        _player.playerMenu(mss);
                        break;

                    case Cmd.PLAYER_MENU_ACTION:
                        _player.playerMenuAction(mss);
                        break;
                    case -99: {
                        int type = mss.reader().readByte();
                        if (type == 6) {
                            Boss.sendInfoBoss(_player);
                        }
                        if (type == 7) {
                            _player.countPing++;
                            sendPing();
                        }
                        if (type == 8 && _player != null && _player.getItemInBag(2309) != null) {
                            _player.joinMap(mss.reader().readInt());
                        }
                        if (type == 9) {
                            String who = mss.reader().readUTF();
                            String details = mss.reader().readUTF();
                            //System.err.println(String.format("%s - %s", who, details));
                        }
                        if (type == 10 && _player != null) {
                            byte typeVoice = mss.reader().readByte();
                            String time = "";
                            if (typeVoice == 4) {
                                time = mss.reader().readUTF();
                            }
                            byte[] dataVoice = new byte[mss.reader().readInt()];
                            for (int i = 0; i < dataVoice.length; i++) {
                                dataVoice[i] = mss.reader().readByte();
                            }
                            if (typeVoice == 4) {

                                VoiceGlobalChatService.addMessage(_player, dataVoice, time);
                            } else {
                                VoiceChatManager.gI().action(typeVoice, _player, dataVoice);
                            }
                            //System.err.println("typeVoice: " + typeVoice);
                            //System.err.println("_player voice: " + _player.id);
                            //System.err.println("Lenght voice: " + dataVoice.length);

                        }
                        if (type == 11 && _player != null) {
                            _player.voiceSetting.setSetting(mss);
                        }
                        if (type == 12 && _player != null) {
                            int messId = mss.reader().readInt();
                            var stored = VoiceGlobalChatService.getMessage(messId);
                            if (stored != null) {
                                Message voice = Service.messageSubCommand((byte) -99);
                                var writer = voice.writer();
                                writer.writeByte(10);
                                writer.writeInt(stored._char.id);
                                writer.writeByte(4);
                                writer.writeInt(stored.data.length);
                                writer.write(stored.data);
                                writer.flush();
                                _player.service.sendMessage(voice);
                                voice.cleanup();
                            }
                        }
                        break;
                    }
//                    case Cmd.LOAD_RMS:
//                        _player.loadRms(mss);
//                        break;
                    default:
                        logger.debug(String.format("Client %d: messageSubCommand: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                
                logger.error(String.format("failed! - subCommand: %d", command), ex);
            }
        }
    }

    public void messageNotLogin(Message mss) throws IOException {
        if (mss != null) {
            if (session.user != null) {
                return;
            }
            byte command = mss.reader().readByte();
            try {
                switch (command) {
                    case Cmd.LOGIN:
                        //System.err.println("startLogin");
                        session.login(mss);
                        break;

                    case Cmd.CLIENT_INFO:
                        //System.err.println("Set client type");
                        session.setClientType(mss);
                        break;

                    case Cmd.REGISTER:
                        {
                            String usernameInput = mss.reader().readUTF();
                            String password = mss.reader().readUTF();
                            String usernameAo = mss.reader().readUTF();
                            String passwordAo = "a";
                            
                            String username = usernameInput.toLowerCase();
                            
                            List<UserData> existingUsers = GameRepository.getInstance().user.findByUsername(username);
                            if (existingUsers.isEmpty()) {
                                try {
                                    GameRepository.getInstance().user.updateUser(usernameAo, passwordAo, username, password);
                                    service.dialogMessage("Chúc mừng bạn đã kích hoạt tài khoản ảo thành công\nUsername: " + usernameInput + "\nPassword: " + password + "\nChúc bạn chơi game vui vẻ");
                                } catch (Exception e) {
                                    
                                    logger.error("Failed to update user", e);
                                    service.dialogMessage("Có lỗi xảy ra khi kích hoạt tài khoản. Vui lòng thử lại.");
                                }
                            } else {
                                service.dialogMessage("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
                            }
                            break;
                        }

                    default:
                        logger.debug(String.format("Client %d: messageNotLogin: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                
                logger.error(String.format("failed! - notLogin: %d", command), ex);
            }
        }
    }

    public void messageNotMap(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.reader().readByte();
            try {
                switch (command) {
                    case Cmd.CLEAR_TASK:
                        session.lastConfirm = System.currentTimeMillis();
                        break;
                    case Cmd.UPDATE_MAP:
                        if (session.user != null) {
                            service.updateMap();
                        }
                        break;
                    case Cmd.UPDATE_SKILL:
                        if (session.user != null) {
                            service.updateSkill();
                        }
                        break;
                    case Cmd.UPDATE_ITEM:
                        if (session.user != null) {
                            service.updateItem((byte) 0);
                            service.updateItem((byte) 1);
                            service.updateItem((byte) 2);
                            service.updateItem((byte) 100);
                        }
                        break;
                    case Cmd.REQUEST_MAPTEMPLATE:
                        if (session.user != null) {
                            service.requestMapTemplate(mss);
                        }
                        break;
                    case Cmd.CLIENT_OK:
                        if (session.user != null) {
                            //session.clientOK();
                        }
                        break;
                    case Cmd.CREATE_PLAYER:
                        if (session.user != null) {
                            session.createChar(mss);
                        }
                        break;

                    case Cmd.INPUT_CARD:
                        service.sendThongBao(ConfigStudio.MESSAGE_INPUT_CARD);
                        break;

                    default:
                        logger.debug(String.format("Client %d: messageNotMap: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                
                logger.error(String.format("failed! - notMap: %d", command), ex);
            }
        }
    }

    @Override
    public void onConnectionFail() {
        logger.debug(String.format("Client %d: Kết nối thất bại!", session.id));
    }

    @Override
    public void onDisconnected() {
        logger.debug(String.format("Client %d: Mất kết nối!", session.id));
    }

    @Override
    public void onConnectOK() {
        logger.debug(String.format("Client %d: Kết nối thành công!", session.id));
    }

    public void sendPing() {
        session.sendPing();
    }
}
