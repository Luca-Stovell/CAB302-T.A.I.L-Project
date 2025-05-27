package com.example.cab302tailproject.model;

import java.sql.Timestamp;

public record ContentTableData(Timestamp lastModified, int week, String topic, String type, int classroom,
                               int materialID) {

}
