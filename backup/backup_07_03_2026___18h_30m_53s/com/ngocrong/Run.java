package com.ngocrong;

import com.ngocrong.repository.*;
import com.ngocrong.security.MatrixChallengePC;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.DropRateService;
import _HunrProvision.services.BoMongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Run implements CommandLineRunner {

    public static void main(String[] args) {

        SpringApplication.run(Run.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        GameRepository.getInstance().user = userDataRepository;
        GameRepository.getInstance().player = playerDataRepository;
        GameRepository.getInstance().giftCode = giftCodeDataRepository;
        GameRepository.getInstance().giftCodeHistory = giftCodeHistoryDataRepository;
        GameRepository.getInstance().disciple = discipleDataRepository;
        GameRepository.getInstance().clan = clanDataRepository;
        GameRepository.getInstance().clanMember = clanMemberDataRepository;
        GameRepository.getInstance().consignmentItem = consignmentItemDataRepository;
        GameRepository.getInstance().ccuDataRepository = ccuDataRepository;
        GameRepository.getInstance().eventOpenRepository = eventOpenRepository;
        GameRepository.getInstance().dhvtSieuHangRepository = dhvtSieuHangRepository;
        GameRepository.getInstance().dhvtSieuHangRewardRepository = dhvtSieuHangRewardRepository;
        GameRepository.getInstance().gameEventRepository = gameEventRepository;
        GameRepository.getInstance().eventVQTD = eventVQTD;
        GameRepository.getInstance().eventTet = eventTet;
        GameRepository.getInstance().historyGoldBar = historyGoldBar;
        GameRepository.getInstance().historyTradeRepository = historyTradeRepository;
        GameRepository.getInstance().whisDataRepository = whisDataRepository;
        GameRepository.getInstance().dropRateRepository = dropRateRepository;
        GameRepository.getInstance().osinCheckInRepository = osinReward;
        GameRepository.getInstance().osinLixiRepository = osinLixiRepository;
        GameRepository.getInstance().statisticServerRepository = statisticServerRepository;
        GameRepository.getInstance().securityRepository = security;
        GameRepository.getInstance().mabuEggRepository = mabuEggRepository;
        GameRepository.getInstance().boMongNhiemVu = boMongNhiemVuRepository;
        GameRepository.getInstance().boMongNhiemVuConfig = boMongNhiemVuConfigRepository;
        GameRepository.getInstance().boMongBossConfig = boMongBossConfigRepository;
        GameRepository.getInstance().boMongConfig = boMongConfigRepository;
        GameRepository.getInstance().boMongMocDiem = boMongMocDiemRepository;
        GameRepository.getInstance().botConfig = botConfigRepository;
        DropRateService.load();
        BoMongService.loadConfig();
        MatrixChallengePC.loadPCKey();
        DragonBall.getInstance().start();
    }

    @Autowired
    OsinCheckInRepository osinReward;

    @Autowired
    OsinLixiRepository osinLixiRepository;

    @Autowired
    WhisDataRepository whisDataRepository;

    @Autowired
    UserDataRepository userDataRepository;

    @Autowired
    PlayerDataRepository playerDataRepository;

    @Autowired
    GiftCodeDataRepository giftCodeDataRepository;

    @Autowired
    GiftCodeHistoryDataRepository giftCodeHistoryDataRepository;

    @Autowired
    DiscipleDataRepository discipleDataRepository;

    @Autowired
    ClanDataRepository clanDataRepository;

    @Autowired
    ClanMemberDataRepository clanMemberDataRepository;

    @Autowired
    ConsignmentItemDataRepository consignmentItemDataRepository;

    @Autowired
    CCUDataRepository ccuDataRepository;
    @Autowired
    EventOpenRepository eventOpenRepository;
    @Autowired
    DHVTSieuHangRepository dhvtSieuHangRepository;
    @Autowired
    DhvtSieuHangRewardRepository dhvtSieuHangRewardRepository;

    @Autowired
    GameEventRepository gameEventRepository;

    @Autowired
    EventVQTD eventVQTD;

    @Autowired
    SuKienTetRepository eventTet;

    @Autowired
    HistoryGoldBarRepository historyGoldBar;
    @Autowired
    HistoryTradeRepository historyTradeRepository;

    @Autowired
    DropRateRepository dropRateRepository;

    @Autowired
    StatisticServerRepository statisticServerRepository;

    @Autowired
    SecurityRepository security;

    @Autowired
    MabuEggRepository mabuEggRepository;


    @Autowired
    BoMongNhiemVuRepository boMongNhiemVuRepository;

    @Autowired
    BoMongNhiemVuConfigRepository boMongNhiemVuConfigRepository;

    @Autowired
    BoMongBossConfigRepository boMongBossConfigRepository;

    @Autowired
    BoMongConfigRepository boMongConfigRepository;

    @Autowired
    BoMongMocDiemRepository boMongMocDiemRepository;

    @Autowired
    BotConfigRepository botConfigRepository;
}
