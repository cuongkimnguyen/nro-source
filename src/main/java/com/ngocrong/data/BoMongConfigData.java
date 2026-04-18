package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nr_bo_mong_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoMongConfigData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "config_key")
    public String configKey;

    @Column(name = "config_value")
    public String configValue;
}

