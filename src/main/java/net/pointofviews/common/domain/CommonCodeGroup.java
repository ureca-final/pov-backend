package net.pointofviews.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CommonCodeGroup {

    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "common_code_group_name")
    private String name;

    @Column(name = "common_code_group_description")
    private String description;

    private boolean disabled;
}
