package net.pointofviews.common.domain;

import jakarta.persistence.*;

@Entity
public class CommonCode {
    @Id
    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "group_code")
    private CommonCodeGroup groupCode;

    @Column(name = "common_code_name")
    private String name;

    @Column(name = "common_code_description")
    private String description;

    private boolean disabled;
}
