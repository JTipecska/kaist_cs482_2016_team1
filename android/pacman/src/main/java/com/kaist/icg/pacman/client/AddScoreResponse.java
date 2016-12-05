package com.kaist.icg.pacman.client;

public class AddScoreResponse {
    private boolean isNew;

    public AddScoreResponse(boolean isNew) {
        this.isNew = isNew;
    }

    public AddScoreResponse() {
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
