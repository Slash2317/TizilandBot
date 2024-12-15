package com.slash.tizilandbot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slash.tizilandbot.domain.DataOld;

import java.io.*;

public class DataOldRepositoryImpl implements DataOldRepository {

    @Override
    public DataOld loadData() {
        File file = new File(getFilepath());
        if (!file.exists()) {
            return null;
        }
        else {
            try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String json = sb.toString();

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, DataOld.class);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void renameFolderLocation() {
        File file = new File(getFolderPath());
        if (file.exists()) {
            file.renameTo(new File(getNewFolderPath()));
        }
    }

    private String getFolderPath() {
        return System.getProperty("user.home") + File.separator + "RealTiziBot";
    }

    private String getNewFolderPath() {
        return System.getProperty("user.home") + File.separator + "TizilandBot";
    }

    private String getFilepath() {
        return getFolderPath() + File.separator + "data.json";
    }
}
