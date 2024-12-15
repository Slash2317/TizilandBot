package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.domain.DataOld;

public interface DataOldRepository {

    DataOld loadData();

    void renameFolderLocation();
}
