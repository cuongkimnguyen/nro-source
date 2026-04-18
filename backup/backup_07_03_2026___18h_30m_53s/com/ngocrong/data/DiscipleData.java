package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nr_disciple")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscipleData {

    @Id
    @Column(name = "id")
    public Integer id;

    @Column(name = "name")
    public String name;

    @Column(name = "skill")
    public String skill;

    @Column(name = "info")
    public String info;

    @Column(name = "item_body")
    public String itemBody;

    @Column(name = "planet")
    public Byte planet;

    @Column(name = "status")
    public Byte status;

    @Column(name = "skill_opened")
    public Byte skillOpened;

    @Column(name = "type")
    public Byte type;

    @Column(name = "bonus")
    public Byte bonus;
}
