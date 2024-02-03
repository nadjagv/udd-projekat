package com.udd.uddprojekat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contract_table")
@ToString
public class ContractTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "server_filename")
    private String serverFilename;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "government_name")
    private String governmentName;

    @Column(name = "government_level")
    private String governmentLevel;

    @Column(name = "government_address")
    private String governmentAddress;
}
