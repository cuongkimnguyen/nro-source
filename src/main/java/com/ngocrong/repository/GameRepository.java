package com.ngocrong.repository;

public class GameRepository {

    private static GameRepository instance;

    public UserDataRepository user;
    public PlayerDataRepository player;
    public GiftCodeDataRepository giftCode;
    public GiftCodeHistoryDataRepository giftCodeHistory;
    public DiscipleDataRepository disciple;
    public ClanDataRepository clan;
    public ClanMemberDataRepository clanMember;
    public ConsignmentItemDataRepository consignmentItem;
    public CCUDataRepository ccuDataRepository;
    public EventOpenRepository eventOpenRepository;
    public DHVTSieuHangRepository dhvtSieuHangRepository;
    public DhvtSieuHangRewardRepository dhvtSieuHangRewardRepository;
    public GameEventRepository gameEventRepository;
    public EventVQTD eventVQTD;
    public SuKienTetRepository eventTet;
    public HistoryGoldBarRepository historyGoldBar;
    public HistoryTradeRepository historyTradeRepository;
    public WhisDataRepository whisDataRepository;
    public OsinCheckInRepository osinCheckInRepository;
    public OsinLixiRepository osinLixiRepository;
    public DropRateRepository dropRateRepository;
    public StatisticServerRepository statisticServerRepository;
    public TopSuperRankRepository topSuperRankRepository;
    public SecurityRepository securityRepository;
    public MabuEggRepository mabuEggRepository;
    public BoMongNhiemVuRepository boMongNhiemVu;
    public BoMongNhiemVuConfigRepository boMongNhiemVuConfig;
    public BoMongBossConfigRepository boMongBossConfig;
    public BoMongConfigRepository boMongConfig;
    public BoMongMocDiemRepository boMongMocDiem;
    public BotConfigRepository botConfig;

    public static GameRepository getInstance() {
        if (instance == null) {
            instance = new GameRepository();
        }
        return instance;
    }

}
