package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.domain.Data;

public interface DataRepository {

    Data loadData();

    void saveData(Data data);
}
