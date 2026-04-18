package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bot_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotConfigData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "botname_json", columnDefinition = "LONGTEXT")
    public String botnameJson;

    @Column(name = "caitrang_id_json", columnDefinition = "LONGTEXT")
    public String caitrangIdJson;

    @Column(name = "ratio_caitrang")
    public Integer ratioCaitrang;

    @Column(name = "ratio_trangbi")
    public Integer ratioTrangbi;

    @Column(name = "is_used", nullable = false)
    public Boolean isUsed = false;

    @Column(name = "used_botnames", columnDefinition = "LONGTEXT")
    public String usedBotnames;
}
