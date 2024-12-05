package com.slash.tizilandbot.service;

import com.slash.tizilandbot.domain.Data;

public interface DataRepository {

    Data loadData();

    void saveData(Data data);
}
